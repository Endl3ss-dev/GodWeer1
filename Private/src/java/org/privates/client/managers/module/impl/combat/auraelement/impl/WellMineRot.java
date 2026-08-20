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

public class WellMineRot extends AbstractRotationElement {

    private Vector3d currentAimPoint = null;
    private long sessionStart = 0;

    private float spiralPhase = 0f;
    private float spiralRadiusPhase = (float) Math.PI;
    private float jitterPhase = 0f;
    private float jitterRadius = 0f;
    private float jitterSpeed = 0f;

    private boolean isDeflecting = false;
    private float deflectionProgress = 0f;
    private Vector3d deflectionTargetPoint = null;
    private float lastCooldown = 1.0f;

    private float baseSpiralSpeed;
    private float baseRadius;
    private float verticalOscillationSpeed;
    private float verticalOscillationRange;
    private float smoothness;

    private boolean isDisabling = false;
    private Vector2f flickOffTarget = null;
    private long disableStartTime = 0;
    private static final long FLICK_OFF_DURATION = 350;

    private int currentPatternType = 0;
    private final List<PatternSnapshot> recentPatterns = new ArrayList<>();
    private static final int MAX_PATTERN_HISTORY = 8;

    private float noiseOffsetX = 0f;
    private float noiseOffsetY = 0f;
    private float noiseOffsetZ = 0f;
    private float noiseSpeed = 0f;

    private float microShakePhase = 0f;
    private float microShakeIntensity = 0f;

    private float breathingPhase = 0f;
    private float breathingSpeed = 0f;
    private float breathingIntensity = 0f;

    private float driftPhaseX = 0f;
    private float driftPhaseY = 0f;
    private float driftSpeedX = 0f;
    private float driftSpeedY = 0f;
    private float driftIntensity = 0f;

    private long lastParameterChange = 0;
    private static final long PARAMETER_CHANGE_INTERVAL = 2500;

    private float lastYawDiff = 0f;
    private float lastPitchDiff = 0f;

    private float chaosPhase = 0f;
    private float chaosIntensity = 0f;

    private float targetingErrorX = 0f;
    private float targetingErrorY = 0f;
    private long lastErrorUpdate = 0;

    private static class PatternSnapshot {
        float spiralSpeed;
        float radius;
        float jitterIntensity;
        float noiseLevel;
        int patternType;

        PatternSnapshot(float spiralSpeed, float radius, float jitterIntensity, float noiseLevel, int patternType) {
            this.spiralSpeed = spiralSpeed;
            this.radius = radius;
            this.jitterIntensity = jitterIntensity;
            this.noiseLevel = noiseLevel;
            this.patternType = patternType;
        }

        boolean isSimilar(PatternSnapshot other) {
            float threshold = 0.15f;
            return Math.abs(this.spiralSpeed - other.spiralSpeed) < threshold &&
                    Math.abs(this.radius - other.radius) < threshold &&
                    Math.abs(this.jitterIntensity - other.jitterIntensity) < threshold &&
                    this.patternType == other.patternType;
        }
    }

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (isDisabling) {
            return handleDisablingFlick(parentModule, currentLerpRotation);
        }

        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (sessionStart == 0) {
            sessionStart = currentTime;
            randomizeParameters();
        }

        if (currentTime - lastParameterChange > PARAMETER_CHANGE_INTERVAL) {
            randomizeParameters();
            lastParameterChange = currentTime;
        }

        float currentCooldown = mc.player.getCooledAttackStrength(0.0f);

        lastCooldown = currentCooldown;

        spiralPhase += baseSpiralSpeed;
        spiralRadiusPhase += baseSpiralSpeed * randomizeValue(2.8f, 4.5f);
        jitterPhase += jitterSpeed;
        microShakePhase += randomizeValue(0.25f, 0.45f);
        breathingPhase += breathingSpeed;
        driftPhaseX += driftSpeedX;
        driftPhaseY += driftSpeedY;
        chaosPhase += randomizeValue(0.1f, 0.3f);

        noiseOffsetX += noiseSpeed * randomizeValue(-1f, 1f);
        noiseOffsetY += noiseSpeed * randomizeValue(-1f, 1f);
        noiseOffsetZ += noiseSpeed * randomizeValue(-1f, 1f);

        if (currentTime - lastErrorUpdate > 150 + ThreadLocalRandom.current().nextInt(200)) {
            targetingErrorX = randomizeValue(-0.05f, 0.05f);
            targetingErrorY = randomizeValue(-0.03f, 0.03f);
            lastErrorUpdate = currentTime;
        }

        float dynamicRadius = calculateDynamicRadius();
        float verticalOffset = calculateVerticalOffset();
        Vector3d targetBasePosition = target.getPositionVec().add(0, target.getHeight() * (0.45 + verticalOffset + targetingErrorY), 0);

        Vector3d right = Vector3d.fromPitchYaw(0, currentLerpRotation.x - 90);
        Vector3d up = Vector3d.fromPitchYaw(currentLerpRotation.y - 90, currentLerpRotation.x);

        Vector3d spiralOffset = calculateSpiralOffset(right, up, dynamicRadius);
        Vector3d jitterOffset = calculateJitterOffset(right, up, target);
        Vector3d microShake = calculateMicroShake(right, up);
        Vector3d breathingOffset = calculateBreathingOffset(up);
        Vector3d driftOffset = calculateDriftOffset(right, up);
        Vector3d chaosOffset = calculateChaosOffset(right, up);
        Vector3d noiseOffset = new Vector3d(noiseOffsetX, noiseOffsetY, noiseOffsetZ).scale(0.08f);

        Vector3d finalAimTarget = targetBasePosition
                .add(spiralOffset)
                .add(jitterOffset)
                .add(microShake)
                .add(breathingOffset)
                .add(driftOffset)
                .add(chaosOffset)
                .add(noiseOffset);

        if (isDeflecting && deflectionProgress < 1.0f) {
            deflectionProgress = Math.min(1.0f, deflectionProgress + randomizeValue(0.06f, 0.12f));
            float smoothProgress = (float) (1.0f - Math.pow(1.0f - deflectionProgress, randomizeValue(2.5f, 4.0f)));
            finalAimTarget = finalAimTarget.lerp(deflectionTargetPoint, smoothProgress);

            if (deflectionProgress >= 1.0f) {
                isDeflecting = false;
                deflectionProgress = 0f;
                deflectionTargetPoint = null;
            }
        }

        if (currentAimPoint == null) {
            currentAimPoint = finalAimTarget;
        } else {
            float lerpFactor = calculateLerpFactor();
            currentAimPoint = currentAimPoint.lerp(finalAimTarget, lerpFactor);
        }

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float yawSpeed = randomizeValue(15f, 30f);
        float pitchSpeed = randomizeValue(8f, 15f);

        float yawStep = Math.min(Math.abs(yawDiff), yawSpeed) * Math.signum(yawDiff);
        float pitchStep = Math.min(Math.abs(pitchDiff), pitchSpeed) * Math.signum(pitchDiff);

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        lastYawDiff = yawStep;
        lastPitchDiff = pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), randomizeValue(70, 85), randomizeValue(30, 40), 0, 4);
        parentModule.setLerpRotation(result);
        return result;
    }

    private float calculateDynamicRadius() {
        float base = baseRadius * (0.5f + (float) (Math.sin(spiralRadiusPhase) * 0.5f));
        float chaos = (float) Math.sin(chaosPhase) * chaosIntensity * 0.3f;
        return base + chaos + randomizeValue(-0.05f, 0.05f);
    }

    private float calculateVerticalOffset() {
        float breathing = (float) Math.sin(breathingPhase) * breathingIntensity * 0.1f;
        float oscillation = (float) Math.sin(System.currentTimeMillis() * verticalOscillationSpeed) * verticalOscillationRange;
        return oscillation + breathing + randomizeValue(-0.02f, 0.02f);
    }

    private Vector3d calculateSpiralOffset(Vector3d right, Vector3d up, float radius) {
        float phaseVariation = spiralPhase + (float) Math.sin(spiralPhase * 0.5f) * 0.3f;
        return right.scale(Math.cos(phaseVariation) * radius)
                .add(up.scale(Math.sin(phaseVariation) * radius));
    }

    private Vector3d calculateJitterOffset(Vector3d right, Vector3d up, LivingEntity target) {
        float jitterRadiusMultiplier = jitterRadius * target.getWidth();
        float jitterVariation = (float) (Math.sin(jitterPhase * 1.7f) + Math.cos(jitterPhase * 0.9f)) * 0.5f;
        return right.scale(Math.cos(jitterPhase) * jitterRadiusMultiplier * (1f + jitterVariation))
                .add(up.scale(Math.sin(jitterPhase) * jitterRadiusMultiplier * (1f + jitterVariation)));
    }

    private Vector3d calculateMicroShake(Vector3d right, Vector3d up) {
        float shakeX = (float) Math.sin(microShakePhase) * microShakeIntensity;
        float shakeY = (float) Math.cos(microShakePhase * 1.3f) * microShakeIntensity * 0.7f;
        return right.scale(shakeX).add(up.scale(shakeY));
    }

    private Vector3d calculateBreathingOffset(Vector3d up) {
        float breathing = (float) Math.sin(breathingPhase) * breathingIntensity;
        return up.scale(breathing);
    }

    private Vector3d calculateDriftOffset(Vector3d right, Vector3d up) {
        float driftX = (float) Math.sin(driftPhaseX) * driftIntensity;
        float driftY = (float) Math.cos(driftPhaseY) * driftIntensity * 0.6f;
        return right.scale(driftX).add(up.scale(driftY));
    }

    private Vector3d calculateChaosOffset(Vector3d right, Vector3d up) {
        float chaosX = (float) (Math.sin(chaosPhase) + Math.cos(chaosPhase * 2.3f)) * chaosIntensity * 0.5f;
        float chaosY = (float) (Math.cos(chaosPhase * 1.7f) + Math.sin(chaosPhase * 0.8f)) * chaosIntensity * 0.3f;
        return right.scale(chaosX).add(up.scale(chaosY));
    }

    private float calculateLerpFactor() {
        float base = isDeflecting ? randomizeValue(0.30f, 0.45f) : randomizeValue(0.15f, 0.25f);
        float variation = (float) Math.sin(spiralPhase * 0.3f) * 0.05f;
        return base + variation;
    }

    private Vector2f handleDisablingFlick(AuraNew parentModule, Vector2f currentRotation) {
        long elapsedTime = System.currentTimeMillis() - disableStartTime;

        if (elapsedTime >= FLICK_OFF_DURATION || flickOffTarget == null) {
            resetState();
            return currentRotation;
        }

        float progress = (float) elapsedTime / FLICK_OFF_DURATION;
        float smoothProgress = (float) (1.0 - Math.pow(1.0 - progress, randomizeValue(3.5f, 5.0f)));

        float yawDiff = MathHelper.wrapDegrees(flickOffTarget.x - currentRotation.x);
        float pitchDiff = flickOffTarget.y - currentRotation.y;

        float newYaw = currentRotation.x + yawDiff * smoothProgress;
        float newPitch = currentRotation.y + pitchDiff * smoothProgress;

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), 120, 120, 0, 4);
        parentModule.setLerpRotation(result);
        return result;
    }

    private void randomizeParameters() {
        PatternSnapshot newPattern;
        int attempts = 0;

        do {
            baseSpiralSpeed = randomizeValue(0.03f, 0.12f);
            baseRadius = randomizeValue(0.15f, 0.55f);
            verticalOscillationSpeed = randomizeValue(0.0008f, 0.002f);
            verticalOscillationRange = randomizeValue(0.12f, 0.32f);
            smoothness = randomizeValue(0.80f, 0.98f);
            jitterRadius = randomizeValue(0.35f, 0.75f);
            jitterSpeed = randomizeValue(0.15f, 0.35f);
            noiseSpeed = randomizeValue(0.008f, 0.025f);
            microShakeIntensity = randomizeValue(0.02f, 0.08f);
            breathingSpeed = randomizeValue(0.0015f, 0.004f);
            breathingIntensity = randomizeValue(0.08f, 0.18f);
            driftSpeedX = randomizeValue(0.01f, 0.04f);
            driftSpeedY = randomizeValue(0.008f, 0.035f);
            driftIntensity = randomizeValue(0.05f, 0.15f);
            chaosIntensity = randomizeValue(0.03f, 0.12f);
            currentPatternType = ThreadLocalRandom.current().nextInt(0, 10);

            newPattern = new PatternSnapshot(baseSpiralSpeed, baseRadius, jitterRadius, noiseSpeed, currentPatternType);
            attempts++;
        } while (isPatternTooSimilar(newPattern) && attempts < 20);

        recentPatterns.add(newPattern);
        if (recentPatterns.size() > MAX_PATTERN_HISTORY) {
            recentPatterns.remove(0);
        }

        spiralPhase = randomizeValue(0f, (float) Math.PI * 2f);
        jitterPhase = randomizeValue(0f, (float) Math.PI * 2f);
        microShakePhase = randomizeValue(0f, (float) Math.PI * 2f);
        breathingPhase = randomizeValue(0f, (float) Math.PI * 2f);
    }

    private boolean isPatternTooSimilar(PatternSnapshot newPattern) {
        for (PatternSnapshot recent : recentPatterns) {
            if (recent.isSimilar(newPattern)) {
                return true;
            }
        }
        return false;
    }

    private void triggerDeflection(Minecraft mc, LivingEntity target, Vector2f currentRotation) {
        isDeflecting = true;
        deflectionProgress = 0f;

        float deflectionAngle = randomizeValue(55f, 105f);
        float direction = ThreadLocalRandom.current().nextBoolean() ? 1f : -1f;
        float finalYaw = currentRotation.x + (deflectionAngle * direction);

        float finalPitch = currentRotation.y;
        if (ThreadLocalRandom.current().nextFloat() < 0.35f) {
            float verticalAngle = randomizeValue(1f, 25f);
            float verticalDirection = ThreadLocalRandom.current().nextBoolean() ? 1f : -1f;
            finalPitch += verticalAngle * verticalDirection;
        }

        finalPitch = MathHelper.clamp(finalPitch, -89.9f, 89.9f);

        float distanceToTarget = mc.player.getDistance(target);
        Vector3d playerEyes = mc.player.getEyePosition(1f);
        Vector3d newDirectionVector = Vector3d.fromPitchYaw(finalPitch, finalYaw);

        this.deflectionTargetPoint = playerEyes.add(newDirectionVector.scale(distanceToTarget));
    }

    private float randomizeValue(float min, float max) {
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        resetState();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        if (!isDisabling) {
            isDisabling = true;
            disableStartTime = System.currentTimeMillis();

            Vector2f currentRotation = parentModule.getLerpRotation();
            float flickAngle = randomizeValue(25f, 70f);
            float direction = ThreadLocalRandom.current().nextBoolean() ? 1f : -1f;

            float targetYaw = currentRotation.x + (flickAngle * direction);
            float targetPitch = currentRotation.y + randomizeValue(-8f, 8f);

            flickOffTarget = new Vector2f(targetYaw, targetPitch);
        }
    }

    private void resetState() {
        currentAimPoint = null;
        sessionStart = 0;
        spiralPhase = randomizeValue(0f, (float) Math.PI * 2f);
        spiralRadiusPhase = randomizeValue(0f, (float) Math.PI * 2f);
        jitterPhase = randomizeValue(0f, (float) Math.PI * 2f);
        isDeflecting = false;
        deflectionProgress = 0f;
        deflectionTargetPoint = null;
        lastCooldown = 1.0f;
        isDisabling = false;
        flickOffTarget = null;
        disableStartTime = 0;
        noiseOffsetX = 0f;
        noiseOffsetY = 0f;
        noiseOffsetZ = 0f;
        microShakePhase = 0f;
        breathingPhase = 0f;
        driftPhaseX = 0f;
        driftPhaseY = 0f;
        chaosPhase = 0f;
        lastParameterChange = 0;
        lastYawDiff = 0f;
        lastPitchDiff = 0f;
        recentPatterns.clear();
        randomizeParameters();
    }
}