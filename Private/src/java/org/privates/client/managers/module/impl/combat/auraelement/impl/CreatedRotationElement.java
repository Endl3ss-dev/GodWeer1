package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.CreateRotation;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CreatedRotationElement extends AbstractRotationElement {

    private CreateRotation.RotationProfile profile;
    private Vector3d currentAimPoint = null;
    private int currentBodyPartIndex = 0;
    private long lastBodyPartSwitch = 0;
    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;
    private int ticksSinceSnap = 0;
    private long sessionStart = 0;
    private float currentYawSpeed;
    private float currentPitchSpeed;
    private float currentSmoothness;
    private long lastSpeedUpdate = 0;
    private final List<Vector3d> bodyPartOffsets = new ArrayList<>();
    private long lastProfileReload = 0;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (profile == null || !profile.isValid() || currentTime - lastProfileReload > 1000) {
            profile = loadProfileFromCreateRotation();
            lastProfileReload = currentTime;
            if (profile == null || !profile.isValid()) {
                return currentLerpRotation;
            }
            applyProfileParameters();
        }

        if (sessionStart == 0) {
            sessionStart = currentTime;
            initializeBodyParts(target);
            randomizeParameters();
        }

        if (currentTime - lastSpeedUpdate > ThreadLocalRandom.current().nextInt(600, 1200)) {
            randomizeParameters();
            lastSpeedUpdate = currentTime;
        }

        Vector3d targetPoint = getTargetAimPoint(target, currentTime);

        if (currentAimPoint == null) {
            currentAimPoint = target.getPositionVec().add(0, target.getHeight() * 0.6, 0);
        }

        float transitionSpeed = 0.12f + ThreadLocalRandom.current().nextFloat() * 0.10f;
        currentAimPoint = currentAimPoint.lerp(targetPoint, transitionSpeed);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        boolean shouldSnap = handleSnapMechanism(targetYaw, currentLerpRotation.x);

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
        float pitchDelta = Math.abs(targetPitch - currentLerpRotation.y);

        float effectiveYawSpeed = currentYawSpeed;
        float effectivePitchSpeed = currentPitchSpeed;

        if (shouldSnap) {
            effectiveYawSpeed *= 2.0f;
            effectivePitchSpeed *= 1.5f;
        } else {
            if (yawDelta > 35f) {
                effectiveYawSpeed *= 1.1f + (yawDelta / 180f) * 0.2f;
            } else if (yawDelta < 10f) {
                effectiveYawSpeed *= 0.85f;
            }

            if (pitchDelta > 20f) {
                effectivePitchSpeed *= 1.08f + (pitchDelta / 90f) * 0.15f;
            } else if (pitchDelta < 8f) {
                effectivePitchSpeed *= 0.88f;
            }
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        if (profile.getJitterIntensity() > 0 && !shouldSnap) {
            float jitter = profile.getJitterIntensity();
            targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5f) * jitter * 2f;
            targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5f) * jitter;
        }

        float momentumBlend = 0.30f + ThreadLocalRandom.current().nextFloat() * 0.15f;
        yawMomentum = MathHelper.lerp(momentumBlend, yawMomentum, yawDiff * 0.10f);
        pitchMomentum = MathHelper.lerp(momentumBlend, pitchMomentum, pitchDiff * 0.08f);

        float accelerationFactor = 1.0f + (yawDelta / 180f) * profile.getYawAccel() / 5000f;
        yawDiff += yawMomentum * accelerationFactor;
        pitchDiff += pitchMomentum * accelerationFactor;

        float interpolationFactor = calculateSmoothInterpolation(currentSmoothness);

        float yawStep = yawDiff * interpolationFactor;
        float pitchStep = pitchDiff * interpolationFactor;

        float maxYawMove = effectiveYawSpeed / 20f;
        float maxPitchMove = effectivePitchSpeed / 20f;

        if (Math.abs(yawStep) > maxYawMove) {
            yawStep = Math.signum(yawStep) * maxYawMove;
        }
        if (Math.abs(pitchStep) > maxPitchMove) {
            pitchStep = Math.signum(pitchStep) * maxPitchMove;
        }

        if (profile.getMicroCorrection() > 0 && yawDelta < 5 && pitchDelta < 5) {
            float micro = profile.getMicroCorrection();
            yawStep += (ThreadLocalRandom.current().nextFloat() - 0.5f) * micro * 0.2f;
            pitchStep += (ThreadLocalRandom.current().nextFloat() - 0.5f) * micro * 0.15f;
        }

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), effectiveYawSpeed, effectivePitchSpeed, 0, 5);
        parentModule.setLerpRotation(result);
        return result;
    }


    private CreateRotation.RotationProfile loadProfileFromCreateRotation() {
        CreateRotation module = CreateRotation.getInstance();
        if (module != null && module.isEnabled()) {
            CreateRotation.RotationProfile loadedProfile = module.getGeneratedProfile();
            if (loadedProfile != null && loadedProfile.isValid()) {
                return loadedProfile;
            }
        }
        return new CreateRotation.RotationProfile();
    }


    private void applyProfileParameters() {
        if (profile == null || !profile.isValid()) return;

        currentYawSpeed = profile.getYawSpeed();
        currentPitchSpeed = profile.getPitchSpeed();
        currentSmoothness = profile.getAimSmoothness();

        yawMomentum = 0f;
        pitchMomentum = 0f;
        ticksSinceSnap = 0;
    }


    private void randomizeParameters() {
        if (profile == null || !profile.isValid()) return;

        float variance = profile.getRandomVariance();
        currentYawSpeed = randomizeValue(profile.getYawSpeed(), variance);
        currentPitchSpeed = randomizeValue(profile.getPitchSpeed(), variance);
        currentSmoothness = randomizeValue(profile.getAimSmoothness(), variance * 0.5f);
    }


    private float randomizeValue(float base, float variancePercent) {
        float variance = base * variancePercent;
        float min = base - variance;
        float max = base + variance;
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }


    private boolean handleSnapMechanism(float targetYaw, float currentYaw) {
        if (!profile.getRotationType().equals("Snap") && !profile.getRotationType().equals("Hybrid")) {
            return false;
        }

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));

        if (profile.getRotationType().equals("Snap")) {
            if (ticksSinceSnap >= profile.getSnapFocusTicks() && yawDelta > 15) {
                ticksSinceSnap = 0;
                return true;
            }
            ticksSinceSnap++;
        } else if (profile.getRotationType().equals("Hybrid")) {
            if (yawDelta > 30 && ticksSinceSnap >= profile.getSnapFocusTicks()) {
                ticksSinceSnap = 0;
                return true;
            }
            ticksSinceSnap++;
        }

        return false;
    }


    private Vector3d getTargetAimPoint(LivingEntity target, long currentTime) {
        if (profile.getMultiPointCount() <= 1 || profile.getBodyParts().isEmpty()) {
            return target.getPositionVec().add(0, target.getHeight() * 0.6, 0);
        }

        if (currentTime - lastBodyPartSwitch > 300 + ThreadLocalRandom.current().nextLong(200)) {
            currentBodyPartIndex = (currentBodyPartIndex + 1) % profile.getBodyParts().size();
            lastBodyPartSwitch = currentTime;
        }

        String targetPart = profile.getBodyParts().get(currentBodyPartIndex);
        double yOffset;

        switch (targetPart) {
            case "Head":
                yOffset = target.getHeight() * 0.90;
                break;
            case "Body":
                yOffset = target.getHeight() * 0.55;
                break;
            case "Legs":
                yOffset = target.getHeight() * 0.25;
                break;
            default:
                yOffset = target.getHeight() * 0.60;
        }

        double randomX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
        double randomZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;

        return target.getPositionVec().add(randomX, yOffset, randomZ);
    }


    private void initializeBodyParts(LivingEntity target) {
        bodyPartOffsets.clear();

        if (profile.getMultiPointCount() <= 1) {
            bodyPartOffsets.add(new Vector3d(0, 0.6, 0));
            return;
        }

        int parts = Math.min(profile.getMultiPointCount(), 7);
        for (int i = 0; i < parts; i++) {
            double heightPercent = (double) i / (parts - 1);
            double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;
            double radius = 0.10 + ThreadLocalRandom.current().nextDouble() * 0.08;

            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;

            bodyPartOffsets.add(new Vector3d(xOffset, heightPercent, zOffset));
        }
    }


    private float calculateSmoothInterpolation(float t) {
        return t * t * t * (t * (t * 6f - 15f) + 10f);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        profile = loadProfileFromCreateRotation();
        currentAimPoint = null;
        currentBodyPartIndex = 0;
        lastBodyPartSwitch = 0;
        yawMomentum = 0f;
        pitchMomentum = 0f;
        ticksSinceSnap = 0;
        sessionStart = 0;
        lastSpeedUpdate = 0;
        lastProfileReload = 0;
        bodyPartOffsets.clear();

        if (profile != null && profile.isValid()) {
            applyProfileParameters();
        }
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        currentAimPoint = null;
        bodyPartOffsets.clear();
        sessionStart = 0;
        profile = null;
        lastProfileReload = 0;
    }
}