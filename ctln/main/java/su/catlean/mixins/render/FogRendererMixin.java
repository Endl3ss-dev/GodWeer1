// deobf by pusia
package su.catlean.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.catlean.api.event.GetFogBufferEvent;
import su.catlean.api.event.events.world.ApplyFogEvent;

@Mixin({FogRenderer.class})
public abstract class FogRendererMixin {

   public FogRendererMixin() {
   }

   @ModifyVariable(
      method = {"updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Vector4f applyFogHook(Vector4f vector4f) {
      return vector4f.w != 0.0F && ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getColorVec() : vector4f;
   }

   @ModifyVariable(
      method = {"updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 4
   )
   private float modifyEnvironmentalStart(float amount) {
      return ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getEStart() : amount;
   }

   @ModifyVariable(
      method = {"updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 5
   )
   private float envEndHook(float amount) {
      return ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getEEnd() : amount;
   }

   @ModifyVariable(
      method = {"updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 6
   )
   private float renderStartHook(float amount) {
      return ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getRStart() : amount;
   }

   @ModifyVariable(
      method = {"updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 7
   )
   private float renderEndHook(float amount) {
      return ApplyFogEvent.INSTANCE.call() ? ApplyFogEvent.INSTANCE.getREnd() : amount;
   }

   @ModifyExpressionValue(
      method = {"getBuffer"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/fog/FogRenderer;fogEnabled:Z"
      )}
   )
   private boolean getFogBufferHook(boolean flag) {
      return GetFogBufferEvent.INSTANCE.call() ? false : flag;
   }
}