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


public class RockStarIntaveRot extends AbstractRotationElement {

    private Vector3d aimPoint = null;
    private int currentBodyPart = 0;
    private int nextBodyPart = 1;
    private final List<Vector3d> bodyPartOffsets = new ArrayList<>();
    private long multipointStartTime = 0;
    private float smoothness = 0.655f;
    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;
    private long lastSmoothnessUpdate = 0;
    private float baseYawSpeed = 310.19f;
    private float basePitchSpeed = 78.57f;
    private long lastSpeedUpdate = 0;
    private long pauseStart = 0;
    private long pauseDuration = 0;
    private boolean pauseActive = false;
    private int pauseCounter = 0;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (handlePause(currentTime)) {
            return currentLerpRotation;
        }

        if (currentTime - lastSpeedUpdate > ThreadLocalRandom.current().nextInt((int) randomize(1200f), (int) randomize(2000f))) {
            baseYawSpeed = randomize(85.19f);
            basePitchSpeed = randomize(45.57f);
            lastSpeedUpdate = currentTime;
        }

        if (currentTime - lastSmoothnessUpdate > ThreadLocalRandom.current().nextInt((int) randomize(900f), (int) randomize(1500f))) {
            smoothness = randomize(0.655f);
            lastSmoothnessUpdate = currentTime;
        }

        if (bodyPartOffsets.isEmpty()) {
            initializeBodyParts();
        }

        Vector3d targetPoint = getTargetPoint(target, currentTime);

        if (aimPoint == null) {
            aimPoint = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);
        }

        float blendSpeed = randomize(3.01f);
        aimPoint = aimPoint.lerp(targetPoint, blendSpeed * randomize(0.01f));

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = aimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float jitterAmplitude = randomize(0.85f);
        long jitterPeriod = (long) randomize(350f);
        if (jitterPeriod <= 0) jitterPeriod = 1;

        float phase = (currentTime % jitterPeriod) / (float) jitterPeriod;
        float jitterOffset = (float) Math.sin(phase * 2.0f * Math.PI) * jitterAmplitude;

        targetYaw += jitterOffset;
        targetPitch += (float) Math.cos(phase * 2.0f * Math.PI) * jitterAmplitude * randomize(0.2f);

        float verticalBias = randomize(0.30f);
        targetPitch += verticalBias;

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
        float effectiveYawSpeed = baseYawSpeed;
        float effectivePitchSpeed = basePitchSpeed;

        float accelFactor = randomize(5f);
        if (yawDelta > randomize(30f)) {
            effectiveYawSpeed *= 1.0f + (yawDelta / 180f) * accelFactor * randomize(0.1f);
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float momentum = randomize(0.172f);
        yawMomentum = MathHelper.lerp(momentum, yawMomentum, yawDiff * randomize(0.08f));
        pitchMomentum = MathHelper.lerp(momentum, pitchMomentum, pitchDiff * randomize(0.06f));

        yawDiff += yawMomentum;
        pitchDiff += pitchMomentum;

        float interpolation = smoothness * smoothness * smoothness * (smoothness * (smoothness * 6f - 15f) + 10f);

        float yawStep = yawDiff * interpolation;
        float pitchStep = pitchDiff * interpolation;

        if (Math.abs(yawStep) > Math.abs(yawDiff)) yawStep = yawDiff;
        if (Math.abs(pitchStep) > Math.abs(pitchDiff)) pitchStep = pitchDiff;

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), effectiveYawSpeed, effectivePitchSpeed, 0, 5);
        parentModule.setLerpRotation(result);
        return result;
    }


    private float randomize(float base) {
        return base - (base * 0.4f * ThreadLocalRandom.current().nextFloat());
    }


    private boolean handlePause(long currentTime) {
        if (pauseActive) {
            if (currentTime - pauseStart > pauseDuration) {
                pauseActive = false;
            }
            return true;
        }

        if (pauseCounter < randomize(53f) && ThreadLocalRandom.current().nextFloat() < randomize(0.01f)) {
            pauseActive = true;
            pauseStart = currentTime;
            pauseDuration = (long) randomize(60f);
            pauseCounter++;
            return true;
        }
        return false;
    }


    private Vector3d getTargetPoint(LivingEntity target, long currentTime) {
        long multipointDuration = (long) (randomize(500f) + 250f);
        if (currentTime - multipointStartTime > multipointDuration) {
            currentBodyPart = nextBodyPart;
            do {
                nextBodyPart = ThreadLocalRandom.current().nextInt(bodyPartOffsets.size());
            } while (nextBodyPart == currentBodyPart && bodyPartOffsets.size() > 1);
            multipointStartTime = currentTime;
        }

        if (bodyPartOffsets.isEmpty()) {
            return target.getPositionVec().add(0, target.getEyeHeight() * 0.5, 0);
        }

        currentBodyPart = Math.max(0, Math.min(currentBodyPart, bodyPartOffsets.size() - 1));
        nextBodyPart = Math.max(0, Math.min(nextBodyPart, bodyPartOffsets.size() - 1));

        long elapsed = currentTime - multipointStartTime;
        float progress = Math.min(1.0f, (float) elapsed / multipointDuration);
        progress = progress * progress * (3.0f - 2.0f * progress);

        Vector3d startOffset = bodyPartOffsets.get(currentBodyPart);
        Vector3d endOffset = bodyPartOffsets.get(nextBodyPart);

        double interpolatedY = MathHelper.lerp(progress, startOffset.y, endOffset.y);
        double targetHeight = target.getHeight() * interpolatedY;

        return target.getPositionVec().add(0, targetHeight, 0);
    }


    private void initializeBodyParts() {
        bodyPartOffsets.clear();
        bodyPartOffsets.add(new Vector3d(0, 0.85, 0));
        bodyPartOffsets.add(new Vector3d(0, 0.55, 0));
        bodyPartOffsets.add(new Vector3d(0, 0.25, 0));
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        aimPoint = null;
        currentBodyPart = 0;
        nextBodyPart = 1;
        bodyPartOffsets.clear();
        multipointStartTime = 0;
        smoothness = 0.655f;
        yawMomentum = 0f;
        pitchMomentum = 0f;
        lastSmoothnessUpdate = 0;
        baseYawSpeed = 310.19f;
        basePitchSpeed = 78.57f;
        lastSpeedUpdate = 0;
        pauseStart = 0;
        pauseDuration = 0;
        pauseActive = false;
        pauseCounter = 0;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        aimPoint = null;
        bodyPartOffsets.clear();
    }
}