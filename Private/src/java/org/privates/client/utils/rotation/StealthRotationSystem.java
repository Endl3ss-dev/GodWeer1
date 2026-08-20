package org.privates.client.utils.rotation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;

import java.util.concurrent.ThreadLocalRandom;

import static org.privates.client.api.interfaces.IMinecraft.mc;


public class StealthRotationSystem {

    // === ОСНОВНЫЕ ПОЛЯ ===
    private Vector2f currentRotation = new Vector2f(0, 0);
    private Vector2f lastRotation = new Vector2f(0, 0);
    private Vector2f naturalRotation = new Vector2f(0, 0);

    // === ЧЕЛОВЕЧЕСКИЕ ФАКТОРЫ ===
    private float playerStress = 0.0f;
    private float fatigue = 0.0f;
    private float focus = 1.0f;
    private long lastDamageTime = 0;
    private boolean isLowHP = false;
    private float currentHP = 20.0f;

    // === TIMING ===
    private long systemStartTime = System.currentTimeMillis();
    private long lastRotationUpdate = 0;
    private long lastMultiPointSwitch = 0;
    private long lastMicroCorrection = 0;

    // === МУЛЬТИПОИНТ СИСТЕМА ===
    private int currentMultiPoint = 0;
    private String currentBodyPart = "chest";
    private Vector3d lastTargetPoint = Vector3d.ZERO;
    private boolean shouldSwitchPoint = false;

    // === НАСТРОЙКИ НЕЗАМЕТНОСТИ ===
    private boolean useHumanTiming = true;
    private boolean useFatigueSystem = true;
    private boolean useStressResponse = true;
    private boolean useLowHPTremor = true;
    private boolean useNaturalDrift = true;
    private boolean useBlinkingSystem = true;
    private boolean useBreathingPattern = true;

    // === АНТИДЕТЕКТ СИСТЕМЫ ===
    private final boolean simulateHumanReactionTime = true;
    private final boolean simulateMouseAcceleration = true;
    private final boolean simulateEyeMovement = true;
    private final boolean simulateHandTremor = true;
    private final boolean simulateConcentration = true;

    // === КОНСТРУКТОР ===
    public StealthRotationSystem() {
        resetSystem();
    }

    // === ГЛАВНЫЙ МЕТОД ===
    public void updateStealthRotation(LivingEntity target) {
        if (target == null) return;

        updateHumanFactors();
        updateMultiPointSystem(target);
        applyHumanBehavior(target);

        normalizeRotation();
        applyStealthRotationComponent();
    }

    // === ОБНОВЛЕНИЕ ЧЕЛОВЕЧЕСКИХ ФАКТОРОВ ===
    private void updateHumanFactors() {
        long time = System.currentTimeMillis();
        float timePlaying = (time - systemStartTime) / 1000.0f;

        // Обновляем HP
        if (mc.player != null) {
            currentHP = mc.player.getHealth();
            isLowHP = currentHP < 6.0f; // Меньше 3 сердец
        }

        // Усталость (накапливается со временем)
        if (useFatigueSystem) {
            fatigue = Math.min(1.0f, timePlaying / 3600.0f * 0.3f); // За час игры - 30% усталости
            fatigue += (float) (Math.sin(time * 0.0001) * 0.1f); // Микро колебания
        }

        // Стресс от урона
        if (useStressResponse && time - lastDamageTime < 5000) {
            float stressDecay = (time - lastDamageTime) / 5000.0f;
            playerStress = Math.max(0.0f, 0.8f * (1.0f - stressDecay));
        } else {
            playerStress *= 0.95f; // Постепенное снижение стресса
        }

        // Фокус (зависит от усталости и стресса)
        if (simulateConcentration) {
            focus = Math.max(0.3f, 1.0f - fatigue * 0.5f - playerStress * 0.3f);
            focus += (float) (Math.sin(time * 0.0005) * 0.1f); // Колебания концентрации
            focus = MathHelper.clamp(focus, 0.3f, 1.0f);
        }
    }

    // === МУЛЬТИПОИНТ СИСТЕМА ===
    private void updateMultiPointSystem(LivingEntity target) {
        long time = System.currentTimeMillis();

        // Переключение точек каждые 100-500мс
        int switchInterval = 100 + ThreadLocalRandom.current().nextInt(400);

        if (time - lastMultiPointSwitch > switchInterval || shouldSwitchPoint) {
            switchMultiPoint(target);
            lastMultiPointSwitch = time;
            shouldSwitchPoint = false;
        }
    }

    private void switchMultiPoint(LivingEntity target) {
        // Рандомно выбираем часть тела
        String[] bodyParts = {
                "head", "neck", "chest", "stomach",
                "leftShoulder", "rightShoulder", "leftArm", "rightArm",
                "leftHand", "rightHand", "leftLeg", "rightLeg",
                "leftFoot", "rightFoot", "back", "spine"
        };

        String oldBodyPart = currentBodyPart;
        do {
            currentBodyPart = bodyParts[ThreadLocalRandom.current().nextInt(bodyParts.length)];
        } while (currentBodyPart.equals(oldBodyPart) && bodyParts.length > 1);

        // Получаем позицию для выбранной части тела
        Vector3d newTargetPoint = getBodyPartPosition(target, currentBodyPart);

        // Добавляем небольшую рандомизацию в пределах части тела
        float randomness = 0.1f + fatigue * 0.05f + playerStress * 0.08f;
        newTargetPoint = newTargetPoint.add(
                (ThreadLocalRandom.current().nextFloat() - 0.5f) * randomness,
                (ThreadLocalRandom.current().nextFloat() - 0.5f) * randomness * 0.6f,
                (ThreadLocalRandom.current().nextFloat() - 0.5f) * randomness
        );

        lastTargetPoint = newTargetPoint;
    }

    private Vector3d getBodyPartPosition(LivingEntity target, String bodyPart) {
        Vector3d basePos = target.getPositionVec();
        double height = target.getHeight();
        double width = target.getWidth();

        switch (bodyPart) {
            case "head":
                return basePos.add(0, height * 0.95, 0);
            case "neck":
                return basePos.add(0, height * 0.85, 0);
            case "chest":
                return basePos.add(0, height * 0.7, 0);
            case "stomach":
                return basePos.add(0, height * 0.5, 0);
            case "leftShoulder":
                return basePos.add(-width * 0.3, height * 0.8, 0);
            case "rightShoulder":
                return basePos.add(width * 0.3, height * 0.8, 0);
            case "leftArm":
                return basePos.add(-width * 0.4, height * 0.6, 0);
            case "rightArm":
                return basePos.add(width * 0.4, height * 0.6, 0);
            case "leftHand":
                return basePos.add(-width * 0.5, height * 0.4, 0);
            case "rightHand":
                return basePos.add(width * 0.5, height * 0.4, 0);
            case "leftLeg":
                return basePos.add(-width * 0.2, height * 0.25, 0);
            case "rightLeg":
                return basePos.add(width * 0.2, height * 0.25, 0);
            case "leftFoot":
                return basePos.add(-width * 0.2, height * 0.05, 0);
            case "rightFoot":
                return basePos.add(width * 0.2, height * 0.05, 0);
            case "back":
                return basePos.add(0, height * 0.6, width * 0.3);
            case "spine":
                return basePos.add(0, height * 0.5, width * 0.1);
            default:
                return basePos.add(0, height * 0.7, 0);
        }
    }

    // === ПРИМЕНЕНИЕ ЧЕЛОВЕЧЕСКОГО ПОВЕДЕНИЯ ===
    private void applyHumanBehavior(LivingEntity target) {
        Vector3d targetPos = lastTargetPoint;
        float[] rawRotations = calculateRotation(targetPos);

        // Применяем все человеческие факторы
        applyHumanReactionTime(rawRotations);
        applyFatigueEffects(rawRotations);
        applyStressEffects(rawRotations);
        applyLowHPTremor(rawRotations);
        applyNaturalDrift(rawRotations);
        applyBreathingPattern(rawRotations);
        applyBlinkingEffect(rawRotations);
        applyMouseAcceleration(rawRotations);
        applyEyeMovementSimulation(rawRotations);
        applyHandTremor(rawRotations);
        applyMicroCorrections(rawRotations);

        // Плавная интерполяция с учетом фокуса
        float smoothness = calculateDynamicSmoothness();
        interpolateToTarget(rawRotations[0], rawRotations[1], smoothness);
    }

    // === ЧЕЛОВЕЧЕСКАЯ РЕАКЦИЯ ===
    private void applyHumanReactionTime(float[] rotations) {
        if (!simulateHumanReactionTime) return;

        long time = System.currentTimeMillis();

        // Человеческая реакция 150-300мс
        int reactionDelay = 150 + ThreadLocalRandom.current().nextInt(150);
        reactionDelay += (int) (fatigue * 100); // Усталость замедляет реакцию
        reactionDelay += (int) (playerStress * 50); // Стресс может как ускорить, так и замедлить

        if (time - lastRotationUpdate < reactionDelay) {
            // Еще не время реагировать
        }
    }

    // === ЭФФЕКТЫ УСТАЛОСТИ ===
    private void applyFatigueEffects(float[] rotations) {
        if (!useFatigueSystem) return;

        long time = System.currentTimeMillis();

        // Микро дрожание от усталости
        float fatigueShake = (float) (Math.sin(time * 0.008) * fatigue * 0.15f);
        rotations[0] += fatigueShake;
        rotations[1] += fatigueShake * 0.6f;

        // Замедление от усталости
        float fatigueSlow = 1.0f + fatigue * 0.8f;
        // Применяется в calculateDynamicSmoothness()
    }

    // === ЭФФЕКТЫ СТРЕССА ===
    private void applyStressEffects(float[] rotations) {
        if (!useStressResponse) return;

        long time = System.currentTimeMillis();

        // Дрожание от стресса
        float stressShake = (float) (Math.sin(time * 0.012) * playerStress * 0.2f);
        float stressJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * playerStress * 0.3f;

        rotations[0] += stressShake + stressJitter;
        rotations[1] += (stressShake + stressJitter) * 0.7f;
    }

    // === ДРОЖАНИЕ ПРИ НИЗКОМ HP ===
    private void applyLowHPTremor(float[] rotations) {
        if (!useLowHPTremor || !isLowHP) return;

        long time = System.currentTimeMillis();
        float hpRatio = currentHP / 20.0f; // 20 = max HP
        float severity = 1.0f - hpRatio; // Чем меньше HP, тем сильнее дрожание

        // Интенсивное дрожание при критическом HP
        float tremorX = (float) (Math.sin(time * 0.015) * severity * 0.4f);
        float tremorY = (float) (Math.cos(time * 0.018) * severity * 0.3f);
        float randomTremor = (ThreadLocalRandom.current().nextFloat() - 0.5f) * severity * 0.5f;

        rotations[0] += tremorX + randomTremor;
        rotations[1] += tremorY + randomTremor * 0.8f;

        // Дополнительные случайные всплески паники
        if (ThreadLocalRandom.current().nextFloat() < severity * 0.05f) {
            rotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;
            rotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 1.2f;
        }
    }

    // === ЕСТЕСТВЕННЫЙ ДРЕЙФ ===
    private void applyNaturalDrift(float[] rotations) {
        if (!useNaturalDrift) return;

        long time = System.currentTimeMillis();

        // Медленный естественный дрейф мыши
        float driftX = (float) (Math.sin(time * 0.0003) * 0.05f);
        float driftY = (float) (Math.cos(time * 0.0002) * 0.03f);

        rotations[0] += driftX;
        rotations[1] += driftY;
    }

    // === ПАТТЕРН ДЫХАНИЯ ===
    private void applyBreathingPattern(float[] rotations) {
        if (!useBreathingPattern) return;

        long time = System.currentTimeMillis();

        // Дыхание влияет на прицеливание (4 секунды на цикл)
        float breathingCycle = (float) (Math.sin(time * 0.0015) * 0.08f);

        rotations[0] += breathingCycle * 0.3f;
        rotations[1] += breathingCycle;
    }

    // === ЭФФЕКТ МОРГАНИЯ ===
    private void applyBlinkingEffect(float[] rotations) {
        if (!useBlinkingSystem) return;

        long time = System.currentTimeMillis();

        // Моргание каждые 3-8 секунд
        if ((time % (3000 + ThreadLocalRandom.current().nextInt(5000))) < 100) {
            float blinkDisruption = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.1f;
            rotations[0] += blinkDisruption;
            rotations[1] += blinkDisruption * 0.6f;
        }
    }

    // === УСКОРЕНИЕ МЫШИ ===
    private void applyMouseAcceleration(float[] rotations) {
        if (!simulateMouseAcceleration) return;

        // Вычисляем скорость движения мыши
        float deltaYaw = Math.abs(getAngleDifference(rotations[0], currentRotation.x));
        float deltaPitch = Math.abs(getAngleDifference(rotations[1], currentRotation.y));
        float mouseSpeed = deltaYaw + deltaPitch;

        // Ускорение/замедление в зависимости от скорости
        float acceleration = 1.0f;
        if (mouseSpeed > 10.0f) {
            acceleration = 1.0f + (mouseSpeed - 10.0f) * 0.02f; // Ускорение на быстрых движениях
        } else if (mouseSpeed < 2.0f) {
            acceleration = 0.8f + mouseSpeed * 0.1f; // Замедление на медленных движениях
        }

        // Применяется в calculateDynamicSmoothness()
    }

    // === СИМУЛЯЦИЯ ДВИЖЕНИЯ ГЛАЗ ===
    private void applyEyeMovementSimulation(float[] rotations) {
        if (!simulateEyeMovement) return;

        long time = System.currentTimeMillis();

        // Саккады - быстрые движения глаз
        if ((time % 2000) < 50) { // Каждые 2 секунды
            float saccadeX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.2f;
            float saccadeY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.15f;

            rotations[0] += saccadeX;
            rotations[1] += saccadeY;
        }

        // Микро движения глаз
        float microX = (float) (Math.sin(time * 0.02) * 0.02f);
        float microY = (float) (Math.cos(time * 0.025) * 0.015f);

        rotations[0] += microX;
        rotations[1] += microY;
    }

    // === ДРОЖАНИЕ РУК ===
    private void applyHandTremor(float[] rotations) {
        if (!simulateHandTremor) return;

        long time = System.currentTimeMillis();

        // Естественное дрожание рук (8-12 Гц)
        float tremorFreq = 0.01f + (float) (Math.random() * 0.004f);
        float tremorX = (float) (Math.sin(time * tremorFreq) * 0.03f);
        float tremorY = (float) (Math.cos(time * tremorFreq * 1.1f) * 0.025f);

        // Усиливается от стресса и усталости
        float tremorMultiplier = 1.0f + playerStress * 0.5f + fatigue * 0.3f;

        rotations[0] += tremorX * tremorMultiplier;
        rotations[1] += tremorY * tremorMultiplier;
    }

    // === МИКРО КОРРЕКЦИИ ===
    private void applyMicroCorrections(float[] rotations) {
        long time = System.currentTimeMillis();

        // Микро коррекции каждые 50-200мс
        if (time - lastMicroCorrection > (50 + ThreadLocalRandom.current().nextInt(150))) {
            float microCorrectionX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.05f;
            float microCorrectionY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.03f;

            rotations[0] += microCorrectionX;
            rotations[1] += microCorrectionY;

            lastMicroCorrection = time;
        }
    }

    // === ДИНАМИЧЕСКАЯ ПЛАВНОСТЬ ===
    private float calculateDynamicSmoothness() {
        float baseSmoothness = 8.0f;

        // Факторы влияющие на плавность
        baseSmoothness *= (1.0f + fatigue * 0.6f); // Усталость замедляет
        baseSmoothness *= (1.0f + playerStress * 0.3f); // Стресс может замедлить
        baseSmoothness /= focus; // Фокус ускоряет

        if (isLowHP) {
            baseSmoothness *= 0.7f; // Паника ускоряет, но менее точно
        }

        // Случайные вариации
        baseSmoothness += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;

        return Math.max(3.0f, Math.min(15.0f, baseSmoothness));
    }

    // === УВЕДОМЛЕНИЕ О ПОЛУЧЕНИИ УРОНА ===
    public void onPlayerDamage() {
        lastDamageTime = System.currentTimeMillis();
        playerStress = Math.min(1.0f, playerStress + 0.4f);
        shouldSwitchPoint = true; // Быстрая смена цели при получении урона
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private void interpolateToTarget(float targetYaw, float targetPitch, float speed) {
        float yawDelta = getAngleDifference(targetYaw, currentRotation.x);
        float pitchDelta = getAngleDifference(targetPitch, currentRotation.y);

        currentRotation.x += yawDelta / speed;
        currentRotation.y += pitchDelta / speed;

        normalizeRotation();
    }

    private float[] calculateRotation(Vector3d targetPos) {
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerPos);

        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    private float getAngleDifference(float target, float current) {
        float difference = target - current;
        while (difference > 180.0F) difference -= 360.0F;
        while (difference < -180.0F) difference += 360.0F;
        return difference;
    }

    private void normalizeRotation() {
        currentRotation.x = wrapAngleTo180(currentRotation.x);
        currentRotation.y = MathHelper.clamp(currentRotation.y, -90.0F, 90.0F);
    }

    private float wrapAngleTo180(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    private void applyStealthRotationComponent() {
        // Динамические параметры для максимальной незаметности
        float yawSpeed = 120 + ThreadLocalRandom.current().nextInt(60); // 120-180
        float pitchSpeed = 60 + ThreadLocalRandom.current().nextInt(40); // 60-100
        int minYawSpeed = 2 + ThreadLocalRandom.current().nextInt(3); // 2-5
        int maxYawSpeed = 4 + ThreadLocalRandom.current().nextInt(4); // 4-8

        Rotation rotation = new Rotation(currentRotation.x, currentRotation.y);
        RotationComponent.update(rotation, yawSpeed, pitchSpeed, minYawSpeed, maxYawSpeed);
    }

    // === ПУБЛИЧНЫЕ МЕТОДЫ ===

    public void resetSystem() {
        currentRotation = new Vector2f(mc.player != null ? mc.player.rotationYaw : 0,
                mc.player != null ? mc.player.rotationPitch : 0);
        lastRotation = new Vector2f(currentRotation.x, currentRotation.y);
        naturalRotation = new Vector2f(currentRotation.x, currentRotation.y);

        playerStress = 0.0f;
        fatigue = 0.0f;
        focus = 1.0f;

        systemStartTime = System.currentTimeMillis();
        lastRotationUpdate = 0;
        lastMultiPointSwitch = 0;
        lastMicroCorrection = 0;

        currentMultiPoint = 0;
        currentBodyPart = "chest";
        shouldSwitchPoint = false;

    }

    public String getStealthInfo() {
        return String.format("§6[Stealth] §eTarget: §a%s §e| Stress: §c%.1f §e| Fatigue: §e%.1f §e| Focus: §a%.1f §e| HP: §c%.1f",
                currentBodyPart, playerStress * 100, fatigue * 100, focus * 100, currentHP);
    }

    // Геттеры для основных значений
    public float getYaw() {
        return currentRotation.x;
    }

    public float getPitch() {
        return currentRotation.y;
    }

    public String getCurrentBodyPart() {
        return currentBodyPart;
    }

    public float getPlayerStress() {
        return playerStress;
    }

    public float getFatigue() {
        return fatigue;
    }

    public float getFocus() {
        return focus;
    }

    // Настройки системы
    public void setHumanTiming(boolean enabled) {
        this.useHumanTiming = enabled;
    }

    public void setFatigueSystem(boolean enabled) {
        this.useFatigueSystem = enabled;
    }

    public void setStressResponse(boolean enabled) {
        this.useStressResponse = enabled;
    }

    public void setLowHPTremor(boolean enabled) {
        this.useLowHPTremor = enabled;
    }

    public void setNaturalDrift(boolean enabled) {
        this.useNaturalDrift = enabled;
    }

    public void setBlinkingSystem(boolean enabled) {
        this.useBlinkingSystem = enabled;
    }

    public void setBreathingPattern(boolean enabled) {
        this.useBreathingPattern = enabled;
    }

    // Методы для интеграции с AuraNew
    public void applyToRotationComponent(LivingEntity target) {
        updateStealthRotation(target);
    }
}