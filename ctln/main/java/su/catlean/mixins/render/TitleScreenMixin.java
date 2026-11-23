// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.PostTitleScreenInitEvent;

@Mixin({TitleScreen.class})
public class TitleScreenMixin extends Screen {

   protected TitleScreenMixin(Component component) {
      super(component);
   }

   @Inject(
      method = {"init"},
      at = {@At("RETURN")}
   )
   public void postInitHook(CallbackInfo callbackInfo) {
      PostTitleScreenInitEvent.INSTANCE.call();
   }
}