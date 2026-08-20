package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.concurrent.ThreadLocalRandom;


public class SmoothSnapRot extends AbstractRotationElement {

    private boolean isSnapping = false;
    private boolean isDoing360 = false;
    private boolean isLegacySnap = false;

    private int phaseTicksRemaining = 0;
    private int snapDuration = 0;

    private Vector2f initialRotation;
    private Vector2f targetRotation;
    private Vector2f overshotTargetRotation;

    private static final int SPIN_DURATION_TICKS = 8;
    private static final float LEGACY_YAW_STEP = 25.0f;
    private static final float LEGACY_PITCH_STEP = 12.0f;


    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            resetState();
            return currentLerpRotation;
        }

        boolean canAttack = parentModule.perfectDelay().cooldownComplete() && parentModule.cooldownComplete();

        // if (!isSnapping && canAttack && parentModule.isValidTarget(target)) {
        //     if (parentModule.onlyCrits().getValue() && !mc.player.isElytraFlying()) {
        //         boolean isInLiquid = mc.player.isActualySwimming() ||
        //                 mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) ||
        //                 mc.player.areEyesInFluid(FluidTags.LAVA);
//
        //         if (!isInLiquid) {
        //             boolean shouldCrit = parentModule.shouldCritical();
        //             boolean critModuleActive = Criticals.getInstance().isEnabled() && !mc.player.isOnGround();
        //             if (!shouldCrit && !critModuleActive) {
        //                 return currentLerpRotation;
        //             }


        isSnapping = true;
        initialRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        targetRotation = getTargetRotation(target);

        float random = ThreadLocalRandom.current().nextFloat();
        if (random < 0.20f) { // 20% шанс на 360
            isDoing360 = true;
            isLegacySnap = false;
            phaseTicksRemaining = SPIN_DURATION_TICKS;
        } else if (random < 0.50f) { // 30% шанс на легаси
            isDoing360 = false;
            isLegacySnap = true;
            snapDuration = ThreadLocalRandom.current().nextInt(3, 6);
            phaseTicksRemaining = snapDuration;
        } else { // 50% шанс на плавный снап
            isDoing360 = false;
            isLegacySnap = false;
            snapDuration = ThreadLocalRandom.current().nextInt(5, 9);
            phaseTicksRemaining = snapDuration;
            setupOvershoot();
        }


        if (isSnapping) {
            phaseTicksRemaining--;

            if (isDoing360) {
                return handle360Spin(parentModule, mc);
            } else if (isLegacySnap) {
                return handleLegacySnap(parentModule, target, mc, currentLerpRotation);
            } else {
                return handleSmoothSnap(parentModule, target, mc);
            }
        }

        return currentLerpRotation;
    }

    private Vector2f handle360Spin(AuraNew parentModule, Minecraft mc) {
        float spinProgress = 1.0f - ((float) phaseTicksRemaining / SPIN_DURATION_TICKS);
        float easedProgress = easeInOutQuint(spinProgress);
        float newYaw = initialRotation.x + (360f * easedProgress);
        float pitchOffset = (float) Math.sin(easedProgress * Math.PI) * 15.0f;
        float newPitch = initialRotation.y - pitchOffset;

        Vector2f newRotation = normalizeAngles(new Vector2f(newYaw, newPitch));
        applyRotation(new Rotation(newRotation.x, newRotation.y), 360, 180, 0, 1);
        parentModule.setLerpRotation(newRotation);

        if (phaseTicksRemaining <= 0) {
            isDoing360 = false;
            snapDuration = ThreadLocalRandom.current().nextInt(5, 9);
            phaseTicksRemaining = snapDuration;
            initialRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
            setupOvershoot();
        }
        return newRotation;
    }

    private Vector2f handleLegacySnap(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        float yawDiff = MathHelper.wrapDegrees(targetRotation.x - currentLerpRotation.x);
        float pitchDiff = targetRotation.y - currentLerpRotation.y;

        float yawStep = MathHelper.clamp(yawDiff, -LEGACY_YAW_STEP, LEGACY_YAW_STEP);
        float pitchStep = MathHelper.clamp(pitchDiff, -LEGACY_PITCH_STEP, LEGACY_PITCH_STEP);

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        Vector2f newRotation = normalizeAngles(new Vector2f(newYaw, newPitch));
        newRotation.x = GCDUtils.getSensitivity(newRotation.x);
        newRotation.y = GCDUtils.getSensitivity(newRotation.y);

        applyRotation(new Rotation(newRotation.x, newRotation.y), 360, 180, 0, 2);
        parentModule.setLerpRotation(newRotation);

        if (phaseTicksRemaining <= 0) {
            performAttack(parentModule, target, mc);
            isSnapping = false;
        }
        return newRotation;
    }

    private Vector2f handleSmoothSnap(AuraNew parentModule, LivingEntity target, Minecraft mc) {
        float snapProgress = 1.0f - ((float) phaseTicksRemaining / snapDuration);
        float easedProgress = easeInOutQuint(snapProgress);

        Vector2f currentTarget = snapProgress < 0.75f ? overshotTargetRotation : targetRotation;

        float yawDelta = MathHelper.wrapDegrees(currentTarget.x - initialRotation.x);
        float pitchDelta = currentTarget.y - initialRotation.y;

        float newYaw = initialRotation.x + yawDelta * easedProgress;
        float newPitch = initialRotation.y + pitchDelta * easedProgress;

        float jitterX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (1.0f - easedProgress) * 1.8f;
        float jitterY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (1.0f - easedProgress) * 1.2f;

        newYaw += jitterX;
        newPitch += jitterY;

        Vector2f newRotation = normalizeAngles(new Vector2f(newYaw, newPitch));
        newRotation.x = GCDUtils.getSensitivity(newRotation.x);
        newRotation.y = GCDUtils.getSensitivity(newRotation.y);

        applyRotation(new Rotation(newRotation.x, newRotation.y), 250, 120, 0, 2);
        parentModule.setLerpRotation(newRotation);

        if (phaseTicksRemaining <= 0) {
            performAttack(parentModule, target, mc);
            isSnapping = false;
        }
        return newRotation;
    }

    private void setupOvershoot() {
        float yawDelta = MathHelper.wrapDegrees(targetRotation.x - initialRotation.x);
        float pitchDelta = targetRotation.y - initialRotation.y;

        float overshootYaw = yawDelta * (1.1f + ThreadLocalRandom.current().nextFloat() * 0.1f);
        float overshootPitch = pitchDelta * (1.05f + ThreadLocalRandom.current().nextFloat() * 0.08f);

        overshotTargetRotation = normalizeAngles(new Vector2f(initialRotation.x + overshootYaw, initialRotation.y + overshootPitch));
    }

    private void performAttack(AuraNew parentModule, LivingEntity target, Minecraft mc) {
        parentModule.script().cleanup().addTickStep(0, () -> {

            parentModule.lastAttackTime = System.currentTimeMillis();
            mc.playerController.attackEntity(mc.player, target);
            mc.player.swingArm(Hand.MAIN_HAND);
            parentModule.stopWatch().reset();
            parentModule.perfectDelay().reset(350L);

        });
    }

    private Vector2f getTargetRotation(LivingEntity target) {
        Vector3d aimPoint = getAimPoint(target);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = aimPoint.subtract(playerEyes);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        return normalizeAngles(new Vector2f(yaw, pitch));
    }

    private Vector2f normalizeAngles(Vector2f angles) {
        return new Vector2f(MathHelper.wrapDegrees(angles.x), MathHelper.clamp(angles.y, -90f, 90f));
    }

    private Vector3d getAimPoint(LivingEntity target) {
        float height = target.getEyeHeight() * (0.65f + ThreadLocalRandom.current().nextFloat() * 0.3f);
        Vector3d point = target.getPositionVec().add(0, height, 0);

        float widthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.4f;
        float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.4f;
        point = point.add(widthOffset, 0, depthOffset);

        return point;
    }

    private float easeInOutQuint(float t) {
        return t < 0.5f ? 16f * t * t * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 5f) / 2f;
    }

    private void resetState() {
        isSnapping = false;
        isDoing360 = false;
        isLegacySnap = false;
        phaseTicksRemaining = 0;
        snapDuration = 0;
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