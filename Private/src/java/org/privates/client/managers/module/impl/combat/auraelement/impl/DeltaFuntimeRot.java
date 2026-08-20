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

import java.util.Random;

public class DeltaFuntimeRot extends AbstractRotationElement {

    private final Random random = new Random();

    private long phase1 = System.currentTimeMillis() ^ 0x5DEECE66DL;
    private long phase2 = System.currentTimeMillis() ^ 0x1B3F4A7C2D9EL;
    private long phase3 = System.currentTimeMillis() ^ 0x9E3779B97F4A7C15L;
    private long phase4 = System.currentTimeMillis() ^ 0x8B8B8B8B8B8B8B8BL;

    private boolean wasOvershooting = false;
    private float overshootRecovery = 0f;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        Vector3d eyes = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec()
                .add(0, target.getEyeHeight() * 0.75 + randomOffset(-0.1, 0.15), 0)
                .add(randomOffset(-0.05, 0.05), 0, randomOffset(-0.05, 0.05));

        double dx = targetPos.x - eyes.x;
        double dy = targetPos.y - eyes.y;
        double dz = targetPos.z - eyes.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(dy, distXZ)));
        targetPitch = MathHelper.clamp(targetPitch, -89.9F, 89.9F);

        float distance = (float) eyes.distanceTo(targetPos);
        float angularSize = (float) Math.toDegrees(Math.atan2(target.getWidth() * 0.9, distance));

        long time = System.nanoTime() / 1_000_000L;

        double jitterX = fastNoise(time * 0.017, phase1) * angularSize * 0.9
                + fastNoise(time * 0.043, phase2) * angularSize * 0.4
                + fastNoise(time * 0.009, phase3) * angularSize * 0.6;

        double jitterY = fastNoise(time * 0.021, phase1 + 1000) * angularSize * 0.5
                + fastNoise(time * 0.067, phase2 + 2000) * angularSize * 0.25
                + fastNoise(time * 0.012, phase4) * angularSize * 0.35;

        targetYaw += (float) jitterX;
        targetPitch += (float) jitterY;

        if (random.nextFloat() < 0.13f) {
            float overshoot = 0.7f + random.nextFloat() * 2.1f;
            targetYaw += wasOvershooting ? -overshootRecovery : overshoot;
            targetPitch += wasOvershooting ? -overshootRecovery * 0.7f : overshoot * 0.7f;
            wasOvershooting = true;
            overshootRecovery = overshoot * (0.4f + random.nextFloat() * 0.9f);
        } else if (wasOvershooting && random.nextFloat() < 0.4f) {
            targetYaw -= overshootRecovery * 0.7f;
            targetPitch -= overshootRecovery * 0.5f;
            overshootRecovery *= 0.6f;
            if (overshootRecovery < 0.1f) wasOvershooting = false;
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float baseSpeed = 0.15f + random.nextFloat() * 0.42f;
        float dynamicSpeed = baseSpeed
                + (float) fastNoise(time * 0.03, phase4) * 0.18f
                + (float) Math.sin(time * 0.007) * 0.08f;

        float reactionFactor = Math.min(1.0f, distance / 5.5f);
        dynamicSpeed *= 0.6f + reactionFactor * 0.8f;

        float t = Math.min(1.0f, dynamicSpeed * 2.2f);
        float ease = t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;

        float finalYaw = currentLerpRotation.x + yawDiff * ease;
        float finalPitch = currentLerpRotation.y + pitchDiff * ease;

        if (Math.abs(yawDiff) < 4.0f && Math.abs(pitchDiff) < 3.0f) {
            finalYaw += (random.nextFloat() - 0.5f) * 0.4f;
            finalPitch += (random.nextFloat() - 0.5f) * 0.25f;
        }

        float gcdFixYaw = GCDUtils.getSensitivity(finalYaw - currentLerpRotation.x) + currentLerpRotation.x;
        float gcdFixPitch = GCDUtils.getSensitivity(finalPitch - currentLerpRotation.y) + currentLerpRotation.y;

        if (random.nextFloat() < 0.07f) {
            float randSens = 0.02f + random.nextFloat() * 0.09f;
            gcdFixYaw += randSens;
            gcdFixPitch += randSens * 0.6f;
        }

        Vector2f result = new Vector2f(gcdFixYaw, gcdFixPitch);

        applyRotation(new Rotation(result.x, result.y), 180, 90, 0, 7);
        parentModule.setLerpRotation(result);

        phase1 += 3 + random.nextInt(7);
        phase2 += 5 + random.nextInt(9);
        phase3 += 2 + random.nextInt(5);
        phase4 += 7 + random.nextInt(11);

        return result;
    }

    private double fastNoise(double t, long seed) {
        long i = (long) (t + seed);
        i = i ^ (i >> 33);
        i *= 0x9e3779b97f4a7c15L;
        i = i ^ (i >> 29);
        i *= 0x9e3779b97f4a7c15L;
        i = i ^ (i >> 32);
        double val = (i >> 16) / (double) 0xFFFF;
        double f = t - (long) t;
        double fade = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
        return val * 2.0 - 1.0 + fade * 0.1;
    }

    private double randomOffset(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        wasOvershooting = false;
        overshootRecovery = 0f;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}