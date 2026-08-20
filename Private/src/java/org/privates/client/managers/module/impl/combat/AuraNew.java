package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.material.Material;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.auraelement.IRotationElement;
import org.privates.client.managers.module.impl.combat.auraelement.impl.*;
import org.privates.client.managers.module.impl.movement.Freezing;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.math.PerfectDelay;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.ViaUtil;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.rotation.AuraUtil;
import org.privates.client.utils.rotation.HumanRotationTrainer;
import org.privates.client.utils.rotation.RayTraceUtil;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.common.impl.taskript.Script;
import org.privates.lib.util.time.StopWatch;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Category1
@Customize
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Attack Aura", description = "Бьет игроков, сосет за щеку, ебет пасты,соснет хуй без вашего соглашение", category = Category.COMBAT)
public class AuraNew extends Module {

    private int recentHitTime;
    public boolean snap;
    private final int imminentHitTime = 1;
    private Vector3d lastTargetPos = Vector3d.ZERO;
    public Vector3d lastTargetVelocity;
    public int fd;
    private float adaptSmoothness = 0.5f;
    private int hitCounter = 0;
    private long shakeStartTime = 0;
    public long lastAttackTime = 0L;
    public boolean canCrit;
    public int attackCount;
    public long combatStartTime;
    public float lastYawDiff;
    public float lastPitchDiff;
    public Vector3d lastAimPoint;
    public int currentBodyPart;
    public final List<Vector3d> bodyPartOffsets = new ArrayList<>();
    public Vector3d dynamicAimPoint;
    public static final float MAX_PITCH = 90.0F;
    public static final float MIN_PITCH = -90.0F;
    private static final float COOLDOWN_THRESHOLD = 0.93F;
    public static final float CRIT_COOLDOWN_THRESHOLD = 0.5F;

    public long pauseEndTime;
    public long lastReactionTime;
    public long lastLookUpTime = 0;
    public long nextLookUpDelay = ThreadLocalRandom.current().nextLong(90000, 180000);
    public boolean isLookingUp = false;
    public long lookUpStartTime = 0;
    public int lookUpDuration = 0;
    public boolean isFlicking;
    public long targetAcquiredTime;
    public boolean funtimeSnapActive;
    public long funtimeSnapStartTime;
    public long funtimeSnapDuration;
    public Vector2f funtimeSnapOriginalRotation;
    public boolean funtimeShouldAttack;
    public boolean funtimeSnapCompleted;
    public boolean isSnapping;
    public long snapStartTime;
    public Vector2f snapPrevRotation;
    public boolean smotionSnapActive;
    public long smotionSnapEnd;
    public int snapMixMode;
    public long nextSnapMixChange;
    public boolean snapMixInSnap;
    public int snapMixTicks;
    public long funtimeSnapHoldDuration = 0L;
    public long snapMixSnapEnd;
    public float currentRotationMomentumYaw;
    public float currentRotationMomentumPitch;
    public long lastTargetSwitchTime;
    public LivingEntity previousTarget;
    public float fatigueMultiplier = 1.0f;
    public long lastPatternChangeTime;
    public int currentRotationPattern;
    public float swayPhase;
    public float breathingPhase;
    public boolean isLocked;
    public final LinkedList<Vector2f> rotationHistoryList = new LinkedList<>();
    public long correctionStartTime;
    public boolean isCorrectingOvershoot;
    public int horizontalBiasDirection = 1;
    private float pizdeckolbasitLagosa = ThreadLocalRandom.current().nextFloat(50, 70);
    private long lastFrequencyUpdate = System.currentTimeMillis();
    private float combatAggressiveness = 0.5f;
    private boolean shouldSnapBack = false;

    private TargetPoint currentTargetPoint = TargetPoint.HEAD;
    private int attackCounter = 0;
    private final StopWatch pointSwitchTimer = new StopWatch();

    private final StopWatch rotationStopWatch = new StopWatch();
    private boolean isRotating = false;
    private Vector2f targetRotation = new Vector2f(0.0F, 0.0F);
    private Vector2f startRotation = new Vector2f(0.0F, 0.0F);
    private float rotationDuration = 0f;

    private long lastClickTime = 0;

    private final float spookyPointBase = 0f;
    private Map<String, IRotationElement> rotationElements;

    private final float spookyPointProgress = 0f;

    private final TargetPoint spookyBasePoint = TargetPoint.HEAD;
    private final TargetPoint spookyNextPoint = TargetPoint.TORSO;
    private final long spookyLastSwitch = 0;

    private final int spookAttackCounter = 0;
    private final boolean spookSwingLeft = true;

    private final long spookSwingTime = 0;
    private final boolean spookSwingActive = false;

    private long randomizeDelayForSpook(long base) {
        return base + ThreadLocalRandom.current().nextInt(-15, 16);
    }

    public static AuraNew getInstance() {
        return Instance.get(AuraNew.class);
    }

    private final ModeSetting componentMode = new ModeSetting(this, "Ротации", "Custom", "Smooth", "HVH", "Фантайм", "LonyGrief", "СкупиДуел", "СпукиТайм", "HW", "RW", "Intave", "Matrix", "Повторяюшая", "TestNew", "Neural", "Training", "Neuro Self");
    private final ModeSetting movementCorrection = new ModeSetting(this, "Коррекция движения", "Незаметная", "Сфокусированная", "Грубая");
    private final ModeSetting sprintReset = new ModeSetting(this, "Sprint Reset", "Обычный", "Легитный");
    private int sprintTicks = 0;
    private long pressTime = 0;

    private final ModeSetting attackMode = new ModeSetting(this, "Режим задержки атаки", "1.9", "1.8");
    private final ModeSetting cooldownMode = new ModeSetting(this, "Режим кулдауна", "Динамический", "Кастомный меч");
    private final SliderSetting customSwordCooldown = new SliderSetting(this, "Кулдаун меча (мс)", 450F, 50F, 1000F, 10F).setVisible(() -> cooldownMode.is("Кастомный меч"));
    private final SliderSetting dynamicCps = new SliderSetting(this, "CPS (1.8)", 12F, 8F, 20F, 1F).setVisible(() -> attackMode.is("1.8"));

    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция", 3F, 3F, 6F, 0.1F);
    private final SliderSetting preRange = new SliderSetting(this, "Доп дистанция", 1F, 0F, 3F, 0.1F).setVisible(() -> !componentMode.is("Грим") && !componentMode.is("Нейро"));
    private final BooleanSetting elytraTargetMode = new BooleanSetting(this, "Элитра Таргет", false);
    private final SliderSetting elytraDistance = new SliderSetting(this, "Дистанция элитры", 65F, 10F, 100F, 1F).setVisible(elytraTargetMode::getValue);
    private final ModeSetting elytraTargetType = new ModeSetting(this, "Режим элитра-таргета", "Обычный", "Кастомизированный").setVisible(elytraTargetMode::getValue);
    private final BooleanSetting elytraCustom = new BooleanSetting(this, "Включить кастомизацию", false).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Кастомизированный"));
    private final SliderSetting elytraPredictDistance = new SliderSetting(this, "Дистанция предикта", 4.7F, 2F, 10F, 0.1F).setVisible(() -> elytraTargetMode.getValue() && !elytraCustom.getValue());
    private final BooleanSetting adaptivePredict = new BooleanSetting(this, "Адаптивный предикт", true).setVisible(() -> elytraTargetMode.getValue() && !elytraCustom.getValue());


    private final SliderSetting elytraCustomPredictDistance = new SliderSetting(this, "Дистанция предикта", 4.7F, 2F, 10F, 0.1F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final BooleanSetting elytraCustomAdaptivePredict = new BooleanSetting(this, "Адаптивный предикт", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomMovingThreshold = new SliderSetting(this, "Порог движения", 0.05f, 0.01f, 0.2f, 0.01f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomAccelerationFactor = new SliderSetting(this, "Фактор ускорения", 0.5f, 0.0f, 2.0f, 0.1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomChaseMultiplier = new SliderSetting(this, "Множитель преследования", 1.3f, 1.0f, 3.0f, 0.1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomPerpendicularOffset = new SliderSetting(this, "Перпендикулярное смещение", 1.5f, 0.0f, 5.0f, 0.1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomMaxOffsetMultiplier = new SliderSetting(this, "Макс множитель смещения", 1.5f, 0.5f, 3.0f, 0.1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomPlayerToTargetThreshold = new SliderSetting(this, "Порог игрок-цель", 0.7f, 0.1f, 1.0f, 0.05f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomChaseDirectionThreshold = new SliderSetting(this, "Порог направления преследования", 0.7f, 0.1f, 1.0f, 0.05f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());

    private final SliderSetting elytraCustomYawSpeed = new SliderSetting(this, "Скорость Yaw", 360F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomPitchSpeed = new SliderSetting(this, "Скорость Pitch", 180F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());

    private final BooleanSetting elytraCustomDetectPass = new BooleanSetting(this, "Определять прохождение точки", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final SliderSetting elytraCustomPassThreshold = new SliderSetting(this, "Порог прохождения", -0.3f, -1.0f, 0.0f, 0.05f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomDetectPass.getValue());

    private final BooleanSetting elytraCustomRetreatOnHit = new BooleanSetting(this, "Отлетать после удара", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());
    private final BooleanSetting elytraCustomRetreatHealthCheck = new BooleanSetting(this, "Проверка хп цели", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatMinTime = new SliderSetting(this, "Мин время отлета (мс)", 100f, 50f, 500f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatMaxTime = new SliderSetting(this, "Макс время отлета (мс)", 300f, 100f, 700f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final BooleanSetting elytraCustomRetreatOnlyGrounded = new BooleanSetting(this, "Отлет только если цель на земле", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final BooleanSetting elytraCustomRetreatOnlyNoElytra = new BooleanSetting(this, "Отлет только без элитры у цели", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());

    private final SliderSetting elytraCustomRetreatYawMin = new SliderSetting(this, "Мин Yaw отлета", -15f, -180f, 0f, 1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatYawMax = new SliderSetting(this, "Макс Yaw отлета", 15f, 0f, 180f, 1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatPitchMin = new SliderSetting(this, "Мин Pitch отлета", 25f, -90f, 90f, 1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatPitchMax = new SliderSetting(this, "Макс Pitch отлета", 45f, -90f, 90f, 1f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());

    private final SliderSetting elytraCustomRetreatYawSpeed = new SliderSetting(this, "Скорость Yaw отлета", 360F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomRetreatPitchSpeed = new SliderSetting(this, "Скорость Pitch отлета", 180F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());

    private final ModeSetting elytraCustomRetreatInterpolation = new ModeSetting(this, "Интерполяция отлета", "Плавная", "Линейная", "Экспоненциальная", "Синусоидальная").setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());

    private final SliderSetting elytraCustomReturnYawSpeed = new SliderSetting(this, "Скорость Yaw возврата", 360F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomReturnPitchSpeed = new SliderSetting(this, "Скорость Pitch возврата", 180F, 10F, 360F, 5F).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());

    private final BooleanSetting elytraCustomUseFakeLag = new BooleanSetting(this, "FakeLag на отлете", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue());
    private final SliderSetting elytraCustomFakeLagDuration = new SliderSetting(this, "Длительность FakeLag (мс)", 150f, 50f, 500f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue() && elytraCustomRetreatOnHit.getValue() && elytraCustomUseFakeLag.getValue());

    private final BooleanSetting elytraCustomVisualLines = new BooleanSetting(this, "Визуальные линии", true).setVisible(() -> elytraTargetMode.getValue() && elytraCustom.getValue());

    public BooleanSetting elytraCustom() {
        return elytraCustom;
    }

    public SliderSetting elytraCustomPredictDistance() {
        return elytraCustomPredictDistance;
    }

    public BooleanSetting elytraCustomAdaptivePredict() {
        return elytraCustomAdaptivePredict;
    }

    public SliderSetting elytraCustomMovingThreshold() {
        return elytraCustomMovingThreshold;
    }

    public SliderSetting elytraCustomAccelerationFactor() {
        return elytraCustomAccelerationFactor;
    }

    public SliderSetting elytraCustomChaseMultiplier() {
        return elytraCustomChaseMultiplier;
    }

    public SliderSetting elytraCustomPerpendicularOffset() {
        return elytraCustomPerpendicularOffset;
    }

    public SliderSetting elytraCustomMaxOffsetMultiplier() {
        return elytraCustomMaxOffsetMultiplier;
    }

    public SliderSetting elytraCustomPlayerToTargetThreshold() {
        return elytraCustomPlayerToTargetThreshold;
    }

    public SliderSetting elytraCustomChaseDirectionThreshold() {
        return elytraCustomChaseDirectionThreshold;
    }

    public SliderSetting elytraCustomYawSpeed() {
        return elytraCustomYawSpeed;
    }

    public SliderSetting elytraCustomPitchSpeed() {
        return elytraCustomPitchSpeed;
    }

    public BooleanSetting elytraCustomDetectPass() {
        return elytraCustomDetectPass;
    }

    public SliderSetting elytraCustomPassThreshold() {
        return elytraCustomPassThreshold;
    }

    public BooleanSetting elytraCustomRetreatOnHit() {
        return elytraCustomRetreatOnHit;
    }

    public BooleanSetting elytraCustomRetreatHealthCheck() {
        return elytraCustomRetreatHealthCheck;
    }

    public SliderSetting elytraCustomRetreatMinTime() {
        return elytraCustomRetreatMinTime;
    }

    public SliderSetting elytraCustomRetreatMaxTime() {
        return elytraCustomRetreatMaxTime;
    }

    public BooleanSetting elytraCustomRetreatOnlyGrounded() {
        return elytraCustomRetreatOnlyGrounded;
    }

    public BooleanSetting elytraCustomRetreatOnlyNoElytra() {
        return elytraCustomRetreatOnlyNoElytra;
    }

    public SliderSetting elytraCustomRetreatYawMin() {
        return elytraCustomRetreatYawMin;
    }

    public SliderSetting elytraCustomRetreatYawMax() {
        return elytraCustomRetreatYawMax;
    }

    public SliderSetting elytraCustomRetreatPitchMin() {
        return elytraCustomRetreatPitchMin;
    }

    public SliderSetting elytraCustomRetreatPitchMax() {
        return elytraCustomRetreatPitchMax;
    }

    public SliderSetting elytraCustomRetreatYawSpeed() {
        return elytraCustomRetreatYawSpeed;
    }

    public SliderSetting elytraCustomRetreatPitchSpeed() {
        return elytraCustomRetreatPitchSpeed;
    }

    public ModeSetting elytraCustomRetreatInterpolation() {
        return elytraCustomRetreatInterpolation;
    }

    public SliderSetting elytraCustomReturnYawSpeed() {
        return elytraCustomReturnYawSpeed;
    }

    public SliderSetting elytraCustomReturnPitchSpeed() {
        return elytraCustomReturnPitchSpeed;
    }

    public BooleanSetting elytraCustomUseFakeLag() {
        return elytraCustomUseFakeLag;
    }

    public SliderSetting elytraCustomFakeLagDuration() {
        return elytraCustomFakeLagDuration;
    }

    public BooleanSetting elytraCustomVisualLines() {
        return elytraCustomVisualLines;
    }

    private final BooleanSetting smartRetreatOnHit = new BooleanSetting(this, "Отлетать после удара", true).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный"));
    private final SliderSetting smartRetreatMinTime = new SliderSetting(this, "Мин. время отлета (мс)", 100f, 50f, 500f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue());
    private final SliderSetting smartRetreatMaxTime = new SliderSetting(this, "Макс. время отлета (мс)", 300f, 100f, 700f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue());
    private final BooleanSetting smartRetreatOnlyGrounded = new BooleanSetting(this, "Отлет только если цель на земле", true).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue());
    private final BooleanSetting smartRetreatOnlyNoElytra = new BooleanSetting(this, "Отлет только без элитры у цели", true).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue());
    private final BooleanSetting smartUseFakeLagOnRetreat = new BooleanSetting(this, "FakeLag на отлете", true).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue());
    private final SliderSetting smartFakeLagDuration = new SliderSetting(this, "Длительность FakeLag (мс)", 150f, 50f, 500f, 10f).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный") && smartRetreatOnHit.getValue() && smartUseFakeLagOnRetreat.getValue());

    private final BooleanSetting smartVisualLines = new BooleanSetting(this, "Визуальные линии", true).setVisible(() -> elytraTargetMode.getValue() && elytraTargetType.is("Умный"));

    public BooleanSetting smartRetreatOnHit() {
        return smartRetreatOnHit;
    }

    public SliderSetting smartRetreatMinTime() {
        return smartRetreatMinTime;
    }

    public SliderSetting smartRetreatMaxTime() {
        return smartRetreatMaxTime;
    }

    public BooleanSetting smartRetreatOnlyGrounded() {
        return smartRetreatOnlyGrounded;
    }

    public BooleanSetting smartRetreatOnlyNoElytra() {
        return smartRetreatOnlyNoElytra;
    }

    public BooleanSetting smartUseFakeLagOnRetreat() {
        return smartUseFakeLagOnRetreat;
    }

    public SliderSetting smartFakeLagDuration() {
        return smartFakeLagDuration;
    }

    private final ModeSetting smoothAttackMode = new ModeSetting(this, "Режим атаки", "Focus", "Snap").setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMsBeforeSnap = new SliderSetting(this, "Мс до снапа", 100f, 0f, 150f, 5f).setVisible(() -> componentMode.is("Smooth") && smoothAttackMode.is("Snap"));
    private final SliderSetting smoothMsAfterSnap = new SliderSetting(this, "Мс после снапа", 100f, 0f, 150f, 5f).setVisible(() -> componentMode.is("Smooth") && smoothAttackMode.is("Snap"));

    private final SliderSetting smoothMinYawSpeed = new SliderSetting(this, "Скорость Yaw min", 5f, 1f, 50f, 1f).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMaxYawSpeed = new SliderSetting(this, "Скорость Yaw max", 30f, 10f, 360f, 5f).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMinPitchSpeed = new SliderSetting(this, "Скорость Pitch min", 3f, 1f, 30f, 1f).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMaxPitchSpeed = new SliderSetting(this, "Скорость Pitch max", 20f, 5f, 180f, 5f).setVisible(() -> componentMode.is("Smooth"));

    private final ModeSetting smoothInterpolationType = new ModeSetting(this, "Интерполяция", "Линейная", "Плавная", "Экспоненциальная", "Синусоидальная", "Кубическая").setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothInterpolationStrength = new SliderSetting(this, "Сила интерполяции", 0.5f, 0.1f, 1.0f, 0.05f).setVisible(() -> componentMode.is("Smooth"));


    private final BooleanSetting smoothUseMultipoint = new BooleanSetting(this, "Мультипоинты", false).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMultipointMax = new SliderSetting(this, "Макс количество точек", 8f, 2f, 24f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());
    private final SliderSetting smoothMultipointMin = new SliderSetting(this, "Мин количество точек", 3f, 2f, 12f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());
    private final SliderSetting smoothMultipointSwitchSpeed = new SliderSetting(this, "Скорость перехода", 0.3f, 0.1f, 2.0f, 0.1f).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());
    private final SliderSetting smoothMultipointSmoothness = new SliderSetting(this, "Плавность перехода", 0.5f, 0.1f, 1.0f, 0.05f).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());
    private final BooleanSetting smoothMultipointJerkTransition = new BooleanSetting(this, "Рывки между переходами", false).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());
    private final BooleanSetting smoothMultipointStepTransition = new BooleanSetting(this, "Шаговые переходы", false).setVisible(() -> componentMode.is("Smooth") && smoothUseMultipoint.getValue());

    private final BooleanSetting smoothUseJitter = new BooleanSetting(this, "Jitter", false).setVisible(() -> componentMode.is("Smooth"));
    private final ModeSetting smoothJitterType = new ModeSetting(this, "Тип Jitter", "Круговой", "Волновой", "Хаотичный").setVisible(() -> componentMode.is("Smooth") && smoothUseJitter.getValue());
    private final SliderSetting smoothJitterRadius = new SliderSetting(this, "Радиус jitter", 2.0f, 0.1f, 10.0f, 0.1f).setVisible(() -> componentMode.is("Smooth") && smoothUseJitter.getValue());
    private final SliderSetting smoothJitterSpeed = new SliderSetting(this, "Скорость jitter", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> componentMode.is("Smooth") && smoothUseJitter.getValue());
    private final SliderSetting smoothJitterInterpolation = new SliderSetting(this, "Интерполяция jitter", 0.5f, 0.0f, 1.0f, 0.05f).setVisible(() -> componentMode.is("Smooth") && smoothUseJitter.getValue());
    private final BooleanSetting smoothJitterStep = new BooleanSetting(this, "Шаговый jitter", false).setVisible(() -> componentMode.is("Smooth") && smoothUseJitter.getValue());

    private final BooleanSetting smoothUsePitchLimit = new BooleanSetting(this, "Настройка max pitch", false).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothMaxPitch = new SliderSetting(this, "Max поднятие pitch", 60f, 30f, 89f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUsePitchLimit.getValue());
    private final SliderSetting smoothMinPitch = new SliderSetting(this, "Min поднятие pitch", -60f, -89f, -30f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUsePitchLimit.getValue());

    private final BooleanSetting smoothUseStepRotation = new BooleanSetting(this, "Ротация шагами", false).setVisible(() -> componentMode.is("Smooth"));
    private final SliderSetting smoothStepYaw = new SliderSetting(this, "Шаг yaw", 15f, 1f, 90f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUseStepRotation.getValue());
    private final SliderSetting smoothStepPitch = new SliderSetting(this, "Шаг pitch", 10f, 1f, 60f, 1f).setVisible(() -> componentMode.is("Smooth") && smoothUseStepRotation.getValue());

    private final SliderSetting lazinessH = new SliderSetting(this, "Плавность H", 5F, 1F, 100, 1F).setVisible(() -> componentMode.is("Легитная"));
    private final SliderSetting lazinessV = new SliderSetting(this, "Плавность V", 5F, 1F, 100F, 1F).setVisible(() -> componentMode.is("Легитная"));
    private final SliderSetting fantimeVibeNoise = new SliderSetting(this, "Сила шума", 1.0F, 0.1F, 5.0F, 0.1F).setVisible(() -> componentMode.is("Фантайм - новая"));
    private final SliderSetting fantimeVibeSmoothness = new SliderSetting(this, "Плавность", 0.5F, 0.1F, 1.0F, 0.05F).setVisible(() -> componentMode.is("Фантайм - новая"));
    private final SliderSetting fantimeVibeShakeIntensity = new SliderSetting(this, "Интенсивность тряски", 0.5F, 0.0F, 2.0F, 0.1F).setVisible(() -> componentMode.is("Фантайм - новая"));

    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false).setVisible(onlyCrits::getValue);
    private final BooleanSetting autoJump = new BooleanSetting(this, "Авто прыжок", false);

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Прочее",
            BooleanSetting.of("Не бить когда ешь", true),
            BooleanSetting.of("Бить только с оружием", false),
            BooleanSetting.of("Выключить после смерти", false),
            BooleanSetting.of("Ломать щиты", true)
    );

    private final BooleanSetting smartHitbox = new BooleanSetting(this, "Улучшить хитбоксы", false);
    private final BooleanSetting randomCrits = new BooleanSetting(this, "No crit + random", false).setVisible(onlyCrits::getValue);
    private final BooleanSetting enableRayTrace = new BooleanSetting(this, "RayTrace", true);

    private final BooleanSetting customRotationEnabled = new BooleanSetting(this, "Включить кастом", true).setVisible(() -> componentMode.is("Custom"));

    // Фикс Скорости Ударов - ускорение ротации когда цель в радиусе атаки
    private final BooleanSetting hitSpeedFix = new BooleanSetting(this, "Фикс Скорости Ударов", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand hitSpeedMultiplier = new SliderSettingRand(this, "Множитель скорости", 1.5f, 2.0f, 1.0f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && hitSpeedFix.getValue());
    private final SliderSettingRand hitSmoothnessReduction = new SliderSettingRand(this, "Снижение плавности", 0.3f, 0.5f, 0.0f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && hitSpeedFix.getValue());
    private final SliderSetting hitFixActivationAngle = new SliderSetting(this, "Угол активации (градусы)", 15f, 5f, 45f, 1f).setVisible(() -> customRotationEnabled.getValue() && hitSpeedFix.getValue());

    private final ModeSetting rotationUpdateMode = new ModeSetting(this, "Обновление ротации", "Тики", "FPS", "Продвинутая").setVisible(() -> componentMode.is("Custom"));
    private final BooleanSetting enableMultipoint = new BooleanSetting(this, "Включить мультипоинт", true).setVisible(customRotationEnabled::getValue);
    private final ModeSetting aimPointMode = new ModeSetting(this, "Режим точки прицеливания", "Динамический", "Ближайшая", "Циклический", "Случайный", "Взвешенный", "Прогнозирующий").setVisible(() -> customRotationEnabled.getValue() && enableMultipoint.getValue());
    private final SliderSetting bodyParts = new SliderSetting(this, "Части тела", 6f, 1f, 24f, 1f).setVisible(() -> customRotationEnabled.getValue() && enableMultipoint.getValue());
    private final BooleanSetting smoothAimPointTransition = new BooleanSetting(this, "Плавный переход точки", true).setVisible(() -> customRotationEnabled.getValue() && enableMultipoint.getValue());
    private final SliderSettingRand aimPointTransitionSpeed = new SliderSettingRand(this, "Скорость перехода", 0.15f, 0.25f, 0.01f, 1.0f, 0.01f, "").setVisible(() -> customRotationEnabled.getValue() && smoothAimPointTransition.getValue() && enableMultipoint.getValue());
    private final ModeSetting verticalAimBias = new ModeSetting(this, "Вертикальное смещение", "Нет", "Голова", "Верхняя грудь", "Грудь", "Живот", "Ноги", "Адаптивное").setVisible(() -> customRotationEnabled.getValue() && enableMultipoint.getValue());
    private final SliderSettingRand verticalBiasStrength = new SliderSettingRand(this, "Сила смещения", 0.4f, 0.6f, 0.0f, 1.0f, 0.01f, "").setVisible(() -> customRotationEnabled.getValue() && !verticalAimBias.is("Нет") && enableMultipoint.getValue());
    private final BooleanSetting horizontalBias = new BooleanSetting(this, "Горизонтальное смещение", false).setVisible(() -> customRotationEnabled.getValue() && enableMultipoint.getValue());
    private final ModeSetting horizontalBiasMode = new ModeSetting(this, "Режим гор. смещения", "Центр", "Левая сторона", "Правая сторона", "Случайная сторона", "Циклическое").setVisible(() -> customRotationEnabled.getValue() && horizontalBias.getValue() && enableMultipoint.getValue());
    private final SliderSettingRand horizontalBiasStrength = new SliderSettingRand(this, "Сила гор. смещения", 0.25f, 0.35f, 0.0f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && horizontalBias.getValue() && enableMultipoint.getValue());
    private final SliderSettingRand yawSpeed = new SliderSettingRand(this, "Скорость Yaw", 160f, 200f, 1f, 360f, 1f, "").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand pitchSpeed = new SliderSettingRand(this, "Скорость Pitch", 80f, 100f, 1f, 180f, 1f, "").setVisible(customRotationEnabled::getValue);
    private final BooleanSetting stepRotation = new BooleanSetting(this, "Ротация шагами", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand stepYaw = new SliderSettingRand(this, "Шаг Yaw", 12f, 18f, 1f, 90f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && stepRotation.getValue());
    private final SliderSettingRand stepPitch = new SliderSettingRand(this, "Шаг Pitch", 8f, 12f, 1f, 60f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && stepRotation.getValue());
    private final BooleanSetting jaggedRotation = new BooleanSetting(this, "Рваная ротация", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand jaggedIntensity = new SliderSettingRand(this, "Интенсивность рваности", 0.4f, 0.6f, 0.1f, 2.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && jaggedRotation.getValue());
    private final SliderSettingRand jaggedFrequency = new SliderSettingRand(this, "Частота рваности", 0.25f, 0.35f, 0.1f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && jaggedRotation.getValue());
    private final BooleanSetting limitMaxPitch = new BooleanSetting(this, "Ограничить макс. Pitch", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand maxPitchLimit = new SliderSettingRand(this, "Макс. Pitch", 55f, 65f, 30f, 89f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && limitMaxPitch.getValue());
    private final SliderSettingRand minPitchLimit = new SliderSettingRand(this, "Мин. Pitch", -65f, -55f, -89f, -30f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && limitMaxPitch.getValue());
    private final BooleanSetting adaptiveSpeed = new BooleanSetting(this, "Адаптивная скорость", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand accelerationFactor = new SliderSettingRand(this, "Множитель ускорения", 1.3f, 1.7f, 1.0f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && adaptiveSpeed.getValue());
    private final BooleanSetting distanceBasedSpeed = new BooleanSetting(this, "Скорость от дистанции", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand closeRangeMultiplier = new SliderSettingRand(this, "Множитель вблизи", 0.6f, 0.8f, 0.3f, 2.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && distanceBasedSpeed.getValue());
    private final SliderSettingRand farRangeMultiplier = new SliderSettingRand(this, "Множитель вдали", 1.2f, 1.4f, 0.5f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && distanceBasedSpeed.getValue());
    private final BooleanSetting targetVelocityMatching = new BooleanSetting(this, "Подстройка под скорость цели", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand velocityMatchFactor = new SliderSettingRand(this, "Сила подстройки", 0.4f, 0.6f, 0.0f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && targetVelocityMatching.getValue());
    private final BooleanSetting angleBasedSpeed = new BooleanSetting(this, "Скорость от угла", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand smallAngleMultiplier = new SliderSettingRand(this, "Множитель малых углов", 0.5f, 0.7f, 0.2f, 2.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && angleBasedSpeed.getValue());
    private final SliderSettingRand largeAngleMultiplier = new SliderSettingRand(this, "Множитель больших углов", 1.4f, 1.6f, 0.5f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && angleBasedSpeed.getValue());
    private final BooleanSetting fpsAdaptation = new BooleanSetting(this, "Адаптация от FPS", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting fpsLowThreshold = new SliderSetting(this, "Порог низкого FPS", 60f, 30f, 120f, 5f).setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSetting fpsHighThreshold = new SliderSetting(this, "Порог высокого FPS", 120f, 90f, 240f, 5f).setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSettingRand fpsLowSpeedMultiplier = new SliderSettingRand(this, "Множитель скорости (низкий FPS)", 0.6f, 0.8f, 0.3f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSettingRand fpsHighSpeedMultiplier = new SliderSettingRand(this, "Множитель скорости (высокий FPS)", 1.2f, 1.4f, 1.0f, 2.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSettingRand fpsLowSmoothnessMultiplier = new SliderSettingRand(this, "Множитель плавности (низкий FPS)", 0.7f, 0.9f, 0.3f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSettingRand fpsHighSmoothnessMultiplier = new SliderSettingRand(this, "Множитель плавности (высокий FPS)", 1.1f, 1.3f, 1.0f, 2.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && fpsAdaptation.getValue());
    private final SliderSettingRand rotationSmoothness = new SliderSettingRand(this, "Плавность ротации", 0.45f, 0.55f, 0.0f, 1.0f, 0.01f, "").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand rotationSmoothingStrength = new SliderSettingRand(this, "Сила сглаживания", 0.9f, 1.1f, 0.1f, 3.0f, 0.05f, "").setVisible(customRotationEnabled::getValue);
    private final BooleanSetting dynamicSmoothness = new BooleanSetting(this, "Динамическая плавность", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand smallAngleSmoothness = new SliderSettingRand(this, "Плавность на малых углах", 1.1f, 1.3f, 0.3f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && dynamicSmoothness.getValue());
    private final SliderSettingRand largeAngleSmoothness = new SliderSettingRand(this, "Плавность на больших углов", 0.7f, 0.9f, 0.3f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && dynamicSmoothness.getValue());
    private final ModeSetting interpolationType = new ModeSetting(this, "Тип интерполяции", "Линейная", "Плавная", "Экспоненциальная", "Синусоидальная", "Кубическая", "Квадратичная", "Elastic", "Bounce").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand rotationAcceleration = new SliderSettingRand(this, "Ускорение ротации", 0.9f, 1.1f, 0.1f, 10.0f, 0.1f, "").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand rotationDeceleration = new SliderSettingRand(this, "Замедление ротации", 0.9f, 1.1f, 0.1f, 10.0f, 0.1f, "").setVisible(customRotationEnabled::getValue);
    private final BooleanSetting humanize = new BooleanSetting(this, "Гуманизация", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand humanizePower = new SliderSettingRand(this, "Сила гуманизации", 0.8f, 1.2f, 0.0f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSettingRand reactionTime = new SliderSettingRand(this, "Время реакции (мс)", 15f, 25f, 0f, 500f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSettingRand reactionTimeVariation = new SliderSettingRand(this, "Разброс реакции (мс)", 5f, 15f, 0f, 200f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final BooleanSetting randomPauses = new BooleanSetting(this, "Случайные паузы", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand pauseChance = new SliderSettingRand(this, "Шанс паузы (%)", 3f, 7f, 0f, 50f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final SliderSettingRand minPauseDuration = new SliderSettingRand(this, "Мин. пауза (мс)", 40f, 60f, 10f, 500f, 10f, "").setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final SliderSettingRand maxPauseDuration = new SliderSettingRand(this, "Макс. пауза (мс)", 130f, 170f, 50f, 1000f, 10f, "").setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final BooleanSetting simulateFatigue = new BooleanSetting(this, "Симулировать усталость", true).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSettingRand fatigueTime = new SliderSettingRand(this, "Время до усталости (сек)", 25f, 35f, 5f, 180f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && humanize.getValue() && simulateFatigue.getValue());
    private final SliderSettingRand fatigueEffect = new SliderSettingRand(this, "Эффект усталости", 0.25f, 0.35f, 0.1f, 0.9f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && humanize.getValue() && simulateFatigue.getValue());
    private final BooleanSetting aimDrift = new BooleanSetting(this, "Дрейф прицела", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand driftStrength = new SliderSettingRand(this, "Сила дрейфа", 0.4f, 0.6f, 0.1f, 3.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && aimDrift.getValue());
    private final SliderSettingRand driftSpeed = new SliderSettingRand(this, "Скорость дрейфа", 0.4f, 0.6f, 0.1f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && aimDrift.getValue());
    private final BooleanSetting enableJitter = new BooleanSetting(this, "Джиттер", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand jitterStrength = new SliderSettingRand(this, "Сила джиттера", 0.8f, 1.2f, 0.0f, 10.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSettingRand jitterSpeed = new SliderSettingRand(this, "Скорость джиттера", 0.9f, 1.1f, 0.1f, 20.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSettingRand jitterRadius = new SliderSettingRand(this, "Радиус джиттера", 25.0f, 35.0f, 0.0f, 100.0f, 1.0f, "").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSettingRand jitterSmoothness = new SliderSettingRand(this, "Плавность джиттера", 0.45f, 0.55f, 0.0f, 1.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSettingRand jitterPatternChangeSpeed = new SliderSettingRand(this, "Скорость замены паттернов", 0.4f, 0.6f, 0.0f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterNoRepeatPatterns = new BooleanSetting(this, "Не повторять прошлые паттерны", true).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterSyncWithMultipoint = new BooleanSetting(this, "Синхронизировать с Multipoint", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterYawOnly = new BooleanSetting(this, "Джиттер только Yaw", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterPitchOnly = new BooleanSetting(this, "Джиттер только Pitch", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final ModeSetting jitterType = new ModeSetting(this, "Тип джиттера", "Синус", "Спираль", "Круговой", "Волновой", "Хаотичный", "Мультиволновой", "Комбинированный").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting enableLookUp = new BooleanSetting(this, "Отводка взгляда вверх", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand minLookUpDelay = new SliderSettingRand(this, "Задержка отводки (мс)", 7000f, 13000f, 5000f, 30000f, 100f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());
    private final SliderSettingRand maxLookUpDelay = new SliderSettingRand(this, "Макс задержка отводки (мс)", 15000f, 20000f, 15001f, 30000f, 100f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());
    private final SliderSettingRand minLookUpDuration = new SliderSettingRand(this, "Длительность (мс)", 250f, 350f, 100f, 2000f, 50f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());
    private final SliderSettingRand maxLookUpDuration = new SliderSettingRand(this, "Макс длительность (мс)", 800f, 1200f, 500f, 2000f, 50f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());
    private final SliderSettingRand minLookUpPitch = new SliderSettingRand(this, "Мин pitch отводки", -45f, -40f, -89f, -10f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());
    private final SliderSettingRand maxLookUpPitch = new SliderSettingRand(this, "Макс pitch отводки", -20f, -15f, -60f, 10f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && enableLookUp.getValue());

    private final BooleanSetting enablePullbackAfterHit = new BooleanSetting(this, "Отводка после удара", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand pullbackYawDegrees = new SliderSettingRand(this, "Градусы отводки Yaw", 3f, 8f, 0f, 45f, 0.5f, "").setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());
    private final SliderSettingRand pullbackPitchDegrees = new SliderSettingRand(this, "Градусы отводки Pitch", 2f, 5f, 0f, 30f, 0.5f, "").setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());
    private final SliderSettingRand pullbackSpeedMultiplier = new SliderSettingRand(this, "Множитель скорости", 0.3f, 0.6f, 0.1f, 2.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());
    private final ModeSetting pullbackDirection = new ModeSetting(this, "Сторона отводки", "Случайная", "Влево", "Вправо", "Вверх", "Вниз", "Влево-Вверх", "Вправо-Вверх", "Влево-Вниз", "Вправо-Вниз").setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());
    private final BooleanSetting pullbackYawEnabled = new BooleanSetting(this, "Отводка Yaw", true).setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());
    private final BooleanSetting pullbackPitchEnabled = new BooleanSetting(this, "Отводка Pitch", true).setVisible(() -> customRotationEnabled.getValue() && enablePullbackAfterHit.getValue());

    // Отводка после выключения ротации - скорость и плавность возврата
    private final SliderSettingRand disableRotationReturnSpeed = new SliderSettingRand(this, "Скорость возврата при выключении", 100f, 150f, 10f, 360f, 5f, "").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand disableRotationReturnSmoothness = new SliderSettingRand(this, "Плавность возврата при выключении", 0.4f, 0.6f, 0.0f, 1.0f, 0.05f, "").setVisible(customRotationEnabled::getValue);

    public BooleanSetting enableLookUp() {
        return enableLookUp;
    }

    public SliderSettingRand minLookUpDelay() {
        return minLookUpDelay;
    }

    public SliderSettingRand maxLookUpDelay() {
        return maxLookUpDelay;
    }

    public SliderSettingRand minLookUpDuration() {
        return minLookUpDuration;
    }

    public SliderSettingRand maxLookUpDuration() {
        return maxLookUpDuration;
    }

    public SliderSettingRand minLookUpPitch() {
        return minLookUpPitch;
    }

    public SliderSettingRand maxLookUpPitch() {
        return maxLookUpPitch;
    }

    public BooleanSetting enablePullbackAfterHit() {
        return enablePullbackAfterHit;
    }

    public SliderSettingRand pullbackYawDegrees() {
        return pullbackYawDegrees;
    }

    public SliderSettingRand pullbackPitchDegrees() {
        return pullbackPitchDegrees;
    }

    public SliderSettingRand pullbackSpeedMultiplier() {
        return pullbackSpeedMultiplier;
    }

    public ModeSetting pullbackDirection() {
        return pullbackDirection;
    }

    public BooleanSetting pullbackYawEnabled() {
        return pullbackYawEnabled;
    }

    public BooleanSetting pullbackPitchEnabled() {
        return pullbackPitchEnabled;
    }

    public SliderSettingRand disableRotationReturnSpeed() {
        return disableRotationReturnSpeed;
    }

    public SliderSettingRand disableRotationReturnSmoothness() {
        return disableRotationReturnSmoothness;
    }

    public ModeSetting rotationUpdateMode() {
        return rotationUpdateMode;
    }

    private final BooleanSetting enableMicroMovements = new BooleanSetting(this, "Микродвижения", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand microMovementStrength = new SliderSettingRand(this, "Сила микродвижений", 0.5f, 0.8f, 0.1f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final SliderSettingRand microMovementFrequency = new SliderSettingRand(this, "Частота микродвижений", 0.02f, 0.05f, 0.001f, 0.2f, 0.001f, "").setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final ModeSetting microMovementPattern = new ModeSetting(this, "Паттерн микродвижений", "Хаотичный", "Круговой", "Восьмерка", "Зигзаг", "Perlin").setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final BooleanSetting legacyRotation = new BooleanSetting(this, "Легаси ротация", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand legacyYawStep = new SliderSettingRand(this, "Шаг Yaw", 15f, 20f, 1f, 90f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final SliderSettingRand legacyPitchStep = new SliderSettingRand(this, "Шаг Pitch", 10f, 15f, 1f, 60f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final BooleanSetting smoothLegacy = new BooleanSetting(this, "Плавный легаси", true).setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final BooleanSetting useGCD = new BooleanSetting(this, "GCD", true).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting strictGCD = new BooleanSetting(this, "Строгий GCD", false).setVisible(() -> customRotationEnabled.getValue() && useGCD.getValue());
    private final BooleanSetting limitRotation = new BooleanSetting(this, "Ограничить угол", true).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand maxYaw = new SliderSettingRand(this, "Макс. угол Yaw", 180f, 180f, 1f, 180f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final SliderSettingRand maxPitch = new SliderSettingRand(this, "Макс. угол Pitch", 90f, 90f, 1f, 90f, 1f, "").setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final BooleanSetting smoothLimit = new BooleanSetting(this, "Плавное ограничение", true).setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final ModeSetting rotationTiming = new ModeSetting(this, "Тайминг ротации", "Постоянная", "До и после удара", "Nexus").setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand rotationBeforeHit = new SliderSettingRand(this, "Ротация до удара (мс)", 15f, 25f, 0f, 200f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final SliderSettingRand rotationAfterHit = new SliderSettingRand(this, "Ротация после удара (мс)", 15f, 25f, 0f, 200f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final BooleanSetting randomRotationStart = new BooleanSetting(this, "Случайное начало ротации", false).setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final SliderSettingRand randomRotationOffset = new SliderSettingRand(this, "Смещение начала (градусы)", 30f, 45f, 5f, 180f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара") && randomRotationStart.getValue());
    private final BooleanSetting advancedAntiCheat = new BooleanSetting(this, "Продвинутый обход АЧ", false).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting randomizeRotationPattern = new BooleanSetting(this, "Рандомизация паттернов", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final BooleanSetting pixelPerfect = new BooleanSetting(this, "Пиксельная точность", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final BooleanSetting eyeTracking = new BooleanSetting(this, "Имитация отслеживания взглядом", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final SliderSettingRand eyeTrackingLag = new SliderSettingRand(this, "Задержка взгляда (мс)", 30f, 50f, 0f, 150f, 5f, "").setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && eyeTracking.getValue());
    private final BooleanSetting contextAware = new BooleanSetting(this, "Контекстная осведомленность", false).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting combatMode = new BooleanSetting(this, "Боевой режим", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && contextAware.getValue());
    private final BooleanSetting stealthMode = new BooleanSetting(this, "Скрытный режим", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && contextAware.getValue());
    private final BooleanSetting naturalSway = new BooleanSetting(this, "Естественное покачивание", false).setVisible(customRotationEnabled::getValue);
    private final SliderSettingRand swayStrength = new SliderSettingRand(this, "Сила покачивания", 0.3f, 0.5f, 0.1f, 2.0f, 0.05f, "").setVisible(() -> customRotationEnabled.getValue() && naturalSway.getValue());
    private final SliderSettingRand swayFrequency = new SliderSettingRand(this, "Частота покачивания", 0.5f, 0.8f, 0.1f, 5.0f, 0.1f, "").setVisible(() -> customRotationEnabled.getValue() && naturalSway.getValue());
    private final BooleanSetting angleNormalization = new BooleanSetting(this, "Нормализация углов", true).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting smoothAngleWrapping = new BooleanSetting(this, "Плавный переход через 360°", true).setVisible(() -> customRotationEnabled.getValue() && angleNormalization.getValue());
    private final BooleanSetting priorityTargeting = new BooleanSetting(this, "Приоритетное наведение", false).setVisible(customRotationEnabled::getValue);

    // Getters для Фикс Скорости Ударов
    public BooleanSetting hitSpeedFix() {
        return hitSpeedFix;
    }

    public SliderSettingRand hitSpeedMultiplier() {
        return hitSpeedMultiplier;
    }

    public SliderSettingRand hitSmoothnessReduction() {
        return hitSmoothnessReduction;
    }

    public SliderSetting hitFixActivationAngle() {
        return hitFixActivationAngle;
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final PerfectDelay perfectDelay = new PerfectDelay();
    private final StopWatch stopWatch = new StopWatch();
    private final Script script = new Script();
    private final HumanRotationTrainer humanRotationTrainer = new HumanRotationTrainer();
    public LivingEntity target;

    private Vector2f lerpRotation = Vector2f.ZERO;
    private int count;
    private IRotationElement currentRotationElement;

    public AuraNew() {
        if (!componentMode.values.contains("Nova")) {
            List<String> updated = new ArrayList<>(componentMode.values);
            updated.add("Nova");
            componentMode.values = List.copyOf(updated);
        }
    }

    @Override
    public void toggle() {
        super.toggle();
        if (isEnabled()) {
            initRotationElements();
        }
        reset();
    }

    public boolean isPointVisible(Vector3d point) {
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                eyePos, point, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player
        ));
        return result.getType() == RayTraceResult.Type.MISS;
    }

    public boolean shouldPlayerFalling() {
        return AuraUtil.isPlayerFalling(onlyCrits.getValue(), smartCrits.getValue());
    }

    public BooleanSetting stepRotation() {
        return stepRotation;
    }

    public SliderSettingRand stepYaw() {
        return stepYaw;
    }

    public SliderSettingRand stepPitch() {
        return stepPitch;
    }

    public BooleanSetting jaggedRotation() {
        return jaggedRotation;
    }

    public SliderSettingRand jaggedIntensity() {
        return jaggedIntensity;
    }

    public SliderSettingRand jaggedFrequency() {
        return jaggedFrequency;
    }

    public BooleanSetting limitMaxPitch() {
        return limitMaxPitch;
    }

    public SliderSettingRand maxPitchLimit() {
        return maxPitchLimit;
    }

    public SliderSettingRand minPitchLimit() {
        return minPitchLimit;
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket) {
            if (componentMode.is("Спукитайм")) {
                perfectDelay.reset(randomizeDelayForSpook(550L));
            } else {
                perfectDelay.reset(550L);
            }
        } else if (packet instanceof CAnimateHandPacket) {
            if (componentMode.is("Спукитайм")) {
                perfectDelay.reset(randomizeDelayForSpook(450L));
            } else {
                perfectDelay.reset(450L);
            }
            if (componentMode.is("Фантайм - новая")) {
                shouldSnapBack = true;
            }
        }
    }

    public BooleanSetting adaptivePredict() {
        return adaptivePredict;
    }

    public void setLerpRotation(Vector2f lerpRotation) {
        this.lerpRotation = lerpRotation;
    }

    public Vector2f getLerpRotation() {
        return this.lerpRotation;
    }

    public float calculatePingFactor() {
        return Optional.ofNullable(mc.getConnection())
                .map(connection -> connection.getPlayerInfo(mc.player.getGameProfile().getId()))
                .map(NetworkPlayerInfo::getResponseTime)
                .map(ping -> ping / 50.0f)
                .orElse(1.0f);
    }

    int p;

    @EventHandler
    public void onInput(MoveInputEvent e) {
        if (p > 0) {
            e.setForward(0);
            p--;
        }
        if (sprintReset.is("Легитный") && sprintTicks > 0) {
            e.setForward(0);
            sprintTicks--;
        }
        if (sprintReset.is("Легитный") && pressTime > 0 && System.currentTimeMillis() >= pressTime) {
            e.setForward(1.0f);
            pressTime = 0;
        }
        if (autoJump.getValue() && target != null && mc.player.isOnGround() && !mc.player.isInWater() && !mc.player.isInLava()) {
            e.setJump(true);
        }
    }

    private void initRotationElements() {
        if (rotationElements == null) {
            rotationElements = new HashMap<>();
            rotationElements.put("Custom", new CustomRot());
            rotationElements.put("Test", new SlothAiRotation());
            rotationElements.put("TestNew", new SmoothNewRot());
            rotationElements.put("Повторяюшая", new RepetitiveRot());
            rotationElements.put("RW", new RWRotation());
            rotationElements.put("Intave", new IntaveRot());
            rotationElements.put("Matrix", new MatrixRot());
            rotationElements.put("HW", new HWRot());
            rotationElements.put("LonyGrief", new UniAcRot());
            rotationElements.put("СпукиТайм", new SmoothNewRot());
            rotationElements.put("HVH", new ReallyWorldRot());
            rotationElements.put("Smooth", new SmoothUpdateRot());
            rotationElements.put("Nova", new NovaRot());
            rotationElements.put("Neuro Self", new NeuroSelfRot());
        }
        IRotationElement newElement = rotationElements.get(componentMode.getValue());
        if (newElement != null && newElement != currentRotationElement) {
            if (currentRotationElement != null) {
                currentRotationElement.onDisable(this);
            }
            currentRotationElement = newElement;
            currentRotationElement.onEnable(this);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        initRotationElements();
        reset();
        if (currentRotationElement != null) {
            currentRotationElement.onEnable(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        humanRotationTrainer.flushModel();
        reset();
        humanRotationTrainer.reset();
        RotationComponent.getInstance().resetRotation();
        if (currentRotationElement != null) {
            currentRotationElement.onDisable(this);
        }
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        reset();
        if (currentRotationElement != null) {
            currentRotationElement.onEnable(this);
        }
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
        if (currentRotationElement != null) {
            currentRotationElement.onEnable(this);
        }
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (target == null || !checks.getValue("Отображать FOV")) {
        }

        // Warning for LonyGrief + RayTrace combination
        if (componentMode.getValue().equals("LonyGrief") && enableRayTrace.getValue()) {
            MatrixStack matrix = event.getMatrix();
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();

            String warningText = "Выключите в AttackAura чекбокс RayTrace чтобы ротация работала";
            float textWidth = Fonts.SF_MEDIUM.getWidth(warningText, 12f);
            float x = (screenWidth - textWidth) / 2f;
            float y = screenHeight / 2f - 50f;

            // Draw warning text in white
            Fonts.SF_MEDIUM.draw(matrix, warningText, x, y, ColorUtil.getColor(255, 255, 255), 12f);
        }
    }

    public float getElytraDistance() {
        return elytraDistance.getValue();
    }

    public float getElytraPredictDistance() {
        return elytraPredictDistance.getValue();
    }

    @EventHandler
    public void onEvent(MoveEvent event) {
        if (target == null || mc.player == null || mc.world == null) {
            canCrit = false;
            return;
        }
        final boolean fallCheck = mc.player.nextFallDistance != 0F;
        canCrit = !event.isToGround() && event.getFrom().y > event.getTo().y && fallCheck;
    }

    private final boolean rotationEnabled = true;
    private final BooleanSetting targetFollow = new BooleanSetting(this, "Таргетированный", false);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(GameUpdateEvent event) {
        if (componentMode.is("Neural")) {
            if (target != null) {
                Vector2f neuralRot = humanRotationTrainer.update(mc, target);
                lerpRotation = neuralRot;
                RotationComponent.update(new Rotation(neuralRot.x, neuralRot.y), 220, 120, 0, 4);
            }
            return;
        }
        if (componentMode.is("Training")) {
            humanRotationTrainer.update(mc);
            return;
        }
        if (target == null) return;
        if (!rotationEnabled) {
            return;
        }
        if (elytraTargetMode.getValue() && mc.player.isElytraFlying()) {
            ElytraAuraLogic.updateElytraRotation(this, target, mc, lerpRotation, elytraTargetType.getValue());
            return;
        }


        if (target != null && mc.player != null && mc.world != null) {
            updateRotation();
        } else {
            reset();
        }

        if (currentRotationElement != null) {
            // Skip rotation for LonyGrief mode (no rotation killaura)
            if (!componentMode.getValue().equals("LonyGrief")) {
                if (targetFollow.getValue()) {
                    intelligentTargetTracking();
                } else {
                    currentRotationElement.updateRotation(this, target, mc, lerpRotation);
                }
            }
        }
    }

    private void intelligentTargetTracking() {
        if (target == null || !targetFollow.getValue()) return;
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();
        double distance = playerPos.distanceTo(targetPos);
        Vector3d predictedTargetPos = targetPos.add(target.getMotion().scale(Math.min(1.0, distance / 10.0) * 3.0));
        Vector3d aimPoint = new Vector3d(predictedTargetPos.x, predictedTargetPos.y + target.getEyeHeight() * 0.5, predictedTargetPos.z);
        Vector3d direction = aimPoint.subtract(mc.player.getEyePosition(1.0F));
        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
        float yawDelta = MathHelper.wrapDegrees(yaw - lerpRotation.x);
        float distanceFactor = distance < 0.7 ? 2.0f : (float) Math.max(1.0, 4.0 / Math.max(distance, 1.0));
        float yawSpeed = (0.25f + Math.min(Math.abs(yawDelta) / 180.0f, 1.0f) * 0.3f) * distanceFactor;
        float pitchSpeed = (0.2f + Math.min(Math.abs(pitch - lerpRotation.y) / 90.0f, 1.0f) * 0.25f) * distanceFactor;
        float newYaw = wrapLerp(yawSpeed, lerpRotation.x, yaw);
        float newPitch = wrapLerp(pitchSpeed, lerpRotation.y, MathHelper.clamp(pitch, -90.0f, 90.0f));
        lerpRotation = new Vector2f(newYaw, newPitch);
        RotationComponent.update(new org.privates.client.managers.component.impl.rotation.Rotation(GCDUtils.getSensitivity(newYaw), GCDUtils.getSensitivity(newPitch)), 180, 110, 0, 4);
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (componentMode.is("Training")) {
            target = null;
            return;
        }
        updateTarget();
        updateAttack();
    }

    private void updateRotation() {
        double maxHeight = (AuraUtil.getStrictDistance((target)) / attackDistance());
        String mode = componentMode.getValue();
        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d targetPos;
        float yaw = 0, pitch = 0;
        float[] ranges = getRanges();
        ranges = new float[]{ranges[0], ranges[1], ranges[0] + ranges[1]};
        boolean canAttack = shouldAttack2(target, false, true, true, 0L, ranges);

        switch (mode) {
            case "Фантайм":
                FunTimeRotation.rotation(target, canAttack, (float) attackDistance(), checkReturn());
                break;
            case "СкупиДуел":
                SpookyTimeRotation.applyRotation(target, canAttack, (float) attackDistance(), checkReturn());
                break;

        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!elytraTargetMode.getValue() || !elytraCustom.getValue() || !elytraCustomVisualLines.getValue()) return;
        if (!mc.player.isElytraFlying()) return;

        renderSmartElytraVisuals(event);
    }

    private void renderSmartElytraVisuals(Render3DLastEvent event) {
        MatrixStack matrixStack = event.getMatrix();
        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

        double renderX = mc.getRenderManager().renderPosX();
        double renderY = mc.getRenderManager().renderPosY();
        double renderZ = mc.getRenderManager().renderPosZ();
        RenderSystem.translated(-renderX, -renderY, -renderZ);

        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        Vector3d playerPos = mc.player.getPositionVec();

        if (ElytraAuraLogic.visualPredictPoint != null) {
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            int predictColor = ColorUtil.getColor(0, 255, 255, 200);
            addLine(buffer, playerPos, ElytraAuraLogic.visualPredictPoint, predictColor);
            tessellator.draw();
        }

        if (ElytraAuraLogic.visualRetreatPoint != null) {
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            int retreatColor = ColorUtil.getColor(255, 100, 100, 200);
            addLine(buffer, playerPos, ElytraAuraLogic.visualRetreatPoint, retreatColor);
            tessellator.draw();
        }

        if (ElytraAuraLogic.visualReturnPoint != null) {
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            int returnColor = ColorUtil.getColor(100, 255, 100, 200);
            addLine(buffer, playerPos, ElytraAuraLogic.visualReturnPoint, returnColor);
            tessellator.draw();
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        matrixStack.pop();
    }

    private void addLine(BufferBuilder buffer, Vector3d from, Vector3d to, int color) {
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = (color >> 24 & 0xFF) / 255.0f;

        buffer.pos(from.x, from.y, from.z).color(r, g, b, a).endVertex();
        buffer.pos(to.x, to.y, to.z).color(r, g, b, a).endVertex();
    }

    public static boolean shouldAttack2(LivingEntity livingTarget, boolean rayCast, boolean distanceCheck, boolean fallCheck, long cooldownMSOffset, float[] ranges) {
        if (distanceCheck && livingTarget != null && !mc.player.validDistance(livingTarget, ranges[0], true))
            return false;

        if (!msCooldownReached(cooldownMSOffset)) return false;

        boolean validNext = isBestMomentToHit(fallCheck);

        if (validNext && rayCast && !anyEntityOnRay(livingTarget, ranges[0]))
            validNext = false;

        return validNext;
    }

    public static boolean anyEntityOnRay(LivingEntity livingIn, double range) {
        return livingIn != null && RayTraceUtil.isViewEntity(livingIn, MathHelper.wrapDegrees(mc.player.rotationYaw), mc.player.rotationPitch, (float) range, true);
    }

    public static double convenientFallOffset() {
        double fallOffset = mc.player.fallDistance;
        if (mc.world != null && !mc.player.isOnGround() && mc.player.getMotion().y < -.0784000015258789D && !mc.world.getBlockState(mc.player.getPosition()).getMaterial().isLiquid() && !mc.world.getBlockState(mc.player.getPosition().up()).getMaterial().isLiquid()) {
            if (mc.player.fallDistance < -mc.player.getMotion().y && mc.player.ticksOnGround > 6)
                fallOffset = -mc.player.getMotion().y;
        }
        return fallOffset;
    }

    public static double getYCapacityOnPlayerPos(int rangeY) {
        if (mc.world == null) return 1.D;
        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        double minDst = rangeY * 2.D, dst;
        double maxY = 255, minY = -64;
        final float selfWD2 = mc.player.getWidth() / 2.F - 1E-2F;
        RayTraceResult first, second;
        for (final Vector3d vec : Arrays.asList(eyePos.add(-selfWD2, .0D, -selfWD2), eyePos.add(selfWD2, .0D, selfWD2), eyePos.add(selfWD2, .0D, -selfWD2), eyePos.add(-selfWD2, .0D, selfWD2))) {
            first = mc.world.rayTraceBlocks(new RayTraceContext(vec, vec.add(0.D, -rangeY, 0.D), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.ANY, mc.player));
            second = mc.world.rayTraceBlocks(new RayTraceContext(vec, vec.add(0.D, rangeY, 0.D), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.ANY, mc.player));
            if (maxY > second.getHitVec().y) maxY = second.getHitVec().y;
            if (minY < first.getHitVec().y) minY = first.getHitVec().y;
            dst = maxY - minY;
            if (minDst > dst) minDst = dst;
        }
        return minDst - mc.player.getHeight();
    }

    public static boolean isBestMomentToHit(boolean fallCheck) {
        if (Criticals.getInstance().isEnabled()) return true;
        if (!fallCheck) return true;
        float adaptiveFallValue = 0.F, maxFallOff = .2F;
        if (cpsBypassTrigger() && getYCapacityOnPlayerPos(2) > .20000004768371582D) {
            adaptiveFallValue = maxFallOff;
        }
        final boolean hasFall = convenientFallOffset() > adaptiveFallValue || getYCapacityOnPlayerPos(2) < .1F;
        if (hasFall) return true;
        final boolean badLiquidMoment = !mc.player.movementInput.jump && (mc.player.isInWater() || mc.player.isInLava()) || mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA) || mc.player.isMaterialInBB(Material.WEB);
        final boolean skipFallCheck =
                badLiquidMoment ||
                        (!mc.player.movementInput.jump && mc.player.ticksOnGround > 6) && Excellent.inst().moduleManager().get(AuraNew.class).smartCrits.getValue() ||
                        mc.player.isOnLadder() ||
                        mc.player.isElytraFlying() ||
                        mc.player.isRidingHorse() ||
                        mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.SLOW_FALLING) ||
                        mc.player.abilities.isFlying ||
                        mc.player.getPosY() < -64.D;
        return skipFallCheck;
    }

    @Getter
    private static final StopWatch cooldownTimer = new StopWatch();

    public static boolean msCooldownReached(long msOffset) {
        return cooldownTimer.finished(getMsCooldown() + msOffset);
    }

    public static long hitCounterCPSBypass;

    public static boolean cpsBypassTrigger() {
        return hitCounterCPSBypass % 7 == 3;
    }

    public static long getMsCooldown() {
        long msCooldown;
        double attributeAttackSpeed = mc.player.getAttributeValue(Attributes.ATTACK_SPEED);
        float maxDeviation = .2F;
        msCooldown = (long) ((1.F / attributeAttackSpeed) * 1000.F * (1.F - Math.min(maxDeviation, 1.F)));

        if (attributeAttackSpeed == 4.D || attributeAttackSpeed == 4.4000000059604645 || attributeAttackSpeed == 4.800000011920929)
            msCooldown = 450L;
        msCooldown = Math.max(msCooldown, 450L);

        if (cpsBypassTrigger()) msCooldown += 50L;
        return msCooldown;
    }

    public static boolean msCooldownHasMs(long ms) {
        return cooldownTimer.finished(ms);
    }

    public float[] getRanges() {
        return new float[]{attackRange.getValue(), preRange.getValue()};
    }

    public float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }

    public float randomLerp(float min, float max) {
        return Interpolator.lerp(max, min, new SecureRandom().nextFloat());
    }

    public float cooldownFromLastSwing() {
        return MathHelper.clamp(mc.player.ticksSinceLastSwing / randomLerp(8, 12), 0.0F, 1.0F);
    }

    private long getDynamicCooldown() {
        if (cooldownMode.is("Динамический")) {
            if (mc.player.getHeldItemMainhand().isEmpty()) {
                return 250L;
            } else if (mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) {
                return 550L;
            } else if (mc.player.getHeldItemMainhand().getItem() instanceof AxeItem) {
                return 850L;
            }
            return 450L;
        } else if (cooldownMode.is("Кастомный меч")) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) {
                return customSwordCooldown.getValue().longValue();
            }
            if (mc.player.getHeldItemMainhand().isEmpty()) {
                return 250L;
            } else if (mc.player.getHeldItemMainhand().getItem() instanceof AxeItem) {
                return 850L;
            }
            return 450L;
        }
        return getMsCooldown();
    }

    private void updateAttack() {
        if (componentMode.is("Training")) return;
        if (checkReturn()) return;

        if (shieldBreaker()) return;

        if (shouldAttack() && rayTrace() && AuraUtil.getStrictDistance(target) < attackDistance()) {
            if (onlyCrits.getValue()) {
            }

            boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
            boolean sprinting = mc.player.isSprinting();

            boolean isRageMode = sprintReset.is("Пакетный (RAGE)") || sprintReset.is("Обычный (RAGE)");

            if (!isInLiquid && sprinting) {
                if (sprintReset.is("Обычный")) {
                    p = 1;
                } else if (sprintReset.is("Легитный")) {
                    int releaseTicks = ThreadLocalRandom.current().nextInt(1, 2);
                    sprintTicks = releaseTicks;
                    int pressDelay = ThreadLocalRandom.current().nextInt(15, 51);
                    pressTime = System.currentTimeMillis() + pressDelay;
                } else if (sprintReset.is("Пакетный (RAGE)")) {
                    handlePacketSprintReset();
                } else if (sprintReset.is("Обычный (RAGE)")) {
                    handleInstantSprintReset();
                }

                if (!isRageMode && mc.player.isServerSprintState()) return;
            }

            Vector2f vec2f = RotationUtil.calculate(target);
            float distance = (float) AuraUtil.getStrictDistance(target);
            float maxPitchOffset = 20.0f;
            float pitchOffset = MathHelper.clamp(1.5f * (attackRange.getValue() - distance) / attackRange.getValue() * maxPitchOffset, 0.0f, maxPitchOffset);

            if (componentMode.is("ХолиВорлд")) {
                RotationComponent.update(new Rotation(vec2f.x, vec2f.y + pitchOffset), 350, 350, 0, 5);
            } else if (componentMode.is("Фантайм - снап")) {
                long currentTime = System.currentTimeMillis();
                float tryaskaX = (float) (Math.sin(currentTime / 50.0) * 10 + Math.sin(currentTime / 80.0) * 5);
                float tryaskaY = (float) (Math.cos(currentTime / 60.0) * 8 + Math.cos(currentTime / 90.0) * 4);
                float targetShakeX = tryaskaX;
                float targetShakeY = tryaskaY * 0.8f;
                float Xplavnostb = 1;
                float Yplavnostb = 1;
                float shlashivanie = 1f;

                Xplavnostb = Xplavnostb + (targetShakeX - Xplavnostb) * shlashivanie;
                Yplavnostb = Yplavnostb + (targetShakeY - Yplavnostb) * shlashivanie;

                vec2f = RotationUtil.calculate(target);

                int returnSpeed = ThreadLocalRandom.current().nextInt(40, 45);

                RotationComponent.update(
                        new Rotation(vec2f.x + Xplavnostb, vec2f.y + Yplavnostb),
                        180, returnSpeed, 0, 5
                );
            } else if (componentMode.is("Снапы")) {
                RotationComponent.update(new Rotation(vec2f.x, vec2f.y + pitchOffset), 350, 350, 0, 5);
            }

            if (attackMode.is("1.8")) {
                long currentTime = System.currentTimeMillis();
                float cps = dynamicCps.getValue();
                float variation = randomLerp(0.8f, 1.2f);
                long clickInterval = (long) (1000.0 / (cps * variation));
                if (currentTime - lastClickTime >= clickInterval) {
                    attackEntity(target);
                    lastClickTime = currentTime;
                }
            } else {
                if (randomCrits.getValue() && shouldCritical() && ThreadLocalRandom.current().nextFloat() < 0.3f) {
                    int critDelay = ThreadLocalRandom.current().nextInt(1, 3);
                    scheduler.schedule(() -> attackEntity(target), critDelay * 50L, TimeUnit.MILLISECONDS);
                } else {
                    attackEntity(target);
                }
            }

            count = (count + 1) % 2;
            canCrit = false;
            stopWatch.reset();
            recentHitTime = (int) System.currentTimeMillis();
        }
    }

    private void handlePacketSprintReset() {
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CEntityActionPacket(mc.player, net.minecraft.network.play.client.CEntityActionPacket.Action.STOP_SPRINTING));
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CEntityActionPacket(mc.player, net.minecraft.network.play.client.CEntityActionPacket.Action.START_SPRINTING));
    }

    private void handleInstantSprintReset() {
        mc.player.setSprinting(false);
        mc.player.setSprinting(true);
    }

    private void attackEntity(Entity entity) {
        long currentTime = System.currentTimeMillis();
        long cooldown = getDynamicCooldown();

        if (attackMode.is("1.9") && currentTime - lastAttackTime < cooldown) {
            return;
        }

        rotateGrim(true);

        if (componentMode.is("Фантайм - новая")) {
            TargetPoint attackPoint = currentTargetPoint;

            float recoilYaw = randomLerp(-15F, 15F) * fantimeVibeNoise.getValue();
            float recoilPitch = randomLerp(-8F, 6F) * fantimeVibeNoise.getValue();

            switch (attackPoint) {
                case HEAD:
                    recoilYaw *= 1.2F;
                    recoilPitch *= 0.8F;
                    break;
                case LEFT_ARM:
                    recoilYaw = Math.abs(recoilYaw) * -1;
                    break;
                case RIGHT_ARM:
                    recoilYaw = Math.abs(recoilYaw);
                    break;
                case LEGS:
                    recoilPitch = -Math.abs(recoilPitch);
                    break;
            }

            float currentYaw = mc.player.rotationYaw;
            float currentPitch = mc.player.rotationPitch;

            float jitterAmplitude = randomLerp(2.5F, 4.5F) * fantimeVibeShakeIntensity.getValue();
            long currentTimeRot = System.currentTimeMillis();

            float finalRecoilYaw = recoilYaw;
            float finalRecoilPitch = recoilPitch;
            script.cleanup()
                    .addTickStep(0, () -> {
                        float jitterYaw = (float) (Math.sin(currentTimeRot / 60.0) * jitterAmplitude);
                        float jitterPitch = (float) (Math.cos(currentTimeRot / 40.0) * (jitterAmplitude * 0.6F));

                        float finalYaw = currentYaw + finalRecoilYaw + jitterYaw;
                        float finalPitch = MathHelper.clamp(currentPitch + finalRecoilPitch + jitterPitch, -90F, 90F);

                        RotationComponent.update(new Rotation(finalYaw, finalPitch),
                                randomLerp(130F, 190F),
                                randomLerp(110F, 170F),
                                0, 5);
                    })
                    .addTickStep(1, () -> {
                        float postJitterYaw = (float) (Math.sin((currentTimeRot + 100) / 50.0) * (jitterAmplitude * 1.2F));
                        float postJitterPitch = (float) (Math.cos((currentTimeRot + 100) / 30.0) * (jitterAmplitude * 0.7F));

                        RotationComponent.update(new Rotation(
                                currentYaw + (finalRecoilYaw * randomLerp(0.3F, 0.5F)) + postJitterYaw,
                                MathHelper.clamp(currentPitch + (finalRecoilPitch * randomLerp(0.3F, 0.5F)) + postJitterPitch, -90F, 90F)
                        ), randomLerp(100F, 150F), randomLerp(90F, 140F), 0, 5);
                    })
                    .addTickStep(2, () -> {
                        float finalJitterYaw = (float) (Math.sin((currentTimeRot + 200) / 40.0) * (jitterAmplitude * 0.8F));
                        float finalJitterPitch = (float) (Math.cos((currentTimeRot + 200) / 25.0) * (jitterAmplitude * 0.5F));

                        RotationComponent.update(new Rotation(
                                currentYaw + (finalRecoilYaw * randomLerp(-0.2F, 0.2F)) + finalJitterYaw,
                                MathHelper.clamp(currentPitch + (finalRecoilPitch * randomLerp(-0.2F, 0.2F)) + finalJitterPitch, -90F, 90F)
                        ), randomLerp(80F, 120F), randomLerp(70F, 110F), 0, 5);

                        rotateGrim(false);
                    });

            lerpRotation = new Vector2f(
                    lerpRotation.x + randomLerp(-20F, 20F) * fantimeVibeNoise.getValue(),
                    MathHelper.clamp(lerpRotation.y + randomLerp(-15F, 15F) * fantimeVibeNoise.getValue(), -90F, 90F)
            );
        }

        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
        lastAttackTime = System.currentTimeMillis();
        if (!componentMode.is("Фантайм - новая")) {
            script.cleanup().addTickStep(0, () -> rotateGrim(false));
        }

        if (componentMode.is("Вулкан")) {
            long time = System.currentTimeMillis();
            float pitchWave = (float) (Math.sin(time / 400.0) * 4.0);
            float yawWave = (float) (Math.sin(time / 90.0) * 8.0);
            mc.player.rotationYaw += yawWave;
            mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch + pitchWave, -60F, 60F);
        }
    }

    private float getEffectiveAimDistance() {
        if (elytraTargetMode.getValue() && mc.player.isElytraFlying()) {
            return elytraDistance.getValue();
        }
        return attackRange.getValue() + preRange.getValue();
    }

    private void updateTarget() {
        float searchDistance = getEffectiveAimDistance();
        target = TargetComponent.getTarget(searchDistance);
        if (previousTarget != null && target == null) {
            lerpRotation = Vector2f.ZERO;
            RotationComponent.getInstance().smoothResetRotation();
        }
        previousTarget = target;
    }

    public Entity getCurrentTarget() {
        return target;
    }

    public void rotateGrim(boolean start) {
        if (componentMode.is("Грим") || componentMode.is("Фантайм - новая")) {
            if (start) {
                final Vector4f rotation = AuraUtil.calculateRotation(target);
                float yaw = rotation.x + randomLerp(-0.03f, 0.03f);
                float pitch = rotation.y + randomLerp(-0.03f, 0.03f);
                float distance = (float) AuraUtil.getStrictDistance(target);
                float maxPitchOffset = 20.0f;
                float pitchOffset = MathHelper.clamp(1.5f * (attackRange.getValue() - distance) / attackRange.getValue() * maxPitchOffset, 0.0f, maxPitchOffset);
                float yOffset = Criticals.getInstance().isEnabled() && !mc.player.isOnGround() ? 1e-6f : 0f;
                ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY() - yOffset, mc.player.getPosZ(), yaw, pitch + pitchOffset, mc.player.isOnGround());
            } else {
                ViaUtil.sendPositionPacket(mc.player.rotationYaw, mc.player.rotationPitch, false);
            }
        }
    }

    private float lastRequiredFallDistance;

    public boolean shouldAttack() {
        if (currentRotationElement instanceof FunTimeBypassRot) {
            ((FunTimeBypassRot) currentRotationElement).onAttack(this);
        }
        if (currentRotationElement instanceof LonyRot) {
            ((LonyRot) currentRotationElement).onAttack(this);
        }

        long cooldown = getDynamicCooldown();
        if (componentMode.is("Спукитайм")) {
            if (!perfectDelay.cooldownComplete(randomizeDelayForSpook(cooldown))) return false;
        } else {
            if (!perfectDelay.cooldownComplete(cooldown)) return false;
        }

        if (mc.player.isElytraFlying()) {
            return true;
        }

        Freezing freezing = Freezing.getInstance();
        if (freezing != null && freezing.isEnabled() && !mc.player.isOnGround()) {
            return true;
        }

        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();
        this.lastRequiredFallDistance = ThreadLocalRandom.current().nextFloat() * 0.01f;

        boolean isInLiquid = mc.player.isActualySwimming() ||
                mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) ||
                mc.player.areEyesInFluid(FluidTags.LAVA);

        if (isInLiquid) return true;

        if (onlyCrits.getValue() && !smartCrits.getValue()) {
            return shouldCritical()
                    && mc.player.fallDistance > 0
                    && canCrit
                    && isBlockAboveHead;
        }

        if (smartCrits.getValue()) {
            boolean canSmartCrit = isBlockAboveHead && !mc.player.movementInput.jump && !canCrit
                    && mc.player.isOnGround()
                    && mc.player.collidedVertically;

            return (shouldCritical() && mc.player.fallDistance > 0 && canCrit && isBlockAboveHead) || canSmartCrit;
        }

        if (randomCrits.getValue()) {
            return shouldCritical() || (mc.player.fallDistance > this.lastRequiredFallDistance && canCrit && isBlockAboveHead) || (ThreadLocalRandom.current().nextFloat() < 0.3f);
        }

        return true;
    }

    private boolean shieldBreaker() {
        if (!checks.getValue("Ломать щиты")) return false;
        Slot axeSlot = InvUtil.getAxeSlot();
        if (target != null && target.getActiveItemStack().getItem() == Items.SHIELD && axeSlot != null && rayTrace()) {
            int originalSlot = mc.player.inventory.currentItem;
            InvUtil.clickSlot(axeSlot, originalSlot, ClickType.SWAP, true);
            attackEntity(target);
            InvUtil.clickSlot(axeSlot, originalSlot, ClickType.SWAP, true);
            return true;
        }
        return false;
    }

    private boolean checkReturn() {
        boolean eating = mc.player.isHandActive() && checks.getValue("Не бить когда ешь");
        boolean noWeapon = !isHoldingWeapon() && checks.getValue("Бить только с оружием");
        return eating || noWeapon;
    }

    public boolean isHoldingWeapon() {
        return mc.player.getHeldItemMainhand().getItem() instanceof SwordItem
                || mc.player.getHeldItemMainhand().getItem() instanceof AxeItem;
    }

    private boolean shouldCritical() {
        boolean isDeBuffed = mc.player.isPotionActive(Effects.LEVITATION);
        boolean isInLiquid = mc.player.isActualySwimming() ||
                mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) ||
                mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean isFlying = mc.player.isElytraFlying();
        boolean isClimbing = mc.player.isOnLadder();
        boolean isCantJump = mc.player.isPassenger();
        boolean isOnWeb = PlayerUtil.isPlayerInWeb();

        return !(isDeBuffed || isInLiquid || isFlying || isClimbing || isCantJump || isOnWeb);
    }

    public boolean cooldownComplete() {
        return mc.player.getCooledAttackStrength(1.5F) >= 0.93F;
    }

    public boolean rayTrace() {
        // If rayTrace is disabled, always return true (no check)
        if (!enableRayTrace.getValue()) {
            return true;
        }

        if (target == null) {
            return false;
        }

        if (mc.player.isElytraFlying()) {
            return true;
        }

        return (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), target)
                || componentMode.is("трррр")
                || componentMode.is("fdh")
                || componentMode.is("fgh")
                || componentMode.is("Сhfgh"));
    }

    public double attackDistance() {
        if (onlyCrits.getValue() && mc.player.isElytraFlying()) {
            return attackRange.getValue();
        }
        return Math.max(mc.playerController.extendedReach() ? 6.0D : 3.0D, attackRange.getValue());
    }

    public ModeSetting smoothAttackMode() {
        return smoothAttackMode;
    }

    public SliderSetting smoothMsBeforeSnap() {
        return smoothMsBeforeSnap;
    }

    public SliderSetting smoothMsAfterSnap() {
        return smoothMsAfterSnap;
    }

    public SliderSetting smoothMinYawSpeed() {
        return smoothMinYawSpeed;
    }

    public SliderSetting smoothMaxYawSpeed() {
        return smoothMaxYawSpeed;
    }

    public SliderSetting smoothMinPitchSpeed() {
        return smoothMinPitchSpeed;
    }

    public SliderSetting smoothMaxPitchSpeed() {
        return smoothMaxPitchSpeed;
    }

    public ModeSetting smoothInterpolationType() {
        return smoothInterpolationType;
    }

    public SliderSetting smoothInterpolationStrength() {
        return smoothInterpolationStrength;
    }

    public BooleanSetting smoothUseMultipoint() {
        return smoothUseMultipoint;
    }

    public SliderSetting smoothMultipointMax() {
        return smoothMultipointMax;
    }

    public SliderSetting smoothMultipointMin() {
        return smoothMultipointMin;
    }

    public SliderSetting smoothMultipointSwitchSpeed() {
        return smoothMultipointSwitchSpeed;
    }

    public SliderSetting smoothMultipointSmoothness() {
        return smoothMultipointSmoothness;
    }

    public BooleanSetting smoothMultipointJerkTransition() {
        return smoothMultipointJerkTransition;
    }

    public BooleanSetting smoothMultipointStepTransition() {
        return smoothMultipointStepTransition;
    }

    public BooleanSetting smoothUseJitter() {
        return smoothUseJitter;
    }

    public ModeSetting smoothJitterType() {
        return smoothJitterType;
    }

    public SliderSetting smoothJitterRadius() {
        return smoothJitterRadius;
    }

    public SliderSetting smoothJitterSpeed() {
        return smoothJitterSpeed;
    }

    public SliderSetting smoothJitterInterpolation() {
        return smoothJitterInterpolation;
    }

    public BooleanSetting smoothJitterStep() {
        return smoothJitterStep;
    }

    public BooleanSetting smoothUsePitchLimit() {
        return smoothUsePitchLimit;
    }

    public SliderSetting smoothMaxPitch() {
        return smoothMaxPitch;
    }

    public SliderSetting smoothMinPitch() {
        return smoothMinPitch;
    }

    public BooleanSetting smoothUseStepRotation() {
        return smoothUseStepRotation;
    }

    public SliderSetting smoothStepYaw() {
        return smoothStepYaw;
    }

    public SliderSetting smoothStepPitch() {
        return smoothStepPitch;
    }

    private void reset() {
        TargetComponent.clearTarget();
        TargetComponent.updateTargetList();
        target = null;
        canCrit = false;
        lastTargetPos = Vector3d.ZERO;
        lastTargetVelocity = Vector3d.ZERO;
        adaptSmoothness = 0.5f;
        hitCounter = 0;
        shakeStartTime = 0;
        pizdeckolbasitLagosa = ThreadLocalRandom.current().nextFloat(50, 70);
        lastFrequencyUpdate = System.currentTimeMillis();
        combatAggressiveness = 0.5f;
        shouldSnapBack = false;
        currentTargetPoint = TargetPoint.HEAD;
        attackCounter = 0;
        pointSwitchTimer.reset();
        isRotating = false;
        targetRotation = new Vector2f(0.0F, 0.0F);
        startRotation = new Vector2f(0.0F, 0.0F);
        rotationDuration = 0f;
        lastAttackTime = 0;
        lastClickTime = 0;
        sprintTicks = 0;
        pressTime = 0;
    }

    private enum TargetPoint {
        HEAD,
        LEFT_ARM,
        RIGHT_ARM,
        TORSO,
        LEGS
    }
}
