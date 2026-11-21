package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;

import java.security.SecureRandom;
import java.util.Random;

public final class FuntimeNewRotation extends RotateModel {
    private static final long LOOK_UP_DELAY_MIN_MS = 90_000L;
    private static final long LOOK_UP_DELAY_MAX_MS = 180_000L;
    private static final long LOOK_UP_DURATION_MIN_MS = 220L;
    private static final long LOOK_UP_DURATION_MAX_MS = 400L;
    private static final float ATTACK_HOLD_TICKS = 4.0f;

    private final Random random = new SecureRandom();
    private float attackTicks;
    private float lastYaw;
    private float lastPitch;
    private long nextLookUpAt;
    private long lookUpUntil;
    private boolean rotating;
    private int idleTicks;
    private int timeout;
    private float yawReturnSpeed;
    private float pitchReturnSpeed;

    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (mc.player == null || currentRotation == null) {
            return currentRotation;
        }

        boolean hasTarget = target != null && target.isAlive();
        if (!hasTarget) {
            return processIdle(currentRotation, new RotateVector(mc.player.getYaw(), mc.player.getPitch()));
        }

        this.target = target;
        RotateVector targetRotation = targetRotation(target);
        long now = System.currentTimeMillis();
        updateLookUp(now);

        AuraModule aura = AuraModule.getInstance();
        boolean attackReady = aura != null
                && aura.canAttackNow()
                && aura.canAttackTargetNow();
        if (attackReady) {
            attackTicks = ATTACK_HOLD_TICKS;
        }

        boolean attackPhase = attackTicks > 0.0f;
        if (attackPhase) {
            attackTicks -= 1.0f;
            lastYaw = targetRotation.getYaw();
            lastPitch = targetRotation.getPitch();
        } else if (!rotating) {
            lastYaw = targetRotation.getYaw();
            lastPitch = targetRotation.getPitch();
        }

        float yawOscillation = (float) Math.cos(now / 115.0D) * randomBetween(1.05f, 2.15f);
        float pitchOscillation = (float) Math.sin(now / 135.0D) * randomBetween(0.45f, 1.10f);
        float desiredPitch = now < lookUpUntil
                ? -randomBetween(54.0f, 63.0f)
                : lastPitch;
        RotateVector desired = new RotateVector(lastYaw + yawOscillation, desiredPitch + pitchOscillation);

        float yawSpeed = attackPhase
                ? randomBetween(38.0f, 48.0f)
                : randomBetween(9.0f, 16.0f);
        float pitchSpeed = attackPhase
                ? randomBetween(22.0f, 31.0f)
                : randomBetween(6.0f, 11.0f);

        yawReturnSpeed = randomBetween(29.0f, 35.0f);
        pitchReturnSpeed = randomBetween(17.0f, 28.0f);
        timeout = randomInt(0, 1);
        idleTicks = 0;
        rotating = true;

        return approach(currentRotation, desired, yawSpeed, pitchSpeed);
    }

    private RotateVector targetRotation(LivingEntity entity) {
        Vec3d eyePos = mc.player.getEyePos();
        Box box = entity.getBoundingBox();
        Vec3d nearest = new Vec3d(
                MathHelper.clamp(eyePos.x, box.minX, box.maxX),
                MathHelper.clamp(eyePos.y, box.minY, box.maxY),
                MathHelper.clamp(eyePos.z, box.minZ, box.maxZ)
        );
        Vec3d delta = nearest.subtract(eyePos);
        if (delta.lengthSquared() < 1.0E-6D) {
            delta = box.getCenter().subtract(eyePos);
        }

        double horizontal = Math.hypot(delta.x, delta.z);
        float yaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizontal));
        return new RotateVector(yaw, MathHelper.clamp(pitch, -90.0f, 90.0f));
    }

    private RotateVector processIdle(RotateVector current, RotateVector cameraRotation) {
        if (!rotating) {
            return cameraRotation;
        }

        if (idleTicks > timeout) {
            RotateVector returned = approach(current, cameraRotation, yawReturnSpeed, pitchReturnSpeed);
            float yawDelta = Math.abs(MathHelper.wrapDegrees(cameraRotation.getYaw() - returned.getYaw()));
            float pitchDelta = Math.abs(cameraRotation.getPitch() - returned.getPitch());
            if (Math.hypot(yawDelta, pitchDelta) < 1.0f) {
                rotating = false;
                return cameraRotation;
            }
            return returned;
        }

        idleTicks++;
        return current;
    }

    private RotateVector approach(RotateVector current, RotateVector desired, float yawSpeed, float pitchSpeed) {
        float yawDelta = MathHelper.wrapDegrees(desired.getYaw() - current.getYaw());
        float pitchDelta = desired.getPitch() - current.getPitch();
        float yawStep = MathHelper.clamp(yawDelta, -Math.abs(yawSpeed), Math.abs(yawSpeed));
        float pitchStep = MathHelper.clamp(pitchDelta, -Math.abs(pitchSpeed), Math.abs(pitchSpeed));

        yawStep = clampToDelta(applyGCD(yawStep), yawDelta);
        pitchStep = clampToDelta(applyGCD(pitchStep), pitchDelta);
        return new RotateVector(
                current.getYaw() + yawStep,
                MathHelper.clamp(current.getPitch() + pitchStep, -90.0f, 90.0f)
        );
    }

    private void updateLookUp(long now) {
        if (nextLookUpAt == 0L) {
            nextLookUpAt = now + randomLong(LOOK_UP_DELAY_MIN_MS, LOOK_UP_DELAY_MAX_MS);
        }
        if (now >= nextLookUpAt) {
            lookUpUntil = now + randomLong(LOOK_UP_DURATION_MIN_MS, LOOK_UP_DURATION_MAX_MS);
            nextLookUpAt = now + randomLong(LOOK_UP_DELAY_MIN_MS, LOOK_UP_DELAY_MAX_MS);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetState();
    }

    private void resetState() {
        attackTicks = 0.0f;
        lastYaw = 0.0f;
        lastPitch = 0.0f;
        nextLookUpAt = 0L;
        lookUpUntil = 0L;
        rotating = false;
        idleTicks = 0;
        timeout = 0;
        yawReturnSpeed = 0.0f;
        pitchReturnSpeed = 0.0f;
    }

    private float randomBetween(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private long randomLong(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }

    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static float clampToDelta(float step, float delta) {
        if (Math.abs(delta) < 0.001f || Math.signum(step) != Math.signum(delta)) {
            return 0.0f;
        }
        return Math.abs(step) > Math.abs(delta) ? delta : step;
    }
}
