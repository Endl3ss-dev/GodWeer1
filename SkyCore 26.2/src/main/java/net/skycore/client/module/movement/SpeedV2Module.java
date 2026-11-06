package net.skycore.client.module.movement;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.ClientTickEvent;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PacketSendEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.movement.speed.GrimSpeedBypass;
import net.skycore.client.movement.speed.SpeedTimer;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.util.MovementHelper;
import net.skycore.client.util.SliderSetting;

/**
 * Speedv2: режим Releon [ReallyWorld] (фазовый буст таймера + задержка пакетов понга)
 * с профилями обхода GrimAC 2.3.66 - 2.3.71.
 */
public final class SpeedV2Module extends ClientModule {
   public static final SpeedV2Module INSTANCE = new SpeedV2Module();

   private static final int CHARGING = 0;
   private static final int BOOSTING = 1;

   private final BooleanSetting bypass = this.add(new BooleanSetting("Обход Grim", true));
   private final ModeSetting grimVersion = this.add(
      new ModeSetting("Grim версия", GrimSpeedBypass.VERSION_2371, GrimSpeedBypass.VERSIONS));
   private final SliderSetting boostMultiplier = this.add(new SliderSetting("Множитель буста", 1.8, 1.0, 3.0, 0.05));
   private final SliderSetting chargeMultiplier = this.add(new SliderSetting("Множитель заряда", 0.2, 0.05, 1.0, 0.01));
   private final SliderSetting chargeDuration = this.add(new SliderSetting("Заряд, мс", 1250, 200, 4000, 50));
   private final SliderSetting boostDuration = this.add(new SliderSetting("Буст, мс", 2400, 200, 6000, 50));
   private final SliderSetting fullBoostJumps = this.add(new SliderSetting("Прыжков до сброса", 3, 1, 10, 1));
   private final SliderSetting smoothing = this.add(new SliderSetting("Сглаживание, мс", 250, 0, 1000, 25));
   private final SliderSetting setbackCooldown = this.add(new SliderSetting("Пауза после флага, мс", 1500, 0, 5000, 100));
   private final BooleanSetting positionPackets = this.add(new BooleanSetting("Позиционные пакеты", true));
   private final BooleanSetting stopOnHurt = this.add(new BooleanSetting("Не бустить при уроне", true));
   private final BooleanSetting jumpPacket = this.add(new BooleanSetting("Пакет перед прыжком", true));

   private final Queue<ServerboundPongPacket> delayedPongs = new ConcurrentLinkedQueue<>();
   private int phase = CHARGING;
   private boolean active;
   private boolean airborne;
   private boolean releasingPongs;
   private int completedJumps;
   private int groundTicks;
   private long phaseStartedAt;
   private long cooldownUntil;

   private SpeedV2Module() {
      super("Speedv2", "Releon ReallyWorld + обход Grim 2.3.66-2.3.71", ModuleCategory.MOVEMENT);
      this.grimVersion.visibleWhen(this.bypass::get);
      this.smoothing.visibleWhen(this.bypass::get);
      this.setbackCooldown.visibleWhen(this.bypass::get);
      this.positionPackets.visibleWhen(this.bypass::get);
      this.stopOnHurt.visibleWhen(this.bypass::get);
      this.jumpPacket.visibleWhen(this.bypass::get);

      SkycoreClient.events().subscribe(ClientTickEvent.class, this::onClientTick);
      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, this::onTickEnd);
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
      SkycoreClient.events().subscribe(PacketSendEvent.class, this::onPacketSend);
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacketReceive);
   }

   @Override
   protected void onEnable() {
      this.stopCycle();
      this.cooldownUntil = 0L;
   }

   @Override
   protected void onDisable() {
      this.stopCycle();
      SpeedTimer.clear();
   }

   private GrimSpeedBypass.Profile profile() {
      return GrimSpeedBypass.profile(this.grimVersion.get());
   }

   private float effectiveBoostMultiplier() {
      float value = this.boostMultiplier.get().floatValue();
      if (this.bypass.get()) {
         value = Math.min(value, this.profile().maxMultiplier);
      }

      return Math.max(1.0F, value);
   }

   private long effectiveBoostDurationMs() {
      long value = this.boostDuration.get().longValue();
      if (this.bypass.get()) {
         value = Math.min(value, this.profile().maxBoostMs);
      }

      return Math.max(100L, value);
   }

   private void onClientTick(ClientTickEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || !MovementHelper.isMoving()) {
         this.stopCycle();
         SpeedTimer.clear();
         return;
      }

      long now = System.nanoTime();
      if (!this.active) {
         this.active = true;
         this.beginCharging(now);
      }

      if (this.phase == CHARGING && this.elapsedMs(now) >= this.chargeDuration.get().longValue()) {
         this.beginBoosting(now);
      } else if (this.phase == BOOSTING && this.elapsedMs(now) >= this.effectiveBoostDurationMs()) {
         this.beginCharging(now);
      }

      SpeedTimer.set(this.currentMultiplier(now));

      if (this.phase == BOOSTING && this.shouldDeclarePosition() && this.currentMultiplier(now) > 1.0F) {
         GrimSpeedBypass.sendPosition(player);
      }
   }

   private long elapsedMs(long now) {
      return (now - this.phaseStartedAt) / 1000000L;
   }

   private boolean shouldDeclarePosition() {
      return this.bypass.get() && (this.profile().positionPackets || this.positionPackets.get());
   }

   private float currentMultiplier(long now) {
      if (now < this.cooldownUntil) {
         return 1.0F;
      }

      if (this.phase == CHARGING) {
         return this.chargeMultiplier.get().floatValue();
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && this.bypass.get() && this.stopOnHurt.get() && player.hurtTime > 0) {
         return 1.0F;
      }

      float target = this.effectiveBoostMultiplier();
      long smoothingMs = this.smoothing.get().longValue();
      if (!this.bypass.get() || smoothingMs <= 0L) {
         return target;
      }

      long elapsed = this.elapsedMs(now);
      if (elapsed >= smoothingMs) {
         return target;
      }

      float progress = (float)elapsed / (float)smoothingMs;
      return 1.0F + (target - 1.0F) * progress;
   }

   private void onTickEnd(PlayerTickEndEvent event) {
      if (!this.isEnabled() || !this.active || this.phase != BOOSTING) {
         return;
      }

      LocalPlayer player = event.player();
      if (player == null) {
         return;
      }

      if (player.verticalCollision) {
         if (this.airborne) {
            this.airborne = false;
            this.completedJumps++;
         }
      } else {
         this.airborne = true;
         Vec3 velocity = player.getDeltaMovement();
         if (this.completedJumps >= this.fullBoostJumps.get().intValue() && velocity.y <= 0.0) {
            this.beginCharging(System.nanoTime());
         }
      }
   }

   private void onInput(PlayerInputEvent event) {
      if (!this.isEnabled() || !this.active || this.phase != BOOSTING) {
         this.groundTicks = 0;
         return;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      Input input = event.keyPresses();
      boolean moving = input.forward() || input.backward() || input.left() || input.right();
      if (player == null || !moving) {
         this.groundTicks = 0;
         return;
      }

      this.groundTicks = player.verticalCollision ? this.groundTicks + 1 : 0;
      event.setSprint(true);
      if (this.groundTicks > 0) {
         if (this.bypass.get() && this.jumpPacket.get()) {
            GrimSpeedBypass.sendStatus(player);
         }

         event.setJump(true);
      }
   }

   private void onPacketSend(PacketSendEvent event) {
      if (!this.isEnabled() || this.releasingPongs || !this.active || event.isCancelled()) {
         return;
      }

      if (event.getPacket() instanceof ServerboundPongPacket packet) {
         this.delayedPongs.add(packet);
         event.cancel();
      }
   }

   private void onPacketReceive(PacketReceiveEvent event) {
      if (!this.isEnabled() || !this.active || event.isCancelled()) {
         return;
      }

      if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         long now = System.nanoTime();
         this.beginCharging(now);
         if (this.bypass.get()) {
            this.cooldownUntil = now + this.setbackCooldown.get().longValue() * 1000000L;
         }
      }
   }

   private void beginCharging(long now) {
      this.flushPongs();
      this.phase = CHARGING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = now;
   }

   private void beginBoosting(long now) {
      this.phase = BOOSTING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = now;
   }

   private void stopCycle() {
      this.flushPongs();
      this.active = false;
      this.phase = CHARGING;
      this.airborne = false;
      this.completedJumps = 0;
      this.groundTicks = 0;
      this.phaseStartedAt = 0L;
   }

   private void flushPongs() {
      ServerboundPongPacket packet;
      while ((packet = this.delayedPongs.poll()) != null) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && player.connection != null) {
            this.releasingPongs = true;

            try {
               player.connection.send(packet);
            } finally {
               this.releasingPongs = false;
            }
         }
      }
   }
}
