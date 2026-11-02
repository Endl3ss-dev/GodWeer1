package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;

public final class FunTimeRotation extends RotateModel {
    private static final float ATTACK_SPEED = 130.0f;
    private static final float RETURN_SPEED = 45.0f;
    private static final long PRE_AIM_MS = 100L;
    private static final long SNAP_WINDOW_MS = 250L;
    private static final long SNAP_SWING_MS = 240L;
    private static final long RETURN_DELAY_MS = 435L;
    private static final long JITTER_DURATION_MS = 500L;

    private int lastSwingAttack = -1;
    private int lastUpdateTick = Integer.MIN_VALUE;
    private RotateVector cachedRotation;

    @Override
    public RotateVector update(RotateVector currentAngle, LivingEntity target) {
        if (mc.player == null || currentAngle == null) {
            return currentAngle;
        }

        if (lastUpdateTick == mc.player.age && cachedRotation != null) {
            return cachedRotation;
        }
        lastUpdateTick = mc.player.age;

        AuraModule aura = AuraModule.getInstance();
        if (target != null && target.isAlive() && canAttack(aura)) {
            RotateVector targetAngle = calcAim(
                    target.getEntityPos().add(0.0, target.getHeight() * 0.5, 0.0)
            );
            return cachedRotation = quantize(currentAngle, limitStraight(currentAngle, targetAngle, ATTACK_SPEED));
        }

        RotateVector playerViewAngle = new RotateVector(mc.player.getYaw(), mc.player.getPitch());
        long sinceAttack = timeSinceAttack(aura);
        float yaw = (float) (randomLerp(12.0f, 24.0f)
                * Math.sin(System.currentTimeMillis() / 40.0D));
        float pitch = (float) (randomLerp(4.0f, 12.0f)
                * Math.cos(System.currentTimeMillis() / 40.0D));

        if ((target == null || !target.isAlive()) && sinceAttack >= JITTER_DURATION_MS) {
            yaw = 0.0f;
            pitch = 0.0f;
        }

        float returnSpeed = sinceAttack >= RETURN_DELAY_MS ? RETURN_SPEED : 0.0f;
        RotateVector returned = limitStraight(currentAngle, playerViewAngle, returnSpeed);
        int count = aura == null ? 0 : aura.getAttackCount();
        if (count > 0 && count % 4 == 0 && sinceAttack < SNAP_WINDOW_MS) {
            pitch = -90.0f;
            if (sinceAttack >= SNAP_SWING_MS && lastSwingAttack != count) {
                mc.player.swingHand(Hand.MAIN_HAND);
                lastSwingAttack = count;
            }
        }

        return cachedRotation = quantize(currentAngle, new RotateVector(
                returned.getYaw() + yaw,
                MathHelper.clamp(returned.getPitch() + pitch, -90.0f, 90.0f)
        ));
    }

    private RotateVector quantize(RotateVector previous, RotateVector desired) {
        float yawDelta = MathHelper.wrapDegrees(desired.getYaw() - previous.getYaw());
        float pitchDelta = MathHelper.wrapDegrees(desired.getPitch() - previous.getPitch());
        return new RotateVector(
                previous.getYaw() + applyGCD(yawDelta),
                MathHelper.clamp(previous.getPitch() + applyGCD(pitchDelta), -90.0f, 90.0f)
        );
    }

    private boolean canAttack(AuraModule aura) {
        return aura != null
                && aura.canAttackTargetNow()
                && aura.getTimeUntilNextAttackMs() <= PRE_AIM_MS;
    }

    private RotateVector limitStraight(RotateVector current, RotateVector target, float speed) {
        float yawDelta = MathHelper.wrapDegrees(target.getYaw() - current.getYaw());
        float pitchDelta = target.getPitch() - current.getPitch();
        float difference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
        float yawLimit = difference == 0.0f ? 0.0f : Math.abs(yawDelta / difference) * speed;
        float pitchLimit = difference == 0.0f ? 0.0f : Math.abs(pitchDelta / difference) * speed;
        return new RotateVector(
                current.getYaw() + MathHelper.clamp(yawDelta, -yawLimit, yawLimit),
                current.getPitch() + MathHelper.clamp(pitchDelta, -pitchLimit, pitchLimit)
        );
    }

    private long timeSinceAttack(AuraModule aura) {
        if (aura == null || aura.lastAttackTime <= 0L) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, System.currentTimeMillis() - aura.lastAttackTime);
    }

    private float randomLerp(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastSwingAttack = -1;
        lastUpdateTick = Integer.MIN_VALUE;
        cachedRotation = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastSwingAttack = -1;
        lastUpdateTick = Integer.MIN_VALUE;
        cachedRotation = null;
    }
}
