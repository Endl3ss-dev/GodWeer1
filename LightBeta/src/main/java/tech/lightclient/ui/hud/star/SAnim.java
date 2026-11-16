package tech.lightclient.ui.hud.star;

public class SAnim {
    private long duration;
    private float value;
    private SEasing easing;
    private long startTime;
    private float startValue;
    private float targetValue;

    public SAnim(long duration, float initialValue, SEasing easing) {
        this.duration = duration;
        this.easing = easing;
        this.value = initialValue;
        this.startValue = initialValue;
        this.targetValue = initialValue;
    }

    public SAnim(long duration, SEasing easing) {
        this(duration, 0.0f, easing);
    }

    public void update(boolean bool) {
        update(bool ? 1.0f : 0.0f);
    }

    public float update(float newValue) {
        long currentTime = System.currentTimeMillis();
        if (newValue != this.targetValue) {
            this.startValue = this.value;
            this.targetValue = newValue;
            this.startTime = currentTime;
        }

        long elapsed = currentTime - this.startTime;
        if (elapsed >= this.duration) {
            this.value = this.targetValue;
            return this.value;
        }
        float progress = (float) elapsed / (float) this.duration;
        float easedProgress = this.easing.ease(progress, 0.0f, 1.0f, 1.0f);
        this.value = this.startValue + (this.targetValue - this.startValue) * easedProgress;
        return this.value;
    }

    public void setValue(float newValue) {
        this.value = newValue;
        this.startValue = newValue;
        this.targetValue = newValue;
    }

    public float getValue() {
        return this.value;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setEasing(SEasing easing) {
        this.easing = easing;
    }
}
