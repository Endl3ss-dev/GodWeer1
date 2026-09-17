package godweer.utils.movement;

public final class MotionAudit {

    public static final int SAMPLE = 0;
    public static final int FLAG_ROLLBACK = 1;
    public static final int FLAG_LIMIT = 2;
    public static final int PAUSE = 3;
    public static final int CEILING_DOWN = 4;
    public static final int CEILING_UP = 5;
    public static final int GRACE = 6;
    public static final int SKIP = 7;
    public static final int STATE = 8;
    public static final int CORRECTION = 9;

    public static final int KINDS = 10;

    private static final int CAPACITY = 4096;
    private static final int MASK = CAPACITY - 1;
    private static final int MAX_DUMP = 1200;

    private static final long[] TICK = new long[CAPACITY];
    private static final double[] ACTUAL = new double[CAPACITY];
    private static final double[] LIMIT = new double[CAPACITY];
    private static final double[] EXTRA = new double[CAPACITY];
    private static final int[] KIND = new int[CAPACITY];
    private static final int[] TOTALS = new int[KINDS];

    private static int head;
    private static int size;
    private static int written;

    private MotionAudit() {
    }

    public static void reset() {
        head = 0;
        size = 0;
        written = 0;
        for (int i = 0; i < KINDS; i++) {
            TOTALS[i] = 0;
        }
    }

    // Вызывается на каждом тике: только записи в примитивные массивы, ни одной аллокации.
    public static void record(int kind, long tick, double actual, double limit, double extra) {
        int index = head;
        KIND[index] = kind;
        TICK[index] = tick;
        ACTUAL[index] = actual;
        LIMIT[index] = limit;
        EXTRA[index] = extra;

        head = (index + 1) & MASK;
        if (size < CAPACITY) {
            size++;
        }
        if (kind >= 0 && kind < KINDS) {
            TOTALS[kind]++;
        }
        written++;
    }

    public static int size() {
        return size;
    }

    public static int written() {
        return written;
    }

    public static int dropped() {
        return written > CAPACITY ? written - CAPACITY : 0;
    }

    public static int count(int kind) {
        return kind >= 0 && kind < KINDS ? TOTALS[kind] : 0;
    }

    public static int flags() {
        return TOTALS[FLAG_ROLLBACK] + TOTALS[FLAG_LIMIT];
    }

    public static String label(int kind) {
        return switch (kind) {
            case FLAG_ROLLBACK -> "FLAG_ROLLBACK";
            case FLAG_LIMIT -> "FLAG_LIMIT";
            case PAUSE -> "PAUSE";
            case CEILING_DOWN -> "CEILING-DOWN";
            case CEILING_UP -> "CEILING-UP";
            case GRACE -> "GRACE";
            case SKIP -> "SKIP";
            case STATE -> "STATE";
            case CORRECTION -> "CORRECTION";
            default -> "SAMPLE";
        };
    }

    public static void renderBody(StringBuilder out, long lastTick) {
        int count = size;
        int oldest = head - count;
        int shown = 0;

        for (int i = 0; i < count; i++) {
            int index = (oldest + i) & MASK;
            long tick = TICK[index];
            if (tick > lastTick) {
                break;
            }

            int kind = KIND[index];
            out.append(tick).append(" | ").append(label(kind));
            if (kind != STATE) {
                out.append(" | cur=").append(fixed(ACTUAL[index], 3))
                        .append(" lim=").append(fixed(LIMIT[index], 3));
            }
            if (kind == FLAG_ROLLBACK || kind == CORRECTION) {
                out.append(" setback=").append(fixed(EXTRA[index], 3));
            } else if (kind == GRACE) {
                out.append(" hp=").append(fixed(EXTRA[index], 1));
            } else if (kind == FLAG_LIMIT) {
                out.append(" overrun=").append(fixed(EXTRA[index], 3));
            } else if (kind == CEILING_DOWN || kind == CEILING_UP) {
                out.append(" ceiling=").append(fixed(EXTRA[index], 3));
            }
            out.append('\n');

            if (++shown >= MAX_DUMP) {
                out.append("... обрезано, всего строк: ").append(written).append('\n');
                break;
            }
        }
    }

    public static void renderTotals(StringBuilder out) {
        for (int kind = 0; kind < KINDS; kind++) {
            int total = TOTALS[kind];
            if (total > 0) {
                out.append(label(kind)).append('=').append(total).append(' ');
            }
        }
        out.setLength(Math.max(0, out.length() - 1));
        out.append('\n');
    }

    // Ручное форматирование вместо String.format: дёргается пачкой при выгрузке лога.
    public static String fixed(double value, int digits) {
        long factor = 1L;
        for (int i = 0; i < digits; i++) {
            factor *= 10L;
        }

        boolean negative = value < 0.0;
        long scaled = Math.round(negative ? -value * factor : value * factor);
        long whole = scaled / factor;
        long fraction = scaled % factor;

        StringBuilder builder = new StringBuilder(16);
        if (negative) {
            builder.append('-');
        }
        builder.append(whole).append('.');
        for (long pad = factor / 10L; pad > 0L; pad /= 10L) {
            builder.append((fraction / pad) % 10L);
        }
        return builder.toString();
    }
}
