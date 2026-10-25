package swerve.acd;

/**
 * Снимок журнала для форматирования и экспорта.
 *
 * <p>Снимок, а не вид на живой буфер: отчёт строится вне блокировки хранилища, иначе
 * поток экспорта держал бы замок на всём времени склейки текста (а при тысяче записей
 * это десятки миллисекунд, в течение которых сетевой поток не смог бы ничего записать).
 * Копируется только массив ссылок, сами записи неизменяемы.
 */
public final class AcdReport {

    private final AcdEvent[] events;
    private final int capacity;
    private final long dropped;
    private final long merged;
    private final int sessionId;
    private final int sessionsObserved;
    private final long sessionStartMs;
    private final long sessionEndMs;
    private final String sessionServer;
    private final int[] byCategory;
    private final int confirmed;
    private final int observed;
    private final int heuristic;
    private final long takenMs;

    AcdReport(AcdEvent[] events, int capacity, long dropped, long merged, int sessionId, int sessionsObserved,
              long sessionStartMs, long sessionEndMs, String sessionServer, int[] byCategory,
              int confirmed, int observed, int heuristic, long takenMs) {
        this.events = events;
        this.capacity = capacity;
        this.dropped = dropped;
        this.merged = merged;
        this.sessionId = sessionId;
        this.sessionsObserved = sessionsObserved;
        this.sessionStartMs = sessionStartMs;
        this.sessionEndMs = sessionEndMs;
        this.sessionServer = sessionServer;
        this.byCategory = byCategory;
        this.confirmed = confirmed;
        this.observed = observed;
        this.heuristic = heuristic;
        this.takenMs = takenMs;
    }

    /**
     * Подмножество записей не старше {@code fromMs} — то, что уходит в отчёт при
     * включённом временном окне фильтрации.
     *
     * <p>Сводка пересчитывается по отфильтрованному набору, а {@code capacity},
     * {@code dropped} и {@code merged} остаются буферными: они описывают хранилище,
     * а не окно, иначе «вытеснено 5» выглядело бы как «в окне вытеснено 5».
     *
     * @return новый снимок; если в окно ничего не попало — пустой снимок с прежней сессией
     */
    public AcdReport since(long fromMs) {
        int kept = 0;
        for (int i = 0; i < this.events.length; i++) {
            if (this.events[i].timeMs() >= fromMs) {
                kept++;
            }
        }
        if (kept == this.events.length) {
            return this;
        }
        AcdEvent[] copy = new AcdEvent[kept];
        int[] counts = new int[AcdCategory.values().length];
        int confirmedNow = 0;
        int observedNow = 0;
        int heuristicNow = 0;
        int index = 0;
        for (int i = 0; i < this.events.length; i++) {
            AcdEvent event = this.events[i];
            if (event.timeMs() < fromMs) {
                continue;
            }
            copy[index++] = event;
            counts[event.category().ordinal()]++;
            switch (event.confidence()) {
                case CONFIRMED -> confirmedNow++;
                case OBSERVED -> observedNow++;
                case HEURISTIC -> heuristicNow++;
            }
        }
        return new AcdReport(copy, this.capacity, this.dropped, this.merged, this.sessionId,
                this.sessionsObserved, this.sessionStartMs, this.sessionEndMs, this.sessionServer,
                counts, confirmedNow, observedNow, heuristicNow, this.takenMs);
    }

    /** Записи в порядке поступления, от самой старой к самой новой. */
    public AcdEvent[] events() {
        return this.events;
    }

    public int size() {
        return this.events.length;
    }

    /** Лимит буфера на момент снимка — нужен, чтобы отличить «столько было» от «столько влезло». */
    public int capacity() {
        return this.capacity;
    }

    /** Сколько записей вытеснено лимитом (включая уменьшение лимита настройкой). */
    public long dropped() {
        return this.dropped;
    }

    /** Сколько повторов схлопнуто в существующие записи, а не добавлено отдельно. */
    public long merged() {
        return this.merged;
    }

    public int sessionId() {
        return this.sessionId;
    }

    public int sessionsObserved() {
        return this.sessionsObserved;
    }

    public long sessionStartMs() {
        return this.sessionStartMs;
    }

    /** 0, если соединение ещё живо. */
    public long sessionEndMs() {
        return this.sessionEndMs;
    }

    public String sessionServer() {
        return this.sessionServer;
    }

    /** Число записей по {@link AcdCategory#ordinal} — только по тому, что осталось в буфере. */
    public int count(AcdCategory category) {
        return category.ordinal() < this.byCategory.length ? this.byCategory[category.ordinal()] : 0;
    }

    public int confirmedCount() {
        return this.confirmed;
    }

    public int observedCount() {
        return this.observed;
    }

    public int heuristicCount() {
        return this.heuristic;
    }

    public long takenMs() {
        return this.takenMs;
    }
}
