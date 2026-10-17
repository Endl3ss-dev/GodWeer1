package swerve.mixins.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import swerve.cosmetics.melee.MeleeRenderer;
import swerve.cosmetics.melee.MeleeState;

/**
 * Ножи от первого лица: анимированная модель заменяет меч в руке.
 *
 * <p>Сигнатуры — mojmap 1.21.4: renderHandsWithItems и renderArmWithItem.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinMeleeHand {

    @Inject(
            method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"
                    + "Lnet/minecraft/client/player/LocalPlayer;I)V",
            at = @At("HEAD"))
    private void swerve$beginMeleeHands(float tickProgress, PoseStack pose,
                                       MultiBufferSource.BufferSource buffer, LocalPlayer player,
                                       int light, CallbackInfo ci) {
        MeleeRenderer.getInstance().beginHands();
    }

    // В 1.21.4 нет рендера очереди кадров, поэтому отложенный предмет рисуется в конце метода.
    @Inject(
            method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"
                    + "Lnet/minecraft/client/player/LocalPlayer;I)V",
            at = @At("RETURN"))
    private void swerve$endMeleeHands(float tickProgress, PoseStack pose,
                                      MultiBufferSource.BufferSource buffer, LocalPlayer player,
                                      int light, CallbackInfo ci) {
        MeleeRenderer.getInstance().flushHandItem(player, pose, buffer, light);
    }

    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FF"
                    + "Lnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;F"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void swerve$meleeSkin(AbstractClientPlayer player, float tickProgress, float pitch,
                                  InteractionHand hand, float swingProgress, ItemStack item,
                                  float equipProgress, PoseStack pose, MultiBufferSource buffer,
                                  int light, CallbackInfo ci) {
        if (player.isScoping()) {
            return;
        }
        if (MeleeState.hidesEmptyMainHand(player, hand, item)) {
            ci.cancel();
        } else if (MeleeState.rendersFirstPerson(item)) {
            // Анимация ножа заменяет опускание предмета, остальные предметы опускаются как обычно.
            float retire = MeleeState.holdsSkinned(hand) ? 0.0F : equipProgress;
            if (MeleeRenderer.getInstance().renderFirstPerson(player, hand, pose, buffer, light, retire)) {
                ci.cancel();
            }
        } else if (MeleeState.attachesHandItem(player, hand, item)) {
            if (MeleeRenderer.getInstance().deferHandItem(item, equipProgress)) {
                ci.cancel();
            }
        }
    }
}
