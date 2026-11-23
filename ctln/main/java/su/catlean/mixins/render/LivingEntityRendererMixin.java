// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.catlean.api.event.events.render.EntityAlphaEvent;
import su.catlean.api.event.events.render.LookRenderEvent;

@Mixin({LivingEntityRenderer.class})
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

   @Unique
   private S prevState;

   public LivingEntityRendererMixin() {
   }

   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void renderHook(
      S livingEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo callbackInfo
   ) {
      this.prevState = (S)livingEntityRenderState;
      if (livingEntityRenderState instanceof AvatarRenderState avatarRenderState
         && Minecraft.getInstance().player != null
         && Objects.equals(avatarRenderState.id, Minecraft.getInstance().player.getId())) {
         LookRenderEvent.INSTANCE.call(livingEntityRenderState.yRot, livingEntityRenderState.xRot, livingEntityRenderState.bodyRot);
         livingEntityRenderState.yRot = LookRenderEvent.INSTANCE.getYaw();
         livingEntityRenderState.xRot = LookRenderEvent.INSTANCE.getPitch();
         livingEntityRenderState.bodyRot = LookRenderEvent.INSTANCE.getBodyYaw();
      }
   }

   @ModifyArgs(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
      )
   )
   private void renderHook(Args args) {
      if (this.prevState != null
         && this.prevState instanceof AvatarRenderState avatarRenderState
         && Minecraft.getInstance().player != null
         && !Objects.equals(avatarRenderState.id, Minecraft.getInstance().player.getId())
         && EntityAlphaEvent.INSTANCE.call(new Vec3(avatarRenderState.x, avatarRenderState.y, avatarRenderState.z))) {
         args.set(6, this.withAlpha(new Color(654311423), EntityAlphaEvent.INSTANCE.getAlpha()).getRGB());
      }
   }

   @Unique
   private Color withAlpha(Color color, float amount) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(255.0F * amount));
   }
}