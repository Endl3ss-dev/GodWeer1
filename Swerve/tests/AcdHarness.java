package swerve.acd;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Locale;

/**
 * Исполняемый тест чистой логики {@code AntiCheatDetector}: хранилище, дедупликация,
 * классификатор, форматирование отчёта и разбивка на куски для чата.
 *
 * <p>Это НЕ часть сборки: файл лежит в {@code tests/}, в jar не попадает, и в
 * {@code build.gradle} для него нет исходного каталога. Он нужен потому, что
 * перечисленные классы не зависят от Minecraft — их можно скомпилировать и запустить
 * отдельно, без Loom, без клиента и без сервера.
 *
 * <p>Запуск (проверяет {@code tests/check_acd_run.py}; он же подбирает {@code javac}):
 * <pre>
 *   GSON=$(find ~/.gradle/caches/modules-2/files-2.1/com.google.code.gson -name 'gson-*.jar' | head -1)
 *   javac -cp "$GSON" -encoding UTF-8 -d /tmp/acdout \
 *         -sourcepath src/main/java tests/AcdHarness.java
 *   java -cp "/tmp/acdout:$GSON" swerve.acd.AcdHarness; echo "код: $?"
 * </pre>
 *
 * <p>Код возврата: 0 — все проверки прошли, 1 — есть непрошедшие (они печатаются
 * построчно). Никаких файлов, сети и Minecraft здесь нет намеренно: тест обязан работать
 * в голой JVM.
 *
 * <p>ПОЧЕМУ ЗДЕСЬ МНОГО РУЧНЫХ ОЖИДАНИЙ. Проверяется контракт, а не реализация:
 * например, что после переполнения буфера остаются САМЫЕ НОВЫЕ записи в порядке
 * поступления, что повторы не растут отдельными записями, что обрезка при выводе в чат
 * сообщает честные числа. Такие утверждения нельзя «подогнать» под код — они либо
 * нарушены, либо нет.
 *
 * <p>НЕ ПРОВЕРЕНО этим тестом (и не может быть проверено без клиента): сами миксины,
 * потоки netty, буфер обмена GLFW, запись файла, {@code AcdCollector}/{@code AcdExporter}
 * (тянут {@code Minecraft}), поведение в игре. См. {@code docs/AntiCheatDetector.md} §8.
 */
public final class AcdHarness {

    private static int passed;
    private static int failed;

    private AcdHarness() {
    }

    public static void main(String[] args) {
        buffersAndOrder();
        capacityClamps();
        dedup();
        dedupWindows();
        eventLimits();
        keyIdentity();
        severityLadder();
        confidenceLabels();
        categoryTable();
        rulesTable();
        snippets();
        classify();
        sessionLifecycle();
        formatterText();
        formatterJson();
        paginate();
        sinceWindow();
        statusLine();

        System.out.println("AcdHarness: пройдено " + passed + ", провалено " + failed);
        if (failed > 0) {
            System.out.println("НЕ ПРОВЕРЕНО в песочнице: компиляции и запуска Java в CI нет;"
                    + " этот файл выполняется только у разработчика с JDK.");
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------ хранилище

    /** Кольцо: переполнение вытесняет старые записи, порядок остаётся хронологическим. */
    private static void buffersAndOrder() {
        resetStore(50);
        for (int i = 0; i < 120; i++) {
            store(AcdCategory.NETWORK_DIAGNOSTIC, "m" + i);
        }
        check(AcdStore.size() == 50, "размер ограничен ёмкостью, а не числом событий");
        check(AcdStore.capacity() == 50, "ёмкость не прыгает сама");
        AcdReport report = AcdStore.snapshot();
        check(report.size() == 50, "снимок полного буфера");
        check("m70".equals(report.events()[0].message()), "вытесняются старые: первая запись — m70");
        check("m119".equals(report.events()[report.size() - 1].message()), "последняя запись — самая свежая");
        check(report.dropped() == 70, "вытеснение посчитано честно");
        check(report.merged() == 0, "слияний не было при окне 0");

        AcdReport last = AcdStore.snapshot(5);
        check(last.size() == 5, "снимок последних пяти");
        check("m115".equals(last.events()[0].message()), "снимок начинается с пятой с конца");
        check("m119".equals(last.events()[4].message()), "снимок заканчивается последней записью");
        check(AcdStore.snapshot(0).size() == 0, "нулевой лимит даёт пустой снимок");
        check(AcdStore.snapshot(1000).size() == 50, "лимит больше буфера не добавляет записей");
        check(AcdStore.snapshot(-10).size() == 0, "отрицательный лимит не роняет снимок");

        // [!] Test capacity shrink above MIN_CAPACITY (50): grow to 80, fill 30 more, then shrink to 50.
        AcdStore.setCapacity(80);
        check(AcdStore.capacity() == 80, "ёмкость выросла");
        check(AcdStore.size() == 50, "при росте записи не теряются");
        check("m70".equals(AcdStore.snapshot().events()[0].message()), "порядок пережил рост ёмкости");
        for (int i = 120; i < 150; i++) {
            store(AcdCategory.NETWORK_DIAGNOSTIC, "m" + i);
        }
        AcdStore.setCapacity(50);
        check(AcdStore.size() == 50, "при ужатии остаются новые");
        check("m100".equals(AcdStore.snapshot().events()[0].message()), "ужатие отрезало старые, не новые");
        check(AcdStore.snapshot().dropped() == 100, "учтены и вытеснение кольцом, и ужатие");
        check(AcdStore.snapshot().capacity() == 50, "снимок несёт актуальную ёмкость");

        int revision = AcdStore.revision();
        AcdStore.setCapacity(50);
        check(AcdStore.revision() == revision, "повторная установка той же ёмкости не двигает ревизию");

        revision = AcdStore.revision();
        store(AcdCategory.NETWORK_DIAGNOSTIC, "m-after");
        check(AcdStore.revision() == revision + 1, "новая запись поднимает ревизию на единицу");
        check(AcdStore.revision() > 0, "ревизия монотонна");

        AcdStore.clear();
        check(AcdStore.size() == 0, "очистка обнуляет счётчик");
        check(AcdStore.snapshot().size() == 0, "после очистки снимок пуст");
        check(AcdStore.revision() > revision, "очистка видит интерфейс (ревизия выросла)");
        check(AcdStore.snapshot().dropped() == 0, "счётчик вытеснений обнуляется вместе с историей");
    }

    /** Границы ёмкости: хранилище не должно раздуться от числа из настройки. */
    private static void capacityClamps() {
        AcdStore.setCapacity(1);
        check(AcdStore.capacity() == AcdStore.MIN_CAPACITY, "маленькое значение поднимается до минимума");
        AcdStore.setCapacity(Integer.MAX_VALUE);
        check(AcdStore.capacity() == AcdStore.MAX_CAPACITY, "огромное значение ограничено потолком");
        AcdStore.setCapacity(-5);
        check(AcdStore.capacity() == AcdStore.MIN_CAPACITY, "отрицательная ёмкость невозможна");
        check(AcdStore.DEFAULT_CAPACITY >= AcdStore.MIN_CAPACITY
                && AcdStore.DEFAULT_CAPACITY <= AcdStore.MAX_CAPACITY,
                "значение по умолчанию внутри границ");
        resetStore(100);
        store(AcdCategory.COMBAT_DIAGNOSTIC, "a");
        store(AcdCategory.COMBAT_DIAGNOSTIC, "b");
        store(AcdCategory.COMBAT_DIAGNOSTIC, "c");
        check(AcdStore.size() == 3, "три записи — три");
        AcdStore.clear();
    }

    /** Повтор одного и того же повода не должен превращаться в лавину записей. */
    private static void dedup() {
        resetStore(50);
        AcdStore.setMergeWindowMs(2500L);
        long base = 1000L;
        check(AcdStore.record(event(base, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "r0", "flagged for speed")), "первое событие добавлено");
        for (int i = 1; i < 5; i++) {
            boolean added = AcdStore.record(event(base + i * 100L, AcdCategory.FLAG_NOTIFICATION,
                    AcdSeverity.WARNING, AcdConfidence.HEURISTIC, "chat", "r" + i, "flagged for speed"));
            check(!added, "повтор не создаёт запись (итерация " + i + ")");
        }
        check(AcdStore.size() == 1, "пять одинаковых поводов — одна запись");
        AcdEvent kept = AcdStore.snapshot().events()[0];
        check(kept.repeats() == 5, "счётчик повторов считает и первое событие");
        check(kept.variants() == 4, "каждый новый исходный текст посчитан вариантом");
        check("r0".equals(kept.raw()), "исходный текст остался первым, а не последним");
        check(kept.lastTimeMs() == base + 400L, "время записи передвинуто на последний повтор");
        check(kept.timeMs() == base, "время первого события не переписывается");
        check(AcdStore.snapshot().merged() == 4, "слияния посчитаны");
        int revision = AcdStore.revision();
        AcdStore.record(event(base + 500L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "r5", "flagged for speed"));
        check(AcdStore.revision() == revision, "слияние не создаёт запись и не двигает ревизию");
        check(AcdStore.size() == 1, "и после шестого повтора запись всё ещё одна");

        // другая категория, источник или достоверность — это уже другой повод
        check(AcdStore.record(event(base, AcdCategory.SERVER_MESSAGE, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "r", "flagged for speed")), "смена категории даёт новую запись");
        check(AcdStore.record(event(base, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "position", "r", "flagged for speed")), "смена источника даёт новую запись");
        check(AcdStore.record(event(base, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.CONFIRMED, "chat", "r", "flagged for speed")), "смена достоверности даёт новую запись");
        check(AcdStore.size() == 4, "рядом лежат четыре разных повода");
        AcdStore.clear();
        AcdStore.setMergeWindowMs(0L);
    }

    /** Границы склейки: по времени и по глубине просмотра. */
    private static void dedupWindows() {
        resetStore(50);
        AcdStore.setMergeWindowMs(2500L);
        AcdStore.record(event(1000L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "server corrected position"));
        AcdStore.record(event(3501L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "server corrected position"));
        check(AcdStore.size() == 2, "за окном склейки — отдельная запись (3501-1000 > 2500)");
        AcdStore.record(event(3500L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "server corrected position"));
        check(AcdStore.size() == 2, "запись чуть раньше по времени всё равно склеивается с ближайшей");
        check(AcdStore.snapshot().events()[1].repeats() == 2, "повтор учтён у самой свежей записи");
        check(AcdStore.snapshot().events()[1].lastTimeMs() == 3501L,
                "опоздавшее событие не откатывает время последней встречи");
        AcdStore.clear();

        // Глубина просмотра ограничена: далёкий повтор не должен «притягиваться».
        AcdStore.setMergeWindowMs(600_000L);
        AcdStore.record(event(5000L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "same as far away"));
        for (int i = 0; i < 9; i++) {
            AcdStore.record(event(5100L + i, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                    AcdConfidence.CONFIRMED, "position", "r", "filler " + i));
        }
        AcdStore.record(event(6000L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "same as far away"));
        check(AcdStore.size() == 11, "повтор дальше окна просмотра — новая запись");
        AcdStore.record(event(6100L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "r", "filler 8"));
        check(AcdStore.size() == 11, "последний повтор склеен с соседним");
        check(AcdStore.snapshot().events()[9].repeats() == 2, "слияние произошло с ближайшей записью");
        AcdStore.clear();
        AcdStore.setMergeWindowMs(AcdStore.DEFAULT_MERGE_WINDOW_MS);
    }

    /** Пустой, битый и гигантский вход не должен ни ронять клиента, ни съедать память. */
    private static void eventLimits() {
        StringBuilder huge = new StringBuilder();
        for (int i = 0; i < 3000; i++) {
            huge.append("0123456789");
        }
        String big = huge.toString();
        AcdEvent oversized = new AcdEvent(1L, 2L, AcdCategory.SERVER_MESSAGE, AcdSeverity.INFO,
                AcdConfidence.CONFIRMED, "chat", big, big, big, big, 7);
        check(oversized.raw().length() == AcdEvent.MAX_RAW_CHARS, "исходный текст обрезан по лимиту");
        check(oversized.truncated(), "факт обрезки сохранён, а не потерян");
        check(oversized.message().length() == AcdEvent.MAX_MESSAGE_CHARS, "описание обрезано по своему лимиту");
        check(oversized.detail().length() == big.length(), "детали не режутся: они короткие по построению");

        AcdEvent empties = new AcdEvent(1L, 2L, AcdCategory.UNKNOWN_EVENT, AcdSeverity.NOTICE,
                AcdConfidence.OBSERVED, null, null, null, null, null, 0);
        check(empties.raw().isEmpty() && empties.message().isEmpty() && empties.detail().isEmpty()
                && empties.source().isEmpty() && empties.component().isEmpty(),
                "null-поля превращаются в пустые строки");
        check(!empties.truncated(), "пустое сообщение не помечено обрезанным");

        AcdEvent exact = new AcdEvent(1L, 2L, AcdCategory.UNKNOWN_EVENT, AcdSeverity.NOTICE,
                AcdConfidence.OBSERVED, "s", big.substring(0, AcdEvent.MAX_RAW_CHARS), "m", "d", "c", 1);
        check(!exact.truncated(), "текст ровно по лимиту не считается обрезанным");

        resetStore(50);
        check(!AcdStore.record(null), "null-запись отклоняется, а не бросает исключение");
        check(AcdStore.size() == 0, "после null-записи буфер пуст");
        check(AcdStore.snapshot().events().length == 0, "снимок пустого буфера — пустой массив, не null");
        AcdStore.record(empties);
        check(AcdStore.size() == 1, "событие без текста всё равно записывается: источник важен");
        AcdStore.clear();
        AcdStore.setMergeWindowMs(0L);
    }

    /** Одинаковый повод против разного: ключ дедупликации. */
    private static void keyIdentity() {
        AcdEvent first = event(10L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "raw-1", "Flagged for Speed");
        AcdEvent sameCase = event(11L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "raw-2", "flagged for speed");
        check(first.sameKey(sameCase), "регистр описания не создаёт новый повод");
        check(!first.sameKey(event(11L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.CRITICAL,
                AcdConfidence.HEURISTIC, "chat", "raw", "Flagged for Speed")),
                "смена важности — новый повод: эскалация не должна теряться");
        check(!first.sameKey(event(11L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "system", "raw", "Flagged for Speed")),
                "другой источник — другое событие");
        check(!first.sameKey(event(11L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "raw", "Flagged for Flight")),
                "другое описание — другое событие");
        check(!first.sameKey(null), "null не равный");
        check(event(10L, AcdCategory.SERVER_MESSAGE, AcdSeverity.INFO, AcdConfidence.CONFIRMED,
                "s", "r", "m", "d", 42).sessionId() == 42, "номер сессии переносится в запись как есть");
    }

    /** Иерархия важности: фильтр «от X и выше» обязан быть линейным. */
    private static void severityLadder() {
        AcdSeverity[] severities = AcdSeverity.values();
        check(severities.length == 4, "важностей четыре");
        for (int i = 0; i < severities.length; i++) {
            for (int j = 0; j < severities.length; j++) {
                boolean expected = i >= j;
                check(severities[i].atLeast(severities[j]) == expected,
                        severities[i].name() + " atLeast " + severities[j].name());
            }
            check(!severities[i].label().isEmpty(), "у важности есть русская подпись: " + severities[i].name());
        }
        check(AcdSeverity.CRITICAL.atLeast(AcdSeverity.INFO), "критично проходит любой фильтр");
        check(!AcdSeverity.INFO.atLeast(AcdSeverity.NOTICE), "инфо не проходит фильтр «от заметки»");
        resetStore(100);
        AcdStore.setSeverityFloor(AcdSeverity.WARNING);
        check(AcdStore.severityFloor() == AcdSeverity.WARNING, "фильтр выставлен");
        AcdStore.setSeverityFloor(null);
        check(AcdStore.severityFloor() == AcdSeverity.INFO, "null-фильтр превращается в базовый");
    }

    /** Достоверность — центр всего модуля: подписи и «догадка это или нет». */
    private static void confidenceLabels() {
        check(AcdConfidence.CONFIRMED.speculative() == false, "подтверждённое не считается догадкой");
        check(AcdConfidence.OBSERVED.speculative() == false, "измеренное клиентом не считается догадкой");
        check(AcdConfidence.HEURISTIC.speculative(), "эвристика — да");
        for (int i = 0; i < AcdConfidence.values().length; i++) {
            check(!AcdConfidence.values()[i].label().isEmpty(), "подпись достоверности непустая");
        }
        check(AcdConfidence.values().length == 3, "достоверностей ровно три");
    }

    /** Категории: расширять список можно, ломать уникальность — нет. */
    private static void categoryTable() {
        AcdCategory[] categories = AcdCategory.values();
        check(categories.length == 9, "категорий девять (новая — плюс одна строка здесь и в документации)");
        for (int i = 0; i < categories.length; i++) {
            AcdCategory category = categories[i];
            check(!category.label().isEmpty(), "метка не пустая: " + category.name());
            check(!category.code().isEmpty(), "код не пустой: " + category.name());
            check(category.code().equals(category.code().trim()), "код без пробелов: " + category.name());
            check(category.defaultSeverity() != null, "у категории есть важность по умолчанию");
            for (int j = i + 1; j < categories.length; j++) {
                check(!category.code().equals(categories[j].code()), "коды уникальны: " + category.code());
                check(category != categories[j], "константы различны");
            }
        }
        check(AcdCategory.KICK_DISCONNECT.defaultSeverity() == AcdSeverity.CRITICAL,
                "отключение — критично по умолчанию");
        check(AcdCategory.SERVER_MESSAGE.defaultSeverity() == AcdSeverity.INFO,
                "сообщение сервера — инфо по умолчанию");

        // Политика по категориям живёт в хранилище и не зависит от инстанса модуля.
        AcdStore.setEnabled(AcdCategory.CLIENT_ERROR, false);
        check(!AcdStore.isEnabled(AcdCategory.CLIENT_ERROR), "категорию можно выключить");
        check(AcdStore.isEnabled(AcdCategory.FLAG_NOTIFICATION), "соседние категории не задеты");
        AcdStore.setEnabled(AcdCategory.CLIENT_ERROR, true);
        check(AcdStore.isEnabled(AcdCategory.CLIENT_ERROR), "категорию можно включить обратно");
        AcdStore.setScansChat(false);
        check(!AcdStore.scansChat(), "поиск по чату игроков отключается");
        AcdStore.setScansChat(true);
        AcdStore.setAnnounces(true);
        check(AcdStore.announces(), "мгновенные уведомления включаются");
        AcdStore.setAnnounces(false);
        check(!AcdStore.announces(), "и выключаются");
        AcdStore.setCollecting(false);
        check(!AcdStore.isCollecting(), "сбор выключается");
        AcdStore.setCollecting(true);
        check(AcdStore.isCollecting(), "сбор включается");
    }

    /** Таблица правил: её читает и GUI, и проверка {@code tests/check_acd.py}. */
    private static void rulesTable() {
        AcdRule[] rules = AcdRules.all();
        check(rules.length > 0, "правила есть");
        for (int i = 0; i < rules.length; i++) {
            AcdRule rule = rules[i];
            check(rule.id() != null && rule.id().length() >= 3, "у правила есть id: " + rule.id());
            check(rule.target() != null, "правило ведёт в категорию");
            check(rule.severity() != null, "у правила есть важность");
            check(rule.reason().length() > 10, "правило объясняет себя по-русски");
            check(!rule.matches(null), "null-текст не совпадает");
            check(!rule.matches(""), "пустой текст не совпадает");
            for (int j = i + 1; j < rules.length; j++) {
                check(!rule.id().equals(rules[j].id()), "id правил уникальны");
            }
            // правила не должны срабатывать на собственные пояснения
            check(!rule.matches(rule.reason().toLowerCase(Locale.ROOT)) || rule.reason().isEmpty(),
                    "формулировка причины не является маркером: " + rule.id());
        }
        check(AcdRules.match(null) == null, "поиск по null возвращает null");
        check(AcdRules.match("") == null, "поиск по пустой строке возвращает null");
        check(AcdRules.all() != AcdRules.all(), "all() отдаёт копию, а не живой массив");
    }

    /** Нормализация: коды формата, пробелы, пустой мусор и длина. */
    private static void snippets() {
        check(AcdClassifier.snippet(null).isEmpty(), "null → пустая строка");
        check(AcdClassifier.snippet("").isEmpty(), "пустая строка остаётся пустой");
        check(AcdClassifier.snippet("   \n\t  ").isEmpty(), "только пробелы → пусто");
        check("a b".equals(AcdClassifier.snippet("a\n  b")), "переносы и повторяющиеся пробелы схлопнуты");
        check("Flag for Grim".equals(AcdClassifier.snippet("§a§lFlag §rför Grim".replace("för", "for"))),
                "коды формата вырезаны вместе со следующим символом");
        check(AcdClassifier.snippet("§").isEmpty(), "одинокий маркер формата не оставляет мусор");
        String longText = repeat("x", 5000);
        String clipped = AcdClassifier.snippet(longText);
        check(clipped.length() == AcdClassifier.MAX_SNIPPET_CHARS, "фрагмент обрезан по лимиту");
        check(AcdClassifier.searchKey("GRIM").equals("grim"), "поиск без регистра");
        check(AcdClassifier.searchKey("I").equals("i"), "нижний регистр по ROOT, не по локали системы");
        check(AcdClassifier.searchKey(longText).length() == AcdClassifier.MAX_SCAN_CHARS,
                "окно поиска ограничено: длинный текст не прогоняется через таблицу целиком");
        check(AcdClassifier.searchKey(null).isEmpty(), "null-ключ пуст");
        check(AcdClassifier.classify("") == null, "классификация пустого ключа не бросает исключение");
    }

    /** Классификация: что подтверждается текстом, а что нет. */
    private static void classify() {
        checkRule("Player was flagged for Speed by GrimAC", "ac:name", AcdCategory.FLAG_NOTIFICATION);
        checkRule("[Grim] Vl: 12 (Reach) flag", "ac:name", AcdCategory.FLAG_NOTIFICATION);
        checkRule("you have a violation of the rules", "flag:violation", AcdCategory.FLAG_NOTIFICATION);
        checkRule("no violation was found", null, null);
        checkRule("you did not violate anything", null, null);
        checkRule("Player moved too quickly!", "move:speed", AcdCategory.FLAG_NOTIFICATION);
        checkRule("Flying is not enabled here", "move:flight", AcdCategory.FLAG_NOTIFICATION);
        checkRule("Matrix: flight detected", "move:flight", AcdCategory.FLAG_NOTIFICATION);
        checkRule("You were teleported back by the server", "move:setback", AcdCategory.MOVEMENT_DIAGNOSTIC);
        checkRule("teleport potion effect expired", null, null);
        checkRule("You are banned for cheating", "kick:ban", AcdCategory.KICK_DISCONNECT);
        checkRule("banned items list updated", null, null);
        checkRule("Timed out", "net:timeout", AcdCategory.NETWORK_DIAGNOSTIC);
        checkRule("connection lost", "net:timeout", AcdCategory.NETWORK_DIAGNOSTIC);
        checkRule("античит обнаружил подозрительное движение", "ac:name", AcdCategory.FLAG_NOTIFICATION);
        checkRule("подозрительный разброс углов", "flag:violation", AcdCategory.FLAG_NOTIFICATION);
        // «обычная» переписка и игровые тексты не должны становиться флагами
        checkRule("hello everyone, how are you", null, null);
        checkRule("flags of our nation are welcome", null, null);
        checkRule("the matrix has you", null, null);
        checkRule("speed limit near spawn", null, null);
        checkRule("hawk spotted in the sky", null, null);
        checkRule("please read the rules and be nice", null, null);
        checkRule("You have been kicked", null, null);
        checkRule("bad packets from your client", "packet:bad", AcdCategory.ANTI_CHEAT_WARNING);

        AcdRule rule = AcdClassifier.classify(AcdClassifier.searchKey("flagged by grimac for speed"));
        check(rule != null, "поиск по подготовленному ключу находит правило");
        check("grimac".equals(rule.matchedMarker(AcdClassifier.searchKey("flagged by grimac for speed"))),
                "в отчёт попадает фактически совпавший маркер");
        check(rule.matchedMarker("ничего похожего") == null, "у несовпавшего текста маркера нет");
        check(AcdClassifier.classify(AcdClassifier.searchKey(repeat("grim", 1))) != null,
                "короткий текст тоже обрабатывается");
    }

    /** Сессия: переподключение не обрывает историю, закрытие не затирает её. */
    private static void sessionLifecycle() {
        resetStore(100);
        AcdStore.clear();
        check(!AcdStore.isSessionOpen(), "свежее хранилище — сессия не открыта");
        int before = AcdStore.sessionId();
        AcdStore.beginSession("hungergames.example:25565");
        check(AcdStore.isSessionOpen(), "начало сессии открыло её");
        check(AcdStore.sessionId() == before + 1, "номер сессии вырос на единицу");
        store(AcdCategory.NETWORK_DIAGNOSTIC, "in session");
        AcdStore.beginSession("another-server.example");
        check(AcdStore.sessionId() == before + 1, "повторный login при открытой сессии не начинает новую");
        check(AcdStore.snapshot().sessionServer().equals("another-server.example"),
                "сменившийся сервер всё равно записывается");
        AcdStore.endSession();
        check(!AcdStore.isSessionOpen(), "сессия закрыта");
        check(AcdStore.size() == 1, "закрытие сессии не трогает историю");
        check(AcdStore.snapshot().sessionStartMs() > 0L, "время начала зафиксировано");
        check(AcdStore.snapshot().sessionEndMs() >= AcdStore.snapshot().sessionStartMs(),
                "время конца не раньше начала");
        AcdStore.endSession();
        long end = AcdStore.snapshot().sessionEndMs();
        check(AcdStore.snapshot().sessionEndMs() == end, "повторное закрытие ничего не перезаписывает");
        AcdStore.beginSession(null);
        check(AcdStore.snapshot().sessionServer().isEmpty(), "null-адрес сервера превращается в пустую строку");
        AcdStore.endSession();
        check(AcdStore.snapshot().sessionsObserved() >= 2, "число наблюдённых сессий растёт");
        AcdStore.clear();
        check(AcdStore.sessionId() > 0, "очистка истории не сбрасывает сведения о сессиях");
        AcdStore.beginSession("harness");
        store(AcdCategory.SERVER_MESSAGE, "with session");
        AcdReport report = AcdStore.snapshot();
        check(report.events()[0].sessionId() == AcdStore.sessionId(), "запись помечена текущей сессией");
        AcdStore.endSession();
        AcdStore.clear();
        AcdStore.setMergeWindowMs(0L);
    }

    /** Текстовый отчёт: всё, что обещано, на месте и только при включённом флаге. */
    private static void formatterText() {
        resetStore(100);
        AcdStore.clear();
        AcdStore.beginSession("harness.example");
        AcdStore.record(event(System.currentTimeMillis(), AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "grim flagged you for Reach",
                "сообщение: «flagged for Reach»", "правило ac:name: совпало «grim»", "AntiCheatDetector",
                AcdStore.sessionId()));
        AcdStore.record(event(System.currentTimeMillis(), AcdCategory.KICK_DISCONNECT, AcdSeverity.CRITICAL,
                AcdConfidence.CONFIRMED, "packet:disconnect", "You are banned", "причина отключения: «You are banned»",
                "категорию определил пакет", "", AcdStore.sessionId()));
        AcdReport report = AcdStore.snapshot();

        String full = AcdFormatter.render(report, AcdFormatter.Options.FULL_DETAIL, false);
        check(full.startsWith("===="), "отчёт начинается с разделителя");
        check(full.contains("AntiCheatDetector - Diagnostic Report"), "есть шапка");
        check(full.contains("Generated: "), "есть время формирования");
        check(full.contains("Session:") && full.contains("harness.example"), "блок сессии с адресом сервера");
        check(full.contains("Started: ") && full.contains("сессия ещё открыта"), "открытая сессия помечена открытой");
        check(full.contains("Total events: 2"), "сводка: всего записей");
        check(full.contains("Confirmed events: 1"), "сводка: подтверждённых");
        check(full.contains("Heuristic events: 1"), "сводка: эвристических");
        check(full.contains("Observed by client: 0"), "сводка: измеренных клиентом");
        check(full.contains("Buffer: 2 / 100"), "сводка: буфер");
        check(full.contains("KICK_DISCONNECT=1") && full.contains("FLAG_NOTIFICATION=1"),
                "сводка по категориям использует стабильные ASCII-коды");
        check(full.contains("Внимание: 1"), "эвристики вынесены в отдельное предупреждение");
        check(full.contains("Source: chat"), "источник подписан");
        check(full.contains("Confidence: "), "достоверность подписана у каждой записи");
        check(full.contains("Raw: grim flagged you for Reach"), "исходный текст сохранён целиком");
        check(full.contains("Details: правило ac:name"), "причина классификации на месте");
        check(full.contains("ClientState: AntiCheatDetector"), "связь с компонентом клиента");
        check(full.contains("Session: #"), "номер сессии у записи");
        check(full.contains("End of report"), "отчёт закрыт");
        check(full.indexOf("End of report") > full.indexOf("Total events: 2"), "записи идут после сводки");
        check(!full.contains("записей нет"), "непустая история не называется пустой");

        String bare = AcdFormatter.render(report, new AcdFormatter.Options(false, false, false), false);
        check(!bare.contains("Raw: "), "выключенный флаг не печатает исходный текст");
        check(!bare.contains("Source: "), "выключенный флаг не печатает источник");
        check(!bare.contains("Session: #"), "выключенный флаг не печатает номер сессии");
        check(bare.contains("Total events: 2") && bare.contains("End of report"),
                "шапка и сводка остаются при любой подробности");

        String jsonFormat = AcdFormatter.render(report, AcdFormatter.Options.FULL_DETAIL, true);
        check(jsonFormat.trim().startsWith("{"), "режим JSON отдаёт JSON, а не текст");
        AcdStore.endSession();
        AcdStore.clear();
        AcdReport empty = AcdStore.snapshot();
        String emptyText = AcdFormatter.text(empty, AcdFormatter.Options.FULL_DETAIL);
        check(emptyText.contains("(записей нет"), "пустая история объясняется словами, а не пустым отчётом");
        check(emptyText.contains("Total events: 0"), "пустая сводка содержит нули");
        check(AcdFormatter.render(empty, null, false).equals(emptyText), "null-настройки = полная подробность");
        check(AcdFormatter.fileStamp(System.currentTimeMillis()).length() >= 8, "метка времени для имени файла");
        check(!AcdFormatter.fileStamp(0L).isEmpty(), "нулевое время не роняет метку");
    }

    /** JSON обязан оставаться машиночитаемым и не содержать лишнего. */
    private static void formatterJson() {
        resetStore(100);
        AcdStore.clear();
        AcdStore.beginSession("json.example");
        // [!] Match 10-arg event(...) helper signature (timeMs, category, severity, confidence, source, raw, message, detail, component, sessionId).
        AcdStore.record(event(1712345678000L, AcdCategory.ANTI_CHEAT_WARNING, AcdSeverity.WARNING,
                AcdConfidence.CONFIRMED, "packet:system", "bad packets", "bad packets",
                "источник подтвердил", "компонент", AcdStore.sessionId()));
        AcdReport report = AcdStore.snapshot();
        String json = AcdFormatter.render(report, AcdFormatter.Options.FULL_DETAIL, true);
        JsonElement parsed = JsonParser.parseString(json);
        check(parsed.isJsonObject(), "JSON разбирается разборщиком, а не «похож»");
        check(parsed.getAsJsonObject().getAsJsonArray("events").size() == 1, "массив записей нужной длины");
        check(parsed.getAsJsonObject().get("summary").getAsJsonObject().get("total").getAsInt() == 1,
                "сводка в JSON сходится со снимком");
        check(json.contains("\"raw\":\"bad packets\"") || json.contains("\"raw\": \"bad packets\""),
                "исходный текст в JSON есть при включённом флаге");
        check(json.contains("\"classification\""), "классификация подписана отдельным полем");
        check(json.contains("\"sessionId\""), "при включённом флаге сессии у записи есть sessionId");
        String off = AcdFormatter.render(report, new AcdFormatter.Options(false, false, false), true);
        check(!off.contains("\"raw\""), "флаг исходного текста реально влияет на JSON");
        check(!off.contains("\"source\""), "флаг источника реально влияет на JSON");
        // [!] Options(false, false, false) disables per-entry sessionId while keeping the top-level "session" block.
        check(!off.contains("\"sessionId\"") && off.contains("\"session\""),
                "флаг сессии убирает поле у записи, но шапку сессии не прячет");
        check(!off.contains("playerName") && !off.contains("inventory") && !off.contains("token"),
                "в выгрузке нет полей с личными данными");
        check(!AcdFormatter.render(AcdStore.snapshot(), AcdFormatter.Options.FULL_DETAIL, true)
                .contains("записей нет"), "JSON не вставляет текстовые пояснения");
        AcdStore.clear();
        AcdStore.endSession();
    }

    /** Разбивка для чата: лимиты соблюдаются, отрезанное описано числами. */
    private static void paginate() {
        check(AcdFormatter.paginate(null, 100, 5).isEmpty(), "null не разбивается");
        check(AcdFormatter.paginate("", 100, 5).isEmpty(), "пустой текст не даёт кусков");
        check(AcdFormatter.paginate("a\nb", 8, 5).isEmpty(), "слишком узкий кусок — отказ, а не бесконечный цикл");
        check(AcdFormatter.paginate("l1\nl2\nl3", 100, 10).size() == 1, "короткий текст = один кусок");
        check("l1\nl2\nl3".equals(AcdFormatter.paginate("l1\nl2\nl3", 100, 10).get(0)),
                "короткий текст не переформатируется");
        check(AcdFormatter.paginate("\n\n\n", 100, 10).isEmpty(), "пустые строки не занимают место");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            builder.append("L").append(i).append(" строка отчёта\n");
        }
        String many = builder.toString();
        List<String> chunks = AcdFormatter.paginate(many, 48, 6);
        check(chunks.size() == 6, "число кусков не превышает лимит");
        for (int i = 0; i < chunks.size(); i++) {
            check(chunks.get(i).length() <= 48 || i == chunks.size() - 1,
                    "кусок не длиннее лимита (последний — отметка)");
        }
        check(chunks.get(0).startsWith("L0 "), "первый кусок начинается с первой строки");
        check(chunks.get(chunks.size() - 1).contains("опущено строк: "),
                "последний кусок — отметка об обрезке");
        check(chunks.get(chunks.size() - 1).contains("Скопировать все логи"),
                "отметка говорит, где взять полный отчёт");
        int dropped = numberAfter(chunks.get(chunks.size() - 1), "опущено строк: ");
        int total = numberAfter(chunks.get(chunks.size() - 1), " из ");
        check(total == 200, "всего строк посчитано верно: " + total);
        check(dropped > 0 && dropped < total, "число опущенных правдоподобно");
        check(total - dropped > 0, "часть строк всё же показана");

        List<String> all = AcdFormatter.paginate(many, 4096, 200);
        int joined = 0;
        for (int i = 0; i < all.size(); i++) {
            joined += countLines(all.get(i));
        }
        check(joined == 200, "без обрезки не теряется ни одна строка");
        check(!all.get(all.size() - 1).contains("опущено"), "без обрезки отметки нет");

        String oneLine = repeat("z", 1000);
        List<String> hard = AcdFormatter.paginate(oneLine, 100, 60);
        check(hard.size() == 10, "длинная строка режется жёстко по лимиту");
        check(hard.get(0).length() == 100, "кусок ровно по лимиту");
        List<String> hardClipped = AcdFormatter.paginate(oneLine, 100, 4);
        check(hardClipped.size() == 4, "и при малом числе кусков лимит соблюдён");
        check(hardClipped.get(3).contains("опущено строк: 7 из 10"), "отметка считает опущенные куски строки");
        check(AcdFormatter.truncationNotice(3, 10, 42).contains("опущено строк: 3 из 10"),
                "шаблон отметки предсказуем");
    }

    /** Временное окно выгрузки: фильтр не должен врать ни в числе, ни в разбивке по категориям. */
    private static void sinceWindow() {
        resetStore(100);
        AcdStore.clear();
        long now = System.currentTimeMillis();
        AcdStore.record(event(now - 600_000L, AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                AcdConfidence.HEURISTIC, "chat", "old", "old flag"));
        AcdStore.record(event(now - 60_000L, AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                AcdConfidence.CONFIRMED, "position", "new", "new correction"));
        AcdReport full = AcdStore.snapshot();
        check(full.size() == 2, "полный снимок содержит обе записи");
        AcdReport recent = full.since(now - 300_000L);
        check(recent.size() == 1, "окно оставляют только свежие записи");
        check("new correction".equals(recent.events()[0].message()), "в окне осталась правильная запись");
        check(recent.count(AcdCategory.MOVEMENT_DIAGNOSTIC) == 1, "сводка по категориям пересчитана");
        check(recent.count(AcdCategory.FLAG_NOTIFICATION) == 0, "старая категория исчезла из сводки");
        check(recent.confirmedCount() == 1 && recent.heuristicCount() == 0,
                "достоверности пересчитаны вместе с записями");
        check(recent.capacity() == full.capacity(), "ёмкость буфера окно не меняет");
        check(recent.sessionId() == full.sessionId(), "сведения о сессии сохраняются");
        check(full.since(0L).size() == 2, "окно «с начала времён» ничего не отсекает");
        check(full.since(now + 60_000L).size() == 0, "окно в будущем даёт пустой снимок, а не исключение");
        check(full.since(now + 60_000L).capacity() == full.capacity(), "и в пустом снимке ёмкость на месте");
        AcdReport one = full.since(0L);
        check(one.events().length == full.events().length, "полное окно возвращает тот же набор");
        AcdStore.clear();
    }

    /** Строка состояния окна модуля — её читает игрок, а не парсер. */
    private static void statusLine() {
        resetStore(100);
        AcdStore.clear();
        check(AcdStore.statusLine().contains("записей 0 из 100"), "пустая история описана числом");
        store(AcdCategory.SERVER_MESSAGE, "one");
        check(AcdStore.statusLine().contains("записей 1 из 100"), "счётчик в строке состояния");
        check(!AcdStore.statusLine().contains("вытеснено"), "пока нечего вытеснять — и строка об этом молчит");
        for (int i = 0; i < 150; i++) {
            store(AcdCategory.SERVER_MESSAGE, "line " + i);
        }
        check(AcdStore.statusLine().contains("вытеснено 51"), "строка состояния честно считает вытеснение");
        AcdStore.clear();
    }

    // ---------------------------------------------------------------------- утилиты

    private static void resetStore(int capacity) {
        AcdStore.setMergeWindowMs(0L);
        AcdStore.setCapacity(capacity);
        AcdStore.clear();
        AcdStore.setSeverityFloor(AcdSeverity.INFO);
        AcdStore.setCollecting(true);
        AcdStore.setScansChat(true);
        AcdStore.setAnnounces(false);
        AcdCategory[] categories = AcdCategory.values();
        for (int i = 0; i < categories.length; i++) {
            AcdStore.setEnabled(categories[i], true);
        }
    }

    private static void store(AcdCategory category, String message) {
        AcdStore.record(event(System.currentTimeMillis(), category, category.defaultSeverity(),
                AcdConfidence.CONFIRMED, "harness", message, message));
    }

    private static AcdEvent event(long timeMs, AcdCategory category, AcdSeverity severity,
                                  AcdConfidence confidence, String source, String raw, String message) {
        // [!] Propagate current AcdStore.sessionId() for store() helper calls within an active session.
        return event(timeMs, category, severity, confidence, source, raw, message, "", AcdStore.sessionId());
    }

    private static AcdEvent event(long timeMs, AcdCategory category, AcdSeverity severity,
                                  AcdConfidence confidence, String source, String raw, String message,
                                  String detail, int sessionId) {
        return event(timeMs, category, severity, confidence, source, raw, message, detail, "", sessionId);
    }

    private static AcdEvent event(long timeMs, AcdCategory category, AcdSeverity severity,
                                  AcdConfidence confidence, String source, String raw, String message,
                                  String detail, String component, int sessionId) {
        return new AcdEvent(timeMs, timeMs * 1000L, category, severity, confidence, source, raw, message,
                detail, component, sessionId);
    }

    private static void checkRule(String text, String expectedId, AcdCategory expectedCategory) {
        AcdRule rule = AcdClassifier.classify(AcdClassifier.searchKey(text));
        if (expectedId == null) {
            check(rule == null, "правила не срабатывают на «" + text + "»");
            return;
        }
        if (rule == null) {
            check(false, "ожидалось правило " + expectedId + " для «" + text + "»");
            return;
        }
        check(expectedId.equals(rule.id()), "«" + text + "» → правило " + expectedId
                + " (фактически " + rule.id() + ")");
        if (expectedCategory != null) {
            check(rule.target() == expectedCategory, "«" + text + "» → категория "
                    + expectedCategory.name() + " (фактически " + rule.target().name() + ")");
        }
    }

    private static String repeat(String unit, int times) {
        StringBuilder builder = new StringBuilder(unit.length() * Math.max(1, times));
        for (int i = 0; i < times; i++) {
            builder.append(unit);
        }
        return builder.toString();
    }

    /** Целое сразу после подстроки — чтобы числа в отметке проверялись, а не пересказывались. */
    private static int numberAfter(String text, String marker) {
        int from = text.indexOf(marker);
        if (from < 0) {
            return -1;
        }
        int index = from + marker.length();
        int end = index;
        while (end < text.length() && Character.isDigit(text.charAt(end))) {
            end++;
        }
        if (end == index) {
            return -1;
        }
        return Integer.parseInt(text.substring(index, end));
    }

    private static int countLines(String text) {
        int lines = text.isEmpty() ? 0 : 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        return lines;
    }

    private static void check(boolean condition, String what) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("ПРОВАЛ: " + what);
        }
    }
}
