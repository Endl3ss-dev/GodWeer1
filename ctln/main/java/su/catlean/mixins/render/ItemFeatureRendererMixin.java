// deobf by pusia
package su.catlean.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import su.catlean.api.event.events.render.ItemCommandEvent;

@Mixin({ItemFeatureRenderer.class})
public abstract class ItemFeatureRendererMixin {

   public ItemFeatureRendererMixin() {
   }

   @ModifyExpressionValue(
      method = {"prepareMainSubmit", "prepareOutlineSubmit"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;getVertexBuilder(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      )}
   )
   private VertexConsumer catlean$redirectItemConsumer(VertexConsumer vertexConsumer) {
      return ItemCommandEvent.INSTANCE.call() && ItemCommandEvent.INSTANCE.getProvider() != null ? ItemCommandEvent.INSTANCE.getProvider() : vertexConsumer;
   }

   @ModifyExpressionValue(
      method = {"prepareFoilSubmit"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;foilType()Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;"
      )}
   )
   private FoilType renderHook(FoilType foilType) {
      return ItemCommandEvent.INSTANCE.call() ? FoilType.NONE : foilType;
   }
}