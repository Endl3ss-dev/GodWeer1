package net.skycore.client.module.combat;

import com.google.gson.JsonObject;
import java.util.List;
import net.skycore.client.config.AuraClickModeMigration;
import net.skycore.client.config.Translations;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/** Run via testAuraClicks. Synthetic elapsed times, not sleeps or a live server. */
public final class AuraClickEffectsTest {
   private static void check(boolean expected, String message) {
      if (!expected) throw new AssertionError(message);
   }

   private static void limits(NumberSetting setting, double min, double max, double step) {
      check(setting.getMin() == min && setting.getMax() == max && setting.getStep() == step,
         setting.getName() + " range/step");
   }

   private static void settings() {
      AuraClicks clicks = new AuraClicks();
      AuraBurst burst = new AuraBurst();
      AuraCameraJitter cameraJitter = new AuraCameraJitter();
      AuraFakeRat fakeRat = new AuraFakeRat();

      check(clicks.mode.getModes().equals(List.of("1.12", "1.8")) && !clicks.usesBurst(), "click modes/default");
      check(clicks.settings.length == 1, "the removed CPS options are not registered");
      clicks.mode.set("1.8");
      check(clicks.usesBurst(), "1.8 selects the retained burst cadence");
      clicks.mode.set("1.12");
      check(!clicks.usesBurst(), "1.12 keeps its legacy cooldown cadence");
      limits(burst.attacksPerTick, 1, AuraBurst.MAX_ATTACKS_PER_TICK, 1);
      limits(burst.antiKickDelay, 10, 200, 10);
      check(burst.antiKick.get() && burst.antiKickDelay.isVisible(), "anti kick defaults");
      burst.antiKick.set(false);
      check(!burst.antiKickDelay.isVisible(), "delay hidden without anti kick");

      limits(cameraJitter.speed, 0, 20, 0.1);
      limits(cameraJitter.strength, 0, 5, 0.1);
      check(!cameraJitter.enabled.get() && !cameraJitter.whileAiming.isVisible() && !cameraJitter.onHit.isVisible()
         && !cameraJitter.speed.isVisible() && !cameraJitter.strength.isVisible(), "camera jitter off by default");

      check(!fakeRat.enabled.get(), "FakeRat off by default");
      check(fakeRat.yawMode.getModes().equals(List.of("Default", "Backward", "Sideway Left", "Sideway Right", "Slow Spin",
         "Fast Spin", "Ultra Fast Spin", "Jitter", "Custom Spin", "Custom")), "yaw modes and no redundant Off");
      check(fakeRat.pitchMode.getModes().equals(List.of("Up", "Down", "Default", "Jitter", "Custom")), "pitch modes and no redundant Off");
      check(!fakeRat.yawMode.isVisible() && !fakeRat.pitchMode.isVisible() && !fakeRat.jitterSpeed.isVisible()
         && !fakeRat.customYaw.isVisible() && !fakeRat.customPitch.isVisible(), "FakeRat controls hidden while disabled");
      fakeRat.enabled.set(true);
      check(fakeRat.yawMode.isVisible() && fakeRat.pitchMode.isVisible(), "FakeRat exposes its modes when enabled");
      limits(fakeRat.jitterSpeed, 0, 20, 0.1);
      limits(fakeRat.jitterStrength, 0, 5, 0.1);
      limits(fakeRat.customYaw, 0, 360, 1);
      limits(fakeRat.customPitch, -90, 90, 1);
      limits(fakeRat.spinSpeed, 0, 100, 1);
      limits(fakeRat.switchFrequency, 0, 10, 1);
      limits(fakeRat.switchDuration, 0.5, 4, 0.1);

      fakeRat.yawMode.set("Jitter");
      check(fakeRat.jitterSpeed.isVisible() && fakeRat.jitterStrength.isVisible(), "jitter settings visible for yaw jitter");
      fakeRat.yawMode.set("Custom");
      fakeRat.pitchMode.set("Custom");
      check(fakeRat.customYaw.isVisible() && fakeRat.customPitch.isVisible() && fakeRat.customJitter.isVisible()
         && fakeRat.customSpin.isVisible(), "custom controls visible");
      check(!fakeRat.spinSpeed.isVisible(), "custom spin settings stay hidden until enabled");
      fakeRat.customSpin.set(true);
      check(fakeRat.spinSpeed.isVisible() && fakeRat.switchSides.isVisible(), "custom spin settings visible");
      fakeRat.switchSides.set(true);
      check(fakeRat.switchFrequency.isVisible() && fakeRat.switchDuration.isVisible(), "side-switch settings visible");

      for (Setting<?>[] group : List.of(clicks.settings, burst.settings, cameraJitter.settings, fakeRat.settings)) {
         for (Setting<?> setting : group) {
            String russian = Translations.russian(setting.getName());
            check(!russian.equals(setting.getName()) && Translations.english(russian).equals(setting.getName()),
               "translation: " + setting.getName());
         }
      }
      for (String mode : fakeRat.yawMode.getModes()) {
         check(Translations.english(Translations.russian(mode)).equals(mode), "yaw translation: " + mode);
      }
      for (String mode : fakeRat.pitchMode.getModes()) {
         check(Translations.english(Translations.russian(mode)).equals(mode), "pitch translation: " + mode);
      }
   }

   private static void profileModeMigration() {
      JsonObject oldCps = new JsonObject();
      oldCps.addProperty("Режим кликов", "1.8");
      AuraClickModeMigration.migrate(oldCps);
      check("1.12".equals(oldCps.get("Click mode").getAsString()), "legacy CPS 1.8 falls back to 1.12");
      check(oldCps.get(AuraClickModeMigration.SCHEMA_KEY).getAsInt() == AuraClickModeMigration.CURRENT_SCHEMA,
         "legacy profile is marked after migration");
      AuraClickModeMigration.migrate(oldCps);
      check("1.12".equals(oldCps.get("Click mode").getAsString()), "migration is idempotent");

      JsonObject oldBurst = new JsonObject();
      oldBurst.addProperty("Click mode", "1.8 NoDelay");
      AuraClickModeMigration.migrate(oldBurst);
      check("1.8".equals(oldBurst.get("Click mode").getAsString()), "legacy NoDelay is renamed to 1.8");

      JsonObject current = new JsonObject();
      current.addProperty("Click mode", "1.8");
      current.addProperty(AuraClickModeMigration.SCHEMA_KEY, AuraClickModeMigration.CURRENT_SCHEMA);
      AuraClickModeMigration.migrate(current);
      check("1.8".equals(current.get("Click mode").getAsString()), "new-schema 1.8 remains the burst mode");
   }

   private static void burst() {
      AuraBurst burst = new AuraBurst();
      burst.attacksPerTick.set(7.0);
      check(burst.attacks() == 7, "attacks per tick");

      long now = 0;
      burst.antiKick.set(false);
      burst.reset();
      check(burst.ready(now, 10), "first burst ready");
      burst.recordBurst(now, 10);
      check(!burst.ready(now, 10) && !burst.ready(now + 1_000_000, 10), "one burst per game tick");
      check(burst.ready(now, 11), "no delay without anti kick");

      burst.antiKick.set(true);
      burst.antiKickDelay.set(50.0);
      burst.reset();
      burst.recordBurst(now, 20);
      check(!burst.ready(now, 21) && !burst.ready(now + 49_999_999, 21), "anti-kick window is open");
      check(burst.ready(now + 50_000_000, 21), "anti-kick window closes after 50 ms");
      burst.recordBurst(now + 50_000_000, 21);
      check(!burst.ready(now + 50_000_000, 21) && !burst.ready(now + 50_000_000, 22), "same-tick callback cannot re-burst");
      burst.reset();
      check(burst.ready(now, 22), "reset clears the anti-kick window");
   }

   private static void cameraJitter() {
      AuraCameraJitter jitter = new AuraCameraJitter();
      var none = AuraCameraJitter.Offset.NONE;
      jitter.recordHit(0);
      check(jitter.sample(0, true).equals(none), "disabled has no effect");
      jitter.enabled.set(true);
      jitter.speed.set(10.0);
      jitter.strength.set(2.0);
      jitter.sample(0, true);
      var peak = jitter.sample(25_000_000, true);
      check(Math.abs(peak.yaw() - 2) < 1e-5, "10 Hz quarter cycle, two degrees");
      jitter.recordHit(30_000_000);
      check(jitter.sample(30_000_000, false).equals(none), "aim-only stops without a target; hit switch off");
      jitter.whileAiming.set(false);
      jitter.onHit.set(true);
      check(jitter.sample(40_000_000, true).equals(none), "no pulse on merely aiming or enabling hits");
      jitter.recordHit(40_000_000);
      check(jitter.sample(40_000_000, false).pitch() == -2, "hit-only impulse even without active aim");
      check(jitter.sample(100_000_000, false).pitch() != 0, "pulse decays over several frames");
      check(jitter.sample(160_000_000, false).equals(none), "pulse expires");
      jitter.whileAiming.set(true);
      jitter.recordHit(200_000_000);
      for (int i = 0; i < 150; i++) {
         var offset = jitter.sample(200_000_000L + i * 1_000_000L, true);
         check(Math.abs(offset.yaw()) <= 2 && Math.abs(offset.pitch()) <= 2, "combined bound");
      }
      jitter.reset();
      check(jitter.sample(360_000_000, false).equals(none), "lifecycle clears visual state");
      for (NumberSetting zero : List.of(jitter.speed, jitter.strength)) {
         zero.set(0.0);
         jitter.recordHit(400_000_000);
         check(jitter.sample(401_000_000, true).equals(none), "zero " + zero.getName());
         zero.reset();
      }
   }

   private static void fakeRatRotation() {
      AuraFakeRat fakeRat = new AuraFakeRat();
      fakeRat.enabled.set(true);
      AuraFakeRat.RenderRotation rotation = fakeRat.sample(0, 37.0F, 12.0F);
      check(!rotation.applyYaw() && !rotation.applyPitch(), "Default preserves vanilla head rotation");

      fakeRat.yawMode.set("Backward");
      rotation = fakeRat.sample(0, 37.0F, 12.0F);
      check(rotation.applyYaw() && Math.abs(Math.abs(rotation.yaw()) - 180.0F) < 1e-5 && !rotation.applyPitch(), "backward yaw");
      fakeRat.yawMode.set("Sideway Left");
      check(fakeRat.sample(10, 0, 0).yaw() == -90.0F, "left yaw");
      fakeRat.yawMode.set("Sideway Right");
      check(fakeRat.sample(20, 0, 0).yaw() == 90.0F, "right yaw");

      fakeRat.yawMode.set("Slow Spin");
      fakeRat.sample(0, 0, 0);
      check(fakeRat.sample(1_000_000_000L, 0, 0).yaw() == 30.0F, "slow spin preset is 30 degrees per second");
      fakeRat.yawMode.set("Fast Spin");
      fakeRat.sample(0, 0, 0);
      check(fakeRat.sample(500_000_000L, 0, 0).yaw() == 90.0F, "fast spin preset is 180 degrees per second");
      fakeRat.yawMode.set("Ultra Fast Spin");
      fakeRat.sample(0, 0, 0);
      float ultraFastHalfTurn = fakeRat.sample(250_000_000L, 0, 0).yaw();
      check(Math.abs(Math.abs(ultraFastHalfTurn) - 180.0F) < 1e-5, "ultra-fast spin preset is 720 degrees per second");

      fakeRat.yawMode.set("Jitter");
      fakeRat.jitterSpeed.set(10.0);
      fakeRat.jitterStrength.set(2.0);
      fakeRat.sample(0, 0, 0);
      check(Math.abs(fakeRat.sample(25_000_000L, 0, 0).yaw() - 2.0F) < 1e-5, "FakeRat jitter phase and strength");

      fakeRat.yawMode.set("Custom Spin");
      fakeRat.spinSpeed.set(1.0);
      fakeRat.switchSides.set(false);
      fakeRat.sample(0, 0, 0);
      check(fakeRat.sample(250_000_000L, 0, 0).yaw() == 5.0F, "custom speed is degrees per tick at 20 TPS");
      check(AuraFakeRat.spinAngle(1.0, 90.0, false, 0.0, 0.5) == 90.0F, "zero switch frequency keeps one direction");
      double beforeReverse = AuraFakeRat.spinAngle(1.25, 100.0, true, 1.0, 0.5);
      double afterReverse = AuraFakeRat.spinAngle(1.5, 100.0, true, 1.0, 0.5);
      check(afterReverse < beforeReverse, "side switch smoothly reverses angular velocity");

      fakeRat.yawMode.set("Custom");
      fakeRat.pitchMode.set("Custom");
      fakeRat.customSpin.set(false);
      fakeRat.customJitter.set(false);
      fakeRat.customYaw.set(270.0);
      fakeRat.customPitch.set(-30.0);
      rotation = fakeRat.sample(1_000_000_000L, 0, 0);
      check(rotation.applyYaw() && rotation.yaw() == -90.0F, "custom yaw wraps around the head axis");
      check(rotation.applyPitch() && rotation.pitch() == -30.0F, "custom pitch");
      fakeRat.pitchMode.set("Up");
      check(fakeRat.sample(1_100_000_000L, 0, 0).pitch() == -90.0F, "pitch up");
      fakeRat.pitchMode.set("Down");
      check(fakeRat.sample(1_200_000_000L, 0, 0).pitch() == 90.0F, "pitch down");
      fakeRat.yawMode.set("Default");
      fakeRat.pitchMode.set("Default");
      fakeRat.reset();
      check(!fakeRat.sample(2_000_000_000L, 0, 0).applyYaw(), "visual state resets to Default");
   }

   private static void jumpPeak() {
      AuraJumpPeak peak = new AuraJumpPeak();
      peak.observe(1, 0.42, false, 0.0F, true);
      peak.observe(2, 0.33, false, 0.0F, true);
      peak.observe(3, 0.24, false, 0.0F, true);
      peak.observe(4, 0.15, false, 0.0F, true);
      peak.observe(5, 0.06, false, 0.0F, true);
      check(peak.isJumpArcActive() && !peak.isAtPeak(), "ascending jump is not attack-ready");
      peak.observe(6, -0.03, false, 0.0F, true);
      check(peak.isJumpArcActive() && peak.isAtPeak(), "first descending sample with no fall distance is the apex window");
      peak.consumePeak();
      check(!peak.isAtPeak() && peak.isJumpArcActive(), "the apex can be consumed only once");
      peak.observe(6, -0.03, false, 0.0F, true);
      check(!peak.isAtPeak(), "duplicate update in the same player tick cannot reopen the apex");
      peak.observe(7, -0.12, false, 0.08F, true);
      check(!peak.isAtPeak() && peak.isJumpArcActive(), "landing approach is outside the apex window");
      peak.observe(8, 0.0, true, 0.0F, true);
      check(!peak.isAtPeak() && !peak.isJumpArcActive(), "ground contact ends the jump arc");

      AuraJumpPeak falling = new AuraJumpPeak();
      falling.observe(1, -0.08, false, 0.0F, true);
      falling.observe(2, -0.16, false, 0.08F, true);
      check(!falling.isJumpArcActive() && !falling.isAtPeak(), "walking off a ledge is not a jump peak");
   }

   public static void main(String[] args) {
      settings();
      profileModeMigration();
      burst();
      cameraJitter();
      fakeRatRotation();
      jumpPeak();
      System.out.println("Aura effects: click-mode migration, 1.8 bursts, camera jitter, FakeRat rendering values and 1.12 jump apex passed.");
   }
}
