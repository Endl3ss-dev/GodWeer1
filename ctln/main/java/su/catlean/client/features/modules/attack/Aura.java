// deobf by pusia
package su.catlean.client.features.modules.attack;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.client.PostTickEvent;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.SprintEvent;
import su.catlean.api.event.events.player.UpdateCrosshairTarget;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.aura.AuraBacktrack;
import su.catlean.client.features.modules.attack.aura.AuraDamageHandler;
import su.catlean.client.features.modules.attack.aura.AuraHandler;
import su.catlean.client.features.modules.attack.aura.AuraMovementHandler;
import su.catlean.client.features.modules.attack.aura.AuraSprintHandler;
import su.catlean.client.features.modules.attack.aura.AuraTargetSelector;
import su.catlean.client.features.modules.attack.aura.AuraUseItemHandler;
import su.catlean.client.features.modules.attack.aura.AuraWeaponSwitchHandler;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.features.modules.rage.MaceElytraTarget;
import su.catlean.client.render.EntityPreviewRenderer;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.client.services.rotation.modes.AdvancedRotation;
import su.catlean.client.services.rotation.modes.CustomRotation;
import su.catlean.client.services.rotation.modes.DefaultRotation;
import su.catlean.client.services.rotation.modes.HvhRotation;
import su.catlean.client.services.rotation.modes.NoRotation;
import su.catlean.client.services.rotation.modes.PolarRotation;
import su.catlean.client.services.rotation.modes.RotationStrategy;
import su.catlean.client.services.rotation.modes.SnapRotation;
import su.catlean.client.services.rotation.modes.VulcanRotation;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StringHolder;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.world.WorldUtil;

public final class Aura extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Aura.class, "attackRange", "getAttackRange()F", 0), new PropertyReference1Impl(Aura.class, "ignoreWalls", "getIgnoreWalls()Z", 0), new PropertyReference1Impl(Aura.class, "fov", "getFov()I", 0), new PropertyReference1Impl(Aura.class, "rotationMode", "getRotationMode()Lsu/catlean/cg;", 0), new PropertyReference1Impl(Aura.class, "priorityManual", "getPriorityManual()Z", 0), new PropertyReference1Impl(Aura.class, "switchMode", "getSwitchMode()Lsu/catlean/fj;", 0), new PropertyReference1Impl(Aura.class, "onlyWeapon", "getOnlyWeapon()Z", 0), new PropertyReference1Impl(Aura.class, "oldDelay", "getOldDelay()Z", 0), new PropertyReference1Impl(Aura.class, "minCps", "getMinCps()I", 0), new PropertyReference1Impl(Aura.class, "maxCps", "getMaxCps()I", 0), new PropertyReference1Impl(Aura.class, "shieldBreaker", "getShieldBreaker()Z", 0), new PropertyReference1Impl(Aura.class, "splashWhileOnGround", "getSplashWhileOnGround()Z", 0), new PropertyReference1Impl(Aura.class, "backTrack", "getBackTrack()Z", 0), new PropertyReference1Impl(Aura.class, "moveDelay", "getMoveDelay()I", 0), new PropertyReference1Impl(Aura.class, "attackChance", "getAttackChance()I", 0), new PropertyReference1Impl(Aura.class, "pauseWhileEating", "getPauseWhileEating()Z", 0), new PropertyReference1Impl(Aura.class, "pauseInInventory", "getPauseInInventory()Z", 0), new PropertyReference1Impl(Aura.class, "whileShielding", "getWhileShielding()Lsu/catlean/k;", 0), new PropertyReference1Impl(Aura.class, "deathDisable", "getDeathDisable()Z", 0), new PropertyReference1Impl(Aura.class, "overrideCrosshair", "getOverrideCrosshair()Z", 0), new PropertyReference1Impl(Aura.class, "rotateOnDisable", "getRotateOnDisable()Z", 0), new PropertyReference1Impl(Aura.class, "onHitSwap", "getOnHitSwap()Z", 0), new PropertyReference1Impl(Aura.class, "randomizeDelay", "getRandomizeDelay()Lsu/catlean/n9;", 0), new PropertyReference1Impl(Aura.class, "sprintMode", "getSprintMode()Lsu/catlean/ko;", 0), new PropertyReference1Impl(Aura.class, "esp", "getEsp()Lsu/catlean/ds;", 0), new PropertyReference1Impl(Aura.class, "sort", "getSort()Lsu/catlean/qq;", 0), new PropertyReference1Impl(Aura.class, "targets", "getTargets()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Aura.class, "antibot", "getAntibot()Z", 0), new PropertyReference1Impl(Aura.class, "players", "getPlayers()Z", 0), new PropertyReference1Impl(Aura.class, "mobs", "getMobs()Z", 0), new PropertyReference1Impl(Aura.class, "animals", "getAnimals()Z", 0), new PropertyReference1Impl(Aura.class, "villagers", "getVillagers()Z", 0), new PropertyReference1Impl(Aura.class, "hostiles", "getHostiles()Z", 0), new PropertyReference1Impl(Aura.class, "onlyAngry", "getOnlyAngry()Z", 0), new PropertyReference1Impl(Aura.class, "projectiles", "getProjectiles()Z", 0), new PropertyReference1Impl(Aura.class, "ignoreInvisible", "getIgnoreInvisible()Z", 0), new PropertyReference1Impl(Aura.class, "ignoreNamed", "getIgnoreNamed()Z", 0), new PropertyReference1Impl(Aura.class, "ignoreTeam", "getIgnoreTeam()Z", 0), new PropertyReference1Impl(Aura.class, "ignoreCreative", "getIgnoreCreative()Z", 0), new PropertyReference1Impl(Aura.class, "ignoreNaked", "getIgnoreNaked()Z", 0) };
   @NotNull
   public static final Aura INSTANCE = new Aura();
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 3.0F, RangesKt.rangeTo(3.0F, 6.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting ignoreWalls = SettingFactory.defaultImpl(INSTANCE, "ignore-walls", true, null, null, 12, null);
   @NotNull
   private static final IntSetting fov = StringHolder.computeIntSetting(
      SettingFactory.defaultImpl(INSTANCE, "fov", 180, new IntRange(1, 180), null, null, 24, null), 1, "trigger"
   );
   @NotNull
   private static final EnumSetting rotationMode = SettingFactory.defaultImpl(INSTANCE, "rotation-mode", Aura.RotationMode.DEFAULT, null, null, 12, null);
   @NotNull
   private static final BooleanSetting priorityManual = SettingFactory.defaultImpl(
      INSTANCE, "priority-manual", true, null, () -> INSTANCE.getRotationMode() == Aura.RotationMode.SNAP, 4, null
   );
   @NotNull
   private static final EnumSetting autoWeapon = SettingFactory.defaultImpl(INSTANCE, "auto-weapon", Aura.AutoWeapon.NONE, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyWeapon = SettingFactory.defaultImpl(INSTANCE, "only-weapon", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting oldDelay = SettingFactory.defaultImpl(INSTANCE, "old-delay", false, null, null, 12, null);
   @NotNull
   private static final IntSetting minCps = SettingFactory.defaultImpl(INSTANCE, "min-cps", 9, new IntRange(1, 20), null, () -> INSTANCE.getOldDelay(), 8, null);
   @NotNull
   private static final IntSetting maxCps = SettingFactory.defaultImpl(
      INSTANCE, "max-cps", 11, new IntRange(1, 20), null, () -> INSTANCE.getOldDelay(), 8, null
   );
   @NotNull
   private static final BooleanSetting shieldBreaker = SettingFactory.defaultImpl(INSTANCE, "shield-breaker", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting splashWhileOnGround = SettingFactory.defaultImpl(INSTANCE, "splash-while-on-ground", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting backTrack = SettingFactory.defaultImpl(INSTANCE, "back-track", false, null, null, 12, null);
   @NotNull
   private static final IntSetting moveDelay = SettingFactory.defaultImpl(
      INSTANCE, "move-delay", 250, new IntRange(0, 1000), null, () -> INSTANCE.getBackTrack(), 8, null
   );
   @NotNull
   private static final IntSetting attackChance = SettingFactory.defaultImpl(INSTANCE, "attack-chance", 100, new IntRange(50, 100), null, null, 24, null);
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(INSTANCE, "pause-while-eating", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pauseInInventory = SettingFactory.defaultImpl(INSTANCE, "pause-in-inventory", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting whileShielding = SettingFactory.defaultImpl(INSTANCE, "while-shielding", Aura.WhileShielding.UNPRESS, null, null, 12, null);
   @NotNull
   private static final BooleanSetting disableOnDeath = SettingFactory.defaultImpl(INSTANCE, "disable-on-death", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting overrideCrosshair = SettingFactory.defaultImpl(INSTANCE, "override-crosshair", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rotateOnDisable = SettingFactory.defaultImpl(INSTANCE, "rotate-on-disable", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting offhandHitSwap = SettingFactory.defaultImpl(INSTANCE, "offhand-hit-swap", false, null, null, 12, null);
   @NotNull
   private static final EnumSetting randomizeDelay = SettingFactory.defaultImpl(INSTANCE, "randomize-delay", Aura.RandomizeDelay.OFF, null, null, 12, null);
   @NotNull
   private static final EnumSetting sprint = SettingFactory.defaultImpl(INSTANCE, "sprint", Aura.Sprint.NORMAL, null, null, 12, null);
   @NotNull
   private static final EnumSetting esp = SettingFactory.defaultImpl(INSTANCE, "esp", AutoCrystal.HighlightTarget.COMETS, null, null, 12, null);
   @NotNull
   private static final EnumSetting sort = SettingFactory.defaultImpl(INSTANCE, "sort", Aura.Sort.Distance, null, null, 12, null);
   @NotNull
   private static final GroupSetting targets = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "targets", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting antibot = SettingFactory.defaultImpl(INSTANCE, "antibot", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting players = SettingFactory.defaultImpl(INSTANCE, "players", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting mobs = SettingFactory.defaultImpl(INSTANCE, "mobs", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting animals = SettingFactory.defaultImpl(INSTANCE, "animals", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting villagers = SettingFactory.defaultImpl(INSTANCE, "villagers", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting hostiles = SettingFactory.defaultImpl(INSTANCE, "hostiles", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting onlyAngryHostiles = SettingFactory.computeBooleanSetting(
      INSTANCE, "only-angry-hostiles", true, INSTANCE.getTargets(), () -> INSTANCE.getHostiles()
   );
   @NotNull
   private static final BooleanSetting projectiles = SettingFactory.defaultImpl(INSTANCE, "projectiles", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting ignoreInvisibleEntities = SettingFactory.defaultImpl(
      INSTANCE, "ignore-invisible-entities", false, INSTANCE.getTargets(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting ignoreNamed = SettingFactory.defaultImpl(INSTANCE, "ignore-named", false, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting ignoreTeam = SettingFactory.defaultImpl(INSTANCE, "ignore-team", false, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting ignoreCreative = SettingFactory.defaultImpl(INSTANCE, "ignore-creative", true, INSTANCE.getTargets(), null, 8, null);
   @NotNull
   private static final BooleanSetting ignoreNaked = SettingFactory.defaultImpl(INSTANCE, "ignore-naked", false, INSTANCE.getTargets(), null, 8, null);
   @Nullable
   private static Entity entity;
   @NotNull
   private static List items;
   @NotNull
   private static Rotation rotation = new Rotation(0.0F, 0.0F, false, null, 12, null);
   private static boolean flag;

   private Aura() {
      super("aura", SubCategories.getSubCategory2(), null, 4, null);
   }

   public final float getAttackRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   public final boolean getIgnoreWalls() {
      return (Boolean)ignoreWalls.computeObject(this, $$delegatedProperties[1]);
   }

   public final int getFov() {
      return ((Number)fov.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   @NotNull
   public final Aura.RotationMode getRotationMode() {
      return (Aura.RotationMode)rotationMode.computeObject(this, $$delegatedProperties[3]);
   }

   public final boolean getPriorityManual() {
      return (Boolean)priorityManual.computeObject(this, $$delegatedProperties[4]);
   }

   @NotNull
   public final Aura.AutoWeapon getSwitchMode() {
      return (Aura.AutoWeapon)autoWeapon.computeObject(this, $$delegatedProperties[5]);
   }

   public final boolean getOnlyWeapon() {
      return (Boolean)onlyWeapon.computeObject(this, $$delegatedProperties[6]);
   }

   public final boolean getOldDelay() {
      return (Boolean)oldDelay.computeObject(this, $$delegatedProperties[7]);
   }

   public final int getMinCps() {
      return ((Number)minCps.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   public final int getMaxCps() {
      return ((Number)maxCps.computeObject(this, $$delegatedProperties[9])).intValue();
   }

   public final boolean getShieldBreaker() {
      return (Boolean)shieldBreaker.computeObject(this, $$delegatedProperties[10]);
   }

   public final boolean getSplashWhileOnGround() {
      return (Boolean)splashWhileOnGround.computeObject(this, $$delegatedProperties[11]);
   }

   public final boolean getBackTrack() {
      return (Boolean)backTrack.computeObject(this, $$delegatedProperties[12]);
   }

   public final int getMoveDelay() {
      return ((Number)moveDelay.computeObject(this, $$delegatedProperties[13])).intValue();
   }

   public final int getAttackChance() {
      return ((Number)attackChance.computeObject(this, $$delegatedProperties[14])).intValue();
   }

   public final boolean getPauseWhileEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[15]);
   }

   public final boolean getPauseInInventory() {
      return (Boolean)pauseInInventory.computeObject(this, $$delegatedProperties[16]);
   }

   @NotNull
   public final Aura.WhileShielding getWhileShielding() {
      return (Aura.WhileShielding)whileShielding.computeObject(this, $$delegatedProperties[17]);
   }

   public final boolean getDeathDisable() {
      return (Boolean)disableOnDeath.computeObject(this, $$delegatedProperties[18]);
   }

   public final boolean getOverrideCrosshair() {
      return (Boolean)overrideCrosshair.computeObject(this, $$delegatedProperties[19]);
   }

   public final boolean getRotateOnDisable() {
      return (Boolean)rotateOnDisable.computeObject(this, $$delegatedProperties[20]);
   }

   public final boolean getOnHitSwap() {
      return (Boolean)offhandHitSwap.computeObject(this, $$delegatedProperties[21]);
   }

   @NotNull
   public final Aura.RandomizeDelay getRandomizeDelay() {
      return (Aura.RandomizeDelay)randomizeDelay.computeObject(this, $$delegatedProperties[22]);
   }

   @NotNull
   public final Aura.Sprint getSprintMode() {
      return (Aura.Sprint)sprint.computeObject(this, $$delegatedProperties[23]);
   }

   @NotNull
   public final AutoCrystal.HighlightTarget getEsp() {
      return (AutoCrystal.HighlightTarget)esp.computeObject(this, $$delegatedProperties[24]);
   }

   @NotNull
   public final Aura.Sort getSort() {
      return (Aura.Sort)sort.computeObject(this, $$delegatedProperties[25]);
   }

   @NotNull
   public final GroupValue getTargets() {
      return (GroupValue)targets.computeObject(this, $$delegatedProperties[26]);
   }

   public final boolean getAntibot() {
      return (Boolean)antibot.computeObject(this, $$delegatedProperties[27]);
   }

   public final boolean getPlayers() {
      return (Boolean)players.computeObject(this, $$delegatedProperties[28]);
   }

   public final boolean getMobs() {
      return (Boolean)mobs.computeObject(this, $$delegatedProperties[29]);
   }

   public final boolean getAnimals() {
      return (Boolean)animals.computeObject(this, $$delegatedProperties[30]);
   }

   public final boolean getVillagers() {
      return (Boolean)villagers.computeObject(this, $$delegatedProperties[31]);
   }

   public final boolean getHostiles() {
      return (Boolean)hostiles.computeObject(this, $$delegatedProperties[32]);
   }

   public final boolean getOnlyAngry() {
      return (Boolean)onlyAngryHostiles.computeObject(this, $$delegatedProperties[33]);
   }

   public final boolean getProjectiles() {
      return (Boolean)projectiles.computeObject(this, $$delegatedProperties[34]);
   }

   public final boolean getIgnoreInvisible() {
      return (Boolean)ignoreInvisibleEntities.computeObject(this, $$delegatedProperties[35]);
   }

   public final boolean getIgnoreNamed() {
      return (Boolean)ignoreNamed.computeObject(this, $$delegatedProperties[36]);
   }

   public final boolean getIgnoreTeam() {
      return (Boolean)ignoreTeam.computeObject(this, $$delegatedProperties[37]);
   }

   public final boolean getIgnoreCreative() {
      return (Boolean)ignoreCreative.computeObject(this, $$delegatedProperties[38]);
   }

   public final boolean getIgnoreNaked() {
      return (Boolean)ignoreNaked.computeObject(this, $$delegatedProperties[39]);
   }

   @Nullable
   public final Entity getEntity() {
      return entity;
   }

   public final void setEntity(Entity entity) {
      Aura.entity = entity;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!this.isFlag4()) {
         if (this.getBackTrack()) {
            AuraBacktrack.INSTANCE.update();
         }

         if (entity != null && entity instanceof LivingEntity && ((LivingEntity)entity).isDeadOrDying()) {
            SoundManager.INSTANCE.updateFlag(false);
         }

         AuraTargetSelector.INSTANCE
            .applyPlayers(
               () -> {
                  entity = AuraTargetSelector.INSTANCE.computeEntity(entity);
                  Iterable items2 = items;
                  boolean flag2 = false;

                  for (Object object : items2) {
                     AuraHandler auraHandler = (AuraHandler)object;
                     auraHandler.update2();
                  }

                  if (entity == null) {
                     if (INSTANCE.getRotateOnDisable() && flag) {
                        MinecraftHolder.getLocalPlayer().forceSetRotation(rotation.getAmount(), false, rotation.getAmount2(), false);
                        flag = false;
                     }

                     rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
                     return Unit.INSTANCE;
                  } else {
                     if (INSTANCE.getSwitchMode() == Aura.AutoWeapon.NORMAL) {
                        AuraWeaponSwitchHandler.defaultImpl(AuraWeaponSwitchHandler.INSTANCE, false, 1, null);
                     }

                     boolean checkGetDeltaMovement = AuraSprintHandler.INSTANCE
                        .checkGetDeltaMovement(AuraMovementHandler.defaultImpl(AuraMovementHandler.INSTANCE, 0, 1, null));
                     rotation = INSTANCE.getFov() == 1
                        ? new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null)
                        : INSTANCE.getRotationMode2().getRotationStrategy().fetchBoundingBox(entity, rotation, checkGetDeltaMovement);
                     boolean checkDistanceToSqr;
                     if (INSTANCE.getRotationMode2().getRotationStrategy().isFlag3()) {
                        InteractionManager instance = InteractionManager.INSTANCE;
                        Entity entity2 = entity;
                        Rotation rotation2 = rotation;
                        boolean flag3 = !INSTANCE.getIgnoreWalls();
                        float attackRange = INSTANCE.getAttackRange();
                        Rotation rotation3 = rotation2;
                        Entity entity3 = entity2;
                        checkDistanceToSqr = instance.checkDistanceToSqr(entity3, rotation3, attackRange, flag3);
                     } else {
                        checkDistanceToSqr = MinecraftHolder.getLocalPlayer().hasLineOfSight(entity) || INSTANCE.getIgnoreWalls();
                     }

                     flag2 = checkDistanceToSqr;
                     final boolean flag2Copy = flag2;
                     RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(rotation, 130, () -> {
                        flag = true;
                        if (checkGetDeltaMovement && flag2Copy && !INSTANCE.isFlag2()) {
                           INSTANCE.applyGetX(entity, rotation);
                        }

                        return Unit.INSTANCE;
                     }));
                     return Unit.INSTANCE;
                  }
               }
            );
      } else {
         entity = null;
         if (this.getRotateOnDisable() && flag) {
            MinecraftHolder.getLocalPlayer().forceSetRotation(rotation.getAmount(), false, rotation.getAmount2(), false);
            flag = false;
         }

         rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);

         for (Object item : items) {
            AuraHandler auraHandler2 = (AuraHandler)item;
            auraHandler2.update2();
         }
      }
   }

   @Flow
   private final void onSprint(SprintEvent sprintEvent) {
      if (AuraSprintHandler.INSTANCE.isFlag2() || su.catlean.client.features.modules.basic.Sprint.INSTANCE.isFlag()) {
         sprintEvent.setSprinting(false);
      }
   }

   @Flow
   private final void onPostTick(PostTickEvent postTickEvent) {
      AuraDamageHandler.INSTANCE.update3();
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundEntityEventPacket) {
         if (((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 48
            && entity != null
            && Intrinsics.areEqual(((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel()), entity)) {
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE, "Aura", "Успешно сломали щит игроку " + entity.getName().getString(), 2, MessageType.SUCCESS, null, 16, null
            );
         }

         if (((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 3
            && Intrinsics.areEqual(
               ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel()), MinecraftHolder.getLocalPlayer()
            )
            && this.getDeathDisable()) {
            this.updateText10("Disabling due to death!");
         }
      }

      if (this.getBackTrack()) {
         AuraBacktrack.INSTANCE.onReceivePacket(receivePacket);
      }
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      if (this.getBackTrack()) {
         AuraBacktrack.INSTANCE.applyClear();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (AuraWeaponSwitchHandler.INSTANCE.checkGetItem(this.getOnlyWeapon(), this.getSwitchMode()) && entity != null) {
         switch (Aura.WhenMappings.values[this.getEsp().ordinal()]) {
            case 1:
               EntityPreviewRenderer instance = EntityPreviewRenderer.INSTANCE;
               PoseStack stack = render3DEvent.getStack();
               Entity entity2 = entity;
               float coerceIn = 1.0F - RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().distanceTo(entity) / this.getAttackRange(), 0.0F, 1.0F);
               Entity entity3 = entity2;
               instance.render(stack, entity3, coerceIn);
               break;
            case 2:
               EntityPreviewRenderer.INSTANCE
                  .render2(
                     render3DEvent.getStack(),
                     entity,
                     1.0F - RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().distanceTo(entity) / this.getAttackRange(), 0.0F, 1.0F)
                  );
               break;
            case 3:
               EntityPreviewRenderer instance2 = EntityPreviewRenderer.INSTANCE;
               PoseStack stack2 = render3DEvent.getStack();
               Entity entity4 = entity;
               instance2.render(stack2, entity4);
         }

         if (this.getBackTrack()) {
            AuraBacktrack.INSTANCE.onRender3D(render3DEvent);
         }
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (AuraWeaponSwitchHandler.INSTANCE.checkGetItem(this.getOnlyWeapon(), this.getSwitchMode()) && entity != null) {
         if (Aura.WhenMappings.values[this.getEsp().ordinal()] == 3) {
            EntityPreviewRenderer.INSTANCE.applySetModifyBuffer();
         }
      }
   }

   @Flow
   private final void onUpdateCrosshairTarget(UpdateCrosshairTarget updateCrosshairTarget) {
      if (entity != null && this.getOverrideCrosshair()) {
         Profiler.get().pop();
         updateCrosshairTarget.cancel();
         if (MinecraftHolder.getMinecraft().getCameraEntity() != null
            && MinecraftHolder.getMinecraft().level != null
            && MinecraftHolder.getMinecraft().player != null) {
            HitResult hitResult = WorldUtil.INSTANCE
               .computeHitResult(MinecraftHolder.getLocalPlayer().blockInteractionRange(), this.getAttackRange(), rotation.getAmount(), rotation.getAmount2());
            MinecraftHolder.getMinecraft().hitResult = hitResult;
            MinecraftHolder.getMinecraft().crosshairPickEntity = hitResult instanceof EntityHitResult ? ((EntityHitResult)hitResult).getEntity() : null;
         }
      }
   }

   @Override
   public void update2() {
      this.applyGetXRot();
   }

   @Override
   public void update4() {
      if (this.getRotateOnDisable() && MinecraftHolder.getMinecraft().player != null) {
         MinecraftHolder.getLocalPlayer().forceSetRotation(rotation.getAmount(), false, rotation.getAmount2(), false);
      }

      this.applyGetXRot();
   }

   public final boolean isFlag() {
      return this.getEnabled() && entity != null && AuraWeaponSwitchHandler.INSTANCE.checkGetItem(this.getOnlyWeapon(), this.getSwitchMode());
   }

   public final void applyGetXRot() {
      entity = null;
      AuraBacktrack.INSTANCE.applyRemoveIf();
      if (MinecraftHolder.getMinecraft().player != null) {
         rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
         AuraSprintHandler.INSTANCE.setFlag2(false);
      }
   }

   private final boolean isFlag2() {
      if (this.getShieldBreaker() && entity instanceof Player && ((Player)entity).isUsingItem()) {
         if (!Intrinsics.areEqual(((Player)entity).getOffhandItem().getItem(), Items.SHIELD)
            && !Intrinsics.areEqual(((Player)entity).getMainHandItem().getItem(), Items.SHIELD)) {
            return false;
         } else {
            SearchInvResult inventory2 = InventoryManager.INSTANCE.fetchInventory2(arg -> arg.getItem() instanceof AxeItem);
            if (!inventory2.isFlag()) {
               return false;
            } else {
               if (inventory2.getValue() > 8) {
                  GuiMove.INSTANCE.setFlag5(true);
                  ContainerUtil.applyHandleContainerInput(
                     inventory2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, true
                  );
                  MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity);
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  int value = inventory2.getValue();
                  int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  boolean flag2 = true;
                  ContainerInput swap = ContainerInput.SWAP;
                  int value2 = selectedSlot;
                  int value3 = value;
                  ContainerUtil.applyHandleContainerInput(value3, value2, swap, flag2);
                  GuiMove.INSTANCE.setFlag5(false);
               } else {
                  InventoryManager.INSTANCE.updateValue(inventory2.getValue());
                  MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity);
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  InventoryManager.INSTANCE.update();
               }

               AuraMovementHandler.INSTANCE.setValue(10);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private final void applyGetX(Entity entity, Rotation rotation) {
      if (this.getAttackChance() != 100 && !(Random.Default.nextFloat() * 100.0F < this.getAttackChance())) {
         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
      } else {
         Iterable items2 = items;

         for (Object object : items2) {
            AuraHandler auraHandler = (AuraHandler)object;
            auraHandler.update();
         }

         Rotation rotation2 = Rotation.vectorUtil.computeRotation(entity.getBoundingBox().getCenter());
         if (this.getRotationMode() == Aura.RotationMode.GRIM_SILENT) {
            LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
            localPlayer.connection
               .send(
                  (Packet)(
                     new PosRot(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), rotation2.getAmount(), rotation2.getAmount2(), localPlayer.onGround(), localPlayer.horizontalCollision)
                  )
               );
         }

         PiercingWeapon piercinG_WEAPON = (PiercingWeapon)MinecraftHolder.getLocalPlayer().getItemInHand(InteractionHand.MAIN_HAND).get(DataComponents.PIERCING_WEAPON);
         if (piercinG_WEAPON != null && !MinecraftHolder.getMultiPlayerGameMode().isSpectator()) {
            MinecraftHolder.getMultiPlayerGameMode().piercingAttack(piercinG_WEAPON);
         } else {
            MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity);
         }

         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         if (this.getRotationMode() == Aura.RotationMode.GRIM_SILENT) {
            LocalPlayer localPlayer2 = MinecraftHolder.getLocalPlayer();
            localPlayer2.connection
               .send(
                  (Packet)(new PosRot(localPlayer2.getX(), localPlayer2.getY(), localPlayer2.getZ(), localPlayer2.getYRot(), localPlayer2.getXRot(), localPlayer2.onGround(), localPlayer2.horizontalCollision))
               );
         }

         Iterable items3 = items;

         for (Object object2 : items3) {
            AuraHandler auraHandler2 = (AuraHandler)object2;
            auraHandler2.applyMax(rotation);
         }
      }

      MinecraftHolder.getLocalPlayer().resetOnlyAttackStrengthTicker();
      AdvancedRotation.INSTANCE.update();
   }

   public final boolean isFlag3() {
      return MinecraftHolder.getClientLevel()
         .getBlockCollisions((Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(-0.25, 1.0, -0.25))
         .iterator()
         .hasNext();
   }

   private final boolean isFlag4() {
      return WorldUtil.INSTANCE.isFlag2() && this.getPauseInInventory()
         || MinecraftHolder.getLocalPlayer().isUsingItem()
            && this.getPauseWhileEating()
            && MinecraftHolder.getLocalPlayer().getUseItem().get(DataComponents.BLOCKS_ATTACKS) == null
         || MinecraftHolder.getLocalPlayer().isUsingItem()
            && this.getWhileShielding() == Aura.WhileShielding.PAUSE
            && MinecraftHolder.getLocalPlayer().getUseItem().get(DataComponents.BLOCKS_ATTACKS) != null
         || !AuraWeaponSwitchHandler.INSTANCE.checkGetItem(this.getOnlyWeapon(), this.getSwitchMode())
         || Blink.INSTANCE.getEnabled() && Blink.INSTANCE.getAttackAction() == Blink.AttackAction.NONE && !ElytraTarget.INSTANCE.getEnabled();
   }

   @NotNull
   public final Aura.RotationMode getRotationMode2() {
      return (!ElytraTarget.INSTANCE.getEnabled() || !MinecraftHolder.getLocalPlayer().isFallFlying()) && !MaceElytraTarget.INSTANCE.getEnabled()
         ? this.getRotationMode()
         : Aura.RotationMode.DEFAULT;
   }

   static {
      AuraHandler[] auraHandlers = new AuraHandler[]{
         AuraMovementHandler.INSTANCE, AuraDamageHandler.INSTANCE, AuraSprintHandler.INSTANCE, AuraUseItemHandler.INSTANCE, AuraWeaponSwitchHandler.INSTANCE
      };
      items = CollectionsKt.listOf(auraHandlers);
   }

   public static enum AutoWeapon {
      NORMAL,
      NONE,
      SILENT;

      private static final Aura.AutoWeapon[] autoWeapons = $values();
      private static final EnumEntries<Aura.AutoWeapon> enumEntries = EnumEntriesKt.enumEntries(autoWeapons);

      private AutoWeapon() {
      }

      @NotNull
      public static EnumEntries<Aura.AutoWeapon> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.AutoWeapon[] $values() {
         return new Aura.AutoWeapon[]{NORMAL, NONE, SILENT};
      }
   }

   public static enum RandomizeDelay {
      OFF,
      VULCAN,
      DEFAULT;

      private static final Aura.RandomizeDelay[] randomizeDelays = $values();
      private static final EnumEntries<Aura.RandomizeDelay> enumEntries = EnumEntriesKt.enumEntries(randomizeDelays);

      private RandomizeDelay() {
      }

      @NotNull
      public static EnumEntries<Aura.RandomizeDelay> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.RandomizeDelay[] $values() {
         return new Aura.RandomizeDelay[]{OFF, VULCAN, DEFAULT};
      }
   }

   public static enum RotationMode {
      SNAP(SnapRotation.INSTANCE),
      DEFAULT(DefaultRotation.INSTANCE),
      NONE(NoRotation.INSTANCE),
      VULCAN(VulcanRotation.INSTANCE),
      HVH(HvhRotation.INSTANCE),
      POLAR_SEP_25(PolarRotation.INSTANCE),
      GRIM_SILENT(NoRotation.INSTANCE),
      ADVANCED(AdvancedRotation.INSTANCE),
      CUSTOM(CustomRotation.INSTANCE);

      @NotNull
      private final RotationStrategy rotationStrategy;
      private static final Aura.RotationMode[] rotationModes = $values();
      private static final EnumEntries<Aura.RotationMode> enumEntries = EnumEntriesKt.enumEntries(rotationModes);

      private RotationMode(RotationStrategy rotationStrategy) {
         this.rotationStrategy = rotationStrategy;
      }

      @NotNull
      public final RotationStrategy getRotationStrategy() {
         return this.rotationStrategy;
      }

      @NotNull
      public static EnumEntries<Aura.RotationMode> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.RotationMode[] $values() {
         return new Aura.RotationMode[]{SNAP, DEFAULT, NONE, VULCAN, HVH, POLAR_SEP_25, GRIM_SILENT, ADVANCED, CUSTOM};
      }
   }

   public static enum Sort {
      Distance,
      Health,
      FOV;

      private static final Aura.Sort[] sorts = $values();
      private static final EnumEntries<Aura.Sort> enumEntries = EnumEntriesKt.enumEntries(sorts);

      private Sort() {
      }

      @NotNull
      public static EnumEntries<Aura.Sort> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.Sort[] $values() {
         return new Aura.Sort[]{Distance, Health, FOV};
      }
   }

   public static enum Sprint {
      LEGIT,
      LEGIT_EXTRA,
      NORMAL,
      HVH;

      private static final Aura.Sprint[] sprints = $values();
      private static final EnumEntries<Aura.Sprint> enumEntries = EnumEntriesKt.enumEntries(sprints);

      private Sprint() {
      }

      @NotNull
      public static EnumEntries<Aura.Sprint> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.Sprint[] $values() {
         return new Aura.Sprint[]{LEGIT, LEGIT_EXTRA, NORMAL, HVH};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoCrystal.HighlightTarget.values().length];

         try {
            values2[AutoCrystal.HighlightTarget.COMETS.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoCrystal.HighlightTarget.BLOOM_CIRCLE.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoCrystal.HighlightTarget.SELECTION.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }

   public static enum WhileShielding {
      PAUSE,
      IGNORE,
      UNPRESS,
      LEGIT;

      private static final Aura.WhileShielding[] whileShieldings = $values();
      private static final EnumEntries<Aura.WhileShielding> enumEntries = EnumEntriesKt.enumEntries(whileShieldings);

      private WhileShielding() {
      }

      @NotNull
      public static EnumEntries<Aura.WhileShielding> getEnumEntries() {
         return enumEntries;
      }

      private static final Aura.WhileShielding[] $values() {
         return new Aura.WhileShielding[]{PAUSE, IGNORE, UNPRESS, LEGIT};
      }
   }
}