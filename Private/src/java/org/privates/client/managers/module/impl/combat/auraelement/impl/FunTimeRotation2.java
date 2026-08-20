package org.privates.client.managers.module.impl.combat.auraelement.impl;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class FunTimeRotation2 implements IMinecraft {

    private enum RotationState {
        IDLE,
        PRE_ATTACK,
        POST_ATTACK,
        WITHDRAWING
    }

    private RotationState currentState = RotationState.IDLE;

    private float idleYawOffset = 0;
    private int offsetDirection = 1;
    private float currentYawOffset = 0;

    private static final long PRE_ATTACK_TIME = 75;
    private static final long POST_ATTACK_TIME = 75;

    private long stateStartTime = 0;
    private long lastAttackTime = 0;
    private boolean wasAttacking = false;

    private float aimOffsetX = 0;
    private float aimOffsetY = 0.9F;
    private float aimOffsetZ = 0;

    private boolean isLookingUp = false;
    private long lastLookUpTime = System.currentTimeMillis();
    private long nextLookUpDelay = ThreadLocalRandom.current().nextLong(10000, 20000);
    private long lookUpStartTime = 0;
    private long lookUpTotalDuration = 0;
    private float lookUpTargetPitch = -87F;

    private float currentShakeYaw = 0;
    private boolean shakeDirectionRight = true;
    private long lastShakeUpdate = 0;

    public void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        long currentTime = System.currentTimeMillis();
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        float attackCooldown = mc.player.getCooledAttackStrength(0);
        boolean cooldownAlmostReady = attackCooldown >= 0.85F;
        boolean inRange = AuraUtil.getStrictDistance(target) <= attackDistance;

        updateState(currentTime, isAttack, cooldownAlmostReady, inRange, target);

        handleLookUp(currentTime);
        handleShake(currentTime);

        Vector3d aimPoint = target.getPositionVec().add(aimOffsetX, aimOffsetY, aimOffsetZ);
        Vector3d direction = aimPoint.subtract(playerEyePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float targetPitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(direction.y, Math.hypot(direction.x, direction.z))),
                -90, 90);

        float desiredOffset = calculateDesiredOffset(currentTime);

        float offsetSpeed = getOffsetSpeed();
        currentYawOffset = currentYawOffset + (desiredOffset - currentYawOffset) * offsetSpeed;

        float finalYaw = targetYaw + currentYawOffset + currentShakeYaw;
        float finalPitch = calculateFinalPitch(currentTime, targetPitch);

        float yawSpeed = getYawSpeed();
        float pitchSpeed = getPitchSpeed();

        Rotation rotation = new Rotation(finalYaw, finalPitch);
        RotationComponent.update(rotation, yawSpeed, pitchSpeed, 6, 5, 0, 15, false);
    }

    private void updateState(long currentTime, boolean isAttack, boolean cooldownAlmostReady, boolean inRange, LivingEntity target) {

        switch (currentState) {

            case IDLE:
                if (idleYawOffset == 0) {
                    chooseOffsetDirection(target);
                }

                if (cooldownAlmostReady && inRange) {
                    switchState(RotationState.PRE_ATTACK, currentTime);
                }
                break;

            case PRE_ATTACK:
                if (currentTime - stateStartTime >= PRE_ATTACK_TIME) {
                    if (isAttack && !wasAttacking) {
                        wasAttacking = true;
                        lastAttackTime = currentTime;
                        generateNewAimPoint();
                        switchState(RotationState.POST_ATTACK, currentTime);
                    }
                }

                if (!cooldownAlmostReady && currentTime - stateStartTime > 200) {
                    switchState(RotationState.IDLE, currentTime);
                }
                break;

            case POST_ATTACK:
                if (currentTime - stateStartTime >= POST_ATTACK_TIME) {
                    wasAttacking = false;
                    switchState(RotationState.WITHDRAWING, currentTime);
                }
                break;

            case WITHDRAWING:
                if (Math.abs(currentYawOffset - idleYawOffset) < 3.0F) {
                    chooseOffsetDirection(target);
                    switchState(RotationState.IDLE, currentTime);
                }
                break;
        }
    }

    private void switchState(RotationState newState, long currentTime) {
        currentState = newState;
        stateStartTime = currentTime;
    }

    private void chooseOffsetDirection(LivingEntity target) {
        float currentYaw = mc.player.rotationYaw;

        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d targetPos = target.getPositionVec().add(0, 0.9, 0);
        Vector3d direction = targetPos.subtract(playerEyePos).normalize();
        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));

        float diff = MathHelper.wrapDegrees(currentYaw - targetYaw);

        if (diff > 0) {
            offsetDirection = 1;
        } else {
            offsetDirection = -1;
        }

        idleYawOffset = Mathf.randomValue(30, 35) * offsetDirection;
    }

    private float calculateDesiredOffset(long currentTime) {
        switch (currentState) {
            case IDLE:
                return idleYawOffset;

            case PRE_ATTACK:
                long elapsed = currentTime - stateStartTime;
                float progress = Math.min(1.0F, (float) elapsed / PRE_ATTACK_TIME);
                return idleYawOffset * (1.0F - smoothStep(progress));

            case POST_ATTACK:
                return 0;

            case WITHDRAWING:
                return idleYawOffset;

            default:
                return 0;
        }
    }

    private float getOffsetSpeed() {
        switch (currentState) {
            case PRE_ATTACK:
                return 0.18F;
            case WITHDRAWING:
                return 0.05F;
            default:
                return 0.08F;
        }
    }

    private float getYawSpeed() {
        if (isLookingUp) {
            return Mathf.randomValue(28, 45);
        }

        switch (currentState) {
            case PRE_ATTACK:
                return Mathf.randomValue(40, 60);
            case POST_ATTACK:
                return Mathf.randomValue(25, 35);
            case WITHDRAWING:
                return Mathf.randomValue(10, 16);
            default:
                return Mathf.randomValue(6, 12);
        }
    }

    private float getPitchSpeed() {
        if (isLookingUp) {
            return Mathf.randomValue(35, 55);
        }

        switch (currentState) {
            case PRE_ATTACK:
                return Mathf.randomValue(30, 45);
            case POST_ATTACK:
                return Mathf.randomValue(18, 28);
            case WITHDRAWING:
                return Mathf.randomValue(8, 14);
            default:
                return Mathf.randomValue(5, 10);
        }
    }

    private void generateNewAimPoint() {
        aimOffsetX = Mathf.randomValue(-0.25F, 0.25F);
        aimOffsetY = Mathf.randomValue(0.3F, 1.5F);
        aimOffsetZ = Mathf.randomValue(-0.25F, 0.25F);
    }

    private void handleLookUp(long currentTime) {
        if (!isLookingUp && currentState == RotationState.IDLE &&
                currentTime - lastLookUpTime >= nextLookUpDelay) {

            isLookingUp = true;
            lookUpStartTime = currentTime;
            lookUpTotalDuration = ThreadLocalRandom.current().nextLong(1200, 2000);
            lookUpTargetPitch = Mathf.randomValue(-90, -85);
            nextLookUpDelay = ThreadLocalRandom.current().nextLong(10000, 20000);
        }

        if (isLookingUp && currentTime - lookUpStartTime >= lookUpTotalDuration) {
            isLookingUp = false;
            lastLookUpTime = currentTime;
        }
    }

    private void handleShake(long currentTime) {
        if (isLookingUp) {
            if (currentTime - lastShakeUpdate > ThreadLocalRandom.current().nextLong(35, 70)) {
                float shakeMax = Mathf.randomValue(12, 16);
                float shakeStep = Mathf.randomValue(4, 8);

                if (shakeDirectionRight) {
                    currentShakeYaw += shakeStep;
                    if (currentShakeYaw >= shakeMax) {
                        shakeDirectionRight = false;
                    }
                } else {
                    currentShakeYaw -= shakeStep;
                    if (currentShakeYaw <= -shakeMax) {
                        shakeDirectionRight = true;
                    }
                }
                lastShakeUpdate = currentTime;
            }
        } else {
            currentShakeYaw *= 0.85F;
            if (Math.abs(currentShakeYaw) < 0.3F) {
                currentShakeYaw = 0;
            }
        }
    }

    private float calculateFinalPitch(long currentTime, float basePitch) {
        if (isLookingUp) {
            long elapsed = currentTime - lookUpStartTime;
            float halfDuration = lookUpTotalDuration / 2F;

            if (elapsed < halfDuration) {
                float progress = elapsed / halfDuration;
                return MathHelper.lerp(smoothStep(progress), basePitch, lookUpTargetPitch);
            } else {
                float progress = (elapsed - halfDuration) / halfDuration;
                return MathHelper.lerp(smoothStep(progress), lookUpTargetPitch, basePitch);
            }
        }

        return basePitch;
    }

    private float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

    public void reset() {
        currentState = RotationState.IDLE;
        idleYawOffset = 0;
        currentYawOffset = 0;
        aimOffsetX = 0;
        aimOffsetY = 0.9F;
        aimOffsetZ = 0;
        isLookingUp = false;
        currentShakeYaw = 0;
        wasAttacking = false;
        lastLookUpTime = System.currentTimeMillis();
        nextLookUpDelay = ThreadLocalRandom.current().nextLong(10000, 20000);
    }

    public RotationState getCurrentState() {
        return currentState;
    }
}