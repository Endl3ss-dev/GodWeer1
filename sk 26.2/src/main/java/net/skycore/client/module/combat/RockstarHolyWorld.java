package net.skycore.client.module.combat;

import java.util.random.RandomGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.combat.Rotation;

// Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/HolyWorldRotationMode.java.
// Frame integrator for the visible look. Partial tick is used by the caller for position, never for elapsed time.
final class RockstarHolyWorld {
   private final RandomGenerator random;
   private final Axis yaw = new Axis(), pitch = new Axis();
   private Vec3 center;
   private Rotation applied;
   private long lastFrame;
   private float gain, ramp, period, highGain, lowGain, rampSpeed, rampMax, near, settle, settleScale;
   private boolean burst;

   RockstarHolyWorld(RandomGenerator random) { this.random = random; }

   void reset() {
      center = null;
      applied = null;
      yaw.clear();
      pitch.clear();
      gain = ramp = 0;
      burst = false;
   }

   Rotation frame(long now, Vec3 eyes, Vec3 targetCenter, Rotation view, float mouseStep) {
      boolean first = center == null;
      if (!first && now <= lastFrame) return view;
      float dt = first ? 1 / 60F : Mth.clamp((now - lastFrame) / 1e9F, 0.0001F, 0.1F);
      lastFrame = now;
      if (first) {
         center = targetCenter;
         highGain = random.nextFloat(0.5F, 1.2F);
         lowGain = random.nextFloat(0.08F, 0.2F);
         rampSpeed = random.nextFloat(0.018F, 0.023F) * (7F / 30);
         rampMax = random.nextFloat(0.25F, 0.3F);
         near = random.nextFloat(2, 2.5F);
         settle = random.nextFloat(6, 9);
         settleScale = random.nextFloat(0.15F, 0.2F);
         period = random.nextFloat(0.5F, 3.25F);
      } else {
         center = center.add(targetCenter.subtract(center).scale(Mth.clamp(blend(25, dt), 0.05F, 0.95F)));
      }
      Rotation desired = RockstarAim.look(eyes, center, view);
      float dy = Mth.wrapDegrees(desired.getYaw() - view.getYaw()), dp = desired.getPitch() - view.getPitch();
      if (!Float.isFinite(dy) || !Float.isFinite(dp) || Math.hypot(dy, dp) < 0.1) {
         yaw.decay(dt);
         pitch.decay(dt);
         return applied = view;
      }
      // Compare the actual view with our previous OUTPUT: the difference is the user's mouse movement.
      float manualYaw = applied == null ? 0 : Mth.wrapDegrees(view.getYaw() - applied.getYaw());
      float manualPitch = applied == null ? 0 : view.getPitch() - applied.getPitch();
      float distance = (float)Math.hypot(dy, dp - 1.75F);
      float force = 600 / Math.max(10, distance);
      float noise = 0.13F * noiseGain(distance, dt);
      float y = yaw.step(dy, dy * force, manualYaw, dt, mouseStep, false, noise, random);
      float p = pitch.step(dp, (dp - 1.75F) * force * 0.23F, manualPitch, dt, mouseStep, true, noise, random);
      return applied = new Rotation(view.getYaw() + y, Mth.clamp(view.getPitch() + p, -90, 90));
   }

   private float noiseGain(float distance, float dt) {
      period -= dt;
      if (period <= 0) {
         burst = !burst;
         period = burst ? random.nextFloat(0.1F, 1.25F) : random.nextFloat(0.5F, 3.25F);
      }
      gain = Mth.lerp(blend(burst ? 7 : 2.5F, dt), gain, burst ? highGain : lowGain);
      ramp = Math.min(rampMax, ramp + dt * rampSpeed);
      float settleGain = Mth.lerp(Mth.clamp((distance - near) / (settle - near), 0, 1), settleScale, 1);
      return (gain + ramp) * settleGain;
   }

   private static float blend(float rate, float dt) { return (float)-Math.expm1(-rate * dt); }

   private static final class Axis {
      private float speed, noiseSpeed, remainder;
      void clear() { speed = noiseSpeed = remainder = 0; }
      void decay(float dt) {
         float keep = 1 - blend(20, dt);
         speed *= keep;
         noiseSpeed *= keep;
         remainder *= keep;
      }
      float step(float delta, float force, float manual, float dt, float mouseStep, boolean vertical, float noise, RandomGenerator random) {
         noiseSpeed = Mth.lerp(blend(25, dt), noiseSpeed, (float)random.nextGaussian() * noise * Math.abs(force));
         force += noiseSpeed * (vertical ? 2 : 1);
         if (Math.abs(manual / dt) >= 0.1F && Math.abs(force) > 0.0001F) {
            force *= Math.signum(force) == Math.signum(manual) ? 0.95F : 1 - Math.min(0.8F, Math.abs(manual / (dt * force)));
         }
         float limit = Math.max(Math.abs(delta) / dt * 1.5F, 1);
         float follow = Mth.clamp(blend(20, dt), 0.05F, 0.95F) * (vertical ? 0.75F : 1);
         speed = Mth.clamp(Mth.lerp(follow, speed, force), -limit, limit);
         float motion = speed * dt;
         if (mouseStep < 0.0001F) return motion;
         remainder += motion;
         float rounded = Math.round(remainder / mouseStep) * mouseStep;
         remainder -= rounded;
         return rounded;
      }
   }
}
