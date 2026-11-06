package releon.ru.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.screens.mainmenu.NeonMainMenuScreen;

@Mixin({MinecraftClient.class})
public abstract class TitleScreenMixin {
   public TitleScreenMixin() {
   }

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void replaceVanillaTitleScreen(Screen screen, CallbackInfo ci) {
      if (screen instanceof TitleScreen) {
         ci.cancel();
         ((MinecraftClient)(Object)this).setScreen(new NeonMainMenuScreen());
      }
   }
}

