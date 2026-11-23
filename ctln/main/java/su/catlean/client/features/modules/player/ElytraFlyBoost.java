// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.LocalizationManager;
import su.catlean.mixins.accessors.ClientInputAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.EntityPositionUtil;
import su.catlean.utils.world.WorldUtil;

public final class ElytraFlyBoost implements ElytraFlyStrategy {
   @NotNull
   public static final ElytraFlyBoost INSTANCE = new ElytraFlyBoost();
   private static boolean flag;
   private static boolean flag2;
   private static float amount;
   @Nullable
   private static ItemStack itemStack;
   @Nullable
   private static ItemStack itemStack2;
   @NotNull
   private static Item item = Items.AIR;
   @NotNull
   private static Item item2 = Items.AIR;
   private static int value = -1;
   private static int value2 = -1;
   private static long time;

   private ElytraFlyBoost() {
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ElytraFlyBoost.flag = flag;
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
      ElytraFlyBoost.amount = amount;
   }

   @Nullable
   public final ItemStack getItemStack() {
      return itemStack;
   }

   public final void setItemStack(ItemStack itemStack) {
      ElytraFlyBoost.itemStack = itemStack;
   }

   @Nullable
   public final ItemStack getItemStack2() {
      return itemStack2;
   }

   public final void setItemStack2(ItemStack itemStack) {
      itemStack2 = itemStack;
   }

   @NotNull
   public final Item getItem() {
      return item;
   }

   @NotNull
   public final Item getItem2() {
      return item2;
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      ElytraFlyBoost.value = value;
   }

   public final int getValue2() {
      return value2;
   }

   public final void setValue2(int value) {
      value2 = value;
   }

   public final long getTime() {
      return time;
   }

   public final void setTime(long time) {
      ElytraFlyBoost.time = time;
   }

   @Override
   public void onPreSync(PreSyncEvent e) {
      if (ElytraPlus.INSTANCE.getRotate()) {
         if (MinecraftHolder.getMinecraft().options.keyJump.isDown() && MinecraftHolder.getLocalPlayer().isFallFlying() && flag) {
            MinecraftHolder.getLocalPlayer().setXRot(-45.0F);
         }

         if (MinecraftHolder.getMinecraft().options.keyShift.isDown() && MinecraftHolder.getLocalPlayer().isFallFlying() && flag) {
            MinecraftHolder.getLocalPlayer().setXRot(45.0F);
         }

         MinecraftHolder.getLocalPlayer().setYRot(WorldUtil.INSTANCE.getAmount6());
      }

      if (!WorldUtil.INSTANCE.isFlag4() && MinecraftHolder.getMinecraft().options.keyJump.isDown() && MinecraftHolder.getLocalPlayer().isFallFlying() && flag) {
         MinecraftHolder.getLocalPlayer().setXRot(-90.0F);
      }

      if (WorldUtil.INSTANCE.getValue() < 5 && !MinecraftHolder.getLocalPlayer().onGround()) {
         MinecraftHolder.getLocalPlayer().setXRot(-45.0F);
      }
   }

   @Override
   public void onPreElytra(PreElytraEvent e) {
      if (WorldUtil.INSTANCE.getValue() < 4) {
         ElytraPlus.INSTANCE.setAmount3(MinecraftHolder.getLocalPlayer().getXRot());
         MinecraftHolder.getLocalPlayer().setXRot(-45.0F);
      }
   }

   @Override
   public void onAfterElytra(AfterElytraEvent e) {
      if (WorldUtil.INSTANCE.getValue() < 4) {
         MinecraftHolder.getLocalPlayer().setXRot(ElytraPlus.INSTANCE.getAmount3());
      }
   }

   @Override
   public void onMove(MoveEvent e) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying() && flag) {
         if (MinecraftHolder.getLocalPlayer().horizontalCollision || MinecraftHolder.getLocalPlayer().verticalCollision) {
            ElytraPlus.INSTANCE.setAmount(0.0F);
            amount = 0.0F;
         }

         if (WorldUtil.INSTANCE.getValue() < 4) {
            e.setY(0.2);
            e.cancel();
            return;
         }

         if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
            double ySpeed = ElytraPlus.INSTANCE.getYSpeed();
            byte byteValue = 9;
            double value3 = ySpeed;
            amount += byteValue;
            float amount2 = amount;
            double value4 = amount2 / 100.0F;
            double value5 = 1.0;
            e.setY(value3 * Math.min(value4, value5));
         } else if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
            double value6 = -ElytraPlus.INSTANCE.getYSpeed();
            byte byteValue2 = 9;
            double value7 = value6;
            amount += byteValue2;
            float amount3 = amount;
            double value8 = amount3 / 100.0F;
            double value9 = 1.0;
            e.setY(value7 * Math.min(value8, value9));
         } else {
            switch (ElytraFlyBoost.WhenMappings.values[ElytraPlus.INSTANCE.getAntiKick().ordinal()]) {
               case 1:
                  e.setY(MinecraftHolder.getLocalPlayer().tickCount % 2 == 0 ? 0.08 : -0.08);
                  break;
               case 2:
                  e.setY(-0.08);
                  break;
               case 3:
                  e.setY(0.0);
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         }

         if (!WorldUtil.INSTANCE.isFlag4()) {
            ElytraPlus.INSTANCE.setAmount(0.0F);
         }

         if (MinecraftHolder.getLocalPlayer().input.getMoveVector().x > 0.0F) {
            ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
               .setMoveVector(new Vec2(1.0F, MinecraftHolder.getLocalPlayer().input.getMoveVector().y));
         } else if (MinecraftHolder.getLocalPlayer().input.getMoveVector().x < 0.0F) {
            ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
               .setMoveVector(new Vec2(-1.0F, MinecraftHolder.getLocalPlayer().input.getMoveVector().y));
         }

         WorldUtil instance = WorldUtil.INSTANCE;
         double xzSpeed = ElytraPlus.INSTANCE.getXzSpeed();
         byte byteValue3 = 9;
         double value10 = xzSpeed;
         WorldUtil worldUtil = instance;
         ElytraPlus.INSTANCE.setAmount(ElytraPlus.INSTANCE.getAmount() + byteValue3);
         float amount4 = ElytraPlus.INSTANCE.getAmount();
         double value11 = amount4 / 100.0F;
         double value12 = 1.0;
         worldUtil.onMove(e, value10 * Math.min(value11, value12));
         if (ElytraPlus.INSTANCE.getStayMad() && !this.isFlag5() && WorldUtil.INSTANCE.getValue() > 10) {
            e.setY(0.42);
         }

         e.cancel();
      }
   }

   @Override
   public void onAfterSendPacket(AfterSendPacket event) {
      if (event.getPacket() instanceof ServerboundPlayerCommandPacket
         && ElytraPlus.INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK
         && ((ServerboundPlayerCommandPacket)event.getPacket()).getAction() == Action.START_FALL_FLYING) {
         this.applyCurrentTimeMillis(false);
      }
   }

   private final void applyCurrentTimeMillis(boolean flag) {
      if (!flag || !((float)(System.currentTimeMillis() - time) < ElytraPlus.INSTANCE.getFireDelay() * 1000.0F)) {
         if (!flag || MinecraftHolder.getLocalPlayer().isFallFlying()) {
            if (flag || WorldUtil.INSTANCE.getValue() <= 1) {
               int value3 = this.getValue3();
               if (value3 == -1) {
                  value2 = -1;
               } else {
                  value2 = value3;
                  boolean areEqual = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET);
                  int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  if (!areEqual && selectedSlot != value3) {
                     InventoryManager.INSTANCE.applyGetInventory2(value3);
                  }

                  ContainerUtil.defaultImpl(areEqual ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                  if (!areEqual && selectedSlot != value3) {
                     InventoryManager.INSTANCE.applyGetInventory2(selectedSlot);
                  }

                  ElytraFlyBoost.flag = true;
                  time = System.currentTimeMillis();
               }
            }
         }
      }
   }

   @Override
   public void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundPlayerPositionPacket) {
         ElytraPlus.INSTANCE.setAmount(0.0F);
         amount = 0.0F;
         if (ElytraPlus.INSTANCE.getDisableOnFlag() && ElytraPlus.INSTANCE.getMode() == ElytraPlus.Mode.FIRE_WORK) {
            ElytraPlus.INSTANCE.applyLength(LocalizationManager.INSTANCE.computeText("modules.common.flag-disable", new Object[0]));
         }
      }
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent e) {
      boolean isEmptyBlock = MinecraftHolder.getClientLevel()
         .isEmptyBlock(BlockPos.containing((Position)EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer())));
      boolean flag3 = this.isFlag6() && isEmptyBlock && MinecraftHolder.getLocalPlayer().getDeltaMovement().y() < 0.0;
      if ((!(WorldUtil.INSTANCE.getAmount3() > 0.0) || !isEmptyBlock) && !flag3) {
         if (MinecraftHolder.getLocalPlayer().onGround()) {
            flag2 = false;
            return;
         }
      } else {
         this.applyGetItem();
      }

      if (!WorldUtil.INSTANCE.isFlag4()) {
         ElytraPlus.INSTANCE.setAmount(0.0F);
      }

      if (this.isFlag3()) {
         if (!MinecraftHolder.getLocalPlayer().isFallFlying() && !flag2 && MinecraftHolder.getLocalPlayer().getDeltaMovement().y() < 0.0) {
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            flag2 = true;
         }

         if (WorldUtil.INSTANCE.getValue() < 4) {
            MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
         }

         this.applyCurrentTimeMillis(true);
      }
   }

   @Override
   public void update() {
      amount = 0.0F;
      value2 = -1;
      item2 = Items.AIR;
      itemStack2 = null;
      flag = false;
      if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem(), Items.ELYTRA)
         && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().containerMenu.getCarried().getItem(), Items.ELYTRA)
         && InventoryManager.INSTANCE.getValue() == -1) {
         this.update4();
      } else if (this.computeValue(false) == -1) {
         this.update3();
      } else if (this.computeValue(true) == -1) {
         itemStack2 = MinecraftHolder.getLocalPlayer().getInventory().getItem(ElytraPlus.INSTANCE.getFireSlot() - 1).copy();
         item2 = MinecraftHolder.getLocalPlayer().getInventory().getItem(ElytraPlus.INSTANCE.getFireSlot() - 1).getItem();
      }
   }

   @Override
   public void update2() {
      flag2 = false;
      if (!ElytraPlus.INSTANCE.getKeepFlying()) {
         MinecraftHolder.getLocalPlayer().setDeltaMovement(0.0, MinecraftHolder.getLocalPlayer().getDeltaMovement().y(), 0.0);
         new Thread(() -> {
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            INSTANCE.update5();
            value2 = -1;
            item2 = Items.AIR;
            itemStack2 = null;
            INSTANCE.applyGetItem2();
            INSTANCE.update6();
         }).start();
      }
   }

   @Override
   public void onFireWorkVelocity(FireWorkVelocityEvent e) {
   }

   @Override
   public void onSetPose(SetPoseEvent e) {
   }

   @Override
   public void onReceivePacket2(ReceivePacket e) {
   }

   private final int computeValue(boolean flag) {
      int value3;
      if (flag) {
         InventoryManager instance = InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.FIREWORK_ROCKET};
         value3 = instance.computeSearchInvResult(items).getValue();
      } else {
         InventoryManager instance2 = InventoryManager.INSTANCE;
         Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
         value3 = instance2.computeSearchInvResult2(items2).getValue();
      }

      return value3;
   }

   private final void update3() {
      ElytraPlus.INSTANCE.applyLength(LocalizationManager.INSTANCE.computeText("modules.common.no-fireworks", new Object[0]));
      flag = false;
   }

   private final void update4() {
      ElytraPlus.INSTANCE.applyLength(LocalizationManager.INSTANCE.computeText("modules.common.no-elytra", new Object[0]));
      flag = false;
   }

   private final void updateValue(int value) {
      ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
      ContainerUtil.defaultImpl(ElytraPlus.INSTANCE.getFireSlot() - 1 + 36, 0, null, false, 14, null);
      ContainerUtil.defaultImpl(value, 0, null, true, 6, null);
   }

   private final void update5() {
      if (value2 != -1 && itemStack2 != null && !Intrinsics.areEqual(item2, Items.FIREWORK_ROCKET) && !Intrinsics.areEqual(item2, Items.AIR)) {
         int item3 = this.fetchItem(itemStack2, item2);
         item3 = item3 < 9 && item3 != -1 ? item3 + 36 : item3;
         ContainerUtil.defaultImpl(item3, 0, null, false, 14, null);
         ContainerUtil.defaultImpl(ElytraPlus.INSTANCE.getFireSlot() - 1 + 36, 0, null, false, 14, null);
         ContainerUtil.defaultImpl(item3, 0, null, false, 14, null);
      }
   }

   private final void update6() {
      value = -1;
      item = Items.AIR;
      itemStack = null;
   }

   private final int fetchItem(ItemStack itemStack, Item item) {
      if (itemStack == null) {
         return -1;
      } else {
         for (int i = 0; i < 45; i++) {
            ItemStack item3 = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
            if (ItemStack.isSameItem(item3, itemStack) && Intrinsics.areEqual(item3.getItem(), item)) {
               return i;
            }
         }

         return -1;
      }
   }

   private final int getValue3() {
      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)) {
         return -2;
      } else {
         int value3 = this.computeValue(true);
         int value4 = this.computeValue(false);
         if (value4 == -1) {
            this.update3();
            return -1;
         } else if (value3 == -1) {
            if (!ElytraPlus.INSTANCE.getAllowFireSwap()) {
               ElytraPlus.INSTANCE.applyLength(LocalizationManager.INSTANCE.computeText("modules.common.no-fireworks", new Object[0]));
               return ElytraPlus.INSTANCE.getFireSlot() - 1;
            } else {
               this.updateValue(value4);
               return ElytraPlus.INSTANCE.getFireSlot() - 1;
            }
         } else {
            return value3;
         }
      }
   }

   private final boolean isFlag3() {
      return this.isFlag4() ? false : this.getValue3() != -1;
   }

   private final boolean isFlag4() {
      ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
      return !Intrinsics.areEqual(itemBySlot.getItem(), Items.ELYTRA) || !ElytraPlus.INSTANCE.checkGetDamageValue(itemBySlot);
   }

   private final void applyGetItem() {
      int value3 = InventoryManager.INSTANCE.getValue();
      if (value3 == -1 && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().containerMenu.getCarried().getItem(), Items.ELYTRA)) {
         this.update4();
      } else if (this.isFlag4()) {
         if (value == -1) {
            ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
            value = value3;
            item = itemBySlot.getItem();
            itemStack = itemBySlot.copy();
         }

         ContainerUtil.defaultImpl(value3, 0, null, false, 14, null);
         ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
         if (value != -1) {
            ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
         }

         ElytraPlus.INSTANCE.setFlag2(true);
      }
   }

   private final void applyGetItem2() {
      if (value != -1 && !Intrinsics.areEqual(item, Items.AIR)) {
         if (!ElytraPlus.INSTANCE.isFlag2()) {
            return;
         }

         ItemStack item3 = MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
         boolean flag3 = !Intrinsics.areEqual(item3, ItemStack.EMPTY) && !ItemStack.isSameItem(item3, itemStack);
         int item4 = this.fetchItem(itemStack, item);
         item4 = item4 < 9 && item4 != -1 ? item4 + 36 : item4;
         if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().containerMenu.getCarried().getItem(), Items.AIR)) {
            ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
            if (value != -1) {
               Object object = null;
               byte byteValue = 14;
               boolean flag4 = false;
               Object object2 = null;
               byte byteValue2 = 0;
               int value3 = value;
               ContainerUtil.defaultImpl(value3, byteValue2, (ContainerInput)object2, flag4, byteValue, object);
            }

            return;
         }

         if (item4 == -1) {
            return;
         }

         ContainerUtil.defaultImpl(item4, 0, null, false, 14, null);
         Object object3 = null;
         byte byteValue3 = 14;
         boolean flag5 = false;
         Object object4 = null;
         byte byteValue4 = 0;
         byte byteValue5 = 6;
         ContainerUtil.defaultImpl(byteValue5, byteValue4, (ContainerInput)object4, flag5, byteValue3, object3);
         if (!flag3) {
            ContainerUtil.defaultImpl(item4, 0, null, false, 14, null);
         } else {
            int value4 = this.getValue4();
            if (value4 != -1) {
               ContainerUtil.defaultImpl(value4, 0, null, false, 14, null);
            }
         }
      }

      this.update6();
   }

   private final int getValue4() {
      for (int i = 9; i < 45; i++) {
         if (MinecraftHolder.getLocalPlayer().getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public final boolean isFlag5() {
      return MinecraftHolder.getLocalPlayer().getY() < 0.0
         ? false
         : !MinecraftHolder.getClientLevel()
            .getBlockCollisions((Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().move(0.0, -3.0, 0.0))
            .iterator()
            .hasNext();
   }

   public final boolean isFlag6() {
      return MinecraftHolder.getClientLevel()
         .getBlockState(
            BlockPos.containing(MinecraftHolder.getLocalPlayer().getX(), MinecraftHolder.getLocalPlayer().getY() - 0.1, MinecraftHolder.getLocalPlayer().getZ())
         )
         .getBlock() instanceof LiquidBlock;
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[ElytraPlus.AntiKick.values().length];

         try {
            values2[ElytraPlus.AntiKick.Jitter.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[ElytraPlus.AntiKick.Glide.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[ElytraPlus.AntiKick.Off.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}