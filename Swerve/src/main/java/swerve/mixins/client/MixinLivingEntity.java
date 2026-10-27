package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.impl.movement.Speed;
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

    // Speed: единственный член, из которого vanilla берёт приращение скорости в travel()
    // (LivingEntity.getFrictionInfluencedSpeed). Правим его — и разгон идёт через ванильное
    // трение, а не через подмену deltaMovement после интегрирования. Обоснование и границы:
    // docs/Speed.md §2.
    //
    // Проверка типа идёт первой: метод вызывается каждый тик для ВСЕХ живых сущностей,
    // и для мобов дальнейшая работа здесь бессмысленна.
    @ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At("RETURN"))
    private float swerve$speedAcceleration(float original) {
        if (!((Object) this instanceof LocalPlayer player)) {
            return original;
        }
        Speed speed = ModuleManager.get(Speed.class);
        if (speed == null || !speed.isEnable()) {
            return original;
        }
        try {
            return speed.onAcceleration(original, player);
        } catch (Exception exception) {
            Swerve.LOGGER.error("Module 'Speed' failed while scaling acceleration", exception);
            speed.setEnabled(false);
            return original;
        }
    }
}
