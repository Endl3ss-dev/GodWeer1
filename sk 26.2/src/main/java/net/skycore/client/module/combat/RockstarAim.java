package net.skycore.client.module.combat;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.combat.Rotation;

/** Rotation geometry only. Target selection, ray checks before hits and the click clock belong to Attack Aura. */
final class RockstarAim {
   // Adapted from Rockstar/src/main/java/moscow/rockstar/module/combat/Aura.java (rotateHead and mode registration).
   enum Mode {
      SIMPLE("Simple"), HOLY_WORLD("HolyWorld"), VON_TAM("VonTam"), FUN_TIME("FunTime"), SPOOKY_TIME("SpookyTime"),
      ONE_TICK("OneTick"), REALLY_WORLD("ReallyWorld"), SLIME_WORLD("SlimeWorld"), SLOTH("Sloth"), NONE("No Rotation");

      final String key;
      private static final Mode[] ALL = values();

      Mode(String name) { key = "Rockstar " + name; }

      static Mode from(String key) {
         for (Mode mode : ALL) if (mode.key.equals(key)) return mode;
         return null; // existing SkyCore rotation
      }

      static String[] alongside(String... legacy) {
         String[] keys = Arrays.copyOf(legacy, legacy.length + ALL.length);
         for (int i = 0; i < ALL.length; i++) keys[legacy.length + i] = ALL[i].key;
         return keys;
      }
   }

   record Turn(Rotation angles, float yawSpeed, float pitchSpeed, float returnSpeed) {}

   record SlimeScene(boolean blockedEye, boolean nearBlock, boolean onGround, boolean gliding, float viewPitch) {}

   record Geometry(Vec3 eyes, double feetY, Vec3 velocity, AABB box, Vec3 targetEyes, boolean living) {
      Vec3 nearest() {
         if (!living) return box.getCenter();
         Vec3 p = new Vec3(Mth.clamp(eyes.x, box.minX, box.maxX), Mth.clamp(eyes.y, box.minY, box.maxY), Mth.clamp(eyes.z, box.minZ, box.maxZ));
         return p.distanceToSqr(eyes) < 1e-10 ? box.getCenter() : p;
      }
      Rotation towards(Vec3 p, Rotation current) { return look(eyes, p, current); }
      Rotation led(int ticks, Rotation current) {
         Vec3 center = box.getCenter();
         double y = living ? Mth.clamp((feetY + nearest().y) * 0.5, box.minY, box.maxY) : center.y;
         return look(eyes.add(velocity.x * ticks, 0, velocity.z * ticks), new Vec3(center.x, y, center.z), current);
      }
      Rotation height(double fraction, Rotation current) {
         Vec3 center = box.getCenter();
         return towards(new Vec3(center.x, living ? box.minY + box.getYsize() * fraction : center.y, center.z), current);
      }
   }

   static Rotation look(Vec3 eye, Vec3 point, Rotation current) {
      Vec3 d = point.subtract(eye);
      double horizontal = Math.hypot(d.x, d.z);
      if (horizontal < 1e-6 && Math.abs(d.y) < 1e-6) return current;
      float yaw = horizontal < 1e-6 ? current.getYaw() : (float)Math.toDegrees(Math.atan2(d.z, d.x)) - 90;
      return new Rotation(yaw, (float)-Math.toDegrees(Math.atan2(d.y, horizontal)));
   }

   private final RandomGenerator random;
   final RockstarHolyWorld holy;
   private Mode mode;
   private Object subject;
   private int lastTick, samples;
   private long acquired, grounded;
   private float yawOffset, pitchOffset;
   private Rotation prepared;

   RockstarAim() { this(ThreadLocalRandom.current()); }
   RockstarAim(RandomGenerator random) { this.random = random; holy = new RockstarHolyWorld(random); }

   /** Tick modes step once per player tick. HolyWorld is integrated once per real render frame instead. */
   boolean begin(Mode selected, Object target, int tick, boolean renderFrame, long now) {
      if (target != subject || mode != selected) {
         reset();
         subject = target;
         mode = selected;
         acquired = grounded = now;
      }
      if (target == null || selected == null || selected == Mode.NONE || renderFrame != (selected == Mode.HOLY_WORLD)) return false;
      if (!renderFrame && samples != 0 && lastTick == tick) return false;
      lastTick = tick;
      samples++;
      return true;
   }

   void reset() {
      mode = null;
      subject = null;
      samples = 0;
      prepared = null;
      yawOffset = pitchOffset = 0;
      holy.reset();
   }

   private float between(float a, float b) { return random.nextFloat(a, b); }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/{OneTick,ReallyWorld}RotationMode.java.
   Turn point(Mode selected, Geometry g, Rotation current, Predicate<Rotation> stillHits) {
      Rotation nearest = g.towards(g.nearest(), current);
      return switch (selected) {
         case SIMPLE -> {
            float yaw = g.living && g.eyes.distanceToSqr(g.targetEyes) > 9 ? g.towards(g.targetEyes, current).getYaw() : nearest.getYaw();
            yield new Turn(new Rotation(yaw, nearest.getPitch()), 180, 180, 180);
         }
         case ONE_TICK -> {
            // Keep the prepared angle only while it still hits THIS target. Normal client movement sends the look.
            if (prepared == null || !stillHits.test(prepared)) prepared = nearest;
            yield new Turn(prepared, 180, 180, 180);
         }
         case REALLY_WORLD -> new Turn(nearest, 160, 160, 45); // donor's native-protocol path, not the old Via branch
         default -> throw new IllegalArgumentException("Not a point mode: " + selected);
      };
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/FunTimeRotationMode.java (also used by VonTam).
   Turn funTime(Geometry g, Rotation current, boolean ready, long now) {
      double ms = (now - acquired) / 1e6;
      float yawWave = triangle(ms, 470), pitchWave = triangle(ms, 300);
      Rotation base = ready ? g.led(1, current) : current;
      float scale = ready ? 0.25F : 1;
      return new Turn(new Rotation(base.getYaw() + scale * yawWave, Mth.clamp(base.getPitch() + scale * pitchWave, -90, 90)),
         ready ? between(30, 69) : between(20, 35), between(8, 15), 180);
   }

   private static float triangle(double ms, int halfPeriod) {
      return (float)(40 * Math.abs(ms % (2 * halfPeriod) / halfPeriod - 1) - 20);
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/SpookyTimeRotationMode.java.
   Turn spookyTime(Geometry g, Rotation current, boolean inRange, boolean falling) {
      Rotation aim = g.led(2, current);
      float yawSpeed = between(60, 80), pitchSpeed = between(40, 80);
      if (falling) pitchSpeed /= 12;
      if (!inRange) { yawSpeed /= between(5, 10); pitchSpeed /= between(5, 10); }
      return new Turn(new Rotation(aim.getYaw() + between(-5, 5), Mth.clamp(aim.getPitch() + between(-5, 5), -90, 90)), yawSpeed, pitchSpeed, 180);
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/SlothRotationMode.java.
   Turn sloth(Geometry g, Rotation current, boolean inRange, boolean ready) {
      Rotation aim = g.height(between(0.5F, 1), current);
      if (samples % 5 == 0) {
         boolean near = Math.abs(Mth.wrapDegrees(aim.getYaw() - current.getYaw())) < 8 && Math.abs(aim.getPitch() - current.getPitch()) < 6;
         float scale = near ? 0.35F : 1, blend = samples > 20 ? 0.35F : 0.18F;
         yawOffset = Mth.lerp(blend, yawOffset, scale * (inRange ? between(-2.5F, 2.5F) : between(-8, 8)));
         pitchOffset = Mth.lerp(blend, pitchOffset, scale * (inRange ? between(-1.5F, 1.5F) : between(-4, 4)));
      }
      return new Turn(new Rotation(aim.getYaw() + yawOffset, Mth.clamp(aim.getPitch() + pitchOffset, -85, 85)),
         ready ? between(18, 36) : between(7, 16), ready ? between(10, 24) : between(4, 12), between(15, 45));
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/rotations/modes/SlimeWorldRotationMode.java.
   Turn slimeWorld(Geometry g, Rotation current, boolean inRange, SlimeScene scene, long now) {
      Rotation aim = g.height(0.5, current);
      float dy = Mth.wrapDegrees(aim.getYaw() + yawOffset - current.getYaw());
      float dp = aim.getPitch() + pitchOffset - current.getPitch();
      double ms = (now - acquired) / 1e6;
      boolean blocked = inRange && scene.blockedEye;
      float nextY = blocked ? random.nextFloat() : (float)Math.ceil(between(10, 15) * Math.cos(ms / 65));
      float nextP = blocked ? random.nextFloat() : (float)Math.ceil(between(5, 15) * Math.sin(ms / 45));
      if (scene.onGround) grounded = now;
      if (scene.gliding && now - grounded >= 500_000_000L) {
         // Sample the signed amplitude even when the sine makes the second endpoint smaller than -1.
         float amplitude = (float)(6 + Math.sin(lastTick % 69 / 5.0 * 1488) * 35);
         nextP = (float)(57 + (g.feetY - g.box.minY + 1) * (-1 + random.nextFloat() * amplitude));
      }
      double distance = new Vec3(g.eyes.x, g.feetY, g.eyes.z).distanceTo(g.box.getCenter());
      float yBlend = inRange ? (float)Math.min(between(0.14F, 0.88F), Math.abs(nextY - yawOffset) * distance) : between(0.52F, 0.69F);
      float pBlend = inRange ? (float)Math.min(between(0.14F, 0.88F), Math.abs(nextP - pitchOffset) * distance) : between(0.1F, 0.5F);
      yawOffset = Mth.lerp(yBlend, yawOffset, nextY);
      pitchOffset = Mth.lerp(pBlend, pitchOffset, nextP);
      float yawGain = blocked ? between(1.95F, 2.85F) : between(0.7F, 1.55F);
      float pitchGain = !inRange ? between(0.25F, 0.35F) : blocked ? between(0.45F, 0.76F) : between(0.35F, 0.55F);
      float pitch = current.getPitch() + pitchGain * (dp + between(0, 20) + Mth.clamp(scene.viewPitch, -84, 84));
      return new Turn(new Rotation(current.getYaw() + yawGain * dy, Math.min(Mth.clamp(pitch, -85, 85) + (scene.nearBlock ? 4 : 0), 85)), 160, 160, between(5, 50));
   }
}
