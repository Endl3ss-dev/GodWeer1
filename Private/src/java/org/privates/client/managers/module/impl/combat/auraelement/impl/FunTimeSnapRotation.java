package org.privates.client.managers.module.impl.combat.auraelement.impl;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.rotation.FreeLookComponent;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class FunTimeSnapRotation implements IMinecraft {

    float tick;
    boolean hasSnapped = false;
    long snapStartTime = 0;
    long lastAttackTime = 0;
    float targetYaw = 0;
    float targetPitch = 0;
    boolean isJittering = false;
    long jitterStartTime = 0;
    float frozenYaw = 0;
    float frozenPitch = 0;
    float lastYawJitter = 0;
    float lastPitchJitter = 0;
    long lastJitterUpdate = 0;

    public void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();

        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        double distanceY = Math.abs(dy);

        long currentTime = System.currentTimeMillis();

        if (!AuraNew.getInstance().isLookingUp && currentTime - AuraNew.getInstance().lastLookUpTime >= AuraNew.getInstance().nextLookUpDelay) {
            AuraNew.getInstance().isLookingUp = true;
            AuraNew.getInstance().lookUpStartTime = currentTime;
            AuraNew.getInstance().lookUpDuration = ThreadLocalRandom.current().nextInt(301, 400);
            AuraNew.getInstance().lastLookUpTime = currentTime;
            AuraNew.getInstance().nextLookUpDelay = ThreadLocalRandom.current().nextLong(9000, 18000);
        }

        if (AuraNew.getInstance().isLookingUp && currentTime - AuraNew.getInstance().lookUpStartTime >= AuraNew.getInstance().lookUpDuration) {
            AuraNew.getInstance().isLookingUp = false;
        }

        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        float oscillY = (float) Math.cos(currentTime / Mathf.randomValue(420D, 480D));
        float offsetY = Mathf.randomValue(0.05F, 0.07F) * oscillY;

        float oscillZ = (float) Math.cos(currentTime / Mathf.randomValue(470D, 530D));
        float offsetZ = Mathf.randomValue(0.05F, 0.07F) * oscillZ;

        float oscillX = (float) Math.cos(currentTime / Mathf.randomValue(13000D, 15000D));
        float offsetX = Mathf.randomValue(0.4F, 0.6F) * oscillX;

        Vector3d directionVec = target.getPositionVec()
                .add(offsetZ, MathHelper.clamp(playerEyePos.y - target.getPosY(), 0.0F, 1.5F) + offsetX, offsetY)
                .subtract(playerEyePos).normalize();

        float baseYaw = FreeLookComponent.getFreeYaw();

        long timeSinceAttack = currentTime - lastAttackTime;

        if (isAttack && AuraUtil.getStrictDistance(target) < attackDistance && !check && !hasSnapped && !isJittering) {
            hasSnapped = true;
            snapStartTime = currentTime;
            calculateSnapTarget(target, playerEyePos, offsetX);
        }

        float fov = (float) AuraUtil.calculateFOVFromCamera(target);
        float baseFov = 360;
        float yawChangeSpeed = Mathf.randomValue(6, 12);
        float randomAttackShift = 0;
        float pitchChangeSpeed = Mathf.randomValue(0, 3);
        float waveA = (float) Math.cos(currentTime / Mathf.randomValue(40D, 50D));
        float waveB = (float) Math.sin(currentTime / Mathf.randomValue(65D, 75D));

        if (hasSnapped) {
            long snapDuration = ThreadLocalRandom.current().nextLong(90, 120);
            long timeSinceSnap = currentTime - snapStartTime;

            if (timeSinceSnap < snapDuration) {
                performSnap(currentTime, playerEyePos, target);
                return;
            } else {
                hasSnapped = false;
                isJittering = true;
                jitterStartTime = currentTime;
                frozenYaw = mc.player.rotationYaw;
                frozenPitch = mc.player.rotationPitch;
                lastAttackTime = currentTime;
                tick = 0;
            }
        }

        if (isJittering) {
            long jitterDuration = ThreadLocalRandom.current().nextLong(120, 180);
            long jitterTime = currentTime - jitterStartTime;

            if (jitterTime < jitterDuration) {
                performJitter(currentTime);
                return;
            } else {
                isJittering = false;
            }
        }

        if (tick > 0 && Math.abs(fov) < baseFov) {
            directionVec = target.getPositionVec()
                    .add(Mathf.randomValue(-0.06F, 0.06F), MathHelper.clamp(playerEyePos.y - target.getPosY(), 0.0F, 1) + offsetX / 2, Mathf.randomValue(-0.04F, 0.04F))
                    .subtract(playerEyePos).normalize();

            yawChangeSpeed = Mathf.randomValue(24, 30);
            baseYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));

            pitchChangeSpeed = Mathf.randomValue(1, 3);
            waveA = (float) Math.cos(currentTime / Mathf.randomValue(75D, 85D));
            waveB = (float) Math.sin(currentTime / Mathf.randomValue(95D, 105D));
            randomAttackShift = ThreadLocalRandom.current().nextLong(-4, 4);
            tick--;
        }

        if (timeSinceAttack > 600) {
            performIdle(currentTime);
            return;
        }

        float basePitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))),
                AuraNew.MIN_PITCH, AuraNew.MAX_PITCH);

        float yawJitter = waveA * AuraNew.getInstance().randomLerp(Mathf.randomValue(16, 20), Mathf.randomValue(20, 24)) + randomAttackShift;
        float pitchJitter = waveB * AuraNew.getInstance().randomLerp(Mathf.randomValue(16, 20), Mathf.randomValue(20, 24)) + randomAttackShift;

        float finalPitch = AuraNew.getInstance().isLookingUp ? Mathf.randomValue(-75, -66) : basePitch + pitchJitter;

        Rotation newRotation = new Rotation(baseYaw + yawJitter, finalPitch);

        float finalYawSpeed = yawChangeSpeed + Mathf.randomValue(-2, 2);
        float finalPitchSpeed = AuraNew.getInstance().isLookingUp ? 360 : pitchChangeSpeed + Mathf.randomValue(-1, 1);

        RotationComponent.update(newRotation, finalYawSpeed, finalPitchSpeed,
                ThreadLocalRandom.current().nextInt(5, 8),
                ThreadLocalRandom.current().nextInt(4, 7), 0, 15, false);
    }

    private void calculateSnapTarget(LivingEntity target, Vector3d playerEyePos, float offsetX) {
        float heightVariation = Mathf.randomValue(0.45f, 0.55f);
        float targetHeight = target.getHeight() * heightVariation;

        float horizontalSpread = target.getWidth() * Mathf.randomValue(0.2f, 0.35f);
        float randomOffsetX = Mathf.randomValue(-horizontalSpread, horizontalSpread);
        float randomOffsetZ = Mathf.randomValue(-horizontalSpread, horizontalSpread);
        float verticalOffset = Mathf.randomValue(-0.08f, 0.08f);

        Vector3d aimPoint = target.getPositionVec().add(randomOffsetX, targetHeight + verticalOffset, randomOffsetZ);

        Vector3d targetVelocity = target.getMotion();
        float predictionFactor = Mathf.randomValue(0.15f, 0.25f);
        aimPoint = aimPoint.add(targetVelocity.scale(predictionFactor));

        Vector3d directionVec = aimPoint.subtract(playerEyePos).normalize();

        targetYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        targetPitch = (float) -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z)));
        targetPitch = MathHelper.clamp(targetPitch, AuraNew.MIN_PITCH, AuraNew.MAX_PITCH);

        targetYaw += Mathf.randomValue(-8f, 8f);
        targetPitch += Mathf.randomValue(-3f, 3f);
    }

    private void performSnap(long currentTime, Vector3d playerEyePos, LivingEntity target) {
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));
        float pitchDelta = Math.abs(targetPitch - currentPitch);
        float totalDelta = yawDelta + pitchDelta;

        float baseSpeed = Mathf.randomValue(170f, 190f);
        float speedMultiplier = 1.0f;

        if (totalDelta > 90f) {
            speedMultiplier = Mathf.randomValue(2.3f, 2.7f);
        } else if (totalDelta > 60f) {
            speedMultiplier = Mathf.randomValue(1.9f, 2.2f);
        } else if (totalDelta > 30f) {
            speedMultiplier = Mathf.randomValue(1.4f, 1.7f);
        } else if (totalDelta > 15f) {
            speedMultiplier = Mathf.randomValue(1.1f, 1.4f);
        }

        float distance = (float) AuraUtil.getStrictDistance(target);
        float distanceFactor = MathHelper.clamp(distance / 3.5f, 0.8f, 1.5f);

        float yawSpeed = baseSpeed * speedMultiplier * distanceFactor * Mathf.randomValue(0.95f, 1.05f);
        float pitchSpeed = baseSpeed * speedMultiplier * distanceFactor * 0.85f * Mathf.randomValue(0.95f, 1.05f);

        yawSpeed = MathHelper.clamp(yawSpeed, 170f, 460f);
        pitchSpeed = MathHelper.clamp(pitchSpeed, 140f, 390f);

        float microYaw = (float) Math.sin(currentTime / Mathf.randomValue(13D, 17D)) * Mathf.randomValue(0.8f, 1.5f);
        float microPitch = (float) Math.cos(currentTime / Mathf.randomValue(18D, 22D)) * Mathf.randomValue(0.5f, 1.2f);

        Rotation snapRotation = new Rotation(targetYaw + microYaw, targetPitch + microPitch);
        RotationComponent.update(snapRotation, yawSpeed, pitchSpeed,
                ThreadLocalRandom.current().nextInt(0, 2),
                ThreadLocalRandom.current().nextInt(0, 2), 0, 15, false);
    }

    private void performJitter(long currentTime) {
        if (currentTime - lastJitterUpdate > ThreadLocalRandom.current().nextLong(20, 35)) {
            float jitterIntensity = Mathf.randomValue(0.15f, 0.25f);
            lastYawJitter = Mathf.randomValue(-15f, 15f) * jitterIntensity;
            lastPitchJitter = Mathf.randomValue(-5f, 5f) * jitterIntensity;
            lastJitterUpdate = currentTime;
        }

        float waveYaw = (float) Math.sin(currentTime / Mathf.randomValue(50D, 70D)) * Mathf.randomValue(0.3f, 0.8f);
        float wavePitch = (float) Math.cos(currentTime / Mathf.randomValue(60D, 80D)) * Mathf.randomValue(0.2f, 0.5f);

        float finalYaw = frozenYaw + lastYawJitter + waveYaw;
        float finalPitch = MathHelper.clamp(frozenPitch + lastPitchJitter + wavePitch, -90f, 90f);

        Rotation jitterRotation = new Rotation(finalYaw, finalPitch);

        float jitterSpeed = Mathf.randomValue(20f, 30f);
        float jitterPitchSpeed = Mathf.randomValue(10f, 15f);

        RotationComponent.update(jitterRotation, jitterSpeed, jitterPitchSpeed,
                ThreadLocalRandom.current().nextInt(5, 8),
                ThreadLocalRandom.current().nextInt(4, 7), 0, 15, false);
    }

    private void performIdle(long currentTime) {
        float idleYaw = mc.player.rotationYaw + Mathf.randomValue(-3.5f, 3.5f);
        float idlePitch = MathHelper.clamp(mc.player.rotationPitch + Mathf.randomValue(-1.8f, 1.8f), -90f, 90f);

        float breathWave = (float) Math.sin(currentTime / Mathf.randomValue(800D, 1200D)) * Mathf.randomValue(0.4f, 0.7f);
        idlePitch += breathWave;

        Rotation idleRotation = new Rotation(idleYaw, idlePitch);

        float idleSpeed = Mathf.randomValue(12f, 18f);
        float idlePitchSpeed = Mathf.randomValue(6f, 10f);

        RotationComponent.update(idleRotation, idleSpeed, idlePitchSpeed,
                ThreadLocalRandom.current().nextInt(6, 9),
                ThreadLocalRandom.current().nextInt(5, 8), 0, 15, false);
    }
}