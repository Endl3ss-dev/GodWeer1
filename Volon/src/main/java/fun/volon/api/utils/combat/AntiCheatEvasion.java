package fun.volon.api.utils.combat;

import fun.volon.api.QClient;
import fun.volon.api.utils.math.PerlinNoise;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.rotate.Rotation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Универсальная система обхода античитов для всех ротаций.
 *
 * Возможности:
 *  1. Плавная отводка при включении  (startSmoothStart)
 *  2. Плавная отводка при выключении (startSmoothReturn)
 *  3. Испытательная отводка          (triggerTestEvasion)
 *
 * Использование в ротации:
 *   // В начале updateRotations:
 *   if (AntiCheatEvasion.INSTANCE.handleTransitions()) return;
 *
 *   // Перед отправкой финальной ротации:
 *   float[] out = AntiCheatEvasion.INSTANCE.process(newYaw, newPitch, target, "sloth");
 *   RotationStorage.update(new Rotation(out[0], out[1]), ...);
 *
 *   // При включении модуля:
 *   AntiCheatEvasion.INSTANCE.startSmoothStart();
 *
 *   // При выключении модуля:
 *   AntiCheatEvasion.INSTANCE.startSmoothReturn();
 */
public class AntiCheatEvasion implements QClient {

    public static final AntiCheatEvasion INSTANCE = new AntiCheatEvasion();

    // ─────────────────────────────────────────────────────────────────────────
    // РЕЖИМ ОТВОДКИ
    // ─────────────────────────────────────────────────────────────────────────

    public enum EvasionMode {
        DISABLED,   // Выключена
        SMOOTH,     // Плавная (старт/стоп + микро-шум)
        TEST        // Испытательная (плавная + периодические тест-отводки)
    }

    private EvasionMode mode = EvasionMode.DISABLED;

    /** Устанавливает режим из строки настройки Aura */
    public void setMode(String modeName) {
        mode = switch (modeName) {
            case "Плавная"        -> EvasionMode.SMOOTH;
            case "Испытательная"  -> EvasionMode.TEST;
            default               -> EvasionMode.DISABLED;
        };
    }

    /**
     * Устанавливает режим из двух чекбоксов Aura.
     * Испытательная имеет приоритет над плавной.
     */
    public void setMode(boolean smoothEnabled, boolean testEnabled) {
        EvasionMode newMode;
        if (testEnabled) {
            newMode = EvasionMode.TEST;
        } else if (smoothEnabled) {
            newMode = EvasionMode.SMOOTH;
        } else {
            newMode = EvasionMode.DISABLED;
        }
        mode = newMode;
    }

    public EvasionMode getMode() { return mode; }

    // ─────────────────────────────────────────────────────────────────────────
    // ПЛАВНАЯ ОТВОДКА — состояние
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isReturningToOriginal = false;
    private boolean isSmoothStarting      = false;

    private float transStartYaw   = 0F;
    private float transStartPitch = 0F;
    private float transTargetYaw  = 0F;
    private float transTargetPitch= 0F;

    private float originalPlayerYaw   = 0F;
    private float originalPlayerPitch = 0F;

    private long  transitionStartTime = 0L;
    private long  transitionNoiseSeed = 0L;

    /** Длительность плавного перехода в мс */
    private static final long TRANSITION_DURATION = 1200L;

    // ─────────────────────────────────────────────────────────────────────────
    // ИСПЫТАТЕЛЬНАЯ ОТВОДКА — состояние
    // ─────────────────────────────────────────────────────────────────────────

    private boolean testEvasionActive = false;
    private int     testEvasionPhase  = 0;   // 1 = отводка, 2 = возврат
    private long    testEvasionStart  = 0L;
    private long    testEvasionNextAt = 0L;  // когда запустить следующую

    private float testOffsetYaw   = 0F;
    private float testOffsetPitch = 0F;

    private float testFromYaw   = 0F;
    private float testFromPitch = 0F;
    private float testToYaw     = 0F;
    private float testToPitch   = 0F;

    /** Длительность фазы отклонения */
    private static final long TEST_HOLD_MS   = 120L;
    /** Длительность фазы возврата */
    private static final long TEST_RETURN_MS = 180L;

    // ─────────────────────────────────────────────────────────────────────────
    // МИКРО-ШУМ
    // ─────────────────────────────────────────────────────────────────────────

    private double noiseT = 0.0;

    // ─────────────────────────────────────────────────────────────────────────
    // ПУБЛИЧНЫЙ API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Вызывается при ВКЛЮЧЕНИИ модуля.
     * Запоминает текущий взгляд игрока и начинает плавный переход к цели.
     */
    public void startSmoothStart() {
        if (mc.player == null || mode == EvasionMode.DISABLED) return;

        originalPlayerYaw   = mc.player.getYaw();
        originalPlayerPitch = mc.player.getPitch();

        transStartYaw   = originalPlayerYaw;
        transStartPitch = originalPlayerPitch;
        transTargetYaw  = originalPlayerYaw;
        transTargetPitch= originalPlayerPitch;

        transitionStartTime = System.currentTimeMillis();
        transitionNoiseSeed = System.currentTimeMillis();

        isSmoothStarting      = true;
        isReturningToOriginal = false;
    }

    /**
     * Вызывается при ВЫКЛЮЧЕНИИ модуля.
     * Плавно возвращает взгляд к исходной позиции с небольшим случайным смещением.
     */
    public void startSmoothReturn() {
        if (mc.player == null || mode == EvasionMode.DISABLED) return;

        transStartYaw   = mc.player.getYaw();
        transStartPitch = mc.player.getPitch();

        transTargetYaw   = originalPlayerYaw + (float)((Math.random() - 0.5) * 2.0);
        transTargetPitch = originalPlayerPitch + (float)((Math.random() - 0.5) * 1.2);
        transTargetYaw   = MathHelper.wrapDegrees(transTargetYaw);
        transTargetPitch = MathHelper.clamp(transTargetPitch, -90F, 90F);

        transitionStartTime = System.currentTimeMillis();
        transitionNoiseSeed = System.currentTimeMillis();

        isReturningToOriginal = true;
        isSmoothStarting      = false;
    }

    /**
     * Вручную запустить испытательную отводку прямо сейчас.
     */
    public void triggerTestEvasion(float currentYaw, float currentPitch) {
        if (testEvasionActive) return;

        testOffsetYaw   = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 10f; // ±5°
        testOffsetPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 5f;  // ±2.5°

        testFromYaw   = currentYaw;
        testFromPitch = currentPitch;
        testToYaw     = currentYaw   + testOffsetYaw;
        testToPitch   = MathHelper.clamp(currentPitch + testOffsetPitch, -90f, 90f);

        testEvasionStart  = System.currentTimeMillis();
        testEvasionPhase  = 1;
        testEvasionActive = true;
    }

    /**
     * Обрабатывает активные переходы (плавный старт / плавный возврат).
     * Должен вызываться в начале updateRotations().
     *
     * @return true — переход активен, ротацию обновлять не нужно
     */
    public boolean handleTransitions() {
        if (mode == EvasionMode.DISABLED) return false;
        if (isSmoothStarting) {
            handleSmoothTransition(true);
            return true;
        }
        if (isReturningToOriginal) {
            handleSmoothTransition(false);
            return true;
        }
        return false;
    }

    /**
     * Основная обработка ротации: испытательная отводка + микро-шум.
     * Вызывается перед отправкой финальной ротации в RotationStorage.
     *
     * @param targetYaw   желаемый yaw
     * @param targetPitch желаемый pitch
     * @param target      цель (может быть null)
     * @param rotationType тип ротации ("sloth", "legit", "neurollm", ...)
     * @return [yaw, pitch] после обработки
     */
    public float[] process(float targetYaw, float targetPitch,
                           LivingEntity target, String rotationType) {
        if (mc.player == null) return new float[]{targetYaw, targetPitch};

        float currentYaw   = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        long  now          = System.currentTimeMillis();

        // Обновляем цель для плавного старта
        if (isSmoothStarting) {
            transTargetYaw   = targetYaw;
            transTargetPitch = targetPitch;
        }

        // Режим "Выключена" — ничего не делаем
        if (mode == EvasionMode.DISABLED) {
            return new float[]{targetYaw, targetPitch};
        }

        // Режим "Испытательная" — автоматически запускаем тест-отводку по расписанию
        if (mode == EvasionMode.TEST) {
            if (!testEvasionActive && now >= testEvasionNextAt) {
                float angDist = (float) Math.hypot(
                    MathHelper.wrapDegrees(targetYaw - currentYaw),
                    targetPitch - currentPitch
                );
                if (angDist < 12f) {
                    triggerTestEvasion(currentYaw, currentPitch);
                    scheduleNextTestEvasion();
                }
            }

            if (testEvasionActive) {
                float[] te = tickTestEvasion(targetYaw, targetPitch, currentYaw, currentPitch, now);
                if (te != null) {
                    targetYaw   = te[0];
                    targetPitch = te[1];
                }
            }
        }

        // Режим "Плавная" и "Испытательная" — добавляем микро-шум
        return addMicroNoise(targetYaw, targetPitch, rotationType);
    }

    /**
     * Полный сброс состояния (при смене цели или отключении).
     */
    public void reset() {
        isSmoothStarting      = false;
        isReturningToOriginal = false;
        testEvasionActive     = false;
        testEvasionPhase      = 0;
        noiseT                = 0.0;
        scheduleNextTestEvasion();
    }

    public boolean isTransitionActive() {
        return isSmoothStarting || isReturningToOriginal;
    }

    public boolean isTestEvasionActive() {
        return testEvasionActive;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ПЛАВНЫЙ ПЕРЕХОД — внутренняя реализация
    // ─────────────────────────────────────────────────────────────────────────

    private void handleSmoothTransition(boolean isStarting) {
        if (mc.player == null) return;

        long  now     = System.currentTimeMillis();
        long  elapsed = now - transitionStartTime;

        if (elapsed >= TRANSITION_DURATION) {
            // Переход завершён
            if (!isStarting) {
                // Финально ставим целевую ротацию
                RotationStorage.update(
                    new Rotation(transTargetYaw, transTargetPitch),
                    180F, 180F, 0F, 0, 1
                );
            }
            isReturningToOriginal = false;
            isSmoothStarting      = false;
            return;
        }

        double progress = (double) elapsed / TRANSITION_DURATION;
        // Easing: easeInOutQuart
        progress = progress < 0.5
            ? 8.0 * progress * progress * progress * progress
            : 1.0 - Math.pow(-2.0 * progress + 2.0, 4.0) / 2.0;

        float interpYaw   = (float)(transStartYaw   + MathHelper.wrapDegrees(transTargetYaw   - transStartYaw)   * progress);
        float interpPitch = (float)(transStartPitch + (transTargetPitch - transStartPitch) * progress);

        // Perlin-шум затухает к концу перехода
        float noiseAmp = (float)(1.0 - progress * 0.7);
        float n1 = PerlinNoise.noise((float)(transitionNoiseSeed * 0.0006 + elapsed * 0.0012));
        float n2 = PerlinNoise.noise((float)(transitionNoiseSeed * 0.0009 + elapsed * 0.0018));

        interpYaw   += n1 * 0.8f * noiseAmp;
        interpPitch += n2 * 0.6f * noiseAmp;

        interpYaw   = MathHelper.wrapDegrees(interpYaw);
        interpPitch = MathHelper.clamp(interpPitch, -90F, 90F);

        // Адаптивная скорость
        float yawDiff   = Math.abs(MathHelper.wrapDegrees(interpYaw   - (isStarting ? originalPlayerYaw : transTargetYaw)));
        float pitchDiff = Math.abs(interpPitch - (isStarting ? originalPlayerPitch : transTargetPitch));
        float yawSpeed   = Math.min(90F, yawDiff   * 0.8F + 15F);
        float pitchSpeed = Math.min(90F, pitchDiff * 0.6F + 10F);

        RotationStorage.update(
            new Rotation(interpYaw, interpPitch),
            yawSpeed, pitchSpeed,
            yawSpeed, pitchSpeed,
            0, 1, false
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ИСПЫТАТЕЛЬНАЯ ОТВОДКА — внутренняя реализация
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Тикает испытательную отводку.
     * @return [yaw, pitch] если отводка активна, иначе null
     */
    private float[] tickTestEvasion(float targetYaw, float targetPitch,
                                    float currentYaw, float currentPitch, long now) {
        long elapsed = now - testEvasionStart;

        if (testEvasionPhase == 1) {
            // Фаза 1: плавно уходим в сторону
            if (elapsed < TEST_HOLD_MS) {
                float t = smoothStep((float) elapsed / TEST_HOLD_MS);
                float y = MathHelper.lerp(t, testFromYaw,   testToYaw);
                float p = MathHelper.lerp(t, testFromPitch, testToPitch);
                return new float[]{y, MathHelper.clamp(p, -90f, 90f)};
            } else {
                // Переходим к возврату
                testEvasionPhase = 2;
                testEvasionStart = now;
                elapsed = 0;
            }
        }

        if (testEvasionPhase == 2) {
            // Фаза 2: плавно возвращаемся к цели
            if (elapsed < TEST_RETURN_MS) {
                float t = smoothStep((float) elapsed / TEST_RETURN_MS);
                float y = MathHelper.lerp(t, testToYaw,   targetYaw);
                float p = MathHelper.lerp(t, testToPitch, targetPitch);
                return new float[]{y, MathHelper.clamp(p, -90f, 90f)};
            } else {
                // Отводка завершена
                testEvasionActive = false;
                testEvasionPhase  = 0;
            }
        }

        return null;
    }

    private void scheduleNextTestEvasion() {
        // Следующая испытательная отводка через 4–9 секунд
        testEvasionNextAt = System.currentTimeMillis()
            + 4000L + (long)(ThreadLocalRandom.current().nextDouble() * 5000.0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // МИКРО-ШУМ
    // ─────────────────────────────────────────────────────────────────────────

    private float[] addMicroNoise(float yaw, float pitch, String rotationType) {
        noiseT += 0.08 + ThreadLocalRandom.current().nextDouble() * 0.04;

        float scale = switch (rotationType.toLowerCase()) {
            case "sloth", "neurollm" -> 0.35f;
            case "legit"             -> 0.15f;
            case "wellmine"          -> 0.25f;
            default                  -> 0.20f;
        };

        float ny = PerlinNoise.noise((float)(noiseT * 1.3))  * 0.18f
                 + PerlinNoise.noise((float)(noiseT * 2.7))  * 0.09f;
        float np = PerlinNoise.noise((float)(noiseT * 1.1 + 5.0)) * 0.12f
                 + PerlinNoise.noise((float)(noiseT * 2.3 + 3.0)) * 0.06f;

        return new float[]{
            yaw   + ny * scale,
            MathHelper.clamp(pitch + np * scale * 0.6f, -90f, 90f)
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // УТИЛИТЫ
    // ─────────────────────────────────────────────────────────────────────────

    private static float smoothStep(float x) {
        x = MathHelper.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }
}
