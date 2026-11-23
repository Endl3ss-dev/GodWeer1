// deobf by pusia
package su.catlean.mixins.render;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LoadingOverlay.class})
public abstract class MixinLoadingOverlay {
   @Final
   @Shadow
   private boolean fadeIn;
   @Shadow
   private float currentProgress;
   @Shadow
   private long fadeOutStart = -1L;
   @Shadow
   private long fadeInStart = -1L;
   @Final
   @Shadow
   private ReloadInstance reload;
   @Final
   @Shadow
   private Consumer<Optional<Throwable>> onFinish;

   public MixinLoadingOverlay() {
   }

   @Inject(
      method = {"extractRenderState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float amount, CallbackInfo callbackInfo) {
   }
}