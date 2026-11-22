package releon.ru.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import releon.ru.api.drag.core.ElementManager;
import releon.ru.api.module.impl.visual.Hud;
import releon.ru.utils.render.ui.Render2DCoordinateSpace;

@Mixin({ChatScreen.class})
public abstract class ChatScreenMixin {
   public ChatScreenMixin() {
   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$dragMouseClicked(Click event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      Click dragEvent = this.releon$toDragEvent(event);
      Hud hud = Hud.getInstance();
      if (hud != null && hud.handleMouseClicked(dragEvent, doubled)) {
         cir.setReturnValue(true);
      } else {
         if (ElementManager.getInstance().handleMouseClicked(dragEvent)) {
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(
      method = {"removed"},
      at = {@At("HEAD")},
      require = 0
   )
   private void releon$cancelDragOnRemoved(CallbackInfo ci) {
      ElementManager.getInstance().cancelActiveElement();
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")},
      require = 0
   )
   private void releon$cancelDragOnClose(CallbackInfo ci) {
      ElementManager.getInstance().cancelActiveElement();
   }

   @Unique
   private Click releon$toDragEvent(Click event) {
      float scale = Render2DCoordinateSpace.guiIndependentScale();
      if (Math.abs(scale - 1.0F) <= 1.0E-4F) {
         return event;
      } else {
         MouseInput info = new MouseInput(event.button(), event.modifiers());
         return new Click(event.x() / (double)scale, event.y() / (double)scale, info);
      }
   }
}
