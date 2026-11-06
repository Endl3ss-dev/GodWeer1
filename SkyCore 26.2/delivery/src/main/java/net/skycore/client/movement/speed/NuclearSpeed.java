package net.skycore.client.movement.speed;

import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.skycore.client.module.movement.SpeedModule;

 
public final class NuclearSpeed extends SpeedHandler {
   public NuclearSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onDisable() {
      SpeedFlyingSpeed.clear();
      SpeedTimer.clear();
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (player.getAbilities().flying) {
         return;
      }

      if (this.module.nuclearMode("Matrix")) {
         this.matrix(player);
      }

      if (this.module.nuclearMode("MetaHvH")) {
         this.metaHvH(player);
      }

      if (this.module.nuclearMode("Collision")) {
         this.collision(player);
      }

      if (this.module.nuclearMode("HolyWorld")) {
         this.holyWorld(player);
      }
   }

   private boolean canBoost(LocalPlayer player) {
      return !player.isFallFlying() && !player.isInWater() && !player.isEyeInFluid(FluidTags.WATER) && !player.onGround();
   }

   private void autoJump(LocalPlayer player) {
      if (this.module.nuclearAutojump() && SpeedAction.isMoving() && player.onGround() && !player.input.keyPresses.jump()) {
         player.jumpFromGround();
      }
   }

   private void matrix(LocalPlayer player) {
      if (this.canBoost(player)) {
         SpeedAction.setSpeed(this.module.nuclearMatrixSpeed());
      }

      this.autoJump(player);
   }

   private void metaHvH(LocalPlayer player) {
      float currentSpeed = 0.358F;
      MobEffectInstance speed = player.getEffect(MobEffects.SPEED);
      if (speed != null) {
         if (speed.getAmplifier() == 0) {
            currentSpeed = 0.358F * 1.263F;
         } else if (speed.getAmplifier() == 1) {
            currentSpeed = 0.358F * 1.453F;
         } else if (speed.getAmplifier() >= 2) {
            currentSpeed = 0.358F * 1.652F;
         }
      }

      if (this.canBoost(player)) {
         SpeedAction.setSpeed(currentSpeed);
      }

      this.autoJump(player);
   }

   private void collision(LocalPlayer player) {
      AABB area = player.getBoundingBox().inflate(0.1);
      List<Entity> entities = player.level().getEntities(player, area);
      
      
      int collisions = 1;

      for (Entity entity : entities) {
         if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
            collisions++;
         }
      }

      if (collisions > 1 && !player.onGround()) {
         SpeedFlyingSpeed.set((float)(this.module.nuclearCollisionSpeed() / 10.0));
      }
   }

   private void holyWorld(LocalPlayer player) {
      if (!SpeedAction.isMoving()) {
         return;
      }

      AABB area = player.getBoundingBox().inflate(0.34, 0.15, 0.34);
      List<Entity> entities = player.level().getEntities(player, area);
      int players = 0;

      for (Entity entity : entities) {
         if (entity instanceof Player && entity != player && entity.isAlive()) {
            players++;
         }
      }

      if (players <= 0) {
         return;
      }

      double boost = Math.max(0.0, 0.045);
      SpeedAction.addVelocity(SpeedAction.forward(boost));
   }
}
