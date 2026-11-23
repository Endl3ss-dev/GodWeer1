package net.skycore.client.module.movement;

/**
 * Human-like smooth rotation for Scaffold's {@code Smooth} mode.
 *
 * <p>This is a time-based controller, not a per-tick step: each axis runs a
 * trapezoidal velocity profile. The angular velocity accelerates towards a
 * target velocity that is proportional to the remaining angle (capped by the
 * configured maximum speed), so a long turn starts with a fast flick, then
 * brakes proportionally as it approaches the target and lands softly without
 * oscillation or overshoot. Integration uses real elapsed time, therefore the
 * motion stays identical at any tick rate and survives lag spikes (the frame
 * delta is clamped).
 */
final class ScaffoldSmoothRotation {
   private static final double MIN_DT_SECONDS = 0.001;
   private static final double MAX_DT_SECONDS = 0.1;

   private final Axis yawAxis = new Axis(true);
   private final Axis pitchAxis = new Axis(false);
   private boolean initialized;
   private long lastUpdateNanos;

   void reset() {
      this.initialized = false;
      this.yawAxis.velocity = 0.0F;
      this.pitchAxis.velocity = 0.0F;
      this.lastUpdateNanos = 0L;
   }

   /** Seeds the controller from the rotation the player actually has now. */
   void seed(float yaw, float pitch) {
      this.yawAxis.position = yaw;
      this.pitchAxis.position = clamp(pitch, -90.0F, 90.0F);
      this.yawAxis.velocity = 0.0F;
      this.pitchAxis.velocity = 0.0F;
      this.initialized = true;
      this.lastUpdateNanos = System.nanoTime();
   }

   boolean isInitialized() {
      return this.initialized;
   }

   float yaw() {
      return this.yawAxis.position;
   }

   float pitch() {
      return this.pitchAxis.position;
   }

   /**
    * Advances both axes towards the target using the real time elapsed since
    * the previous call. Returns {@code {yaw, pitch}} to apply this tick.
    */
   float[] update(float targetYaw, float targetPitch, double maxSpeed, double acceleration, double brake) {
      long now = System.nanoTime();
      double dt = this.lastUpdateNanos == 0L ? 0.05 : (now - this.lastUpdateNanos) / 1.0E9;
      this.lastUpdateNanos = now;
      return this.update(targetYaw, targetPitch, maxSpeed, acceleration, brake,
         Math.max(MIN_DT_SECONDS, Math.min(MAX_DT_SECONDS, dt)));
   }

   /** Deterministic-step variant, also used by the headless test. */
   float[] update(float targetYaw, float targetPitch, double maxSpeed, double acceleration, double brake, double dtSeconds) {
      this.yawAxis.step(targetYaw, maxSpeed, acceleration, brake, dtSeconds);
      this.pitchAxis.step(targetPitch, maxSpeed, acceleration, brake, dtSeconds);
      return new float[]{this.yawAxis.position, this.pitchAxis.position};
   }

   /** True when both axes are within tolerance of the target and nearly still. */
   boolean isSettled(float targetYaw, float targetPitch, float tolerance) {
      return Math.abs(wrapDegrees(targetYaw - this.yawAxis.position)) <= tolerance
         && Math.abs(targetPitch - this.pitchAxis.position) <= tolerance
         && Math.abs(this.yawAxis.velocity) <= 90.0F
         && Math.abs(this.pitchAxis.velocity) <= 90.0F;
   }

   private static float wrapDegrees(float degrees) {
      float wrapped = degrees % 360.0F;
      if (wrapped >= 180.0F) {
         wrapped -= 360.0F;
      }
      if (wrapped < -180.0F) {
         wrapped += 360.0F;
      }
      return wrapped;
   }

   private static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   private static final class Axis {
      private final boolean yaw;
      private float position;
      private float velocity;

      private Axis(boolean yaw) {
         this.yaw = yaw;
      }

      private void step(float target, double maxSpeed, double acceleration, double brake, double dt) {
         float delta = this.yaw ? wrapDegrees(target - this.position) : target - this.position;
         // Target velocity is proportional to the remaining angle: far away it
         // saturates at max speed, close in it decays and brakes the turn.
         float desiredVelocity = clamp((float) (delta * brake), (float) -maxSpeed, (float) maxSpeed);
         float velocityChange = clamp(
            desiredVelocity - this.velocity,
            (float) (-acceleration * dt),
            (float) (acceleration * dt));
         this.velocity += velocityChange;

         float step = (float) (this.velocity * dt);
         if (Math.abs(step) >= Math.abs(delta)) {
            // Land exactly on the target instead of overshooting/oscillating.
            this.position = this.yaw ? wrapDegrees(target) : clamp(target, -90.0F, 90.0F);
            this.velocity = 0.0F;
         } else {
            this.position += step;
            this.position = this.yaw ? wrapDegrees(this.position) : clamp(this.position, -90.0F, 90.0F);
         }
      }
   }
}
