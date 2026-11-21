package net.skycore.client.module.movement.training;

import java.util.ArrayList;
import java.util.List;

/** Pure statistics tests: no client, world, network or file writes. */
public final class ScaffoldTrainingAnalysisTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   private static List<ScaffoldTrainingAnalysis.Sample> samples(float rate, boolean ground) {
      return samples(rate, ground, false);
   }

   private static List<ScaffoldTrainingAnalysis.Sample> samples(float rate, boolean ground, boolean split) {
      List<ScaffoldTrainingAnalysis.Sample> values = new ArrayList<>();
      for (int i = 0; i <= 700; i++) {
         values.add(new ScaffoldTrainingAnalysis.Sample(i * 50_000_000L, i, i, rate, split ? i : 0, 1, false,
            0, 64, 0, (i % 40) * 2, i % 20, ground, i % 20 < 10,
            !ground, true, false, false, false, false, 0));
      }
      return values;
   }

   private static List<ScaffoldTrainingAnalysis.Placement> placements(boolean ground, boolean predicted) {
      List<ScaffoldTrainingAnalysis.Placement> values = new ArrayList<>();
      for (int i = 1; i <= 40; i++) {
         values.add(new ScaffoldTrainingAnalysis.Placement(i * 500_000_000L, i * 10,
            0, 63, i, "UP", 0.5, 64, i + 0.5, true, predicted, true, ground, 5));
      }
      return values;
   }

   public static void main(String[] args) {
      check(ScaffoldTrainingAnalysis.wrap(-358) == 2, "yaw wrap");
      check(ScaffoldTrainingAnalysis.wrap(358) == -2, "reverse yaw wrap");
      check(ScaffoldTrainingAnalysis.boundedPercentile(List.of(1.0, 2.0, 3.0, 1000.0), 0.5, 2, 60) == 2,
         "robust median and lower bound");
      check(ScaffoldTrainingAnalysis.boundedPercentile(List.of(1000.0), 0.85, 2, 60) == 60, "upper bound");
      check(ScaffoldTrainingAnalysis.boundedPercentile(List.of(Double.NaN, 3.0), 0.5, 2, 60) == 3, "filter NaN");
      boolean rejected = false;
      try {
         ScaffoldTrainingAnalysis.boundedPercentile(List.of(Double.NaN), 0.5, 2, 60);
      } catch (IllegalArgumentException expected) {
         rejected = true;
      }
      check(rejected, "no finite measurements rejected");
      var empty = ScaffoldTrainingAnalysis.analyze("empty", "normal", "manual", List.of(), List.of());
      check(!empty.applicable() && empty.proposedSettings().isEmpty(), "empty session");
      var normal = ScaffoldTrainingAnalysis.analyze("test", "normal", "manual", samples(20, true), placements(true, true));
      check(normal.applicable(), "normal session eligible");
      check(normal.bridgePlacements() == 40 && normal.durationSeconds() == 35, "counts and monotonic elapsed time");
      check(normal.proposedSettings().get("Turn Speed") == 2, "ground yaw percentile");
      check(normal.proposedSettings().get("Pitch Speed") == 2, "ground pitch lower bound");
      check(normal.proposedSettings().get("Sneak Delay") == 5, "observed sneak duration");
      check(!normal.proposedSettings().containsKey("Jump Turn Speed"), "do not invent jump data");
      check(!normal.proposedSettings().containsKey("Turn Smoothing"), "do not invent learned smoothing");
      var segmented = ScaffoldTrainingAnalysis.analyze("gaps", "normal", "manual", samples(20, true, true), placements(true, true));
      check(!segmented.applicable() && segmented.motionSamples() == 0, "never infer rotation across pause segments");
      var assisted = ScaffoldTrainingAnalysis.analyze("coach", "assisted", "manual", samples(20, true), placements(true, true));
      check(!assisted.applicable() && assisted.proposedSettings().isEmpty(), "20 TPS does not make coached sessions manual");
      var disguised = new ArrayList<>(samples(20, true));
      var first = disguised.getFirst();
      disguised.set(0, new ScaffoldTrainingAnalysis.Sample(first.nanos(), first.clientTick(), first.worldTick(), 20, 0,
         0.25F, true, first.x(), first.y(), first.z(), first.yaw(), first.pitch(), true, false, false,
         true, false, false, false, false, 0));
      var fakeNormal = ScaffoldTrainingAnalysis.analyze("disguised", "normal", "manual", disguised, placements(true, true));
      check(!fakeNormal.applicable() && fakeNormal.proposedSettings().isEmpty(), "sample markers prevent assisted data labelled normal");
      var slow = ScaffoldTrainingAnalysis.analyze("slow", "slow", "manual", samples(3, true), placements(true, true));
      check(!slow.applicable() && slow.proposedSettings().isEmpty(), "never extrapolate 3 TPS profile");
      var badRate = ScaffoldTrainingAnalysis.analyze("mixed", "normal", "manual", samples(3, true), placements(true, true));
      check(!badRate.applicable() && badRate.proposedSettings().isEmpty(), "normal label cannot hide a changed TPS");
      var error = ScaffoldTrainingAnalysis.analyze("error", "normal", "error-queue-full", samples(20, true), placements(true, true));
      check(!error.applicable(), "partial overflow session cannot apply");
      var attempts = ScaffoldTrainingAnalysis.analyze("attempts", "normal", "manual", samples(20, true), placements(true, false));
      check(!attempts.applicable() && attempts.bridgePlacements() == 0, "consumed action is not a changed block");
      var airOnly = ScaffoldTrainingAnalysis.analyze("air", "normal", "manual", samples(20, false), placements(false, true));
      check(!airOnly.applicable(), "air-only recording cannot calibrate ground controls");
      check(airOnly.proposedSettings().containsKey("Jump Turn Speed"), "air measurements separated");
      List<ScaffoldTrainingAnalysis.Sample> tellySamples = new ArrayList<>();
      for (int i = 0; i <= 20; i++) {
         tellySamples.add(new ScaffoldTrainingAnalysis.Sample(i * 50_000_000L, i, i, 20, 0,
            0.25F, true, 0, 64, 0, (i <= 10 ? i : 20 - i) * 18, 45,
            i == 0 || i == 20, false, i == 1, true, false, false, false, true, 0));
      }
      List<ScaffoldTrainingAnalysis.Placement> threeBlocks = new ArrayList<>();
      for (int i = 1; i <= 3; i++) {
         threeBlocks.add(new ScaffoldTrainingAnalysis.Placement(i * 200_000_000L, i * 4,
            i, 63, 0, "EAST", i, 63.8, 0.5, true, true, true, false, 0));
      }
      // A rollback/retry of the same cell must not inflate a three-block jump to four.
      threeBlocks.add(new ScaffoldTrainingAnalysis.Placement(800_000_000L, 16,
         3, 63, 0, "EAST", 3, 63.8, 0.5, true, true, true, false, 0));
      var telly = ScaffoldTrainingAnalysis.analyze("telly", "assisted", "manual", tellySamples, threeBlocks);
      check(!telly.applicable(), "coached Telly measurements do not become a human controller");
      check(telly.styleStatistics().get("completedJumps") == 1, "one observed takeoff and landing");
      check(telly.styleStatistics().get("observedPlacementsPerJump") == 3, "three placements per jump");
      check(telly.styleStatistics().get("threeOrMoreBlockJumps") == 1, "three-block sequence");
      check(telly.styleStatistics().get("flatJumps") == 1, "constant target Y");
      check(telly.styleStatistics().get("meanYawExcursionDegrees") == 180, "turn backwards and return");
      check(telly.styleStatistics().get("observedFixedYFraction") == 1, "fixed-Y placement statistics");
      System.out.println("ScaffoldTrainingAnalysisTest passed");
   }
}
