package net.skycore.client.movement.speed;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PacketSendEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.SpeedModule;

/**
 * Releon Speed (releon/ru/api/module/impl/movement/Speed.java, 388 строк).
 * Режимы: Vanilla, Grim, ReallyWorld, HolyWorld, FunTime, SpookyTime.
 * Фазовые бусты ReallyWorld/HolyWorld/FunTime/SpookyTime меняют таймер (DeltaTracker),
 * как DeltaTrackerTimerMixin источника, и задерживают пакеты понга.
 */
public final class ReleonSpeed extends SpeedHandler {
   private static final int REALLY_WORLD = 0;
   private static final int HOLY_WORLD = 1;
   private static final int FUN_TIME = 2;
   private static final int SPOOKY_TIME = 3;
   private static final int CHARGING = 0;
   private static final int BOOSTING = 1;

   private final BoostCycle[] cycles = new BoostCycle[]{
      new BoostCycle(REALLY_WORLD), new BoostCycle(HOLY_WORLD), new BoostCycle(FUN_TIME), new BoostCycle(SPOOKY_TIME)
   };

   public ReleonSpeed(SpeedModule module) {
      super(module);
   }

   @Override
   public void onEnable() {
      this.stopAllCycles();
   }

   @Override
   public void onDisable() {
      this.stopAllCycles();
   }

   @Override
   public void onTickStart(LocalPlayer player) {
      if (this.module.releonMode("Vanilla")) {
         SpeedAction.setSpeed(this.module.releonSpeed() / 3.0);
      }
   }

   @Override
   public void onTravelPre(LocalPlayer player) {
      if (this.module.releonMode("Grim") && SpeedAction.hasMovementInput()) {
         int collisions = SpeedAction.countCollisions(0.5, false);
         SpeedAction.addVelocity(SpeedAction.forward(0.07 * (double)collisions));
      }
   }

   @Override
   public void onClientTick() {
      BoostCycle active = this.activeCycle();
      this.stopInactiveCycles(active);

      if (active != null) {
         LocalPlayer player = SpeedAction.player();
         if (player != null && SpeedAction.isMoving()) {
            active.update(System.nanoTime());
         } else {
            active.stop();
         }

         SpeedTimer.set(active.currentMultiplier());
      } else {
         SpeedTimer.clear();
      }
   }

   @Override
   public void onTickEnd(LocalPlayer player) {
      BoostCycle active = this.activeCycle();
      if (active != null) {
         active.tick(player);
      }
   }

   @Override
   public void onInput(PlayerInputEvent event) {
      BoostCycle active = this.activeCycle();
      if (active != null) {
         active.applyInput(SpeedAction.player(), event);
      }
   }

   @Override
   public void onPacketSend(PacketSendEvent event) {
      BoostCycle active = this.activeCycle();
      if (active != null) {
         active.handleSend(event);
      }
   }

   @Override
   public void onPacketReceive(PacketReceiveEvent event) {
      BoostCycle active = this.activeCycle();
      if (active != null) {
         active.handleReceive(event);
      }
   }

   @Override
   public boolean hasTimerOverride() {
      BoostCycle active = this.activeCycle();
      return active != null && active.active;
   }

   @Override
   public float timerMultiplier() {
      BoostCycle active = this.activeCycle();
      return active == null ? 1.0F : active.currentMultiplier();
   }

   private BoostCycle activeCycle() {
      if (this.module.releonMode("ReallyWorld")) {
         return this.cycles[REALLY_WORLD];
      }

      if (this.module.releonMode("HolyWorld")) {
         return this.cycles[HOLY_WORLD];
      }

      if (this.module.releonMode("FunTime")) {
         return this.cycles[FUN_TIME];
      }

      return this.module.releonMode("SpookyTime") ? this.cycles[SPOOKY_TIME] : null;
   }

   private void stopInactiveCycles(BoostCycle active) {
      for (BoostCycle cycle : this.cycles) {
         if (cycle != active) {
            cycle.stop();
         }
      }
   }

   private void stopAllCycles() {
      for (BoostCycle cycle : this.cycles) {
         cycle.stop();
      }
   }

   private float chargeMultiplier(int mode) {
      switch (mode) {
         case REALLY_WORLD:
            return 0.2F;
         case HOLY_WORLD:
            return 0.1F;
         case FUN_TIME:
            return 0.2F;
         default:
            return 0.01F;
      }
   }

   private float boostMultiplier(int mode) {
      switch (mode) {
         case REALLY_WORLD:
            return 1.8F;
         case HOLY_WORLD:
            return 2.6F;
         case FUN_TIME:
            return 1.8F;
         default:
            return 4.2F;
      }
   }

   private long chargeDurationNanos(int mode) {
      switch (mode) {
         case REALLY_WORLD:
         case HOLY_WORLD:
            return 1250000000L;
         case FUN_TIME:
            return 1350000000L;
         default:
            return 1000000000L;
      }
   }

   private long maxBoostDurationNanos(int mode) {
      switch (mode) {
         case REALLY_WORLD:
         case HOLY_WORLD:
            return 2400000000L;
         case FUN_TIME:
            return 2000000000L;
         default:
            return 3500000000L;
      }
   }

   private int fullBoostJumps(int mode) {
      switch (mode) {
         case REALLY_WORLD:
            return 3;
         case HOLY_WORLD:
         case FUN_TIME:
            return 4;
         default:
            return 2;
      }
   }

   private final class BoostCycle {
      private final int boostMode;
      private final Queue<ServerboundPongPacket> delayedTransactions = new ConcurrentLinkedQueue<>();
      private int phase = CHARGING;
      private boolean active;
      private boolean airborne;
      private boolean releasingTransactions;
      private int completedJumps;
      private int groundTicks;
      private long phaseStartedAt;

      private BoostCycle(int boostMode) {
         this.boostMode = boostMode;
      }

      private void update(long now) {
         if (!this.active) {
            this.active = true;
            this.beginCharging();
         } else if (this.phase == CHARGING && now - this.phaseStartedAt >= ReleonSpeed.this.chargeDurationNanos(this.boostMode)) {
            this.beginBoosting();
         } else if (this.phase == BOOSTING && now - this.phaseStartedAt >= ReleonSpeed.this.maxBoostDurationNanos(this.boostMode)) {
            this.beginCharging();
         }
      }

      private void tick(LocalPlayer player) {
         if (player != null && this.active && this.phase == BOOSTING) {
            if (player.verticalCollision) {
               if (this.airborne) {
                  this.airborne = false;
                  this.completedJumps++;
               }
            } else {
               this.airborne = true;
               Vec3 velocity = player.getDeltaMovement();
               if (this.completedJumps >= ReleonSpeed.this.fullBoostJumps(this.boostMode) && velocity.y <= 0.0) {
                  this.beginCharging();
               }
            }
         }
      }

      private void applyInput(LocalPlayer player, PlayerInputEvent event) {
         Input input = event.keyPresses();
         boolean moving = input.forward() || input.backward() || input.left() || input.right();
         if (player != null && this.active && this.phase == BOOSTING && moving) {
            this.groundTicks = player.verticalCollision ? this.groundTicks + 1 : 0;
            event.setSprint(true);
            if (this.groundTicks > 0) {
               event.setJump(true);
            }
         } else {
            this.groundTicks = 0;
         }
      }

      private void handleSend(PacketSendEvent event) {
         if (this.releasingTransactions) {
            return;
         }

         if (this.active && event.getPacket() instanceof ServerboundPongPacket packet) {
            this.delayedTransactions.add(packet);
            event.cancel();
         }
      }

      private void handleReceive(PacketReceiveEvent event) {
         if (!this.releasingTransactions && this.active && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            this.beginCharging();
         }
      }

      private void beginCharging() {
         this.flushTransactions();
         this.phase = CHARGING;
         this.airborne = false;
         this.completedJumps = 0;
         this.groundTicks = 0;
         this.phaseStartedAt = System.nanoTime();
      }

      private void beginBoosting() {
         this.phase = BOOSTING;
         this.airborne = false;
         this.completedJumps = 0;
         this.groundTicks = 0;
         this.phaseStartedAt = System.nanoTime();
      }

      private void stop() {
         this.flushTransactions();
         this.active = false;
         this.phase = CHARGING;
         this.airborne = false;
         this.completedJumps = 0;
         this.groundTicks = 0;
         this.phaseStartedAt = 0L;
      }

      private float currentMultiplier() {
         if (!this.active) {
            return 1.0F;
         }

         return this.phase == CHARGING
            ? ReleonSpeed.this.chargeMultiplier(this.boostMode)
            : ReleonSpeed.this.boostMultiplier(this.boostMode);
      }

      private void flushTransactions() {
         ServerboundPongPacket packet;
         while ((packet = this.delayedTransactions.poll()) != null) {
            LocalPlayer player = SpeedAction.player();
            if (player != null && player.connection != null) {
               this.releasingTransactions = true;

               try {
                  player.connection.send(packet);
               } finally {
                  this.releasingTransactions = false;
               }
            }
         }
      }
   }
}
