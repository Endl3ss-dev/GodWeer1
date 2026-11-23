// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.world.level.block.SweetBerryBushBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.world.SweetBerryBushCollisionEvent;

@Mixin({SweetBerryBushBlock.class})
public class SweetBerryBushBlockMixin {

   public SweetBerryBushBlockMixin() {
   }

   @Inject(
      method = {"entityInside"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollisionHook(CallbackInfo callbackInfo) {
      if (SweetBerryBushCollisionEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}