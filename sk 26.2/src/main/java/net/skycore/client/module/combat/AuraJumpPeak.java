package net.skycore.client.module.combat;

/** Tracks one jump arc and exposes only its single apex sample to Attack Aura. */
final class AuraJumpPeak {
   private static final float MAX_PEAK_FALL_DISTANCE = 0.001F;

   private int sampledTick = Integer.MIN_VALUE;
   private double previousVerticalVelocity;
   private boolean jumpArcActive;
   private boolean ascending;
   private boolean peakAvailable;

   void observe(int tick, double verticalVelocity, boolean onGround, float fallDistance, boolean validJumpContext) {
      if (tick == this.sampledTick) {
         return;
      }
      this.sampledTick = tick;
      this.peakAvailable = false;

      if (onGround) {
         this.clearArc();
         this.previousVerticalVelocity = verticalVelocity;
         return;
      }
      if (!validJumpContext) {
         this.clearArc();
         this.previousVerticalVelocity = verticalVelocity;
         return;
      }

      if (!this.jumpArcActive && verticalVelocity > 0.0 && fallDistance <= MAX_PEAK_FALL_DISTANCE) {
         this.jumpArcActive = true;
         this.ascending = true;
      }

      if (this.jumpArcActive && this.ascending && this.previousVerticalVelocity > 0.0
         && verticalVelocity <= 0.0 && fallDistance <= MAX_PEAK_FALL_DISTANCE) {
         this.peakAvailable = true;
         this.ascending = false;
      }
      this.previousVerticalVelocity = verticalVelocity;
   }

   boolean isJumpArcActive() {
      return this.jumpArcActive;
   }

   boolean isAtPeak() {
      return this.peakAvailable;
   }

   void consumePeak() {
      this.peakAvailable = false;
   }

   void reset() {
      this.sampledTick = Integer.MIN_VALUE;
      this.previousVerticalVelocity = 0.0;
      this.clearArc();
   }

   private void clearArc() {
      this.jumpArcActive = false;
      this.ascending = false;
      this.peakAvailable = false;
   }
}
