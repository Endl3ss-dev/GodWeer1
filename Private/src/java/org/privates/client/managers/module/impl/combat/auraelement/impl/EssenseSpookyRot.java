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
import org.privates.client.utils.other.PerlinNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class EssenseSpookyRot extends AbstractRotationElement {

    private enum BodySection {
        UPPER_BODY(0.70), MID_BODY(0.50), LOWER_BODY(0.25);

        private final double heightRatio;

        BodySection(double heightRatio) {
            this.heightRatio = heightRatio;
        }
    }

    private final PerlinNoise perlin = new PerlinNoise(System.currentTimeMillis());
    private final List<Vector3d> targetPoints = new ArrayList<>();
    private Vector3d aimPosition = null;
    private int sectionIndex = 0;
    private long lastSectionChange = 0;
    private long sectionChangeInterval = 520;
    private float smoothFactor = 0.58f;
    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;
    private long rotationStartTime = 0;
    private float baseYawVelocity = 382.5f;
    private float basePitchVelocity = 81.2f;
    private long lastVelocityUpdate = 0;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (rotationStartTime == 0) {
            rotationStartTime = currentTime;
            initializeTargetPoints();
            randomizeVelocities();
        }

        if (currentTime - lastVelocityUpdate > ThreadLocalRandom.current().nextInt(800, 1400)) {
            randomizeVelocities();
            smoothFactor = randomizeInRange(0.58f, 0.15f, 0.25f);
            lastVelocityUpdate = currentTime;
        }

        if (targetPoints.isEmpty()) {
            initializeTargetPoints();
        }

        Vector3d targetPosition = selectTargetSection(target, currentTime);

        if (aimPosition == null) {
            aimPosition = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);
        }

        float transition = randomizeInRange(0.06f, 0.20f, 0.30f);
        aimPosition = aimPosition.lerp(targetPosition, transition);

        Vector3d eyePosition = mc.player.getEyePosition(1.0f);
        Vector3d vectorToTarget = aimPosition.subtract(eyePosition);

        float rawYaw = (float) Math.toDegrees(Math.atan2(vectorToTarget.z, vectorToTarget.x)) - 90.0f;
        float rawPitch = (float) -Math.toDegrees(Math.atan2(vectorToTarget.y, Math.sqrt(vectorToTarget.x * vectorToTarget.x + vectorToTarget.z * vectorToTarget.z)));

        applyChaoticMovement(currentTime);

        float perlinInfluence = randomizeInRange(0.30f, 0.25f, 0.40f);
        rawYaw += (float) perlin.noise(currentTime * 0.0006, 0, 0.1) * perlinInfluence;
        rawPitch += (float) perlin.noise(0, currentTime * 0.0006, 0.1) * (perlinInfluence * 0.5f);

        float yawDistance = Math.abs(MathHelper.wrapDegrees(rawYaw - currentLerpRotation.x));
        float dynamicYawSpeed = baseYawVelocity;
        float dynamicPitchSpeed = basePitchVelocity;

        if (yawDistance > 45f) {
            dynamicYawSpeed *= randomizeInRange(1.15f, 0.10f, 0.20f);
        } else if (yawDistance < 15f) {
            dynamicYawSpeed *= randomizeInRange(0.85f, 0.10f, 0.15f);
        }

        float yawDifference = MathHelper.wrapDegrees(rawYaw - currentLerpRotation.x);
        float pitchDifference = rawPitch - currentLerpRotation.y;

        float momentumBlend = randomizeInRange(0.25f, 0.20f, 0.30f);
        yawMomentum = MathHelper.lerp(momentumBlend, yawMomentum, yawDifference * 0.09f);
        pitchMomentum = MathHelper.lerp(momentumBlend, pitchMomentum, pitchDifference * 0.07f);

        float accelerationBoost = randomizeInRange(0.8f, 0.15f, 0.25f) + (yawDistance / 180f) * randomizeInRange(3.0f, 0.25f, 0.40f);
        yawDifference += yawMomentum * accelerationBoost;
        pitchDifference += pitchMomentum * accelerationBoost;

        float interpolation = calculateInterpolation(smoothFactor);
        float yawChange = yawDifference * interpolation;
        float pitchChange = pitchDifference * interpolation;

        if (Math.abs(yawChange) > Math.abs(yawDifference)) yawChange = yawDifference;
        if (Math.abs(pitchChange) > Math.abs(pitchDifference)) pitchChange = pitchDifference;

        float resultYaw = currentLerpRotation.x + yawChange;
        float resultPitch = currentLerpRotation.y + pitchChange;

        resultYaw = GCDUtils.getSensitivity(resultYaw);
        resultPitch = GCDUtils.getSensitivity(MathHelper.clamp(resultPitch, -89.9f, 89.9f));

        Vector2f finalRotation = new Vector2f(resultYaw, resultPitch);
        applyRotation(new Rotation(resultYaw, resultPitch), dynamicYawSpeed, dynamicPitchSpeed, 0, 5);
        parentModule.setLerpRotation(finalRotation);
        return finalRotation;
    }


    private void randomizeVelocities() {
        baseYawVelocity = randomizeInRange(382.5f, 0.15f, 0.25f);
        basePitchVelocity = randomizeInRange(81.2f, 0.15f, 0.25f);
    }


    private float randomizeInRange(float base, float minPercent, float maxPercent) {
        float variance = minPercent + ThreadLocalRandom.current().nextFloat() * (maxPercent - minPercent);
        float offset = base * variance;
        return base + (ThreadLocalRandom.current().nextBoolean() ? offset : -offset);
    }


    private Vector3d selectTargetSection(LivingEntity target, long currentTime) {
        if (currentTime - lastSectionChange > sectionChangeInterval) {
            sectionIndex = ThreadLocalRandom.current().nextInt(targetPoints.size());
            sectionChangeInterval = (long) randomizeInRange(520f, 0.25f, 0.40f);
            lastSectionChange = currentTime;
        }

        Vector3d basePosition = targetPoints.get(sectionIndex);

        double xVariance = (ThreadLocalRandom.current().nextDouble() - 0.5) * randomizeInRange(0.14f, 0.25f, 0.40f);
        double zVariance = (ThreadLocalRandom.current().nextDouble() - 0.5) * randomizeInRange(0.14f, 0.25f, 0.40f);

        return target.getPositionVec().add(
                basePosition.x + xVariance,
                basePosition.y * target.getHeight(),
                basePosition.z + zVariance
        );
    }


    private void applyChaoticMovement(long currentTime) {
        float timeScale = currentTime * 0.001f;

        float oscillation1 = (float) Math.sin(timeScale * randomizeInRange(1.5f, 0.25f, 0.40f)) * randomizeInRange(0.35f, 0.25f, 0.40f);
        float oscillation2 = (float) Math.cos(timeScale * randomizeInRange(3.2f, 0.25f, 0.40f)) * randomizeInRange(0.28f, 0.25f, 0.40f);

        yawMomentum += (oscillation1 + oscillation2) * 0.007f;

        float pitchOsc1 = (float) Math.cos(timeScale * randomizeInRange(2.0f, 0.25f, 0.40f)) * randomizeInRange(0.22f, 0.25f, 0.40f);

        pitchMomentum += pitchOsc1 * 0.005f;
    }


    private void initializeTargetPoints() {
        targetPoints.clear();
        targetPoints.add(new Vector3d(0, BodySection.UPPER_BODY.heightRatio, 0));
        targetPoints.add(new Vector3d(0, BodySection.MID_BODY.heightRatio, 0));
        targetPoints.add(new Vector3d(0, BodySection.LOWER_BODY.heightRatio, 0));
    }


    private float calculateInterpolation(float t) {
        return t * t * t * (t * (t * 6f - 15f) + 10f);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        aimPosition = null;
        targetPoints.clear();
        sectionIndex = 0;
        lastSectionChange = 0;
        sectionChangeInterval = 520;
        smoothFactor = 0.58f;
        yawMomentum = 0f;
        pitchMomentum = 0f;
        rotationStartTime = 0;
        baseYawVelocity = 382.5f;
        basePitchVelocity = 81.2f;
        lastVelocityUpdate = 0;
        randomizeVelocities();
        initializeTargetPoints();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        aimPosition = null;
        targetPoints.clear();
        rotationStartTime = 0;
    }
}