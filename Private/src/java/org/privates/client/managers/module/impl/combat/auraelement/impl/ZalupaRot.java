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

import java.util.concurrent.ThreadLocalRandom;

public class ZalupaRot extends AbstractRotationElement {

    private float lastPitchTarget;
    private long lastPitchChangeTime;

    private float jitterPhase;
    private float jitterDirection;
    private float jitterAmplitude;

    private float patternOffsetYaw, patternOffsetPitch;
    private float patternSpeed, patternRadius;
    private float patternPhase;
    private long lastPatternShift;

    private float randomYawMultiplier, randomPitchMultiplier;
    private float randomPatternType;
    private float jitterSpeed;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) {
            return currentLerpRotation;
        }

        long time = System.currentTimeMillis();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if (time - lastPatternShift > 10000) {
            randomYawMultiplier = rng.nextFloat(0.5f, 2.5f);
            randomPitchMultiplier = rng.nextFloat(0.3f, 1.8f);
            randomPatternType = rng.nextFloat(0f, 3f);
            patternSpeed = rng.nextFloat(0.08f, 0.25f);
            patternRadius = rng.nextFloat(5f, 15f);
            jitterSpeed = rng.nextFloat(0.35f, 0.55f);
            jitterAmplitude = 16f + rng.nextFloat(-3f, 3f);
            jitterDirection = rng.nextFloat(0f, 1f) > 0.5f ? 1f : -1f;
            lastPatternShift = time;
        }

        jitterPhase += jitterSpeed * (0.85f + rng.nextFloat(0.3f));
        float smoothJitterYaw = (float) Math.sin(jitterPhase) * jitterAmplitude * jitterDirection;
        float smoothJitterPitch = (float) Math.sin(jitterPhase * 0.7f) * (jitterAmplitude * 0.4f);

        Vector3d playerEyes = mc.player.getEyePosition(1.0F);

        Vector3d targetPoint = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);

        patternPhase += patternSpeed * (0.7f + rng.nextFloat(0.6f));

        if (randomPatternType < 1f) {
            patternOffsetYaw = (float) Math.sin(patternPhase) * patternRadius * randomYawMultiplier;
            patternOffsetPitch = (float) Math.cos(patternPhase * 1.3) * (patternRadius * 0.4f) * randomPitchMultiplier;
        } else if (randomPatternType < 2f) {
            patternOffsetYaw = (float) (Math.sin(patternPhase) + Math.cos(patternPhase * 0.7)) * (patternRadius * 0.6f) * randomYawMultiplier;
            patternOffsetPitch = (float) (Math.sin(patternPhase * 1.5) * Math.cos(patternPhase * 0.5)) * (patternRadius * 0.3f) * randomPitchMultiplier;
        } else {
            patternOffsetYaw = (float) (Math.tan(patternPhase * 0.5) % 1.0) * patternRadius * randomYawMultiplier;
            patternOffsetPitch = (float) (Math.sin(patternPhase * 2) * Math.cos(patternPhase)) * (patternRadius * 0.35f) * randomPitchMultiplier;
        }

        Vector3d delta = targetPoint.subtract(playerEyes);
        double dist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, dist));

        targetYaw += smoothJitterYaw + patternOffsetYaw;
        targetPitch += smoothJitterPitch + patternOffsetPitch;

        targetPitch = MathHelper.clamp(targetPitch, -89.9f, 89.9f);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float finalYaw = currentLerpRotation.x;
        float finalPitch = currentLerpRotation.y;

        float yawSpeed = 120f + rng.nextFloat(-30f, 30f);
        float yawStep = yawDiff * (0.85f + rng.nextFloat(0.3f));

        if (Math.abs(yawStep) > yawSpeed) {
            yawStep = Math.copySign(yawSpeed, yawStep);
        }

        finalYaw += yawStep;

        if (Math.abs(pitchDiff) > 20f) {
            if (time - lastPitchChangeTime > 100) {
                float pitchSpeed = 2f + rng.nextFloat(-0.5f, 0.5f);
                float pitchStep = pitchDiff * 0.08f;

                if (Math.abs(pitchStep) > pitchSpeed) {
                    pitchStep = Math.copySign(pitchSpeed, pitchStep);
                }

                finalPitch += pitchStep;
                lastPitchChangeTime = time;
            }
        }

        finalYaw = GCDUtils.getSensitivity(finalYaw);
        finalPitch = GCDUtils.getSensitivity(MathHelper.clamp(finalPitch, -89.9f, 89.9f));

        Vector2f newRotation = new Vector2f(finalYaw, finalPitch);

        applyRotation(new Rotation(finalYaw, finalPitch),
                80 + rng.nextInt(40),
                40 + rng.nextInt(30),
                0, 3);
        parentModule.setLerpRotation(newRotation);

        return newRotation;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        lastPitchTarget = 0;
        lastPitchChangeTime = 0;

        jitterPhase = rng.nextFloat(0, (float) Math.PI * 2);
        jitterDirection = rng.nextFloat(0f, 1f) > 0.5f ? 1f : -1f;
        jitterAmplitude = 16f + rng.nextFloat(-3f, 3f);

        patternOffsetYaw = patternOffsetPitch = 0;
        patternPhase = rng.nextFloat(0, (float) Math.PI * 2);
        lastPatternShift = 0;

        randomYawMultiplier = rng.nextFloat(0.5f, 2.5f);
        randomPitchMultiplier = rng.nextFloat(0.3f, 1.8f);
        randomPatternType = rng.nextFloat(0f, 3f);
        patternSpeed = rng.nextFloat(0.08f, 0.25f);
        patternRadius = rng.nextFloat(15f, 45f);
        jitterSpeed = rng.nextFloat(0.15f, 0.35f);
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}