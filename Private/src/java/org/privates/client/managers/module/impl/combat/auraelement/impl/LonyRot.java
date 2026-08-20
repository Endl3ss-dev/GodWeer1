package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.Random;

import static java.lang.Math.hypot;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class LonyRot extends AbstractRotationElement {

    private long lastHitTime = 0;
    private final Random random = new Random();

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f rotateVector) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastHit = currentTime - lastHitTime;

        boolean canAttack = AuraUtil.getStrictDistance(target) < parentModule.attackDistance();

        if (canAttack && parentModule.rayTrace() && timeSinceLastHit >= getAttackDelay()) {
            onAttack(parentModule);
        }

        Vector3d vec = getTargetVector(target, mc, timeSinceLastHit);
        float[] speeds = getRotationSpeeds(timeSinceLastHit);

        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
        float pitchToTarget = (float) (Math.toDegrees(-Math.atan2(vec.y, hypot(vec.x, vec.z))));

        float yawDelta = wrapDegrees(yawToTarget - rotateVector.x);
        float pitchDelta = pitchToTarget - rotateVector.y;

        float maxYawStep = Math.min(Math.max(Math.abs(yawDelta), 0.5f), speeds[0]);
        float maxPitchStep = Math.min(Math.max(Math.abs(pitchDelta), 0.5f), speeds[1]);

        float yaw = rotateVector.x + (yawDelta > 0 ? maxYawStep : -maxYawStep);
        float pitch = clamp(rotateVector.y + (pitchDelta > 0 ? maxPitchStep : -maxPitchStep), -89.0F, 89.0F);

        yaw = GCDUtils.getSensitivity(yaw);
        pitch = GCDUtils.getSensitivity(pitch);
        pitch = clamp(pitch, -70, 80);

        Rotation newRotation = new Rotation(yaw, pitch);
        RotationComponent.update(newRotation, 360, 180, 6, 5, 0, 15, false);
        parentModule.setLerpRotation(new Vector2f(yaw, pitch));

        return new Vector2f(yaw, pitch);
    }

    private Vector3d getTargetVector(LivingEntity target, Minecraft mc, long timeSinceLastHit) {
        Vector3d targetPoint;

        if (timeSinceLastHit < 450) {
            targetPoint = target.getPositionVec().add(0, target.getEyeHeight() * 0.75, 0);
        } else if (timeSinceLastHit >= 450 && timeSinceLastHit < 550) {
            float randomMult = 0.75f + random.nextFloat() * 0.25f;
            targetPoint = target.getPositionVec().add(0, target.getEyeHeight() * randomMult, 0);
        } else if (timeSinceLastHit > 2000) {
            float playerYaw = mc.player.rotationYaw;
            float playerPitch = mc.player.rotationPitch;
            Vector3d playerDirection = Vector3d.fromPitchYaw(playerPitch, playerYaw);
            targetPoint = mc.player.getEyePosition(mc.getRenderPartialTicks()).add(playerDirection.scale(3));
        } else {
            targetPoint = target.getPositionVec().add(0, target.getEyeHeight() * 0.75, 0);
        }

        return targetPoint.subtract(mc.player.getEyePosition(1.0F));
    }

    private float[] getRotationSpeeds(long timeSinceLastHit) {
        float yawSpeed = 3.5f;
        float pitchSpeed = 2.5f;

        if (timeSinceLastHit >= 450 && timeSinceLastHit < 550) {
            yawSpeed = 300f;
            pitchSpeed = 100f;
        } else if (timeSinceLastHit > 2000) {
            float returnProgress = Math.min(1.0f, (timeSinceLastHit - 2000) / 1000f);
            yawSpeed = 7.5f + returnProgress * (22.5f - 7.5f);
            pitchSpeed = 1.8f + returnProgress * (7.2f - 1.8f);
        }

        return new float[]{yawSpeed, pitchSpeed};
    }

    private int getAttackDelay() {
        int min = 650;
        int max = 820;
        return min + random.nextInt(max - min + 1);
    }

    public void onAttack(AuraNew parentModule) {
        lastHitTime = System.currentTimeMillis();
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastHitTime = 0;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}