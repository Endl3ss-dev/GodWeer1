package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.module.combat.AttackAuraModule;
import net.skycore.client.module.movement.SpeedModule;

 
public final class WildSpeed extends SpeedHandler {
   private int latest = 1;

   public WildSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onDisable() {
      this.latest = 1;
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (player.horizontalCollision) {
         this.latest = -this.latest;
      }

      if (this.module.wildMode("Vanilla")) {
         WildMovementPhysics.process(0.42);
      } else if (this.module.wildMode("ST duel")) {
         this.handle(0.16);
      } else if (this.module.wildMode("HW")) {
         this.handle(0.1);
      } else if (this.module.wildMode("Grim-Entity")) {
         this.refresh(player);
      } else if (this.module.wildMode("Ares-Entity")) {
         this.render(player);
      } else if (this.module.wildMode("TargetStrafe")) {
         this.tick(player);
      }
   }

    
   private void handle(double expand) {
      LocalPlayer player = SpeedAction.player();
      if (player == null || player.onGround()) {
         return;
      }

      int armorStands = 0;
      int living = 0;

      for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(expand))) {
         if (entity instanceof ArmorStand) {
            armorStands++;
         } else if (entity instanceof LivingEntity) {
            living++;
         }

         if (armorStands > 1 || living > 1) {
            this.drawAnimation(player);
            return;
         }
      }
   }

    
   private void refresh(LocalPlayer player) {
      double motion = 6.0E-4F;
      Entity nearest = null;
      double best = Double.MAX_VALUE;
      double range = 0.2F;
      LivingEntity target = SkycoreClient.modules().get(AttackAuraModule.class).map(AttackAuraModule::currentTarget).orElse(null);

      for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(1.0))) {
         if (entity != player && entity instanceof Player && entity == target) {
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double distanceSq = dx * dx + dz * dz;
            if (distanceSq <= range && distanceSq < best) {
               best = distanceSq;
               nearest = entity;
            }
         }
      }

      if (nearest != null) {
         double[] direction = this.directionTo(player.position(), nearest.position(), motion);
         player.push(direction[0], 0.0, direction[1]);
         player.hurtMarked = true;
      }
   }

    
   private void render(LocalPlayer player) {
      Entity nearest = null;
      double best = Double.MAX_VALUE;
      double range = 2.25;

      for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(2.0))) {
         if (entity != player && entity instanceof Player) {
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double distanceSq = dx * dx + dz * dz;
            if (distanceSq <= range && distanceSq < best) {
               best = distanceSq;
               nearest = entity;
            }
         }
      }

      if (nearest != null && !player.onGround()) {
         this.drawAnimation(player);
      }
   }

    
   private void tick(LocalPlayer player) {
      LivingEntity target = SkycoreClient.modules().get(AttackAuraModule.class).map(AttackAuraModule::currentTarget).orElse(null);
      if (target == null || player.onGround()) {
         return;
      }

      Entity nearest = null;
      double best = Double.MAX_VALUE;
      double range = 2.25;

      for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(2.0))) {
         if (entity != player && entity instanceof Player) {
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double distanceSq = dx * dx + dz * dz;
            if (distanceSq <= range && distanceSq < best) {
               best = distanceSq;
               nearest = entity;
            }
         }
      }

      Vec3 velocity = player.getDeltaMovement();
      double speed = SpeedAction.horizontalDistance(velocity);
      if (nearest != null) {
         speed *= 1.0 + this.module.wildPending() / 10.0;
      }

      double max = this.module.wildTarget() / 20.0;
      if (speed > max) {
         speed = max;
      }

      if (speed < 0.15) {
         speed = 0.15;
      }

      double radius = this.module.wildPrevious();
      double distance = player.distanceTo(target);
      double forward = 0.0;
      double strafe = this.latest;
      if (distance > radius + 0.5) {
         forward = 1.0;
      } else if (distance < radius - 0.5) {
         forward = -1.0;
      }

      double dx = target.getX() - player.getX();
      double dz = target.getZ() - player.getZ();
      float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      if (forward != 0.0) {
         if (strafe > 0.0) {
            yaw += forward > 0.0 ? -45.0F : 45.0F;
         } else if (strafe < 0.0) {
            yaw += forward > 0.0 ? 45.0F : -45.0F;
         }

         strafe = 0.0;
         forward = forward > 0.0 ? 1.0 : -1.0;
      }

      double sin = Math.sin(Math.toRadians(yaw + 90.0F));
      double cos = Math.cos(Math.toRadians(yaw + 90.0F));
      double x = forward * speed * cos + strafe * speed * sin;
      double z = forward * speed * sin - strafe * speed * cos;
      player.setDeltaMovement(x, velocity.y, z);
      player.hurtMarked = true;
   }

    
   private void drawAnimation(LocalPlayer player) {
      Vec3 velocity = player.getDeltaMovement();
      double multiplier = 1.0 + this.module.wildPending() / 10.0;
      double max = this.module.wildTarget() / 20.0;
      double x = velocity.x;
      double z = velocity.z;
      double boostedX = x * multiplier;
      double boostedZ = z * multiplier;
      double length = Math.sqrt(boostedX * boostedX + boostedZ * boostedZ);
      if (length > max) {
         double scale = max / length;
         boostedX *= scale;
         boostedZ *= scale;
      }

      player.push(boostedX - x, 0.0, boostedZ - z);
      player.hurtMarked = true;
   }

   private double[] directionTo(Vec3 from, Vec3 to, double speed) {
      double dx = to.x - from.x;
      double dz = to.z - from.z;
      double length = Math.sqrt(dx * dx + dz * dz);
      return length == 0.0 ? new double[]{0.0, 0.0} : new double[]{dx / length * speed, dz / length * speed};
   }
}
