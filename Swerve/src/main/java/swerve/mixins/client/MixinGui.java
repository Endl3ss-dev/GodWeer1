package swerve.mixins.client;

import swerve.modules.impl.render.SpatialGui;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.gui.Gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: HUD рисуется в ванильном масштабе (skipWindowOverride), по настройкам скрываются
// HUD целиком, хотбар с декорациями и прицел (в режиме курсора от первого лица).
@Mixin(Gui.class)
public class MixinGui {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialHudBegin(CallbackInfo ci) {
        SpatialState.skipWindowOverride = true;
        if (SpatialGui.shouldHideHud()) {
            SpatialState.skipWindowOverride = false;
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void swerve$spatialHudEnd(CallbackInfo ci) {
        SpatialState.skipWindowOverride = false;
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialCrosshair(CallbackInfo ci) {
        if (SpatialGui.shouldHideCrosshair()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderHotbarAndDecorations", "renderExperienceLevel"}, at = @At("HEAD"), cancellable = true)
    private void swerve$spatialHotbar(CallbackInfo ci) {
        if (SpatialGui.shouldHideHotbar()) {
            ci.cancel();
        }
    }
}
