package releon.ru.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import releon.ru.api.module.impl.visual.ESP;
import releon.ru.api.module.impl.visual.KillEffect;
import releon.ru.utils.render.hands.ShaderHandMaterialRenderer;

@Mixin({PlayerEntityRenderer.class})
public abstract class AvatarRendererMixin {
   private static final Identifier RELEON_LEGACY_SOUL_TEXTURE = Identifier.tryParse("releon:textures/effects/legacy_soul.png");

   public AvatarRendererMixin() {
   }

   @ModifyArg(
      method = {"renderArm"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;)V"
      ),
      index = 2,
      require = 0
   )
   private RenderLayer releon$useShaderHandMaterial(RenderLayer original) {
      return ShaderHandMaterialRenderer.armLayer(original);
   }

   @Inject(
      method = {"updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V"},
      at = {@At("TAIL")},
      require = 0
   )
   private void releon$hideEspAvatarLabels(PlayerLikeEntity entity, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
      if (ESP.shouldHideVanillaName(entity)) {
         ESP.captureServerHealth(entity, state.playerName);
         state.displayName = null;
         state.playerName = null;
      }

      if (KillEffect.isLegacySoulFake(entity)) {
         state.hatVisible = false;
         state.jacketVisible = false;
         state.leftPantsLegVisible = false;
         state.rightPantsLegVisible = false;
         state.leftSleeveVisible = false;
         state.rightSleeveVisible = false;
         state.capeVisible = false;
         state.extraEars = false;
         state.equippedHeadStack = ItemStack.EMPTY;
         state.equippedChestStack = ItemStack.EMPTY;
         state.equippedLegsStack = ItemStack.EMPTY;
         state.equippedFeetStack = ItemStack.EMPTY;
         state.rightHandItem = ItemStack.EMPTY;
         state.leftHandItem = ItemStack.EMPTY;
         state.rightArmPose = ArmPose.EMPTY;
         state.leftArmPose = ArmPose.EMPTY;
         state.isUsingItem = false;
      }
   }

   @Inject(
      method = {"shouldRenderFeatures(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Z"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$disableSoulLayers(PlayerEntityRenderState state, CallbackInfoReturnable<Boolean> cir) {
      if (KillEffect.isLegacySoulFake(state.id)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$overrideSoulTexture(PlayerEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
      if (RELEON_LEGACY_SOUL_TEXTURE != null && KillEffect.isLegacySoulFake(state.id)) {
         cir.setReturnValue(RELEON_LEGACY_SOUL_TEXTURE);
      }
   }
}
