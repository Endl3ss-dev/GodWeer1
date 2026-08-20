package fun.volon.client.modules.impl.combat.components.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.combat.AntiCheatEvasion;
import fun.volon.api.utils.combat.RayTraceUtil;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;
import fun.volon.client.modules.impl.combat.components.gcd.GCDUtil;
import fun.volon.client.modules.impl.combat.components.interpolation.BestPoint;
import fun.volon.api.utils.rotate.Rotation;

import java.util.concurrent.ThreadLocalRandom;

public class WellMineRotation extends RotationsSystem {

    private final Aura aura;

    private float wellmineSpeedAcceleration = 0.0f;
    private boolean wellmineBack = false;

    private int wellmineTargetId = Integer.MIN_VALUE;
    private Vec3d lastWellmineAimPoint = null;
    private float wellmineFilteredYawStep = 0.0f;
    private float wellmineFilteredPitchStep = 0.0f;
    private float wellmineNoiseYaw = 0.0f;
    private float wellmineNoisePitch = 0.0f;

    public WellMineRotation(Aura aura) {
        this.aura = aura;
    }

    public void reset() {
        wellmineSpeedAcceleration = 0.0f;
        wellmineBack = false;
        wellmineTargetId = Integer.MIN_VALUE;
        lastWellmineAimPoint = null;
        wellmineFilteredYawStep = 0.0f;
        wellmineFilteredPitchStep = 0.0f;
        wellmineNoiseYaw = 0.0f;
        wellmineNoisePitch = 0.0f;
        AntiCheatEvasion.INSTANCE.reset();
    }

    @Override
    public void updateRotations(LivingEntity target) {
        if (mc.player == null || target == null) return;
        if (AntiCheatEvasion.INSTANCE.handleTransitions()) return;

        // Reset state when target changes
        if (target.getId() != wellmineTargetId) {
            wellmineTargetId = target.getId();
            wellmineSpeedAcceleration = 0.0f;
            wellmineBack = false;
            lastWellmineAimPoint = null;
            wellmineFilteredYawStep = 0.0f;
            wellmineFilteredPitchStep = 0.0f;
        }

        // Get aim point — use BestPoint multipoint, fallback to bounding box center
        Vec3d aimPoint = getAimPoint(target);
        lastWellmineAimPoint = aimPoint;

        Vec3d vec = aimPoint.subtract(mc.player.getEyePos());
        float targetYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float targetPitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))),
                -90.0, 90.0
        );

        // Use current player yaw/pitch as base (same as FreeLookUtil.freeYaw in original)
        float baseYaw = mc.player.getYaw();
        float basePitch = mc.player.getPitch();

        // Acceleration/deceleration logic — exact copy of original
        if (!wellmineBack) {
            if (wellmineSpeedAcceleration >= 1.0f) {
                wellmineSpeedAcceleration = 0.0f;
            } else {
                float diff = Math.abs(MathHelper.wrapDegrees(targetYaw - baseYaw));
                wellmineSpeedAcceleration += diff > 40.0f ? 0.0025f : 0.005f;
            }

            // Check if aimed at target using raytrace
            boolean aimed = RayTraceUtil.rayTraceSingleEntity(baseYaw, basePitch, 6.0, target);
            if (wellmineSpeedAcceleration >= 0.18f || aimed) {
                wellmineBack = true;
            }
        } else {
            if (wellmineSpeedAcceleration >= -0.01f) {
                float diff = Math.abs(MathHelper.wrapDegrees(targetYaw - baseYaw));
                wellmineSpeedAcceleration -= diff > 40.0f ? 0.04f : 0.01f;
            }
            if (wellmineSpeedAcceleration <= -0.01f) {
                wellmineBack = false;
            }
        }

        // Random noise — exact values from original
        float randomYaw = (float) ThreadLocalRandom.current().nextDouble(-0.75, 0.75);
        float randomPitch = (float) ThreadLocalRandom.current().nextDouble(-0.75, 0.75);
        targetYaw += randomYaw;
        targetPitch += randomPitch;

        // Smooth value clamped to [-1, 1]
        float smoothVal = Math.min(Math.max(wellmineSpeedAcceleration, -1.0f), 1.0f);
        float yawDelta = MathHelper.wrapDegrees(targetYaw - baseYaw) * smoothVal;
        float pitchDelta = (targetPitch - basePitch) * (smoothVal / 2.0f);

        float newYaw = baseYaw + yawDelta;
        float newPitch = MathHelper.clamp(basePitch + pitchDelta, -89.0f, 89.0f);

        // Apply GCD — exact copy of original
        float gcd = GCDUtil.getGCDValue();
        float yawStep = MathHelper.wrapDegrees(newYaw - baseYaw);
        float pitchStep = newPitch - basePitch;
        yawStep -= yawStep % gcd;
        pitchStep -= pitchStep % gcd;

        Rotation newRotation = new Rotation(
                baseYaw + yawStep,
                MathHelper.clamp(basePitch + pitchStep, -89.0f, 89.0f)
        );

        float[] out = AntiCheatEvasion.INSTANCE.process(newRotation.getYaw(), newRotation.getPitch(), target, "wellmine");
        RotationStorage.update(
                new Rotation(out[0], out[1]),
                360.0f, 360.0f, 360.0f, 360.0f,
                1, 15,
                Aura.clientLook.isState()
        );
    }

    private Vec3d getAimPoint(LivingEntity target) {
        double dist = mc.player.distanceTo(target) + 2.5;
        Vec3d multipoint = BestPoint.getMultipoint(target, Math.max(6.0, dist));
        if (multipoint != null) {
            return multipoint;
        }
        return target.getBoundingBox().getCenter();
    }
}
