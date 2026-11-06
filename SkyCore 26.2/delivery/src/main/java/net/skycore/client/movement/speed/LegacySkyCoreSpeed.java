package net.skycore.client.movement.speed;

import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.AABB;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.SpeedModule;

 
public final class LegacySkyCoreSpeed extends SpeedHandler {
   private int ticks;
   private int groundTicks;

   public LegacySkyCoreSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onEnable() {
      this.resetRWState(true);
   }

   @Override
   public void onDisable() {
      this.resetRWState(true);
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (!this.module.legacyMode("Collision") || !SpeedAction.isMoving()) {
         return;
      }

      double scanRadius = 1.25;
      AABB area = player.getBoundingBox().inflate(scanRadius);
      List<Entity> nearby = player.level().getEntities(player, area);

      for (Entity entity : nearby) {
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
            int collisions = this.collisions(player, player.getBoundingBox().inflate(0.1), false);
            if (collisions > 0) {
               SpeedAction.addVelocity(SpeedAction.forward(0.02));
            }
         } else {
            int collisions = this.collisions(player, player.getBoundingBox().inflate(1.25), true);
            if (collisions > 0) {
               SpeedAction.addVelocity(SpeedAction.forward(0.032));
            }
         }

         break;
      }
   }

   private int collisions(LocalPlayer player, AABB area, boolean checkDistance) {
      int collisions = 0;

      for (Entity entity : player.level().getEntities(player, area)) {
         if (entity == player) {
            continue;
         }

         if (entity instanceof ArmorStand && !(entity instanceof Boat)) {
            continue;
         }

         if (checkDistance && player.distanceTo(entity) > 1.25) {
            continue;
         }

         collisions++;
      }

      return collisions;
   }

   @Override
   public void onTickEnd(LocalPlayer player) {
      if (!this.module.legacyMode("RW") || !this.canUseRW(player)) {
         this.resetRWState(true);
         return;
      }

      SpeedTimer.set(1.9F);
      if (this.ticks > 3) {
         double boost = 0.03;
         if (this.ticks % 2 == 0) {
            player.push(0.0, 0.03, 0.0);
            boost = player.onGround() ? 0.085 : 0.03;
         }

         double yaw = Math.toRadians(SpeedAction.directionDegrees(player));
         double x = -Math.sin(yaw);
         double z = Math.cos(yaw);
         player.push(x * boost, 0.0, z * boost);
      }

      this.ticks++;
      if (this.ticks % 2 == 0) {
         SpeedTimer.set(0.3F);
         if (player.connection != null) {
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
         }
      }
   }

   @Override
   public void onInput(PlayerInputEvent event) {
      LocalPlayer player = SpeedAction.player();
      if (!this.module.legacyMode("RW") || player == null || !SpeedAction.isMoving()) {
         this.groundTicks = 0;
         return;
      }

      if (player.verticalCollision) {
         this.groundTicks++;
      } else {
         this.groundTicks = 0;
      }

      if (this.groundTicks >= 1) {
         player.jumpFromGround();
      }
   }

   @Override
   public void onPacketReceive(PacketReceiveEvent event) {
      LocalPlayer player = SpeedAction.player();
      if (!this.module.legacyMode("RW") || player == null) {
         return;
      }

      if (event.getPacket() instanceof net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket) {
         if (this.ticks % 2 == 1) {
            this.ticks++;
         }

         SpeedTimer.set(1.0F);
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

   private void resetRWState(boolean resetTimer) {
      this.ticks = 0;
      this.groundTicks = 0;
      if (resetTimer) {
         SpeedTimer.clear();
      }
   }
}
