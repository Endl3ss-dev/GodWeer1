package net.skycore.client.module.movement.training;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/** Offline, bounded-session statistics. No Minecraft state, ML, or cross-TPS extrapolation. */
public final class ScaffoldTrainingAnalysis {
   private ScaffoldTrainingAnalysis() { }

   public record Sample(long nanos, long clientTick, long worldTick, float tickRate, int segment,
                        float clientSpeed, boolean assisted, double x, double y, double z, float yaw, float pitch,
                        boolean grounded, boolean shift, boolean jump, boolean forward,
                        boolean backward, boolean left, boolean right, boolean sprint, int slot) { }

   public record Placement(long nanos, long clientTick, int x, int y, int z,
                           String face, double hitX, double hitY, double hitZ,
                           boolean consumed, boolean predictedChange, boolean bridgeCandidate,
                           boolean grounded, int crouchAge) { }

   public record Report(int schema, String session, String mode, String stopReason,
                        int samples, int attempts, int bridgePlacements, int motionSamples,
                        double durationSeconds, boolean applicable, List<String> warnings,
                        Map<String, Double> proposedSettings, Map<String, Double> styleStatistics) { }

   public static Report analyze(String id, String mode, String reason,
                                List<Sample> samples, List<Placement> attempts) {
      List<Placement> uses = attempts.stream()
         .filter(p -> p.consumed() && p.predictedChange() && p.bridgeCandidate()).toList();
      List<Double> yaw = new ArrayList<>(), pitch = new ArrayList<>(), air = new ArrayList<>();
      List<Double> returns = new ArrayList<>(), sneak = new ArrayList<>(), release = new ArrayList<>();
      int nearest = 0;
      boolean rateValid = true;
      for (Sample sample : samples) {
         rateValid &= Float.isFinite(sample.tickRate()) && Math.abs(sample.tickRate() - 20) < 0.1
            && Float.isFinite(sample.clientSpeed()) && Math.abs(sample.clientSpeed() - 1) < 0.001 && !sample.assisted();
      }
      for (int i = 1; i < samples.size() && !uses.isEmpty(); i++) {
         Sample previous = samples.get(i - 1), current = samples.get(i);
         long dt = current.nanos() - previous.nanos();
         // Exclude pauses, duplicate timestamps, teleports and tiny render intervals.
         if (previous.segment() != current.segment() || dt < 10_000_000L || dt > 250_000_000L
            || distanceSquared(previous, current) > 1.0) continue;
         while (nearest + 1 < uses.size()
            && Math.abs(uses.get(nearest + 1).nanos() - current.nanos())
               < Math.abs(uses.get(nearest).nanos() - current.nanos())) nearest++;
         Placement placement = uses.get(nearest);
         if (Math.abs(placement.nanos() - current.nanos()) > 750_000_000L) continue;
         double scale = 50_000_000.0 / dt; // real-time degrees per normal 50 ms tick
         double dy = Math.abs(wrap(current.yaw() - previous.yaw())) * scale;
         double dp = Math.abs(current.pitch() - previous.pitch()) * scale;
         if (!Double.isFinite(dy) || !Double.isFinite(dp)) continue;
         if (current.grounded() && previous.grounded() && placement.grounded()) {
            if (dy > 0.05) yaw.add(dy);
            if (dp > 0.05) pitch.add(dp);
            if (current.nanos() > placement.nanos() && dy > 0.05) returns.add(dy);
         } else if (!current.grounded() && !placement.grounded()) {
            if (Math.max(dy, dp) > 0.05) air.add(Math.max(dy, dp));
         }
      }
      for (Placement placement : uses) {
         if (placement.grounded() && placement.crouchAge() > 0 && placement.crouchAge() <= 20) {
            sneak.add((double)placement.crouchAge());
         }
      }
      // First shift release after a nearby placement. Count each release once.
      int lastUse = -1;
      for (int i = 1; i < samples.size(); i++) {
         Sample current = samples.get(i), previous = samples.get(i - 1);
         while (lastUse + 1 < uses.size() && uses.get(lastUse + 1).nanos() <= current.nanos()) lastUse++;
         if (lastUse >= 0 && previous.segment() == current.segment()
            && previous.shift() && !current.shift() && current.grounded()) {
            Placement placement = uses.get(lastUse);
            long ticks = current.clientTick() - placement.clientTick();
            if (placement.grounded() && ticks >= 0 && ticks <= 20) release.add((double)ticks);
         }
      }
      List<String> warnings = new ArrayList<>();
      if (reason.startsWith("error")) warnings.add("Recording interrupted by an error: " + reason);
      if (!"normal".equals(mode) || !rateValid) warnings.add("Only an unassisted 20 TPS / 1x normal recording can produce an applicable profile; slow and coached data are not rescaled.");
      if (uses.size() < 30) warnings.add("Need at least 30 locally observed bridge placements.");
      if (yaw.size() < 20 || pitch.size() < 20) warnings.add("Need at least 20 ground yaw and 20 ground pitch motion samples near placements.");
      double duration = samples.size() < 2 ? 0 : (samples.getLast().nanos() - samples.getFirst().nanos()) / 1e9;
      if (duration < 30) warnings.add("Need at least 30 seconds of recording.");
      boolean applicable = warnings.isEmpty();
      Map<String, Double> proposed = new LinkedHashMap<>();
      if (yaw.size() >= 20) proposed.put("Turn Speed", boundedPercentile(yaw, 0.85, 2, 60));
      if (pitch.size() >= 20) proposed.put("Pitch Speed", boundedPercentile(pitch, 0.85, 2, 45));
      if (returns.size() >= 20) proposed.put("Return Speed", boundedPercentile(returns, 0.85, 2, 60));
      if (sneak.size() >= 10) proposed.put("Sneak Delay", boundedPercentile(sneak, 0.5, 1, 10));
      if (release.size() >= 10) proposed.put("Release Delay", boundedPercentile(release, 0.5, 0, 10));
      if (air.size() >= 20) proposed.put("Jump Turn Speed", boundedPercentile(air, 0.85, 10, 120));
      if (!"normal".equals(mode) || !rateValid) proposed.clear();
      // These are proposals for existing controls, not a learned motion policy.
      warnings.add("Placement observations include client prediction, not server acknowledgement.");
      warnings.add("Quantiles approximate your controls; smoothing, edge distance and aim policy are not learned.");
      if (air.size() < 20) warnings.add("Jump data insufficient: Jump Turn Speed is left unchanged.");
      return new Report(2, id, mode, reason, samples.size(), attempts.size(), uses.size(),
         yaw.size() + pitch.size() + air.size(), duration, applicable, List.copyOf(warnings), Map.copyOf(proposed), summarizeStyle(samples, uses));
   }

   /** Sequence measurements remain useful for coached data, without treating assisted aim as human. */
   private static Map<String, Double> summarizeStyle(List<Sample> samples, List<Placement> uses) {
      int start = -1, completed = 0, three = 0, flat = 0, totalBlocks = 0, useCursor = 0;
      double yawSum = 0, secondsSum = 0, ticksSum = 0;
      for (int i = 1; i < samples.size(); i++) {
         Sample previous = samples.get(i - 1), current = samples.get(i);
         if (previous.segment() != current.segment()) {
            start = -1;
            continue;
         }
         if (previous.grounded() && !current.grounded()) start = i - 1;
         if (start < 0 || !current.grounded() || previous.grounded()) continue;
         Sample takeoff = samples.get(start);
         int blocks = 0, firstY = 0;
         Set<Position> unique = new HashSet<>();
         boolean sameY = true;
         while (useCursor < uses.size() && uses.get(useCursor).nanos() < takeoff.nanos()) useCursor++;
         for (int j = useCursor; j < uses.size() && uses.get(j).nanos() <= current.nanos(); j++) {
            Placement use = uses.get(j);
            if (use.grounded() || !unique.add(new Position(use.x(), use.y(), use.z()))) continue;
            if (blocks == 0) firstY = use.y();
            else sameY &= use.y() == firstY;
            blocks++;
         }
         double excursion = 0;
         for (int j = start; j <= i; j++) excursion = Math.max(excursion, Math.abs(wrap(samples.get(j).yaw() - takeoff.yaw())));
         completed++;
         if (blocks >= 3) three++;
         if (blocks > 0 && sameY) flat++;
         totalBlocks += blocks;
         yawSum += excursion;
         secondsSum += (current.nanos() - takeoff.nanos()) / 1e9;
         ticksSum += current.clientTick() - takeoff.clientTick();
         start = -1;
      }
      Map<String, Double> metrics = new LinkedHashMap<>();
      metrics.put("completedJumps", (double)completed);
      if (completed > 0) {
         metrics.put("observedPlacementsPerJump", (double)totalBlocks / completed);
         metrics.put("threeOrMoreBlockJumps", (double)three);
         metrics.put("flatJumps", (double)flat);
         metrics.put("meanYawExcursionDegrees", yawSum / completed);
         metrics.put("meanJumpRealSeconds", secondsSum / completed);
         metrics.put("meanJumpClientTicks", ticksSum / completed);
      }
      if (!uses.isEmpty()) {
         int y = uses.getFirst().y();
         metrics.put("observedFixedYFraction", (double)uses.stream().filter(use -> use.y() == y).count() / uses.size());
      }
      return Map.copyOf(metrics);
   }

   private record Position(int x, int y, int z) { }

   private static double distanceSquared(Sample a, Sample b) {
      double x = a.x() - b.x(), y = a.y() - b.y(), z = a.z() - b.z();
      return x * x + y * y + z * z;
   }

   static double wrap(double degrees) {
      return ((degrees + 180) % 360 + 360) % 360 - 180;
   }

   static double boundedPercentile(List<Double> values, double quantile, double min, double max) {
      double[] sorted = values.stream().filter(Double::isFinite).mapToDouble(Double::doubleValue).sorted().toArray();
      if (sorted.length == 0) throw new IllegalArgumentException("No finite measurements");
      int index = Math.min(sorted.length - 1, (int)Math.floor((sorted.length - 1) * quantile));
      return Math.max(min, Math.min(max, Math.rint(sorted[index])));
   }
}
