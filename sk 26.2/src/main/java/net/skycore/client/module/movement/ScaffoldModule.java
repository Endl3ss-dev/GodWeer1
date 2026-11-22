package net.skycore.client.module.movement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.mixin.MinecraftGetRightClickDelayAccessor;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/**
 * Port of MotherHack {@code fun.motherhack.modules.impl.movement.Scaffold}
 * (the newer "Новый текстовый документ (8).txt" iteration) to Minecraft 26.2 / SkyCore.
 *
 * <p>Mode, setting names, defaults, delays, rotation branches and placement order are kept
 * one-to-one. Only the platform bindings differ: Yarn -&gt; Mojang mappings, Orbit events -&gt;
 * SkyCore {@code EventBus}, MotherHack {@code RotationManager} -&gt; SkyCore
 * {@code RotationManager}/{@code RotationController} plus direct movement packets,
 * {@code ClientCommandC2SPacket.PRESS_SHIFT_KEY} -&gt; {@code keyShift} input state
 * (26.2 no longer has shift actions in the player-command packet).
 */
public final class ScaffoldModule extends ClientModule {
   private static Minecraft mc() {
      return Minecraft.getInstance();
   }

   private enum Mode {
      NCP, NCPStrict, Eagle, CatLean
   }

   private enum NCPRotate {
      Linear, Packet, Smooth, Normal
   }

   private enum Rotate {
      Normal, Smooth, Packet, Linear, Snap, GrimMatrix, None
   }

   private enum Switch {
      Normal, Silent, Inventory, None
   }

   private enum CatLeanSwitch {
      Normal, Silent, Inventory, None
   }

   private static final String[] MODES = {"NCP", "NCPStrict", "Eagle", "CatLean"};
   private static final String[] NCP_ROTATES = {"Linear", "Packet", "Smooth", "Normal"};
   private static final String[] ROTATES = {"Normal", "Smooth", "Packet", "Linear", "Snap", "GrimMatrix", "None"};
   private static final String[] SWITCHES = {"Normal", "Silent", "Inventory", "None"};
   private static final String[] CAT_LEAN_SWITCHES = {"Normal", "Silent", "Inventory", "None"};

   /** MotherHack registers rotation changers at priority 5000; keep the same rank. */
   private static final int ROTATION_PRIORITY = 5000;
   private static final int ROTATION_TIMEOUT_TICKS = 2;

   // Main settings
   private final ModeSetting mode = this.add(new ModeSetting("Mode", "NCPStrict", MODES));
   private final ModeSetting autoSwitch = this.add(new ModeSetting("Switch", "Silent", SWITCHES));
   private final ModeSetting rotateMode = this.add(new ModeSetting("Rotate", "GrimMatrix", ROTATES));

   // Common settings
   private final BooleanSetting lockY = this.add(new BooleanSetting("LockY", false));
   private final BooleanSetting autoJump = this.add(new BooleanSetting("AutoJump", false));
   private final BooleanSetting allowShift = this.add(new BooleanSetting("WorkWhileSneaking", false));
   private final BooleanSetting tower = this.add(new BooleanSetting("Tower", false));

   // NCP specific settings
   private final BooleanSetting safewalk = this.add(new BooleanSetting("SafeWalk", true));
   private final BooleanSetting onlyNotHoldingSpace = this.add(new BooleanSetting("OnlyNotHoldingSpace", false));
   private final ModeSetting ncpRotateMode = this.add(new ModeSetting("NCPRotate", "Normal", NCP_ROTATES));
   private final NumberSetting smoothSpeed = this.add(new NumberSetting("SmoothSpeed", 15, 5, 30, 1));
   private final BooleanSetting ncpDelay = this.add(new BooleanSetting("PlaceDelay", false));
   private final NumberSetting ncpMinDelay = this.add(new NumberSetting("MinDelay", 50, 0, 300, 10));
   private final NumberSetting ncpMaxDelay = this.add(new NumberSetting("MaxDelay", 100, 50, 500, 10));

   // NCPStrict specific settings
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
   private final BooleanSetting noServerRotate = this.add(new BooleanSetting("NoServerRotate", true));

   // CatLean specific settings
   private final NumberSetting catLeanKeepHeight = this.add(new NumberSetting("KeepHeight", 0, 0, 5, 1));
   private final NumberSetting catLeanDelay = this.add(new NumberSetting("Delay", 1, 0, 10, 1));
   private final ModeSetting catLeanSwitchMode = this.add(new ModeSetting("CatLean Switch", "Normal", CAT_LEAN_SWITCHES));
   private final BooleanSetting catLeanTower = this.add(new BooleanSetting("CatLean Tower", true));
   private final BooleanSetting catLeanSafeWalk = this.add(new BooleanSetting("CatLean SafeWalk", false));
   private final BooleanSetting catLeanAllowShift = this.add(new BooleanSetting("AllowShift", true));
   private final BooleanSetting catLeanLegit = this.add(new BooleanSetting("Legit", false));
   private final BooleanSetting catLeanSaveAngles = this.add(new BooleanSetting("SaveAngles", false));
   private final BooleanSetting catLeanTelly = this.add(new BooleanSetting("Telly", false));
   private final BooleanSetting catLeanAirPlace = this.add(new BooleanSetting("AirPlace", false));

   // State tracking
   private final Timer lockYTimer = new Timer();
   private final Timer timer = new Timer(); // For tower logic in NCP mode
   private BlockPosWithFacing currentBlock;
   private int prevY = -999;
   private boolean wasSneaking = false;
   private BlockPosWithFacing delayedBlock;
   private float[] currentRotations = new float[2];
   private float[] lastRotations = new float[2];
   private float[] targetRotationsNCP = new float[2];
   private float[] snapRotations = null;
   private int blocksPlaced = 0;
   private int trustLevel = 0;
   private long nextPlaceTime = 0;
   private long ncpNextPlaceTime = 0;
   private final Random random = new Random();

   // CatLean state tracking
   private int catLeanLastPlaceTick = 0;
   private double catLeanStartY = 0;

   // NCPStrict improvements
   private boolean lastMomentPlaced = false;
   private int lastMomentTick = 0;
   private static final Set<Block> BLACKLIST = blacklist();

   private static Set<Block> blacklist() {
      return Set.of(
         Blocks.CHEST,
         Blocks.ENDER_CHEST,
         Blocks.TRAPPED_CHEST,
         Blocks.CRAFTING_TABLE,
         Blocks.FURNACE,
         Blocks.ENCHANTING_TABLE,
         Blocks.ANVIL,
         Blocks.TNT,
         Blocks.SAND,
         Blocks.RED_SAND,
         Blocks.GRAVEL,
         Blocks.SCAFFOLDING,
         Blocks.SLIME_BLOCK,
         Blocks.HONEY_BLOCK,
         Blocks.LADDER,
         Blocks.VINE,
         Blocks.CACTUS,
         Blocks.LILY_PAD
      );
   }

   private Subscription tickSubscription;

   public ScaffoldModule() {
      super("Scaffold", "MotherHack Scaffold port: NCP, NCPStrict, Eagle and CatLean bridge builders", ModuleCategory.MOVEMENT);

      this.rotateMode.visibleWhen(() -> !this.isMode(Mode.NCP) && !this.isMode(Mode.CatLean));

      this.lockY.visibleWhen(() -> !this.isMode(Mode.Eagle) && !this.isMode(Mode.CatLean));
      this.autoJump.visibleWhen(() -> !this.isMode(Mode.Eagle) && !this.isMode(Mode.CatLean));
      this.allowShift.visibleWhen(() -> !this.isMode(Mode.Eagle) && !this.isMode(Mode.CatLean));
      this.tower.visibleWhen(() -> !this.isMode(Mode.Eagle) && !this.isMode(Mode.CatLean));

      this.safewalk.visibleWhen(() -> this.isMode(Mode.NCP));
      this.onlyNotHoldingSpace.visibleWhen(() -> this.isMode(Mode.NCP));
      this.ncpRotateMode.visibleWhen(() -> this.isMode(Mode.NCP));
      this.smoothSpeed.visibleWhen(() -> this.isMode(Mode.NCP) && this.ncpRotateMode.is("Smooth"));
      this.ncpDelay.visibleWhen(() -> this.isMode(Mode.NCP));
      this.ncpMinDelay.visibleWhen(() -> this.isMode(Mode.NCP) && this.ncpDelay.get());
      this.ncpMaxDelay.visibleWhen(() -> this.isMode(Mode.NCP) && this.ncpDelay.get());

      this.lockYDelay.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.lockY.get());
      this.staticPitch.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.rotateMode.is("GrimMatrix"));
      this.fixedPitch.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.rotateMode.is("GrimMatrix") && this.staticPitch.get());
      this.grimPitchLimit.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.rotateMode.is("GrimMatrix") && !this.staticPitch.get());
      this.randomizeDelay.visibleWhen(() -> this.isMode(Mode.NCPStrict));
      this.minPlaceDelay.visibleWhen(() -> this.isMode(Mode.NCPStrict));
      this.maxPlaceDelay.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.randomizeDelay.get());
      this.buildTrust.visibleWhen(() -> this.isMode(Mode.NCPStrict));
      this.trustBlocks.visibleWhen(() -> this.isMode(Mode.NCPStrict) && this.buildTrust.get());
      this.lastMomentPlace.visibleWhen(() -> this.isMode(Mode.NCPStrict));
      this.noServerRotate.visibleWhen(() -> this.isMode(Mode.NCPStrict));

      this.catLeanKeepHeight.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanDelay.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanSwitchMode.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanTower.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanSafeWalk.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanAllowShift.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanLegit.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanSaveAngles.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanTelly.visibleWhen(() -> this.isMode(Mode.CatLean));
      this.catLeanAirPlace.visibleWhen(() -> this.isMode(Mode.CatLean));
   }

   private boolean isMode(Mode expected) {
      return expected.name().equals(this.mode.get());
   }

   private Mode modeValue() {
      return Mode.valueOf(this.mode.get());
   }

   private NCPRotate ncpRotateValue() {
      return NCPRotate.valueOf(this.ncpRotateMode.get());
   }

   private Rotate rotateValue() {
      return Rotate.valueOf(this.rotateMode.get());
   }

   private Switch switchValue() {
      return Switch.valueOf(this.autoSwitch.get());
   }

   private CatLeanSwitch catLeanSwitchValue() {
      return CatLeanSwitch.valueOf(this.catLeanSwitchMode.get());
   }

   @Override
   protected void onEnable() {
      this.prevY = -999;
      this.delayedBlock = null;
      this.snapRotations = null;
      this.lockYTimer.reset();
      this.blocksPlaced = 0;
      this.trustLevel = 0;
      this.nextPlaceTime = 0;
      this.ncpNextPlaceTime = 0;
      this.catLeanLastPlaceTick = 0;
      this.lastMomentPlaced = false;
      this.lastMomentTick = 0;

      if (mc().player != null) {
         this.lastRotations[0] = mc().player.getYRot();
         this.lastRotations[1] = mc().player.getXRot();
         this.currentRotations[0] = mc().player.getYRot();
         this.currentRotations[1] = mc().player.getXRot();
         this.targetRotationsNCP[0] = mc().player.getYRot();
         this.targetRotationsNCP[1] = mc().player.getXRot();
         this.catLeanStartY = mc().player.getY();
      }

      if (this.tickSubscription != null) {
         this.tickSubscription.close();
      }
      this.tickSubscription = SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.onPlayerTick());
   }

   @Override
   protected void onDisable() {
      if (this.wasSneaking && mc() != null) {
         mc().options.keyShift.setDown(false);
         this.wasSneaking = false;
      }
      this.stopManagedRotation();
      if (this.tickSubscription != null) {
         this.tickSubscription.close();
         this.tickSubscription = null;
      }
   }

   private boolean nullCheck() {
      return mc().player == null || mc().level == null || mc().gameMode == null;
   }

   private void onPlayerTick() {
      if (this.nullCheck()) {
         return;
      }

      // Update lastRotations every tick so grimPitchLimit has fresh reference
      this.lastRotations[0] = mc().player.getYRot();
      this.lastRotations[1] = mc().player.getXRot();

      Mode value = this.modeValue();
      if (value == Mode.Eagle) {
         this.handleEagleMode();
      } else if (value == Mode.NCP) {
         this.handleNCPMode();
      } else if (value == Mode.NCPStrict) {
         this.handleNCPStrictMode();
      } else if (value == Mode.CatLean) {
         this.handleCatLeanMode();
      }

      if (this.currentBlock == null) {
         this.stopManagedRotation();
      }
   }

   private void handleEagleMode() {
      BlockPos checkPos = BlockPos.containing(mc().player.getX(), mc().player.getY() - 1.0, mc().player.getZ());
      boolean shouldSneak = mc().level.getBlockState(checkPos).isAir();

      mc().options.keyShift.setDown(shouldSneak);
      this.wasSneaking = shouldSneak;
   }

   private void handleNCPMode() {
      if (this.safewalk.get() && mc().player.onGround() && !mc().player.noPhysics) {
         Vec3 velocity = mc().player.getDeltaMovement();
         double x = velocity.x;
         double z = velocity.z;
         double increment = 0.05;

         while (x != 0.0 && this.isOffsetBBEmpty(x, 0.0)) {
            if (x < increment && x >= -increment) {
               x = 0.0;
            } else if (x > 0.0) {
               x -= increment;
            } else {
               x += increment;
            }
         }

         while (z != 0.0 && this.isOffsetBBEmpty(0.0, z)) {
            if (z < increment && z >= -increment) {
               z = 0.0;
            } else if (z > 0.0) {
               z -= increment;
            } else {
               z += increment;
            }
         }

         while (x != 0.0 && z != 0.0 && this.isOffsetBBEmpty(x, z)) {
            if (x < increment && x >= -increment) {
               x = 0.0;
            } else if (x > 0.0) {
               x -= increment;
            } else {
               x += increment;
            }

            if (z < increment && z >= -increment) {
               z = 0.0;
            } else if (z > 0.0) {
               z -= increment;
            } else {
               z += increment;
            }
         }

         if (x != velocity.x || z != velocity.z) {
            mc().player.setDeltaMovement(x, velocity.y, z);
         }
      }

      this.currentBlock = null;

      if (mc().player.isCrouching() && !this.allowShift.get()) {
         return;
      }

      int prevSlot = this.prePlace(false);
      if (prevSlot == -1) {
         return;
      }

      if (mc().options.keyJump.isDown() && !this.isMoving()) {
         this.prevY = (int) Math.floor(mc().player.getY() - 1);
      }

      if (this.isMoving() && this.autoJump.get()) {
         if (mc().options.keyJump.isDown()) {
            if (this.onlyNotHoldingSpace.get()) {
               this.prevY = (int) Math.floor(mc().player.getY() - 1);
            }
         } else if (mc().player.onGround()) {
            mc().player.jumpFromGround();
         }
      }

      BlockPos targetPos = this.lockY.get() && this.prevY != -999
         ? BlockPos.containing(mc().player.getX(), this.prevY, mc().player.getZ())
         : new BlockPos((int) Math.floor(mc().player.getX()), (int) Math.floor(mc().player.getY() - 1), (int) Math.floor(mc().player.getZ()));

      if (!mc().level.getBlockState(targetPos).canBeReplaced()) {
         return;
      }

      this.currentBlock = this.checkNearBlocksExtended(targetPos);

      if (this.currentBlock != null) {
         Vec3 hitVec = new Vec3(
            this.currentBlock.position().getX() + 0.5,
            this.currentBlock.position().getY() + 0.5,
            this.currentBlock.position().getZ() + 0.5
         ).add(Vec3.atLowerCornerOf(this.currentBlock.facing().getUnitVec3i()).scale(0.5));

         this.currentRotations = rotationsTo(hitVec.x, hitVec.y, hitVec.z);

         this.applyNCPRotation();

         if (this.canPlaceBlockNCP()) {
            this.placeBlockNCP(prevSlot);
         }
      }
   }

   private void applyNCPRotation() {
      switch (this.ncpRotateValue()) {
         case Linear:
            this.pushManagedRotation();
            this.setBodyHead(this.currentRotations[0]);
            break;

         case Packet:
            break;

         case Smooth:
            float yawDiff = this.currentRotations[0] - this.targetRotationsNCP[0];
            float pitchDiff = this.currentRotations[1] - this.targetRotationsNCP[1];

            while (yawDiff > 180.0F) {
               yawDiff -= 360.0F;
            }
            while (yawDiff < -180.0F) {
               yawDiff += 360.0F;
            }

            float speed = this.smoothSpeed.get().floatValue();

            float yawStep = Math.max(-speed, Math.min(speed, yawDiff));
            float pitchStep = Math.max(-speed, Math.min(speed, pitchDiff));

            this.targetRotationsNCP[0] += yawStep;
            this.targetRotationsNCP[1] += pitchStep;

            this.targetRotationsNCP[1] = Math.max(-90.0F, Math.min(90.0F, this.targetRotationsNCP[1]));

            this.setBodyHead(this.targetRotationsNCP[0]);

            mc().player.connection.send(new ServerboundMovePlayerPacket.Rot(
               this.targetRotationsNCP[0],
               this.targetRotationsNCP[1],
               mc().player.onGround(),
               mc().player.horizontalCollision
            ));
            break;

         case Normal:
            this.setBodyHead(this.currentRotations[0]);
            break;
      }
   }

   private boolean canPlaceBlockNCP() {
      if (!this.ncpDelay.get()) {
         return true;
      }

      long currentTime = System.currentTimeMillis();

      if (currentTime < this.ncpNextPlaceTime) {
         return false;
      }

      int minDelay = this.ncpMinDelay.get().intValue();
      int maxDelay = this.ncpMaxDelay.get().intValue();
      int delay = minDelay + this.random.nextInt(Math.max(1, maxDelay - minDelay + 1));
      this.ncpNextPlaceTime = currentTime + delay;

      return true;
   }

   private boolean isOffsetBBEmpty(double x, double z) {
      return mc().level.noCollision(mc().player, mc().player.getBoundingBox().inflate(-0.1, 0.0, -0.1).move(x, -2.0, z));
   }

   private void placeBlockNCP(int prevSlot) {
      if (this.currentBlock == null) {
         return;
      }

      float offset = 0.2F;
      if (!mc().level.noCollision(mc().player, mc().player.getBoundingBox().inflate(-offset, 0.0, -offset).move(0.0, -0.5, 0.0))) {
         return;
      }

      if (mc().options.keyJump.isDown() && !this.isMoving() && this.tower.get()) {
         mc().player.setDeltaMovement(0.0, 0.42, 0.0);
         if (this.timer.passed(1500L)) {
            mc().player.setDeltaMovement(mc().player.getDeltaMovement().x, -0.28, mc().player.getDeltaMovement().z);
            this.timer.reset();
         }
      } else {
         this.timer.reset();
      }

      Vec3 hit = new Vec3(
         this.currentBlock.position().getX() + Math.random(),
         this.currentBlock.position().getY() + 0.99F,
         this.currentBlock.position().getZ() + Math.random()
      );
      BlockHitResult hitResult = new BlockHitResult(hit, this.currentBlock.facing(), this.currentBlock.position(), false);

      if (this.ncpRotateValue() != NCPRotate.Smooth) {
         mc().player.connection.send(new ServerboundMovePlayerPacket.Rot(
            this.currentRotations[0],
            this.currentRotations[1],
            mc().player.onGround(),
            mc().player.horizontalCollision
         ));
      }

      boolean needSneak = needSneak(mc().level.getBlockState(hitResult.getBlockPos()).getBlock());
      if (needSneak && !mc().player.isCrouching()) {
         this.setServerSneak(true);
      }

      InteractionHand hand = prevSlot == -2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      mc().gameMode.useItemOn(mc().player, hand, hitResult);
      mc().player.connection.send(new ServerboundSwingPacket(hand));

      this.prevY = this.currentBlock.position().getY();

      if (needSneak) {
         this.setServerSneak(false);
      }

      this.postPlace(prevSlot);
   }

   private void handleNCPStrictMode() {
      this.currentBlock = null;

      if (mc().player.isCrouching() && !this.allowShift.get()) {
         return;
      }

      int prevSlot = this.prePlace(false);
      if (prevSlot == -1) {
         return;
      }

      if (mc().options.keyJump.isDown() && !this.isMoving()) {
         this.prevY = (int) Math.floor(mc().player.getY() - 1);
      }

      if (this.isMoving() && this.autoJump.get()) {
         if (mc().options.keyJump.isDown()) {
            if (this.onlyNotHoldingSpace.get()) {
               this.prevY = (int) Math.floor(mc().player.getY() - 1);
            }
         } else if (mc().player.onGround()) {
            mc().player.jumpFromGround();
         }
      }

      boolean isJumping = !mc().player.onGround() && mc().player.getDeltaMovement().y > 0.0;

      if (this.lockY.get() && this.prevY != -999) {
         if (this.delayedBlock != null) {
            if (isJumping || (this.lockYDelay.get() > 0.0 && this.lockYTimer.passed(this.lockYDelay.get().longValue()))) {
               this.currentBlock = this.delayedBlock;
               this.delayedBlock = null;
               this.lockYTimer.reset();
            }
         }

         BlockPos targetPos = BlockPos.containing(mc().player.getX(), this.prevY, mc().player.getZ());

         if (!mc().level.getBlockState(targetPos).canBeReplaced()) {
            return;
         }

         BlockPosWithFacing foundBlock = this.checkNearBlocksExtended(targetPos);

         if (foundBlock != null) {
            if (this.rotateValue() != Rotate.None) {
               Vec3 hitVec = new Vec3(
                  foundBlock.position().getX() + 0.5,
                  foundBlock.position().getY() + 0.5,
                  foundBlock.position().getZ() + 0.5
               ).add(Vec3.atLowerCornerOf(foundBlock.facing().getUnitVec3i()).scale(0.5));

               this.currentRotations = this.getRotationsWithGrimMatrix(hitVec);
               this.applyRotation();
            }

            if (isJumping || this.lockYDelay.get() == 0.0) {
               this.currentBlock = foundBlock;
            } else {
               this.delayedBlock = foundBlock;
               if (!this.lockYTimer.passed(this.lockYDelay.get().longValue())) {
                  this.lockYTimer.reset();
               }
            }
         }
      } else {
         BlockPos targetPos = new BlockPos(
            (int) Math.floor(mc().player.getX()),
            (int) Math.floor(mc().player.getY() - 1),
            (int) Math.floor(mc().player.getZ())
         );

         if (!mc().level.getBlockState(targetPos).canBeReplaced()) {
            return;
         }

         this.currentBlock = this.checkNearBlocksExtended(targetPos);

         if (this.currentBlock != null && this.rotateValue() != Rotate.None) {
            Vec3 hitVec = new Vec3(
               this.currentBlock.position().getX() + 0.5,
               this.currentBlock.position().getY() + 0.5,
               this.currentBlock.position().getZ() + 0.5
            ).add(Vec3.atLowerCornerOf(this.currentBlock.facing().getUnitVec3i()).scale(0.5));

            this.currentRotations = this.getRotationsWithGrimMatrix(hitVec);
            this.applyRotation();
         }
      }

      if (this.currentBlock != null) {
         boolean trustPassed = !this.buildTrust.get() || this.blocksPlaced >= this.trustBlocks.get().intValue();

         if (this.buildTrust.get() && !trustPassed) {
            if (this.canPlaceBlockLegit()) {
               this.placeBlockWithOrder(prevSlot);
               this.blocksPlaced++;
               if (this.blocksPlaced >= this.trustBlocks.get().intValue()) {
                  this.trustLevel = 1;
               }
            }
            return;
         }

         if (this.trustLevel < 40 && this.buildTrust.get()) {
            this.trustLevel++;
         }

         if (this.lastMomentPlace.get()) {
            if (this.shouldPlaceLastMoment() && this.canPlaceBlockTrusted()) {
               this.placeBlockWithOrder(prevSlot);
               this.blocksPlaced++;
               this.lastMomentPlaced = true;
               this.lastMomentTick = mc().player.tickCount;
            }
         } else if (this.canPlaceBlockTrusted()) {
            this.placeBlockWithOrder(prevSlot);
            this.blocksPlaced++;
         }
      }
   }

   private boolean shouldPlaceLastMoment() {
      if (mc().player.onGround()) {
         return true;
      }

      double playerX = mc().player.getX();
      double playerZ = mc().player.getZ();
      double playerY = mc().player.getY();

      double offsetX = Math.abs(playerX - Math.floor(playerX) - 0.5);
      double offsetZ = Math.abs(playerZ - Math.floor(playerZ) - 0.5);

      if (offsetX > 0.3 || offsetZ > 0.3) {
         return true;
      }

      double y = playerY;
      double velocityY = mc().player.getDeltaMovement().y;
      int ticks = 0;

      while (ticks < 10) {
         velocityY -= 0.08;
         velocityY *= 0.98;
         y += velocityY;
         ticks++;

         BlockPos below = new BlockPos((int) Math.floor(playerX), (int) Math.floor(y) - 1, (int) Math.floor(playerZ));
         if (!mc().level.getBlockState(below).canBeReplaced()) {
            break;
         }
      }

      return ticks <= 3;
   }

   private boolean canPlaceBlockLegit() {
      return ((MinecraftGetRightClickDelayAccessor)mc).skycore$getRightClickDelay() == 0;
   }

   private boolean canPlaceBlockTrusted() {
      long currentTime = System.currentTimeMillis();
      if (currentTime < this.nextPlaceTime) {
         return false;
      }

      if (this.randomizeDelay.get()) {
         int minDelay = this.minPlaceDelay.get().intValue();
         int maxDelay = this.maxPlaceDelay.get().intValue();
         int delay = minDelay + this.random.nextInt(Math.max(1, maxDelay - minDelay + 1));
         if (this.buildTrust.get() && this.trustLevel < 40) {
            delay += 20;
         }
         this.nextPlaceTime = currentTime + delay;
      } else {
         this.nextPlaceTime = currentTime + this.minPlaceDelay.get().longValue();
      }

      return true;
   }

   private float[] getRotationsWithGrimMatrix(Vec3 vec) {
      float[] targetRotations = rotationsTo(vec.x, vec.y, vec.z);

      if (this.rotateValue() != Rotate.GrimMatrix) {
         return targetRotations;
      }

      float yaw = targetRotations[0];
      float pitch = targetRotations[1];

      if (this.staticPitch.get()) {
         pitch = this.fixedPitch.get().floatValue();
      } else {
         float pitchDiff = pitch - this.lastRotations[1];
         float maxPitchChange = this.grimPitchLimit.get().floatValue();

         if (Math.abs(pitchDiff) > maxPitchChange) {
            pitch = this.lastRotations[1] + (pitchDiff > 0.0F ? maxPitchChange : -maxPitchChange);
         }
      }

      this.lastRotations[0] = yaw;
      this.lastRotations[1] = pitch;

      return new float[]{yaw, pitch};
   }

   private void applyRotation() {
      Rotate value = this.rotateValue();
      if (value == Rotate.Packet) {
         this.sendPacketRotation();
      } else if (value == Rotate.Snap) {
         this.snapRotations = this.currentRotations.clone();
         this.setBodyHead(this.currentRotations[0]);
         // 26.2 PacketSendEvent cannot replace a packet, so the snap rotation is
         // emitted as its own movement packet right before the interaction.
         this.sendSnapRotation();
      } else if (value == Rotate.Linear) {
         this.pushManagedRotation();
      } else if (value == Rotate.GrimMatrix) {
         this.sendPacketRotation();
      } else if (value != Rotate.None) {
         this.pushManagedRotation();
      }
   }

   /** MotherHack {@code RotationManager.addPacketRotation}: immediate Full packet with the target view. */
   private void sendPacketRotation() {
      if (mc().player == null) {
         return;
      }
      float[] rotations = this.currentRotations;
      if (rotations[0] == RotationController.getServerYaw() || rotations[1] == RotationController.getServerPitch()) {
         return;
      }
      mc().player.connection.send(new ServerboundMovePlayerPacket.Full(
         mc().player.getX(),
         mc().player.getY(),
         mc().player.getZ(),
         rotations[0],
         rotations[1],
         mc().player.onGround(),
         mc().player.horizontalCollision
      ));
   }

   private void sendSnapRotation() {
      float[] snap = this.snapRotations;
      if (snap == null || mc().player == null) {
         return;
      }
      mc().player.connection.send(new ServerboundMovePlayerPacket.Full(
         mc().player.getX(),
         mc().player.getY(),
         mc().player.getZ(),
         snap[0],
         snap[1],
         mc().player.onGround(),
         mc().player.horizontalCollision
      ));
      this.snapRotations = null;
   }

   /** MotherHack {@code RotationChanger} equivalent: silent server rotation, camera untouched. */
   private void pushManagedRotation() {
      RotationManager.updateExact(new Rotation(this.currentRotations[0], this.currentRotations[1]), ROTATION_PRIORITY, ROTATION_TIMEOUT_TICKS);
   }

   private void stopManagedRotation() {
      if (RotationManager.INSTANCE.isAimingAtPriority(ROTATION_PRIORITY)) {
         RotationManager.INSTANCE.stopRotationIfPriority(ROTATION_PRIORITY);
      }
   }

   /** MotherHack {@code setBodyYaw}/{@code setHeadYaw}; 26.2 keeps these as public entity fields. */
   private void setBodyHead(float yaw) {
      mc().player.yBodyRot = yaw;
      mc().player.yHeadRot = yaw;
   }

   private void placeBlockWithOrder(int prevSlot) {
      if (this.currentBlock == null) {
         return;
      }

      Vec3 hitVec = this.calculateSafeHitVec(this.currentBlock.position(), this.currentBlock.facing());
      BlockHitResult hitResult = new BlockHitResult(hitVec, this.currentBlock.facing(), this.currentBlock.position(), false);

      if (this.rotateValue() != Rotate.None) {
         mc().player.connection.send(new ServerboundMovePlayerPacket.Rot(
            this.currentRotations[0],
            this.currentRotations[1],
            mc().player.onGround(),
            mc().player.horizontalCollision
         ));
      }

      InteractionHand hand = prevSlot == -2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      mc().gameMode.useItemOn(mc().player, hand, hitResult);
      mc().player.connection.send(new ServerboundSwingPacket(hand));

      if (this.tower.get() && mc().options.keyJump.isDown() && !this.isMoving() && mc().player.onGround()) {
         mc().player.setDeltaMovement(mc().player.getDeltaMovement().x, 0.42, mc().player.getDeltaMovement().z);
         mc().player.connection.send(new ServerboundMovePlayerPacket.Full(
            mc().player.getX(),
            mc().player.getY(),
            mc().player.getZ(),
            mc().player.getYRot(),
            mc().player.getXRot(),
            false,
            mc().player.horizontalCollision
         ));
      }

      this.prevY = this.currentBlock.position().getY();
      this.postPlace(prevSlot);
   }

   /**
    * Kept for parity with the source. The MotherHack build never calls it: every mode path
    * goes through {@link #placeBlockNCP(int)} or {@link #placeBlockWithOrder(int)}.
    */
   @SuppressWarnings("unused")
   private void placeBlock(int prevSlot) {
      if (this.currentBlock == null) {
         return;
      }

      Vec3 hit = new Vec3(
         this.currentBlock.position().getX() + 0.5,
         this.currentBlock.position().getY() + 0.5,
         this.currentBlock.position().getZ() + 0.5
      ).add(Vec3.atLowerCornerOf(this.currentBlock.facing().getUnitVec3i()).scale(0.5));
      BlockHitResult hitResult = new BlockHitResult(hit, this.currentBlock.facing(), this.currentBlock.position(), false);

      if (mc().options.keyJump.isDown() && !this.isMoving() && this.tower.get()) {
         if (mc().player.onGround()) {
            mc().player.jumpFromGround();
         }
      }

      boolean needSneak = needSneak(mc().level.getBlockState(this.currentBlock.position()).getBlock());

      if (needSneak && !mc().player.isCrouching()) {
         this.setServerSneak(true);
      }

      InteractionHand hand = prevSlot == -2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      mc().gameMode.useItemOn(mc().player, hand, hitResult);
      mc().player.connection.send(new ServerboundSwingPacket(hand));

      this.prevY = this.currentBlock.position().getY();

      if (needSneak) {
         this.setServerSneak(false);
      }

      this.postPlace(prevSlot);
   }

   private int prePlace(boolean shouldSwitch) {
      if (mc().player == null || mc().level == null || mc().gameMode == null) {
         return -1;
      }

      if (isBuildingStack(mc().player.getOffhandItem())) {
         return -2;
      }

      if (isBuildingStack(mc().player.getMainHandItem())) {
         return mc().player.getInventory().getSelectedSlot();
      }

      int currentSlot = mc().player.getInventory().getSelectedSlot();

      int hotbarSlot = this.findBlockInHotbar();
      int inventorySlot = this.findBlockInInventory();

      if (shouldSwitch) {
         if (this.switchValue() == Switch.Inventory) {
            if (inventorySlot != -1) {
               mc().gameMode.handleContainerInput(mc().player.containerMenu.containerId, inventorySlot, currentSlot, ContainerInput.SWAP, mc().player);
               return inventorySlot;
            }
         } else if (this.switchValue() == Switch.Normal || this.switchValue() == Switch.Silent) {
            if (hotbarSlot != -1) {
               if (this.switchValue() == Switch.Silent) {
                  mc().player.connection.send(new ServerboundSetCarriedItemPacket(hotbarSlot));
                  mc().player.getInventory().setSelectedSlot(hotbarSlot);
               }
               return hotbarSlot;
            }
         }
      }

      return hotbarSlot != -1 ? hotbarSlot : -1;
   }

   private void postPlace(int prevSlot) {
      if (prevSlot == -1 || prevSlot == -2) {
         return;
      }

      if (this.switchValue() == Switch.Inventory) {
         int currentSlot = mc().player.getInventory().getSelectedSlot();
         mc().gameMode.handleContainerInput(mc().player.containerMenu.containerId, prevSlot, currentSlot, ContainerInput.SWAP, mc().player);
      } else if (this.switchValue() == Switch.Silent) {
         mc().player.connection.send(new ServerboundSetCarriedItemPacket(prevSlot));
         mc().player.getInventory().setSelectedSlot(prevSlot);
      }
   }

   private boolean isMoving() {
      Input keys = mc().player.input.keyPresses;
      return keys.forward() || keys.backward() || keys.left() || keys.right() || mc().options.keyJump.isDown();
   }

   private int findBlockInHotbar() {
      for (int i = 0; i < 9; i++) {
         if (isBuildingStack(mc().player.getInventory().getItem(i))) {
            return i;
         }
      }
      return -1;
   }

   private int findBlockInInventory() {
      for (int i = 9; i < 36; i++) {
         if (isBuildingStack(mc().player.getInventory().getItem(i))) {
            return i;
         }
      }
      return -1;
   }

   private static boolean isBuildingStack(ItemStack stack) {
      return !stack.isEmpty()
         && stack.getItem() instanceof BlockItem item
         && !item.getBlock().defaultBlockState().canBeReplaced()
         && !BLACKLIST.contains(item.getBlock());
   }

   private BlockPosWithFacing checkNearBlocksExtended(BlockPos blockPos) {
      BlockPosWithFacing result = this.checkNearBlocks(blockPos);
      if (result != null) {
         return result;
      }

      int[][] offsets = {
         {-1, 0, 0}, {1, 0, 0},
         {0, 0, 1}, {0, 0, -1},
         {-2, 0, 0}, {2, 0, 0},
         {0, 0, 2}, {0, 0, -2},
         {0, -1, 0}, {1, -1, 0},
         {-1, -1, 0}, {0, -1, 1},
         {0, -1, -1}
      };

      for (int[] offset : offsets) {
         result = this.checkNearBlocks(blockPos.offset(offset[0], offset[1], offset[2]));
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   private BlockPosWithFacing checkNearBlocks(BlockPos blockPos) {
      Vec3 eyePos = mc().player.getEyePosition();
      BlockPosWithFacing best = null;
      double bestScore = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         BlockPos neighbor = blockPos.relative(direction);
         BlockState neighborState = mc().level.getBlockState(neighbor);
         if (neighborState.isAir() || neighborState.canBeReplaced()) {
            continue;
         }
         if (BLACKLIST.contains(neighborState.getBlock())) {
            continue;
         }

         Direction placeFace = direction.getOpposite();
         Vec3 hitVec = this.calculateSafeHitVec(neighbor, placeFace);

         double distance = eyePos.distanceTo(hitVec);
         if (distance > 4.2) {
            continue;
         }

         if (!isFaceVisible(neighbor, placeFace, eyePos)) {
            continue;
         }

         BlockHitResult ray = mc().level.clip(new ClipContext(
            eyePos,
            hitVec,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            mc().player
         ));
         if (ray == null || ray.getType() == HitResult.Type.MISS || !ray.getBlockPos().equals(neighbor)) {
            continue;
         }

         double score = distance;
         if (placeFace == Direction.UP) {
            score -= 0.5;
         }
         if (score < bestScore) {
            bestScore = score;
            best = new BlockPosWithFacing(neighbor, placeFace);
         }
      }

      return best;
   }

   private Vec3 calculateSafeHitVec(BlockPos pos, Direction face) {
      return safeHitVec(pos, face, this.random.nextDouble(), this.random.nextDouble(), this.random.nextDouble());
   }

   static Vec3 safeHitVec(BlockPos pos, Direction face, double rx, double ry, double rz) {
      double x = pos.getX();
      double y = pos.getY();
      double z = pos.getZ();

      return switch (face) {
         case UP -> new Vec3(x + rx, y + 1.0, z + rz);
         case DOWN -> new Vec3(x + rx, y, z + rz);
         case NORTH -> new Vec3(x + rx, y + ry, z);
         case SOUTH -> new Vec3(x + rx, y + ry, z + 1.0);
         case WEST -> new Vec3(x, y + ry, z + rz);
         case EAST -> new Vec3(x + 1.0, y + ry, z + rz);
      };
   }

   private static boolean isFaceVisible(BlockPos pos, Direction face, Vec3 eyePos) {
      AABB box = new AABB(pos);
      return switch (face) {
         case UP -> eyePos.y > box.maxY;
         case DOWN -> eyePos.y < box.minY;
         case NORTH -> eyePos.z < box.minZ;
         case SOUTH -> eyePos.z > box.maxZ;
         case WEST -> eyePos.x < box.minX;
         case EAST -> eyePos.x > box.maxX;
      };
   }

   private static boolean needSneak(Block block) {
      // Faithful to the source: the MotherHack build always returns false, so the
      // PRESS/RELEASE_SHIFT_KEY path never runs.
      return false;
   }

   /**
    * 26.2 replacement for {@code ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY}/
    * {@code RELEASE_SHIFT_KEY}: the player-command packet no longer carries shift actions,
    * sneaking travels as input state, so the sneak key is pressed/released instead.
    */
   private void setServerSneak(boolean state) {
      mc().options.keyShift.setDown(state);
      this.wasSneaking = state;
   }

   /**
    * MotherHack {@code RotationUtils.getRotations(double, double, double)}: feet X/Z with
    * eye Y, exactly as the source computes it.
    */
   static float[] rotationsTo(double x, double y, double z) {
      Minecraft client = Minecraft.getInstance();
      double deltaX = x - client.player.getX();
      double deltaY = y - client.player.getEyeY();
      double deltaZ = z - client.player.getZ();
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

      float yaw = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);
      float pitch = (float)(-Math.toDegrees(Math.atan2(deltaY, distance)));
      return new float[]{yaw, pitch};
   }

   /** Pure part of {@link #getRotationsWithGrimMatrix(Vec3)} for headless tests. */
   static float[] grimPitch(float[] target, float lastPitch, boolean staticPitch, float fixedPitch, float limit) {
      float yaw = target[0];
      float pitch = target[1];

      if (staticPitch) {
         pitch = fixedPitch;
      } else {
         float pitchDiff = pitch - lastPitch;
         if (Math.abs(pitchDiff) > limit) {
            pitch = lastPitch + (pitchDiff > 0.0F ? limit : -limit);
         }
      }
      return new float[]{yaw, pitch};
   }

   // ============================================
   // CATLEAN MODE IMPLEMENTATION
   // ============================================

   private void handleCatLeanMode() {
      if (!this.catLeanAllowShift.get() && mc().player.isCrouching()) {
         return;
      }

      boolean moving = Math.abs(mc().player.getDeltaMovement().x) > 0.01 || Math.abs(mc().player.getDeltaMovement().z) > 0.01;
      if (!moving && !mc().options.keyJump.isDown()) {
         return;
      }

      if (!mc().player.onGround() && !this.catLeanTower.get() && !this.catLeanAirPlace.get()) {
         return;
      }

      int currentTick = mc().player.tickCount;
      int ticksSinceLastPlace = currentTick - this.catLeanLastPlaceTick;
      if (ticksSinceLastPlace < this.catLeanDelay.get().intValue()) {
         return;
      }

      double keepHeightValue = this.catLeanKeepHeight.get();
      if (keepHeightValue > 0.0 && mc().player.getY() < this.catLeanStartY - keepHeightValue) {
         return;
      }

      BlockPos bestPos = this.findCatLeanBestPlacement();
      if (bestPos == null) {
         return;
      }

      if (this.placeCatLeanBlock(bestPos)) {
         this.catLeanLastPlaceTick = currentTick;
      }

      if (this.catLeanTower.get() && mc().options.keyJump.isDown() && mc().player.onGround()) {
         Vec3 velocity = mc().player.getDeltaMovement();
         mc().player.setDeltaMovement(velocity.x, 0.42, velocity.z);
      }
   }

   private BlockPos findCatLeanBestPlacement() {
      Vec3 playerPos = mc().player.position();
      BlockPos currentBlockPos = mc().player.blockPosition();
      BlockPos posBelow = currentBlockPos.below();
      double reach = mc().player.blockInteractionRange();
      int rangeInt = (int) Math.ceil(reach);

      Map<BlockPos, Float> possiblePositions = new LinkedHashMap<>();

      if (this.catLeanSafeWalk.get()) {
         BlockState belowState = mc().level.getBlockState(posBelow);
         if (belowState.isAir() || belowState.canBeReplaced()) {
            possiblePositions.put(posBelow, 0.0F);
         }
      }

      int minY = this.catLeanTelly.get() ? (int) Math.floor(this.catLeanStartY) : currentBlockPos.getY() - rangeInt;
      int maxY = this.catLeanTelly.get() ? (int) Math.floor(this.catLeanStartY) : currentBlockPos.getY() + 2;

      for (int y = minY; y <= maxY; y++) {
         for (int x = -rangeInt; x <= rangeInt; x++) {
            for (int z = -rangeInt; z <= rangeInt; z++) {
               BlockPos checkPos = new BlockPos(currentBlockPos.getX() + x, y, currentBlockPos.getZ() + z);

               BlockState state = mc().level.getBlockState(checkPos);
               if (!state.isAir() && !state.canBeReplaced()) {
                  continue;
               }

               boolean hasSupport = false;
               Direction supportDir = null;
               for (Direction dir : Direction.values()) {
                  BlockPos neighborPos = checkPos.relative(dir);
                  BlockState neighborState = mc().level.getBlockState(neighborPos);
                  if (!neighborState.isAir() && neighborState.isCollisionShapeFullBlock(mc().level, neighborPos)) {
                     hasSupport = true;
                     supportDir = dir;
                     break;
                  }
               }

               if (!hasSupport || supportDir == null) {
                  continue;
               }

               double distance = playerPos.distanceToSqr(checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5);
               if (distance > reach * reach) {
                  continue;
               }

               Vec3 hitVec = new Vec3(
                  checkPos.getX() + 0.5 + supportDir.getStepX() * 0.5,
                  checkPos.getY() + 0.5 + supportDir.getStepY() * 0.5,
                  checkPos.getZ() + 0.5 + supportDir.getStepZ() * 0.5
               );

               BlockHitResult rayTrace = mc().level.clip(new ClipContext(
                  mc().player.getEyePosition(),
                  hitVec,
                  ClipContext.Block.OUTLINE,
                  ClipContext.Fluid.NONE,
                  mc().player
               ));

               if (rayTrace != null && rayTrace.getType() != HitResult.Type.MISS
                  && !rayTrace.getBlockPos().equals(checkPos.relative(supportDir.getOpposite()))) {
                  continue;
               }

               float yawDiff = this.calculateCatLeanYawDifference(checkPos);
               possiblePositions.put(checkPos, yawDiff);
            }
         }
      }

      if (possiblePositions.isEmpty()) {
         return null;
      }

      BlockPos bestPos = null;
      float bestYaw = Float.MAX_VALUE;
      for (Map.Entry<BlockPos, Float> entry : possiblePositions.entrySet()) {
         if (entry.getValue() < bestYaw) {
            bestYaw = entry.getValue();
            bestPos = entry.getKey();
         }
      }

      return bestPos;
   }

   private boolean placeCatLeanBlock(BlockPos pos) {
      BlockState targetState = mc().level.getBlockState(pos);
      if (!targetState.canBeReplaced()) {
         return false;
      }

      int blockSlot = this.findCatLeanBlockSlot();
      if (blockSlot == -1) {
         return false;
      }

      Direction placementSide = this.findCatLeanPlacementSide(pos);
      if (placementSide == null) {
         return false;
      }

      BlockPos placeOn = pos.relative(placementSide.getOpposite());
      int previousSlot = mc().player.getInventory().getSelectedSlot();
      CatLeanSwitch switchMode = this.catLeanSwitchValue();

      if (switchMode == CatLeanSwitch.Inventory) {
         mc().gameMode.handleContainerInput(
            mc().player.inventoryMenu.containerId,
            blockSlot < 9 ? blockSlot + 36 : blockSlot,
            mc().player.getInventory().getSelectedSlot(),
            ContainerInput.SWAP,
            mc().player
         );
      } else if (switchMode != CatLeanSwitch.None) {
         mc().player.getInventory().setSelectedSlot(blockSlot);
      }

      Vec3 hitVec = new Vec3(
         placeOn.getX() + 0.5 + placementSide.getStepX() * 0.5,
         placeOn.getY() + 0.5 + placementSide.getStepY() * 0.5,
         placeOn.getZ() + 0.5 + placementSide.getStepZ() * 0.5
      );

      BlockHitResult hitResult = new BlockHitResult(hitVec, placementSide, placeOn, false);

      if (!this.catLeanLegit.get()) {
         float[] rotation = this.calculateCatLeanRotation(hitVec);

         if (switchMode == CatLeanSwitch.Silent) {
            mc().player.connection.send(new ServerboundMovePlayerPacket.Rot(
               rotation[0],
               rotation[1],
               mc().player.onGround(),
               mc().player.horizontalCollision
            ));
         } else if (!this.catLeanSaveAngles.get()) {
            mc().player.setYRot(rotation[0]);
            mc().player.setXRot(rotation[1]);
         }
      }

      mc().gameMode.useItemOn(mc().player, InteractionHand.MAIN_HAND, hitResult);

      if (switchMode == CatLeanSwitch.Silent) {
         mc().player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
      } else {
         mc().player.swing(InteractionHand.MAIN_HAND);
      }

      if (switchMode == CatLeanSwitch.Inventory) {
         mc().gameMode.handleContainerInput(
            mc().player.inventoryMenu.containerId,
            blockSlot < 9 ? blockSlot + 36 : blockSlot,
            mc().player.getInventory().getSelectedSlot(),
            ContainerInput.SWAP,
            mc().player
         );
      } else if (switchMode != CatLeanSwitch.None) {
         mc().player.getInventory().setSelectedSlot(previousSlot);
      }

      return true;
   }

   private Direction findCatLeanPlacementSide(BlockPos pos) {
      for (Direction dir : Direction.values()) {
         BlockPos neighborPos = pos.relative(dir);
         BlockState neighborState = mc().level.getBlockState(neighborPos);
         if (!neighborState.isAir() && neighborState.isCollisionShapeFullBlock(mc().level, neighborPos)) {
            return dir;
         }
      }
      return null;
   }

   private int findCatLeanBlockSlot() {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc().player.getInventory().getItem(i);
         if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
            return i;
         }
      }
      return -1;
   }

   private float calculateCatLeanYawDifference(BlockPos pos) {
      Vec3 blockCenter = Vec3.atCenterOf(pos);
      double deltaX = blockCenter.x - mc().player.getX();
      double deltaZ = blockCenter.z - mc().player.getZ();
      float targetYaw = (float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
      float currentYaw = mc().player.getYRot();
      return Math.abs(Mth.wrapDegrees(targetYaw - currentYaw));
   }

   private float[] calculateCatLeanRotation(Vec3 target) {
      Vec3 eyePos = mc().player.getEyePosition();
      double deltaX = target.x - eyePos.x;
      double deltaY = target.y - eyePos.y;
      double deltaZ = target.z - eyePos.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      float yaw = (float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(deltaY, distance)));
      return new float[]{yaw, pitch};
   }

   /** Suffix text shown next to the module name: the active mode, like the source. */
   public String suffix() {
      return this.mode.get();
   }

   private static final class Timer {
      private long startTime = System.currentTimeMillis();

      void reset() {
         this.startTime = System.currentTimeMillis();
      }

      boolean passed(long time) {
         return System.currentTimeMillis() - this.startTime > time;
      }
   }

   private static final class BlockPosWithFacing {
      private final BlockPos position;
      private final Direction facing;

      private BlockPosWithFacing(BlockPos position, Direction facing) {
         this.position = position;
         this.facing = facing;
      }

      BlockPos position() {
         return this.position;
      }

      Direction facing() {
         return this.facing;
      }
   }
}
