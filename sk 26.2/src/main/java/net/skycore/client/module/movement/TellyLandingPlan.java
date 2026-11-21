package net.skycore.client.module.movement;

/** Short collision-free forecast, not a server/physics oracle. Coordinates are feet. */
final class TellyLandingPlan {
   record Landing(double x, double z, int ticks, boolean reachedPlane) { }

   static Landing predict(double x, double y, double z, double vx, double vy, double vz,
                          double floorY, int dx, int dz, boolean braking) {
      for (int tick = 1; tick <= 40; tick++) {
         // Conservative vanilla-air approximation; potion effects/obstacles/lag
         // may invalidate it. Recomputed each input tick from actual motion.
         x += vx;
         y += vy;
         z += vz;
         if (vy < 0 && y <= floorY) return new Landing(x, z, tick, true);
         vy = (vy - 0.08) * 0.98;
         vx = vx * 0.91 + (braking ? 0 : dx * 0.02);
         vz = vz * 0.91 + (braking ? 0 : dz * 0.02);
      }
      return new Landing(x, z, 40, false);
   }

   static int requiredBlocks(int firstX, int firstZ, int landingX, int landingZ,
                             int dx, int dz, int maximum) {
      long distance = ((long)landingX - firstX) * dx + ((long)landingZ - firstZ) * dz;
      return (int)Math.max(0, Math.min(maximum, distance + 1));
   }

   static boolean shortLanding(int needed, int maximum) { return needed < maximum; }
}
