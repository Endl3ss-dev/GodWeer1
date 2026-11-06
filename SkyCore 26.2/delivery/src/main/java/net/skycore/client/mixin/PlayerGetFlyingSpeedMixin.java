package net.skycore.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.player.Player;
import net.skycore.client.movement.speed.SpeedFlyingSpeed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

 
@Mixin(Player.class)
public abstract class PlayerGetFlyingSpeedMixin {
   @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
   private float skycore$speedFlyingSpeed(float original) {
      return SpeedFlyingSpeed.apply((Player)(Object)this, original);
   }
}
