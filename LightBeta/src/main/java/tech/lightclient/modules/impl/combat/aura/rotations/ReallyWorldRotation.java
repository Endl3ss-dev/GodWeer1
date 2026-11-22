package tech.lightclient.modules.impl.combat.aura.rotations;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.client.utils.player.rotation.SensUtility;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.aura.elytra.AuraElytraUtils;
import tech.lightclient.modules.impl.player.ElytraFunctional;
import tech.lightclient.modules.impl.player.ElytraMotion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public class ReallyWorldRotation extends RotateModel {
    private float appliedRandomYaw;
    private float appliedRandomPitch;

    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (mc.player == null || target == null) {
            return currentRotation;
        }
        long currentTime = System.currentTimeMillis();

        this.target = target;

        /*
        Vec3d targetPoint = calculateTargetPoint(target);
        RotateVector targetRotation = calcAim(targetPoint);

        float yaw = targetRotation.getYaw();
        float pitch = targetRotation.getPitch();

        float inferredBaseYaw = currentRotation.getYaw() - appliedRandomYaw;
        float inferredBasePitch = currentRotation.getPitch() - appliedRandomPitch;

        float nextBaseYaw = yaw;
        float nextBasePitch = pitch;

        updateRandomOffsets();

        yaw = nextBaseYaw + appliedRandomYaw;
        pitch = nextBasePitch + appliedRandomPitch;

        yaw = applyGCD(yaw);
        pitch = applyGCD(pitch);

        return new RotateVector(yaw, pitch);
        
         */


        var groundLerpAnimation = animation("lerpReallyWorld", Easings.SMOOTH_STEP, 300);
        var targetPos = target.getEntityPos();

        Vec3d hvec;

        if (target.isGliding()) {
            hvec = targetPos.add(
                    0,
                    0.5f,
                    0);
        } else {
            hvec = targetPos.add(
                    0,
                    1.2f,
                    0);
        }


        double motionX = target.getX() - target.lastX;
        double motionZ = target.getZ() - target.lastZ;

        double velocitySpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);


        if (AuraElytraUtils.shouldMovePredict(target) && AuraModule.getInstance().shouldPredict && velocitySpeed > 0.01f) {
            var predictValue = ElytraFunctional.predictValue.get();

            Vec3d hvhVelocity = AuraElytraUtils.calcGlidingVelocityPredicted(target, target.getVelocity(), predictValue);
            hvec = hvec.add(hvhVelocity);
        }

        Vec3d targetPoint = hvec;
        RotateVector targetRotation = calcAim(targetPoint);

        float yaw;
        float pitch;

        long timeSinceAttack = System.currentTimeMillis() - AuraModule.getInstance().lastAttackTime;

        if (!mc.player.isGliding()) {
            if (AuraModule.getNormalizedAttackCooldown() < 0.7f) {
                groundLerpAnimation.hide();
            } else {
                groundLerpAnimation.show();
            }

            float speed = groundLerpAnimation.getProgress();

            float targetYaw = targetRotation.getYaw();
            float targetPitch = targetRotation.getPitch();

            yaw = MathUtil.lerpAngle(currentRotation.getYaw(), targetYaw, speed);
            pitch = MathUtil.lerpAngle(currentRotation.getPitch(), targetPitch, speed);

            yaw += ThreadLocalRandom.current().nextFloat(-0.5f, 0.5f);
            pitch += ThreadLocalRandom.current().nextFloat(-0.25f, 0.25f);

        } else {
            groundLerpAnimation.hide();

            Vec3d glidingVelocity = AuraElytraUtils.calcGlidingVelocityPredicted(target, target.getVelocity(), 0.15f);

            Vec3d predictedTargetPos = targetPos.add(glidingVelocity);
            double distanceToPredictedPos = mc.player.getEntityPos().distanceTo(predictedTargetPos);

            boolean elytraMotionPass = LightClient.getFunctionManager().getElytraMotion().isState() && distanceToPredictedPos <= (double) ElytraMotion.elytraMotionDistance.get();

            if (ElytraFunctional.elytraAntiAim.get() && !elytraMotionPass) {
                if (ElytraFunctional.smartAntiAim.get()) {
                    if (!AuraElytraUtils.isLeaving(target)
                            && !(timeSinceAttack > ElytraFunctional.elytraAntiAimDuration.get())) {
                        yaw = MathUtil.lerpAngle(currentRotation.getYaw(),
                                targetRotation.getYaw(), 0);
                        pitch = ElytraFunctional.elytraAntiAimPitch.get();
                    } else {
                        yaw = targetRotation.getYaw();
                        pitch = MathHelper.clamp(targetRotation.getPitch(), -90.0F, 90.0F);
                    }
                } else {
                    if (!(timeSinceAttack > ElytraFunctional.elytraAntiAimDuration.get())) {
                        yaw = MathUtil.lerpAngle(currentRotation.getYaw(),
                                targetRotation.getYaw(), 0);
                        pitch = ElytraFunctional.elytraAntiAimPitch.get();
                    } else {
                        yaw = targetRotation.getYaw();
                        pitch = MathHelper.clamp(targetRotation.getPitch(), -90.0F, 90.0F);
                    }
                }
            } else {
                yaw = targetRotation.getYaw();
                pitch = MathHelper.clamp(targetRotation.getPitch(), -90.0F, 90.0F);
            }
        }

        float gcd = SensUtility.getGCDValue();
        yaw -= (yaw - currentRotation.getYaw()) % gcd;
        pitch -= (pitch - currentRotation.getPitch()) % gcd;

        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        currentRotation = new RotateVector(yaw, pitch);

        return currentRotation;
    }

    private void resetRandomOffsets() {
        appliedRandomYaw = 0.0f;
        appliedRandomPitch = 0.0f;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetRandomOffsets();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetRandomOffsets();
    }
}
