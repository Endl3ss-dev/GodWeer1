// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.EquipArmEvent;
import su.catlean.api.event.events.render.HandModifyEvent;
import su.catlean.api.event.events.render.SwingArmEvent;

@Mixin({ItemInHandRenderer.class})
public class ItemInHandRendererMixin {

   public ItemInHandRendererMixin() {
   }

   @Inject(
      method = {"submitArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
      )}
   )
   private void onRenderItem(
      AbstractClientPlayer abstractClientPlayer,
      float amount,
      float amount2,
      InteractionHand interactionHand,
      float amount3,
      ItemStack itemStack,
      float amount4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int value,
      CallbackInfo callbackInfo
   ) {
      HandModifyEvent.INSTANCE.call(interactionHand, poseStack, itemStack, false);
   }

   @Inject(
      method = {"applyEatTransform"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void applyEatOrDrinkTransformationHook(
      PoseStack poseStack, float amount, HumanoidArm humanoidArm, ItemStack itemStack, Player player, CallbackInfo callbackInfo
   ) {
      if (HandModifyEvent.INSTANCE
         .call(
            Minecraft.getInstance().options.mainHand().get() == HumanoidArm.RIGHT
               ? (humanoidArm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
               : (humanoidArm == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND),
            poseStack,
            itemStack,
            true
         )) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"swingArm(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void swingArmHook(float amount, PoseStack poseStack, int value, HumanoidArm humanoidArm, CallbackInfo callbackInfo) {
      if (SwingArmEvent.INSTANCE.call(amount, poseStack, value, humanoidArm)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"applyItemArmTransform"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void swingArmHook(PoseStack poseStack, HumanoidArm humanoidArm, float amount, CallbackInfo callbackInfo) {
      if (EquipArmEvent.INSTANCE.call(amount, humanoidArm)) {
         callbackInfo.cancel();
      }
   }
}