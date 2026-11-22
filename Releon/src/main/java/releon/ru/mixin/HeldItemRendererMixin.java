package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.events.impl.HandAnimationEvent;
import releon.ru.api.events.impl.HandOffsetEvent;
import releon.ru.api.events.impl.HeldItemUpdateEvent;
import releon.ru.api.events.impl.ItemRendererEvent;
import releon.ru.manager.Manager;
import releon.ru.utils.render.hands.ShaderHandMaterialRenderer;

@Mixin(
   value = {HeldItemRenderer.class},
   priority = 900
)
public abstract class HeldItemRendererMixin {
   @Unique
   private static final boolean releon$holdMyItemsLoaded = FabricLoader.getInstance().isModLoaded("hold-my-items");
   @Shadow
   private ItemStack mainHand;
   @Shadow
   private ItemStack offHand;
   @Unique
   private MatrixStack releon$customSwingMatrices;
   @Unique
   private Hand releon$customSwingHand;
   @Unique
   private float releon$customSwingProgress;

   public HeldItemRendererMixin() {
   }

   @Inject(
      method = {"updateHeldItems"},
      at = {@At("TAIL")}
   )
   private void releon$updateHeldItems(CallbackInfo ci) {
      HeldItemUpdateEvent event = Manager.postEvent(new HeldItemUpdateEvent(this.mainHand, this.offHand));
      if (event.getMainHand() != this.mainHand) {
         this.mainHand = event.getMainHand();
      }

      if (event.getOffHand() != this.offHand) {
         this.offHand = event.getOffHand();
      }
   }

   @WrapOperation(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
      )},
      require = 0
   )
   private void releon$itemRenderHook(
      HeldItemRenderer instance,
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue nodeCollector,
      int light,
      Operation<Void> original
   ) {
      if (releon$holdMyItemsLoaded) {
         original.call(new Object[]{instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, nodeCollector, light});
      } else {
         ItemRendererEvent event = Manager.postEvent(new ItemRendererEvent(player, item, hand));
         original.call(
            new Object[]{
               instance, event.getPlayer(), tickDelta, pitch, event.getHand(), swingProgress, event.getStack(), equipProgress, matrices, nodeCollector, light
            }
         );
      }
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
         shift = Shift.AFTER
      )},
      require = 0
   )
   private void releon$handOffset(
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue nodeCollector,
      int light,
      CallbackInfo ci
   ) {
      if (!releon$holdMyItemsLoaded && !stack.isEmpty() && !stack.contains(DataComponentTypes.MAP_ID)) {
         HandOffsetEvent event = Manager.postEvent(new HandOffsetEvent(matrices, stack, hand));
         float scale = event.getScale();
         if (scale != 1.0F) {
            matrices.scale(scale, scale, scale);
         }
      }
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At("HEAD")},
      require = 0
   )
   private void releon$beginShaderHandMaterial(
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue nodeCollector,
      int light,
      CallbackInfo ci
   ) {
      ShaderHandMaterialRenderer.beginSubmission(player);
   }

   @WrapOperation(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"
      )},
      require = 0
   )
   private void releon$baseSwingAnimation(
      HeldItemRenderer instance,
      MatrixStack matrices,
      Arm arm,
      float equipProgress,
      Operation<Void> original,
      @Local(argsOnly = true) AbstractClientPlayerEntity player,
      @Local(argsOnly = true) Hand hand,
      @Local(argsOnly = true,ordinal = 2) float swingProgress
   ) {
      if (releon$holdMyItemsLoaded) {
         original.call(new Object[]{instance, matrices, arm, equipProgress});
      } else if (player.isUsingItem() && player.getActiveHand() == hand) {
         original.call(new Object[]{instance, matrices, arm, equipProgress});
      } else {
         HandAnimationEvent event = Manager.postEvent(new HandAnimationEvent(matrices, hand, swingProgress));
         if (event.isCancelled()) {
            this.releon$markCustomSwing(matrices, hand, swingProgress);
         } else {
            original.call(new Object[]{instance, matrices, arm, equipProgress});
         }
      }
   }

   @WrapOperation(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V"
      )},
      require = 0
   )
   private void releon$swingAnimation(
      HeldItemRenderer instance,
      float swingProgress,
      MatrixStack matrices,
      int armX,
      Arm arm,
      Operation<Void> original,
      @Local(argsOnly = true) AbstractClientPlayerEntity player,
      @Local(argsOnly = true) Hand hand
   ) {
      if (releon$holdMyItemsLoaded) {
         original.call(new Object[]{instance, swingProgress, matrices, armX, arm});
      } else if (player.isUsingItem() && player.getActiveHand() == hand) {
         original.call(new Object[]{instance, swingProgress, matrices, armX, arm});
      } else if (!this.releon$consumeCustomSwing(matrices, hand, swingProgress)) {
         HandAnimationEvent event = Manager.postEvent(new HandAnimationEvent(matrices, hand, swingProgress));
         if (!event.isCancelled()) {
            original.call(new Object[]{instance, swingProgress, matrices, armX, arm});
         }
      }
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At("TAIL")},
      require = 0
   )
   private void releon$clearSwingAnimation(
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue nodeCollector,
      int light,
      CallbackInfo ci
   ) {
      ShaderHandMaterialRenderer.endSubmission();
      this.releon$clearCustomSwing();
   }

   @Unique
   private void releon$markCustomSwing(MatrixStack matrices, Hand hand, float swingProgress) {
      this.releon$customSwingMatrices = matrices;
      this.releon$customSwingHand = hand;
      this.releon$customSwingProgress = swingProgress;
   }

   @Unique
   private boolean releon$consumeCustomSwing(MatrixStack matrices, Hand hand, float swingProgress) {
      boolean matches = this.releon$customSwingMatrices == matrices
         && this.releon$customSwingHand == hand
         && Float.compare(this.releon$customSwingProgress, swingProgress) == 0;
      if (matches) {
         this.releon$clearCustomSwing();
      }

      return matches;
   }

   @Unique
   private void releon$clearCustomSwing() {
      this.releon$customSwingMatrices = null;
      this.releon$customSwingHand = null;
      this.releon$customSwingProgress = 0.0F;
   }
}
