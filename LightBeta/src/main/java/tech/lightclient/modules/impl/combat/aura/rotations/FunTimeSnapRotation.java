package tech.lightclient.modules.impl.combat.aura.rotations;

import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.security.SecureRandom;

public final class FunTimeSnapRotation extends RotateModel {
    private static final float MAX_IDLE_FOV = 35.0f;
    private static final float DIAGONAL_JITTER_DEGREES = 10.0f;
    private static final float RUNNING_TARGET_BLEND = 0.42f;
    private static final float RUNNING_TARGET_ATTACK_BLEND = 0.58f;
    private static final long SIDE_SNAP_DURATION_MS = 350L;
    private static final long SIDE_SNAP_TURN_MS = 140L;
    private static final int SIDE_SNAP_EVERY_TICKS = 50;
    private static final int ATTACK_SKIP_EVERY_TICKS = 6;
    private static final float SIDE_SNAP_ANGLE = 80.0f;

    private final SecureRandom secureRandom = new SecureRandom();
    private static long sideSnapUntil;
    private static int lastTick = -1;
    private static int sideSnapStage;
    private static int tickCounter;
    private static long sideSnapStartedAt;
    private static float sideSnapStartYaw;
    private static float sideSnapTargetYaw;
    private static float sideSnapPitch;
    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (mc.player == null || currentRotation == null) {
            return currentRotation;
        }

        AuraModule aura = AuraModule.getInstance();
        long now = System.currentTimeMillis();
        boolean aiming = target != null && target.isAlive();

        //тики тут прибавляются
        if (mc.player.age != lastTick) {
            lastTick = mc.player.age;
            tickCounter++;
        }

        if (tickCounter >= SIDE_SNAP_EVERY_TICKS && sideSnapUntil == 0L) {
            sideSnapUntil = now + SIDE_SNAP_DURATION_MS;
            tickCounter = 0;
            sideSnapStage = 0;

            sideSnapStartedAt = now;
            sideSnapStartYaw = currentRotation.getYaw();
            float targetYaw = aiming
                    ? calcAim(getMovingTargetPoint(target)).getYaw()
                    : mc.player.getYaw();
            float targetDelta = MathHelper.wrapDegrees(targetYaw - currentRotation.getYaw());
            float direction = targetDelta >= 0.0f ? -1.0f : 1.0f;
            sideSnapTargetYaw = sideSnapStartYaw + direction * SIDE_SNAP_ANGLE;
            sideSnapPitch = currentRotation.getPitch();
        }

        if (sideSnapUntil != 0L) {
            if (now < sideSnapUntil) {
                long elapsed = now - (sideSnapUntil - SIDE_SNAP_DURATION_MS);
                if (sideSnapStage == 0 && elapsed >= 50L) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    sideSnapStage = 1;
                } else if (sideSnapStage == 1 && elapsed >= 180L) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    sideSnapStage = 2;
                }

                float progress = MathHelper.clamp(
                        (float) (now - sideSnapStartedAt) / SIDE_SNAP_TURN_MS,
                        0.0f,
                        1.0f
                );
                // плавность
                progress = progress * progress * (3.0f - 2.0f * progress);
                float yawDelta = MathHelper.wrapDegrees(sideSnapTargetYaw - sideSnapStartYaw);
                return new RotateVector(
                        applyGCD(sideSnapStartYaw + yawDelta * progress),
                        applyGCD(MathHelper.clamp(sideSnapPitch, -90.0f, 90.0f))
                );
            }
            sideSnapUntil = 0L;
        }

        if (aiming) {
            this.target = target;

            Vec3d movingPoint = getMovingTargetPoint(target);
            RotateVector targetRotation = calcAim(movingPoint);
            float yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
            float pitchDelta = targetRotation.getPitch() - currentRotation.getPitch();

            boolean attackReady = aura != null && aura.canAttackTargetNow()
                    && AuraModule.getAttackCooldown() >= 0.85f;
            double horizontalSpeed = Math.hypot(target.getVelocity().x, target.getVelocity().z);
            boolean runningTarget = horizontalSpeed > 0.03D;

            float blend = runningTarget
                    ? (attackReady ? RUNNING_TARGET_ATTACK_BLEND : RUNNING_TARGET_BLEND)
                    : (attackReady ? 1.0f : 0.90f);
            float yaw = currentRotation.getYaw() + yawDelta * blend;
            float pitch = currentRotation.getPitch() + pitchDelta * blend;

            float diagonal = (float) Math.sin(now / 260.0D) * DIAGONAL_JITTER_DEGREES;
            return new RotateVector(
                    applyGCD(yaw + diagonal),
                    applyGCD(MathHelper.clamp(pitch - diagonal, -90.0f, 90.0f))
            );
        }

        long sinceAttack = aura == null ? Long.MAX_VALUE : now - aura.lastAttackTime;
        float yawNoise = sinceAttack <= 650L ? randomFloat(-2.0f, 2.0f) : randomFloat(-1.0f, 1.0f);
        float pitchNoise = sinceAttack <= 650L ? randomFloat(-1.0f, 1.0f) : randomFloat(-0.5f, 0.5f);
        RotateVector limited = clampToViewFov(
                mc.player.getYaw() + yawNoise,
                mc.player.getPitch() + pitchNoise
        );
        return new RotateVector(applyGCD(limited.getYaw()), applyGCD(limited.getPitch()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sideSnapUntil = 0L;
        lastTick = mc.player != null ? mc.player.age : -1;
        tickCounter = 0;
        sideSnapStage = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        sideSnapUntil = 0L;
        tickCounter = 0;
        sideSnapStage = 0;
        sideSnapStartedAt = 0L;
    }

    public static boolean isAirSwingTick() {
        AuraModule aura = AuraModule.getInstance();
        return aura != null
                && "FunTime Snap".equals(aura.getRotationModeLabel())
                && AuraModule.target != null
                && AuraModule.target.isAlive()
                && MinecraftTick.isFourthTick();
    }

    public static boolean isSideSnapActive() {
        return sideSnapUntil != 0L && System.currentTimeMillis() < sideSnapUntil;
    }

    private float randomFloat(float min, float max) {
        return MathHelper.lerp(secureRandom.nextFloat(), min, max);
    }

    private Vec3d getMovingTargetPoint(LivingEntity target) {
        Vec3d center = target.getBoundingBox().getCenter();
        Vec3d velocity = target.getVelocity();
        double horizontalSpeed = Math.hypot(velocity.x, velocity.z);
        if (horizontalSpeed < 0.01D) {
            return center;
        }

        return center.add(velocity.x * 0.85D, velocity.y * 0.35D, velocity.z * 0.85D);
    }

    private RotateVector clampToViewFov(float yaw, float pitch) {
        AuraModule aura = AuraModule.getInstance();
        float configuredFov = aura == null ? MAX_IDLE_FOV : aura.getFunTimeFov();
        float maxFov = MathHelper.clamp(configuredFov, 0.0f, MAX_IDLE_FOV);
        float yawOffset = MathHelper.wrapDegrees(yaw - mc.player.getYaw());
        float pitchOffset = pitch - mc.player.getPitch();
        float distance = (float) Math.hypot(yawOffset, pitchOffset);
        if (distance <= maxFov || distance < 0.001f) {
            return new RotateVector(yaw, pitch);
        }

        float scale = maxFov / distance;
        return new RotateVector(
                mc.player.getYaw() + yawOffset * scale,
                mc.player.getPitch() + pitchOffset * scale
        );
    }

    private static final class MinecraftTick {
        private static boolean isFourthTick() {
            var player = net.minecraft.client.MinecraftClient.getInstance().player;
            return player != null && player.age > 0 && player.age % ATTACK_SKIP_EVERY_TICKS == 0;
        }
    }
}
