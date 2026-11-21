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
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.ServerRotationManager;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.util.InventoryHelper;
import net.skycore.client.util.ResetSpeedHelper;
import net.skycore.client.util.SendDonationHelper;
import org.lwjgl.glfw.GLFW;

/** Flat, state-based Telly. Recording timing is descriptive, not a forced click script. */
final class TellyScaffoldController {
   enum Phase { IDLE, RUN, TAKEOFF, BUILD, RETURN, STOP }
   private Phase phase = Phase.IDLE;
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
   private int cycleTicks;
   private boolean backward;
   private int rowX, rowY, rowZ;
   private BlockPos firstGap;
   private int landingBlocks;
   private boolean braking, stopAfterLanding;
   private float approachYaw, approachPitch;
   private long nextUseNanos, lastFrameNanos, lastInputNanos;
   private boolean cameraFree;
   private float freeYaw, freePitch;
   private boolean fresh;
   private boolean airborne;
   private Vec3 lastPosition;
   private String lastMessage;
   private PlacementIntent intent;
   private record PlacementIntent(BlockPos target, BlockHitResult hit, float yaw, float pitch, Vec3 position, int tick) { }

   void reset() {
      this.clearCycle();
      this.lastMessage = null;
      this.nextUseNanos = 0;
   }

   private void clearCycle() {
      Minecraft mc = Minecraft.getInstance();
      if (this.cameraFree && mc != null && this.world == mc.level && this.player == mc.player && mc.player != null) {
         mc.player.setYRot(this.freeYaw);
         mc.player.setXRot(this.freePitch);
      }
      this.cameraFree = false;
      this.intent = null;
      this.lastFrameNanos = this.lastInputNanos = 0;
      this.phase = Phase.IDLE;
      this.world = this.player = null;
      this.heading = null;
      this.lastPosition = null;
      this.next = this.pending = this.firstGap = null;
      this.braking = this.stopAfterLanding = false;
      this.landingBlocks = 0;
      this.fresh = this.airborne = false;
      this.airTicks = this.blocks = this.launchTicks = this.cycleTicks = 0;

   }

   Phase phase() { return this.phase; }

   void pause(String reason, ScaffoldModule.TellySettings settings) {
      this.clearCycle();
      if (settings.diagnostics() && !reason.equals(this.lastMessage)) {
         SendDonationHelper.displayInfo("Telly: " + reason);
      }
      this.lastMessage = reason;
   }

   private String failure(Minecraft mc, ClientModule owner) {
      if (mc.player == null || mc.level == null || mc.gameMode == null) return "ожидание мира";
      if (mc.gui.screen() != null || mc.isPaused()
         || GLFW.glfwGetWindowAttrib(mc.getWindow().handle(), GLFW.GLFW_FOCUSED) != GLFW.GLFW_TRUE) return "пауза GUI/фокуса; закрой меню и удерживай W или S на платформе";
      if (mc.level.tickRateManager().isFrozen() || !Float.isFinite(mc.level.tickRateManager().tickrate())
         || Math.abs(mc.level.tickRateManager().tickrate() - 20) > 0.1) return "нужна обычная скорость tick rate 20; замороженный/ускоренный мир не поддерживается";
      if (!Float.isFinite(ResetSpeedHelper.getSpeed()) || Math.abs(ResetSpeedHelper.getSpeed() - 1) > 0.001) return "отключи замедление Coach/Timer: нужен клиент 1x";
      if (!Float.isFinite(mc.player.getYRot()) || !Float.isFinite(mc.player.getXRot())
         || !Double.isFinite(mc.player.getX()) || !Double.isFinite(mc.player.getY()) || !Double.isFinite(mc.player.getZ())) return "некорректная позиция/камера";
      Vec3 motion = mc.player.getDeltaMovement();
      if (!Double.isFinite(motion.x) || !Double.isFinite(motion.y) || !Double.isFinite(motion.z)) return "некорректная скорость игрока";
      if (!mc.player.isAlive() || mc.player.isSpectator() || mc.player.isPassenger() || mc.player.isFallFlying()
         || mc.player.getAbilities().flying || mc.player.isInWater() || mc.player.isInLava()) return "нужен обычный игрок без полёта, жидкости и транспорта";
      if (mc.player.isUsingItem() || mc.options.keyAttack.isDown() || mc.options.keyUse.isDown()) return "отпусти LMB/RMB для автоматических установок";
      if (RotationController.isActive() || RotationManager.INSTANCE.isRotating() || ServerRotationManager.INSTANCE.isRotating()
         || mc.getCameraEntity() != mc.player) return "другой контроллер камеры активен";
      for (ClientModule module : SkycoreClient.modules().enabled()) {
         if (module != owner && module.affectsGameplay()) return "отключи конфликтующий модуль " + module.getName();
      }
      if (this.world != null && (this.world != mc.level || this.player != mc.player)) return "мир/игрок изменился; новый старт с платформы";
      return null;
   }

   void input(Minecraft mc, PlayerInputEvent event, ClientModule owner, ScaffoldModule.TellySettings settings) {
      this.fresh = false;
      String error = this.failure(mc, owner);
      if (error != null) { this.pause(error, settings); return; }
      Input physical = event.keyPresses();
      if (physical.forward() == physical.backward() || physical.left() || physical.right() || physical.shift()) {
         this.pause("удерживай W или S; Space разрешён при ручном прыжке", settings);
         return;
      }
      if (this.phase == Phase.IDLE) {
         if (!mc.player.onGround()) return;
         BlockPos support = BlockPos.containing(mc.player.getX(), mc.player.getY() - 0.5, mc.player.getZ());
         if (!ScaffoldModule.validSupport(mc, support)) {
            this.pause("для старта встань на полный твёрдый блок", settings);
            return;
         }
         this.backward = physical.backward();
         this.heading = movementHeading(mc.player.getYRot(), this.backward);
         this.rowX = support.getX();
         this.rowY = support.getY();
         this.rowZ = support.getZ();
         this.freeYaw = mc.player.getYRot();
         this.freePitch = mc.player.getXRot();
         this.cameraFree = !settings.camLock();
         this.world = mc.level;
         this.player = mc.player;
         this.floorY = mc.player.getY();
         this.approachYaw = this.forwardYaw = mc.player.getYRot();
         this.approachPitch = this.forwardPitch = mc.player.getXRot();
         this.next = support.relative(this.heading);
         this.phase = Phase.RUN;
         this.lastMessage = null;
      }
      if (physical.backward() != this.backward) {
         this.pause("направление W/S изменилось; новый старт с платформы", settings);
         return;
      }
      this.lastInputNanos = System.nanoTime();
      double lateral = this.heading.getStepX() != 0 ? mc.player.getZ() - (this.rowZ + 0.5) : mc.player.getX() - (this.rowX + 0.5);
      if (Math.abs(lateral) > 0.8) {
         this.pause("выход из фиксированного ряда X/Z", settings);
         return;
      }
      if (this.lastPosition != null && this.lastPosition.distanceToSqr(mc.player.position()) > 2.25) {
         this.pause("скачок позиции; цикл сброшен", settings);
         return;
      }
      this.lastPosition = mc.player.position();
      if (mc.player.getY() < this.floorY - 0.15 || this.airTicks > 30) {
         this.pause("ниже моста; вернись на платформу для нового старта", settings);
         return;
      }
      this.fresh = true;
      if (this.phase == Phase.STOP) {
         event.setShift(true);
         event.clearMovement(false, true);
         mc.player.setSprinting(false);
         return;
      }
      if (!mc.player.onGround()) {
         if (this.phase == Phase.RUN) {
            // Accept a real manual takeoff, not an arbitrary walk off the edge.
            if (mc.player.getDeltaMovement().y <= 0.0) {
               this.pause("сход с края без прыжка; новый старт с платформы", settings);
               return;
            }
            this.beginJump(mc, settings);
         }
         this.airborne = true;
         this.airTicks++;
         if (this.phase == Phase.TAKEOFF) this.phase = Phase.BUILD;
      } else if (this.airborne) {
         this.phase = Phase.RETURN;
         this.airborne = false;
         this.pending = null;
      }
      if (this.phase == Phase.RUN) {
         // S is permission to cycle backwards, NOT a demand for vanilla backward
         // sprint. Turn the head along world travel first, then drive forward.
         if (!runAligned(mc.player.getYRot(), this.heading)) {
            event.clearMovement(false, true);
            mc.player.setSprinting(false);
            return;
         }
         // Rebase the next gap from current support on a long runway; don't retain
         // an unreachable target many blocks behind or beyond the player.
         BlockPos support = this.heading.getStepX() != 0
            ? new BlockPos(Mth.floor(mc.player.getX()), this.rowY, this.rowZ)
            : new BlockPos(this.rowX, this.rowY, Mth.floor(mc.player.getZ()));
         if (ScaffoldModule.validSupport(mc, support)) this.next = support.relative(this.heading);
         for (int i = 0; i < 8 && ScaffoldModule.validSupport(mc, this.next); i++) this.next = this.next.relative(this.heading);
         BlockPos probe = BlockPos.containing(mc.player.getX() + this.heading.getStepX() * settings.jumpDistance(),
            this.next.getY(), mc.player.getZ() + this.heading.getStepZ() * settings.jumpDistance());
         if (!ScaffoldModule.validSupport(mc, probe)) {
            if (this.slot(mc, settings) < 0) {
               event.clearMovement(false, true);
               this.pause("нет полных строительных блоков в доступном слоте", settings);
               return;
            }
            if (settings.autoJump() || physical.jump()) this.beginJump(mc, settings);
         }
      }
      if (this.phase == Phase.TAKEOFF && ++this.launchTicks > 5) {
         this.pause("прыжок не произошёл: проверь препятствие над головой", settings);
         event.clearMovement(false, true);
         return;
      }
      if (this.phase != Phase.RUN && ++this.cycleTicks > 80) {
         this.pause("таймаут цикла; новый старт с платформы", settings);
         return;
      }
      if (settings.smartStop() && this.airborne && this.firstGap != null) {
         this.updateLanding(mc, settings);
         if (this.phase == Phase.RETURN && this.blocks >= this.landingBlocks
            && TellyLandingPlan.shortLanding(this.landingBlocks, settings.blocks())) {
            this.stopAfterLanding = this.braking = true;
         }
      }
      if (this.phase == Phase.STOP || (this.phase == Phase.RETURN && mc.player.onGround()) || this.braking) {
         event.setShift(this.braking || this.phase == Phase.STOP);

         event.clearMovement(false, true);
         mc.player.setSprinting(false);
         return;
      }
      Vec3 worldMove = new Vec3(this.heading.getStepX(), 0, this.heading.getStepZ());
      Vec2 local = ScaffoldModule.localInput(worldMove, mc.player.getYRot());
      boolean jump = mc.player.onGround() && this.phase == Phase.TAKEOFF && (settings.autoJump() || physical.jump());
      boolean sprint = settings.sprint() && (this.phase == Phase.RUN || jump);
      event.setMoveVector(local);
      event.setKeyPresses(new Input(local.y > 0.01F, local.y < -0.01F,
         local.x > 0.01F, local.x < -0.01F, jump, false, sprint));
      mc.player.setSprinting(sprint);
   }

   private void beginJump(Minecraft mc, ScaffoldModule.TellySettings settings) {
      this.forwardYaw = settings.returnToView() ? (this.cameraFree ? this.freeYaw : this.approachYaw)
         : headingYaw(this.heading) + (this.backward ? 180 : 0);
      this.forwardPitch = settings.returnToView() ? (this.cameraFree ? this.freePitch : this.approachPitch) : settings.returnPitch();
      this.phase = Phase.TAKEOFF;
      this.airTicks = this.blocks = this.launchTicks = this.cycleTicks = 0;

      this.pending = null;
      this.airborne = false;
      this.firstGap = this.next;
      this.landingBlocks = settings.blocks();
      this.braking = this.stopAfterLanding = false;
   }

   private void updateLanding(Minecraft mc, ScaffoldModule.TellySettings settings) {
      Vec3 motion = mc.player.getDeltaMovement();
      var predicted = TellyLandingPlan.predict(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
         motion.x, motion.y, motion.z, this.floorY, this.heading.getStepX(), this.heading.getStepZ(), this.braking);
      if (predicted.reachedPlane()) this.landingBlocks = TellyLandingPlan.requiredBlocks(
         this.firstGap.getX(), this.firstGap.getZ(), Mth.floor(predicted.x()), Mth.floor(predicted.z()),
         this.heading.getStepX(), this.heading.getStepZ(), settings.blocks());
   }

   private void finishBuild(ScaffoldModule.TellySettings settings) {
      this.phase = Phase.RETURN;
      this.stopAfterLanding = settings.smartStop() && TellyLandingPlan.shortLanding(this.landingBlocks, settings.blocks());
      this.braking = this.stopAfterLanding;
   }

   void tick(Minecraft mc, ClientModule owner, ScaffoldModule.TellySettings settings) {
      // Check GUI even when input ticks stopped, but do not permanently latch HALT.
      String error = this.failure(mc, owner);
      if (error != null) { this.pause(error, settings); return; }
      if (!this.fresh) return;
      this.fresh = false;
      // Movement/air counters belong to input ticks; aim/CPS belong to frame time.
   }

   void frame(Minecraft mc, ClientModule owner, ScaffoldModule.TellySettings settings, long now) {
      // Rendering validates lifecycle/free-look only; it never places or emits movement packets.
      if (this.phase == Phase.IDLE) return;
      String error = this.failure(mc, owner);
      if (error != null) { this.pause(error, settings); return; }
      if (this.lastInputNanos == 0 || now - this.lastInputNanos > 250_000_000L)
         this.pause("нет свежего ввода; цикл сброшен", settings);
   }

   void beforeMovement(Minecraft mc, ClientModule owner, ScaffoldModule.TellySettings settings, long now) {
      this.intent = null;
      if (this.phase == Phase.IDLE) return;
      String error = this.failure(mc, owner);
      if (error != null) { this.pause(error, settings); return; }
      if (this.lastInputNanos == 0 || now - this.lastInputNanos > 250_000_000L) {
         this.pause("нет свежего ввода; цикл сброшен", settings);
         return;
      }
      if (this.cameraFree != !settings.camLock()) {
         this.freeYaw = mc.player.getYRot();
         this.freePitch = mc.player.getXRot();
         this.cameraFree = !settings.camLock();
      }
      float ticks = this.lastFrameNanos == 0 ? 1 : (float)Math.min(1, Math.max(0, (now - this.lastFrameNanos) / 50_000_000.0));
      this.lastFrameNanos = now;
      if (this.phase == Phase.RUN) {
         this.rotate(mc, headingYaw(this.heading), this.approachPitch, settings.turnSpeed() * ticks, settings.smoothing());
         return;
      }
      if (this.phase == Phase.STOP) return;
      if (this.phase == Phase.RETURN) {
         if (this.rotate(mc, this.forwardYaw, this.forwardPitch, settings.returnSpeed() * ticks, settings.smoothing())
            && mc.player.onGround()) {
            this.phase = this.stopAfterLanding ? Phase.STOP : Phase.RUN;
            if (this.stopAfterLanding && settings.diagnostics())
               SendDonationHelper.displayInfo("Telly Smart Stop: короткий прыжок завершён; отпусти W/S для нового старта.");
            this.approachYaw = this.forwardYaw;
            this.approachPitch = this.forwardPitch;
            this.airTicks = this.cycleTicks = 0;
         }
         return;
      }
      if (this.pending != null) {
         if (ScaffoldModule.validSupport(mc, this.pending)) {
            this.blocks++;
            this.next = this.pending.relative(this.heading);
            this.pending = null;
            if (this.blocks >= settings.blocks()) { this.finishBuild(settings); return; }
         } else {
            // Timeout measured in real time, not number of rendered frames.
            if (now - this.nextUseNanos > 250_000_000L) this.pause("блок не появился локально", settings);
            return;
         }
      }
      if (this.phase != Phase.BUILD || mc.player.onGround() || this.next == null) return;
      if (!flatTarget(this.next, this.rowX, this.rowY, this.rowZ, this.heading)) { this.pause("защита фиксированной высоты/ряда", settings); return; }
      if (settings.smartStop() && this.blocks >= this.landingBlocks) {
         if (mc.player.getDeltaMovement().y < 0) this.finishBuild(settings);
         return;
      }
      BlockHitResult target = ScaffoldModule.findTellyHit(mc, this.next, this.heading, settings.reach(), settings.samples());
      if (target == null) return;
      Rotation look = Rotation.lookingAt(target.getLocation(), mc.player.getEyePosition());
      this.rotate(mc, look.getYaw(), look.getPitch(), settings.turnSpeed() * ticks, settings.smoothing());
      // Recast AFTER rotation, not next game tick. Never use the old camera ray.
      BlockHitResult actual = ScaffoldModule.viewHit(mc, settings.reach());
      if (!ScaffoldModule.sameFace(actual, target) || !placementHit(actual, this.next, this.rowX, this.rowY, this.rowZ, this.heading)
         || this.airTicks < settings.startTick() || !cpsReady(now, this.nextUseNanos)
         || !ScaffoldModule.validTarget(mc, this.next)) return;
      this.intent = new PlacementIntent(this.next, target, mc.player.getYRot(), mc.player.getXRot(),
         mc.player.position(), mc.player.tickCount);
   }

   void afterMovement(Minecraft mc, ClientModule owner, ScaffoldModule.TellySettings settings, long now) {
      PlacementIntent prepared = this.intent;
      this.intent = null; // One movement callback can authorize at most one use.
      if (prepared == null) return;
      String error = this.failure(mc, owner);
      if (error != null) { this.pause(error, settings); return; }
      if (this.phase != Phase.BUILD || this.next == null || !this.next.equals(prepared.target())
         || mc.player.onGround() || mc.player.tickCount != prepared.tick()
         || mc.player.position().distanceToSqr(prepared.position()) > 1.0e-8
         || !sameView(prepared.yaw(), prepared.pitch(), mc.player.getYRot(), mc.player.getXRot())
         || !cpsReady(now, this.nextUseNanos) || !ScaffoldModule.validTarget(mc, prepared.target())
         || !ScaffoldModule.validSupport(mc, prepared.hit().getBlockPos())) return;
      // Fresh world/raycast after ordinary sendPosition. No injected PosRot and
      // no reuse of a face prepared in a different position/tick/view.
      BlockHitResult actual = ScaffoldModule.viewHit(mc, settings.reach());
      if (!ScaffoldModule.sameFace(actual, prepared.hit())
         || !placementHit(actual, prepared.target(), this.rowX, this.rowY, this.rowZ, this.heading)) return;
      int slot = this.slot(mc, settings);
      if (slot < 0) { this.pause("строительные блоки закончились", settings); return; }
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      try {
         InventoryHelper.selectHotbarSlot(slot);
         this.nextUseNanos = now + cpsInterval(settings.cps());
         if (mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, actual).consumesAction()) {
            if (settings.swing()) mc.player.swing(InteractionHand.MAIN_HAND);
            this.pending = prepared.target();
         }
      } finally {
         InventoryHelper.selectHotbarSlot(originalSlot);
      }
   }

   static boolean sameView(float yaw, float pitch, float actualYaw, float actualPitch) {
      return Math.abs(Mth.wrapDegrees(yaw - actualYaw)) < 0.01F && Math.abs(pitch - actualPitch) < 0.01F;
   }

   boolean freeCameraActive() {
      Minecraft mc = Minecraft.getInstance();
      return this.cameraFree && this.phase != Phase.IDLE && mc != null && mc.player != null
         && this.world == mc.level && this.player == mc.player && mc.gui.screen() == null;
   }
   float freeYaw() { return this.freeYaw; }
   float freePitch() { return this.freePitch; }
   boolean mouseTurn(double yaw, double pitch) {
      if (!this.freeCameraActive()) return false;
      this.freeYaw += (float)(yaw * 0.15);
      this.freePitch = Mth.clamp(this.freePitch + (float)(pitch * 0.15), -90, 90);
      return true;
   }

   private int slot(Minecraft mc, ScaffoldModule.TellySettings settings) {
      int selected = mc.player.getInventory().getSelectedSlot();
      if (this.buildingSlot(mc, selected)) return selected;
      if (settings.autoSwitch()) for (int slot = 0; slot < 9; slot++) if (this.buildingSlot(mc, slot)) return slot;
      return -1;
   }

   private boolean buildingSlot(Minecraft mc, int slot) {
      var stack = mc.player.getInventory().getItem(slot);
      return !stack.isEmpty() && stack.getItem() instanceof BlockItem item
         && !(item.getBlock() instanceof FallingBlock) && !(item.getBlock() instanceof EntityBlock)
         && item.getBlock().defaultBlockState().isCollisionShapeFullBlock(mc.level, this.next);
   }

   private boolean rotate(Minecraft mc, float yaw, float pitch, float limit, float smoothing) {
      mc.player.setYRot(ScaffoldModule.stepAngle(mc.player.getYRot(), yaw, limit, smoothing));
      mc.player.setXRot(Mth.clamp(mc.player.getXRot() + ScaffoldModule.smoothStep(pitch - mc.player.getXRot(), limit, smoothing), -90, 90));
      mc.player.setYHeadRot(mc.player.getYRot());
      return Math.abs(Mth.wrapDegrees(yaw - mc.player.getYRot())) < 1 && Math.abs(pitch - mc.player.getXRot()) < 1;
   }

   static long cpsInterval(double cps) { return (long)Math.ceil(1_000_000_000.0 / Math.max(1, Math.min(15, cps))); }
   static boolean cpsReady(long now, long next) { return next == 0 || now - next >= 0; }
   static boolean flatTarget(BlockPos target, int x, int y, int z, Direction heading) {
      return target.getY() == y && (heading.getStepX() != 0 ? target.getZ() == z : target.getX() == x);
   }
   static boolean placementHit(BlockHitResult hit, BlockPos target, int x, int y, int z, Direction heading) {
      return hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK && !hit.isInside()
         && hit.getDirection() == heading && heading != Direction.UP && heading != Direction.DOWN
         && hit.getBlockPos().relative(hit.getDirection()).equals(target) && flatTarget(target, x, y, z, heading)
         && centralHit(hit);
   }
   static boolean centralHit(BlockHitResult hit) {
      double u = hit.getLocation().y - hit.getBlockPos().getY();
      double v = hit.getDirection().getStepX() != 0
         ? hit.getLocation().z - hit.getBlockPos().getZ() : hit.getLocation().x - hit.getBlockPos().getX();
      return u >= 0.2 && u <= 0.8 && v >= 0.2 && v <= 0.8;
   }
   static Direction movementHeading(float yaw, boolean backward) {
      Direction facing = cardinal(yaw);
      return backward ? facing.getOpposite() : facing;
   }
   static boolean runAligned(float yaw, Direction heading) {
      return Math.abs(Mth.wrapDegrees(yaw - headingYaw(heading))) < 5;
   }

   static float headingYaw(Direction direction) {
      return switch (direction) {
         case SOUTH -> 0;
         case WEST -> 90;
         case NORTH -> 180;
         default -> -90;
      };
   }

   static Direction cardinal(float yaw) {
      return switch (Math.floorMod(Math.round(yaw / 90), 4)) {
         case 0 -> Direction.SOUTH;
         case 1 -> Direction.WEST;
         case 2 -> Direction.NORTH;
         default -> Direction.EAST;
      };
   }
}
