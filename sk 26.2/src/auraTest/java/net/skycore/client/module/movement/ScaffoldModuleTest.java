package net.skycore.client.module.movement;

import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.skycore.client.combat.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.NumberSetting;

/** Headless geometry/settings smoke tests; not a simulation of vanilla edge clamping. */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   private static void near(double actual, double expected, String message) {
      check(Math.abs(actual - expected) < 0.0001, message + ": " + actual);
   }

   private static void direction(float strafe, float forward, float yaw, double x, double z) {
      Vec3 value = ScaffoldModule.inputDirection(strafe, forward, yaw);
      near(value.x, x, "direction X");
      near(value.y, 0, "horizontal direction");
      near(value.z, z, "direction Z");
   }

   public static void main(String[] args) {
      check(TellyScaffoldController.cpsInterval(15) == 66_666_667L, "15 CPS wall-clock period");
      check(TellyScaffoldController.cpsInterval(100) == TellyScaffoldController.cpsInterval(15), "CPS capped at 15");
      check(!TellyScaffoldController.cpsReady(99, 100), "no early click");
      check(TellyScaffoldController.cpsReady(100, 100), "deadline click allowed");
      check(TellyScaffoldController.cpsReady(100000, 100), "lag allows one attempt, caller schedules from now");
      check(TellyScaffoldController.xHeading(-90, false) == Direction.EAST, "W moves east");
      check(TellyScaffoldController.xHeading(-90, true) == Direction.WEST, "S reverses east approach");
      check(TellyScaffoldController.xHeading(90, false) == Direction.WEST, "W moves west");
      check(TellyScaffoldController.xHeading(90, true) == Direction.EAST, "S reverses west approach");
      check(TellyScaffoldController.xHeading(0, false) != TellyScaffoldController.xHeading(0, true), "Z-facing fallback reverses too");
      BlockPos rowTarget = new BlockPos(1, 63, 0);
      check(TellyScaffoldController.placementHit(new BlockHitResult(new Vec3(1, 63.5, 0.5), Direction.EAST,
         new BlockPos(0, 63, 0), false), rowTarget, 63, 0), "central X-side click allowed");
      check(!TellyScaffoldController.placementHit(new BlockHitResult(new Vec3(1, 63.99, 0.01), Direction.EAST,
         new BlockPos(0, 63, 0), false), rowTarget, 63, 0), "edge click rejected");
      check(!TellyScaffoldController.placementHit(new BlockHitResult(new Vec3(0.5, 64, 0.5), Direction.UP,
         new BlockPos(0, 63, 0), false), new BlockPos(0, 64, 0), 63, 0), "never build upward");
      check(!TellyScaffoldController.flatXTarget(new BlockPos(1, 64, 0), 63, 0), "jump cannot change bridge Y");
      check(!TellyScaffoldController.flatXTarget(new BlockPos(1, 63, 1), 63, 0), "cannot widen bridge along Z");
      check(TellyScaffoldController.cardinal(0) == Direction.SOUTH, "telly south");
      check(TellyScaffoldController.cardinal(90) == Direction.WEST, "telly west");
      check(TellyScaffoldController.cardinal(-90) == Direction.EAST, "telly east");
      check(TellyScaffoldController.cardinal(540) == Direction.NORTH, "telly wrapped north");
      direction(0, 1, 0, 0, 1);
      direction(0, 1, 90, -1, 0);
      direction(0, 1, 180, 0, -1);
      direction(0, 1, -90, 1, 0);
      direction(0, -1, 0, 0, -1);
      direction(1, 0, 0, 1, 0);
      direction(-1, 0, 0, -1, 0);
      direction(0, 0, 50, 0, 0);
      direction(1, 1, 0, Math.sqrt(0.5), Math.sqrt(0.5));
      near(ScaffoldModule.stepAngle(179, -179, 5, 1), 181, "shortest wrap path");
      near(ScaffoldModule.stepAngle(-179, 179, 1, 1), -180, "negative wrap path");
      near(ScaffoldModule.stepAngle(20, 25, 18, 1), 25, "no overshoot");
      near(ScaffoldModule.stepAngle(20, -30, 18, 1), 2, "turn limit");
      for (float start : new float[] {-720, -179, 0, 179, 720}) {
         float value = start;
         for (int i = 0; i < 100; i++) {
            float next = ScaffoldModule.stepAngle(value, 137, 2, 1);
            check(Math.abs(next - value) <= 2.001, "bounded step");
            value = next;
         }
         near(Mth.wrapDegrees(value - 137), 0, "converges");
      }
      // The face grid is inset from corners, with its fixed coordinate on the face.
      BlockPos support = new BlockPos(-3, 63, 5);
      for (Direction face : Direction.values()) {
         Vec3 normal = Vec3.atLowerCornerOf(face.getUnitVec3i());
         Vec3 centre = Vec3.atCenterOf(support).add(normal.scale(0.5));
         for (double u : new double[] {0.08, 0.5, 0.92}) {
            for (double v : new double[] {0.08, 0.5, 0.92}) {
               Vec3 point = ScaffoldModule.facePoint(support, face, u, v);
               near(point.subtract(centre).dot(normal), 0, "sample on face plane");
               check(point.x >= -3 && point.x <= -2 && point.y >= 63 && point.y <= 64
                  && point.z >= 5 && point.z <= 6, "sample inside block bounds");
            }
         }
      }
      // Vec3.directionFromRotation uses float trigonometry: allow its precision here.
      check(Math.abs(ScaffoldModule.angularCost(179, 0, new Rotation(-179, 0)) - 2) < 0.02,
         "nearest yaw across wrap");
      check(ScaffoldModule.angularCost(0, 89, new Rotation(160, 89))
         < ScaffoldModule.angularCost(0, 89, new Rotation(2, 85)), "crosshair cost near vertical view");
      check(ScaffoldModule.angularCost(0, 30, new Rotation(2, 32))
         < ScaffoldModule.angularCost(0, 30, new Rotation(40, 35)), "nearest crosshair point wins");
      for (float delta : new float[] {-180, -20, -0.1F, 0, 0.1F, 20, 180}) {
         float step = ScaffoldModule.smoothStep(delta, 30, 0.5F);
         check(Math.abs(step) <= 30 && Math.abs(step) <= Math.abs(delta), "smooth step bounded");
         check(step == 0 || Math.signum(step) == Math.signum(delta), "smooth step direction");
      }
      check(Math.abs(ScaffoldModule.smoothStep(10, 30, 0.5F))
         < Math.abs(ScaffoldModule.smoothStep(60, 30, 0.5F)), "eases near target");
      // Airborne movement must retain its world heading while the camera turns back.
      for (float heading : new float[] {-179, -90, 0, 90, 179}) {
         for (float camera : new float[] {-179, -90, 0, 90, 179}) {
            for (float[] keys : new float[][] {{0, 1}, {1, 0}, {1, 1}, {0, 0}, {0, -1}}) {
               Vec3 desired = ScaffoldModule.inputDirection(keys[0], keys[1], heading);
               var local = ScaffoldModule.localInput(desired, camera);
               Vec3 actual = ScaffoldModule.inputDirection(local.x, local.y, camera);
               near(actual.x, desired.x, "air movement world X");
               near(actual.z, desired.z, "air movement world Z");
            }
         }
      }
      near(ScaffoldModule.smoothStep(180, 0, 0.5F), 0, "zero frame time cannot rotate");
      check(!ScaffoldCoachModule.clockConflict(false, 1, 0.25F), "can borrow idle clock");
      check(!ScaffoldCoachModule.clockConflict(true, 0.25F, 0.25F), "own clock can be restored");
      check(ScaffoldCoachModule.clockConflict(true, 2, 0.25F), "must not reset a foreign timer");
      check(ScaffoldCoachModule.clockConflict(false, 0.5F, 0.25F), "must not claim occupied clock");
      check(ScaffoldCoachModule.clockConflict(true, Float.NaN, 0.25F), "invalid clock is a conflict");
      check(ScaffoldCoachModule.lineTarget(new BlockPos(8, 63, 2), 0, 2, Direction.EAST), "forward flat X row");
      check(!ScaffoldCoachModule.lineTarget(new BlockPos(8, 63, 3), 0, 2, Direction.EAST), "no sideways detour");
      check(!ScaffoldCoachModule.lineTarget(new BlockPos(-1, 63, 2), 0, 2, Direction.EAST), "no backwards row");
      check(ScaffoldCoachModule.lineTarget(new BlockPos(0, 63, -8), 0, 2, Direction.NORTH), "forward negative Z row");
      check(!ScaffoldCoachModule.lineTarget(new BlockPos(0, 63, 2), 0, 2, Direction.UP), "no tower row");
      check(!ScaffoldCoachModule.manualHitAllowed(BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO)),
         "manual miss must not dispatch block use");
      check(ScaffoldCoachModule.manualHitAllowed(new BlockHitResult(new Vec3(8, 65, 3), Direction.UP,
         new BlockPos(8, 64, 3), false)), "manual top-face click outside the practice Y reaches vanilla");
      check(ScaffoldCoachModule.manualHitAllowed(new BlockHitResult(new Vec3(-1, 63.5, 3), Direction.WEST,
         new BlockPos(-1, 63, 3), false)), "manual click outside the practice row reaches vanilla");
      check(ScaffoldCoachModule.manualHitAllowed(new BlockHitResult(new Vec3(0, 64, 0), Direction.UP,
         new BlockPos(0, 63, 0), true)), "inside-hit placement context belongs to vanilla, not Coach");
      check(!ScaffoldCoachModule.INSTANCE.isEnabled(), "coach disabled by default");
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.getSettings().size() == 17, "only Telly controls registered");
      for (var setting : module.getSettings()) check((setting.getName().startsWith("Telly ") || setting.getName().equals("Cam Lock")), "no legacy mode controls");
      var defaults = module.tellySettings();
      check(defaults.autoJump() && defaults.autoSwitch() && defaults.diagnostics(), "usable Telly defaults");
      check(defaults.startTick() == 1 && defaults.cps() == 15 && defaults.blocks() == 3, "responsive defaults");
      check(defaults.jumpDistance() < 0.85, "do not jump a whole block before edge");
      var quiet = new ScaffoldModule.TellySettings(true, true, true, true, false,
         0.3, 3, 1, 15, 180, 180, 1, 4.5, 5, true, true, 20);
      TellyScaffoldController controller = new TellyScaffoldController();
      controller.pause("GUI", quiet);
      check(controller.phase() == TellyScaffoldController.Phase.IDLE, "GUI pauses to resumable idle, not permanent HALT");
      controller.pause("no blocks", quiet);
      check(controller.phase() == TellyScaffoldController.Phase.IDLE, "item failure can be retried after selecting blocks");
      controller.reset();
      check(controller.phase() == TellyScaffoldController.Phase.IDLE, "disable clears the complete cycle");
      for (Direction heading : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
         check(TellyScaffoldController.cardinal(TellyScaffoldController.headingYaw(heading)) == heading, "heading round trip");
      }
      for (var setting : module.getSettings()) {
         if (setting instanceof NumberSetting number) {
            check(Double.isFinite(number.get()) && number.get() >= number.getMin()
               && number.get() <= number.getMax(), "valid default: " + setting.getName());
         }
      }
      System.out.println("ScaffoldModuleTest passed");
   }
}
