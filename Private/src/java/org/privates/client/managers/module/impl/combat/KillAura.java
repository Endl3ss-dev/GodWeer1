package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.auraelement.IRotationElement;
import org.privates.client.managers.module.impl.combat.auraelement.impl.*;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.math.PerfectDelay;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.ViaUtil;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.rotation.AuraUtil;
import org.privates.client.utils.rotation.RayTraceUtil;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.client.utils.server.ServerTPS;
import org.privates.common.impl.taskript.Script;
import org.privates.lib.util.time.StopWatch;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Category1
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AuraNew", category = Category.COMBAT, description = "Автоматически атакует ближайших врагов.")
public class KillAura extends Module {

    public static KillAura getInstance() {
        return Instance.get(KillAura.class);
    }

    private final ModeSetting componentMode = new ModeSetting(this, "Ротации", "Custom", "Smooth", "Snap", "Smotion Snap", "ReallyWorld", "HVH", "Современная", "СпукиТайм", "HW", "Фантайм", "SpookyFast", "Vulcan", "Intave", "Matrix", "LonyGrief", "WellMine", "MineLegacy", "Test", "Повторяюшая");
    private final ModeSetting correctionType = new ModeSetting(this, "Тип коррекции", "Свободный", "Сфокусированный");
    private final MultiBooleanSetting esp23 = new MultiBooleanSetting(this, "Таргет есп", BooleanSetting.of("Призраки", false), BooleanSetting.of("Кругляшок", false), BooleanSetting.of("Круг", false), BooleanSetting.of("Феерверки", false));
    private final SliderSetting fov = new SliderSetting(this, "Угол обзора", 180F, 30F, 180F, 1F).setVisible(() -> !componentMode.is("Grim 1.16.5") && !componentMode.is("HW"));
    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция", 3F, 3F, 6F, 0.1F);
    private final SliderSetting preRange = new SliderSetting(this, "Доп дистанция", 1F, 0F, 3F, 0.1F).setVisible(() -> !componentMode.is("Grim 1.16.5") && !componentMode.is("HW"));
    private final SliderSetting elRange = new SliderSetting(this, "Дистанция атаки на элитре", 3f, 1f, 5f, 0.1f);
    private final BooleanSetting syncTPS = new BooleanSetting(this, "Синхронизация по TPS", false);
    private final SliderSetting tpsMultiplier = new SliderSetting(this, "Множитель TPS", 0.5f, 0.1f, 2.0f, 0.1f).setVisible(syncTPS::getValue);
    private final MultiBooleanSetting targetSettings = new MultiBooleanSetting(this, "Кого атаковать", BooleanSetting.of("Игроки", true), BooleanSetting.of("Мобы", true), BooleanSetting.of("Невидимые", true), BooleanSetting.of("Только в броне", false));
    private final BooleanSetting noHitThroughBlocks = new BooleanSetting(this, "Не бить через блоки", true);
    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false).setVisible(onlyCrits::getValue);
    private final BooleanSetting targetFollow = new BooleanSetting(this, "Таргетированный", false);
    private final BooleanSetting noTurnAfterOverrun = new BooleanSetting(this, "Не разворачиваться при обгоне", true);
    private final SliderSetting overrunDistance = new SliderSetting(this, "Дистанция обгона", 7.0f, 3.0f, 15.0f, 0.5f).setVisible(noTurnAfterOverrun::getValue);
    private final BooleanSetting smartDistance = new BooleanSetting(this, "Умная Дистанция", false);
    private final BooleanSetting smartHitbox = new BooleanSetting(this, "Улучшить хитбоксы", false);
    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Прочее", BooleanSetting.of("Отображать FOV", false).setVisible(() -> !componentMode.is("Grim 1.16.5") && !componentMode.is("HW")), BooleanSetting.of("Не бить когда ешь", false), BooleanSetting.of("Бить только с оружием", false), BooleanSetting.of("Выключить после смерти", true));
    private final BooleanSetting customRotationEnabled = new BooleanSetting(this, "Включить кастом", true).setVisible(() -> componentMode.is("Custom"));
    private final ModeSetting aimPointMode = new ModeSetting(this, "Режим точки прицеливания", "Динамический", "Ближайшая", "Циклический", "Случайный", "Взвешенный", "Прогнозирующий").setVisible(customRotationEnabled::getValue);
    private final SliderSetting bodyParts = new SliderSetting(this, "Части тела", 6f, 1f, 24f, 1f).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting smoothAimPointTransition = new BooleanSetting(this, "Плавный переход точки", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting aimPointTransitionSpeed = new SliderSetting(this, "Скорость перехода", 0.2f, 0.01f, 1.0f, 0.01f).setVisible(() -> customRotationEnabled.getValue() && smoothAimPointTransition.getValue());
    private final ModeSetting verticalAimBias = new ModeSetting(this, "Вертикальное смещение", "Нет", "Голова", "Верхняя грудь", "Грудь", "Живот", "Ноги", "Адаптивное").setVisible(customRotationEnabled::getValue);
    private final SliderSetting verticalBiasStrength = new SliderSetting(this, "Сила смещения", 0.5f, 0.0f, 1.0f, 0.01f).setVisible(() -> customRotationEnabled.getValue() && !verticalAimBias.is("Нет"));
    private final BooleanSetting horizontalBias = new BooleanSetting(this, "Горизонтальное смещение", false).setVisible(customRotationEnabled::getValue);
    private final ModeSetting horizontalBiasMode = new ModeSetting(this, "Режим гор. смещения", "Центр", "Левая сторона", "Правая сторона", "Случайная сторона", "Циклическое").setVisible(() -> customRotationEnabled.getValue() && horizontalBias.getValue());
    private final SliderSetting horizontalBiasStrength = new SliderSetting(this, "Сила гор. смещения", 0.3f, 0.0f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && horizontalBias.getValue());
    private final BooleanSetting enablePrediction = new BooleanSetting(this, "Предсказание движения", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting predictionFactor = new SliderSetting(this, "Сила предсказания", 1.2f, 0.0f, 10.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue());
    private final BooleanSetting pingPrediction = new BooleanSetting(this, "Предсказание по пингу", true).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue());
    private final BooleanSetting velocityPrediction = new BooleanSetting(this, "Учет ускорения цели", false).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue());
    private final SliderSetting velocityWeight = new SliderSetting(this, "Вес ускорения", 0.5f, 0.1f, 2.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue() && velocityPrediction.getValue());
    private final BooleanSetting strafePredict = new BooleanSetting(this, "Предсказание стрейфов", false).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue());
    private final SliderSetting strafePredictStrength = new SliderSetting(this, "Сила предсказания стрейфов", 0.7f, 0.1f, 2.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enablePrediction.getValue() && strafePredict.getValue());
    private final SliderSetting yawSpeed = new SliderSetting(this, "Скорость Yaw", 180f, 1f, 360f, 1f).setVisible(customRotationEnabled::getValue);
    private final SliderSetting pitchSpeed = new SliderSetting(this, "Скорость Pitch", 90f, 1f, 180f, 1f).setVisible(customRotationEnabled::getValue);
    private final SliderSetting minRotationSpeed = new SliderSetting(this, "Мин. скорость", 5f, 1f, 100f, 1f).setVisible(customRotationEnabled::getValue);
    private final SliderSetting maxRotationSpeed = new SliderSetting(this, "Макс. скорость", 300f, 50f, 360f, 1f).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting adaptiveSpeed = new BooleanSetting(this, "Адаптивная скорость", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting accelerationFactor = new SliderSetting(this, "Множитель ускорения", 1.5f, 1.0f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && adaptiveSpeed.getValue());
    private final BooleanSetting distanceBasedSpeed = new BooleanSetting(this, "Скорость от дистанции", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting closeRangeMultiplier = new SliderSetting(this, "Множитель вблизи", 0.7f, 0.3f, 2.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && distanceBasedSpeed.getValue());
    private final SliderSetting farRangeMultiplier = new SliderSetting(this, "Множитель вдали", 1.3f, 0.5f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && distanceBasedSpeed.getValue());
    private final BooleanSetting targetVelocityMatching = new BooleanSetting(this, "Подстройка под скорость цели", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting velocityMatchFactor = new SliderSetting(this, "Сила подстройки", 0.5f, 0.0f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && targetVelocityMatching.getValue());
    private final BooleanSetting angleBasedSpeed = new BooleanSetting(this, "Скорость от угла", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting smallAngleMultiplier = new SliderSetting(this, "Множитель малых углов", 0.6f, 0.2f, 2.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && angleBasedSpeed.getValue());
    private final SliderSetting largeAngleMultiplier = new SliderSetting(this, "Множитель больших углов", 1.5f, 0.5f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && angleBasedSpeed.getValue());
    private final SliderSetting rotationSmoothness = new SliderSetting(this, "Плавность ротации", 0.5f, 0.0f, 1.0f, 0.01f).setVisible(customRotationEnabled::getValue);
    private final SliderSetting rotationSmoothingStrength = new SliderSetting(this, "Сила сглаживания", 1.0f, 0.1f, 3.0f, 0.05f).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting dynamicSmoothness = new BooleanSetting(this, "Динамическая плавность", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting smallAngleSmoothness = new SliderSetting(this, "Плавность на малых углах", 1.2f, 0.3f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && dynamicSmoothness.getValue());
    private final SliderSetting largeAngleSmoothness = new SliderSetting(this, "Плавность на больших углов", 0.8f, 0.3f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && dynamicSmoothness.getValue());
    private final ModeSetting interpolationType = new ModeSetting(this, "Тип интерполяции", "Линейная", "Плавная", "Экспоненциальная", "Синусоидальная", "Кубическая", "Квадратичная", "Elastic", "Bounce").setVisible(customRotationEnabled::getValue);
    private final SliderSetting rotationAcceleration = new SliderSetting(this, "Ускорение ротации", 1.0f, 0.1f, 10.0f, 0.1f).setVisible(customRotationEnabled::getValue);
    private final SliderSetting rotationDeceleration = new SliderSetting(this, "Замедление ротации", 1.0f, 0.1f, 10.0f, 0.1f).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting momentumBased = new BooleanSetting(this, "На основе инерции", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting momentumDecay = new SliderSetting(this, "Затухание инерции", 0.9f, 0.5f, 0.99f, 0.01f).setVisible(() -> customRotationEnabled.getValue() && momentumBased.getValue());
    private final BooleanSetting humanize = new BooleanSetting(this, "Гуманизация", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting humanizePower = new SliderSetting(this, "Сила гуманизации", 1.0f, 0.0f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSetting reactionTime = new SliderSetting(this, "Время реакции (мс)", 20f, 0f, 500f, 5f).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSetting reactionTimeVariation = new SliderSetting(this, "Разброс реакции (мс)", 10f, 0f, 200f, 5f).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final BooleanSetting simulateInaccuracy = new BooleanSetting(this, "Симуляция неточности", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting overshootChance = new SliderSetting(this, "Шанс промаха (%)", 10f, 0f, 100f, 1f).setVisible(() -> customRotationEnabled.getValue() && simulateInaccuracy.getValue());
    private final SliderSetting overshootStrength = new SliderSetting(this, "Сила промаха", 1.5f, 0.1f, 10.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && simulateInaccuracy.getValue());
    private final SliderSetting correctionDelay = new SliderSetting(this, "Задержка коррекции (мс)", 50f, 0f, 300f, 10f).setVisible(() -> customRotationEnabled.getValue() && simulateInaccuracy.getValue());
    private final BooleanSetting randomPauses = new BooleanSetting(this, "Случайные паузы", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting pauseChance = new SliderSetting(this, "Шанс паузы (%)", 5f, 0f, 50f, 1f).setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final SliderSetting minPauseDuration = new SliderSetting(this, "Мин. пауза (мс)", 50f, 10f, 500f, 10f).setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final SliderSetting maxPauseDuration = new SliderSetting(this, "Макс. пауза (мс)", 150f, 50f, 1000f, 10f).setVisible(() -> customRotationEnabled.getValue() && randomPauses.getValue());
    private final BooleanSetting simulateFatigue = new BooleanSetting(this, "Симулировать усталость", true).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue());
    private final SliderSetting fatigueTime = new SliderSetting(this, "Время до усталости (сек)", 30f, 5f, 180f, 1f).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue() && simulateFatigue.getValue());
    private final SliderSetting fatigueEffect = new SliderSetting(this, "Эффект усталости", 0.3f, 0.1f, 0.9f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && humanize.getValue() && simulateFatigue.getValue());
    private final BooleanSetting aimDrift = new BooleanSetting(this, "Дрейф прицела", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting driftStrength = new SliderSetting(this, "Сила дрейфа", 0.5f, 0.1f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && aimDrift.getValue());
    private final SliderSetting driftSpeed = new SliderSetting(this, "Скорость дрейфа", 0.5f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && aimDrift.getValue());
    private final BooleanSetting enableJitter = new BooleanSetting(this, "Джиттер", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting jitterStrength = new SliderSetting(this, "Сила джиттера", 1.0f, 0.0f, 10.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSetting jitterSpeed = new SliderSetting(this, "Скорость джиттера", 1.0f, 0.1f, 20.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSetting jitterRadius = new SliderSetting(this, "Радиус джиттера", 3.0f, 0.0f, 10.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final SliderSetting jitterPatternChangeSpeed = new SliderSetting(this, "Скорость замены паттернов", 0.5f, 0.0f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterNoRepeatPatterns = new BooleanSetting(this, "Не повторять прошлые паттерны", true).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterSyncWithMultipoint = new BooleanSetting(this, "Синхронизировать с Multipoint", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterYawOnly = new BooleanSetting(this, "Джиттер только Yaw", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting jitterPitchOnly = new BooleanSetting(this, "Джиттер только Pitch", false).setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final ModeSetting jitterType = new ModeSetting(this, "Тип джиттера", "Случайный", "Синус", "Косинус", "Спираль", "Perlin Noise", "Simplex Noise", "Комбинированный", "Волновой").setVisible(() -> customRotationEnabled.getValue() && enableJitter.getValue());
    private final BooleanSetting enableMicroMovements = new BooleanSetting(this, "Микродвижения", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting microMovementStrength = new SliderSetting(this, "Сила микродвижений", 0.5f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final SliderSetting microMovementFrequency = new SliderSetting(this, "Частота микродвижений", 0.02f, 0.001f, 0.2f, 0.001f).setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final ModeSetting microMovementPattern = new ModeSetting(this, "Паттерн микродвижений", "Хаотичный", "Круговой", "Восьмерка", "Зигзаг", "Perlin").setVisible(() -> customRotationEnabled.getValue() && enableMicroMovements.getValue());
    private final BooleanSetting enableFlicking = new BooleanSetting(this, "Флики", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting flickChance = new SliderSetting(this, "Шанс флика (%)", 5f, 0f, 100f, 1f).setVisible(() -> customRotationEnabled.getValue() && enableFlicking.getValue());
    private final SliderSetting flickSpeedMultiplier = new SliderSetting(this, "Множитель скорости флика", 3.0f, 1.0f, 20.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && enableFlicking.getValue());
    private final SliderSetting flickDistance = new SliderSetting(this, "Дистанция флика (градусы)", 15f, 5f, 90f, 1f).setVisible(() -> customRotationEnabled.getValue() && enableFlicking.getValue());
    private final BooleanSetting instantSnap = new BooleanSetting(this, "Моментальный поворот", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting snapChance = new SliderSetting(this, "Шанс снапа (%)", 15f, 1f, 100f, 1f).setVisible(() -> customRotationEnabled.getValue() && instantSnap.getValue());
    private final SliderSetting snapSmoothness = new SliderSetting(this, "Плавность снапа", 0.7f, 0.0f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && instantSnap.getValue());
    private final BooleanSetting legacyRotation = new BooleanSetting(this, "Легаси ротация", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting legacyYawStep = new SliderSetting(this, "Шаг Yaw", 15f, 1f, 90f, 1f).setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final SliderSetting legacyPitchStep = new SliderSetting(this, "Шаг Pitch", 10f, 1f, 60f, 1f).setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final BooleanSetting smoothLegacy = new BooleanSetting(this, "Плавный легаси", true).setVisible(() -> customRotationEnabled.getValue() && legacyRotation.getValue());
    private final BooleanSetting useGCD = new BooleanSetting(this, "GCD", true).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting strictGCD = new BooleanSetting(this, "Строгий GCD", false).setVisible(() -> customRotationEnabled.getValue() && useGCD.getValue());
    private final BooleanSetting limitRotation = new BooleanSetting(this, "Ограничить угол", true).setVisible(customRotationEnabled::getValue);
    private final SliderSetting maxYaw = new SliderSetting(this, "Макс. угол Yaw", 180f, 1f, 180f, 1f).setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final SliderSetting maxPitch = new SliderSetting(this, "Макс. угол Pitch", 90f, 1f, 90f, 1f).setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final BooleanSetting smoothLimit = new BooleanSetting(this, "Плавное ограничение", true).setVisible(() -> customRotationEnabled.getValue() && limitRotation.getValue());
    private final ModeSetting rotationTiming = new ModeSetting(this, "Тайминг ротации", "Постоянная", "До и после удара", "При ударе", "Nexus").setVisible(customRotationEnabled::getValue);
    private final SliderSetting rotationBeforeHit = new SliderSetting(this, "Ротация до удара (мс)", 15f, 0f, 200f, 5f).setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final SliderSetting rotationAfterHit = new SliderSetting(this, "Ротация после удара (мс)", 15f, 0f, 200f, 5f).setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final BooleanSetting randomRotationStart = new BooleanSetting(this, "Случайное начало ротации", false).setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара"));
    private final SliderSetting randomRotationOffset = new SliderSetting(this, "Смещение начала (градусы)", 30f, 5f, 180f, 5f).setVisible(() -> customRotationEnabled.getValue() && rotationTiming.is("До и после удара") && randomRotationStart.getValue());

    private final BooleanSetting spin360AfterHit = new BooleanSetting(this, "Крутиться 360° после удара", false).setVisible(() -> customRotationEnabled.getValue() && (rotationTiming.is("До и после удара") || rotationTiming.is("При ударе")));
    private final SliderSetting spin360Speed = new SliderSetting(this, "Скорость вращения 360°", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && spin360AfterHit.getValue());

    private final BooleanSetting deflectAfterHit = new BooleanSetting(this, "Отводить ротацию после удара", false).setVisible(customRotationEnabled::getValue);
    private final ModeSetting deflectionMode = new ModeSetting(this, "Режим отводки", "Только вправо", "Только влево", "Только вверх", "Рандомное место").setVisible(() -> customRotationEnabled.getValue() && deflectAfterHit.getValue());
    private final SliderSetting deflectionDegrees = new SliderSetting(this, "Градусы отводки", 30f, 5f, 180f, 5f).setVisible(() -> customRotationEnabled.getValue() && deflectAfterHit.getValue());
    private final SliderSetting deflectionSpeed = new SliderSetting(this, "Скорость отводки", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && deflectAfterHit.getValue());

    private final BooleanSetting advancedAntiCheat = new BooleanSetting(this, "Продвинутый обход АЧ", false).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting randomizeRotationPattern = new BooleanSetting(this, "Рандомизация паттернов", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final SliderSetting patternChangeInterval = new SliderSetting(this, "Интервал смены паттерна (сек)", 5f, 1f, 30f, 1f).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && randomizeRotationPattern.getValue());
    private final BooleanSetting mouseAcceleration = new BooleanSetting(this, "Симуляция ускорения мыши", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final SliderSetting mouseAccelStrength = new SliderSetting(this, "Сила ускорения мыши", 1.0f, 0.5f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && mouseAcceleration.getValue());
    private final BooleanSetting pixelPerfect = new BooleanSetting(this, "Пиксельная точность", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final BooleanSetting eyeTracking = new BooleanSetting(this, "Имитация отслеживания взглядом", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue());
    private final SliderSetting eyeTrackingLag = new SliderSetting(this, "Задержка взгляда (мс)", 30f, 0f, 150f, 5f).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && eyeTracking.getValue());
    private final BooleanSetting contextAware = new BooleanSetting(this, "Контекстная осведомленность", false).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting combatMode = new BooleanSetting(this, "Боевой режим", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && contextAware.getValue());
    private final BooleanSetting stealthMode = new BooleanSetting(this, "Скрытный режим", false).setVisible(() -> customRotationEnabled.getValue() && advancedAntiCheat.getValue() && contextAware.getValue());
    private final BooleanSetting naturalSway = new BooleanSetting(this, "Естественное покачивание", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting swayStrength = new SliderSetting(this, "Сила покачивания", 0.3f, 0.1f, 2.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && naturalSway.getValue());
    private final SliderSetting swayFrequency = new SliderSetting(this, "Частота покачивания", 0.5f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && naturalSway.getValue());
    private final BooleanSetting breathingEffect = new BooleanSetting(this, "Эффект дыхания", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting breathingRate = new SliderSetting(this, "Частота дыхания (вд/мин)", 15f, 5f, 30f, 1f).setVisible(() -> customRotationEnabled.getValue() && breathingEffect.getValue());
    private final SliderSetting breathingIntensity = new SliderSetting(this, "Интенсивность дыхания", 0.2f, 0.05f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && breathingEffect.getValue());
    private final BooleanSetting targetSwitchDelay = new BooleanSetting(this, "Задержка смены цели", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting minSwitchDelay = new SliderSetting(this, "Мин. задержка смены (мс)", 50f, 0f, 500f, 10f).setVisible(() -> customRotationEnabled.getValue() && targetSwitchDelay.getValue());
    private final SliderSetting maxSwitchDelay = new SliderSetting(this, "Макс. задержка смены (мс)", 150f, 50f, 1000f, 10f).setVisible(() -> customRotationEnabled.getValue() && targetSwitchDelay.getValue());
    private final BooleanSetting smoothTargetSwitch = new BooleanSetting(this, "Плавная смена цели", true).setVisible(() -> customRotationEnabled.getValue() && targetSwitchDelay.getValue());
    private final BooleanSetting aimAssist = new BooleanSetting(this, "Помощь в прицеливании", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting aimAssistStrength = new SliderSetting(this, "Сила помощи", 0.3f, 0.1f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && aimAssist.getValue());
    private final SliderSetting aimAssistRange = new SliderSetting(this, "Радиус помощи (градусы)", 10f, 1f, 45f, 1f).setVisible(() -> customRotationEnabled.getValue() && aimAssist.getValue());
    private final BooleanSetting dynamicSensitivity = new BooleanSetting(this, "Динамическая чувствительность", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting sensitivityNear = new SliderSetting(this, "Чувствительность вблизи", 0.8f, 0.3f, 2.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && dynamicSensitivity.getValue());
    private final SliderSetting sensitivityFar = new SliderSetting(this, "Чувствительность вдали", 1.2f, 0.5f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && dynamicSensitivity.getValue());
    private final BooleanSetting lockOnTarget = new BooleanSetting(this, "Захват цели", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting lockStrength = new SliderSetting(this, "Сила захвата", 0.7f, 0.1f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && lockOnTarget.getValue());
    private final SliderSetting lockBreakDistance = new SliderSetting(this, "Дистанция разрыва захвата", 8f, 3f, 20f, 0.5f).setVisible(() -> customRotationEnabled.getValue() && lockOnTarget.getValue());
    private final BooleanSetting rotationHistory = new BooleanSetting(this, "История ротаций", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting historySize = new SliderSetting(this, "Размер истории", 10f, 3f, 50f, 1f).setVisible(() -> customRotationEnabled.getValue() && rotationHistory.getValue());
    private final BooleanSetting predictiveHistory = new BooleanSetting(this, "Предсказательная история", false).setVisible(() -> customRotationEnabled.getValue() && rotationHistory.getValue());
    private final BooleanSetting angleNormalization = new BooleanSetting(this, "Нормализация углов", true).setVisible(customRotationEnabled::getValue);
    private final BooleanSetting smoothAngleWrapping = new BooleanSetting(this, "Плавный переход через 360°", true).setVisible(() -> customRotationEnabled.getValue() && angleNormalization.getValue());
    private final BooleanSetting rotationBlending = new BooleanSetting(this, "Смешивание ротаций", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting blendFactor = new SliderSetting(this, "Фактор смешивания", 0.5f, 0.1f, 1.0f, 0.05f).setVisible(() -> customRotationEnabled.getValue() && rotationBlending.getValue());
    private final ModeSetting blendMode = new ModeSetting(this, "Режим смешивания", "Линейный", "Взвешенный", "Адаптивный").setVisible(() -> customRotationEnabled.getValue() && rotationBlending.getValue());
    private final BooleanSetting priorityTargeting = new BooleanSetting(this, "Приоритетное наведение", false).setVisible(customRotationEnabled::getValue);
    private final ModeSetting priorityMode = new ModeSetting(this, "Режим приоритета", "Ближайший", "Слабейший", "Опасный", "Низкое HP").setVisible(() -> customRotationEnabled.getValue() && priorityTargeting.getValue());
    private final BooleanSetting leadTarget = new BooleanSetting(this, "Упреждение цели", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting leadAmount = new SliderSetting(this, "Величина упреждения", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && leadTarget.getValue());
    private final BooleanSetting gravityCompensation = new BooleanSetting(this, "gravity copmress", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting gravityFactor = new SliderSetting(this, "Фактор гравитации", 1.0f, 0.1f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && gravityCompensation.getValue());
    private final BooleanSetting windCompensation = new BooleanSetting(this, "Compress Wind", false).setVisible(customRotationEnabled::getValue);
    private final SliderSetting windStrength = new SliderSetting(this, "Wind", 0.5f, 0.1f, 3.0f, 0.1f).setVisible(() -> customRotationEnabled.getValue() && windCompensation.getValue());

    public LivingEntity target;
    public Vector2f lerpRotation = Vector2f.ZERO;
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
    public long pauseEndTime;
    public long lastReactionTime;
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
    public Vector3d lastTargetVelocity;
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
    private final BooleanSetting missDetect = new BooleanSetting(this, "Miss Detect", false);
    private LivingEntity lastAttackedTarget;
    private float healthBeforeAttack;
    private int missCounter;
    private boolean rotationEnabled = true;


    public final Script script = new Script();
    public final PerfectDelay perfectDelay = new PerfectDelay();
    public final StopWatch stopWatch = new StopWatch();
    public final SecureRandom secureRandom = new SecureRandom();
    public static final ServerTPS serverTPS = new ServerTPS();
    public final Random snapRandom = new Random();

    private Map<String, IRotationElement> rotationElements;
    private IRotationElement currentRotationElement;

    public interface ICustomAttackHandler {
        void handleAttack(KillAura parentModule, Entity entity, Minecraft mc);
    }

    public BooleanSetting enableJitter() {
        return enableJitter;
    }

    public SliderSetting jitterStrength() {
        return jitterStrength;
    }

    public SliderSetting jitterSpeed() {
        return jitterSpeed;
    }

    public SliderSetting jitterRadius() {
        return jitterRadius;
    }

    public SliderSetting jitterPatternChangeSpeed() {
        return jitterPatternChangeSpeed;
    }

    public BooleanSetting jitterNoRepeatPatterns() {
        return jitterNoRepeatPatterns;
    }

    public BooleanSetting jitterSyncWithMultipoint() {
        return jitterSyncWithMultipoint;
    }

    public BooleanSetting jitterYawOnly() {
        return jitterYawOnly;
    }

    public BooleanSetting jitterPitchOnly() {
        return jitterPitchOnly;
    }


    @Override
    public void toggle() {
        super.toggle();
        if (isEnabled()) {
            initRotationElements();
        }
        reset();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        initRotationElements();
        reset();
        //if (currentRotationElement != null) {
        //    currentRotationElement.onEnable(this);
    }


    @Override
    public void onDisable() {
        super.onDisable();
        reset();
        RotationComponent.getInstance().resetRotation();
    }

    public boolean shouldPlayerFalling() {
        return AuraUtil.isPlayerFalling(onlyCrits.getValue(), smartCrits.getValue());

    }


    private void initRotationElements() {
        if (rotationElements == null) {
            rotationElements = new HashMap<>();
            rotationElements.put("Custom", new CustomRot());
            rotationElements.put("RSpooky", new RockStarSpookyRot());
            rotationElements.put("Фантайм", new FTRot());
            rotationElements.put("Test", new HolyWorldNew());
            rotationElements.put("СпукиТайм", new SpookyTimeNewRot());
            rotationElements.put("Повторяюшая", new RepetitiveRot());
            rotationElements.put("RW", new ArbuzRWRot());
            rotationElements.put("Intave", new IntaveRot());
            rotationElements.put("IntaveR", new RockStarIntaveRot());
            rotationElements.put("Matrix", new MatrixRot());
            rotationElements.put("HW", new HWRot());
            rotationElements.put("SpookyFast", new CelestialRot());
            rotationElements.put("NSpooky", new NursultanSpookyRot());
            rotationElements.put("MineLegacy", new MineLegacyRot());
            rotationElements.put("WellMine", new WellMineRot());


            rotationElements.put("HVH", new ReallyWorldRot());

            rotationElements.put("Воровская", new StealingRotation());

            rotationElements.put("LegendsGrief", new LegendsGriefRot());
            rotationElements.put("EsSpooky", new EssenseSpookyRot());
            rotationElements.put("Smooth", new DeltaFuntimeRot());
            rotationElements.put("Smotion Snap", new SmoothSnapRot());
            rotationElements.put("Современная", new HumanizedRot());


            rotationElements.put("Самостоятельная", new CreatedRotationElement());
        }
        IRotationElement newElement = rotationElements.get(componentMode.getValue());
        if (newElement != null && newElement != currentRotationElement) {
            // if (currentRotationElement != null) {
            //     currentRotationElement.onDisable(this);
            // }
            // currentRotationElement = newElement;
            // currentRotationElement.onEnable(this);
        }
    }

    public BooleanSetting randomRotationStart() {
        return randomRotationStart;
    }

    public SliderSetting randomRotationOffset() {
        return randomRotationOffset;
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket) {
            perfectDelay.reset(550L);
        } else if (packet instanceof CAnimateHandPacket) {
            perfectDelay.reset(450L);
        }
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        reset();
        //  if (currentRotationElement != null) {
        //      currentRotationElement.onEnable(this);
        //  }
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
        // if (currentRotationElement != null) {
        //     currentRotationElement.onEnable(this);
        // }
    }

    public Vector2f getLerpRotation() {
        return this.lerpRotation;
    }

    public void setLerpRotation(Vector2f lerpRotation) {
        this.lerpRotation = lerpRotation;
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (target == null || !checks.getValue("Отображать FOV")) return;
        MatrixStack matrix = event.getMatrix();
        float baseFov = this.fov.getValue();
        float calcFov = (float) AuraUtil.calculateFOVFromCamera(target);
        float centerX = mw.getScaledWidth() / 2F;
        float centerY = mw.getScaledHeight() / 2F;
        boolean allow = calcFov < baseFov;
        float delta = Math.max(5F, calcFov - baseFov);
        if (!allow) {
            float percent = delta / (fov.max - baseFov);
            int calcColor = ColorUtil.multAlpha(ColorUtil.RED, 1F - percent);
            RenderUtil.Rounded.roundedOutline(matrix, centerX - calcFov, centerY - calcFov, calcFov * 2F, calcFov * 2F, delta, calcColor, Round.of(calcFov - delta));
        }
        int baseColor = allow ? ColorUtil.GREEN : ColorUtil.RED;
        float outline = allow ? 5F : delta;
        RenderUtil.Rounded.roundedOutline(matrix, centerX - baseFov, centerY - baseFov, baseFov * 2F, baseFov * 2F, outline, baseColor, Round.of(baseFov - outline));
    }

    @EventHandler
    public void onEvent(MoveEvent event) {
        if (target == null || mc.player == null || mc.world == null) {
            canCrit = false;
            return;
        }
        final boolean fallCheck = mc.player.nextFallDistance > ThreadLocalRandom.current().nextFloat() * 0.15;
        canCrit = !event.isToGround() && event.getFrom().y > event.getTo().y && fallCheck;
    }


    @EventHandler
    public void onEvent(UpdateEvent event) {
        updateTarget();
        updateAttack();
    }


    public float calculatePingFactor() {
        return Optional.ofNullable(mc.getConnection())
                .map(connection -> connection.getPlayerInfo(mc.player.getGameProfile().getId()))
                .map(NetworkPlayerInfo::getResponseTime)
                .map(ping -> ping / 50.0f)
                .orElse(1.0f);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(GameUpdateEvent event) {
        if (target == null) return;
        if (!rotationEnabled) {
            return;
        }
        ElytraTarget elytraTarget = Excellent.inst().moduleManager().get(ElytraTarget.class);
        if (elytraTarget != null && elytraTarget.isEnabled() && mc.player.isElytraFlying()) {
            return;
        }

        if (currentRotationElement != null) {
            if (targetFollow.getValue()) {
                intelligentTargetTracking();
            } else {
                //      currentRotationElement.updateRotation(this, target, mc, lerpRotation);
            }
        }


    }


    public Vector3d getVisibleTargetPoint() {
        if (target == null) return null;
        AxisAlignedBB box = target.getBoundingBox();
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        double[][] points = {
                {box.getCenter().x, box.maxY - 0.1, box.getCenter().z},
                {box.getCenter().x, box.getCenter().y, box.getCenter().z},
                {box.getCenter().x, box.minY + target.getHeight() * 0.75, box.getCenter().z},
                {box.minX + 0.1, box.maxY - 0.2, box.minZ + 0.1},
                {box.maxX - 0.1, box.maxY - 0.2, box.minZ + 0.1},
                {box.minX + 0.1, box.maxY - 0.2, box.maxZ - 0.1},
                {box.maxX - 0.1, box.maxY - 0.2, box.maxZ - 0.1},
                {box.getCenter().x, box.minY + target.getHeight() * 0.5, box.getCenter().z},
        };
        Vector3d bestPoint = null;
        double bestDistance = Double.MAX_VALUE;
        for (double[] point : points) {
            Vector3d checkPoint = new Vector3d(point[0], point[1], point[2]);
            if (isPointVisible(checkPoint)) {
                double distance = eyePos.distanceTo(checkPoint);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPoint = checkPoint;
                }
            }
        }
        return bestPoint != null ? bestPoint : target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
    }


    public boolean isPointVisible(Vector3d point) {
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                eyePos, point, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player
        ));
        return result.getType() == RayTraceResult.Type.MISS;
    }


    public float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }

    public float randomLerp(float min, float max) {
        return Interpolator.lerp(max, min, secureRandom.nextFloat());
    }


    public float cooldownFromLastSwing() {
        return MathHelper.clamp(mc.player.ticksSinceLastSwing / randomLerp(8, 12), 0.0F, 1.0F);
    }

    int p = 1;
    private final ModeSetting attackMode = new ModeSetting(this, "Режим ударов", "1.9", "1.8");
    private long lastClickTime = 0;
    private final SliderSetting dynamicCps = new SliderSetting(this, "CPS (1.8)", 12F, 8F, 20F, 1F).setVisible(() -> attackMode.is("1.8"));
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final BooleanSetting randomCrits = new BooleanSetting(this, "Рандом криты", false).setVisible(onlyCrits::getValue);
    private int count;
    private int recentHitTime;


    private void updateAttack() {
        if (shouldAttack() && rayTrace() && AuraUtil.getStrictDistance(target) < attackDistance()) {
            boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
            boolean sprinting = mc.player.isSprinting();
            if (!isInLiquid && sprinting) {
                p = 1;
                if (mc.player.isServerSprintState()) return;
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
                // Dynamic CPS for 1.8 mode
                long currentTime = System.currentTimeMillis();
                float cps = dynamicCps.getValue();
                float variation = randomLerp(0.8f, 1.2f); // Random variation for natural clicking
                long clickInterval = (long) (1000.0 / (cps * variation)); // Interval in milliseconds
                if (currentTime - lastClickTime >= clickInterval) {
                    attackEntity(target);
                    lastClickTime = currentTime;
                }
            } else {
                // 1.9-style attacks with cooldown
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
        } else {
            if (!mc.player.canEntityBeSeen(target) && PlayerUtil.isHoly()) {
                RotationComponent.update(new Rotation(Rotation.cameraYaw(), 90), 350, 350, 0, 5);
            }
        }
    }


    private boolean shouldAttack() {
        if (!perfectDelay.cooldownComplete()) return false;
        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
        if (isInLiquid) return false;
        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();
        boolean canDefaultCrit = (mc.player.fallDistance > 0 && canCrit && isBlockAboveHead) || (Criticals.getInstance().isEnabled() && !mc.player.isOnGround());
        boolean canSmartCrit = isBlockAboveHead && !mc.player.movementInput.jump && !canCrit && mc.player.isOnGround() && mc.player.collidedVertically;
        if (onlyCrits.getValue() && !smartCrits.getValue() && !randomCrits.getValue()) {
            return shouldCritical() && canDefaultCrit;
        }
        if (smartCrits.getValue()) {
            return (shouldCritical() || canDefaultCrit) || canSmartCrit;
        }
        if (randomCrits.getValue()) {
            return shouldCritical() || canDefaultCrit || (ThreadLocalRandom.current().nextFloat() < 0.3f);
        }
        return true;
    }


    private void attackEntity(Entity entity) {
        long currentTime = System.currentTimeMillis();
        if (attackMode.is("1.9") && currentTime - lastAttackTime < 500) {
            return;
        }

        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
        lastAttackTime = System.currentTimeMillis();
        if (!componentMode.is("Фантайм - новая")) {
            script.cleanup().addTickStep(0, () -> rotateGrim(false));
        }

        if (componentMode.is("Matrix")) {
            float offset = ThreadLocalRandom.current().nextBoolean() ? 18.0f : -18.0f;
            mc.player.rotationYaw += offset;
        }
        if (componentMode.is("Вулкан")) {
            long time = System.currentTimeMillis();
            float pitchWave = (float) (Math.sin(time / 400.0) * 4.0);
            float yawWave = (float) (Math.sin(time / 90.0) * 8.0);
            mc.player.rotationYaw += yawWave;
            mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch + pitchWave, -60F, 60F);
        }
    }


    private void updateTarget() {
        LivingEntity potentialTarget = findTarget();

        ElytraTarget elytraTarget = Excellent.inst().moduleManager().get(ElytraTarget.class);
        if (elytraTarget != null && elytraTarget.isEnabled() && potentialTarget instanceof PlayerEntity) {
            elytraTarget.setCurrentTarget((PlayerEntity) potentialTarget);

        }
        if (target != potentialTarget) {
            if (targetSwitchDelay.getValue() && target != null) {
                long currentTime = System.currentTimeMillis();
                float delayTime = randomLerp(minSwitchDelay.getValue(), maxSwitchDelay.getValue());
                if (currentTime - lastTargetSwitchTime < delayTime) {
                    return;
                }
            }
            previousTarget = target;
            target = potentialTarget;
            if (target != null) {
                targetAcquiredTime = System.currentTimeMillis();
                lastReactionTime = 0L;
                lastTargetSwitchTime = System.currentTimeMillis();
                isLocked = lockOnTarget.getValue();
                missCounter = 0;
                if (reactionTimeVariation.getValue() > 0) {
                    lastReactionTime = (long) (reactionTime.getValue() + randomLerp(-reactionTimeVariation.getValue(), reactionTimeVariation.getValue()));
                }
            }
        }
        if (target != null) {
            if (!isValidTarget(target)) {
                target = null;
                isLocked = false;
                return;
            }
            if (isLocked && lockOnTarget.getValue()) {
                if (mc.player.getDistance(target) > lockBreakDistance.getValue()) {
                    isLocked = false;
                }
            }
            if (!componentMode.is("Grim 1.16.5") && !componentMode.is("HW") && !componentMode.is("SmotionElytra")) {
                if (AuraUtil.calculateFOVFromCamera(target) > this.fov.getValue()) {
                    target = null;
                    isLocked = false;
                }
            }
        }
    }


    public void rotateGrim(boolean start) {
        if (componentMode.is("Grim 1.16.5") || componentMode.is("Vulcan + Grim")) {
            if (start && target != null) {
                Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
                Vector3d playerEyes = mc.player.getEyePosition(1.0F);
                Vector3d toTarget = targetPos.subtract(playerEyes).normalize();
                double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
                float yaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0f;
                float pitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));
                yaw += randomLerp(-0.02f, 0.02f);
                pitch += randomLerp(-0.02f, 0.02f);
                float yOffset = Criticals.getInstance().isEnabled() && !mc.player.isOnGround() ? 1e-6f : 0f;
                ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY() - yOffset, mc.player.getPosZ(), yaw, pitch, mc.player.isOnGround());
            } else {
                ViaUtil.sendPositionPacket(mc.player.rotationYaw, mc.player.rotationPitch, false);
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


    public boolean shouldCritical() {
        boolean canCrit = mc.player.fallDistance > ThreadLocalRandom.current().nextFloat() * 0.15f &&
                !mc.player.isOnGround() &&
                !mc.player.isOnLadder() &&
                !mc.player.isPassenger() &&
                !mc.player.isPotionActive(Effects.BLINDNESS) &&
                !mc.player.isPotionActive(Effects.LEVITATION) &&
                !mc.player.isPotionActive(Effects.SLOW_FALLING) &&
                !(mc.player.isActualySwimming() || (mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER)) || mc.player.areEyesInFluid(FluidTags.LAVA)) &&
                !mc.player.abilities.isFlying &&
                !mc.player.isElytraFlying() &&
                !PlayerUtil.isPlayerInWeb();
        return canCrit;

    }

    public boolean cooldownComplete() {
        return mc.player.getCooledAttackStrength(1.5F) >= 0.93F;
    }

    public boolean rayTrace() {
        if (mc.player.isElytraFlying() || (target != null && target.isElytraFlying())) return true;
        if (smartHitbox.getValue() && target != null) {
            Vector3d visiblePoint = getVisibleTargetPoint();
            if (visiblePoint != null) {
                return mc.player.getEyePosition(1.0F).distanceTo(visiblePoint) <= attackDistance();
            }
        }
        return (RayTraceUtil.rayTraceEntity(lerpRotation.x, lerpRotation.y, attackDistance(), target)
                || componentMode.is("HVH") || componentMode.is("СпукиТайм") || componentMode.is("Фантайм") || componentMode.is("HW"));
    }

    public double attackDistance() {
        if (mc.player.isElytraFlying() || (target != null && target.isElytraFlying())) return elRange.getValue();
        if (smartDistance.getValue() && target != null) return 3.0;
        return Math.max(mc.playerController.extendedReach() ? 6.0D : 3.0D, attackRange.getValue());
    }


    private void reset() {
        target = null;
        canCrit = false;
        if (mc.player != null) {
            lerpRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        }
        lastAttackTime = 0L;
        attackCount = 0;
        combatStartTime = System.currentTimeMillis();
        lastYawDiff = 0f;
        lastPitchDiff = 0f;
        lastAimPoint = null;
        currentBodyPart = 0;
        bodyPartOffsets.clear();
        dynamicAimPoint = null;
        pauseEndTime = 0L;
        lastReactionTime = 0L;
        isFlicking = false;
        targetAcquiredTime = 0L;
        funtimeSnapActive = false;
        isSnapping = false;
        smotionSnapActive = false;
        snapMixMode = 0;
        lastTargetVelocity = Vector3d.ZERO;
        currentRotationMomentumYaw = 0f;
        currentRotationMomentumPitch = 0f;
        lastTargetSwitchTime = 0L;
        previousTarget = null;
        fatigueMultiplier = 1.0f;
        lastPatternChangeTime = System.currentTimeMillis();
        currentRotationPattern = 0;
        swayPhase = 0f;
        breathingPhase = 0f;
        isLocked = false;
        rotationHistoryList.clear();
        correctionStartTime = 0L;
        isCorrectingOvershoot = false;
        horizontalBiasDirection = 1;
        lastAttackedTarget = null;
        healthBeforeAttack = 0;
        missCounter = 0;
    }


    private boolean checkIfPassedTarget(Entity entity) {
        if (entity == null || mc.player == null) return false;
        Vector3d lookDirection = Vector3d.fromPitchYaw(0, mc.player.rotationYaw);
        Vector3d toTarget = entity.getPositionVec().subtract(mc.player.getPositionVec()).normalize();
        return lookDirection.dotProduct(toTarget) < 0;
    }

    private boolean isMob(Entity entity) {
        return entity instanceof MonsterEntity || entity instanceof SlimeEntity || entity instanceof GhastEntity;
    }

    public boolean isValidTarget(LivingEntity entity) {
        if (entity == null || entity == mc.player || !entity.isAlive() || entity.getDistance(mc.player) > attackRange.getValue() + preRange.getValue()) {
            return false;
        }
        boolean isPlayer = entity instanceof PlayerEntity;
        if (isPlayer && Excellent.inst().friendManager().isFriend(entity.getName().getString())) {
            return false;
        }

        if (isPlayer && AntiBot.getInstance().isBot(entity)) {
            return false;
        }

        boolean canAttackType = false;
        if (isPlayer && targetSettings.getValue("Игроки")) {
            canAttackType = true;
        } else if (!isPlayer && isMob(entity) && targetSettings.getValue("Мобы")) {
            canAttackType = true;
        }
        if (!canAttackType) {
            return false;
        }
        if (!targetSettings.getValue("Невидимые") && entity.isInvisible()) {
            return false;
        }
        if (targetSettings.getValue("Только в броне")) {
            boolean hasArmor = false;
            for (ItemStack stack : entity.getArmorInventoryList()) {
                if (!stack.isEmpty()) {
                    hasArmor = true;
                    break;
                }
            }
            return hasArmor;
        }
        return true;
    }

    private LivingEntity findTarget() {
        if (priorityTargeting.getValue()) {
            return findPriorityTarget();
        }
        return mc.world.getEntitiesWithinAABB(LivingEntity.class, mc.player.getBoundingBox().grow(attackRange.getValue() + preRange.getValue()), this::isValidTarget)
                .stream()
                .min(Comparator.comparingDouble(e -> e.getDistanceSq(mc.player)))
                .orElse(null);
    }


    private LivingEntity findPriorityTarget() {
        List<LivingEntity> targets = mc.world.getEntitiesWithinAABB(LivingEntity.class, mc.player.getBoundingBox().grow(attackRange.getValue() + preRange.getValue()), this::isValidTarget);
        if (targets.isEmpty()) return null;
        switch (priorityMode.getValue()) {
            case "Ближайший":
                return targets.stream().min(Comparator.comparingDouble(e -> e.getDistanceSq(mc.player))).orElse(null);
            case "Слабейший":
                return targets.stream().min(Comparator.comparingDouble(LivingEntity::getHealth)).orElse(null);
            case "Низкое HP":
                return targets.stream().filter(e -> e.getHealth() < e.getMaxHealth() * 0.5f).min(Comparator.comparingDouble(LivingEntity::getHealth)).orElse(targets.get(0));
            case "Опасный":
                return targets.stream().filter(e -> e instanceof PlayerEntity).findFirst().orElse(targets.get(0));
            default:
                return targets.get(0);
        }
    }

    public boolean isHoldingWeapon() {
        return mc.player.getHeldItemMainhand().getItem() instanceof SwordItem
                || mc.player.getHeldItemMainhand().getItem() instanceof AxeItem;
    }


    private boolean shieldBreaker() {
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


    private void updateFatigue() {
        if (!simulateFatigue.getValue() || !humanize.getValue()) {
            fatigueMultiplier = 1.0f;
            return;
        }
        long elapsed = (System.currentTimeMillis() - combatStartTime) / 1000;
        float fatigueTimeVal = fatigueTime.getValue();
        float fatigueEffectVal = fatigueEffect.getValue();
        fatigueMultiplier = Math.max(1.0f - fatigueEffectVal, 1.0f - (elapsed / fatigueTimeVal) * fatigueEffectVal);
    }


    private void updateRotationPatterns() {
        if (!randomizeRotationPattern.getValue() || !advancedAntiCheat.getValue()) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPatternChangeTime > patternChangeInterval.getValue() * 1000) {
            currentRotationPattern = secureRandom.nextInt(5);
            lastPatternChangeTime = currentTime;
        }
    }
}
