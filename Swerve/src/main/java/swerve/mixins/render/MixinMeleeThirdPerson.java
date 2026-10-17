package swerve.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import swerve.cosmetics.melee.MeleeDesign;
import swerve.cosmetics.melee.MeleeRenderer;
import swerve.cosmetics.melee.MeleeState;

/**
 * Ножи от третьего лица: модель в руке игрока.
 *
 * <p>Сигнатура renderArmWithItem — mojmap 1.21.4 (PlayerRenderState, ItemStackRenderState, ...).
 */
@Mixin(PlayerItemInHandLayer.class)
public abstract class MixinMeleeThirdPerson {

    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;"
                    + "Lnet/minecraft/client/renderer/item/ItemStackRenderState;"
                    + "Lnet/minecraft/world/entity/HumanoidArm;"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void swerve$meleeSkin(PlayerRenderState state, ItemStackRenderState itemState, HumanoidArm arm,
                                  PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!MeleeState.thirdPerson()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(state.id);
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        MeleeDesign design = MeleeState.designOf(player);
        if (design == null) {
            return;
        }
        ItemStack item = player.getItemInHand(
                arm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (!MeleeState.applies(item)) {
            return;
        }
        PlayerRenderer renderer = renderer(player);
        if (renderer == null) {
            return;
        }
        pose.pushPose();
        ((PlayerModel) renderer.getModel()).translateToHand(arm, pose);
        pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        boolean rendered = MeleeRenderer.getInstance().renderThirdPerson(player, arm, pose, buffer, light);
        pose.popPose();
        if (rendered) {
            ci.cancel();
        }
    }

    private static PlayerRenderer renderer(AbstractClientPlayer player) {
        try {
            // [!] В 1.21.4 у EntityRenderer два параметра типа.
            EntityRenderer<?, ?> renderer =
                    Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            return renderer instanceof PlayerRenderer pr ? pr : null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
