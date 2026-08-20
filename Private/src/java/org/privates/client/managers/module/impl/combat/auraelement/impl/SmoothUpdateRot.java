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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SmoothUpdateRot extends AbstractRotationElement {

    private final List<Vector3d> multipointTargets = new ArrayList<>();
    private int currentPointIndex = 0;
    private long lastPointSwitch = 0;
    private Vector3d currentTargetPoint = null;
    private Vector3d previousTargetPoint = null;
    private float pointTransitionProgress = 1.0f;

    private float jitterPhase = 0f;
    private float jitterCircularAngle = 0f;
    private float jitterWavePhase = 0f;
    private float jitterChaoticX = 0f;
    private float jitterChaoticY = 0f;
    private long lastChaoticUpdate = 0;

    private long lastStepTime = 0;
    private Vector2f stepTarget = Vector2f.ZERO;
    private Vector2f lastStepRotation = Vector2f.ZERO;
    private boolean stepInitialized = false;

    private float lastYawDiff = 0f;
    private float lastPitchDiff = 0f;

    private float accumulatedYaw = 0f;
    private float accumulatedPitch = 0f;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (parentModule.smoothAttackMode().is("Snap")) {
            long timeSinceAttack = currentTime - parentModule.lastAttackTime;
            long msBeforeSnap = parentModule.smoothMsBeforeSnap().getValue().longValue();
            long msAfterSnap = parentModule.smoothMsAfterSnap().getValue().longValue();
            long attackCooldownMs = 550;

            boolean shouldRotate = timeSinceAttack <= msAfterSnap ||
                    (timeSinceAttack >= (attackCooldownMs - msBeforeSnap));

            if (!shouldRotate) {
                return currentLerpRotation;
            }
        }

        if (parentModule.smoothUseMultipoint().getValue()) {
            updateMultipointTargets(parentModule, target);
        }

        Vector3d aimPoint = getAimPoint(parentModule, target, currentTime);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = aimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        if (parentModule.smoothUsePitchLimit().getValue()) {
            float maxPitch = parentModule.smoothMaxPitch().getValue();
            float minPitch = parentModule.smoothMinPitch().getValue();
            targetPitch = MathHelper.clamp(targetPitch, minPitch, maxPitch);
        }

        if (parentModule.smoothUseJitter().getValue()) {
            Vector2f jitter = calculateJitter(parentModule, target, currentTime);
            targetYaw += jitter.x;
            targetPitch += jitter.y;
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        Vector2f speeds = calculateSpeeds(parentModule, yawDiff, pitchDiff);
        float interpolationFactor = calculateInterpolationFactor(parentModule, yawDiff, pitchDiff);

        float yawStep = yawDiff * interpolationFactor;
        float pitchStep = pitchDiff * interpolationFactor;

        if (Math.abs(yawStep) > speeds.x) yawStep = Math.copySign(speeds.x, yawStep);
        if (Math.abs(pitchStep) > speeds.y) pitchStep = Math.copySign(speeds.y, pitchStep);

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        if (parentModule.smoothUseStepRotation().getValue()) {
            Vector2f stepped = applyStepRotation(parentModule, new Vector2f(newYaw, newPitch), currentLerpRotation, currentTime);
            newYaw = stepped.x;
            newPitch = stepped.y;
        }

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        lastYawDiff = yawDiff;
        lastPitchDiff = pitchDiff;

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), speeds.x * 5, speeds.y * 5, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }

    private void updateMultipointTargets(AuraNew parentModule, LivingEntity target) {
        int maxPoints = parentModule.smoothMultipointMax().getValue().intValue();
        int minPoints = parentModule.smoothMultipointMin().getValue().intValue();
        int targetPointCount = minPoints + ThreadLocalRandom.current().nextInt(maxPoints - minPoints + 1);

        if (multipointTargets.size() != targetPointCount) {
            multipointTargets.clear();
            for (int i = 0; i < targetPointCount; i++) {
                float heightPercent = (float) i / (targetPointCount - 1);
                float widthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.6f;
                float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.6f;
                multipointTargets.add(new Vector3d(widthOffset, heightPercent * target.getHeight(), depthOffset));
            }
        }
    }

    private Vector3d getAimPoint(AuraNew parentModule, LivingEntity target, long currentTime) {
        if (!parentModule.smoothUseMultipoint().getValue()) {
            return target.getPositionVec().add(0, target.getEyeHeight() * 0.65, 0);
        }

        long switchDelay = (long) (parentModule.smoothMultipointSwitchSpeed().getValue() * 1000);

        if (currentTime - lastPointSwitch > switchDelay) {
            previousTargetPoint = currentTargetPoint;
            currentPointIndex = (currentPointIndex + 1) % multipointTargets.size();
            currentTargetPoint = multipointTargets.get(currentPointIndex);
            pointTransitionProgress = 0f;
            lastPointSwitch = currentTime;
        }

        if (previousTargetPoint != null && pointTransitionProgress < 1.0f) {
            float smoothness = parentModule.smoothMultipointSmoothness().getValue();
            pointTransitionProgress += smoothness * 0.1f;
            pointTransitionProgress = Math.min(pointTransitionProgress, 1.0f);

            float easedProgress = easeOutCubic(pointTransitionProgress);

            if (parentModule.smoothMultipointJerkTransition().getValue()) {
                easedProgress += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.15f;
                easedProgress = MathHelper.clamp(easedProgress, 0f, 1f);
            }

            Vector3d offset = previousTargetPoint.add(
                    (currentTargetPoint.x - previousTargetPoint.x) * easedProgress,
                    (currentTargetPoint.y - previousTargetPoint.y) * easedProgress,
                    (currentTargetPoint.z - previousTargetPoint.z) * easedProgress
            );

            return target.getPositionVec().add(offset);
        }

        if (currentTargetPoint == null) {
            currentTargetPoint = multipointTargets.get(0);
        }

        return target.getPositionVec().add(currentTargetPoint);
    }

    private Vector2f calculateSpeeds(AuraNew parentModule, float yawDiff, float pitchDiff) {
        float minYawSpeed = parentModule.smoothMinYawSpeed().getValue();
        float maxYawSpeed = parentModule.smoothMaxYawSpeed().getValue();
        float minPitchSpeed = parentModule.smoothMinPitchSpeed().getValue();
        float maxPitchSpeed = parentModule.smoothMaxPitchSpeed().getValue();

        // Нормализация разницы углов для расчета скорости
        float yawNormalized = MathHelper.clamp(Math.abs(yawDiff) / 180f, 0f, 1f);
        float pitchNormalized = MathHelper.clamp(Math.abs(pitchDiff) / 90f, 0f, 1f);

        float yawSpeed = MathHelper.lerp(yawNormalized, minYawSpeed, maxYawSpeed);
        float pitchSpeed = MathHelper.lerp(pitchNormalized, minPitchSpeed, maxPitchSpeed);

        return new Vector2f(yawSpeed, pitchSpeed);
    }

    private float calculateInterpolationFactor(AuraNew parentModule, float yawDiff, float pitchDiff) {
        String interpolationType = parentModule.smoothInterpolationType().getValue();
        float strength = parentModule.smoothInterpolationStrength().getValue();

        // Увеличенная базовая скорость интерполяции
        float totalDiff = (Math.abs(yawDiff) + Math.abs(pitchDiff)) / 270f;
        totalDiff = MathHelper.clamp(totalDiff, 0f, 1f);

        float baseFactor = 0f;

        switch (interpolationType) {
            case "Линейная":
                baseFactor = totalDiff;
                break;
            case "Плавная":
                baseFactor = totalDiff * totalDiff * (3.0f - 2.0f * totalDiff);
                break;
            case "Экспоненциальная":
                baseFactor = 1.0f - (float) Math.pow(1.0f - totalDiff, 2);
                break;
            case "Синусоидальная":
                baseFactor = (float) Math.sin(totalDiff * Math.PI / 2);
                break;
            case "Кубическая":
                baseFactor = totalDiff * totalDiff * totalDiff;
                break;
        }

        // Усиленная интерполяция: минимум 0.15, максимум до 1.0
        float finalFactor = 0.15f + (baseFactor * strength * 0.85f);
        return MathHelper.clamp(finalFactor, 0.05f, 1.0f);
    }

    private Vector2f calculateJitter(AuraNew parentModule, LivingEntity target, long currentTime) {
        String jitterType = parentModule.smoothJitterType().getValue();
        float radius = parentModule.smoothJitterRadius().getValue();
        float speed = parentModule.smoothJitterSpeed().getValue();
        float interpolation = parentModule.smoothJitterInterpolation().getValue();

        // ИСПРАВЛЕНИЕ: Радиус теперь в градусах, а не в процентах
        // Увеличиваем радиус в 10-20 раз для видимого эффекта
        float effectiveRadius = radius * 15f; // Радиус теперь реально влияет на угол обзора

        float targetJitterX = 0f;
        float targetJitterY = 0f;

        switch (jitterType) {
            case "Круговой":
                jitterCircularAngle += speed * 0.1f;
                targetJitterX = (float) Math.cos(jitterCircularAngle) * effectiveRadius;
                targetJitterY = (float) Math.sin(jitterCircularAngle) * effectiveRadius * 0.6f;
                break;

            case "Волновой":
                jitterWavePhase += speed * 0.05f;
                targetJitterX = (float) Math.sin(jitterWavePhase) * effectiveRadius;
                targetJitterY = (float) Math.cos(jitterWavePhase * 0.7f) * effectiveRadius * 0.5f;
                break;

            case "Хаотичный":
                if (currentTime - lastChaoticUpdate > 100) {
                    jitterChaoticX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2f * effectiveRadius;
                    jitterChaoticY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2f * effectiveRadius * 0.6f;
                    lastChaoticUpdate = currentTime;
                }
                targetJitterX = jitterChaoticX;
                targetJitterY = jitterChaoticY;
                break;
        }

        if (parentModule.smoothJitterStep().getValue()) {
            long stepInterval = 50;
            if (currentTime - lastStepTime > stepInterval) {
                jitterPhase = targetJitterX;
                lastStepTime = currentTime;
            }
            targetJitterX = jitterPhase;
        }

        float smoothFactor = 1.0f - interpolation;
        float currentJitterX = MathHelper.lerp(smoothFactor, targetJitterX, jitterPhase);
        float currentJitterY = MathHelper.lerp(smoothFactor, targetJitterY, jitterPhase * 0.6f);

        jitterPhase = currentJitterX;

        return new Vector2f(currentJitterX, currentJitterY);
    }

    private Vector2f applyStepRotation(AuraNew parentModule, Vector2f target, Vector2f current, long currentTime) {
        float yawStep = parentModule.smoothStepYaw().getValue();
        float pitchStep = parentModule.smoothStepPitch().getValue();
        long stepDelay = 50; // Задержка между шагами в мс

        // Инициализация при первом вызове
        if (!stepInitialized) {
            lastStepRotation = current;
            stepTarget = current;
            lastStepTime = currentTime;
            stepInitialized = true;
            accumulatedYaw = 0f;
            accumulatedPitch = 0f;
            return current;
        }

        // Вычисляем разницу между целью и текущей позицией
        float yawDiff = MathHelper.wrapDegrees(target.x - lastStepRotation.x);
        float pitchDiff = target.y - lastStepRotation.y;

        // Накапливаем изменения
        accumulatedYaw += yawDiff;
        accumulatedPitch += pitchDiff;

        // Проверяем, пора ли делать следующий шаг
        boolean shouldStep = currentTime - lastStepTime >= stepDelay;

        if (shouldStep) {
            // Определяем размер шага
            float yawChange = 0f;
            float pitchChange = 0f;

            // Yaw шаг
            if (Math.abs(accumulatedYaw) > 0.01f) {
                if (Math.abs(accumulatedYaw) <= yawStep) {
                    // Если накопленное меньше шага - двигаемся на всю накопленную величину
                    yawChange = accumulatedYaw;
                    accumulatedYaw = 0f;
                } else {
                    // Если больше - делаем шаг в направлении цели
                    yawChange = Math.copySign(yawStep, accumulatedYaw);
                    accumulatedYaw -= yawChange;
                }
            }

            // Pitch шаг
            if (Math.abs(accumulatedPitch) > 0.01f) {
                if (Math.abs(accumulatedPitch) <= pitchStep) {
                    pitchChange = accumulatedPitch;
                    accumulatedPitch = 0f;
                } else {
                    pitchChange = Math.copySign(pitchStep, accumulatedPitch);
                    accumulatedPitch -= pitchChange;
                }
            }

            // Применяем шаг
            stepTarget = new Vector2f(
                    lastStepRotation.x + yawChange,
                    lastStepRotation.y + pitchChange
            );

            lastStepRotation = stepTarget;
            lastStepTime = currentTime;
        }

        // Возвращаем текущую целевую позицию (остается неизменной между шагами)
        return stepTarget;
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        multipointTargets.clear();
        currentPointIndex = 0;
        lastPointSwitch = 0;
        currentTargetPoint = null;
        previousTargetPoint = null;
        pointTransitionProgress = 1.0f;
        jitterPhase = 0f;
        jitterCircularAngle = 0f;
        jitterWavePhase = 0f;
        jitterChaoticX = 0f;
        jitterChaoticY = 0f;
        lastChaoticUpdate = 0;
        lastStepTime = 0;
        stepTarget = Vector2f.ZERO;
        lastStepRotation = Vector2f.ZERO;
        stepInitialized = false;
        lastYawDiff = 0f;
        lastPitchDiff = 0f;
        accumulatedYaw = 0f;
        accumulatedPitch = 0f;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        multipointTargets.clear();
        currentTargetPoint = null;
        previousTargetPoint = null;
        stepInitialized = false;
        accumulatedYaw = 0f;
        accumulatedPitch = 0f;
    }
}