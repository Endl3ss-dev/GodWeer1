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
import org.privates.client.managers.module.impl.combat.auraelement.IRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.concurrent.ThreadLocalRandom;

public class FTRot implements IRotationElement {

    private Vector2f rotate;
    private int attackCount = 0;

    private boolean isSnappingUp = false;
    private long snapStartTime = 0L;
    private final long snapDuration = 150L;
    private Vector2f snapStartRotation;
    private int attacksBeforeSnap = 20;

    private boolean isSnappingUpAfterAttack = false;
    private long snapUpStartTime = 0L;
    private final long snapUpDuration = 120L;
    private Vector2f snapUpStartRotation;
    private float snapUpTargetPitch;

    private float lastAttackStrength = 1.0f;

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (this.rotate == null) {
            this.rotate = currentLerpRotation;
        }

        if (mc.player != null) {
            float currentAttackStrength = mc.player.getCooledAttackStrength(0.0F);
            if (lastAttackStrength >= 0.9f && currentAttackStrength < 0.2f) {
                attackCount++;
                if (!isSnappingUp && !isSnappingUpAfterAttack && ThreadLocalRandom.current().nextFloat() < 0.12f) {
                    isSnappingUpAfterAttack = true;
                    snapUpStartTime = System.currentTimeMillis();
                    snapUpStartRotation = new Vector2f(rotate.x, rotate.y);
                    snapUpTargetPitch = 85.0f + ThreadLocalRandom.current().nextFloat() * 5.0f;
                }
            }
            this.lastAttackStrength = currentAttackStrength;
        }


        if (target == null) {
            if (mc.player != null) {
                this.rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
            }
            return this.rotate;
        }

        long currentTime = System.currentTimeMillis();
        float yaw, pitch;

        if (isSnappingUpAfterAttack) {
            float progress = (currentTime - snapUpStartTime) / (float) snapUpDuration;
            if (progress >= 1.0f) {
                isSnappingUpAfterAttack = false;
                yaw = rotate.x;
                pitch = snapUpTargetPitch;
            } else {
                float easeProgress = 1 - (float) Math.pow(1 - progress, 3);
                float currentPitch = snapUpStartRotation.y + (snapUpTargetPitch - snapUpStartRotation.y) * easeProgress;
                yaw = rotate.x;
                pitch = MathHelper.clamp(currentPitch, -90.0f, 90.0f);
            }
        } else {
            if (attackCount >= attacksBeforeSnap && !isSnappingUp) {
                isSnappingUp = true;
                snapStartTime = currentTime;
                snapStartRotation = new Vector2f(rotate.x, rotate.y);
                attackCount = 0;
                attacksBeforeSnap = 20 + ThreadLocalRandom.current().nextInt(6);
            }

            if (isSnappingUp) {
                float progress = (currentTime - snapStartTime) / (float) snapDuration;

                if (progress >= 1.0f) {
                    isSnappingUp = false;
                    yaw = rotate.x;
                    pitch = -90.0f;
                } else {
                    float targetPitch = -90.0f;
                    float currentPitch = snapStartRotation.y + (targetPitch - snapStartRotation.y) * progress;
                    yaw = rotate.x;
                    pitch = MathHelper.clamp(currentPitch, -90.0f, 90.0f);
                }
            } else {
                Vector3d eyePos = mc.player.getEyePosition(1.0F);
                Vector3d targetPos = target.getPositionVec().add(0.0F, MathHelper.clamp(target.getPosY() - target.getHeight(), 0.0F, (target.getHeight() / 2.0F)), 0.0F);
                Vector3d vec = targetPos.subtract(eyePos);

                float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0F);
                float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));

                float yawDelta = MathHelper.wrapDegrees(yawToTarget - rotate.x);
                float pitchDelta = pitchToTarget - rotate.y;

                float clampedYawSpeed = Math.min(Math.max(Math.abs(yawDelta), 0.5f), 99f);
                float clampedPitchSpeed = Math.min(Math.max(Math.abs(pitchDelta), 0.5f), 95f);

                float targetYaw = rotate.x + (yawDelta > 0 ? clampedYawSpeed : -clampedYawSpeed);
                float targetPitch = MathHelper.clamp(rotate.y + (pitchDelta > 0 ? clampedPitchSpeed : -clampedPitchSpeed), -70f, 70f);

                if (!parentModule.shouldPlayerFalling()) {
                    float smoothYaw = (mc.player.rotationYaw - rotate.x) * 0.15f;
                    float smoothPitch = (mc.player.rotationPitch - rotate.y) * 0.2f;
                    targetYaw = rotate.x + smoothYaw;
                    targetPitch = MathHelper.clamp(rotate.y + smoothPitch, -70f, 70f);
                }

                float lookAroundYaw = (float) (Math.sin(System.currentTimeMillis() / 50.0) * 8.5f);
                float lookAroundPitch = (float) (Math.cos(System.currentTimeMillis() / 1300.0) * 1.0f);

                yaw = targetYaw + lookAroundYaw;
                pitch = targetPitch + lookAroundPitch;

                float gcd = GCDUtils.getGCDValue();
                yaw -= (yaw - rotate.x) % gcd;
                pitch -= (pitch - rotate.y) % gcd;
            }
        }

        Vector2f newRotation = new Vector2f(yaw, pitch);
        this.rotate = newRotation;

        RotationComponent.update(new Rotation(newRotation.x, newRotation.y), 180, 90, 0, 0);
        parentModule.setLerpRotation(newRotation);
        return newRotation;
    }

    public void onAttack(AuraNew parentModule) {
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        reset(Minecraft.getInstance());
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        reset(Minecraft.getInstance());
    }

    private void reset(Minecraft mc) {
        if (mc.player != null) {
            this.rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        } else {
            this.rotate = null;
        }
        isSnappingUp = false;
        isSnappingUpAfterAttack = false;
        attackCount = 0;
        attacksBeforeSnap = 20 + ThreadLocalRandom.current().nextInt(6);
        lastAttackStrength = 1.0f;
    }
}