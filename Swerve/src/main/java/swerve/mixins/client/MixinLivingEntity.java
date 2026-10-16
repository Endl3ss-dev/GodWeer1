package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// SpatialGui: толчок спринт-прыжка идёт по направлению камеры панели, как и движение WASD.
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @ModifyExpressionValue(method = "jumpFromGround", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"))
    private float swerve$spatialSprintJump(float yaw) {
        if (!((Object) this instanceof LocalPlayer)) {
            return yaw;
        }
        if (!SpatialState.active() || SpatialState.hooked == null || !SpatialState.shouldCapture()) {
            return yaw;
        }
        return yaw + SpatialState.cameraYawOffset;
    }
}
