// deobf by pusia
package su.catlean.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.RenderArmorEvent;

@Mixin({HumanoidArmorLayer.class})
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {

   public HumanoidArmorLayerMixin() {
   }

   @Inject(
      method = {"renderArmorPiece"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderArmor(
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      ItemStack itemStack,
      EquipmentSlot equipmentSlot,
      int value,
      S humanoidRenderState,
      CallbackInfo callbackInfo
   ) {
      if (RenderArmorEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}