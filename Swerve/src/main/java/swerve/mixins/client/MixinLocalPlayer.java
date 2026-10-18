package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import swerve.utils.render.spatial.SpatialAvatar;
import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.impl.movement.ElytraFlight;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: от первого лица покачивание рук следует за камерой панели, а не за поворотом игрока.
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {

    @Shadow
    public float xBob;

    @Shadow
    public float yBob;

    @Shadow
    public float yBobO;

    @Shadow
    protected abstract boolean isControlledCamera();

    @Unique
    private float swerve$xBobBefore;

    @Unique
    private float swerve$yBobBefore;

    // Adapted from AlphaDLC/src/main/java/zov/alphadlc/mixin/ClientPlayerEntityMixin.java
    @Inject(method = "tick", at = @At("HEAD"))
    private void swerve$elytraPlayerUpdate(CallbackInfo ci) {
        ElytraFlight flight = ModuleManager.get(ElytraFlight.class);
        if (flight != null && flight.isEnable()) {
            try {
                flight.onPlayerUpdate();
            } catch (Exception exception) {
                Swerve.LOGGER.error("Module 'ElytraFlight' failed during player update", exception);
                flight.setEnabled(false);
            }
        }
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"))
    private void swerve$spatialCaptureBob(CallbackInfo ci) {
        swerve$xBobBefore = this.xBob;
        swerve$yBobBefore = this.yBob;
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void swerve$spatialBobFollowsCamera(CallbackInfo ci) {
        if (!SpatialAvatar.swayActive() || !this.isControlledCamera()) {
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        this.xBob = swerve$xBobBefore + (camera.getXRot() - swerve$xBobBefore) * 0.5F;
        this.yBob = swerve$yBobBefore + Mth.wrapDegrees(camera.getYRot() - swerve$yBobBefore) * 0.5F;
    }

    @ModifyReturnValue(method = "getViewXRot", at = @At("RETURN"))
    private float swerve$spatialViewXRot(float original, float partialTick) {
        if (!SpatialAvatar.swayActive()) {
            return original;
        }
        return Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
    }

    @ModifyReturnValue(method = "getViewYRot", at = @At("RETURN"))
    private float swerve$spatialViewYRot(float original, float partialTick) {
        if (!SpatialAvatar.swayActive()) {
            return original;
        }
        float bob = Mth.lerp(partialTick, this.yBobO, this.yBob);
        return bob + Mth.wrapDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() - bob);
    }
}
