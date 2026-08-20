package fun.volon.client.modules.impl.combat.components.rotations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.math.MathUtils;
import fun.volon.api.utils.rotate.RaytracingUtil;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;

public class LonyGriefRotation extends RotationsSystem {
    private static int ticksSinceLastJerk = 0;
    private static int nextJerkTick = 8;
    private static float yawSpeedMultiplier = 0.0f;
    private static float pitchSpeedMultiplier = 0.0f;
    private static float accelerationSpeed = 0.15f;
    private static float decelerationSpeed = 0.35f;

    private final Aura aura;

    public LonyGriefRotation(Aura aura) {
        this.aura = aura;
    }

    @Override
    public void updateRotations(LivingEntity target) {
        Vec3d targetVec = target.getPos().add(0, target.getHeight() * 0.6f, 0);
        Rotation targetRotation = new Rotation(
                RotationUtils.getRotations(targetVec).x,
                RotationUtils.getRotations(targetVec).y
        );

        Rotation currentRotation = new Rotation(mc.player.getYaw(), mc.player.getPitch());
        Rotation result = process(currentRotation, targetRotation, targetVec, target);

        RotationStorage.update(result, 360, 360, 360, 360, 1, 1, Aura.clientLook.isState());
    }

    public Rotation process(Rotation currentRotation, Rotation targetRotation, Vec3d vec3d, Entity entity) {
        Rotation delta = calculateDelta(currentRotation, targetRotation);
        float yawDelta = delta.getYaw();
        float pitchDelta = delta.getPitch();

        float distance = aura.range.get() + 1.0f;
        boolean hasTarget = entity != null;
        EntityHitResult hitResult = RaytracingUtil.raytraceEntity(distance, currentRotation, false);
        boolean hasTrace = hasTarget && hitResult != null && hitResult.getEntity() == entity;

        float accelerationSpeed = 0.03f;
        if (Math.abs(yawDelta) > 15.0f) {
            accelerationSpeed = MathUtils.randomInRange(0.06f, 0.32f);
            yawDelta *= 1.5f;
        }

        if (!hasTrace) {
            yawSpeedMultiplier = MathUtils.interpolate(yawSpeedMultiplier, 1.0f, accelerationSpeed);
            pitchSpeedMultiplier = MathUtils.interpolate(pitchSpeedMultiplier, 1.0f, accelerationSpeed);
        } else {
            float decelerationSpeed = 0.55f;
            yawSpeedMultiplier = MathUtils.interpolate(yawSpeedMultiplier, 0.0f, decelerationSpeed);
            pitchSpeedMultiplier = MathUtils.interpolate(pitchSpeedMultiplier, 0.0f, decelerationSpeed);
        }

        float baseYawSpeed = MathUtils.randomInRange(38.0f, 48.0f) / (mc.player.isGliding() ? 3f : 1f);
        float basePitchSpeed = MathUtils.randomInRange(12.0f, 19.0f) / (mc.player.isGliding() ? 3f : 1f);

        float distanceFactor = MathHelper.clamp(Math.abs(yawDelta) / 15.0f, 0.2f, 1.0f);

        float yawSmoothMultiplier = yawSpeedMultiplier * yawSpeedMultiplier * (3 - 2 * yawSpeedMultiplier);
        float pitchSmoothMultiplier = pitchSpeedMultiplier * pitchSpeedMultiplier * (3 - 2 * pitchSpeedMultiplier);

        float yawSpeed = baseYawSpeed * yawSmoothMultiplier * distanceFactor;
        float pitchSpeed = basePitchSpeed * pitchSmoothMultiplier;

        if (Math.abs(yawDelta) > 5.0f) {
            float noise = (float) Math.sin(System.currentTimeMillis() / 20.0) * 5.0f;
            yawDelta += noise;
        }

        if (Math.abs(pitchDelta) > 3.0f) {
            float pitchNoise = (float) Math.cos(System.currentTimeMillis() / 20.0) * 155.5f;
            pitchDelta += pitchNoise;
        }

        ticksSinceLastJerk++;
        if (ticksSinceLastJerk >= nextJerkTick) {
            boolean isFliking = Math.abs(yawDelta) > 15.0f;
            if (!isFliking && hasTrace) {
                float jerkYaw = MathUtils.randomInRange(0.4f, 0.8f);
                float jerkPitch = MathUtils.randomInRange(0.5f, 0.9f);
                yawDelta *= jerkYaw;
                pitchDelta *= jerkPitch;
            }
            ticksSinceLastJerk = 0;
            nextJerkTick = (int) MathUtils.randomInRange(4, 10);
        }

        float clampedYawDelta = MathHelper.clamp(yawDelta, -yawSpeed, yawSpeed);
        float clampedPitchDelta = MathHelper.clamp(pitchDelta, -pitchSpeed, pitchSpeed);

        if (hasTrace && Math.abs(clampedYawDelta) < 0.2f) clampedYawDelta = 0;

        return new Rotation(
                currentRotation.getYaw() + clampedYawDelta,
                currentRotation.getPitch() + clampedPitchDelta
        );
    }

    private Rotation calculateDelta(Rotation current, Rotation target) {
        float yawDelta = MathHelper.wrapDegrees(target.getYaw() - current.getYaw());
        float pitchDelta = target.getPitch() - current.getPitch();
        return new Rotation(yawDelta, pitchDelta);
    }
}
