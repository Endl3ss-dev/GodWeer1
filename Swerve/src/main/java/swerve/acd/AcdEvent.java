package swerve.acd;

/**
 * Одна запись журнала: что произошло, откуда, что именно сказал источник и
 * насколько надёжно это истолковано.
 *
 * <p>Поля неизменяемы, кроме счётчиков группировки ({@link #repeats()},
 * {@link #variants()}, {@link #lastTimeMs()}): дедупликация дописывает их в ту же
 * запись вместо роста буфера. Исходный текст ({@link #raw()}) не заменяется
 * нормализованным описанием ({@link #message()}) — в отчёте идут оба.
 *
 * <p>Время хранится дважды: {@link #timeMs()} — настенное время для чтения человеком,
 * {@link #monotonicNs()} — монотонные наносекунды {@code System.nanoTime()} для
 * корректного порядка и относительных меток (системные часы могут прыгнуть).
 */
public final class AcdEvent {

    /** Больше этого исходный текст обрезается: 1 МБ дампа в одном сообщении не нужен ни в памяти, ни в отчёте. */
    public static final int MAX_RAW_CHARS = 4096;

    /** Ограничение нормализованного описания: оно входит в ключ дедупликации. */
    public static final int MAX_MESSAGE_CHARS = 512;

    private final long timeMs;
    private final long monotonicNs;
    private final AcdCategory category;
    private final AcdSeverity severity;
    private final AcdConfidence confidence;
    private final String source;
    private final String raw;
    private final String message;
    private final String detail;
    private final String component;
    private final int sessionId;
    private final boolean truncated;

    private int repeats = 1;
    private int variants;
    private long lastTimeMs;

    public AcdEvent(long timeMs, long monotonicNs, AcdCategory category, AcdSeverity severity,
                    AcdConfidence confidence, String source, String raw, String message, String detail,
                    String component, int sessionId) {
        this.timeMs = timeMs;
        this.monotonicNs = monotonicNs;
        this.category = category;
        this.severity = severity;
        this.confidence = confidence;
        this.source = nullToEmpty(source);
        this.component = nullToEmpty(component);
        this.message = clamp(nullToEmpty(message), MAX_MESSAGE_CHARS);
        this.detail = nullToEmpty(detail);
        this.sessionId = sessionId;
        this.lastTimeMs = timeMs;

        boolean oversize = raw != null && raw.length() > MAX_RAW_CHARS;
        this.truncated = oversize;
        this.raw = oversize ? raw.substring(0, MAX_RAW_CHARS) : nullToEmpty(raw);
    }

    public long timeMs() {
        return this.timeMs;
    }

    public long monotonicNs() {
        return this.monotonicNs;
    }

    public AcdCategory category() {
        return this.category;
    }

    public AcdSeverity severity() {
        return this.severity;
    }

    public AcdConfidence confidence() {
        return this.confidence;
    }

    /** Кто прислал событие: «Server message», «Packet ClientboundPlayerPositionPacket», … */
    public String source() {
        return this.source;
    }

    /** Исходный текст сообщения либо пустая строка, если текста не было. */
    public String raw() {
        return this.raw;
    }

    /** Нормализованное описание события одним предложением. */
    public String message() {
        return this.message;
    }

    /** Почему классифицировано так, а не иначе, и числовые детали. */
    public String detail() {
        return this.detail;
    }

    /** Swerve-компонент, к которому относится событие (имя модуля), если он известен. */
    public String component() {
        return this.component;
    }

    public int sessionId() {
        return this.sessionId;
    }

    /** Сколько событий с этим ключом попало в запись, включая первое. */
    public int repeats() {
        return this.repeats;
    }

    /** Сколько из повторов пришли с другим исходным текстом — честный сигнал, что группировка не слила разное. */
    public int variants() {
        return this.variants;
    }

    public long lastTimeMs() {
        return this.lastTimeMs;
    }

    /** Было ли исходное сообщение длиннее {@link #MAX_RAW_CHARS} и обрезано. */
    public boolean truncated() {
        return this.truncated;
    }

    /**
     * Тот ли это повод: категория, важность, достоверность, источник и нормализованное
     * описание. Важность входит в ключ намеренно: усилений (INFO → CRITICAL) слияние
     * проглотило бы молча.
     * Регистр в описании не учитывается — сервер может писать одно и то же разными
     * буквами, а исходный текст при этом сохраняется отдельным полем.
     */
    public boolean sameKey(AcdEvent other) {
        return other != null
                && this.category == other.category
                && this.severity == other.severity
                && this.confidence == other.confidence
                && this.message.equalsIgnoreCase(other.message)
                && this.source.equals(other.source);
    }

    // Исходный текст отличается — считаем вариант, но запись остаётся одной.
    void merge(long timeMs, String rawText) {
        this.repeats++;
        if (timeMs > this.lastTimeMs) {
            // событие могло опоздать из-за часов или очереди: откатывать время нельзя,
            // иначе каждое опоздание расширяет окно склейки для следующих повторов
            this.lastTimeMs = timeMs;
        }
        if (rawText != null && !rawText.equals(this.raw)) {
            this.variants++;
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String clamp(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }
}
