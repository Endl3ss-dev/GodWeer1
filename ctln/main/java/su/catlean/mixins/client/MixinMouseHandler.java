// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.interfaces.IMouse;

@Mixin({MouseHandler.class})
public class MixinMouseHandler implements IMouse {
   @Shadow
   private double xpos;
   @Shadow
   private double ypos;

   public MixinMouseHandler() {
   }

   @Inject(
      method = {"onButton"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onMouseButtonHook(long time, MouseButtonInfo mouseButtonInfo, int value, CallbackInfo callbackInfo) {
      if (time == Minecraft.getInstance().getWindow().handle()
         && InputEvent.INSTANCE.call(mouseButtonInfo.button(), InputEvent.Device.Mouse, value == 0 ? InputEvent.Action.Release : InputEvent.Action.Press)) {
         callbackInfo.cancel();
      }
   }

   @Override
   public void setPosition(int value, int value2) {
      this.xpos = value;
      this.ypos = value2;
   }
}