package net.skycore.client.module.movement;

import java.util.List;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for Eagle, its AutoPlace controls and the Telly mode (CatLean Scaffold port). */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("Eagle"), "Eagle is the default mode suffix");
      check(module.getSettings().size() == 28,
         "Eagle plus AutoPlace, thirteen CatLean Telly and nine CUSTOM timing settings");

      ModeSetting mode = (ModeSetting) module.getSettings().get(0);
      check(mode.getName().equals("Mode"), "Mode setting name");
      check(mode.get().equals("Eagle"), "Eagle is the default mode");
      check(mode.getModes().equals(List.of("Eagle", "Telly")), "declared modes");

      BooleanSetting autoPlace = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("AutoPlace")).findFirst().orElseThrow();
      NumberSetting cps = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CPS")).findFirst().orElseThrow();
      ModeSetting autoSwitch = (ModeSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("AutoSwitch")).findFirst().orElseThrow();
      ModeSetting rotate = (ModeSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Rotate")).findFirst().orElseThrow();
      BooleanSetting clientLock = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("ClientLock")).findFirst().orElseThrow();

      check(!autoPlace.get(), "AutoPlace is opt-in");
      check(cps.get() == 10.0 && cps.getMin() == 1.0 && cps.getMax() == 20.0 && cps.getStep() == 1.0,
         "CPS range");
      check(autoSwitch.get().equals("Silent"), "Silent is the default AutoSwitch mode");
      check(autoSwitch.getModes().equals(List.of("Normal", "Silent", "Inventory", "None")),
         "AutoSwitch modes");
      check(autoSwitch.isVisible() == false && cps.isVisible() == false && rotate.isVisible() == false
         && clientLock.isVisible() == false,
         "AutoPlace settings are hidden while AutoPlace is disabled");
      check(rotate.get().equals("Normal"), "Normal is the default Rotate mode");
      check(rotate.getModes().equals(List.of("Normal", "Smooth", "Linear", "None")), "Rotate modes");
      check(clientLock.get(), "ClientLock is enabled by default");

      autoPlace.set(true);
      check(cps.isVisible() && autoSwitch.isVisible() && rotate.isVisible() && clientLock.isVisible(),
         "AutoPlace settings become visible when enabled");

      rotate.set("None");
      check(clientLock.isVisible() == false, "ClientLock is hidden while Rotate is None");
      rotate.set("Normal");
      check(clientLock.isVisible(), "ClientLock returns with a rotating mode");

      BooleanSetting tower = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Tower")).findFirst().orElseThrow();
      ModeSetting timing = (ModeSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Timing")).findFirst().orElseThrow();
      BooleanSetting grimSilent = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("GrimSilent")).findFirst().orElseThrow();
      check(timing.get().equals("GRIM"), "GRIM is the default Telly timing");
      check(timing.getModes().equals(List.of("SEQUENTIAL", "VANILLA", "GRIM", "CUSTOM")),
         "Telly timings");
      check(!tower.isVisible(), "Telly settings are hidden in Eagle mode");
      autoPlace.set(false);
      mode.set("Telly");
      check(module.suffix().equals("Telly"), "suffix follows the mode");
      check(tower.isVisible() && timing.isVisible() && autoSwitch.isVisible(),
         "Telly settings appear with the mode");
      check(autoPlace.isVisible(), "AutoPlace toggle stays visible in Telly mode");
      check(!cps.isVisible() && !rotate.isVisible() && !clientLock.isVisible(),
         "Eagle-only settings hide in Telly mode");
      check(!grimSilent.get(), "GrimSilent is off by default");

      BooleanSetting customVisible = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomVisible")).findFirst().orElseThrow();
      NumberSetting customRotateDelay = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomRotateDelay")).findFirst().orElseThrow();
      NumberSetting customPlaceDelay = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomPlaceDelay")).findFirst().orElseThrow();
      NumberSetting customYawStep = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomYawStep")).findFirst().orElseThrow();
      BooleanSetting customRetry = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomRetry")).findFirst().orElseThrow();
      NumberSetting customRetryDelay = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("CustomRetryDelay")).findFirst().orElseThrow();

      check(customVisible.get(), "CUSTOM rotation is visible by default");
      check(customRotateDelay.get() == 0.0 && customRotateDelay.getMin() == 0.0 && customRotateDelay.getMax() == 4.0,
         "rotation delay after reaching the angles is 0..4 ticks");
      check(customPlaceDelay.get() == 1.0 && customPlaceDelay.getMin() == 0.0 && customPlaceDelay.getMax() == 4.0,
         "placement is one tick after the rotation by default");
      check(customYawStep.get() == 180.0 && customYawStep.getMin() == 30.0 && customYawStep.getMax() == 180.0,
         "yaw step limit");
      check(customRetry.get() && customRetryDelay.get() == 2.0
         && customRetryDelay.getMin() == 1.0 && customRetryDelay.getMax() == 4.0,
         "placement retry is enabled with a two-tick delay");
      check(!customVisible.isVisible() && !customRotateDelay.isVisible() && !customPlaceDelay.isVisible()
         && !customRetryDelay.isVisible(),
         "CUSTOM settings are hidden while the GRIM timing is selected");

      timing.set("CUSTOM");
      check(customVisible.isVisible() && customRotateDelay.isVisible() && customPlaceDelay.isVisible()
         && customYawStep.isVisible() && customRetry.isVisible() && customRetryDelay.isVisible(),
         "CUSTOM settings appear with the CUSTOM timing");
      customRetry.set(false);
      check(!customRetryDelay.isVisible(), "retry delay hides together with CustomRetry");
      customRetry.set(true);
      timing.set("GRIM");
      check(!customVisible.isVisible(), "leaving CUSTOM hides its settings again");
      System.out.println("ScaffoldModuleTest passed");
   }
}
