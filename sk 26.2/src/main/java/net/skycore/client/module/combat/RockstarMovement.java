package net.skycore.client.module.combat;

import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.skycore.client.event.PlayerInputEvent;

final class RockstarMovement {
   private RockstarMovement() {}

   static void apply(PlayerInputEvent event, float viewYaw, float taskYaw, boolean silent) {
      Vec2 input = event.moveVector();
      Vec2 output = correct(input, viewYaw, taskYaw, silent);
      if (input == output) return; // DIRECT or no input: preserve the original Input too
      Input keys = event.keyPresses();
      event.setMoveVector(output);
      event.setKeyPresses(new Input(output.y > 0, output.y < 0, output.x > 0, output.x < 0, keys.jump(), keys.shift(), keys.sprint()));
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/systems/event/impl/player/InputEvent.java#setYaw
   // and Rockstar/src/main/java/moscow/rockstar/util/rotations/RotationUpdateListener.java.
   // Both donor modes use the task yaw for physics. Only SILENT remaps the keys relative to the free view.
   static Vec2 correct(Vec2 input, float viewYaw, float taskYaw, boolean silent) {
      if (!silent || viewYaw == taskYaw || input.x == 0 && input.y == 0) return input;
      // W = +forward (y), A = +left (x). Enumerate the same eight keyboard directions;
      // compare circular distances so +179 and -179 aren't treated as opposite headings.
      double desired = viewYaw - taskYaw + Math.toDegrees(Math.atan2(-input.x, input.y));
      int left = 0, forward = 0;
      double best = Double.POSITIVE_INFINITY;
      for (int f = -1; f <= 1; f++) {
         for (int l = -1; l <= 1; l++) {
            if (f == 0 && l == 0) continue;
            double error = Math.abs(Math.IEEEremainder(desired - Math.toDegrees(Math.atan2(-l, f)), 360));
            if (error < best) { best = error; left = l; forward = f; }
         }
      }
      // 26.2 KeyboardInput uses a normalized vector, not 1.21.4's two digital axes.
      float scale = (float)(Math.min(1, Math.hypot(input.x, input.y)) / Math.hypot(left, forward));
      return new Vec2(left * scale, forward * scale);
   }
}
