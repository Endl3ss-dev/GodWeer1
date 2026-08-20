package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;


public class IntaveRot extends AbstractRotationElement {

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) return currentLerpRotation;

        if (mc.player.isElytraFlying()) {
            boolean hasPassedTarget = checkIfPassedTarget(target);
            double distanceToTarget = mc.player.getDistance(target);
        }


        Vector3d targetPos = target.getPositionVec().add(
                0,
                target.getEyeHeight() * (0.6 + Math.random() * 0.2),
                0
        );
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerPos);

        float baseYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float basePitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDeviation = (float) (Math.sin(System.currentTimeMillis() * 0.015) * 1.5);
        float pitchDeviation = (float) (Math.cos(System.currentTimeMillis() * 0.015));

        float targetYaw = baseYaw + yawDeviation;
        float targetPitch = MathHelper.clamp(basePitch + pitchDeviation, -90f, 90f);

        Vector3d targetMotion = new Vector3d(
                target.getPosX() - target.prevPosX,
                target.getPosY() - target.prevPosY,
                target.getPosZ() - target.prevPosZ
        );

        targetYaw += Math.toDegrees(Math.atan2(targetMotion.z, targetMotion.x)) * 0.3;
        targetPitch += Math.toDegrees(targetMotion.y) * 0.2;

        float smoothYaw = GCDUtils.getSensitivity(targetYaw);
        float smoothPitch = GCDUtils.getSensitivity(targetPitch);

        Vector2f newLerpRotation = new Vector2f(smoothYaw, smoothPitch);
        applyRotation(new Rotation(smoothYaw, smoothPitch), 95f, 35f, 0, 4);
        parentModule.setLerpRotation(newLerpRotation);
        return newLerpRotation;
    }

    @Override
    public void onEnable(AuraNew parentModule) {

    }

    @Override
    public void onDisable(AuraNew parentModule) {

    }
}