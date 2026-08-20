/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.Optional;
import minced.core.Easing;

public class Animation {
    boolean paused;
    float value;
    boolean finished;
    boolean repeat;
    boolean reverse;
    Runnable onFinish;
    float duration;
    long startTime = System.currentTimeMillis();
    int repeatCount;
    float target;
    float animatedValue;
    Runnable onUpdate;
    Runnable onStart;
    Easing easing;

    public void setPaused(boolean param1) {
        this.paused = param1;
    }

    public float getValue() {
        return this.value;
    }

    public void setFinished(boolean param1) {
        this.finished = param1;
    }

    public void setRepeat(boolean param1) {
        this.repeat = param1;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setOnFinish(Runnable param1) {
        this.onFinish = param1;
    }

    public void setDuration(float param1) {
        this.duration = param1;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setReverse(boolean param1) {
        this.reverse = param1;
    }

    private boolean checkFinished(long param1) {
        boolean bl = this.finished = (float)param1 - this.duration > (float)this.startTime;
        if (this.finished) {
            this.animatedValue = this.target;
            Optional.ofNullable(this.onFinish).ifPresent(Runnable::run);
            if (this.repeat) {
                this.handleRepeat();
            }
            return true;
        }
        return false;
    }

    public void setRepeatCount(int param1) {
        this.repeatCount = param1;
    }

    public void setTarget(float param1) {
        this.target = param1;
    }

    public Runnable getOnUpdate() {
        return this.onUpdate;
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    public void setValue(float param1) {
        this.value = param1;
    }

    private void handleRepeat() {
        if (--this.repeatCount > 0 || this.repeatCount == -1) {
            this.restart();
        } else {
            this.finished = true;
        }
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
        this.finished = false;
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void setStartTime(long param1) {
        this.startTime = param1;
    }

    public void setOnUpdate(Runnable param1) {
        this.onUpdate = param1;
    }

    public Runnable getOnStart() {
        return this.onStart;
    }

    public void setOnStart(Runnable param1) {
        this.onStart = param1;
    }

    private void startTransition(long param1, float param3) {
        this.target = param3;
        this.startTime = param1;
        this.value = this.animatedValue;
        Optional.ofNullable(this.onStart).ifPresent(Runnable::run);
    }

    public Animation(Easing param1, float param2, long param3, boolean param5, boolean param6, int param7) {
        this.easing = param1;
        this.duration = Math.max(0L, param3);
        this.value = param2;
        this.animatedValue = param2;
        this.reverse = param5;
        this.repeat = param6;
        this.repeatCount = Math.max(0, param7);
        this.finished = false;
        this.paused = false;
    }

    public float getTarget() {
        return this.target;
    }

    public void animate(float param1) {
        if (this.paused) {
            return;
        }
        long v2 = System.currentTimeMillis();
        if (this.target != param1) {
            this.startTransition(v2, param1);
        } else if (this.checkFinished(v2)) {
            return;
        }
        float v4 = this.getProgress();
        float v5 = (float)this.easing.method3420(v4);
        float v6 = this.reverse ? this.value - param1 : param1 - this.value;
        this.animatedValue = this.value + v6 * v5;
        Optional.ofNullable(this.onUpdate).ifPresent(Runnable::run);
    }

    public void setAnimatedValue(float param1) {
        this.animatedValue = param1;
    }

    public Runnable I() {
        return this.onFinish;
    }

    public void setEasing(Easing param1) {
        this.easing = param1;
    }

    public float getAnimatedValue() {
        return this.animatedValue;
    }

    public float getProgress() {
        long v1 = System.currentTimeMillis() - this.startTime;
        return Math.min(1.0f, (float)v1 / this.duration);
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Animation(Easing param1, float param2, long param3) {
        this(param1, param2, param3, false, false, 0);
    }

    public Animation(Easing param1, long param2) {
        this(param1, 0.0f, param2, false, false, 0);
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isAnimating() {
        return (double)this.getProgress() < 1.0;
    }

    public Animation(Easing param1, long param2, boolean param4, boolean param5, int param6) {
        this(param1, 0.0f, param2, param4, param5, param6);
    }

    public float getDuration() {
        return this.duration;
    }

    public Easing getEasing() {
        return this.easing;
    }
}



