package swerve.mixins.render;

import swerve.utils.render.spatial.CameraHandle;
import swerve.utils.rotation.FreeLook;
import swerve.utils.movement.ElytraFlightRotation;
import swerve.utils.render.spatial.SpatialCamera;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: после ванильной установки камеры её позиция/углы переопределяются логикой панели.
// setPosition/setRotation ванильные: вместе с ними обновляются blockPosition и векторы взгляда.
@Mixin(Camera.class)
public abstract class MixinCamera implements CameraHandle {

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    // Adapted from AlphaDLC/src/main/java/zov/alphadlc/mixin/CameraMixin.java
    @Redirect(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void swerve$elytraCameraRotation(Camera camera, float yaw, float pitch,
            BlockGetter level, Entity entity, boolean detached, boolean mirrored, float partialTick) {
        // Do not apply the local player's free look to spectator cameras.
        if (entity != Minecraft.getInstance().player) {
            setRotation(yaw, pitch);
            return;
        }
        ElytraFlightRotation.captureCamera(yaw, pitch);
        if (FreeLook.active()) {
            // [+] Молчаливая ротация ауры: камера смотрит туда, куда её увёл игрок,
            // тело при этом уже развёрнуто на цель (FreeLookComponent из Private).
            yaw = FreeLook.yaw();
            pitch = FreeLook.pitch();
        } else if (ElytraFlightRotation.isActive()) {
            yaw = ElytraFlightRotation.getFreeYaw();
            pitch = ElytraFlightRotation.getFreePitch();
        }
        // Match AlphaDLC's RotationEvent order, including front-facing third person.
        if (detached && mirrored) {
            yaw += 180;
            pitch = -pitch;
        }
        setRotation(yaw, pitch);
    }

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
