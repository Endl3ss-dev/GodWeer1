package net.skycore.client.module.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
      direction(0, 1, 0, 0, 1);
      direction(0, 1, 90, -1, 0);
      direction(0, 1, 180, 0, -1);
      direction(0, 1, -90, 1, 0);
      direction(0, -1, 0, 0, -1);
      direction(1, 0, 0, 1, 0);
      direction(-1, 0, 0, -1, 0);
      direction(0, 0, 50, 0, 0);
      direction(1, 1, 0, Math.sqrt(0.5), Math.sqrt(0.5));
      near(ScaffoldModule.stepAngle(179, -179, 5), 181, "shortest wrap path");
      near(ScaffoldModule.stepAngle(-179, 179, 1), -180, "negative wrap path");
      near(ScaffoldModule.stepAngle(20, 25, 18), 25, "no overshoot");
      near(ScaffoldModule.stepAngle(20, -30, 18), 2, "turn limit");
      for (float start : new float[] {-720, -179, 0, 179, 720}) {
         float value = start;
         for (int i = 0; i < 100; i++) {
            float next = ScaffoldModule.stepAngle(value, 137, 2);
            check(Math.abs(next - value) <= 2.001, "bounded step");
            value = next;
         }
         near(Mth.wrapDegrees(value - 137), 0, "converges");
      }
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.getSettings().size() == 14, "all controls registered");
      for (var setting : module.getSettings()) {
         if (setting instanceof NumberSetting number) {
            check(Double.isFinite(number.get()) && number.get() >= number.getMin()
               && number.get() <= number.getMax(), "valid default: " + setting.getName());
         }
      }
      System.out.println("ScaffoldModuleTest passed");
   }
}
