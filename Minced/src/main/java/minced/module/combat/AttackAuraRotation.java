/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.RotationTargetUtil;
import minced.core.AuraModeHandler;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class AttackAuraRotation
implements AuraModeHandler {
    static double GLIDING_HORIZONTAL_INSET;
    static double GROUND_HORIZONTAL_INSET;
    static double GLIDING_VERTICAL_INSET;
    static double GROUND_VERTICAL_INSET;
    static double MIN_X_DIVISOR;
    static double MIN_Z_DIVISOR;
    static double MAX_X_DIVISOR;
    static double MAX_Z_DIVISOR;
    static String MODE_NAME = "Legit";
    static MinecraftClient client = MinecraftClient.getInstance();
    static double RAYCAST_DISTANCE;
    float yawProgress;
    static float ON_TARGET_MIN_YAW_DECAY;
    static float ON_TARGET_MAX_YAW_DECAY;
    float pitchProgress;
    static float ON_TARGET_MIN_PITCH_DECAY;
    static float ON_TARGET_MAX_PITCH_DECAY;
    static float GLIDING_MIN_YAW_GAIN;
    static float GLIDING_MAX_YAW_GAIN;
    static float GLIDING_MIN_PITCH_GAIN;
    static float GLIDING_MAX_PITCH_GAIN;
    static float SWIMMING_MIN_YAW_GAIN;
    static float SWIMMING_MAX_YAW_GAIN;
    static float SWIMMING_MIN_PITCH_GAIN;
    static float SWIMMING_MAX_PITCH_GAIN;
    static float GROUND_MIN_YAW_GAIN;
    static float GROUND_MAX_YAW_GAIN;
    static float GROUND_MIN_PITCH_GAIN;
    static float GROUND_MAX_PITCH_GAIN;
    static float ROTATION_SPEED;

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AttackAuraRotation.client.player == null || param2 == null) {
            return;
        }
        Vec3d v3 = AttackAuraRotation.client.player.getCameraPosVec(1.0f);
        Vec3d v4 = AttackAuraRotation.client.player.getRotationVec(1.0f);
        Vec3d v5 = v3.add(v4.multiply(RAYCAST_DISTANCE));
        Box v6 = this.getAimBox(param2);
        Optional v7 = v6.raycast(v3, v5);
        boolean v8 = v6.contains(v3);
        if (v7.isPresent() || v8) {
            this.yawProgress = MathHelper.clamp((float)(this.yawProgress - ThreadLocalRandom.current().nextFloat(ON_TARGET_MIN_YAW_DECAY, ON_TARGET_MAX_YAW_DECAY)), (float)0.0f, (float)1.0f);
            this.pitchProgress = MathHelper.clamp((float)(this.pitchProgress - ThreadLocalRandom.current().nextFloat(ON_TARGET_MIN_PITCH_DECAY, ON_TARGET_MAX_PITCH_DECAY)), (float)0.0f, (float)1.0f);
        } else if (AttackAuraRotation.client.player.isGliding()) {
            this.yawProgress = MathHelper.clamp((float)(this.yawProgress + ThreadLocalRandom.current().nextFloat(GLIDING_MIN_YAW_GAIN, GLIDING_MAX_YAW_GAIN)), (float)0.0f, (float)1.0f);
            this.pitchProgress = MathHelper.clamp((float)(this.pitchProgress + ThreadLocalRandom.current().nextFloat(GLIDING_MIN_PITCH_GAIN, GLIDING_MAX_PITCH_GAIN)), (float)0.0f, (float)1.0f);
        } else if (param2.isInSwimmingPose()) {
            this.yawProgress = MathHelper.clamp((float)(this.yawProgress + ThreadLocalRandom.current().nextFloat(SWIMMING_MIN_YAW_GAIN, SWIMMING_MAX_YAW_GAIN)), (float)0.0f, (float)1.0f);
            this.pitchProgress = MathHelper.clamp((float)(this.pitchProgress + ThreadLocalRandom.current().nextFloat(SWIMMING_MIN_PITCH_GAIN, SWIMMING_MAX_PITCH_GAIN)), (float)0.0f, (float)1.0f);
        } else {
            this.yawProgress = MathHelper.clamp((float)(this.yawProgress + ThreadLocalRandom.current().nextFloat(GROUND_MIN_YAW_GAIN, GROUND_MAX_YAW_GAIN)), (float)0.0f, (float)1.0f);
            this.pitchProgress = MathHelper.clamp((float)(this.pitchProgress + ThreadLocalRandom.current().nextFloat(GROUND_MIN_PITCH_GAIN, GROUND_MAX_PITCH_GAIN)), (float)0.0f, (float)1.0f);
        }
        Vec2f v9 = RotationTargetUtil.method1977(param2.getBoundingBox().getCenter());
        float v10 = AttackAuraRotation.client.player.getYaw();
        float v11 = AttackAuraRotation.client.player.getPitch();
        float v12 = MathHelper.wrapDegrees((float)(v9.x - v10));
        float v13 = MathHelper.wrapDegrees((float)(v9.y - v11));
        float v14 = v10 + v12 * this.yawProgress;
        float v15 = v11 + v13 * this.pitchProgress;
        AimRotation.method367(new Rotation(v14, v15), ROTATION_SPEED, 1, 6);
    }

    private Box getAimBox(LivingEntity param1) {
        Box v2 = param1.getBoundingBox();
        double v3 = param1.isGliding() ? GLIDING_HORIZONTAL_INSET : GROUND_HORIZONTAL_INSET;
        double v5 = param1.isGliding() ? GLIDING_VERTICAL_INSET : GROUND_VERTICAL_INSET;
        return new Box(v2.minX + v2.getLengthX() * v3 / MIN_X_DIVISOR, v2.minY, v2.minZ + v2.getLengthZ() * v3 / MIN_Z_DIVISOR, v2.maxX - v2.getLengthX() * v3 / MAX_X_DIVISOR, v2.maxY - v2.getLengthY() * v5, v2.maxZ - v2.getLengthZ() * v3 / MAX_Z_DIVISOR);
    }

    @Override
    public String getName() {
        return MODE_NAME;
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.resetAimProgress();
    }

    private void resetAimProgress() {
        this.yawProgress = 0.0f;
        this.pitchProgress = 0.0f;
    }

    @Override
    public void reset(AttackAura param1) {
        this.resetAimProgress();
    }

    static {
        RAYCAST_DISTANCE = 999.0;
        ON_TARGET_MIN_YAW_DECAY = 0.005f;
        ON_TARGET_MAX_YAW_DECAY = 0.02f;
        ON_TARGET_MIN_PITCH_DECAY = 0.005f;
        ON_TARGET_MAX_PITCH_DECAY = 0.02f;
        GLIDING_MIN_YAW_GAIN = 9.0E-5f;
        GLIDING_MAX_YAW_GAIN = 0.009f;
        GLIDING_MIN_PITCH_GAIN = 9.0E-4f;
        GLIDING_MAX_PITCH_GAIN = 0.009f;
        SWIMMING_MIN_YAW_GAIN = 9.0E-5f;
        SWIMMING_MAX_YAW_GAIN = 0.009f;
        SWIMMING_MIN_PITCH_GAIN = 9.0E-5f;
        SWIMMING_MAX_PITCH_GAIN = 9.0E-4f;
        GROUND_MIN_YAW_GAIN = 9.0E-5f;
        GROUND_MAX_YAW_GAIN = 0.009f;
        GROUND_MIN_PITCH_GAIN = 9.0E-4f;
        GROUND_MAX_PITCH_GAIN = 0.009f;
        ROTATION_SPEED = 180.0f;
        GLIDING_HORIZONTAL_INSET = -0.5;
        GROUND_HORIZONTAL_INSET = 0.1f;
        GLIDING_VERTICAL_INSET = -0.5;
        GROUND_VERTICAL_INSET = 0.1f;
        MIN_X_DIVISOR = 2.0;
        MIN_Z_DIVISOR = 2.0;
        MAX_X_DIVISOR = 2.0;
        MAX_Z_DIVISOR = 2.0;
    }
}



