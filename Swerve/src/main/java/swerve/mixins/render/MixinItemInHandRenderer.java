package swerve.mixins.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.impl.render.ViewModel;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Руки от первого лица: смещения/масштаб ViewModel, полная замена взмаха (без опускания
// руки) и гашение сдвига пустой руки. Цели — renderArmWithItem, swingArm,
// applyItemArmTransform, renderPlayerArm; сигнатуры — mojmap 1.21.4.
@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

    // Единственный pushPose в renderArmWithItem: здесь ещё не применена ни одна ветка
    // анимации, поэтому смещения одинаковы для предмета, пустой руки, карты, арбалета,
    // блока, лука, еды и riptide.
    @Inject(method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER))
    private void swerve$applyViewModel(AbstractClientPlayer player, float partialTick, float pitch,
                                       InteractionHand hand, float swingProgress, ItemStack item,
                                       float equippedProgress, PoseStack poseStack, MultiBufferSource buffer,
                                       int combinedLight, CallbackInfo ci) {
        ViewModel.applyTransform(poseStack, player, hand);
    }

    // Ванильный swingArm не вызывается вовсе, пока модуль включён: он и делает сдвиг руки
    // вниз (-0.4/0.2/-0.2 * sin) с доворотом -80° по X.
    @WrapOperation(method = "renderArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FFLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"))
    private void swerve$swing(ItemInHandRenderer instance, float swingProgress, float equippedProgress,
                              PoseStack poseStack, int armX, HumanoidArm arm, Operation<Void> original) {
        if (!ViewModel.applySwing(poseStack, arm, swingProgress)) {
            original.call(instance, swingProgress, equippedProgress, poseStack, armX, arm);
        }
    }

    // equippedProgress = 0 — рука сразу в финальной позиции: при смене предмета не падает.
    @WrapOperation(method = "renderArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"))
    private void swerve$equip(ItemInHandRenderer instance, PoseStack poseStack, HumanoidArm arm,
                              float equippedProgress, Operation<Void> original) {
        original.call(instance, poseStack, arm, ViewModel.equipProgress(equippedProgress));
    }

    // Пустая рука: renderPlayerArm считает свой сдвиг сам, поэтому в «Статично» взмах
    // обнуляется аргументом, а в остальных пресетах сдвиг гасится обратным translate.
    @WrapOperation(method = "renderArmWithItem", require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V"))
    private void swerve$bareArm(ItemInHandRenderer instance, PoseStack poseStack, MultiBufferSource buffer,
                                int combinedLight, float equippedProgress, float swingProgress, HumanoidArm arm,
                                Operation<Void> original) {
        float progress = ViewModel.staticSwing() ? 0.0F : swingProgress;
        ViewModel.cancelArmDip(poseStack, arm, progress);
        original.call(instance, poseStack, buffer, combinedLight, ViewModel.equipProgress(equippedProgress),
                progress, arm);
    }
}
