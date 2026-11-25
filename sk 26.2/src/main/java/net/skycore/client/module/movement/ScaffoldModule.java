package net.skycore.client.module.movement;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
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
 * Scaffold for SkyCore.
 *
 * <p>Modes: {@code Eagle} (edge protection with optional AutoPlace) and
 * {@code Telly} (bridge scaffold, formerly the SEQUENTIAL timing of the
 * CatLean port).
 *
 * <p>Telly places "legit": for every placement the aim point on the support face
 * is chosen, the rotation towards it is GCD-quantized, a real ray is cast along
 * that rotation, and the ray hit (not a synthetic point) is sent as the click
 * position. The angles are put on the wire with an explicit packet right before
 * the use packet, and the camera angles are restored right after it, so the
 * server never keeps a placement rotation and no movement fix is needed.
 *
 * <p>Eagle AutoPlace: while the player moves, placement is locked to the dominant
 * movement axis (X or Z); the support is searched behind the player and the yaw is
 * snapped to the cardinal direction opposite to the movement. {@code ClientLock}
 * chooses whether the managed rotation is drawn on the player's own screen.
 */
public final class ScaffoldModule extends ClientModule {
   private static final int ROTATION_PRIORITY = 40;
   private static final int ROTATION_TIMEOUT_TICKS = 2;
   private static final double MAX_REACH = 4.5;
   private static final int TELLY_NO_VALUE = -999;
   private static final int TELLY_AIM_ATTEMPTS = 4;

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Eagle", "Eagle", "Telly"));
   private final BooleanSetting autoPlace = this.add(new BooleanSetting("AutoPlace", false));
   private final NumberSetting cps = this.add(new NumberSetting("CPS", 10, 1, 20, 1));
   private final ModeSetting autoSwitch = this.add(new ModeSetting(
      "AutoSwitch", "Silent", "Normal", "Silent", "Inventory", "None"));
   private final ModeSetting rotate = this.add(new ModeSetting(
      "Rotate", "Normal", "Normal", "Smooth", "Linear", "None"));
   private final BooleanSetting clientLock = this.add(new BooleanSetting("ClientLock", true));

   // Telly settings.
   private final BooleanSetting safeWalk = this.add(new BooleanSetting("SafeWalk", false));
   private final BooleanSetting allowShift = this.add(new BooleanSetting("AllowShift", false));
   private final BooleanSetting legit = this.add(new BooleanSetting("Legit", true));
   private final BooleanSetting tellySetting = this.add(new BooleanSetting("Telly", false));
   private final BooleanSetting keepHeight = this.add(new BooleanSetting("KeepHeight", false));

   private boolean wasSneaking;
   private long nextPlaceNanos;
   private Subscription tickSubscription;
   private Subscription inputSubscription;
   /** Locked bridge axis: {@code Boolean.TRUE} = X, {@code Boolean.FALSE} = Z, null = free. */
   private Boolean bridgeXAxis;
   private int bridgeMoveSign = 1;
   private boolean moveFixActive;
   private float moveFixViewYaw;

   private static final Direction[] TELLY_FACES = {
      Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};

   /** Candidate target cells around the cell under the player, in priority order. */
   private static final BlockPos[] TELLY_OFFSETS = {
      new BlockPos(0, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0),
      new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(-2, 0, 0),
      new BlockPos(2, 0, 0), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2),
      new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(-1, -1, 0),
      new BlockPos(0, -1, 1), new BlockPos(0, -1, -1)
   };

   /** The fixed bridge Y (height of the last placed block), -999 while unset. */
   private int tellyValue = TELLY_NO_VALUE;
   /** SafeWalk currently holds shift for the player. */
   private boolean tellyShiftFlag;
   /** Fall distance accumulated while airborne. */
   private double tellyFallDistance;
   /** Container slot swapped in by the last Inventory fetch, for the swap back. */
   private int tellySwapSlot = -1;

   public ScaffoldModule() {
      super("Scaffold", "Eagle edge protection with optional automatic placement", ModuleCategory.MOVEMENT);
      this.cps.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle"));
      this.autoSwitch.visibleWhen(() -> this.autoPlace.get() || this.mode.is("Telly"));
      this.rotate.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle"));
      this.clientLock.visibleWhen(() -> this.autoPlace.get() && this.mode.is("Eagle") && !this.rotate.is("None"));
      this.safeWalk.visibleWhen(() -> this.mode.is("Telly"));
      this.allowShift.visibleWhen(() -> this.mode.is("Telly") && !this.safeWalk.get());
      this.legit.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySetting.visibleWhen(() -> this.mode.is("Telly"));
      this.keepHeight.visibleWhen(() -> this.mode.is("Telly"));
   }

   @Override
   protected void onEnable() {
      this.wasSneaking = false;
      this.nextPlaceNanos = 0L;
      this.bridgeXAxis = null;
      this.moveFixActive = false;
      this.tellyValue = TELLY_NO_VALUE;
      this.tellySwapSlot = -1;
      this.tellyShiftFlag = false;
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
      if (this.wasSneaking && minecraft != null && !this.isPhysicalShiftDown(minecraft)) {
         minecraft.options.keyShift.setDown(false);
      }
      this.wasSneaking = false;
      this.stopManagedRotation();
      this.releaseTellyShift(minecraft);
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
      if (this.mode.is("Telly")) {
         if (this.wasSneaking) {
            if (!this.isPhysicalShiftDown(minecraft)) {
               minecraft.options.keyShift.setDown(false);
            }
            this.wasSneaking = false;
         }
         this.releaseManagedRotation();
         if (!this.canAutoPlace(minecraft)) {
            return;
         }
         this.onTellyTick(minecraft);
         return;
      }
      if (this.tellyShiftFlag) {
         this.releaseTellyShift(minecraft);
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
      // Building happens against the server rotation, so an active sprint is
      // dropped here as well as in the input event; it must not send packets.
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
   // Telly
   // ------------------------------------------------------------------

   private record TellyPlan(BlockHitResult hit, Rotation rotation) {
   }

   private void onTellyTick(Minecraft minecraft) {
      this.updateTellyFallDistance(minecraft.player);
      TellyPlan plan = this.findTellyPlacement(minecraft);
      if (plan != null) {
         this.placeTelly(minecraft, plan);
      }
   }

   /**
    * Gates (jump/telly, shift, block availability), the target cell, SafeWalk
    * and the Legit collision check; then a ray-validated placement plan.
    * Returns {@code null} when this tick has nothing to place.
    */
   private TellyPlan findTellyPlacement(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      if (minecraft.options.keyJump.isDown() && this.tellyFallDistance <= 0.0 && this.tellySetting.get()) {
         return null;
      }
      if (!(this.safeWalk.get() || !player.isShiftKeyDown() || this.allowShift.get())
         || this.fetchTellyInventory(minecraft, false) == -1) {
         return null;
      }
      if (minecraft.options.keyJump.isDown() && !this.hasMovementInput(player)) {
         this.tellyValue = (int) Math.floor(player.getY() - 1.0);
      }
      if (player.getY() < this.tellyValue) {
         this.tellyValue = TELLY_NO_VALUE;
      }
      double baseY = this.checkFullBlock(player) ? player.getY() : Math.ceil(player.getY());
      BlockPos containing = (this.keepHeight.get() || this.tellySetting.get()) && this.tellyValue != TELLY_NO_VALUE
         ? BlockPos.containing(player.getX(), this.tellyValue, player.getZ())
         : BlockPos.containing(player.getX(), baseY, player.getZ()).below();
      if (this.safeWalk.get() && this.hasMovementInput(player)) {
         boolean cellEmpty = minecraft.level.getBlockState(containing).canBeReplaced();
         if (!player.isShiftKeyDown() && cellEmpty) {
            minecraft.options.keyShift.setDown(true);
            this.tellyShiftFlag = true;
         } else if (player.isShiftKeyDown() && !cellEmpty && this.tellyShiftFlag
            && !this.isPhysicalShiftDown(minecraft)) {
            minecraft.options.keyShift.setDown(false);
            this.tellyShiftFlag = false;
         }
      }
      // Legit: only place once the centre of the hitbox has left the support.
      if (this.legit.get() && minecraft.level.getBlockCollisions(player,
            player.getBoundingBox().inflate(-0.25, 0.0, -0.25).move(0.0, -0.1, 0.0))
         .iterator().hasNext()) {
         return null;
      }
      return this.findTellyPlan(minecraft, containing);
   }

   /**
    * First (cell, support face) combination that can really be placed: the cell is
    * replaceable and free of the player, the support is solid, and a ray along the
    * computed rotation hits exactly that support face within reach.
    */
   private TellyPlan findTellyPlan(Minecraft minecraft, BlockPos containing) {
      LocalPlayer player = minecraft.player;
      if (!minecraft.level.getBlockState(containing).canBeReplaced()) {
         return null;
      }
      for (BlockPos offset : TELLY_OFFSETS) {
         BlockPos cell = containing.offset(offset);
         if (!minecraft.level.getBlockState(cell).canBeReplaced()
            || player.getBoundingBox().intersects(new AABB(cell))) {
            continue;
         }
         for (Direction face : TELLY_FACES) {
            BlockPos support = cell.relative(face.getOpposite());
            if (minecraft.level.getBlockState(support).canBeReplaced()) {
               continue;
            }
            TellyPlan plan = this.planTellyHit(minecraft, support, face);
            if (plan != null) {
               return plan;
            }
         }
      }
      return null;
   }

   /** Aim at a point on the face, verify with a real ray, use the ray hit as click point. */
   private TellyPlan planTellyHit(Minecraft minecraft, BlockPos support, Direction face) {
      LocalPlayer player = minecraft.player;
      Vec3 eye = player.getEyePosition();
      double reach = Math.min(MAX_REACH, player.blockInteractionRange());
      for (int attempt = 0; attempt < TELLY_AIM_ATTEMPTS; attempt++) {
         Vec3 aim = this.facePoint(support, face, attempt == TELLY_AIM_ATTEMPTS - 1);
         if (eye.distanceToSqr(aim) > reach * reach) {
            return null;
         }
         Rotation rotation = this.computeTellyRotation(player, eye, aim);
         Vec3 look = this.viewVector(rotation);
         BlockHitResult ray = minecraft.level.clip(new ClipContext(
            eye, eye.add(look.scale(reach)),
            ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
         if (ray.getType() == HitResult.Type.BLOCK
            && ray.getBlockPos().equals(support)
            && ray.getDirection() == face) {
            return new TellyPlan(new BlockHitResult(ray.getLocation(), face, support, false), rotation);
         }
      }
      return null;
   }

   /**
    * A point on the given face of the support block: the face centre when
    * {@code centre}, otherwise a random point inside the middle of the face.
    */
   private Vec3 facePoint(BlockPos support, Direction face, boolean centre) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      double jitter = centre ? 0.5 : random.nextDouble(0.15, 0.85);
      double x = support.getX() + (face == Direction.WEST ? 0.0 : face == Direction.EAST ? 1.0 : jitter);
      double y = support.getY() + (face == Direction.DOWN ? 0.0 : face == Direction.UP ? 1.0 : jitter);
      double z = support.getZ() + (face == Direction.NORTH ? 0.0 : face == Direction.SOUTH ? 1.0 : jitter);
      return new Vec3(x, y, z);
   }

   /**
    * Sends the placement angles, performs the use, switches back and restores
    * the camera angles. The block is put into the hand first, so the carried-item
    * packet can never come after the use packet.
    */
   private void placeTelly(Minecraft minecraft, TellyPlan plan) {
      LocalPlayer player = minecraft.player;
      int inventory = this.fetchTellyInventory(minecraft, true);
      if (inventory == -1) {
         return;
      }
      BlockHitResult hit = plan.hit();
      BlockPos placed = hit.getBlockPos().relative(hit.getDirection());
      this.sendPosRot(player, plan.rotation().getYaw(), plan.rotation().getPitch());

      Block supportBlock = minecraft.level.getBlockState(hit.getBlockPos()).getBlock();
      boolean interactive = this.testInteractiveBlock(supportBlock) && !player.isShiftKeyDown();
      if (interactive) {
         this.sendSneakInput(minecraft, true);
      }
      InteractionHand hand = inventory == -2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      var result = minecraft.gameMode.useItemOn(player, hand, hit);
      if (result != null && result.consumesAction()) {
         player.swing(hand);
         this.tellyValue = placed.getY();
      }
      if (interactive) {
         this.sendSneakInput(minecraft, false);
      }
      this.applyTellyGetInventory(minecraft, inventory);
      // The server must not keep the placement rotation: vanilla's own movement
      // packet would otherwise carry no rotation change.
      this.sendPosRot(player, player.getYRot(), player.getXRot());
   }

   /**
    * -2 = offhand, otherwise the slot to restore afterwards (the selected slot,
    * or the block slot itself for Normal), or -1 when no block is available for
    * the current switch mode. Hotbar selection is local only: vanilla sends the
    * carried-item packet itself before the use, and the restore on its next tick.
    */
   private int fetchTellyInventory(Minecraft minecraft, boolean forUse) {
      LocalPlayer player = minecraft.player;
      if (this.isTellyBlock(player.getOffhandItem())) {
         return -2;
      }
      int selected = player.getInventory().getSelectedSlot();
      if (this.isTellyBlock(player.getMainHandItem())) {
         return selected;
      }
      if (this.autoSwitch.is("None")) {
         return -1;
      }
      int hotbarSlot = this.findTellyHotbarSlot(player);
      // A foreign container menu (a shop-GUI closer) makes a container swap
      // impossible; the hotbar path works with any menu state.
      boolean inventoryMode = this.autoSwitch.is("Inventory") && this.hasCarriedMenu(player);
      int inventorySlot = inventoryMode ? this.findTellyInventorySlot(player) : -1;
      if (!forUse) {
         return (inventoryMode ? inventorySlot : hotbarSlot) >= 0 ? selected : -1;
      }
      if (inventoryMode) {
         if (inventorySlot < 0) {
            return -1;
         }
         this.swapContainerSlot(minecraft, inventorySlot, selected);
         this.tellySwapSlot = inventorySlot;
         return selected;
      }
      if (hotbarSlot < 0) {
         return -1;
      }
      player.getInventory().setSelectedSlot(hotbarSlot);
      return this.autoSwitch.is("Normal") ? hotbarSlot : selected;
   }

   /** The swap back: container swap, or local slot restore (a no-op for Normal). */
   private void applyTellyGetInventory(Minecraft minecraft, int inventory) {
      if (inventory < 0) {
         return;
      }
      LocalPlayer player = minecraft.player;
      if (this.tellySwapSlot >= 0) {
         this.swapContainerSlot(minecraft, this.tellySwapSlot, player.getInventory().getSelectedSlot());
         this.tellySwapSlot = -1;
      } else {
         player.getInventory().setSelectedSlot(inventory);
      }
   }

   private int findTellyHotbarSlot(LocalPlayer player) {
      for (int slot = 0; slot < 9; slot++) {
         if (this.isTellyBlock(player.getInventory().getItem(slot))) {
            return slot;
         }
      }
      return -1;
   }

   /** Container slot id of the first block in the inventory (hotbar indices +36). */
   private int findTellyInventorySlot(LocalPlayer player) {
      for (int i = 35; i >= 0; i--) {
         if (this.isTellyBlock(player.getInventory().getItem(i))) {
            return i < 9 ? i + 36 : i;
         }
      }
      return -1;
   }

   private void swapContainerSlot(Minecraft minecraft, int slot, int button) {
      LocalPlayer player = minecraft.player;
      if (!this.hasCarriedMenu(player)) {
         return;
      }
      minecraft.gameMode.handleContainerInput(
         player.containerMenu.containerId, slot, button, ContainerInput.SWAP, player);
      this.sendPacket(player, new ServerboundContainerClosePacket(player.containerMenu.containerId));
   }

   /** The stack that will be placed, mirroring {@link #fetchTellyInventory}. */
   private ItemStack tellyStack(LocalPlayer player) {
      if (this.isTellyBlock(player.getOffhandItem())) {
         return player.getOffhandItem();
      }
      if (this.isTellyBlock(player.getMainHandItem())) {
         return player.getMainHandItem();
      }
      for (int i = 0; i < 9; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (this.isTellyBlock(stack)) {
            return stack;
         }
      }
      if (this.autoSwitch.is("Inventory")) {
         for (int i = 9; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (this.isTellyBlock(stack)) {
               return stack;
            }
         }
      }
      return ItemStack.EMPTY;
   }

   /** Full-block shape check: slabs and the like place one level higher. */
   private boolean checkFullBlock(LocalPlayer player) {
      if (this.tellyStack(player).getItem() instanceof BlockItem blockItem) {
         return Block.isShapeFullBlock(
            blockItem.getBlock().defaultBlockState().getCollisionShape(player.level(), BlockPos.ZERO));
      }
      return true;
   }

   private boolean isTellyBlock(ItemStack stack) {
      return !stack.isEmpty()
         && stack.getItem() instanceof BlockItem blockItem
         && !blockItem.getBlock().defaultBlockState().canBeReplaced()
         && !(blockItem.getBlock() instanceof FallingBlock);
   }

   private boolean testInteractiveBlock(Block block) {
      return block instanceof BaseEntityBlock
         || block == Blocks.CRAFTING_TABLE
         || block instanceof TrapDoorBlock
         || block instanceof LeverBlock
         || block instanceof ButtonBlock
         || block instanceof DoorBlock;
   }

   private boolean hasMovementInput(LocalPlayer player) {
      return player.input != null
         && (player.input.getMoveVector().y != 0.0F || player.input.getMoveVector().x != 0.0F);
   }

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
    * Absolute angles from the eye towards the target, GCD-quantized against the
    * camera rotation (what the server holds after the restore packet).
    */
   private Rotation computeTellyRotation(LocalPlayer player, Vec3 eye, Vec3 target) {
      float baseYaw = player.getYRot();
      float basePitch = player.getXRot();
      double dx = target.x - eye.x;
      double dz = target.z - eye.z;
      float yaw = (float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float) -Math.toDegrees(Math.atan2(target.y - eye.y, Math.hypot(dx, dz)));
      float gcd = MouseSensitivity.getGCDValue();
      if (gcd != 0.0F) {
         yaw = baseYaw + Math.round(Mth.wrapDegrees(yaw - baseYaw) / gcd) * gcd;
         pitch = basePitch + Math.round((pitch - basePitch) / gcd) * gcd;
      }
      return new Rotation(yaw, Mth.clamp(pitch, -90.0F, 90.0F));
   }

   /** Same formula as the vanilla view vector. */
   private Vec3 viewVector(Rotation rotation) {
      return Vec3.directionFromRotation(rotation.getPitch(), rotation.getYaw());
   }

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

   private boolean isPhysicalShiftDown(Minecraft minecraft) {
      return org.lwjgl.glfw.GLFW.glfwGetKey(minecraft.getWindow().handle(),
         org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
   }

   /** Releases a SafeWalk-held shift unless the player physically holds it. */
   private void releaseTellyShift(Minecraft minecraft) {
      if (this.tellyShiftFlag && minecraft != null && !this.isPhysicalShiftDown(minecraft)) {
         minecraft.options.keyShift.setDown(false);
      }
      this.tellyShiftFlag = false;
   }

   /**
    * Kept only so an existing mixin that calls it still compiles: the GrimBlockFly
    * packet delay was removed, no packet is ever buffered.
    */
   public static boolean handlePacketDelay(Packet<?> packet) {
      return false;
   }

   private void onPlayerInput(PlayerInputEvent event) {
      if (!this.isEnabled()) {
         return;
      }
      // Telly puts the angles on the wire with explicit packets and holds no
      // managed rotation, so no movement remap is needed.
      if (this.mode.is("Telly")) {
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
      // Sprint is ignored while AutoPlace manages a rotation.
      event.setSprint(false);
   }

   // ------------------------------------------------------------------
   // Eagle / AutoPlace
   // ------------------------------------------------------------------

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
      boolean shouldSneak = this.isPhysicalShiftDown(minecraft)
         || minecraft.level.getBlockState(checkPos).isAir();
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

   /**
    * True while the player carries their own inventory menu. A module that closes
    * a container screen without telling the server leaves {@code containerMenu} on
    * a foreign menu; a container swap would then be rejected by the server.
    */
   private boolean hasCarriedMenu(LocalPlayer player) {
      return player.containerMenu == player.inventoryMenu;
   }

   private long placeIntervalNanos() {
      int blocksPerSecond = Math.max(1, this.cps.get().intValue());
      return 1_000_000_000L / blocksPerSecond;
   }

   /**
    * Tracks the dominant movement axis. The intent is read from the movement
    * keys relative to the steering yaw (not from the managed head rotation).
    * Hysteresis keeps the axis from flapping while moving diagonally.
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
      // While moving, only the support behind the player along the locked axis
      // (then the support below) may be used, never a side face.
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
      // ClientLock on = rotation is shown on the player's screen,
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
      // Linear advances by a fixed angular amount per client tick.
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
      if (inventorySlot < 0 || !this.hasCarriedMenu(minecraft.player)) {
         // No inventory block, or a foreign menu is carried: use the hotbar path.
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
