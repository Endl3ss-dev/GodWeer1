// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.sounds.SoundEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.SwitchMode;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVectorEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.TickService;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyFirework implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyFirework INSTANCE = new ElytraFlyFirework();
   private static float amount;
   private static float amount2;
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil2 = new PlayerUtil();
   @NotNull
   private static HashMap map;

   private ElytraFlyFirework() {
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
   }

   @Override
   public void onPreElytra(PreElytraEvent e) {
      amount = MinecraftHolder.getLocalPlayer().getYRot();
      amount2 = MinecraftHolder.getLocalPlayer().getXRot();
      MinecraftHolder.getLocalPlayer().setYRot(this.getAmount());
      if (!ElytraPlus.INSTANCE.getPitchControl()) {
         MinecraftHolder.getLocalPlayer().setXRot(0.0F);
      }

      MinecraftHolder.getLocalPlayer().setXRot(this.getAmount2());
   }

   @Override
   public void onAfterElytra(AfterElytraEvent e) {
      MinecraftHolder.getLocalPlayer().setYRot(amount);
      MinecraftHolder.getLocalPlayer().setXRot(amount2);
   }

   @Override
   public void onMove(MoveEvent e) {
   }

   @Override
   public void onAfterSendPacket(AfterSendPacket event) {
   }

   @Override
   public void onReceivePacket(ReceivePacket e) {
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent e) {
      if (!this.isFlag()) {
         if (ElytraPlus.INSTANCE.getStopMove() == ElytraPlus.StopMove.BLOCK) {
            e.cancel();
            return;
         }

         if (ElytraPlus.INSTANCE.getStopMove() == ElytraPlus.StopMove.TIMER) {
            TickService.INSTANCE.setAmount2(0.05F);
         }
      }

      int value = InventoryManager.INSTANCE.getValue();
      InventoryManager instance = InventoryManager.INSTANCE;
      Item[] items = new Item[]{Items.FIREWORK_ROCKET};
      SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items);
      if (value >= 0 && searchInvResult2.isFlag() && !MinecraftHolder.getLocalPlayer().isInWater() && !MinecraftHolder.getLocalPlayer().isInLava()) {
         if (!ElytraPlus.INSTANCE.getPauseWhileEating()
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
               if (!MinecraftHolder.getLocalPlayer().isFallFlying() && playerUtil2.testValue2(ElytraPlus.INSTANCE.getSwapDelay())) {
                  if (value >= 36) {
                     GuiMove.INSTANCE.setFlag5(true);
                     int value2 = value - 36;
                     ContainerUtil.applyHandleContainerInput(6, value2, ContainerInput.SWAP, true);
                     ElytraSwap.INSTANCE.setFlag(true);
                     MinecraftHolder.getLocalPlayer().startFallFlying();
                     Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                     PacketUtil.applySend(serverboundPlayerCommandPacket);
                     if (playerUtil.testValue2(ElytraPlus.INSTANCE.getFireworkDelay())) {
                        ElytraTarget.INSTANCE.applyGetItem(ElytraPlus.INSTANCE.getOffHandFirework(), ElytraPlus.INSTANCE.getAlternativeFireworkSwap());
                     }

                     boolean flag = true;
                     ContainerInput swap = ContainerInput.SWAP;
                     byte byteValue = 6;
                     ContainerUtil.applyHandleContainerInput(byteValue, value2, swap, flag);
                     GuiMove.INSTANCE.setFlag5(false);
                  } else {
                     GuiMove.INSTANCE.setFlag5(true);
                     ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
                     Packet serverboundPlayerCommandPacket2 = null;
                     byte byteValue2 = 14;
                     boolean flag2 = false;
                     Object object = null;
                     byte byteValue3 = 0;
                     byte byteValue4 = 6;
                     ContainerUtil.defaultImpl(byteValue4, byteValue3, (ContainerInput)object, flag2, byteValue2, serverboundPlayerCommandPacket2);
                     if (!ElytraPlus.INSTANCE.getMinimizeClicks()) {
                        ContainerUtil.defaultImpl(value, 0, null, true, 6, null);
                     }

                     ElytraSwap.INSTANCE.setFlag(true);
                     MinecraftHolder.getLocalPlayer().startFallFlying();
                     serverboundPlayerCommandPacket2 = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                     PacketUtil.applySend((Packet)serverboundPlayerCommandPacket2);
                     if (playerUtil.testValue2(ElytraPlus.INSTANCE.getFireworkDelay())) {
                        ElytraTarget.INSTANCE.applyGetItem(ElytraPlus.INSTANCE.getOffHandFirework(), ElytraPlus.INSTANCE.getAlternativeFireworkSwap());
                     }

                     if (!ElytraPlus.INSTANCE.getMinimizeClicks()) {
                        ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
                     }

                     ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
                     serverboundPlayerCommandPacket2 = null;
                     byteValue2 = 6;
                     flag2 = true;
                     object = null;
                     byteValue3 = 0;
                     ContainerUtil.defaultImpl(value, byteValue3, (ContainerInput)object, flag2, byteValue2, serverboundPlayerCommandPacket2);
                     GuiMove.INSTANCE.setFlag5(false);
                  }
               }

               RotationManager.INSTANCE.updateRotation(new Rotation(this.getAmount(), this.getAmount2(), false, SwitchMode.FORCE, 4, null), 0);
            }
         }
      }
   }

   @Override
   public void update() {
   }

   @Override
   public void update2() {
   }

   private final boolean isFlag() {
      return MinecraftHolder.getMinecraft().options.keyUp.isDown()
         || MinecraftHolder.getMinecraft().options.keyLeft.isDown()
         || MinecraftHolder.getMinecraft().options.keyRight.isDown()
         || MinecraftHolder.getMinecraft().options.keyDown.isDown()
         || MinecraftHolder.getMinecraft().options.keyJump.isDown()
         || MinecraftHolder.getMinecraft().options.keyShift.isDown();
   }

   private final float getAmount() {
      return WorldUtil.INSTANCE.getAmount6();
   }

   private final float getAmount2() {
      return MinecraftHolder.getMinecraft().options.keyJump.isDown()
         ? -90.0F
         : (
            MinecraftHolder.getMinecraft().options.keyShift.isDown()
               ? 90.0F
               : (!ElytraPlus.INSTANCE.getPitchControl() ? 0.0F : MinecraftHolder.getLocalPlayer().getXRot())
         );
   }

   @Override
   public void onFireWorkVelocity(FireWorkVelocityEvent e) {
      e.cancel();
      Vec3 vector = FireWorkVectorEvent.INSTANCE.call(MinecraftHolder.getLocalPlayer().getLookAngle())
         ? FireWorkVectorEvent.INSTANCE.getVector()
         : MinecraftHolder.getLocalPlayer().getLookAngle();
      if (ElytraPlus.INSTANCE.getCustomSpeed()) {
         this.update3();
      }

      float degrees = Math.abs((float)Math.toDegrees(-Math.atan2(vector.y, Math.hypot(vector.x, vector.z))));
      float value;
      if (!Blink.INSTANCE.getEnabled() && ElytraPlus.INSTANCE.getBoost()) {
         Iterable entrySet = (ElytraPlus.INSTANCE.getCustomSpeed() ? map : ElytraTarget.INSTANCE.getMap()).entrySet();
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
               float value2 = Math.abs(degrees - ((Number)entry.getKey()).floatValue());

               do {
                  Object object3 = iterator.next();
                  Entry entry2 = (Entry)object3;
                  float value3 = Math.abs(degrees - ((Number)entry2.getKey()).floatValue());
                  if (Float.compare(value2, value3) > 0) {
                     object2 = object3;
                     value2 = value3;
                  }
               } while (iterator.hasNext());

               object = object2;
            }
         }

         value = (Entry)object != null ? ((Number)((Entry)object).getValue()).floatValue() : 0.1F;
      } else {
         value = 0.1F;
      }

      float value4 = value;
      float value5 = 1.5F;
      e.setVelocity(
         MinecraftHolder.getLocalPlayer()
            .getDeltaMovement()
            .add(
               vector.x * value4 + (vector.x * value5 - MinecraftHolder.getLocalPlayer().getDeltaMovement().x) * 0.5,
               vector.y * value4 + (vector.y * value5 - MinecraftHolder.getLocalPlayer().getDeltaMovement().y) * 0.5,
               vector.z * value4 + (vector.z * value5 - MinecraftHolder.getLocalPlayer().getDeltaMovement().z) * 0.5
            )
      );
   }

   @Override
   public void onSetPose(SetPoseEvent e) {
      e.cancel();
      e.setPose(Pose.STANDING);
   }

   @Override
   public void onReceivePacket2(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundSoundPacket) {
         Holder<SoundEvent> sound = ((ClientboundSoundPacket)e.getPacket()).getSound();
         if (Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_ELYTRA)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_NETHERITE)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_DIAMOND)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GOLD)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_IRON)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GENERIC)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_CHAIN)
            || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_LEATHER)) {
            e.cancel();
         }
      }
   }

   private final void update3() {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.0F, ElytraPlus.INSTANCE.getDegrees0()),
         TuplesKt.to(5.0F, ElytraPlus.INSTANCE.getDegrees5()),
         TuplesKt.to(10.0F, ElytraPlus.INSTANCE.getDegrees10()),
         TuplesKt.to(15.0F, ElytraPlus.INSTANCE.getDegrees15()),
         TuplesKt.to(20.0F, ElytraPlus.INSTANCE.getDegrees20()),
         TuplesKt.to(25.0F, ElytraPlus.INSTANCE.getDegrees25()),
         TuplesKt.to(30.0F, ElytraPlus.INSTANCE.getDegrees30()),
         TuplesKt.to(35.0F, ElytraPlus.INSTANCE.getDegrees35()),
         TuplesKt.to(40.0F, ElytraPlus.INSTANCE.getDegrees40()),
         TuplesKt.to(45.0F, ElytraPlus.INSTANCE.getDegrees45()),
         TuplesKt.to(50.0F, ElytraPlus.INSTANCE.getDegrees50()),
         TuplesKt.to(55.0F, ElytraPlus.INSTANCE.getDegrees55()),
         TuplesKt.to(60.0F, ElytraPlus.INSTANCE.getDegrees60()),
         TuplesKt.to(65.0F, ElytraPlus.INSTANCE.getDegrees65()),
         TuplesKt.to(70.0F, ElytraPlus.INSTANCE.getDegrees70()),
         TuplesKt.to(75.0F, ElytraPlus.INSTANCE.getDegrees75()),
         TuplesKt.to(80.0F, ElytraPlus.INSTANCE.getDegrees80()),
         TuplesKt.to(85.0F, ElytraPlus.INSTANCE.getDegrees85()),
         TuplesKt.to(90.0F, ElytraPlus.INSTANCE.getDegrees90())
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