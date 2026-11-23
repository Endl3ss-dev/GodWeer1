package net.skycore.client.module.movement;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
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

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Eagle", "Eagle", "CatLean"));
   private final BooleanSetting autoPlace = this.add(new BooleanSetting("AutoPlace", false));
   private final NumberSetting cps = this.add(new NumberSetting("CPS", 10, 1, 20, 1));
   private final ModeSetting autoSwitch = this.add(new ModeSetting(
      "AutoSwitch", "Silent", "Normal", "Silent", "Inventory", "None"));
   private final ModeSetting rotate = this.add(new ModeSetting(
      "Rotate", "Normal", "Normal", "Smooth", "Linear", "None"));
   private final BooleanSetting clientLock = this.add(new BooleanSetting("ClientLock", true));

   // CatLean mode settings (ported from the CatLean Scaffold module).
   private final ModeSetting catLeanTiming = this.add(new ModeSetting("Timing", "GRIM", "SEQUENTIAL", "VANILLA", "GRIM"));
   private final BooleanSetting tower = this.add(new BooleanSetting("Tower", true));
   private final BooleanSetting safeWalk = this.add(new BooleanSetting("SafeWalk", false));
   private final BooleanSetting allowShift = this.add(new BooleanSetting("AllowShift", false));
   private final BooleanSetting legit = this.add(new BooleanSetting("Legit", true));
   private final BooleanSetting saveAngles = this.add(new BooleanSetting("SaveAngles", true));
   private final BooleanSetting telly = this.add(new BooleanSetting("Telly", false));
   private final BooleanSetting clampSetting = this.add(new BooleanSetting("Clamp", true));
   private final BooleanSetting airPlace = this.add(new BooleanSetting("AirPlace", false));
   private final BooleanSetting grimDelay = this.add(new BooleanSetting("GrimDelay", false));
   private final BooleanSetting keepY = this.add(new BooleanSetting("KeepY", false));
   private final BooleanSetting catLeanRotate = this.add(new BooleanSetting("CatLeanRotate", true));
   private final BooleanSetting grimSilent = this.add(new BooleanSetting("GrimSilent", false));

   private boolean wasSneaking;
   private long nextPlaceNanos;
   private Subscription tickSubscription;
   private Subscription inputSubscription;
   /** Locked bridge axis: {@code Boolean.TRUE} = X, {@code Boolean.FALSE} = Z, null = free. */
   private Boolean bridgeXAxis;
   private int bridgeMoveSign = 1;
   private boolean moveFixActive;
   private float moveFixViewYaw;

   private static final int CATLEAN_ROTATION_PRIORITY = 41;
   private static final int CATLEAN_NO_KEEP_Y = -999;
   private static final BlockPos[] CATLEAN_OFFSETS = {
      new BlockPos(0, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0),
      new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(-2, 0, 0),
      new BlockPos(2, 0, 0), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2),
      new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0),
      new BlockPos(0, -1, 1), new BlockPos(0, -1, -1)
   };
   private int catLeanKeepYValue = CATLEAN_NO_KEEP_Y;
   private Rotation catLeanSavedRotation;
   private boolean catLeanShiftHeld;
   private long lastTowerDipMillis;
   private final java.util.concurrent.ConcurrentLinkedQueue<DelayedPacket> delayedPackets =
      new java.util.concurrent.ConcurrentLinkedQueue<>();

   public ScaffoldModule() {
      super("Scaffold", "Eagle edge protection with optional automatic placement", ModuleCategory.MOVEMENT);
      this.cps.visibleWhen(() -> this.autoPlace.get());
      this.autoSwitch.visibleWhen(() -> this.autoPlace.get() || this.mode.is("CatLean"));
      this.rotate.visibleWhen(() -> this.autoPlace.get());
      this.clientLock.visibleWhen(() -> this.autoPlace.get() && !this.rotate.is("None"));
      this.catLeanTiming.visibleWhen(() -> this.mode.is("CatLean"));
      this.tower.visibleWhen(() -> this.mode.is("CatLean"));
      this.safeWalk.visibleWhen(() -> this.mode.is("CatLean"));
      this.allowShift.visibleWhen(() -> this.mode.is("CatLean") && !this.safeWalk.get());
      this.legit.visibleWhen(() -> this.mode.is("CatLean"));
      this.saveAngles.visibleWhen(() -> this.mode.is("CatLean"));
      this.telly.visibleWhen(() -> this.mode.is("CatLean"));
      this.clampSetting.visibleWhen(() -> this.mode.is("CatLean"));
      this.airPlace.visibleWhen(() -> this.mode.is("CatLean"));
      this.grimDelay.visibleWhen(() -> this.mode.is("CatLean"));
      this.keepY.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanRotate.visibleWhen(() -> this.mode.is("CatLean"));
      this.grimSilent.visibleWhen(() -> this.mode.is("CatLean"));
   }

   @Override
   protected void onEnable() {
      this.wasSneaking = false;
      this.nextPlaceNanos = 0L;
      this.bridgeXAxis = null;
      this.moveFixActive = false;
      this.catLeanKeepYValue = CATLEAN_NO_KEEP_Y;
      this.catLeanSavedRotation = null;
      this.catLeanShiftHeld = false;
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
      if (RotationManager.INSTANCE.isAimingAtPriority(CATLEAN_ROTATION_PRIORITY)) {
         RotationManager.INSTANCE.stopRotationIfPriority(CATLEAN_ROTATION_PRIORITY);
      }
      this.flushDelayedPackets(true);
      if (this.catLeanShiftHeld && minecraft != null) {
         minecraft.options.keyShift.setDown(false);
         this.catLeanShiftHeld = false;
      }
      this.catLeanSavedRotation = null;
      this.moveFixActive = false;
      this.bridgeXAxis = null;
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
         return;
      }

      if (this.mode.is("CatLean")) {
         if (this.wasSneaking) {
            minecraft.options.keyShift.setDown(false);
            this.wasSneaking = false;
         }
         this.onCatLeanTick(minecraft);
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

      long now = System.nanoTime();
      if (now < this.nextPlaceNanos) {
         return;
      }

      if (this.placeBlock(minecraft, target)) {
         this.nextPlaceNanos = now + this.placeIntervalNanos();
      }
   }

   // ------------------------------------------------------------------
   // CatLean mode. Ported from the CatLean Scaffold module (deobfuscated
   // Kotlin): same settings, target search order, clamped hit point,
   // tower/safe-walk/legit/keep-height behaviour and rotation timings,
   // mapped onto SkyCore's RotationManager and switch machinery.
   // Approximations where the CatLean services were not provided:
   // InteractionManager.computeBlockPosWithFacing -> validSupport + eye
   // raycast over the same neighbour offsets; WorldUtil.isFlag4 ->
   // player.onGround(); WorldUtil.getAmount3 -> vertical velocity.
   // ------------------------------------------------------------------

   private void onCatLeanTick(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      boolean jumpDown = minecraft.options.keyJump.isDown();

      // Telly: while holding jump and already falling, CatLean waits.
      if (jumpDown && player.getDeltaMovement().y <= 0.0 && this.telly.get()) {
         this.flushDelayedPackets(false);
         return;
      }

      int slot = this.fetchCatLeanBlock(player);
      boolean canBuild = slot != -1
         && (this.safeWalk.get() || !player.isShiftKeyDown() || this.allowShift.get());
      if (!canBuild) {
         this.submitSavedAngles(player);
         this.flushDelayedPackets(false);
         return;
      }

      if (jumpDown && !player.onGround()) {
         this.catLeanKeepYValue = Mth.floor(player.getY() - 1.0);
      }
      if (player.getY() < this.catLeanKeepYValue) {
         this.catLeanKeepYValue = CATLEAN_NO_KEEP_Y;
      }

      boolean fullBlock = this.isFullBlock(player, slot);
      double baseY = fullBlock ? player.getY() : Math.ceil(player.getY());
      BlockPos targetPos = (this.keepY.get() || this.telly.get()) && this.catLeanKeepYValue != CATLEAN_NO_KEEP_Y
         ? BlockPos.containing(player.getX(), this.catLeanKeepYValue, player.getZ())
         : BlockPos.containing(player.getX(), baseY, player.getZ()).below();

      // SafeWalk: hold shift while the target cell is still empty.
      if (this.safeWalk.get() && player.onGround()) {
         boolean replaceable = minecraft.level.getBlockState(targetPos).canBeReplaced();
         if (replaceable && !player.isShiftKeyDown()) {
            minecraft.options.keyShift.setDown(true);
            this.catLeanShiftHeld = true;
         } else if (!replaceable && this.catLeanShiftHeld) {
            minecraft.options.keyShift.setDown(false);
            this.catLeanShiftHeld = false;
         }
      }

      CatLeanTarget target = this.airPlace.get()
         ? (minecraft.level.getBlockState(targetPos).canBeReplaced()
            ? new CatLeanTarget(targetPos, Direction.DOWN) : null)
         : this.computeCatLeanTarget(minecraft, targetPos);

      if (target == null || this.legit.get() && this.hasGroundCollision(minecraft)) {
         this.submitSavedAngles(player);
         this.flushDelayedPackets(false);
         return;
      }

      // Clamped hit point on the support face, as in CatLean: x/z clamped
      // into the support block around the player, y near the top of the face.
      BlockPos support = target.support();
      Direction face = target.face();
      Vec3 faceCenter = this.faceCentre(support, face);
      double yFraction = player.getY() - Math.floor(player.getY());
      double hitYOffset = yFraction == 0.5 ? 0.39 : 0.82 + ThreadLocalRandom.current().nextDouble(0.0, 0.16);
      double hitX = this.clampSetting.get()
         ? Mth.clamp(player.getX() + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), support.getX(), support.getX() + 1.0)
         : faceCenter.x;
      double hitZ = this.clampSetting.get()
         ? Mth.clamp(player.getZ() + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), support.getZ(), support.getZ() + 1.0)
         : faceCenter.z;
      Vec3 hitPoint = new Vec3(hitX, support.getY() + hitYOffset, hitZ);
      BlockHitResult hit = new BlockHitResult(hitPoint, face, support, false);
      Rotation desired = Rotation.lookingAt(hitPoint, player.getEyePosition());
      this.catLeanSavedRotation = desired;

      if (this.submitCatLeanRotation(player, desired)) {
         this.placeCatLean(minecraft, hit, desired);
      }
      this.flushDelayedPackets(false);
   }

   /** Searches the CatLean neighbour offsets for the first placeable cell with a support face. */
   private CatLeanTarget computeCatLeanTarget(Minecraft minecraft, BlockPos targetPos) {
      if (!minecraft.level.getBlockState(targetPos).canBeReplaced()) {
         return null;
      }
      for (BlockPos offset : CATLEAN_OFFSETS) {
         CatLeanTarget target = this.findCatLeanSupport(minecraft, targetPos.offset(offset));
         if (target != null) {
            return target;
         }
      }
      return null;
   }

   private CatLeanTarget findCatLeanSupport(Minecraft minecraft, BlockPos cell) {
      LocalPlayer player = minecraft.player;
      if (!minecraft.level.getBlockState(cell).canBeReplaced()) {
         return null;
      }
      Vec3 eye = player.getEyePosition();
      double reach = Math.min(MAX_REACH, player.blockInteractionRange());
      for (Direction face : Direction.values()) {
         BlockPos support = cell.relative(face.getOpposite());
         if (!this.validSupport(minecraft, support)
            || new AABB(support).intersects(player.getBoundingBox())) {
            continue;
         }
         Vec3 hitPoint = this.faceCentre(support, face);
         if (eye.distanceToSqr(hitPoint) > reach * reach) {
            continue;
         }
         BlockHitResult visible = minecraft.level.clip(new ClipContext(
            eye,
            hitPoint.add(hitPoint.subtract(eye).normalize().scale(0.001)),
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player));
         if (visible.getType() == HitResult.Type.BLOCK
            && visible.getBlockPos().equals(support)
            && visible.getDirection() == face
            && !visible.isInside()) {
            return new CatLeanTarget(support, face);
         }
      }
      return null;
   }

   /** Legit: do not place while a (shrunk) collision box still touches ground below. */
   private boolean hasGroundCollision(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      double shrink = this.catLeanTiming.is("VANILLA") ? -0.1 : -0.25;
      AABB box = player.getBoundingBox().inflate(shrink, 0.0, shrink).move(0.0, -0.1, 0.0);
      return minecraft.level.getBlockCollisions(player, box).iterator().hasNext();
   }

   /**
    * Submits the CatLean rotation for the current Timing and reports whether
    * the block may be placed this tick: SEQUENTIAL places right after the
    * submit, VANILLA (visible) and GRIM (silent) wait until the rotation has
    * actually arrived. With GrimSilent the rotation goes through raw PosRot
    * packets around the placement instead of the rotation manager.
    */
   private boolean submitCatLeanRotation(LocalPlayer player, Rotation desired) {
      if (!this.catLeanRotate.get() || this.grimSilent.get()) {
         return true;
      }
      boolean visible = this.catLeanTiming.is("VANILLA");
      if (!RotationManager.updateExact(desired, CATLEAN_ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS, visible)) {
         return false;
      }
      return this.catLeanTiming.is("SEQUENTIAL")
         || this.isCloseToRotation(player, desired, 2.5F);
   }

   /** Keeps the last bridge angles while there is nothing to place (SaveAngles). */
   private void submitSavedAngles(LocalPlayer player) {
      if (this.catLeanSavedRotation == null || !this.saveAngles.get()
         || !this.catLeanRotate.get() || this.grimSilent.get()) {
         return;
      }
      boolean visible = this.catLeanTiming.is("VANILLA");
      RotationManager.updateExact(this.catLeanSavedRotation, CATLEAN_ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS, visible);
   }

   private void placeCatLean(Minecraft minecraft, BlockHitResult hit, Rotation desired) {
      LocalPlayer player = minecraft.player;

      // Tower: hold jump in the air to keep boosting upwards, with the
      // periodic small dip CatLean applies roughly every 1.5 s.
      if (minecraft.options.keyJump.isDown() && !player.onGround() && this.tower.get()) {
         long now = System.currentTimeMillis();
         boolean dip = now - this.lastTowerDipMillis >= 1500L;
         if (dip) {
            this.lastTowerDipMillis = now;
         }
         player.setDeltaMovement(player.getDeltaMovement().x, dip ? -0.28 : 0.42, player.getDeltaMovement().z);
      }

      boolean packetRotation = this.grimSilent.get() && this.catLeanRotate.get();
      if (packetRotation) {
         this.sendPosRot(player, desired.getYaw(), desired.getPitch());
      }
      BlockPos placedPos = hit.getBlockPos().relative(hit.getDirection());
      this.placeBlock(minecraft, new PlacementTarget(placedPos, hit, desired));
      if (packetRotation) {
         this.sendPosRot(player, player.getYRot(), player.getXRot());
      }
      this.catLeanKeepYValue = hit.getBlockPos().getY();
   }

   private void sendPosRot(LocalPlayer player, float yaw, float pitch) {
      if (player.connection != null) {
         player.connection.send(new ServerboundMovePlayerPacket.PosRot(
            player.getX(), player.getY(), player.getZ(), yaw, pitch,
            player.onGround(), player.horizontalCollision));
      }
   }

   /** -2 = offhand, -1 = nothing, otherwise a hotbar marker like CatLean's fetchInventory. */
   private int fetchCatLeanBlock(LocalPlayer player) {
      if (this.isCatLeanBlock(player.getOffhandItem())) {
         return -2;
      }
      if (this.isCatLeanBlock(player.getMainHandItem())) {
         return player.getInventory().getSelectedSlot();
      }
      return this.findHotbarBlockSlot(player) >= 0 || this.findInventoryBlockSlot(player) >= 0
         ? player.getInventory().getSelectedSlot()
         : -1;
   }

   private boolean isFullBlock(LocalPlayer player, int slot) {
      ItemStack stack = slot == -2 ? player.getOffhandItem()
         : player.getInventory().getItem(Math.max(0, slot));
      if (stack.getItem() instanceof BlockItem blockItem) {
         return Block.isShapeFullBlock(
            blockItem.getBlock().defaultBlockState().getCollisionShape(player.level(), BlockPos.ZERO));
      }
      return true;
   }

   private boolean isCatLeanBlock(ItemStack stack) {
      return !stack.isEmpty()
         && stack.getItem() instanceof BlockItem blockItem
         && !blockItem.getBlock().defaultBlockState().canBeReplaced()
         && !(blockItem.getBlock() instanceof FallingBlock);
   }

   private record CatLeanTarget(BlockPos support, Direction face) {
   }

   // GrimDelay: buffer incoming packets for up to 500 ms while active
   // (CatLean's grim-block-fly). Position/disconnect packets flush the
   // buffer and always pass through, chat/time/sound/health/bossbar too.

   private record DelayedPacket(Packet<?> packet, long receivedAt) {
   }

   public static boolean handlePacketDelay(Packet<?> packet) {
      ScaffoldModule module = SkycoreClient.modules() == null ? null
         : SkycoreClient.modules().get(ScaffoldModule.class).orElse(null);
      if (module == null || !module.isEnabled() || !module.mode.is("CatLean") || !module.grimDelay.get()) {
         return false;
      }
      if (packet instanceof ClientboundPlayerPositionPacket || packet instanceof ClientboundDisconnectPacket) {
         module.flushDelayedPackets(true);
         return false;
      }
      if (packet instanceof ClientboundSystemChatPacket
         || packet instanceof ClientboundSetTimePacket
         || packet instanceof ClientboundSoundPacket
         || packet instanceof ClientboundStopSoundPacket
         || packet instanceof ClientboundSetActionBarTextPacket
         || packet instanceof ClientboundSetHealthPacket
         || packet instanceof ClientboundBossEventPacket) {
         return false;
      }
      module.delayedPackets.add(new DelayedPacket(packet, System.currentTimeMillis()));
      return true;
   }

   private void flushDelayedPackets(boolean all) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || minecraft.getConnection() == null) {
         if (all) {
            this.delayedPackets.clear();
         }
         return;
      }
      long now = System.currentTimeMillis();
      DelayedPacket delayed;
      while ((delayed = this.delayedPackets.peek()) != null
         && (all || now - delayed.receivedAt() >= 500L)) {
         this.delayedPackets.poll();
         try {
            @SuppressWarnings("unchecked")
            Packet<PacketListener> packet = (Packet<PacketListener>) delayed.packet();
            packet.handle((PacketListener) minecraft.getConnection());
         } catch (Exception ignored) {
            // A stale buffered packet must never crash the client.
         }
      }
   }

   private void onPlayerInput(PlayerInputEvent event) {
      if (!this.isEnabled()) {
         return;
      }
      if (this.mode.is("CatLean")) {
         // Silent CatLean timings turn only the server rotation; keep the
         // world-space walking direction tied to the free camera, exactly as
         // in Eagle's silent mode. VANILLA shows the rotation, so no fix.
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player == null || !this.catLeanRotate.get() || this.grimSilent.get()
            || this.catLeanTiming.is("VANILLA")) {
            return;
         }
         if (RotationManager.INSTANCE.isAimingAtPriority(CATLEAN_ROTATION_PRIORITY)) {
            float viewYaw = RotationController.isActive() && !RotationController.isClientLook()
               ? FreeLookController.getFreeYaw()
               : minecraft.player.getYRot();
            MovementHelper.fixMovement(event, viewYaw);
         }
         return;
      }
      if (!this.autoPlace.get()) {
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
    * Tracks the dominant movement axis. The intent is read from the movement
    * keys relative to the steering yaw (not from the managed head rotation),
    * so walking straight forward also builds. Hysteresis keeps the axis from
    * flapping while the player moves diagonally.
    */
   private void updateBridgeAxis(LocalPlayer player) {
      double[] intent = this.movementIntent(player);
      if (intent == null) {
         this.bridgeXAxis = null;
         return;
      }

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

   /** World-space horizontal direction the player wants to move in, or null. */
   private double[] movementIntent(LocalPlayer player) {
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

      Vec3 motion = player.getDeltaMovement();
      if (motion.x * motion.x + motion.z * motion.z > 0.0025) {
         return new double[]{motion.x, motion.z};
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
      BlockPos targetPos = BlockPos.containing(player.getX(), player.getY() - 1.0, player.getZ());
      if (!minecraft.level.getBlockState(targetPos).canBeReplaced()) {
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
