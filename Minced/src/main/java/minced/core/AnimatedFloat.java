/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import minced.core.Easings;
import minced.core.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class AnimatedFloat {
    boolean animating;
    long animationStartNanos;
    static double NANOS_PER_SECOND;
    double animationDurationNanos;
    Easing easing;
    float currentValue;
    float targetValue;
    float startValue;
    float speed;
    static double DURATION_SCALE;
    static float FPS_STEP_SCALE;

    private double getProgress() {
        if (!this.animating) {
            return 1.0;
        }
        long v1 = System.nanoTime();
        double v3 = (double)(v1 - this.animationStartNanos) / NANOS_PER_SECOND;
        return MathHelper.clamp((double)(v3 / this.animationDurationNanos), (double)0.0, (double)1.0);
    }

    public void setEasing(Easing param1) {
        this.easing = param1 != null ? param1 : Easings.field11952;
    }

    public AnimatedFloat(float param1, float param2) {
        this(param1, param2, Easings.field11952);
    }

    public boolean isAnimating() {
        return !this.isFinished();
    }

    public float getValue() {
        return this.currentValue;
    }

    public boolean isFinished() {
        return this.getProgress() >= 1.0;
    }

    public float getTarget() {
        return this.targetValue;
    }

    public void setTarget(float param1) {
        if (this.targetValue != param1 || !this.animating) {
            this.targetValue = param1;
            this.startValue = this.currentValue;
            this.animationStartNanos = System.nanoTime();
            this.animationDurationNanos = 1.0 / (double)this.speed * DURATION_SCALE;
            this.animating = true;
        }
        if (this.isFinished()) {
            this.currentValue = this.targetValue;
            this.animating = false;
            return;
        }
        double v2 = this.getProgress();
        float v4 = (float)this.easing.method3420(v2);
        this.currentValue = MathHelper.lerp((float)v4, (float)this.startValue, (float)this.targetValue);
    }

    public AnimatedFloat(float param1, float param2, Easing param3) {
        this.currentValue = param1;
        this.targetValue = param1;
        this.startValue = param1;
        this.speed = param2;
        this.easing = param3 != null ? param3 : Easings.field11952;
        this.animationStartNanos = 0L;
        this.animationDurationNanos = 0.0;
        this.animating = false;
    }

    public void setSpeed(float param1) {
        this.speed = param1;
        this.animationDurationNanos = 1.0 / (double)param1;
    }

    public void setValue(float param1) {
        this.currentValue = param1;
        this.targetValue = param1;
        this.startValue = param1;
        this.animating = false;
    }

    public static float approach(float param0, float param1) {
        if (param0 == param1) {
            return param0;
        }
        MinecraftClient v2 = MinecraftClient.getInstance();
        int v3 = v2 != null ? Math.max(v2.getCurrentFps(), 5) : 60;
        float v4 = 1.0f / (float)v3;
        float v5 = (param1 - param0) / (float)v3 * FPS_STEP_SCALE;
        if (v5 > 0.0f) {
            v5 = Math.max(v4, v5);
            v5 = Math.min(param1 - param0, v5);
        } else if (v5 < 0.0f) {
            v5 = Math.min(-v4, v5);
            v5 = Math.max(param1 - param0, v5);
        }
        return param0 + v5;
    }

    static {
        DURATION_SCALE = 2.0;
        NANOS_PER_SECOND = 1.0E9;
        FPS_STEP_SCALE = 15.0f;
    }
}



