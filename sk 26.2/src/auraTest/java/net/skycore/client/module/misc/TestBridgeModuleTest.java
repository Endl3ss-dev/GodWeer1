package net.skycore.client.module.misc;

import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for Test Bridge. */
public final class TestBridgeModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      TestBridgeModule module = TestBridgeModule.INSTANCE;
      check(module.getCategory() == ModuleCategory.MISC, "misc category");
      check(!module.isEnabled(), "disabled by default");
      check(module.getSettings().size() == 1, "Port is the only setting");

      NumberSetting port = (NumberSetting) module.getSettings().get(0);
      check(port.getName().equals("Port"), "Port setting name");
      check(port.get() == 25599.0 && port.getMin() == 1024.0 && port.getMax() == 49151.0 && port.getStep() == 1.0,
         "Port range");
      System.out.println("TestBridgeModuleTest passed");
   }
}
