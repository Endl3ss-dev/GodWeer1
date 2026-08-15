package godweer.utils.animation;

public final class Easings {

    public static final double C1 = 1.70158D;
    public static final double C2 = 2.5949095D;
    public static final double C3 = 2.70158D;
    public static final double C4 = 2.0943951023931953D;
    public static final double C5 = 1.3962634015954636D;

    public static final Easing LINEAR = value -> value;

    public static final Easing QUAD_IN = powIn(2.0D);
    public static final Easing QUAD_OUT = powOut(2.0D);
    public static final Easing QUAD_IN_OUT = powInOut(2.0D);

    public static final Easing CUBIC_IN = powIn(3.0D);
    public static final Easing CUBIC_OUT = powOut(3.0D);
    public static final Easing CUBIC_IN_OUT = powInOut(3.0D);

    public static final Easing QUART_IN = powIn(4.0D);
    public static final Easing QUART_OUT = powOut(4.0D);
    public static final Easing QUART_IN_OUT = powInOut(4.0D);

    public static final Easing QUINT_IN = powIn(5.0D);
    public static final Easing QUINT_OUT = powOut(5.0D);
    public static final Easing QUINT_IN_OUT = powInOut(5.0D);

    public static final Easing SINE_IN = value -> 1.0D - Math.cos(value * Math.PI / 2.0D);
    public static final Easing SINE_OUT = value -> Math.sin(value * Math.PI / 2.0D);
    public static final Easing SINE_IN_OUT = value -> -(Math.cos(Math.PI * value) - 1.0D) / 2.0D;

    public static final Easing CIRC_IN = value -> 1.0D - Math.sqrt(1.0D - value * value);
    public static final Easing CIRC_OUT = value -> Math.sqrt(1.0D - (value - 1.0D) * (value - 1.0D));
    public static final Easing CIRC_IN_OUT = value -> value < 0.5D
            ? (1.0D - Math.sqrt(1.0D - 4.0D * value * value)) / 2.0D
            : (Math.sqrt(1.0D - (-2.0D * value + 2.0D) * (-2.0D * value + 2.0D)) + 1.0D) / 2.0D;

    public static final Easing ELASTIC_IN = value -> value != 0.0D && value != 1.0D
            ? Math.pow(-2.0D, 10.0D * value - 10.0D) * Math.sin((value * 10.0D - 10.75D) * C4)
            : value;
    public static final Easing ELASTIC_OUT = value -> value != 0.0D && value != 1.0D
            ? Math.pow(2.0D, -10.0D * value) * Math.sin((value * 10.0D - 0.75D) * C4) + 1.0D
            : value;
    public static final Easing ELASTIC_IN_OUT = value -> {
        if (value == 0.0D || value == 1.0D) {
            return value;
        }
        return value < 0.5D
                ? -(Math.pow(2.0D, 20.0D * value - 10.0D) * Math.sin((20.0D * value - 11.125D) * C5)) / 2.0D
                : Math.pow(2.0D, -20.0D * value + 10.0D) * Math.sin((20.0D * value - 11.125D) * C5) / 2.0D + 1.0D;
    };

    public static final Easing EXPO_IN = value -> value != 0.0D ? Math.pow(2.0D, 10.0D * value - 10.0D) : value;
    public static final Easing EXPO_OUT = value -> value != 1.0D ? 1.0D - Math.pow(2.0D, -10.0D * value) : value;
    public static final Easing EXPO_IN_OUT = value -> {
        if (value == 0.0D || value == 1.0D) {
            return value;
        }
        return value < 0.5D
                ? Math.pow(2.0D, 20.0D * value - 10.0D) / 2.0D
                : (2.0D - Math.pow(2.0D, -20.0D * value + 10.0D)) / 2.0D;
    };

    public static final Easing BACK_IN = value -> C3 * value * value * value - C1 * value * value;
    public static final Easing BACK_OUT = value -> {
        double shifted = value - 1.0D;
        return 1.0D + C3 * shifted * shifted * shifted + C1 * shifted * shifted;
    };
    public static final Easing BACK_IN_OUT = value -> value < 0.5D
            ? (2.0D * value) * (2.0D * value) * (7.189819D * value - C2) / 2.0D
            : ((2.0D * value - 2.0D) * (2.0D * value - 2.0D) * (3.5949095D * (value * 2.0D - 2.0D) + C2) + 2.0D) / 2.0D;

    public static final Easing BOUNCE_OUT = value -> {
        double n1 = 7.5625D;
        double d1 = 2.75D;
        if (value < 1.0D / d1) {
            return n1 * value * value;
        }
        if (value < 2.0D / d1) {
            double shifted = value - 1.5D / d1;
            return n1 * shifted * shifted + 0.75D;
        }
        if (value < 2.5D / d1) {
            double shifted = value - 2.25D / d1;
            return n1 * shifted * shifted + 0.9375D;
        }
        double shifted = value - 2.625D / d1;
        return n1 * shifted * shifted + 0.984375D;
    };
    public static final Easing BOUNCE_IN = value -> 1.0D - BOUNCE_OUT.ease(1.0D - value);
    public static final Easing BOUNCE_IN_OUT = value -> value < 0.5D
            ? (1.0D - BOUNCE_OUT.ease(1.0D - 2.0D * value)) / 2.0D
            : (1.0D + BOUNCE_OUT.ease(2.0D * value - 1.0D)) / 2.0D;

    private Easings() {
    }

    public static Easing powIn(double n) {
        return value -> Math.pow(value, n);
    }

    public static Easing powOut(double n) {
        return value -> 1.0D - Math.pow(1.0D - value, n);
    }

    public static Easing powInOut(double n) {
        return value -> value < 0.5D
                ? Math.pow(2.0D, n - 1.0D) * Math.pow(value, n)
                : 1.0D - Math.pow(-2.0D * value + 2.0D, n) / 2.0D;
    }
}
