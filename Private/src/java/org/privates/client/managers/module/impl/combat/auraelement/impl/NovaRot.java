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

import java.util.concurrent.ThreadLocalRandom;

public class NovaRot extends AbstractRotationElement {
    private long lastUpdateTime = 0L;
    private long lastJitterUpdateTime = 0L;
    private long lastTimeDiffUpdate = 0L;

    private boolean alternatingFlag = true;
    private float smoothedYawWave = 0.0F;
    private float smoothedPitchWave = 0.0F;
    private float currentShakeYaw = 0.0F;
    private float currentShakePitch = 0.0F;

    private float smoothYaw = 0.0F;
    private float smoothPitch = 0.0F;
    private boolean smoothInit = false;

    private static final float FREEZE_DISTANCE = 0.33f;
    private static final float PITCH_MIN = -45.0f;
    private static final float PITCH_MAX = 55.0f;
    private static final float SAFE_MAX_YAW_STEP = 11.5f;
    private static final float SAFE_MAX_PITCH_STEP = 4.2f;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        long now = System.currentTimeMillis();
        if (!smoothInit) {
            smoothYaw = currentLerpRotation.x;
            smoothPitch = currentLerpRotation.y;
            smoothInit = true;
            lastTimeDiffUpdate = now;
        }

        if (mc.player.getDistance(target) <= FREEZE_DISTANCE) {
            float frozenYaw = GCDUtils.getSensitivity(smoothYaw);
            float frozenPitch = GCDUtils.getSensitivity(smoothPitch);
            Vector2f frozen = new Vector2f(frozenYaw, frozenPitch);
            applyRotation(new Rotation(frozenYaw, frozenPitch), 45.0f, 32.0f, 2, 3);
            parentModule.setLerpRotation(frozen);
            return frozen;
        }

        if (now - lastUpdateTime >= 150L) {
            alternatingFlag = !alternatingFlag;
            lastUpdateTime = now;
        }

        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        float time = now / 1000.0F;

        float offsetX = 0.17F * (float) Math.cos(now / 910.0);
        float offsetY = 0.08F * (float) Math.sin(now / 840.0);
        float offsetZ = 0.12F * (float) Math.cos(now / 980.0);

        // SpookyTime-style: целимся не в глаза, а чуть ниже/в центр тела
        Vector3d targetPoint = target.getPositionVec().add(
                offsetX,
                MathHelper.clamp(playerEyePos.y - target.getPosY(), 0.15F, target.getHeight() * 0.85F) + offsetY,
                offsetZ
        );

        if (!isPointVisible(targetPoint)) {
            float frozenYaw = GCDUtils.getSensitivity(smoothYaw);
            float frozenPitch = GCDUtils.getSensitivity(smoothPitch);
            Vector2f frozen = new Vector2f(frozenYaw, frozenPitch);
            applyRotation(new Rotation(frozenYaw, frozenPitch), 15.0f, 3.0f, 2, 3);
            parentModule.setLerpRotation(frozen);
            return frozen;
        }

        Vector3d direction = targetPoint.subtract(playerEyePos).normalize();
        float baseYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float basePitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.hypot(direction.x, direction.z)));
        basePitch = MathHelper.clamp(basePitch, PITCH_MIN, PITCH_MAX);

        float targetYawWave = (float) Math.sin(time * 1.25F) * 1.1F;
        float targetPitchWave = (float) Math.cos(time * 1.75F) * 0.7F;
        smoothedYawWave += (targetYawWave - smoothedYawWave) * 0.72F;
        smoothedPitchWave += (targetPitchWave - smoothedPitchWave) * 0.72F;

        float waveA = (float) Math.cos(now / 65.0);
        float waveB = (float) Math.sin(now / 52.0);
        float wantedShakeYaw = waveA * 7.0F + smoothedYawWave;
        float wantedShakePitch = waveB * 2.1F + smoothedPitchWave;

        long timeDiff = now - lastTimeDiffUpdate;
        lastTimeDiffUpdate = now;

        float yawSpeedPerTick = 2200.0F + ThreadLocalRandom.current().nextFloat() * 100.0F;
        float pitchSpeedPerTick = 12.0F + ThreadLocalRandom.current().nextFloat() * 13.0F;
        float maxYawDelta = yawSpeedPerTick * timeDiff / 50.0F;
        float maxPitchDelta = pitchSpeedPerTick * timeDiff / 50.0F;

        currentShakeYaw = moveTowards(currentShakeYaw, wantedShakeYaw, maxYawDelta);
        currentShakePitch = moveTowards(currentShakePitch, wantedShakePitch, maxPitchDelta);

        if (now - lastJitterUpdateTime > 130L) {
            currentShakeYaw += ThreadLocalRandom.current().nextFloat(-0.18F, 0.18F);
            currentShakePitch += ThreadLocalRandom.current().nextFloat(-0.12F, 0.12F);
            lastJitterUpdateTime = now;
        }

        float targetYaw = baseYaw + currentShakeYaw;
        float targetPitch = MathHelper.clamp(basePitch + currentShakePitch, PITCH_MIN, PITCH_MAX);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - smoothYaw);
        float pitchDiff = targetPitch - smoothPitch;

        float yawChangeSpeed;
        float pitchChangeSpeed;
        if (alternatingFlag) {
            yawChangeSpeed = 24.0F + ThreadLocalRandom.current().nextFloat(0.0F, 2.0F);
            pitchChangeSpeed = 8.0F + ThreadLocalRandom.current().nextFloat(0.0F, 2.0F);
        } else {
            yawChangeSpeed = 21.0F + ThreadLocalRandom.current().nextFloat(0.0F, 2.0F);
            pitchChangeSpeed = 9.0F + ThreadLocalRandom.current().nextFloat(0.0F, 2.0F);
        }

        smoothYaw += MathHelper.clamp(yawDiff, -yawChangeSpeed, yawChangeSpeed);
        smoothPitch = MathHelper.clamp(smoothPitch + MathHelper.clamp(pitchDiff, -pitchChangeSpeed, pitchChangeSpeed), PITCH_MIN, PITCH_MAX);

        // Anti-spike: чтобы в "жимких" проверках не было резких бум-рывков
        float safeYawDiff = MathHelper.wrapDegrees(smoothYaw - currentLerpRotation.x);
        float safePitchDiff = smoothPitch - currentLerpRotation.y;
        smoothYaw = currentLerpRotation.x + MathHelper.clamp(safeYawDiff, -SAFE_MAX_YAW_STEP, SAFE_MAX_YAW_STEP);
        smoothPitch = MathHelper.clamp(currentLerpRotation.y + MathHelper.clamp(safePitchDiff, -SAFE_MAX_PITCH_STEP, SAFE_MAX_PITCH_STEP), PITCH_MIN, PITCH_MAX);

        float finalYaw = GCDUtils.getSensitivity(smoothYaw);
        float finalPitch = GCDUtils.getSensitivity(smoothPitch);
        Vector2f result = new Vector2f(finalYaw, finalPitch);

        applyRotation(new Rotation(finalYaw, finalPitch), yawChangeSpeed, pitchChangeSpeed, 2, 3);
        parentModule.setLerpRotation(result);
        return result;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastUpdateTime = 0L;
        lastJitterUpdateTime = 0L;
        lastTimeDiffUpdate = 0L;
        alternatingFlag = true;
        smoothedYawWave = 0.0F;
        smoothedPitchWave = 0.0F;
        currentShakeYaw = 0.0F;
        currentShakePitch = 0.0F;
        smoothInit = false;

        if (Minecraft.getInstance().player != null) {
            smoothYaw = Minecraft.getInstance().player.rotationYaw;
            smoothPitch = Minecraft.getInstance().player.rotationPitch;
        }
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        RotationComponent.getInstance().smoothResetRotation();
        lastUpdateTime = 0L;
        lastJitterUpdateTime = 0L;
        lastTimeDiffUpdate = 0L;
        smoothInit = false;
    }

    private float moveTowards(float current, float target, float maxDelta) {
        if (Math.abs(target - current) <= maxDelta) {
            return target;
        }
        return current + Math.signum(target - current) * maxDelta;
    }
}
