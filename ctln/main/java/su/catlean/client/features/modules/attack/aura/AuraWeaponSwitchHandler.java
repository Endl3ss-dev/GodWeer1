// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.services.InventoryManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ItemTypeUtil;

public final class AuraWeaponSwitchHandler implements AuraHandler {
   @NotNull
   public static final AuraWeaponSwitchHandler INSTANCE = new AuraWeaponSwitchHandler();
   private static int value = -1;

   private AuraWeaponSwitchHandler() {
   }

   public final boolean checkGetItem(boolean flag, Aura.AutoWeapon switchMode) {
      ItemStack mainHandItem = MinecraftHolder.getLocalPlayer().getMainHandItem();
      if (!flag) {
         return true;
      } else {
         return switchMode == Aura.AutoWeapon.NONE
            ? ItemTypeUtil.testItemStack(mainHandItem)
               || mainHandItem.getItem() instanceof AxeItem
               || mainHandItem.getItem() instanceof TridentItem
               || mainHandItem.getItem() instanceof MaceItem
            : InventoryManager.INSTANCE.getSearchInvResult().isFlag() || InventoryManager.INSTANCE.getSearchInvResult3().isFlag();
      }
   }

   public final void updateFlag(boolean flag) {
      SearchInvResult searchInvResult = InventoryManager.INSTANCE.getSearchInvResult();
      if (searchInvResult.isFlag()) {
         if (flag) {
            searchInvResult.update();
         } else {
            searchInvResult.update();
         }
      } else {
         SearchInvResult searchInvResult3 = InventoryManager.INSTANCE.getSearchInvResult3();
         if (searchInvResult3.isFlag()) {
            if (flag) {
               searchInvResult3.update();
            } else {
               searchInvResult3.update();
            }
         }
      }
   }

   public static void defaultImpl(AuraWeaponSwitchHandler auraWeaponSwitchHandler, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = false;
      }

      auraWeaponSwitchHandler.updateFlag(flag);
   }

   @Override
   public void update() {
      if (Aura.INSTANCE.getSwitchMode() == Aura.AutoWeapon.SILENT) {
         value = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
         this.updateFlag(true);
      }
   }

   @Override
   public void applyMax(Rotation rotation) {
      if (Aura.INSTANCE.getSwitchMode() == Aura.AutoWeapon.SILENT) {
         this.update3();
      }
   }

   @Override
   public void update2() {
      this.update3();
   }

   public final void update3() {
      if (value != -1) {
         InventoryManager.INSTANCE.applyGetInventory2(value);
         value = -1;
      }
   }
}