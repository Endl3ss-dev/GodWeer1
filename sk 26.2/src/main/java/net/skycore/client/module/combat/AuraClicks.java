package net.skycore.client.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/** Client-thread clock for 1.8 clicks. Polling never consumes a click or re-rolls its CPS. */
final class AuraClicks {
   final ModeSetting mode = new ModeSetting("Click mode", "1.12", "1.12", "1.8");
   final ModeSetting pattern = new ModeSetting("CPS mode", "Static CPS", "Static CPS", "Custom CPS");
   final NumberSetting fixed = new NumberSetting("Clicks per second", 10, 1, 35, 1);
   final NumberSetting min = new NumberSetting("Min clicks per second", 8, 1, 35, 1);
   final NumberSetting max = new NumberSetting("Max clicks per second", 12, 1, 35, 1);
   final BooleanSetting tickHits = new BooleanSetting("Tick hits", false);
   final NumberSetting pauseMs = new NumberSetting("Pause duration", "Pause the click clock after each attack (milliseconds).", 0.1, 0.05, 0.45, 0.01, " ms");
   final Setting<?>[] settings = {mode, pattern, fixed, min, max, tickHits, pauseMs};
   private final RandomGenerator random;
   private boolean scheduled;
   private long nextClick;

   AuraClicks() {
      this(ThreadLocalRandom.current());
   }

   AuraClicks(RandomGenerator random) {
      this.random = random;
      pattern.visibleWhen(this::usesCps);
      fixed.visibleWhen(() -> usesCps() && pattern.is("Static CPS"));
      min.visibleWhen(() -> usesCps() && pattern.is("Custom CPS"));
      max.visibleWhen(min::isVisible);
      tickHits.visibleWhen(this::usesCps);
      pauseMs.visibleWhen(() -> usesCps() && tickHits.get());
   }

   boolean usesCps() {
      return mode.is("1.8");
   }

   boolean ready(long now) {
      return !scheduled || now - nextClick >= 0;
   }

   void recordClick(long now) {
      int cps = fixed.get().intValue();
      boolean variable = false;
      if (pattern.is("Custom CPS")) {
         // Adapted from Rockstar/src/main/java/moscow/rockstar/module/combat/Aura.java#getAttackDelayMs (inclusive, order-independent bounds).
         int a = min.get().intValue();
         int b = max.get().intValue();
         variable = a != b;
         cps = variable ? random.nextInt(Math.min(a, b), Math.max(a, b) + 1) : a;
      }
      long period = Math.round(1_000_000_000.0 / cps);
      long pause = tickHits.get() ? Math.round(pauseMs.get() * 1_000_000.0) : 0;
      // A fixed rate can absorb modest frame lateness without drifting. A variable delay starts
      // at the real click so the next sampled interval is never shortened below the custom bounds.
      // Leave at least a third of a fixed beat between clicks; drop entire missed beats, never queue a burst.
      long late = now - nextClick;
      long credit = !variable && scheduled && late >= 0 && late < period ? Math.min(late, period * 2 / 3) : 0;
      nextClick = now + period - credit + pause;
      scheduled = true;
   }

   void reset() {
      scheduled = false;
   }
}
