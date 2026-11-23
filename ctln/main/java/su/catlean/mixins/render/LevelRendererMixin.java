// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.WeatherRenderEvent;
import su.catlean.api.event.events.world.ApplyFogEvent;

@Mixin({LevelRenderer.class})
public class LevelRendererMixin {

   public LevelRendererMixin() {
   }

   @ModifyVariable(
      method = {"render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 7
   )
   private Vector4f getFogColorHook(Vector4f vector4f) {
      return ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getColorVec() : vector4f;
   }

   @Inject(
      method = {"addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderWeatherHook(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      if (WeatherRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}