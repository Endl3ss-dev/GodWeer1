package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Wild MovementPhysics.process(double) (ru/wild/util/player/MovementPhysics.java).
 * Ставит горизонталь по вводу без нормализации диагонали — как в источнике.
 */
public final class WildMovementPhysics {
   public static void process(double speed) {
      LocalPlayer player = SpeedAction.player();
      if (player == null || player.input == null) {
         return;
      }

      float yaw = player.getYRot();
      double radians = Math.toRadians(yaw);
      double x = 0.0;
      double z = 0.0;
      Vec2 movement = player.input.getMoveVector();
      float forward = movement.y;
      float strafe = movement.x;

      if (forward > 0.0F) {
         x -= Math.sin(radians) * speed;
         z += Math.cos(radians) * speed;
      }

      if (forward < 0.0F) {
         x += Math.sin(radians) * speed;
         z -= Math.cos(radians) * speed;
      }

      if (strafe > 0.0F) {
         x += Math.cos(radians) * speed;
         z += Math.sin(radians) * speed;
      }

      if (strafe < 0.0F) {
         x -= Math.cos(radians) * speed;
         z -= Math.sin(radians) * speed;
      }

      if (strafe > 0.0F && forward > 0.0F) {
         x = Math.cos(Math.toRadians(yaw + 45.0F)) * speed;
         z = Math.sin(Math.toRadians(yaw + 45.0F)) * speed;
      }

      if (strafe < 0.0F && forward > 0.0F) {
         x = -Math.cos(Math.toRadians(yaw - 45.0F)) * speed;
         z = -Math.sin(Math.toRadians(yaw - 45.0F)) * speed;
      }

      if (strafe > 0.0F && forward < 0.0F) {
         x = -Math.cos(Math.toRadians(yaw + 135.0F)) * speed;
         z = -Math.sin(Math.toRadians(yaw + 135.0F)) * speed;
      }

      if (strafe < 0.0F && forward < 0.0F) {
         x = Math.cos(Math.toRadians(yaw - 135.0F)) * speed;
         z = Math.sin(Math.toRadians(yaw - 135.0F)) * speed;
      }

      Vec3 velocity = player.getDeltaMovement();
      player.setDeltaMovement(x, velocity.y, z);
   }

   private WildMovementPhysics() {
   }
}
