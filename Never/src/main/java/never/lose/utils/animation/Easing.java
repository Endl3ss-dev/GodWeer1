package never.lose.utils.animation;

public interface Easing {

    Easing LINEAR = (t, b, c, d) -> c * t / d + b;

    Easing CUBIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t + 1) + b;

    Easing CUBIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    };

    Easing QUINTIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t * t * t + 1) + b;

    Easing BACK_OUT = (t, b, c, d) -> {
        float s = 1.70158f;
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    };

    Easing BOUNCE_OUT = (t, b, c, d) -> {
        if ((t /= d) < (1 / 2.75f)) {
            return c * (7.5625f * t * t) + b;
        } else if (t < (2 / 2.75f)) {
            return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + 0.75f) + b;
        } else if (t < (2.5f / 2.75f)) {
            return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + 0.9375f) + b;
        }
        return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + 0.984375f) + b;
    };

    float ease(float t, float b, float c, float d);
}
