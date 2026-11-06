package net.skycore.client.module.movement;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.PlayerTickStartEvent;
import net.skycore.client.movement.speed.GrimSpeedBypass;
import net.skycore.client.movement.speed.SpeedAction;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.util.MovementHelper;
import net.skycore.client.util.SliderSetting;

/**
 * Speedv3: режим Nuclear [MetaHvH] как в Speed, плюс слой обхода Grim 2.3.66-2.3.71.
 *
 * База (1:1 с Nuclear): в воздухе держит скорость 0.358, домноженную на множитель
 * амплификатора Speed (0 -> 1.263, 1 -> 1.453, 2+ -> 1.652); на земле авто-прыжок.
 * Не работает на элитре, в воде и под водой.
 *
 * Почему флагало: Grim предсказывает смещение по предыдущему тику, а MetaHvH
 * ставит сразу целевую скорость на первом же тике после отрыва от земли, без
 * разгона, с постоянным значением и без учёта сета/урона/скользких блоков.
 * Обход это и закрывает: разгон, потолок за тик, джиттер, декларация позиции,
 * пауза после сета, отказ от буста при уроне, коллизии и скользком полу.
 */
public final class SpeedV3Module extends ClientModule {
   public static final SpeedV3Module INSTANCE = new SpeedV3Module();

   private final SliderSetting speed = this.add(new SliderSetting("Скорость", 0.358, 0.25, 0.6, 0.005));
   private final BooleanSetting autojump = this.add(new BooleanSetting("Авто-прыжок", true));

   private final BooleanSetting bypass = this.add(new BooleanSetting("Обход Grim", true));
   private final SliderSetting maxStep = this.add(new SliderSetting("Макс. скорость за тик", 0.42, 0.30, 0.60, 0.005));
   private final SliderSetting rampTicks = this.add(new SliderSetting("Разгон, тиков", 3, 0, 10, 1));
   private final SliderSetting jitter = this.add(new SliderSetting("Джиттер, %", 2.0, 0.0, 10.0, 0.5));
   private final SliderSetting setbackCooldown = this.add(new SliderSetting("Пауза после сета, мс", 1200, 0, 5000, 100));
   private final BooleanSetting positionPackets = this.add(new BooleanSetting("Позиционные пакеты", true));
   private final BooleanSetting jumpPacket = this.add(new BooleanSetting("Пакет перед прыжком", true));
   private final BooleanSetting stopOnHurt = this.add(new BooleanSetting("Не бустить при уроне", true));
   private final BooleanSetting skipSlippery = this.add(new BooleanSetting("Не бустить на скользких блоках", true));
   private final BooleanSetting skipCollision = this.add(new BooleanSetting("Не бустить при коллизии", false));

   private int airTicks;
   private long cooldownUntil;

   private SpeedV3Module() {
      super("Speedv3", "Nuclear MetaHvH + обход Grim 2.3.66-2.3.71", ModuleCategory.MOVEMENT);
      this.maxStep.visibleWhen(this.bypass::get);
      this.rampTicks.visibleWhen(this.bypass::get);
      this.jitter.visibleWhen(this.bypass::get);
      this.setbackCooldown.visibleWhen(this.bypass::get);
      this.positionPackets.visibleWhen(this.bypass::get);
      this.jumpPacket.visibleWhen(this.bypass::get);
      this.stopOnHurt.visibleWhen(this.bypass::get);
      this.skipSlippery.visibleWhen(this.bypass::get);
      this.skipCollision.visibleWhen(this.bypass::get);

      SkycoreClient.events().subscribe(PlayerTickStartEvent.class, this::onTickStart);
      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, this::onTickEnd);
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacketReceive);
   }

   @Override
   protected void onEnable() {
      this.airTicks = 0;
      this.cooldownUntil = 0L;
   }

   @Override
   protected void onDisable() {
      this.airTicks = 0;
      this.cooldownUntil = 0L;
   }

   private float amplifierMultiplier(LocalPlayer player) {
      MobEffectInstance effect = player.getEffect(MobEffects.SPEED);
      if (effect == null) {
         return 1.0F;
      }

      if (effect.getAmplifier() == 0) {
         return 1.263F;
      } else if (effect.getAmplifier() == 1) {
         return 1.453F;
      } else if (effect.getAmplifier() >= 2) {
         return 1.652F;
      }

      return 1.0F;
   }

   private void onTickStart(PlayerTickStartEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      LocalPlayer player = event.player();
      if (player == null || player.getAbilities().flying) {
         return;
      }

      if (player.onGround()) {
         this.airTicks = 0;
      } else {
         this.airTicks++;
      }

      if (player.isFallFlying() || player.isInWater() || player.isEyeInFluid(FluidTags.WATER)) {
         return;
      }

      if (player.onGround()) {
         return;
      }

      float target = (float)(this.speed.get() * (double)this.amplifierMultiplier(player));
      if (!this.bypass.get()) {
         SpeedAction.setSpeed(target);
         return;
      }

      if (!this.canBoost(player)) {
         return;
      }

      float effective = this.rampedSpeed(target);
      effective = this.applyJitter(effective);
      SpeedAction.setSpeed(effective);

      if (this.positionPackets.get()) {
         GrimSpeedBypass.sendPosition(player);
      }
   }

   private boolean canBoost(LocalPlayer player) {
      long now = System.nanoTime();
      if (now < this.cooldownUntil) {
         return false;
      }

      if (this.stopOnHurt.get() && player.hurtTime > 0) {
         return false;
      }

      if (this.skipCollision.get() && player.horizontalCollision) {
         return false;
      }

      if (this.skipSlippery.get() && this.onSlipperyBlock(player)) {
         return false;
      }

      return true;
   }

   private boolean onSlipperyBlock(LocalPlayer player) {
      if (player.level() == null) {
         return false;
      }

      BlockState state = player.level().getBlockState(player.blockPosition().below());
      return state.is(Blocks.ICE)
         || state.is(Blocks.PACKED_ICE)
         || state.is(Blocks.BLUE_ICE)
         || state.is(Blocks.FROSTED_ICE)
         || state.is(Blocks.SLIME_BLOCK)
         || state.is(Blocks.HONEY_BLOCK)
         || state.is(Blocks.SOUL_SAND);
   }

   private float rampedSpeed(float target) {
      int ticks = this.rampTicks.get().intValue();
      float value = target;
      if (ticks > 0 && this.airTicks <= ticks) {
         float progress = (float)this.airTicks / (float)(ticks + 1);
         value = target * progress;
      }

      float limit = this.maxStep.get().floatValue();
      return Math.min(value, limit);
   }

   private float applyJitter(float value) {
      double percent = this.jitter.get();
      if (percent <= 0.0) {
         return value;
      }

      double delta = ThreadLocalRandom.current().nextDouble(-percent, percent) / 100.0;
      return (float)(value * (1.0 + delta));
   }

   private void onTickEnd(PlayerTickEndEvent event) {
      if (!this.isEnabled() || !this.bypass.get() || !this.positionPackets.get()) {
         return;
      }

      LocalPlayer player = event.player();
      if (player == null || player.onGround()) {
         return;
      }

      if (!player.isFallFlying() && !player.isInWater() && this.canBoost(player)) {
         GrimSpeedBypass.sendPosition(player);
      }
   }

   private void onInput(PlayerInputEvent event) {
      if (!this.isEnabled() || !this.autojump.get()) {
         return;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || !player.onGround() || !MovementHelper.isMoving()) {
         return;
      }

      if (player.input == null || player.input.keyPresses.jump()) {
         return;
      }

      if (this.bypass.get()) {
         long now = System.nanoTime();
         if (now < this.cooldownUntil || (this.stopOnHurt.get() && player.hurtTime > 0)) {
            return;
         }

         if (this.jumpPacket.get()) {
            GrimSpeedBypass.sendStatus(player);
         }
      }

      player.jumpFromGround();
   }

   private void onPacketReceive(PacketReceiveEvent event) {
      if (!this.isEnabled() || !this.bypass.get() || event.isCancelled()) {
         return;
      }

      if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.cooldownUntil = System.nanoTime() + this.setbackCooldown.get().longValue() * 1000000L;
         this.airTicks = 0;
      }
   }
}
