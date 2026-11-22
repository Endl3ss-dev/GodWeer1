package net.skycore.client.module.combat;

import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/** Visual offsets in degrees; never applied to the player or to combat rotations. */
public final class AuraCameraJitter {
   public record Offset(float yaw, float pitch) {
      public static final Offset NONE = new Offset(0, 0);
   }

   final BooleanSetting enabled = new BooleanSetting("Camera jitter", false);
   final BooleanSetting whileAiming = new BooleanSetting("While aiming", true);
   final BooleanSetting onHit = new BooleanSetting("On attacks", false);
   final NumberSetting speed = new NumberSetting("Shake speed", "", 8, 0, 20, 0.1, " Hz");
   final NumberSetting strength = new NumberSetting("Shake strength", "", 1, 0, 5, 0.1, "°");
   final Setting<?>[] settings = {enabled, whileAiming, onHit, speed, strength};
   private static final long HIT_PULSE_NS = 120_000_000L;
   private boolean aiming, pulsing;
   private long aimStart, lastHit;

   AuraCameraJitter() {
      whileAiming.visibleWhen(enabled::get);
      onHit.visibleWhen(enabled::get);
      speed.visibleWhen(enabled::get);
      strength.visibleWhen(enabled::get);
   }

   void recordHit(long now) {
      pulsing = enabled.get() && onHit.get() && speed.get() > 0 && strength.get() > 0;
      lastHit = now;
   }

   Offset sample(long now, boolean activeAim) {
      if (!enabled.get() || speed.get() == 0 || strength.get() == 0) {
         reset();
         return Offset.NONE;
      }
      boolean aim = activeAim && whileAiming.get();
      if (aim && !aiming) aimStart = now;
      aiming = aim;
      long age = now - lastHit;
      pulsing = pulsing && onHit.get() && age >= 0 && age < HIT_PULSE_NS;
      if (!aim && !pulsing) return Offset.NONE;

      double radiansPerNs = 2 * Math.PI * speed.get() / 1_000_000_000.0;
      double phase = (now - aimStart) * radiansPerNs;
      double yaw = aim ? Math.sin(phase) : 0;
      double pitch = aim ? 0.65 * Math.sin(phase * 1.37) : 0;
      if (pulsing) {
         // Blend, don't add: enabling both triggers cannot exceed the strength slider.
         double gain = 1.0 - (double)age / HIT_PULSE_NS;
         double hitPhase = age * radiansPerNs;
         yaw = (1 - gain) * yaw + gain * Math.sin(hitPhase);
         pitch = (1 - gain) * pitch - gain * Math.cos(hitPhase);
      }
      double amplitude = strength.get();
      return new Offset((float)(yaw * amplitude), (float)(pitch * amplitude));
   }

   void reset() {
      aiming = false;
      pulsing = false;
   }
}
