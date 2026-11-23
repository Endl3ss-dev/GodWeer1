package net.skycore.client.module.movement;

import java.util.List;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/** Headless settings smoke test for Eagle and its optional AutoPlace controls. */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   public static void main(String[] args) {
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("Eagle"), "Eagle is the only mode");
      check(module.getSettings().size() == 11, "Eagle plus ten AutoPlace settings");

      ModeSetting mode = (ModeSetting) module.getSettings().get(0);
      check(mode.getName().equals("Mode"), "Mode setting name");
      check(mode.get().equals("Eagle"), "Eagle is the default mode");
      check(mode.getModes().equals(List.of("Eagle")), "Eagle is the only declared mode");

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
      BooleanSetting lockY = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("LockY")).findFirst().orElseThrow();
      BooleanSetting diagonal = (BooleanSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("Diagonal")).findFirst().orElseThrow();
      NumberSetting smoothSpeed = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("SmoothSpeed")).findFirst().orElseThrow();
      NumberSetting smoothAccel = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("SmoothAccel")).findFirst().orElseThrow();
      NumberSetting smoothBrake = (NumberSetting) module.getSettings().stream()
         .filter(setting -> setting.getName().equals("SmoothBrake")).findFirst().orElseThrow();
      check(module.getSettings().stream().noneMatch(setting -> setting.getName().equals("LastMomentPlace")),
         "LastMomentPlace was removed");

      check(!autoPlace.get(), "AutoPlace is opt-in");
      check(cps.get() == 10.0 && cps.getMin() == 1.0 && cps.getMax() == 20.0 && cps.getStep() == 1.0,
         "CPS range");
      check(autoSwitch.get().equals("Silent"), "Silent is the default AutoSwitch mode");
      check(autoSwitch.getModes().equals(List.of("Normal", "Silent", "Inventory", "None")),
         "AutoSwitch modes");
      check(autoSwitch.isVisible() == false && cps.isVisible() == false && rotate.isVisible() == false
         && clientLock.isVisible() == false && lockY.isVisible() == false && diagonal.isVisible() == false
         && smoothSpeed.isVisible() == false && smoothAccel.isVisible() == false && smoothBrake.isVisible() == false,
         "AutoPlace settings are hidden while AutoPlace is disabled");
      check(rotate.get().equals("Normal"), "Normal is the default Rotate mode");
      check(rotate.getModes().equals(List.of("Normal", "Smooth", "Linear", "None")), "Rotate modes");
      check(clientLock.get(), "ClientLock is enabled by default");
      check(!lockY.get(), "LockY is opt-in");
      check(!diagonal.get(), "Diagonal is opt-in");
      check(smoothSpeed.get() == 420.0 && smoothAccel.get() == 2600.0 && smoothBrake.get() == 12.0,
         "Smooth defaults");

      autoPlace.set(true);
      check(cps.isVisible() && autoSwitch.isVisible() && rotate.isVisible() && clientLock.isVisible()
         && lockY.isVisible() && diagonal.isVisible(),
         "AutoPlace settings become visible when enabled");
      check(smoothSpeed.isVisible() == false, "Smooth settings are hidden until Smooth is selected");

      rotate.set("Smooth");
      check(smoothSpeed.isVisible() && smoothAccel.isVisible() && smoothBrake.isVisible(),
         "Smooth settings appear in Smooth mode");
      rotate.set("Normal");

      rotate.set("None");
      check(clientLock.isVisible() == false, "ClientLock is hidden while Rotate is None");
      rotate.set("Normal");
      check(clientLock.isVisible(), "ClientLock returns with a rotating mode");

      // Smooth controller: 180-degree flick must accelerate, cap its speed,
      // brake softly, settle quickly and never overshoot the target.
      ScaffoldSmoothRotation smoother = new ScaffoldSmoothRotation();
      smoother.seed(0.0F, 0.0F);
      double dt = 1.0 / 20.0;
      float previousStep = 0.0F;
      float secondStep = 0.0F;
      float maxStep = 0.0F;
      float yaw = 0.0F;
      boolean settled = false;
      for (int tick = 0; tick < 100; tick++) {
         float[] next = smoother.update(170.0F, 80.0F, 420.0, 2600.0, 12.0, dt);
         float step = next[0] - yaw;
         yaw = next[0];
         if (tick == 0) previousStep = step;
         if (tick == 1) secondStep = step;
         maxStep = Math.max(maxStep, step);
         check(yaw <= 170.0F + 0.001F, "Smooth never overshoots the target yaw");
         if (smoother.isSettled(170.0F, 80.0F, 3.0F)) {
            settled = true;
            break;
         }
      }
      check(secondStep > previousStep, "Smooth accelerates at the start of a turn");
      check(maxStep <= 420.0F * dt + 0.001F, "Smooth respects the maximum speed");
      check(settled, "Smooth settles on the target");
      check(Math.abs(yaw - 170.0F) <= 3.0F, "Smooth lands on the target yaw");

      System.out.println("ScaffoldModuleTest passed");
   }
}
