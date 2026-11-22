package tech.lightclient.mixins.render;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventHeldItemRenderer;
import tech.lightclient.modules.impl.render.SwingAnimations;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @ModifyVariable(method = "renderFirstPersonItem", at = @At("HEAD"), argsOnly = true)
    private ItemStack light$modifyAutoToolStack(ItemStack stack, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                                                 float swingProgress, ItemStack original, float equipProgress, MatrixStack matrices,
                                                 OrderedRenderCommandQueue vertexConsumers, int light) {
        if (hand == Hand.MAIN_HAND) {
            var autoTool = LightClient.getFunctionManager().getAutoTool();
            if (autoTool != null && autoTool.isState() && autoTool.invisible.get()) {
                ItemStack visual = autoTool.getVisualStack();
                if (!visual.isEmpty()) {
                    return visual;
                }
            }
        }
        return stack;
    }

    @Inject(method = "swingArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applySwingOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"))
    private void light$onVanillaSwing(float swingProgress, MatrixStack matrices, int side, Arm arm, CallbackInfo ci) {
        if (mc.player == null || !LightClient.getFunctionManager().getViewModel().isState()) return;

        Hand hand = arm == mc.player.getMainArm() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        EventHeldItemRenderer event = new EventHeldItemRenderer(hand, ItemStack.EMPTY, 0, matrices);
        LightClient.getEventBus().post(event);
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at = @At("HEAD"))
    private void syncHandRotation(float tickDelta, MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers,
                                   ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (mc.player != null &&
                LightClient.getFunctionManager().getTweaks().isState() &&
                LightClient.getFunctionManager().getTweaks().freezeHands.get()) {
            mc.player.renderYaw      = mc.player.getYaw();
            mc.player.lastRenderYaw  = mc.player.getYaw();
            mc.player.renderPitch    = mc.player.getPitch();
            mc.player.lastRenderPitch = mc.player.getPitch();
        }
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
                    ordinal = 4
            ),
            cancellable = true
    )
    private void light$renderSwingAnimations(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                                               float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                                               OrderedRenderCommandQueue vertexConsumers, int light, CallbackInfo ci) {
        SwingAnimations swingAnimations = LightClient.getFunctionManager().getSwingAnimations();
        if (swingAnimations == null || !swingAnimations.isState() || !swingAnimations.shouldRenderForTarget()) return;
        if (hand != Hand.MAIN_HAND) return;

        Arm arm = player.getMainArm();
        ItemDisplayContext displayContext = arm == Arm.RIGHT
                ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        // SwingAnimations отменяет ванильный swingArm, поэтому EventHeldItemRenderer
        // постим здесь — иначе ViewModel не получает свой matrices и не применяется.
        Runnable renderItem = () -> {
            if (mc.player != null && LightClient.getFunctionManager().getViewModel() != null
                    && LightClient.getFunctionManager().getViewModel().isState()) {
                LightClient.getEventBus().post(new EventHeldItemRenderer(hand, item, equipProgress, matrices));
            }
            ((HeldItemRenderer) (Object) this).renderItem(
                    player, item, displayContext, matrices, vertexConsumers, light
            );
        };

        swingAnimations.renderSwordAnimation(matrices, tickDelta, swingProgress, equipProgress, arm, true, renderItem);
        matrices.pop();
        ci.cancel();
    }
}
