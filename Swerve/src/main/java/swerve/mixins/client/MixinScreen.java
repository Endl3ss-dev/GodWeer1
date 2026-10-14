package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import swerve.utils.render.spatial.SpatialScale;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: перехват экрана при init, раскладка под масштаб панели, без ванильного фона/блюра —
// панель прозрачна вокруг самого GUI.
@Mixin(Screen.class)
public abstract class MixinScreen {

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    private void swerve$spatialHook(Minecraft minecraft, int width, int height, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (SpatialState.hookable(screen)) {
            SpatialState.hookScreen(screen);
        }
    }

    @ModifyVariable(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int swerve$spatialInitWidth(int width) {
        return SpatialScale.fixSize((Screen) (Object) this, width, true);
    }

    @ModifyVariable(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int swerve$spatialInitHeight(int height) {
        return SpatialScale.fixSize((Screen) (Object) this, height, false);
    }

    @ModifyVariable(method = "resize", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int swerve$spatialResizeWidth(int width) {
        return SpatialScale.fixSize((Screen) (Object) this, width, true);
    }

    @ModifyVariable(method = "resize", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int swerve$spatialResizeHeight(int height) {
        return SpatialScale.fixSize((Screen) (Object) this, height, false);
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialNoBackground(CallbackInfo ci) {
        if (SpatialState.capturing()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialNoBlur(CallbackInfo ci) {
        if (SpatialState.capturing()) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "renderTransparentBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"))
    private void swerve$spatialNoGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2, int colorFrom, int colorTo,
                                          Operation<Void> original) {
        if (SpatialState.hooked == (Object) this && SpatialState.capturing()) {
            return;
        }
        original.call(graphics, x1, y1, x2, y2, colorFrom, colorTo);
    }
}
