package org.privates.client.managers.module.impl.combat.auraelement.impl;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class NewFunTimeRotation implements IMinecraft {

    float currentAimHeight = 0.5f;
    long lastAttackTime = 0;

    boolean shouldRecoil = false;
    long recoilStartTime = 0;
    float recoilYaw = 0;
    float recoilPitch = 0;
    float recoilAmount = 0;

    boolean shouldLookDown = false;
    long lookDownStartTime = 0;
    long nextLookDownTime = 0;
    float lookDownTargetPitch = 0;

    public void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        if (target == null) {
            reset();
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (nextLookDownTime == 0) {
            nextLookDownTime = currentTime + ThreadLocalRandom.current().nextLong(10000, 20000);
        }

        if (!shouldLookDown && currentTime >= nextLookDownTime) {
            shouldLookDown = true;
            lookDownStartTime = currentTime;
            lookDownTargetPitch = Mathf.randomValue(-85f, -90f);
            nextLookDownTime = currentTime + ThreadLocalRandom.current().nextLong(10000, 20000);
        }

        if (shouldLookDown) {
            long lookDownElapsed = currentTime - lookDownStartTime;
            if (lookDownElapsed < 2000) {
                performLookDown(currentTime, lookDownElapsed);
                return;
            } else {
                shouldLookDown = false;
            }
        }

        if (shouldRecoil) {
            long recoilElapsed = currentTime - recoilStartTime;
            if (recoilElapsed < 150) {
                performRecoil(currentTime);
                return;
            } else {
                shouldRecoil = false;
            }
        }

        if (isAttack && AuraUtil.getStrictDistance(target) < attackDistance && !check) {
            performAttack(target, currentTime);
            return;
        }

        performTracking(target, currentTime);
    }

    private void performAttack(LivingEntity target, long currentTime) {
        currentAimHeight = Mathf.randomValue(0.25f, 0.9f);

        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        float targetHeight = target.getHeight();
        float aimHeight = targetHeight * currentAimHeight;

        float horizontalSpread = target.getWidth() * Mathf.randomValue(0.1f, 0.35f);
        float randomOffsetX = Mathf.randomValue(-horizontalSpread, horizontalSpread);
        float randomOffsetZ = Mathf.randomValue(-horizontalSpread, horizontalSpread);

        Vector3d aimPoint = target.getPositionVec().add(randomOffsetX, aimHeight, randomOffsetZ);

        Vector3d targetVelocity = target.getMotion();
        float predictionFactor = Mathf.randomValue(0.05f, 0.25f);
        aimPoint = aimPoint.add(targetVelocity.scale(predictionFactor));

        Vector3d directionVec = aimPoint.subtract(playerEyePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z)));
        targetPitch = MathHelper.clamp(targetPitch, AuraNew.MIN_PITCH, AuraNew.MAX_PITCH);

        float attackYawSpeed = Mathf.randomValue(150f, 350f);
        float attackPitchSpeed = Mathf.randomValue(120f, 300f);

        Rotation attackRotation = new Rotation(targetYaw, targetPitch);
        RotationComponent.update(attackRotation, attackYawSpeed, attackPitchSpeed, 0, 0, 0, 15, false);

        float baseRecoil;
        if (currentAimHeight > 0.7f) {
            baseRecoil = Mathf.randomValue(12f, 16f);
        } else if (currentAimHeight < 0.4f) {
            baseRecoil = Mathf.randomValue(12f, 16f);
        } else {
            baseRecoil = Mathf.randomValue(7f, 10f);
        }

        float currentYaw = mc.player.rotationYaw;
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);

        recoilYaw = targetYaw;
        recoilPitch = targetPitch;
        recoilAmount = baseRecoil * Math.signum(yawDiff);

        shouldRecoil = true;
        recoilStartTime = currentTime;
        lastAttackTime = currentTime;
    }

    private void performRecoil(long currentTime) {
        float targetYaw = recoilYaw + recoilAmount;
        float targetPitch = recoilPitch;

        float recoilYawSpeed = Mathf.randomValue(40f, 120f);
        float recoilPitchSpeed = Mathf.randomValue(30f, 90f);

        Rotation recoilRotation = new Rotation(targetYaw, targetPitch);
        RotationComponent.update(recoilRotation, recoilYawSpeed, recoilPitchSpeed, 0, 0, 0, 15, false);
    }

    private void performLookDown(long currentTime, long elapsed) {
        float currentPitch = mc.player.rotationPitch;
        float currentYaw = mc.player.rotationYaw;

        float progress;
        float targetPitch;

        if (elapsed < 1000) {
            progress = elapsed / 1000f;
            float easeProgress = (float) (1.0 - Math.pow(1.0 - progress, 2.0));
            targetPitch = MathHelper.lerp(easeProgress, currentPitch, lookDownTargetPitch);
        } else {
            progress = (elapsed - 1000) / 1000f;
            float easeProgress = (float) (1.0 - Math.pow(1.0 - progress, 2.0));
            targetPitch = MathHelper.lerp(easeProgress, lookDownTargetPitch, 0f);
        }

        float shake = Mathf.randomValue(12f, 16f);
        float shakeYaw = (float) Math.sin(currentTime / Mathf.randomValue(60D, 100D)) * shake;
        float shakePitch = (float) Math.cos(currentTime / Mathf.randomValue(70D, 110D)) * shake * 0.5f;

        float finalYaw = currentYaw + shakeYaw;
        float finalPitch = targetPitch + shakePitch;

        float lookSpeed = Mathf.randomValue(30f, 80f);
        float lookPitchSpeed = Mathf.randomValue(25f, 70f);

        Rotation lookRotation = new Rotation(finalYaw, finalPitch);
        RotationComponent.update(lookRotation, lookSpeed, lookPitchSpeed, 0, 0, 0, 15, false);
    }

    private void performTracking(LivingEntity target, long currentTime) {
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        float targetHeight = target.getHeight();
        float aimHeight = targetHeight * 0.55f;

        Vector3d targetPos = target.getPositionVec().add(0, aimHeight, 0);

        Vector3d targetVelocity = target.getMotion();
        targetPos = targetPos.add(targetVelocity.scale(0.08f));

        Vector3d directionVec = targetPos.subtract(playerEyePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z)));
        targetPitch = MathHelper.clamp(targetPitch, AuraNew.MIN_PITCH, AuraNew.MAX_PITCH);

        float oscillation = Mathf.randomValue(2f, 5f);
        float oscillationYaw = (float) Math.sin(currentTime / 180D) * oscillation;
        float oscillationPitch = (float) Math.cos(currentTime / 200D) * oscillation * 0.6f;

        targetYaw += oscillationYaw;
        targetPitch += oscillationPitch;

        float trackingSpeed = Mathf.randomValue(18f, 45f);
        float trackingPitchSpeed = Mathf.randomValue(15f, 38f);

        Rotation trackingRotation = new Rotation(targetYaw, targetPitch);
        RotationComponent.update(trackingRotation, trackingSpeed, trackingPitchSpeed,
                ThreadLocalRandom.current().nextInt(2, 5),
                ThreadLocalRandom.current().nextInt(2, 4), 0, 15, false);
    }

    private void reset() {
        currentAimHeight = 0.5f;
        lastAttackTime = 0;
        shouldRecoil = false;
        shouldLookDown = false;
        recoilStartTime = 0;
        lookDownStartTime = 0;
        nextLookDownTime = 0;
    }
}