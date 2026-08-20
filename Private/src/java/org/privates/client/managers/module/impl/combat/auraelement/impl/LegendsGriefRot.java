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


public class LegendsGriefRot extends AbstractRotationElement {

    private enum BodyPart {
        HEAD(0.9), CHEST(0.6), LEGS(0.2);
        private final double height;

        BodyPart(double height) {
            this.height = height;
        }
    }

    private final List<BodyPart> bodyParts = new ArrayList<>();
    private int currentBodyPartIndex = 0;
    private int nextBodyPartIndex = 1;
    private long lastPartSwitchTime = 0;
    private long partSwitchDuration = 0;

    private Vector3d currentAimPoint = null;

    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;

    private float spiralPhase = 0f;
    private float spiralRadiusPhase = 0f;

    private long lastPauseTime = 0;
    private long pauseDuration = 0;
    private boolean isPaused = false;

    private float smoothness;
    private float smoothnessVariance;
    private float minYawSpeed, maxYawSpeed;
    private float minPitchSpeed, maxPitchSpeed;
    private float acceleration;
    private float blendSpeed;
    private float microMoveStrength;
    private float momentumDecay;
    private float verticalBias;
    private float stabilityFactor;
    private long pauseChanceTicks;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (isPaused) {
            if (currentTime - lastPauseTime > pauseDuration) {
                isPaused = false;
            } else {
                return currentLerpRotation;
            }
        }

        if (currentTime > lastPauseTime + stabilityFactor) {
            if (ThreadLocalRandom.current().nextInt((int) pauseChanceTicks) == 0) {
                isPaused = true;
                lastPauseTime = currentTime;
                pauseDuration = ThreadLocalRandom.current().nextLong(15, 30);
                return currentLerpRotation;
            }
        }

        if (bodyParts.isEmpty()) {
            onEnable(parentModule);
        }

        Vector3d targetPoint = getBlendedTargetPoint(target, currentTime);

        if (currentAimPoint == null) {
            currentAimPoint = targetPoint;
        } else {
            currentAimPoint = currentAimPoint.lerp(targetPoint, (float) (1.0f - Math.pow(1.0f - blendSpeed, 0.1)));
        }

        Vector3d finalAimPoint = applyJitter(currentAimPoint, currentTime);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = finalAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        yawMomentum = MathHelper.lerp(1.0f - momentumDecay, yawMomentum, yawDiff * 0.1f);
        pitchMomentum = MathHelper.lerp(1.0f - momentumDecay, pitchMomentum, pitchDiff * 0.1f);

        yawDiff += yawMomentum;
        pitchDiff += pitchMomentum;

        float dynamicSmoothness = smoothness + (ThreadLocalRandom.current().nextFloat() - 0.5f) * smoothnessVariance;
        float interpolationFactor = 1.0f - dynamicSmoothness;

        float yawStep = yawDiff * interpolationFactor;
        float pitchStep = pitchDiff * interpolationFactor;

        float totalDelta = Math.abs(yawDiff) + Math.abs(pitchDiff);
        float accel = 1.0f + (totalDelta / 180.0f) * acceleration;

        float yawSpeed = MathHelper.lerp(totalDelta / 360f, minYawSpeed, maxYawSpeed) * accel;
        float pitchSpeed = MathHelper.lerp(totalDelta / 180f, minPitchSpeed, maxPitchSpeed) * accel;

        if (Math.abs(yawStep) > yawSpeed) yawStep = Math.copySign(yawSpeed, yawStep);
        if (Math.abs(pitchStep) > pitchSpeed) pitchStep = Math.copySign(pitchSpeed, pitchStep);

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), 360, 180, 0, 5);
        parentModule.setLerpRotation(result);
        return result;
    }

    private Vector3d getBlendedTargetPoint(LivingEntity target, long currentTime) {
        if (currentTime - lastPartSwitchTime > partSwitchDuration) {
            currentBodyPartIndex = nextBodyPartIndex;
            nextBodyPartIndex = ThreadLocalRandom.current().nextInt(bodyParts.size());
            if (nextBodyPartIndex == currentBodyPartIndex) {
                nextBodyPartIndex = (nextBodyPartIndex + 1) % bodyParts.size();
            }
            lastPartSwitchTime = currentTime;
            partSwitchDuration = (long) (200 + ThreadLocalRandom.current().nextDouble() * 200);
        }

        float progress = MathHelper.clamp((float) (currentTime - lastPartSwitchTime) / partSwitchDuration, 0.0f, 1.0f);
        progress = progress * progress * (3.0f - 2.0f * progress);

        BodyPart currentPart = bodyParts.get(currentBodyPartIndex);
        BodyPart nextPart = bodyParts.get(nextBodyPartIndex);

        double interpolatedY = MathHelper.lerp(progress, currentPart.height, nextPart.height) + verticalBias * (ThreadLocalRandom.current().nextDouble() - 0.5);

        return target.getPositionVec().add(0, target.getHeight() * interpolatedY, 0);
    }

    private Vector3d applyJitter(Vector3d aimPoint, long currentTime) {
        float time = currentTime * 0.001f;

        spiralPhase += 0.17f;
        spiralRadiusPhase += 0.012f;

        float spiralRadius = 0.12f + (float) Math.sin(spiralRadiusPhase) * 0.08f;
        float spiralX = (float) Math.cos(spiralPhase) * spiralRadius;
        float spiralZ = (float) Math.sin(spiralPhase) * spiralRadius;

        float microX = (float) (Math.sin(time * 25) * Math.cos(time * 15)) * microMoveStrength * 0.05f;
        float microY = (float) (Math.cos(time * 20) * Math.sin(time * 12)) * microMoveStrength * 0.05f;
        float microZ = (float) (Math.sin(time * 18) * Math.cos(time * 22)) * microMoveStrength * 0.05f;

        return aimPoint.add(spiralX + microX, microY, spiralZ + microZ);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        bodyParts.clear();
        bodyParts.add(BodyPart.HEAD);
        bodyParts.add(BodyPart.CHEST);
        bodyParts.add(BodyPart.LEGS);

        currentAimPoint = null;
        lastPartSwitchTime = 0;
        yawMomentum = 0;
        pitchMomentum = 0;
        isPaused = false;
        lastPauseTime = 0;

        smoothness = 0.666f;
        smoothnessVariance = 0.290f;
        minYawSpeed = 1.34f;
        maxYawSpeed = 78.60f;
        minPitchSpeed = 1.05f;
        maxPitchSpeed = 28.98f;
        acceleration = 3f;
        blendSpeed = 0.207f;
        microMoveStrength = 2.5f;
        momentumDecay = 0.167f;
        verticalBias = 0.30f;
        stabilityFactor = 600.16f;
        pauseChanceTicks = 18;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        currentAimPoint = null;
    }
}