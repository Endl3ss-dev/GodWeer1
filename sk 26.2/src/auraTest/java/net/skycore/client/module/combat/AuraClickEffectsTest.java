package net.skycore.client.module.combat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;
import net.skycore.client.config.Translations;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/** Run via testAuraClicks. Synthetic elapsed times, not sleeps or a live server. */
public final class AuraClickEffectsTest {
   private static void check(boolean expected, String message) {
      if (!expected) throw new AssertionError(message);
   }

   private static void limits(NumberSetting s, double min, double max, double step) {
      check(s.getMin() == min && s.getMax() == max && s.getStep() == step, s.getName() + " range/step");
   }

   private static void settings() {
      AuraClicks c = new AuraClicks();
      AuraCameraJitter j = new AuraCameraJitter();
      check(c.mode.getModes().equals(List.of("1.12", "1.8")) && !c.usesCps(), "legacy default");
      check(!c.pattern.isVisible() && !c.fixed.isVisible() && !c.min.isVisible() && !c.max.isVisible() && !c.tickHits.isVisible(), "legacy UI");
      c.mode.set("1.8");
      check(c.pattern.isVisible() && c.fixed.isVisible() && !c.min.isVisible() && !c.max.isVisible() && c.tickHits.isVisible(), "static UI");
      check(!c.minPauseMs.isVisible() && !c.maxPauseMs.isVisible(), "pause bounds hidden");
      c.tickHits.set(true);
      check(c.minPauseMs.isVisible() && c.maxPauseMs.isVisible(), "pause bounds visible");
      c.pattern.set("Custom CPS");
      check(!c.fixed.isVisible() && c.min.isVisible() && c.max.isVisible(), "custom UI");
      for (NumberSetting s : List.of(c.fixed, c.min, c.max)) limits(s, 1, 35, 1);
      limits(c.minPauseMs, 0, 0.45, 0.01);
      limits(c.maxPauseMs, 0, 0.45, 0.01);
      limits(j.speed, 0, 20, 0.1);
      limits(j.strength, 0, 5, 0.1);
      check(!j.enabled.get() && !j.whileAiming.isVisible() && !j.onHit.isVisible() && !j.speed.isVisible() && !j.strength.isVisible(), "jitter off by default");
      j.enabled.set(true);
      check(j.whileAiming.isVisible() && j.onHit.isVisible() && j.speed.isVisible() && j.strength.isVisible(), "jitter UI");
      for (Setting<?>[] group : List.of(c.settings, j.settings)) {
         for (Setting<?> s : group) {
            String ru = Translations.russian(s.getName());
            check(!ru.equals(s.getName()) && Translations.english(ru).equals(s.getName()), "translation: " + s.getName());
         }
      }
      check(Translations.english("Статичный CPS").equals("Static CPS") && Translations.english("Кастомный CPS").equals("Custom CPS"), "CPS labels");
   }

   private static void deadline(AuraClicks c, long t) {
      check(!c.ready(t - 1) && c.ready(t), "due exactly at " + t);
   }

   private static void staticClock() {
      AuraClicks c = new AuraClicks();
      c.mode.set("1.8");
      for (int cps = 1; cps <= 35; cps++) {
         c.fixed.set((double)cps);
         c.reset();
         long t = -1_000_000;
         long period = Math.round(1_000_000_000.0 / cps);
         check(c.ready(t), "immediate first click");
         for (int i = 0; i < 3; i++) {
            c.recordClick(t);
            t += period;
            deadline(c, t);
         }
      }
      for (int fps : List.of(60, 120)) {
         c.reset();
         int clicks = 0;
         for (int frame = 0; frame < fps * 10; frame++) {
            long now = Math.round(frame * (1_000_000_000.0 / fps));
            if (c.ready(now)) {
               c.recordClick(now);
               clicks++;
            }
            check(!c.ready(now), "no repeat in frame/tick catch-up callbacks");
         }
         check(clicks == 350, "35 CPS over ten seconds at " + fps + " FPS: " + clicks);
      }
      long afterStall = 60_000_000_000L;
      c.recordClick(afterStall);
      deadline(c, afterStall + Math.round(1_000_000_000.0 / 35));
   }

   private static void customClock() {
      var expected = new SplittableRandom(26);
      AuraClicks c = new AuraClicks(new SplittableRandom(26));
      c.mode.set("1.8");
      c.pattern.set("Custom CPS");
      c.min.set(14.0);
      c.max.set(8.0);
      long now = 0;
      Set<Integer> seen = new HashSet<>();
      for (int i = 0; i < 100; i++) {
         now += 2_000_000; // even when late, a custom interval starts at this real click
         int cps = expected.nextInt(8, 15);
         seen.add(cps);
         for (int poll = 0; poll < 10; poll++) check(c.ready(now), "poll must not consume readiness or randomness");
         c.recordClick(now);
         now += Math.round(1_000_000_000.0 / cps);
         deadline(c, now);
      }
      check(seen.size() == 7, "inclusive reversed bounds");
      c.reset();
      c.max.set(14.0);
      c.recordClick(now);
      deadline(c, now + Math.round(1_000_000_000.0 / 14));
   }

   private static long sampledPauseNanos(AuraClicks clicks, long start, long maxPauseNanos) {
      long low = 0;
      long high = maxPauseNanos + 1;
      while (low < high) {
         long middle = low + (high - low) / 2;
         if (clicks.isTemporarilyPaused(start + middle)) low = middle + 1;
         else high = middle;
      }
      return low;
   }

   private static void pause() {
      AuraClicks c = new AuraClicks(new SplittableRandom(19));
      c.mode.set("1.8");
      c.fixed.set(10.0);
      c.tickHits.set(true);
      c.minPauseMs.set(0.15);
      c.maxPauseMs.set(0.15);
      long start = Long.MAX_VALUE - 1_000_000L; // nanoTime deadlines also work across signed wraparound
      c.recordClick(start);
      check(c.isTemporarilyPaused(start), "pause begins immediately after a successful click");
      check(c.isTemporarilyPaused(start + 149_999L), "0.15 ms remains active for 150,000 ns");
      check(!c.isTemporarilyPaused(start + 150_000L), "pause ends at the configured duration");
      check(!c.ready(start + 100_000_000L - 1) && c.ready(start + 100_000_000L), "pause does not extend the CPS interval");

      c.minPauseMs.set(0.05);
      c.maxPauseMs.set(0.25);
      Set<Long> sampled = new HashSet<>();
      long now = 1_000_000_000L;
      for (int i = 0; i < 100; i++) {
         c.recordClick(now);
         long pause = sampledPauseNanos(c, now, 250_000L);
         check(pause >= 50_000L && pause <= 250_000L && pause % 10_000L == 0, "random pause stays within inclusive bounds and 0.01 ms steps");
         sampled.add(pause);
         now += 1_000_000L;
      }
      check(sampled.size() > 1, "random pause changes between successful clicks");

      c.minPauseMs.set(0.25);
      c.maxPauseMs.set(0.05);
      c.recordClick(now);
      long reversed = sampledPauseNanos(c, now, 250_000L);
      check(reversed >= 50_000L && reversed <= 250_000L, "reversed pause bounds are order-independent");

      c.tickHits.set(false);
      c.recordClick(0);
      check(!c.isTemporarilyPaused(0), "disabled tick hits do not pause Attack Aura");
      deadline(c, 100_000_000L);
      c.reset();
      check(!c.isTemporarilyPaused(1), "reset clears the temporary pause");
   }

   private static void jitter() {
      AuraCameraJitter j = new AuraCameraJitter();
      var none = AuraCameraJitter.Offset.NONE;
      j.recordHit(0);
      check(j.sample(0, true).equals(none), "disabled has no effect");
      j.enabled.set(true);
      j.speed.set(10.0);
      j.strength.set(2.0);
      j.sample(0, true);
      var peak = j.sample(25_000_000, true);
      check(Math.abs(peak.yaw() - 2) < 1e-5, "10 Hz quarter cycle, two degrees");
      j.recordHit(30_000_000);
      check(j.sample(30_000_000, false).equals(none), "aim-only stops without a target; hit switch off");
      j.whileAiming.set(false);
      j.onHit.set(true);
      check(j.sample(40_000_000, true).equals(none), "no pulse on merely aiming or enabling hits");
      j.recordHit(40_000_000);
      check(j.sample(40_000_000, false).pitch() == -2, "hit-only impulse even without active aim");
      check(j.sample(100_000_000, false).pitch() != 0, "pulse decays over several frames");
      check(j.sample(160_000_000, false).equals(none), "pulse expires");
      j.whileAiming.set(true);
      j.recordHit(200_000_000);
      for (int i = 0; i < 150; i++) {
         var offset = j.sample(200_000_000L + i * 1_000_000L, true);
         check(Math.abs(offset.yaw()) <= 2 && Math.abs(offset.pitch()) <= 2, "combined bound");
      }
      j.reset();
      check(j.sample(360_000_000, false).equals(none), "lifecycle clears visual state");
      for (NumberSetting zero : List.of(j.speed, j.strength)) {
         zero.set(0.0);
         j.recordHit(400_000_000);
         check(j.sample(401_000_000, true).equals(none), "zero " + zero.getName());
         zero.reset();
      }
   }

   public static void main(String[] args) {
      settings();
      staticClock();
      customClock();
      pause();
      jitter();
      System.out.println("Aura click effects: settings, static CPS, custom CPS, pauses and jitter passed.");
   }
}
