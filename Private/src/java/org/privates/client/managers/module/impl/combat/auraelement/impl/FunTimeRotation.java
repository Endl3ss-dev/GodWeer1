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
public class FunTimeRotation implements IMinecraft {

    long lastAttackTime;
    float lockedYaw;
    float lockedPitch;
    boolean isLocked;
    long lockStartTime;

    float currentAimOffsetX;
    float currentAimOffsetY;
    float currentAimOffsetZ;

    int hitCounter;
    boolean isPeriodicLookAway;
    long lookAwayStartTime;
    float lookAwayTargetYaw;
    float lookAwayTargetPitch;
    boolean lookAwayCompleted;

    float jitterPhaseYaw;
    float jitterPhasePitch;
    float jitterAmplitudeYaw;
    float jitterAmplitudePitch;
    long lastJitterUpdate;

    public void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        long currentTime = System.currentTimeMillis();

        if (!AuraNew.getInstance().isLookingUp && currentTime - AuraNew.getInstance().lastLookUpTime >= AuraNew.getInstance().nextLookUpDelay) {
            AuraNew.getInstance().isLookingUp = true;
            AuraNew.getInstance().lookUpStartTime = currentTime;
            AuraNew.getInstance().lookUpDuration = ThreadLocalRandom.current().nextInt(250, 350);
            AuraNew.getInstance().lastLookUpTime = currentTime;
            AuraNew.getInstance().nextLookUpDelay = ThreadLocalRandom.current().nextLong(10000, 20000);
        }

        if (AuraNew.getInstance().isLookingUp && currentTime - AuraNew.getInstance().lookUpStartTime >= AuraNew.getInstance().lookUpDuration) {
            AuraNew.getInstance().isLookingUp = false;
        }

        if (currentTime - lastJitterUpdate > ThreadLocalRandom.current().nextInt(700, 1300)) {
            jitterPhaseYaw = Mathf.randomValue(0.9F, 2.2F);
            jitterPhasePitch = Mathf.randomValue(0.7F, 1.8F);
            jitterAmplitudeYaw = Mathf.randomValue(5.0F, 9.0F);
            jitterAmplitudePitch = Mathf.randomValue(0.25F, 0.65F);
            lastJitterUpdate = currentTime;
        }

        float dynamicJitterYaw = (float) Math.sin(currentTime / (45D * jitterPhaseYaw)) * jitterAmplitudeYaw;
        float dynamicJitterPitch = (float) Math.cos(currentTime / (75D * jitterPhasePitch)) * jitterAmplitudePitch;

        float secondaryJitterYaw = (float) Math.cos(currentTime / 32D) * Mathf.randomValue(2.8F, 5.5F);
        float secondaryJitterPitch = (float) Math.sin(currentTime / 58D) * Mathf.randomValue(0.18F, 0.45F);

        Vector3d targetAimPoint = target.getPositionVec().add(
                currentAimOffsetX,
                MathHelper.clamp(playerEyePos.y - target.getPosY(), 0.15F, 1.35F) + currentAimOffsetZ,
                currentAimOffsetY
        );

        Vector3d directionVec = targetAimPoint.subtract(playerEyePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float targetPitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))),
                AuraNew.MIN_PITCH, AuraNew.MAX_PITCH
        );

        boolean shouldAttack = isAttack && AuraUtil.getStrictDistance(target) < attackDistance && !check;
        boolean canAttack = AuraNew.getInstance().cooldownFromLastSwing() > AuraNew.CRIT_COOLDOWN_THRESHOLD;

        if (isPeriodicLookAway) {
            long lookAwayDuration = currentTime - lookAwayStartTime;

            if (lookAwayDuration <= 140) {
                float lookAwayProgress = lookAwayDuration / 140F;
                float easing = (float) (1 - Math.pow(1 - lookAwayProgress, 2.8));

                float currentYaw = mc.player.rotationYaw;
                float currentPitch = mc.player.rotationPitch;

                float finalYaw = currentYaw + (lookAwayTargetYaw - currentYaw) * easing + dynamicJitterYaw * 0.6F + secondaryJitterYaw * 0.6F;
                float finalPitch = currentPitch + (lookAwayTargetPitch - currentPitch) * easing + dynamicJitterPitch * 0.6F + secondaryJitterPitch * 0.6F;

                float yawSpeed = Mathf.randomValue(16, 26);
                float pitchSpeed = Mathf.randomValue(1.3F, 2.6F);

                Rotation newRotation = new Rotation(finalYaw, finalPitch);
                RotationComponent.update(newRotation, yawSpeed, pitchSpeed, 6, 5, 0, 15, false);
                return;
            } else if (!lookAwayCompleted) {
                lookAwayCompleted = true;
                isLocked = true;
                lockStartTime = currentTime;

                currentAimOffsetX = Mathf.randomValue(-0.12F, 0.12F);
                currentAimOffsetY = Mathf.randomValue(-0.1F, 0.1F);
                currentAimOffsetZ = Mathf.randomValue(-0.06F, 0.06F);

                float preAimOffsetYaw = Mathf.randomValue(-0.9F, 0.9F);
                float preAimOffsetPitch = Mathf.randomValue(-0.25F, 0.25F);

                lockedYaw = targetYaw + preAimOffsetYaw;
                lockedPitch = targetPitch + preAimOffsetPitch;
            } else {
                isPeriodicLookAway = false;
                lookAwayCompleted = false;
            }
        }

        long timeSinceLastAttack = currentTime - lastAttackTime;
        boolean isPreAttackWindow = shouldAttack && canAttack && timeSinceLastAttack > 80;

        if (isPreAttackWindow) {
            if (!isLocked) {
                isLocked = true;
                lockStartTime = currentTime;
                lastAttackTime = currentTime;

                hitCounter++;

                currentAimOffsetX = Mathf.randomValue(-0.12F, 0.12F);
                currentAimOffsetY = Mathf.randomValue(-0.1F, 0.1F);
                currentAimOffsetZ = Mathf.randomValue(-0.06F, 0.06F);

                float preAimOffsetYaw = Mathf.randomValue(-0.9F, 0.9F);
                float preAimOffsetPitch = Mathf.randomValue(-0.25F, 0.25F);

                lockedYaw = targetYaw + preAimOffsetYaw;
                lockedPitch = targetPitch + preAimOffsetPitch;

                if (hitCounter >= ThreadLocalRandom.current().nextInt(10, 13)) {
                    hitCounter = 0;
                }
            }
        }

        float finalYaw;
        float finalPitch;
        float yawSpeed;
        float pitchSpeed;

        long lockDuration = currentTime - lockStartTime;

        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));
        float pitchDelta = Math.abs(targetPitch - currentPitch);

        double playerVelocity = Math.sqrt(mc.player.getMotion().x * mc.player.getMotion().x +
                mc.player.getMotion().z * mc.player.getMotion().z);
        double targetVelocity = Math.sqrt(target.getMotion().x * target.getMotion().x +
                target.getMotion().z * target.getMotion().z);

        boolean isHighMovement = playerVelocity > 0.18 || targetVelocity > 0.18;

        if (isLocked && lockDuration <= 95) {
            float lockProgress = lockDuration / 95F;
            float acceleration = (float) Math.pow(lockProgress, 0.68);

            if (yawDelta > 12.0F || pitchDelta > 6.0F) {
                float correctionYaw = Mathf.randomValue(-0.35F, 0.35F);
                float correctionPitch = Mathf.randomValue(-0.12F, 0.12F);

                lockedYaw = targetYaw + correctionYaw;
                lockedPitch = targetPitch + correctionPitch;
            }

            finalYaw = lockedYaw + dynamicJitterYaw + secondaryJitterYaw;
            finalPitch = lockedPitch + dynamicJitterPitch + secondaryJitterPitch;

            float baseSpeed = isHighMovement ? Mathf.randomValue(155, 215) : Mathf.randomValue(135, 195);
            yawSpeed = baseSpeed * acceleration + Mathf.randomValue(17, 28);
            pitchSpeed = Mathf.randomValue(1.6F, 3.6F);
        } else if (isLocked && lockDuration <= 175) {
            if (yawDelta > 8.5F || pitchDelta > 4.5F) {
                float fineAdjustYaw = Mathf.randomValue(-0.25F, 0.25F);
                float fineAdjustPitch = Mathf.randomValue(-0.1F, 0.1F);

                lockedYaw = targetYaw + fineAdjustYaw;
                lockedPitch = targetPitch + fineAdjustPitch;
            }

            finalYaw = lockedYaw + dynamicJitterYaw + secondaryJitterYaw;
            finalPitch = lockedPitch + dynamicJitterPitch + secondaryJitterPitch;

            yawSpeed = Mathf.randomValue(8, 14);
            pitchSpeed = Mathf.randomValue(0.9F, 2.0F);
        } else if (isLocked && shouldAttack && hitCounter == 0 && !isPeriodicLookAway) {
            isLocked = false;
            isPeriodicLookAway = true;
            lookAwayStartTime = currentTime;
            lookAwayCompleted = false;

            float lookAwayDirection = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
            float lookAwayYawOffset = Mathf.randomValue(15, 23) * lookAwayDirection;
            float lookAwayPitchOffset = Mathf.randomValue(-10, 10);

            if (Math.abs(lookAwayPitchOffset) > 5) {
                lookAwayPitchOffset = lookAwayPitchOffset > 0 ?
                        Mathf.randomValue(5, 10) : Mathf.randomValue(-10, -5);
            }

            lookAwayTargetYaw = mc.player.rotationYaw + lookAwayYawOffset;
            lookAwayTargetPitch = MathHelper.clamp(mc.player.rotationPitch + lookAwayPitchOffset, -90, 90);

            finalYaw = lockedYaw + dynamicJitterYaw + secondaryJitterYaw;
            finalPitch = lockedPitch + dynamicJitterPitch + secondaryJitterPitch;

            yawSpeed = Mathf.randomValue(5, 9);
            pitchSpeed = Mathf.randomValue(0.7F, 1.5F);
        } else {
            isLocked = false;

            float slowDriftYaw = (float) Math.sin(currentTime / 2400D) * Mathf.randomValue(0.7F, 1.5F);
            float slowDriftPitch = (float) Math.cos(currentTime / 3100D) * Mathf.randomValue(0.12F, 0.28F);

            finalYaw = lockedYaw + dynamicJitterYaw + secondaryJitterYaw + slowDriftYaw;
            finalPitch = lockedPitch + dynamicJitterPitch + secondaryJitterPitch + slowDriftPitch;

            yawSpeed = Mathf.randomValue(5, 9);
            pitchSpeed = Mathf.randomValue(0.7F, 1.5F);
        }

        float microTremorYaw = (float) Math.sin(currentTime / 10D) * Mathf.randomValue(0.6F, 1.3F);
        float microTremorPitch = (float) Math.cos(currentTime / 15D) * Mathf.randomValue(0.04F, 0.11F);

        finalYaw += microTremorYaw;
        finalPitch += microTremorPitch;

        if (AuraNew.getInstance().isLookingUp) {
            finalPitch = Mathf.randomValue(-72, -68);
            pitchSpeed = Mathf.randomValue(180, 240);
        }

        Rotation newRotation = new Rotation(finalYaw, finalPitch);
        RotationComponent.update(newRotation, yawSpeed, pitchSpeed, 64, 54, 0, 15, false);
    }
}