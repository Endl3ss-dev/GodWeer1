package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.rotation.RotateVector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Test нейро-ротация (под-ротация "Test" в режиме Neuro).
 *
 * Архитектура:
 *  1. Геометрическая наводка — всегда ведёт прицел к цели (lerp к centerYaw/Pitch).
 *     Это основа, которая гарантирует что прицел НИКОГДА не улетит от цели.
 *  2. Модель — небольшая аддитивная поправка к шагу (<= 2°).
 *     Она учит человеческий стиль (micro-вариативность), а не управляет наводкой.
 *  3. Кламп к хитбоксу — применяется ВСЕГДА, финальная страховка.
 *
 * Без обученной модели работает как чистая геометрическая ротация.
 */
public class TNeuroRotation extends RotateModel {

    // ── Параметры из AuraModule ───────────────────────────────────────────────
    public static volatile float   aimSpeed      = 0.20f;
    public static volatile float   activationFov = 45.0f;

    // ── Миссы ────────────────────────────────────────────────────────────────
    public static volatile boolean missesEnabled = false;
    public static volatile int     missesOutOf10 = 1;

    private static volatile long  missVisualUntil = 0L;
    private static volatile float missYawOff      = 0f;
    private static volatile float missPitchOff    = 0f;

    /** Вызывается из AuraAttackHandler при промахе. */
    public static void triggerMissVisual() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        missYawOff      = (r.nextBoolean() ? 1f : -1f) * (8f + r.nextFloat() * 10f);
        missPitchOff    = (r.nextBoolean() ? 1f : -1f) * (4f + r.nextFloat() * 6f);
        missVisualUntil = System.currentTimeMillis() + 160L;
    }

    /**
     * Текущий stateYaw/Pitch ротации — используется рекордером чтобы
     * записывать шаг самой ротации (а не абсолютное движение мыши).
     */
    private static TNeuroRotation activeInstance = null;

    public static float getStateYaw() {
        return activeInstance != null && activeInstance.seeded ? activeInstance.stateYaw : Float.NaN;
    }

    public static float getStatePitch() {
        return activeInstance != null && activeInstance.seeded ? activeInstance.statePitch : Float.NaN;
    }

    // ── Константы ────────────────────────────────────────────────────────────
    private static final float  AIM_HEIGHT_OFFSET      = -0.10f;
    private static final double MULTIPOINT_FRACTION_XZ = 0.55D;
    private static final double MULTIPOINT_FRACTION_Y  = 0.60D;
    private static final long   MP_INTERVAL_MIN_MS     = 200L;
    private static final long   MP_INTERVAL_MAX_MS     = 500L;
    private static final float  HITBOX_SHRINK          = 0.20f;
    private static final float  MAGNET_ANGLE           = 3.0f;
    /** Шаг ~22 мс (~45 Гц) — как в NeuroRotation (Old) */
    private static final long   STEP_NS                = 22_000_000L;
    /** Максимальная поправка от модели за шаг (°). Малое значение = безопасно. */
    private static final float  MODEL_MAX_NUDGE        = 1.8f;

    // ── Мультипоинты ─────────────────────────────────────────────────────────
    private Vec3d   mpOffset       = Vec3d.ZERO;
    private long    mpLastUpdate   = 0L;
    private long    mpNextInterval = 300L;
    private boolean mpInit         = false;

    // ── Состояние ────────────────────────────────────────────────────────────
    /** Текущая позиция ротации. Публично для рекордера датасета. */
    public float   stateYaw, statePitch;
    private boolean seeded         = false;
    private long    lastStepNs     = 0L;
    private float   acquireEase    = 0f;
    private float   smoothStepYaw  = 0f;
    private float   smoothStepPitch = 0f;

    // ── update ───────────────────────────────────────────────────────────────

    @Override
    public RotateVector update(RotateVector cur, LivingEntity target) {
        if (cur == null || mc.player == null || target == null || !target.isAlive()) {
            seeded = false;
            return cur;
        }

        // Точка прицела
        Vec3d aimPt = aimPoint(target);
        Vec3d eye   = mc.player.getEyePos();
        double dx   = aimPt.x - eye.x;
        double dy   = aimPt.y - eye.y;
        double dz   = aimPt.z - eye.z;
        double horiz = Math.max(0.5D, Math.sqrt(dx * dx + dz * dz));
        double dist  = Math.sqrt(dx * dx + dy * dy + dz * dz);

        float centerYaw   = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float centerPitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));

        // FOV-проверка
        float camDiff = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - mc.player.getYaw()),
                centerPitch - mc.player.getPitch());
        if (camDiff > activationFov + 5f) {
            seeded = false;
            return cur;
        }

        // Инициализация
        if (!seeded) {
            stateYaw       = cur.getYaw();
            statePitch     = cur.getPitch();
            acquireEase    = 0f;
            smoothStepYaw  = 0f;
            smoothStepPitch = 0f;
            lastStepNs     = 0L;
            seeded         = true;
        }

        // Тайм-гейт
        long nowNs = System.nanoTime();
        if (nowNs - lastStepNs < STEP_NS) {
            return buildOutput();
        }
        lastStepNs = nowNs;

        // ── Геометрический шаг (ВСЕГДА ведёт к цели) ────────────────────────
        float yawErr   = MathHelper.wrapDegrees(centerYaw - stateYaw);
        float pitchErr = centerPitch - statePitch;

        // Базовый lerp — идентично NeuroRotation (Old)
        float lerp = MathHelper.clamp(0.08f + aimSpeed * 0.85f, 0.12f, 0.36f);

        // Разгон как в Old: 0→1 за ~8 шагов (~176мс)
        acquireEase = MathHelper.clamp(acquireEase + 0.12f, 0f, 1f);
        lerp *= (0.55f + 0.45f * acquireEase);

        // Гашение агрессии вплотную
        float proximity = MathHelper.clamp(((float) dist - 0.8f) / 2.2f, 0.6f, 1.0f);
        lerp *= proximity;

        float stepYaw   = yawErr   * lerp;
        float stepPitch = pitchErr * lerp;
        // Небольшая поправка от модели (как в Old)
        float[] pred = TNeuroSelfModel.predict(target);
        if (pred != null) {
            float conf = TNeuroSelfModel.getLastConfidence();
            float gain = MathHelper.clamp(aimSpeed / 0.20f, 0.45f, 1.25f) * conf;
            stepYaw   += MathHelper.clamp(pred[0] * gain, -1.5f, 1.5f) * 0.35f;
            stepPitch += MathHelper.clamp(pred[1] * gain, -1.0f, 1.0f) * 0.35f;
        }

        // Максимальный шаг
        stepYaw   = MathHelper.clamp(stepYaw,   -12f, 12f);
        stepPitch = MathHelper.clamp(stepPitch,  -8f,  8f);

        // Сглаживание шага — убирает дёрганье
        smoothStepYaw   += (stepYaw   - smoothStepYaw)   * 0.5f;
        smoothStepPitch += (stepPitch - smoothStepPitch) * 0.5f;

        stateYaw   += smoothStepYaw;
        statePitch += smoothStepPitch;

        // Кламп к хитбоксу ТОЛЬКО когда уже близко к цели.
        // Если применять всегда — при старте мгновенно телепортирует на хитбокс.
        double halfW = Math.max(
                target.getBoundingBox().maxX - target.getBoundingBox().minX,
                target.getBoundingBox().maxZ - target.getBoundingBox().minZ) * 0.5D;
        float hbYawR = (float) Math.toDegrees(Math.atan2(halfW, Math.max(0.5D, dist)));
        float errToCenter = (float) Math.hypot(
                MathHelper.wrapDegrees(centerYaw - stateYaw),
                centerPitch - statePitch);
        if (errToCenter <= hbYawR + 4.0f) {
            float[] cl = clampToHitbox(target, stateYaw, statePitch, HITBOX_SHRINK);
            stateYaw   = cl[0];
            statePitch = cl[1];
        }

        return buildOutput();
    }

    private RotateVector buildOutput() {
        float oy = stateYaw;
        float op = statePitch;
        if (System.currentTimeMillis() < missVisualUntil) {
            oy += missYawOff;
            op  = MathHelper.clamp(op + missPitchOff, -90f, 90f);
        }
        return new RotateVector(applyGCD(oy), applyGCD(MathHelper.clamp(op, -90f, 90f)));
    }

    // ── Мультипоинты ─────────────────────────────────────────────────────────

    private Vec3d aimPoint(LivingEntity target) {
        if (!mpInit) randMultiPoint(target);
        if (System.currentTimeMillis() - mpLastUpdate >= mpNextInterval) randMultiPoint(target);
        Vec3d c = target.getBoundingBox().getCenter();
        return new Vec3d(c.x + mpOffset.x,
                         c.y + AIM_HEIGHT_OFFSET + mpOffset.y,
                         c.z + mpOffset.z);
    }

    private void randMultiPoint(LivingEntity target) {
        var rnd = ThreadLocalRandom.current();
        var box = target.getBoundingBox();
        double hx = (box.maxX - box.minX) * 0.5D * MULTIPOINT_FRACTION_XZ;
        double hz = (box.maxZ - box.minZ) * 0.5D * MULTIPOINT_FRACTION_XZ;
        double hy = (box.maxY - box.minY) * 0.5D * MULTIPOINT_FRACTION_Y;
        mpOffset       = new Vec3d(rnd.nextDouble(-hx, hx),
                                   rnd.nextDouble(-hy, hy),
                                   rnd.nextDouble(-hz, hz));
        mpLastUpdate   = System.currentTimeMillis();
        mpNextInterval = rnd.nextLong(MP_INTERVAL_MIN_MS, MP_INTERVAL_MAX_MS + 1);
        mpInit         = true;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        super.onEnable();
        activeInstance  = this;
        seeded = mpInit = false;
        mpOffset = Vec3d.ZERO;
        mpLastUpdate = 0L;
        mpNextInterval = 300L;
        acquireEase     = 0f;
        smoothStepYaw   = 0f;
        smoothStepPitch = 0f;
        lastStepNs      = 0L;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        activeInstance = null;
        seeded = mpInit = false;
    }
}
