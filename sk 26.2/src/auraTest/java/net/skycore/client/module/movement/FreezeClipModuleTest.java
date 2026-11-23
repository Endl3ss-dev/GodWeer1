package net.skycore.client.module.movement;

import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for Freeze Clip. */
public final class FreezeClipModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      FreezeClipModule module = FreezeClipModule.INSTANCE;
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.getSettings().size() == 3, "Delay, Distance and Auto settings");

      NumberSetting delay = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Delay")).findFirst().orElseThrow();
      NumberSetting distance = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Distance")).findFirst().orElseThrow();
      BooleanSetting auto = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Auto")).findFirst().orElseThrow();

      check(delay.get() == 1000.0 && delay.getMin() == 0.0 && delay.getMax() == 3000.0 && delay.getStep() == 50.0,
         "Delay range");
      check(distance.get() == 3.0 && distance.getMin() == 1.0 && distance.getMax() == 5.0 && distance.getStep() == 0.5,
         "Distance range");
      check(auto.get(), "Auto trigger is on by default");
      System.out.println("FreezeClipModuleTest passed");
   }
}
