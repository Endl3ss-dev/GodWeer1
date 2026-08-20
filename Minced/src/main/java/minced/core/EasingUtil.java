/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import java.util.function.Predicate;
import net.minecraft.util.math.MathHelper;

public class EasingUtil {
    public boolean rayAligned;
    float distance;
    static float field9259;
    static float field9260;
    float yawError;
    static float field9261;
    float pitchError;
    static float field9262;
    float targetSpeed;
    static float field9263;
    float yawGain;
    static float field9264;
    static float field9265;
    float pitchGain;
    static float field9266;
    static float field9267;
    float yawStepAbs;
    static float field9268;
    float pitchStepAbs;
    static float field9269;

    public void method2679() {
        this.distance = MathHelper.clamp((float)this.distance, (float)field9259, (float)field9260);
        this.yawError = MathHelper.clamp((float)this.yawError, (float)0.0f, (float)field9261);
        this.pitchError = MathHelper.clamp((float)this.pitchError, (float)0.0f, (float)field9262);
        this.targetSpeed = MathHelper.clamp((float)this.targetSpeed, (float)0.0f, (float)field9263);
        this.yawGain = MathHelper.clamp((float)this.yawGain, (float)field9264, (float)field9265);
        this.pitchGain = MathHelper.clamp((float)this.pitchGain, (float)field9266, (float)field9267);
        this.yawStepAbs = MathHelper.clamp((float)this.yawStepAbs, (float)0.0f, (float)field9268);
        this.pitchStepAbs = MathHelper.clamp((float)this.pitchStepAbs, (float)0.0f, (float)field9269);
    }

    public boolean method2680() {
        return Float.isFinite(this.distance) && Float.isFinite(this.yawError) && Float.isFinite(this.pitchError) && Float.isFinite(this.targetSpeed) && Float.isFinite(this.yawGain) && Float.isFinite(this.pitchGain) && Float.isFinite(this.yawStepAbs) && Float.isFinite(this.pitchStepAbs) && this.distance > 0.0f && this.yawError >= 0.0f && this.pitchError >= 0.0f && this.yawGain > 0.0f && this.pitchGain > 0.0f && this.yawStepAbs >= 0.0f && this.pitchStepAbs >= 0.0f;
    }

    static {
        field9259 = 0.0f;
        field9260 = 0.0f;
        field9261 = 0.0f;
        field9262 = 0.0f;
        field9263 = 0.0f;
        field9264 = 0.0f;
        field9265 = 0.0f;
        field9266 = 0.0f;
        field9267 = 0.0f;
        field9268 = 0.0f;
        field9269 = 0.0f;
    }
}

