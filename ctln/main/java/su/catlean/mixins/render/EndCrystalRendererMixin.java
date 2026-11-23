// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.render.CrystalRenderEvent;
import su.catlean.api.event.events.render.CrystalYOffsetEvent;
import su.catlean.api.event.events.render.SetCrystalScaleEvent;

@Mixin({EndCrystalRenderer.class})
public class EndCrystalRendererMixin {

   public EndCrystalRendererMixin() {
   }

   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHook(
      EndCrystalRenderState endCrystalRenderState,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      CameraRenderState cameraRenderState,
      CallbackInfo callbackInfo
   ) {
      if (CrystalRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
      )}
   )
   private void scaleHook(
      EndCrystalRenderState endCrystalRenderState,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      CameraRenderState cameraRenderState,
      CallbackInfo callbackInfo
   ) {
      if (SetCrystalScaleEvent.INSTANCE.call(endCrystalRenderState.ageInTicks)) {
         float scale = SetCrystalScaleEvent.INSTANCE.getScale();
         poseStack.scale(scale, scale, scale);
      }
   }

   @Inject(
      method = {"getY"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void getYOffsetHook(float amount, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (CrystalYOffsetEvent.INSTANCE.call(amount)) {
         callbackInfoReturnable.setReturnValue(CrystalYOffsetEvent.INSTANCE.getOffset());
      }
   }
}