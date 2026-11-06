package net.skycore.client.movement.speed;

import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.SpeedModule;

/**
 * Rockstar Speed (moscow/rockstar/module/movement/Speed.java, 125 строк).
 * Единственный режим "Сущности": буст в сторону ближайшей сущности, сила от числа коллизий.
 */
public final class RockstarSpeed extends SpeedHandler {
   public RockstarSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      this.updateEntitySpeed(player);
   }

   private void updateEntitySpeed(LocalPlayer player) {
      int collisions = this.countCollisions(player);
      if (collisions <= 0 || (this.module.rockstarRequireMoving() && !SpeedAction.isMoving())) {
         return;
      }

      double finalSpeed = this.module.rockstarSpeed() * 0.01 * (double)collisions;
      if (finalSpeed <= 0.0) {
         return;
      }

      Entity nearest = this.findNearestEntity(player);
      if (nearest == null) {
         return;
      }

      Vec3 direction = this.getDirectionToPoint(player.position(), nearest.position(), finalSpeed);
      player.push(direction.x, 0.0, direction.z);
   }

   private int countCollisions(LocalPlayer player) {
      AABB expandedBox = player.getBoundingBox().inflate(this.module.rockstarExpand());
      List<Entity> entities = player.level().getEntities(player, expandedBox);
      int collisions = 0;

      for (Entity entity : entities) {
         if (this.isValidEntity(player, entity) && expandedBox.intersects(entity.getBoundingBox())) {
            collisions++;
         }
      }

      return collisions;
   }

   private Entity findNearestEntity(LocalPlayer player) {
      Entity nearest = null;
      double bestSq = Double.MAX_VALUE;
      double range = this.module.rockstarRange();
      double maxRangeSq = range * range;

      for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(range + 3.0))) {
         if (!this.isValidEntity(player, entity)) {
            continue;
         }

         double dx = entity.getX() - player.getX();
         double dz = entity.getZ() - player.getZ();
         double distanceSq = dx * dx + dz * dz;
         if (distanceSq <= maxRangeSq && distanceSq < bestSq) {
            bestSq = distanceSq;
            nearest = entity;
         }
      }

      return nearest;
   }

   private boolean isValidEntity(LocalPlayer player, Entity entity) {
      if (entity == player || (this.module.rockstarOnlyPlayers() && !(entity instanceof Player))) {
         return false;
      }

      return entity instanceof LivingEntity || entity instanceof Boat;
   }

   private Vec3 getDirectionToPoint(Vec3 from, Vec3 to, double speed) {
      double dx = to.x - from.x;
      double dz = to.z - from.z;
      double length = Math.sqrt(dx * dx + dz * dz);
      if (length == 0.0) {
         return Vec3.ZERO;
      }

      return new Vec3(dx / length * speed, 0.0, dz / length * speed);
   }
}
