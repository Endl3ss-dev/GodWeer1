// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.FrameBufferEvent;

@Mixin({OutputTarget.class})
public class MixinRenderPhase {
   public MixinRenderPhase() {
   }

   @Inject(
      method = {"getRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getHook(CallbackInfoReturnable<RenderTarget> callbackInfoReturnable) {
      if (GofraState.INSTANCE.getModifyBuffer() && FrameBufferEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(FrameBufferEvent.INSTANCE.getFrameBuffer());
         callbackInfoReturnable.cancel();
      }
   }
}