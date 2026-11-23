package net.skycore.client.module.movement;

import java.util.List;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for Slab Pass. */
public final class SlabPassModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      SlabPassModule module = SlabPassModule.INSTANCE;
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("Auto"), "Auto is the default mode suffix");
      check(module.getSettings().size() == 2, "Mode plus Boost settings");

      ModeSetting mode = (ModeSetting) module.getSettings().get(0);
      check(mode.getName().equals("Mode"), "Mode setting name");
      check(mode.get().equals("Auto"), "Auto is the default mode");
      check(mode.getModes().equals(List.of("Auto", "Over", "Through")), "declared modes");

      NumberSetting boost = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Boost")).findFirst().orElseThrow();
      check(boost.get() == 1.0 && boost.getMin() == 0.8 && boost.getMax() == 1.3 && boost.getStep() == 0.05,
         "Boost range");
      check(boost.isVisible(), "Boost is visible in Auto mode");

      mode.set("Through");
      check(module.suffix().equals("Through"), "suffix follows the mode");
      check(!boost.isVisible(), "Boost hides in Through mode");
      mode.set("Over");
      check(boost.isVisible(), "Boost returns in Over mode");
      System.out.println("SlabPassModuleTest passed");
   }
}
