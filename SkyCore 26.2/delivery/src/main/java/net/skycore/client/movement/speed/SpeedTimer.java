package net.skycore.client.movement.speed;

import net.skycore.client.util.ResetSpeedHelper;

 
public final class SpeedTimer {
   private static boolean owned;

   public static void set(float multiplier) {
      owned = true;
      ResetSpeedHelper.setSpeed(multiplier);
   }

   public static void clear() {
      if (owned) {
         owned = false;
         ResetSpeedHelper.resetSpeed();
      }
   }

   public static boolean isOwned() {
      return owned;
   }

   private SpeedTimer() {
   }
}
