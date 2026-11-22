package net.skycore.client.module.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/**
 * Headless smoke tests for the MotherHack Scaffold port: pure math plus a settings
 * contract check. No client, world or network access.
 */
public final class ScaffoldModuleTest {
   private static void check(boolean condition, String message) {
      if (!condition) throw new AssertionError(message);
   }

   private static void near(double actual, double expected, String message) {
      check(Math.abs(actual - expected) < 0.0001, message + ": " + actual);
   }

   public static void main(String[] args) {
      // Safe hit vector: one random point per face, always on the correct plane.
      BlockPos pos = new BlockPos(-3, 63, 5);
      for (Direction face : Direction.values()) {
         Vec3 point = ScaffoldModule.safeHitVec(pos, face, 0.5, 0.5, 0.5);
         Vec3 normal = Vec3.atLowerCornerOf(face.getUnitVec3i());
         Vec3 centre = Vec3.atCenterOf(pos).add(normal.scale(0.5));
         near(point.subtract(centre).dot(normal), 0, "sample on face plane");
         check(point.x >= pos.getX() && point.x <= pos.getX() + 1, "sample inside X bounds");
         check(point.y >= pos.getY() && point.y <= pos.getY() + 1, "sample inside Y bounds");
         check(point.z >= pos.getZ() && point.z <= pos.getZ() + 1, "sample inside Z bounds");
      }
      // Random components must be used as given, without clamping to the centre.
      Vec3 corner = ScaffoldModule.safeHitVec(pos, Direction.UP, 0.05, 0.0, 0.95);
      near(corner.x, pos.getX() + 0.05, "UP keeps random X");
      near(corner.y, pos.getY() + 1.0, "UP sits on top face");
      near(corner.z, pos.getZ() + 0.95, "UP keeps random Z");
      Vec3 north = ScaffoldModule.safeHitVec(pos, Direction.NORTH, 0.1, 0.2, 0.0);
      near(north.z, pos.getZ(), "NORTH keeps min Z");
      near(north.y, pos.getY() + 0.2, "NORTH keeps random Y");

      // Grim+Matrix pitch handling, both StaticPitch branches.
      float[] target = {12.0F, -40.0F};
      float[] fixed = ScaffoldModule.grimPitch(target, 0.0F, true, 80.0F, 67.0F);
      near(fixed[0], 12.0, "static pitch keeps yaw");
      near(fixed[1], 80.0, "static pitch forces FixedPitch");
      float[] limited = ScaffoldModule.grimPitch(target, -10.0F, false, 80.0F, 67.0F);
      near(limited[1], -40.0, "small pitch delta is not limited");
      float[] clampedDown = ScaffoldModule.grimPitch(new float[]{0.0F, -89.0F}, 0.0F, false, 80.0F, 67.0F);
      near(clampedDown[1], -67.0, "large pitch delta clamps to limit");
      float[] clampedUp = ScaffoldModule.grimPitch(new float[]{0.0F, 89.0F}, 0.0F, false, 80.0F, 67.0F);
      near(clampedUp[1], 67.0, "clamp is symmetric");
      float[] noLimit = ScaffoldModule.grimPitch(new float[]{0.0F, 89.0F}, 0.0F, false, 80.0F, 110.0F);
      near(noLimit[1], 89.0, "limit above 90 keeps the target pitch");

      // Settings contract: every MotherHack setting name survived the port.
      ScaffoldModule module = new ScaffoldModule();
      check(module.getCategory() == ModuleCategory.MOVEMENT, "movement category");
      check(!module.isEnabled(), "disabled by default");
      check(module.suffix().equals("NCPStrict"), "default mode is NCPStrict");
      String[] expected = {
         "Mode", "Switch", "Rotate", "LockY", "AutoJump", "WorkWhileSneaking", "Tower",
         "SafeWalk", "OnlyNotHoldingSpace", "NCPRotate", "SmoothSpeed", "PlaceDelay", "MinDelay", "MaxDelay",
         "LockYDelay", "StaticPitch", "FixedPitch", "GrimPitchLimit", "RandomizeDelay",
         "MinPlaceDelay", "MaxPlaceDelay", "BuildTrust", "TrustBlocks", "LastMomentPlace", "NoServerRotate",
         "KeepHeight", "Delay", "CatLean Switch", "CatLean Tower", "CatLean SafeWalk", "AllowShift",
         "Legit", "SaveAngles", "Telly", "AirPlace"
      };
      check(module.getSettings().size() == expected.length, "setting count matches the port: " + module.getSettings().size());
      for (String name : expected) {
         check(module.getSettings().stream().anyMatch(setting -> setting.getName().equals(name)), "missing setting " + name);
      }
      for (var setting : module.getSettings()) {
         if (setting instanceof NumberSetting number) {
            check(Double.isFinite(number.get()) && number.get() >= number.getMin()
               && number.get() <= number.getMax(), "valid default: " + setting.getName());
         } else if (setting instanceof ModeSetting mode) {
            check(mode.getModes().contains(mode.get()), "declared mode: " + setting.getName());
         } else {
            check(setting instanceof BooleanSetting, "known setting type: " + setting.getName());
         }
      }
      ModeSetting mode = (ModeSetting) module.getSettings().stream().filter(s -> s.getName().equals("Mode")).findFirst().orElseThrow();
      check(mode.getModes().equals(java.util.List.of("NCP", "NCPStrict", "Eagle", "CatLean")), "mode list matches the source");
      ModeSetting rotate = (ModeSetting) module.getSettings().stream().filter(s -> s.getName().equals("Rotate")).findFirst().orElseThrow();
      check(rotate.getModes().equals(java.util.List.of("Normal", "Smooth", "Packet", "Linear", "Snap", "GrimMatrix", "None")), "rotate list matches the source");
      ModeSetting catSwitch = (ModeSetting) module.getSettings().stream().filter(s -> s.getName().equals("CatLean Switch")).findFirst().orElseThrow();
      check(catSwitch.get().equals("Normal"), "CatLean switch default");
      System.out.println("ScaffoldModuleTest passed");
   }
}
