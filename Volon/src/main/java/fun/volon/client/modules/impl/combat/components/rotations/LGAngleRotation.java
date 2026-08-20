package fun.volon.client.modules.impl.combat.components.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.combat.AntiCheatEvasion;
import fun.volon.api.utils.math.TimerUtils;
import fun.volon.api.utils.rotate.MultipointUtils;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;
import fun.volon.client.modules.impl.combat.components.gcd.GCDUtil;

import java.util.concurrent.ThreadLocalRandom;

/**
 * CakeWorld / LGAngle — порт ротации с Toshiba для SlothAC-серверов.
 */
public class LGAngleRotation extends RotationsSystem {

    private final Aura aura;
    private final TimerUtils strikeTimer = new TimerUtils();

    private float lastYaw;
    private float lastPitch;

    public LGAngleRotation(Aura aura) {
        this.aura = aura;
    }

    public void reset() {
        strikeTimer.reset();
        AntiCheatEvasion.INSTANCE.reset();
        if (mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        } else {
            lastYaw = 0.0F;
            lastPitch = 0.0F;
        }
    }

    public void onStrike() {
        strikeTimer.reset();
    }

    @Override
    public void updateRotations(LivingEntity target) {
        if (mc.player == null || target == null) return;
        if (AntiCheatEvasion.INSTANCE.handleTransitions()) return;

        float currentYaw = lastYaw;
        float currentPitch = lastPitch;

        Vec3d aim = resolveAimPoint(target);
        if (shouldUseElytraPredict(target)) {
            aim = getPredictedPoint(target, aim);
        }

        Vec2f targetRot = RotationUtils.getRotations(aim);
        float targetYaw = targetRot.x;
        float targetPitch = targetRot.y;

        float yawDelta = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDelta = targetPitch - currentPitch;
        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        boolean canAttack = canStrike(target);
        float distanceToTarget = mc.player.distanceTo(target);

        float baseSpeed = canAttack ? 0.87F : 0.56F;

        if (distanceToTarget > 0.0F && distanceToTarget < 0.66F) {
            float closeRangeSpeed = MathHelper.clamp(distanceToTarget / 1.5F * 0.35F, 0.1F, 0.6F);
            baseSpeed = canAttack ? 0.85F : Math.min(baseSpeed, closeRangeSpeed);
        }

        float lineYaw = rotationDifference > 1.0E-4F
                ? Math.abs(yawDelta / rotationDifference) * 180.0F
                : 180.0F;
        float linePitch = rotationDifference > 1.0E-4F
                ? Math.abs(pitchDelta / rotationDifference) * 180.0F
                : 180.0F;

        float jitterYaw = 0.0F;
        float jitterPitch = 0.0F;
        if (!canAttack) {
            long time = System.currentTimeMillis();
            jitterYaw = (float) (randomLerp(18.0F, 27.0F) * Math.sin(time / 50.0D));
            jitterPitch = (float) (randomLerp(15.0F, 22.0F) * Math.sin(time / 13.0D));
        }

        boolean auraIdle = !aura.isEnable() || aura.getTarget() == null;
        if (auraIdle && strikeTimer.finished(1000L)) {
            baseSpeed = 0.35F;
            jitterYaw = 0.0F;
            jitterPitch = 0.0F;
        }

        float moveYaw = MathHelper.clamp(yawDelta, -lineYaw, lineYaw);
        float movePitch = MathHelper.clamp(pitchDelta, -linePitch, linePitch);

        float newYaw = MathHelper.lerp(baseSpeed, currentYaw, currentYaw + moveYaw) + jitterYaw;
        float newPitch = MathHelper.lerp(baseSpeed, currentPitch, currentPitch + movePitch) + jitterPitch;

        float gcd = GCDUtil.getGCDValue();
        if (gcd > 0.0F) {
            newYaw = currentYaw + Math.round((newYaw - currentYaw) / gcd) * gcd;
            newPitch = currentPitch + Math.round((newPitch - currentPitch) / gcd) * gcd;
        }

        newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
        lastYaw = newYaw;
        lastPitch = newPitch;

        float[] out = AntiCheatEvasion.INSTANCE.process(newYaw, newPitch, target, "lgangle");
        float rotSpeed = canAttack ? 360.0F : 45.0F;
        RotationStorage.update(
                new Rotation(out[0], out[1]),
                rotSpeed, rotSpeed, rotSpeed, rotSpeed,
                0, 1,
                Aura.clientLook.isState()
        );
    }

    private boolean canStrike(LivingEntity target) {
        if (aura.getTarget() == null || target != aura.getTarget()) {
            return false;
        }
        return mc.player.getAttackCooldownProgress(1.0F) >= 0.9F
                && aura.getWhiteRiseTicksToAttack() <= 1;
    }

    private Vec3d resolveAimPoint(LivingEntity target) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Vec3d base = MultipointUtils.getClosestPoint(target);
        if (base == null) {
            base = getPredictedBox(target).getCenter();
        }

        Vec3d offset = new Vec3d(
                (rnd.nextDouble() - 0.5) * 0.02,
                rnd.nextDouble() * 0.07,
                (rnd.nextDouble() - 0.5) * 0.02
        );
        return base.add(offset);
    }

    private float randomLerp(float min, float max) {
        return MathHelper.lerp(ThreadLocalRandom.current().nextFloat(), min, max);
    }
}
