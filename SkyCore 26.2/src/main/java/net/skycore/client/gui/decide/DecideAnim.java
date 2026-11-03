package net.skycore.client.gui.decide;

public final class DecideAnim {
    private DecideAnim() {
    }

    public enum Easing {
        LINEAR,
        SINE_OUT,
        SINE_IN,
        QUAD_OUT,
        EASE_IN_OUT_QUAD;

        public float apply(float x) {
            if (x <= 0.0f) {
                return 0.0f;
            }
            if (x >= 1.0f) {
                return 1.0f;
            }
            return switch (this) {
                case LINEAR -> x;
                case SINE_OUT -> (float) Math.sin((x * Math.PI) * 0.5);
                case SINE_IN -> 1.0f - (float) Math.cos((x * Math.PI) * 0.5);
                case QUAD_OUT -> 1.0f - (1.0f - x) * (1.0f - x);
                case EASE_IN_OUT_QUAD -> x < 0.5f
                        ? 2.0f * x * x
                        : 1.0f - (float) Math.pow(-2.0f * x + 2.0f, 2.0) * 0.5f;
            };
        }
    }

    /**
     * Exact zero-allocation port of Decide's satoshi EaseInOutQuad(durationMs, endPoint).
     */
    public static final class SatoshiAnim {
        private final long durationMs;
        private final float endPoint;
        private boolean forwards = true;
        private long startTimeMs = System.currentTimeMillis();

        public SatoshiAnim(long durationMs, float endPoint) {
            this.durationMs = Math.max(1L, durationMs);
            this.endPoint = endPoint;
        }

        public SatoshiAnim(long durationMs, float endPoint, boolean initialForwards) {
            this.durationMs = Math.max(1L, durationMs);
            this.endPoint = endPoint;
            this.forwards = initialForwards;
            this.startTimeMs = System.currentTimeMillis() - this.durationMs;
        }

        public void resetToZero() {
            this.forwards = false;
            this.startTimeMs = System.currentTimeMillis() - this.durationMs;
        }

        public void restartForwards() {
            this.forwards = true;
            this.startTimeMs = System.currentTimeMillis();
        }

        public void setDirection(boolean forward) {
            if (this.forwards == forward) {
                return;
            }
            long now = System.currentTimeMillis();
            long elapsed = Math.min(this.durationMs, Math.max(0L, now - this.startTimeMs));
            long remaining = this.durationMs - elapsed;
            this.forwards = forward;
            this.startTimeMs = now - remaining;
        }

        public float getOutput() {
            long elapsed = System.currentTimeMillis() - this.startTimeMs;
            if (this.forwards) {
                if (elapsed >= this.durationMs) {
                    return this.endPoint;
                }
                float progress = Math.max(0.0f, (float) elapsed / (float) this.durationMs);
                return Easing.EASE_IN_OUT_QUAD.apply(progress) * this.endPoint;
            }
            if (elapsed >= this.durationMs) {
                return 0.0f;
            }
            float rev = Math.max(0.0f, 1.0f - (float) elapsed / (float) this.durationMs);
            return Easing.EASE_IN_OUT_QUAD.apply(rev) * this.endPoint;
        }
    }

    /**
     * Exact zero-allocation port of Decide's ru.decide.utils.animation.Animation.
     */
    public static final class SmoothAnim {
        private long startMs;
        private float durationSec;
        private float fromValue;
        private float toValue;
        private float value;
        private Easing easing = Easing.LINEAR;

        public void setValue(float v) {
            this.fromValue = v;
            this.toValue = v;
            this.value = v;
            this.durationSec = 0.0f;
            this.startMs = System.currentTimeMillis();
        }

        public void run(float valueTo, float durationSeconds, Easing easing) {
            if (Math.abs(this.toValue - valueTo) < 1.0E-5f && !isFinished()) {
                return;
            }
            update();
            this.fromValue = this.value;
            this.toValue = valueTo;
            this.durationSec = Math.max(0.001f, durationSeconds);
            this.easing = easing == null ? Easing.LINEAR : easing;
            this.startMs = System.currentTimeMillis();
        }

        public void update() {
            if (this.durationSec <= 0.0f) {
                this.value = this.toValue;
                return;
            }
            float part = (float) (System.currentTimeMillis() - this.startMs) / (this.durationSec * 1000.0f);
            if (part >= 1.0f) {
                this.value = this.toValue;
                return;
            }
            if (part <= 0.0f) {
                this.value = this.fromValue;
                return;
            }
            float eased = this.easing.apply(part);
            this.value = this.fromValue + (this.toValue - this.fromValue) * eased;
        }

        public float get() {
            return this.value;
        }

        public boolean isFinished() {
            if (this.durationSec <= 0.0f) {
                return true;
            }
            return (float) (System.currentTimeMillis() - this.startMs) >= this.durationSec * 1000.0f;
        }
    }
}
