package net.skycore.client.module.combat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.combat.Rotation;
import net.skycore.client.config.Translations;
import net.skycore.client.event.PlayerInputEvent;
import static net.skycore.client.module.combat.RockstarAim.Mode.*;

/** Reproducible math, cadence and input regressions; no world, sleeps, packets or game window. */
public final class RockstarAuraTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }
   private static void close(double actual, double expected, String message) {
      check(Math.abs(actual - expected) < 0.0003, message + ": " + actual + " != " + expected);
   }
   private static void range(float actual, float lo, float hi) { check(actual >= lo && actual <= hi, "out of range: " + actual); }
   private static RockstarAim aim() { return new RockstarAim(new Random(262)); }
   private static final Vec3 EYES = new Vec3(0, 1.6, 0);
   private static final Rotation VIEW = new Rotation(12, -6);
   private static final RockstarAim.Geometry LIVING = new RockstarAim.Geometry(EYES, 0, new Vec3(0.3, 0.2, 0.1),
      new AABB(1, 0, 4, 2, 2, 5), new Vec3(1.5, 1.6, 4.5), true);
   private static final RockstarAim.Geometry PROJECTILE = new RockstarAim.Geometry(EYES, 0, Vec3.ZERO,
      new AABB(-2, 0, 0, -1.8, 0.2, 0.2), new Vec3(-1.9, 0.1, 0.1), false);

   private static void modesAndCadence() {
      var keys = Arrays.asList(alongside("ReallyWorld", "HvH", "Funtime", "HolyWorldLegit"));
      check(keys.subList(0, 4).equals(List.of("ReallyWorld", "HvH", "Funtime", "HolyWorldLegit")), "legacy keys/order");
      check(keys.size() == 14 && new HashSet<>(keys).size() == 14, "distinct UI choices");
      for (var legacy : keys.subList(0, 4)) check(from(legacy) == null, "native dispatch");
      for (var mode : values()) {
         check(from(mode.key) == mode && keys.contains(mode.key), "saved key");
         check(Translations.english(Translations.russian(mode.key)).equals(mode.key), "label roundtrip");
      }
      check(Translations.russian("Silent").equals("Скрытая") && Translations.russian("Direct").equals("Прямая"), "V2 labels");
      var a = aim();
      check(!a.begin(NONE, LIVING, 1, false, 0) && !a.begin(SLOTH, null, 1, false, 0), "no rotation/target");
      check(a.begin(SLOTH, LIVING, 1, false, 0), "first tick");
      for (int i = 1; i <= 65; i++) check(!a.begin(SLOTH, LIVING, 1, false, i), "no duplicated tick");
      check(!a.begin(SLOTH, LIVING, 1, true, 99), "tick modes never advance on render");
      check(a.begin(SLOTH, LIVING, 2, false, 100), "next tick");
      check(a.begin(SLOTH, PROJECTILE, 2, false, 110), "new target");
      check(a.begin(FUN_TIME, PROJECTILE, 2, false, 111), "new mode");
      a.reset();
      check(a.begin(FUN_TIME, PROJECTILE, 2, false, 112), "reset without waiting for tick");
      check(!a.begin(HOLY_WORLD, LIVING, 4, false, 120), "no frame step from physics");
      check(a.begin(HOLY_WORLD, LIVING, 4, true, 121) && a.begin(HOLY_WORLD, LIVING, 4, true, 122), "separate frames within one tick");
   }

   private static void rotations() {
      var a = aim();
      a.begin(SIMPLE, LIVING, 0, false, 0);
      var simple = a.point(SIMPLE, LIVING, VIEW, r -> false);
      close(simple.angles().getYaw(), LIVING.towards(LIVING.targetEyes(), VIEW).getYaw(), "simple far yaw");
      close(simple.angles().getPitch(), LIVING.towards(LIVING.nearest(), VIEW).getPitch(), "nearest pitch");
      close(simple.yawSpeed(), 180, "simple speed");
      var rw = a.point(REALLY_WORLD, LIVING, VIEW, r -> false);
      close(rw.angles().getYaw(), LIVING.towards(LIVING.nearest(), VIEW).getYaw(), "ReallyWorld nearest yaw");
      close(rw.yawSpeed(), 160, "RW speed"); close(rw.returnSpeed(), 45, "RW return");
      a.begin(ONE_TICK, LIVING, 1, false, 0);
      var held = a.point(ONE_TICK, LIVING, VIEW, r -> false).angles();
      check(a.point(ONE_TICK, PROJECTILE, VIEW, r -> true).angles() == held, "valid prepared angle");
      check(a.point(ONE_TICK, PROJECTILE, VIEW, r -> false).angles().getDelta(held) > 10, "miss recalculates");
      a.begin(ONE_TICK, PROJECTILE, 1, false, 0);
      var fresh = a.point(ONE_TICK, PROJECTILE, VIEW, r -> { throw new AssertionError("old target ray"); });
      close(fresh.angles().getDelta(PROJECTILE.towards(PROJECTILE.box().getCenter(), VIEW)), 0, "projectile center");
      a.begin(FUN_TIME, LIVING, 1, false, 0);
      var ready = a.funTime(LIVING, VIEW, true, 0);
      close(ready.angles().getYaw(), LIVING.led(1, VIEW).getYaw() + 5, "FunTime one-tick lead & narrow wave");
      range(ready.yawSpeed(), 30, 69); range(ready.pitchSpeed(), 8, 15);
      var idle = a.funTime(LIVING, VIEW, false, 470_000_000L);
      close(idle.angles().getYaw(), VIEW.getYaw() - 20, "full yaw wave"); range(idle.yawSpeed(), 20, 35);
      close(a.funTime(LIVING, VIEW, false, 300_000_000L).angles().getPitch(), VIEW.getPitch() - 20, "pitch period");
      var spooky = a.spookyTime(LIVING, VIEW, true, true);
      range(spooky.angles().getYaw() - LIVING.led(2, VIEW).getYaw(), -5, 5);
      range(spooky.yawSpeed(), 60, 80); range(spooky.pitchSpeed(), 40f/12, 80f/12);
      range(a.spookyTime(PROJECTILE, VIEW, false, false).yawSpeed(), 6, 16);
      for (int i = 0; i < 70; i++) {
         boolean attack = i % 2 == 0;
         a.begin(SLOTH, LIVING, i, false, i * 50_000_000L);
         var sloth = a.sloth(LIVING, VIEW, true, attack);
         range(sloth.yawSpeed(), attack ? 18 : 7, attack ? 36 : 16); range(sloth.angles().getPitch(), -85, 85);
      }
      var b = aim(); a = aim();
      for (int i = 0; i < 70; i++) {
         long t = i * 50_000_000L;
         var g = i < 35 ? LIVING : PROJECTILE;
         a.begin(SLIME_WORLD, g, i, false, t); b.begin(SLIME_WORLD, g, i, false, t);
         var plain = a.slimeWorld(g, VIEW, i % 3 == 0, new RockstarAim.SlimeScene(i % 2 == 0, false, i == 0, g.living(), -30), t);
         var block = b.slimeWorld(g, VIEW, i % 3 == 0, new RockstarAim.SlimeScene(i % 2 == 0, true, i == 0, g.living(), -30), t);
         range(plain.angles().getPitch(), -85, 85); close(block.angles().getPitch(), Math.min(85, plain.angles().getPitch() + 4), "Slime near-block pitch");
      }
   }

   private static void frames() {
      var a = aim().holy; var b = aim().holy;
      Rotation view = new Rotation(179, 0);
      Vec3 center = EYES.add(-0.05, 0, -4);
      var first = a.frame(0, EYES, center, view, 0.15F);
      check(first.getDelta(view) < 2, "short arc at yaw seam");
      check(a.frame(0, EYES, center, first, 0.15F) == first, "duplicate timestamp");
      b.frame(0, EYES, center, view, 0.15F);
      var afterStall = a.frame(10_000_000_000L, EYES, LIVING.box().getCenter(), first, 0.15F);
      close(afterStall.getDelta(b.frame(100_000_000L, EYES, LIVING.box().getCenter(), first, 0.15F)), 0, "100ms dt cap");
      a = aim().holy; b = aim().holy;
      Vec3 west = EYES.add(-4, 0, 0);
      Rotation start = a.frame(0, EYES, west, Rotation.ZERO, 0);
      b.frame(0, EYES, west, Rotation.ZERO, 0);
      Rotation opposed = new Rotation(start.getYaw() - 3, start.getPitch());
      float unopposedStep = a.frame(16_666_667L, EYES, west, start, 0).getYaw() - start.getYaw();
      float opposedStep = b.frame(16_666_667L, EYES, west, opposed, 0).getYaw() - opposed.getYaw();
      check(opposedStep > 0 && opposedStep < unopposedStep, "yield to opposing manual input");
      for (int fps : List.of(30, 60, 144, 240)) {
         var holy = aim().holy;
         view = new Rotation(90, 5);
         Rotation initial = view, desired = LIVING.towards(LIVING.box().getCenter(), view);
         for (int i = 0; i < fps; i++) {
            Rotation next = holy.frame(i * 1_000_000_000L / fps, EYES, LIVING.box().getCenter(), view, 0.15F);
            double steps = (next.getYaw() - view.getYaw()) / 0.15;
            close(steps, Math.rint(steps), "residual mouse steps");
            range(next.getPitch(), -90, 90); view = next;
         }
         check(desired.getDelta(view) < initial.getDelta(desired) / 10, "frame tracking at " + fps);
         holy.reset();
         check(holy.frame(0, EYES, EYES, VIEW, 0).getDelta(VIEW) == 0, "reset/zero length");
      }
   }

   private static void movement() {
      Input keys = new Input(true, false, false, false, true, true, true);
      var e = new PlayerInputEvent(null, keys, new Vec2(0, 1));
      RockstarMovement.apply(e, 0, 90, false);
      check(e.keyPresses() == keys && e.moveVector().y == 1, "Direct leaves movement and action flags alone");
      RockstarMovement.apply(e, 0, 90, true);
      check(e.keyPresses().left() && !e.keyPresses().forward() && e.moveVector().x == 1, "Silent W -> A");
      check(e.keyPresses().jump() && e.keyPresses().shift() && e.keyPresses().sprint(), "action flags preserved");
      var zero = new PlayerInputEvent(null, keys, Vec2.ZERO);
      RockstarMovement.apply(zero, 0, 90, true);
      check(zero.moveVector() == Vec2.ZERO && zero.keyPresses() == keys, "no synthetic movement from rest");
      for (int l = -1; l <= 1; l++) for (int f = -1; f <= 1; f++) {
         if (l == 0 && f == 0) continue;
         float s = (float)(0.72 / Math.hypot(l, f));
         Vec2 input = new Vec2(l*s, f*s);
         for (int angle = -365; angle <= 365; angle++) {
            float view = 179, task = view + angle;
            Vec2 out = RockstarMovement.correct(input, view, task, true);
            close(Math.hypot(out.x, out.y), 0.72, "magnitude");
            double want = view + Math.toDegrees(Math.atan2(-l, f));
            double got = task + Math.toDegrees(Math.atan2(-out.x, out.y));
            check(Math.abs(Math.IEEEremainder(want - got, 360)) <= 22.50001, "nearest of eight across yaw seam");
         }
      }
   }

   public static void main(String[] args) {
      modesAndCadence(); rotations(); frames(); movement();
      System.out.println("Rockstar: modes, aim geometry, tick/frame cadence, mouse steps and V2 input passed");
   }
}
