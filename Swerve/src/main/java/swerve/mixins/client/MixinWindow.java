package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.mojang.blaze3d.platform.Window;

import swerve.utils.render.spatial.SpatialScale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// SpatialGui: перехваченный экран живёт в масштабе панели («авто» от окна), а не в настройке игрока.
// Подмена выключена на время отрисовки мира и HUD флагом SpatialState.skipWindowOverride.
@Mixin(Window.class)
public abstract class MixinWindow {

    @ModifyReturnValue(method = "getGuiScale", at = @At("RETURN"))
    private double swerve$spatialGuiScale(double original) {
        return SpatialScale.shouldOverride() ? SpatialScale.effective((Window) (Object) this) : original;
    }

    @ModifyReturnValue(method = "getGuiScaledWidth", at = @At("RETURN"))
    private int swerve$spatialScaledWidth(int original) {
        return SpatialScale.shouldOverride() ? SpatialScale.scaledWidth((Window) (Object) this) : original;
    }

    @ModifyReturnValue(method = "getGuiScaledHeight", at = @At("RETURN"))
    private int swerve$spatialScaledHeight(int original) {
        return SpatialScale.shouldOverride() ? SpatialScale.scaledHeight((Window) (Object) this) : original;
    }
}
