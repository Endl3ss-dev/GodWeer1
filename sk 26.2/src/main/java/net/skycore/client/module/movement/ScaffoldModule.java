package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.ServerRotationManager;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.InventoryHelper;

/** Visible, edge-triggered bridge building. Never sends synthetic rotation packets. */
public final class ScaffoldModule extends ClientModule {
   private final BooleanSetting autoSwitch = this.add(new BooleanSetting("Auto Switch", "Temporarily select building blocks from the hotbar", true));
   private final BooleanSetting swing = this.add(new BooleanSetting("Swing", "Swing after an accepted placement interaction", true));
   private final BooleanSetting returnView = this.add(new BooleanSetting("Return View", "Return to the approach view before releasing movement", true));
   private final BooleanSetting requireMove = this.add(new BooleanSetting("Require Movement", "Only start a bridge cycle while moving", true));
   private final NumberSetting edgeDistance = number("Edge Distance", "Look ahead for missing ground", 0.22, 0.05, 0.4, 0.01, " blocks");
   private final NumberSetting yawSpeed = number("Turn Speed", "Maximum visible yaw change per tick", 30, 2, 60, 1, " deg/tick");
   private final NumberSetting pitchSpeed = number("Pitch Speed", "Maximum visible pitch change per tick", 24, 2, 45, 1, " deg/tick");
   private final NumberSetting returnSpeed = number("Return Speed", "Maximum view return change per tick", 40, 2, 60, 1, " deg/tick");
   private final NumberSetting sneakDelay = number("Sneak Delay", "Minimum crouch ticks before placement", 1, 1, 10, 1, " ticks");
   private final NumberSetting placeDelay = number("Place Delay", "Aligned ticks before placement", 0, 0, 10, 1, " ticks");
   private final NumberSetting releaseDelay = number("Release Delay", "Extra crouch ticks after observing a placed block", 0, 0, 10, 1, " ticks");
   private final NumberSetting retryDelay = number("Retry Delay", "Ticks before retrying a failed cycle", 5, 1, 20, 1, " ticks");
   private final NumberSetting timeout = number("Cycle Timeout", "Maximum aiming or placement-wait ticks", 60, 20, 120, 1, " ticks");
   private final NumberSetting reach = number("Place Reach", "Maximum distance to the clicked face", 4.0, 2, 4.5, 0.1, " blocks");

   private final NumberSetting aimSamples = number("Aim Samples", "Points per axis on each visible support face", 5, 3, 7, 1, " points");
   private final NumberSetting smoothing = number("Turn Smoothing", "Slow down near the desired angle; 1 is constant speed", 0.5, 0.15, 1, 0.05, "");
   private final BooleanSetting jumpAssist = this.add(new BooleanSetting("Jump Assist", "Place at bridge height during a manual jump; no auto jump or tower", false));
   private final NumberSetting jumpTurnSpeed = number("Jump Turn Speed", "Maximum yaw/pitch step while jumping", 60, 10, 120, 1, " deg/tick");
   private final NumberSetting jumpLookAhead = number("Jump Look Ahead", "Horizontal motion prediction for an airborne placement", 1, 0, 2, 0.25, " ticks");

   private enum Phase { WALK, AIM, WAIT, RETURN }
   private Phase phase = Phase.WALK;
   private Subscription inputSubscription;
   private Object world;
   private Object player;
   private Vec3 cycleOrigin;
   private Placement placement;
   private float approachYaw;
   private float approachPitch;
   private int phaseTicks;
   private int crouchTicks;
   private int alignedTicks;
   private int placedTicks;
   private int retryTicks;
   private boolean edge;
   private boolean inputReady;
   private boolean jumpArmed;
   private boolean jumpCycle;
   private boolean leftGround;
   private int jumpTicks;
   private int bridgeY;
   private Vec3 takeoff;
   private float jumpHeading;
   private float jumpPitch;
   private boolean jumpViewPending;

   public ScaffoldModule() {
      super("Scaffold", "Crouches at edges, visibly aims, places a bridge block and returns the view", ModuleCategory.MOVEMENT);
      this.returnSpeed.visibleWhen(() -> this.returnView.get());
      this.jumpTurnSpeed.visibleWhen(() -> this.jumpAssist.get());
      this.jumpLookAhead.visibleWhen(() -> this.jumpAssist.get());
   }

   private NumberSetting number(String name, String description, double value, double min, double max, double step, String suffix) {
      return this.add(new NumberSetting(name, description, value, min, max, step, suffix));
   }

   @Override
   protected void onEnable() {
      this.reset();
      if (this.inputSubscription != null) this.inputSubscription.close();
      this.inputSubscription = SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
   }

   @Override
   protected void onDisable() {
      if (this.inputSubscription != null) {
         this.inputSubscription.close();
         this.inputSubscription = null;
      }
      // No KeyMapping state was changed: physical sneak resumes on the next input tick.
      // Do not snap the camera back on disable, world change or GUI interruption.
      this.reset();
   }

   private void reset() {
      this.phase = Phase.WALK;
      this.placement = null;
      this.world = null;
      this.player = null;
      this.cycleOrigin = null;
      this.edge = false;
      this.inputReady = false;
      this.jumpArmed = this.jumpCycle = this.leftGround = this.jumpViewPending = false;
      this.jumpTicks = 0;
      this.takeoff = null;
      this.phaseTicks = this.crouchTicks = this.alignedTicks = this.placedTicks = this.retryTicks = 0;
   }

   private boolean available(Minecraft mc) {
      return mc.player != null && mc.level != null && mc.gameMode != null && mc.gui.screen() == null
         && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isPassenger()
         && !mc.player.isFallFlying() && !mc.player.isInWater() && !mc.player.isInLava()
         && !mc.player.isUsingItem() && !mc.options.keyAttack.isDown() && !mc.options.keyUse.isDown()
         && !RotationManager.INSTANCE.isRotating() && !ServerRotationManager.INSTANCE.isRotating()
         && !RotationController.isActive() && mc.getCameraEntity() == mc.player;
   }

   private void onInput(PlayerInputEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (!this.isEnabled() || !this.available(mc)) {
         this.reset();
         return;
      }
      if (this.world != mc.level || this.player != mc.player) {
         this.reset();
         this.world = mc.level;
         this.player = mc.player;
      }
      if (mc.player.getAbilities().flying) {
         this.reset();
         return;
      }
      if (this.jumpAssist.get() && (this.jumpArmed
         || (mc.player.onGround() && event.keyPresses().jump() && this.phase != Phase.RETURN))) {
         if (this.onJumpInput(mc, event)) return;
      }
      if (this.cycleOrigin != null && this.phase != Phase.WALK
         && this.cycleOrigin.distanceToSqr(mc.player.position()) > 0.75 * 0.75) {
         this.reset();
         return;
      }
      // With jump assist, finish the grounded view return before a held jump
      // takes off again; otherwise W could launch backwards relative to the bridge.
      if (!mc.player.onGround() || (event.keyPresses().jump()
         && (!this.jumpAssist.get() || this.phase != Phase.RETURN))) {
         this.reset();
         return;
      }
      this.inputReady = true;
      if (this.phase == Phase.WALK) {
         Vec3 direction = inputDirection(event.moveVector().x, event.moveVector().y, mc.player.getYRot());
         boolean moving = direction.lengthSqr() > 0.0;
         if (!moving && this.requireMove.get()) {
            this.edge = false;
         } else {
            if (!moving) direction = mc.player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
            double speed = Math.hypot(mc.player.getDeltaMovement().x, mc.player.getDeltaMovement().z);
            double distance = Math.min(0.55, this.edgeDistance.get() + speed);
            Vec3 next = mc.player.position().add(direction.scale(distance));
            // A narrow collision probe tests ground under the projected centre, before the
            // whole player box has left the ledge. Vanilla crouch performs the actual edge clamp.
            AABB probe = new AABB(next.x - 0.025, next.y - 0.12, next.z - 0.025,
               next.x + 0.025, next.y - 0.01, next.z + 0.025);
            this.edge = mc.level.noCollision(mc.player, probe);
            if (this.edge && this.retryTicks == 0) {
               BlockPos target = BlockPos.containing(next.x, mc.player.getY() - 0.5, next.z);
               Placement candidate = this.findPlacement(mc, target);
               if (candidate != null && this.findSlot(mc, target) >= 0) {
                  this.placement = candidate;
                  this.cycleOrigin = mc.player.position();
                  this.approachYaw = mc.player.getYRot();
                  this.approachPitch = mc.player.getXRot();
                  this.setPhase(Phase.AIM);
               }
            }
         }
      }
      if (this.edge || this.phase != Phase.WALK) {
         event.setShift(true);
         event.setSprint(false);
         mc.player.setSprinting(false);
         this.crouchTicks++;
         // Creep in vanilla sneak until the side face is visible. Once aiming, hold
         // movement so turning the camera cannot redirect held W off the bridge.
         if (this.phase != Phase.WALK || this.findSlot(mc, mc.player.blockPosition().below()) < 0) {
            event.clearMovement(false, true);
         }
      } else {
         this.crouchTicks = 0;
      }
   }

   /** Arm only on an observed grounded jump input; never activate on an arbitrary fall. */
   private boolean onJumpInput(Minecraft mc, PlayerInputEvent event) {
      if (!this.jumpArmed) {
         // Do not inherit a ground aim, stale target or forced sneak into takeoff.
         this.phase = Phase.WALK;
         this.placement = null;
         this.cycleOrigin = null;
         this.jumpArmed = true;
         this.jumpCycle = false;
         this.leftGround = false;
         this.jumpViewPending = false;
         this.jumpTicks = 0;
         this.takeoff = mc.player.position();
         this.bridgeY = BlockPos.containing(this.takeoff.x, this.takeoff.y - 0.5, this.takeoff.z).getY();
         this.jumpHeading = mc.player.getYRot();
         this.jumpPitch = mc.player.getXRot();
      }
      if (!mc.player.onGround()) this.leftGround = true;
      if (this.leftGround && mc.player.onGround()) {
         // Landed: return the view if a jump cycle was in progress, then resume edge mode.
         this.jumpArmed = false;
         this.jumpCycle = false;
         this.cycleOrigin = mc.player.position();
         if (this.jumpViewPending || this.phase != Phase.WALK) this.setPhase(Phase.RETURN);
         this.jumpViewPending = false;
         return false;
      }
      if (++this.jumpTicks > 30 || mc.player.getY() < this.takeoff.y - 0.15
         || mc.player.position().distanceToSqr(this.takeoff) > 16) {
         this.reset();
         return true;
      }
      this.inputReady = true;
      this.edge = false;
      // On the takeoff input leave jump, shift and sprint entirely to vanilla.
      if (!this.leftGround) return true;
      if (this.phase == Phase.WALK && this.retryTicks == 0) {
         Vec3 motion = mc.player.getDeltaMovement();
         double prediction = this.jumpLookAhead.get();
         BlockPos current = BlockPos.containing(mc.player.getX(), this.bridgeY, mc.player.getZ());
         BlockPos ahead = BlockPos.containing(mc.player.getX() + motion.x * prediction,
            this.bridgeY, mc.player.getZ() + motion.z * prediction);
         Placement candidate = this.findPlacement(mc, current);
         if (candidate == null && !ahead.equals(current)) candidate = this.findPlacement(mc, ahead);
         if (candidate != null && this.findSlot(mc, candidate.target()) >= 0) {
            this.placement = candidate;
            this.cycleOrigin = mc.player.position();
            this.approachYaw = this.jumpHeading;
            this.approachPitch = this.jumpPitch;
            this.jumpViewPending = true;
            this.jumpCycle = true;
            this.setPhase(Phase.AIM);
         }
      }
      if (this.phase != Phase.WALK || this.jumpViewPending) {
         // Preserve the world-space WASD heading while the visible camera turns.
         // No velocity, position, jump, or sneak packets are manufactured here.
         Vec3 direction = inputDirection(event.moveVector().x, event.moveVector().y, this.jumpHeading);
         Vec2 corrected = localInput(direction, mc.player.getYRot());
         Input keys = event.keyPresses();
         event.setMoveVector(corrected);
         event.setKeyPresses(new Input(corrected.y > 0.01F, corrected.y < -0.01F,
            corrected.x > 0.01F, corrected.x < -0.01F, keys.jump(), keys.shift(), false));
         mc.player.setSprinting(false);
      }
      return true;
   }

   @Override
   protected void onTick() {
      Minecraft mc = Minecraft.getInstance();
      if (!this.available(mc) || this.world != mc.level || this.player != mc.player) {
         this.reset();
         return;
      }
      // Consume one fresh keyboard-input sample; do not run using a stale GUI/world state.
      if (!this.inputReady) return;
      this.inputReady = false;
      if (this.retryTicks > 0) this.retryTicks--;
      if (this.phase == Phase.WALK) return;
      if ((!mc.player.onGround() && !this.jumpCycle)
         || (this.placement == null && this.phase != Phase.RETURN)) {
         this.reset();
         return;
      }
      this.phaseTicks++;
      if (this.phase == Phase.RETURN) {
         // Manual mouse movement must not keep the player locked indefinitely.
         if (this.phaseTicks > 200) {
            this.reset();
            return;
         }
         float speed = this.jumpCycle ? this.jumpTurnSpeed.get().floatValue() : this.returnSpeed.get().floatValue();
         if (!this.returnView.get() || this.rotate(mc, this.approachYaw, this.approachPitch, speed, speed)) {
            this.setPhase(Phase.WALK);
            this.placement = null;
            this.jumpCycle = false;
         }
         return;
      }
      if (this.phaseTicks > this.timeout.get().intValue()) {
         this.finishCycle(true);
         return;
      }
      if (this.phase == Phase.WAIT) {
         // This observes the client world (including prediction), not a server acknowledgement.
         if (!mc.level.getBlockState(this.placement.target()).canBeReplaced()) {
            if (++this.placedTicks > (this.jumpCycle ? 0 : this.releaseDelay.get().intValue())) {
               if (this.jumpCycle) {
                  // Stay looking at the bridge until landing: returning between airborne
                  // placements wastes the short jump window and reverses WASD repeatedly.
                  this.setPhase(Phase.WALK);
                  this.placement = null;
                  this.jumpCycle = false;
               } else {
                  this.finishCycle(false);
               }
            }
         } else {
            this.placedTicks = 0;
         }
         return;
      }
      if (!validTarget(mc, this.placement.target()) || !validSupport(mc, this.placement.hit().getBlockPos())) {
         this.finishCycle(true);
         return;
      }
      BlockHitResult actualHit = this.viewHit(mc);
      // Any point on the intended face is sufficient. Do not force the crosshair
      // all the way to a sample after it has already reached a valid placement ray.
      if (!sameFace(actualHit, this.placement.hit())) {
         Placement refreshed = this.findPlacement(mc, this.placement.target());
         if (refreshed == null) {
            this.finishCycle(true);
            return;
         }
         this.placement = refreshed;
         Rotation rotation = Rotation.lookingAt(this.placement.hit().getLocation(), mc.player.getEyePosition());
         float yawLimit = this.jumpCycle ? this.jumpTurnSpeed.get().floatValue() : this.yawSpeed.get().floatValue();
         float pitchLimit = this.jumpCycle ? this.jumpTurnSpeed.get().floatValue() : this.pitchSpeed.get().floatValue();
         this.rotate(mc, rotation.getYaw(), rotation.getPitch(), yawLimit, pitchLimit);
         this.alignedTicks = 0;
         // Click no earlier than the next tick: let vanilla transmit the visible view first.
         return;
      }
      if ((!this.jumpCycle && this.crouchTicks < this.sneakDelay.get().intValue())
         || ++this.alignedTicks <= (this.jumpCycle ? 0 : this.placeDelay.get().intValue())) return;
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      int slot = this.findSlot(mc, this.placement.target());
      if (slot < 0) {
         this.finishCycle(true);
         return;
      }
      try {
         InventoryHelper.selectHotbarSlot(slot);
         if (mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, actualHit).consumesAction()) {
            if (this.swing.get()) mc.player.swing(InteractionHand.MAIN_HAND);
            this.placedTicks = 0;
            this.setPhase(Phase.WAIT);
         } else {
            this.finishCycle(true);
         }
      } finally {
         InventoryHelper.selectHotbarSlot(originalSlot);
      }
   }

   private void setPhase(Phase next) {
      this.phase = next;
      this.phaseTicks = this.alignedTicks = 0;
   }

   private void finishCycle(boolean failed) {
      this.retryTicks = failed ? this.retryDelay.get().intValue() : 0;
      this.setPhase(Phase.RETURN);
   }

   private boolean rotate(Minecraft mc, float yaw, float pitch, float yawLimit, float pitchLimit) {
      float nextYaw = stepAngle(mc.player.getYRot(), yaw, yawLimit, this.smoothing.get().floatValue());
      float nextPitch = mc.player.getXRot() + smoothStep(pitch - mc.player.getXRot(), pitchLimit, this.smoothing.get().floatValue());
      mc.player.setYRot(nextYaw);
      mc.player.setXRot(Mth.clamp(nextPitch, -90.0F, 90.0F));
      return Math.abs(Mth.wrapDegrees(yaw - nextYaw)) < 0.1F && Math.abs(pitch - nextPitch) < 0.1F;
   }

   static float stepAngle(float current, float target, float limit, float smoothing) {
      return current + smoothStep(Mth.wrapDegrees(target - current), limit, smoothing);
   }

   static float smoothStep(float delta, float limit, float smoothing) {
      float step = Math.min(Math.abs(delta), Math.min(limit, Math.max(0.15F, Math.abs(delta) * smoothing)));
      return Math.copySign(step, delta);
   }

   static Vec2 localInput(Vec3 worldDirection, float yaw) {
      double angle = Math.toRadians(yaw);
      return new Vec2((float)(worldDirection.x * Math.cos(angle) + worldDirection.z * Math.sin(angle)),
         (float)(worldDirection.z * Math.cos(angle) - worldDirection.x * Math.sin(angle)));
   }

   static double angularCost(float yaw, float pitch, Rotation rotation) {
      // Compare actual view rays: yaw degrees alone overestimate turns near straight down.
      double dot = Vec3.directionFromRotation(pitch, yaw).dot(rotation.toVector());
      return Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
   }

   static Vec3 inputDirection(float strafe, float forward, float yaw) {
      double angle = Math.toRadians(yaw);
      return new Vec3(strafe * Math.cos(angle) - forward * Math.sin(angle), 0.0,
         forward * Math.cos(angle) + strafe * Math.sin(angle)).normalize();
   }

   private int findSlot(Minecraft mc, BlockPos target) {
      int selected = mc.player.getInventory().getSelectedSlot();
      if (this.isBuildingBlock(mc, selected, target)) return selected;
      if (this.autoSwitch.get()) {
         for (int slot = 0; slot < 9; slot++) {
            if (this.isBuildingBlock(mc, slot, target)) return slot;
         }
      }
      return -1;
   }

   private boolean isBuildingBlock(Minecraft mc, int slot, BlockPos target) {
      ItemStack stack = mc.player.getInventory().getItem(slot);
      if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem item)) return false;
      return !(item.getBlock() instanceof FallingBlock) && !(item.getBlock() instanceof EntityBlock)
         && item.getBlock().defaultBlockState().isCollisionShapeFullBlock(mc.level, target);
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

   private Placement findPlacement(Minecraft mc, BlockPos target) {
      BlockHitResult hit = findHit(mc, target, this.reach.get(), this.aimSamples.get().intValue());
      return hit == null ? null : new Placement(target, hit);
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

   private BlockHitResult viewHit(Minecraft mc) {
      return viewHit(mc, this.reach.get());
   }

   static BlockHitResult viewHit(Minecraft mc, double requestedReach) {
      Vec3 eye = mc.player.getEyePosition();
      double range = Math.min(requestedReach, mc.player.blockInteractionRange());
      return mc.level.clip(new ClipContext(eye, eye.add(mc.player.getLookAngle().scale(range)),
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
   }

   private static boolean sameFace(BlockHitResult actual, BlockHitResult expected) {
      return actual.getType() == HitResult.Type.BLOCK && actual.getBlockPos().equals(expected.getBlockPos())
         && actual.getDirection() == expected.getDirection() && !actual.isInside();
   }

   private record Placement(BlockPos target, BlockHitResult hit) { }
}
