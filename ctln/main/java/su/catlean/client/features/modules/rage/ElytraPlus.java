// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.player.ElytraFlyBoost;
import su.catlean.client.features.modules.player.ElytraFlyControl;
import su.catlean.client.features.modules.player.ElytraFlyFirework;
import su.catlean.client.features.modules.player.ElytraFlyGlide;
import su.catlean.client.features.modules.player.ElytraFlyPlus;
import su.catlean.client.features.modules.player.ElytraFlyStrafe;
import su.catlean.client.features.modules.player.ElytraFlyStrategy;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;

public final class ElytraPlus extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ElytraPlus.class, "mode", "getMode()Lsu/catlean/fy;", 0), new PropertyReference1Impl(ElytraPlus.class, "twoBee", "getTwoBee()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "lift", "getLift()Lsu/catlean/api/setting/values/Bind;", 0), new PropertyReference1Impl(ElytraPlus.class, "onlySpace", "getOnlySpace()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "stopOnGround", "getStopOnGround()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "infDurability", "getInfDurability()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "vertical", "getVertical()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "ncpStrictMode", "getNcpStrictMode()Lsu/catlean/vs;", 0), new PropertyReference1Impl(ElytraPlus.class, "antiKick", "getAntiKick()Lsu/catlean/n1;", 0), new PropertyReference1Impl(ElytraPlus.class, "xzSpeed", "getXzSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "ySpeed", "getYSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "fireSlot", "getFireSlot()I", 0), new PropertyReference1Impl(ElytraPlus.class, "accelerate", "getAccelerate()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "accelerateFactor", "getAccelerateFactor()F", 0), new PropertyReference1Impl(ElytraPlus.class, "fireDelay", "getFireDelay()F", 0), new PropertyReference1Impl(ElytraPlus.class, "rotate", "getRotate()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "stayMad", "getStayMad()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "keepFlying", "getKeepFlying()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "disableOnFlag", "getDisableOnFlag()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "allowFireSwap", "getAllowFireSwap()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "instantFly", "getInstantFly()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "cruiseControl", "getCruiseControl()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "pitchControl", "getPitchControl()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "factor", "getFactor()F", 0), new PropertyReference1Impl(ElytraPlus.class, "upSpeed", "getUpSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "downFactor", "getDownFactor()F", 0), new PropertyReference1Impl(ElytraPlus.class, "stopMotion", "getStopMotion()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "minUpSpeed", "getMinUpSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "forceHeight", "getForceHeight()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "manualHeight", "getManualHeight()I", 0), new PropertyReference1Impl(ElytraPlus.class, "sneakDownSpeed", "getSneakDownSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "speedLimit", "getSpeedLimit()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "maxSpeed", "getMaxSpeed()F", 0), new PropertyReference1Impl(ElytraPlus.class, "redeployInterval", "getRedeployInterval()F", 0), new PropertyReference1Impl(ElytraPlus.class, "redeployTimeOut", "getRedeployTimeOut()F", 0), new PropertyReference1Impl(ElytraPlus.class, "redeployDelay", "getRedeployDelay()F", 0), new PropertyReference1Impl(ElytraPlus.class, "infiniteMaxHeight", "getInfiniteMaxHeight()I", 0), new PropertyReference1Impl(ElytraPlus.class, "stopMove", "getStopMove()Lsu/catlean/ck;", 0), new PropertyReference1Impl(ElytraPlus.class, "swapDelay", "getSwapDelay()I", 0), new PropertyReference1Impl(ElytraPlus.class, "fireworkDelay", "getFireworkDelay()I", 0), new PropertyReference1Impl(ElytraPlus.class, "offHandFirework", "getOffHandFirework()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "alternativeFireworkSwap", "getAlternativeFireworkSwap()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "pauseWhileEating", "getPauseWhileEating()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "minimizeClicks", "getMinimizeClicks()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "boost", "getBoost()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "customSpeed", "getCustomSpeed()Z", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees0", "getDegrees0()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees5", "getDegrees5()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees10", "getDegrees10()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees15", "getDegrees15()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees20", "getDegrees20()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees25", "getDegrees25()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees30", "getDegrees30()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees35", "getDegrees35()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees40", "getDegrees40()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees45", "getDegrees45()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees50", "getDegrees50()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees55", "getDegrees55()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees60", "getDegrees60()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees65", "getDegrees65()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees70", "getDegrees70()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees75", "getDegrees75()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees80", "getDegrees80()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees85", "getDegrees85()F", 0), new PropertyReference1Impl(ElytraPlus.class, "degrees90", "getDegrees90()F", 0) };
   @NotNull
   public static final ElytraPlus INSTANCE = new ElytraPlus();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", ElytraPlus.Mode.FIRE_WORK, null, null, 12, null);
   @NotNull
   private static final BooleanSetting vanilla = SettingFactory.defaultImpl(
      INSTANCE, "vanilla", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 4, null
   );
   @NotNull
   private static final BindSetting lift = SettingFactory.defaultImpl(
      INSTANCE, "lift", new Bind(0, false, false, 7, null), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && INSTANCE.getTwoBee(), 4, null
   );
   @NotNull
   private static final BooleanSetting onlySpace = SettingFactory.defaultImpl(
      INSTANCE, "only-space", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && INSTANCE.getTwoBee(), 4, null
   );
   @NotNull
   private static final BooleanSetting stopOnGround = SettingFactory.defaultImpl(
      INSTANCE, "stop-on-ground", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.PACKET, 4, null
   );
   @NotNull
   private static final BooleanSetting infDurability = SettingFactory.defaultImpl(
      INSTANCE, "inf-durability", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.PACKET, 4, null
   );
   @NotNull
   private static final BooleanSetting vertical = SettingFactory.defaultImpl(
      INSTANCE, "vertical", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.PACKET, 4, null
   );
   @NotNull
   private static final EnumSetting ncpStrict = SettingFactory.defaultImpl(
      INSTANCE, "ncp-strict", ElytraPlus.NcpStrict.Off, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.PACKET, 4, null
   );
   @NotNull
   private static final EnumSetting antiKick = SettingFactory.defaultImpl(
      INSTANCE, "anti-kick", ElytraPlus.AntiKick.Jitter, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final FloatSetting xZSpeed = SettingFactory.defaultImpl(
      INSTANCE,
      "x-z-speed",
      1.55F,
      RangesKt.rangeTo(0.1F, 10.0F),
      null,
      () -> INSTANCE.getMode() != ElytraPlus.Mode.BOOST
         && INSTANCE.getMode() != ElytraPlus.Mode.INFINITE
         && INSTANCE.getMode() != ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting ySpeed = SettingFactory.defaultImpl(
      INSTANCE,
      "y-speed",
      0.47F,
      RangesKt.rangeTo(0.0F, 2.0F),
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK || INSTANCE.getMode() == ElytraPlus.Mode.PACKET && INSTANCE.getVertical(),
      8,
      null
   );
   @NotNull
   private static final IntSetting fireSlot = SettingFactory.defaultImpl(
      INSTANCE, "fire-slot", 1, new IntRange(1, 9), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 8, null
   );
   @NotNull
   private static final BooleanSetting acceleration = SettingFactory.defaultImpl(
      INSTANCE, "acceleration", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.CONTROL || INSTANCE.getMode() == ElytraPlus.Mode.PACKET, 4, null
   );
   @NotNull
   private static final FloatSetting accelerateFactor = SettingFactory.defaultImpl(
      INSTANCE,
      "accelerate-factor",
      9.0F,
      RangesKt.rangeTo(0.1F, 10.0F),
      null,
      () -> INSTANCE.getAccelerate() && (INSTANCE.getMode() == ElytraPlus.Mode.CONTROL || INSTANCE.getMode() == ElytraPlus.Mode.PACKET),
      8,
      null
   );
   @NotNull
   private static final FloatSetting fireDelay = SettingFactory.defaultImpl(
      INSTANCE, "fire-delay", 1.5F, RangesKt.rangeTo(0.0F, 1.5F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 8, null
   );
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(
      INSTANCE, "rotate", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting groundSafe = SettingFactory.defaultImpl(
      INSTANCE, "ground-safe", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting keepFlying = SettingFactory.defaultImpl(
      INSTANCE, "keep-flying", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting disableOnFlag = SettingFactory.defaultImpl(
      INSTANCE, "disable-on-flag", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting allowFireSwap = SettingFactory.defaultImpl(
      INSTANCE, "allow-fire-swap", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting instantFly = SettingFactory.defaultImpl(
      INSTANCE,
      "instant-fly",
      true,
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && !INSTANCE.getTwoBee() || INSTANCE.getMode() == ElytraPlus.Mode.CONTROL,
      4,
      null
   );
   @NotNull
   private static final BooleanSetting cruiseControl = SettingFactory.defaultImpl(
      INSTANCE, "cruise-control", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 4, null
   );
   @NotNull
   private static final BooleanSetting pitchControl = SettingFactory.defaultImpl(
      INSTANCE,
      "pitch-control",
      false,
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.CONTROL || INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      4,
      null
   );
   @NotNull
   private static final FloatSetting factor = SettingFactory.defaultImpl(
      INSTANCE, "factor", 1.5F, RangesKt.rangeTo(0.1F, 50.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 8, null
   );
   @NotNull
   private static final FloatSetting upSpeed = SettingFactory.defaultImpl(
      INSTANCE,
      "up-speed",
      1.0F,
      RangesKt.rangeTo(0.01F, 5.0F),
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && !INSTANCE.getTwoBee() || INSTANCE.getMode() == ElytraPlus.Mode.CONTROL,
      8,
      null
   );
   @NotNull
   private static final FloatSetting glide = SettingFactory.defaultImpl(
      INSTANCE,
      "glide",
      1.0F,
      RangesKt.rangeTo(0.0F, 2.0F),
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && !INSTANCE.getTwoBee() || INSTANCE.getMode() == ElytraPlus.Mode.CONTROL,
      8,
      null
   );
   @NotNull
   private static final BooleanSetting stopMotion = SettingFactory.defaultImpl(
      INSTANCE, "stop-motion", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && !INSTANCE.getTwoBee(), 4, null
   );
   @NotNull
   private static final FloatSetting minUpSpeed = SettingFactory.defaultImpl(
      INSTANCE,
      "min-up-speed",
      0.5F,
      RangesKt.rangeTo(0.1F, 5.0F),
      null,
      () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && INSTANCE.getCruiseControl(),
      8,
      null
   );
   @NotNull
   private static final BooleanSetting forceHeight = SettingFactory.defaultImpl(
      INSTANCE, "force-height", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && INSTANCE.getCruiseControl(), 4, null
   );
   @NotNull
   private static final IntSetting height = SettingFactory.defaultImpl(
      INSTANCE, "height", 121, new IntRange(1, 256), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST && INSTANCE.getForceHeight(), 8, null
   );
   @NotNull
   private static final FloatSetting downSpeed = SettingFactory.defaultImpl(
      INSTANCE, "down-speed", 1.0F, RangesKt.rangeTo(0.01F, 5.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.CONTROL, 8, null
   );
   @NotNull
   private static final BooleanSetting speedLimit = SettingFactory.defaultImpl(
      INSTANCE, "speed-limit", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 4, null
   );
   @NotNull
   private static final FloatSetting maxSpeed = SettingFactory.defaultImpl(
      INSTANCE, "max-speed", 2.5F, RangesKt.rangeTo(0.1F, 10.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 8, null
   );
   @NotNull
   private static final FloatSetting redeployInterval = SettingFactory.defaultImpl(
      INSTANCE, "redeploy-interval", 1.0F, RangesKt.rangeTo(0.1F, 5.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 8, null
   );
   @NotNull
   private static final FloatSetting redeployTimeout = SettingFactory.defaultImpl(
      INSTANCE, "redeploy-timeout", 5.0F, RangesKt.rangeTo(0.1F, 20.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 8, null
   );
   @NotNull
   private static final FloatSetting redeployDelay = SettingFactory.defaultImpl(
      INSTANCE, "redeploy-delay", 0.5F, RangesKt.rangeTo(0.1F, 1.0F), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.BOOST, 8, null
   );
   @NotNull
   private static final IntSetting infiniteMaxHeight = SettingFactory.defaultImpl(
      INSTANCE, "infinite-max-height", 200, new IntRange(50, 360), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.INFINITE, 8, null
   );
   @NotNull
   private static final EnumSetting stopMove = SettingFactory.defaultImpl(
      INSTANCE, "stop-move", ElytraPlus.StopMove.BLOCK, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final IntSetting elytraDelay = SettingFactory.defaultImpl(
      INSTANCE, "elytra-delay", 2, new IntRange(0, 40), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 8, null
   );
   @NotNull
   private static final IntSetting fireworkDelay = SettingFactory.defaultImpl(
      INSTANCE, "firework-delay", 8, new IntRange(0, 40), null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 8, null
   );
   @NotNull
   private static final BooleanSetting offhandFirework = SettingFactory.defaultImpl(
      INSTANCE, "offhand-firework", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting altFireworkSwap = SettingFactory.defaultImpl(
      INSTANCE, "alt-firework-swap", false, null, () -> INSTANCE.getOffHandFirework() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(
      INSTANCE, "pause-while-eating", true, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting minimizeClicks = SettingFactory.defaultImpl(
      INSTANCE, "minimize-clicks", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting boost = SettingFactory.defaultImpl(
      INSTANCE, "boost", false, null, () -> INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final BooleanSetting customSpeed = SettingFactory.defaultImpl(
      INSTANCE, "custom-speed", false, null, () -> INSTANCE.getBoost() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK, 4, null
   );
   @NotNull
   private static final FloatSetting degrees0 = SettingFactory.defaultImpl(
      INSTANCE,
      "0-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees5 = SettingFactory.defaultImpl(
      INSTANCE,
      "5-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees10 = SettingFactory.defaultImpl(
      INSTANCE,
      "10-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees15 = SettingFactory.defaultImpl(
      INSTANCE,
      "15-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees20 = SettingFactory.defaultImpl(
      INSTANCE,
      "20-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees25 = SettingFactory.defaultImpl(
      INSTANCE,
      "25-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees30 = SettingFactory.defaultImpl(
      INSTANCE,
      "30-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees35 = SettingFactory.defaultImpl(
      INSTANCE,
      "35-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees40 = SettingFactory.defaultImpl(
      INSTANCE,
      "40-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees45 = SettingFactory.defaultImpl(
      INSTANCE,
      "45-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees50 = SettingFactory.defaultImpl(
      INSTANCE,
      "50-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees55 = SettingFactory.defaultImpl(
      INSTANCE,
      "55-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees60 = SettingFactory.defaultImpl(
      INSTANCE,
      "60-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees65 = SettingFactory.defaultImpl(
      INSTANCE,
      "65-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees70 = SettingFactory.defaultImpl(
      INSTANCE,
      "70-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees75 = SettingFactory.defaultImpl(
      INSTANCE,
      "75-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees80 = SettingFactory.defaultImpl(
      INSTANCE,
      "80-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees85 = SettingFactory.defaultImpl(
      INSTANCE,
      "85-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final FloatSetting degrees90 = SettingFactory.defaultImpl(
      INSTANCE,
      "90-degrees",
      0.15F,
      RangesKt.rangeTo(0.0F, 1.0F),
      null,
      () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed() && INSTANCE.getMode() == ElytraPlus.Mode.SILENT_FIRE_WORK,
      8,
      null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   private static boolean flag;
   private static boolean flag2;
   private static float amount;
   private static float amount2;
   private static float amount3;

   private ElytraPlus() {
      super("elytra-plus", SubCategories.getSubCategory5(), null, 4, null);
   }

   @NotNull
   public final ElytraPlus.Mode getMode() {
      return (ElytraPlus.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   public final boolean getTwoBee() {
      return (Boolean)vanilla.computeObject(this, $$delegatedProperties[1]);
   }

   @NotNull
   public final Bind getLift() {
      return (Bind)lift.computeObject(this, $$delegatedProperties[2]);
   }

   public final boolean getOnlySpace() {
      return (Boolean)onlySpace.computeObject(this, $$delegatedProperties[3]);
   }

   public final boolean getStopOnGround() {
      return (Boolean)stopOnGround.computeObject(this, $$delegatedProperties[4]);
   }

   public final boolean getInfDurability() {
      return (Boolean)infDurability.computeObject(this, $$delegatedProperties[5]);
   }

   public final boolean getVertical() {
      return (Boolean)vertical.computeObject(this, $$delegatedProperties[6]);
   }

   @NotNull
   public final ElytraPlus.NcpStrict getNcpStrictMode() {
      return (ElytraPlus.NcpStrict)ncpStrict.computeObject(this, $$delegatedProperties[7]);
   }

   @NotNull
   public final ElytraPlus.AntiKick getAntiKick() {
      return (ElytraPlus.AntiKick)antiKick.computeObject(this, $$delegatedProperties[8]);
   }

   public final float getXzSpeed() {
      return ((Number)xZSpeed.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   public final float getYSpeed() {
      return ((Number)ySpeed.computeObject(this, $$delegatedProperties[10])).floatValue();
   }

   public final int getFireSlot() {
      return ((Number)fireSlot.computeObject(this, $$delegatedProperties[11])).intValue();
   }

   public final boolean getAccelerate() {
      return (Boolean)acceleration.computeObject(this, $$delegatedProperties[12]);
   }

   public final float getAccelerateFactor() {
      return ((Number)accelerateFactor.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   public final float getFireDelay() {
      return ((Number)fireDelay.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   public final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[15]);
   }

   public final boolean getStayMad() {
      return (Boolean)groundSafe.computeObject(this, $$delegatedProperties[16]);
   }

   public final boolean getKeepFlying() {
      return (Boolean)keepFlying.computeObject(this, $$delegatedProperties[17]);
   }

   public final boolean getDisableOnFlag() {
      return (Boolean)disableOnFlag.computeObject(this, $$delegatedProperties[18]);
   }

   public final boolean getAllowFireSwap() {
      return (Boolean)allowFireSwap.computeObject(this, $$delegatedProperties[19]);
   }

   public final boolean getInstantFly() {
      return (Boolean)instantFly.computeObject(this, $$delegatedProperties[20]);
   }

   public final boolean getCruiseControl() {
      return (Boolean)cruiseControl.computeObject(this, $$delegatedProperties[21]);
   }

   public final boolean getPitchControl() {
      return (Boolean)pitchControl.computeObject(this, $$delegatedProperties[22]);
   }

   public final float getFactor() {
      return ((Number)factor.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   public final float getUpSpeed() {
      return ((Number)upSpeed.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   public final float getDownFactor() {
      return ((Number)glide.computeObject(this, $$delegatedProperties[25])).floatValue();
   }

   public final boolean getStopMotion() {
      return (Boolean)stopMotion.computeObject(this, $$delegatedProperties[26]);
   }

   public final float getMinUpSpeed() {
      return ((Number)minUpSpeed.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   public final boolean getForceHeight() {
      return (Boolean)forceHeight.computeObject(this, $$delegatedProperties[28]);
   }

   public final int getManualHeight() {
      return ((Number)height.computeObject(this, $$delegatedProperties[29])).intValue();
   }

   public final float getSneakDownSpeed() {
      return ((Number)downSpeed.computeObject(this, $$delegatedProperties[30])).floatValue();
   }

   public final boolean getSpeedLimit() {
      return (Boolean)speedLimit.computeObject(this, $$delegatedProperties[31]);
   }

   public final float getMaxSpeed() {
      return ((Number)maxSpeed.computeObject(this, $$delegatedProperties[32])).floatValue();
   }

   public final float getRedeployInterval() {
      return ((Number)redeployInterval.computeObject(this, $$delegatedProperties[33])).floatValue();
   }

   public final float getRedeployTimeOut() {
      return ((Number)redeployTimeout.computeObject(this, $$delegatedProperties[34])).floatValue();
   }

   public final float getRedeployDelay() {
      return ((Number)redeployDelay.computeObject(this, $$delegatedProperties[35])).floatValue();
   }

   public final int getInfiniteMaxHeight() {
      return ((Number)infiniteMaxHeight.computeObject(this, $$delegatedProperties[36])).intValue();
   }

   @NotNull
   public final ElytraPlus.StopMove getStopMove() {
      return (ElytraPlus.StopMove)stopMove.computeObject(this, $$delegatedProperties[37]);
   }

   public final int getSwapDelay() {
      return ((Number)elytraDelay.computeObject(this, $$delegatedProperties[38])).intValue();
   }

   public final int getFireworkDelay() {
      return ((Number)fireworkDelay.computeObject(this, $$delegatedProperties[39])).intValue();
   }

   public final boolean getOffHandFirework() {
      return (Boolean)offhandFirework.computeObject(this, $$delegatedProperties[40]);
   }

   public final boolean getAlternativeFireworkSwap() {
      return (Boolean)altFireworkSwap.computeObject(this, $$delegatedProperties[41]);
   }

   public final boolean getPauseWhileEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[42]);
   }

   public final boolean getMinimizeClicks() {
      return (Boolean)minimizeClicks.computeObject(this, $$delegatedProperties[43]);
   }

   public final boolean getBoost() {
      return (Boolean)boost.computeObject(this, $$delegatedProperties[44]);
   }

   public final boolean getCustomSpeed() {
      return (Boolean)customSpeed.computeObject(this, $$delegatedProperties[45]);
   }

   public final float getDegrees0() {
      return ((Number)degrees0.computeObject(this, $$delegatedProperties[46])).floatValue();
   }

   public final float getDegrees5() {
      return ((Number)degrees5.computeObject(this, $$delegatedProperties[47])).floatValue();
   }

   public final float getDegrees10() {
      return ((Number)degrees10.computeObject(this, $$delegatedProperties[48])).floatValue();
   }

   public final float getDegrees15() {
      return ((Number)degrees15.computeObject(this, $$delegatedProperties[49])).floatValue();
   }

   public final float getDegrees20() {
      return ((Number)degrees20.computeObject(this, $$delegatedProperties[50])).floatValue();
   }

   public final float getDegrees25() {
      return ((Number)degrees25.computeObject(this, $$delegatedProperties[51])).floatValue();
   }

   public final float getDegrees30() {
      return ((Number)degrees30.computeObject(this, $$delegatedProperties[52])).floatValue();
   }

   public final float getDegrees35() {
      return ((Number)degrees35.computeObject(this, $$delegatedProperties[53])).floatValue();
   }

   public final float getDegrees40() {
      return ((Number)degrees40.computeObject(this, $$delegatedProperties[54])).floatValue();
   }

   public final float getDegrees45() {
      return ((Number)degrees45.computeObject(this, $$delegatedProperties[55])).floatValue();
   }

   public final float getDegrees50() {
      return ((Number)degrees50.computeObject(this, $$delegatedProperties[56])).floatValue();
   }

   public final float getDegrees55() {
      return ((Number)degrees55.computeObject(this, $$delegatedProperties[57])).floatValue();
   }

   public final float getDegrees60() {
      return ((Number)degrees60.computeObject(this, $$delegatedProperties[58])).floatValue();
   }

   public final float getDegrees65() {
      return ((Number)degrees65.computeObject(this, $$delegatedProperties[59])).floatValue();
   }

   public final float getDegrees70() {
      return ((Number)degrees70.computeObject(this, $$delegatedProperties[60])).floatValue();
   }

   public final float getDegrees75() {
      return ((Number)degrees75.computeObject(this, $$delegatedProperties[61])).floatValue();
   }

   public final float getDegrees80() {
      return ((Number)degrees80.computeObject(this, $$delegatedProperties[62])).floatValue();
   }

   public final float getDegrees85() {
      return ((Number)degrees85.computeObject(this, $$delegatedProperties[63])).floatValue();
   }

   public final float getDegrees90() {
      return ((Number)degrees90.computeObject(this, $$delegatedProperties[64])).floatValue();
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return stopWatch;
   }

   @NotNull
   public final StopWatch getStopWatch2() {
      return stopWatch2;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ElytraPlus.flag = flag;
   }

   public final boolean isFlag2() {
      return flag2;
   }

   public final void setFlag2(boolean flag) {
      flag2 = flag;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      ElytraPlus.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   public final float getAmount3() {
      return amount3;
   }

   public final void setAmount3(float amount) {
      amount3 = amount;
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().level != null) {
         amount = 0.0F;
         amount2 = (float)MinecraftHolder.getLocalPlayer().getY();
         this.getMode().getElytraFlyStrategy().update();
      }
   }

   @Flow
   private final void onPreElytra(PreElytraEvent preElytraEvent) {
      this.getMode().getElytraFlyStrategy().onPreElytra(preElytraEvent);
   }

   @Flow
   private final void onAfterElytra(AfterElytraEvent afterElytraEvent) {
      this.getMode().getElytraFlyStrategy().onAfterElytra(afterElytraEvent);
   }

   @Flow
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      this.getMode().getElytraFlyStrategy().onPreSync(preSyncEvent);
   }

   @Flow(
      priority = -20
   )
   public final void onMove(MoveEvent e) {
      this.getMode().getElytraFlyStrategy().onMove(e);
   }

   @Flow
   public final void onAfterSendPacket(AfterSendPacket e) {
      this.getMode().getElytraFlyStrategy().onAfterSendPacket(e);
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      this.getMode().getElytraFlyStrategy().onReceivePacket(e);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      this.getMode().getElytraFlyStrategy().onPlayerUpdate(e);
   }

   @Flow
   private final void onFireWorkVelocity(FireWorkVelocityEvent fireWorkVelocityEvent) {
      this.getMode().getElytraFlyStrategy().onFireWorkVelocity(fireWorkVelocityEvent);
   }

   @Flow
   private final void onSetPose(SetPoseEvent setPoseEvent) {
      this.getMode().getElytraFlyStrategy().onSetPose(setPoseEvent);
   }

   @Flow
   private final void onReceivePacket2(ReceivePacket receivePacket) {
      this.getMode().getElytraFlyStrategy().onReceivePacket2(receivePacket);
   }

   @Override
   public void update4() {
      this.getMode().getElytraFlyStrategy().update2();
   }

   public final boolean isFlag3() {
      return Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA);
   }

   public final boolean checkGetDamageValue(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage() - 1 && stack.has(DataComponents.GLIDER);
   }

   public final void applyLength(String m) {
      if (m.length() == 0) {
         this.applyUnplug();
      } else {
         this.updateText10(m);
      }
   }

   public static void defaultImpl(ElytraPlus elytraPlus, String text, int value, Object object) {
      if ((value & 1) != 0) {
         text = "";
      }

      elytraPlus.applyLength(text);
   }

   @NotNull
   public final String computeText(String key, Object[] args) {
      Module thisValue = this;
      Object[] objects = new Object[]{args};
      return ChatUtil.defaultImpl(thisValue, key, objects, false, 4, null);
   }

   public final void update() {
      Module thisValue = this;
      Object[] objects = new Object[]{String.valueOf(this.getInfiniteMaxHeight())};
      this.updateText10(ChatUtil.defaultImpl(thisValue, "need-more-height", objects, false, 4, null));
   }

   static {
   }

   public static enum AntiKick {
      Off,
      Jitter,
      Glide;

      private static final ElytraPlus.AntiKick[] antiKicks = $values();
      private static final EnumEntries<ElytraPlus.AntiKick> enumEntries = EnumEntriesKt.enumEntries(antiKicks);

      private AntiKick() {
      }

      @NotNull
      public static EnumEntries<ElytraPlus.AntiKick> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraPlus.AntiKick[] $values() {
         return new ElytraPlus.AntiKick[]{Off, Jitter, Glide};
      }
   }

   public static enum Mode {
      FIRE_WORK(ElytraFlyBoost.INSTANCE),
      BOOST(ElytraFlyControl.INSTANCE),
      CONTROL(ElytraFlyStrafe.INSTANCE),
      INFINITE(ElytraFlyPlus.INSTANCE),
      PACKET(ElytraFlyGlide.INSTANCE),
      SILENT_FIRE_WORK(ElytraFlyFirework.INSTANCE);

      @NotNull
      private final ElytraFlyStrategy elytraFlyStrategy;
      private static final ElytraPlus.Mode[] modes = $values();
      private static final EnumEntries<ElytraPlus.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode(ElytraFlyStrategy elytraFlyStrategy) {
         this.elytraFlyStrategy = elytraFlyStrategy;
      }

      @NotNull
      public final ElytraFlyStrategy getElytraFlyStrategy() {
         return this.elytraFlyStrategy;
      }

      @NotNull
      public static EnumEntries<ElytraPlus.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraPlus.Mode[] $values() {
         return new ElytraPlus.Mode[]{FIRE_WORK, BOOST, CONTROL, INFINITE, PACKET, SILENT_FIRE_WORK};
      }
   }

   public static enum NcpStrict {
      Off,
      Old,
      New,
      Motion;

      private static final ElytraPlus.NcpStrict[] ncpStricts = $values();
      private static final EnumEntries<ElytraPlus.NcpStrict> enumEntries = EnumEntriesKt.enumEntries(ncpStricts);

      private NcpStrict() {
      }

      @NotNull
      public static EnumEntries<ElytraPlus.NcpStrict> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraPlus.NcpStrict[] $values() {
         return new ElytraPlus.NcpStrict[]{Off, Old, New, Motion};
      }
   }

   public static enum StopMove {
      OFF,
      TIMER,
      BLOCK;

      private static final ElytraPlus.StopMove[] stopMoves = $values();
      private static final EnumEntries<ElytraPlus.StopMove> enumEntries = EnumEntriesKt.enumEntries(stopMoves);

      private StopMove() {
      }

      @NotNull
      public static EnumEntries<ElytraPlus.StopMove> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraPlus.StopMove[] $values() {
         return new ElytraPlus.StopMove[]{OFF, TIMER, BLOCK};
      }
   }
}