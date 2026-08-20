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

public class RWRotation extends AbstractRotationElement {
    private float smoothYaw;
    private float smoothPitch;
    private long lastUpdateMs;
    private long lastJitterUpdateMs;
    private float jitterYaw;
    private float jitterPitch;
    private float speedYaw;
    private float speedPitch;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        long now = System.currentTimeMillis();
        if (lastUpdateMs == 0L) {
            lastUpdateMs = now;
            smoothYaw = currentLerpRotation.x;
            smoothPitch = currentLerpRotation.y;
        }

        float dt = MathHelper.clamp((now - lastUpdateMs) / 1000.0f, 0.001f, 0.05f);
        lastUpdateMs = now;

        Vector3d eyes = mc.player.getEyePosition(1.0f);
        Vector3d targetPoint = target.getPositionVec().add(0.0, target.getEyeHeight() * 0.86, 0.0);
        Vector3d delta = targetPoint.subtract(eyes);
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizontal));
        targetPitch = MathHelper.clamp(targetPitch, -89.0f, 89.0f);

        if (now - lastJitterUpdateMs > 55L) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            jitterYaw = random.nextFloat(-0.08f, 0.08f);
            jitterPitch = random.nextFloat(-0.05f, 0.05f);
            lastJitterUpdateMs = now;
        }

        float desiredYaw = targetYaw + jitterYaw;
        float desiredPitch = MathHelper.clamp(targetPitch + jitterPitch, -89.0f, 89.0f);

        float yawDiff = MathHelper.wrapDegrees(desiredYaw - smoothYaw);
        float pitchDiff = desiredPitch - smoothPitch;
        float absYaw = Math.abs(yawDiff);
        float absPitch = Math.abs(pitchDiff);

        float accelYaw = 44.0f * dt;
        float accelPitch = 38.0f * dt;
        speedYaw += yawDiff * accelYaw;
        speedPitch += pitchDiff * accelPitch;

        speedYaw *= 0.80f;
        speedPitch *= 0.79f;

        float dynamicYawStep = MathHelper.clamp(5.8f + absYaw * 0.18f, 5.8f, 13.5f);
        float dynamicPitchStep = MathHelper.clamp(4.3f + absPitch * 0.16f, 4.3f, 10.0f);

        float stepYaw = MathHelper.clamp(speedYaw, -dynamicYawStep, dynamicYawStep);
        float stepPitch = MathHelper.clamp(speedPitch, -dynamicPitchStep, dynamicPitchStep);

        smoothYaw += stepYaw;
        smoothPitch = MathHelper.clamp(smoothPitch + stepPitch, -89.0f, 89.0f);

        float finalYaw = GCDUtils.getSensitivity(smoothYaw);
        float finalPitch = GCDUtils.getSensitivity(smoothPitch);
        Vector2f result = new Vector2f(finalYaw, finalPitch);

        applyRotation(new Rotation(finalYaw, finalPitch), 225.0f, 165.0f, 0, 2);
        parentModule.setLerpRotation(result);
        return result;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastUpdateMs = 0L;
        lastJitterUpdateMs = 0L;
        jitterYaw = 0.0f;
        jitterPitch = 0.0f;
        speedYaw = 0.0f;
        speedPitch = 0.0f;
        if (Minecraft.getInstance().player != null) {
            smoothYaw = Minecraft.getInstance().player.rotationYaw;
            smoothPitch = Minecraft.getInstance().player.rotationPitch;
        }
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        lastUpdateMs = 0L;
        speedYaw = 0.0f;
        speedPitch = 0.0f;
    }
}

