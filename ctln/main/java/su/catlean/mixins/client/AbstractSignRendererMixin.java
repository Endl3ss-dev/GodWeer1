// deobf by pusia
package su.catlean.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.SignTextRenderEvent;

@Mixin({AbstractSignRenderer.class})
public class AbstractSignRendererMixin {

   public AbstractSignRendererMixin() {
   }

   @Inject(
      method = {"submitSignText"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public final void renderTextHook(
      SignRenderState signRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, SignText signText, CallbackInfo callbackInfo
   ) {
      if (SignTextRenderEvent.INSTANCE.call(signRenderState.blockPos)) {
         callbackInfo.cancel();
      }
   }
}