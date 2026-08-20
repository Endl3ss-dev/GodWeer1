package org.privates.client.utils.other;


import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;

public class InfinityAnimation {
    private Animation animation;
    private Easing easing;
    private float endpoint;
    private float output;

    public InfinityAnimation() {
        this.easing = Easings.LINEAR;
        this.animation = new Animation()
                .setSize(0.0f)
                .setSpeed(0)
                .setForward(false)
                .setEasing(this.easing);
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public float animate(float endpoint, int speed) {
        int actualSpeed = Math.max(1, speed);
        this.output = this.endpoint - this.animation.get();
        this.endpoint = endpoint;

        if (this.output != this.endpoint - endpoint) {
            this.animation = new Animation()
                    .setSize(this.endpoint - this.output)
                    .setSpeed(actualSpeed)
                    .setForward(false)
                    .setEasing(this.easing);
        }

        return this.output;
    }

    public boolean finished() {
        return (this.output == this.endpoint) ||
                this.animation.finished() ||
                this.animation.finished(false);
    }

    public float get() {
        this.output = this.endpoint - this.animation.get();
        return this.output;
    }

    public InfinityAnimation easing(Easing easing) {
        this.easing = easing;
        return this;
    }
}