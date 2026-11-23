// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.RenderNameTagEvent;
import su.catlean.api.event.events.render.ShouldRenderEntityEvent;

@Mixin({EntityRenderer.class})
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

   public EntityRendererMixin() {
   }

   @Inject(
      method = {"submitNameDisplay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderLabelIfPresent(
      S entityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo callbackInfo
   ) {
      if (RenderNameTagEvent.INSTANCE.call(entityRenderState)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void shouldRender(T entity, Frustum frustum, double amount, double amount2, double amount3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (GofraState.INSTANCE.getShouldRender() && ShouldRenderEntityEvent.INSTANCE.call(entity)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}