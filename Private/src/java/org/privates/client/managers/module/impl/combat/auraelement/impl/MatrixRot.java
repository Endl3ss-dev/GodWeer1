package org.privates.client.managers.module.impl.combat.auraelement.impl;

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

public class MatrixRot extends AbstractRotationElement {

    private float smoothYaw;
    private float smoothPitch;
    private long lastUpdateMs;

    private static final float FREEZE_DISTANCE = 0.85f;
    private static final float AIM_HEIGHT_FACTOR = 0.65f;
    private static final float PITCH_MIN = -75.0f;
    private static final float PITCH_MAX = 75.0f;
    private static final float PITCH_THRESHOLD = 5.0f;

    private float noiseYaw = 0f;
    private float noisePitch = 0f;
    private long noiseTimer = 0L;
    private static final long NOISE_INTERVAL_MS = 150L;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target,
                                   Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long now = System.currentTimeMillis();
        if (lastUpdateMs == 0L) {
            lastUpdateMs = now;
            smoothYaw = currentLerpRotation.x;
            smoothPitch = currentLerpRotation.y;
        }

        float dist = mc.player.getDistance(target);

        if (dist <= FREEZE_DISTANCE) {
            float fy = GCDUtils.getSensitivity(smoothYaw);
            float fp = GCDUtils.getSensitivity(smoothPitch);
            Vector2f frozen = new Vector2f(fy, fp);
            applyRotation(new Rotation(fy, fp), 40.0f, 28.0f, 2, 3);
            parentModule.setLerpRotation(frozen);
            return frozen;
        }

        float dt = MathHelper.clamp((now - lastUpdateMs) / 1000.0f, 0.001f, 0.05f);
        float dtScale = dt * 60.0f;
        lastUpdateMs = now;

        Vector3d eyes = mc.player.getEyePosition(1.0f);
        Vector3d targetPoint = target.getPositionVec().add(0.0, target.getEyeHeight() * AIM_HEIGHT_FACTOR, 0.0);

        if (!isPointVisible(targetPoint)) {
            float fy = GCDUtils.getSensitivity(smoothYaw);
            float fp = GCDUtils.getSensitivity(smoothPitch);
            Vector2f frozen = new Vector2f(fy, fp);
            applyRotation(new Rotation(fy, fp), 12.0f, 3.0f, 2, 3);
            parentModule.setLerpRotation(frozen);
            return frozen;
        }

        Vector3d delta = targetPoint.subtract(eyes);
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizontal));
        targetPitch = MathHelper.clamp(targetPitch, PITCH_MIN, PITCH_MAX);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - smoothYaw);
        float pitchDiff = targetPitch - smoothPitch;

        float yawSpeed = MathHelper.clamp(
                2.8f + Math.abs(yawDiff) * 0.19f,
                7.5f, 12.5f
        ) * dtScale;

        float pitchSpeed;
        if (Math.abs(pitchDiff) > PITCH_THRESHOLD) {
            pitchSpeed = MathHelper.clamp(
                    2.0f + Math.abs(pitchDiff) * 0.15f,
                    1.2f, 2.8f
            ) * dtScale;
        } else {
            pitchSpeed = 0.08f * dtScale;
        }

        smoothYaw += MathHelper.clamp(yawDiff, -yawSpeed, yawSpeed);
        smoothPitch = MathHelper.clamp(
                smoothPitch + MathHelper.clamp(pitchDiff, -pitchSpeed, pitchSpeed),
                PITCH_MIN, PITCH_MAX
        );

        long nowNoise = System.currentTimeMillis();
        if (nowNoise - noiseTimer > NOISE_INTERVAL_MS) {
            noiseTimer = nowNoise;
            noiseYaw = (float) (Math.random() * 0.36 - 0.18);
            noisePitch = (float) (Math.random() * 0.22 - 0.11);
        }

        float finalYaw = GCDUtils.getSensitivity(smoothYaw + noiseYaw * 0.3f);
        float finalPitch = GCDUtils.getSensitivity(smoothPitch + noisePitch * 0.2f);
        Vector2f result = new Vector2f(finalYaw, finalPitch);

        applyRotation(new Rotation(finalYaw, finalPitch), 60.0f, 14.0f, 2, 3);
        parentModule.setLerpRotation(result);
        return result;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastUpdateMs = 0L;
        if (Minecraft.getInstance().player != null) {
            smoothYaw = Minecraft.getInstance().player.rotationYaw;
            smoothPitch = Minecraft.getInstance().player.rotationPitch;
        }
        noiseYaw = 0f;
        noisePitch = 0f;
        noiseTimer = 0L;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        RotationComponent.getInstance().smoothResetRotation();
        lastUpdateMs = 0L;
    }
}