package net.skycore.client.module.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.ServerRotationManager;
import net.skycore.client.event.BlockUseObservationEvent;
import net.skycore.client.event.KeyEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ActionSetting;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.ResetSpeedHelper;
import net.skycore.client.util.SendDonationHelper;
import org.lwjgl.glfw.GLFW;

/** Singleplayer rehearsal only. Server stays at 20 TPS; actual player input is assisted and labelled. */
public final class ScaffoldCoachModule extends ClientModule {
   public static final ScaffoldCoachModule INSTANCE = new ScaffoldCoachModule();
   private final NumberSetting speed = this.add(new NumberSetting("Practice Speed", "Client simulation factor, not server TPS", 0.25, 0.15, 1, 0.05, "x"));
   private final KeyBindSetting slowKey = this.add(new KeyBindSetting("Slow Toggle Key", GLFW.GLFW_KEY_K));
   private final KeyBindSetting aimKey = this.add(new KeyBindSetting("Aim Hold Key", GLFW.GLFW_KEY_LEFT_ALT));
   private final NumberSetting aimSpeed = this.add(new NumberSetting("Coach Aim Speed", "Visible aim limit in real-time degrees per second", 180, 30, 540, 10, " deg/s"));
   private final NumberSetting aimCone = this.add(new NumberSetting("Coach Aim Cone", "Only help with points within this angle of the crosshair", 180, 15, 180, 5, " deg"));
   private final NumberSetting clickDelay = this.add(new NumberSetting("Practice Click Delay", "Real milliseconds between held-RMB attempts", 150, 80, 500, 10, " ms"));
   private final BooleanSetting assistAim = this.add(new BooleanSetting("Coach Aim", "Guide to a visible point only while Aim Hold Key is held", true));
   private final BooleanSetting lockLine = this.add(new BooleanSetting("Lock Bridge Line", "Constrain targets to the forward cardinal row captured on enable/reset", true));
   private final BooleanSetting showCue = this.add(new BooleanSetting("Three Block Cue", "Notify on the third observed block in a jump", true));
   private final List<Subscription> subscriptions = new ArrayList<>();
   private final Set<BlockPos> jumpPositions = new HashSet<>();
   private Object world, player;
   private int bridgeY, originX, originZ;
   private Direction heading = Direction.SOUTH;
   private boolean slow = true, clockOwned, ready, useArmed, wasGrounded, aimApplied;
   private float ownedSpeed = 1;
   private long lastFrame, lastSearch, nextClick;
   private int cycle, jumpBlocks;
   private BlockHitResult target;

   private ScaffoldCoachModule() {
      super("Scaffold Coach", "Singleplayer flat Telly rehearsal: client slow motion, held-key aim and real-time manual clicks", ModuleCategory.MOVEMENT);
      this.add(new ActionSetting("Reset Bridge Origin", "Capture the full block below your feet and forward direction as the new bridge row").action(this::resetHeight));
   }

   @Override
   protected void onEnable() {
      Minecraft mc = Minecraft.getInstance();
      if (!this.localWorld(mc) || !mc.player.onGround() || mc.player.getAbilities().flying
         || !ScaffoldModule.validSupport(mc, mc.player.blockPosition().below()) || this.conflict() != null
         || Math.abs(ResetSpeedHelper.getSpeed() - 1) > 0.001F) {
         this.setEnabled(false);
         info("Enable on a solid full-block platform in singleplayer at 20 TPS, with gameplay modules OFF and normal client timer.");
         return;
      }
      this.world = mc.level;
      this.player = mc.player;
      this.bridgeY = mc.player.blockPosition().below().getY();
      this.originX = mc.player.blockPosition().getX();
      this.originZ = mc.player.blockPosition().getZ();
      this.heading = mc.player.getDirection();
      this.slow = true;
      this.wasGrounded = true;
      this.cycle = this.jumpBlocks = 0;
      this.jumpPositions.clear();
      this.lastFrame = this.lastSearch = this.nextClick = 0;
      this.useArmed = this.ready = false;
      this.subscriptions.add(SkycoreClient.events().subscribe(KeyEvent.class, this::onKey));
      this.subscriptions.add(SkycoreClient.events().subscribe(WorldChangeEvent.class, event -> this.setEnabled(false)));
      this.subscriptions.add(SkycoreClient.events().subscribe(BlockUseObservationEvent.class, this::onPlacement));
      info("Coach ON at block Y=" + this.bridgeY + ". " + KeyBinds.name(this.slowKey.get())
         + ": slow on/off; hold " + KeyBinds.name(this.aimKey.get()) + ": aim; RMB: place. Assisted data only.");
   }

   @Override
   protected void onDisable() {
      this.releaseClock();
      this.subscriptions.forEach(Subscription::close);
      this.subscriptions.clear();
      this.jumpPositions.clear();
      this.world = this.player = null;
      this.target = null;
      this.ready = this.aimApplied = this.useArmed = false;
   }

   private boolean localWorld(Minecraft mc) {
      return mc.player != null && mc.level != null && mc.gameMode != null && mc.getSingleplayerServer() != null
         && Float.isFinite(mc.level.tickRateManager().tickrate())
         && Math.abs(mc.level.tickRateManager().tickrate() - 20) < 0.1F;
   }

   private String conflict() {
      for (ClientModule module : SkycoreClient.modules().enabled()) {
         if (module != this && module.affectsGameplay()) return module.getName();
      }
      if (RotationController.isActive() || RotationManager.INSTANCE.isRotating() || ServerRotationManager.INSTANCE.isRotating()) return "rotation controller";
      return null;
   }

   private boolean usable(Minecraft mc) {
      return this.localWorld(mc) && this.world == mc.level && this.player == mc.player
         && mc.gui.screen() == null && !mc.isPaused() && !mc.level.tickRateManager().isFrozen()
         && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isPassenger()
         && !mc.player.isFallFlying() && !mc.player.getAbilities().flying
         && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isUsingItem()
         && !mc.options.keyAttack.isDown() && mc.getCameraEntity() == mc.player
         && GLFW.glfwGetWindowAttrib(mc.getWindow().handle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
   }

   private void onKey(KeyEvent event) {
      if (event.pressed() && event.key().key() == this.slowKey.get() && event.client().gui.screen() == null) this.toggleSlow();
   }

   public void toggleSlow() {
      if (!this.isEnabled()) throw new IllegalStateException("Enable Scaffold Coach first");
      this.slow = !this.slow;
      if (!this.slow) this.releaseClock();
      info("Slow motion " + (this.slow ? "requested" : "OFF: normal client speed"));
   }

   public void resetHeight() {
      Minecraft mc = Minecraft.getInstance();
      if (!this.isEnabled() || !this.localWorld(mc) || !mc.player.onGround()
         || !ScaffoldModule.validSupport(mc, mc.player.blockPosition().below())) {
         info("Stand on a full block in your singleplayer world, with Coach enabled.");
         return;
      }
      this.bridgeY = mc.player.blockPosition().below().getY();
      this.originX = mc.player.blockPosition().getX();
      this.originZ = mc.player.blockPosition().getZ();
      this.heading = mc.player.getDirection();
      this.target = null;
      this.lastSearch = 0;
      info("Bridge origin captured: Y=" + this.bridgeY + ", direction=" + this.heading.name());
   }

   /** Driven by renderFrame, not by the slowed simulation tick. */
   public void onFrame(long now) {
      if (!this.isEnabled()) return;
      Minecraft mc = Minecraft.getInstance();
      this.aimApplied = false;
      if (!this.localWorld(mc) || this.world != mc.level || this.player != mc.player || this.conflict() != null) {
         this.setEnabled(false);
         info("Coach OFF: world/TPS changed or gameplay/rotation conflict. Server TPS was not modified.");
         return;
      }
      if (!Float.isFinite(mc.player.getYRot()) || !Float.isFinite(mc.player.getXRot())) {
         this.setEnabled(false);
         return;
      }
      if (mc.player.getY() < this.bridgeY + 0.8 && !mc.player.onGround() && this.slow) {
         this.slow = false;
         this.releaseClock();
         info("Below bridge: slow motion OFF. Return to the platform, then press " + KeyBinds.name(this.slowKey.get()) + ".");
      }
      this.ready = this.usable(mc) && mc.player.getY() >= this.bridgeY + 0.8;
      if (!this.ready) {
         this.releaseClock();
         this.target = null;
         this.useArmed = false;
         this.lastFrame = now;
         return;
      }
      float desired = this.slow ? this.speed.get().floatValue() : 1;
      if (clockConflict(this.clockOwned, ResetSpeedHelper.getSpeed(), this.ownedSpeed)) {
         this.setEnabled(false);
         info("Coach OFF: another client timer took ownership.");
         return;
      }
      if (desired < 0.999F) {
         ResetSpeedHelper.setSpeed(desired);
         this.clockOwned = true;
         this.ownedSpeed = desired;
      } else this.releaseClock();
      if (this.wasGrounded && !mc.player.onGround()) {
         this.cycle++;
         this.jumpBlocks = 0;
         this.jumpPositions.clear();
      }
      this.wasGrounded = mc.player.onGround();
      double dt = this.lastFrame == 0 ? 0 : Math.min(0.05, Math.max(0, (now - this.lastFrame) / 1e9));
      this.lastFrame = now;
      boolean held = this.assistAim.get() && KeyBinds.isPressed(mc.getWindow().handle(), this.aimKey.get());
      if (held && this.heldBlock(mc)) {
         if (this.lastSearch == 0 || now - this.lastSearch >= 50_000_000L) {
            this.target = this.findTarget(mc);
            this.lastSearch = now;
         }
         if (this.target != null) {
            BlockPos destination = this.target.getBlockPos().relative(this.target.getDirection());
            if (ScaffoldModule.validTarget(mc, destination) && ScaffoldModule.validSupport(mc, this.target.getBlockPos())) {
               Rotation rotation = Rotation.lookingAt(this.target.getLocation(), mc.player.getEyePosition());
               double cost = ScaffoldModule.angularCost(mc.player.getYRot(), mc.player.getXRot(), rotation);
               if (cost <= this.aimCone.get()) {
                  float limit = (float)(this.aimSpeed.get() * dt);
                  float yaw = mc.player.getYRot(), pitch = mc.player.getXRot();
                  mc.player.setYRot(ScaffoldModule.stepAngle(yaw, rotation.getYaw(), limit, 0.5F));
                  mc.player.setXRot(Mth.clamp(pitch + ScaffoldModule.smoothStep(rotation.getPitch() - pitch, limit, 0.5F), -90, 90));
                  this.aimApplied = Math.abs(mc.player.getYRot() - yaw) + Math.abs(mc.player.getXRot() - pitch) > 0.0001F;
               }
            }
         }
      } else this.target = null;
      // Native click dispatch is tick-bound. In this SP-only coach, dispatch actual
      // RMB intent in wall-clock time and suppress only its duplicate vanilla path.
      if (this.heldBlock(mc)) {
         boolean clicked = false;
         while (mc.options.keyUse.consumeClick()) clicked = true;
         boolean down = mc.options.keyUse.isDown();
         boolean armed = this.useArmed;
         if (!down) this.useArmed = true;
         if (armed && (clicked || down) && (this.nextClick == 0 || now - this.nextClick >= 0)) this.manualUse(mc, now);
      } else this.useArmed = false;
   }

   private BlockHitResult findTarget(Minecraft mc) {
      BlockHitResult best = null;
      double bestCost = this.aimCone.get();
      BlockPos centre = mc.player.blockPosition();
      for (int x = -3; x <= 3; x++) {
         for (int z = -3; z <= 3; z++) {
            BlockPos destination = new BlockPos(centre.getX() + x, this.bridgeY, centre.getZ() + z);
            if (this.lockLine.get() && !lineTarget(destination, this.originX, this.originZ, this.heading)) continue;
            BlockHitResult hit = ScaffoldModule.findHit(mc, destination, 4.5, 3);
            if (hit == null) continue;
            Rotation rotation = Rotation.lookingAt(hit.getLocation(), mc.player.getEyePosition());
            double cost = ScaffoldModule.angularCost(mc.player.getYRot(), mc.player.getXRot(), rotation);
            if (cost < bestCost) {
               best = hit;
               bestCost = cost;
            }
         }
      }
      return best;
   }

   private boolean heldBlock(Minecraft mc) {
      if (mc.player.getMainHandItem().isEmpty() || !(mc.player.getMainHandItem().getItem() instanceof BlockItem item)) return false;
      return !(item.getBlock() instanceof FallingBlock) && !(item.getBlock() instanceof EntityBlock)
         && item.getBlock().defaultBlockState().isCollisionShapeFullBlock(mc.level, mc.player.blockPosition());
   }

   /** True only for the exact native path replaced by manualUse. */
   public boolean handlesUse() {
      Minecraft mc = Minecraft.getInstance();
      return this.isEnabled() && this.ready && this.usable(mc) && this.conflict() == null && this.heldBlock(mc);
   }

   private void manualUse(Minecraft mc, long now) {
      BlockHitResult hit = ScaffoldModule.viewHit(mc, 4.5);
      if (hit.getType() != HitResult.Type.BLOCK || hit.isInside()) return;
      BlockPos destination = hit.getBlockPos().relative(hit.getDirection());
      if (destination.getY() != this.bridgeY
         || (this.lockLine.get() && !lineTarget(destination, this.originX, this.originZ, this.heading))
         || !ScaffoldModule.validTarget(mc, destination)
         || !ScaffoldModule.validSupport(mc, hit.getBlockPos())) return;
      this.nextClick = now + this.clickDelay.get().longValue() * 1_000_000L;
      // Send the ACTUAL position/view before the manual interaction, since the next
      // ordinary client movement tick may be delayed. No virtual look or fake block.
      mc.player.connection.send(new PosRot(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
         mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision));
      if (mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit).consumesAction()) mc.player.swing(InteractionHand.MAIN_HAND);
   }

   private void onPlacement(BlockUseObservationEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (this.isEnabled() && this.world == mc.level && mc.player != null && !mc.player.onGround()
         && event.consumed() && event.predictedChange() && event.target().getY() == this.bridgeY
         && (!this.lockLine.get() || lineTarget(event.target(), this.originX, this.originZ, this.heading))) {
         BlockPos pos = event.target();
         if (!this.jumpPositions.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()))) return;
         this.jumpBlocks = this.jumpPositions.size();
         if (this.jumpBlocks == 3 && this.showCue.get()) info("Jump " + this.cycle + ": 3 observed blocks at fixed Y=" + this.bridgeY);
      }
   }

   private void releaseClock() {
      if (this.clockOwned && !clockConflict(true, ResetSpeedHelper.getSpeed(), this.ownedSpeed)) ResetSpeedHelper.resetSpeed();
      this.clockOwned = false;
   }

   static boolean lineTarget(BlockPos pos, int originX, int originZ, Direction heading) {
      if (heading.getStepY() != 0) return false;
      return heading.getStepX() != 0
         ? pos.getZ() == originZ && ((long)pos.getX() - originX) * heading.getStepX() >= 0
         : pos.getX() == originX && ((long)pos.getZ() - originZ) * heading.getStepZ() >= 0;
   }

   static boolean clockConflict(boolean owned, float actual, float lastSet) {
      return !Float.isFinite(actual) || Math.abs(actual - (owned ? lastSet : 1)) > 0.001F;
   }

   public State snapshot() {
      return new State(this.isEnabled(), this.slow && this.clockOwned, this.aimApplied,
         ResetSpeedHelper.getSpeed(), this.bridgeY, this.cycle, this.jumpBlocks,
         this.heading.name(), this.originX, this.originZ, this.lockLine.get());
   }

   public void status() {
      info(this.snapshot().toString());
   }

   public record State(boolean enabled, boolean slowed, boolean aimApplied, float clientSpeed,
                       int bridgeY, int jumpCycle, int observedJumpBlocks,
                       String heading, int originX, int originZ, boolean lineLocked) { }

   private static void info(String text) {
      SendDonationHelper.displayInfo("Scaffold Coach: " + text);
   }
}
