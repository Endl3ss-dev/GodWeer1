package net.skycore.client.module.combat;

import net.minecraft.util.Mth;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/**
 * Local-only head rotation values for the third-person avatar render state.
 * Nothing here writes to the player entity, the rotation controller, or network packets.
 */
final class AuraFakeRat {
   static final double TICKS_PER_SECOND = 20.0;

   record RenderRotation(boolean applyYaw, float yaw, boolean applyPitch, float pitch) {
   }

   final BooleanSetting enabled = new BooleanSetting("FakeRat",
      "Changes only the local avatar head in third person; never changes attack or server rotations.", false);
   final ModeSetting yawMode = new ModeSetting("Yaw mode", "Default", "Default", "Backward", "Sideway Left", "Sideway Right",
      "Slow Spin", "Fast Spin", "Ultra Fast Spin", "Jitter", "Custom Spin", "Custom");
   final ModeSetting pitchMode = new ModeSetting("Pitch mode", "Default", "Up", "Down", "Default", "Jitter", "Custom");
   final NumberSetting jitterSpeed = new NumberSetting("FakeRat jitter speed", "", 8.0, 0.0, 20.0, 0.1, " Hz");
   final NumberSetting jitterStrength = new NumberSetting("FakeRat jitter strength", "", 1.0, 0.0, 5.0, 0.1, "°");
   final NumberSetting customYaw = new NumberSetting("Custom yaw", "", 0.0, 0.0, 360.0, 1.0, "°");
   final NumberSetting customPitch = new NumberSetting("Custom pitch", "", 0.0, -90.0, 90.0, 1.0, "°");
   final BooleanSetting customJitter = new BooleanSetting("Custom jitter", false);
   final BooleanSetting customSpin = new BooleanSetting("Custom spin", false);
   final NumberSetting spinSpeed = new NumberSetting("Custom spin speed",
      "Angular speed in degrees per game tick.", 10.0, 0.0, 100.0, 1.0, "°/tick");
   final BooleanSetting switchSides = new BooleanSetting("Switch spin sides", false);
   final NumberSetting switchFrequency = new NumberSetting("Side switch frequency", "Direction changes per second; zero keeps one direction.",
      1.0, 0.0, 10.0, 1.0, " Hz");
   final NumberSetting switchDuration = new NumberSetting("Side switch duration", "Seconds used to smooth each direction reversal.",
      0.5, 0.5, 4.0, 0.1, " s");
   final Setting<?>[] settings = {
      this.enabled, this.yawMode, this.pitchMode, this.jitterSpeed, this.jitterStrength, this.customYaw, this.customPitch,
      this.customJitter, this.customSpin, this.spinSpeed, this.switchSides, this.switchFrequency, this.switchDuration
   };

   private long animationStartedNanos = Long.MIN_VALUE;
   private long lastSampleNanos = Long.MIN_VALUE;
   private String animationSignature = "";

   AuraFakeRat() {
      this.yawMode.visibleWhen(this.enabled::get);
      this.pitchMode.visibleWhen(this.enabled::get);
      this.jitterSpeed.visibleWhen(this::usesJitterSettings);
      this.jitterStrength.visibleWhen(this::usesJitterSettings);
      this.customYaw.visibleWhen(() -> this.enabled.get() && this.yawMode.is("Custom"));
      this.customPitch.visibleWhen(() -> this.enabled.get() && this.pitchMode.is("Custom"));
      this.customJitter.visibleWhen(() -> this.enabled.get() && (this.yawMode.is("Custom") || this.pitchMode.is("Custom")));
      this.customSpin.visibleWhen(() -> this.enabled.get() && this.yawMode.is("Custom"));
      this.spinSpeed.visibleWhen(this::usesCustomSpin);
      this.switchSides.visibleWhen(this::usesCustomSpin);
      this.switchFrequency.visibleWhen(() -> this.usesCustomSpin() && this.switchSides.get());
      this.switchDuration.visibleWhen(() -> this.usesCustomSpin() && this.switchSides.get());
   }

   RenderRotation sample(long now, float vanillaYaw, float vanillaPitch) {
      if (!this.enabled.get()) {
         this.reset();
         return new RenderRotation(false, vanillaYaw, false, vanillaPitch);
      }
      this.updateClock(now);
      double elapsed = Math.max(0.0, now - this.animationStartedNanos) / 1_000_000_000.0;
      float yaw = vanillaYaw;
      float pitch = vanillaPitch;
      boolean applyYaw = false;
      boolean applyPitch = false;

      switch (this.yawMode.get()) {
         case "Backward" -> {
            yaw = 180.0F;
            applyYaw = true;
         }
         case "Sideway Left" -> {
            yaw = -90.0F;
            applyYaw = true;
         }
         case "Sideway Right" -> {
            yaw = 90.0F;
            applyYaw = true;
         }
         case "Slow Spin" -> {
            yaw = spinAngle(elapsed, 30.0, false, 0.0, 0.0);
            applyYaw = true;
         }
         case "Fast Spin" -> {
            yaw = spinAngle(elapsed, 180.0, false, 0.0, 0.0);
            applyYaw = true;
         }
         case "Ultra Fast Spin" -> {
            yaw = spinAngle(elapsed, 720.0, false, 0.0, 0.0);
            applyYaw = true;
         }
         case "Jitter" -> {
            yaw = jitterOffset(elapsed, 0.0);
            applyYaw = true;
         }
         case "Custom Spin" -> {
            yaw = this.customSpinAngle(elapsed);
            applyYaw = true;
         }
         case "Custom" -> {
            yaw = this.customYaw.get().floatValue();
            if (this.customSpin.get()) yaw += this.customSpinAngle(elapsed);
            if (this.customJitter.get()) yaw += jitterOffset(elapsed, 0.0);
            applyYaw = true;
         }
         default -> {
            // Default deliberately preserves the normal local head yaw already in the render state.
         }
      }

      switch (this.pitchMode.get()) {
         case "Up" -> {
            pitch = -90.0F;
            applyPitch = true;
         }
         case "Down" -> {
            pitch = 90.0F;
            applyPitch = true;
         }
         case "Jitter" -> {
            pitch = jitterOffset(elapsed, Math.PI / 2.0);
            applyPitch = true;
         }
         case "Custom" -> {
            pitch = this.customPitch.get().floatValue();
            if (this.customJitter.get()) pitch += jitterOffset(elapsed, Math.PI / 2.0);
            applyPitch = true;
         }
         default -> {
            // Default preserves the normal local head pitch already in the render state.
         }
      }

      return new RenderRotation(applyYaw, applyYaw ? Mth.wrapDegrees(yaw) : vanillaYaw,
         applyPitch, applyPitch ? Mth.clamp(pitch, -90.0F, 90.0F) : vanillaPitch);
   }

   void reset() {
      this.animationStartedNanos = Long.MIN_VALUE;
      this.lastSampleNanos = Long.MIN_VALUE;
      this.animationSignature = "";
   }

   private boolean usesJitterSettings() {
      return this.enabled.get() && (this.yawMode.is("Jitter") || this.pitchMode.is("Jitter")
         || this.customJitter.get() && (this.yawMode.is("Custom") || this.pitchMode.is("Custom")));
   }

   private boolean usesCustomSpin() {
      return this.enabled.get() && (this.yawMode.is("Custom Spin") || this.yawMode.is("Custom") && this.customSpin.get());
   }

   private float jitterOffset(double elapsedSeconds, double phaseOffset) {
      if (this.jitterSpeed.get() == 0.0 || this.jitterStrength.get() == 0.0) return 0.0F;
      double phase = elapsedSeconds * 2.0 * Math.PI * this.jitterSpeed.get() + phaseOffset;
      return (float)(Math.sin(phase) * this.jitterStrength.get());
   }

   private float customSpinAngle(double elapsedSeconds) {
      double degreesPerSecond = this.spinSpeed.get() * TICKS_PER_SECOND;
      return spinAngle(elapsedSeconds, degreesPerSecond, this.switchSides.get(), this.switchFrequency.get(), this.switchDuration.get());
   }

   private void updateClock(long now) {
      String signature = this.yawMode.get() + "|" + this.pitchMode.get() + "|" + this.jitterSpeed.get() + "|" + this.jitterStrength.get()
         + "|" + this.customYaw.get() + "|" + this.customPitch.get() + "|" + this.customJitter.get() + "|" + this.customSpin.get()
         + "|" + this.spinSpeed.get() + "|" + this.switchSides.get() + "|" + this.switchFrequency.get() + "|" + this.switchDuration.get();
      boolean stale = this.lastSampleNanos != Long.MIN_VALUE
         && (now < this.lastSampleNanos || now - this.lastSampleNanos > 1_000_000_000L);
      if (this.animationStartedNanos == Long.MIN_VALUE || !signature.equals(this.animationSignature) || stale) {
         this.animationStartedNanos = now;
      }
      this.animationSignature = signature;
      this.lastSampleNanos = now;
   }

   /** Deterministic spin phase. Switching reverses angular velocity with a smoothstep ramp. */
   static float spinAngle(double elapsedSeconds, double degreesPerSecond, boolean switchSides, double frequencyHz, double durationSeconds) {
      if (!(elapsedSeconds > 0.0) || degreesPerSecond == 0.0) return 0.0F;
      double integratedDirectionSeconds;
      if (!switchSides || !(frequencyHz > 0.0)) {
         integratedDirectionSeconds = elapsedSeconds;
      } else {
         double interval = 1.0 / frequencyHz;
         double transition = Math.min(interval, Math.max(0.0, durationSeconds));
         if (elapsedSeconds <= interval) {
            integratedDirectionSeconds = elapsedSeconds;
         } else {
            double phase = (elapsedSeconds - interval) % (2.0 * interval);
            integratedDirectionSeconds = interval;
            if (phase <= interval) {
               integratedDirectionSeconds += rampDownIntegral(phase, transition);
            } else {
               integratedDirectionSeconds += rampDownIntegral(interval, transition)
                  + rampUpIntegral(phase - interval, transition);
            }
         }
      }
      double degrees = degreesPerSecond * integratedDirectionSeconds;
      return Mth.wrapDegrees((float)(degrees % 360.0));
   }

   private static double rampDownIntegral(double elapsed, double duration) {
      if (!(duration > 0.0)) return -elapsed;
      if (elapsed >= duration) return -(elapsed - duration);
      double t = elapsed / duration;
      // Integral of 1 - 2 * smoothstep(t), whose average over the ramp is zero.
      return duration * (t - 2.0 * t * t * t + t * t * t * t);
   }

   private static double rampUpIntegral(double elapsed, double duration) {
      if (!(duration > 0.0)) return elapsed;
      if (elapsed >= duration) return elapsed - duration;
      return -rampDownIntegral(elapsed, duration);
   }
}
