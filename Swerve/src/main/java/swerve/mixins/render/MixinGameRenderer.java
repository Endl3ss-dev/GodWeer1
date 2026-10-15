package swerve.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.impl.render.HandAnimations;
import swerve.modules.impl.render.SpatialGui;
import swerve.utils.render.anim.HandMotion;
import swerve.utils.render.spatial.ScreenCapture;
import swerve.utils.render.spatial.ScreenSurface;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: кадр панели. Порядок внутри render(): HEAD (начало кадра) → renderLevel →
// renderItemInHand HEAD (панель под руками) → INVOKE Gui.render (панель под HUD) →
// renderWithTooltip (съёмка экрана в текстуру вместо отрисовки) → TAIL. Сигнатуры — mojmap 1.21.4.
@Mixin(value = GameRenderer.class, priority = 1100)
public abstract class MixinGameRenderer {

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float swerve$spatialFov(float original, Camera camera, float partialTick, boolean useFovSetting) {
        if (!useFovSetting) {
            return original;
        }
        float factor = SpatialState.fovProgress();
        SpatialGui module = SpatialGui.get();
        if (factor <= 0.0F || module == null) {
            return original;
        }
        return Mth.lerp(factor, original, module.targetFov());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void swerve$spatialFrameStart(CallbackInfo ci) {
        SpatialState.beginFrame();
    }

    @Inject(method = "processBlurEffect", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialNoBlur(CallbackInfo ci) {
        if (SpatialState.capturing()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void swerve$spatialDrawBeforeHud(CallbackInfo ci) {
        if (SpatialState.active() && (SpatialState.shouldCapture() || ScreenSurface.isClosing())) {
            ScreenSurface.renderInWorldPost();
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/PostChain;process(Lcom/mojang/blaze3d/pipeline/RenderTarget;Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;)V"))
    private void swerve$spatialPostEffect(PostChain chain, RenderTarget target, GraphicsResourceAllocator allocator,
                                          Operation<Void> original) {
        if (SpatialState.capturing()) {
            return;
        }
        original.call(chain, target, allocator);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void swerve$spatialScreenRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                            Operation<Void> original) {
        if (SpatialState.active() && screen == SpatialState.hooked && SpatialState.hookable(screen)) {
            SpatialState.skipWindowOverride = false;
            ScreenCapture.extract(screen, partialTick, original);
            SpatialState.skipWindowOverride = true;
            ScreenSurface.renderInWorldPost();
            return;
        }
        original.call(screen, graphics, mouseX, mouseY, partialTick);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void swerve$spatialFrameEnd(CallbackInfo ci) {
        if (SpatialState.capturing()) {
            SpatialState.skipWindowOverride = true;
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialHands(CallbackInfo ci) {
        if (!SpatialState.active()) {
            return;
        }
        boolean closing = ScreenSurface.isClosing();
        if (!SpatialState.shouldCapture() && !closing) {
            return;
        }
        ScreenSurface.renderInWorldPost();
        boolean firstPerson = closing ? ScreenSurface.isClosingFirstPerson() : SpatialState.effectiveFirstPerson;
        SpatialGui module = SpatialGui.get();
        if (firstPerson && module != null && module.hideHands()) {
            ci.cancel();
        }
    }

    // [+] HandAnimations: боббинг рук при выключенном View Bobbing — единственный booleanValue() в методе.
    @ModifyExpressionValue(method = "renderItemInHand", at = @At(value = "INVOKE",
            target = "Ljava/lang/Boolean;booleanValue()Z"))
    private boolean swerve$forceHandBob(boolean original) {
        return original || HandAnimations.forceHandBob();
    }

    // [+] HandAnimations: множитель ванильного боббинга рук (только для рук, камера не трогается).
    @WrapOperation(method = "renderItemInHand", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private void swerve$handBob(GameRenderer instance, PoseStack poseStack, float partialTick, Operation<Void> original) {
        HandAnimations module = HandAnimations.get();
        if (module == null || !HandAnimations.overrideHandBob()) {
            original.call(instance, poseStack, partialTick);
            return;
        }
        HandMotion.applyHandBob(poseStack, partialTick, module.handBobScale());
    }
}
