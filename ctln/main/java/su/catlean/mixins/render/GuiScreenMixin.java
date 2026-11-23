// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.ScreenEvent;

@Mixin({Gui.class})
public abstract class GuiScreenMixin {

   public GuiScreenMixin() {
   }

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void setScreenHook(Screen screen, CallbackInfo callbackInfo) {
      if (ScreenEvent.INSTANCE.call(screen)) {
         callbackInfo.cancel();
      }
   }
}