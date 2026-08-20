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
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.concurrent.ThreadLocalRandom;

public class FunTimeBypassRot extends AbstractRotationElement {

    private int currentPointIndex = 0;
    private long lastHitTime = 0;

    @Override
    @Native

    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        long currentTime = System.currentTimeMillis();
        long timeSinceLastHit = currentTime - lastHitTime;

        boolean canAttack = AuraUtil.getStrictDistance(target) < parentModule.attackDistance();

        if (canAttack && parentModule.rayTrace() && timeSinceLastHit >= 550) {
            onAttack(parentModule);
        }

        Vector3d targetPoint;
        float yawSpeed;
        float pitchSpeed;

        if (timeSinceLastHit < 450) {
            targetPoint = target.getPositionVec().add(0, target.getEyeHeight() * 0.75, 0);
            yawSpeed = Mathf.randomValue(1.875f, 7.5f);
            pitchSpeed = Mathf.randomValue(0.375f, 1.875f);
        } else if (timeSinceLastHit >= 450 && timeSinceLastHit < 550) {
            Vector3d[] bodyPoints = getBodyPoints(target, playerEyePos);
            currentPointIndex = ThreadLocalRandom.current().nextInt(0, 4);
            targetPoint = bodyPoints[currentPointIndex];

            yawSpeed = Mathf.randomValue(375, 468.75f);
            pitchSpeed = Mathf.randomValue(112.5f, 135);
        } else if (timeSinceLastHit > 2000) {
            float returnProgress = Math.min(1.0f, (timeSinceLastHit - 2000) / 1000f);

            float playerYaw = mc.player.rotationYaw;
            float playerPitch = mc.player.rotationPitch;

            Vector3d playerDirection = Vector3d.fromPitchYaw(playerPitch, playerYaw);
            targetPoint = playerEyePos.add(playerDirection.scale(3));

            yawSpeed = (float) Mathf.lerp(9.375f, 28.125f, returnProgress);
            pitchSpeed = (float) Mathf.lerp(2.25f, 9, returnProgress);
        } else {
            targetPoint = target.getPositionVec().add(0, target.getEyeHeight() * 0.75, 0);
            yawSpeed = Mathf.randomValue(9.375f, 18.75f);
            pitchSpeed = Mathf.randomValue(2.25f, 5.25f);
        }

        Vector3d directionVec = targetPoint.subtract(playerEyePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z)));

        float yawDelta = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDelta = targetPitch - currentLerpRotation.y;

        float yawStep = Math.min(Math.abs(yawDelta), yawSpeed) * Math.signum(yawDelta);
        float pitchStep = Math.min(Math.abs(pitchDelta), pitchSpeed) * Math.signum(pitchDelta);

        float yaw = currentLerpRotation.x + yawStep;
        float pitch = currentLerpRotation.y + pitchStep;

        int minClamp = -70;
        int maxClamp = 80;

        pitch = MathHelper.clamp(pitch, minClamp, maxClamp);

        float yawMin = 2;
        float yawMax = 5;
        float pitchMin = 2;
        float pitchMax = 4;

        float yawRand = (float) Math.ceil(MathHelper.lerp(ThreadLocalRandom.current().nextFloat(), yawMin, yawMax) * Math.cos(System.currentTimeMillis() / 56D));
        float pitchRand = (float) Math.ceil(MathHelper.lerp(ThreadLocalRandom.current().nextFloat(), pitchMin, pitchMax) * Math.sin(System.currentTimeMillis() / 57D));

        yaw += yawRand * 0.84f;
        pitch += pitchRand * 0.8f;

        yaw = GCDUtils.getSensitivity(yaw);
        pitch = GCDUtils.getSensitivity(pitch);

        pitch = MathHelper.clamp(pitch, minClamp, maxClamp);

        Rotation newRotation = new Rotation(yaw, pitch);
        RotationComponent.update(newRotation, 360, 360, 6, 5, 0, 15, false);
        parentModule.setLerpRotation(new Vector2f(yaw, pitch));

        return new Vector2f(yaw, pitch);
    }

    public void onAttack(AuraNew parentModule) {
        lastHitTime = System.currentTimeMillis();
    }

    private Vector3d[] getBodyPoints(LivingEntity target, Vector3d playerEyePos) {
        float randomMult = Mathf.randomValue(0.75f, 1.0f);

        Vector3d head = target.getPositionVec().add(0, target.getEyeHeight() * randomMult, 0);
        Vector3d chest = target.getPositionVec().add(0, target.getEyeHeight() * 0.6 * randomMult, 0);
        Vector3d leftArm = target.getPositionVec().add(-0.3 * randomMult, target.getEyeHeight() * 0.7 * randomMult, 0);
        Vector3d rightArm = target.getPositionVec().add(0.3 * randomMult, target.getEyeHeight() * 0.7 * randomMult, 0);

        return new Vector3d[]{head, chest, leftArm, rightArm};
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        currentPointIndex = 0;
        lastHitTime = 0;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}