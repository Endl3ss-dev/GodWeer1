package net.skycore.client.movement.speed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.skycore.client.mixin.LocalPlayerCanStartSprintingAccessor;
import net.skycore.client.module.movement.SpeedModule;

 
public final class SwerveSpeed extends SpeedHandler {
   private static final float MIN_MULTIPLIER = 1.0F;
   private static final float MAX_MULTIPLIER = 1.6F;

   public SwerveSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onClientTick() {
      if (!this.module.swerveAutoSprint()) {
         return;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || player.isSprinting()) {
         return;
      }

      if (!player.onGround() && !player.isUnderWater()) {
         return;
      }

      if (((LocalPlayerCanStartSprintingAccessor)player).skycore$canStartSprinting()) {
         player.setSprinting(true);
      }
   }

    
   public float onAcceleration(float acceleration, LivingEntity entity) {
      float factor = this.clampMultiplier((float)this.module.swerveMultiplier());
      if (factor <= MIN_MULTIPLIER) {
         return acceleration;
      }

      if (acceleration == 0.0F || !Float.isFinite(acceleration)) {
         return acceleration;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || entity != player) {
         return acceleration;
      }

      if (player.getAbilities().flying || player.isPassenger()) {
         return acceleration;
      }

      if (this.module.swerveOnlyGround() && !player.onGround()) {
         return acceleration;
      }

      if (this.module.swerveOnlyForward()) {
         if (player.input == null) {
            return acceleration;
         }

         Vec2 move = player.input.getMoveVector();
         if (move.y <= 0.0F) {
            return acceleration;
         }
      }

      return acceleration * factor;
   }

   private float clampMultiplier(float value) {
      if (!Float.isFinite(value) || value < MIN_MULTIPLIER) {
         return MIN_MULTIPLIER;
      }

      return value > MAX_MULTIPLIER ? MAX_MULTIPLIER : value;
   }
}
