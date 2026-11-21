package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
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
   enum Phase { IDLE, RUN, TAKEOFF, BUILD, RETURN }
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
   private int rowY, rowZ;
   private long nextUseNanos, lastFrameNanos, lastInputNanos;
   private boolean cameraFree;
   private float freeYaw, freePitch;
   private boolean fresh;
   private boolean airborne;
   private Vec3 lastPosition;
   private String lastMessage;

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
      this.lastFrameNanos = this.lastInputNanos = 0;
      this.phase = Phase.IDLE;
      this.world = this.player = null;
      this.heading = null;
      this.lastPosition = null;
      this.next = this.pending = null;
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
      if (mc.getSingleplayerServer() == null) return "пока поддерживается только одиночный мир";
      if (mc.gui.screen() != null || mc.isPaused()
         || GLFW.glfwGetWindowAttrib(mc.getWindow().handle(), GLFW.GLFW_FOCUSED) != GLFW.GLFW_TRUE) return "пауза GUI/фокуса; закрой меню и удерживай W или S на платформе";
      if (mc.level.tickRateManager().isFrozen() || !Float.isFinite(mc.level.tickRateManager().tickrate())
         || Math.abs(mc.level.tickRateManager().tickrate() - 20) > 0.1) return "нужно /tick rate 20";
      if (!Float.isFinite(ResetSpeedHelper.getSpeed()) || Math.abs(ResetSpeedHelper.getSpeed() - 1) > 0.001) return "отключи замедление Coach/Timer: нужен клиент 1x";
      if (!Float.isFinite(mc.player.getYRot()) || !Float.isFinite(mc.player.getXRot())
         || !Double.isFinite(mc.player.getX()) || !Double.isFinite(mc.player.getY()) || !Double.isFinite(mc.player.getZ())) return "некорректная позиция/камера";
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
         this.heading = xHeading(mc.player.getYRot(), this.backward);
         this.rowY = support.getY();
         this.rowZ = support.getZ();
         this.freeYaw = mc.player.getYRot();
         this.freePitch = mc.player.getXRot();
         this.cameraFree = !settings.camLock();
         this.world = mc.level;
         this.player = mc.player;
         this.floorY = mc.player.getY();
         this.forwardYaw = mc.player.getYRot();
         this.forwardPitch = mc.player.getXRot();
         this.next = support.relative(this.heading);
         this.phase = Phase.RUN;
         this.lastMessage = null;
      }
      if (physical.backward() != this.backward) {
         this.pause("направление W/S изменилось; новый старт с платформы", settings);
         return;
      }
      this.lastInputNanos = System.nanoTime();
      if (Math.abs(mc.player.getZ() - (this.rowZ + 0.5)) > 0.8) {
         this.pause("выход из фиксированного ряда X", settings);
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
         // Rebase the next gap from current support on a long runway; don't retain
         // an unreachable target many blocks behind or beyond the player.
         BlockPos support = new BlockPos(Mth.floor(mc.player.getX()), this.rowY, this.rowZ);
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
      if (this.phase == Phase.RETURN && mc.player.onGround()) {
         event.clearMovement(false, true);
         mc.player.setSprinting(false);
         return;
      }
      Vec3 worldMove = new Vec3(this.heading.getStepX(), 0, this.heading.getStepZ());
      Vec2 local = ScaffoldModule.localInput(worldMove, mc.player.getYRot());
      boolean jump = mc.player.onGround() && this.phase == Phase.TAKEOFF && (settings.autoJump() || physical.jump());
      boolean sprint = settings.sprint() && !this.backward && (this.phase == Phase.RUN || jump);
      event.setMoveVector(local);
      event.setKeyPresses(new Input(local.y > 0.01F, local.y < -0.01F,
         local.x > 0.01F, local.x < -0.01F, jump, false, sprint));
      mc.player.setSprinting(sprint);
   }

   private void beginJump(Minecraft mc, ScaffoldModule.TellySettings settings) {
      this.forwardYaw = settings.returnToView() ? (this.cameraFree ? this.freeYaw : mc.player.getYRot())
         : headingYaw(this.heading) + (this.backward ? 180 : 0);
      this.forwardPitch = settings.returnToView() ? (this.cameraFree ? this.freePitch : mc.player.getXRot()) : settings.returnPitch();
      this.phase = Phase.TAKEOFF;
      this.airTicks = this.blocks = this.launchTicks = this.cycleTicks = 0;

      this.pending = null;
      this.airborne = false;
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
      if (this.phase == Phase.RETURN) {
         if (this.rotate(mc, this.forwardYaw, this.forwardPitch, settings.returnSpeed() * ticks, settings.smoothing())
            && mc.player.onGround()) {
            this.phase = Phase.RUN;
            this.airTicks = this.cycleTicks = 0;
         }
         return;
      }
      if (this.pending != null) {
         if (ScaffoldModule.validSupport(mc, this.pending)) {
            this.blocks++;
            this.next = this.pending.relative(this.heading);
            this.pending = null;
            if (this.blocks >= settings.blocks()) { this.phase = Phase.RETURN; return; }
         } else {
            // Timeout measured in real time, not number of rendered frames.
            if (now - this.nextUseNanos > 250_000_000L) this.pause("блок не появился локально", settings);
            return;
         }
      }
      if (this.phase != Phase.BUILD || mc.player.onGround() || this.next == null) return;
      if (!flatXTarget(this.next, this.rowY, this.rowZ)) { this.pause("защита фиксированного Y/Z", settings); return; }
      BlockHitResult target = ScaffoldModule.findTellyHit(mc, this.next, this.heading, settings.reach(), settings.samples());
      if (target == null) return;
      Rotation look = Rotation.lookingAt(target.getLocation(), mc.player.getEyePosition());
      this.rotate(mc, look.getYaw(), look.getPitch(), settings.turnSpeed() * ticks, settings.smoothing());
      // Recast AFTER rotation, not next game tick. Never use the old camera ray.
      BlockHitResult actual = ScaffoldModule.viewHit(mc, settings.reach());
      if (!ScaffoldModule.sameFace(actual, target) || !placementHit(actual, this.next, this.rowY, this.rowZ)
         || this.airTicks < settings.startTick() || !cpsReady(now, this.nextUseNanos)
         || !ScaffoldModule.validTarget(mc, this.next)) return;
      int slot = this.slot(mc, settings);
      if (slot < 0) { this.pause("строительные блоки закончились", settings); return; }
      int originalSlot = mc.player.getInventory().getSelectedSlot();
      try {
         InventoryHelper.selectHotbarSlot(slot);
         mc.player.connection.send(new PosRot(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
            mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision));
         this.nextUseNanos = now + cpsInterval(settings.cps());
         if (mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, actual).consumesAction()) {
            if (settings.swing()) mc.player.swing(InteractionHand.MAIN_HAND);
            this.pending = this.next;
         }
      } finally {
         InventoryHelper.selectHotbarSlot(originalSlot);
      }
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
   static boolean flatXTarget(BlockPos target, int y, int z) { return target.getY() == y && target.getZ() == z; }
   static boolean placementHit(BlockHitResult hit, BlockPos target, int y, int z) {
      return hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK && !hit.isInside()
         && (hit.getDirection() == Direction.EAST || hit.getDirection() == Direction.WEST)
         && hit.getBlockPos().relative(hit.getDirection()).equals(target) && flatXTarget(target, y, z)
         && centralHit(hit);
   }
   static boolean centralHit(BlockHitResult hit) {
      double u = hit.getLocation().y - hit.getBlockPos().getY();
      double v = hit.getLocation().z - hit.getBlockPos().getZ();
      return u >= 0.2 && u <= 0.8 && v >= 0.2 && v <= 0.8;
   }
   static Direction xHeading(float yaw, boolean backward) {
      double x = -Math.sin(Math.toRadians(yaw));
      if (Math.abs(x) < 0.0001) x = 1; // Facing Z: deterministic X fallback.
      if (backward) x = -x;
      return x < 0 ? Direction.WEST : Direction.EAST;
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
