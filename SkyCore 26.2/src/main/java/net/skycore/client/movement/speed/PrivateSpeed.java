package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.skycore.client.module.movement.SpeedModule;

/**
 * Private Speed (org/privates/client/managers/module/impl/movement/Speed.java, 252 строки).
 * Режимы: HvH, Entity, SP-DUEL, NCP, Matrix703, AAC5.
 * Test / VerusB17 / Spartan422 в источнике недостижимы (их нет в списке режимов) — код перенесён как есть.
 */
public final class PrivateSpeed extends SpeedHandler {
   private int stage;
   private double lastDist;

   public PrivateSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onDisable() {
      this.stage = 0;
      this.lastDist = 0.0;
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (!player.onGround() && this.module.privateMode("HvH") && !player.isFallFlying()) {
         this.hvH(player);
      }

      if (this.module.privateMode("Entity")) {
         this.entity(player);
      }

      if (this.module.privateMode("Test")) {
         this.test(player);
      }

      if (this.module.privateMode("SP-DUEL")) {
         this.spDuel(player);
      }

      if (this.module.privateMode("VerusB17")) {
         this.verus(player);
      }

      if (this.module.privateMode("NCP")) {
         this.ncp(player);
      }

      if (this.module.privateMode("Spartan422")) {
         this.spartan(player);
      }

      if (this.module.privateMode("Matrix703")) {
         this.matrix(player);
      }

      if (this.module.privateMode("AAC5")) {
         this.aac(player);
      }
   }

   private void hvH(LocalPlayer player) {
      float melonBallSpeed = 0.36F;
      ItemStack offHand = player.getOffhandItem();
      MobEffectInstance speedEffect = player.getEffect(MobEffects.SPEED);
      MobEffectInstance slownessEffect = player.getEffect(MobEffects.SLOWNESS);
      String itemName = offHand.getHoverName().getString();
      float appliedSpeed;
      if (speedEffect != null) {
         if (speedEffect.getAmplifier() == 2) {
            appliedSpeed = melonBallSpeed * 1.155F;
            if (itemName.contains("Ломтик Дыни")) {
               if (speedEffect.getAmplifier() == 2) {
                  appliedSpeed = 0.41755F;
               } else {
                  appliedSpeed = 0.41755F * 0.52F;
               }
            }
         } else if (speedEffect.getAmplifier() == 1) {
            appliedSpeed = melonBallSpeed;
         } else {
            appliedSpeed = 0.0F;
         }
      } else {
         appliedSpeed = melonBallSpeed * 0.68F;
      }

      if (slownessEffect != null) {
         appliedSpeed *= 0.835F;
      }

      if (!player.onGround()) {
         appliedSpeed *= 1.435F;
      }

      SpeedAction.setSpeed(appliedSpeed);
   }

   private void entity(LocalPlayer player) {
      float expandValue = 0.3F;
      if (this.module.privateOvertake()) {
         Player nearest = this.nearestPlayer(player);
         if (nearest != null) {
            double distance = player.distanceTo(nearest);
            if (distance <= 0.25) {
               expandValue = (float)this.module.privateOvertakeDistance();
            }
         }
      }

      AABB area = player.getBoundingBox().inflate(expandValue);
      int armorStands = this.count(player, area, true);
      boolean canBoost = armorStands > 0.3 || this.count(player, area, false) > 0.3;
      if (canBoost && !player.onGround()) {
         SpeedFlyingSpeed.set(armorStands > 1 ? 1.0F / (float)armorStands : 0.05F);
      }
   }

   private void test(LocalPlayer player) {
      AABB area = player.getBoundingBox().inflate(0.085F);
      int armorStands = this.count(player, area, true);
      boolean canBoost = armorStands > 1 || this.count(player, area, false) > 1;
      if (canBoost && !player.onGround()) {
         SpeedFlyingSpeed.set(armorStands > 1 ? 1.0F / (float)armorStands : 0.03F);
      }
   }

   private void spDuel(LocalPlayer player) {
      AABB area = player.getBoundingBox().inflate(0.35);
      int armorStands = this.count(player, area, true);
      boolean canBoost = armorStands > 1 || this.count(player, area, false) > 1;
      if (canBoost && !player.onGround()) {
         SpeedFlyingSpeed.set(armorStands > 1 ? 1.0F / (float)armorStands : 0.05F);
      }
   }

   private void verus(LocalPlayer player) {
      if (player.fallDistance > 2.5F) {
         this.stage = 1;
      }

      if (this.stage == 0 && player.onGround()) {
         player.push(0.0, 0.42 - player.getDeltaMovement().y, 0.0);
         this.sendPosition(player, player.getX(), player.getY() + 0.42, player.getZ(), false);
         this.stage = 1;
      } else if (this.stage == 1) {
         double[] dir = SpeedAction.forward(0.66);
         this.setHorizontal(player, dir);
         this.sendPosition(player, player.getX() + dir[0], player.getY(), player.getZ() + dir[1], false);
         this.stage = 2;
      } else if (this.stage == 2) {
         double current = SpeedAction.horizontalSpeed();
         double speed = 0.66 - (current - 0.33) * 0.3;
         double[] dir = SpeedAction.forward(speed);
         this.setHorizontal(player, dir);
         this.sendPosition(player, player.getX() + dir[0], player.getY(), player.getZ() + dir[1], true);
         this.stage = 3;
      } else if (this.stage == 3) {
         double[] dir = SpeedAction.forward(0.66);
         this.setHorizontal(player, dir);
         this.stage = 0;
      }
   }

   private void ncp(LocalPlayer player) {
      if (player.onGround() && SpeedAction.isMoving()) {
         player.jumpFromGround();
         player.push(player.getDeltaMovement().x * 0.68, 0.0, player.getDeltaMovement().z * 0.68);
         this.sendStatus(player);
      } else if (!player.onGround()) {
         double speed = 0.489;
         if (SpeedAction.horizontalSpeed() < speed) {
            SpeedAction.setSpeed(speed);
         } else {
            SpeedAction.setSpeed(SpeedAction.horizontalSpeed() - 0.006);
         }
      }
   }

   private void spartan(LocalPlayer player) {
      if (player.onGround() && SpeedAction.isMoving()) {
         player.jumpFromGround();
         player.push(player.getDeltaMovement().x * 0.6, 0.0, player.getDeltaMovement().z * 0.6);
         this.sendPosition(player, player.getX(), player.getY(), player.getZ(), true);
      } else if (!player.onGround()) {
         double dist = SpeedAction.deltaPositionSpeed();
         if (dist > this.lastDist) {
            this.lastDist = dist;
            SpeedAction.setSpeed(this.lastDist);
         } else {
            this.lastDist = dist - 0.005;
            SpeedAction.setSpeed(this.lastDist);
         }
      }
   }

   private void matrix(LocalPlayer player) {
      if (player.onGround() && SpeedAction.isMoving()) {
         player.jumpFromGround();
         player.setDeltaMovement(player.getDeltaMovement().x, 0.419, player.getDeltaMovement().z);
         this.sendPosition(player, player.getX(), player.getY() + 0.419, player.getZ(), false);
         double[] dir = SpeedAction.forward(1.98);
         this.setHorizontal(player, dir);
         this.sendPosition(player, player.getX() + dir[0], player.getY(), player.getZ() + dir[1], true);
      } else if (!player.onGround()) {
         double current = SpeedAction.horizontalSpeed();
         if (current > 0.5) {
            SpeedAction.setSpeed(current - 0.005);
         }
      }
   }

   private void aac(LocalPlayer player) {
      if (player.onGround() && SpeedAction.isMoving()) {
         player.jumpFromGround();
         player.setDeltaMovement(player.getDeltaMovement().x, 0.42, player.getDeltaMovement().z);
         this.sendPosition(player, player.getX(), player.getY() + 0.42, player.getZ(), false);
         double[] dir = SpeedAction.forward(2.1);
         this.setHorizontal(player, dir[0] * 0.86, dir[1] * 0.86);
         this.sendPosition(player, player.getX() + dir[0], player.getY(), player.getZ() + dir[1], true);
      } else if (!player.onGround()) {
         SpeedAction.setSpeed(SpeedAction.horizontalSpeed() - 0.01);
      }
   }

   private void setHorizontal(LocalPlayer player, double[] motion) {
      player.setDeltaMovement(motion[0], player.getDeltaMovement().y, motion[1]);
   }

   private void setHorizontal(LocalPlayer player, double x, double z) {
      player.setDeltaMovement(x, player.getDeltaMovement().y, z);
   }

   private int count(LocalPlayer player, AABB area, boolean armorStands) {
      int count = 0;

      for (Entity entity : player.level().getEntities(player, area)) {
         if (armorStands) {
            if (entity instanceof ArmorStand) {
               count++;
            }
         } else if (entity instanceof LivingEntity) {
            count++;
         }
      }

      return count;
   }

   private Player nearestPlayer(LocalPlayer player) {
      Player nearest = null;
      double minDistance = Double.MAX_VALUE;

      for (Player other : player.level().players()) {
         if (other == player || other.isSpectator() || !other.isAlive()) {
            continue;
         }

         double distance = player.distanceTo(other);
         if (distance < minDistance) {
            minDistance = distance;
            nearest = other;
         }
      }

      return nearest;
   }

   private void sendPosition(LocalPlayer player, double x, double y, double z, boolean onGround) {
      if (player.connection != null) {
         player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y, z, onGround, player.horizontalCollision));
      }
   }

   private void sendStatus(LocalPlayer player) {
      if (player.connection != null) {
         player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(player.onGround(), player.horizontalCollision));
      }
   }
}
