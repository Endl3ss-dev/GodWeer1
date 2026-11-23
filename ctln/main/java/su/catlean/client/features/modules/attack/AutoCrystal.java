// deobf by pusia
package su.catlean.client.features.modules.attack;

import net.minecraft.network.PacketListener;
import su.catlean.utils.KotlinCompat;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Attempt;
import su.catlean.api.data.ConfigState;
import su.catlean.api.data.CrystalWrapper;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.api.data.DamageData;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PostSyncEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.RenderEntityEvent;
import su.catlean.api.event.events.world.AfterEntitySpawn;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.esp.NameTags;
import su.catlean.client.features.modules.player.AutoMend;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.features.modules.protect.Offhand;
import su.catlean.client.features.modules.protect.TrapPlanner;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.render.EntityPreviewRenderer;
import su.catlean.client.render.GradientQuad;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.BreakingBlocksService;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.CrystalService;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerConfigProfile;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientboundEntityEventPacketAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.ItemPredicate;
import su.catlean.utils.inventory.ItemTypeUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.render.Identifiers;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class AutoCrystal extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoCrystal.class, "mainGroup", "getMainGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "timing", "getTiming()Lsu/catlean/g1;", 0), new PropertyReference1Impl(AutoCrystal.class, "multiAction", "getMultiAction()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "onCrystalSpawn", "getOnCrystalSpawn()Lsu/catlean/vn;", 0), new PropertyReference1Impl(AutoCrystal.class, "spawnTiming", "getSpawnTiming()Lsu/catlean/fg;", 0), new PropertyReference1Impl(AutoCrystal.class, "rotate", "getRotate()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "rayTrace", "getRayTrace()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "crystalMultiPoint", "getCrystalMultiPoint()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "asyncCalc", "getAsyncCalc()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "failNotify", "getFailNotify()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "predict", "getPredict()Lsu/catlean/td;", 0), new PropertyReference1Impl(AutoCrystal.class, "predictTicksValue", "getPredictTicksValue()I", 0), new PropertyReference1Impl(AutoCrystal.class, "placeGroup", "getPlaceGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "oldVer", "getOldVer()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "placeDelay", "getPlaceDelay()I", 0), new PropertyReference1Impl(AutoCrystal.class, "placeRange", "getPlaceRange()F", 0), new PropertyReference1Impl(AutoCrystal.class, "limitPlace", "getLimitPlace()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "removeFire", "getRemoveFire()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "removeLava", "getRemoveLava()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "breakGroup", "getBreakGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "breakDelay", "getBreakDelay()I", 0), new PropertyReference1Impl(AutoCrystal.class, "grimBreakRange", "getGrimBreakRange()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "explodeRange", "getExplodeRange()F", 0), new PropertyReference1Impl(AutoCrystal.class, "limitBreak", "getLimitBreak()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "randomizeDelay", "getRandomizeDelay()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "pauseGroup", "getPauseGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "mining", "getMining()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "eating", "getEating()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "inventory", "getInventory()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "switchPause", "getSwitchPause()I", 0), new PropertyReference1Impl(AutoCrystal.class, "offHandPause", "getOffHandPause()I", 0), new PropertyReference1Impl(AutoCrystal.class, "damagesGroup", "getDamagesGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "assumeBestArmor", "getAssumeBestArmor()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "minDamage", "getMinDamage()F", 0), new PropertyReference1Impl(AutoCrystal.class, "maxSelfDamage", "getMaxSelfDamage()F", 0), new PropertyReference1Impl(AutoCrystal.class, "efficiencyFactor", "getEfficiencyFactor()F", 0), new PropertyReference1Impl(AutoCrystal.class, "lethalMultiplier", "getLethalMultiplier()I", 0), new PropertyReference1Impl(AutoCrystal.class, "overrideSelfDamage", "getOverrideSelfDamage()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "overrideMinDamage", "getOverrideMinDamage()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "armorScale", "getArmorScale()F", 0), new PropertyReference1Impl(AutoCrystal.class, "facePlaceHp", "getFacePlaceHp()F", 0), new PropertyReference1Impl(AutoCrystal.class, "explodeEChestFarm", "getExplodeEChestFarm()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "antiPortal", "getAntiPortal()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "antiQuiver", "getAntiQuiver()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "terrainIgnore", "getTerrainIgnore()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "friendProtection", "getFriendProtection()Lsu/catlean/se;", 0), new PropertyReference1Impl(AutoCrystal.class, "calcShields", "getCalcShields()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "damageSync", "getDamageSync()Lsu/catlean/ll;", 0), new PropertyReference1Impl(AutoCrystal.class, "facePlaceSync", "getFacePlaceSync()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "lowCrystals", "getLowCrystals()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "stacksLeft", "getStacksLeft()F", 0), new PropertyReference1Impl(AutoCrystal.class, "minimizeSelfDamage", "getMinimizeSelfDamage()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "maxDamageReduction", "getMaxDamageReduction()F", 0), new PropertyReference1Impl(AutoCrystal.class, "switchGroup", "getSwitchGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "gappleSwitch", "getGappleSwitch()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "autoSwitch", "getAutoSwitch()Lsu/catlean/vv;", 0), new PropertyReference1Impl(AutoCrystal.class, "antiWeakness", "getAntiWeakness()Lsu/catlean/vv;", 0), new PropertyReference1Impl(AutoCrystal.class, "idPredictGroup", "getIdPredictGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "idPredict", "getIdPredict()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "idOffset", "getIdOffset()I", 0), new PropertyReference1Impl(AutoCrystal.class, "idAttacks", "getIdAttacks()I", 0), new PropertyReference1Impl(AutoCrystal.class, "idDelay", "getIdDelay()I", 0), new PropertyReference1Impl(AutoCrystal.class, "idPredictOnlyDuels", "getIdPredictOnlyDuels()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "swingOnce", "getSwingOnce()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "onlyBreakingSurr", "getOnlyBreakingSurr()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "targeting", "getTargeting()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "targetSort", "getTargetSort()Lsu/catlean/oc;", 0), new PropertyReference1Impl(AutoCrystal.class, "ignoreCreative", "getIgnoreCreative()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "ignoreNaked", "getIgnoreNaked()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "renderGroup", "getRenderGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoCrystal.class, "fakeCrystalRemove", "getFakeCrystalRemove()Z", 0), new PropertyReference1Impl(AutoCrystal.class, "removeDelay", "getRemoveDelay()I", 0), new PropertyReference1Impl(AutoCrystal.class, "targetEsp", "getTargetEsp()Lsu/catlean/ds;", 0), new PropertyReference1Impl(AutoCrystal.class, "renderMode", "getRenderMode()Lsu/catlean/vb;", 0), new PropertyReference1Impl(AutoCrystal.class, "textPattern", "getTextPattern()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoCrystal.class, "alpha", "getAlpha()F", 0) };
   @NotNull
   public static final AutoCrystal INSTANCE = new AutoCrystal();
   @NotNull
   private static final GroupSetting main = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "main", null, null, 6, null);
   @NotNull
   private static final EnumSetting timing = SettingFactory.defaultImpl(INSTANCE, "timing", AutoCrystal.Timing.VANILLA, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting oneTickAction = SettingFactory.defaultImpl(INSTANCE, "one-tick-action", true, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final EnumSetting onCrystalSpawn = SettingFactory.defaultImpl(
      INSTANCE, "on-crystal-spawn", AutoCrystal.OnCrystalSpawn.ACTION_X, INSTANCE.getMainGroup(), null, 8, null
   );
   @NotNull
   private static final EnumSetting spawnTiming = SettingFactory.defaultImpl(
      INSTANCE, "spawn-timing", AutoCrystal.SpawnTiming.ADD_TO_WORLD, INSTANCE.getMainGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(INSTANCE, "rotate", true, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting rayTrace = SettingFactory.defaultImpl(INSTANCE, "ray-trace", false, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting crystalMultiPoint = SettingFactory.defaultImpl(
      INSTANCE, "crystal-multi-point", false, INSTANCE.getMainGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting asyncCalc = SettingFactory.defaultImpl(INSTANCE, "async-calc", false, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting failNotify = SettingFactory.defaultImpl(INSTANCE, "fail-notify", false, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final EnumSetting predict = SettingFactory.defaultImpl(INSTANCE, "predict", AutoCrystal.Predict.AUTO, INSTANCE.getMainGroup(), null, 8, null);
   @NotNull
   private static final IntSetting predictTicks = SettingFactory.computeIntSetting(
      INSTANCE, "predict-ticks", 3, new IntRange(1, 10), INSTANCE.getMainGroup(), () -> INSTANCE.getPredict() == AutoCrystal.Predict.CUSTOM
   );
   @NotNull
   private static final GroupSetting place = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "place", null, null, 6, null);
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(INSTANCE, "1.12", false, INSTANCE.getPlaceGroup(), null, 8, null);
   @NotNull
   private static final IntSetting placeDelay = SettingFactory.defaultImpl(
      INSTANCE, "place-delay", 0, new IntRange(0, 10), INSTANCE.getPlaceGroup(), null, 16, null
   );
   @NotNull
   private static final FloatSetting placeRange = SettingFactory.defaultImpl(
      INSTANCE, "place-range", 5.0F, RangesKt.rangeTo(1.0F, 6.0F), INSTANCE.getPlaceGroup(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting limitPlace = SettingFactory.defaultImpl(INSTANCE, "limit-place", true, INSTANCE.getPlaceGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting removeFire = SettingFactory.defaultImpl(INSTANCE, "remove-fire", true, INSTANCE.getPlaceGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting removeLava = SettingFactory.defaultImpl(INSTANCE, "remove-lava", true, INSTANCE.getPlaceGroup(), null, 8, null);
   @NotNull
   private static final GroupSetting breakValue = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "break", null, null, 6, null);
   @NotNull
   private static final IntSetting breakDelay = SettingFactory.defaultImpl(
      INSTANCE, "break-delay", 0, new IntRange(0, 10), INSTANCE.getBreakGroup(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting grimBreakRange = SettingFactory.defaultImpl(INSTANCE, "grim-break-range", false, INSTANCE.getBreakGroup(), null, 8, null);
   @NotNull
   private static final FloatSetting breakRange = SettingFactory.computeFloatSetting(
      INSTANCE, "break-range", 5.0F, RangesKt.rangeTo(1.0F, 6.0F), INSTANCE.getBreakGroup(), () -> !INSTANCE.getGrimBreakRange()
   );
   @NotNull
   private static final BooleanSetting limitBreak = SettingFactory.defaultImpl(INSTANCE, "limit-break", true, INSTANCE.getBreakGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting randomizeDelay = SettingFactory.defaultImpl(INSTANCE, "randomize-delay", false, INSTANCE.getBreakGroup(), null, 8, null);
   @NotNull
   private static final GroupSetting pause = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "pause", null, null, 6, null);
   @NotNull
   private static final BooleanSetting mining = SettingFactory.defaultImpl(INSTANCE, "mining", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting eating = SettingFactory.defaultImpl(INSTANCE, "eating", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting inventory = SettingFactory.defaultImpl(INSTANCE, "inventory", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final IntSetting switchPause = SettingFactory.defaultImpl(
      INSTANCE, "switch-pause", 0, new IntRange(0, 10), INSTANCE.getPauseGroup(), null, 16, null
   );
   @NotNull
   private static final IntSetting offHand = SettingFactory.defaultImpl(INSTANCE, "off-hand", 2, new IntRange(0, 5), INSTANCE.getPauseGroup(), null, 16, null);
   @NotNull
   private static final GroupSetting damages = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "damages", null, null, 6, null);
   @NotNull
   private static final BooleanSetting assumeBestArmor = SettingFactory.defaultImpl(
      INSTANCE, "assume-best-armor", false, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final FloatSetting minDamage = SettingFactory.defaultImpl(
      INSTANCE, "min-damage", 6.0F, RangesKt.rangeTo(2.0F, 20.0F), INSTANCE.getDamagesGroup(), null, 16, null
   );
   @NotNull
   private static final FloatSetting maxSelfDamage = SettingFactory.defaultImpl(
      INSTANCE, "max-self-damage", 10.0F, RangesKt.rangeTo(2.0F, 20.0F), INSTANCE.getDamagesGroup(), null, 16, null
   );
   @NotNull
   private static final FloatSetting minDamageFactor = SettingFactory.defaultImpl(
      INSTANCE, "min-damage-factor", 0.1F, RangesKt.rangeTo(0.1F, 5.0F), INSTANCE.getDamagesGroup(), null, 16, null
   );
   @NotNull
   private static final IntSetting lethalMultiplier = SettingFactory.defaultImpl(
      INSTANCE, "lethal-multiplier", 1, new IntRange(1, 3), INSTANCE.getDamagesGroup(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting overrideSelfDamage = SettingFactory.defaultImpl(
      INSTANCE, "override-self-damage", true, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting overrideMinDamage = SettingFactory.defaultImpl(
      INSTANCE, "override-min-damage", true, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final FloatSetting armorPercent = SettingFactory.computeFloatSetting(
      INSTANCE, "armor-percent", 5.0F, RangesKt.rangeTo(0.0F, 40.0F), INSTANCE.getDamagesGroup(), () -> INSTANCE.getOverrideMinDamage()
   );
   @NotNull
   private static final FloatSetting facePlaceHealth = SettingFactory.computeFloatSetting(
      INSTANCE, "face-place-health", 5.0F, RangesKt.rangeTo(0.0F, 20.0F), INSTANCE.getDamagesGroup(), () -> INSTANCE.getOverrideMinDamage()
   );
   @NotNull
   private static final BooleanSetting antiEChestFarm = SettingFactory.computeBooleanSetting(
      INSTANCE, "anti-e-chest-farm", true, INSTANCE.getDamagesGroup(), () -> INSTANCE.getOverrideMinDamage()
   );
   @NotNull
   private static final BooleanSetting antiPortal = SettingFactory.computeBooleanSetting(
      INSTANCE, "anti-portal", true, INSTANCE.getDamagesGroup(), () -> INSTANCE.getOverrideMinDamage()
   );
   @NotNull
   private static final BooleanSetting antiQuiver = SettingFactory.computeBooleanSetting(
      INSTANCE, "anti-quiver", true, INSTANCE.getDamagesGroup(), () -> INSTANCE.getOverrideMinDamage()
   );
   @NotNull
   private static final BooleanSetting destructibleTerrain = SettingFactory.defaultImpl(
      INSTANCE, "destructible-terrain", true, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final EnumSetting friendProtection = SettingFactory.defaultImpl(
      INSTANCE, "friend-protection", AutoCrystal.FriendProtection.ONLY_LETHAL, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting calcShields = SettingFactory.defaultImpl(INSTANCE, "calc-shields", false, INSTANCE.getDamagesGroup(), null, 8, null);
   @NotNull
   private static final EnumSetting damageSync = SettingFactory.defaultImpl(
      INSTANCE, "damage-sync", AutoCrystal.DamageSync.OFF, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting facePlaceSync = SettingFactory.computeBooleanSetting(
      INSTANCE, "face-place-sync", true, INSTANCE.getDamagesGroup(), () -> INSTANCE.getDamageSync() == AutoCrystal.DamageSync.CONDITIONS
   );
   @NotNull
   private static final BooleanSetting lowCrystals = SettingFactory.computeBooleanSetting(
      INSTANCE, "low-crystals", true, INSTANCE.getDamagesGroup(), () -> INSTANCE.getDamageSync() == AutoCrystal.DamageSync.CONDITIONS
   );
   @NotNull
   private static final FloatSetting stacksLeft = SettingFactory.computeFloatSetting(
      INSTANCE,
      "stacks-left",
      1.5F,
      RangesKt.rangeTo(0.5F, 5.0F),
      INSTANCE.getDamagesGroup(),
      () -> INSTANCE.getDamageSync() == AutoCrystal.DamageSync.CONDITIONS && INSTANCE.getLowCrystals()
   );
   @NotNull
   private static final BooleanSetting minimizeSelfDamage = SettingFactory.defaultImpl(
      INSTANCE, "minimize-self-damage", true, INSTANCE.getDamagesGroup(), null, 8, null
   );
   @NotNull
   private static final FloatSetting maxDamageReduction = SettingFactory.computeFloatSetting(
      INSTANCE, "max-damage-reduction", 1.5F, RangesKt.rangeTo(0.01F, 10.0F), INSTANCE.getDamagesGroup(), () -> INSTANCE.getMinimizeSelfDamage()
   );
   @NotNull
   private static final GroupSetting swap = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "swap", null, null, 6, null);
   @NotNull
   private static final BooleanSetting gappleSwitch = SettingFactory.defaultImpl(INSTANCE, "gapple-switch", true, INSTANCE.getSwitchGroup(), null, 8, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(
      INSTANCE, "switch", AutoCrystal.Switch.NORMAL, INSTANCE.getSwitchGroup(), null, 8, null
   );
   @NotNull
   private static final EnumSetting antiWeakness = SettingFactory.defaultImpl(
      INSTANCE, "anti-weakness", AutoCrystal.Switch.SILENT, INSTANCE.getSwitchGroup(), null, 8, null
   );
   @NotNull
   private static final GroupSetting idPredict = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "id-predict", null, null, 6, null);
   @NotNull
   private static final BooleanSetting predict2 = SettingFactory.defaultImpl(INSTANCE, "predict", false, INSTANCE.getIdPredictGroup(), null, 8, null);
   @NotNull
   private static final IntSetting idOffset = SettingFactory.defaultImpl(
      INSTANCE, "id-offset", 1, new IntRange(1, 10), INSTANCE.getIdPredictGroup(), null, 16, null
   );
   @NotNull
   private static final IntSetting idAttacks = SettingFactory.defaultImpl(
      INSTANCE, "id-attacks", 3, new IntRange(1, 10), INSTANCE.getIdPredictGroup(), null, 16, null
   );
   @NotNull
   private static final IntSetting idDelay = SettingFactory.defaultImpl(
      INSTANCE, "id-delay", 25, new IntRange(0, 500), INSTANCE.getIdPredictGroup(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting predictOnlyInDuels = SettingFactory.defaultImpl(
      INSTANCE, "predict-only-in-duels", false, INSTANCE.getIdPredictGroup(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting swingOnce = SettingFactory.defaultImpl(INSTANCE, "swing-once", false, INSTANCE.getIdPredictGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting onlyBreakingSurround = SettingFactory.defaultImpl(
      INSTANCE, "only-breaking-surround", false, INSTANCE.getIdPredictGroup(), null, 8, null
   );
   @NotNull
   private static final GroupSetting targeting = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "targeting", null, null, 6, null);
   @NotNull
   private static final EnumSetting targetSort = SettingFactory.defaultImpl(
      INSTANCE, "target-sort", AutoCrystal.TargetSort.DISTANCE, INSTANCE.getTargeting(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting ignoreCreative = SettingFactory.defaultImpl(INSTANCE, "ignore-creative", true, INSTANCE.getTargeting(), null, 8, null);
   @NotNull
   private static final BooleanSetting ignoreNaked = SettingFactory.defaultImpl(INSTANCE, "ignore-naked", false, INSTANCE.getTargeting(), null, 8, null);
   @NotNull
   private static final GroupSetting render = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "render", null, null, 6, null);
   @NotNull
   private static final BooleanSetting fakeCrystalRemove = SettingFactory.defaultImpl(
      INSTANCE, "fake-crystal-remove", false, INSTANCE.getRenderGroup(), null, 8, null
   );
   @NotNull
   private static final IntSetting removeDelay = SettingFactory.computeIntSetting(
      INSTANCE, "remove-delay", 50, new IntRange(0, 200), INSTANCE.getRenderGroup(), () -> INSTANCE.getFakeCrystalRemove()
   );
   @NotNull
   private static final EnumSetting highlightTarget = SettingFactory.defaultImpl(
      INSTANCE, "highlight-target", AutoCrystal.HighlightTarget.COMETS, INSTANCE.getRenderGroup(), null, 8, null
   );
   @NotNull
   private static final EnumSetting renderMode = SettingFactory.defaultImpl(
      INSTANCE, "render-mode", AutoCrystal.RenderMode.SHADER, INSTANCE.getRenderGroup(), null, 8, null
   );
   @NotNull
   private static final StringSetting textPattern = SettingFactory.defaultImpl(
      INSTANCE, "text-pattern", "damage / self", INSTANCE.getRenderGroup(), null, 8, null
   );
   @NotNull
   private static final FloatSetting alpha = SettingFactory.defaultImpl(
      INSTANCE, "alpha", 0.4F, RangesKt.rangeTo(0.0F, 1.0F), INSTANCE.getRenderGroup(), null, 16, null
   );
   @Nullable
   private static Player player;
   @NotNull
   private static AtomicReference atomicReference = new AtomicReference(null);
   @NotNull
   private static AtomicReference atomicReference2 = new AtomicReference(null);
   @NotNull
   private static AtomicBoolean atomicBoolean = new AtomicBoolean();
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil2 = new PlayerUtil();
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final PlayerUtil playerUtil3 = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil4 = new PlayerUtil();
   private static int value;
   private static int value2;
   private static int value3;
   private static int value4;
   @Nullable
   private static AutoCrystal.RenderPosition renderPosition;
   @Nullable
   private static Function0 callback;
   @Nullable
   private static Function0 callback2;
   private static volatile boolean flag;
   private static volatile boolean flag2;
   private static volatile boolean flag3;
   private static long time;
   private static long time2;
   private static volatile int value5;
   @NotNull
   private static final Map map = new LinkedHashMap();
   @NotNull
   private static final LinkedList<BlockPos> linkedList = new LinkedList();
   @NotNull
   private static AtomicReference atomicReference3 = new AtomicReference();
   private static volatile boolean flag4;
   @NotNull
   private static StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 1000L);

   private AutoCrystal() {
      super("auto-crystal", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final GroupValue getMainGroup() {
      return (GroupValue)main.computeObject(this, $$delegatedProperties[0]);
   }

   private final AutoCrystal.Timing getTiming() {
      return (AutoCrystal.Timing)timing.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getMultiAction() {
      return (Boolean)oneTickAction.computeObject(this, $$delegatedProperties[2]);
   }

   private final AutoCrystal.OnCrystalSpawn getOnCrystalSpawn() {
      return (AutoCrystal.OnCrystalSpawn)onCrystalSpawn.computeObject(this, $$delegatedProperties[3]);
   }

   private final AutoCrystal.SpawnTiming getSpawnTiming() {
      return (AutoCrystal.SpawnTiming)spawnTiming.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getRayTrace() {
      return (Boolean)rayTrace.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getCrystalMultiPoint() {
      return (Boolean)crystalMultiPoint.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getAsyncCalc() {
      return (Boolean)asyncCalc.computeObject(this, $$delegatedProperties[8]);
   }

   public final boolean getFailNotify() {
      return (Boolean)failNotify.computeObject(this, $$delegatedProperties[9]);
   }

   private final AutoCrystal.Predict getPredict() {
      return (AutoCrystal.Predict)predict.computeObject(this, $$delegatedProperties[10]);
   }

   private final int getPredictTicksValue() {
      return ((Number)predictTicks.computeObject(this, $$delegatedProperties[11])).intValue();
   }

   private final GroupValue getPlaceGroup() {
      return (GroupValue)place.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getOldVer() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[13]);
   }

   private final int getPlaceDelay() {
      return ((Number)placeDelay.computeObject(this, $$delegatedProperties[14])).intValue();
   }

   private final float getPlaceRange() {
      return ((Number)placeRange.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final boolean getLimitPlace() {
      return (Boolean)limitPlace.computeObject(this, $$delegatedProperties[16]);
   }

   private final boolean getRemoveFire() {
      return (Boolean)removeFire.computeObject(this, $$delegatedProperties[17]);
   }

   private final boolean getRemoveLava() {
      return (Boolean)removeLava.computeObject(this, $$delegatedProperties[18]);
   }

   private final GroupValue getBreakGroup() {
      return (GroupValue)breakValue.computeObject(this, $$delegatedProperties[19]);
   }

   private final int getBreakDelay() {
      return ((Number)breakDelay.computeObject(this, $$delegatedProperties[20])).intValue();
   }

   private final boolean getGrimBreakRange() {
      return (Boolean)grimBreakRange.computeObject(this, $$delegatedProperties[21]);
   }

   private final float getExplodeRange() {
      return ((Number)breakRange.computeObject(this, $$delegatedProperties[22])).floatValue();
   }

   private final boolean getLimitBreak() {
      return (Boolean)limitBreak.computeObject(this, $$delegatedProperties[23]);
   }

   public final boolean getRandomizeDelay() {
      return (Boolean)randomizeDelay.computeObject(this, $$delegatedProperties[24]);
   }

   private final GroupValue getPauseGroup() {
      return (GroupValue)pause.computeObject(this, $$delegatedProperties[25]);
   }

   private final boolean getMining() {
      return (Boolean)mining.computeObject(this, $$delegatedProperties[26]);
   }

   private final boolean getEating() {
      return (Boolean)eating.computeObject(this, $$delegatedProperties[27]);
   }

   private final boolean getInventory() {
      return (Boolean)inventory.computeObject(this, $$delegatedProperties[28]);
   }

   private final int getSwitchPause() {
      return ((Number)switchPause.computeObject(this, $$delegatedProperties[29])).intValue();
   }

   private final int getOffHandPause() {
      return ((Number)offHand.computeObject(this, $$delegatedProperties[30])).intValue();
   }

   private final GroupValue getDamagesGroup() {
      return (GroupValue)damages.computeObject(this, $$delegatedProperties[31]);
   }

   public final boolean getAssumeBestArmor() {
      return (Boolean)assumeBestArmor.computeObject(this, $$delegatedProperties[32]);
   }

   private final float getMinDamage() {
      return ((Number)minDamage.computeObject(this, $$delegatedProperties[33])).floatValue();
   }

   private final float getMaxSelfDamage() {
      return ((Number)maxSelfDamage.computeObject(this, $$delegatedProperties[34])).floatValue();
   }

   private final float getEfficiencyFactor() {
      return ((Number)minDamageFactor.computeObject(this, $$delegatedProperties[35])).floatValue();
   }

   private final int getLethalMultiplier() {
      return ((Number)lethalMultiplier.computeObject(this, $$delegatedProperties[36])).intValue();
   }

   private final boolean getOverrideSelfDamage() {
      return (Boolean)overrideSelfDamage.computeObject(this, $$delegatedProperties[37]);
   }

   private final boolean getOverrideMinDamage() {
      return (Boolean)overrideMinDamage.computeObject(this, $$delegatedProperties[38]);
   }

   private final float getArmorScale() {
      return ((Number)armorPercent.computeObject(this, $$delegatedProperties[39])).floatValue();
   }

   private final float getFacePlaceHp() {
      return ((Number)facePlaceHealth.computeObject(this, $$delegatedProperties[40])).floatValue();
   }

   private final boolean getExplodeEChestFarm() {
      return (Boolean)antiEChestFarm.computeObject(this, $$delegatedProperties[41]);
   }

   private final boolean getAntiPortal() {
      return (Boolean)antiPortal.computeObject(this, $$delegatedProperties[42]);
   }

   private final boolean getAntiQuiver() {
      return (Boolean)antiQuiver.computeObject(this, $$delegatedProperties[43]);
   }

   public final boolean getTerrainIgnore() {
      return (Boolean)destructibleTerrain.computeObject(this, $$delegatedProperties[44]);
   }

   private final AutoCrystal.FriendProtection getFriendProtection() {
      return (AutoCrystal.FriendProtection)friendProtection.computeObject(this, $$delegatedProperties[45]);
   }

   private final boolean getCalcShields() {
      return (Boolean)calcShields.computeObject(this, $$delegatedProperties[46]);
   }

   private final AutoCrystal.DamageSync getDamageSync() {
      return (AutoCrystal.DamageSync)damageSync.computeObject(this, $$delegatedProperties[47]);
   }

   private final boolean getFacePlaceSync() {
      return (Boolean)facePlaceSync.computeObject(this, $$delegatedProperties[48]);
   }

   private final boolean getLowCrystals() {
      return (Boolean)lowCrystals.computeObject(this, $$delegatedProperties[49]);
   }

   private final float getStacksLeft() {
      return ((Number)stacksLeft.computeObject(this, $$delegatedProperties[50])).floatValue();
   }

   private final boolean getMinimizeSelfDamage() {
      return (Boolean)minimizeSelfDamage.computeObject(this, $$delegatedProperties[51]);
   }

   private final float getMaxDamageReduction() {
      return ((Number)maxDamageReduction.computeObject(this, $$delegatedProperties[52])).floatValue();
   }

   private final GroupValue getSwitchGroup() {
      return (GroupValue)swap.computeObject(this, $$delegatedProperties[53]);
   }

   private final boolean getGappleSwitch() {
      return (Boolean)gappleSwitch.computeObject(this, $$delegatedProperties[54]);
   }

   private final AutoCrystal.Switch getAutoSwitch() {
      return (AutoCrystal.Switch)switchValue.computeObject(this, $$delegatedProperties[55]);
   }

   private final AutoCrystal.Switch getAntiWeakness() {
      return (AutoCrystal.Switch)antiWeakness.computeObject(this, $$delegatedProperties[56]);
   }

   private final GroupValue getIdPredictGroup() {
      return (GroupValue)idPredict.computeObject(this, $$delegatedProperties[57]);
   }

   private final boolean getIdPredict() {
      return (Boolean)predict2.computeObject(this, $$delegatedProperties[58]);
   }

   private final int getIdOffset() {
      return ((Number)idOffset.computeObject(this, $$delegatedProperties[59])).intValue();
   }

   private final int getIdAttacks() {
      return ((Number)idAttacks.computeObject(this, $$delegatedProperties[60])).intValue();
   }

   private final int getIdDelay() {
      return ((Number)idDelay.computeObject(this, $$delegatedProperties[61])).intValue();
   }

   private final boolean getIdPredictOnlyDuels() {
      return (Boolean)predictOnlyInDuels.computeObject(this, $$delegatedProperties[62]);
   }

   private final boolean getSwingOnce() {
      return (Boolean)swingOnce.computeObject(this, $$delegatedProperties[63]);
   }

   private final boolean getOnlyBreakingSurr() {
      return (Boolean)onlyBreakingSurround.computeObject(this, $$delegatedProperties[64]);
   }

   private final GroupValue getTargeting() {
      return (GroupValue)targeting.computeObject(this, $$delegatedProperties[65]);
   }

   private final AutoCrystal.TargetSort getTargetSort() {
      return (AutoCrystal.TargetSort)targetSort.computeObject(this, $$delegatedProperties[66]);
   }

   private final boolean getIgnoreCreative() {
      return (Boolean)ignoreCreative.computeObject(this, $$delegatedProperties[67]);
   }

   private final boolean getIgnoreNaked() {
      return (Boolean)ignoreNaked.computeObject(this, $$delegatedProperties[68]);
   }

   private final GroupValue getRenderGroup() {
      return (GroupValue)render.computeObject(this, $$delegatedProperties[69]);
   }

   private final boolean getFakeCrystalRemove() {
      return (Boolean)fakeCrystalRemove.computeObject(this, $$delegatedProperties[70]);
   }

   private final int getRemoveDelay() {
      return ((Number)removeDelay.computeObject(this, $$delegatedProperties[71])).intValue();
   }

   private final AutoCrystal.HighlightTarget getTargetEsp() {
      return (AutoCrystal.HighlightTarget)highlightTarget.computeObject(this, $$delegatedProperties[72]);
   }

   private final AutoCrystal.RenderMode getRenderMode() {
      return (AutoCrystal.RenderMode)renderMode.computeObject(this, $$delegatedProperties[73]);
   }

   private final String getTextPattern() {
      return (String)textPattern.computeObject(this, $$delegatedProperties[74]);
   }

   private final float getAlpha() {
      return ((Number)alpha.computeObject(this, $$delegatedProperties[75])).floatValue();
   }

   @Nullable
   public final Player getPlayer() {
      return player;
   }

   public final void setPlayer(Player player) {
      AutoCrystal.player = player;
   }

   @NotNull
   public final AtomicReference getAtomicReference() {
      return atomicReference;
   }

   @NotNull
   public final AtomicBoolean getAtomicBoolean() {
      return atomicBoolean;
   }

   public final int getValue4() {
      return value4;
   }

   public final void setValue4(int value) {
      value4 = value;
   }

   public final long getTime() {
      return time;
   }

   public final void setTime(long time) {
      AutoCrystal.time = time;
   }

   public final long getTime2() {
      return time2;
   }

   public final void setTime2(long time) {
      time2 = time;
   }

   @NotNull
   public final Map getMap() {
      return map;
   }

   @NotNull
   public final LinkedList<BlockPos> getLinkedList() {
      return linkedList;
   }

   @NotNull
   public final AtomicReference getAtomicReference3() {
      return atomicReference3;
   }

   public final boolean isFlag4() {
      return flag4;
   }

   public final void setFlag4(boolean flag) {
      flag4 = flag;
   }

   @NotNull
   public final StopWatch getStopWatch2() {
      return stopWatch2;
   }

   @Override
   public void update2() {
      this.applySet();
   }

   @Override
   public void update4() {
      this.applySet();
   }

   private final void applySet() {
      CrystalService.INSTANCE.applyClear();
      linkedList.clear();
      playerUtil2.update();
      playerUtil.update();
      playerUtil3.updateValue(20);
      flag = false;
      atomicReference2.set(null);
      atomicReference.set(null);
      renderPosition = null;
      player = null;
      time = 0L;
      value5 = 0;
      value4 = 0;
      callback = null;
      callback2 = null;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getRenderMode() == AutoCrystal.RenderMode.TECHNO) {
         AutoCrystal.PlaceRenderer.INSTANCE.applyRemoveIf();
      }

      float var59 = Math.max(this.getPlaceRange(), this.getExplodeRange()) + 12.0F;
      Map var60 = new HashMap(map);
      int var61 = 0;

      for (Entry var63 : (Iterable<java.util.Map.Entry>)var60.entrySet()) {
         BlockPos var66 = (BlockPos)var63.getKey();
         Long var67 = (Long)var63.getValue();
         if ((float)(MinecraftHolder.getTime() - var67) > Math.max(100, ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null)) * 2.0F) {
            map.remove(var66);
         }
      }

      if (player != null && player.isDeadOrDying()) {
         SoundManager.INSTANCE.updateFlag(true);
         player = null;
      } else {
         player = PlayerTargetUtil.INSTANCE.computePlayer(var59, this.getTargetSort(), this.getIgnoreNaked(), this.getIgnoreCreative());
         if (player == null) {
            atomicReference.set(null);
            ServerConfigProfile.INSTANCE.update();
         } else {
            ConfigState var72 = this.getConfigState();
            if (this.getAsyncCalc()) {
               new Thread(() -> INSTANCE.applySet(var72)).start();
            } else {
               this.applySet(var72);
            }

            flag4 = false;
            if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.END_CRYSTAL)
               && this.getGappleSwitch()
               && MinecraftHolder.getMinecraft().options.keyUse.isDown()
               && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.END_CRYSTAL)) {
               InventoryManager var10000 = InventoryManager.INSTANCE;
               Item[] var73 = new Item[]{Items.ENCHANTED_GOLDEN_APPLE};
               var10000.computeSearchInvResult(var73).update();
            }

            var61 = value3++;
            if (var61 >= 20) {
               Object var54 = null;
               byte var55 = 2;
               boolean var56 = false;
               Item var57 = Items.END_CRYSTAL;
               value2 = Mth.clamp(value - InventoryManager.defaultImpl(InventoryManager.INSTANCE, var57, var56, var55, var54), 0, 255);
               var54 = null;
               var55 = 2;
               var56 = false;
               var57 = Items.END_CRYSTAL;
               value = InventoryManager.defaultImpl(InventoryManager.INSTANCE, var57, var56, var55, var54);
               value3 = 0;
            }

            this.applyGetId();
            if (this.getRotate()) {
               Vec3 var75 = (Vec3)atomicReference3.get();
               if (var75 != null) {
                  Rotation var76 = Rotation.vectorUtil.computeRotation(var75);
                  RotationManager.INSTANCE.updateRotation(var76, 140);
                  if (stopWatch2.testValue(500)) {
                     atomicReference3.lazySet(null);
                     renderPosition = null;
                  }
               }
            }
         }
      }
   }

   @Flow
   private final void onPostSync(PostSyncEvent postSyncEvent) {
      if (callback2 != null) {
         callback2.invoke();
      }

      callback2 = null;
      if (callback != null) {
         callback.invoke();
      }

      callback = null;
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      CrystalService.INSTANCE.applyEntrySet();
      if (player != null) {
         switch (AutoCrystal.WhenMappings.values[this.getTargetEsp().ordinal()]) {
            case 1:
               EntityPreviewRenderer var33 = EntityPreviewRenderer.INSTANCE;
               PoseStack var34 = render3DEvent.getStack();
               Entity var10002 = (Entity)player;
               float var32 = 1.0F - RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().distanceTo((Entity)player) / 12.0F, 0.0F, 1.0F);
               Entity var29 = var10002;
               var33.render(var34, var29, var32);
               break;
            case 2:
               EntityPreviewRenderer var10000 = EntityPreviewRenderer.INSTANCE;
               PoseStack var10001 = render3DEvent.getStack();
               Entity var28 = (Entity)player;
               var10000.render(var10001, var28);
         }
      }

      AutoCrystal.RenderPosition var31 = renderPosition;
      if (this.getRenderMode() != AutoCrystal.RenderMode.OFF && var31 != null) {
         animation.applyDoubleValue(0.0F);
         this.render(
            render3DEvent.getStack(),
            KotlinCompat.replace(
               KotlinCompat.replace(this.getTextPattern(), "damage", String.valueOf(NameTags.INSTANCE.computeAmount(var31.getAmount())), false, 4, null),
               "self",
               String.valueOf(NameTags.INSTANCE.computeAmount(var31.getAmount2())),
               false,
               4,
               null
            ),
            new AABB(var31.getPos())
         );
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (player != null) {
         if (AutoCrystal.WhenMappings.values[this.getTargetEsp().ordinal()] == 2) {
            EntityPreviewRenderer.INSTANCE.applySetModifyBuffer();
         }
      }
   }

   @Flow
   private final void onAfterEntitySpawn(AfterEntitySpawn afterEntitySpawn) {
      if (afterEntitySpawn.getEntity() instanceof EndCrystal && INSTANCE.getSpawnTiming() == AutoCrystal.SpawnTiming.ADD_TO_WORLD) {
         INSTANCE.updateValue(afterEntitySpawn.getEntity().getId(), INSTANCE.computeVec3(afterEntitySpawn.getEntity()));
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      try {
         Packet<? extends PacketListener> var20 = receivePacket.getPacket();
         if (var20 instanceof ClientboundAddEntityPacket) {
            AutoCrystal.AttackAction.INSTANCE.computeUnit(((ClientboundAddEntityPacket)var20).getId());
            if (Intrinsics.areEqual(((ClientboundAddEntityPacket)var20).getType(), EntityTypes.END_CRYSTAL)
               && player != null
               && INSTANCE.getSpawnTiming() == AutoCrystal.SpawnTiming.PACKET) {
               if (INSTANCE.getAsyncCalc()) {
                  new Thread(
                        () -> INSTANCE.updateValue(
                           ((ClientboundAddEntityPacket)var20).getId(),
                           new Vec3(
                              ((ClientboundAddEntityPacket)var20).getX(),
                              ((ClientboundAddEntityPacket)var20).getY(),
                              ((ClientboundAddEntityPacket)var20).getZ()
                           )
                        )
                     )
                     .start();
               } else {
                  INSTANCE.updateValue(
                     ((ClientboundAddEntityPacket)var20).getId(),
                     new Vec3(
                        ((ClientboundAddEntityPacket)var20).getX(), ((ClientboundAddEntityPacket)var20).getY(), ((ClientboundAddEntityPacket)var20).getZ()
                     )
                  );
               }
            }
         }

         if (var20 instanceof ClientboundExplodePacket) {
            for (Entity var23 : Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering())) {
               if (var23 instanceof EndCrystal
                  && var23.distanceToSqr(
                        ((ClientboundExplodePacket)var20).center().x,
                        ((ClientboundExplodePacket)var20).center().y,
                        ((ClientboundExplodePacket)var20).center().z
                     )
                     <= 144.0
                  && !CrystalService.INSTANCE.checkContainsKey(((EndCrystal)var23).getId())) {
                  CrystalService.INSTANCE.computeLongValue(((EndCrystal)var23).getId(), MinecraftHolder.getTime());
               }
            }
         }

         if (var20 instanceof ClientboundEntityEventPacket
            && ((ClientboundEntityEventPacket)var20).getEventId() == 3
            && player != null
            && ((ClientboundEntityEventPacketAccessor)var20).getEntityId() == player.getId()) {
            stopWatch.update();
         }

         if (var20 instanceof ClientboundSoundPacket
            && (
               Intrinsics.areEqual(((ClientboundSoundPacket)var20).getSound().value(), SoundEvents.ANVIL_PLACE)
                  || Intrinsics.areEqual(((ClientboundSoundPacket)var20).getSound().value(), SoundEvents.ANVIL_LAND)
                  || Intrinsics.areEqual(((ClientboundSoundPacket)var20).getSound().value(), SoundEvents.ANVIL_FALL)
            )) {
            ServerConfigProfile.INSTANCE.getStopWatch3();
         }
      } catch (Exception var24) {
         ChatUtil.defaultImpl(this, "AUTO CRYSTAL PACKET HANDLE ERROR", false, 2, null);
         var24.printStackTrace();
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundSetCarriedItemPacket) {
         if (value5 > 0) {
            int var7 = value5;
            value5 = var7 + -1;
            return;
         }

         playerUtil4.update();
      }
   }

   private final void updateValue(int value, Vec3 vec3) {
      Attempt var10000 = CrystalService.INSTANCE.computeAttempt(vec3);
      if (var10000 != null) {
         Attempt var36 = var10000;
         time = MinecraftHolder.getTime() - var36.getTime();
         GradientQuad.INSTANCE.applyLongValue();
         Player var37 = player;
         if (var37 != null
            && playerUtil2.testValue(INSTANCE.getBreakDelay())
            && INSTANCE.getOnCrystalSpawn() != AutoCrystal.OnCrystalSpawn.NO_REACTION
            && !INSTANCE.isFlag()
            && INSTANCE.getMultiAction()
            && !INSTANCE.getRandomizeDelay()) {
            ConfigState var39 = INSTANCE.computeConfigState(var37);
            if (INSTANCE.getRotate()) {
               INSTANCE.applySet(vec3);
            }

            if (CrystalInteractPoint.INSTANCE.fetchY(INSTANCE, vec3, var39, false, 0) != null) {
               INSTANCE.applyGetEyePosition(value, vec3, true);
            }

            if (INSTANCE.getOnCrystalSpawn() == AutoCrystal.OnCrystalSpawn.ACTION_X && playerUtil.testValue(INSTANCE.getPlaceDelay())) {
               DamageData var41 = (DamageData)atomicReference.get();
               if (var41 != null) {
                  DamageData var40 = var41;
                  INSTANCE.applyGetBlockPos((BlockHitResult)var40.getObject(), true);
               }
            }
         }
      }
   }

   @Flow
   private final void onRenderEntity(RenderEntityEvent renderEntityEvent) {
      if (this.getFakeCrystalRemove() && player != null && renderEntityEvent.getState() != null) {
         int var11 = this.getRemoveDelay();
         long var12 = MinecraftHolder.getTime() - CrystalService.INSTANCE.fetchOrDefault(renderEntityEvent.getState().getId());
         if (var11 <= var12 ? var12 < 201L : false) {
            renderEntityEvent.cancel();
         }
      }
   }

   private final void applyGetId() {
      DamageData var23 = (DamageData)atomicReference.get();
      CrystalWrapper var24 = (CrystalWrapper)atomicReference2.get();
      if (!this.isFlag() && this.getMultiAction()) {
         if (var24 != null) {
            if (playerUtil2.testValue(INSTANCE.getBreakDelay())) {
               INSTANCE.applyGetEyePosition(var24.getCrystal().getId(), INSTANCE.computeVec3((Entity)var24.getCrystal()), false);
            }
         }

         if (var23 != null) {
            if (playerUtil.testValue(INSTANCE.getPlaceDelay())) {
               INSTANCE.applyGetBlockPos((BlockHitResult)var23.getObject(), false);
            }
         }
      } else if (var24 != null && playerUtil2.testValue(this.getBreakDelay())) {
         this.applyGetEyePosition(var24.getCrystal().getId(), this.computeVec3((Entity)var24.getCrystal()), false);
      } else if (var23 != null && playerUtil.testValue(this.getPlaceDelay())) {
         this.applyGetBlockPos((BlockHitResult)var23.getObject(), false);
      }
   }

   private final void applySet(ConfigState configState) {
      atomicReference.set(TrapPlanner.computeDamageData(this, configState));
      atomicReference2.set(CrystalInteractPoint.INSTANCE.computeCrystalWrapper(this, configState));
   }

   private final boolean isFlag() {
      DamageData var14 = (DamageData)atomicReference.get();
      if (this.getRayTrace() && var14 != null) {
         Vec3 var15 = InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer());
         Vec3 var16 = ((BlockHitResult)var14.getObject()).getLocation();
         BlockPos var12 = ((BlockHitResult)var14.getObject()).getBlockPos();
         Vec3 var17 = Vec3iUtil.computeVec3(var12).add(0.0, 0.5, 0.0);
         AABB var18 = new AABB(var17.x - 1.0F, var17.y, var17.z - 1.0F, var17.x + 1.0F, var17.y + 2.0F, var17.z + 1.0F);
         if (var18.clip(var15, var16).isEmpty() && !var18.contains(var15) && !var18.contains(var16)) {
            return true;
         } else {
            BlockHitResult var19 = CrystalPredictor.defaultImpl(CrystalPredictor.INSTANCE, var15, var16, false, null, null, 28, null);
            return var19 != null
               && var19.getType() != Type.MISS
               && (
                  !Intrinsics.areEqual(var19.getBlockPos(), ((BlockHitResult)var14.getObject()).getBlockPos())
                     || var19.getDirection() != ((BlockHitResult)var14.getObject()).getDirection()
               );
         }
      } else {
         return false;
      }
   }

   private final void render(PoseStack poseStack, String text, AABB aABB) {
      Color var22 = ThemeManager.INSTANCE.getColor2();
      if (this.getRenderMode() == AutoCrystal.RenderMode.SHADER) {
         Render3D.defaultImpl(Render3D.INSTANCE, aABB, Render2D.INSTANCE.computeColor(var22, this.getAlpha() * animation.getAmount()), null, 4, null);
      } else if (this.getRenderMode() == AutoCrystal.RenderMode.DEAFULT) {
         Render3D.defaultImpl2(Render3D.INSTANCE, aABB, Render2D.INSTANCE.computeColor(var22, this.getAlpha() * animation.getAmount()), null, 4, null);
         Render3D.defaultImpl3(Render3D.INSTANCE, aABB, Render2D.INSTANCE.computeColor(var22, animation.getAmount()), null, 4, null);
      } else {
         AutoCrystal.PlaceRenderer.INSTANCE.render(poseStack, aABB);
      }

      Render3D.defaultImpl(
         Render3D.INSTANCE,
         text,
         aABB.getCenter(),
         Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), animation.getAmount()),
         0.0F,
         0.0F,
         false,
         null,
         120,
         null
      );
   }

   private final boolean checkGetItem(boolean flag) {
      boolean var41 = MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof EndCrystalItem;
      boolean var42 = MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof EndCrystalItem;
      boolean var43 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.ENCHANTED_GOLDEN_APPLE)
         || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.GOLDEN_APPLE);
      if (!stopWatch.testValue(500)) {
         return true;
      } else if (AutoMend.INSTANCE.isFlag()) {
         return true;
      } else if (Blink.INSTANCE.getEnabled()) {
         return true;
      } else if (SpeedMine.INSTANCE.getAtomicBoolean().get()) {
         return true;
      } else if (MinecraftHolder.getMultiPlayerGameMode().isDestroying() && !var41 && this.getMining()) {
         return true;
      } else if (!flag && this.getSwitchPause() > 0 && !playerUtil4.testValue(this.getSwitchPause())) {
         return true;
      } else if (this.getOffHandPause() > 0 && 5 - Offhand.INSTANCE.getValue3() < this.getOffHandPause()) {
         return true;
      } else {
         if (flag) {
            if (this.getAutoSwitch() == AutoCrystal.Switch.NONE && !var41 && !var42) {
               return true;
            }

            if (this.getAutoSwitch() == AutoCrystal.Switch.SILENT || this.getAutoSwitch() == AutoCrystal.Switch.NORMAL) {
               InventoryManager var10000 = InventoryManager.INSTANCE;
               Item[] var44 = new Item[]{Items.END_CRYSTAL};
               if (!var10000.computeSearchInvResult(var44).isFlag() && !var41) {
                  return true;
               }
            }

            if (this.getAutoSwitch() == AutoCrystal.Switch.INVENTORY) {
               InventoryManager var46 = InventoryManager.INSTANCE;
               Item[] var45 = new Item[]{Items.END_CRYSTAL};
               if (!var46.computeSearchInvResult2(var45).isFlag() && !var41) {
                  return true;
               }
            }

            if (SpeedMine.defaultImpl(SpeedMine.INSTANCE, false, 1, null).isEmpty()) {
               atomicBoolean.set(false);
            }

            if (atomicBoolean.get()) {
               return true;
            }
         }

         if (MinecraftHolder.getLocalPlayer().isUsingItem() && this.getEating()) {
            return true;
         } else {
            return this.getInventory() && WorldUtil.INSTANCE.isFlag2()
               ? true
               : !var41
                  && this.getGappleSwitch()
                  && MinecraftHolder.getMinecraft().options.keyUse.isDown()
                  && var43
                  && this.getAutoSwitch() != AutoCrystal.Switch.SILENT
                  && this.getAutoSwitch() != AutoCrystal.Switch.INVENTORY;
         }
      }
   }

   private final void applyGetEyePosition(int value, Vec3 vec3, boolean flag) {
      if ((!CrystalService.INSTANCE.checkContainsKey(value) || !this.getLimitBreak()) && !this.checkGetItem(false) && player != null) {
         if (this.getRotate() && !flag) {
            if (this.getTiming() == AutoCrystal.Timing.VANILLA) {
               flag3 = !flag3;
            }

            AABB var18 = new AABB(vec3.subtract(1.0, 0.0, 1.0), vec3.add(1.0, 2.0, 1.0));
            Vec3 var10001;
            if (this.getCrystalMultiPoint() && !var18.contains(MinecraftHolder.getLocalPlayer().getEyePosition())) {
               double var10004 = vec3.x - 1.0;
               double var10003 = RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().x, var10004, vec3.x + 1.0);
               var10004 = RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().y, vec3.y, vec3.y + 2.0);
               double var10006 = vec3.z - 1.0;
               var10001 = new Vec3(var10003, var10004, RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().z, var10006, vec3.z + 1.0));
            } else {
               var10001 = vec3.add(0.0, 0.5, 0.0);
            }

            this.applySet(var10001);
            flag4 = true;
         }

         callback2 = () -> {
            AutoCrystal var37 = INSTANCE;
            MobEffectInstance var39 = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.WEAKNESS);
            MobEffectInstance var40 = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.STRENGTH);
            int var41 = -1;
            SearchInvResult var42 = InventoryManager.INSTANCE.getSearchInvResult2();
            SearchInvResult var43 = InventoryManager.INSTANCE.fetchInventory2(AutoCrystal.ToolPredicate.INSTANCE);
            if (var37.getAntiWeakness() != AutoCrystal.Switch.NONE && var39 != null && (var40 == null || var40.getAmplifier() < var39.getAmplifier())) {
               var41 = var37.fetchInventory(var42, var43, var37.getAntiWeakness());
            }

            ServerboundAttackPacket var45 = new ServerboundAttackPacket(value);
            PacketUtil.applySend((Packet)var45);
            CrystalService.INSTANCE.applyIntValue(TuplesKt.to(value, vec3));
            Iterable<Entity> var46 = Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering());
            var var49 = new ArrayList();

            for (Object var52 : var46) {
               Entity var53 = (Entity)var52;
               if (var53 instanceof ItemEntity && vec3.distanceToSqr(INSTANCE.computeVec3(var53)) < 2.25) {
                  var49.add(var52);
               }
            }

            for (Entity var56 : (Iterable<Entity>)var49) {
               CrystalService.INSTANCE.computeLongValue(var56.getId(), MinecraftHolder.getTime());
            }

            INSTANCE.applySwing(false, true);
            playerUtil2.update();
            int var57 = value4++;
            if (var41 != -1) {
               switch (AutoCrystal.WhenMappings.values2[var37.getAntiWeakness().ordinal()]) {
                  case 1:
                     int var10001x = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                     boolean var32 = true;
                     ContainerInput var33 = ContainerInput.SWAP;
                     int var34 = var10001x;
                     ContainerUtil.applyHandleContainerInput(var41, var34, var33, var32);
                     break;
                  case 2:
                  case 3:
                     if (var37.getAntiWeakness() == AutoCrystal.Switch.SILENT) {
                        value5++;
                     }

                     InventoryManager.INSTANCE.applyGetInventory2(var41);
               }
            }

            return Unit.INSTANCE;
         };
         if (this.getTiming() == AutoCrystal.Timing.VANILLA || flag) {
            callback2.invoke();
            callback2 = null;
         }
      }
   }

   public final void applyGetBlockPos(BlockHitResult bhr, boolean flag) {
      if (!this.checkGetItem(true)) {
         if (!CrystalService.INSTANCE.checkGetCenter(bhr.getBlockPos(), this.getLimitPlace())) {
            DamageData var34 = (DamageData)atomicReference.get();
            if (ServerConfigProfile.INSTANCE
               .checkGetServerData(
                  var34 != null ? var34.getDamageData() : null,
                  this.getRandomizeDelay() && value4 % 5 == 0,
                  this.getDamageSync(),
                  this.getFacePlaceSync(),
                  this.getLowCrystals(),
                  this.getStacksLeft(),
                  this.getMinDamage()
               )) {
               if (!TrapPlanner.checkAbove(this, bhr.getBlockPos(), false, this.getConfigState())) {
                  if (this.getRotate() && !flag4) {
                     if (this.getTiming() == AutoCrystal.Timing.SEQUENTIAL) {
                        flag3 = !flag3;
                     }

                     this.applySet(bhr.getLocation());
                  }

                  callback = () -> {
                     Block var87 = MinecraftHolder.getClientLevel().getBlockState(bhr.getBlockPos().above()).getBlock();
                     int var88 = -1;
                     boolean var89 = MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof EndCrystalItem;
                     boolean var90 = MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof EndCrystalItem || var89;
                     InventoryManager var10000 = InventoryManager.INSTANCE;
                     Item[] var92 = new Item[]{Items.END_CRYSTAL};
                     int var91 = var10000.computeSearchInvResult(var92).getValue();
                     boolean var108 = INSTANCE.getAutoSwitch() == AutoCrystal.Switch.INVENTORY;
                     boolean var93 = false;
                     boolean var94 = false;
                     if (INSTANCE.getRemoveFire() && Intrinsics.areEqual(var87, Blocks.FIRE)) {
                        Packet var82 = (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, bhr.getBlockPos().above(), Direction.UP));
                        PacketUtil.applySend(var82);
                        var82 = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                        PacketUtil.applySend(var82);
                     }

                     if (INSTANCE.getRemoveLava() && INSTANCE.getRemoveFire() && Intrinsics.areEqual(var87, Blocks.TRIPWIRE)) {
                        Packet var101 = (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, bhr.getBlockPos().above(), Direction.UP));
                        PacketUtil.applySend(var101);
                        MinecraftHolder.getMultiPlayerGameMode().destroyBlock(bhr.getBlockPos().above());
                        var101 = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                        PacketUtil.applySend(var101);
                     }

                     if (INSTANCE.getRemoveLava() && Intrinsics.areEqual(var87, Blocks.LAVA)) {
                        INSTANCE.applyGetBlockPos(bhr);
                     }

                     if (!var90
                        && !SpeedMine.defaultImpl(SpeedMine.INSTANCE, false, 1, null).isEmpty()
                        && SpeedMine.INSTANCE.getResetOnSwitch()
                        && SpeedMine.INSTANCE.getSwitchMode() == SpeedMine.SwitchMode.Alternative) {
                        var94 = true;
                        var108 = true;
                     }

                     if (var108 && !MinecraftHolder.getLocalPlayer().containerMenu.getCarried().isEmpty()) {
                        return Unit.INSTANCE;
                     } else {
                        if (var108 && var91 == -1) {
                           var10000 = InventoryManager.INSTANCE;
                           Item[] var95 = new Item[]{Items.END_CRYSTAL};
                           var91 = var10000.computeSearchInvResult2(var95).getValue();
                        }

                        if (INSTANCE.getAutoSwitch() != AutoCrystal.Switch.NONE && !var90 && var91 != -1) {
                           var88 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                           if (var108) {
                              int var112 = var91 < 9 ? var91 + 36 : var91;
                              int var10001 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                              boolean var103 = true;
                              ContainerInput var83 = ContainerInput.SWAP;
                              int var84 = var10001;
                              int var85 = var112;
                              ContainerUtil.applyHandleContainerInput(var85, var84, var83, var103);
                              var93 = true;
                              atomicBoolean.set(var94);
                           } else {
                              if (INSTANCE.getAutoSwitch() == AutoCrystal.Switch.SILENT) {
                                 int var109 = value5++;
                              }

                              InventoryManager.INSTANCE.applyGetInventory2(var91);
                           }
                        }

                        if ((var91 == -1 || INSTANCE.getAutoSwitch() == AutoCrystal.Switch.NONE) && !var90) {
                           return Unit.INSTANCE;
                        } else {
                           if (MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof EndCrystalItem
                              || var89
                              || INSTANCE.getAutoSwitch() == AutoCrystal.Switch.SILENT) {
                              float var117;
                              BlockPos var10002;
                              Direction var10003;
                              label157: {
                                 CrystalService var113 = CrystalService.INSTANCE;
                                 BlockPos var104 = bhr.getBlockPos();
                                 var113.applyGetX(var104);
                                 PacketUtil.applyCurrentSequence(
                                    var2x -> (Packet)(new ServerboundUseItemOnPacket(var89 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, bhr, var2x))
                                 );
                                 var10002 = bhr.getBlockPos();
                                 var10003 = bhr.getDirection();
                                 if (var34 != null) {
                                    DamageComparatorResult var10004 = var34.getDamageData();
                                    if (var10004 != null) {
                                       var117 = var10004.getAmount();
                                       break label157;
                                    }
                                 }

                                 var117 = 1.0F;
                              }

                              float var118;
                              label152: {
                                 if (var34 != null) {
                                    DamageComparatorResult var10005 = var34.getDamageData();
                                    if (var10005 != null) {
                                       var118 = var10005.getAmount2();
                                       break label152;
                                    }
                                 }

                                 var118 = 1.0F;
                              }

                              float var105 = var118;
                              float var106 = var117;
                              Direction var107 = var10003;
                              renderPosition = new AutoCrystal.RenderPosition(var10002, var107, var106, var105);
                              INSTANCE.applySwing(var89, false);
                              BreakingBlocksService.INSTANCE.applyPut(bhr.getBlockPos());

                              for (Object var98 : (Iterable)AutoCrystal.EntriesMappings.enumEntries) {
                                 Direction var99 = (Direction)var98;
                                 BreakingBlocksService.INSTANCE.applyPut(bhr.getBlockPos().relative(var99));
                              }

                              if (INSTANCE.getRenderMode() == AutoCrystal.RenderMode.TECHNO) {
                                 AutoCrystal.PlaceRenderer.INSTANCE.update();
                              }

                              if (INSTANCE.getIdPredict()
                                 && (!INSTANCE.getIdPredictOnlyDuels() || MinecraftHolder.getClientLevel().getWorldBorder().getSize() == 100.0)
                                 && (!INSTANCE.getOnlyBreakingSurr() || !SpeedMine.INSTANCE.computeItems(false).isEmpty())) {
                                 AutoCrystal.AttackAction.INSTANCE
                                    .applySend(INSTANCE.getIdOffset(), INSTANCE.getIdAttacks(), INSTANCE.getIdDelay(), INSTANCE.getSwingOnce());
                                 if (!flag2) {
                                    flag2 = true;
                                    INSTANCE.applyGetBlockPos(bhr, true);
                                 }

                                 flag2 = false;
                              }

                              float var116;
                              label128: {
                                 playerUtil.update();
                                 if (var34 != null) {
                                    DamageComparatorResult var115 = var34.getDamageData();
                                    if (var115 != null) {
                                       var116 = var115.getAmount();
                                       break label128;
                                    }
                                 }

                                 var116 = 0.0F;
                              }

                              ServerConfigProfile.INSTANCE.updateAmount(var116);
                              linkedList.add(bhr.getBlockPos());

                              while (linkedList.size() > 10) {
                                 linkedList.poll();
                              }

                              animation.updateAmount(1.0F);
                              INSTANCE.applyGetInventory(var88, var91, var93, var94);
                           }

                           return Unit.INSTANCE;
                        }
                     }
                  };
                  if (this.getTiming() == AutoCrystal.Timing.VANILLA || flag) {
                     if (callback != null) {
                        callback.invoke();
                     }

                     callback = null;
                  }
               }
            }
         }
      }
   }

   private final int fetchInventory(SearchInvResult searchInvResult, SearchInvResult searchInvResult2, AutoCrystal.Switch switchValue) {
      int var11 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
      switch (AutoCrystal.WhenMappings.values2[switchValue.ordinal()]) {
         case 1:
            if (searchInvResult2.isFlag()) {
               ContainerUtil.applyHandleContainerInput(
                  searchInvResult2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, true
               );
               return searchInvResult2.getValue();
            }
            break;
         case 2:
         case 3:
            if (switchValue == AutoCrystal.Switch.SILENT) {
               int var12 = value5++;
            }

            searchInvResult.update();
      }

      return var11;
   }

   private final void applyGetInventory(int value, int value2, boolean flag, boolean flag2) {
      if ((this.getAutoSwitch() == AutoCrystal.Switch.SILENT || flag) && value != -1) {
         if (flag) {
            int var10000 = value2 < 9 ? value2 + 36 : value2;
            int var10001 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            boolean var16 = true;
            ContainerInput var17 = ContainerInput.SWAP;
            int var18 = var10001;
            int var19 = var10000;
            ContainerUtil.applyHandleContainerInput(var19, var18, var17, var16);
         } else if (this.getAutoSwitch() == AutoCrystal.Switch.SILENT) {
            int var21 = value5++;
            InventoryManager.INSTANCE.applyGetInventory2(value);
         }
      }
   }

   private final void applyGetBlockPos(BlockHitResult blockHitResult) {
      InventoryManager var10000 = InventoryManager.INSTANCE;
      Item[] var21 = new Item[]{Items.STRING};
      SearchInvResult var20 = var10000.computeSearchInvResult(var21);
      var10000 = InventoryManager.INSTANCE;
      Item[] var22 = new Item[]{Items.REDSTONE};
      SearchInvResult var26 = var10000.computeSearchInvResult(var22);
      var10000 = InventoryManager.INSTANCE;
      Item[] var23 = new Item[]{Items.TORCH};
      SearchInvResult var27 = var10000.computeSearchInvResult(var23);
      SearchInvResult var28 = var20.isFlag() ? var20 : (var26.isFlag() ? var26 : var27);
      if (var28.isFlag()) {
         InventoryManager.INSTANCE.updateValue(var28.getValue());
         MultiPlayerGameMode var31 = MinecraftHolder.getMultiPlayerGameMode();
         LocalPlayer var10001 = MinecraftHolder.getLocalPlayer();
         InteractionHand var10002 = InteractionHand.MAIN_HAND;
         BlockPos var18 = blockHitResult.getBlockPos();
         var31.useItemOn(
            var10001, var10002, new BlockHitResult(Vec3iUtil.computeVec3(var18).add(0.0, 0.5, 0.0), Direction.UP, blockHitResult.getBlockPos(), false)
         );
         Packet var24 = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
         PacketUtil.applySend(var24);
         Packet var25 = (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockHitResult.getBlockPos().above(), Direction.UP));
         PacketUtil.applySend(var25);
         MinecraftHolder.getMultiPlayerGameMode().destroyBlock(blockHitResult.getBlockPos().above());
         InventoryManager.INSTANCE.update();
      }
   }

   public final boolean checkAbove(BlockPos base, ArrayList entities) {
      AABB var5 = new AABB(base.above());
      return entities.stream().anyMatch(var1x -> var1x != null && ((Entity)var1x).getBoundingBox().intersects(var5) && var1x instanceof EndCrystal);
   }

   public final void applySwing(boolean flag, boolean flag2) {
      if (!flag2) {
         MinecraftHolder.getLocalPlayer().swing(flag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
      } else {
         Packet var10 = (Packet)(new ServerboundSwingPacket(flag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND));
         PacketUtil.applySend(var10);
      }
   }

   public final int getValue() {
      return switch (AutoCrystal.WhenMappings.values3[this.getPredict().ordinal()]) {
         case 1 -> Math.min((int)Math.floor(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 1.5 / 50.0), 10);
         case 2 -> this.getPredictTicksValue();
         case 3 -> 0;
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   public final int computeValue(boolean flag) {
      return switch (AutoCrystal.WhenMappings.values3[this.getPredict().ordinal()]) {
         case 1 -> flag ? Math.min((int)Math.floor(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) / 50.0), 10) : 0;
         case 2 -> flag ? this.getPredictTicksValue() : 0;
         case 3 -> 0;
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   public final boolean isFlag2() {
      Player var4 = player;
      if (var4 == null) {
         return false;
      } else {
         for (Entity var6 : Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering())) {
            if (var6 instanceof ItemEntity
               && Intrinsics.areEqual(((ItemEntity)var6).getItem().getItem(), Items.OBSIDIAN)
               && ((ItemEntity)var6).getItem().getCount() >= 8
               && ((ItemEntity)var6).isAlive()) {
               AABB var7 = var4.getBoundingBox();
               if (((ItemEntity)var6).getBoundingBox().intersects(var7.setMinY(var7.getCenter().y()).inflate(0.5, 0.0, 0.5))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public final void applySet(Vec3 v) {
      stopWatch2.update();
      atomicReference3.set(v);
   }

   private final ConfigState computeConfigState(Player player) {
      List<Entity> var10002 = Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering());
      float var10004 = this.getPlaceRange();
      float var10005 = this.getExplodeRange();
      float var10006 = this.getMaxSelfDamage();
      float var10007 = this.getMinDamage();
      boolean var10008 = this.getOldVer();
      float var10009 = this.getArmorScale();
      float var10010 = this.getFacePlaceHp();
      boolean var10011 = this.getOverrideSelfDamage();
      float var10012 = this.getEfficiencyFactor();
      boolean var10013 = this.getRayTrace();
      AutoCrystal.FriendProtection var10014 = this.getFriendProtection();
      boolean var10015 = this.getRemoveFire();
      boolean var10016 = this.getTerrainIgnore();
      int var10017 = this.getLethalMultiplier();
      boolean var10018 = this.getMinimizeSelfDamage();
      float var10019 = this.getMaxDamageReduction();
      boolean var10020 = this.getCalcShields();
      boolean var10021 = this.getRemoveLava();
      boolean var10022 = this.getGrimBreakRange();
      boolean var10023 = this.getExplodeEChestFarm();
      boolean var10024 = this.getAntiPortal();
      boolean var57 = this.getOverrideMinDamage();
      boolean var58 = this.getAntiQuiver();
      boolean var59 = var10024;
      boolean var60 = var10023;
      boolean var61 = var10022;
      boolean var62 = var10021;
      boolean var63 = var10020;
      float var64 = var10019;
      boolean var65 = var10018;
      int var66 = var10017;
      boolean var67 = var10016;
      boolean var68 = var10015;
      AutoCrystal.FriendProtection var69 = var10014;
      boolean var70 = var10013;
      float var71 = var10012;
      boolean var72 = var10011;
      float var73 = var10010;
      float var74 = var10009;
      boolean var75 = var10008;
      float var76 = var10007;
      float var77 = var10006;
      float var78 = var10005;
      float var79 = var10004;
      List<Entity> var81 = var10002;
      return new ConfigState(
         var81,
         player,
         var79,
         var78,
         var77,
         var76,
         var75,
         var74,
         var73,
         var72,
         var71,
         var70,
         var69,
         var68,
         var67,
         var66,
         var65,
         var64,
         var63,
         var62,
         var61,
         var60,
         var59,
         var58,
         var57
      );
   }

   @NotNull
   public final ConfigState getConfigState() {
      return this.computeConfigState(player);
   }

   static {
   }

   public static final class AttackAction {
      @NotNull
      public static final AutoCrystal.AttackAction INSTANCE = new AutoCrystal.AttackAction();
      private static int value = -1;
      @NotNull
      private static final StopWatch stopWatch = new StopWatch();

      private AttackAction() {
      }

      public final void applySend(int value, int value2, int value3, boolean flag) {
         if (stopWatch.testValue(value3)) {
            stopWatch.update();
            int var12 = value;
            if (value <= value2) {
               while (true) {
                  int var13 = AutoCrystal.AttackAction.value + var12;
                  Entity var14 = MinecraftHolder.getClientLevel().getEntity(var13);
                  if (var14 == null || var14 instanceof EndCrystal) {
                     MinecraftHolder.getClientPacketListener().send((Packet)(new ServerboundAttackPacket(var13)));
                     if (!flag || var12 == value2) {
                        MinecraftHolder.getClientPacketListener().send((Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND)));
                     }
                  }

                  if (var12 == value2) {
                     break;
                  }

                  var12++;
               }
            }
         }
      }

      @Nullable
      public final Unit computeUnit(int value) {
         Unit var10000;
         if (value > AutoCrystal.AttackAction.value) {
            AutoCrystal.AttackAction.value = value;
            var10000 = Unit.INSTANCE;
         } else {
            var10000 = null;
         }

         return var10000;
      }
   }

   public static enum DamageSync {
      ALWAYS,
      CONDITIONS,
      OFF;

      private static final AutoCrystal.DamageSync[] damageSyncs = $values();
      private static final EnumEntries<AutoCrystal.DamageSync> enumEntries = EnumEntriesKt.enumEntries(damageSyncs);

      private DamageSync() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.DamageSync> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.DamageSync[] $values() {
         return new AutoCrystal.DamageSync[]{ALWAYS, CONDITIONS, OFF};
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static enum FriendProtection {
      NONE,
      ONLY_LETHAL,
      FULL;

      private static final AutoCrystal.FriendProtection[] friendProtections = $values();
      private static final EnumEntries<AutoCrystal.FriendProtection> enumEntries = EnumEntriesKt.enumEntries(friendProtections);

      private FriendProtection() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.FriendProtection> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.FriendProtection[] $values() {
         return new AutoCrystal.FriendProtection[]{NONE, ONLY_LETHAL, FULL};
      }
   }

   public static enum HighlightTarget {
      OFF,
      COMETS,
      SELECTION,
      BLOOM_CIRCLE;

      private static final AutoCrystal.HighlightTarget[] highlightTargets = $values();
      private static final EnumEntries<AutoCrystal.HighlightTarget> enumEntries = EnumEntriesKt.enumEntries(highlightTargets);

      private HighlightTarget() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.HighlightTarget> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.HighlightTarget[] $values() {
         return new AutoCrystal.HighlightTarget[]{OFF, COMETS, SELECTION, BLOOM_CIRCLE};
      }
   }

   public static enum OnCrystalSpawn {
      NO_REACTION,
      ATTACK,
      ACTION_X;

      private static final AutoCrystal.OnCrystalSpawn[] onCrystalSpawns = $values();
      private static final EnumEntries<AutoCrystal.OnCrystalSpawn> enumEntries = EnumEntriesKt.enumEntries(onCrystalSpawns);

      private OnCrystalSpawn() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.OnCrystalSpawn> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.OnCrystalSpawn[] $values() {
         return new AutoCrystal.OnCrystalSpawn[]{NO_REACTION, ATTACK, ACTION_X};
      }
   }

   public static final class PlaceMarker {
      private int value;

      public PlaceMarker(int value) {
         this.value = value;
      }

      public PlaceMarker(int value, int value2, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value2 & 1) != 0) {
            value = 8;
         }

         this(value);
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }
   }

   public static final class PlaceRenderer {
      @NotNull
      public static final AutoCrystal.PlaceRenderer INSTANCE = new AutoCrystal.PlaceRenderer();
      @NotNull
      private static final Identifier catlean = Identifiers.computeIdentifier("catlean", "textures/exclude/block.png");
      @NotNull
      private static final List<AutoCrystal.PlaceMarker> items = new ArrayList();

      private PlaceRenderer() {
      }

      public final void render(PoseStack m, AABB box) {
         MeshBuilder var32 = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 6, false, 4, null);
         Color var33 = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(0), 0.1F);
         Color var34 = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(180), 1.0F);
         if (Render3D.INSTANCE.getFrustum().isVisible(box)) {
            Vec3 var35 = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
            float var36 = (float)(box.minX - var35.x());
            float var37 = (float)(box.minY - var35.y());
            float var38 = (float)(box.minZ - var35.z());
            float var39 = (float)(box.maxX - var35.x());
            float var40 = (float)(box.maxY - var35.y());
            float var41 = (float)(box.maxZ - var35.z());
            float var42 = Render2D.INSTANCE.computeAmount(MinecraftHolder.getLocalPlayer().tickCount - 1.0F, (float)MinecraftHolder.getLocalPlayer().tickCount)
               % 80
               / 80.0F;
            var32.computeMeshBuilder(var36, var37, var38).computeMeshBuilder(0.0F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var36, var40, var38).computeMeshBuilder(0.0F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var39, var40, var38).computeMeshBuilder(0.32F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var39, var37, var38).computeMeshBuilder(0.32F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var39, var37, var38).computeMeshBuilder(0.32F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var39, var40, var38).computeMeshBuilder(0.32F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var39, var40, var41).computeMeshBuilder(0.64F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var39, var37, var41).computeMeshBuilder(0.64F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var39, var37, var41).computeMeshBuilder(0.64F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var36, var37, var41).computeMeshBuilder(0.96F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var36, var40, var41).computeMeshBuilder(0.96F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var39, var40, var41).computeMeshBuilder(0.64F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var36, var37, var41).computeMeshBuilder(0.96F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var36, var37, var38).computeMeshBuilder(1.28F + var42, 1.0F).fetchAlpha(var33);
            var32.computeMeshBuilder(var36, var40, var38).computeMeshBuilder(1.28F + var42, 0.0F).fetchAlpha(var34);
            var32.computeMeshBuilder(var36, var40, var41).computeMeshBuilder(0.96F + var42, 0.0F).fetchAlpha(var34);
            Render3D.INSTANCE.testAABB(box, Direction.UP, Render2D.INSTANCE.computeColor(var34, 0.4F));
            Render3D.INSTANCE.testAABB2(box, Direction.UP, Render2D.INSTANCE.computeColor(var34, 0.4F));
            MeshBuilder.defaultImpl(
               var32,
               Pipelines.INSTANCE.getRenderPipeline18(),
               null,
               null,
               m.last().pose(),
               null,
               MapsKt.mapOf(TuplesKt.to("Sampler0", MinecraftHolder.getMinecraft().getTextureManager().getTexture(catlean).getTextureView())),
               null,
               86,
               null
            );

            for (Object var46 : items) {
               AutoCrystal.PlaceMarker var47 = (AutoCrystal.PlaceMarker)var46;
               double var49 = 0.5 + RangesKt.coerceIn(Render2D.INSTANCE.computeAmount(var47.getValue() + 1.0F, (float)var47.getValue()) / 8.0F, 0.0F, 0.5F);
               Render3D var10000 = Render3D.INSTANCE;
               AABB var10001 = box.setMaxY(box.maxY - (1.0 - var49)).inflate(-var49, 0.0, -var49);
               Direction var10002 = Direction.UP;
               Color var28 = Render2D.INSTANCE.computeColor(var34, (float)var49);
               Direction var29 = var10002;
               AABB var30 = var10001;
               var10000.testAABB2(var30, var29, var28);
            }
         }
      }

      public final void update() {
         items.add(new AutoCrystal.PlaceMarker(0, 1, null));
      }

      public final void applyRemoveIf() {
         items.removeIf(var0 -> {
            int var4 = ((AutoCrystal.PlaceMarker)var0).getValue();
            ((AutoCrystal.PlaceMarker)var0).setValue(var4 + -1);
            return var4 < 0;
         });
      }
   }

   public static enum Predict {
      AUTO,
      CUSTOM,
      OFF;

      private static final AutoCrystal.Predict[] predicts = $values();
      private static final EnumEntries<AutoCrystal.Predict> enumEntries = EnumEntriesKt.enumEntries(predicts);

      private Predict() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.Predict> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.Predict[] $values() {
         return new AutoCrystal.Predict[]{AUTO, CUSTOM, OFF};
      }
   }

   public static enum RenderMode {
      SHADER,
      DEAFULT,
      TECHNO,
      OFF;

      private static final AutoCrystal.RenderMode[] renderModes = $values();
      private static final EnumEntries<AutoCrystal.RenderMode> enumEntries = EnumEntriesKt.enumEntries(renderModes);

      private RenderMode() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.RenderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.RenderMode[] $values() {
         return new AutoCrystal.RenderMode[]{SHADER, DEAFULT, TECHNO, OFF};
      }
   }

   public static final class RenderPosition {
      @NotNull
      private final BlockPos pos;
      @NotNull
      private final Direction side;
      private final float amount;
      private final float amount2;

      public RenderPosition(BlockPos pos, Direction side, float amount, float amount2) {
         this.pos = pos;
         this.side = side;
         this.amount = amount;
         this.amount2 = amount2;
      }

      @NotNull
      public final BlockPos getPos() {
         return this.pos;
      }

      @NotNull
      public final Direction getSide() {
         return this.side;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      @NotNull
      public final BlockPos getPos2() {
         return this.pos;
      }

      @NotNull
      public final Direction getSide2() {
         return this.side;
      }

      public final float getAmount3() {
         return this.amount;
      }

      public final float getAmount4() {
         return this.amount2;
      }

      @NotNull
      @Override
      public String toString() {
         float var3 = this.amount2;
         float var4 = this.amount;
         Direction var5 = this.side;
         BlockPos var6 = this.pos;
         return "RenderPosition(pos=" + var6 + ", side=" + var5 + ", damage=" + var4 + ", selfDamage=" + var3 + ")";
      }

      @Override
      public int hashCode() {
         int var3 = this.pos.hashCode();
         var3 = var3 * 31 + this.side.hashCode();
         var3 = var3 * 31 + Float.hashCode(this.amount);
         return var3 * 31 + Float.hashCode(this.amount2);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof AutoCrystal.RenderPosition var5)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pos, var5.pos)) {
            return false;
         } else if (this.side != var5.side) {
            return false;
         } else {
            return Float.compare(this.amount, var5.amount) != 0 ? false : Float.compare(this.amount2, var5.amount2) == 0;
         }
      }
   }

   public static enum SpawnTiming {
      PACKET,
      ADD_TO_WORLD;

      private static final AutoCrystal.SpawnTiming[] spawnTimings = $values();
      private static final EnumEntries<AutoCrystal.SpawnTiming> enumEntries = EnumEntriesKt.enumEntries(spawnTimings);

      private SpawnTiming() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.SpawnTiming> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.SpawnTiming[] $values() {
         return new AutoCrystal.SpawnTiming[]{PACKET, ADD_TO_WORLD};
      }
   }

   public static enum Switch {
      NONE,
      NORMAL,
      SILENT,
      INVENTORY;

      private static final AutoCrystal.Switch[] switchValues = $values();
      private static final EnumEntries<AutoCrystal.Switch> enumEntries = EnumEntriesKt.enumEntries(switchValues);

      private Switch() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.Switch> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.Switch[] $values() {
         return new AutoCrystal.Switch[]{NONE, NORMAL, SILENT, INVENTORY};
      }
   }

   public static enum TargetSort {
      DISTANCE,
      FOV,
      HEALTH;

      private static final AutoCrystal.TargetSort[] targetSorts = $values();
      private static final EnumEntries<AutoCrystal.TargetSort> enumEntries = EnumEntriesKt.enumEntries(targetSorts);

      private TargetSort() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.TargetSort> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.TargetSort[] $values() {
         return new AutoCrystal.TargetSort[]{DISTANCE, FOV, HEALTH};
      }
   }

   public static enum Timing {
      VANILLA,
      SEQUENTIAL;

      private static final AutoCrystal.Timing[] timings = $values();
      private static final EnumEntries<AutoCrystal.Timing> enumEntries = EnumEntriesKt.enumEntries(timings);

      private Timing() {
      }

      @NotNull
      public static EnumEntries<AutoCrystal.Timing> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoCrystal.Timing[] $values() {
         return new AutoCrystal.Timing[]{VANILLA, SEQUENTIAL};
      }
   }

   public static final class ToolPredicate implements ItemPredicate {
      public static final AutoCrystal.ToolPredicate INSTANCE = new AutoCrystal.ToolPredicate();

      public ToolPredicate() {
      }

      @Override
      public final boolean checkGetItem(ItemStack it) {
         return ItemTypeUtil.testItemStack(it) || ItemTypeUtil.testItemStack2(it) || it.getItem() instanceof AxeItem || it.getItem() instanceof ShovelItem;
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;
      public static final int[] values3;

      static {
         int[] var0 = new int[AutoCrystal.HighlightTarget.values().length];

         try {
            var0[AutoCrystal.HighlightTarget.COMETS.ordinal()] = 1;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[AutoCrystal.HighlightTarget.SELECTION.ordinal()] = 2;
         } catch (NoSuchFieldError var8) {
         }

         values = var0;
         var0 = new int[AutoCrystal.Switch.values().length];

         try {
            var0[AutoCrystal.Switch.INVENTORY.ordinal()] = 1;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[AutoCrystal.Switch.SILENT.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[AutoCrystal.Switch.NORMAL.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         values2 = var0;
         var0 = new int[AutoCrystal.Predict.values().length];

         try {
            var0[AutoCrystal.Predict.AUTO.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[AutoCrystal.Predict.CUSTOM.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[AutoCrystal.Predict.OFF.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         values3 = var0;
      }
   }
}