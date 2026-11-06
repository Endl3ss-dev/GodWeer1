package tech.lightclient.modules.impl.combat.aura.rotations;

import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.RotationRecorderModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

/**
 * NeuroTest — расширенная ротация с поддержкой профилей серверов.
 * <p>
 * Модели: sharp / smooth / precise / lightning / balanced
 * Наводка Pitch: Обычная / Фикс при ударе / Снап / Дрейф / ФликВниз / Заморозка
 * Пресеты: Funtime (FTAngle: cap=130°, lerp=0.85, shake в idle, редкий флик на 86-й хит)
 */
public class NeuroTestRotation extends RotateModel {

    public static volatile String selectedModel = "sharp";

    public static volatile boolean assistEnabled = true;
    public static volatile float assistYaw = 0.15f;
    public static volatile float assistPitch = 0.20f;

    public static volatile boolean fovLimitEnabled = true;
    public static volatile float maxFov = 8.0f;

    public static volatile float cfgMaxStep = 22.0f;
    public static volatile float cfgMaxPitchStep = 12.0f;

    public static volatile float cfgSmooth = 0.5f;
    public static volatile long cfgStepNs = 10_000_000L;

    public static volatile float jitterDeg = 0.0f;

    public static volatile boolean stickyEnabled = false;
    public static volatile float stickyMult = 2.0f;

    public static volatile boolean predictEnabled = false;
    public static volatile float predictTicks = 2.0f;

    public static volatile float antiPredictTicks = 0.5f;

    public static volatile float aimHeightOffset = -0.38f;
    public static volatile boolean multiPointEnabled = true;
    public static volatile float multiPointRadiusX = 0.3f;
    public static volatile float multiPointRadiusY = 0.3f;
    public static volatile float multiPointRadiusZ = 0.3f;
    public static volatile long multiPointIntervalMinMs = 150L;
    public static volatile long multiPointIntervalMaxMs = 400L;

    public static volatile float tweenSpeed = 0.75f;

    public static volatile float firstAttackTweenSpeed = 0.6f;

    public static volatile float firstAttackMaxStep = 14.0f;
    public static volatile float firstAttackMaxPitchStep = 7.0f;

    public static volatile String pitchMode = "Обычная";

    public static volatile float cfgHitTiming = 0.85f;
    public static volatile float cfgSpeedAfterHit = 0.5f;
    public static volatile float cfgSpeedBeforeHit = 1.0f;
    public static volatile float cfgPostHitSmooth = 0.3f;

    // ── Пресет-специфичные настройки ────────────────────────────────────────
    /** Активный пресет. "" = ручной режим, "funtime" = FTAngle-профиль */
    public static volatile String activePreset = "";
    public static volatile boolean funtimePresetEnabled = false;

    // Funtime-пресет параметры (FTAngle из by-module.md)
    /** Лимит cap по одной оси = abs(axisΔ/total) * ftCap */
    public static volatile float ftCap = 130f;
    /** lerp-коэффициент в атаке (ближе к 1 = резче) */
    public static volatile float ftLerpAttack = 0.85f;
    /** Амплитуда тряски yaw в idle: rand(min..max) */
    public static volatile float ftShakeYawMin = 18f;
    public static volatile float ftShakeYawMax = 28f;
    /** Амплитуда тряски pitch в idle */
    public static volatile float ftShakePitchMin = 6f;
    public static volatile float ftShakePitchMax = 16f;
    /** Период тряски (делитель мс для sin/cos — больше = медленнее) */
    public static volatile float ftShakeYawPeriod = 60000f;
    public static volatile float ftShakePitchPeriod = 60000f;
    /** После удара — заморозка доворота на N мс */
    public static volatile long ftPostHitFreezMs = 535L;
    /** Лимит доворота в idle до истечения ftPostHitFreezMs (обычно 0) */
    public static volatile float ftIdleCapBeforeFreeze = 0f;
    /** Лимит доворота в idle после истечения ftPostHitFreezMs */
    public static volatile float ftIdleCapAfterFreeze = 45f;
    /** Каждый N-й хит в окне ftFlickWindowMs → флик вниз */
    public static volatile int ftFlickEveryNHits = 86;
    public static volatile long ftFlickWindowMs = 250L;
    /** Длительность флика вниз (мс) */
    public static volatile long ftFlickDurationMs = 240L;

    // ── Internal state ───────────────────────────────────────────────────────
    private static float currentHitTiming = 0.85f;

    // Funtime internal state
    private int ftHitCounter = 0;
    private long ftLastHitTime = 0L;
    private long ftFlickEndTime = 0L;   // >0 пока идёт флик вниз
    private long ftLastAttackTime = 0L; // время последнего удара (для заморозки)
    private int ftLastGameTick = -1;
    private long ftAirSwingUntil = 0L;
    private float ftAirSwingYawOffset = 35.0f;

    private static final int T = RotationFeatures.WINDOW;
    private static final int F = RotationFeatures.FEATURES;

    private static final float WARMUP_GAIN = 0.75f;

    private final Deque<float[]> window = new ArrayDeque<>();

    private float prevDYaw;
    private float prevDPitch;

    private float smoothedSpeedMult = 1.0f;

    private long lastStepNs = 0L;

    private float stateYaw;
    private float statePitch;

    private float targetYaw;
    private float targetPitch;
    private boolean hasTarget = false;

    private boolean seeded = false;
    private int warmupLeft = 0;
    private String activeModel = null;

    private java.util.UUID lastTargetUuid = null;
    private boolean targetTransitioning = false;

    private float testPitchFrozen = 0f;
    private boolean testPitchFrozenInit = false;

    private Vec3d multiPointOffset = Vec3d.ZERO;
    private long lastMultiPointUpdate = 0L;
    private long nextMultiPointInterval = 250L;
    private boolean multiPointInitialized = false;

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        window.clear();
    }

    private void reset() {
        window.clear();

        prevDYaw = 0f;
        prevDPitch = 0f;

        smoothedSpeedMult = cfgSpeedBeforeHit;
        currentHitTiming = cfgHitTiming;

        lastStepNs = 0L;

        hasTarget = false;

        seeded = false;
        warmupLeft = 1;
        activeModel = selectedModel;

        lastTargetUuid = null;
        targetTransitioning = false;

        testPitchFrozen = 0f;
        testPitchFrozenInit = false;

        multiPointOffset = Vec3d.ZERO;
        lastMultiPointUpdate = 0L;
        nextMultiPointInterval = 250L;
        multiPointInitialized = false;

        // Funtime state reset
        ftHitCounter = 0;
        ftLastHitTime = 0L;
        ftFlickEndTime = 0L;
        ftLastAttackTime = 0L;
        ftLastGameTick = -1;
        ftAirSwingUntil = 0L;
        ftAirSwingYawOffset = 35.0f;
    }
    @Override
    public RotateVector update(RotateVector current, LivingEntity target) {
        if (target == null || current == null) {
            return current;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        java.util.UUID curUuid = target.getUuid();
        if (!curUuid.equals(lastTargetUuid)) {
            targetTransitioning = true;
            lastTargetUuid = curUuid;
        }

        if (RotationRecorderModule.isRecording()) {
            if (mc.player == null) {
                return current;
            }
            return new RotateVector(mc.player.getYaw(), mc.player.getPitch());
        }

        // ── Если активен пресет Funtime — используем FTAngle логику целиком ──
        if ("funtime".equals(activePreset) || funtimePresetEnabled) {
            return updateFuntime(current, target);
        }

        String key = selectedModel;

        if (activeModel == null || !activeModel.equals(key)) {
            reset();
        }

        if (!NeuroModel.isReady(key)) {
            return calcAim(aimPoint(target));
        }

        float curYaw = current.getYaw();
        float curPitch = current.getPitch();

        if (!seeded) {
            if (mc.player == null) {
                return current;
            }
            targetYaw = curYaw;
            targetPitch = curPitch;
            hasTarget = true;
            seeded = true;
            window.clear();
        }

        long now = System.nanoTime();
        boolean doStep = now - lastStepNs >= cfgStepNs;

        if (doStep) {
            lastStepNs = now;

            stateYaw = curYaw;
            statePitch = curPitch;

            RotateVector aim = calcAim(aimPoint(target));

            float[] frame = RotationFeatures.extract(stateYaw, statePitch, target, prevDYaw, prevDPitch);

            if (window.isEmpty()) {
                for (int k = 0; k < T; k++) {
                    window.addLast(frame);
                }
            } else {
                window.addLast(frame);
                while (window.size() > T) {
                    window.removeFirst();
                }
            }

            float[][][] input = new float[1][T][F];
            int i = 0;
            for (float[] fr : window) {
                input[0][i++] = fr;
            }

            float[] out = NeuroModel.predict(key, input);
            if (out != null) {
                float dYaw = out[0];
                float dPitch = out[1];

                if (warmupLeft > 0) {
                    dYaw *= WARMUP_GAIN;
                    dPitch *= WARMUP_GAIN;
                    warmupLeft--;
                }

                float stepSmooth = (Math.abs(dYaw) < 5.0f || Math.abs(dPitch) < 5.0f) ? 0.06f : 0.2f;
                dYaw = lerp(prevDYaw, dYaw, stepSmooth);
                dPitch = lerp(prevDPitch, dPitch, stepSmooth);

                float cd = AuraModule.getNormalizedAttackCooldown();
                float targetSpeedMult = cd < currentHitTiming ? cfgSpeedAfterHit : cfgSpeedBeforeHit;
                smoothedSpeedMult = MathHelper.lerp(0.12f, smoothedSpeedMult, targetSpeedMult);

                boolean smoothApproach = AuraModule.getInstance().firstAttack || targetTransitioning;
                float maxStep = (smoothApproach && firstAttackMaxStep > 0f ? firstAttackMaxStep * 2f : cfgMaxStep) * smoothedSpeedMult;
                float maxPitchStep = (smoothApproach && firstAttackMaxPitchStep > 0f ? firstAttackMaxPitchStep * 2f : cfgMaxPitchStep) * smoothedSpeedMult;

                dYaw = MathHelper.clamp(dYaw, -maxStep, maxStep);
                dPitch = MathHelper.clamp(dPitch, -maxPitchStep, maxPitchStep);

                prevDYaw = dYaw;
                prevDPitch = dPitch;

                stateYaw += dYaw;
                float neuroPitch = statePitch + dPitch;

                float ay = assistEnabled ? assistYaw * smoothedSpeedMult : 0f;
                float ap = assistEnabled ? assistPitch * smoothedSpeedMult : 0f;

                if (stickyEnabled && mc.player != null && mc.player.distanceTo(target) <= 3.5f) {
                    ay = clamp01(ay * stickyMult);
                    ap = clamp01(ap * stickyMult);
                }

                if (ay > 0f) {
                    RotateVector centerAim = calcAim(target.getBoundingBox().getCenter());
                    float yawErr = wrap(centerAim.getYaw() - stateYaw);
                    stateYaw += yawErr * ay;
                }
                if (ap > 0f) {
                    neuroPitch = lerp(neuroPitch, aim.getPitch(), ap);
                }

                if (fovLimitEnabled) {
                    float yawErr = wrap(aim.getYaw() - stateYaw);
                    if (Math.abs(yawErr) > maxFov) {
                        float clampedYaw = aim.getYaw() - Math.signum(yawErr) * maxFov;
                        stateYaw = lerp(stateYaw, clampedYaw, 0.25f);
                    }
                    float pitchErr = aim.getPitch() - neuroPitch;
                    if (Math.abs(pitchErr) > maxFov) {
                        float clampedPitch = aim.getPitch() - Math.signum(pitchErr) * maxFov;
                        neuroPitch = lerp(neuroPitch, clampedPitch, 0.25f);
                    }
                }

                if (jitterDeg > 0.0f) {
                    stateYaw += (float) ThreadLocalRandom.current().nextDouble(-jitterDeg, jitterDeg);
                    neuroPitch += (float) ThreadLocalRandom.current().nextDouble(-jitterDeg, jitterDeg);
                }

                targetYaw = stateYaw;

                float distance = mc.player.distanceTo(target);
                float pitchOffset = calcPitchOffset(distance);

                targetPitch = MathHelper.clamp(neuroPitch + pitchOffset, -90f, 90f);
                hasTarget = true;
            }
        }

        if (!hasTarget) {
            return new RotateVector(curYaw, curPitch);
        }

        boolean smoothApproach = AuraModule.getInstance().firstAttack || targetTransitioning;
        float t = smoothApproach ? Math.min(cfgSmooth * 1.2f, 0.7f) : cfgSmooth;
        t *= smoothedSpeedMult;

        float yawDelta = wrap(targetYaw - curYaw);
        float pitchDelta = targetPitch - curPitch;

        if (targetTransitioning
                && Math.abs(yawDelta) < 5.0f
                && Math.abs(pitchDelta) < 5.0f) {
            targetTransitioning = false;
        }
        float outYaw = curYaw + yawDelta * t;

        float yawSpeed = Math.abs(yawDelta);
        float pitchSpeed = Math.abs(pitchDelta);
        float minPitchFactor = 0.35f;
        float pitchFactor = pitchSpeed > 0.001f ? Math.clamp(yawSpeed / pitchSpeed, minPitchFactor, 1.0f) : 0.0f;

        float cd = AuraModule.getNormalizedAttackCooldown();
        float pitchBoost = cd > 0.7f ? 1.0f + (cd - 0.7f) / 0.3f * 1.8f : 1.0f;
        pitchFactor = Math.min(1.0f, pitchFactor * pitchBoost);

        float outPitch = curPitch + pitchDelta * t * pitchFactor;

        outPitch = applyPitchMode(outPitch, curPitch, pitchDelta, t, pitchFactor, cd);

        outYaw = applyGCD(outYaw);
        outPitch = applyGCD(outPitch);

        return new RotateVector(outYaw, MathHelper.clamp(outPitch, -90f, 90f));
    }

    /**
     * Смещение pitch в зависимости от выбранной Наводки Pitch и дистанции.
     * Режимы: Обычная / Фикс при ударе / Снап / Дрейф / ФликВниз / Заморозка
     */
    private float calcPitchOffset(float distance) {
        switch (pitchMode) {
            case "Дрейф":
                // Плавный sway-offset, меняется со временем
                return (float) (Math.sin(System.currentTimeMillis() / 800.0) * (distance < 2f ? 0.7f : 0.3f));
            case "ФликВниз":
                // Нейтральный offset — флик обрабатывается в applyPitchMode
                return distance < 2f ? -0.8f : -0.1f;
            case "Заморозка":
                // Нейтральный — заморозка обрабатывается в applyPitchMode
                return distance < 2f ? -0.8f : -0.2f;
            case "Снап":
                return distance < 2f ? -0.7f : -0.1f;
            case "Фикс при ударе":
                return -0.5f;
            case "Обычная":
            default:
                return distance < 2f ? -0.8f : -0.1f;
        }
    }

    /**
     * Дополнительная обработка pitch по режиму Наводки Pitch.
     * Режимы: Обычная / Фикс при ударе / Снап / Дрейф / ФликВниз / Заморозка
     */
    private float applyPitchMode(float outPitch, float curPitch, float pitchDelta,
                                 float t, float pitchFactor, float cd) {
        MinecraftClient mc = MinecraftClient.getInstance();

        switch (pitchMode) {
            case "Фикс при ударе": {
                // Фиксируем pitch в момент, когда cd > 0.65 (перед ударом)
                if (mc.player != null) {
                    if (cd > 0.65f) {
                        testPitchFrozen = outPitch;
                        testPitchFrozenInit = true;
                    } else {
                        if (!testPitchFrozenInit || mc.player.getVelocity().getY() < -0.0743) {
                            testPitchFrozen = outPitch;
                            testPitchFrozenInit = true;
                        }
                        outPitch = testPitchFrozen;
                    }
                }
                break;
            }
            case "Снап": {
                // Быстрый snap с x2 скоростью
                outPitch = curPitch + pitchDelta * Math.min(1.0f, t * 2.0f) * pitchFactor;
                break;
            }
            case "Дрейф": {
                // Sway: медленный sin-дрейф поверх нормальной ротации
                // calcPitchOffset уже добавил sway-offset, здесь только замедляем lerp
                outPitch = MathHelper.lerp(0.25f, curPitch, outPitch);
                break;
            }
            case "ФликВниз": {
                // Каждые ftFlickEveryNHits хитов в окне — флик вниз на 240мс
                // Запускается через notifyHit(). Здесь применяем если флик активен.
                long now = System.currentTimeMillis();
                if (ftFlickEndTime > 0 && now < ftFlickEndTime) {
                    float progress = 1f - (float)(ftFlickEndTime - now) / ftFlickDurationMs;
                    float flickPitch = MathHelper.lerp(0.55f, curPitch, curPitch - 90f);
                    outPitch = MathHelper.lerp(progress, outPitch, flickPitch);
                }
                break;
            }
            case "Заморозка": {
                // SpookyTime-стиль: при потере цели или cd < 0.3 — hold текущий pitch
                if (mc.player != null && cd < 0.3f) {
                    if (!testPitchFrozenInit) {
                        testPitchFrozen = curPitch;
                        testPitchFrozenInit = true;
                    }
                    // eased-lerp обратно к замороженному значению
                    outPitch = MathHelper.lerp(0.15f, outPitch, testPitchFrozen);
                } else {
                    testPitchFrozenInit = false;
                }
                break;
            }
            default:
                break;
        }

        return outPitch;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FTAngle (Funtime Preset) — per-tick логика по by-module.md
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Полная реализация FTAngle-профиля по by-module.md:
     *   attack: cap = abs(axisΔ/total)*130°; next = lerp(0.85, cur, cur+clamp(Δ,±cap))
     *   idle:   yaw += rand(18..28)*sin(ms/60000), pitch += rand(6..16)*cos(ms/60000)
     *           лимит доворота = 0° первые 535мс после удара, потом 45°
     *   каждый 86-й хит в окне 250мс → pitch флик вниз 240мс
     * Финал GCD-снап.
     */
    private RotateVector updateFuntime(RotateVector current, LivingEntity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return current;

        boolean attack = AuraModule.canAttack;
        long now = System.currentTimeMillis();

        if (funtimePresetEnabled && mc.player.age != ftLastGameTick) {
            ftLastGameTick = mc.player.age;
            if (mc.player.age > 0 && mc.player.age % 4 == 0) {
                RotateVector tickAim = calcAim(aimPoint(target));
                float targetDelta = wrap(tickAim.getYaw() - current.getYaw());
                ftAirSwingYawOffset = targetDelta >= 0.0f ? -35.0f : 35.0f;
                ftAirSwingUntil = now + 50L;
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }

        if (funtimePresetEnabled && now < ftAirSwingUntil) {
            return new RotateVector(
                    applyGCD(current.getYaw() + ftAirSwingYawOffset),
                    applyGCD(MathHelper.clamp(current.getPitch(), -90.0f, 90.0f))
            );
        }

        RotateVector aim = calcAim(aimPoint(target));
        float curYaw = current.getYaw();
        float curPitch = current.getPitch();

        float yawDelta = wrap(aim.getYaw() - curYaw);
        float pitchDelta = aim.getPitch() - curPitch;
        float total = (float) Math.sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta);
        if (total < 0.001f) total = 0.001f;

        float outYaw;
        float outPitch;

        if (attack) {
            // ── Атака: per-axis cap=130°, lerp=0.85 ──────────────────────────
            // cap по каждой оси пропорционально вкладу оси в total
            float yawCap  = (Math.abs(yawDelta)  / total) * ftCap;
            float pitchCap = (Math.abs(pitchDelta) / total) * ftCap;

            float appliedYaw   = MathHelper.clamp(yawDelta,   -yawCap,   yawCap);
            float appliedPitch = MathHelper.clamp(pitchDelta, -pitchCap, pitchCap);

            // lerp(0.85, cur, cur+applied) = cur + 0.85 * applied
            outYaw   = curYaw   + appliedYaw   * ftLerpAttack;
            outPitch = curPitch + appliedPitch * ftLerpAttack;

            ftLastAttackTime = now;

        } else {
            // ── Idle: сначала мягкий доворот к цели, потом добавляем тряску ──
            // Лимит доворота: 0° первые 535мс после удара, потом 45°
            float idleCap = (now - ftLastAttackTime) < ftPostHitFreezMs
                    ? ftIdleCapBeforeFreeze
                    : ftIdleCapAfterFreeze;

            // Мягкий доворот в пределах idleCap (lerp 0.15 — медленно, не резко)
            float cappedYaw   = idleCap > 0f ? MathHelper.clamp(yawDelta,   -idleCap, idleCap)   : 0f;
            float cappedPitch = idleCap > 0f ? MathHelper.clamp(pitchDelta, -idleCap, idleCap) : 0f;

            outYaw   = curYaw   + cappedYaw   * 0.15f;
            outPitch = curPitch + cappedPitch * 0.15f;

            // Тряска: rand амплитуда * sin/cos(ms / 60000) — медленная синусоида
            // Делим ms на 60000 чтобы полный период ≈ 6 сек (не дёргаться каждый тик)
            double tSec = now / ftShakeYawPeriod; // ftShakeYawPeriod = 60000 по умолчанию
            double tSecP = now / ftShakePitchPeriod;

            float shakeAmpYaw   = ThreadLocalRandom.current().nextFloat(ftShakeYawMin,   ftShakeYawMax);
            float shakeAmpPitch = ThreadLocalRandom.current().nextFloat(ftShakePitchMin, ftShakePitchMax);

            // Амплитуда небольшая — это лишь «человеческий» дрейф, не резкая тряска
            float shakeScale = 0.05f; // масштабируем чтобы не было рывков
            outYaw   += shakeAmpYaw   * (float) Math.sin(tSec)  * shakeScale;
            outPitch += shakeAmpPitch * (float) Math.cos(tSecP) * shakeScale;
        }

        // ── Флик вниз (каждые 86 хитов) ──────────────────────────────────────
        if (ftFlickEndTime > 0 && now < ftFlickEndTime) {
            // pitch = lerp(0.55, cur, cur-90) — плавно уходит вниз
            float progress = (float)(ftFlickDurationMs - (ftFlickEndTime - now)) / ftFlickDurationMs;
            progress = MathHelper.clamp(progress, 0f, 1f);
            float flickTarget = MathHelper.lerp(0.55f, curPitch, curPitch - 90f);
            outPitch = MathHelper.lerp(progress, outPitch, flickTarget);
        }

        outYaw   = applyGCD(outYaw);
        outPitch = applyGCD(MathHelper.clamp(outPitch, -90f, 90f));

        return new RotateVector(outYaw, outPitch);
    }

    /**
     * Вызывается при каждом swing (как randomizeHitTiming).
     * Считает хиты в окне ftFlickWindowMs и запускает флик.
     */
    public void notifyHit() {
        long now = System.currentTimeMillis();

        // Обновляем hit-timing рандом
        float base = cfgHitTiming;
        float rnd = ThreadLocalRandom.current().nextFloat(-0.07f, 0.07f);
        currentHitTiming = MathHelper.clamp(base + rnd, 0.0f, 1.0f);

        if (!funtimePresetEnabled && !"ФликВниз".equals(pitchMode)) return;

        // Сбрасываем счётчик если окно устарело
        if (now - ftLastHitTime > ftFlickWindowMs) {
            ftHitCounter = 0;
        }
        ftLastHitTime = now;
        ftHitCounter++;

        int threshold = funtimePresetEnabled ? ftFlickEveryNHits : 50;
        if (ftHitCounter >= threshold) {
            ftHitCounter = 0;
            ftFlickEndTime = now + ftFlickDurationMs;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════

    private Vec3d aimPoint(LivingEntity target) {
        if (multiPointEnabled && !multiPointInitialized) {
            randMultiPoint();
        }

        if (multiPointEnabled) {
            long now = System.currentTimeMillis();
            if (now - lastMultiPointUpdate >= nextMultiPointInterval) {
                randMultiPoint();
            }
        }

        Vec3d bbCenter = target.getBoundingBox().getCenter();
        Vec3d point = multiPointEnabled
                ? new Vec3d(bbCenter.x + multiPointOffset.x, bbCenter.y + aimHeightOffset + multiPointOffset.y, bbCenter.z + multiPointOffset.z)
                : new Vec3d(bbCenter.x, bbCenter.y + aimHeightOffset, bbCenter.z);

        Vec3d v = target.getVelocity();

        if (predictEnabled) {
            point = point.add(v.x * predictTicks, v.y * predictTicks, v.z * predictTicks);
        }

        if (antiPredictTicks > 0f) {
            point = point.subtract(v.x * antiPredictTicks, v.y * antiPredictTicks, v.z * antiPredictTicks);
        }

        return point;
    }

    private void randMultiPoint() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        multiPointOffset = new Vec3d(
                rnd.nextDouble(-multiPointRadiusX, multiPointRadiusX),
                rnd.nextDouble(-multiPointRadiusY, multiPointRadiusY),
                rnd.nextDouble(-multiPointRadiusZ, multiPointRadiusZ)
        );
        lastMultiPointUpdate = System.currentTimeMillis();
        nextMultiPointInterval = rnd.nextLong(multiPointIntervalMinMs, multiPointIntervalMaxMs + 1);
        multiPointInitialized = true;
    }

    private static float wrap(float yaw) {
        yaw %= 360.0f;
        if (yaw >= 180.0f) {
            yaw -= 360.0f;
        }
        if (yaw < -180.0f) {
            yaw += 360.0f;
        }
        return yaw;
    }

    private static float clamp01(float v) {
        return MathHelper.clamp(v, 0.0f, 1.0f);
    }

    public static void randomizeHitTiming() {
        float base = cfgHitTiming;
        float rnd = ThreadLocalRandom.current().nextFloat(-0.07f, 0.07f);
        currentHitTiming = MathHelper.clamp(base + rnd, 0.0f, 1.0f);
    }

    public static boolean isFuntimeAirSwingTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        return funtimePresetEnabled && client.player != null
                && client.player.age > 0 && client.player.age % 4 == 0;
    }
}
