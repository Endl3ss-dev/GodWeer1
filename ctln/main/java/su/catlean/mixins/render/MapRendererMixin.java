// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.MapRenderEvent;

@Mixin({MapRenderer.class})
public class MapRendererMixin {

   public MapRendererMixin() {
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void drawHook(
      MapRenderState mapRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, boolean flag, int value, CallbackInfo callbackInfo
   ) {
      if (flag && MapRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}