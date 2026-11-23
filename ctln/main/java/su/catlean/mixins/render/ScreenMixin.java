// deobf by pusia
package su.catlean.mixins.render;

import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.FilesDraggedEvent;
import su.catlean.api.event.events.render.RenderGuiBackgroundEvent;
import su.catlean.api.event.events.render.ScreenMouseCoordsEvent;

@Mixin({Screen.class})
public class ScreenMixin {

   public ScreenMixin() {
   }

   @Inject(
      method = {"extractTransparentBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderInGameBackground(GuiGraphicsExtractor guiGraphicsExtractor, CallbackInfo callbackInfo) {
      if (RenderGuiBackgroundEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractRenderState"},
      at = {@At("HEAD")}
   )
   public void renderHook(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float amount, CallbackInfo callbackInfo) {
      ScreenMouseCoordsEvent.INSTANCE.call(value, value2);
   }

   @Inject(
      method = {"onFilesDrop"},
      at = {@At("HEAD")}
   )
   public void filesDragged(List<Path> items, CallbackInfo callbackInfo) {
      FilesDraggedEvent.INSTANCE.call(items);
   }
}