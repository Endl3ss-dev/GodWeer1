package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;

import java.util.Optional;
import java.util.Random;

/**
 * Very small aim assist.  Unlike the old port this class does not modify
 * fields that do not exist in the current mappings and does not rotate the
 * player directly.  RotationTask remains the only owner of the actual
 * player rotation.
 */
public class LegitRotation extends RotateModel {
    private static final float DEFAULT_ASSIST_STRENGTH = 0.20f;
    private static final float MAGNET_ANGLE = 3.0f;
    private static final float MAX_ASSIST_YAW = 0.8f;
    private static final float MAX_ASSIST_PITCH = 0.6f;
    private static final float ASSIST_SMOOTHING = 0.12f;
    private static final float RENDER_SMOOTHING = 0.15f;
    private static final float DEADZONE = 0.2f;

    private static final float RANDOM_SPEED_MIN = 0.85f;
    private static final float RANDOM_SPEED_MAX = 1.15f;
    private static final long RANDOM_CHANGE_INTERVAL_MS = 500L;

    private final float fixedActivationAngle;

    private final Random random = new Random();
    private float assistYaw;
    private float assistPitch;
    private float renderYaw;
    private float renderPitch;
    private float currentSpeedYaw = 1.0f;
    private float currentSpeedPitch = 1.0f;
    private float targetSpeedYaw = 1.0f;
    private float targetSpeedPitch = 1.0f;
    private long lastSpeedChange;
    private boolean active;

    public LegitRotation() {
        this(0.0f);
    }

    /**
     * @param fixedActivationAngle fixed camera FOV for a preset; zero keeps
     *                             the user-configurable Legit FOV.
     */
    public LegitRotation(float fixedActivationAngle) {
        this.fixedActivationAngle = fixedActivationAngle;
    }

    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (currentRotation == null || mc.player == null || target == null || !target.isAlive()) {
            fadeOut();
            return currentRotation;
        }

        // Под-ротация Neuro: играем предсказания обученной модели вместо ассиста
        if (AuraModule.isLegitNeuroEngine()) {
            float[] pred = LegitNeuroModel.predict(target);
            if (pred != null) {
                return updateNeuro(currentRotation, target, pred);
            }
        }

        updateRandomSpeed();

        Vec3d eyePos = mc.player.getCameraPosVec(1.0F);
        Vec3d targetPoint = getTargetPoint(target, eyePos);
        RotateVector targetRotation = calcAim(targetPoint);

        // Activation is measured from the real camera, so the assist cannot
        // acquire a target behind the player. The actual step, however, must
        // be measured from currentRotation: RotationTask restores the real
        // player yaw after syncing, therefore using mc.player.getYaw() here
        // would recreate the same tiny offset every tick and the head would
        // never continue toward the target.
        float cameraYaw = mc.player.getYaw();
        float cameraPitch = mc.player.getPitch();
        float cameraDiffYaw = MathHelper.wrapDegrees(targetRotation.getYaw() - cameraYaw);
        float cameraDiffPitch = targetRotation.getPitch() - cameraPitch;
        float cameraTotalDiff = (float) Math.hypot(cameraDiffYaw, cameraDiffPitch);

        float activationAngle = getActivationAngle();
        if (cameraTotalDiff > activationAngle) {
            fadeOut();
            return currentRotation;
        }

        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        float diffYaw = MathHelper.wrapDegrees(targetRotation.getYaw() - currentYaw);
        float diffPitch = targetRotation.getPitch() - currentPitch;
        float totalDiff = (float) Math.hypot(diffYaw, diffPitch);

        if (!active) {
            active = true;
            assistYaw = 0.0f;
            assistPitch = 0.0f;
            renderYaw = 0.0f;
            renderPitch = 0.0f;
        }

        if (totalDiff <= DEADZONE) {
            assistYaw *= 0.8f;
            assistPitch *= 0.8f;
        } else {
            float strength = totalDiff <= MAGNET_ANGLE
                    ? 1.0f
                    : 1.0f - MathHelper.clamp(
                    (totalDiff - MAGNET_ANGLE) / (activationAngle - MAGNET_ANGLE),
                    0.0f,
                    1.0f
            );

            float assistStrength = getAssistStrength();
            float stepYaw = diffYaw * assistStrength * strength * currentSpeedYaw;
            float stepPitch = diffPitch * assistStrength * strength * currentSpeedPitch;
            assistYaw += (stepYaw - assistYaw) * ASSIST_SMOOTHING;
            assistPitch += (stepPitch - assistPitch) * ASSIST_SMOOTHING;
            assistYaw = softClamp(assistYaw, MAX_ASSIST_YAW);
            assistPitch = softClamp(assistPitch, MAX_ASSIST_PITCH);
        }

        renderYaw += (assistYaw - renderYaw) * RENDER_SMOOTHING * currentSpeedYaw;
        renderPitch += (assistPitch - renderPitch) * RENDER_SMOOTHING * currentSpeedPitch;
        applySuperSmooth(currentYaw + renderYaw, currentPitch + renderPitch);
        return null;
    }

    /**
     * Нейро-движок: модель предсказывает доворот за тик и воспроизводит стиль
     * наводки игрока (включая мультипоинты по хитбоксу), а код лишь удерживает
     * прицел в пределах зоны цели и мягко подтягивает к её центру.
     */
    private RotateVector updateNeuro(RotateVector currentRotation, LivingEntity target, float[] pred) {
        Vec3d center = target.getBoundingBox().getCenter();
        double dx = center.x - mc.player.getX();
        double dy = center.y - mc.player.getEyePos().y;
        double dz = center.z - mc.player.getZ();
        double horiz = Math.max(0.5D, Math.sqrt(dx * dx + dz * dz));

        float centerYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float centerPitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));

        float cameraYaw = mc.player.getYaw();
        float cameraPitch = mc.player.getPitch();

        // Текущее отклонение взгляда от центра цели

        // Радиус мультипоинтов: сколько градусов можно гулять по хитбоксу

        float totalDiff = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - cameraYaw),
                centerPitch - cameraPitch);

        if (totalDiff > getActivationAngle()) {
            fadeOut();
            return currentRotation;
        }

        if (!active) {
            active = true;
            renderYaw = 0.0f;
            renderPitch = 0.0f;
        }

        AuraModule aura = AuraModule.getInstance();
        float response = aura == null ? DEFAULT_ASSIST_STRENGTH : aura.getLegitAimSpeed();
        float modelGain = MathHelper.clamp(response / DEFAULT_ASSIST_STRENGTH, 0.45f, 1.25f);
        float centerPull = MathHelper.clamp(0.08f + response * 0.45f, 0.08f, 0.24f);
        float maxYawStep = MathHelper.clamp(0.8f + response * 9.0f, 1.5f, 4.0f);
        float maxPitchStep = MathHelper.clamp(0.5f + response * 5.0f, 1.0f, 2.5f);

        // Модель даёт только небольшую поправку, а направление задаёт центр
        // цели. Так Neuro не застревает на границе хитбокса.
        float stepYaw = MathHelper.clamp(pred[0] * modelGain, -2.0f, 2.0f);
        float stepPitch = MathHelper.clamp(pred[1] * modelGain, -1.25f, 1.25f);
        stepYaw += MathHelper.wrapDegrees(centerYaw - cameraYaw) * centerPull;
        stepPitch += (centerPitch - cameraPitch) * centerPull;
        stepYaw = MathHelper.clamp(stepYaw, -maxYawStep, maxYawStep);
        stepPitch = MathHelper.clamp(stepPitch, -maxPitchStep, maxPitchStep);

        // Мягкое притяжение к центру - сильнее, чем дальше от него
        // Кандидат нового положения внутри зоны цели: свободные мультипоинты,
        // но выйти за хитбокс нельзя; на границе шаг разворачивается внутрь
        // Мёртвый зон: цель почти в прицеле и внутри зоны - не дёргаемся
        if (totalDiff <= DEADZONE) {
            renderYaw *= 0.8f;
            renderPitch *= 0.8f;
            return null;
        }

        float smoothing = MathHelper.clamp(response * 0.6f, 0.06f, 0.18f);
        renderYaw += (stepYaw - renderYaw) * smoothing;
        renderPitch += (stepPitch - renderPitch) * smoothing;

        // Кламп в границы хитбокса применяем ТОЛЬКО когда прицел уже почти на
        // цели (в пределах углового размера хитбокса + запас). Иначе на подходе
        // кламп мгновенно "телепортировал" бы прицел на тело, из-за чего наводка
        // при включении Aura казалась моментальной. Пока идёт подход к цели —
        // работает только плавный шаг renderYaw/renderPitch.
        float targetYaw = cameraYaw + renderYaw;
        float targetPitch = cameraPitch + renderPitch;
        double dist = Math.max(0.5D, Math.sqrt(dx * dx + dy * dy + dz * dz));
        double halfW = Math.max(
                target.getBoundingBox().maxX - target.getBoundingBox().minX,
                target.getBoundingBox().maxZ - target.getBoundingBox().minZ) * 0.5D;
        float hbYawRadius = (float) Math.toDegrees(Math.atan2(halfW, dist));
        float errToCenter = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - targetYaw),
                centerPitch - targetPitch);
        if (errToCenter <= hbYawRadius + 4.0f) {
            float[] clamped = clampToHitbox(target, targetYaw, targetPitch, 0.15f);
            targetYaw = clamped[0];
            targetPitch = clamped[1];
        }
        applySuperSmooth(targetYaw, targetPitch);
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetAssist();
        lastSpeedChange = 0L;
        currentSpeedYaw = 1.0f;
        currentSpeedPitch = 1.0f;
        targetSpeedYaw = 1.0f;
        targetSpeedPitch = 1.0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetAssist();
    }

    private void updateRandomSpeed() {
        long now = System.currentTimeMillis();
        if (now - lastSpeedChange >= RANDOM_CHANGE_INTERVAL_MS) {
            targetSpeedYaw = RANDOM_SPEED_MIN + random.nextFloat() * (RANDOM_SPEED_MAX - RANDOM_SPEED_MIN);
            targetSpeedPitch = RANDOM_SPEED_MIN + random.nextFloat() * (RANDOM_SPEED_MAX - RANDOM_SPEED_MIN);
            lastSpeedChange = now;
        }

        currentSpeedYaw += (targetSpeedYaw - currentSpeedYaw) * 0.1f;
        currentSpeedPitch += (targetSpeedPitch - currentSpeedPitch) * 0.1f;

        float jitter = 1.0f + (random.nextFloat() - 0.5f) * 0.02f;
        currentSpeedYaw *= jitter;
        currentSpeedPitch *= jitter;
    }

    private float softClamp(float value, float max) {
        if (value > max) return max + (value - max) * 0.3f;
        if (value < -max) return -max + (value + max) * 0.3f;
        return value;
    }

    private float getActivationAngle() {
        if (fixedActivationAngle > 0.0f) {
            return fixedActivationAngle;
        }
        AuraModule aura = AuraModule.getInstance();
        return aura == null ? 45.0f : Math.max(MAGNET_ANGLE + 0.01f, aura.getLegitFov());
    }

    private float getAssistStrength() {
        AuraModule aura = AuraModule.getInstance();
        return aura == null ? DEFAULT_ASSIST_STRENGTH : aura.getLegitAimSpeed();
    }

    private void fadeOut() {
        if (!active) return;

        assistYaw *= 0.85f;
        assistPitch *= 0.85f;
        renderYaw *= 0.85f;
        renderPitch *= 0.85f;
        if (Math.abs(assistYaw) < 0.001f && Math.abs(assistPitch) < 0.001f
                && Math.abs(renderYaw) < 0.001f && Math.abs(renderPitch) < 0.001f) {
            resetAssist();
        }
    }

    private void resetAssist() {
        active = false;
        assistYaw = 0.0f;
        assistPitch = 0.0f;
        renderYaw = 0.0f;
        renderPitch = 0.0f;
    }

    private void applySuperSmooth(float targetYaw, float targetPitch) {
        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        float currentHeadYaw = mc.player.headYaw;

        float yawStep = MathHelper.wrapDegrees(targetYaw - currentYaw) * 0.35f;
        float pitchStep = (targetPitch - currentPitch) * 0.35f;
        mc.player.setYaw(currentYaw + yawStep);
        mc.player.setPitch(currentPitch + pitchStep);

        float headDiff = MathHelper.wrapDegrees(mc.player.getYaw() - currentHeadYaw);
        mc.player.headYaw = currentHeadYaw + headDiff * 0.08f;
        mc.player.lastHeadYaw += (mc.player.headYaw - mc.player.lastHeadYaw) * 0.09f;
        mc.player.lastYaw += MathHelper.wrapDegrees(mc.player.getYaw() - mc.player.lastYaw) * 0.15f;
        mc.player.lastPitch += (mc.player.getPitch() - mc.player.lastPitch) * 0.5f;
    }

    private Vec3d getTargetPoint(LivingEntity target, Vec3d eyePos) {
        Vec3d look = mc.player.getRotationVec(1.0F);
        Vec3d reach = eyePos.add(look.multiply(999.0D));

        Box box = getPredictedBox(target);
        double shrinkXZ = target.isGliding() ? -0.5D : 0.1D;
        double shrinkY = target.isGliding() ? -0.5D : 0.1D;
        box = new Box(
                box.minX + box.getLengthX() * shrinkXZ / 2.0D,
                box.minY,
                box.minZ + box.getLengthZ() * shrinkXZ / 2.0D,
                box.maxX - box.getLengthX() * shrinkXZ / 2.0D,
                box.maxY - box.getLengthY() * shrinkY,
                box.maxZ - box.getLengthZ() * shrinkXZ / 2.0D
        );

        Optional<Vec3d> hit = box.raycast(eyePos, reach);
        if (hit.isPresent() || box.contains(eyePos)) {
            return hit.orElse(getPredictedPoint(target));
        }

        Vec3d predicted = getPredictedPoint(target);
        if (target.isInSwimmingPose()) {
            return predicted.add(0.0D, -0.5D, 0.0D);
        }
        return predicted;
    }

    private Box getPredictedBox(LivingEntity target) {
        Vec3d velocity = target.getVelocity();
        return target.getBoundingBox().offset(velocity.x * 2.0D, velocity.y * 2.0D, velocity.z * 2.0D);
    }

    private Vec3d getPredictedPoint(LivingEntity target) {
        Vec3d basePoint = target.getEntityPos().add(0.0D, target.getEyeHeight(target.getPose()), 0.0D);
        Vec3d velocity = target.getVelocity();
        Vec3d predicted = basePoint.add(velocity.x, velocity.y, velocity.z);
        return target.isOnGround() ? predicted : predicted.add(0.0D, -0.2D, 0.0D);
    }
}
