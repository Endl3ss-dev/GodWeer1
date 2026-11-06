package net.skycore.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.skycore.client.module.movement.SpeedModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

 
@Mixin(LivingEntity.class)
public abstract class LivingEntityFrictionSpeedMixin {
   @ModifyExpressionValue(
      method = "handleRelativeFrictionAndCalculateMovement",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFrictionInfluencedSpeed(F)F")
   )
   private float skycore$swerveFriction(float value) {
      return SpeedModule.swerveAcceleration(value, (LivingEntity)(Object)this);
   }
}
