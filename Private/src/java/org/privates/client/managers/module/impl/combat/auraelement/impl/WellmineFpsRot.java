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

public class WellmineFpsRot extends AbstractRotationElement {

    private float jitterPhase = 0f;
    private long lastJitterUpdate = 0;
    private float currentJitterX = 0f;
    private float currentJitterY = 0f;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.7, 0);
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerPos);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastJitterUpdate > 50) {
            jitterPhase += 0.1f;
            lastJitterUpdate = currentTime;
        }

        float jitterStrength = 0.27f;
        float targetJitterX = (float) Math.sin(jitterPhase) * jitterStrength;
        float targetJitterY = (float) Math.cos(jitterPhase * 1.3f) * jitterStrength * 0.5f;

        currentJitterX = MathHelper.lerp(0.3f, currentJitterX, targetJitterX);
        currentJitterY = MathHelper.lerp(0.3f, currentJitterY, targetJitterY);

        targetYaw += currentJitterX;
        targetPitch += currentJitterY;

        int currentFps = mc.getDebugFPS();
        if (currentFps <= 0) currentFps = 60;
        if (currentFps > 240) currentFps = 240;

        float fpsRatio = currentFps / 240f;

        float baseSpeed = 0.28f;
        float yawSpeed = baseSpeed * (0.7f + fpsRatio * 0.3f);
        float pitchSpeed = yawSpeed * 0.75f;

        float randomFactor = ThreadLocalRandom.current().nextFloat() * 0.15f + 0.925f;
        yawSpeed *= randomFactor;
        pitchSpeed *= randomFactor;

        float smoothYaw = wrapLerp(yawSpeed, currentLerpRotation.x, targetYaw);
        float smoothPitch = wrapLerp(pitchSpeed, currentLerpRotation.y, MathHelper.clamp(targetPitch, -90f, 90f));

        smoothYaw = GCDUtils.getSensitivity(smoothYaw);
        smoothPitch = GCDUtils.getSensitivity(smoothPitch);

        Vector2f newLerpRotation = new Vector2f(smoothYaw, smoothPitch);

        float applyYawSpeed = 120f + ThreadLocalRandom.current().nextFloat() * 20f - 10f;
        float applyPitchSpeed = 80f + ThreadLocalRandom.current().nextFloat() * 15f - 7.5f;

        applyRotation(new Rotation(smoothYaw, smoothPitch), applyYawSpeed, applyPitchSpeed, 0, 3);
        parentModule.setLerpRotation(newLerpRotation);

        return newLerpRotation;
    }

    private Vector2f calculateTargetRotation(LivingEntity target, Minecraft mc) {
        Vector3d aimPoint = getAimPoint(target);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = aimPoint.subtract(playerEyes);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        return normalizeAngles(new Vector2f(yaw, pitch));
    }

    private Vector3d getAimPoint(LivingEntity target) {
        float height = target.getEyeHeight() * (0.7f + ThreadLocalRandom.current().nextFloat() * 0.2f);
        Vector3d point = target.getPositionVec().add(0, height, 0);

        float widthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.25f;
        float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.25f;
        point = point.add(widthOffset, 0, depthOffset);

        return point;
    }

    private Vector2f normalizeAngles(Vector2f angles) {
        return new Vector2f(MathHelper.wrapDegrees(angles.x), MathHelper.clamp(angles.y, -90f, 90f));
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        jitterPhase = 0f;
        lastJitterUpdate = 0;
        currentJitterX = 0f;
        currentJitterY = 0f;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        jitterPhase = 0f;
        currentJitterX = 0f;
        currentJitterY = 0f;
    }
}
