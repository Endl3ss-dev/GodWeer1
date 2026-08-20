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

import java.util.concurrent.ThreadLocalRandom;


public class NursultanSpookyRot extends AbstractRotationElement {

    private final PerlinNoise perlin = new PerlinNoise(System.currentTimeMillis());
    private Vector3d currentAimPoint = null;
    private int currentMultiPoint = 0;
    private long lastMultiPointSwitch = 0;
    private float yawMomentum = 0f;
    private float pitchMomentum = 0f;
    private long lastPauseTime = 0;
    private long nextPauseDelay = 0;
    private boolean inPause = false;
    private int pauseCounter = 0;
    private long sessionStart = 0;
    private float baseYawSpeed = 157.4f;
    private float basePitchSpeed = 27.3f;
    private float baseSmoothness = 0.507f;
    private long lastSpeedUpdate = 0;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (sessionStart == 0) {
            sessionStart = currentTime;
            randomizeParameters();
        }

        if (currentTime - lastSpeedUpdate > ThreadLocalRandom.current().nextInt(700, 1400)) {
            randomizeParameters();
            lastSpeedUpdate = currentTime;
        }

        if (handlePauseMechanism(currentTime)) {
            return currentLerpRotation;
        }

        Vector3d targetPoint = getMultiPointTarget(target, currentTime);

        if (currentAimPoint == null) {
            currentAimPoint = target.getPositionVec().add(0, target.getHeight() * 0.7, 0);
        }

        float transitionSpeed = randomizeValue(0.15f, 0.25f);
        currentAimPoint = currentAimPoint.lerp(targetPoint, transitionSpeed);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        applyChaoticMovement(currentTime);

        float perlinInfluence = randomizeValue(0.3f, 0.25f);
        targetYaw += (float) perlin.noise(currentTime * 0.0008, 0, 0.12) * perlinInfluence;
        targetPitch += (float) perlin.noise(0, currentTime * 0.0008, 0.12) * perlinInfluence * 0.6f;

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
        float pitchDelta = Math.abs(targetPitch - currentLerpRotation.y);

        float effectiveYawSpeed = baseYawSpeed;
        float effectivePitchSpeed = basePitchSpeed;

        if (yawDelta > 35f) {
            effectiveYawSpeed *= randomizeValue(1.15f, 0.25f) + (yawDelta / 180f) * 0.2f;
        } else if (yawDelta < 15f) {
            effectiveYawSpeed *= randomizeValue(0.85f, 0.25f);
        }

        if (pitchDelta > 20f) {
            effectivePitchSpeed *= randomizeValue(1.12f, 0.25f) + (pitchDelta / 90f) * 0.18f;
        } else if (pitchDelta < 10f) {
            effectivePitchSpeed *= randomizeValue(0.88f, 0.25f);
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float momentumBlend = randomizeValue(0.28f, 0.25f);
        yawMomentum = MathHelper.lerp(momentumBlend, yawMomentum, yawDiff * 0.1f);
        pitchMomentum = MathHelper.lerp(momentumBlend, pitchMomentum, pitchDiff * 0.08f);

        float accelerationBoost = randomizeValue(1.0f, 0.25f) + (yawDelta / 180f) * randomizeValue(3.0f, 0.25f);
        yawDiff += yawMomentum * accelerationBoost;
        pitchDiff += pitchMomentum * accelerationBoost;

        float interpolationFactor = calculateSmoothInterpolation(baseSmoothness);

        float yawStep = yawDiff * interpolationFactor;
        float pitchStep = pitchDiff * interpolationFactor;

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


    private void randomizeParameters() {
        baseYawSpeed = randomizeValue(157.4f, 0.25f);
        basePitchSpeed = randomizeValue(27.3f, 0.25f);
        baseSmoothness = randomizeValue(0.507f, 0.25f);
    }


    private float randomizeValue(float base, float variancePercent) {
        float variance = base * variancePercent;
        float min = base - variance;
        float max = base;
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }


    private Vector3d getMultiPointTarget(LivingEntity target, long currentTime) {
        if (currentTime - lastMultiPointSwitch > randomizeValue(280f, 0.25f)) {
            currentMultiPoint = (currentMultiPoint + 1) % 2;
            lastMultiPointSwitch = currentTime;
        }

        double heightPercent = currentMultiPoint == 0 ? randomizeValue(0.85f, 0.25f) : randomizeValue(0.55f, 0.25f);

        double xJitter = (ThreadLocalRandom.current().nextDouble() - 0.5) * randomizeValue(0.12f, 0.25f);
        double zJitter = (ThreadLocalRandom.current().nextDouble() - 0.5) * randomizeValue(0.12f, 0.25f);

        return target.getPositionVec().add(
                xJitter,
                target.getHeight() * heightPercent,
                zJitter
        );
    }


    private void applyChaoticMovement(long currentTime) {
        float timeScale = currentTime * 0.001f;

        float wave1 = (float) Math.sin(timeScale * randomizeValue(1.8f, 0.25f)) * randomizeValue(0.4f, 0.25f);
        float wave2 = (float) Math.cos(timeScale * randomizeValue(3.5f, 0.25f)) * randomizeValue(0.32f, 0.25f);
        float wave3 = (float) Math.sin(timeScale * randomizeValue(6.2f, 0.25f)) * randomizeValue(0.25f, 0.25f);

        yawMomentum += (wave1 + wave2 + wave3) * 0.01f;

        float pitchWave1 = (float) Math.cos(timeScale * randomizeValue(2.3f, 0.25f)) * randomizeValue(0.28f, 0.25f);
        float pitchWave2 = (float) Math.sin(timeScale * randomizeValue(4.8f, 0.25f)) * randomizeValue(0.22f, 0.25f);

        pitchMomentum += (pitchWave1 + pitchWave2) * 0.008f;
    }


    private boolean handlePauseMechanism(long currentTime) {
        if (inPause) {
            if (currentTime - lastPauseTime > nextPauseDelay) {
                inPause = false;
            }
            return true;
        }

        long elapsed = currentTime - sessionStart;

        if (elapsed > 5000 && pauseCounter < 13) {
            float pauseChance = randomizeValue(0.015f, 0.25f);

            if (ThreadLocalRandom.current().nextFloat() < pauseChance) {
                inPause = true;
                lastPauseTime = currentTime;
                nextPauseDelay = (long) randomizeValue(60f, 0.25f);
                pauseCounter++;
                return true;
            }
        }

        return false;
    }


    private float calculateSmoothInterpolation(float t) {
        return t * t * t * (t * (t * 6f - 15f) + 10f);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        currentAimPoint = null;
        currentMultiPoint = 0;
        lastMultiPointSwitch = 0;
        yawMomentum = 0f;
        pitchMomentum = 0f;
        lastPauseTime = 0;
        nextPauseDelay = 0;
        inPause = false;
        pauseCounter = 0;
        sessionStart = 0;
        baseYawSpeed = 157.4f;
        basePitchSpeed = 27.3f;
        baseSmoothness = 0.507f;
        lastSpeedUpdate = 0;
        randomizeParameters();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        currentAimPoint = null;
        sessionStart = 0;
    }
}