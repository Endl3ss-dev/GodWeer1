// deobf by pusia
package su.catlean.client.features.modules.rage;

import net.minecraft.sounds.SoundEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVectorEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.Criticals;
import su.catlean.client.features.modules.attack.aura.AuraMovementHandler;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.world.WorldUtil;

public final class MaceElytraTarget extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(MaceElytraTarget.class, "height", "getHeight()I", 0), new PropertyReference1Impl(MaceElytraTarget.class, "swapDelay", "getSwapDelay()I", 0), new PropertyReference1Impl(MaceElytraTarget.class, "fireworkDelay", "getFireworkDelay()I", 0), new PropertyReference1Impl(MaceElytraTarget.class, "offHandFirework", "getOffHandFirework()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "alternativeFireworkSwap", "getAlternativeFireworkSwap()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "pauseWhileEating", "getPauseWhileEating()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "minimizeClicks", "getMinimizeClicks()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "peekAssist", "getPeekAssist()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "windBurstSync", "getWindBurstSync()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "boost", "getBoost()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "customSpeed", "getCustomSpeed()Z", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees0", "getDegrees0()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees5", "getDegrees5()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees10", "getDegrees10()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees15", "getDegrees15()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees20", "getDegrees20()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees25", "getDegrees25()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees30", "getDegrees30()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees35", "getDegrees35()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees40", "getDegrees40()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees45", "getDegrees45()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees50", "getDegrees50()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees55", "getDegrees55()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees60", "getDegrees60()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees65", "getDegrees65()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees70", "getDegrees70()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees75", "getDegrees75()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees80", "getDegrees80()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees85", "getDegrees85()F", 0), new PropertyReference1Impl(MaceElytraTarget.class, "degrees90", "getDegrees90()F", 0) };
   @NotNull
   public static final MaceElytraTarget INSTANCE = new MaceElytraTarget();
   @NotNull
   private static final IntSetting height = SettingFactory.defaultImpl(INSTANCE, "height", 25, new IntRange(25, 60), null, null, 24, null);
   @NotNull
   private static final IntSetting elytraDelay = SettingFactory.defaultImpl(INSTANCE, "elytra-delay", 2, new IntRange(0, 40), null, null, 24, null);
   @NotNull
   private static final IntSetting fireworkDelay = SettingFactory.defaultImpl(INSTANCE, "firework-delay", 8, new IntRange(0, 40), null, null, 24, null);
   @NotNull
   private static final BooleanSetting offhandFirework = SettingFactory.defaultImpl(INSTANCE, "offhand-firework", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting altFireworkSwap = SettingFactory.defaultImpl(
      INSTANCE, "alt-firework-swap", false, null, () -> INSTANCE.getOffHandFirework(), 4, null
   );
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(INSTANCE, "pause-while-eating", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting minimizeClicks = SettingFactory.defaultImpl(INSTANCE, "minimize-clicks", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting peekAssist = SettingFactory.defaultImpl(INSTANCE, "peek-assist", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting windBurstSync = SettingFactory.defaultImpl(
      INSTANCE, "wind-burst-sync", true, null, () -> INSTANCE.getPeekAssist(), 4, null
   );
   @NotNull
   private static final BooleanSetting boost = SettingFactory.defaultImpl(INSTANCE, "boost", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting customSpeed = SettingFactory.defaultImpl(INSTANCE, "custom-speed", false, null, () -> INSTANCE.getBoost(), 4, null);
   @NotNull
   private static final FloatSetting degrees0 = SettingFactory.defaultImpl(
      INSTANCE, "0-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees5 = SettingFactory.defaultImpl(
      INSTANCE, "5-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees10 = SettingFactory.defaultImpl(
      INSTANCE, "10-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees15 = SettingFactory.defaultImpl(
      INSTANCE, "15-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees20 = SettingFactory.defaultImpl(
      INSTANCE, "20-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees25 = SettingFactory.defaultImpl(
      INSTANCE, "25-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees30 = SettingFactory.defaultImpl(
      INSTANCE, "30-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees35 = SettingFactory.defaultImpl(
      INSTANCE, "35-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees40 = SettingFactory.defaultImpl(
      INSTANCE, "40-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees45 = SettingFactory.defaultImpl(
      INSTANCE, "45-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees50 = SettingFactory.defaultImpl(
      INSTANCE, "50-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees55 = SettingFactory.defaultImpl(
      INSTANCE, "55-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees60 = SettingFactory.defaultImpl(
      INSTANCE, "60-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees65 = SettingFactory.defaultImpl(
      INSTANCE, "65-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees70 = SettingFactory.defaultImpl(
      INSTANCE, "70-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees75 = SettingFactory.defaultImpl(
      INSTANCE, "75-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees80 = SettingFactory.defaultImpl(
      INSTANCE, "80-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees85 = SettingFactory.defaultImpl(
      INSTANCE, "85-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees90 = SettingFactory.defaultImpl(
      INSTANCE, "90-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil2 = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil3 = new PlayerUtil();
   @NotNull
   private static HashMap map;
   private static boolean flag;
   private static int value;

   private MaceElytraTarget() {
      super("mace-elytra-target", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final int getHeight() {
      return ((Number)height.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final int getSwapDelay() {
      return ((Number)elytraDelay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final int getFireworkDelay() {
      return ((Number)fireworkDelay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getOffHandFirework() {
      return (Boolean)offhandFirework.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getAlternativeFireworkSwap() {
      return (Boolean)altFireworkSwap.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getPauseWhileEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getMinimizeClicks() {
      return (Boolean)minimizeClicks.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPeekAssist() {
      return (Boolean)peekAssist.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getWindBurstSync() {
      return (Boolean)windBurstSync.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getBoost() {
      return (Boolean)boost.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getCustomSpeed() {
      return (Boolean)customSpeed.computeObject(this, $$delegatedProperties[10]);
   }

   private final float getDegrees0() {
      return ((Number)degrees0.computeObject(this, $$delegatedProperties[11])).floatValue();
   }

   private final float getDegrees5() {
      return ((Number)degrees5.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final float getDegrees10() {
      return ((Number)degrees10.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   private final float getDegrees15() {
      return ((Number)degrees15.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   private final float getDegrees20() {
      return ((Number)degrees20.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final float getDegrees25() {
      return ((Number)degrees25.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   private final float getDegrees30() {
      return ((Number)degrees30.computeObject(this, $$delegatedProperties[17])).floatValue();
   }

   private final float getDegrees35() {
      return ((Number)degrees35.computeObject(this, $$delegatedProperties[18])).floatValue();
   }

   private final float getDegrees40() {
      return ((Number)degrees40.computeObject(this, $$delegatedProperties[19])).floatValue();
   }

   private final float getDegrees45() {
      return ((Number)degrees45.computeObject(this, $$delegatedProperties[20])).floatValue();
   }

   private final float getDegrees50() {
      return ((Number)degrees50.computeObject(this, $$delegatedProperties[21])).floatValue();
   }

   private final float getDegrees55() {
      return ((Number)degrees55.computeObject(this, $$delegatedProperties[22])).floatValue();
   }

   private final float getDegrees60() {
      return ((Number)degrees60.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   private final float getDegrees65() {
      return ((Number)degrees65.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   private final float getDegrees70() {
      return ((Number)degrees70.computeObject(this, $$delegatedProperties[25])).floatValue();
   }

   private final float getDegrees75() {
      return ((Number)degrees75.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   private final float getDegrees80() {
      return ((Number)degrees80.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   private final float getDegrees85() {
      return ((Number)degrees85.computeObject(this, $$delegatedProperties[28])).floatValue();
   }

   private final float getDegrees90() {
      return ((Number)degrees90.computeObject(this, $$delegatedProperties[29])).floatValue();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (value > 0) {
         int value2 = value;
         value = value2 + -1;
      } else {
         int value3 = InventoryManager.INSTANCE.getValue();
         InventoryManager instance = InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.FIREWORK_ROCKET};
         SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items);
         boolean flag2 = Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() != null;
         boolean flag3 = flag2
            && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(this.computeVec3(Aura.INSTANCE.getEntity())) < 4.0
            && MinecraftHolder.getLocalPlayer().getDeltaMovement().y < -1.0
            && AuraMovementHandler.INSTANCE.getValue() <= 0;
         if (value3 >= 0 && searchInvResult2.isFlag() && !MinecraftHolder.getLocalPlayer().isInWater() && !MinecraftHolder.getLocalPlayer().isInLava()) {
            if (!this.getPauseWhileEating()
               || !MinecraftHolder.getLocalPlayer().isUsingItem()
               || !MinecraftHolder.getLocalPlayer().getMainHandItem().getComponents().has(DataComponents.FOOD)
                  && !MinecraftHolder.getLocalPlayer().getMainHandItem().getComponents().has(DataComponents.POTION_CONTENTS)) {
               if (MinecraftHolder.getLocalPlayer().onGround()) {
                  if (!MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
                     MinecraftHolder.getMinecraft().options.keyJump.setDown(true);
                     TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 3, (byte)0, false, () -> {
                        if (!InputUtil.checkContains(32)) {
                           MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
                        }
                     }, 6, null);
                  }
               } else {
                  if (!flag3 && !MinecraftHolder.getLocalPlayer().isFallFlying() && playerUtil2.testValue2(this.getSwapDelay())) {
                     this.updateValue(value3);
                  }

                  if (this.getPeekAssist()) {
                     this.applyGetY();
                  }
               }
            }
         }
      }
   }

   private final void applyGetY() {
      if (Aura.INSTANCE.getEntity() != null) {
         Rotation rotation = Rotation.vectorUtil.computeRotation(Aura.INSTANCE.getEntity().getEyePosition().add(0.0, this.getHeight(), 0.0));
         boolean flag2 = MinecraftHolder.getLocalPlayer().getY() - Aura.INSTANCE.getEntity().getY() > this.getHeight();
         boolean flag3 = AuraMovementHandler.INSTANCE.getValue() > 0 || !playerUtil3.testValue(10);
         if (flag3) {
            if (!flag) {
               ElytraTarget.INSTANCE.applyGetItem(this.getOffHandFirework(), this.getAlternativeFireworkSwap());
            }

            flag = true;
         }

         if (flag2) {
            flag = false;
         }

         if (flag) {
            RotationManager.INSTANCE.updateRotation(rotation, 200);
         }
      }
   }

   private final void updateValue(int value) {
      GuiMove.INSTANCE.setFlag5(true);
      if (value >= 36) {
         this.applyStartFallFlying2(value - 36);
      } else {
         this.applyStartFallFlying(value);
      }

      GuiMove.INSTANCE.setFlag5(false);
   }

   private final void applyStartFallFlying(int value) {
      ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
      ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
      if (!this.getMinimizeClicks()) {
         ContainerUtil.defaultImpl(value, 0, null, true, 6, null);
      }

      ElytraSwap.INSTANCE.setFlag(true);
      MinecraftHolder.getLocalPlayer().startFallFlying();
      Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
      PacketUtil.applySend(serverboundPlayerCommandPacket);
      if (playerUtil.testValue2(this.getFireworkDelay())) {
         ElytraTarget.INSTANCE.applyGetItem(this.getOffHandFirework(), this.getAlternativeFireworkSwap());
      }

      if (!this.getMinimizeClicks()) {
         ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
      }

      ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
      serverboundPlayerCommandPacket = null;
      byte byteValue = 6;
      boolean flag2 = true;
      Object object = null;
      byte byteValue2 = 0;
      ContainerUtil.defaultImpl(value, byteValue2, (ContainerInput)object, flag2, byteValue, serverboundPlayerCommandPacket);
   }

   private final void applyStartFallFlying2(int value) {
      ContainerUtil.applyHandleContainerInput(6, value, ContainerInput.SWAP, true);
      ElytraSwap.INSTANCE.setFlag(true);
      MinecraftHolder.getLocalPlayer().startFallFlying();
      Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
      PacketUtil.applySend(serverboundPlayerCommandPacket);
      if (playerUtil.testValue2(this.getFireworkDelay())) {
         ElytraTarget.INSTANCE.applyGetItem(this.getOffHandFirework(), this.getAlternativeFireworkSwap());
      }

      boolean flag2 = true;
      ContainerInput swap = ContainerInput.SWAP;
      byte byteValue = 6;
      ContainerUtil.applyHandleContainerInput(byteValue, value, swap, flag2);
   }

   @Flow
   private final void onFireWorkVelocity(FireWorkVelocityEvent fireWorkVelocityEvent) {
      fireWorkVelocityEvent.cancel();
      Vec3 vector = FireWorkVectorEvent.INSTANCE.call(MinecraftHolder.getLocalPlayer().getLookAngle())
         ? FireWorkVectorEvent.INSTANCE.getVector()
         : MinecraftHolder.getLocalPlayer().getLookAngle();
      if (this.getCustomSpeed()) {
         this.update();
      }

      float degrees = Math.abs((float)Math.toDegrees(-Math.atan2(vector.y, Math.hypot(vector.x, vector.z))));
      float value2;
      if (!Blink.INSTANCE.getEnabled() && this.getBoost()) {
         Iterable entrySet = (this.getCustomSpeed() ? map : ElytraTarget.INSTANCE.getMap()).entrySet();
         Iterator iterator = entrySet.iterator();
         Object object;
         if (!iterator.hasNext()) {
            object = null;
         } else {
            Object object2 = iterator.next();
            if (!iterator.hasNext()) {
               object = object2;
            } else {
               Entry entry = (Entry)object2;
               float value3 = Math.abs(degrees - ((Number)entry.getKey()).floatValue());

               do {
                  Object object3 = iterator.next();
                  Entry entry2 = (Entry)object3;
                  float value4 = Math.abs(degrees - ((Number)entry2.getKey()).floatValue());
                  if (Float.compare(value3, value4) > 0) {
                     object2 = object3;
                     value3 = value4;
                  }
               } while (iterator.hasNext());

               object = object2;
            }
         }

         value2 = (Entry)object != null ? ((Number)((Entry)object).getValue()).floatValue() : 0.1F;
      } else {
         value2 = 0.1F;
      }

      float value5 = value2;
      float value6 = 1.5F;
      fireWorkVelocityEvent.setVelocity(
         MinecraftHolder.getLocalPlayer()
            .getDeltaMovement()
            .add(
               vector.x * value5 + (vector.x * value6 - MinecraftHolder.getLocalPlayer().getDeltaMovement().x) * 0.5,
               vector.y * value5 + (vector.y * value6 - MinecraftHolder.getLocalPlayer().getDeltaMovement().y) * 0.5,
               vector.z * value5 + (vector.z * value6 - MinecraftHolder.getLocalPlayer().getDeltaMovement().z) * 0.5
            )
      );
   }

   @Flow
   private final void onSetPose(SetPoseEvent setPoseEvent) {
      setPoseEvent.cancel();
      setPoseEvent.setPose(Pose.STANDING);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSoundPacket) {
         Holder<SoundEvent> sound = ((ClientboundSoundPacket)receivePacket.getPacket()).getSound();
         if (Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_ELYTRA)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_NETHERITE)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_DIAMOND)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GOLD)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_IRON)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GENERIC)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_CHAIN)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_LEATHER)) {
            receivePacket.cancel();
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundAttackPacket && this.isFlag()) {
         if (!Intrinsics.areEqual(Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)sendPacket.getPacket()), Aura.INSTANCE.getEntity())
            || WorldUtil.INSTANCE.getAmount3() < 3.0) {
            return;
         }

         value = 7;
      }
   }

   public final boolean isFlag() {
      return this.getEnabled()
         && this.getWindBurstSync()
         && EnchantmentUtil.checkContains(MinecraftHolder.getLocalPlayer().getMainHandItem(), Enchantments.WIND_BURST);
   }

   private final void update() {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.0F, this.getDegrees0()),
         TuplesKt.to(5.0F, this.getDegrees5()),
         TuplesKt.to(10.0F, this.getDegrees10()),
         TuplesKt.to(15.0F, this.getDegrees15()),
         TuplesKt.to(20.0F, this.getDegrees20()),
         TuplesKt.to(25.0F, this.getDegrees25()),
         TuplesKt.to(30.0F, this.getDegrees30()),
         TuplesKt.to(35.0F, this.getDegrees35()),
         TuplesKt.to(40.0F, this.getDegrees40()),
         TuplesKt.to(45.0F, this.getDegrees45()),
         TuplesKt.to(50.0F, this.getDegrees50()),
         TuplesKt.to(55.0F, this.getDegrees55()),
         TuplesKt.to(60.0F, this.getDegrees60()),
         TuplesKt.to(65.0F, this.getDegrees65()),
         TuplesKt.to(70.0F, this.getDegrees70()),
         TuplesKt.to(75.0F, this.getDegrees75()),
         TuplesKt.to(80.0F, this.getDegrees80()),
         TuplesKt.to(85.0F, this.getDegrees85()),
         TuplesKt.to(90.0F, this.getDegrees90())
      };
      map = MapsKt.hashMapOf(pairs);
   }

   static {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.0F, 0.155F),
         TuplesKt.to(5.0F, 0.16F),
         TuplesKt.to(10.0F, 0.19F),
         TuplesKt.to(15.0F, 0.22F),
         TuplesKt.to(20.0F, 0.22F),
         TuplesKt.to(25.0F, 0.27F),
         TuplesKt.to(30.0F, 0.28F),
         TuplesKt.to(35.0F, 0.34F),
         TuplesKt.to(40.0F, 0.37F),
         TuplesKt.to(45.0F, 0.37F),
         TuplesKt.to(50.0F, 0.34F),
         TuplesKt.to(55.0F, 0.34F),
         TuplesKt.to(60.0F, 0.2F),
         TuplesKt.to(65.0F, 0.18F),
         TuplesKt.to(70.0F, 0.16F),
         TuplesKt.to(75.0F, 0.16F),
         TuplesKt.to(80.0F, 0.13F),
         TuplesKt.to(85.0F, 0.13F),
         TuplesKt.to(90.0F, 0.13F)
      };
      map = MapsKt.hashMapOf(pairs);
   }
}