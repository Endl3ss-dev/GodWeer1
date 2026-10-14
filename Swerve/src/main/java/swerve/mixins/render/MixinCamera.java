package swerve.mixins.render;

import swerve.utils.render.spatial.CameraHandle;
import swerve.utils.render.spatial.SpatialCamera;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: после ванильной установки камеры её позиция/углы переопределяются логикой панели.
// setPosition/setRotation ванильные: вместе с ними обновляются blockPosition и векторы взгляда.
@Mixin(Camera.class)
public abstract class MixinCamera implements CameraHandle {

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "setup", at = @At("TAIL"))
    private void swerve$spatialCamera(BlockGetter level, Entity entity, boolean detached, boolean mirrored,
                                      float partialTick, CallbackInfo ci) {
        SpatialCamera.onSetup((Camera) (Object) this, entity, partialTick);
    }

    @Override
    public void swerve$applyCamera(double x, double y, double z, float yaw, float pitch) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }
}
