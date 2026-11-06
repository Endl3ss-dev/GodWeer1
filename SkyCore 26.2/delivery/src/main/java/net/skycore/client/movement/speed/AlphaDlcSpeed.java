package net.skycore.client.movement.speed;

import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.module.movement.SpeedModule;

 
public final class AlphaDlcSpeed extends SpeedHandler {
   private int ticks;
   private int groundTicks;

   public AlphaDlcSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onEnable() {
      this.resetRWState();
   }

   @Override
   public void onDisable() {
      this.resetRWState();
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (this.module.alphaMode("Collision")) {
         this.tickCollision(player);
      } else if (this.module.alphaMode("RW")) {
         this.tickRW(player);
      }
   }

   @Override
   public void onInput(net.skycore.client.event.PlayerInputEvent event) {
      LocalPlayer player = SpeedAction.player();
      if (!this.module.alphaMode("RW") || player == null || !SpeedAction.isMoving()) {
         this.groundTicks = 0;
         return;
      }

      this.groundTicks = player.verticalCollision ? ++this.groundTicks : 0;
      if (this.groundTicks >= 1) {
         player.jumpFromGround();
      }
   }

   @Override
   public void onPacketReceive(PacketReceiveEvent event) {
      LocalPlayer player = SpeedAction.player();
      if (!this.module.alphaMode("RW") || player == null) {
         return;
      }

      if (event.getPacket() instanceof net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket && this.ticks % 2 == 1) {
         this.ticks++;
      }
   }

   private boolean canUseRW(LocalPlayer player) {
      return player != null
         && player.level() != null
         && player.connection != null
         && SpeedAction.isMoving()
         && !player.isPassenger()
         && !player.getAbilities().flying;
   }

   private void tickRW(LocalPlayer player) {
      if (!this.canUseRW(player)) {
         this.resetRWState();
         return;
      }

      if (this.ticks > 3) {
         double boost = 0.03;
         if (this.ticks % 2 == 0) {
            player.push(0.0, 0.03, 0.0);
            boost = player.onGround() ? 0.085 : 0.03;
         }

         double yaw = SpeedAction.direction(player);
         double x = -Math.sin(yaw);
         double z = Math.cos(yaw);
         if (player.input.getMoveVector().y == -1.0F) {
            x = 0.0;
            z = 0.0;
         }

         player.push(x * boost, 0.0, z * boost);
      }

      this.ticks++;
      if (this.ticks % 2 == 0) {
         player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
      }
   }

   private void tickCollision(LocalPlayer player) {
      if (!SpeedAction.isMoving()) {
         return;
      }

      double scanRadius = 1.25;
      AABB searchBox = player.getBoundingBox().inflate(scanRadius);
      List<Entity> nearbyEntities = player.level().getEntities(player, searchBox);

      for (Entity entity : nearbyEntities) {
         if (!(entity instanceof Player) || entity == player) {
            continue;
         }

         double distanceX = Math.abs(player.getX() - entity.getX());
         double distanceZ = Math.abs(player.getZ() - entity.getZ());
         if (distanceX > 2.1 || distanceZ > 1.3) {
            continue;
         }

         double entitySpeed = SpeedAction.entitySpeed(entity);
         if (entitySpeed < 5.0) {
            double boostAmount = 0.02;
            AABB collisionBox = player.getBoundingBox().inflate(0.1);
            List<Entity> collisionEntities = player.level().getEntities(player, collisionBox);
            boolean found = false;

            for (Entity collisionEntity : collisionEntities) {
               if (collisionEntity instanceof Player && collisionEntity != player) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               break;
            }

            SpeedAction.addVelocity(SpeedAction.forward(boostAmount));
            break;
         }

         double boostAmount = 0.032;
         AABB checkBox = player.getBoundingBox().inflate(1.25);
         List<Entity> potentialCollisions = player.level().getEntities(player, checkBox);
         int collisions = 0;

         for (Entity collisionEntity : potentialCollisions) {
            if (collisionEntity == player) {
               continue;
            }

            double distToCollision = player.distanceTo(collisionEntity);
            if (distToCollision <= 1.25) {
               collisions++;
            }
         }

         if (collisions <= 0) {
            break;
         }

         SpeedAction.addVelocity(SpeedAction.forward(boostAmount));
         break;
      }
   }

   private void resetRWState() {
      this.ticks = 0;
      this.groundTicks = 0;
   }
}
