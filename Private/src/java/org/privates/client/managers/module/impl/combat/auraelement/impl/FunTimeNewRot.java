package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;

import java.util.concurrent.ThreadLocalRandom;


public class FunTimeNewRot extends AbstractRotationElement {

    // Snap-up переменные
    private int attackCount = 0;
    private boolean isSnappingUp = false;
    private long snapStartTime = 0L;
    private final long snapDuration = 150L;
    private Vector2f snapStartRotation;
    private int attacksBeforeSnap = 19;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f rotate) {
        if (target == null) return rotate;

        // Вычисляем направление к цели
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);
        Vector3d direction = targetPos.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotate.x);
        float pitchDelta = targetPitch - rotate.y;

        float yaw, pitch;
        float clampedYaw, clampedPitch;
        float gcd;

        // Проверка на начало snap-up
        if (attackCount >= attacksBeforeSnap && !isSnappingUp) {
            isSnappingUp = true;
            snapStartTime = System.currentTimeMillis();
            snapStartRotation = new Vector2f(rotate.x, rotate.y);
            attackCount = 0;
            attacksBeforeSnap = 20 + ThreadLocalRandom.current().nextInt(6);
        }

        // Snap-up анимация
        if (isSnappingUp) {
            float progress = (System.currentTimeMillis() - snapStartTime) / (float) snapDuration;
            if (progress >= 1.0f) {
                isSnappingUp = false;
                rotate = new Vector2f(rotate.x, -90.0f);
            } else {
                float targetPitchSnap = -90.0f;
                rotate = new Vector2f(rotate.x, MathHelper.clamp(snapStartRotation.y + (targetPitchSnap - snapStartRotation.y) * progress, -90.0f, 90.0f));
            }
        } else {
            // Обычная ротация
            clampedYaw = MathHelper.clamp(Math.abs(yawDelta), 0.5f, 99.0f);
            clampedPitch = MathHelper.clamp(Math.abs(pitchDelta), 0.5f, 95.0f);

            float finalTargetYaw = rotate.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
            float finalTargetPitch = MathHelper.clamp(rotate.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -70.0f, 70.0f);

            if (!shouldPlayerFalling()) {
                finalTargetYaw = rotate.x + (mc.player.rotationYaw - rotate.x) * 0.15f;
                finalTargetPitch = MathHelper.clamp(rotate.y + (mc.player.rotationPitch - rotate.y) * 0.2f, -70.0f, 70.0f);
            }

            float time = System.currentTimeMillis();
            yaw = finalTargetYaw + (float) Math.sin(time / 55.0) * 17.0f;
            pitch = finalTargetPitch + (float) Math.cos(time / 650.0) * 2.0f;

            gcd = getGCDValue();
            yaw -= (yaw - rotate.x) % gcd;
            pitch -= (pitch - rotate.y) % gcd;

            rotate = new Vector2f(yaw, pitch);
        }

        // Применяем ротацию
        applyRotation(new Rotation(rotate.x, rotate.y), 100f, 100f, 0, 5);
        parentModule.setLerpRotation(rotate);

        return rotate;
    }

    // Метод для получения GCD значения
    private float getGCDValue() {
        float sensitivity = (float) (mc.gameSettings.mouseSensitivity * 0.6f + 0.2f);
        float gcd = sensitivity * sensitivity * sensitivity * 1.2f;
        return gcd;
    }

    // Проверка на падение игрока
    private boolean shouldPlayerFalling() {
        return !mc.player.isOnGround() && mc.player.getMotion().y < -0.1;
    }

    // Метод для увеличения счетчика атак (вызывать при каждой атаке)
    public void incrementAttackCount() {
        attackCount++;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        attackCount = 0;
        attacksBeforeSnap = 19;
        isSnappingUp = false;
        snapStartTime = 0L;
        snapStartRotation = null;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        isSnappingUp = false;
        attackCount = 0;
        snapStartRotation = null;
    }
}