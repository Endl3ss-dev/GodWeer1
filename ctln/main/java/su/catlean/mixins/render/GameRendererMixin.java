// deobf by pusia
package su.catlean.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.HurtTiltEvent;
import su.catlean.api.event.events.render.PostRenderHandEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.RenderHandEvent;
import su.catlean.api.event.events.render.ShowFloatingItemEvent;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {

   public GameRendererMixin() {
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE_STRING",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
         args = {"ldc=hand"}
      )}
   )
   private void render3dHook(
      DeltaTracker deltaTracker,
      CallbackInfo callbackInfo,
      @Local(name = {"projectionMatrix"}) Matrix4f matrix4f,
      @Local(name = {"modelViewMatrix"}) Matrix4fc matrix4fc,
      @Local(name = {"bobStack"}) PoseStack poseStack
   ) {
      PoseStack poseStack2 = new PoseStack();
      poseStack2.last().pose().set(matrix4fc);
      Render3DEvent.INSTANCE.call(poseStack2, matrix4f);
   }

   @Inject(
      method = {"renderItemInHand"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHand(CameraRenderState cameraRenderState, float amount, Matrix4fc matrix4fc, CallbackInfo callbackInfo) {
      if (RenderHandEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V",
         shift = Shift.AFTER
      )}
   )
   private void catlean$postRenderHand(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      PostRenderHandEvent.INSTANCE.call();
   }

   @Inject(
      method = {"displayItemActivation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void showFloatingItemHook(ItemStack itemStack, CallbackInfo callbackInfo) {
      if (ShowFloatingItemEvent.INSTANCE.call(itemStack)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"bobHurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void tiltViewWhenHurtHook(CameraRenderState cameraRenderState, PoseStack poseStack, CallbackInfo callbackInfo) {
      if (HurtTiltEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}