package tech.lightclient.modules.impl.combat;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.client.FastestEvent;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.entity.EventAttack;
import tech.lightclient.client.events.impl.entity.EventMove;
import tech.lightclient.client.events.impl.player.RotationEvent;
import tech.lightclient.client.events.impl.player.SprintEvent;
import tech.lightclient.client.events.impl.client.OptimizedUpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.eventbus.EventPriority;
import tech.lightclient.client.utils.math.time.NewStopWatch;
import tech.lightclient.client.utils.player.rotation.CorrectionType;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.client.utils.server.ServerTPSManager;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;
import tech.lightclient.modules.impl.combat.aura.AuraAttackHandler;
import tech.lightclient.modules.impl.combat.aura.AuraAutoDamageHandler;
import tech.lightclient.modules.impl.combat.aura.AuraChecks;
import tech.lightclient.modules.impl.combat.aura.AuraShieldBreaker;
import tech.lightclient.modules.impl.combat.aura.AuraTargetSelector;
import tech.lightclient.modules.impl.combat.aura.elytra.AuraElytraUtils;
import tech.lightclient.modules.impl.combat.aura.rotations.*;
import tech.lightclient.modules.impl.combat.aura.rotations.NeuroSelfRecorder;
import tech.lightclient.modules.impl.combat.aura.rotations.TNeuroSelfRecorder;
import tech.lightclient.modules.impl.movement.AutoSprint;
import tech.lightclient.modules.impl.player.ElytraMotion;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

@FunctionAdd(name = "Aura", alias = "Aura", category = Category.Combat, description = "Автоматически атакует сущностей вокруг вас")
public class AuraModule extends Function {
    private static final int AURA_ROTATION_PRIORITY = 5;

    @Getter
    private static AuraModule instance;
    private static final long BASE_ATTACK_TIMER_DELAY_MS = 548L;
    private static final long BASE_ATTACK_PROGRESS_DELAY_MS = 548L;
    private static final long HASTE_ATTACK_TIMER_REDUCTION_MS = 70L;
    private static final long FASTEST_ROTATION_INTERVAL_NS = 5_000_000L;

    private final ModeSetting rotationMode = new ModeSetting("Режим ротации",
            "ReallyWorld", "ReallyWorld", "Legit", "Neuro", "FT", "NeuroTest", "Снап", "Фт360");

    private final BooleanSetting showLegitFov = new BooleanSetting("Показывать круг FOV", false)
            .setVisible(() -> "Legit".equals(rotationMode.get()));
    private final SliderSetting legitFov = new SliderSetting("FOV", 45.0f, 1.0f, 360.0f, 1.0f)
            .setVisible(() -> "Legit".equals(rotationMode.get()));
    private final SliderSetting legitAimSpeed = new SliderSetting("Скорость наводки", 0.20f, 0.10f, 0.30f, 0.01f)
            .setVisible(() -> "Legit".equals(rotationMode.get()));
    private final ModeSetting legitEngine = new ModeSetting("Подротация", "Default", "Default", "Neuro")
            .setVisible(() -> "Legit".equals(rotationMode.get()));

    // ── Отдельная ротация Neuro (полная копия Legit-настроек) ────────────────
    private final BooleanSetting showNeuroFov = new BooleanSetting("Показывать круг FOV", false)
            .setVisible(() -> "Neuro".equals(rotationMode.get()));
    private final SliderSetting neuroFov = new SliderSetting("FOV", 45.0f, 1.0f, 360.0f, 1.0f)
            .setVisible(() -> "Neuro".equals(rotationMode.get()));
    private final SliderSetting neuroAimSpeed = new SliderSetting("Скорость наводки", 0.20f, 0.10f, 0.30f, 0.01f)
            .setVisible(() -> "Neuro".equals(rotationMode.get()));
    // Миссы: N из 10 ударов Neuro будут промахами (человечность).
    private final BooleanSetting neuroMisses = new BooleanSetting("Миссы", false)
            .setVisible(() -> "Neuro".equals(rotationMode.get()));
    private final SliderSetting neuroMissRate = new SliderSetting("Миссов из 10", 1.0f, 1.0f, 10.0f, 1.0f)
            .setVisible(() -> "Neuro".equals(rotationMode.get()) && neuroMisses.get());
    // Под-ротация: Old — стабильная текущая, Test — новая более обучаемая.
    private final ModeSetting neuroSubRotation = new ModeSetting("Под-ротация", "Old", "Old", "Test")
            .setVisible(() -> "Neuro".equals(rotationMode.get()));

    // ── Пресет (выбирается первым, определяет видимость остального) ──────────
    // Кастом = все настройки вручную
    // Funtime = готовые значения FTAngle-профиля по by-module.md, настройки скрыты
    public final ModeSetting neuroTestPreset = new ModeSetting("Пресет", "Кастом",
            "Кастом", "Funtime")
            .setVisible(() -> "NeuroTest".equals(rotationMode.get()));

    // helper: кастом-настройки видны только при выборе "Кастом"
    private boolean ntCustom() { return "NeuroTest".equals(rotationMode.get()) && "Кастом".equals(neuroTestPreset.get()); }

    public final ModeSetting neuroTestModel = new ModeSetting("Модель", "Резкая",
            "Резкая", "Плавная", "Точная", "Молниеносная", "Сбалансированная", "Легит", "Нейтральная", "Авто")
            .setVisible(this::ntCustom);

    public final ModeSetting neuroTestPitchMode = new ModeSetting("Наводка Pitch", "Обычная",
            "Обычная", "Фикс при ударе", "Снап", "Дрейф", "ФликВниз", "Заморозка")
            .setVisible(this::ntCustom);

    public final BooleanSetting neuroTestAssist = new BooleanSetting("Помощник прицела", true);
    public final SliderSetting neuroTestAssistYaw = new SliderSetting("Сила помощи (влево/вправо)", 0.15f, 0.0f, 1.0f, 0.01f);
    public final SliderSetting neuroTestAssistPitch = new SliderSetting("Сила помощи (вверх/вниз)", 0.20f, 0.0f, 1.0f, 0.01f);

    public final BooleanSetting neuroTestFovLimit = new BooleanSetting("Ограничение обзора (FOV)", true);
    public final SliderSetting neuroTestMaxFov = new SliderSetting("Макс. угол отклонения°", 8.0f, 1.0f, 45.0f, 0.5f);

    public final SliderSetting neuroTestMaxStep = new SliderSetting("Скорость поворота (горизонталь)", 22.0f, 1.0f, 90.0f, 1.0f);
    public final SliderSetting neuroTestMaxPitchStep = new SliderSetting("Скорость поворота (вертикаль)", 12.0f, 1.0f, 45.0f, 1.0f);
    public final SliderSetting neuroTestSmooth = new SliderSetting("Плавность движения", 0.5f, 0.0f, 1.0f, 0.05f);
    public final SliderSetting neuroTestReactionMs = new SliderSetting("Скорость реакции (мс)", 45.0f, 10.0f, 100.0f, 1.0f);

    public final SliderSetting neuroTestJitter = new SliderSetting("Дрожь прицела (человечность°)", 0.0f, 0.0f, 3.0f, 0.05f);
    public final BooleanSetting neuroTestSticky = new BooleanSetting("Прилипание к цели", false);
    public final SliderSetting neuroTestStickyMult = new SliderSetting("Сила прилипания", 2.0f, 1.0f, 4.0f, 0.1f);
    public final BooleanSetting neuroTestPredict = new BooleanSetting("Предугадывание движения цели", false);
    public final SliderSetting neuroTestPredictTicks = new SliderSetting("Сила предугадывания", 2.0f, 0.0f, 6.0f, 0.5f);

    public final SliderSetting neuroTestHitTiming = new SliderSetting("Момент удара", 0.85f, 0.0f, 1.0f, 0.05f);
    public final SliderSetting neuroTestSpeedAfterHit = new SliderSetting("Скорость наводки после удара", 0.5f, 0.0f, 1.0f, 0.05f);
    public final SliderSetting neuroTestSpeedBeforeHit = new SliderSetting("Скорость наводки перед ударом", 1.0f, 0.0f, 3.0f, 0.05f);
    public final SliderSetting neuroTestPostHitSmooth = new SliderSetting("Плавность после удара", 0.3f, 0.0f, 1.0f, 0.05f);

    public final SliderSetting snapRandomizationStrengthBeforeHit = new SliderSetting("Snap рандом до удара", 10f, 0f, 16f, 1f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final SliderSetting snapRandomizationStrengthOnHit = new SliderSetting("Snap рандом при ударе", 10f, 0f, 16f, 1f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final SliderSetting snapLerpBeforeHit = new SliderSetting("Snap плавность до удара", 1f, 0f, 1f, 0.1f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final SliderSetting snapLerpOnHit = new SliderSetting("Snap плавность при ударе", 1f, 0f, 1f, 0.1f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final SliderSetting snapFovSetting = new SliderSetting("Snap FOV", 40f, 0f, 180f, 1f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final SliderSetting snapHitTiming = new SliderSetting("Snap тайминг удара", 0.7f, 0f, 1f, 0.05f)
            .setVisible(() -> "Снап".equals(rotationMode.get()));
    public final BooleanSetting snapBypass = new BooleanSetting("Обход", false)
            .setVisible(() -> "Снап".equals(rotationMode.get()));

    public final SliderSetting funTimeFov = new SliderSetting("FunTime Snap FOV", 35.0f, 0.0f, 35.0f, 1.0f)
            .setVisible(() -> "FunTime Snap".equals(rotationMode.get()));

    public static final ModeListSetting targets = new ModeListSetting("Цели",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Игроки без брони", true),
            new BooleanSetting("Тиммейты", false),
            new BooleanSetting("Невидимые", true),
            new BooleanSetting("Боты", false),
            new BooleanSetting("Мобы", true),
            new BooleanSetting("Животные", false));

    private final BooleanSetting attackThroughBlocks = new BooleanSetting("Через блоки", false);

    public final ModeListSetting attack = new ModeListSetting("Атака",
            new BooleanSetting("Ломать щит", false),
            new BooleanSetting("Во время еды", false));
    private final BooleanSetting maximumDamage = new BooleanSetting("Макс Урон", false);
    private final StringSetting maxDamageItemName = new StringSetting("Предмет урона (d)", "Ярост")
            .setVisible(() -> maximumDamage.get());
    private final StringSetting maxHealItemName = new StringSetting("Предмет здоровья (h)", "Бестия")
            .setVisible(() -> maximumDamage.get());
    
    private final BooleanSetting autoDamage = new BooleanSetting("Авто Урон", false);
    private final StringSetting autoDamageItemName = new StringSetting("Предмет авто-урона (d)", "Ярост")
            .setVisible(() -> autoDamage.get());
    private final StringSetting autoHealItemName = new StringSetting("Предмет авто-здоровья (h)", "Бестия")
            .setVisible(() -> autoDamage.get());
    private final BooleanSetting autoDamagePauseAttack = new BooleanSetting("Пауза атаки при свапе", true)
            .setVisible(() -> autoDamage.get());
    private final BooleanSetting autoDamageSmartSwap = new BooleanSetting("Умный свап", true)
            .setVisible(() -> autoDamage.get());

    private final SliderSetting attackRange = new SliderSetting("Дистанция атаки", 3.0f, 2.0f, 6.0f, 0.1f);
    private final BooleanSetting increaseRangeInBlocks = new BooleanSetting("Увеличить дистанцию в блоках", false);
    private final SliderSetting rangeIncreaseAmount = new SliderSetting("На сколько увеличить", 1.0f, 0.1f, 3.0f, 0.1f);

    private final ModeSetting clickMode = new ModeSetting("Режим клика", "1.9", "1.9", "1.8");
    private final SliderSetting minCps = new SliderSetting("Мин. CPS", 7.0f, 1.0f, 20.0f, 0.5f);
    private final SliderSetting maxCps = new SliderSetting("Макс. CPS", 11.0f, 1.0f, 20.0f, 0.5f);
    private final BooleanSetting tpsSynchronization = new BooleanSetting("Синхронизация TPS", true);
    private final BooleanSetting fasterRotate = new BooleanSetting("Быстрая ротация", true);
    private final ModeSetting attackSpeedMode = new ModeSetting("Скорость атаки", "Медленная", "Медленная", "Быстрая", "Самая быстрая");
    private final BooleanSetting smartCrits = new BooleanSetting("Умные криты", false);
    private final ModeSetting sprintResetMode = new ModeSetting("Сброс спринта", "Обычный", "Выкл", "Обычный", "SpookyTime");
    private final ModeSetting correctionType = new ModeSetting("Тип коррекции", "Таргетированная", "Свободный", "Таргетированная");

    // Отводка: при выключении Aura оставить прицел на последней точке наводки
    // (для ВСЕХ ротаций). Выкл — прицел плавно возвращается назад, как раньше.
    private final BooleanSetting holdAimOnDisable = new BooleanSetting("Отводка", false);

    private final ModeSetting raytraceMode = new ModeSetting("Режим рейтрейса", "Всегда", "Всегда", "Умный");
    private final BooleanSetting raytraceWhenFlying = new BooleanSetting("Проверка при полёте", true);
    private final BooleanSetting raytraceWhenSwimming = new BooleanSetting("Проверка при плавании", true);
    private final BooleanSetting raytraceWhenMoving = new BooleanSetting("Проверка при движении", true);

    private final BooleanSetting autoMove = new BooleanSetting("Авто движение", false);

    @Setter
    @Getter
    public static LivingEntity target = null;
    public static LivingEntity subconsciousTarget = null;

    public volatile long lastAttackTime = 0;
    private volatile int attackCount = 0;
    private volatile int criticalAttackCount = 0;
    private volatile long sprintResetUntil = 0;
    private long lastTargetActiveTime = 0;
    private int nextCpsDelay = 0;
    @Getter
    public boolean firstAttack = true;
    private long snapBypassUntil = 0;
    private RotateVector headVector = new RotateVector(0f, 0f);
    private LivingEntity lastRotationTarget = null;
    /** Момент захвата текущей цели. Используется для плавного старта наводки в Neuro. */
    private long targetAcquiredAt = 0L;
    private volatile float cachedAttackCooldown = 1.0f;
    private volatile long cachedTimeUntilNextAttack = 0L;
    private volatile long lastFastestRotationNs = 0L;
    private boolean autoMoveActive = false;
    public boolean shouldPredict = false;

    public boolean needReset = false;
    public static volatile boolean canAttack = false;

    private final RotationController rotationController = new RotationController();
    private final AuraAttackHandler attackHandler = new AuraAttackHandler(this);

    public static NewStopWatch elytraTargetTimer = new NewStopWatch();

    public AuraModule() {
        instance = this;
        minCps.setVisible(() -> isLegacyClickMode());
        maxCps.setVisible(() -> isLegacyClickMode());

        neuroTestAssist.setVisible(this::ntCustom);
        neuroTestAssistYaw.setVisible(() -> ntCustom() && neuroTestAssist.get());
        neuroTestAssistPitch.setVisible(() -> ntCustom() && neuroTestAssist.get());
        neuroTestFovLimit.setVisible(this::ntCustom);
        neuroTestMaxFov.setVisible(() -> ntCustom() && neuroTestFovLimit.get());
        neuroTestMaxStep.setVisible(this::ntCustom);
        neuroTestMaxPitchStep.setVisible(this::ntCustom);
        neuroTestSmooth.setVisible(this::ntCustom);
        neuroTestReactionMs.setVisible(this::ntCustom);
        neuroTestJitter.setVisible(this::ntCustom);
        neuroTestSticky.setVisible(this::ntCustom);
        neuroTestStickyMult.setVisible(() -> ntCustom() && neuroTestSticky.get());
        neuroTestPredict.setVisible(this::ntCustom);
        neuroTestPredictTicks.setVisible(() -> ntCustom() && neuroTestPredict.get());
        neuroTestHitTiming.setVisible(this::ntCustom);
        neuroTestSpeedAfterHit.setVisible(this::ntCustom);
        neuroTestSpeedBeforeHit.setVisible(this::ntCustom);
        neuroTestPostHitSmooth.setVisible(this::ntCustom);

        rangeIncreaseAmount.setVisible(() -> increaseRangeInBlocks.get());
        raytraceWhenFlying.setVisible(() -> "Умный".equals(raytraceMode.get()));
        raytraceWhenSwimming.setVisible(() -> "Умный".equals(raytraceMode.get()));
        raytraceWhenMoving.setVisible(() -> "Умный".equals(raytraceMode.get()));

        addSettings(
                rotationMode, showLegitFov, legitFov, legitAimSpeed, legitEngine,
                showNeuroFov, neuroFov, neuroAimSpeed, neuroMisses, neuroMissRate, neuroSubRotation,
                // NeuroTest: сначала пресет (Кастом/Funtime), потом кастом-настройки (скрываются при пресете)
                neuroTestPreset,
                neuroTestModel, neuroTestPitchMode,
                neuroTestAssist, neuroTestAssistYaw, neuroTestAssistPitch,
                neuroTestFovLimit, neuroTestMaxFov,
                neuroTestMaxStep, neuroTestMaxPitchStep, neuroTestSmooth, neuroTestReactionMs, neuroTestJitter,
                neuroTestSticky, neuroTestStickyMult, neuroTestPredict, neuroTestPredictTicks,
                neuroTestHitTiming, neuroTestSpeedAfterHit, neuroTestSpeedBeforeHit, neuroTestPostHitSmooth,
                snapRandomizationStrengthBeforeHit,
                snapRandomizationStrengthOnHit, snapLerpBeforeHit, snapLerpOnHit, snapFovSetting, snapHitTiming, snapBypass,
                funTimeFov,
                targets,
                attackThroughBlocks, attack, attackRange, increaseRangeInBlocks, rangeIncreaseAmount, clickMode, minCps, maxCps, tpsSynchronization,
                fasterRotate, attackSpeedMode, smartCrits, sprintResetMode, correctionType, holdAimOnDisable, raytraceMode, raytraceWhenFlying, raytraceWhenSwimming,
                raytraceWhenMoving, autoMove,
                // Макс Урон и Авто Урон - в самом низу настроек Aura
                maximumDamage, maxDamageItemName, maxHealItemName,
                autoDamage, autoDamageItemName, autoHealItemName, autoDamagePauseAttack, autoDamageSmartSwap
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        target = null;
        subconsciousTarget = null;
        firstAttack = true;
        lastRotationTarget = null;
        if (mc.player != null) {
            headVector = new RotateVector(mc.player.getYaw(), mc.player.getPitch());
        }

        sprintResetUntil = 0;
        cachedAttackCooldown = 1.0f;
        cachedTimeUntilNextAttack = 0L;
        lastFastestRotationNs = 0L;
        updateAttackCooldownCache();

        SnapRotation.snapIdle = false;
        targetAcquiredAt = 0L;
        rotationController.setRotationMode(rotationMode.get());
        rotationController.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        attackHandler.cancelPendingAttack(mc);
        boolean hadTargetRecently = (System.currentTimeMillis() - lastTargetActiveTime) <= 500L;
        if (holdAimOnDisable.get()) {
            // Отводка: телепортируем реальный прицел игрока на ТЕКУЩУЮ позицию
            // silent-ротации (visualHeadYaw/Pitch) — то есть туда, куда ротация
            // фактически дошла, а не на желаемый target (headVector).
            // headVector — это target следующего тика, он может быть впереди
            // visualHeadYaw, что и вызывало небольшой телепорт вперёд.
            // Для Legit реальный прицел уже стоит на месте (двигает камеру
            // напрямую), поэтому достаточно просто снять задачу ротации.
            if ("Legit".equals(rotationMode.get())) {
                RotationTask.remove("aura");
            } else {
                // Используем visualHeadYaw/Pitch — фактическая текущая позиция ротации.
                // Если RotationTask не был активен — fallback на headVector.
                float holdYaw   = RotationTask.isRotating()
                        ? RotationTask.visualHeadYaw
                        : headVector.getYaw();
                float holdPitch = RotationTask.isRotating()
                        ? RotationTask.visualHeadPitch
                        : headVector.getPitch();
                RotationTask.holdAimAtLast(holdYaw, holdPitch);
            }
        } else if ((hadTargetRecently || RotationTask.isRotating()) && RotationTask.rotationPriority <= AURA_ROTATION_PRIORITY) {
            // Немедленная плавная отводка: прицел начинает возвращаться сразу,
            // без задержки 800мс. resetDelayMs=0 = начать RESET немедленно.
            RotationTask.rotationState    = RotationTask.RotationState.RESET;
            RotationTask.returnStartTime  = System.currentTimeMillis() - 800L; // пропускаем задержку
            RotationTask.resetDelayMs     = 0L;
            RotationTask.rotationPriority = 0;
            RotationTask.inactiveMs       = 0L;
            RotationTask.needSmoothReset  = true;
        } else {
            RotationTask.remove("aura");
        }
        target = null;
        subconsciousTarget = null;
        firstAttack = true;
        lastRotationTarget = null;
        if (mc.player != null) {
            headVector = new RotateVector(mc.player.getYaw(), mc.player.getPitch());
        }

        sprintResetUntil = 0;
        cachedAttackCooldown = 1.0f;
        cachedTimeUntilNextAttack = 0L;
        lastFastestRotationNs = 0L;

        // Сброс авто-урона + возврат предмета здоровья в левую руку
        AuraAutoDamageHandler.restoreHealItem(mc, this);
        AuraAutoDamageHandler.reset();

        rotationController.onDisable();
    }

    @EventHandler
    public void onRenderHud(EventRender.Screen.Hud event) {
        boolean legit = "Legit".equals(rotationMode.get());
        boolean neuro = "Neuro".equals(rotationMode.get());
        boolean neuroLegit = legit && "Neuro".equals(legitEngine.get());

        boolean showCircle;
        float configuredFov;
        if (neuro) {
            showCircle = showNeuroFov.get();
            configuredFov = getNeuroFov();
        } else if (legit) {
            showCircle = showLegitFov.get() || neuroLegit;
            configuredFov = getLegitFov();
        } else {
            return;
        }

        if (!showCircle || mc.player == null || !mc.options.getPerspective().isFirstPerson()) {
            return;
        }

        float gameFov = mc.gameRenderer.getFov(
                mc.getEntityRenderDispatcher().camera,
                mc.getRenderTickCounter().getTickProgress(true),
                true
        );
        if (gameFov <= 0.0f) return;

        float centerX = event.getViewportWidth() * 0.5f;
        float centerY = event.getViewportHeight() * 0.5f;
        // Угол 90° и больше уже выходит за пределы экрана. Рисуем границу
        // по диагонали viewport, а проверка целей продолжает работать до 360°.
        float radius = configuredFov >= 89.0f
                ? (float) Math.hypot(centerX, centerY)
                : (float) (centerY * Math.tan(Math.toRadians(configuredFov))
                / Math.tan(Math.toRadians(gameFov * 0.5f)));

        event.getRenderer().circleOutline(centerX, centerY, radius, 0.0f, 1.0f,
                ClientColors.ICON.getRGB(), 1.0f);
    }

    @EventHandler
    public void onUpdate(TickEvent event) {
        if (!isState()) return;
        if (mc.player == null || mc.world == null) return;

        updateAttackCooldownCache();
        attackHandler.maintain(mc, target);

        // Проверка автосвапа
        if (isAutoDamageEnabled() && target != null && target.isAlive()) {
            AuraAutoDamageHandler.checkAndSwap(mc, this);
        }

        if (target != null && target.isAlive()) {
            if (target.handSwinging) elytraTargetTimer.reset();

            lastTargetActiveTime = System.currentTimeMillis();

            if (!shouldUseFastestRotation()) {
                applyAuraRotationMode(false);
            }

            canAttack = canAttack() && canAttackTarget();
            needReset = System.currentTimeMillis() - lastAttackTime > 395;

            if ("Быстрая".equals(attackSpeedMode.get())
                    && (target != null && target.isAlive() || attackHandler.hasPendingAttack())) {
                attackHandler.tryAttackCurrentTarget(mc, target);
            }

        } else {
            firstAttack = true;
            if ("Быстрая".equals(attackSpeedMode.get()) && attackHandler.hasPendingAttack()) {
                attackHandler.tryAttackCurrentTarget(mc, null);
            }
            if ("FT".equals(rotationMode.get()) || "FuntimeNew".equals(rotationMode.get())
                    || "FunTime Snap".equals(rotationMode.get())
                    || isFt360ReturnActive()) {
                applyAuraRotationMode(false);
            } else {
                lastRotationTarget = null;
                headVector.setYaw(mc.player.getYaw());
                headVector.setPitch(mc.player.getPitch());
                startSmoothAuraReset();
            }
        }
    }

    @EventHandler
    public void onOptimizedUpdate(OptimizedUpdateEvent event) {
        if (nullCheck()) return;

        AntiBotSystem.scanWorld(mc.world);

        if (!isCurrentTargetValid()) {
            AuraTargetSelector.TargetResult result = AuraTargetSelector.selectTargets(mc, this);
            target = result.mainTarget;
            subconsciousTarget = result.subconsciousTarget;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;

        if (!mc.player.isAlive()) this.setState(false);

        if (!nullCheck()) {
            if (LightClient.getFunctionManager().getElytraMotion().isState() && target != null && mc.player.isGliding()) {
                Vec3d targetPos = target.getBoundingBox().getCenter();
                Vec3d glidingVelocity = AuraElytraUtils.calcGlidingVelocityPredicted(target, target.getVelocity(), 0.15f);
                Vec3d predictedTargetPos = targetPos.add(glidingVelocity);
                double distanceToPredictedPos = mc.player.getEntityPos().distanceTo(predictedTargetPos);

                shouldPredict = distanceToPredictedPos > (double) ElytraMotion.elytraMotionDistance.get();
            }

            if (isState() && "Медленная".equals(attackSpeedMode.get())
                    && (target != null && target.isAlive() || attackHandler.hasPendingAttack())) {
                attackHandler.tryAttackCurrentTarget(mc, target);
            }
        }

        updateAutoMove();
    }

    private void updateAutoMove() {
        if (!isState()) return;
        if (mc.player == null || !autoMove.get()) {
            if (autoMoveActive) {
                stopAutoMove();
                autoMoveActive = false;
            }
            return;
        }

        if (target != null && target.isAlive()) {
            handleAutoMove();
            autoMoveActive = true;
        } else if (autoMoveActive) {
            stopAutoMove();
            autoMoveActive = false;
        }
    }

    @EventHandler
    public void onFastest(FastestEvent event) {
        if (!isState() || mc.player == null) return;

        attackHandler.maintain(mc, target);

        // Проверка и выполнение автосвапа
        if (isAutoDamageEnabled() && target != null && target.isAlive()) {
            AuraAutoDamageHandler.checkAndSwap(mc, this);
        }

        if (!shouldRunFastestRotation()) {
            return;
        }

        if (shouldUseFastestRotation()
                && (target != null && target.isAlive() || isFt360ReturnActive())) {
            applyAuraRotationMode(true);
        }

        if (target != null && target.isAlive()) {
            canAttack = canAttack() && canAttackTarget();
            needReset = System.currentTimeMillis() - lastAttackTime > 395;
        }

        if ("Самая быстрая".equals(attackSpeedMode.get())
                && (System.currentTimeMillis() - lastAttackTime >= 50L)
                && (target != null && target.isAlive() || attackHandler.hasPendingAttack())) {
            attackHandler.tryAttackCurrentTarget(mc, target);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSprint(SprintEvent event) {
        if (!isState() || mc.player == null) return;
        if (attackHandler.isMaximumDamageSprintPaused()) {
            event.cancel();
            event.setSprinting(false);
            mc.options.sprintKey.setPressed(false);
            return;
        }
        if ("Выкл".equals(sprintResetMode.get())) return;

        if (mc.player.isSubmergedInWater() || mc.player.isSwimming()) {
            return;
        }

        if ("SpookyTime".equals(sprintResetMode.get()) && target != null) {
            event.cancel();
            event.setSprinting(false);
            return;
        }

        if ("Обычный".equals(sprintResetMode.get())) {
            setSprinting(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(EventMove e) {
        if (nullCheck())
            return;

        if (LightClient.getFunctionManager().getElytraMotion().isState() && target != null && mc.player.isGliding()) {
            if (!shouldPredict) {
                e.cancel();
                e.setMovement(Vec3d.ZERO);
            }
        }
    }

    @EventHandler
    public void onAttackSwing(EventAttack.Swing e) {
        if (nullCheck()) return;
        if (target == null) return;

        if ("NeuroTest".equals(rotationMode.get())) {
            RotateModel model = rotationController.getCurrentRotation();
            if (model instanceof NeuroTestRotation nt) {
                nt.notifyHit();
            } else {
                NeuroTestRotation.randomizeHitTiming();
            }
        }

    }
    private void setSprinting(SprintEvent event) {
        boolean hardCollision = (mc.player.horizontalCollision && !mc.player.collidedSoftly) || AuraChecks.hasAnySideCollision(mc);

        if (hardCollision) {
            event.setSprinting(false);
        }

        float cooldown = 0.8f;

        if ((firstAttack && !mc.player.isOnGround() && mc.player.getVelocity().getY() < -0.073 && subconsciousTarget != null) && getAttackCooldown() > cooldown) {
            event.setSprinting(false);
        }

        if ((!mc.player.isOnGround()) && target != null && getAttackCooldown() > cooldown) {
            event.setSprinting(false);
        }

        if (!hardCollision && mc.player.forwardSpeed > 0 && !mc.player.isSprinting()) {
            mc.options.sprintKey.setPressed(true);
        }
    }


    private boolean canAttack() {
        long now = System.currentTimeMillis();

        if (now - lastAttackTime < getAttackTimerDelay()) return false;
        
        // Проверка авто-урона: если идет свап и включена пауза атаки
        if (isAutoDamageEnabled() && !AuraAutoDamageHandler.canAttack(this)) {
            return false;
        }

        if (isLegacyClickMode()) {
            if (now - lastAttackTime < nextCpsDelay) return false;
            double cps = randomCps();
            cps += ThreadLocalRandom.current().nextInt(-4, 2);
            cps = Math.max(1.0, cps);
            nextCpsDelay = (int) scaleByTps(Math.max(1L, Math.round(1000.0 / cps)));
            return true;
        } else {
            float cooldownThreshold = AuraChecks.isHoldingCombatWeapon(mc)
                    ? getTpsScaledCooldownThreshold(0.85f)
                    : 0.85f;
            if (AuraChecks.isHoldingCombatWeapon(mc) && cachedAttackCooldown < cooldownThreshold) return false;
            if (now - lastAttackTime < scaleByTps(50L)) return false;

            if (cachedTimeUntilNextAttack > 0) return false;

            return true;
        }
    }

    private long getAttackTimerDelay() {
        long base = Math.max(0L, BASE_ATTACK_TIMER_DELAY_MS - getHasteAttackTimerReduction());
        // Neuro: небольшая человечная вариативность кулдауна между ударами.
        if ("Neuro".equals(rotationMode.get())) {
            base += neuroAttackJitterMs;
        }
        return base;
    }

    // Случайная добавка к кулдауну атаки Neuro (обновляется после каждого удара).
    private volatile long neuroAttackJitterMs = 0L;

    private long getAttackProgressDelay() {
        return scaleByTps(Math.max(1L, BASE_ATTACK_PROGRESS_DELAY_MS - getHasteAttackTimerReduction()));
    }

    private long getHasteAttackTimerReduction() {
        if (mc.player == null) {
            return 0L;
        }

        var haste = mc.player.getStatusEffect(StatusEffects.HASTE);
        if (haste == null) {
            return 0L;
        }

        return (long) (haste.getAmplifier() + 1) * HASTE_ATTACK_TIMER_REDUCTION_MS;
    }


    private boolean canAttackTarget() {
        if (target == null) return false;

        if (!attack.getByName("Во время еды").get() && mc.player.isUsingItem() && !AuraShieldBreaker.isUsingShield(mc)) {
            return false;
        }

        return true;
    }


    private double randomCps() {
        double min = minCps.get();
        double max = maxCps.get();
        if (min > max) {
            double t = min;
            min = max;
            max = t;
        }
        if (Math.abs(max - min) < 0.0001) {
            return min;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean isLegacyClickMode() {
        return instance != null && "1.8".equals(instance.clickMode.get());
    }

    public static float getNormalizedAttackCooldown() {
        if (instance == null || instance.mc.player == null) {
            return 1.0f;
        }
        instance.updateAttackCooldownCache();
        float cd = instance.cachedAttackCooldown;
        if (isLegacyClickMode()) {
            float avgCps = (instance.minCps.get() + instance.maxCps.get()) / 2.0f;
            if (avgCps <= 0.1f) return 1.0f;
            float intervalMs = 1000.0f / avgCps;
            long hasteReduction = 0L;
            var haste = instance.mc.player.getStatusEffect(StatusEffects.HASTE);
            if (haste != null) {
                hasteReduction = (long) (haste.getAmplifier() + 1) * HASTE_ATTACK_TIMER_REDUCTION_MS;
            }
            long progressDelay = Math.max(1L, BASE_ATTACK_PROGRESS_DELAY_MS - hasteReduction);
            float scale = (float) progressDelay / intervalMs;
            return MathHelper.clamp(cd * scale, 0.0f, 1.0f);
        }
        return MathHelper.clamp(cd, 0.0f, 1.0f);
    }

    private long scaleByTps(long ms) {
        if (!tpsSynchronization.get()) {
            return ms;
        }
        float factor = ServerTPSManager.getInstance().getPreciseTpsFactor();
        return Math.max(1L, Math.round(ms * factor));
    }

    /**
     * При низком TPS серверу нужно больше тиков для восстановления кулдауна.
     * Повышаем порог: например, при 15 TPS и базовом пороге 0.85
     * эффективный порог = 0.85 * (20/15) = 1.13 → ограниченный до 0.95.
     * Это гарантирует, что мы не атакуем раньше, чем сервер готов.
     */
    private float getTpsScaledCooldownThreshold(float baseThreshold) {
        if (!tpsSynchronization.get()) {
            return baseThreshold;
        }
        float tps = ServerTPSManager.getInstance().getPreciseTps();
        if (tps >= 19.8f) {
            return baseThreshold;
        }
        float factor = 20.0f / Math.max(tps, 10.0f);
        return Math.min(0.95f, baseThreshold * factor);
    }

    public boolean canDealCrit() {
        if (mc.player.isOnGround() && smartCrits.get() && !mc.options.jumpKey.isPressed()) return true;
        return mc.player.fallDistance > getCritThreshold();
    }

    /**
     * Maximum-damage swaps are allowed only for a real airborne critical.
     * Smart-crits intentionally allow ordinary ground attacks, which must not
     * leave the damage item in the offhand while waiting for an attack.
     */
    public boolean canDealMaximumDamageCrit() {
        return mc.player != null
                && !mc.player.isOnGround()
                && canDealCrit();
    }

    public float getCritThreshold() {
        return 0;
    }

    private boolean shouldBypassCritCheck() {
        return mc.player.isTouchingWater()
                || mc.player.isInLava()
                || mc.player.isClimbing()
                || AuraChecks.isInCobweb(mc)
                || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)
                || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || mc.player.getAbilities().flying;
    }


    private void updateAttackCooldownCache() {
        long now = System.currentTimeMillis();

        if (mc.player == null) {
            cachedAttackCooldown = 1.0f;
            cachedTimeUntilNextAttack = 0L;
            return;
        }

        if (AuraChecks.isHoldingCombatWeapon(mc)) {
            float cooldown = mc.player.getAttackCooldownProgress(0.5f);
            cachedAttackCooldown = MathHelper.clamp(cooldown, 0.0f, 1.0f);
            if (cachedAttackCooldown >= 0.83f) {
                cachedTimeUntilNextAttack = 0L;
            } else {
                long rawTime = (long) ((0.83f - cachedAttackCooldown) * 1000.0f);
                cachedTimeUntilNextAttack = scaleByTps(rawTime);
            }
            return;
        }

        long timeSinceLastAttack = now - lastAttackTime;
        long progressDelay = getAttackProgressDelay();
        cachedAttackCooldown = Math.min(1.0f, (float) timeSinceLastAttack / progressDelay);
        cachedTimeUntilNextAttack = Math.max(0L, progressDelay - timeSinceLastAttack);
    }

    private boolean shouldRunFastestRotation() {
        long now = System.nanoTime();
        if (now - lastFastestRotationNs < FASTEST_ROTATION_INTERVAL_NS) {
            return false;
        }

        lastFastestRotationNs = now;
        return true;
    }


    private void forceStopSprintForReset() {
        if (mc.player == null) {
            return;
        }

        mc.options.sprintKey.setPressed(false);
        if (mc.player.isSprinting()) {
            mc.player.setSprinting(false);
        }
    }

    private void applyAuraRotationMode(boolean fastestContext) {
        boolean funTimeIdle = ("FT".equals(rotationMode.get()) || "FunTime Snap".equals(rotationMode.get()))
                && (target == null || !target.isAlive());
        // Фт360 после потери цели сам доводит скрытый угол до камеры, поэтому
        // ротация продолжает обновляться, пока возврат не завершится.
        boolean ft360Idle = isFt360ReturnActive() && (target == null || !target.isAlive());
        if (mc.player == null || (!funTimeIdle && !ft360Idle && (target == null || !target.isAlive()))) {
            lastRotationTarget = null;
            startSmoothAuraReset();
            return;
        }

        // Legit only keeps a silent rotation task while the target remains in
        // its 45-degree camera FOV.  Removing it here is important: otherwise
        // the last spoofed head rotation would remain active after the target
        // leaves the circle and appear to be stuck.
        if (isLegitBasedRotation() && !isTargetInsideLegitFov(target)) {
            RotationTask.remove("aura");
            lastRotationTarget = null;
            headVector.setYaw(mc.player.getYaw());
            headVector.setPitch(mc.player.getPitch());
            return;
        }

        if ("Снап".equals(rotationMode.get()) && SnapRotation.snapIdle) {
            boolean targetChanged = lastRotationTarget != target;
            float yawDiff = Math.abs(MathHelper.wrapDegrees(mc.player.getYaw() - headVector.getYaw()));
            float pitchDiff = Math.abs(mc.player.getPitch() - headVector.getPitch());
            if (!targetChanged && yawDiff < 5f && pitchDiff < 5f) {
                return;
            }
            headVector.setYaw(mc.player.getYaw());
            headVector.setPitch(mc.player.getPitch());
            SnapRotation.snapIdle = false;
        }

        if ("Neuro".equals(rotationMode.get())) {
            // Под-ротация: Old = NeuroRotation, Test = TNeuroRotation
            boolean testSub = "Test".equals(neuroSubRotation.get());
            String neuroMode = testSub ? "NeuroTestV2" : "Neuro";

            // Переключаем внутренний режим RotationController если под-ротация изменилась
            if (!rotationController.getCurrentMode().equals(neuroMode)) {
                rotationController.setRotationMode(neuroMode);
            }

            // Параметры для Old под-ротации
            NeuroRotation.missesEnabled = neuroMisses.get();
            NeuroRotation.missesOutOf10 = Math.round(neuroMissRate.get());

            // Параметры для Test под-ротации
            TNeuroRotation.aimSpeed = neuroAimSpeed.get();
            TNeuroRotation.activationFov = getNeuroFov();
            TNeuroRotation.missesEnabled = neuroMisses.get();
            TNeuroRotation.missesOutOf10 = Math.round(neuroMissRate.get());
        }

        if ("NeuroTest".equals(rotationMode.get())) {

            if ("Кастом".equals(neuroTestPreset.get())) {
                NeuroTestRotation.activePreset = "";
                NeuroTestRotation.funtimePresetEnabled = false;
                // Кастом: все параметры из слайдеров
                NeuroTestRotation.selectedModel = mapNeuroTestModel(neuroTestModel.get());
                NeuroTestRotation.assistEnabled = neuroTestAssist.get();
                NeuroTestRotation.assistYaw = neuroTestAssistYaw.get();
                NeuroTestRotation.assistPitch = neuroTestAssistPitch.get();
                NeuroTestRotation.fovLimitEnabled = neuroTestFovLimit.get();
                NeuroTestRotation.maxFov = neuroTestMaxFov.get();
                NeuroTestRotation.cfgMaxStep = neuroTestMaxStep.get() * 3f;
                NeuroTestRotation.cfgMaxPitchStep = neuroTestMaxPitchStep.get();
                NeuroTestRotation.cfgSmooth = neuroTestSmooth.get();
                NeuroTestRotation.cfgStepNs = (long) (neuroTestReactionMs.get() * 0_100_000.0f);
                NeuroTestRotation.jitterDeg = neuroTestJitter.get();
                NeuroTestRotation.stickyEnabled = neuroTestSticky.get();
                NeuroTestRotation.stickyMult = neuroTestStickyMult.get();
                NeuroTestRotation.predictEnabled = neuroTestPredict.get();
                NeuroTestRotation.predictTicks = neuroTestPredictTicks.get();
                NeuroTestRotation.cfgHitTiming = neuroTestHitTiming.get();
                NeuroTestRotation.cfgSpeedAfterHit = neuroTestSpeedAfterHit.get();
                NeuroTestRotation.cfgSpeedBeforeHit = neuroTestSpeedBeforeHit.get();
                NeuroTestRotation.cfgPostHitSmooth = neuroTestPostHitSmooth.get();
                NeuroTestRotation.pitchMode = neuroTestPitchMode.get();
            } else if ("Funtime".equals(neuroTestPreset.get())) {
                // ── Пресет Funtime: FTAngle-профиль из by-module.md ──────────────
                // attack: cap=130°, lerp=0.85
                // idle: shake yaw rand(18..28)*sin(ms/60), pitch rand(6..16)*cos(ms/60)
                // лимит idle: 0° первые 535мс после удара, потом 45°
                // каждый 86-й хит в окне 250мс → флик вниз 240мс
                NeuroTestRotation.activePreset = "";
                NeuroTestRotation.funtimePresetEnabled = true;
                NeuroTestRotation.selectedModel = "precise";
                // Наводка — ФликВниз (обеспечивает редкий pitch-флик вниз)
                NeuroTestRotation.pitchMode = "Заморозка";
                // Assist выключен — FTAngle сам управляет траекторией
                NeuroTestRotation.assistEnabled = true;
                NeuroTestRotation.fovLimitEnabled = true;
                // Джиттер только в idle (обрабатывается внутри updateFuntime)
                NeuroTestRotation.assistYaw = 0.15f;
                NeuroTestRotation.assistPitch = 0.20f;
                NeuroTestRotation.maxFov = 8.0f;
                NeuroTestRotation.cfgMaxStep = 7.0f;
                NeuroTestRotation.cfgMaxPitchStep = 12.0f;
                NeuroTestRotation.cfgSmooth = 0.75f;
                NeuroTestRotation.cfgStepNs = 45_000_000L;
                NeuroTestRotation.jitterDeg = 0.35f;
                NeuroTestRotation.stickyEnabled = false;
                NeuroTestRotation.predictEnabled = false;
                // Тайминг удара — 0.85 ± рандом (как в оригинале)
                NeuroTestRotation.cfgHitTiming = 0.85f;
                NeuroTestRotation.cfgSpeedAfterHit = 0.50f;
                NeuroTestRotation.cfgSpeedBeforeHit = 1.0f;
                NeuroTestRotation.cfgPostHitSmooth = 0.30f;
                // FTAngle hardcoded values
                NeuroTestRotation.ftCap = 130f;
                NeuroTestRotation.ftLerpAttack = 0.85f;
                NeuroTestRotation.ftShakeYawMin = 18f;
                NeuroTestRotation.ftShakeYawMax = 28f;
                NeuroTestRotation.ftShakePitchMin = 6f;
                NeuroTestRotation.ftShakePitchMax = 16f;
                NeuroTestRotation.ftShakeYawPeriod = 60000f;
                NeuroTestRotation.ftShakePitchPeriod = 60000f;
                NeuroTestRotation.ftPostHitFreezMs = 535L;
                NeuroTestRotation.ftIdleCapBeforeFreeze = 0f;
                NeuroTestRotation.ftIdleCapAfterFreeze = 45f;
                NeuroTestRotation.ftFlickEveryNHits = Integer.MAX_VALUE;
                NeuroTestRotation.ftFlickWindowMs = 250L;
                NeuroTestRotation.ftFlickDurationMs = 240L;
            }
        }

        if ("Снап".equals(rotationMode.get())) {
            SnapRotation.snapRandomizationStrengthBeforeHit = snapRandomizationStrengthBeforeHit.get();
            SnapRotation.snapRandomizationStrengthOnHit = snapRandomizationStrengthOnHit.get();
            SnapRotation.snapLerpBeforeHit = snapLerpBeforeHit.get();
            SnapRotation.snapLerpOnHit = snapLerpOnHit.get();
            SnapRotation.snapFovSetting = snapFovSetting.get();
            SnapRotation.snapHitTiming = snapHitTiming.get();
            SnapRotation.snapBypassEnabled = snapBypass.get();
            SnapRotation.snapBypassUntil = snapBypassUntil;
        }

        String currentMode = rotationMode.get();
        // Для Neuro режима RotationController уже переключён на нужную под-ротацию
        // (Old="Neuro" или Test="NeuroTestV2") внутри блока выше — не перезаписываем.
        if (!"Neuro".equals(currentMode) && !rotationController.getCurrentMode().equals(currentMode)) {
            rotationController.setRotationMode(currentMode);
        }

        boolean targetReacquired = (lastRotationTarget != target);
        if (targetReacquired) {
            // Обновляем headVector от реального положения прицела
            float seedYaw   = RotationTask.isRotating() ? RotationTask.visualHeadYaw   : mc.player.getYaw();
            float seedPitch = RotationTask.isRotating() ? RotationTask.visualHeadPitch : mc.player.getPitch();
            headVector.setYaw(seedYaw);
            headVector.setPitch(seedPitch);

            // Для не-Neuro режимов сбрасываем модель ротации
            if (!"Neuro".equals(rotationMode.get()) && !RotationTask.isRotating()) {
                RotateModel activeModel = rotationController.getCurrentRotation();
                if (activeModel != null) {
                    activeModel.onDisable();
                    activeModel.onEnable();
                }
            }

            lastRotationTarget  = target;
            targetAcquiredAt    = System.currentTimeMillis();
        }

        RotateVector baseRotation;
        boolean isNeuroForBase = "Neuro".equals(rotationMode.get());
        if (targetReacquired) {
            // При новой цели — стартуем с реального положения прицела
            float seedYaw   = RotationTask.isRotating() ? RotationTask.visualHeadYaw   : mc.player.getYaw();
            float seedPitch = RotationTask.isRotating() ? RotationTask.visualHeadPitch : mc.player.getPitch();
            baseRotation = new RotateVector(seedYaw, seedPitch);
        } else if (isNeuroForBase) {
            // Neuro: всегда идём от реального visualHeadYaw — не от headVector.
            // headVector обновляется каждый тик и может быть далеко впереди
            // visualHeadYaw, что приводит к мгновенному рывку.
            baseRotation = RotationTask.isRotating()
                    ? new RotateVector(RotationTask.visualHeadYaw, RotationTask.visualHeadPitch)
                    : new RotateVector(mc.player.getYaw(), mc.player.getPitch());
        } else if (fastestContext) {
            baseRotation = new RotateVector(headVector.getYaw(), headVector.getPitch());
        } else {
            baseRotation = RotationTask.isRotating()
                    ? new RotateVector(RotationTask.visualHeadYaw, RotationTask.visualHeadPitch)
                    : new RotateVector(headVector.getYaw(), headVector.getPitch());
        }

        RotateVector newRotation = rotationController.update(baseRotation, target);

        // Legit owns the visible client rotation itself. A silent Aura task
        // would overwrite that rotation during sync and lock the head.
        // Neuro uses the normal silent-rotation path (does NOT move the real
        // crosshair), so it must fall through to RotationTask below.
        if ("Legit".equals(rotationMode.get())) {
            RotationTask.remove("aura");
            headVector.setYaw(mc.player.getYaw());
            headVector.setPitch(mc.player.getPitch());
            return;
        }

        if (newRotation != null) {
            float continuousYaw = baseRotation.getYaw()
                    + MathHelper.wrapDegrees(newRotation.getYaw() - baseRotation.getYaw());
            newRotation = new RotateVector(continuousYaw, newRotation.getPitch());
            headVector.setYaw(newRotation.getYaw());
            headVector.setPitch(newRotation.getPitch());

            RotationTask.create("aura", 100);

            float aimYawSpeed   = Float.MAX_VALUE;
            float aimPitchSpeed = Float.MAX_VALUE;

            float returnYawSpeed = 220f;
            float returnPitchSpeed = 180f;
            RotationTask.setTargetRotation(
                    newRotation.getYaw(),
                    newRotation.getPitch(),
                    aimYawSpeed,
                    aimPitchSpeed,
                    returnYawSpeed,
                    returnPitchSpeed,
                    0,
                    AURA_ROTATION_PRIORITY,
                    -1L
            );
            RotationTask.needSmoothReset = true;
        }
    }

    private boolean isTargetInsideLegitFov(LivingEntity entity) {
        if (entity == null || mc.player == null) return false;
        Vec3d delta = entity.getEyePos().subtract(mc.player.getCameraPosVec(1.0F));
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizontal));
        float yawDiff = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        float pitchDiff = targetPitch - mc.player.getPitch();
        float fov = "Neuro".equals(rotationMode.get()) ? getNeuroFov() : getLegitFov();
        return Math.hypot(yawDiff, pitchDiff) <= fov;
    }

    private boolean isLegitBasedRotation() {
        return "Legit".equals(rotationMode.get()) || "Neuro".equals(rotationMode.get());
    }

    public float getLegitFov() {
        return MathHelper.clamp(legitFov.get(), 1.0f, 360.0f);
    }

    public float getLegitAimSpeed() {
        return MathHelper.clamp(legitAimSpeed.get(), 0.10f, 0.30f);
    }

    public float getNeuroFov() {
        return MathHelper.clamp(neuroFov.get(), 1.0f, 360.0f);
    }

    public float getNeuroAimSpeed() {
        return MathHelper.clamp(neuroAimSpeed.get(), 0.10f, 0.30f);
    }

    /** Включена ли под-ротация Neuro внутри Legit. */
    public static boolean isLegitNeuroEngine() {
        return instance != null && instance.legitEngine.is("Neuro");
    }

    /** Команда .lneuro play переключает под-ротацию на Neuro. */
    public void selectLegitNeuroEngine() {
        legitEngine.set("Neuro");
    }

    private String mapNeuroTestModel(String label) {
        switch (label) {
            case "Плавная":          return "smooth";
            case "Точная":           return "precise";
            case "Молниеносная":     return "lightning";
            case "Сбалансированная": return "balanced";
            case "Легит":            return "legit";
            case "Нейтральная":      return "neutral";
            case "Авто":             return "auto";
            default:                 return "sharp";
        }
    }

    private boolean shouldUseFastestRotation() {
        // Winlend updates Legit from its high-frequency render/game-update
        // hook, not only from the 20 TPS tick. Without this branch Light calls
        // applySuperSmooth roughly 20 times per second, making headYaw (which
        // follows with the original 0.08 factor) painfully slow.
        if (isLegitBasedRotation()) return true;
        if ("FT".equals(rotationMode.get()) || "FuntimeNew".equals(rotationMode.get())
                || "FunTime Snap".equals(rotationMode.get()) || "SpookyTime".equals(rotationMode.get())) {
            return true;
        }
        if ("Neuro".equals(rotationMode.get())) return true;
        if ("NeuroTest".equals(rotationMode.get())) return true;
        // Мотор Фт360 работает на 240 Гц и обязан получать шаги чаще тика.
        if (isFt360Mode()) return true;
        return fasterRotate.get();
    }

    private boolean isFt360Mode() {
        return "Фт360".equals(rotationMode.get());
    }

    /** Идёт ли возврат скрытого угла Фт360 после потери цели. */
    private boolean isFt360ReturnActive() {
        return isFt360Mode()
                && rotationController.getCurrentRotation() instanceof Ft360Rotation ft360
                && ft360.isActive();
    }

    private void startSmoothAuraReset() {
        if (RotationTask.rotationPriority > AURA_ROTATION_PRIORITY) {
            RotationTask.remove("aura");
            return;
        }

        if (!RotationTask.isRotating()) {
            RotationTask.remove("aura");
            return;
        }
        if (RotationTask.rotationState == RotationTask.RotationState.RESET) {
            return;
        }

        RotationTask.rotationState = RotationTask.RotationState.RESET;
        RotationTask.returnStartTime = System.currentTimeMillis() - 460;
        RotationTask.rotationPriority = 0;
        RotationTask.inactiveMs = 0L;
        RotationTask.needSmoothReset = true;
    }

    @EventHandler
    public void onRotation(RotationEvent event) {
        if (mc.player == null || !isState()) {
            return;
        }

        if (target != null && target.isAlive()) {
            CorrectionType type = "Таргетированная".equals(correctionType.get())
                    ? CorrectionType.TARGET
                    : CorrectionType.FREE;

            if (type == CorrectionType.TARGET) {
                event.rotate(headVector.getYaw(), headVector.getPitch(), type, target, RotationTask.calculateTargetYawForMovement(target));
            } else {
                event.rotate(headVector.getYaw(), headVector.getPitch(), type, target);
            }
            return;
        }

        event.rotate(mc.player.getYaw(), mc.player.getPitch(), CorrectionType.FREE);
    }

    public static boolean isSmartCritsEnabled() {
        return instance != null && instance.smartCrits.get();
    }

    public boolean isAttackThroughBlocksEnabled() {
        return attackThroughBlocks.get();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (event.isReceive()) {
            attackHandler.onPacket(event);
        }
    }

    public boolean isMaximumDamageEnabled() {
        return maximumDamage.get();
    }

    public String getMaxDamageItemName() {
        return maxDamageItemName.get();
    }

    public void setMaxDamageItemName(String name) {
        maxDamageItemName.set(name);
    }

    public String getMaxHealItemName() {
        return maxHealItemName.get();
    }

    public void setMaxHealItemName(String name) {
        maxHealItemName.set(name);
    }
    
    public boolean isAutoDamageEnabled() {
        return autoDamage.get();
    }

    public String getAutoDamageItemName() {
        return autoDamageItemName.get();
    }

    public void setAutoDamageItemName(String name) {
        autoDamageItemName.set(name);
    }

    public String getAutoHealItemName() {
        return autoHealItemName.get();
    }

    public void setAutoHealItemName(String name) {
        autoHealItemName.set(name);
    }
    
    public boolean shouldPauseAttackDuringAutoSwap() {
        return autoDamagePauseAttack.get();
    }
    
    public boolean shouldUseSmartAutoSwap() {
        return autoDamageSmartSwap.get();
    }

    public double getEffectiveAttackRange() {
        double baseRange = attackRange.get();
        if (increaseRangeInBlocks.get() && AuraChecks.isPlayerInBlocks(mc)) {
            baseRange = baseRange + rangeIncreaseAmount.get();
        }
        return baseRange - 0.38f;
    }

    public double getEffectiveSearchRange() {
        double baseRange = attackRange.get();
        if (increaseRangeInBlocks.get() && AuraChecks.isPlayerInBlocks(mc)) {
            baseRange = baseRange + rangeIncreaseAmount.get();
        }

        if (mc.player.isGliding()) {
            baseRange = 35;
        }

        return baseRange + 0.25f;
    }


    public static float getAttackCooldown() {
        if (instance == null || instance.mc.player == null) {
            return 1.0f;
        }
        instance.updateAttackCooldownCache();

        return MathHelper.clamp(instance.cachedAttackCooldown, 0.0f, 1.0f);
    }

    private float getRaytraceYaw() {
        if (RotationTask.isRotating()) {
            return RotationTask.visualHeadYaw;
        }
        return mc.player != null ? mc.player.getYaw() : 0.0f;
    }

    private float getRaytracePitch() {
        if (RotationTask.isRotating()) {
            return RotationTask.visualHeadPitch;
        }
        return mc.player != null ? mc.player.getPitch() : 0.0f;
    }

    public boolean canAttackNow() {
        return canAttack();
    }

    public long getTimeUntilNextAttackMs() {
        updateAttackCooldownCache();
        return cachedTimeUntilNextAttack;
    }

    public boolean canAttackTargetNow() {
        return canAttackTarget();
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getCriticalAttackCount() {
        return criticalAttackCount;
    }

    public float getFunTimeFov() {
        return MathHelper.clamp(funTimeFov.get(), 0.0f, 35.0f);
    }

    public boolean shouldBypassCritCheckForAttack() {
        return shouldBypassCritCheck();
    }

    public String getRaytraceMode() {
        return raytraceMode.get();
    }

    public boolean isRaytraceWhenFlyingEnabled() {
        return raytraceWhenFlying.get();
    }

    public boolean isRaytraceWhenSwimmingEnabled() {
        return raytraceWhenSwimming.get();
    }

    public boolean isRaytraceWhenMovingEnabled() {
        return raytraceWhenMoving.get();
    }

    public boolean isSnapBypassEnabled() {
        return snapBypass.get();
    }

    public String getRotationModeLabel() {
        return rotationMode.get();
    }

    public float getRaytraceYawForAttack() {
        return getRaytraceYaw();
    }

    public float getRaytracePitchForAttack() {
        return getRaytracePitch();
    }

    public boolean shouldUseExactSnapOnAttack() {
        return "Снап".equals(rotationMode.get()) && snapHitTiming.get() >= 0.999f;
    }

    // ── Миссы для Neuro: N промахов из каждых 10 ударов ──────────────────────
    private final java.util.ArrayDeque<Boolean> neuroMissWindow = new java.util.ArrayDeque<>();

    /**
     * Решает, должен ли ТЕКУЩИЙ удар Neuro быть промахом. Держит скользящее
     * окно из 10 последних ударов и не даёт превысить заданное число миссов,
     * распределяя их псевдослучайно (человечно).
     */
    public boolean shouldNeuroMissThisAttack() {
        if (!"Neuro".equals(rotationMode.get()) || !neuroMisses.get()) {
            return false;
        }
        int target = Math.round(neuroMissRate.get()); // сколько миссов из 10
        if (target <= 0) return false;

        int missesInWindow = 0;
        for (boolean b : neuroMissWindow) if (b) missesInWindow++;

        int remainingSlots = 10 - neuroMissWindow.size();
        int missNeeded = target - missesInWindow;

        boolean miss;
        if (missNeeded <= 0) {
            miss = false;
        } else if (missNeeded >= remainingSlots + 1) {
            // нужно израсходовать все оставшиеся слоты на миссы
            miss = true;
        } else {
            // вероятность промаха среди оставшихся слотов
            float p = (float) missNeeded / Math.max(1, remainingSlots);
            miss = ThreadLocalRandom.current().nextFloat() < p;
        }

        neuroMissWindow.addLast(miss);
        while (neuroMissWindow.size() >= 10) {
            neuroMissWindow.pollFirst();
        }
        return miss;
    }


    public void onAttackPerformedByHandler(long attackTime, boolean critical) {
        lastAttackTime = attackTime;
        attackCount++;
        if (critical) {
            criticalAttackCount++;
        }
        // Фт360 отсчитывает интервал жестов по подтверждённым ударам, поэтому
        // событие приходит только после фактически отправленной атаки.
        if (rotationController.getCurrentRotation() instanceof Ft360Rotation ft360) {
            ft360.onAttack();
        }
        if ("Снап".equals(rotationMode.get()) && snapBypass.get()) {
            snapBypassUntil = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(100L, 171L);
            SnapRotation.snapIdle = false;
        }
        cachedAttackCooldown = 0.0f;
        cachedTimeUntilNextAttack = getAttackProgressDelay();
        firstAttack = false;

        // Neuro: обновляем случайную добавку к кулдауну (0..90 мс) — так удары
        // идут с немного разной скоростью, а не идеально ровно (человечность).
        if ("Neuro".equals(rotationMode.get())) {
            neuroAttackJitterMs = ThreadLocalRandom.current().nextLong(0L, 90L);
        }

        AutoSprint autoSprint = AutoSprint.getInstance();

        if (!"Выкл".equals(sprintResetMode.get())) {
            int duration = 50;
            sprintResetUntil = lastAttackTime + duration;
            if (autoSprint != null && autoSprint.isState()) {
                autoSprint.blockSprintUntil(sprintResetUntil);
            }
            forceStopSprintForReset();
        }

        if (autoSprint != null && autoSprint.isState()) {
            autoSprint.onAttackPerformed();
        }

    }

    private boolean isCurrentTargetValid() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        if (mc.player == null) return false;
        if (target instanceof PlayerEntity player && LightClient.getFriendStorage().isFriend(player.getName().getString()))
            return false;

        double safeRange = getEffectiveAttackRange() + 0.5;
        if (mc.player.distanceTo(target) > safeRange) return false;

        return AuraTargetSelector.isValid(mc, this, target, safeRange * safeRange,
                new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()));
    }

    private void handleAutoMove() {
        if (mc.player == null || target == null) return;

        mc.options.forwardKey.setPressed(true);

        if (getAttackCooldown() > 0.1f) {
            mc.options.jumpKey.setPressed(true);
        }

    }

    private void stopAutoMove() {
        if (mc.options == null) return;
        mc.options.forwardKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
    }

}
