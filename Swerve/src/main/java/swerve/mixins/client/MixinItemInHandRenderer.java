package swerve.mixins.client;

import swerve.modules.impl.render.SwingAnimation;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Перехватывает обычный взмах руки в ItemInHandRenderer#renderArmWithItem.
 *
 * ordinal = 2 — это вызов в финальном else, то есть взмах без использования предмета,
 * без блока, без riptide и не для BUNDLE. Два других вызова (ordinal 0 и 1) не трогаем,
 * иначе ломаются анимации лука, еды и мешка.
 *
 * Сигнатура сверена с Mojang-маппингами 1.21.4:
 *   private void swingArm(float attack, float inverseArmHeight, PoseStack, int invert, HumanoidArm)
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    protected abstract void swingArm(float attack, float inverseArmHeight, PoseStack poseStack, int invert, HumanoidArm arm);

    @Redirect(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FFLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V",
                    ordinal = 2
            )
    )
    private void swerve$swingArm(ItemInHandRenderer self, float attack, float inverseArmHeight, PoseStack poseStack, int invert, HumanoidArm arm) {
        if (!SwingAnimation.transform(poseStack, attack, inverseArmHeight, invert, arm)) {
            this.swingArm(attack, inverseArmHeight, poseStack, invert, arm);
        }
    }
}
