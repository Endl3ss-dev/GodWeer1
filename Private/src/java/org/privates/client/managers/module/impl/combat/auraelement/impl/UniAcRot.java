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

public class UniAcRot extends AbstractRotationElement {

    private boolean isSnapping = false;
    private boolean isBeforeAttack = false;
    private long snapStartTime = 0;
    private long snapDuration = 0;
    private Vector2f targetRotation;
    private Vector2f initialRotation;
    
    private float circularJitterPhase = 0f;
    private float jitterRadius = 0f;
    private float jitterStrength = 0f;
    private float jitterSpeed = 0f;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            resetState();
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();
        boolean canAttack = parentModule.perfectDelay().cooldownComplete() && parentModule.cooldownComplete();

        if (!isSnapping && canAttack) {
            startSnap(target, mc, currentTime, true);
        }

        if (isSnapping) {
            return handleSnap(parentModule, target, mc, currentLerpRotation, currentTime);
        }

        return currentLerpRotation;
    }

    private void startSnap(LivingEntity target, Minecraft mc, long currentTime, boolean beforeAttack) {
        isSnapping = true;
        isBeforeAttack = beforeAttack;
        snapStartTime = currentTime;
        
        if (beforeAttack) {
            snapDuration = ThreadLocalRandom.current().nextLong(15, 66);
        } else {
            snapDuration = ThreadLocalRandom.current().nextLong(25, 51);
        }
        
        initialRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        targetRotation = calculateTargetRotation(target, mc);
        
        jitterRadius = ThreadLocalRandom.current().nextFloat() * (15f - 13f) + 13f;
        jitterStrength = ThreadLocalRandom.current().nextFloat() * (2.65f - 2.25f) + 2.25f;
        jitterSpeed = ThreadLocalRandom.current().nextFloat() * (9f - 7f) + 7f;
        circularJitterPhase = 0f;
    }

    private Vector2f handleSnap(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation, long currentTime) {
        long elapsed = currentTime - snapStartTime;
        
        if (elapsed >= snapDuration) {
            if (isBeforeAttack) {
                performAttack(parentModule, target, mc);
                startSnap(target, mc, currentTime, false);
            } else {
                isSnapping = false;
            }
            return currentLerpRotation;
        }

        float progress = (float) elapsed / snapDuration;
        float easedProgress = easeInOutCubic(progress);

        float yawDelta = MathHelper.wrapDegrees(targetRotation.x - initialRotation.x);
        float pitchDelta = targetRotation.y - initialRotation.y;

        float baseYaw = initialRotation.x + yawDelta * easedProgress;
        float basePitch = initialRotation.y + pitchDelta * easedProgress;

        circularJitterPhase += jitterSpeed * 0.02f;
        float jitterX = (float) Math.cos(circularJitterPhase) * jitterRadius * jitterStrength * 0.01f;
        float jitterY = (float) Math.sin(circularJitterPhase) * jitterRadius * jitterStrength * 0.01f * 0.6f;

        float newYaw = baseYaw + jitterX;
        float newPitch = basePitch + jitterY;

        Vector2f newRotation = normalizeAngles(new Vector2f(newYaw, newPitch));
        newRotation.x = GCDUtils.getSensitivity(newRotation.x);
        newRotation.y = GCDUtils.getSensitivity(newRotation.y);

        float yawSpeed = ThreadLocalRandom.current().nextFloat() * (100f - 90f) + 90f;
        float pitchSpeed = ThreadLocalRandom.current().nextFloat() * (24f - 16f) + 16f;
        float smoothness = ThreadLocalRandom.current().nextFloat() * (45f - 35f) + 35f;

        float fpsMultiplier = calculateFPSMultiplier();
        yawSpeed *= fpsMultiplier;
        pitchSpeed *= fpsMultiplier;

        int smoothTicks = Math.round(smoothness / 10f);
        
        applyRotation(new Rotation(newRotation.x, newRotation.y), yawSpeed, pitchSpeed, 0, smoothTicks);
        parentModule.setLerpRotation(newRotation);

        return newRotation;
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
        float height = target.getEyeHeight() * (0.65f + ThreadLocalRandom.current().nextFloat() * 0.3f);
        Vector3d point = target.getPositionVec().add(0, height, 0);

        float widthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
        float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
        point = point.add(widthOffset, 0, depthOffset);

        return point;
    }

    private float calculateFPSMultiplier() {
        int fps = mc.getDebugFPS();
        if (fps <= 0) fps = 60;
        
        float baseFPS = 60f;
        float multiplier = baseFPS / fps;
        
        return MathHelper.clamp(multiplier, 0.5f, 2.0f);
    }

    private void performAttack(AuraNew parentModule, LivingEntity target, Minecraft mc) {
        parentModule.script().cleanup().addTickStep(0, () -> {
            parentModule.lastAttackTime = System.currentTimeMillis();
            mc.playerController.attackEntity(mc.player, target);
            mc.player.swingArm(net.minecraft.util.Hand.MAIN_HAND);
            parentModule.stopWatch().reset();
            parentModule.perfectDelay().reset(350L);
        });
    }

    private Vector2f normalizeAngles(Vector2f angles) {
        return new Vector2f(MathHelper.wrapDegrees(angles.x), MathHelper.clamp(angles.y, -90f, 90f));
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3f) / 2f;
    }

    private void resetState() {
        isSnapping = false;
        isBeforeAttack = false;
        snapStartTime = 0;
        snapDuration = 0;
        circularJitterPhase = 0f;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        resetState();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        resetState();
    }
}
