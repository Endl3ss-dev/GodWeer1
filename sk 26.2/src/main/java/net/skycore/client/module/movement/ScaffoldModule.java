package net.skycore.client.module.movement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.LocalPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.InventoryHelper;

/**
 * Block placement module. The placement search and rotation helpers are implemented here;
 * the existing Coach and Telly rehearsal components use the public geometry API below.
 */
public final class ScaffoldModule extends ClientModule {
   private static final int ROTATION_PRIORITY = 5;
   private static final int NO_LOCKED_Y = Integer.MIN_VALUE;

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "NCPStrict",
      "NCP", "NCPStrict", "Eagle", "CatLean", "Telly"));
   private final ModeSetting autoSwitch = this.add(new ModeSetting("Switch", "Silent",
      "Normal", "Silent", "Inventory", "None"));
   private final ModeSetting rotate = this.add(new ModeSetting("Rotate", "Grim+Matrix",
      "Normal", "Smooth", "Packet", "Linear", "Snap", "Grim+Matrix", "None"));

   private final BooleanSetting lockY = this.add(new BooleanSetting("LockY", false));
   private final BooleanSetting autoJump = this.add(new BooleanSetting("AutoJump", false));
   private final BooleanSetting workWhileSneaking = this.add(new BooleanSetting("WorkWhileSneaking", false));
   private final BooleanSetting tower = this.add(new BooleanSetting("Tower", false));

   private final BooleanSetting safeWalk = this.add(new BooleanSetting("SafeWalk", true));
   private final BooleanSetting onlyNotHoldingSpace = this.add(new BooleanSetting("OnlyNotHoldingSpace", false));
   private final ModeSetting ncpRotate = this.add(new ModeSetting("NCP Rotate", "Normal",
      "Linear", "Packet", "Smooth", "Normal"));
   private final NumberSetting smoothSpeed = this.add(new NumberSetting("SmoothSpeed", 15, 5, 30, 1));
   private final BooleanSetting ncpDelay = this.add(new BooleanSetting("PlaceDelay", false));
   private final NumberSetting ncpMinDelay = this.add(new NumberSetting("MinDelay", 50, 0, 300, 10));
   private final NumberSetting ncpMaxDelay = this.add(new NumberSetting("MaxDelay", 100, 50, 500, 10));

   private final NumberSetting lockYDelay = this.add(new NumberSetting("LockYDelay", 0, 0, 500, 10));
   private final BooleanSetting staticPitch = this.add(new BooleanSetting("StaticPitch", true));
   private final NumberSetting fixedPitch = this.add(new NumberSetting("FixedPitch", 80, 75, 85, 1));
   private final NumberSetting grimPitchLimit = this.add(new NumberSetting("GrimPitchLimit", 67, 30, 110, 1));
   private final BooleanSetting randomizeDelay = this.add(new BooleanSetting("RandomizeDelay", true));
   private final NumberSetting minPlaceDelay = this.add(new NumberSetting("MinPlaceDelay", 50, 0, 300, 10));
   private final NumberSetting maxPlaceDelay = this.add(new NumberSetting("MaxPlaceDelay", 100, 50, 500, 10));
   private final BooleanSetting buildTrust = this.add(new BooleanSetting("BuildTrust", true));
   private final NumberSetting trustBlocks = this.add(new NumberSetting("TrustBlocks", 3, 1, 10, 1));
   private final BooleanSetting lastMomentPlace = this.add(new BooleanSetting("LastMomentPlace", false));
   private final BooleanSetting noServerRotate = this.add(new BooleanSetting("NoServerRotate", false));

   private final NumberSetting catLeanKeepHeight = this.add(new NumberSetting("KeepHeight", 0, 0, 5, 1));
   private final NumberSetting catLeanDelay = this.add(new NumberSetting("CatLean Delay", 1, 0, 10, 1));
   private final BooleanSetting catLeanTower = this.add(new BooleanSetting("CatLean Tower", true));
   private final BooleanSetting catLeanSafeWalk = this.add(new BooleanSetting("CatLean SafeWalk", false));
   private final BooleanSetting catLeanAllowShift = this.add(new BooleanSetting("CatLean AllowShift", true));
   private final ModeSetting catLeanSwitchMode = this.add(new ModeSetting("CatLean Switch", "Normal",
      "Normal", "Silent", "Inventory", "None"));
   private final BooleanSetting catLeanLegit = this.add(new BooleanSetting("CatLean Legit", false));
   private final BooleanSetting catLeanSaveAngles = this.add(new BooleanSetting("CatLean SaveAngles", false));
   private final BooleanSetting catLeanTelly = this.add(new BooleanSetting("CatLean Telly", false));

   // Keep the established Telly practice controls/config names while adding the new modes.
   private final BooleanSetting tellyAutoJump = this.add(new BooleanSetting("Telly Auto Jump", true));
   private final BooleanSetting tellySprint = this.add(new BooleanSetting("Telly Sprint", true));
   private final BooleanSetting tellyAutoSwitch = this.add(new BooleanSetting("Telly Auto Switch", true));
   private final BooleanSetting tellySwing = this.add(new BooleanSetting("Telly Swing", true));
   private final BooleanSetting tellyDiagnostics = this.add(new BooleanSetting("Telly Diagnostics", true));
   private final NumberSetting tellyJumpDistance = this.add(new NumberSetting("Telly Jump Distance", 0.3, 0.1, 0.8, 0.05));
   private final NumberSetting tellyBlocks = this.add(new NumberSetting("Telly Blocks", 3, 1, 4, 1));
   private final NumberSetting tellyStartTick = this.add(new NumberSetting("Telly Start Tick", 1, 0, 8, 1));
   private final NumberSetting tellyCps = this.add(new NumberSetting("Telly CPS", 15, 1, 15, 1));
   private final BooleanSetting tellyCamLock = this.add(new BooleanSetting("Cam Lock", true));
   private final BooleanSetting tellyReturnToView = this.add(new BooleanSetting("Telly Return To View", true));
   private final NumberSetting tellyReturnPitch = this.add(new NumberSetting("Telly Return Pitch", 20, -60, 80, 1));
   private final NumberSetting tellyTurnSpeed = this.add(new NumberSetting("Telly Turn Speed", 180, 15, 180, 5));
   private final NumberSetting tellyReturnSpeed = this.add(new NumberSetting("Telly Return Speed", 180, 15, 180, 5));
   private final NumberSetting tellySmoothing = this.add(new NumberSetting("Telly Smoothing", 1, 0.2, 1, 0.05));
   private final NumberSetting tellyReach = this.add(new NumberSetting("Telly Reach", 4.5, 2, 4.5, 0.1));
   private final NumberSetting tellySamples = this.add(new NumberSetting("Telly Aim Samples", 5, 3, 7, 1));
   private final BooleanSetting tellySmartStop = this.add(new BooleanSetting("Telly Smart Stop", true));

   private final TellyScaffoldController telly = new TellyScaffoldController();
   private final Subscription inputSubscription;
   private final Subscription worldSubscription;
   private int lockedY = NO_LOCKED_Y;
   private int placedCount;
   private long nextPlaceAtNanos;
   private long lockYAtNanos;
   private float previousPitch;
   private boolean previousPitchInitialized;
   private double catLeanStartY = Double.NaN;
   private Object lastWorld;
   private Object lastPlayer;

   public ScaffoldModule() {
      super("Scaffold", "Places blocks below or ahead of the player", ModuleCategory.MOVEMENT);
      this.configureVisibility();
      this.inputSubscription = SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
      this.worldSubscription = SkycoreClient.events().subscribe(WorldChangeEvent.class, event -> {
         this.resetState();
         this.telly.reset();
         this.clearRotationIfOwned();
      });
   }

   private void configureVisibility() {
      this.rotate.visibleWhen(() -> !this.mode.is("NCP") && !this.mode.is("CatLean") && !this.mode.is("Telly"));
      this.lockY.visibleWhen(() -> !this.mode.is("Eagle") && !this.mode.is("CatLean") && !this.mode.is("Telly"));
      this.autoJump.visibleWhen(() -> !this.mode.is("Eagle") && !this.mode.is("CatLean") && !this.mode.is("Telly"));
      this.workWhileSneaking.visibleWhen(() -> !this.mode.is("Eagle") && !this.mode.is("CatLean") && !this.mode.is("Telly"));
      this.tower.visibleWhen(() -> !this.mode.is("Eagle") && !this.mode.is("CatLean") && !this.mode.is("Telly"));
      this.autoSwitch.visibleWhen(() -> !this.mode.is("Telly") && !this.mode.is("CatLean"));

      this.safeWalk.visibleWhen(() -> this.mode.is("NCP"));
      this.onlyNotHoldingSpace.visibleWhen(() -> this.mode.is("NCP"));
      this.ncpRotate.visibleWhen(() -> this.mode.is("NCP"));
      this.smoothSpeed.visibleWhen(() -> this.mode.is("NCP") && this.ncpRotate.is("Smooth"));
      this.ncpDelay.visibleWhen(() -> this.mode.is("NCP"));
      this.ncpMinDelay.visibleWhen(() -> this.mode.is("NCP") && this.ncpDelay.get());
      this.ncpMaxDelay.visibleWhen(() -> this.mode.is("NCP") && this.ncpDelay.get());

      this.lockYDelay.visibleWhen(() -> this.mode.is("NCPStrict") && this.lockY.get());
      this.staticPitch.visibleWhen(() -> this.mode.is("NCPStrict") && this.rotate.is("Grim+Matrix"));
      this.fixedPitch.visibleWhen(() -> this.mode.is("NCPStrict") && this.rotate.is("Grim+Matrix") && this.staticPitch.get());
      this.grimPitchLimit.visibleWhen(() -> this.mode.is("NCPStrict") && this.rotate.is("Grim+Matrix") && !this.staticPitch.get());
      this.randomizeDelay.visibleWhen(() -> this.mode.is("NCPStrict"));
      this.minPlaceDelay.visibleWhen(() -> this.mode.is("NCPStrict"));
      this.maxPlaceDelay.visibleWhen(() -> this.mode.is("NCPStrict") && this.randomizeDelay.get());
      this.buildTrust.visibleWhen(() -> this.mode.is("NCPStrict"));
      this.trustBlocks.visibleWhen(() -> this.mode.is("NCPStrict") && this.buildTrust.get());
      this.lastMomentPlace.visibleWhen(() -> this.mode.is("NCPStrict"));
      this.noServerRotate.visibleWhen(() -> this.mode.is("NCPStrict"));

      this.catLeanKeepHeight.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanDelay.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanTower.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanSafeWalk.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanAllowShift.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanSwitchMode.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanLegit.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanSaveAngles.visibleWhen(() -> this.mode.is("CatLean"));
      this.catLeanTelly.visibleWhen(() -> this.mode.is("CatLean"));

      this.tellyAutoJump.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyAutoSwitch.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyDiagnostics.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyCamLock.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySmartStop.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyJumpDistance.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyBlocks.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyStartTick.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyCps.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyReturnSpeed.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyReturnPitch.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySmoothing.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyReach.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySamples.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyReturnToView.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySprint.visibleWhen(() -> this.mode.is("Telly"));
      this.tellyTurnSpeed.visibleWhen(() -> this.mode.is("Telly"));
      this.tellySwing.visibleWhen(() -> this.mode.is("Telly"));
   }

   @Override
   protected void onEnable() {
      this.resetState();
      this.telly.reset();
   }

   @Override
   protected void onDisable() {
      this.resetState();
      this.telly.reset();
      RotationManager.INSTANCE.stopRotationIfPriority(ROTATION_PRIORITY);
   }

   private void resetState() {
      this.lockedY = NO_LOCKED_Y;
      this.placedCount = 0;
      this.nextPlaceAtNanos = 0;
      this.lockYAtNanos = 0;
      this.previousPitchInitialized = false;
      this.catLeanStartY = Double.NaN;
      this.lastWorld = null;
      this.lastPlayer = null;
   }

   @Override
   protected void onTick() {
      Minecraft mc = Minecraft.getInstance();
      if (this.mode.is("Telly")) {
         this.telly.tick(mc, this, this.tellySettings());
         return;
      }
      if (mc.player == null || mc.level == null || mc.gameMode == null || mc.gui.screen() != null
         || !mc.player.isAlive() || mc.player.isSpectator() || mc.player.isPassenger()) {
         this.clearRotationIfOwned();
         return;
      }
      if (this.lastWorld != mc.level || this.lastPlayer != mc.player) {
         this.resetState();
         this.lastWorld = mc.level;
         this.lastPlayer = mc.player;
      }
      LocalPlayer player = mc.player;
      boolean sneaking = mc.options.keyShift.isDown();
      if (!this.mode.is("Eagle") && !this.workWhileSneaking.get() && sneaking) return;
      if (this.mode.is("CatLean") && !this.catLeanAllowShift.get() && sneaking) return;

      if (this.mode.is("CatLean") && Double.isNaN(this.catLeanStartY)) this.catLeanStartY = player.getY();
      if (this.shouldAutoJump(mc, player)) player.jumpFromGround();
      if ((this.mode.is("CatLean") ? this.catLeanTower.get() : this.tower.get())
         && mc.options.keyJump.isDown() && player.onGround() && !this.isMoving(player)) player.jumpFromGround();

      BlockPos target = this.targetPosition(player);
      if (this.mode.is("NCPStrict") && this.lockY.get()) {
         if (this.lockedY == NO_LOCKED_Y) this.lockedY = target.getY();
         target = new BlockPos(target.getX(), this.lockedY, target.getZ());
         if (this.lockYDelay.get() > 0 && player.getDeltaMovement().y <= 0
            && this.lockYAtNanos != 0
            && System.nanoTime() - this.lockYAtNanos < millisToNanos(this.lockYDelay.get())) return;
      }
      if (!validTarget(mc, target)) return;

      BlockHitResult hit = this.findPlacement(mc, target);
      if (hit == null) return;
      if (this.mode.is("NCPStrict") && this.lastMomentPlace.get() && this.placedCount >= trustThreshold()
         && !this.isLastMoment(player, target)) return;
      if (!this.delayReady()) return;

      Rotation desired = this.adjustRotation(Rotation.lookingAt(hit.getLocation(), player.getEyePosition()));
      boolean trustWarmup = this.mode.is("NCPStrict") && this.buildTrust.get()
         && this.placedCount < this.trustBlocks.get().intValue();
      if (this.mode.is("Eagle")) {
         // Eagle only adds edge-sneak input; leave view and server rotation untouched.
      } else if (this.mode.is("CatLean")) {
         if (!this.catLeanLegit.get()) this.applyRotation(desired, !this.catLeanSaveAngles.get());
      } else if (!this.mode.is("NCPStrict") || !this.noServerRotate.get() || trustWarmup) {
         this.applyRotation(desired, trustWarmup);
      } else if (this.rotate.is("Normal")) {
         player.setYRot(desired.getYaw());
         player.setXRot(desired.getPitch());
      }

      if (this.mode.is("CatLean") && this.catLeanKeepHeight.get() > 0 && !Double.isNaN(this.catLeanStartY)
         && player.getY() < this.catLeanStartY - this.catLeanKeepHeight.get()) return;
      if (this.mode.is("CatLean") && this.catLeanDelay.get() > 0
         && this.placedCount > 0 && !this.elapsed(this.nextPlaceAtNanos)) return;
      if (this.mode.is("NCP") && this.ncpDelay.get() && !this.elapsed(this.nextPlaceAtNanos)) return;

      this.tryPlace(mc, hit);
   }

   private boolean shouldAutoJump(Minecraft mc, LocalPlayer player) {
      if (!this.autoJump.get() || this.mode.is("Eagle") || this.mode.is("CatLean") || this.mode.is("Telly")) return false;
      boolean jumpHeld = mc.options.keyJump.isDown();
      if (this.mode.is("NCP") && this.onlyNotHoldingSpace.get() && jumpHeld) return false;
      if (jumpHeld || !player.onGround()) return false;
      Vec3 motion = player.getDeltaMovement();
      return motion.x * motion.x + motion.z * motion.z > 0.0025;
   }

   private boolean isMoving(LocalPlayer player) {
      Vec3 motion = player.getDeltaMovement();
      return motion.x * motion.x + motion.z * motion.z > 0.0025;
   }

   private BlockPos targetPosition(LocalPlayer player) {
      double x = player.getX();
      double z = player.getZ();
      if (this.mode.is("CatLean") && this.catLeanTelly.get()) {
         Vec3 motion = player.getDeltaMovement();
         double length = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
         if (length > 0.01) {
            x += motion.x / length * 0.35;
            z += motion.z / length * 0.35;
         }
      }
      return BlockPos.containing(x, player.getY() - 0.01, z);
   }

   private BlockHitResult findPlacement(Minecraft mc, BlockPos target) {
      BlockHitResult result = findHit(mc, target, 4.5, 3);
      if (result != null) return result;
      if (!this.mode.is("CatLean")) return null;

      // CatLean checks a small forward ring but still requires a real support face and reach.
      List<BlockPos> candidates = new ArrayList<>();
      for (Direction direction : List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
         candidates.add(target.relative(direction));
      }
      return candidates.stream()
         .filter(candidate -> validTarget(mc, candidate))
         .map(candidate -> findHit(mc, candidate, 4.5, 3))
         .filter(hit -> hit != null)
         .min(Comparator.comparingDouble(hit -> angularCost(mc.player.getYRot(), mc.player.getXRot(),
            Rotation.lookingAt(hit.getLocation(), mc.player.getEyePosition()))))
         .orElse(null);
   }

   private Rotation adjustRotation(Rotation rotation) {
      float pitch = rotation.getPitch();
      if (this.mode.is("NCPStrict") && this.rotate.is("Grim+Matrix")) {
         if (this.staticPitch.get()) pitch = this.fixedPitch.get().floatValue();
         else if (this.previousPitchInitialized) {
            float difference = pitch - this.previousPitch;
            float limit = this.grimPitchLimit.get().floatValue();
            pitch = this.previousPitch + Mth.clamp(difference, -limit, limit);
         }
      }
      this.previousPitch = pitch;
      this.previousPitchInitialized = true;
      return new Rotation(rotation.getYaw(), Mth.clamp(pitch, -90, 90));
   }

   private void applyRotation(Rotation rotation, boolean visible) {
      if (this.rotate.is("None")) return;
      if (this.mode.is("NCP") && this.ncpRotate.is("Packet")) {
         RotationManager.update(rotation, 360, 360, 360, 360, 1, ROTATION_PRIORITY, false);
         return;
      }
      if (this.mode.is("NCP") && this.ncpRotate.is("Smooth")) {
         float speed = this.smoothSpeed.get().floatValue();
         RotationManager.update(rotation, speed, speed, speed, speed, 2, ROTATION_PRIORITY, false);
         return;
      }
      if (visible || this.rotate.is("Normal") || this.mode.is("NCP") && this.ncpRotate.is("Normal")) {
         RotationManager.update(rotation, 180, 180, 180, 180, 1, ROTATION_PRIORITY, true);
      } else if (this.rotate.is("Smooth")) {
         RotationManager.update(rotation, 12, 12, 12, 12, 2, ROTATION_PRIORITY, false);
      } else if (this.rotate.is("Snap")) {
         RotationManager.update(rotation, 360, 360, 360, 360, 1, ROTATION_PRIORITY, false);
      } else if (this.rotate.is("Linear") || this.mode.is("NCP") && this.ncpRotate.is("Linear")) {
         RotationManager.update(rotation, 20, 20, 20, 20, 2, ROTATION_PRIORITY, false);
      } else {
         // Packet and Grim+Matrix use SkyCore's rotation owner; this module emits no raw look packet.
         RotationManager.update(rotation, 180, 180, 180, 180, 2, ROTATION_PRIORITY, false);
      }
   }

   private boolean delayReady() {
      return this.nextPlaceAtNanos == 0 || System.nanoTime() >= this.nextPlaceAtNanos;
   }

   private boolean elapsed(long deadline) {
      return deadline == 0 || System.nanoTime() >= deadline;
   }

   private boolean isLastMoment(LocalPlayer player, BlockPos target) {
      if (player.onGround()) return true;
      double offsetX = Math.abs(player.getX() - Mth.floor(player.getX()) - 0.5);
      double offsetZ = Math.abs(player.getZ() - Mth.floor(player.getZ()) - 0.5);
      if (offsetX > 0.3 || offsetZ > 0.3) return true;
      double fall = player.getY() - target.getY() - 1;
      double speed = player.getDeltaMovement().y;
      return speed < -0.01 && fall >= 0 && fall / -speed <= 3;
   }

   private int trustThreshold() {
      return this.buildTrust.get() ? this.trustBlocks.get().intValue() : 0;
   }

   private void tryPlace(Minecraft mc, BlockHitResult hit) {
      LocalPlayer player = mc.player;
      BlockChoice choice = this.findBlockChoice(player);
      if (choice == null) return;
      int oldSlot = player.getInventory().getSelectedSlot();
      String switchMode = this.mode.is("CatLean") ? this.catLeanSwitchMode.get() : this.autoSwitch.get();
      boolean inventorySwap = false;
      int inventoryMenuSlot = -1;
      try {
         if (choice.hand() == InteractionHand.MAIN_HAND && choice.slot() >= 0 && choice.slot() != oldSlot) {
            if (switchMode.equals("None")) return;
            if (choice.slot() < 9) {
               InventoryHelper.selectHotbarSlot(choice.slot());
            } else if (switchMode.equals("Inventory") && player.containerMenu.slots.size() > choice.slot()) {
               inventoryMenuSlot = choice.slot();
               InventoryHelper.swapHand(player.containerMenu.slots.get(inventoryMenuSlot), InteractionHand.MAIN_HAND, false);
               inventorySwap = true;
            } else {
               return;
            }
         }
         InteractionHand hand = choice.hand();
         InteractionResult action = mc.gameMode.useItemOn(player, hand, hit);
         if (!action.consumesAction()) return;
         player.swing(hand);
         this.placedCount++;
         this.lockedY = hit.getBlockPos().relative(hit.getDirection()).getY();
         long delay = this.nextDelayMillis();
         this.nextPlaceAtNanos = System.nanoTime() + millisToNanos(delay);
         if (this.lockY.get()) this.lockYAtNanos = System.nanoTime();
      } finally {
         if (inventorySwap && inventoryMenuSlot >= 0 && inventoryMenuSlot < player.containerMenu.slots.size()) {
            InventoryHelper.swapHand(player.containerMenu.slots.get(inventoryMenuSlot), InteractionHand.MAIN_HAND, false);
         } else if (switchMode.equals("Silent") && oldSlot != player.getInventory().getSelectedSlot()) {
            InventoryHelper.selectHotbarSlot(oldSlot);
         }
      }
   }

   private long nextDelayMillis() {
      if (this.mode.is("NCP") && !this.ncpDelay.get()) return 0;
      if (this.mode.is("CatLean")) return Math.max(0, this.catLeanDelay.get().longValue());
      if (this.mode.is("NCP")) {
         long min = this.ncpMinDelay.get().longValue();
         long max = Math.max(min, this.ncpMaxDelay.get().longValue());
         return min == max ? min : ThreadLocalRandom.current().nextLong(min, max + 1);
      }
      if (this.mode.is("NCPStrict") && this.randomizeDelay.get()) {
         long min = this.minPlaceDelay.get().longValue();
         long max = Math.max(min, this.maxPlaceDelay.get().longValue());
         return min == max ? min : ThreadLocalRandom.current().nextLong(min, max + 1);
      }
      if (this.mode.is("NCPStrict")) return this.minPlaceDelay.get().longValue();
      return 0;
   }

   private BlockChoice findBlockChoice(LocalPlayer player) {
      ItemStack offhand = player.getOffhandItem();
      if (isBuildingBlock(offhand)) return new BlockChoice(-1, InteractionHand.OFF_HAND);
      int selected = player.getInventory().getSelectedSlot();
      if (isBuildingBlock(player.getInventory().getItem(selected))) return new BlockChoice(selected, InteractionHand.MAIN_HAND);
      String switchMode = this.mode.is("CatLean") ? this.catLeanSwitchMode.get() : this.autoSwitch.get();
      if (switchMode.equals("None")) return null;
      for (int slot = 0; slot < 9; slot++) {
         if (isBuildingBlock(player.getInventory().getItem(slot))) return new BlockChoice(slot, InteractionHand.MAIN_HAND);
      }
      if (switchMode.equals("Inventory")) {
         for (int slot = 9; slot < 36; slot++) {
            if (isBuildingBlock(player.getInventory().getItem(slot))) return new BlockChoice(slot, InteractionHand.MAIN_HAND);
         }
      }
      return null;
   }

   private static boolean isBuildingBlock(ItemStack stack) {
      if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BlockItem item)) return false;
      Block block = item.getBlock();
      return !block.defaultBlockState().canBeReplaced();
   }

   private record BlockChoice(int slot, InteractionHand hand) { }

   private void onInput(PlayerInputEvent event) {
      if (!this.isEnabled()) return;
      Minecraft mc = Minecraft.getInstance();
      if (this.mode.is("Telly")) {
         this.telly.input(mc, event, this, this.tellySettings());
         return;
      }
      if (mc.player == null || mc.level == null || mc.gui.screen() != null) return;
      if (this.mode.is("Eagle") && mc.player.onGround()) {
         BlockPos below = BlockPos.containing(mc.player.getX(), mc.player.getY() - 0.02, mc.player.getZ());
         event.setShift(!validSupport(mc, below));
      }
      boolean safe = this.mode.is("NCP") && this.safeWalk.get()
         || this.mode.is("CatLean") && this.catLeanSafeWalk.get();
      if (safe && mc.player.onGround() && !mc.player.getAbilities().flying) this.applySafeWalk(mc, event);
   }

   private void applySafeWalk(Minecraft mc, PlayerInputEvent event) {
      Vec2 input = event.moveVector();
      if (input == null || input.x * input.x + input.y * input.y < 1.0e-5F) return;
      float scale = 1.0F;
      Vec3 world = inputDirection(input.x, input.y, mc.player.getYRot());
      while (scale > 0.05F) {
         double x = mc.player.getX() + world.x * 0.35 * scale;
         double z = mc.player.getZ() + world.z * 0.35 * scale;
         BlockPos below = BlockPos.containing(x, mc.player.getY() - 0.08, z);
         if (validSupport(mc, below)) {
            if (scale < 0.999F) event.setMoveVector(input.scale(scale));
            return;
         }
         scale -= 0.05F;
      }
      event.setMoveVector(Vec2.ZERO);
   }

   private void clearRotationIfOwned() {
      RotationManager.INSTANCE.stopRotationIfPriority(ROTATION_PRIORITY);
   }

   public void onFrame(long now) {
      if (this.isEnabled() && this.mode.is("Telly")) {
         this.telly.frame(Minecraft.getInstance(), this, this.tellySettings(), now);
      }
   }

   public void beforeMovement() {
      if (this.isEnabled() && this.mode.is("Telly")) {
         this.telly.beforeMovement(Minecraft.getInstance(), this, this.tellySettings(), System.nanoTime());
      }
   }

   public void afterMovement() {
      if (this.isEnabled() && this.mode.is("Telly")) {
         this.telly.afterMovement(Minecraft.getInstance(), this, this.tellySettings(), System.nanoTime());
      }
   }

   public boolean freeCameraActive() {
      return this.isEnabled() && this.mode.is("Telly") && this.telly.freeCameraActive();
   }

   public float freeCameraYaw() { return this.telly.freeYaw(); }
   public float freeCameraPitch() { return this.telly.freePitch(); }

   public boolean mouseTurn(double yaw, double pitch) {
      return this.freeCameraActive() && this.telly.mouseTurn(yaw, pitch);
   }

   TellySettings tellySettings() {
      return new TellySettings(
         this.tellyAutoJump.get(), this.tellySprint.get(), this.tellyAutoSwitch.get(), this.tellySwing.get(),
         this.tellyDiagnostics.get(), this.tellyJumpDistance.get(), this.tellyBlocks.get().intValue(),
         this.tellyStartTick.get().intValue(), this.tellyCps.get(), this.tellyTurnSpeed.get().floatValue(),
         this.tellyReturnSpeed.get().floatValue(), this.tellySmoothing.get().floatValue(), this.tellyReach.get(),
         this.tellySamples.get().intValue(), this.tellyCamLock.get(), this.tellyReturnToView.get(),
         this.tellyReturnPitch.get().floatValue(), this.tellySmartStop.get());
   }

   public record TellySettings(boolean autoJump, boolean sprint, boolean autoSwitch, boolean swing, boolean diagnostics,
      double jumpDistance, int blocks, int startTick, double cps, float turnSpeed, float returnSpeed, float smoothing,
      double reach, int samples, boolean camLock, boolean returnToView, float returnPitch, boolean smartStop) { }

   public static boolean validSupport(Minecraft mc, BlockPos pos) {
      if (mc == null || mc.level == null || pos == null) return false;
      var state = mc.level.getBlockState(pos);
      return !state.isAir() && !state.canBeReplaced() && !state.getCollisionShape(mc.level, pos).isEmpty()
         && state.isFaceSturdy(mc.level, pos, Direction.UP);
   }

   public static boolean validTarget(Minecraft mc, BlockPos pos) {
      return mc != null && mc.level != null && pos != null && mc.level.getBlockState(pos).canBeReplaced();
   }

   public static double angularCost(float yaw, float pitch, Rotation target) {
      if (target == null || !Float.isFinite(yaw) || !Float.isFinite(pitch)
         || !Float.isFinite(target.getYaw()) || !Float.isFinite(target.getPitch())) return Double.POSITIVE_INFINITY;
      double yawDelta = Mth.wrapDegrees(target.getYaw() - yaw) * Math.cos(Math.toRadians(pitch));
      double pitchDelta = target.getPitch() - pitch;
      return Math.hypot(yawDelta, pitchDelta);
   }

   public static float stepAngle(float current, float target, float limit, float smoothing) {
      if (!Float.isFinite(current) || !Float.isFinite(target) || !Float.isFinite(limit) || !Float.isFinite(smoothing)) return current;
      float difference = Mth.wrapDegrees(target - current);
      return current + smoothStep(difference, Math.max(0, limit), Mth.clamp(smoothing, 0, 1));
   }

   public static float smoothStep(float delta, float limit, float smoothing) {
      if (!Float.isFinite(delta) || !Float.isFinite(limit) || !Float.isFinite(smoothing) || limit <= 0 || smoothing <= 0) return 0;
      float magnitude = Math.min(Math.abs(delta), limit) * Mth.clamp(smoothing, 0, 1);
      return Math.copySign(magnitude, delta);
   }

   public static Vec3 inputDirection(float strafe, float forward, float yaw) {
      if (!Float.isFinite(strafe) || !Float.isFinite(forward) || !Float.isFinite(yaw)) return Vec3.ZERO;
      float length = Mth.sqrt(strafe * strafe + forward * forward);
      if (length < 1.0e-5F) return Vec3.ZERO;
      float s = strafe / Math.max(1, length);
      float f = forward / Math.max(1, length);
      double radians = Math.toRadians(yaw);
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);
      return new Vec3(s * cos - f * sin, 0, f * cos + s * sin);
   }

   public static Vec2 localInput(Vec3 worldMove, float yaw) {
      if (worldMove == null || !Float.isFinite(yaw)) return Vec2.ZERO;
      double radians = Math.toRadians(yaw);
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);
      float strafe = (float)(worldMove.x * cos + worldMove.z * sin);
      float forward = (float)(-worldMove.x * sin + worldMove.z * cos);
      return new Vec2(strafe, forward);
   }

   public static Vec3 facePoint(BlockPos pos, Direction face, double u, double v) {
      double a = clamp01(u), b = clamp01(v);
      return switch (face) {
         case UP -> new Vec3(pos.getX() + a, pos.getY() + 1, pos.getZ() + b);
         case DOWN -> new Vec3(pos.getX() + a, pos.getY(), pos.getZ() + b);
         case NORTH -> new Vec3(pos.getX() + a, pos.getY() + b, pos.getZ());
         case SOUTH -> new Vec3(pos.getX() + a, pos.getY() + b, pos.getZ() + 1);
         case WEST -> new Vec3(pos.getX(), pos.getY() + b, pos.getZ() + a);
         case EAST -> new Vec3(pos.getX() + 1, pos.getY() + b, pos.getZ() + a);
      };
   }

   private static double clamp01(double value) {
      return Math.max(0.08, Math.min(0.92, value));
   }

   public static BlockHitResult findHit(Minecraft mc, BlockPos target, double reach, int samples) {
      if (mc == null || mc.player == null || mc.level == null || !validTarget(mc, target) || reach <= 0) return null;
      BlockHitResult best = null;
      double bestDistance = Double.POSITIVE_INFINITY;
      int count = Math.max(1, Math.min(7, samples));
      for (Direction offset : Direction.values()) {
         BlockPos support = target.relative(offset);
         var state = mc.level.getBlockState(support);
         Direction face = offset.getOpposite();
         if (state.isAir() || state.canBeReplaced() || state.getCollisionShape(mc.level, support).isEmpty()
            || !state.isFaceSturdy(mc.level, support, face)) continue;
         for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
               double u = count == 1 ? 0.5 : 0.08 + 0.84 * i / (count - 1.0);
               double v = count == 1 ? 0.5 : 0.08 + 0.84 * j / (count - 1.0);
               Vec3 point = facePoint(support, face, u, v);
               double distance = mc.player.getEyePosition().distanceToSqr(point);
               if (distance > reach * reach || distance >= bestDistance) continue;
               BlockHitResult ray = mc.level.clip(new ClipContext(mc.player.getEyePosition(), point,
                  ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
               if (ray.getType() == HitResult.Type.BLOCK && ray.getBlockPos().equals(support)
                  && ray.getDirection() == face) {
                  best = ray;
                  bestDistance = distance;
               }
            }
         }
      }
      return best;
   }

   public static BlockHitResult findTellyHit(Minecraft mc, BlockPos target, Direction heading, double reach, int samples) {
      if (mc == null || mc.player == null || mc.level == null || target == null || heading == null
         || heading == Direction.UP || heading == Direction.DOWN || !validTarget(mc, target)) return null;
      BlockPos support = target.relative(heading.getOpposite());
      var state = mc.level.getBlockState(support);
      if (state.isAir() || state.canBeReplaced() || state.getCollisionShape(mc.level, support).isEmpty()
         || !state.isFaceSturdy(mc.level, support, heading)) return null;
      int count = Math.max(1, Math.min(7, samples));
      BlockHitResult best = null;
      double bestDistance = Double.POSITIVE_INFINITY;
      for (int i = 0; i < count; i++) {
         for (int j = 0; j < count; j++) {
            double u = count == 1 ? 0.5 : 0.08 + 0.84 * i / (count - 1.0);
            double v = count == 1 ? 0.5 : 0.08 + 0.84 * j / (count - 1.0);
            Vec3 point = facePoint(support, heading, u, v);
            double distance = mc.player.getEyePosition().distanceToSqr(point);
            if (distance > reach * reach || distance >= bestDistance) continue;
            BlockHitResult ray = mc.level.clip(new ClipContext(mc.player.getEyePosition(), point,
               ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
            if (ray.getType() == HitResult.Type.BLOCK && ray.getBlockPos().equals(support)
               && ray.getDirection() == heading) {
               best = ray;
               bestDistance = distance;
            }
         }
      }
      return best;
   }

   public static BlockHitResult viewHit(Minecraft mc, double reach) {
      if (mc == null || mc.player == null || mc.level == null || !Double.isFinite(reach) || reach <= 0) return null;
      Vec3 eye = mc.player.getEyePosition();
      Vec3 end = eye.add(mc.player.getViewVector(1.0F).scale(reach));
      BlockHitResult result = mc.level.clip(new ClipContext(eye, end,
         ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
      return result.getType() == HitResult.Type.BLOCK ? result : null;
   }

   public static boolean sameFace(BlockHitResult first, BlockHitResult second) {
      return first != null && second != null && first.getType() == HitResult.Type.BLOCK
         && second.getType() == HitResult.Type.BLOCK && first.getBlockPos().equals(second.getBlockPos())
         && first.getDirection() == second.getDirection() && !first.isInside() && !second.isInside();
   }

   private static long millisToNanos(long millis) {
      return millis <= 0 ? 0 : millis * 1_000_000L;
   }
}
