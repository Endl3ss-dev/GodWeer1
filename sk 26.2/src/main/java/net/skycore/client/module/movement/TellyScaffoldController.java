package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.util.ResetSpeedHelper;
import org.lwjgl.glfw.GLFW;

/** Experimental SP-only state controller, not a learned policy or a replay.
 * Dataset candidates: 27 contiguous flat 3+ block episodes; median flight 12 ticks,
 * placement offsets 4/7/10. Those offsets are lower bounds, never permission to
 * click without a current reachable face. It does not borrow Coach's slow clock.
 */
final class TellyScaffoldController {
   private enum Phase { RUN, TAKEOFF, BUILD, WAIT, RETURN, HALT }
   private Phase phase = Phase.RUN;
   private Object world;
   private Object player;
   private Direction heading;
   private BlockPos next;
   private BlockPos pending;
   private float forwardYaw;
   private float forwardPitch;
   private double floorY;
   private int airTicks;
   private int launchTicks;
   private int blocks;
   private int waitTicks;
   private int cycleTicks;
   private boolean fresh;
   private boolean airborne;
   private Vec3 lastPosition;

   void interrupt() { this.phase = Phase.HALT; this.fresh = false; }

   void reset() {
      this.phase = Phase.RUN;
      this.world = this.player = null;
      this.heading = null;
      this.lastPosition = null;
      this.next = this.pending = null;
      this.fresh = this.airborne = false;
      this.airTicks = this.blocks = this.waitTicks = this.launchTicks = this.cycleTicks = 0;
   }

   private boolean environment(Minecraft mc, ClientModule owner) {
      if (mc.player == null || mc.level == null || mc.gameMode == null || mc.getSingleplayerServer() == null
         || mc.isPaused() || mc.level.tickRateManager().isFrozen()
         || !Float.isFinite(mc.level.tickRateManager().tickrate())
         || Math.abs(mc.level.tickRateManager().tickrate() - 20) > 0.1
         || !Float.isFinite(ResetSpeedHelper.getSpeed()) || Math.abs(ResetSpeedHelper.getSpeed() - 1) > 0.001
         || !Float.isFinite(mc.player.getYRot()) || !Float.isFinite(mc.player.getXRot())
         || !Double.isFinite(mc.player.getX()) || !Double.isFinite(mc.player.getY()) || !Double.isFinite(mc.player.getZ())
         || mc.player.getAbilities().flying || (!mc.player.onGround() && this.heading == null)
         || GLFW.glfwGetWindowAttrib(mc.getWindow().handle(), GLFW.GLFW_FOCUSED) != GLFW.GLFW_TRUE) return false;
      for (ClientModule module : SkycoreClient.modules().enabled()) {
         if (module != owner && module.affectsGameplay()) return false;
      }
      return true;
   }

   void input(Minecraft mc, PlayerInputEvent event, ClientModule owner) {
      this.fresh = false;
      if (!this.environment(mc, owner) || this.world != null && (this.world != mc.level || this.player != mc.player)) {
         // Interruptions require toggling Scaffold off/on, not a stale automatic resume.
         this.phase = Phase.HALT;
         return;
      }
      if (this.lastPosition != null && this.lastPosition.distanceToSqr(mc.player.position()) > 2.25) this.phase = Phase.HALT;
      this.lastPosition = mc.player.position();
      if (this.phase == Phase.HALT) {
         event.clearMovement(false, true);
         return;
      }
      if (this.heading == null) {
         if (!mc.player.onGround() || !event.keyPresses().forward()) return;
         this.heading = cardinal(mc.player.getYRot());
         BlockPos support = BlockPos.containing(mc.player.getX(), mc.player.getY() - 0.5, mc.player.getZ());
         if (!ScaffoldModule.validSupport(mc, support)) return;
         this.world = mc.level;
         this.player = mc.player;
         this.floorY = mc.player.getY();
         this.forwardYaw = switch (this.heading) {
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH -> 180;
            default -> -90;
         };
         this.forwardPitch = mc.player.getXRot();
         this.next = support.relative(this.heading);
      }
      if (this.next == null) { this.phase = Phase.HALT; return; }
      // This mode requires deliberate held forward input. Releasing it aborts.
      if (!event.keyPresses().forward() || event.keyPresses().backward() || event.keyPresses().left()
         || event.keyPresses().right() || event.keyPresses().shift()) {
         this.phase = Phase.HALT;
         return;
      }
      this.fresh = true;
      if (this.phase == Phase.RUN) {
         if (!mc.player.onGround() || Math.abs(mc.player.getY() - this.floorY) > 0.15) {
            this.phase = Phase.HALT;
            event.clearMovement(false, true);
            return;
         }
         // Skip existing full row cells, but never search indefinitely or change Y.
         for (int i = 0; i < 8 && ScaffoldModule.validSupport(mc, this.next); i++) this.next = this.next.relative(this.heading);
         BlockPos probe = BlockPos.containing(mc.player.getX() + this.heading.getStepX() * 0.85,
            this.next.getY(), mc.player.getZ() + this.heading.getStepZ() * 0.85);
         if (!ScaffoldModule.validSupport(mc, probe)) {
            if (this.slot(mc) < 0) {
               this.phase = Phase.HALT;
               event.clearMovement(false, true);
               return;
            }
            this.phase = Phase.TAKEOFF;
            this.airTicks = this.blocks = this.launchTicks = this.cycleTicks = 0;
            this.airborne = false;
         }
      }
      if (!mc.player.onGround()) {
         this.airborne = true;
         this.airTicks++;
         if (this.phase == Phase.TAKEOFF) this.phase = Phase.BUILD;
      } else if (this.airborne) {
         this.phase = Phase.RETURN;
         this.airborne = false;
      }
      if (++this.cycleTicks > 100 && this.phase != Phase.RUN
         || mc.player.getY() < this.floorY - 0.15 || this.airTicks > 25) {
         this.phase = Phase.HALT;
      }
      if (this.phase == Phase.TAKEOFF && ++this.launchTicks > 4) this.phase = Phase.HALT;
      if (this.phase == Phase.HALT || this.phase == Phase.RETURN && mc.player.onGround()) {
         event.clearMovement(false, true);
         mc.player.setSprinting(false);
         return;
      }
      // Preserve cardinal world movement during visible backward turns.
      Vec3 worldMove = new Vec3(this.heading.getStepX(), 0, this.heading.getStepZ());
      Vec2 local = ScaffoldModule.localInput(worldMove, mc.player.getYRot());
      boolean jump = this.phase == Phase.TAKEOFF && mc.player.onGround();
      boolean sprint = this.phase == Phase.RUN || jump;
      event.setMoveVector(local);
      event.setKeyPresses(new Input(local.y > 0.01F, local.y < -0.01F,
         local.x > 0.01F, local.x < -0.01F, jump, false, sprint));
      mc.player.setSprinting(sprint);
   }

   void tick(Minecraft mc, ClientModule owner) {
      if (!this.fresh) return;
      this.fresh = false;
      if (!this.environment(mc, owner) || this.world != mc.level || this.player != mc.player) {
         this.phase = Phase.HALT;
         return;
      }
      if (this.phase == Phase.HALT || this.next == null) return;
      if (this.phase == Phase.RUN || this.phase == Phase.RETURN) {
         if (this.rotate(mc, this.forwardYaw, this.forwardPitch, 60) && this.phase == Phase.RETURN && mc.player.onGround()) {
            this.phase = Phase.RUN;
            this.airTicks = this.cycleTicks = 0;
         }
         return;
      }
      if (this.phase == Phase.WAIT) {
         if (ScaffoldModule.validSupport(mc, this.pending)) {
            this.blocks++;
            this.next = this.pending.relative(this.heading);
            this.pending = null;
            this.phase = this.blocks >= 3 ? Phase.RETURN : Phase.BUILD;
         } else if (++this.waitTicks > 3) this.phase = Phase.HALT;
         return;
      }
      if (this.phase != Phase.BUILD || mc.player.onGround()) return;
      BlockHitResult target = ScaffoldModule.findHit(mc, this.next, 4.5, 5);
      if (target == null) return;
      Rotation look = Rotation.lookingAt(target.getLocation(), mc.player.getEyePosition());
      BlockHitResult actual = ScaffoldModule.viewHit(mc, 4.5);
      if (!ScaffoldModule.sameFace(actual, target)) {
         this.rotate(mc, look.getYaw(), look.getPitch(), 90);
         // The ordinary movement tick must transmit this actual visible view first.
         return;
      }
      if (this.airTicks < placementTick(this.blocks) || !ScaffoldModule.validTarget(mc, this.next)) return;
      int slot = this.slot(mc);
      // Do not auto-switch: selected block must remain available through native dispatch.
      if (slot < 0) { this.phase = Phase.HALT; return; }
      if (mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, actual).consumesAction()) {
         mc.player.swing(InteractionHand.MAIN_HAND);
         this.pending = this.next;
         this.waitTicks = 0;
         this.phase = Phase.WAIT;
      }
   }

   private int slot(Minecraft mc) {
      if (!(mc.player.getMainHandItem().getItem() instanceof BlockItem item)
         || mc.player.getMainHandItem().isEmpty() || item.getBlock() instanceof FallingBlock
         || item.getBlock() instanceof EntityBlock
         || !item.getBlock().defaultBlockState().isCollisionShapeFullBlock(mc.level, this.next)) return -1;
      return mc.player.getInventory().getSelectedSlot();
   }

   private boolean rotate(Minecraft mc, float yaw, float pitch, float limit) {
      mc.player.setYRot(ScaffoldModule.stepAngle(mc.player.getYRot(), yaw, limit, 0.8F));
      mc.player.setXRot(Mth.clamp(mc.player.getXRot() + ScaffoldModule.smoothStep(pitch - mc.player.getXRot(), limit, 0.8F), -90, 90));
      return Math.abs(Mth.wrapDegrees(yaw - mc.player.getYRot())) < 1 && Math.abs(pitch - mc.player.getXRot()) < 1;
   }

   static int placementTick(int placed) { return 4 + Math.min(2, Math.max(0, placed)) * 3; }

   static Direction cardinal(float yaw) {
      return switch (Math.floorMod(Math.round(yaw / 90), 4)) {
         case 0 -> Direction.SOUTH;
         case 1 -> Direction.WEST;
         case 2 -> Direction.NORTH;
         default -> Direction.EAST;
      };
   }
}
