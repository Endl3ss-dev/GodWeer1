package tech.lightclient.ui.hud.star;

public interface SEasing {
    SEasing BAKEK = generate(0.45f, 1.45f, 0.49f, 1.15f);
    SEasing BAKEK_SIZE = generate(0.27f, 1.09f, 0.49f, 1.06f);
    SEasing BAKEK_BACK = generate(0.62f, -0.16f, 0.8f, 0.37f);
    SEasing FIGMA_EASE_IN_OUT = generate(0.42f, 0.0f, 0.58f, 1.0f);
    SEasing SMOOTH_STEP = (t, b, c, d) -> {
        float x = c * t / d + b;
        return (float) (-2.0 * Math.pow(x, 3.0) + 3.0 * Math.pow(x, 2.0));
    };
    SEasing LINEAR = (t, b, c, d) -> c * t / d + b;
    SEasing SINE_IN_OUT = (t, b, c, d) -> -c / 2.0f * ((float) Math.cos(Math.PI * t / d) - 1.0f) + b;

    float ease(float t, float b, float c, float d);

    static SEasing generate(double x1, double y1, double x2, double y2) {
        final float fx1 = (float) x1;
        final float fy1 = (float) y1;
        final float fx2 = (float) x2;
        final float fy2 = (float) y2;
        return new SEasing() {
            @Override
            public float ease(float t, float b, float c, float d) {
                if (d <= 0.0f || t <= 0.0f) {
                    return b;
                }
                if (t >= d) {
                    return b + c;
                }
                float progress = t / d;
                float tBez = solveTBez(fx1, fx2, progress);
                float y = bezierY(tBez, fy1, fy2);
                return b + c * y;
            }

            private float solveTBez(float ax1, float ax2, float progress) {
                float t = progress;
                for (int i = 0; i < 8; i++) {
                    float x = bezierX(t, ax1, ax2);
                    float dx = bezierDX(t, ax1, ax2);
                    if (Math.abs(x - progress) < 1.0E-5f || Math.abs(dx) < 1.0E-6f) {
                        break;
                    }
                    t -= (x - progress) / dx;
                    t = Math.max(0.0f, Math.min(1.0f, t));
                }
                return t;
            }

            private float bezierX(float t, float ax1, float ax2) {
                return 3.0f * (1.0f - t) * (1.0f - t) * t * ax1 + 3.0f * (1.0f - t) * t * t * ax2 + t * t * t;
            }

            private float bezierDX(float t, float ax1, float ax2) {
                return 3.0f * ((1.0f - t) * (1.0f - 3.0f * t) * ax1 + (2.0f * t - 3.0f * t * t) * ax2) + 3.0f * t * t;
            }

            private float bezierY(float t, float ay1, float ay2) {
                return 3.0f * (1.0f - t) * (1.0f - t) * t * ay1 + 3.0f * (1.0f - t) * t * t * ay2 + t * t * t;
            }
        };
    }
}
