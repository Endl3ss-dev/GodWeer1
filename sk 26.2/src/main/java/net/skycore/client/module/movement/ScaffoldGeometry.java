package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.combat.Rotation;

/**
 * Shared raycast/geometry helpers for the singleplayer bridge rehearsal: used only by
 * {@link ScaffoldCoachModule}. The Scaffold module itself is a MotherHack port and
 * does not use these helpers.
 */
final class ScaffoldGeometry {
   private ScaffoldGeometry() {
   }

   static float stepAngle(float current, float target, float limit, float smoothing) {
      return current + smoothStep(Mth.wrapDegrees(target - current), limit, smoothing);
   }

   static float smoothStep(float delta, float limit, float smoothing) {
      float step = Math.min(Math.abs(delta), Math.min(limit, Math.max(0.15F, Math.abs(delta) * smoothing)));
      return Math.copySign(step, delta);
   }

   static double angularCost(float yaw, float pitch, Rotation rotation) {
      // Compare actual view rays: yaw degrees alone overestimate turns near straight down.
      double dot = Vec3.directionFromRotation(pitch, yaw).dot(rotation.toVector());
      return Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
   }

   static boolean validTarget(Minecraft mc, BlockPos target) {
      return target.getY() >= mc.level.getMinY() && mc.level.getWorldBorder().isWithinBounds(target)
         && mc.level.getBlockState(target).canBeReplaced()
         && !new AABB(target).intersects(mc.player.getBoundingBox());
   }

   static boolean validSupport(Minecraft mc, BlockPos support) {
      BlockState state = mc.level.getBlockState(support);
      return !state.canBeReplaced() && state.getFluidState().isEmpty()
         && !(state.getBlock() instanceof EntityBlock) && !state.is(Blocks.CRAFTING_TABLE)
         && !state.is(Blocks.RESPAWN_ANCHOR) && !state.is(Blocks.NOTE_BLOCK)
         && state.isCollisionShapeFullBlock(mc.level, support);
   }

   // Shared geometry only: the singleplayer coach does not run the Scaffold state machine.
   static BlockHitResult findHit(Minecraft mc, BlockPos target, double requestedReach, int samples) {
      if (!validTarget(mc, target)) return null;
      Vec3 eye = mc.player.getEyePosition();
      double range = Math.min(requestedReach, mc.player.blockInteractionRange());
      BlockHitResult current = viewHit(mc, requestedReach);
      if (current.getType() == HitResult.Type.BLOCK && !current.isInside()
         && current.getBlockPos().relative(current.getDirection()).equals(target)
         && validSupport(mc, current.getBlockPos())) {
         return current;
      }
      BlockHitResult best = null;
      double bestCost = Double.POSITIVE_INFINITY;
      for (Direction face : Direction.values()) {
         BlockPos support = target.relative(face.getOpposite());
         if (!validSupport(mc, support)) continue;
         Vec3 normal = Vec3.atLowerCornerOf(face.getUnitVec3i());
         Vec3 centre = Vec3.atCenterOf(support).add(normal.scale(0.5));
         // Back-facing faces cannot be clicked. Avoid an entire grid of useless raycasts.
         if (eye.subtract(centre).dot(normal) <= 0.0001) continue;
         for (int i = 0; i < samples; i++) {
            for (int j = 0; j < samples; j++) {
               double u = 0.08 + 0.84 * i / (samples - 1.0);
               double v = 0.08 + 0.84 * j / (samples - 1.0);
               Vec3 point = facePoint(support, face, u, v);
               if (eye.distanceToSqr(point) > range * range) continue;
               Rotation rotation = Rotation.lookingAt(point, eye);
               double cost = angularCost(mc.player.getYRot(), mc.player.getXRot(), rotation);
               if (cost >= bestCost) continue;
               BlockHitResult expected = new BlockHitResult(point, face, support, false);
               BlockHitResult visible = mc.level.clip(new ClipContext(eye,
                  point.add(point.subtract(eye).normalize().scale(0.01)),
                  ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
               if (sameFace(visible, expected)) {
                  bestCost = cost;
                  best = expected;
               }
            }
         }
      }
      return best;
   }

   static Vec3 facePoint(BlockPos support, Direction face, double u, double v) {
      return Vec3.atLowerCornerOf(support).add(switch (face) {
         case DOWN -> new Vec3(u, 0, v);
         case UP -> new Vec3(u, 1, v);
         case NORTH -> new Vec3(u, v, 0);
         case SOUTH -> new Vec3(u, v, 1);
         case WEST -> new Vec3(0, v, u);
         case EAST -> new Vec3(1, v, u);
      });
   }

   static BlockHitResult viewHit(Minecraft mc, double requestedReach) {
      Vec3 eye = mc.player.getEyePosition();
      double range = Math.min(requestedReach, mc.player.blockInteractionRange());
      return mc.level.clip(new ClipContext(eye, eye.add(mc.player.getViewVector(1.0F).scale(range)),
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
   }

   static boolean sameFace(BlockHitResult actual, BlockHitResult expected) {
      return actual.getType() == HitResult.Type.BLOCK && actual.getBlockPos().equals(expected.getBlockPos())
         && actual.getDirection() == expected.getDirection() && !actual.isInside();
   }
}
