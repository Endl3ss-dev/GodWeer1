// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.network.ClearClickMapEvent;

@Mixin({AbstractContainerMenu.class})
public class ItemStackHashMixin {

   public ItemStackHashMixin() {
   }

   @Inject(
      method = {"clicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onSlotClickHook(int value, int value2, ContainerInput containerInput, Player player, CallbackInfo callbackInfo) {
      if (ClearClickMapEvent.INSTANCE.call() && callbackInfo != null) {
         callbackInfo.cancel();
      }
   }
}