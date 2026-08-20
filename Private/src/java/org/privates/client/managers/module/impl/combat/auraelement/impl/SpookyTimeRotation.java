//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.privates.client.managers.module.impl.combat.auraelement.impl;


import lombok.Generated;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.GCDUtils;

import java.util.concurrent.ThreadLocalRandom;

public final class SpookyTimeRotation implements IMinecraft {
    private static long lastUpdateTime = 0L;
    private static boolean alternatingFlag = true;
    private static float smoothedYawShake = 0.0F;
    private static float smoothedPitchShake = 0.0F;
    private static float currentShakeYaw = 0.0F;
    private static float currentShakePitch = 0.0F;
    private static long lastTimeDiffUpdate = System.currentTimeMillis();
    private static final float yawSpeed = Mathf.random(190.0F, 220.0F);
    private static final float pitchSpeed = Mathf.random(45.0F, 70.0F);
    private static final float MAX_YAW_STEP = 12.0F;
    private static final float MAX_PITCH_STEP = 6.0F;

    public static void applyRotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        long currentTime = System.currentTimeMillis();

        // Toggle flag every 150ms
        if (currentTime - lastUpdateTime >= 150L) {
            alternatingFlag = !alternatingFlag;
            lastUpdateTime = currentTime;
        }

        // Get player eye position
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        // Calculate oscillations for targeting offsets
        float oscillY = (float) Math.cos((double) (System.currentTimeMillis() / 900L));
        float offsetY = 0.1F * oscillY;

        float oscillZ = (float) Math.cos((double) (System.currentTimeMillis() / 900L));
        float offsetZ = 0.1F * oscillZ;

        float oscillX = (float) Math.cos((double) (System.currentTimeMillis() / 900L));
        float offsetX = 0.2F * oscillX;

        // Calculate direction vector to target with offsets
        Vector3d directionVec = target.getPositionVec()
                .add(offsetZ,
                        MathHelper.clamp(playerEyePos.y - target.getPosY(), 0.0F, 1.2F) + (double) offsetX,
                        offsetY)
                .subtract(playerEyePos)
                .normalize();

        // Calculate base yaw and pitch
        float baseYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float basePitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))),
                -90.0F,
                90.0F
        );

        // Calculate time-based shake patterns
        float time = (float) System.currentTimeMillis() / 1000.0F;
        float targetYawShake = (float) Math.sin(time * 1.3F) * 1.2F;
        float targetPitchShake = (float) Math.cos(time * 1.8F) * 0.8F;

        // Smooth the shake values
        smoothedYawShake += (targetYawShake - smoothedYawShake) * 0.55F;
        smoothedPitchShake += (targetPitchShake - smoothedPitchShake) * 0.75F;

        // Calculate wave-based shake
        float waveA = (float) Math.cos((double) System.currentTimeMillis() / (double) 65.0F);
        float waveB = (float) Math.sin((double) System.currentTimeMillis() / (double) 50.0F);
        float targetShakeYaw = waveA * 2.2F + smoothedYawShake;
        float targetShakePitch = waveB * 1.1F + smoothedPitchShake;

        // Calculate time difference and delta limits
        long timeDiff = currentTime - lastTimeDiffUpdate;
        lastTimeDiffUpdate = currentTime;

        float maxYawDelta = yawSpeed * (float) timeDiff / 50.0F;
        float maxPitchDelta = pitchSpeed * (float) timeDiff / 50.0F;

        // Apply shake with smoothing
        currentShakeYaw = moveTowards(currentShakeYaw, targetShakeYaw, maxYawDelta);
        currentShakePitch = moveTowards(currentShakePitch, targetShakePitch, maxPitchDelta);

        // Add shake to base rotation
        baseYaw += currentShakeYaw;
        basePitch += currentShakePitch;
        basePitch = MathHelper.clamp(basePitch, -55.0F, 55.0F);

        // Calculate rotation change speeds based on alternating flag
        float yawChangeSpeed;
        float pitchChangeSpeed;

        if (alternatingFlag) {
            yawChangeSpeed = 24.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;
            pitchChangeSpeed = 8.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;
        } else {
            yawChangeSpeed = 21.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;
            pitchChangeSpeed = 9.0F + ThreadLocalRandom.current().nextFloat() * 2.0F;
        }

        // Safety clamp against anti-cheat spikes
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;
        float yawDiff = MathHelper.wrapDegrees(baseYaw - currentYaw);
        float pitchDiff = basePitch - currentPitch;

        // Slightly slower in close range (common in "жимки")
        float distance = mc.player.getDistance(target);
        float closeFactor = distance < 2.2F ? 0.72F : 1.0F;

        float nextYaw = currentYaw + MathHelper.clamp(yawDiff, -MAX_YAW_STEP * closeFactor, MAX_YAW_STEP * closeFactor);
        float nextPitch = currentPitch + MathHelper.clamp(pitchDiff, -MAX_PITCH_STEP * closeFactor, MAX_PITCH_STEP * closeFactor);
        nextPitch = MathHelper.clamp(nextPitch, -55.0F, 55.0F);

        // Mouse-sensitivity quantization
        nextYaw = GCDUtils.getSensitivity(nextYaw);
        nextPitch = GCDUtils.getSensitivity(nextPitch);

        Rotation newRotation = new Rotation(nextYaw, nextPitch);
        RotationComponent.update(newRotation, yawChangeSpeed * closeFactor, pitchChangeSpeed * closeFactor, 18.0F, 14.0F, 1, 1, false);
    }

    private static float moveTowards(float current, float target, float maxDelta) {
        return Math.abs(target - current) <= maxDelta
                ? target
                : current + Math.signum(target - current) * maxDelta;
    }

    @Generated
    private SpookyTimeRotation() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
