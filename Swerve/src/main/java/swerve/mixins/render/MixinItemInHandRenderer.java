package swerve.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import swerve.modules.impl.render.SwingAnimation;
import swerve.modules.impl.render.ViewModel;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Unique
    private InteractionHand swerve$hand;
    @Unique
    private AbstractClientPlayer swerve$player;
    @Unique
    private ItemStack swerve$stack;
    @Unique
    private float swerve$partialTick;
    @Unique
    private boolean swerve$using;
    @Unique
    private boolean swerve$eating;

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void swerve$beginHands(float partialTick, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                   LocalPlayer player, int light, CallbackInfo ci) {
        ViewModel.beginHandFrame();
    }

    @Inject(method = "renderHandsWithItems", at = @At("TAIL"))
    private void swerve$endHands(float partialTick, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                 LocalPlayer player, int light, CallbackInfo ci) {
        ViewModel.endHandFrame();
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void swerve$captureContext(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                       float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack,
                                       MultiBufferSource buffer, int light, CallbackInfo ci) {
        swerve$hand = hand;
        swerve$player = player;
        swerve$stack = stack;
        swerve$partialTick = partialTick;
        swerve$using = player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand;
        if (swerve$using) {
            ItemUseAnimation animation = stack.getUseAnimation();
            swerve$eating = animation == ItemUseAnimation.EAT || animation == ItemUseAnimation.DRINK;
        } else {
            swerve$eating = false;
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void swerve$clearContext(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                     float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack,
                                     MultiBufferSource buffer, int light, CallbackInfo ci) {
        swerve$hand = null;
        swerve$player = null;
        swerve$stack = null;
        swerve$using = false;
        swerve$eating = false;
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER))
    private void swerve$applyOffset(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                    float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack,
                                    MultiBufferSource buffer, int light, CallbackInfo ci) {
        ViewModel.applyOffset(poseStack, hand);
    }

    @WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FFLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"))
    private void swerve$swingArm(ItemInHandRenderer instance, float swingProgress, float equippedProgress, PoseStack poseStack,
                                 int direction, HumanoidArm arm, Operation<Void> original) {
        InteractionHand hand = swerve$hand;
        if (hand != null && !swerve$using && SwingAnimation.apply(poseStack, hand, swingProgress)) {
            ViewModel.applyScale(poseStack, hand);
            return;
        }
        original.call(instance, swingProgress, equippedProgress, poseStack, direction, arm);
    }

    @WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyEatTransform(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/world/entity/HumanoidArm;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V"))
    private void swerve$eatTransform(ItemInHandRenderer instance, PoseStack poseStack, float partialTick, HumanoidArm arm,
                                     ItemStack stack, Player player, Operation<Void> original) {
        if (ViewModel.suppressEatAnimation()) {
            return;
        }
        original.call(instance, poseStack, partialTick, arm, stack, player);
    }

    @ModifyVariable(method = "applyItemArmTransform", at = @At("HEAD"), argsOnly = true)
    private float swerve$equipProgress(float equippedProgress) {
        return swerve$hand != null && swerve$using && ViewModel.suppressEatAnimation() ? 0.0F : equippedProgress;
    }

    @Inject(method = "applyItemArmTransform", at = @At("TAIL"))
    private void swerve$afterArmTransform(PoseStack poseStack, HumanoidArm arm, float equippedProgress, CallbackInfo ci) {
        InteractionHand hand = swerve$hand;
        if (hand == null) {
            return;
        }
        ViewModel.applyScale(poseStack, hand);
        if (swerve$eating && ViewModel.suppressEatAnimation()) {
            swerve$applyInPlaceEat(poseStack, arm);
        }
    }

    // Пустая рука не проходит через applyItemArmTransform: масштабируем её после первого
    // translate в renderPlayerArm, чтобы пивот совпадал с кистью, а не с камерой.
    @Inject(method = "renderPlayerArm", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0, shift = At.Shift.AFTER))
    private void swerve$scaleEmptyArm(PoseStack poseStack, MultiBufferSource buffer, int light, float equippedProgress,
                                      float swingProgress, HumanoidArm arm, CallbackInfo ci) {
        ViewModel.applyScale(poseStack, InteractionHand.MAIN_HAND);
    }

    // Ванильная анимация еды тянет предмет к центру экрана, что ломает сдвинутую руку.
    // Повторяем её покачивание и повороты на месте, вокруг примерного центра предмета.
    @Unique
    private void swerve$applyInPlaceEat(PoseStack poseStack, HumanoidArm arm) {
        AbstractClientPlayer player = swerve$player;
        ItemStack stack = swerve$stack;
        if (player == null || stack == null) {
            return;
        }
        int useDuration = stack.getUseDuration(player);
        if (useDuration <= 0) {
            return;
        }

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float centerX = 0.070625F * side;
        float centerY = 0.2F;
        float centerZ = 0.070625F;

        float useLeft = player.getUseItemRemainingTicks() - swerve$partialTick + 1.0F;
        float progress = useLeft / useDuration;
        if (progress < 0.8F) {
            poseStack.translate(0.0F, Mth.abs(Mth.cos(useLeft / 4.0F * Mth.PI) * 0.1F), 0.0F);
        }

        float jiggle = 1.0F - (float) Math.pow(progress, 27.0);
        poseStack.translate(-side * jiggle * 0.2F, jiggle * -0.05F, 0.0F);
        poseStack.translate(centerX, centerY, centerZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(side * jiggle * 90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(jiggle * 10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * jiggle * 30.0F));
        poseStack.translate(-centerX, -centerY, -centerZ);
    }
}
