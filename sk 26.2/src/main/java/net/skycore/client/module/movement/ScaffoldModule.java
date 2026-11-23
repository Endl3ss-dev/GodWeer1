package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.FreeLookController;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.MovementHelper;
import net.skycore.client.util.UseItemHelper;

/**
 * Eagle Scaffold with optional automatic bridge placement for Minecraft 26.2 / SkyCore.
 *
 * <p>The only module mode is still {@code Eagle}. AutoPlace is an independent opt-in
 * feature: it fills the replaceable block below the player, using an adjacent solid
 * support block and the selected building block from the configured switch mode.
 *
 * <p>While the player moves, placement is locked to the dominant movement axis
 * (X or Z): the support is only searched behind the player along that axis and the
 * rotation yaw is snapped to the cardinal direction opposite to the movement, so
 * the view never swings towards a side face and the bridge stays on one row.
 * Movement input is remapped around the managed rotation, therefore the walking
 * trajectory does not bend when the (server) head turns around to place a block.
 * Sprint input is ignored while AutoPlace rotates: the server rotation faces
 * backwards, and sprinting against it would only produce invalid sprint packets.
 *
 * <p>{@code ClientLock} chooses whether the managed rotation is drawn on the
 * player's own screen. With it off, the rotation stays silent (free-look) and
 * the camera can be moved freely while the module builds behind the player.
 */
public final class ScaffoldModule extends ClientModule {
   private enum Mode {
      Eagle
   }

   private static final int ROTATION_PRIORITY = 40;
   private static final int ROTATION_TIMEOUT_TICKS = 2;
   private static final double MAX_REACH = 4.5;

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Eagle", "Eagle"));
   private final BooleanSetting autoPlace = this.add(new BooleanSetting("AutoPlace", false));
   private final NumberSetting cps = this.add(new NumberSetting("CPS", 10, 1, 20, 1));
   private final ModeSetting autoSwitch = this.add(new ModeSetting(
      "AutoSwitch", "Silent", "Normal", "Silent", "Inventory", "None"));
   private final ModeSetting rotate = this.add(new ModeSetting(
      "Rotate", "Normal", "Normal", "Smooth", "Linear", "None"));
   private final BooleanSetting clientLock = this.add(new BooleanSetting("ClientLock", true));
   private final BooleanSetting lockY = this.add(new BooleanSetting("LockY", false));
   private final BooleanSetting lastMomentPlace = this.add(new BooleanSetting("LastMomentPlace", false));

   private boolean wasSneaking;
   private long nextPlaceNanos;
   private Subscription tickSubscription;
   private Subscription inputSubscription;
   /** Locked bridge axis: {@code Boolean.TRUE} = X, {@code Boolean.FALSE} = Z, null = free. */
   private Boolean bridgeXAxis;
   private int bridgeMoveSign = 1;
   private int bridgeIdleTicks;
   private Integer lockedY;
   private boolean moveFixActive;
   private float moveFixViewYaw;

   public ScaffoldModule() {
      super("Scaffold", "Eagle edge protection with optional automatic placement", ModuleCategory.MOVEMENT);
      this.cps.visibleWhen(() -> this.autoPlace.get());
      this.autoSwitch.visibleWhen(() -> this.autoPlace.get());
      this.rotate.visibleWhen(() -> this.autoPlace.get());
      this.clientLock.visibleWhen(() -> this.autoPlace.get() && !this.rotate.is("None"));
      this.lockY.visibleWhen(() -> this.autoPlace.get());
      this.lastMomentPlace.visibleWhen(() -> this.autoPlace.get());
   }

   @Override
   protected void onEnable() {
      this.wasSneaking = false;
      this.nextPlaceNanos = 0L;
      this.bridgeXAxis = null;
      this.bridgeIdleTicks = 0;
      this.lockedY = null;
      this.moveFixActive = false;
      if (this.tickSubscription != null) {
         this.tickSubscription.close();
      }
      this.tickSubscription = SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.onPlayerTick());
      if (this.inputSubscription != null) {
         this.inputSubscription.close();
      }
      this.inputSubscription = SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onPlayerInput);
   }

   @Override
   protected void onDisable() {
      Minecraft minecraft = Minecraft.getInstance();
      if (this.wasSneaking && minecraft != null) {
         minecraft.options.keyShift.setDown(false);
         this.wasSneaking = false;
      }
      this.stopManagedRotation();
      this.moveFixActive = false;
      this.bridgeXAxis = null;
      this.bridgeIdleTicks = 0;
      this.lockedY = null;
      if (this.tickSubscription != null) {
         this.tickSubscription.close();
         this.tickSubscription = null;
      }
      if (this.inputSubscription != null) {
         this.inputSubscription.close();
         this.inputSubscription = null;
      }
   }

   private void onPlayerTick() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || minecraft.level == null || minecraft.gameMode == null) {
         this.releaseManagedRotation();
         this.bridgeXAxis = null;
         this.bridgeIdleTicks = 0;
         this.lockedY = null;
         return;
      }

      this.handleEagleMode(minecraft);
      if (!this.autoPlace.get()) {
         this.releaseManagedRotation();
         return;
      }

      if (!this.canAutoPlace(minecraft)) {
         this.releaseManagedRotation();
         return;
      }

      // Add#1: building happens against the server rotation, so an active sprint
      // is dropped here as well as in the input event; it must not send packets.
      if (!this.rotate.is("None") && minecraft.player.isSprinting()) {
         minecraft.player.setSprinting(false);
      }

      PlacementTarget target = this.findPlacementTarget(minecraft);
      if (target == null) {
         this.releaseManagedRotation();
         return;
      }

      if (this.rotate.is("None")) {
         this.releaseManagedRotation();
      } else {
         if (!this.moveFixActive) {
            this.moveFixViewYaw = this.referenceYaw(minecraft.player);
         }
         this.moveFixActive = true;
         if (!this.applyRotation(minecraft.player, target.rotation())) {
            return;
         }
      }

      if (!this.lastMomentReady(minecraft.player, target)) {
         return;
      }

      long now = System.nanoTime();
      if (now < this.nextPlaceNanos) {
         return;
      }

      if (this.placeBlock(minecraft, target)) {
         this.nextPlaceNanos = now + this.placeIntervalNanos();
      }
   }

   /**
    * LastMomentPlace: while airborne, hold the placement until the player is
    * actually falling and the feet are about to reach the top of the target
    * block. The rotation is already aimed by then, so the block snaps in at
    * the last moment and the player lands on it instead of falling past.
    * On the ground the placement is never delayed.
    */
   private boolean lastMomentReady(LocalPlayer player, PlacementTarget target) {
      if (!this.lastMomentPlace.get() || player.onGround()) {
         return true;
      }
      double velocityY = player.getDeltaMovement().y;
      if (velocityY > 0.0) {
         return false;
      }
      double targetTop = target.target().getY() + 1.0;
      double distance = player.getY() - targetTop;
      return distance <= 0.75 && distance + velocityY <= 0.9;
   }

   private void onPlayerInput(PlayerInputEvent event) {
      if (!this.isEnabled() || !this.autoPlace.get()) {
         return;
      }
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || minecraft.level == null) {
         return;
      }
      if (this.rotate.is("None")) {
         return;
      }

      // Keep the world-space walking direction the player asked for, even though
      // the managed rotation turned the head (visible or silent) away from it.
      if (this.moveFixActive && RotationManager.INSTANCE.isAimingAtPriority(ROTATION_PRIORITY)) {
         float viewYaw = !this.clientLock.get()
            && RotationController.isActive() && !RotationController.isClientLook()
               ? FreeLookController.getFreeYaw()
               : this.moveFixViewYaw;
         MovementHelper.fixMovement(event, viewYaw);
      }

      // Sprint is ignored while AutoPlace manages a rotation (see class javadoc).
      event.setSprint(false);
   }

   /** Stops the managed rotation and the movement correction tied to it. */
   private void releaseManagedRotation() {
      this.stopManagedRotation();
      this.moveFixActive = false;
   }

   /**
    * Yaw the player actually steers with: the free-look yaw while a silent
    * rotation is active, the yaw captured when the rotation engaged while it is
    * visible, and the plain player yaw otherwise.
    */
   private float referenceYaw(LocalPlayer player) {
      if (!this.clientLock.get()
         && RotationController.isActive() && !RotationController.isClientLook()) {
         return FreeLookController.getFreeYaw();
      }
      return this.moveFixActive ? this.moveFixViewYaw : player.getYRot();
   }

   private void handleEagleMode(Minecraft minecraft) {
      BlockPos checkPos = BlockPos.containing(
         minecraft.player.getX(), minecraft.player.getY() - 1.0, minecraft.player.getZ());
      boolean shouldSneak = minecraft.level.getBlockState(checkPos).isAir();
      minecraft.options.keyShift.setDown(shouldSneak);
      this.wasSneaking = shouldSneak;
   }

   private boolean canAutoPlace(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      return minecraft.gui.screen() == null
         && player.isAlive()
         && !player.isSpectator()
         && !player.isUsingItem();
   }

   private long placeIntervalNanos() {
      int blocksPerSecond = Math.max(1, this.cps.get().intValue());
      return 1_000_000_000L / blocksPerSecond;
   }

   /**
    * Tracks the dominant movement axis. The direction is read from the actual
    * horizontal velocity — the trajectory of the body — and never from the
    * camera: with {@code ClientLock} off the player may look and turn anywhere
    * (Fix#1), the module keeps its own locked bridge direction and rotation.
    * The lock survives short stops and only expires after a second without
    * any movement intent. Hysteresis keeps the axis from flapping while the
    * player moves diagonally.
    */
   private void updateBridgeAxis(LocalPlayer player) {
      double[] intent = this.movementIntent(player);
      if (intent == null) {
         if (++this.bridgeIdleTicks > 20) {
            this.bridgeXAxis = null;
         }
         return;
      }
      this.bridgeIdleTicks = 0;

      double dx = intent[0];
      double dz = intent[1];
      boolean xDominant = Math.abs(dx) >= Math.abs(dz);
      if (this.bridgeXAxis == null || xDominant == this.bridgeXAxis) {
         this.bridgeXAxis = xDominant;
      } else if (Math.max(Math.abs(dx), Math.abs(dz)) >= 1.5 * Math.min(Math.abs(dx), Math.abs(dz))) {
         this.bridgeXAxis = xDominant;
      }

      if (this.bridgeXAxis) {
         if (dx != 0.0) {
            this.bridgeMoveSign = dx > 0.0 ? 1 : -1;
         }
      } else if (dz != 0.0) {
         this.bridgeMoveSign = dz > 0.0 ? 1 : -1;
      }
   }

   /** World-space horizontal movement direction, or null when standing still. */
   private double[] movementIntent(LocalPlayer player) {
      // Primary source: real velocity. It already reflects where the body is
      // going, independent of where the (free) camera currently looks.
      Vec3 motion = player.getDeltaMovement();
      if (motion.x * motion.x + motion.z * motion.z > 0.0009) {
         return new double[]{motion.x, motion.z};
      }

      // Fallback for taking the first step from a standstill: movement keys
      // relative to the steering yaw (free-look when silent, view otherwise).
      Input input = player.input != null ? player.input.keyPresses : null;
      if (input != null && (input.forward() || input.backward() || input.left() || input.right())) {
         double forward = (input.forward() ? 1.0 : 0.0) - (input.backward() ? 1.0 : 0.0);
         double strafe = (input.left() ? 1.0 : 0.0) - (input.right() ? 1.0 : 0.0);
         double radians = Math.toRadians(this.referenceYaw(player));
         double facingX = -Math.sin(radians);
         double facingZ = Math.cos(radians);
         // Left of the facing direction: facing south (+Z) the left hand is east (+X).
         double leftX = facingZ;
         double leftZ = -facingX;
         return new double[]{forward * facingX + strafe * leftX, forward * facingZ + strafe * leftZ};
      }
      return null;
   }

   /** Cardinal yaw looking back against the locked movement direction. */
   private float bridgeYaw() {
      if (this.bridgeXAxis) {
         return this.bridgeMoveSign > 0 ? 90.0F : -90.0F;
      }
      return this.bridgeMoveSign > 0 ? 180.0F : 0.0F;
   }

   private PlacementTarget findPlacementTarget(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      this.updateBridgeAxis(player);
      BlockPos belowPos = BlockPos.containing(player.getX(), player.getY() - 1.0, player.getZ());

      // LockY: pin the bridge level to the Y where building started, so jumps
      // extend the bridge only along X/Z instead of staircasing upwards. The
      // level is re-captured whenever the player stands on solid ground at a
      // different height (walking down a slope, starting a new bridge).
      int targetY = belowPos.getY();
      if (this.lockY.get()) {
         if (this.lockedY == null
            || player.onGround() && targetY != this.lockedY
               && !minecraft.level.getBlockState(belowPos).canBeReplaced()) {
            this.lockedY = targetY;
         }
         targetY = this.lockedY;
      } else {
         this.lockedY = null;
      }

      BlockPos targetPos = new BlockPos(belowPos.getX(), targetY, belowPos.getZ());
      if (!minecraft.level.getBlockState(targetPos).canBeReplaced()
         || this.lockY.get() && targetY > belowPos.getY()
            && new AABB(targetPos).intersects(player.getBoundingBox())) {
         return null;
      }

      if (this.rotate.is("None")) {
         return this.findManualPlacementTarget(minecraft, targetPos);
      }

      // Bug#1: while moving, only the support behind the player along the locked
      // axis (then the support below) may be used, never a side face.
      boolean axisLocked = this.bridgeXAxis != null;
      Direction[] faces;
      if (axisLocked) {
         Direction behind = this.bridgeXAxis
            ? (this.bridgeMoveSign > 0 ? Direction.EAST : Direction.WEST)
            : (this.bridgeMoveSign > 0 ? Direction.SOUTH : Direction.NORTH);
         faces = new Direction[]{behind, Direction.UP};
      } else {
         faces = Direction.values();
      }

      Vec3 eye = player.getEyePosition();
      double reach = Math.min(MAX_REACH, player.blockInteractionRange());
      PlacementTarget best = null;
      double bestScore = Double.POSITIVE_INFINITY;

      for (Direction face : faces) {
         BlockPos supportPos = targetPos.relative(face.getOpposite());
         if (!this.validSupport(minecraft, supportPos)
            || new AABB(supportPos).intersects(player.getBoundingBox())) {
            continue;
         }

         Vec3 hitPoint = this.faceCentre(supportPos, face);
         if (eye.distanceToSqr(hitPoint) > reach * reach) {
            continue;
         }

         BlockHitResult visible = minecraft.level.clip(new ClipContext(
            eye,
            hitPoint.add(hitPoint.subtract(eye).normalize().scale(0.001)),
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player));
         if (visible.getType() != HitResult.Type.BLOCK
            || !visible.getBlockPos().equals(supportPos)
            || visible.getDirection() != face
            || visible.isInside()) {
            continue;
         }

         Rotation look = Rotation.lookingAt(hitPoint, eye);
         // Snap the yaw to the bridge axis; only the pitch follows the hit point.
         Rotation rotation = axisLocked ? new Rotation(this.bridgeYaw(), look.getPitch()) : look;
         PlacementTarget candidate = new PlacementTarget(
            targetPos,
            new BlockHitResult(hitPoint, face, supportPos, false),
            rotation);
         if (axisLocked) {
            return candidate;
         }

         double angle = Math.abs(Mth.wrapDegrees(look.getYaw() - player.getYRot()))
            + Math.abs(look.getPitch() - player.getXRot());
         double score = eye.distanceToSqr(hitPoint) + angle * 0.01;
         if (score < bestScore) {
            bestScore = score;
            best = candidate;
         }
      }

      return best;
   }

   private PlacementTarget findManualPlacementTarget(Minecraft minecraft, BlockPos targetPos) {
      if (!(minecraft.hitResult instanceof BlockHitResult hit)
         || hit.getType() != HitResult.Type.BLOCK
         || hit.isInside()) {
         return null;
      }

      BlockPos supportPos = hit.getBlockPos();
      Direction face = hit.getDirection();
      if (!targetPos.equals(supportPos.relative(face)) || !this.validSupport(minecraft, supportPos)) {
         return null;
      }

      Vec3 eye = minecraft.player.getEyePosition();
      return new PlacementTarget(
         targetPos,
         new BlockHitResult(hit.getLocation(), face, supportPos, false),
         Rotation.lookingAt(hit.getLocation(), eye));
   }

   private boolean validSupport(Minecraft minecraft, BlockPos supportPos) {
      BlockState state = minecraft.level.getBlockState(supportPos);
      return !state.isAir()
         && !state.canBeReplaced()
         && state.getFluidState().isEmpty()
         && !state.getCollisionShape(minecraft.level, supportPos).isEmpty();
   }

   private Vec3 faceCentre(BlockPos supportPos, Direction face) {
      double x = supportPos.getX() + 0.5;
      double y = supportPos.getY() + 0.5;
      double z = supportPos.getZ() + 0.5;
      return switch (face) {
         case DOWN -> new Vec3(x, supportPos.getY(), z);
         case UP -> new Vec3(x, supportPos.getY() + 1.0, z);
         case NORTH -> new Vec3(x, y, supportPos.getZ());
         case SOUTH -> new Vec3(x, y, supportPos.getZ() + 1.0);
         case WEST -> new Vec3(supportPos.getX(), y, z);
         case EAST -> new Vec3(supportPos.getX() + 1.0, y, z);
      };
   }

   private boolean applyRotation(LocalPlayer player, Rotation desired) {
      // Add#2: ClientLock on = rotation is shown on the player's screen,
      // off = silent rotation with a free camera (FreeLookController).
      boolean visible = this.clientLock.get();
      if (this.rotate.is("Normal")) {
         return RotationManager.updateExact(desired, ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS, visible);
      }

      if (this.rotate.is("Smooth")) {
         RotationManager.update(desired, 75.0F, 75.0F, 180.0F, 180.0F,
            ROTATION_TIMEOUT_TICKS, ROTATION_PRIORITY, visible);
         return RotationManager.INSTANCE.isAimingAtPriority(ROTATION_PRIORITY)
            && this.isCloseToRotation(player, desired, 2.0F);
      }

      // Linear advances by a fixed angular amount per client tick instead of using
      // the distance-dependent easing used by RotationManager.update.
      float yaw = this.stepAngle(player.getYRot(), desired.getYaw(), 35.0F);
      float pitch = this.stepAngle(player.getXRot(), desired.getPitch(), 35.0F);
      Rotation next = new Rotation(yaw, Mth.clamp(pitch, -90.0F, 90.0F));
      if (!RotationManager.updateExact(next, ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS, visible)) {
         return false;
      }
      return this.isCloseToRotation(player, desired, 2.0F);
   }

   private boolean isCloseToRotation(LocalPlayer player, Rotation target, float tolerance) {
      return Math.abs(Mth.wrapDegrees(target.getYaw() - player.getYRot())) <= tolerance
         && Math.abs(target.getPitch() - player.getXRot()) <= tolerance;
   }

   private float stepAngle(float current, float target, float step) {
      return current + Mth.clamp(Mth.wrapDegrees(target - current), -step, step);
   }

   private boolean placeBlock(Minecraft minecraft, PlacementTarget target) {
      LocalPlayer player = minecraft.player;
      if (this.isBuildingStack(player.getOffhandItem())) {
         return this.interact(minecraft, InteractionHand.OFF_HAND, target.hit());
      }
      if (this.isBuildingStack(player.getMainHandItem())) {
         return this.interact(minecraft, InteractionHand.MAIN_HAND, target.hit());
      }

      return switch (this.autoSwitch.get()) {
         case "Normal" -> this.placeWithNormalSwitch(minecraft, target);
         case "Silent" -> this.placeWithSilentSwitch(minecraft, target);
         case "Inventory" -> this.placeWithInventorySwitch(minecraft, target);
         case "None" -> false;
         default -> false;
      };
   }

   private boolean placeWithNormalSwitch(Minecraft minecraft, PlacementTarget target) {
      int slot = this.findHotbarBlockSlot(minecraft.player);
      if (slot < 0) {
         return false;
      }
      UseItemHelper.switchToHotbarSlot(slot);
      return this.interact(minecraft, InteractionHand.MAIN_HAND, target.hit());
   }

   private boolean placeWithSilentSwitch(Minecraft minecraft, PlacementTarget target) {
      int slot = this.findHotbarBlockSlot(minecraft.player);
      if (slot < 0) {
         return false;
      }

      int previousSlot = minecraft.player.getInventory().getSelectedSlot();
      this.sendCarriedSlot(minecraft, slot);
      minecraft.player.getInventory().setSelectedSlot(slot);
      try {
         return this.interact(minecraft, InteractionHand.MAIN_HAND, target.hit());
      } finally {
         this.sendCarriedSlot(minecraft, previousSlot);
         minecraft.player.getInventory().setSelectedSlot(previousSlot);
      }
   }

   private boolean placeWithInventorySwitch(Minecraft minecraft, PlacementTarget target) {
      int inventorySlot = this.findInventoryBlockSlot(minecraft.player);
      if (inventorySlot < 0) {
         return this.placeWithNormalSwitch(minecraft, target);
      }

      int selectedSlot = minecraft.player.getInventory().getSelectedSlot();
      int menuId = minecraft.player.containerMenu.containerId;
      minecraft.gameMode.handleContainerInput(menuId, inventorySlot, selectedSlot,
         ContainerInput.SWAP, minecraft.player);
      try {
         return this.interact(minecraft, InteractionHand.MAIN_HAND, target.hit());
      } finally {
         minecraft.gameMode.handleContainerInput(menuId, inventorySlot, selectedSlot,
            ContainerInput.SWAP, minecraft.player);
      }
   }

   private boolean interact(Minecraft minecraft, InteractionHand hand, BlockHitResult hit) {
      if (!minecraft.level.getBlockState(hit.getBlockPos().relative(hit.getDirection())).canBeReplaced()) {
         return false;
      }
      var result = minecraft.gameMode.useItemOn(minecraft.player, hand, hit);
      if (result != null && result.consumesAction()) {
         minecraft.player.swing(hand);
         return true;
      }
      return false;
   }

   private void sendCarriedSlot(Minecraft minecraft, int slot) {
      if (minecraft.player.connection != null) {
         minecraft.player.connection.send(new ServerboundSetCarriedItemPacket(slot));
      }
   }

   private int findHotbarBlockSlot(LocalPlayer player) {
      for (int slot = 0; slot < 9; slot++) {
         if (this.isBuildingStack(player.getInventory().getItem(slot))) {
            return slot;
         }
      }
      return -1;
   }

   private int findInventoryBlockSlot(LocalPlayer player) {
      for (int slot = 9; slot < 36; slot++) {
         if (this.isBuildingStack(player.getInventory().getItem(slot))) {
            return slot;
         }
      }
      return -1;
   }

   private boolean isBuildingStack(ItemStack stack) {
      return !stack.isEmpty()
         && stack.getItem() instanceof BlockItem blockItem
         && !blockItem.getBlock().defaultBlockState().canBeReplaced();
   }

   private void stopManagedRotation() {
      if (RotationManager.INSTANCE.isAimingAtPriority(ROTATION_PRIORITY)) {
         RotationManager.INSTANCE.stopRotationIfPriority(ROTATION_PRIORITY);
      }
   }

   public String suffix() {
      return this.mode.get();
   }

   private record PlacementTarget(BlockPos target, BlockHitResult hit, Rotation rotation) {
   }
}
