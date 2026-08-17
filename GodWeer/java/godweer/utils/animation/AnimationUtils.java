package godweer.utils.animation;

import net.minecraft.util.Mth;

public class AnimationUtils {

    private float currentValue;
    private float targetValue;
    private float fromValue;
    private float speed;
    private Easing easing;
    private long startTime;
    private double duration;
    private boolean running;

    public AnimationUtils(float initialValue, float speed, Easing easing) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.fromValue = initialValue;
        this.speed = speed;
        this.easing = easing != null ? easing : Easings.LINEAR;
        this.startTime = 0L;
        this.duration = 0.0D;
        this.running = false;
    }

    public AnimationUtils(float initialValue, float speed) {
        this(initialValue, speed, Easings.LINEAR);
    }

    // Перезапуск только при смене цели: иначе анимация каждый кадр стартовала бы заново.
    public void update(float target) {
        if (this.targetValue != target || !this.running) {
            this.targetValue = target;
            this.fromValue = this.currentValue;
            this.startTime = System.nanoTime();
            this.duration = 1.0D / this.speed * 2.0D;
            this.running = true;
        }

        if (isDone()) {
            this.currentValue = this.targetValue;
            this.running = false;
            return;
        }

        float eased = (float) this.easing.ease(calculatePart());
        this.currentValue = Mth.lerp(eased, this.fromValue, this.targetValue);
    }

    private double calculatePart() {
        if (!this.running) {
            return 1.0D;
        }
        double elapsed = (System.nanoTime() - this.startTime) / 1_000_000_000.0D;
        return Mth.clamp(elapsed / this.duration, 0.0D, 1.0D);
    }

    public float getValue() {
        return this.currentValue;
    }

    public void setValue(float value) {
        this.currentValue = value;
        this.targetValue = value;
        this.fromValue = value;
        this.running = false;
    }

    public float getTarget() {
        return this.targetValue;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        this.duration = 1.0D / speed;
    }

    public Easing getEasing() {
        return this.easing;
    }

    public void setEasing(Easing easing) {
        this.easing = easing != null ? easing : Easings.LINEAR;
    }

    public boolean isDone() {
        return calculatePart() >= 1.0D;
    }

    public boolean isAlive() {
        return !isDone();
    }
}
