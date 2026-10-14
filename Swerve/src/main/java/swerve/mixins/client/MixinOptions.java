package swerve.mixins.client;

import swerve.modules.impl.render.SpatialGui;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SpatialGui: пока открыта панель, игра видит камеру режима панели (первое/третье лицо),
// а во время обратного перехода из третьего лица — ещё третье, чтобы модель игрока не исчезла рывком.
@Mixin(Options.class)
public abstract class MixinOptions {

    @Shadow
    private CameraType cameraType;

    @Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialCameraType(CallbackInfoReturnable<CameraType> cir) {
        if (!SpatialState.active()) {
            return;
        }
        if (SpatialState.shouldCapture()) {
            SpatialGui module = SpatialGui.get();
            if (module.autoCamera()) {
                return;
            }
            SpatialState.wasThirdPersonCamera = this.cameraType != CameraType.FIRST_PERSON;
            cir.setReturnValue(module.useFirstPerson() ? CameraType.FIRST_PERSON : CameraType.THIRD_PERSON_BACK);
        } else if (SpatialState.isClosingThirdToFirstPerson()) {
            cir.setReturnValue(CameraType.THIRD_PERSON_BACK);
        }
    }
}
