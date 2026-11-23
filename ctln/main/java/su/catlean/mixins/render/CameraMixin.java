// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.catlean.api.event.events.player.FreecamStateEvent;
import su.catlean.api.event.events.render.BasicProjectionMatrixEvent;
import su.catlean.api.event.events.render.CameraOffsetEvent;
import su.catlean.api.event.events.render.SetupTerrainEvent;

@Mixin({Camera.class})
public abstract class CameraMixin {

   @Shadow
   private boolean detached;

   public CameraMixin() {
   }

   @Shadow
   protected abstract float getMaxZoom(float value);

   @Shadow
   public abstract float getFov();

   @ModifyArgs(
      method = {"alignWithEntity"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;move(FFF)V",
         ordinal = 0
      )
   )
   private void modifyCameraDistance(Args args) {
      if (CameraOffsetEvent.INSTANCE.call()) {
         args.set(0, -this.getMaxZoom(CameraOffsetEvent.INSTANCE.getOffset()));
      }
   }

   @Inject(
      method = {"getMaxZoom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClipToSpace(float amount, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (CameraOffsetEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(CameraOffsetEvent.INSTANCE.getOffset());
      }
   }

   @Inject(
      method = {"update"},
      at = {@At("RETURN")}
   )
   private void updateHook(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (SetupTerrainEvent.INSTANCE.call()) {
         this.detached = true;
      }
   }

   @Inject(
      method = {"extractRenderState"},
      at = {@At("RETURN")}
   )
   private void getBasicProjectionMatrixHook(CameraRenderState cameraRenderState, float amount, CallbackInfo callbackInfo) {
      BasicProjectionMatrixEvent.INSTANCE.setFov(this.getFov());
      if (BasicProjectionMatrixEvent.INSTANCE.call()) {
         float zoom = BasicProjectionMatrixEvent.INSTANCE.getZoom();
         float zoomX = BasicProjectionMatrixEvent.INSTANCE.getZoomX();
         float zoomY = BasicProjectionMatrixEvent.INSTANCE.getZoomY();
         float fov = BasicProjectionMatrixEvent.INSTANCE.getFov();
         cameraRenderState.projectionMatrix.identity();
         if (zoom != 1.0F) {
            cameraRenderState.projectionMatrix.translate(zoomX, -zoomY, 0.0F);
            cameraRenderState.projectionMatrix.scale(zoom, zoom, 1.0F);
         }

         cameraRenderState.projectionMatrix
            .perspective(
               fov * (float) (Math.PI / 180.0),
               BasicProjectionMatrixEvent.INSTANCE.getRatio(),
               cameraRenderState.depthFar,
               0.05F,
               RenderSystem.getDevice().getDeviceInfo().isZZeroToOne()
            );
      }
   }

   @ModifyArgs(
      method = {"alignWithEntity"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;setRotation(FF)V"
      )
   )
   private void setRotationHook(Args args) {
      if (FreecamStateEvent.INSTANCE.call()) {
         args.setAll(new Object[]{FreecamStateEvent.INSTANCE.getYaw(), FreecamStateEvent.INSTANCE.getPitch()});
      }
   }

   @ModifyArgs(
      method = {"alignWithEntity"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"
      )
   )
   private void setPosHook(Args args) {
      if (FreecamStateEvent.INSTANCE.call()) {
         args.setAll(new Object[]{FreecamStateEvent.INSTANCE.getX(), FreecamStateEvent.INSTANCE.getY(), FreecamStateEvent.INSTANCE.getZ()});
      }
   }
}