package net.skycore.client.module.movement;

import java.util.List;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for the Eagle/Telly Scaffold module. */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   private static BooleanSetting bool(ScaffoldModule module, String name) {
      return (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals(name)).findFirst().orElseThrow();
   }

   private static ModeSetting mode(ScaffoldModule module, String name) {
      return (ModeSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals(name)).findFirst().orElseThrow();
   }

   private static NumberSetting number(ScaffoldModule module, String name) {
      return (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals(name)).findFirst().orElseThrow();
   }

   public static void main(String[] args) {
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("Eagle"), "Eagle is the default mode suffix");
      check(module.getSettings().size() == 11,
         "Mode, AutoPlace, CPS, AutoSwitch, Rotate, ClientLock, SafeWalk, AllowShift, Legit, Telly, KeepHeight");

      ModeSetting mode = mode(module, "Mode");
      check(mode.getName().equals("Mode"), "Mode setting name");
      check(mode.get().equals("Eagle"), "Eagle is the default mode");
      check(mode.getModes().equals(List.of("Eagle", "Telly")), "declared modes");

      BooleanSetting autoPlace = bool(module, "AutoPlace");
      NumberSetting cps = number(module, "CPS");
      ModeSetting autoSwitch = mode(module, "AutoSwitch");
      ModeSetting rotate = mode(module, "Rotate");
      BooleanSetting clientLock = bool(module, "ClientLock");

      check(!autoPlace.get(), "AutoPlace is opt-in");
      check(cps.get() == 10.0 && cps.getMin() == 1.0 && cps.getMax() == 20.0 && cps.getStep() == 1.0,
         "CPS range");
      check(autoSwitch.get().equals("Silent"), "Silent is the default AutoSwitch mode");
      check(autoSwitch.getModes().equals(List.of("Normal", "Silent", "Inventory", "None")),
         "AutoSwitch modes");
      check(rotate.get().equals("Normal"), "Normal is the default Rotate mode");
      check(rotate.getModes().equals(List.of("Normal", "Smooth", "Linear", "None")), "Rotate modes");
      check(clientLock.get(), "ClientLock is enabled by default");
      check(!cps.isVisible() && !autoSwitch.isVisible() && !rotate.isVisible() && !clientLock.isVisible(),
         "AutoPlace settings are hidden while AutoPlace is disabled in Eagle mode");

      autoPlace.set(true);
      check(cps.isVisible() && autoSwitch.isVisible() && rotate.isVisible() && clientLock.isVisible(),
         "AutoPlace settings become visible when enabled");

      rotate.set("None");
      check(!clientLock.isVisible(), "ClientLock is hidden while Rotate is None");
      rotate.set("Normal");
      check(clientLock.isVisible(), "ClientLock returns with a rotating mode");

      BooleanSetting safeWalk = bool(module, "SafeWalk");
      BooleanSetting allowShift = bool(module, "AllowShift");
      BooleanSetting legit = bool(module, "Legit");
      BooleanSetting telly = bool(module, "Telly");
      BooleanSetting keepHeight = bool(module, "KeepHeight");
      check(!safeWalk.isVisible() && !allowShift.isVisible() && !legit.isVisible()
         && !telly.isVisible() && !keepHeight.isVisible(),
         "Telly settings are hidden in Eagle mode");
      check(!safeWalk.get() && !allowShift.get() && legit.get() && !telly.get() && !keepHeight.get(),
         "Telly defaults: Legit on, the rest off");

      autoPlace.set(false);
      mode.set("Telly");
      check(module.suffix().equals("Telly"), "suffix follows the mode");
      check(autoSwitch.isVisible() && safeWalk.isVisible() && allowShift.isVisible()
         && legit.isVisible() && telly.isVisible() && keepHeight.isVisible(),
         "Telly settings appear with the mode");
      check(!cps.isVisible() && !rotate.isVisible() && !clientLock.isVisible(),
         "Eagle-only settings hide in Telly mode");

      safeWalk.set(true);
      check(!allowShift.isVisible(), "AllowShift hides while SafeWalk owns the shift key");
      safeWalk.set(false);
      check(allowShift.isVisible(), "AllowShift returns without SafeWalk");

      mode.set("Eagle");
      check(!safeWalk.isVisible() && !legit.isVisible(), "leaving Telly hides its settings again");
      check(module.suffix().equals("Eagle"), "suffix follows the mode back");
      System.out.println("ScaffoldModuleTest passed");
   }
}
