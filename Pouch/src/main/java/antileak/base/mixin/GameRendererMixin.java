package antileak.base.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import antileak.base.api.events.EventInvoker;
import antileak.base.api.events.implement.EventAspectRatio;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.client.modules.impl.render.Aspect;
import antileak.base.client.modules.impl.render.Removals;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow private float zoom;
    @Shadow private float zoomX;
    @Shadow private float zoomY;

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void punch$hideTotemAnimation(ItemStack stack, CallbackInfo ci) {
        if (ModuleClass.INSTANCE == null || stack == null || !stack.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        Removals removals = ModuleClass.removals;
        if (removals != null && removals.isTotemAnimationDisabled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
            )
    )
    private void punch$captureBlurBeforeHud(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        RenderUtils.beginLiquidBlurFrame();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void punch$resetHurtTime(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (ModuleClass.INSTANCE == null) return;

        Removals removals = ModuleClass.removals;
        if (removals == null || !removals.isScreenShakeDisabled()) return;

        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.hurtTime = 0;
        }
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    private void punch$getBasicProjectionMatrix(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        if (ModuleClass.INSTANCE == null) return;

        if (EventInvoker.hasListeners(EventAspectRatio.class)) {
            EventAspectRatio event = new EventAspectRatio();
            event.call();

            if (event.isCancelled()) {
                float customRatio = Aspect.INSTANCE.getCustomAspectRatio();
                if (customRatio > 0.01f) {
                    Matrix4f matrix4f = new Matrix4f();
                    if (zoom != 1.0f) {
                        matrix4f.translate(zoomX, -zoomY, 0.0f);
                        matrix4f.scale(zoom, zoom, 1.0f);
                    }
                    matrix4f.perspective(fovDegrees * 0.01745329238474369F, customRatio, 0.05f, getFarPlaneDistance());
                    cir.setReturnValue(matrix4f);
                }
            }
        }
    }

    @Shadow
    public float getFarPlaneDistance() { return 0f; }
}
