// deobf by pusia
package su.catlean.client.services.rotation.modes;

import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.EntityPositionUtil;

public final class AdvancedRotation extends RotationStrategy {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AdvancedRotation.class, "advancedOptions", "getAdvancedOptions()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AdvancedRotation.class, "point", "getPoint()Lsu/catlean/fw;", 0), new PropertyReference1Impl(AdvancedRotation.class, "pointJitter", "getPointJitter()Lsu/catlean/d7;", 0), new PropertyReference1Impl(AdvancedRotation.class, "pointVRange", "getPointVRange()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pointHRange", "getPointHRange()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pointHFreq", "getPointHFreq()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pointVFreq", "getPointVFreq()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "speedDistribution", "getSpeedDistribution()Lsu/catlean/wu;", 0), new PropertyReference1Impl(AdvancedRotation.class, "minYawSpeed", "getMinYawSpeed()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "maxYawSpeed", "getMaxYawSpeed()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "minPitchSpeed", "getMinPitchSpeed()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "maxPitchSpeed", "getMaxPitchSpeed()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "jitterDistribution", "getJitterDistribution()Lsu/catlean/wu;", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawJitter", "getYawJitter()Lsu/catlean/d7;", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchJitter", "getPitchJitter()Lsu/catlean/d7;", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawAmplMin", "getYawAmplMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchAmplMin", "getPitchAmplMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawAmplMax", "getYawAmplMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchAmplMax", "getPitchAmplMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawFreqMin", "getYawFreqMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchFreqMin", "getPitchFreqMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawFreqMax", "getYawFreqMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchFreqMax", "getPitchFreqMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "smoothHbScale", "getSmoothHbScale()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawAcceleration", "getYawAcceleration()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchAcceleration", "getPitchAcceleration()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawDeceleration", "getYawDeceleration()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchDeceleration", "getPitchDeceleration()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawSmoother", "getYawSmoother()Lsu/catlean/yr;", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchSmoother", "getPitchSmoother()Lsu/catlean/yr;", 0), new PropertyReference1Impl(AdvancedRotation.class, "yawAccelerationMin", "getYawAccelerationMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchAccelerationMin", "getPitchAccelerationMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "clientLook", "getClientLook()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "overShoot", "getOverShoot()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "overshootTrigger", "getOvershootTrigger()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "overshootStreak", "getOvershootStreak()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "overshootValue", "getOvershootValue()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "overshootMinTicks", "getOvershootMinTicks()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "overshootMaxTicks", "getOvershootMaxTicks()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "headUp", "getHeadUp()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "huIntervalMin", "getHuIntervalMin()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "huIntervalMax", "getHuIntervalMax()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "huTicksMin", "getHuTicksMin()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "huTicksMax", "getHuTicksMax()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "huSpeedMin", "getHuSpeedMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "huSpeedMax", "getHuSpeedMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "clickJitter", "getClickJitter()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "yaw360", "getYaw360()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "yaw360Chance", "getYaw360Chance()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "holdAfterHit", "getHoldAfterHit()Z", 0), new PropertyReference1Impl(AdvancedRotation.class, "holdMin", "getHoldMin()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "holdMax", "getHoldMax()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "holdTiming", "getHoldTiming()Lsu/catlean/nw;", 0), new PropertyReference1Impl(AdvancedRotation.class, "holdChance", "getHoldChance()I", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchMin", "getPitchMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchMax", "getPitchMax()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchITMin", "getPitchITMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "pitchITMax", "getPitchITMax()F", 0), new PropertyReference1Impl( AdvancedRotation.class, "filterLowDeltas", "getFilterLowDeltas()Lsu/catlean/client/features/modules/pvp/attack/aura/rotations/AdvancedRotation$FilterLowDeltas;", 0 ), new PropertyReference1Impl(AdvancedRotation.class, "lowDeltaThreshold", "getLowDeltaThreshold()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "replaceMin", "getReplaceMin()F", 0), new PropertyReference1Impl(AdvancedRotation.class, "replaceMax", "getReplaceMax()F", 0) };
   @NotNull
   public static final AdvancedRotation INSTANCE = new AdvancedRotation();
   @NotNull
   private static final GroupSetting advanced = SettingFactory.defaultImpl(Aura.INSTANCE, "advanced", null, () -> INSTANCE.isFlag(), 4, null);
   @NotNull
   private static final EnumSetting point = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "point", AdvancedRotation.Point.CENTER, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting pointJitter = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "point-jitter", AdvancedRotation.PointJitter.NONE, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pointVRange = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "point-v-range", 0.1F, RangesKt.rangeTo(0.01F, 1.8F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pointHRange = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "point-h-range", 0.1F, RangesKt.rangeTo(0.01F, 0.3F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pointHFreq = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "point-h-freq", 20.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pointVFreq = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "point-v-freq", 10.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting speedDistribution = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "speed-distribution", AdvancedRotation.SpeedDistribution.FLAT, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting minYawSpeed = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "min-yaw-speed", 30.0F, RangesKt.rangeTo(0.1F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting maxYawSpeed = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "max-yaw-speed", 60.0F, RangesKt.rangeTo(0.1F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting minPitchSpeed = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "min-pitch-speed", 10.0F, RangesKt.rangeTo(0.1F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting maxPitchSpeed = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "max-pitch-speed", 20.0F, RangesKt.rangeTo(0.1F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting jitterDistribution = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "jitter-distribution", AdvancedRotation.SpeedDistribution.NORMAL, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting yawJitter = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "yaw-jitter", AdvancedRotation.PointJitter.NONE, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting pitchJitter = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "pitch-jitter", AdvancedRotation.PointJitter.NONE, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawAmplMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-ampl-min", 5.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchAmplMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-ampl-min", 3.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawAmplMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-ampl-max", 10.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchAmplMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-ampl-max", 5.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawFreqMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-freq-min", 20.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchFreqMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-freq-min", 10.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawFreqMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-freq-max", 40.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchFreqMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-freq-max", 20.0F, RangesKt.rangeTo(1.0F, 30.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting smoothBoxExpand = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "smooth-box-expand", 0.0F, RangesKt.rangeTo(0.1F, 5.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawAccel = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-accel", 1.0F, RangesKt.rangeTo(0.1F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchAccel = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-accel", 1.0F, RangesKt.rangeTo(0.1F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting yawDecel = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "yaw-decel", 0.0F, RangesKt.rangeTo(0.0F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchDecel = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-decel", 0.0F, RangesKt.rangeTo(0.0F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting yawSmoother = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "yaw-smoother", NotificationManager.Notification.Type.LINEAR, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting pitchSmoother = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "pitch-smoother", NotificationManager.Notification.Type.LINEAR, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting minFactorYaw = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "min-factor-yaw", 0.5F, RangesKt.rangeTo(0.0F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting minFactorPitch = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "min-factor-pitch", 0.5F, RangesKt.rangeTo(0.0F, 1.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final BooleanSetting clientLook = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "client-look", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final BooleanSetting overShoot = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "over-shoot", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting osTrigger = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "os-trigger", 45.0F, RangesKt.rangeTo(20.0F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getOverShoot()
   );
   @NotNull
   private static final IntSetting osStreak = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "os-streak", 3, new IntRange(1, 10), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getOverShoot()
   );
   @NotNull
   private static final FloatSetting osValue = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "os-value", 25.0F, RangesKt.rangeTo(20.0F, 180.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getOverShoot()
   );
   @NotNull
   private static final IntSetting osMinTicks = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "os-min-ticks", 2, new IntRange(0, 10), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getOverShoot()
   );
   @NotNull
   private static final IntSetting osMaxTicks = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "os-max-ticks", 2, new IntRange(0, 10), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getOverShoot()
   );
   @NotNull
   private static final BooleanSetting headUp = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "head-up", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final IntSetting huIntervalMin = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hu-interval-min", 165, new IntRange(5, 400), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final IntSetting huIntervalMax = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hu-interval-max", 180, new IntRange(5, 400), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final IntSetting huTicksMin = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hu-ticks-min", 3, new IntRange(1, 20), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final IntSetting huTicksMax = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hu-ticks-max", 6, new IntRange(1, 20), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final FloatSetting huSpeedMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "hu-speed-min", 5.0F, RangesKt.rangeTo(1.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final FloatSetting huSpeedMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "hu-speed-max", 36.0F, RangesKt.rangeTo(1.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHeadUp()
   );
   @NotNull
   private static final BooleanSetting clickJitter = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "click-jitter", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final BooleanSetting do360 = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "do-360", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final IntSetting do360Chance = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "do-360-chance", 20, new IntRange(1, 100), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getYaw360()
   );
   @NotNull
   private static final BooleanSetting holdAfterHit = SettingFactory.computeBooleanSetting(
      Aura.INSTANCE, "hold-after-hit", false, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final IntSetting holdMin = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hold-min", 5, new IntRange(0, 15), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHoldAfterHit()
   );
   @NotNull
   private static final IntSetting holdMax = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hold-max", 7, new IntRange(0, 15), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHoldAfterHit()
   );
   @NotNull
   private static final EnumSetting holdTiming = SettingFactory.computeEnumSetting(
      Aura.INSTANCE,
      "hold-timing",
      AdvancedRotation.HoldTiming.AFTER_RANDOM,
      INSTANCE.getAdvancedOptions(),
      () -> INSTANCE.isFlag() && INSTANCE.getHoldAfterHit()
   );
   @NotNull
   private static final IntSetting holdChance = SettingFactory.computeIntSetting(
      Aura.INSTANCE, "hold-chance", 100, new IntRange(1, 100), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag() && INSTANCE.getHoldAfterHit()
   );
   @NotNull
   private static final FloatSetting pitchMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-min", -90.0F, RangesKt.rangeTo(-90.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-max", 90.0F, RangesKt.rangeTo(-90.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchInTargetMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-in-target-min", -90.0F, RangesKt.rangeTo(-90.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting pitchInTargetMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE, "pitch-in-target-max", 90.0F, RangesKt.rangeTo(-90.0F, 90.0F), INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final EnumSetting filterLowDeltas = SettingFactory.computeEnumSetting(
      Aura.INSTANCE, "filter-low-deltas", AdvancedRotation.FilterLowDeltas.OFF, INSTANCE.getAdvancedOptions(), () -> INSTANCE.isFlag()
   );
   @NotNull
   private static final FloatSetting lowDeltaThreshold = SettingFactory.computeFloatSetting(
      Aura.INSTANCE,
      "low-delta-threshold",
      1.0F,
      RangesKt.rangeTo(0.0F, 20.0F),
      INSTANCE.getAdvancedOptions(),
      () -> INSTANCE.isFlag() && INSTANCE.getFilterLowDeltas() != AdvancedRotation.FilterLowDeltas.OFF
   );
   @NotNull
   private static final FloatSetting replaceMin = SettingFactory.computeFloatSetting(
      Aura.INSTANCE,
      "replace-min",
      3.0F,
      RangesKt.rangeTo(0.0F, 45.0F),
      INSTANCE.getAdvancedOptions(),
      () -> INSTANCE.isFlag() && INSTANCE.getFilterLowDeltas() == AdvancedRotation.FilterLowDeltas.REPLACE
   );
   @NotNull
   private static final FloatSetting replaceMax = SettingFactory.computeFloatSetting(
      Aura.INSTANCE,
      "replace-max",
      5.0F,
      RangesKt.rangeTo(0.0F, 45.0F),
      INSTANCE.getAdvancedOptions(),
      () -> INSTANCE.isFlag() && INSTANCE.getFilterLowDeltas() == AdvancedRotation.FilterLowDeltas.REPLACE
   );
   @NotNull
   private static Vec3 vec3 = Vec3.ZERO;
   @NotNull
   private static Vec3 vec32 = Vec3.ZERO;
   @NotNull
   private static Vec3 vec33 = Vec3.ZERO;
   @NotNull
   private static AdvancedRotation.Tracker tracker = new AdvancedRotation.Tracker(0.0F, 0.0F, 3, null);
   @NotNull
   private static AdvancedRotation.Tracker tracker2 = new AdvancedRotation.Tracker(0.0F, 0.0F, 3, null);
   @NotNull
   private static AdvancedRotation.Tracker tracker3 = new AdvancedRotation.Tracker(0.0F, 0.0F, 3, null);
   @NotNull
   private static AdvancedRotation.Tracker tracker4 = new AdvancedRotation.Tracker(0.0F, 0.0F, 3, null);
   @NotNull
   private static AdvancedRotation.Tracker tracker5 = new AdvancedRotation.Tracker(0.0F, 0.0F, 3, null);
   private static float amount = 1.0F;
   private static float amount2 = 1.0F;
   private static int value;
   private static int value2;
   private static float amount3;
   private static float amount4;
   private static int value3;
   private static int value4;
   private static int value5 = MathUtil.defaultImpl(INSTANCE.getHuIntervalMin(), INSTANCE.getHuIntervalMax(), false, 4, null);
   private static int value6;
   private static int value7;
   private static float amount5;
   private static float amount6;
   private static int value8;
   private static int value9;
   private static int value10;
   @NotNull
   private static final List items;

   private AdvancedRotation() {
   }

   public final boolean isFlag() {
      return Aura.INSTANCE.getRotationMode() == Aura.RotationMode.ADVANCED;
   }

   @NotNull
   public final GroupValue getAdvancedOptions() {
      return (GroupValue)advanced.computeObject(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final AdvancedRotation.Point getPoint() {
      return (AdvancedRotation.Point)point.computeObject(this, $$delegatedProperties[1]);
   }

   @NotNull
   public final AdvancedRotation.PointJitter getPointJitter() {
      return (AdvancedRotation.PointJitter)pointJitter.computeObject(this, $$delegatedProperties[2]);
   }

   public final float getPointVRange() {
      return ((Number)pointVRange.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   public final float getPointHRange() {
      return ((Number)pointHRange.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   public final float getPointHFreq() {
      return ((Number)pointHFreq.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   public final float getPointVFreq() {
      return ((Number)pointVFreq.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   @NotNull
   public final AdvancedRotation.SpeedDistribution getSpeedDistribution() {
      return (AdvancedRotation.SpeedDistribution)speedDistribution.computeObject(this, $$delegatedProperties[7]);
   }

   public final float getMinYawSpeed() {
      return ((Number)minYawSpeed.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   public final float getMaxYawSpeed() {
      return ((Number)maxYawSpeed.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   public final float getMinPitchSpeed() {
      return ((Number)minPitchSpeed.computeObject(this, $$delegatedProperties[10])).floatValue();
   }

   public final float getMaxPitchSpeed() {
      return ((Number)maxPitchSpeed.computeObject(this, $$delegatedProperties[11])).floatValue();
   }

   @NotNull
   public final AdvancedRotation.SpeedDistribution getJitterDistribution() {
      return (AdvancedRotation.SpeedDistribution)jitterDistribution.computeObject(this, $$delegatedProperties[12]);
   }

   @NotNull
   public final AdvancedRotation.PointJitter getYawJitter() {
      return (AdvancedRotation.PointJitter)yawJitter.computeObject(this, $$delegatedProperties[13]);
   }

   @NotNull
   public final AdvancedRotation.PointJitter getPitchJitter() {
      return (AdvancedRotation.PointJitter)pitchJitter.computeObject(this, $$delegatedProperties[14]);
   }

   public final float getYawAmplMin() {
      return ((Number)yawAmplMin.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   public final float getPitchAmplMin() {
      return ((Number)pitchAmplMin.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   public final float getYawAmplMax() {
      return ((Number)yawAmplMax.computeObject(this, $$delegatedProperties[17])).floatValue();
   }

   public final float getPitchAmplMax() {
      return ((Number)pitchAmplMax.computeObject(this, $$delegatedProperties[18])).floatValue();
   }

   public final float getYawFreqMin() {
      return ((Number)yawFreqMin.computeObject(this, $$delegatedProperties[19])).floatValue();
   }

   public final float getPitchFreqMin() {
      return ((Number)pitchFreqMin.computeObject(this, $$delegatedProperties[20])).floatValue();
   }

   public final float getYawFreqMax() {
      return ((Number)yawFreqMax.computeObject(this, $$delegatedProperties[21])).floatValue();
   }

   public final float getPitchFreqMax() {
      return ((Number)pitchFreqMax.computeObject(this, $$delegatedProperties[22])).floatValue();
   }

   public final float getSmoothHbScale() {
      return ((Number)smoothBoxExpand.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   public final float getYawAcceleration() {
      return ((Number)yawAccel.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   public final float getPitchAcceleration() {
      return ((Number)pitchAccel.computeObject(this, $$delegatedProperties[25])).floatValue();
   }

   public final float getYawDeceleration() {
      return ((Number)yawDecel.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   public final float getPitchDeceleration() {
      return ((Number)pitchDecel.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   @NotNull
   public final NotificationManager.Notification.Type getYawSmoother() {
      return (NotificationManager.Notification.Type)yawSmoother.computeObject(this, $$delegatedProperties[28]);
   }

   @NotNull
   public final NotificationManager.Notification.Type getPitchSmoother() {
      return (NotificationManager.Notification.Type)pitchSmoother.computeObject(this, $$delegatedProperties[29]);
   }

   public final float getYawAccelerationMin() {
      return ((Number)minFactorYaw.computeObject(this, $$delegatedProperties[30])).floatValue();
   }

   public final float getPitchAccelerationMin() {
      return ((Number)minFactorPitch.computeObject(this, $$delegatedProperties[31])).floatValue();
   }

   public final boolean getClientLook() {
      return (Boolean)clientLook.computeObject(this, $$delegatedProperties[32]);
   }

   public final boolean getOverShoot() {
      return (Boolean)overShoot.computeObject(this, $$delegatedProperties[33]);
   }

   public final float getOvershootTrigger() {
      return ((Number)osTrigger.computeObject(this, $$delegatedProperties[34])).floatValue();
   }

   public final int getOvershootStreak() {
      return ((Number)osStreak.computeObject(this, $$delegatedProperties[35])).intValue();
   }

   public final float getOvershootValue() {
      return ((Number)osValue.computeObject(this, $$delegatedProperties[36])).floatValue();
   }

   public final int getOvershootMinTicks() {
      return ((Number)osMinTicks.computeObject(this, $$delegatedProperties[37])).intValue();
   }

   public final int getOvershootMaxTicks() {
      return ((Number)osMaxTicks.computeObject(this, $$delegatedProperties[38])).intValue();
   }

   public final boolean getHeadUp() {
      return (Boolean)headUp.computeObject(this, $$delegatedProperties[39]);
   }

   public final int getHuIntervalMin() {
      return ((Number)huIntervalMin.computeObject(this, $$delegatedProperties[40])).intValue();
   }

   public final int getHuIntervalMax() {
      return ((Number)huIntervalMax.computeObject(this, $$delegatedProperties[41])).intValue();
   }

   public final int getHuTicksMin() {
      return ((Number)huTicksMin.computeObject(this, $$delegatedProperties[42])).intValue();
   }

   public final int getHuTicksMax() {
      return ((Number)huTicksMax.computeObject(this, $$delegatedProperties[43])).intValue();
   }

   public final float getHuSpeedMin() {
      return ((Number)huSpeedMin.computeObject(this, $$delegatedProperties[44])).floatValue();
   }

   public final float getHuSpeedMax() {
      return ((Number)huSpeedMax.computeObject(this, $$delegatedProperties[45])).floatValue();
   }

   public final boolean getClickJitter() {
      return (Boolean)clickJitter.computeObject(this, $$delegatedProperties[46]);
   }

   public final boolean getYaw360() {
      return (Boolean)do360.computeObject(this, $$delegatedProperties[47]);
   }

   public final int getYaw360Chance() {
      return ((Number)do360Chance.computeObject(this, $$delegatedProperties[48])).intValue();
   }

   public final boolean getHoldAfterHit() {
      return (Boolean)holdAfterHit.computeObject(this, $$delegatedProperties[49]);
   }

   public final int getHoldMin() {
      return ((Number)holdMin.computeObject(this, $$delegatedProperties[50])).intValue();
   }

   public final int getHoldMax() {
      return ((Number)holdMax.computeObject(this, $$delegatedProperties[51])).intValue();
   }

   @NotNull
   public final AdvancedRotation.HoldTiming getHoldTiming() {
      return (AdvancedRotation.HoldTiming)holdTiming.computeObject(this, $$delegatedProperties[52]);
   }

   public final int getHoldChance() {
      return ((Number)holdChance.computeObject(this, $$delegatedProperties[53])).intValue();
   }

   public final float getPitchMin() {
      return ((Number)pitchMin.computeObject(this, $$delegatedProperties[54])).floatValue();
   }

   public final float getPitchMax() {
      return ((Number)pitchMax.computeObject(this, $$delegatedProperties[55])).floatValue();
   }

   public final float getPitchITMin() {
      return ((Number)pitchInTargetMin.computeObject(this, $$delegatedProperties[56])).floatValue();
   }

   public final float getPitchITMax() {
      return ((Number)pitchInTargetMax.computeObject(this, $$delegatedProperties[57])).floatValue();
   }

   @NotNull
   public final AdvancedRotation.FilterLowDeltas getFilterLowDeltas() {
      return (AdvancedRotation.FilterLowDeltas)filterLowDeltas.computeObject(this, $$delegatedProperties[58]);
   }

   public final float getLowDeltaThreshold() {
      return ((Number)lowDeltaThreshold.computeObject(this, $$delegatedProperties[59])).floatValue();
   }

   public final float getReplaceMin() {
      return ((Number)replaceMin.computeObject(this, $$delegatedProperties[60])).floatValue();
   }

   public final float getReplaceMax() {
      return ((Number)replaceMax.computeObject(this, $$delegatedProperties[61])).floatValue();
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      if (this.isFlag2()) {
         return this.getHoldTiming() == AdvancedRotation.HoldTiming.BEFORE_RANDOM
            ? currentRotation
            : this.computeRotation(currentRotation.getAmount(), currentRotation.getAmount2());
      } else {
         Vec3 eyePosition = this.fetchEyePosition(target);
         this.applyGetBoundingBox(target);
         target.setBoundingBox(target.getBoundingBox().inflate(this.getSmoothHbScale()));
         InteractionManager instance = InteractionManager.INSTANCE;
         float attackRange = Aura.INSTANCE.getAttackRange() + 2.0F;
         boolean flag2 = !Aura.INSTANCE.getIgnoreWalls();
         float value11 = attackRange;
         boolean checkDistanceToSqr = instance.checkDistanceToSqr(target, currentRotation, value11, flag2);
         target.setBoundingBox(target.getBoundingBox().inflate(-this.getSmoothHbScale()));
         if (checkDistanceToSqr) {
            amount = amount - this.getYawDeceleration();
            amount2 = amount2 - this.getPitchDeceleration();
         } else {
            amount = amount + this.getYawAcceleration();
            amount2 = amount2 + this.getPitchAcceleration();
         }

         amount = this.computeAmount(amount, RangesKt.rangeTo(this.getYawAccelerationMin(), 1.0F));
         amount2 = this.computeAmount(amount2, RangesKt.rangeTo(this.getPitchAccelerationMin(), 1.0F));
         double degrees = Math.toDegrees(Math.atan2(eyePosition.z - MinecraftHolder.getLocalPlayer().getZ(), eyePosition.x - MinecraftHolder.getLocalPlayer().getX()))
            - 90.0F;
         double value12 = -Math.toDegrees(
            Math.atan2(
               eyePosition.y
                  - (
                     EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         );
         if (this.getOverShoot() && value > 0) {
            degrees += amount4 + this.getOvershootValue() * amount3;
            int value13 = value;
            value = value13 + -1;
         } else if (this.getYaw360() && value7 > 0) {
            amount5 = amount5 + 360.0F / value6;
            degrees += amount5 * amount6;
            int value72 = value7;
            value7 = value72 + -1;
         }

         double wrapDegrees = Mth.wrapDegrees(degrees - currentRotation.getAmount());
         double amount22 = value12 - currentRotation.getAmount2();
         double amount7 = this.computeAmount((ClosedRange)RangesKt.rangeTo(this.getMinYawSpeed(), this.getMaxYawSpeed()), false)
            * ((Number)this.getYawSmoother().getCallback().invoke((double)amount)).doubleValue();
         double amount8 = this.computeAmount((ClosedRange)RangesKt.rangeTo(this.getMinPitchSpeed(), this.getMaxPitchSpeed()), false)
            * ((Number)this.getPitchSmoother().getCallback().invoke((double)amount2)).doubleValue();
         float amount9 = (float)this.computeAmount(Math.abs(wrapDegrees), RangesKt.rangeTo(0.0, amount7));
         float amount10 = (float)this.computeAmount(Math.abs(amount22), RangesKt.rangeTo(0.0, amount8));
         float amount11 = currentRotation.getAmount() + (wrapDegrees > 0.0 ? amount9 : -amount9);
         float amount23 = currentRotation.getAmount2() + (amount22 > 0.0 ? amount10 : -amount10);
         Rotation rotation = this.computeRotation(amount11, amount23);
         amount11 = rotation.getAmount();
         amount23 = rotation.getAmount2();
         if (this.getOverShoot() && amount9 > this.getOvershootTrigger() && value <= 0) {
            int value14 = value2++;
            amount4 = 0.0F;
            if (value2 >= this.getOvershootStreak()) {
               value2 = 0;
               value = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getOvershootMinTicks(), this.getOvershootMaxTicks())), false, 2, null);
               amount3 = amount11 - currentRotation.getAmount() > 0.0F ? 1.0F : -1.0F;
            }
         }

         if (this.getHeadUp()) {
            int value15 = value4++;
            if (value15 > value5) {
               value5 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getHuIntervalMin(), this.getHuIntervalMax())), false, 2, null);
               value4 = 0;
               value3 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getHuTicksMin(), this.getHuTicksMax())), false, 2, null);
            }
         }

         if (this.getHeadUp() && value3 > 0) {
            int value32 = value3;
            value3 = value32 + -1;
            amount23 -= this.computeAmount((ClosedRange)RangesKt.rangeTo(this.getHuSpeedMin(), this.getHuSpeedMax()), true);
            amount11 += amount23 * 0.1F;
         }

         if (this.getClickJitter() && value8 >= 0) {
            Pair value82 = (Pair)((List)items.get(value9)).get(4 - value8);
            amount11 += (float)((Number)value82.getFirst()).doubleValue();
            amount23 += (float)((Number)value82.getSecond()).doubleValue();
            int value83 = value8;
            value8 = value83 + -1;
         }

         if (this.getFilterLowDeltas() != AdvancedRotation.FilterLowDeltas.OFF) {
            float amount12 = currentRotation.getAmount() - amount11;
            float amount24 = currentRotation.getAmount2() - amount23;
            float lowDeltaThreshold2 = this.getLowDeltaThreshold();
            float value16 = Math.abs(amount12);
            if (0.0F <= value16 ? value16 <= lowDeltaThreshold2 : false) {
               amount11 = this.getFilterLowDeltas() == AdvancedRotation.FilterLowDeltas.REMOVE
                  ? currentRotation.getAmount()
                  : currentRotation.getAmount()
                     + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getReplaceMin(), this.getReplaceMax()), false, 2, null);
            }

            lowDeltaThreshold2 = this.getLowDeltaThreshold();
            value16 = Math.abs(amount24);
            if (0.0F <= value16 ? value16 <= lowDeltaThreshold2 : false) {
               amount23 = this.getFilterLowDeltas() == AdvancedRotation.FilterLowDeltas.REMOVE
                  ? currentRotation.getAmount2()
                  : currentRotation.getAmount2()
                     + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getReplaceMin(), this.getReplaceMax()), false, 2, null);
            }
         }

         return MinecraftHolder.getLocalPlayer().getBoundingBox().intersects(target.getBoundingBox())
            ? new Rotation(amount11, this.computeAmount(amount23, RangesKt.rangeTo(this.getPitchITMin(), this.getPitchITMax())), false, null, 12, null)
            : new Rotation(amount11, this.computeAmount(amount23, RangesKt.rangeTo(this.getPitchMin(), this.getPitchMax())), false, null, 12, null);
      }
   }

   private final boolean isFlag2() {
      if (this.getHoldAfterHit()) {
         int value102 = value10;
         value10 = value102 + -1;
         if (value10 > 0) {
            return true;
         }
      }

      return false;
   }

   private final Rotation computeRotation(float amount, float amount2) {
      float time = (float)(MinecraftHolder.getTime() % 10000L) / 500.0F;
      float defaultImpl2 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getYawFreqMin(), this.getYawFreqMax()), false, 2, null);
      float defaultImpl22 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getYawAmplMin(), this.getYawAmplMax()), false, 2, null);
      float defaultImpl23 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getPitchFreqMin(), this.getPitchFreqMax()), false, 2, null);
      float defaultImpl24 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(this.getPitchAmplMin(), this.getPitchAmplMax()), false, 2, null);
      float amount7 = amount;
      float amount22 = amount2;
      switch (AdvancedRotation.Sample.values[this.getYawJitter().ordinal()]) {
         case 1:
            amount7 = amount + (float)Math.sin(time * defaultImpl2) * defaultImpl22;
            break;
         case 2:
            amount7 = amount + tracker.getAmount3() * defaultImpl22;
            break;
         case 3:
            amount7 = amount
               + ((float)Math.sin(MinecraftHolder.getLocalPlayer().tickCount * defaultImpl2) + this.computeAmount((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), true))
                  * defaultImpl22;
            break;
         case 4:
            amount7 = amount + this.computeAmount((ClosedRange)RangesKt.rangeTo(-defaultImpl22, defaultImpl22), true);
         case 5:
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      switch (AdvancedRotation.Sample.values[this.getPitchJitter().ordinal()]) {
         case 1:
            amount22 = amount2 + (float)Math.sin(time * defaultImpl23) * defaultImpl24;
            break;
         case 2:
            amount22 = amount2 + tracker2.getAmount3() * defaultImpl24;
            break;
         case 3:
            amount22 = amount2
               + ((float)Math.sin(MinecraftHolder.getLocalPlayer().tickCount * defaultImpl23) + this.computeAmount((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), true))
                  * defaultImpl24;
            break;
         case 4:
            amount22 = amount2 + this.computeAmount((ClosedRange)RangesKt.rangeTo(-defaultImpl24, defaultImpl24), true);
         case 5:
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return new Rotation(amount7, amount22, false, null, 12, null);
   }

   private final float computeAmount(ClosedRange closedRange, boolean flag) {
      if (flag) {
         return this.getJitterDistribution() == AdvancedRotation.SpeedDistribution.FLAT
            ? MathUtil.defaultImpl2(closedRange, false, 2, null)
            : ((Number)closedRange.getStart()).floatValue()
               + (((Number)closedRange.getEndInclusive()).floatValue() - ((Number)closedRange.getStart()).floatValue())
                  * this.computeAmount(((float)MathUtil.getRandom2() + 3.0F) / 6.0F, RangesKt.rangeTo(0.0F, 1.0F));
      } else {
         return this.getSpeedDistribution() == AdvancedRotation.SpeedDistribution.FLAT
            ? MathUtil.defaultImpl2(closedRange, false, 2, null)
            : ((Number)closedRange.getStart()).floatValue()
               + (((Number)closedRange.getEndInclusive()).floatValue() - ((Number)closedRange.getStart()).floatValue())
                  * this.computeAmount(((float)MathUtil.getRandom2() + 3.0F) / 6.0F, RangesKt.rangeTo(0.0F, 1.0F));
      }
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         return target.getEyePosition();
      } else {
         float time = (float)(MinecraftHolder.getTime() % 10000L) / 500.0F;

         Vec3 vec34 = switch (AdvancedRotation.Sample.values[this.getPointJitter().ordinal()]) {
            case 1 -> new Vec3(
               (float)Math.sin(time * this.getPointHFreq()) * this.getPointHRange(),
               (float)Math.sin(time * this.getPointVFreq()) * this.getPointVRange(),
               (float)Math.sin(time * this.getPointHFreq()) * this.getPointHRange()
            );
            case 2 -> new Vec3(
               tracker3.getAmount3() * this.getPointHRange(), tracker4.getAmount3() * this.getPointVRange(), tracker5.getAmount3() * this.getPointHRange()
            );
            case 3 -> new Vec3(
               (
                     (float)Math.sin(MinecraftHolder.getLocalPlayer().tickCount * this.getPointHFreq())
                        + this.computeAmount((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), true)
                  )
                  * this.getPointHRange(),
               (
                     (float)Math.sin(MinecraftHolder.getLocalPlayer().tickCount * this.getPointVFreq())
                        + this.computeAmount((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), true)
                  )
                  * this.getPointVRange(),
               (
                     (float)Math.sin(MinecraftHolder.getLocalPlayer().tickCount * this.getPointHFreq())
                        + this.computeAmount((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), true)
                  )
                  * this.getPointHRange()
            );
            case 4 -> new Vec3(
               this.computeAmount((ClosedRange)RangesKt.rangeTo(-this.getPointHRange(), this.getPointHRange()), true),
               this.computeAmount((ClosedRange)RangesKt.rangeTo(-this.getPointVRange(), this.getPointVRange()), true),
               this.computeAmount((ClosedRange)RangesKt.rangeTo(-this.getPointHRange(), this.getPointHRange()), true)
            );
            case 5 -> Vec3.ZERO;
            default -> throw new NoWhenBranchMatchedException();
         };
         switch (AdvancedRotation.Sample.values2[this.getPoint().ordinal()]) {
            case 1:
               return target.getBoundingBox().getCenter().add(vec34);
            case 2:
               return target.getEyePosition().add(vec34);
            case 3:
               return EntityPositionUtil.computeVec3(target).add(vec3).add(vec34);
            case 4:
               return EntityPositionUtil.computeVec3(target).add(vec33).add(vec34);
            case 5:
               AABB contract = target.getBoundingBox().contract(this.getPointHRange() + 0.1, this.getPointVRange() + 0.2, this.getPointHRange() + 0.1);
               return new Vec3(
                  this.computeAmount(MinecraftHolder.getLocalPlayer().getEyePosition().x, RangesKt.rangeTo(contract.minX, contract.maxX)),
                  this.computeAmount(MinecraftHolder.getLocalPlayer().getEyePosition().y, RangesKt.rangeTo(contract.minY, contract.maxY)),
                  this.computeAmount(MinecraftHolder.getLocalPlayer().getEyePosition().z, RangesKt.rangeTo(contract.minZ, contract.maxZ))
               );
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   public final double computeAmount(double amount, ClosedFloatingPointRange range) {
      double value11 = ((Number)range.getStart()).doubleValue();
      double value12 = ((Number)range.getEndInclusive()).doubleValue();
      if (value11 > value12) {
         double value13 = value12;
         value12 = value11;
         value11 = value13;
      }

      if (amount < value11) {
         return value11;
      } else {
         return amount > value12 ? value12 : amount;
      }
   }

   public final float computeAmount(float amount, ClosedFloatingPointRange range) {
      float value11 = ((Number)range.getStart()).floatValue();
      float value12 = ((Number)range.getEndInclusive()).floatValue();
      if (value11 > value12) {
         float value13 = value12;
         value12 = value11;
         value11 = value13;
      }

      if (amount < value11) {
         return value11;
      } else {
         return amount > value12 ? value12 : amount;
      }
   }

   public final void update() {
      if (this.getYaw360() && MathUtil.defaultImpl3((ClosedRange)(new IntRange(0, 100)), false, 2, null) >= 100 - this.getYaw360Chance()) {
         value6 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(7, 9)), false, 2, null);
         value7 = value6;
         amount6 = MinecraftHolder.getLocalPlayer().tickCount % 2 == 0 ? -1.0F : 1.0F;
      }

      if (this.getClickJitter()) {
         value8 = 4;
         value9 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(0, 4)), false, 2, null);
      }

      if (this.getHoldAfterHit() && MathUtil.defaultImpl3((ClosedRange)(new IntRange(0, 100)), false, 2, null) >= 100 - this.getHoldChance()) {
         value10 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getHoldMin(), this.getHoldMax())), false, 2, null);
      }

      double pointHRange2 = 0.3 - this.getPointHRange() / 2.0;
      double pointVRange2 = 1.81 - this.getPointVRange() / 2.0;
      ClosedFloatingPointRange<Double> rangeTo = RangesKt.rangeTo(-pointHRange2, pointHRange2);
      ClosedFloatingPointRange<Double> rangeTo2 = RangesKt.rangeTo(0.0, pointVRange2);
      vec33 = new Vec3(
         this.computeAmount(MathUtil.defaultImpl((ClosedRange)rangeTo, false, 2, null), RangesKt.rangeTo(-0.3, 0.3)),
         this.computeAmount(MathUtil.defaultImpl((ClosedRange)rangeTo2, false, 2, null), RangesKt.rangeTo(0.0, 1.81)),
         this.computeAmount(MathUtil.defaultImpl((ClosedRange)rangeTo, false, 2, null), RangesKt.rangeTo(-0.3, 0.3))
      );
   }

   public final void applyGetBoundingBox(Entity target) {
      double value11 = 0.003;
      double value12 = 0.03;
      double value13 = 0.001;
      double value14 = 0.03;
      double pointHRange2 = this.getPointHRange();
      double xsize = target.getBoundingBox().getXsize();
      double ysize = target.getBoundingBox().getYsize();
      double zsize = target.getBoundingBox().getZsize();
      if (Intrinsics.areEqual(vec32, Vec3.ZERO)) {
         vec32 = new Vec3(
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null),
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null),
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null)
         );
      }

      double defaultImpl = vec32.x;
      double defaultImpl2 = vec32.y;
      double defaultImpl3 = vec32.z;
      if (vec3.x >= (xsize - pointHRange2) / 2.0F) {
         defaultImpl = -MathUtil.defaultImpl(value11, value12, false, 4, null);
      }

      if (vec3.x <= -(xsize - pointHRange2) / 2.0F) {
         defaultImpl = MathUtil.defaultImpl(value11, value12, false, 4, null);
      }

      if (vec3.y >= ysize) {
         defaultImpl2 = -MathUtil.defaultImpl(value13, value14, false, 4, null);
      }

      if (vec3.y <= pointHRange2) {
         defaultImpl2 = MathUtil.defaultImpl(value13, value14, false, 4, null);
      }

      if (vec3.z >= (zsize - pointHRange2) / 2.0F) {
         defaultImpl3 = -MathUtil.defaultImpl(value11, value12, false, 4, null);
      }

      if (vec3.z <= -(zsize - pointHRange2) / 2.0F) {
         defaultImpl3 = MathUtil.defaultImpl(value11, value12, false, 4, null);
      }

      if ((-0.03 <= defaultImpl ? defaultImpl <= 0.03 : false) || (-0.03 <= defaultImpl2 ? defaultImpl2 <= 0.03 : false) || (-0.03 <= defaultImpl3 ? defaultImpl3 <= 0.03 : false)) {
         vec32 = new Vec3(
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null),
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null),
            MathUtil.defaultImpl(-pointHRange2, pointHRange2, false, 4, null)
         );
      }

      vec32 = new Vec3(defaultImpl, defaultImpl2, defaultImpl3);
      vec3 = vec3.add(vec32);
      vec3 = new Vec3(
         this.computeAmount(vec3.x, RangesKt.rangeTo(-xsize, xsize)),
         this.computeAmount(vec3.y, RangesKt.rangeTo(0.0, ysize)),
         this.computeAmount(vec3.z, RangesKt.rangeTo(-zsize, zsize))
      );
      vec3.add(MathUtil.defaultImpl(-0.03, 0.03, false, 4, null), 0.0, MathUtil.defaultImpl(-0.03, 0.03, false, 4, null));
   }

   static {
      List[] lists = new List[5];
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.15000153, 0.14999962),
         TuplesKt.to(-0.15000153, -0.14999962),
         TuplesKt.to(-0.6000061, 0.0),
         TuplesKt.to(-0.6000061, 0.0),
         TuplesKt.to(0.45000458, 0.0)
      };
      lists[0] = CollectionsKt.listOf(pairs);
      pairs = new Pair[]{
         TuplesKt.to(-0.15000153, 0.0),
         TuplesKt.to(-0.30000305, 0.0),
         TuplesKt.to(0.30000305, 0.14999962),
         TuplesKt.to(0.15000153, -0.14999962),
         TuplesKt.to(0.0, 0.0)
      };
      lists[1] = CollectionsKt.listOf(pairs);
      pairs = new Pair[]{
         TuplesKt.to(0.15000153, -0.14999962),
         TuplesKt.to(-0.6000061, 0.0),
         TuplesKt.to(0.15000153, 0.14999962),
         TuplesKt.to(-0.15000153, 0.0),
         TuplesKt.to(0.30000305, 0.0)
      };
      lists[2] = CollectionsKt.listOf(pairs);
      pairs = new Pair[]{
         TuplesKt.to(-0.6000061, 0.29999924),
         TuplesKt.to(0.6000061, 0.29999924),
         TuplesKt.to(-0.30000305, -0.44999886),
         TuplesKt.to(0.15000153, 0.0),
         TuplesKt.to(0.0, 0.0)
      };
      lists[3] = CollectionsKt.listOf(pairs);
      pairs = new Pair[]{
         TuplesKt.to(-0.30000305, 0.0),
         TuplesKt.to(-0.29999542, 0.6000004),
         TuplesKt.to(0.30000305, 0.0),
         TuplesKt.to(-0.7500076, -0.44999886),
         TuplesKt.to(0.30000305, 0.0)
      };
      lists[4] = CollectionsKt.listOf(pairs);
      items = CollectionsKt.listOf(lists);
   }

   public static enum FilterLowDeltas {
      REMOVE,
      REPLACE,
      OFF;

      private static final AdvancedRotation.FilterLowDeltas[] filterLowDeltass = $values();
      private static final EnumEntries<AdvancedRotation.FilterLowDeltas> enumEntries = EnumEntriesKt.enumEntries(filterLowDeltass);

      private FilterLowDeltas() {
      }

      @NotNull
      public static EnumEntries<AdvancedRotation.FilterLowDeltas> getEnumEntries() {
         return enumEntries;
      }

      private static final AdvancedRotation.FilterLowDeltas[] $values() {
         return new AdvancedRotation.FilterLowDeltas[]{REMOVE, REPLACE, OFF};
      }
   }

   public static enum HoldTiming {
      BEFORE_RANDOM,
      AFTER_RANDOM;

      private static final AdvancedRotation.HoldTiming[] holdTimings = $values();
      private static final EnumEntries<AdvancedRotation.HoldTiming> enumEntries = EnumEntriesKt.enumEntries(holdTimings);

      private HoldTiming() {
      }

      @NotNull
      public static EnumEntries<AdvancedRotation.HoldTiming> getEnumEntries() {
         return enumEntries;
      }

      private static final AdvancedRotation.HoldTiming[] $values() {
         return new AdvancedRotation.HoldTiming[]{BEFORE_RANDOM, AFTER_RANDOM};
      }
   }

   public static enum Point {
      CENTER,
      EYES,
      CLOSEST,
      ROAMING,
      RUST;

      private static final AdvancedRotation.Point[] points = $values();
      private static final EnumEntries<AdvancedRotation.Point> enumEntries = EnumEntriesKt.enumEntries(points);

      private Point() {
      }

      @NotNull
      public static EnumEntries<AdvancedRotation.Point> getEnumEntries() {
         return enumEntries;
      }

      private static final AdvancedRotation.Point[] $values() {
         return new AdvancedRotation.Point[]{CENTER, EYES, CLOSEST, ROAMING, RUST};
      }
   }

   public static enum PointJitter {
      NONE,
      RANDOM,
      ROAMING,
      SINE,
      DRUNK_SINE;

      private static final AdvancedRotation.PointJitter[] pointJitters = $values();
      private static final EnumEntries<AdvancedRotation.PointJitter> enumEntries = EnumEntriesKt.enumEntries(pointJitters);

      private PointJitter() {
      }

      @NotNull
      public static EnumEntries<AdvancedRotation.PointJitter> getEnumEntries() {
         return enumEntries;
      }

      private static final AdvancedRotation.PointJitter[] $values() {
         return new AdvancedRotation.PointJitter[]{NONE, RANDOM, ROAMING, SINE, DRUNK_SINE};
      }
   }

   public static final class Sample {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[AdvancedRotation.PointJitter.values().length];

         try {
            values3[AdvancedRotation.PointJitter.SINE.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[AdvancedRotation.PointJitter.ROAMING.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values3[AdvancedRotation.PointJitter.DRUNK_SINE.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values3[AdvancedRotation.PointJitter.RANDOM.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values3[AdvancedRotation.PointJitter.NONE.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values3;
         values3 = new int[AdvancedRotation.Point.values().length];

         try {
            values3[AdvancedRotation.Point.CENTER.ordinal()] = 1;
         } catch (NoSuchFieldError exception6) {
         }

         try {
            values3[AdvancedRotation.Point.EYES.ordinal()] = 2;
         } catch (NoSuchFieldError exception7) {
         }

         try {
            values3[AdvancedRotation.Point.ROAMING.ordinal()] = 3;
         } catch (NoSuchFieldError exception8) {
         }

         try {
            values3[AdvancedRotation.Point.RUST.ordinal()] = 4;
         } catch (NoSuchFieldError exception9) {
         }

         try {
            values3[AdvancedRotation.Point.CLOSEST.ordinal()] = 5;
         } catch (NoSuchFieldError exception10) {
         }

         values2 = values3;
      }
   }

   public static enum SpeedDistribution {
      FLAT,
      NORMAL;

      private static final AdvancedRotation.SpeedDistribution[] speedDistributions = $values();
      private static final EnumEntries<AdvancedRotation.SpeedDistribution> enumEntries = EnumEntriesKt.enumEntries(speedDistributions);

      private SpeedDistribution() {
      }

      @NotNull
      public static EnumEntries<AdvancedRotation.SpeedDistribution> getEnumEntries() {
         return enumEntries;
      }

      private static final AdvancedRotation.SpeedDistribution[] $values() {
         return new AdvancedRotation.SpeedDistribution[]{FLAT, NORMAL};
      }
   }

   public static final class Tracker {
      private float amount;
      private float amount2;

      public Tracker(float amount, float amount2) {
         this.amount = amount;
         this.amount2 = amount2;
      }

      public Tracker(float amount, float amount2, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 1) != 0) {
            amount = 0.0F;
         }

         if ((value & 2) != 0) {
            amount2 = 0.0F;
         }

         this(amount, amount2);
      }

      public final float getAmount() {
         return this.amount;
      }

      public final void setAmount(float amount) {
         this.amount = amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final void setAmount2(float amount) {
         this.amount2 = amount;
      }

      public final float getAmount3() {
         this.amount2 = MathUtil.defaultImpl(-0.1F, 0.1F, false, 4, null);
         this.amount = this.amount + this.amount2;
         this.amount = AdvancedRotation.INSTANCE.computeAmount(this.amount, RangesKt.rangeTo(-1.0F, 1.0F));
         return this.amount;
      }
   }
}