// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.render.RenderEntityEvent;
import su.catlean.api.event.events.render.RenderFireOnEntityEvent;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {

   public EntityRenderDispatcherMixin() {
   }

   @Inject(
      method = {"submit"},
      at = {@At("HEAD")}
   )
   private <S extends EntityRenderState> void onRenderHead(
      S entityRenderState,
      CameraRenderState cameraRenderState,
      double amount,
      double amount2,
      double amount3,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      CallbackInfo callbackInfo
   ) {
      if (RenderFireOnEntityEvent.INSTANCE.call()) {
         entityRenderState.displayFireAnimation = false;
      }
   }

   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public <E extends Entity, S extends EntityRenderState> void onRenderEntity(
      E entity, Frustum frustum, double amount, double amount2, double amount3, CallbackInfoReturnable<Boolean> callbackInfoReturnable
   ) {
      if (RenderEntityEvent.INSTANCE.call(entity)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}