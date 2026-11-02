package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Отдельная нейро-ротация (standalone Neuro).
 *
 * В отличие от Legit, эта ротация НЕ двигает реальный прицел игрока: она
 * возвращает целевой вектор, а плавный доворот выполняет RotationTask (silent
 * rotation), как у остальных режимов ротации. Игроку не нужно двигать мышь.
 *
 * Модель {@link NeuroSelfModel} предсказывает доворот за тик (dYaw, dPitch),
 * а код удерживает результат строго в границах хитбокса цели. Точка прицела
 * гуляет по хитбоксу (мультипоинты), чтобы наводка выглядела живой.
 */
public class NeuroRotation extends RotateModel {
    private static final float DEFAULT_ASSIST_STRENGTH = 0.20f;
    private static final float MAGNET_ANGLE = 3.0f;
    // Усадка границ хитбокса внутрь: целимся в тело, а не в самый край.
    private static final float HITBOX_SHRINK = 0.15f;

    // ── Миссы (управляются из AuraModule) ────────────────────────────────────
    public static volatile boolean missesEnabled = false;
    public static volatile int missesOutOf10 = 1;
    // Визуальный увод прицела в момент промаха (человечный мисс).
    private static volatile long missVisualUntil = 0L;
    private static volatile float missYawOff = 0f;
    private static volatile float missPitchOff = 0f;

    // ── Мультипоинты (как в Legit Neuro / NeuroTest) ─────────────────────────
    private static final float AIM_HEIGHT_OFFSET = -0.10f;
    // Доля от полуразмеров хитбокса, в пределах которой гуляет точка прицела.
    // Смещение всегда ВНУТРИ тела цели, поэтому не срезается clampToHitbox.
    private static final double MULTIPOINT_FRACTION_XZ = 0.65D;
    private static final double MULTIPOINT_FRACTION_Y = 0.70D;
    private static final long MULTIPOINT_INTERVAL_MIN_MS = 150L;
    private static final long MULTIPOINT_INTERVAL_MAX_MS = 400L;

    private Vec3d multiPointOffset = Vec3d.ZERO;
    private long lastMultiPointUpdate = 0L;
    private long nextMultiPointInterval = 250L;
    private boolean multiPointInitialized = false;

    private float stateYaw;
    private float statePitch;
    private boolean seeded;

    // Тайм-гейт шага: NeuroRotation.update() вызывается много раз за тик
    // (из onFastest). Без ограничения по времени шаги складывались и наводка
    // была мгновенной. Двигаем состояние не чаще одного шага раз в интервал.
    private static final long STEP_INTERVAL_NS = 22_000_000L; // ~22 мс (~45 Гц)
    private long lastStepNs = 0L;

    // Сглаженный шаг (как render-smoothing у Legit Neuro): убирает резкость,
    // делает наводку плавной и человечной вместо мгновенного снапа.
    private float smoothStepYaw;
    private float smoothStepPitch;
    // Плавный разгон при первом захвате цели: 0 -> 1, чтобы не было рывка.
    private float acquireEase;

    /**
     * Запускает визуальный увод прицела в сторону (мисс). Вызывается из
     * обработчика атак, когда текущий удар помечен как промах.
     */
    public static void triggerMissVisual() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        missYawOff = (r.nextBoolean() ? 1f : -1f) * (8f + r.nextFloat() * 10f);
        missPitchOff = (r.nextBoolean() ? 1f : -1f) * (4f + r.nextFloat() * 6f);
        missVisualUntil = System.currentTimeMillis() + 160L;
    }

    @Override
    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (currentRotation == null || mc.player == null || target == null || !target.isAlive()) {
            seeded = false;
            return currentRotation;
        }

        // Точка прицела с мультипоинтами (гуляет по хитбоксу).
        Vec3d aimPoint = aimPoint(target);
        Vec3d eye = mc.player.getEyePos();
        double dx = aimPoint.x - eye.x;
        double dy = aimPoint.y - eye.y;
        double dz = aimPoint.z - eye.z;
        double horiz = Math.max(0.5D, Math.sqrt(dx * dx + dz * dz));
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        float centerYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float centerPitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));

        // Активация от реальной камеры, чтобы не наводиться за спину.
        float cameraYaw = mc.player.getYaw();
        float cameraPitch = mc.player.getPitch();
        float cameraDiff = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - cameraYaw),
                centerPitch - cameraPitch);
        if (cameraDiff > getActivationAngle()) {
            seeded = false;
            return currentRotation;
        }

        if (!seeded) {
            stateYaw = currentRotation.getYaw();
            statePitch = currentRotation.getPitch();
            smoothStepYaw = 0f;
            smoothStepPitch = 0f;
            acquireEase = 0f;
            lastStepNs = 0L;
            seeded = true;
        }

        // Тайм-гейт: двигаем состояние наводки не чаще, чем раз в интервал,
        // независимо от частоты вызова update(). Иначе наводка мгновенная.
        long nowNs = System.nanoTime();
        boolean doStep = (nowNs - lastStepNs) >= STEP_INTERVAL_NS;
        if (!doStep) {
            // Между шагами возвращаем уже накопленное состояние (с миссом).
            float heldYaw = stateYaw;
            float heldPitch = statePitch;
            if (System.currentTimeMillis() < missVisualUntil) {
                heldYaw += missYawOff;
                heldPitch = MathHelper.clamp(heldPitch + missPitchOff, -90f, 90f);
            }
            return new RotateVector(applyGCD(heldYaw), applyGCD(MathHelper.clamp(heldPitch, -90f, 90f)));
        }
        lastStepNs = nowNs;

        float[] pred = NeuroSelfModel.predict(target);

        AuraModule aura = AuraModule.getInstance();
        float response = aura == null ? DEFAULT_ASSIST_STRENGTH : aura.getNeuroAimSpeed();
        float modelGain = MathHelper.clamp(response / DEFAULT_ASSIST_STRENGTH, 0.45f, 1.25f);

        // Ошибки до точки прицела.
        float yawErr = MathHelper.wrapDegrees(centerYaw - stateYaw);
        float pitchErr = centerPitch - statePitch;

        // Пропорциональная скорость наводки: доля закрываемой ошибки за шаг.
        // Зависит от "Скорости наводки" (0.10..0.30) -> lerp 0.14..0.34.
        // Так движение равномерно-плавное: быстро когда далеко, мягко у цели,
        // без резкого рывка в начале и без "застревания".
        float lerp = MathHelper.clamp(0.08f + response * 0.85f, 0.12f, 0.36f);

        // Разгон в первые тики захвата (чтобы старт не был резким рывком).
        acquireEase = MathHelper.clamp(acquireEase + 0.12f, 0f, 1f);
        lerp *= (0.55f + 0.45f * acquireEase);

        // Гашение агрессии вплотную: чуть мягче у самого тела цели.
        float proximity = MathHelper.clamp(((float) dist - 0.8f) / 2.2f, 0.6f, 1.0f);
        lerp *= proximity;

        float stepYaw = yawErr * lerp;
        float stepPitch = pitchErr * lerp;

        // Небольшая поправка от модели для человечности траектории (не доминирует).
        if (pred != null) {
            stepYaw += MathHelper.clamp(pred[0] * modelGain, -1.5f, 1.5f) * 0.35f;
            stepPitch += MathHelper.clamp(pred[1] * modelGain, -1.0f, 1.0f) * 0.35f;
        }

        // Мягкое ограничение максимальной скорости за шаг (страховка от рывка
        // на очень больших углах), но не душит обычную наводку.
        float maxYawStep = 12.0f;
        float maxPitchStep = 8.0f;
        stepYaw = MathHelper.clamp(stepYaw, -maxYawStep, maxYawStep);
        stepPitch = MathHelper.clamp(stepPitch, -maxPitchStep, maxPitchStep);

        // Лёгкое сглаживание шага, чтобы убрать микро-дёрганье модели.
        float smoothing = 0.5f;
        smoothStepYaw += (stepYaw - smoothStepYaw) * smoothing;
        smoothStepPitch += (stepPitch - smoothStepPitch) * smoothing;

        stateYaw += smoothStepYaw;
        statePitch += smoothStepPitch;

        // Кламп в границы хитбокса применяем ТОЛЬКО когда прицел уже почти на
        // цели (в пределах углового размера хитбокса + небольшой запас). Иначе
        // во время плавного подхода кламп мгновенно "телепортировал" бы прицел
        // на тело — из-за этого наводка казалась моментальной.
        double halfW = Math.max(target.getBoundingBox().maxX - target.getBoundingBox().minX,
                target.getBoundingBox().maxZ - target.getBoundingBox().minZ) * 0.5D;
        float hbYawRadius = (float) Math.toDegrees(Math.atan2(halfW, Math.max(0.5D, dist)));
        float errToCenter = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - stateYaw),
                centerPitch - statePitch);
        if (errToCenter <= hbYawRadius + 4.0f) {
            float[] clamped = clampToHitbox(target, stateYaw, statePitch, HITBOX_SHRINK);
            stateYaw = clamped[0];
            statePitch = clamped[1];
        }

        // Мисс: в момент промаха уводим ВИДИМЫЙ прицел в сторону от тела, при
        // этом истинное состояние (stateYaw/Pitch) остаётся на цели.
        float outYaw = stateYaw;
        float outPitch = statePitch;
        if (System.currentTimeMillis() < missVisualUntil) {
            outYaw += missYawOff;
            outPitch = MathHelper.clamp(outPitch + missPitchOff, -90f, 90f);
        }

        outYaw = applyGCD(outYaw);
        outPitch = applyGCD(MathHelper.clamp(outPitch, -90f, 90f));
        return new RotateVector(outYaw, outPitch);
    }

    /**
     * Точка прицела внутри хитбокса цели с мультипоинтами: смещение случайно
     * перевыбирается каждые 150..400 мс, чтобы наводка гуляла по телу цели,
     * как в Legit Neuro / NeuroTest. Смещение масштабируется под РАЗМЕР
     * хитбокса цели и всегда остаётся внутри тела.
     */
    private Vec3d aimPoint(LivingEntity target) {
        if (!multiPointInitialized) {
            randMultiPoint(target);
        }
        long now = System.currentTimeMillis();
        if (now - lastMultiPointUpdate >= nextMultiPointInterval) {
            randMultiPoint(target);
        }

        Vec3d bbCenter = target.getBoundingBox().getCenter();
        return new Vec3d(
                bbCenter.x + multiPointOffset.x,
                bbCenter.y + AIM_HEIGHT_OFFSET + multiPointOffset.y,
                bbCenter.z + multiPointOffset.z);
    }

    private void randMultiPoint(LivingEntity target) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        net.minecraft.util.math.Box box = target.getBoundingBox();
        // Полуразмеры хитбокса цели; точка гуляет в пределах доли от них.
        double halfX = (box.maxX - box.minX) * 0.5D * MULTIPOINT_FRACTION_XZ;
        double halfZ = (box.maxZ - box.minZ) * 0.5D * MULTIPOINT_FRACTION_XZ;
        double halfY = (box.maxY - box.minY) * 0.5D * MULTIPOINT_FRACTION_Y;
        multiPointOffset = new Vec3d(
                rnd.nextDouble(-halfX, halfX),
                rnd.nextDouble(-halfY, halfY),
                rnd.nextDouble(-halfZ, halfZ)
        );
        lastMultiPointUpdate = System.currentTimeMillis();
        nextMultiPointInterval = rnd.nextLong(MULTIPOINT_INTERVAL_MIN_MS, MULTIPOINT_INTERVAL_MAX_MS + 1);
        multiPointInitialized = true;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        seeded = false;
        multiPointInitialized = false;
        multiPointOffset = Vec3d.ZERO;
        lastMultiPointUpdate = 0L;
        nextMultiPointInterval = 250L;
        smoothStepYaw = 0f;
        smoothStepPitch = 0f;
        acquireEase = 0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        seeded = false;
        multiPointInitialized = false;
    }

    private float getActivationAngle() {
        AuraModule aura = AuraModule.getInstance();
        return aura == null ? 45.0f : Math.max(MAGNET_ANGLE + 0.01f, aura.getNeuroFov());
    }
}
