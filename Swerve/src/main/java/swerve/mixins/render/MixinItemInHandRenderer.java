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

// Руки от первого лица: смещения/масштаб ViewModel, взмах без опускания руки и
// гашение сдвига пустой руки. Цели — renderArmWithItem, swingArm, applyItemArmTransform,
// renderPlayerArm; сигнатуры соответствуют mojmap 1.21.4.
@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

    // Единственный pushPose в renderArmWithItem: точка, где ещё не применена ни одна
    // ветка анимации, поэтому смещения действуют одинаково для предмета, пустой руки,
    // карты, арбалета, блока, лука, еды и riptide.
    @Inject(method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER))
    private void swerve$applyViewModel(AbstractClientPlayer player, float partialTick, float pitch,
                                       InteractionHand hand, float swingProgress, ItemStack item,
                                       float equippedProgress, PoseStack poseStack, MultiBufferSource buffer,
                                       int combinedLight, CallbackInfo ci) {
        ViewModel.applyTransform(poseStack, player, hand);
    }

    // Ванильный swingArm: сдвиг руки вниз/в сторону + доворот на -80° по X.
    // Здесь он заменяется своей версией, где сдвиг и наклон обнулены по умолчанию.
    @WrapOperation(method = "renderArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FFLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V"))
    private void swerve$swing(ItemInHandRenderer instance, float swingProgress, float equippedProgress,
                              PoseStack poseStack, int armX, HumanoidArm arm, Operation<Void> original) {
        if (!ViewModel.applySwing(poseStack, arm, armX, swingProgress, equippedProgress)) {
            original.call(instance, swingProgress, equippedProgress, poseStack, armX, arm);
        }
    }

    // equippedProgress = 0 — рука в финальной позиции: при смене предмета она не падает вниз.
    @WrapOperation(method = "renderArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"))
    private void swerve$equip(ItemInHandRenderer instance, PoseStack poseStack, HumanoidArm arm,
                              float equippedProgress, Operation<Void> original) {
        original.call(instance, poseStack, arm, ViewModel.equipProgress(equippedProgress));
    }

    // То же самое внутри ванильного swingArm: без этого «Без опускания при смене»
    // работало бы во всех ветках, кроме взмаха предметом.
    @WrapOperation(method = "swingArm", require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"))
    private void swerve$equipInSwing(ItemInHandRenderer instance, PoseStack poseStack, HumanoidArm arm,
                                     float equippedProgress, Operation<Void> original) {
        original.call(instance, poseStack, arm, ViewModel.equipProgress(equippedProgress));
    }

    // Пустая рука: renderPlayerArm считает свой сдвиг сам, поэтому в «Статично» взмах
    // обнуляется аргументом, а в «На месте» сдвиг гасится обратным translate.
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
