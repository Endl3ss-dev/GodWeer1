package swerve.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.impl.render.ViewModel;
import swerve.utils.render.HandOutline;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void swerve$beginHands(CallbackInfo ci) {
        ViewModel.beginFrame();
        HandOutline.beforeHands();
    }

    @Inject(method = "renderHandsWithItems", at = @At("RETURN"))
    private void swerve$endHands(CallbackInfo ci) {
        HandOutline.afterHands();
    }

    // Точка сразу после общего pushPose покрывает предметы, пустую руку, карты и все use-action ветки.
    @Inject(method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER))
    private void swerve$applyViewModel(AbstractClientPlayer player, float partialTick, float pitch,
                                       InteractionHand hand, float swingProgress, ItemStack item,
                                       float equippedProgress, PoseStack poseStack, MultiBufferSource buffer,
                                       int combinedLight, CallbackInfo ci) {
        ViewModel.applyTransform(poseStack, player, hand, equippedProgress);
    }
}
