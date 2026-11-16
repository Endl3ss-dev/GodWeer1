package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.skycore.client.module.movement.SpeedModule;

/**
 * Phobia Speed (ruhack/phobia/system/modulesystem/impl/movement/Speed.java, 151 строка).
 * Режимы: Vanilla, Grim, FunTime, Funtime Elytra, HolyWorld.
 */
public final class PhobiaSpeed extends SpeedHandler {
   public PhobiaSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (this.module.phobiaMode("Vanilla")) {
         SpeedAction.setSpeed(this.module.phobiaSpeed() / 3.0);
      }

      if (this.module.phobiaMode("Funtime Elytra")) {
         this.handleFuntimeElytra(player);
      }
   }

   @Override
   public void onTravelPre(LocalPlayer player) {
      if (this.module.phobiaMode("FunTime")) {
         this.handleFunTime(player);
      }

      if (this.module.phobiaMode("Grim") && SpeedAction.hasMovementInput()) {
         this.handleGrim();
      }

      if (this.module.phobiaMode("HolyWorld") && SpeedAction.hasMovementInput()) {
         this.handleHolyWorld();
      }
   }

   @Override
   public void onTickEnd(LocalPlayer player) {
      if (this.module.phobiaMode("Funtime Elytra") && !player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         player.stopFallFlying();
      }
   }

   private void handleFunTime(LocalPlayer player) {
      if (!player.isSwimming()
         && !player.isFallFlying()
         && !player.isShiftKeyDown()
         && SpeedAction.boundingBoxHeight() < 1.5) {
         float motion = player.hasEffect(MobEffects.SPEED) ? 0.32F : 0.28F;
         SpeedAction.setSpeed(motion);
      }
   }

   private void handleFuntimeElytra(LocalPlayer player) {
      int elytraSlot = this.findHotbarElytra(player);
      if (elytraSlot != -1) {
         if (this.module.phobiaDiving()) {
            this.module.rotateDown(55.0F);
         }

         SpeedAction.startFallFlying();
      }
   }

   private int findHotbarElytra(LocalPlayer player) {
      for (int slot = 0; slot < 9; slot++) {
         if (player.getInventory().getItem(slot).is(Items.ELYTRA)) {
            return slot;
         }
      }

      return -1;
   }

   private void handleGrim() {
      int collisions = SpeedAction.countCollisions(0.5, false);
      SpeedAction.addVelocity(SpeedAction.forward(0.07 * (double)collisions));
   }

   private void handleHolyWorld() {
      int collisions = SpeedAction.countCollisions(0.35, false);
      SpeedAction.addVelocity(SpeedAction.forward(0.0205 * (double)collisions));
   }
}
