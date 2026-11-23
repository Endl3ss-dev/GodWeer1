// deobf by pusia
package su.catlean.client.services;

import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.InteractionManagerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.inventory.ItemPredicate;
import su.catlean.utils.inventory.ItemTypeUtil;
import su.catlean.utils.network.PacketUtil;

public final class InventoryManager implements Service {
   @NotNull
   public static final InventoryManager INSTANCE = new InventoryManager();
   private static int value = -1;
   private static int value2 = -1;

   private InventoryManager() {
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundSetCarriedItemPacket) {
         value = ((ServerboundSetCarriedItemPacket)sendPacket.getPacket()).getSlot();
      }
   }

   public final int fetchItem(Item item, boolean flag) {
      int value3 = 0;

      for (int i = 0; i < 45; i++) {
         ItemStack item2 = MinecraftHolder.getMinecraft().player.getInventory().getItem(i);
         if (Intrinsics.areEqual(item2.getItem(), item)) {
            value3 += item2.getCount();
         }
      }

      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().containerMenu.getCarried().getItem(), item)) {
         value2++;
      }

      if (flag && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), item)) {
         value3 += MinecraftHolder.getLocalPlayer().getOffhandItem().getCount();
      }

      return value3;
   }

   public static int defaultImpl(InventoryManager inventoryManager, Item item, boolean flag, int value, Object object) {
      if ((value & 2) != 0) {
         flag = false;
      }

      return inventoryManager.fetchItem(item, flag);
   }

   public final int fetchItem(Item item) {
      int value3 = 0;
      int value4 = 0;

      for (int i = MinecraftHolder.getLocalPlayer().containerMenu.slots.size(); value4 < i; value4++) {
         ItemStack item2 = MinecraftHolder.getLocalPlayer().containerMenu.getSlot(value4).getItem();
         if (Intrinsics.areEqual(item2.getItem(), item)) {
            value3++;
         }
      }

      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().containerMenu.getCarried().getItem(), item)) {
         value3++;
      }

      return value3;
   }

   @NotNull
   public final SearchInvResult fetchInventory(ItemPredicate searcher) {
      for (int i = 0; i < 9; i++) {
         ItemStack item;
         label23: {
            LocalPlayer player = MinecraftHolder.getMinecraft().player;
            if (player != null) {
               Inventory inventory = player.getInventory();
               if (inventory != null) {
                  item = inventory.getItem(i);
                  break label23;
               }
            }

            item = null;
         }

         ItemStack itemStack = item;
         if (itemStack != null && searcher.checkGetItem(itemStack)) {
            return new SearchInvResult(i, true, itemStack);
         }
      }

      return SearchInvResult.searchInvResultCompanion.getSearchInvResult();
   }

   @NotNull
   public final SearchInvResult fetchInventory2(ItemPredicate searcher) {
      for (int i = 36; i >= 0; i--) {
         ItemStack item;
         label27: {
            LocalPlayer player = MinecraftHolder.getMinecraft().player;
            if (player != null) {
               Inventory inventory = player.getInventory();
               if (inventory != null) {
                  item = inventory.getItem(i);
                  break label27;
               }
            }

            item = null;
         }

         ItemStack itemStack = item;
         if (itemStack != null && searcher.checkGetItem(itemStack)) {
            if (i < 9) {
               i += 36;
            }

            return new SearchInvResult(i, true, itemStack);
         }
      }

      return SearchInvResult.searchInvResultCompanion.getSearchInvResult();
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult(List items) {
      return this.fetchInventory(arg -> items.contains(arg.getItem()));
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult(Item[] items) {
      return this.fetchInventory(arg -> ArraysKt.contains(items, arg.getItem()));
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult2(List items) {
      return this.fetchInventory2(arg -> items.contains(arg.getItem()));
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult2(Item[] items) {
      return this.fetchInventory2(arg -> ArraysKt.contains(items, arg.getItem()));
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult(Block[] blocks) {
      Block[] blocks2 = blocks;
      var arrayList = new ArrayList(blocks.length);
      int value3 = 0;

      for (int i = blocks.length; value3 < i; value3++) {
         Block block = blocks2[value3];
         arrayList.add(block.asItem());
      }

      return this.computeSearchInvResult(arrayList);
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult3(List blocks) {
      Iterable blocks2 = blocks;
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks2, 10));

      for (Object object : blocks2) {
         Block block = (Block)object;
         arrayList.add(block.asItem());
      }

      return this.computeSearchInvResult2(arrayList);
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult2(Block[] blocks) {
      Block[] blocks2 = blocks;
      var arrayList = new ArrayList(blocks.length);
      int value3 = 0;

      for (int i = blocks.length; value3 < i; value3++) {
         Block block = blocks2[value3];
         arrayList.add(block.asItem());
      }

      return this.computeSearchInvResult2(arrayList);
   }

   public final void applyGetInventory() {
      value2 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
   }

   public final void update() {
      if (value2 != -1) {
         this.applyGetInventory2(value2);
      }

      value2 = -1;
   }

   public final void updateValue(int value) {
      this.applyGetInventory();
      this.applyGetInventory2(value);
   }

   @NotNull
   public final SearchInvResult computeSearchInvResult(ItemPredicate searcher, AutoSoup.Switch mode) {
      return switch (InventoryManager.WhenMappings.values[mode.ordinal()]) {
         case 1, 2 -> this.fetchInventory(searcher);
         case 3, 4 -> this.fetchInventory2(searcher);
         case 5, 6 -> {
            SearchInvResult inventory = this.fetchInventory(searcher);
            yield inventory.isFlag() ? inventory : this.fetchInventory2(searcher);
         }
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   public final boolean checkOrdinal(Item item, AutoSoup.Switch mode, Function0 action) {
      switch (InventoryManager.WhenMappings.values[mode.ordinal()]) {
         case 1:
         case 2:
            Item[] items = new Item[]{item};
            SearchInvResult searchInvResult = this.computeSearchInvResult(items);
            if (!searchInvResult.isFlag()) {
               return false;
            }

            this.applyGetInventory(searchInvResult.getValue(), mode, action);
            return true;
         case 3:
         case 4:
            Item[] items2 = new Item[]{item};
            SearchInvResult searchInvResult2 = this.computeSearchInvResult2(items2);
            if (!searchInvResult2.isFlag()) {
               return false;
            }

            this.applyGetInventory(searchInvResult2.getValue(), mode, action);
            return true;
         default:
            Item[] items3 = new Item[]{item};
            SearchInvResult searchInvResult3 = this.computeSearchInvResult(items3);
            if (searchInvResult3.isFlag()) {
               this.applyGetInventory(searchInvResult3.getValue(), mode, action);
               return true;
            } else {
               Item[] items4 = new Item[]{item};
               SearchInvResult searchInvResult22 = this.computeSearchInvResult2(items4);
               if (!searchInvResult22.isFlag()) {
                  return false;
               } else {
                  this.applyGetInventory(searchInvResult22.getValue(), mode, action);
                  return true;
               }
            }
      }
   }

   public final void applyGetInventory(int value, AutoSoup.Switch mode, Function0 action) {
      GuiMove.INSTANCE.setFlag5(true);
      switch (InventoryManager.WhenMappings.values[mode.ordinal()]) {
         case 1:
            if (MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() != value) {
               this.applyGetInventory2(value);
            }

            action.invoke();
            break;
         case 2:
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            if (InventoryManager.value != value) {
               this.applyGetInventory2(value);
            }

            action.invoke();
            if (InventoryManager.value != selectedSlot) {
               this.applyGetInventory2(selectedSlot);
            }
            break;
         case 3:
            MinecraftHolder.getMultiPlayerGameMode()
               .handleContainerInput(
                  MinecraftHolder.getLocalPlayer().containerMenu.containerId,
                  value,
                  MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
                  ContainerInput.SWAP,
                  (Player)MinecraftHolder.getLocalPlayer()
               );
            action.invoke();
            break;
         case 4:
            MinecraftHolder.getMultiPlayerGameMode()
               .handleContainerInput(
                  MinecraftHolder.getLocalPlayer().containerMenu.containerId,
                  value,
                  MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
                  ContainerInput.SWAP,
                  (Player)MinecraftHolder.getLocalPlayer()
               );
            action.invoke();
            MinecraftHolder.getMultiPlayerGameMode()
               .handleContainerInput(
                  MinecraftHolder.getLocalPlayer().containerMenu.containerId,
                  value,
                  MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
                  ContainerInput.SWAP,
                  (Player)MinecraftHolder.getLocalPlayer()
               );
            break;
         case 5:
            this.applyGetInventory(value, value >= 9 ? AutoSoup.Switch.INVENTORY_NORMAL : AutoSoup.Switch.NORMAL, action);
            break;
         case 6:
            this.applyGetInventory(value, value >= 9 ? AutoSoup.Switch.INVENTORY_SILENT : AutoSoup.Switch.SILENT, action);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      GuiMove.INSTANCE.setFlag5(false);
   }

   public final void applyGetInventory(int value) {
      MinecraftHolder.getMultiPlayerGameMode()
         .handleContainerInput(
            MinecraftHolder.getLocalPlayer().containerMenu.containerId,
            value < 9 ? value + 36 : value,
            MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
            ContainerInput.SWAP,
            (Player)MinecraftHolder.getLocalPlayer()
         );
   }

   public final void applyGetInventory2(int value) {
      if ((0 <= value ? value < 9 : false) && MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() != value) {
         MinecraftHolder.getLocalPlayer().getInventory().setSelectedSlot(value);
         ((InteractionManagerAccessor)MinecraftHolder.getMinecraft().gameMode).setCarriedIndex(value);
         this.applyGetConnection(value);
      }
   }

   public final void applyGetConnection(int value) {
      if (InventoryManager.value != value) {
         ClientPacketListener connection = MinecraftHolder.getMinecraft().getConnection();
         if (connection != null) {
            connection.send((Packet)(new ServerboundSetCarriedItemPacket(value)));
         }
      }
   }

   public final int getValue() {
      for (ItemStack inventoryItem : (Iterable<ItemStack>)InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer())) {
         if (Intrinsics.areEqual(inventoryItem.getItem(), Items.ELYTRA) && inventoryItem.getDamageValue() < 430) {
            return -2;
         }
      }

      int value3 = -1;

      for (int i = 0; i < 36; i++) {
         ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
         if (Intrinsics.areEqual(item.getItem(), Items.ELYTRA) && item.getDamageValue() < 430) {
            value3 = i;
            break;
         }
      }

      if (value3 < 9 && value3 != -1) {
         value3 += 36;
      }

      return value3;
   }

   @NotNull
   public final SearchInvResult getSearchInvResult() {
      if (MinecraftHolder.getMinecraft().player == null) {
         return SearchInvResult.searchInvResultCompanion.getSearchInvResult();
      } else {
         int value3 = -1;
         float value4 = 1.0F;

         for (int i = 0; i < 9; i++) {
            ItemStack item;
            label33: {
               LocalPlayer player = MinecraftHolder.getMinecraft().player;
               if (player != null) {
                  Inventory inventory = player.getInventory();
                  if (inventory != null) {
                     item = inventory.getItem(i);
                     break label33;
                  }
               }

               item = null;
            }

            ItemStack itemStack = item;
            if (itemStack != null && ItemTypeUtil.testItemStack(itemStack)) {
               float value5 = ((Number)itemStack.getItem().components().get(DataComponents.MAX_DAMAGE)).intValue();
               value5 += EnchantmentHelper.getItemEnchantmentLevel(
                  (Holder)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(Enchantments.SHARPNESS.registryKey()).get())
                     .get(Enchantments.SHARPNESS.identifier())
                     .get(),
                  (ItemInstance)itemStack
               );
               if (value5 > value4) {
                  value4 = value5;
                  value3 = i;
               }
            }
         }

         return value3 == -1
            ? SearchInvResult.searchInvResultCompanion.getSearchInvResult()
            : new SearchInvResult(value3, true, MinecraftHolder.getLocalPlayer().getInventory().getItem(value3));
      }
   }

   @NotNull
   public final SearchInvResult getSearchInvResult2() {
      if (MinecraftHolder.getMinecraft().player == null) {
         return SearchInvResult.searchInvResultCompanion.getSearchInvResult();
      } else {
         ItemStack mainHandItem = MinecraftHolder.getMinecraft().player.getMainHandItem();
         return !ItemTypeUtil.testItemStack(mainHandItem)
               && !ItemTypeUtil.testItemStack2(mainHandItem)
               && !(mainHandItem.getItem() instanceof AxeItem)
               && !(mainHandItem.getItem() instanceof ShovelItem)
            ? this.fetchInventory(
               arg -> ItemTypeUtil.testItemStack(arg)
                  || ItemTypeUtil.testItemStack2(arg)
                  || arg.getItem() instanceof AxeItem
                  || arg.getItem() instanceof ShovelItem
            )
            : new SearchInvResult(
               MinecraftHolder.getMinecraft().player.getInventory().getSelectedSlot(), true, MinecraftHolder.getMinecraft().player.getMainHandItem()
            );
      }
   }

   @NotNull
   public final SearchInvResult getSearchInvResult3() {
      if (MinecraftHolder.getMinecraft().player == null) {
         return SearchInvResult.searchInvResultCompanion.getSearchInvResult();
      } else {
         int value3 = -1;
         float value4 = 1.0F;

         for (int i = 0; i < 9; i++) {
            ItemStack item;
            label38: {
               LocalPlayer player = MinecraftHolder.getMinecraft().player;
               if (player != null) {
                  Inventory inventory = player.getInventory();
                  if (inventory != null) {
                     item = inventory.getItem(i);
                     break label38;
                  }
               }

               item = null;
            }

            ItemStack itemStack = item;
            if (itemStack != null && itemStack.getItem() instanceof AxeItem) {
               AxeItem item2 = (AxeItem)itemStack.getItem();
               Integer maX_DAMAGE = (Integer)item2.components().get(DataComponents.MAX_DAMAGE);
               float itemEnchantmentLevel = (float)(maX_DAMAGE != null ? maX_DAMAGE : 0)
                  + EnchantmentHelper.getItemEnchantmentLevel(
                     (Holder)MinecraftHolder.getClientLevel().registryAccess().get(Enchantments.SHARPNESS).get(), (ItemInstance)itemStack
                  );
               if (itemEnchantmentLevel > value4) {
                  value4 = itemEnchantmentLevel;
                  value3 = i;
               }
            }
         }

         return value3 == -1
            ? SearchInvResult.searchInvResultCompanion.getSearchInvResult()
            : new SearchInvResult(value3, true, MinecraftHolder.getMinecraft().player.getInventory().getItem(value3));
      }
   }

   public final void applyGetInventory(int value, int value2) {
      int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
      if (value != -1) {
         this.applyGetInventory2(value);
         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(value).getItem(), Items.EXPERIENCE_BOTTLE)) {
            int value3 = 1;
            if (value3 <= value2) {
               while (true) {
                  ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                  if (value3 == value2) {
                     break;
                  }

                  value3++;
               }
            }
         } else {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         }

         Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
         PacketUtil.applySend(serverboundSwingPacket);
         this.applyGetInventory2(selectedSlot);
      }
   }

   public static void defaultImpl(InventoryManager inventoryManager, int value, int value2, int value3, Object object) {
      if ((value3 & 2) != 0) {
         value2 = 1;
      }

      inventoryManager.applyGetInventory(value, value2);
   }

   public final void applyGetInventory3(int value) {
      GuiMove.INSTANCE.setFlag5(true);
      if (value != -1) {
         ContainerUtil.defaultImpl(value, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, false, 8, null);
         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
         PacketUtil.applySend(serverboundSwingPacket);
         ContainerUtil.defaultImpl(value, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, false, 8, null);
      }

      GuiMove.INSTANCE.setFlag5(false);
   }

   public final void applyScreen(int value, int value2) {
      int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() % 8 + 1;
      boolean flag = MinecraftHolder.getMinecraft().gui.screen() instanceof ShulkerBoxScreen
         || MinecraftHolder.getMinecraft().gui.screen() instanceof ContainerScreen;
      ContainerUtil.defaultImpl(value, selectedSlot, ContainerInput.SWAP, false, 8, null);
      ContainerUtil.defaultImpl(value2, selectedSlot, ContainerInput.SWAP, false, 8, null);
      boolean flag2 = !flag;
      ContainerInput swap = ContainerInput.SWAP;
      ContainerUtil.applyHandleContainerInput(value, selectedSlot, swap, flag2);
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoSoup.Switch.values().length];

         try {
            values2[AutoSoup.Switch.NORMAL.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoSoup.Switch.SILENT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoSoup.Switch.INVENTORY_NORMAL.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[AutoSoup.Switch.INVENTORY_SILENT.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[AutoSoup.Switch.NORMAL_FULL.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[AutoSoup.Switch.SILENT_FULL.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         values = values2;
      }
   }
}