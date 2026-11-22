package net.skycore.client.module.movement;

import java.util.List;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ModeSetting;

/** Headless settings smoke test for the Eagle-only Scaffold module. */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("Eagle"), "Eagle is the only mode");
      check(module.getSettings().size() == 1, "only Eagle's Mode setting remains");

      var setting = module.getSettings().getFirst();
      check(setting instanceof ModeSetting, "Mode setting type");
      ModeSetting mode = (ModeSetting) setting;
      check(mode.getName().equals("Mode"), "Mode setting name");
      check(mode.get().equals("Eagle"), "Eagle is the default mode");
      check(mode.getModes().equals(List.of("Eagle")), "Eagle is the only declared mode");
      System.out.println("ScaffoldModuleTest passed");
   }
}
