// deobf by pusia
package su.catlean.client.features.modules.basic;

import net.minecraft.network.PacketListener;
import java.util.LinkedList;
import java.util.Queue;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.event.events.player.EventPostPlayerTravel;
import su.catlean.api.event.events.player.FallFlyingEvent;
import su.catlean.api.event.events.player.JumpEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.player.ShouldStopSprintingEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.KeyMappingAccessor;
import su.catlean.mixins.accessors.LivingEntityAccessor;
import su.catlean.mixins.accessors.LocalPlayerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class ElytraRecast extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(ElytraRecast.class, "grim", "getGrim()Z", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "tunnelsOffset", "getTunnelsOffset()Z", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "bypass", "getBypass()Lsu/catlean/i8;", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "grimDurability", "getGrimDurability()Z", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "changePitch", "getChangePitch()Z", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "pitchValue", "getPitchValue()F", 0), new MutablePropertyReference1Impl(ElytraRecast.class, "allowBroken", "getAllowBroken()Z", 0) };
   @NotNull
   public static final ElytraRecast INSTANCE = new ElytraRecast();
   @NotNull
   private static final BooleanSetting grim = SettingFactory.defaultImpl(INSTANCE, "grim", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting tunnelsOffset = SettingFactory.defaultImpl(INSTANCE, "tunnels-offset", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting bypass = SettingFactory.defaultImpl(INSTANCE, "bypass", ElytraRecast.Bypass.SEMI, null, () -> INSTANCE.getGrim(), 4, null);
   @NotNull
   private static final BooleanSetting grimDurability = SettingFactory.defaultImpl(INSTANCE, "grim-durability", true, null, () -> INSTANCE.getGrim(), 4, null);
   @NotNull
   private static final BooleanSetting changePitch = SettingFactory.defaultImpl(INSTANCE, "change-pitch", true, null, () -> !INSTANCE.getGrim(), 4, null);
   @NotNull
   private static final FloatSetting pitch = SettingFactory.defaultImpl(
      INSTANCE, "pitch", 87.0F, RangesKt.rangeTo(0.0F, 90.0F), null, () -> INSTANCE.getChangePitch() && !INSTANCE.getGrim(), 8, null
   );
   @NotNull
   private static final BooleanSetting allowBroken = SettingFactory.defaultImpl(INSTANCE, "allow-broken", true, null, () -> !INSTANCE.getGrim(), 4, null);
   private static float amount;
   private static float amount2;
   private static float amount3;
   private static float amount4;
   private static float amount5;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;
   private static int value;
   @NotNull
   private static final Queue<Packet<? extends PacketListener>> queue = new LinkedList();
   @NotNull
   private static final Queue<Packet<? extends PacketListener>> queue2 = new LinkedList();
   @NotNull
   private static final LinkedList<Packet<? extends PacketListener>> linkedList = new LinkedList();
   @NotNull
   private static final LinkedList<ServerboundMovePlayerPacket> linkedList2 = new LinkedList();
   private static boolean flag5 = true;
   private static double amount6;

   private ElytraRecast() {
      super("elytra-recast", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final boolean getGrim() {
      return (Boolean)grim.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getTunnelsOffset() {
      return (Boolean)tunnelsOffset.computeObject(this, $$delegatedProperties[1]);
   }

   private final ElytraRecast.Bypass getBypass() {
      return (ElytraRecast.Bypass)bypass.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getGrimDurability() {
      return (Boolean)grimDurability.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getChangePitch() {
      return (Boolean)changePitch.computeObject(this, $$delegatedProperties[4]);
   }

   private final float getPitchValue() {
      return ((Number)pitch.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final boolean getAllowBroken() {
      return (Boolean)allowBroken.computeObject(this, $$delegatedProperties[6]);
   }

   @Override
   public void update2() {
      flag4 = false;
      value = 0;
      if (MinecraftHolder.getMinecraft().player != null) {
         amount5 = MinecraftHolder.getLocalPlayer().getYRot();
      }

      linkedList.clear();
      linkedList2.clear();
   }

   @Flow(
      priority = -10
   )
   private final void onPreElytra(PreElytraEvent preElytraEvent) {
      amount2 = MinecraftHolder.getLocalPlayer().getYRot();
      if ((this.getChangePitch() || this.getGrim()) && MinecraftHolder.getLocalPlayer().isFallFlying()) {
         amount = MinecraftHolder.getLocalPlayer().getXRot();
         MinecraftHolder.getLocalPlayer().setXRot(this.getPitchValue());
         if (this.getGrim()) {
            MinecraftHolder.getLocalPlayer().setYRot(amount5);
         }
      }
   }

   @Flow(
      priority = -10
   )
   private final void onAfterElytra(AfterElytraEvent afterElytraEvent) {
      if ((this.getChangePitch() || this.getGrim()) && MinecraftHolder.getLocalPlayer().isFallFlying()) {
         MinecraftHolder.getLocalPlayer().setXRot(amount);
         MinecraftHolder.getLocalPlayer().setYRot(amount2);
      }
   }

   @Flow
   public final void onPlayerTravel(EventPlayerTravel e) {
      if (this.getGrim() && this.isFlag6()) {
         amount3 = MinecraftHolder.getLocalPlayer().getYRot();
         amount4 = MinecraftHolder.getLocalPlayer().getXRot();
         MinecraftHolder.getLocalPlayer().setXRot(this.getPitchValue());
         MinecraftHolder.getLocalPlayer().setYRot(amount5);
      }
   }

   @Flow
   public final void onPostPlayerTravel(EventPostPlayerTravel e) {
      if (this.getGrim() && this.isFlag6()) {
         MinecraftHolder.getLocalPlayer().setYRot(amount3);
         MinecraftHolder.getLocalPlayer().setXRot(amount4);
      }
   }

   @Flow(
      priority = -10
   )
   private final void onJump(JumpEvent jumpEvent) {
      if (this.getGrim()) {
         jumpEvent.setYaw(amount5);
         jumpEvent.cancel();
      }
   }

   @Flow
   private final void onFallFlying(FallFlyingEvent fallFlyingEvent) {
      if (this.getGrim()) {
         if (flag) {
            fallFlyingEvent.setValue(true);
            fallFlyingEvent.cancel();
         }
      } else {
         boolean value2 = fallFlyingEvent.getValue();
         if (flag && !value2) {
            fallFlyingEvent.setValue(this.isFlag());
            fallFlyingEvent.cancel();
         }

         flag = value2;
      }
   }

   @Flow
   private final void onShouldStopSprinting(ShouldStopSprintingEvent shouldStopSprintingEvent) {
      if (this.isFlag2()) {
         shouldStopSprintingEvent.cancel();
      }
   }

   @Flow
   private final void onSetPose(SetPoseEvent setPoseEvent) {
      if (this.isFlag2() && this.getGrimDurability() && this.getGrim()) {
         setPoseEvent.cancel();
         setPoseEvent.setPose(Pose.STANDING);
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getChangePitch() || this.getGrim()) {
         RotationManager.INSTANCE
            .updateRotation(
               new Rotation(this.getGrim() ? amount5 : MinecraftHolder.getLocalPlayer().getYRot(), this.getPitchValue(), false, null, 12, null), 201
            );
      }

      if (value > 0) {
         int value2 = value;
         value = value2 + -1;
         MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
         MinecraftHolder.getMinecraft().options.keyUp.setDown(false);
         MinecraftHolder.getMinecraft().options.keySprint.setDown(false);
         this.applyIsEmpty();
         this.applyHandle();
         this.applyIsEmpty2();
         this.applyGetYRot();
         amount5 = MinecraftHolder.getLocalPlayer().getYRot();
         flag = false;
         flag2 = false;
         if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
            MinecraftHolder.getLocalPlayer().stopFallFlying();
         }
      } else if (!this.isFlag2()) {
         this.applyIsEmpty();
         this.applyHandle();
         flag = false;
         flag3 = false;
      } else {
         MinecraftHolder.getLocalPlayer().setSprinting(true);
         MinecraftHolder.getMinecraft().options.keyJump.setDown(true);
         MinecraftHolder.getMinecraft().options.keyUp.setDown(true);
         MinecraftHolder.getMinecraft().options.keySprint.setDown(true);
         float amount32 = WorldUtil.INSTANCE.getAmount3() > 1.0 ? 0.0F : (float)WorldUtil.INSTANCE.getAmount3();
         if (MinecraftHolder.getLocalPlayer().onGround()) {
            flag3 = false;
         }

         if (amount32 > 0.0F) {
            if (!flag3 && this.isFlag3()) {
               if (queue2.size() > 20) {
                  this.applyHandle();
                  MinecraftHolder.getLocalPlayer().stopFallFlying();
                  flag = false;
               }

               this.applyIsEmpty();
               flag3 = true;
            }

            if (!MinecraftHolder.getLocalPlayer().isFallFlying() && !flag && this.isFlag()) {
               flag = true;
            }
         }

         ((LivingEntityAccessor)MinecraftHolder.getLocalPlayer()).setLastJumpCooldown(0);
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket) {
         flag = false;
         flag2 = false;
         value = 60;
         this.applyIsEmpty();
         this.applyHandle();
         MinecraftHolder.getLocalPlayer().stopFallFlying();
      }

      if (receivePacket.getPacket() instanceof ClientboundSetEntityDataPacket
         && MinecraftHolder.getLocalPlayer().isFallFlying()
         && flag
         && this.getGrimDurability()) {
         receivePacket.cancel();
      }

      if (receivePacket.getPacket() instanceof ClientboundPingPacket && MinecraftHolder.getLocalPlayer().isFallFlying() && flag && this.isFlag3()) {
         receivePacket.cancel();
         queue2.add(receivePacket.getPacket());
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (this.getBypass() == ElytraRecast.Bypass.FULL && MinecraftHolder.getLocalPlayer().isFallFlying() && flag && !flag4 && this.isFlag3()) {
         sendPacket.cancel();
         queue.add(sendPacket.getPacket());
      }

      if (!this.isFlag3() && !flag4) {
         double lastBaseY = ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastBaseY();
         boolean flag6 = !flag5 && this.computeAmount(lastBaseY) >= 0.4;
         if (sendPacket.getPacket() instanceof ServerboundPongPacket) {
            if (flag && MinecraftHolder.getLocalPlayer().isFallFlying()) {
               if (!MinecraftHolder.getClientLevel().noCollision(MinecraftHolder.getLocalPlayer().getBoundingBox().move(0.0, -1.0, 0.0))
                  && (flag5 && this.computeAmount(lastBaseY) < 0.8 || !flag5 && this.computeAmount(lastBaseY) < 0.4)) {
                  linkedList.add(sendPacket.getPacket());
                  sendPacket.cancel();
               }

               if (flag6) {
                  this.applyGetYRot();
               }
            } else {
               this.applyGetYRot();
            }
         }

         if (sendPacket.getPacket() instanceof ServerboundMovePlayerPacket) {
            ServerboundMovePlayerPacket packet = (ServerboundMovePlayerPacket)sendPacket.getPacket();
            if (flag && MinecraftHolder.getLocalPlayer().isFallFlying()) {
               if (packet.getY(amount6) < amount6) {
                  flag5 = true;
               } else if (packet.getY(amount6) > amount6) {
                  flag5 = false;
               }

               amount6 = packet.getY(amount6);
               if (!MinecraftHolder.getClientLevel().noCollision(MinecraftHolder.getLocalPlayer().getBoundingBox().move(0.0, -1.0, 0.0))
                  && (flag5 && this.computeAmount(packet.getY(lastBaseY)) < 0.8 || !flag5 && this.computeAmount(lastBaseY) < 0.4)) {
                  linkedList2.add(packet);
                  sendPacket.cancel();
               }

               if (flag6) {
                  this.applyIsEmpty2();
               }
            } else {
               this.applyIsEmpty2();
            }
         }
      }
   }

   private final double computeAmount(double amount) {
      return amount - Math.floor(amount);
   }

   @Override
   public void update4() {
      if (!InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyUp).getKey().getValue())) {
         MinecraftHolder.getMinecraft().options.keyUp.setDown(false);
      }

      if (!InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyJump).getKey().getValue())) {
         MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
      }

      flag = false;
      flag2 = false;
      this.applyIsEmpty();
      this.applyHandle();
      this.applyIsEmpty2();
      this.applyGetYRot();
   }

   private final boolean isFlag() {
      if (this.isFlag2()) {
         int value2 = InventoryManager.INSTANCE.getValue();
         if (value2 != -2 && value2 != -1 && this.getGrimDurability() && this.getGrim()) {
            ContainerUtil.defaultImpl(value2, 0, null, false, 14, null);
            ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
         }

         ElytraSwap.INSTANCE.setFlag(true);
         MinecraftHolder.getLocalPlayer().startFallFlying();
         Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
         PacketUtil.applySend(serverboundPlayerCommandPacket);
         if (value2 != -2 && value2 != -1 && this.getGrimDurability() && this.getGrim()) {
            ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
            ContainerUtil.defaultImpl(value2, 0, null, true, 6, null);
         }

         if (value2 == -2 && this.getGrimDurability() && this.getGrim()) {
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items = new Item[]{
               Items.NETHERITE_CHESTPLATE,
               Items.DIAMOND_CHESTPLATE,
               Items.IRON_CHESTPLATE,
               Items.GOLDEN_CHESTPLATE,
               Items.LEATHER_CHESTPLATE,
               Items.CHAINMAIL_CHESTPLATE
            };
            SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items);
            if (searchInvResult2.isFlag()) {
               ContainerUtil.defaultImpl(searchInvResult2.getValue(), 0, null, false, 14, null);
               ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
               ContainerUtil.defaultImpl(searchInvResult2.getValue(), 0, null, true, 6, null);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private final boolean isFlag2() {
      if (!MinecraftHolder.getLocalPlayer().getAbilities().flying
         && !MinecraftHolder.getLocalPlayer().isPassenger()
         && !MinecraftHolder.getLocalPlayer().isInWater()
         && !MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.LEVITATION)
         && MinecraftHolder.getLocalPlayer().getFoodData().getFoodLevel() > 6.0F) {
         ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
         return itemBySlot.is(Items.ELYTRA) && (this.checkGetDamageValue(itemBySlot) || this.getAllowBroken() && !this.getGrim())
            || this.getGrimDurability() && InventoryManager.INSTANCE.getValue() != -1;
      } else {
         return false;
      }
   }

   public final boolean checkGetDamageValue(ItemStack s) {
      return s.getDamageValue() < s.getMaxDamage() - 1 && s.has(DataComponents.GLIDER);
   }

   private final boolean isFlag3() {
      return this.getGrimDurability() || this.getTunnelsOffset();
   }

   private final void applyIsEmpty() {
      flag4 = true;

      while (!queue.isEmpty()) {
         Packet poll = (Packet)queue.poll();
         PacketUtil.applySend(poll);
      }

      flag4 = false;
   }

   private final void applyHandle() {
      if (MinecraftHolder.getMinecraft().isSameThread()) {
         while (!queue2.isEmpty()) {
            ((ClientboundPingPacket)queue2.poll()).handle((ClientCommonPacketListener)MinecraftHolder.getClientPacketListener());
         }
      }
   }

   private final void applyGetYRot() {
      flag4 = true;

      while (!linkedList.isEmpty()) {
         Packet poll = (Packet)linkedList.poll();
         PacketUtil.applySend(poll);
      }

      flag4 = false;
      amount5 = MinecraftHolder.getLocalPlayer().getYRot();
   }

   private final void applyIsEmpty2() {
      flag4 = true;

      while (!linkedList2.isEmpty()) {
         Packet poll = (Packet)linkedList2.poll();
         PacketUtil.applySend(poll);
      }

      flag4 = false;
   }

   static {
   }

   public static enum Bypass {
      SEMI,
      FULL;

      private static final ElytraRecast.Bypass[] bypasss = $values();
      private static final EnumEntries<ElytraRecast.Bypass> enumEntries = EnumEntriesKt.enumEntries(bypasss);

      private Bypass() {
      }

      @NotNull
      public static EnumEntries<ElytraRecast.Bypass> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraRecast.Bypass[] $values() {
         return new ElytraRecast.Bypass[]{SEMI, FULL};
      }
   }
}