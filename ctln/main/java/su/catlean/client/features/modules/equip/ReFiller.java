// deobf by pusia
package su.catlean.client.features.modules.equip;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.features.ui.panel.PanelScreen;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.client.services.InventoryManager;

public final class ReFiller extends Module {
   @NotNull
   public static final ReFiller INSTANCE = new ReFiller();
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   private static int value;
   private static int value2;

   private ReFiller() {
      super("re-filler", SubCategories.getSubCategory9(), null, 4, null);
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      ReFiller.value = value;
   }

   public final int getValue2() {
      return value2;
   }

   public final void setValue2(int value) {
      value2 = value;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() != null
         && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ClickGuiScreen)
         && !(MinecraftHolder.getMinecraft().gui.screen() instanceof PanelScreen)
         && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)) {
         value = 0;
      } else {
         if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.TOTEM_OF_UNDYING)) {
            value = 3;
            value2 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
         } else if (value > 0) {
            if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(value2).getItem(), Items.TOTEM_OF_UNDYING)) {
               InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
               Item[] items = new Item[]{Items.TOTEM_OF_UNDYING};
               SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items);
               if (searchInvResult2.isFlag()) {
                  ContainerUtil.applyHandleContainerInput(searchInvResult2.getValue(), value2, ContainerInput.SWAP, true);
                  value = 0;
               }
            }

            int value3 = value;
            value = value3 + -1;
         }

         if (playerUtil.testValue(10)) {
            for (int i = 0; i < 9; i++) {
               if (this.checkGetCount(i)) {
                  return;
               }
            }
         }
      }
   }

   private final boolean checkGetCount(int value) {
      ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
      if (!item.isEmpty() && item.isStackable()) {
         if (item.getMaxStackSize() == 16 && item.getCount() > 8) {
            return false;
         } else if (item.getMaxStackSize() == 64 && item.getCount() > 32) {
            return false;
         } else {
            for (int i = 9; i < 36; i++) {
               ItemStack item2 = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
               if (!item2.isEmpty() && this.checkGetHoverName(item, item2)) {
                  boolean flag = item2.getCount() + item.getCount() > item2.getMaxStackSize();
                  if (item2.getMaxStackSize() == item2.getCount()) {
                     ContainerUtil.applyHandleContainerInput(i, value, ContainerInput.SWAP, true);
                  } else {
                     ContainerUtil.defaultImpl(i, 0, null, false, 14, null);
                     int value3 = value < 9 ? 36 + value : value;
                     boolean flag2 = !flag;
                     Object object = null;
                     byte byteValue = 6;
                     boolean flag3 = flag2;
                     Object object2 = null;
                     byte byteValue2 = 0;
                     int value4 = value3;
                     ContainerUtil.defaultImpl(value4, byteValue2, (ContainerInput)object2, flag3, byteValue, object);
                     if (flag) {
                        ContainerUtil.defaultImpl(i, 0, null, true, 6, null);
                     }
                  }

                  playerUtil.update();
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private final boolean checkGetHoverName(ItemStack itemStack, ItemStack itemStack2) {
      return Intrinsics.areEqual(itemStack.getItem(), itemStack2.getItem()) && Intrinsics.areEqual(itemStack.getHoverName(), itemStack2.getHoverName());
   }
}