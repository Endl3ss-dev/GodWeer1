// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.world.level.block.WebBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.player.CobWebEvent;

@Mixin({WebBlock.class})
public class WebBlockMixin {

   public WebBlockMixin() {
   }

   @Inject(
      method = {"entityInside"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollisionHook(CallbackInfo callbackInfo) {
      if (CobWebEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}