// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.player.KeyboardInputEvent;

@Mixin({KeyboardInput.class})
public class KeyboardInputMixin {

   public KeyboardInputMixin() {
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/player/KeyboardInput;moveVector:Lnet/minecraft/world/phys/Vec2;",
         ordinal = 0,
         shift = Shift.AFTER
      )},
      cancellable = true
   )
   private void tickHook(CallbackInfo callbackInfo) {
      if (KeyboardInputEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}