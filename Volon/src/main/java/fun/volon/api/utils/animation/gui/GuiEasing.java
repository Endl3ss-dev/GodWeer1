package fun.volon.api.utils.animation.gui;

import lombok.Getter;
import lombok.Setter;

public interface GuiEasing {
    GuiEasing SMOOTH_STEP = (t, b, c, d) -> {
        float x = c * t / d + b;
        return (float) (-2 * Math.pow(x, 3) + (3 * Math.pow(x, 2)));
    };

    GuiEasing BOTH_CUBIC = (t, b, c, d) -> {
        float x = c * t / d + b;
        return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
    };

    GuiEasing LINEAR = (t, b, c, d) -> c * t / d + b;

    GuiEasing QUAD_IN = (t, b, c, d) -> c * (t /= d) * t + b;
    GuiEasing QUAD_OUT = (t, b, c, d) -> -c * (t /= d) * (t - 2) + b;
    GuiEasing QUAD_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    };

    GuiEasing CUBIC_IN = (t, b, c, d) -> c * (t /= d) * t * t + b;
    GuiEasing CUBIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t + 1) + b;
    GuiEasing CUBIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    };

    GuiEasing QUARTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t + b;
    GuiEasing QUARTIC_OUT = (t, b, c, d) -> -c * ((t = t / d - 1) * t * t * t - 1) + b;
    GuiEasing QUARTIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    };

    GuiEasing QUINTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t * t + b;
    GuiEasing QUINTIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t * t * t + 1) + b;
    GuiEasing QUINTIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    };

    GuiEasing SINE_IN = (t, b, c, d) -> -c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;
    GuiEasing SINE_OUT = (t, b, c, d) -> c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
    GuiEasing SINE_IN_OUT = (t, b, c, d) -> -c / 2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;

    GuiEasing EXPO_IN = (t, b, c, d) -> (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;
    GuiEasing EXPO_OUT = (t, b, c, d) -> (t == d) ? b + c : c * (-(float) Math.pow(2, -10 * t / d) + 1) + b;
    GuiEasing EXPO_IN_OUT = (t, b, c, d) -> {
        if (t == 0) return b;
        if (t == d) return b + c;
        if ((t /= d / 2) < 1) return c / 2 * (float) Math.pow(2, 10 * (t - 1)) + b;
        return c / 2 * (-(float) Math.pow(2, -10 * --t) + 2) + b;
    };

    GuiEasing CIRC_IN = (t, b, c, d) -> -c * ((float) Math.sqrt(1 - (t /= d) * t) - 1) + b;
    GuiEasing CIRC_OUT = (t, b, c, d) -> c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
    GuiEasing CIRC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return -c / 2 * ((float) Math.sqrt(1 - t * t) - 1) + b;
        return c / 2 * ((float) Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    };

    Elastic ELASTIC_IN = new ElasticIn();
    Elastic ELASTIC_OUT = new ElasticOut();
    Elastic ELASTIC_IN_OUT = new ElasticInOut();

    Back BACK_IN = new BackIn();
    Back BACK_OUT = new BackOut();
    Back BACK_OUT_BIGGY_OVERSHOOT = new BackOut(5.70158f);
    Back BACK_IN_OUT = new BackInOut();

    GuiEasing BOUNCE_OUT = (t, b, c, d) -> {
        if ((t /= d) < (1 / 2.75f)) {
            return c * (7.5625f * t * t) + b;
        } else if (t < (2 / 2.75f)) {
            return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + .75f) + b;
        } else if (t < (2.5f / 2.75f)) {
            return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + .9375f) + b;
        } else {
            return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + .984375f) + b;
        }
    };

    GuiEasing BOUNCE_IN = (t, b, c, d) -> c - GuiEasing.BOUNCE_OUT.ease(d - t, 0, c, d) + b;
    GuiEasing BOUNCE_IN_OUT = (t, b, c, d) -> {
        if (t < d / 2) return GuiEasing.BOUNCE_IN.ease(t * 2, 0, c, d) * .5f + b;
        return GuiEasing.BOUNCE_OUT.ease(t * 2 - d, 0, c, d) * .5f + c * .5f + b;
    };

    float ease(float t, float b, float c, float d);

    @Setter
    @Getter
    abstract class Elastic implements GuiEasing {
        private float amplitude;
        private float period;

        public Elastic(float amplitude, float period) {
            this.amplitude = amplitude;
            this.period = period;
        }

        public Elastic() {
            this(-1f, 0f);
        }
    }

    class ElasticIn extends Elastic {
        public ElasticIn(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticIn() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d) == 1) return b + c;
            if (p == 0) p = d * .3f;
            float s;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            return -(a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
        }
    }

    class ElasticOut extends Elastic {
        public ElasticOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticOut() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d) == 1) return b + c;
            if (p == 0) p = d * .3f;
            float s;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
        }
    }

    class ElasticInOut extends Elastic {
        public ElasticInOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticInOut() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d / 2) == 2) return b + c;
            if (p == 0) p = d * (.3f * 1.5f);
            float s;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4f;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            if (t < 1)
                return -.5f * (a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
            return a * (float) Math.pow(2, -10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) * .5f + c + b;
        }
    }

    @Setter
    @Getter
    abstract class Back implements GuiEasing {
        public static final float DEFAULT_OVERSHOOT = 1.70158f;

        private float overshoot;

        public Back() {
            this(DEFAULT_OVERSHOOT);
        }

        public Back(float overshoot) {
            this.overshoot = overshoot;
        }
    }

    class BackIn extends Back {
        public BackIn() {
            super();
        }

        public BackIn(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c * (t /= d) * t * ((s + 1) * t - s) + b;
        }
    }

    class BackOut extends Back {
        public BackOut() {
            super();
        }

        public BackOut(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
        }
    }

    class BackInOut extends Back {
        public BackInOut() {
            super();
        }

        public BackInOut(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            if ((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= 1.525F) + 1) * t - s)) + b;
            return c / 2 * ((t -= 2) * t * (((s *= 1.525F) + 1) * t + s) + 2) + b;
        }
    }
}
