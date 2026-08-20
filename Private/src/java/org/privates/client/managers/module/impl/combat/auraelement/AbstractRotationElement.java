package org.privates.client.managers.module.impl.combat.auraelement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector4f;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.component.impl.rotation.SmoothRotationComponent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.Optional;
import java.util.Random;


public abstract class AbstractRotationElement implements IRotationElement {
    protected static final Minecraft mc = Minecraft.getInstance();
    protected final Random random = new Random();

    @Override
    public void onEnable(AuraNew parentModule) {
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        RotationComponent.getInstance().stopRotation();
    }


    protected Vector2f calculateSmartRotation(LivingEntity entity, AuraNew parentModule) {
        if (entity == null) return new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);

        Vector3d targetPos;
        if (parentModule.smartHitbox().getValue()) {
            targetPos = getVisibleTargetPoint(entity, parentModule);
            if (targetPos == null) {
                targetPos = entity.getPositionVec().add(0, entity.getEyeHeight(), 0);
            }
        } else {
            targetPos = entity.getPositionVec().add(0, entity.getEyeHeight(), 0);
        }

        return AuraUtil.calculateRotation(mc.player.getEyePosition(1.0F), targetPos);
    }


    protected Vector4f calculateSmartRotationVector4f(LivingEntity entity, AuraNew parentModule) {
        if (entity == null) return new Vector4f(mc.player.rotationYaw, mc.player.rotationPitch, 0, 0);

        if (parentModule.smartHitbox().getValue()) {
            Vector3d visiblePoint = getVisibleTargetPoint(entity, parentModule);
            if (visiblePoint != null) {
                Vector3d playerEyes = mc.player.getEyePosition(1.0F);
                Vector3d direction = visiblePoint.subtract(playerEyes);
                float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
                float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
                return new Vector4f(yaw, pitch, 0, 0);
            }
        }

        return AuraUtil.calculateRotation(entity);
    }


    protected Vector3d getSmartTargetPosition(LivingEntity target, double heightMultiplier, AuraNew parentModule) {
        if (target == null) return Vector3d.ZERO;

        if (parentModule.smartHitbox().getValue()) {
            Vector3d visiblePoint = getVisibleTargetPoint(target, parentModule);
            if (visiblePoint != null) {
                return visiblePoint;
            }
        }

        return target.getPositionVec().add(0, target.getEyeHeight() * heightMultiplier, 0);
    }


    protected Vector3d getVisibleTargetPoint(LivingEntity target, AuraNew parentModule) {
        if (target == null) return null;

        AxisAlignedBB box = target.getBoundingBox();
        Vector3d eyePos = mc.player.getEyePosition(1.0F);

        double[][] points = {
                {box.getCenter().x, box.maxY - 0.1, box.getCenter().z},
                {box.getCenter().x, box.getCenter().y, box.getCenter().z},
                {box.getCenter().x, box.minY + target.getHeight() * 0.75, box.getCenter().z},
                {box.minX + 0.1, box.maxY - 0.2, box.minZ + 0.1},
                {box.maxX - 0.1, box.maxY - 0.2, box.minZ + 0.1},
                {box.minX + 0.1, box.maxY - 0.2, box.maxZ - 0.1},
                {box.maxX - 0.1, box.maxY - 0.2, box.maxZ - 0.1},
                {box.getCenter().x, box.minY + target.getHeight() * 0.5, box.getCenter().z},
                {box.minX + 0.1, box.getCenter().y, box.getCenter().z},
                {box.maxX - 0.1, box.getCenter().y, box.getCenter().z},
                {box.getCenter().x, box.getCenter().y, box.minZ + 0.1},
                {box.getCenter().x, box.getCenter().y, box.maxZ - 0.1}
        };

        Vector3d bestPoint = null;
        double bestDistance = Double.MAX_VALUE;

        for (double[] point : points) {
            Vector3d checkPoint = new Vector3d(point[0], point[1], point[2]);
            if (isPointVisible(checkPoint)) {
                double distance = eyePos.distanceTo(checkPoint);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPoint = checkPoint;
                }
            }
        }

        if (bestPoint == null) {
            return target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
        }

        return bestPoint;
    }


    protected boolean isPointVisible(Vector3d point) {
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                eyePos,
                point,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        ));
        return result.getType() == RayTraceResult.Type.MISS;
    }


    protected float calculatePingFactor() {
        return Optional.ofNullable(mc.getConnection())
                .map(connection -> connection.getPlayerInfo(mc.player.getGameProfile().getId()))
                .map(NetworkPlayerInfo::getResponseTime)
                .orElse(50) / 100f;
    }


    protected float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }


    protected float applyGaussianJitter(float rotation, float strength) {
        return rotation + (float) (random.nextGaussian() * strength);
    }

    protected void applyRotation(Rotation rotation, float yawSpeed, float pitchSpeed, int ticks, int smoothTicks) {
        RotationComponent.update(rotation, yawSpeed, pitchSpeed, ticks, smoothTicks);
    }

    protected void applySmoothRotation(Rotation rotation, float yawAccel, float yawDecel, float pitchAccel, float pitchDecel, int ticks, int smoothTicks, boolean stopOnTarget) {
        SmoothRotationComponent.update(rotation, yawAccel, yawDecel, pitchAccel, pitchDecel, ticks, smoothTicks, stopOnTarget);
    }

    protected boolean checkIfPassedTarget(LivingEntity entity) {
        if (entity == null || mc.player == null) return false;

        float yaw = mc.player.rotationYaw;
        double radianYaw = Math.toRadians(yaw);
        Vector3d lookDirection = new Vector3d(-Math.sin(radianYaw), 0, Math.cos(radianYaw));

        Vector3d toTarget = new Vector3d(
                entity.getPosX() - mc.player.getPosX(),
                0,
                entity.getPosZ() - mc.player.getPosZ()
        );

        lookDirection = lookDirection.normalize();
        if (toTarget.length() > 0) {
            toTarget = toTarget.normalize();
        }
        double dotProduct = lookDirection.x * toTarget.x + lookDirection.z * toTarget.z;
        return dotProduct < 0;
    }

    protected void applyDirectFlightRotation(AuraNew parentModule, Vector2f currentLerpRotation) {
        Vector3d playerDir = new Vector3d(
                mc.player.getPosX() - mc.player.prevPosX,
                mc.player.getPosY() - mc.player.prevPosY,
                mc.player.getPosZ() - mc.player.prevPosZ
        ).normalize();

        if (playerDir.length() < 0.01) {
            float yaw = mc.player.rotationYaw;
            double radianYaw = Math.toRadians(yaw);
            playerDir = new Vector3d(-Math.sin(radianYaw), 0, Math.cos(radianYaw));
        }

        float dirYaw = (float) Math.toDegrees(Math.atan2(playerDir.z, playerDir.x)) - 90F;
        float dirPitch = -5.0f;

        dirYaw += (float) (Math.sin(System.currentTimeMillis() * 0.001));
        dirPitch += (float) (Math.cos(System.currentTimeMillis() * 0.001) * 0.5);

        float yawSpeed = 0.15f;
        float pitchSpeed = 0.1f;

        float smoothYaw = wrapLerp(yawSpeed, currentLerpRotation.x, dirYaw);
        float smoothPitch = wrapLerp(pitchSpeed, currentLerpRotation.y, MathHelper.clamp(dirPitch, -90f, 90f));

        Vector2f newLerpRotation = new Vector2f(
                GCDUtils.getSensitivity(smoothYaw),
                GCDUtils.getSensitivity(smoothPitch)
        );

        applyRotation(new Rotation(newLerpRotation.x, newLerpRotation.y), 180, 110, 0, 4);
    }
}