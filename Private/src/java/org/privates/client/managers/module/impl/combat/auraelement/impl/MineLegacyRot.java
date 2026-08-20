package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.rotation.JitterUtil;

import java.util.concurrent.ThreadLocalRandom;


public class MineLegacyRot extends AbstractRotationElement {

    private Vector3d currentAimPoint = null;
    private Vector3d targetAimPoint = null;
    private int currentPointIndex = 0;
    private long lastPointSwitchTime = 0;
    private long pointSwitchDelay = 450L;

    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;

    private float baseYawSpeed = 165f;
    private float basePitchSpeed = 95f;
    private float smoothness = 0.42f;

    private JitterUtil.JitterType currentJitterType = JitterUtil.JitterType.LEGIT_PERLIN;
    private float jitterStrength = 0.35f;
    private float jitterSpeed = 1.2f;
    private long jitterSeed = System.currentTimeMillis();

    private float humanizationFactor = 0f;
    private long lastHumanizationUpdate = 0L;

    @Override

    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();

        updateHumanization(currentTime);

        Vector3d nextTargetPoint = selectMultiPoint(target, currentTime);

        if (targetAimPoint == null) {
            targetAimPoint = nextTargetPoint;
        }

        float pointTransitionSpeed = 0.08f + ThreadLocalRandom.current().nextFloat() * 0.04f;
        targetAimPoint = targetAimPoint.lerp(nextTargetPoint, pointTransitionSpeed);

        if (currentAimPoint == null) {
            currentAimPoint = target.getPositionVec().add(0, target.getHeight() * 0.6, 0);
        }

        float mainTransitionSpeed = 0.15f + humanizationFactor * 0.05f;
        currentAimPoint = currentAimPoint.lerp(targetAimPoint, mainTransitionSpeed);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        Vector2f jitter = JitterUtil.getJitter(currentJitterType, jitterSeed + currentTime, jitterStrength, jitterSpeed, 0.4f);
        targetYaw += jitter.x;
        targetPitch += jitter.y;

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float yawDistance = Math.abs(yawDiff);
        float pitchDistance = Math.abs(pitchDiff);

        float effectiveYawSpeed = baseYawSpeed;
        float effectivePitchSpeed = basePitchSpeed;

        if (yawDistance > 35f) {
            effectiveYawSpeed *= 1.12f + (yawDistance / 180f) * 0.15f;
        } else if (yawDistance < 10f) {
            effectiveYawSpeed *= 0.88f;
        }

        if (pitchDistance > 20f) {
            effectivePitchSpeed *= 1.08f;
        } else if (pitchDistance < 8f) {
            effectivePitchSpeed *= 0.90f;
        }

        float momentumBlend = 0.28f + humanizationFactor * 0.08f;
        yawMomentum = MathHelper.lerp(momentumBlend, yawMomentum, yawDiff * 0.10f);
        pitchMomentum = MathHelper.lerp(momentumBlend, pitchMomentum, pitchDiff * 0.08f);

        yawDiff += yawMomentum;
        pitchDiff += pitchMomentum;

        float interpolationFactor = smoothness + humanizationFactor * 0.1f;
        interpolationFactor = calculateSmoothInterpolation(interpolationFactor);

        float yawStep = yawDiff * interpolationFactor;
        float pitchStep = pitchDiff * interpolationFactor;

        if (Math.abs(yawStep) > Math.abs(yawDiff)) yawStep = yawDiff;
        if (Math.abs(pitchStep) > Math.abs(pitchDiff)) pitchStep = pitchDiff;

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), effectiveYawSpeed, effectivePitchSpeed, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }

    private Vector3d selectMultiPoint(LivingEntity target, long currentTime) {
        if (currentTime - lastPointSwitchTime > pointSwitchDelay) {
            currentPointIndex = (currentPointIndex + 1) % 3;
            lastPointSwitchTime = currentTime;
            pointSwitchDelay = 400L + ThreadLocalRandom.current().nextLong(200L);
        }

        double heightRatio;
        switch (currentPointIndex) {
            case 0:
                heightRatio = 0.75 + ThreadLocalRandom.current().nextDouble() * 0.10;
                break;
            case 1:
                heightRatio = 0.55 + ThreadLocalRandom.current().nextDouble() * 0.10;
                break;
            case 2:
                heightRatio = 0.30 + ThreadLocalRandom.current().nextDouble() * 0.10;
                break;
            default:
                heightRatio = 0.60;
        }

        double xOffset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.12;
        double zOffset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.12;

        return target.getPositionVec().add(xOffset, target.getHeight() * heightRatio, zOffset);
    }

    private void updateHumanization(long currentTime) {
        if (currentTime - lastHumanizationUpdate > 800L) {
            humanizationFactor = ThreadLocalRandom.current().nextFloat() * 0.15f;
            lastHumanizationUpdate = currentTime;

            if (ThreadLocalRandom.current().nextFloat() < 0.3f) {
                JitterUtil.JitterType[] types = {
                        JitterUtil.JitterType.LEGIT_PERLIN,
                        JitterUtil.JitterType.WAVE,
                        JitterUtil.JitterType.CHAOTIC
                };
                currentJitterType = types[ThreadLocalRandom.current().nextInt(types.length)];
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
                jitterStrength = 0.25f + ThreadLocalRandom.current().nextFloat() * 0.20f;
            }
        }
    }

    private float calculateSmoothInterpolation(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        currentAimPoint = null;
        targetAimPoint = null;
        currentPointIndex = 0;
        lastPointSwitchTime = 0;
        pointSwitchDelay = 450L;

        yawMomentum = 0f;
        pitchMomentum = 0f;

        baseYawSpeed = 165f;
        basePitchSpeed = 95f;
        smoothness = 0.42f;

        currentJitterType = JitterUtil.JitterType.LEGIT_PERLIN;
        jitterStrength = 0.35f;
        jitterSpeed = 1.2f;
        jitterSeed = System.currentTimeMillis();

        humanizationFactor = 0f;
        lastHumanizationUpdate = 0L;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        currentAimPoint = null;
        targetAimPoint = null;
    }
}