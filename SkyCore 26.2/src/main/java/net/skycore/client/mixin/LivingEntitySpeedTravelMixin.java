package net.skycore.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.SpeedModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** PlayerTravelEvent источников: pre-travel хук для Grim/HolyWorld/RW спидов. */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySpeedTravelMixin {
   @Inject(method = "travel", at = @At("HEAD"))
   private void skycore$speedTravelPre(Vec3 position, CallbackInfo callbackInfo) {
      if ((Object)this instanceof LocalPlayer player) {
         SpeedModule.INSTANCE.onTravelPre(player);
      }
   }
}
