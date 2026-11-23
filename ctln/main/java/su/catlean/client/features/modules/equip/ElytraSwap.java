// deobf by pusia
package su.catlean.client.features.modules.equip;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.ElytraRecast;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;
import su.catlean.client.services.InventoryManager;

public final class ElytraSwap extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ElytraSwap.class, "mode", "getMode()Lsu/catlean/fx;", 0), new PropertyReference1Impl(ElytraSwap.class, "switchButton", "getSwitchButton()Lsu/catlean/dd;", 0), new PropertyReference1Impl(ElytraSwap.class, "fireWorkButton", "getFireWorkButton()Lsu/catlean/dd;", 0), new PropertyReference1Impl(ElytraSwap.class, "startFireWork", "getStartFireWork()Z", 0), new PropertyReference1Impl(ElytraSwap.class, "fireWorkMode", "getFireWorkMode()Lsu/catlean/i6;", 0), new PropertyReference1Impl(ElytraSwap.class, "swapMode", "getSwapMode()Lsu/catlean/nz;", 0), new PropertyReference1Impl(ElytraSwap.class, "hotBarSwapMode", "getHotBarSwapMode()Lsu/catlean/np;", 0), new PropertyReference1Impl(ElytraSwap.class, "swapOnLand", "getSwapOnLand()Z", 0) };
   @NotNull
   public static final ElytraSwap INSTANCE = new ElytraSwap();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", ElytraSwap.Mode.ENABLE, null, null, 12, null);
   @NotNull
   private static final BindSetting switchButton = SettingFactory.defaultImpl(
      INSTANCE, "switch-button", new Bind(0, false, false, 7, null), null, () -> INSTANCE.getMode() == ElytraSwap.Mode.BIND, 4, null
   );
   @NotNull
   private static final BindSetting fireWorkButton = SettingFactory.defaultImpl(
      INSTANCE, "fire-work-button", new Bind(0, false, false, 7, null), null, () -> INSTANCE.getMode() == ElytraSwap.Mode.BIND, 4, null
   );
   @NotNull
   private static final BooleanSetting startFireWork = SettingFactory.defaultImpl(
      INSTANCE, "start-fire-work", false, null, () -> INSTANCE.getMode() == ElytraSwap.Mode.BIND, 4, null
   );
   @NotNull
   private static final EnumSetting fireWorkMode = SettingFactory.defaultImpl(
      INSTANCE, "fire-work-mode", ElytraSwap.FireWorkMode.SILENT, null, () -> INSTANCE.getMode() == ElytraSwap.Mode.BIND, 4, null
   );
   @NotNull
   private static final EnumSetting swapMode = SettingFactory.defaultImpl(INSTANCE, "swap-mode", ElytraSwap.SwapMode.ALT, null, null, 12, null);
   @NotNull
   private static final EnumSetting hotbarSwapMode = SettingFactory.defaultImpl(
      INSTANCE, "hotbar-swap-mode", ElytraSwap.HotbarSwapMode.DEFAULT, null, null, 12, null
   );
   @NotNull
   private static final BooleanSetting swapOnLand = SettingFactory.defaultImpl(
      INSTANCE, "swap-on-land", false, null, () -> INSTANCE.getMode() == ElytraSwap.Mode.BIND, 4, null
   );
   private static boolean flag;
   private static boolean flag2;
   private static int value;
   @NotNull
   private static final Item[] items;

   private ElytraSwap() {
      super("elytra-swap", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final ElytraSwap.Mode getMode() {
      return (ElytraSwap.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Bind getSwitchButton() {
      return (Bind)switchButton.computeObject(this, $$delegatedProperties[1]);
   }

   private final Bind getFireWorkButton() {
      return (Bind)fireWorkButton.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getStartFireWork() {
      return (Boolean)startFireWork.computeObject(this, $$delegatedProperties[3]);
   }

   private final ElytraSwap.FireWorkMode getFireWorkMode() {
      return (ElytraSwap.FireWorkMode)fireWorkMode.computeObject(this, $$delegatedProperties[4]);
   }

   private final ElytraSwap.SwapMode getSwapMode() {
      return (ElytraSwap.SwapMode)swapMode.computeObject(this, $$delegatedProperties[5]);
   }

   private final ElytraSwap.HotbarSwapMode getHotBarSwapMode() {
      return (ElytraSwap.HotbarSwapMode)hotbarSwapMode.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getSwapOnLand() {
      return (Boolean)swapOnLand.computeObject(this, $$delegatedProperties[7]);
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ElytraSwap.flag = flag;
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null && this.getMode() == ElytraSwap.Mode.ENABLE) {
         Object object = null;
         byte byteValue = 2;
         boolean flag3 = false;
         boolean flag4 = true;
         defaultImpl(this, flag4, flag3, byteValue, object);
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getSwapOnLand()) {
         if (WorldUtil.INSTANCE.getValue() > 15) {
            flag2 = true;
         }

         if (!MinecraftHolder.getLocalPlayer().isFallFlying() && flag2) {
            if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)) {
               this.updateFlag(false, true);
            }

            flag2 = false;
         }
      }
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      if (afterSendPacket.getPacket() instanceof ServerboundPlayerCommandPacket
         && ((ServerboundPlayerCommandPacket)afterSendPacket.getPacket()).getAction() == Action.START_FALL_FLYING) {
         if (this.getMode() == ElytraSwap.Mode.BIND && this.getStartFireWork() && !flag) {
            this.update();
         }

         flag = false;
      }
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (this.getMode() == ElytraSwap.Mode.BIND && MinecraftHolder.getMinecraft().gui.screen() == null && inputEvent.getAction() == InputEvent.Action.Press) {
         if (inputEvent.getKey() == this.getSwitchButton().getValue()) {
            defaultImpl(this, false, false, 2, null);
         }

         if (this.getFireWorkButton().getValue() != -1
            && inputEvent.getKey() == this.getFireWorkButton().getValue()
            && MinecraftHolder.getLocalPlayer().isFallFlying()) {
            this.update();
         }
      }
   }

   private final void update() {
      TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 0, (byte)-10, false, () -> {
         InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
         SearchInvResult searchInvResult = instance.computeSearchInvResult(items2);
         instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items3 = new Item[]{Items.FIREWORK_ROCKET};
         SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items3);
         su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory();
         if (searchInvResult.isFlag()) {
            searchInvResult.update();
         } else {
            if (!searchInvResult2.isFlag()) {
               ChatUtil.defaultImpl(INSTANCE, ChatUtil.computeText(INSTANCE, "no-fireworks", new Object[0], true), false, 2, null);
               return;
            }

            int value2 = searchInvResult2.getValue();
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            boolean flag3 = true;
            ContainerInput swap = ContainerInput.SWAP;
            int value3 = selectedSlot;
            int value4 = value2;
            ContainerUtil.applyHandleContainerInput(value4, value3, swap, flag3);
         }

         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
         PacketUtil.applySend(serverboundSwingPacket);
         if (INSTANCE.getFireWorkMode() == ElytraSwap.FireWorkMode.SILENT) {
            su.catlean.client.services.InventoryManager.INSTANCE.update();
            if (!searchInvResult.isFlag()) {
               int value5 = searchInvResult2.getValue();
               int selectedSlot2 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
               boolean flag4 = true;
               ContainerInput swap2 = ContainerInput.SWAP;
               int value6 = selectedSlot2;
               int value7 = value5;
               ContainerUtil.applyHandleContainerInput(value7, value6, swap2, flag4);
            }
         }
      }, 5, null);
   }

   public final int getValue() {
      Item[] items2 = items;
      int value2 = 0;

      for (int i = items2.length; value2 < i; value2++) {
         Item item = items2[value2];
         InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items3 = new Item[]{item};
         SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items3);
         if (searchInvResult2.isFlag()) {
            return searchInvResult2.getValue();
         }
      }

      return -1;
   }

   private final int getValue2() {
      int value2 = -1;
      int value3 = -1;

      for (int i = 0; i < 37; i++) {
         ItemStack item;
         label34: {
            LocalPlayer player = MinecraftHolder.getMinecraft().player;
            if (player != null) {
               Inventory inventory = player.getInventory();
               if (inventory != null) {
                  item = inventory.getItem(i);
                  break label34;
               }
            }

            item = null;
         }

         ItemStack itemStack = item;
         if (itemStack != null && Intrinsics.areEqual(itemStack.getItem(), Items.ELYTRA) && ElytraRecast.INSTANCE.checkGetDamageValue(itemStack)) {
            int value4 = 0;
            ItemEnchantments enchantmentsForCrafting = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

            for (Object keySetItem : enchantmentsForCrafting.keySet()) {
               Holder holder = (Holder)keySetItem;
               if (holder != null) {
                  value4 += enchantmentsForCrafting.getLevel(holder);
               }
            }

            if (value4 > value3) {
               value3 = value4;
               value2 = i < 9 ? i + 36 : i;
            }
         }
      }

      return value2;
   }

   private final void updateFlag(boolean flag, boolean flag2) {
      TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 0, (byte)-10, false, () -> {
         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)) {
            int value2 = INSTANCE.getValue();
            if (value2 != -1) {
               Module instance = INSTANCE;
               String defaultImpl = ChatUtil.defaultImpl(INSTANCE, "done-chestplate", new Object[0], false, 4, null);
               MessageType success = MessageType.SUCCESS;
               ItemStack itemStack = MinecraftHolder.getLocalPlayer().getInventory().getItem(value2 >= 36 ? value2 - 36 : value2).copy();
               Object object = null;
               byte byteValue = 4;
               ItemStack itemStack2 = itemStack;
               byte byteValue2 = 0;
               MessageType messageType = success;
               ChatUtil.defaultImpl(instance, defaultImpl, messageType, byteValue2, itemStack2, byteValue, object);
               INSTANCE.updateValue(value2, false);
               NotificationManager.INSTANCE.setFlag(true);
               if (flag) {
                  INSTANCE.applyUnplug();
               }

               NotificationManager.INSTANCE.setFlag(false);
            } else if (flag) {
               INSTANCE.updateText10(ChatUtil.defaultImpl(INSTANCE, "no-chestplate", new Object[0], false, 4, null));
            } else {
               ChatUtil.defaultImpl(INSTANCE, ChatUtil.defaultImpl(INSTANCE, "no-chestplate", new Object[0], false, 4, null), false, 2, null);
            }
         } else if (!flag2) {
            int value22 = INSTANCE.getValue2();
            if (value22 != -1) {
               Module instance2 = INSTANCE;
               String defaultImpl2 = ChatUtil.defaultImpl(INSTANCE, "done-elytra", new Object[0], false, 4, null);
               MessageType success2 = MessageType.SUCCESS;
               ItemStack itemStack3 = MinecraftHolder.getLocalPlayer().getInventory().getItem(value22 >= 36 ? value22 - 36 : value22).copy();
               Object object2 = null;
               byte byteValue3 = 4;
               ItemStack itemStack4 = itemStack3;
               byte byteValue4 = 0;
               MessageType messageType2 = success2;
               ChatUtil.defaultImpl(instance2, defaultImpl2, messageType2, byteValue4, itemStack4, byteValue3, object2);
               INSTANCE.updateValue(value22, true);
               NotificationManager.INSTANCE.setFlag(true);
               if (flag) {
                  INSTANCE.applyUnplug();
               }

               NotificationManager.INSTANCE.setFlag(false);
            } else if (flag) {
               INSTANCE.updateText10(ChatUtil.computeText(INSTANCE, "no-elytra", new Object[0], true));
            } else {
               ChatUtil.defaultImpl(INSTANCE, ChatUtil.computeText(INSTANCE, "no-elytra", new Object[0], true), false, 2, null);
            }
         }
      }, 5, null);
   }

   public static void defaultImpl(ElytraSwap elytraSwap, boolean flag, boolean flag2, int value, Object object) {
      if ((value & 2) != 0) {
         flag2 = false;
      }

      elytraSwap.updateFlag(flag, flag2);
   }

   private final void updateValue(int value, boolean flag) {
      if (36 <= value ? value < 45 : false) {
         if (this.getHotBarSwapMode() == ElytraSwap.HotbarSwapMode.DEFAULT) {
            ContainerUtil.applyHandleContainerInput(6, value - 36, ContainerInput.SWAP, true);
         } else {
            su.catlean.client.services.InventoryManager.defaultImpl(su.catlean.client.services.InventoryManager.INSTANCE, value - 36, 0, 2, null);
         }
      } else if (this.getSwapMode() == ElytraSwap.SwapMode.DEFAULT) {
         ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
         ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
         ContainerUtil.defaultImpl(value, 0, null, true, 6, null);
      } else {
         ContainerUtil.defaultImpl(value, 7, ContainerInput.SWAP, false, 8, null);
         ContainerUtil.defaultImpl(6, 7, ContainerInput.SWAP, false, 8, null);
         ContainerUtil.applyHandleContainerInput(value, 7, ContainerInput.SWAP, true);
      }

      if (flag && this.getStartFireWork()) {
         ElytraSwap.value = MinecraftHolder.getLocalPlayer().tickCount;
         this.update3();
      }
   }

   private final void update3() {
      TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, -1, (byte)-10, false, () -> {
         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)) {
            if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
               MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
            } else {
               MinecraftHolder.getMinecraft().options.keyJump.setDown((MinecraftHolder.getLocalPlayer().tickCount - value) % 2 == 0);
               INSTANCE.update3();
            }
         } else if (MinecraftHolder.getLocalPlayer().onGround()) {
            MinecraftHolder.getMinecraft().options.keyJump.setDown(true);
            INSTANCE.update3();
         }
      }, 4, null);
   }

   static {
      Item[] items2 = new Item[]{
         Items.NETHERITE_CHESTPLATE,
         Items.DIAMOND_CHESTPLATE,
         Items.CHAINMAIL_CHESTPLATE,
         Items.IRON_CHESTPLATE,
         Items.GOLDEN_CHESTPLATE,
         Items.LEATHER_CHESTPLATE
      };
      items = items2;
   }

   public static enum FireWorkMode {
      SILENT,
      NORMAL;

      private static final ElytraSwap.FireWorkMode[] fireWorkModes = $values();
      private static final EnumEntries<ElytraSwap.FireWorkMode> enumEntries = EnumEntriesKt.enumEntries(fireWorkModes);

      private FireWorkMode() {
      }

      @NotNull
      public static EnumEntries<ElytraSwap.FireWorkMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraSwap.FireWorkMode[] $values() {
         return new ElytraSwap.FireWorkMode[]{SILENT, NORMAL};
      }
   }

   public static enum HotbarSwapMode {
      DEFAULT,
      V1_17;

      private static final ElytraSwap.HotbarSwapMode[] hotbarSwapModes = $values();
      private static final EnumEntries<ElytraSwap.HotbarSwapMode> enumEntries = EnumEntriesKt.enumEntries(hotbarSwapModes);

      private HotbarSwapMode() {
      }

      @NotNull
      public static EnumEntries<ElytraSwap.HotbarSwapMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraSwap.HotbarSwapMode[] $values() {
         return new ElytraSwap.HotbarSwapMode[]{DEFAULT, V1_17};
      }
   }

   public static enum Mode {
      ENABLE,
      BIND;

      private static final ElytraSwap.Mode[] modes = $values();
      private static final EnumEntries<ElytraSwap.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<ElytraSwap.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraSwap.Mode[] $values() {
         return new ElytraSwap.Mode[]{ENABLE, BIND};
      }
   }

   public static enum SwapMode {
      DEFAULT,
      ALT;

      private static final ElytraSwap.SwapMode[] swapModes = $values();
      private static final EnumEntries<ElytraSwap.SwapMode> enumEntries = EnumEntriesKt.enumEntries(swapModes);

      private SwapMode() {
      }

      @NotNull
      public static EnumEntries<ElytraSwap.SwapMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ElytraSwap.SwapMode[] $values() {
         return new ElytraSwap.SwapMode[]{DEFAULT, ALT};
      }
   }
}