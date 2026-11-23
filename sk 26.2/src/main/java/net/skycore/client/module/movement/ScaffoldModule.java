package net.skycore.client.module.movement;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.FreeLookController;
import net.skycore.client.combat.MouseSensitivity;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.mixin.ClientLevelGetDestroyingBlocksAccessor;
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
 * <p>Modes: {@code Eagle} (default) and {@code Telly} — a direct port of the
 * CatLean Scaffold module ({@code su.catlean.client.features.modules.rage.Scaffold}),
 * see the Telly section below. AutoPlace is an independent Eagle opt-in
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

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Eagle", "Eagle", "Telly"));
   private final BooleanSetting autoPlace = this.add(new BooleanSetting("AutoPlace", false));
   private final NumberSetting cps = this.add(new NumberSetting("CPS", 10, 1, 20, 1));
   private final ModeSetting autoSwitch = this.add(new ModeSetting(
      "AutoSwitch", "Silent", "Normal", "Silent", "Inventory", "None"));
   private final ModeSetting rotate = this.add(new ModeSetting(
      "Rotate", "Normal", "Normal", "Smooth", "Linear", "None"));
   private final BooleanSetting clientLock = this.add(new BooleanSetting("ClientLock", true));

   // Telly mode settings — names and defaults of the CatLean Scaffold settings
   // (timing, tower, safe-walk, allow-shift, legit, save-angles, telly, clamp,
   // air-place, grim-block-fly, keep-height, rotate, grim-silent). The block
   // switch reuses the AutoSwitch setting above (CatLean's "switch" enum has
   // the same Normal/Silent/Inventory/None values).
   private final ModeSetting tellyTiming = this.add(new ModeSetting("Timing", "GRIM", "SEQUENTIAL", "VANILLA", "GRIM"));
   private final BooleanSetting tower = this.add(new BooleanSetting("Tower", true));
   private final BooleanSetting safeWalk = this.add(new BooleanSetting("SafeWalk", false));
   private final BooleanSetting allowShift = this.add(new BooleanSetting("AllowShift", false));
   private final BooleanSetting legit = this.add(new BooleanSetting("Legit", true));
   private final BooleanSetting saveAngles = this.add(new BooleanSetting("SaveAngles", true));
   private final BooleanSetting tellySetting = this.add(new BooleanSetting("Telly", false));
   private final BooleanSetting clampSetting = this.add(new BooleanSetting("Clamp", true));
   private final BooleanSetting airPlace = this.add(new BooleanSetting("AirPlace", false));
   private final BooleanSetting grimBlockFly = this.add(new BooleanSetting("GrimBlockFly", false));
   private final BooleanSetting keepHeight = this.add(new BooleanSetting("KeepHeight", false));
   private final BooleanSetting tellyRotate = this.add(new BooleanSetting("CatLeanRotate", true));
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

   private static final int TELLY_ROTATION_PRIORITY = 41;
   private static final int TELLY_NO_VALUE = -999;
   /** CatLean's static blockPoss table, same order. */
   private static final BlockPos[] TELLY_OFFSETS = {
      new BlockPos(0, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0),
      new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(-2, 0, 0),
      new BlockPos(2, 0, 0), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2),
      new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0),
      new BlockPos(0, -1, 1), new BlockPos(0, -1, -1)
   };
   /** CatLean's static "value": the fixed bridge Y, -999 while unset. */
   private int tellyValue = TELLY_NO_VALUE;
   /** CatLean's static "rotation": last computed bridge rotation. */
   private Rotation tellyRotation;
   /** CatLean's static "flag": SafeWalk currently holds shift for the player. */
   private boolean tellyShiftFlag;
   /** CatLean's WorldUtil.amount3: fall distance accumulated while airborne. */
   private double tellyFallDistance;
   /** CatLean StopWatch backing the tower dip (1500 ms). */
   private long tellyTowerWatchMillis = System.currentTimeMillis();
   /** Container slot swapped in by the last Inventory fetch, for the swap back. */
   private int tellySwapSlot = -1;
   /** Hit whose placement a GRIM (silent) action deferred to the next tick. */
   private BlockHitResult tellyPendingHit;
   private final java.util.concurrent.ConcurrentLinkedQueue<DelayedPacket> delayedPackets =
      new java.util.concurrent.ConcurrentLinkedQueue<>();

   public ScaffoldModule() {
      super("Scaffold", "Eagle edge protection with optional automatic placement", ModuleCategory.MOVEMENT);
      this.cps.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle"));
      this.autoSwitch.visibleWhen(() -> this.autoPlace.get() || this.mode.is("Telly"));
      this.rotate.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle"));
      this.clientLock.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle") && !this.rotate.is("None"));
      this.tellyTiming.visibleWhen(() -> this.mode.is("Telly"));
      this.tower.visibleWhen(() -> this.mode.is("Telly"));
      this.safeWalk.visibleWhen(() -> this.mode.is("Telly"));
      this.allowShift.visibleWhen(() -> this.mode.is("Telly") && !this.safeWalk.get());
      this.legit.visibleWhen(() -> this.mode.is("Telly"));
      this.saveAngles.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySetting.visibleWhen(() -> this.mode.is("Telly"));
      this.clampSetting.visibleWhen(() -> this.mode.is("Telly"));
      this.airPlace.visibleWhen(() -> this.mode.is("Telly"));
      this.grimBlockFly.visibleWhen(() -> this.mode.is("Telly"));
      this.keepHeight.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyRotate.visibleWhen(() -> this.mode.is("Telly"));
      this.grimSilent.visibleWhen(() -> this.mode.is("Telly"));
   }

   @Override
   protected void onEnable() {
      this.wasSneaking = false;
      this.nextPlaceNanos = 0L;
      this.bridgeXAxis = null;
      this.moveFixActive = false;
      // CatLean update2(): only the fixed bridge Y resets on enable.
      this.tellyValue = TELLY_NO_VALUE;
      this.tellyPendingHit = null;
      this.tellySwapSlot = -1;
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
      if (RotationManager.INSTANCE.isAimingAtPriority(TELLY_ROTATION_PRIORITY)) {
         RotationManager.INSTANCE.stopRotationIfPriority(TELLY_ROTATION_PRIORITY);
      }
      // CatLean update4(): release a SafeWalk-held shift unless the player
      // physically holds left shift (GLFW 340), then flush delayed packets.
      if (this.tellyShiftFlag && minecraft != null
         && !org.lwjgl.glfw.GLFW.glfwGetKey(
            minecraft.getWindow().handle(), org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT)) {
         minecraft.options.keyShift.setDown(false);
      }
      this.tellyShiftFlag = false;
      this.tellyPendingHit = null;
      this.flushDelayedPackets(true);
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
         // CatLean drops its buffered packets while no world is loaded.
         this.delayedPackets.clear();
         return;
      }

      if (this.mode.is("Telly")) {
         if (this.wasSneaking) {
            minecraft.options.keyShift.setDown(false);
            this.wasSneaking = false;
         }
         this.onTellyTick(minecraft);
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
   // Telly mode: a direct port of the CatLean Scaffold module
   // (su.catlean.client.features.modules.rage.Scaffold in ctln/, deobf by
   // pusia), transliterated method by method: applyGetY -> onTellyUpdate,
   // applyGetBlockPos -> applyTellyBlockPos, fetchInventory,
   // applyGetInventory, computeBlockPosWithFacing, checkGetItem and the
   // grim-block-fly packet delay. Only the service boundaries differ:
   //  - CatLean's RotationManager actions become SkyCore
   //    RotationManager.updateExact submits. CatLean fires the placement
   //    callback in PostSync (SEQUENTIAL), during the player update
   //    (VANILLA/Look) or on the next player update (GRIM/Silent); the
   //    first two therefore place right after the submit, GRIM defers
   //    the hit to the next tick (tellyPendingHit). Rotations are GCD
   //    quantized against the last server rotation, as in CatLean's
   //    Rotation.getRotation().
   //  - WorldUtil.getAmount3 (accumulated fall distance) is tracked
   //    locally from the vertical velocity (CatLean uses its MoveEvent),
   //    WorldUtil.isFlag4 is the movement-input check and getAmount4/5
   //    are RotationController.getServerYaw/getServerPitch.
   //  - SkyCore has no AirPlace module: the AirPlace setting stands in
   //    for "AirPlace module enabled && its 2b2t bypass", and the bypass
   //    packet sequence from CatLean's applyGetBlockPos runs inline.
   //  - CatLean's decompiled container swaps pass button -1; the
   //    vanilla-correct selected-slot button performs the same swap.
   // ------------------------------------------------------------------

   private void onTellyTick(Minecraft minecraft) {
      // CatLean's RotationManager invokes a deferred SilentRotationAction
      // callback at the start of the player update, before Scaffold's own
      // handler: a GRIM placement queued last tick lands first.
      if (this.tellyPendingHit != null) {
         BlockHitResult pending = this.tellyPendingHit;
         this.tellyPendingHit = null;
         this.applyTellyBlockPos(minecraft, pending);
      }
      this.onTellyUpdate(minecraft);
      // CatLean onPostTasksProcess: release buffered packets past 500 ms.
      this.flushDelayedPackets(false);
   }

   /** Transliteration of CatLean's applyGetY (its PlayerUpdateEvent handler). */
   private void onTellyUpdate(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      this.updateTellyFallDistance(player);
      if (minecraft.options.keyJump.isDown() && this.tellyFallDistance <= 0.0 && this.tellySetting.get()) {
         return;
      }

      int inventory = this.fetchTellyInventory(minecraft, false);
      if (!(this.safeWalk.get() || !player.isShiftKeyDown() || this.allowShift.get()) || inventory == -1) {
         return;
      }

      if (minecraft.options.keyJump.isDown() && !this.hasMovementInput(player)) {
         this.tellyValue = (int) Math.floor(player.getY() - 1.0);
      }
      if (player.getY() < this.tellyValue) {
         this.tellyValue = TELLY_NO_VALUE;
      }

      boolean fullBlock = this.checkFullBlock(player, inventory);
      double baseY = fullBlock ? player.getY() : Math.ceil(player.getY());
      BlockPos containing = (this.keepHeight.get() || this.tellySetting.get()) && this.tellyValue != TELLY_NO_VALUE
         ? BlockPos.containing(player.getX(), this.tellyValue, player.getZ())
         : BlockPos.containing(player.getX(), baseY, player.getZ()).below();
      BlockPosWithFacing blockPosWithFacing = this.airPlace.get()
         ? new BlockPosWithFacing(containing, Direction.DOWN)
         : this.computeTellyBlockPosWithFacing(minecraft, containing);

      if (this.safeWalk.get() && this.hasMovementInput(player)) {
         if (!player.isShiftKeyDown() && minecraft.level.getBlockState(containing).canBeReplaced()) {
            minecraft.options.keyShift.setDown(true);
            this.tellyShiftFlag = true;
         }
         if (player.isShiftKeyDown() && !minecraft.level.getBlockState(containing).canBeReplaced()) {
            minecraft.options.keyShift.setDown(false);
            this.tellyShiftFlag = false;
         }
      }

      double shrink = this.tellyTiming.is("VANILLA") ? -0.1 : -0.25;
      if (blockPosWithFacing == null
         || this.legit.get() && minecraft.level.getBlockCollisions(player,
               player.getBoundingBox().inflate(shrink, 0.0, shrink).move(0.0, -0.1, 0.0))
            .iterator().hasNext()) {
         if (this.tellyRotation != null && this.saveAngles.get()) {
            Rotation held = this.tellyRotate.get() && !this.grimSilent.get()
               ? this.tellyRotation
               : new Rotation(player.getYRot(), player.getXRot());
            RotationManager.updateExact(held, TELLY_ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS,
               this.tellyTiming.is("VANILLA"));
         }
         return;
      }

      BlockPos position = blockPosWithFacing.position();
      Direction facing = blockPosWithFacing.facing();
      BlockHitResult hitResult = new BlockHitResult(
         new Vec3(
            position.getX() + 0.5 + facing.getStepX() * 0.5,
            position.getY() + 0.5 + facing.getStepY() * 0.5,
            position.getZ() + 0.5 + facing.getStepZ() * 0.5),
         facing, position, false);
      double hitYOffset = player.getY() - Math.floor(player.getY()) == 0.5
         ? 0.39
         : 0.82 + ThreadLocalRandom.current().nextDouble(0.0, 0.16);
      double hitX = this.clampSetting.get()
         ? Mth.clamp(player.getX() + ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
            hitResult.getBlockPos().getX(), hitResult.getBlockPos().getX() + 1.0)
         : hitResult.getLocation().x;
      double hitZ = this.clampSetting.get()
         ? Mth.clamp(player.getZ() + ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
            hitResult.getBlockPos().getZ(), hitResult.getBlockPos().getZ() + 1.0)
         : hitResult.getLocation().z;
      hitResult = new BlockHitResult(new Vec3(hitX, position.getY() + hitYOffset, hitZ), facing, position, false);

      Rotation rotation = this.computeTellyRotation(player, hitResult.getLocation());
      if (this.tellyRotate.get() && this.grimSilent.get()) {
         this.sendPosRot(player, rotation.getYaw(), rotation.getPitch());
      }

      boolean submitted = !this.tellyRotate.get() || this.grimSilent.get()
         || RotationManager.updateExact(rotation, TELLY_ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS,
            this.tellyTiming.is("VANILLA"));
      if (submitted) {
         // CatLean queues a RotationAction whose callback is
         // applyGetBlockPos: SEQUENTIAL and VANILLA fire it this tick,
         // GRIM (SilentRotationAction) on the next player update.
         if (this.tellyTiming.is("GRIM")) {
            this.tellyPendingHit = hitResult;
         } else {
            this.applyTellyBlockPos(minecraft, hitResult);
         }
      }
      this.tellyRotation = rotation;
   }

   /** Transliteration of CatLean's applyGetBlockPos (the rotation callback). */
   private void applyTellyBlockPos(Minecraft minecraft, BlockHitResult hitResult) {
      LocalPlayer player = minecraft.player;
      if (minecraft.options.keyJump.isDown() && this.tellyFallDistance <= 0.0 && this.tellySetting.get()) {
         return;
      }
      int inventory = this.fetchTellyInventory(minecraft, true);
      if (inventory == -1) {
         return;
      }

      if (minecraft.options.keyJump.isDown() && !this.hasMovementInput(player) && this.tower.get()) {
         long now = System.currentTimeMillis();
         boolean dip = now - this.tellyTowerWatchMillis >= 1500L;
         if (dip) {
            this.tellyTowerWatchMillis = now;
         }
         player.setDeltaMovement(player.getDeltaMovement().x, dip ? -0.28 : 0.42, player.getDeltaMovement().z);
      } else {
         this.tellyTowerWatchMillis = System.currentTimeMillis();
      }

      Block supportBlock = minecraft.level.getBlockState(hitResult.getBlockPos()).getBlock();
      boolean interactive = this.testInteractiveBlock(supportBlock) && !player.isShiftKeyDown();
      if (interactive) {
         this.sendSneakInput(minecraft, true);
      }

      boolean mainHand = inventory != -2;
      InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
      if (this.airPlace.get()) {
         // CatLean also requires its AirPlace module enabled with bypass
         // on; the AirPlace setting covers both here (see header). The
         // vanilla useItem call sends its own use packet first, exactly
         // as ContainerUtil.computeInteractionResult does in CatLean.
         minecraft.gameMode.useItem(player, hand);
         this.sendSequencedPacket(minecraft, sequence -> new ServerboundUseItemPacket(
            hand, sequence, RotationController.getServerYaw(), RotationController.getServerPitch()));
         this.sendPacket(player, new ServerboundPlayerActionPacket(
            ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         this.sendSequencedPacket(minecraft, sequence -> new ServerboundUseItemOnPacket(hand, hitResult, sequence));
         player.swing(hand);
         this.sendPacket(player, new ServerboundPlayerActionPacket(
            ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         player.swing(hand);
      } else {
         int count = player.getMainHandItem().getCount();
         if (this.grimBlockFly.get()) {
            this.sendPacket(player, new ServerboundPlayerActionPacket(
               ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         }
         minecraft.gameMode.useItemOn(player, hand, hitResult);
         this.sendPacket(player, new ServerboundSwingPacket(hand));
         if (this.grimBlockFly.get()) {
            this.sendPacket(player, new ServerboundPlayerActionPacket(
               ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
            player.getMainHandItem().setCount(count);
         }
      }

      this.tellyValue = hitResult.getBlockPos().getY();
      if (interactive) {
         this.sendSneakInput(minecraft, false);
      }
      this.applyTellyGetInventory(minecraft, inventory);
      if (this.tellyRotate.get() && this.grimSilent.get()) {
         this.sendPosRot(player, player.getYRot(), player.getXRot());
      }
   }

   /** Transliteration of CatLean's Scaffold.computeBlockPosWithFacing. */
   private BlockPosWithFacing computeTellyBlockPosWithFacing(Minecraft minecraft, BlockPos blockPos) {
      if (!minecraft.level.getBlockState(blockPos).canBeReplaced()) {
         return null;
      }
      for (BlockPos offset : TELLY_OFFSETS) {
         BlockPosWithFacing found = this.computeTellyFacing(minecraft, blockPos.offset(offset));
         if (found != null) {
            return found;
         }
      }
      return null;
   }

   /**
    * Transliteration of InteractionManager.computeBlockPosWithFacing: the
    * first direction whose neighbour is not replaceable. The original
    * performs no raycast, reach or collision check here.
    */
   private BlockPosWithFacing computeTellyFacing(Minecraft minecraft, BlockPos blockPos) {
      // CatLean's static direction -> offset map, in iteration order.
      Direction[] directions = {
         Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
      BlockPos[] offsets = {
         new BlockPos(0, -1, 0), new BlockPos(0, 1, 0), new BlockPos(-1, 0, 0),
         new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
      for (int i = 0; i < directions.length; i++) {
         BlockPos offset = blockPos.offset(offsets[i]);
         if (!minecraft.level.getBlockState(offset).canBeReplaced()) {
            return new BlockPosWithFacing(offset, directions[i]);
         }
      }
      return null;
   }

   /**
    * Transliteration of CatLean's fetchInventory: -2 = offhand, otherwise
    * the selected slot (with the switch applied when {@code forUse}), or
    * -1 when no valid block is available for the current switch mode.
    */
   private int fetchTellyInventory(Minecraft minecraft, boolean forUse) {
      LocalPlayer player = minecraft.player;
      if (this.isTellyBlock(player.getOffhandItem())) {
         return -2;
      }
      if (this.isTellyBlock(player.getMainHandItem())) {
         return player.getInventory().getSelectedSlot();
      }

      int hotbarSlot = this.findTellyHotbarSlot(player);
      int inventorySlot = this.findTellyInventorySlot(player);
      if (!forUse) {
         return switch (this.autoSwitch.get()) {
            case "Inventory" -> inventorySlot >= 0 ? player.getInventory().getSelectedSlot() : -1;
            case "Normal", "Silent" -> hotbarSlot >= 0 ? player.getInventory().getSelectedSlot() : -1;
            default -> -1;
         };
      }
      return switch (this.autoSwitch.get()) {
         case "Inventory" -> {
            if (inventorySlot < 0) {
               yield -1;
            }
            int selected = player.getInventory().getSelectedSlot();
            this.swapContainerSlot(minecraft, inventorySlot, selected);
            this.tellySwapSlot = inventorySlot;
            yield selected;
         }
         case "Normal", "Silent" -> {
            if (hotbarSlot < 0) {
               yield -1;
            }
            int selected = player.getInventory().getSelectedSlot();
            this.selectSlotSilently(minecraft, hotbarSlot);
            yield selected;
         }
         default -> -1;
      };
   }

   /** Transliteration of CatLean's applyGetInventory (the swap back). */
   private void applyTellyGetInventory(Minecraft minecraft, int inventory) {
      if (inventory == -2 || inventory == -1) {
         return;
      }
      LocalPlayer player = minecraft.player;
      switch (this.autoSwitch.get()) {
         case "Inventory" -> {
            if (this.tellySwapSlot >= 0) {
               this.swapContainerSlot(minecraft, this.tellySwapSlot,
                  player.getInventory().getSelectedSlot());
               this.tellySwapSlot = -1;
            }
         }
         case "Silent" -> this.selectSlotSilently(minecraft, inventory);
         default -> {
         }
      }
   }

   /** CatLean's InventoryManager.fetchInventory: first valid hotbar slot. */
   private int findTellyHotbarSlot(LocalPlayer player) {
      for (int slot = 0; slot < 9; slot++) {
         if (this.isTellyBlock(player.getInventory().getItem(slot))) {
            return slot;
         }
      }
      return -1;
   }

   /**
    * CatLean's InventoryManager.fetchInventory2 as a container slot id:
    * inventory indices 36 down to 0 (index 36 is the boots slot, an
    * original quirk), hotbar indices converted with +36.
    */
   private int findTellyInventorySlot(LocalPlayer player) {
      for (int i = 36; i >= 0; i--) {
         if (this.isTellyBlock(player.getInventory().getItem(i))) {
            return i < 9 ? i + 36 : i;
         }
      }
      return -1;
   }

   /** CatLean's applyGetInventory2: silent slot select (local + packet). */
   private void selectSlotSilently(Minecraft minecraft, int slot) {
      LocalPlayer player = minecraft.player;
      if (slot >= 0 && slot < 9 && player.getInventory().getSelectedSlot() != slot) {
         player.getInventory().setSelectedSlot(slot);
         this.sendPacket(player, new ServerboundSetCarriedItemPacket(slot));
      }
   }

   /** CatLean's ContainerUtil.applyHandleContainerInput with close packet. */
   private void swapContainerSlot(Minecraft minecraft, int slot, int button) {
      LocalPlayer player = minecraft.player;
      minecraft.gameMode.handleContainerInput(
         player.containerMenu.containerId, slot, button, ContainerInput.SWAP, player);
      this.sendPacket(player, new ServerboundContainerClosePacket(player.containerMenu.containerId));
   }

   /** Transliteration of CatLean's checkGetItem(int): full-block shape check. */
   private boolean checkFullBlock(LocalPlayer player, int inventory) {
      ItemStack stack;
      if (inventory == -2) {
         stack = player.getOffhandItem();
      } else if (inventory != -1) {
         stack = player.getInventory().getItem(inventory);
      } else {
         return true;
      }
      if (stack.getItem() instanceof BlockItem blockItem) {
         return Block.isShapeFullBlock(
            blockItem.getBlock().defaultBlockState().getCollisionShape(player.level(), BlockPos.ZERO));
      }
      return true;
   }

   /** Transliteration of CatLean's checkGetItem(ItemStack). */
   private boolean isTellyBlock(ItemStack stack) {
      return !stack.isEmpty()
         && stack.getItem() instanceof BlockItem blockItem
         && !blockItem.getBlock().defaultBlockState().canBeReplaced()
         && !(blockItem.getBlock() instanceof FallingBlock);
   }

   /** Transliteration of InteractionManager.testBlock. */
   private boolean testInteractiveBlock(Block block) {
      return block instanceof BaseEntityBlock
         || block == Blocks.CRAFTING_TABLE
         || block instanceof TrapDoorBlock
         || block instanceof LeverBlock
         || block instanceof ButtonBlock
         || block instanceof DoorBlock;
   }

   /** WorldUtil.isFlag4: the player gives movement input. */
   private boolean hasMovementInput(LocalPlayer player) {
      return player.input != null
         && (player.input.getMoveVector().y != 0.0F || player.input.getMoveVector().x != 0.0F);
   }

   /** WorldUtil.amount3 upkeep: fall distance accumulated while airborne. */
   private void updateTellyFallDistance(LocalPlayer player) {
      if (!player.onGround() && !player.isInLiquid()) {
         double vertical = player.getDeltaMovement().y;
         if (vertical < 0.0) {
            this.tellyFallDistance -= vertical;
         }
      } else {
         this.tellyFallDistance = 0.0;
      }
   }

   /**
    * CatLean's RotationManager.computeRotation plus Rotation.getRotation:
    * absolute angles towards the hit point, GCD-quantized against the
    * last server rotation (WorldUtil.getAmount4/5).
    */
   private Rotation computeTellyRotation(LocalPlayer player, Vec3 target) {
      float lastYaw = RotationController.getServerYaw();
      float lastPitch = RotationController.getServerPitch();
      double dx = target.x - player.getX();
      double dz = target.z - player.getZ();
      float yaw = (float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float) -Math.toDegrees(Math.atan2(
         target.y - (player.getY() + player.getEyeHeight()), Math.hypot(dx, dz)));
      float gcd = MouseSensitivity.getGCDValue();
      if (gcd != 0.0F) {
         yaw = lastYaw + Math.round(Mth.wrapDegrees(yaw - lastYaw) / gcd) * gcd;
         pitch = Mth.clamp(lastPitch + Math.round((pitch - lastPitch) / gcd) * gcd, -90.0F, 90.0F);
      }
      return new Rotation(yaw, pitch);
   }

   /** Transliteration of InputPacketUtil.applyWrite (no ViaFabricPlus branch). */
   private void sendSneakInput(Minecraft minecraft, boolean sneak) {
      LocalPlayer player = minecraft.player;
      Input current = player.input != null ? player.input.keyPresses
         : new Input(false, false, false, false, false, false, false);
      Input forced = new Input(current.forward(), current.backward(), current.left(),
         current.right(), current.jump(), sneak, current.sprint());
      if (player.input != null) {
         player.input.keyPresses = forced;
      }
      this.sendPacket(player, new ServerboundPlayerInputPacket(forced));
      player.setShiftKeyDown(sneak);
      minecraft.options.keyShift.setDown(sneak);
   }

   /** CatLean's PacketUtil.applyCurrentSequence. */
   private void sendSequencedPacket(Minecraft minecraft, java.util.function.IntFunction<Packet<?>> factory) {
      LocalPlayer player = minecraft.player;
      if (player == null || player.connection == null
         || !(minecraft.level instanceof ClientLevelGetDestroyingBlocksAccessor accessor)) {
         return;
      }
      var handler = accessor.skycore$getBlockStatePredictionHandler().startPredicting();
      try {
         this.sendPacket(player, factory.apply(handler.currentSequence()));
      } finally {
         handler.close();
      }
   }

   private void sendPosRot(LocalPlayer player, float yaw, float pitch) {
      this.sendPacket(player, new ServerboundMovePlayerPacket.PosRot(
         player.getX(), player.getY(), player.getZ(), yaw, pitch,
         player.onGround(), player.horizontalCollision));
   }

   private void sendPacket(LocalPlayer player, Packet<?> packet) {
      if (player != null && player.connection != null) {
         player.connection.send(packet);
      }
   }

   private record BlockPosWithFacing(BlockPos position, Direction facing) {
   }

   // GrimBlockFly: CatLean's grim-block-fly packet delay. Incoming
   // packets are buffered for 500 ms (its BTPacket queue); chat, time,
   // sound, action bar, health and boss bar packets pass through, and a
   // position or disconnect packet flushes the buffer immediately. The
   // mixin hook in ConnectionPrepareViaNativeConnectMixin feeds packets
   // into handlePacketDelay because SkyCore's PacketReceiveEvent is only
   // published for a few packet types.

   private record DelayedPacket(Packet<?> packet, long receivedAt) {
   }

   public static boolean handlePacketDelay(Packet<?> packet) {
      ScaffoldModule module = SkycoreClient.modules() == null ? null
         : SkycoreClient.modules().get(ScaffoldModule.class).orElse(null);
      if (module == null || !module.isEnabled() || !module.mode.is("Telly") || !module.grimBlockFly.get()) {
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
      if (this.mode.is("Telly")) {
         // Silent Telly timings turn only the server rotation; keep the
         // world-space walking direction tied to the free camera, exactly as
         // in Eagle's silent mode. VANILLA shows the rotation, so no fix.
         // (CatLean delegates this to its MoveFix module; SkyCore folds the
         // equivalent correction into the module, as in Eagle mode.)
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player == null || !this.tellyRotate.get() || this.grimSilent.get()
            || this.tellyTiming.is("VANILLA")) {
            return;
         }
         if (RotationManager.INSTANCE.isAimingAtPriority(TELLY_ROTATION_PRIORITY)) {
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
