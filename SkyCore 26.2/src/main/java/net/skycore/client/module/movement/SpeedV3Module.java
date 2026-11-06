package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickStartEvent;
import net.skycore.client.movement.speed.SpeedTimer;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.util.MovementHelper;
import net.skycore.client.util.SliderSetting;

/**
 * Speedv3: Nuclear [MetaHvH] с расширенным слоем обхода Grim (PredictionEngine / BlockProperties).
 *
 * Логика режима: точные атрибуты движения (бафы/замедление/крауч/использование предмета/спринт),
 * точный friction блока под ногами, раздельные ветки земли и воздуха, в воздухе скорость
 * ДОБАВЛЯЕТСЯ к предсказанному переносу (motion * friction), а не перезаписывается,
 * и держится в конверте Grim (softCap / hardCap) + yaw-демпфирование и микро-джиттер.
 */
public final class SpeedV3Module extends ClientModule {
   public static final SpeedV3Module INSTANCE = new SpeedV3Module();

   private final BooleanSetting autojump = this.add(new BooleanSetting("Авто-прыжок", true));

   private final BooleanSetting grimSmartBhop = this.add(new BooleanSetting("Smart Bhop", true));
   private final BooleanSetting grimPreserveY = this.add(new BooleanSetting("Preserve Y", true));
   private final BooleanSetting grimMicroJitter = this.add(new BooleanSetting("Micro Jitter", false));
   private final BooleanSetting grimYawDamp = this.add(new BooleanSetting("Yaw Damp", true));
   private final BooleanSetting grimExactAttributes = this.add(new BooleanSetting("Exact Attributes", true));

   private final SliderSetting grimAirFactor = this.add(new SliderSetting("Air Factor", 1.0, 0.85, 1.08, 0.005));
   private final SliderSetting grimGroundFactor = this.add(new SliderSetting("Ground Factor", 1.0, 0.90, 1.12, 0.005));
   private final SliderSetting grimOffset = this.add(new SliderSetting("Offset", 0.00045, 0.0, 0.0025, 0.00005));
   private final SliderSetting grimJumpFactor = this.add(new SliderSetting("Jump Factor", 1.0, 0.85, 1.15, 0.01));
   private final SliderSetting grimMomentum = this.add(new SliderSetting("Momentum", 1.0, 0.92, 1.02, 0.005));
   private final SliderSetting grimStrictness = this.add(new SliderSetting("Strictness", 0.00085, 0.0002, 0.0015, 0.00005));
   private final SliderSetting grimInputScale = this.add(new SliderSetting("Input Scale", 1.0, 0.92, 1.05, 0.005));
   private final SliderSetting grimFrictionMult = this.add(new SliderSetting("Friction Mult", 1.0, 0.97, 1.03, 0.005));
   private final SliderSetting grimBhopBoost = this.add(new SliderSetting("Bhop Boost", 1.0, 0.95, 1.08, 0.005));
   private final SliderSetting grimJitterStr = this.add(new SliderSetting("Jitter Strength", 0.000012, 0.0, 0.00008, 0.000002));
   private final SliderSetting grimSprintMult = this.add(new SliderSetting("Sprint Mult", 1.0, 0.98, 1.04, 0.005));
   private final SliderSetting grimFirstAirBoost = this.add(new SliderSetting("First Air Boost", 1.0, 0.97, 1.06, 0.005));

   private int grimAirTicks;
   private boolean grimWasOnGround;
   private boolean justJumped;
   private float grimLastYaw;
   private double grimLastHx;
   private double grimLastHz;
   private double grimPredCarryX;
   private double grimPredCarryZ;

   private SpeedV3Module() {
      super("Speedv3", "Nuclear MetaHvH + точный обход Grim (PredictionEngine)", ModuleCategory.MOVEMENT);
      SkycoreClient.events().subscribe(PlayerTickStartEvent.class, this::onTickStart);
   }

   @Override
   protected void onEnable() {
      LocalPlayer player = Minecraft.getInstance().player;
      this.grimAirTicks = 0;
      this.grimWasOnGround = player != null && player.onGround();
      this.justJumped = false;
      this.grimLastYaw = player != null ? player.getYRot() : 0.0F;
   }

   @Override
   protected void onDisable() {
      SpeedTimer.clear();
      this.grimAirTicks = 0;
      this.justJumped = false;
   }

   private void onTickStart(PlayerTickStartEvent event) {
      if (this.isEnabled()) {
         this.metaHvH();
      }
   }

   private void metaHvH() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || player.level() == null || player.input == null) {
         return;
      }

      if (player.isFallFlying()
         || player.isInWater()
         || player.isInLava()
         || player.getAbilities().flying
         || !MovementHelper.isMoving()) {
         this.grimAirTicks = 0;
         return;
      }

      boolean onGround = player.onGround();
      float yaw = player.getYRot();
      Vec3 motion = player.getDeltaMovement();
      double mx = motion.x;
      double my = motion.y;
      double mz = motion.z;

      float moveAttr = 0.1F;
      if (this.grimExactAttributes.get()) {
         MobEffectInstance speed = player.getEffect(MobEffects.SPEED);
         if (speed != null) {
            int amp = speed.getAmplifier();
            moveAttr *= 1.0F + 0.2F * (float)(amp + 1);
         }

         MobEffectInstance slowness = player.getEffect(MobEffects.SLOWNESS);
         if (slowness != null) {
            int amp = slowness.getAmplifier();
            moveAttr *= Math.max(0.0F, 1.0F - 0.15F * (float)(amp + 1));
         }

         if (player.isShiftKeyDown()) {
            moveAttr *= 0.3F;
         }

         if (player.isUsingItem()) {
            moveAttr *= 0.2F;
         }
      } else {
         MobEffectInstance speed = player.getEffect(MobEffects.SPEED);
         if (speed != null) {
            int amp = speed.getAmplifier();
            moveAttr *= 1.0F + 0.2F * (float)(amp + 1);
         }
      }

      boolean sprinting = player.isSprinting();
      if (sprinting) {
         moveAttr += moveAttr * 0.3F * this.grimSprintMult.get().floatValue();
      }

      Vec2 move = player.input.getMoveVector();
      float forward = move.y;
      float strafe = move.x;
      float inputLen = Mth.sqrt(forward * forward + strafe * strafe);
      if (inputLen > 1.0F) {
         forward /= inputLen;
         strafe /= inputLen;
      }

      forward *= this.grimInputScale.get().floatValue();
      strafe *= this.grimInputScale.get().floatValue();

      float yawRad = yaw * 0.017453292F;
      float sin = Mth.sin(yawRad);
      float cos = Mth.cos(yawRad);
      double inputX = (double)strafe * (double)cos - (double)forward * (double)sin;
      double inputZ = (double)forward * (double)cos + (double)strafe * (double)sin;
      double inputHyp = Math.sqrt(inputX * inputX + inputZ * inputZ);
      if (inputHyp > 1.0) {
         inputX /= inputHyp;
         inputZ /= inputHyp;
      }

      float blockFriction = 0.6F;
      BlockPos under = BlockPos.containing(
         Mth.floor(player.getX()),
         Mth.floor(player.getBoundingBox().minY) - 1,
         Mth.floor(player.getZ()));
      BlockState state = player.level().getBlockState(under);
      if (state != null) {
         Block block = state.getBlock();
         if (block == Blocks.ICE || block == Blocks.PACKED_ICE) {
            blockFriction = 0.98F;
         } else {
            blockFriction = block.getFriction();
         }
      }

      float tickFriction = (onGround ? blockFriction * 0.91F : 0.91F) * this.grimFrictionMult.get().floatValue();

      if (onGround) {
         this.grimAirTicks = 0;
         this.justJumped = false;
      } else {
         this.grimAirTicks++;
      }

      this.grimWasOnGround = onGround;

      double oldHyp = Math.sqrt(mx * mx + mz * mz);

      if (onGround) {
         float f = blockFriction;
         float groundAccel = (float)((double)(moveAttr * (0.21600002F / (f * f * f)) * this.grimGroundFactor.get().floatValue())
            + this.grimOffset.get());
         double keep = oldHyp * (0.6 * this.grimMomentum.get());
         double tx = inputX * (double)groundAccel + (oldHyp > 1.0E-8 ? mx * keep / oldHyp : 0.0);
         double tz = inputZ * (double)groundAccel + (oldHyp > 1.0E-8 ? mz * keep / oldHyp : 0.0);
         double hyp = Math.sqrt(tx * tx + tz * tz);
         float maxGround = (float)((double)(moveAttr * 1.3F * this.grimGroundFactor.get().floatValue()) + 0.02);
         if (hyp > (double)maxGround) {
            tx = tx / hyp * (double)maxGround;
            tz = tz / hyp * (double)maxGround;
         }

         this.applyHorizontal(player, tx, tz);
         if ((this.autojump.get() || this.grimSmartBhop.get())
            && (player.input.keyPresses.jump() || this.grimSmartBhop.get())) {
            this.doVanillaJump(player, yaw);
            this.justJumped = true;
         }
      } else {
         float airBase = sprinting ? 0.025999999F : 0.02F;
         float airAccel = (float)((double)(airBase * (moveAttr / 0.1F) * this.grimAirFactor.get().floatValue())
            + this.grimOffset.get());
         if (this.grimAirTicks <= 1 || this.justJumped) {
            airAccel = (float)((double)airAccel * this.grimFirstAirBoost.get() * this.grimBhopBoost.get());
            this.justJumped = false;
         }

         double predCarryX = mx * (double)tickFriction;
         double predCarryZ = mz * (double)tickFriction;
         this.grimPredCarryX = predCarryX;
         this.grimPredCarryZ = predCarryZ;

         double nx = predCarryX + inputX * (double)airAccel;
         double nz = predCarryZ + inputZ * (double)airAccel;
         nx *= this.grimMomentum.get();
         nz *= this.grimMomentum.get();

         double hyp = Math.sqrt(nx * nx + nz * nz);
         double predictedMax = Math.sqrt(predCarryX * predCarryX + predCarryZ * predCarryZ) + (double)airAccel + 3.0E-4;
         double softCap = predictedMax + this.grimStrictness.get();
         double hardCap = predictedMax + this.grimStrictness.get() * 1.8 + 4.0E-4;
         if (hyp > softCap) {
            double scale = softCap / hyp;
            nx = nx * 0.65 + nx * scale * 0.35;
            nz = nz * 0.65 + nz * scale * 0.35;
            hyp = Math.sqrt(nx * nx + nz * nz);
         }

         if (hyp > hardCap) {
            nx = nx / hyp * hardCap;
            nz = nz / hyp * hardCap;
         }

         if (this.grimYawDamp.get()) {
            float yawDelta = Math.abs(Mth.wrapDegrees(yaw - this.grimLastYaw));
            if (yawDelta > 25.0F && this.grimAirTicks > 2) {
               double damp = 1.0 - Math.min(0.12, (double)yawDelta / 800.0);
               nx *= damp;
               nz *= damp;
            }
         }

         if (this.grimMicroJitter.get() && this.grimAirTicks > 1) {
            double jitter = this.grimJitterStr.get();
            nx += (Math.random() - 0.5) * jitter;
            nz += (Math.random() - 0.5) * jitter;
         }

         if (this.grimPreserveY.get()) {
            player.setDeltaMovement(nx, my, nz);
         } else {
            player.setDeltaMovement(nx, my, nz);
         }

         this.grimLastHx = nx;
         this.grimLastHz = nz;
      }

      this.grimLastYaw = yaw;
   }

   private void doVanillaJump(LocalPlayer player, float yaw) {
      float jumpPower = 0.42F;
      MobEffectInstance jump = player.getEffect(MobEffects.JUMP_BOOST);
      if (jump != null) {
         jumpPower += 0.1F * (float)(jump.getAmplifier() + 1);
      }

      jumpPower *= this.grimJumpFactor.get().floatValue();
      Vec3 motion = player.getDeltaMovement();
      double x = motion.x;
      double z = motion.z;
      if (player.isSprinting()) {
         float yawRad = yaw * 0.017453292F;
         float boost = 0.2F * this.grimJumpFactor.get().floatValue() * this.grimBhopBoost.get().floatValue();
         x -= (double)Mth.sin(yawRad) * (double)boost;
         z += (double)Mth.cos(yawRad) * (double)boost;
      }

      player.setDeltaMovement(x, (double)jumpPower, z);
   }

   private void applyHorizontal(LocalPlayer player, double x, double z) {
      player.setDeltaMovement(x, player.getDeltaMovement().y, z);
   }
}
