package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;

import java.util.concurrent.ThreadLocalRandom;

/**
 * SpookyTime rotation ported from expensive's SpookytimeRotationMode.
 *
 * The source mode is intentionally kept simple: when a hit is ready it turns
 * directly through the clamped remaining angle; between hits it uses the
 * source's negative/low lerp factor and four alternating shake patterns.
 */
public final class SpookyTimeRotation extends RotateModel {

    // The source shake is wider, but Light calls this mode from a much more
    // frequent update path. Keep the same pattern while reducing its radius.
    private static final float SHAKE_YAW_MIN = 1.5f;
    private static final float SHAKE_YAW_MAX = 3.5f;
    private static final float SHAKE_PITCH_MIN = 0.5f;
    private static final float SHAKE_PITCH_MAX = 1.25f;

    private LivingEntity lastTarget;

    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (currentRotation == null || target == null || mc.player == null) {
            lastTarget = null;
            return currentRotation;
        }

        lastTarget = target;

        AuraModule aura = AuraModule.getInstance();
        long timeSinceAttack = aura == null || aura.lastAttackTime <= 0L
                ? Long.MAX_VALUE
                : Math.max(0L, System.currentTimeMillis() - aura.lastAttackTime);
        int hitCounter = aura == null ? 0 : aura.getAttackCount();

        float speed = ThreadLocalRandom.current().nextBoolean() ? 0.12f : 0.06f;

        // Expensive changes the aim point when the current view has already
        // crossed the target: use the head point (100%) instead of the lower
        // fallback point (78%). This is what makes the rotation finish
        // sharply after passing through the entity.
        double distance = mc.player.distanceTo(target);
        boolean rayTracePassed = PlayerUtils.getMouseOver(
                target,
                currentRotation.getYaw(),
                currentRotation.getPitch(),
                distance,
                0.94f
        ) != null;
        Vec3d targetPoint = targetPoint(target, rayTracePassed ? 1.0D : 0.78D);
        RotateVector targetRotation = calcAim(targetPoint);

        float yaw = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
        float pitch = targetRotation.getPitch() - currentRotation.getPitch();
        float hypot = (float) Math.hypot(Math.abs(yaw), Math.abs(pitch));

        if (hypot < 1.0E-4f) {
            return currentRotation;
        }

        // Exact source formula: each axis gets its proportional 180-degree
        // limit before the remaining delta is clamped.
        float maxYaw = Math.abs(yaw / hypot) * 180.0f;
        float maxPitch = Math.abs(pitch / hypot) * 180.0f;
        float clampedYaw = MathHelper.clamp(yaw, -maxYaw, maxYaw);
        float clampedPitch = MathHelper.clamp(pitch, -maxPitch, maxPitch);

        if (aura != null && aura.getTarget() != null && aura.canDealCrit()) {
            return new RotateVector(
                    currentRotation.getYaw() + clampedYaw,
                    currentRotation.getPitch() + clampedPitch
            );
        }

        float factor = timeSinceAttack < 500L ? -0.15f : speed;
        float time1 = (System.currentTimeMillis() % 2000L) / 70.0f;
        float time2 = (System.currentTimeMillis() % 1000L) / 70.0f;
        float offsetYaw;
        float offsetPitch;

        switch (Math.floorMod(hitCounter, 4)) {
            case 0 -> {
                offsetYaw = (float) Math.cos(time1);
                offsetPitch = (float) Math.sin(time2);
            }
            case 1 -> {
                offsetYaw = (float) Math.sin(time1);
                offsetPitch = (float) Math.cos(time2);
            }
            case 2 -> {
                offsetYaw = (float) Math.sin(time1);
                offsetPitch = (float) -Math.cos(time2);
            }
            default -> {
                offsetYaw = (float) -Math.cos(time1);
                offsetPitch = (float) Math.sin(time2);
            }
        }

        float lerpFactor = MathHelper.clamp(randomLerp(factor, factor + 0.25f), 0.0f, 1.0f);
        float nextYaw = MathHelper.lerp(
                lerpFactor,
                currentRotation.getYaw(),
                currentRotation.getYaw() + clampedYaw
        );
        float nextPitch = MathHelper.lerp(
                lerpFactor,
                currentRotation.getPitch(),
                currentRotation.getPitch() + clampedPitch
        );

        if (timeSinceAttack < 1000L) {
            nextYaw += randomLerp(SHAKE_YAW_MIN, SHAKE_YAW_MAX) * offsetYaw;
            nextPitch += randomLerp(SHAKE_PITCH_MIN, SHAKE_PITCH_MAX) * offsetPitch;
        }

        return new RotateVector(nextYaw, nextPitch);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastTarget = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastTarget = null;
    }

    private float randomLerp(float from, float to) {
        return MathHelper.lerp(ThreadLocalRandom.current().nextFloat(), from, to);
    }
}
