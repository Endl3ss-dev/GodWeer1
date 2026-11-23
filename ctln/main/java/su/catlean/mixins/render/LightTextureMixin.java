// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.LightMapUpdateEvent;

@Mixin({Lightmap.class})
public class LightTextureMixin {

   @Final
   @Shadow
   private GpuTexture texture;

   public LightTextureMixin() {
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateHook(LightmapRenderState lightmapRenderState, CallbackInfo callbackInfo) {
      if (LightMapUpdateEvent.INSTANCE.call()) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.texture, new Vector4f(1.0F));
         callbackInfo.cancel();
      }
   }
}