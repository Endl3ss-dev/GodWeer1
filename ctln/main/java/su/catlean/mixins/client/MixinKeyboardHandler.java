// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.InputEvent;

@Mixin({KeyboardHandler.class})
public class MixinKeyboardHandler {
   public MixinKeyboardHandler() {
   }

   @Inject(
      method = {"keyPress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onKey(long time, int value, KeyEvent keyEvent, CallbackInfo callbackInfo) {
      if (time == Minecraft.getInstance().getWindow().handle()
         && Minecraft.getInstance().level != null
         && Minecraft.getInstance().player != null
         && InputEvent.INSTANCE
            .call(
               keyEvent.key(),
               InputEvent.Device.Keyboard,
               value == 0 ? InputEvent.Action.Release : (value == 1 ? InputEvent.Action.Press : InputEvent.Action.Hold)
            )) {
         callbackInfo.cancel();
      }
   }
}