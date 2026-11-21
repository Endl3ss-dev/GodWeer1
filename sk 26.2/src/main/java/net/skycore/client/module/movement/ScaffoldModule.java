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
import net.minecraft.world.phys.Vec2;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;

/** Flat Telly only. Shared raycast helpers are also used by the rehearsal Coach. */
public final class ScaffoldModule extends ClientModule {
   private final TellyScaffoldController telly = new TellyScaffoldController();
   private Subscription inputSubscription;
   private final BooleanSetting autoJump = this.add(new BooleanSetting("Telly Auto Jump", "Jump before the edge; otherwise use manual Space", true));
   private final BooleanSetting sprint = this.add(new BooleanSetting("Telly Sprint", "Sprint during approach and takeoff", true));
   private final BooleanSetting autoSwitch = this.add(new BooleanSetting("Telly Auto Switch", "Select full building blocks from hotbar; restore selection when done", true));
   private final BooleanSetting swing = this.add(new BooleanSetting("Telly Swing", "Swing after accepted interaction", true));
   private final BooleanSetting diagnostics = this.add(new BooleanSetting("Telly Diagnostics", "Explain why Telly is paused or stopped", true));
   private final NumberSetting jumpDistance = number("Telly Jump Distance", "Distance ahead used to detect the edge", 0.3, 0.1, 0.8, 0.05, " blocks");
   private final NumberSetting blockCount = number("Telly Blocks", "Maximum unique blocks per jump, not a guarantee", 3, 1, 4, 1, " blocks");
   private final NumberSetting startTick = number("Telly Start Tick", "Earliest airborne tick for an aligned placement", 1, 0, 8, 1, " ticks");
   private final NumberSetting cps = number("Telly CPS", "Maximum wall-clock placement attempts; unsafe rays are skipped", 15, 1, 15, 1, " cps");
   private final BooleanSetting camLock = this.add(new BooleanSetting("Cam Lock", "Camera follows the Scaffold head; OFF allows mouse free-look", true));
   private final BooleanSetting returnToView = this.add(new BooleanSetting("Telly Return To View", "Return to view saved at takeoff; OFF uses the X-axis approach direction", true));
   private final NumberSetting returnPitch = number("Telly Return Pitch", "Pitch for fixed-direction return", 20, -60, 80, 1, " deg");
   private final NumberSetting turnSpeed = number("Telly Turn Speed", "Head speed per 50ms, updated every frame", 180, 15, 180, 5, " deg/tick");
   private final NumberSetting returnSpeed = number("Telly Return Speed", "Head return speed per 50ms", 180, 15, 180, 5, " deg/tick");
   private final NumberSetting smoothing = number("Telly Smoothing", "1 gives constant bounded turn speed", 1, 0.2, 1, 0.05, "");
   private final NumberSetting reach = number("Telly Reach", "Native interaction range still applies", 4.5, 2, 4.5, 0.1, " blocks");
   private final NumberSetting aimSamples = number("Telly Aim Samples", "Face samples per axis", 5, 3, 7, 1, " points");

   public ScaffoldModule() {
      super("Scaffold", "Flat Telly: approach, jump, visible turn, place and return", ModuleCategory.MOVEMENT);
   }

   private NumberSetting number(String name, String description, double value, double min, double max, double step, String suffix) {
      return this.add(new NumberSetting(name, description, value, min, max, step, suffix));
   }

   record TellySettings(boolean autoJump, boolean sprint, boolean autoSwitch, boolean swing, boolean diagnostics,
                        double jumpDistance, int blocks, int startTick, double cps,
                        float turnSpeed, float returnSpeed, float smoothing, double reach, int samples, boolean camLock, boolean returnToView, float returnPitch) { }

   TellySettings tellySettings() {
      return new TellySettings(this.autoJump.get(), this.sprint.get(), this.autoSwitch.get(), this.swing.get(),
         this.diagnostics.get(), this.jumpDistance.get(), this.blockCount.get().intValue(), this.startTick.get().intValue(),
         this.cps.get(), this.turnSpeed.get().floatValue(), this.returnSpeed.get().floatValue(),
         this.smoothing.get().floatValue(), this.reach.get(), this.aimSamples.get().intValue(), this.camLock.get(), this.returnToView.get(), this.returnPitch.get().floatValue());
   }

   @Override
   protected void onEnable() {
      this.telly.reset();
      if (this.inputSubscription != null) this.inputSubscription.close();
      this.inputSubscription = SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
   }

   @Override
   protected void onDisable() {
      if (this.inputSubscription != null) this.inputSubscription.close();
      this.inputSubscription = null;
      this.telly.reset();
   }

   private void onInput(PlayerInputEvent event) {
      if (this.isEnabled()) this.telly.input(Minecraft.getInstance(), event, this, this.tellySettings());
   }

   @Override
   protected void onTick() {
      this.telly.tick(Minecraft.getInstance(), this, this.tellySettings());
   }

   public void onFrame(long now) {
      if (this.isEnabled()) this.telly.frame(Minecraft.getInstance(), this, this.tellySettings(), now);
   }

   public boolean freeCameraActive() { return this.isEnabled() && this.telly.freeCameraActive(); }
   public float freeCameraYaw() { return this.telly.freeYaw(); }
   public float freeCameraPitch() { return this.telly.freePitch(); }
   public boolean mouseTurn(double yaw, double pitch) {
      return this.freeCameraActive() && this.telly.mouseTurn(yaw, pitch);
   }

   /** X-side support face only; prefer central samples, never top/bottom faces. */
   static BlockHitResult findTellyHit(Minecraft mc, BlockPos target, Direction heading, double reach, int samples) {
      if (!validTarget(mc, target) || (heading != Direction.EAST && heading != Direction.WEST)) return null;
      BlockPos support = target.relative(heading.getOpposite());
      if (!validSupport(mc, support)) return null;
      Vec3 eye = mc.player.getEyePosition();
      double range = Math.min(reach, mc.player.blockInteractionRange());
      Vec3 centre = facePoint(support, heading, 0.5, 0.5);
      BlockHitResult best = null;
      double bestCost = Double.POSITIVE_INFINITY;
      // Test centre explicitly even when the configured grid has an even size.
      for (int index = -1; index < samples * samples; index++) {
         double u = index < 0 ? 0.5 : 0.25 + 0.5 * (index / samples) / (samples - 1.0);
         double v = index < 0 ? 0.5 : 0.25 + 0.5 * (index % samples) / (samples - 1.0);
         Vec3 point = facePoint(support, heading, u, v);
         if (eye.distanceToSqr(point) > range * range) continue;
         double cost = point.distanceToSqr(centre);
         if (cost >= bestCost) continue;
         BlockHitResult expected = new BlockHitResult(point, heading, support, false);
         BlockHitResult visible = mc.level.clip(new ClipContext(eye,
            point.add(point.subtract(eye).normalize().scale(0.001)),
            ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
         if (sameFace(visible, expected)) { best = expected; bestCost = cost; }
      }
      return best;
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
      return mc.level.clip(new ClipContext(eye, eye.add(mc.player.getLookAngle().scale(range)),
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
   }

   static boolean sameFace(BlockHitResult actual, BlockHitResult expected) {
      return actual.getType() == HitResult.Type.BLOCK && actual.getBlockPos().equals(expected.getBlockPos())
         && actual.getDirection() == expected.getDirection() && !actual.isInside();
   }

}
