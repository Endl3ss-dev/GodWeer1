package net.skycore.client.movement.speed;

import net.skycore.client.util.ResetSpeedHelper;

/**
 * Глобальный таймер спидов (mc.timer.timerSpeed в источниках).
 * Источники ставят скорость таймера напрямую: SkyCore-legacy RW — 1.9 / 0.3,
 * Releon ReallyWorld/HolyWorld/FunTime/SpookyTime — фазовые множители 0.2/1.8, 0.1/2.6,
 * 0.2/1.8, 0.01/4.2. Значение уходит в тот же DeltaTracker-хук, что и Timer-модуль.
 */
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
