// deobf by pusia
package su.catlean.mixins.player;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.InsertStackEvent;
import su.catlean.api.event.events.player.UpdateSelectedSlotEvent;

@Mixin({Inventory.class})
public class InventoryMixin {

   public InventoryMixin() {
   }

   @Inject(
      method = {"add(Lnet/minecraft/world/item/ItemStack;)Z"},
      at = {@At("HEAD")}
   )
   private void insertStackHook(ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      InsertStackEvent.INSTANCE.setStack(itemStack);
      InsertStackEvent.INSTANCE.call();
   }

   @Inject(
      method = {"setSelectedSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void setSelectedSlotHook(int value, CallbackInfo callbackInfo) {
      UpdateSelectedSlotEvent.INSTANCE.setSlot(value);
      if (UpdateSelectedSlotEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}