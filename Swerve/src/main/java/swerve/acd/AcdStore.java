package swerve.acd;

/**
 * Хранилище диагностических записей.
 *
 * <p>Живёт отдельно от модуля: {@code ModuleManager} создаёт модули один раз, но
 * обработка событий идёт из миксинов, а интерфейс модуля может быть выключен —
 * история при этом обязана сохраняться (§7 задачи). Поэтому замок, буфер и счётчики
 * статические, а {@code AntiCheatDetector} (пакет {@code swerve.modules.impl.misc}) хранит только политики: что писать и до чего
 * расти буферу.
 *
 * <h2>Потоки</h2>
 * <p>Вызовы приходят и из игрового потока (обработчики пакетов), и из потока netty
 * ({@code onDisconnect}, {@code onPacketError}). Все изменения — под одним замком,
 * критическая секция это несколько сравнений и одна запись в массив: никаких
 * форматирований, строк и IO под замком. {@link #collecting()} — {@code volatile boolean}
 * вне замка: это единственный вопрос, который задаёт каждый вызов, когда модуль выключен.
 *
 * <h2>Порядок и лимит</h2>
 * <p>Буфер — кольцо: порядок поступления сохраняется, переполнение вытесняет самую
 * старую запись за O(1) без сдвига массива и без аллокаций. Дедупликация смотрит
 * только на {@value #MERGE_SCAN} последних записей и только в окне
 * {@value #DEFAULT_MERGE_WINDOW_MS} мс — иначе одна задержка сети слила бы разные
 * события в одно.
 */
public final class AcdStore {

    public static final int MIN_CAPACITY = 50;
    public static final int MAX_CAPACITY = 20_000;
    public static final int DEFAULT_CAPACITY = 1_000;

    /** Сколько последних записей проверяется на «это тот же повод». */
    private static final int MERGE_SCAN = 8;

    /** По умолчанию объединяем повторы, пришедшие не дальше чем за это время. */
    public static final long DEFAULT_MERGE_WINDOW_MS = 1_200L;

    private static final Object LOCK = new Object();

    private static AcdEvent[] buffer = new AcdEvent[DEFAULT_CAPACITY];
    private static int head;
    private static int size;

    private static long dropped;
    private static long merged;
    private static volatile int revision;

    private static int sessionId;
    private static int sessionsObserved;
    private static long sessionStartMs;
    private static long sessionEndMs;
    private static String sessionServer = "";

    private static volatile boolean collecting;
    private static volatile long mergeWindowMs = DEFAULT_MERGE_WINDOW_MS;
    /**
     * Крюк «выключи меня по-настоящему»: его выставляет модуль, потому что {@code swerve.acd}
     * не должен знать ни о каких модулях — иначе пакет-наблюдатель зависит от наблюдаемого.
     */
    private static volatile Runnable stopHook;

    private AcdStore() {
    }

    /** Модуль включён → события пишутся. Вызов из любого потока, идемпотентен. */
    public static void setCollecting(boolean state) {
        collecting = state;
    }

    public static boolean isCollecting() {
        return collecting;
    }

    /** Чем гасить модуль при внутренней поломке (может быть null — тогда только флаг). */
    public static void setStopHook(Runnable hook) {
        stopHook = hook;
    }

    public static Runnable stopHook() {
        return stopHook;
    }

    public static void setMergeWindowMs(long windowMs) {
        mergeWindowMs = windowMs < 0 ? 0 : windowMs;
    }

    // ---- ограничители, которые выставляет модуль
    //
    // Живут здесь, а не в модуле по двум причинам: писать в них должен поток,
    // пришедший из окна настроек (модуль может быть выключен), а читать — сетевой
    // поток, и держать для этого ссылку на инстанс модуля хуже, чем четыре поля.
    // Читаются без замка: это отдельные поля, устаревшее значение стоит максимум
    // одного пропущенного или лишнего записанного события на время между тиками.

    private static final boolean[] ENABLED = new boolean[AcdCategory.values().length];
    private static volatile AcdSeverity severityFloor = AcdSeverity.INFO;
    private static volatile boolean scansChat = true;
    private static volatile boolean announces;

    static {
        for (int i = 0; i < ENABLED.length; i++) {
            ENABLED[i] = true;
        }
    }

    public static boolean isEnabled(AcdCategory category) {
        return category.ordinal() < ENABLED.length && ENABLED[category.ordinal()];
    }

    public static void setEnabled(AcdCategory category, boolean state) {
        if (category.ordinal() < ENABLED.length) {
            ENABLED[category.ordinal()] = state;
        }
    }

    public static AcdSeverity severityFloor() {
        return severityFloor;
    }

    public static void setSeverityFloor(AcdSeverity floor) {
        severityFloor = floor == null ? AcdSeverity.INFO : floor;
    }

    /** Печатать ли WARNING/CRITICAL в локальный чат в момент появления (по умолчанию выкл). */
    public static boolean announces() {
        return announces;
    }

    public static void setAnnounces(boolean state) {
        announces = state;
    }

    /** Сканировать ли подписанный чат игроков в поисках совпадений с правилами. */
    public static boolean scansChat() {
        return scansChat;
    }

    public static void setScansChat(boolean state) {
        scansChat = state;
    }

    public static int sessionId() {
        return sessionId;
    }

    public static int size() {
        synchronized (LOCK) {
            return size;
        }
    }

    public static int capacity() {
        synchronized (LOCK) {
            return buffer.length;
        }
    }

    /** Растёт на каждую новую запись (не на повтор-слияние) — по нему GUI понимает, когда перечитывать. */
    public static int revision() {
        return revision;
    }

    public static boolean isSessionOpen() {
        // Под замком специально: sessionStartMs/sessionEndMs — длинные, вне замка их
        // допустимо прочитать рассогласованно (JLS 17.7), а решение по ним принимается
        // реальное; метод вызывается раз в тик, цена — один monitor enter.
        synchronized (LOCK) {
            return sessionStartMs != 0 && sessionEndMs == 0;
        }
    }

    /**
     * Начинает игровую сессию. Повторный вызов при открытой сессии ничего не меняет:
     * переподключение не должно обрывать уже накопленную историю.
     */
    public static void beginSession(String server) {
        synchronized (LOCK) {
            if (sessionStartMs != 0 && sessionEndMs == 0) {
                if (server != null && !server.equals(sessionServer)) {
                    sessionServer = server;
                }
                return;
            }
            sessionId++;
            sessionsObserved++;
            sessionStartMs = System.currentTimeMillis();
            sessionEndMs = 0L;
            sessionServer = server == null ? "" : server;
            revision++;
        }
    }

    /** Заканчивает сессию. Закрытая сессия не перезаписывается: история остаётся нетронутой. */
    public static void endSession() {
        synchronized (LOCK) {
            if (sessionStartMs == 0 || sessionEndMs != 0) {
                return;
            }
            sessionEndMs = System.currentTimeMillis();
            revision++;
        }
    }

    /**
     * Добавляет запись либо объединяет её с последней такой же.
     *
     * @return {@code true}, если добавлена новая запись
     */
    public static boolean record(AcdEvent event) {
        if (event == null) {
            return false;
        }
        synchronized (LOCK) {
            long window = mergeWindowMs;
            if (window > 0) {
                for (int i = 0; i < MERGE_SCAN && i < size; i++) {
                    int index = Math.floorMod(head + size - 1 - i, buffer.length);
                    AcdEvent existing = buffer[index];
                    if (existing != null && existing.sameKey(event)
                            && event.timeMs() - existing.lastTimeMs() <= window) {
                        existing.merge(event.timeMs(), event.raw());
                        merged++;
                        return false;
                    }
                }
            }

            if (size < buffer.length) {
                buffer[Math.floorMod(head + size, buffer.length)] = event;
                size++;
            } else {
                // Кольцо полно: самая старая запись уходит, её место занимает новая.
                buffer[head] = event;
                head = head + 1 == buffer.length ? 0 : head + 1;
                dropped++;
            }
            revision++;
            return true;
        }
    }

    /**
     * Меняет лимит буфера. При уменьшении уходят самые старые записи — они считаются
     * вытесненными, чтобы в сводке отчёта не выглядело, что их никогда не было.
     */
    public static void setCapacity(int capacity) {
        int target = Math.max(MIN_CAPACITY, Math.min(MAX_CAPACITY, capacity));
        synchronized (LOCK) {
            if (target == buffer.length) {
                return;
            }
            AcdEvent[] ordered = new AcdEvent[size];
            for (int i = 0; i < size; i++) {
                ordered[i] = buffer[Math.floorMod(head + i, buffer.length)];
            }
            int keep = Math.min(target, size);
            AcdEvent[] next = new AcdEvent[target];
            // newest «keep» записей — в начало нового кольца, порядок сохраняется
            System.arraycopy(ordered, size - keep, next, 0, keep);
            dropped += size - keep;
            buffer = next;
            head = 0;
            size = keep;
            revision++;
        }
    }

    /** Полная очистка накопленного; сведения о сессии остаются. */
    public static void clear() {
        synchronized (LOCK) {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = null;
            }
            head = 0;
            size = 0;
            dropped = 0L;
            merged = 0L;
            revision++;
        }
    }

    /** Снимок всего буфера — то, что уходит в отчёт и в буфер обмена. */
    public static AcdReport snapshot() {
        return snapshot(size());
    }

    /**
     * Снимок последних {@code limit} записей (для показа в чат).
     */
    public static AcdReport snapshot(int limit) {
        synchronized (LOCK) {
            int wanted = Math.max(0, Math.min(limit, size));
            int from = size - wanted;
            AcdEvent[] copy = new AcdEvent[wanted];
            int[] byCategory = new int[AcdCategory.values().length];
            int confirmed = 0;
            int observed = 0;
            int heuristic = 0;
            for (int i = 0; i < wanted; i++) {
                AcdEvent event = buffer[Math.floorMod(head + from + i, buffer.length)];
                copy[i] = event;
                byCategory[event.category().ordinal()]++;
                switch (event.confidence()) {
                    case CONFIRMED -> confirmed++;
                    case OBSERVED -> observed++;
                    case HEURISTIC -> heuristic++;
                }
            }
            return new AcdReport(copy, buffer.length, dropped, merged, sessionId, sessionsObserved,
                    sessionStartMs, sessionEndMs, sessionServer, byCategory,
                    confirmed, observed, heuristic, System.currentTimeMillis());
        }
    }

    /** Только счётчики и сессия — для строки состояния в окне модуля, без аллокаций массива. */
    public static String statusLine() {
        synchronized (LOCK) {
            return "записей " + size + " из " + buffer.length
                    + (dropped > 0 ? ", вытеснено " + dropped : "")
                    + (merged > 0 ? ", слито повторов " + merged : "");
        }
    }
}
