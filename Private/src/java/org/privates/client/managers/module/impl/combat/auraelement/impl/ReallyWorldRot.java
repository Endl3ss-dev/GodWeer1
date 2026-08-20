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


public class ReallyWorldRot extends AbstractRotationElement {

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) return currentLerpRotation;
        Vector3d targetPosition = target.getPositionVec().add(0, target.getEyeHeight() * 0.8, 0);
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPosition.subtract(playerEyes).normalize();
        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float rawYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float playerYaw = mc.player.rotationYaw;
        float yawDiff = MathHelper.wrapDegrees(rawYaw - playerYaw);

        if (Math.abs(yawDiff) > 130.0f && mc.player.isElytraFlying()) {
            return currentLerpRotation;
        }

        float rawPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));
        long time = System.currentTimeMillis();
        float microTime = (time % 2000) / 2000.0f;
        double microPhase = Math.sin(microTime * Math.PI * 2) * 0.3;
        double patternMultiplier = 0.9 + Math.sin(time / 200.0) * 0.1;
        double silentCorrection = Math.cos(time / 900.0) * 0.02;
        double distance = mc.player.getDistanceSq(target);
        float stabilizationFactor = Math.min(1.0f, (float) (distance / 20.0));
        float deviationYaw = (float) (microPhase * 1.0 * patternMultiplier * stabilizationFactor);
        float deviationPitch = (float) (microPhase * 0.6 * patternMultiplier * stabilizationFactor);
        float randomComponent = (float) ((Math.sin(time / 150.0) + Math.cos(time / 280.0)) * 0.2 * stabilizationFactor);
        float finalYaw = rawYaw + deviationYaw + (float) silentCorrection;
        float finalPitch = MathHelper.clamp(rawPitch + deviationPitch + randomComponent, -90.0F, 90.0F);

        float yawSmoothing = 0.5f + (float) (Math.sin(time / 600.0) * 0.1);
        float pitchSmoothing = 0.45f + (float) (Math.cos(time / 700.0) * 0.08);
        if (distance < 12.0) {
            yawSmoothing = Math.min(yawSmoothing * 1.15f, 0.7f);
            pitchSmoothing = Math.min(pitchSmoothing * 1.1f, 0.6f);
        }

        float smoothedYaw = wrapLerp(yawSmoothing,
                MathHelper.wrapDegrees(currentLerpRotation.x),
                MathHelper.wrapDegrees(finalYaw));
        float smoothedPitch = wrapLerp(pitchSmoothing,
                currentLerpRotation.y,
                finalPitch);

        if (time % 300 < 70) {
            smoothedYaw += Math.sin(time / 45.0) * 0.07f;
            smoothedPitch += Math.cos(time / 40.0) * 0.04f;
        }

        finalYaw = GCDUtils.getSensitivity(smoothedYaw);
        finalPitch = GCDUtils.getSensitivity(smoothedPitch);

        Vector2f newLerpRotation = new Vector2f(finalYaw, finalPitch);
        applyRotation(new Rotation(finalYaw, finalPitch), 55.0f + (float) (Math.sin(time / 800.0) * 8.0), 35.0f + (float) (Math.cos(time / 900.0) * 6.0), 0, 3);
        parentModule.setLerpRotation(newLerpRotation);
        return newLerpRotation;
    }

    public float wrapLerp(float amount, float from, float to) {
        float diff = to - from;
        if (diff > 180) {
            diff -= 360;
        } else if (diff < -180) {
            diff += 360;
        }
        return from + diff * amount;
    }
}