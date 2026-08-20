package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;

import java.util.concurrent.ThreadLocalRandom;

public class SmoothNewRot extends AbstractRotationElement {

    private long lastFrameTimestamp = 0L;
    private float currentYawSpeed = 0f;
    private float currentPitchSpeed = 0f;
    
    // Smooth micro-shake
    private float shakePhase = 0f;
    private long lastShakeUpdate = 0L;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) return currentLerpRotation;

        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec();
        double distanceToHitbox = playerPos.distanceTo(targetPos.add(0, target.getEyeHeight() * 0.5, 0));
        
        // Stop rotating when very close
        if (distanceToHitbox <= 0.33) {
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();
        float deltaTime = calculateDeltaTime(currentTime);

        // Calculate target rotation
        targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.65, 0);
        Vector3d direction = targetPos.subtract(playerPos);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        // Calculate angle differences
        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
        float pitchDiff = Math.abs(targetPitch - currentLerpRotation.y);

        // Dead zone - if already aiming well, don't rotate
        if (yawDiff < 5f && pitchDiff < 8f) {
            return currentLerpRotation;
        }

        // Smooth base speeds with minimal variation
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        float baseYawSpeed = 0.50f + rand.nextFloat() * 0.05f; // 0.50-0.55
        float basePitchSpeed = 0.20f + rand.nextFloat() * 0.03f; // 0.20-0.23
        
        // Only adjust pitch if significant difference
        boolean shouldAdjustPitch = pitchDiff > 15f;
        
        // Smooth acceleration based on distance
        float yawAcceleration = calculateSmoothAcceleration(yawDiff);
        float pitchAcceleration = shouldAdjustPitch ? calculateSmoothAcceleration(pitchDiff) : 0f;
        
        // Calculate target speeds
        float targetYawSpeed = baseYawSpeed * yawAcceleration;
        float targetPitchSpeed = basePitchSpeed * pitchAcceleration;
        
        // Very smooth speed transitions
        currentYawSpeed = MathHelper.lerp(0.20f, currentYawSpeed, targetYawSpeed);
        currentPitchSpeed = MathHelper.lerp(0.15f, currentPitchSpeed, targetPitchSpeed);

        // Apply smooth rotation
        float smoothYaw = smoothLerp(currentYawSpeed, currentLerpRotation.x, targetYaw, deltaTime);
        float smoothPitch = smoothLerp(currentPitchSpeed, currentLerpRotation.y, MathHelper.clamp(targetPitch, -90f, 90f), deltaTime);

        // Add subtle micro-shake for realism
        Vector2f shake = calculateMicroShake(currentTime);
        smoothYaw += shake.x;
        smoothPitch += shake.y;

        smoothPitch = MathHelper.clamp(smoothPitch, -90f, 90f);

        Vector2f newLerpRotation = new Vector2f(smoothYaw, smoothPitch);

        applyRotation(new Rotation(smoothYaw, smoothPitch), 50f, 15f, 0, 3);

        parentModule.setLerpRotation(newLerpRotation);
        return newLerpRotation;
    }

    /**
     * Smooth acceleration curve - very gradual
     */
    private float calculateSmoothAcceleration(float angleDiff) {
        if (angleDiff > 50f) {
            return 1.15f; // Gentle acceleration for large angles
        } else if (angleDiff > 25f) {
            return 1.0f; // Normal speed
        } else if (angleDiff > 12f) {
            return 0.80f; // Slow down
        } else if (angleDiff > 5f) {
            return 0.60f; // Slower
        } else {
            return 0.35f; // Very slow for precision
        }
    }

    /**
     * Subtle micro-shake for natural human movement
     */
    private Vector2f calculateMicroShake(long currentTime) {
        // Update shake every 40-60ms
        if (currentTime - lastShakeUpdate > 40 + ThreadLocalRandom.current().nextInt(21)) {
            shakePhase += 0.08f + ThreadLocalRandom.current().nextFloat() * 0.04f;
            lastShakeUpdate = currentTime;
        }

        // Very subtle circular shake
        float shakeX = (float) (Math.sin(shakePhase) * 0.15f);
        float shakeY = (float) (Math.cos(shakePhase * 1.3f) * 0.10f);

        // Apply smooth easing
        shakeX *= easeInOutSine(0.5f + (float) Math.sin(shakePhase * 0.5f) * 0.5f);
        shakeY *= easeInOutSine(0.5f + (float) Math.cos(shakePhase * 0.5f) * 0.5f);

        return new Vector2f(shakeX, shakeY);
    }

    private float calculateDeltaTime(long currentTime) {
        if (lastFrameTimestamp == 0L) {
            lastFrameTimestamp = currentTime;
            return 16.67f;
        }

        float deltaMs = currentTime - lastFrameTimestamp;
        lastFrameTimestamp = currentTime;
        return Math.max(deltaMs, 1f);
    }

    private float smoothLerp(float speed, float current, float target, float deltaTime) {
        float diff = MathHelper.wrapDegrees(target - current);
        
        // Smooth sine easing for natural movement
        float easedSpeed = easeInOutSine(Math.min(speed, 1f));
        
        // FPS-independent
        float adjustedSpeed = easedSpeed * (deltaTime / 16.67f);
        
        return current + diff * adjustedSpeed;
    }

    /**
     * Sine easing - smoothest, most natural
     */
    private float easeInOutSine(float t) {
        return -(float) (Math.cos(Math.PI * t) - 1f) / 2f;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastFrameTimestamp = 0L;
        currentYawSpeed = 0f;
        currentPitchSpeed = 0f;
        shakePhase = 0f;
        lastShakeUpdate = 0L;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}
