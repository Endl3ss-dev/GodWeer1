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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class CelestialRot extends AbstractRotationElement {

    private Vector3d dynamicAimPoint = null;
    private int currentBodyPart = 0;
    private final List<BodyPart> bodyParts = new ArrayList<>();
    private long lastBodyPartChange = 0;
    private Vector3d targetAimPoint = null;
    private Vector3d previousAimPoint = null;
    private float smoothingProgress = 0f;
    private long transitionStartTime = 0;
    private boolean isTransitioning = false;
    private long lastUpdateTime = 0;

    private float currentSmoothness = 0f;
    private float currentYawSpeed = 0f;
    private float currentJitterIntensity = 0f;
    private float jitterPhase = 0f;
    private float jitterRadius = 0.1f;
    private float jitterFrequency = 2.0f;
    private AimingPattern currentPattern = AimingPattern.SMOOTH_TRACKING;
    private long lastPatternChangeTime = 0;


    private enum AimingPattern {
        SMOOTH_TRACKING,
        SUBTLE_ADJUSTMENTS,
        PREDICTIVE_FLICK
    }

    private record BodyPart(double heightPercent, Vector3d offset, float probability) {
    }

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) return currentLerpRotation;

        if (bodyParts.isEmpty()) {
            initializeBodyParts();
        }

        long currentTime = System.currentTimeMillis();
        float deltaTime = lastUpdateTime > 0 ? (currentTime - lastUpdateTime) / 1000.0f : 0.016f;
        lastUpdateTime = currentTime;

        Vector3d newTargetPoint = getMultiPointAimPoint(target);

        if (dynamicAimPoint == null) {
            dynamicAimPoint = newTargetPoint;
            targetAimPoint = newTargetPoint;
            previousAimPoint = newTargetPoint;
            switchAimingPattern();
        }

        if (targetAimPoint == null || !targetAimPoint.equals(newTargetPoint) || (currentTime - lastPatternChangeTime > 2000 + ThreadLocalRandom.current().nextInt(1500))) {
            previousAimPoint = targetAimPoint != null ? targetAimPoint : newTargetPoint;
            targetAimPoint = newTargetPoint;
            transitionStartTime = currentTime;
            smoothingProgress = 0f;
            isTransitioning = true;
            switchAimingPattern();
            lastPatternChangeTime = currentTime;
        }

        if (isTransitioning) {
            long elapsed = currentTime - transitionStartTime;
            float transitionDuration = 400f + ThreadLocalRandom.current().nextFloat() * 250f;
            smoothingProgress = Math.min(1.0f, elapsed / transitionDuration);

            float smoothFactor = smoothingProgress * smoothingProgress * (3f - 2f * smoothingProgress);
            dynamicAimPoint = previousAimPoint.lerp(targetAimPoint, smoothFactor);

            if (smoothingProgress >= 1.0f) {
                isTransitioning = false;
            }
        } else {
            float microAdjustment = 0.1f + ThreadLocalRandom.current().nextFloat() * 0.05f;
            dynamicAimPoint = dynamicAimPoint.lerp(targetAimPoint, microAdjustment);
        }

        jitterPhase += deltaTime * jitterFrequency;
        float jitterOffsetX = (float) Math.cos(jitterPhase) * jitterRadius * currentJitterIntensity;
        float jitterOffsetZ = (float) Math.sin(jitterPhase) * jitterRadius * currentJitterIntensity;
        Vector3d finalAimPoint = dynamicAimPoint.add(jitterOffsetX, 0, jitterOffsetZ);

        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d direction = finalAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float distanceToTarget = mc.player.getDistance(target);
        float distanceFactor = MathHelper.clamp(1.1f - (distanceToTarget * 0.04f), 0.7f, 1.4f);

        float yawSpeed = currentYawSpeed * distanceFactor;
        float pitchSpeed = (currentYawSpeed * 0.8f) * distanceFactor;

        float adaptiveSmoothness = currentSmoothness * MathHelper.clamp(Math.abs(yawDiff) / 50f, 0.8f, 1.2f);
        float yawStep = yawDiff * adaptiveSmoothness * deltaTime * 100f;
        float pitchStep = pitchDiff * adaptiveSmoothness * deltaTime * 100f;

        float maxYawStep = yawSpeed * deltaTime;
        float maxPitchStep = pitchSpeed * deltaTime;

        yawStep = MathHelper.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = MathHelper.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        if (Math.abs(yawStep) > Math.abs(yawDiff)) yawStep = yawDiff;
        if (Math.abs(pitchStep) > Math.abs(pitchDiff)) pitchStep = pitchDiff;

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        Vector2f newLerpRotation = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), yawSpeed, pitchSpeed, 0, 5);
        parentModule.setLerpRotation(newLerpRotation);
        return newLerpRotation;
    }

    private void switchAimingPattern() {
        currentPattern = AimingPattern.values()[ThreadLocalRandom.current().nextInt(AimingPattern.values().length)];
        switch (currentPattern) {
            case SMOOTH_TRACKING:
                currentSmoothness = 0.8f + ThreadLocalRandom.current().nextFloat() * 0.2f;
                currentYawSpeed = 440f + ThreadLocalRandom.current().nextFloat() * 120f;
                currentJitterIntensity = 0.25f + ThreadLocalRandom.current().nextFloat() * 0.2f;
                jitterRadius = 0.08f + ThreadLocalRandom.current().nextFloat() * 0.04f;
                jitterFrequency = 1.5f + ThreadLocalRandom.current().nextFloat();
                break;
            case SUBTLE_ADJUSTMENTS:
                currentSmoothness = 0.9f + ThreadLocalRandom.current().nextFloat() * 0.15f;
                currentYawSpeed = 360f + ThreadLocalRandom.current().nextFloat() * 100f;
                currentJitterIntensity = 0.1f + ThreadLocalRandom.current().nextFloat() * 0.15f;
                jitterRadius = 0.05f + ThreadLocalRandom.current().nextFloat() * 0.03f;
                jitterFrequency = 1.0f + ThreadLocalRandom.current().nextFloat() * 0.8f;
                break;
            case PREDICTIVE_FLICK:
                currentSmoothness = 0.65f + ThreadLocalRandom.current().nextFloat() * 0.25f;
                currentYawSpeed = 560f + ThreadLocalRandom.current().nextFloat() * 160f;
                currentJitterIntensity = 0.4f + ThreadLocalRandom.current().nextFloat() * 0.3f;
                jitterRadius = 0.12f + ThreadLocalRandom.current().nextFloat() * 0.05f;
                jitterFrequency = 2.5f + ThreadLocalRandom.current().nextFloat() * 1.5f;
                break;
        }
    }


    private Vector3d getMultiPointAimPoint(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        int switchInterval = 600 + ThreadLocalRandom.current().nextInt(300);
        if (currentTime - lastBodyPartChange > switchInterval) {
            currentBodyPart = selectRandomBodyPart();
            lastBodyPartChange = currentTime;
        }

        BodyPart part = bodyParts.get(currentBodyPart);

        double heightVariation = ThreadLocalRandom.current().nextDouble() * 0.04 - 0.02;
        double finalHeight = part.heightPercent + heightVariation;

        return target.getPositionVec().add(
                part.offset.x,
                target.getHeight() * finalHeight,
                part.offset.z
        );
    }

    private int selectRandomBodyPart() {
        float random = ThreadLocalRandom.current().nextFloat() * 100f;
        float cumulative = 0f;

        for (int i = 0; i < bodyParts.size(); i++) {
            cumulative += bodyParts.get(i).probability;
            if (random <= cumulative) {
                return i;
            }
        }
        return 0;
    }


    private void initializeBodyParts() {
        bodyParts.clear();
        bodyParts.add(new BodyPart(0.95, new Vector3d(0.0, 0.0, 0.0), 15f));
        bodyParts.add(new BodyPart(0.75, new Vector3d(0.0, 0.0, 0.0), 30f));
        bodyParts.add(new BodyPart(0.55, new Vector3d(0.0, 0.0, 0.0), 25f));
        bodyParts.add(new BodyPart(0.7, new Vector3d(-0.2, 0.0, 0.0), 15f));
        bodyParts.add(new BodyPart(0.7, new Vector3d(0.2, 0.0, 0.0), 15f));
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        dynamicAimPoint = null;
        targetAimPoint = null;
        previousAimPoint = null;
        currentBodyPart = 0;
        bodyParts.clear();
        lastBodyPartChange = 0;
        smoothingProgress = 0f;
        transitionStartTime = 0;
        isTransitioning = false;
        lastUpdateTime = 0;
        currentSmoothness = 0f;
        currentYawSpeed = 0f;
        currentJitterIntensity = 0f;
        jitterPhase = 0f;
        lastPatternChangeTime = 0;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        dynamicAimPoint = null;
        targetAimPoint = null;
        previousAimPoint = null;
        bodyParts.clear();
        isTransitioning = false;
        lastUpdateTime = 0;
        currentSmoothness = 0f;
        currentYawSpeed = 0f;
        currentJitterIntensity = 0f;
        jitterPhase = 0f;
        lastPatternChangeTime = 0;
    }
}