package org.privates.client.managers.rotation;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.module.impl.combat.CreateRotation;

import java.util.Random;

@Getter
public class CreatedRotation {

    private final CreateRotation.RotationProfile profile;
    private final Random random = new Random();

    private float currentYaw;
    private float currentPitch;
    private float targetYaw;
    private float targetPitch;

    private float prevYawSpeed = 0;
    private float prevPitchSpeed = 0;
    private int ticksSinceSnap = 0;
    private int currentBodyPartIndex = 0;
    private long lastBodyPartSwitch = 0;

    private float momentumYaw = 0;
    private float momentumPitch = 0;

    public CreatedRotation(CreateRotation.RotationProfile profile, float startYaw, float startPitch) {
        this.profile = profile;
        this.currentYaw = startYaw;
        this.currentPitch = startPitch;
        this.targetYaw = startYaw;
        this.targetPitch = startPitch;
    }

    public void updateTarget(LivingEntity target) {
        if (target == null) return;

        Vector3d targetPos = getTargetPosition(target);
        float[] angles = calculateAngles(targetPos);

        this.targetYaw = angles[0];
        this.targetPitch = angles[1];
    }

    public float[] getRotation() {
        return new float[]{currentYaw, currentPitch};
    }

    public void update() {
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float deltaPitch = targetPitch - currentPitch;

        boolean shouldSnap = false;
        if (profile.getRotationType().equals("Snap")) {
            if (ticksSinceSnap >= profile.getSnapFocusTicks() && Math.abs(deltaYaw) > 10) {
                shouldSnap = true;
                ticksSinceSnap = 0;
            } else {
                ticksSinceSnap++;
            }
        } else if (profile.getRotationType().equals("Hybrid")) {
            if (Math.abs(deltaYaw) > 30 && ticksSinceSnap >= profile.getSnapFocusTicks()) {
                shouldSnap = true;
                ticksSinceSnap = 0;
            } else {
                ticksSinceSnap++;
            }
        }

        float yawSpeed = profile.getYawSpeed();
        float pitchSpeed = profile.getPitchSpeed();

        if (shouldSnap) {
            yawSpeed *= 2.5f;
            pitchSpeed *= 2.0f;
        }

        float variance = profile.getRandomVariance();
        float randomFactorYaw = 1.0f + (random.nextFloat() * variance * 2 - variance);
        float randomFactorPitch = 1.0f + (random.nextFloat() * variance * 2 - variance);

        yawSpeed *= randomFactorYaw;
        pitchSpeed *= randomFactorPitch;

        float smoothness = profile.getSmoothness();
        if (shouldSnap) {
            smoothness = Math.min(smoothness * 1.5f, 1.0f);
        }

        float moveYaw = deltaYaw * smoothness;
        float movePitch = deltaPitch * smoothness;

        float maxYawMove = yawSpeed / 20f;
        float maxPitchMove = pitchSpeed / 20f;

        moveYaw = MathHelper.clamp(moveYaw, -maxYawMove, maxYawMove);
        movePitch = MathHelper.clamp(movePitch, -maxPitchMove, maxPitchMove);

        float accelFactor = profile.getYawAccel() / 10000f;
        float yawAccel = (moveYaw - prevYawSpeed) * accelFactor;
        float pitchAccel = (movePitch - prevPitchSpeed) * accelFactor;

        moveYaw += yawAccel;
        movePitch += pitchAccel;

        if (profile.getJitterIntensity() > 0 && !shouldSnap) {
            float jitter = profile.getJitterIntensity();
            moveYaw += (random.nextFloat() - 0.5f) * jitter * 0.5f;
            movePitch += (random.nextFloat() - 0.5f) * jitter * 0.3f;
        }

        if (profile.getMicroCorrection() > 0 && Math.abs(deltaYaw) < 5 && Math.abs(deltaPitch) < 5) {
            float micro = profile.getMicroCorrection();
            moveYaw += (random.nextFloat() - 0.5f) * micro * 0.3f;
            movePitch += (random.nextFloat() - 0.5f) * micro * 0.2f;
        }

        momentumYaw = momentumYaw * 0.8f + moveYaw * 0.2f;
        momentumPitch = momentumPitch * 0.8f + movePitch * 0.2f;

        currentYaw += momentumYaw;
        currentPitch += momentumPitch;

        currentPitch = MathHelper.clamp(currentPitch, -90f, 90f);

        prevYawSpeed = moveYaw;
        prevPitchSpeed = movePitch;
    }

    private Vector3d getTargetPosition(LivingEntity target) {
        Vector3d basePos = target.getPositionVec();
        double entityHeight = target.getHeight();

        if (profile.getMultiPointCount() <= 1 || profile.getBodyParts().isEmpty()) {
            return basePos.add(0, entityHeight * 0.5, 0);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBodyPartSwitch > 150) {
            currentBodyPartIndex = (currentBodyPartIndex + 1) % profile.getBodyParts().size();
            lastBodyPartSwitch = currentTime;
        }

        String targetPart = profile.getBodyParts().get(currentBodyPartIndex);
        double yOffset;

        switch (targetPart) {
            case "Head":
                yOffset = entityHeight * 0.9;
                break;
            case "Body":
                yOffset = entityHeight * 0.5;
                break;
            case "Legs":
                yOffset = entityHeight * 0.2;
                break;
            default:
                yOffset = entityHeight * 0.5;
        }

        double randomX = (random.nextDouble() - 0.5) * 0.3;
        double randomZ = (random.nextDouble() - 0.5) * 0.3;

        return basePos.add(randomX, yOffset, randomZ);
    }

    private float[] calculateAngles(Vector3d targetPos) {
        Vector3d eyePos = new Vector3d(0, 0, 0);

        double deltaX = targetPos.x - eyePos.x;
        double deltaY = targetPos.y - eyePos.y;
        double deltaZ = targetPos.z - eyePos.z;

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distance));

        return new float[]{yaw, pitch};
    }

    public void setRotation(float yaw, float pitch) {
        this.currentYaw = yaw;
        this.currentPitch = pitch;
    }

    public void reset() {
        this.prevYawSpeed = 0;
        this.prevPitchSpeed = 0;
        this.ticksSinceSnap = 0;
        this.currentBodyPartIndex = 0;
        this.lastBodyPartSwitch = 0;
        this.momentumYaw = 0;
        this.momentumPitch = 0;
    }

    public boolean isOnTarget(float tolerance) {
        float deltaYaw = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));
        float deltaPitch = Math.abs(targetPitch - currentPitch);
        return deltaYaw < tolerance && deltaPitch < tolerance;
    }

    public String getProfileInfo() {
        return String.format("Type: %s | Smooth: %.2f | Speed: %.0f/%.0f | Jitter: %.2f | Multi: %d",
                profile.getRotationType(),
                profile.getSmoothness(),
                profile.getYawSpeed(),
                profile.getPitchSpeed(),
                profile.getJitterIntensity(),
                profile.getMultiPointCount()
        );
    }
}