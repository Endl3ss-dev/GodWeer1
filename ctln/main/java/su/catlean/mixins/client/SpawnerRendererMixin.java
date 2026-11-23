// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.RenderSpawnerEntityEvent;

@Mixin({SpawnerRenderer.class})
public class SpawnerRendererMixin {

   public SpawnerRendererMixin() {
   }

   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/blockentity/state/SpawnerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderHook(CallbackInfo callbackInfo) {
      if (RenderSpawnerEntityEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}