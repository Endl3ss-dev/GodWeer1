package net.skycore.client.module.combat;

import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.Setting;

/** Selects the legacy cooldown-based cadence or the retained 1.8 burst cadence. */
final class AuraClicks {
   static final String BURST_MODE = "1.8";

   final ModeSetting mode = new ModeSetting("Click mode", "1.12", "1.12", BURST_MODE);
   final Setting<?>[] settings = {mode};

   boolean usesBurst() {
      return mode.is(BURST_MODE);
   }
}
