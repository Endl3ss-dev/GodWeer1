package net.skycore.client.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.skycore.client.SkycoreClient;
import net.skycore.client.command.ChatMessages;
import net.skycore.client.combat.AttackHelper;
import net.skycore.client.combat.CriticalHelper;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.SpearHelper;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.mixin.LivingEntityGetNoJumpDelayAccessor;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.InventoryHelper;

/**
 * Spear Dash — «огромный удар» копьём 1.21.11 за счёт рывка («Рывок» / Lunge I–III).
 *
 * <p>Механика (Minecraft Wiki: Spear, Lunge): jab с «Рывком N» даёт мгновенный импульс
 * {@code 0.458 × N} блоков/тик по направлению взгляда — в воздухе дальше, чем на земле,
 * максимум при горизонтальном прицеле; заряд (удержание ПКМ) наносит
 * {@code floor(множитель × скорость сближения)} + атрибутивный урон предмета при
 * относительной скорости ≥ 4.6 бл/с. Сам «Рывок» урона не добавляет, но если начать
 * заряд сразу после джаба, заряд доезжает до цели на скорости рывка: незерит +
 * Рывок III ≈ 33 урона (16.5 сердец).
 *
 * <p>Машина состояний, прыжок через {@code noJumpDelay}, свап хотбара и приоритет
 * ротации — адаптация {@link AutoMaceModule}: WAIT → AIM → JAB → CHARGE → COOLDOWN.
 * Урон модуль не подменяет: сервер считает его сам, задача модуля — попасть живой
 * стадией заряда ровно в пик скорости и не потерять её по дороге.
 *
 * <p>Общая расчётная часть копья — в {@link SpearHelper}. Копьё должно лежать в
 * хотбаре (слоты 1–9).
 */
public final class SpearDashModule extends ClientModule {
   private static final String MODE_JAB_ONLY = "Только рывок";
   private static final String MODE_CHARGE_ONLY = "Только заряд";
   private static final int ROTATION_PRIORITY = 8;
   private static final long SEQUENCE_TIMEOUT_MS = 4000L;
   private static final int AIM_TICKS = 6;
   private static final int COOLDOWN_TICKS = 3;
   private static final float AIM_TOLERANCE = 2.5F;
   private static final float ATTACK_READY = 0.93F;
   private static final double LUNGE_FRICTION = 0.8;
   private static final int MIN_FOOD = 7;

   private final ModeSetting mode = this.add(
         new ModeSetting("Режим", "Рывок + заряд", "Рывок + заряд", MODE_JAB_ONLY, MODE_CHARGE_ONLY));
   private final NumberSetting searchRange = this.add(new NumberSetting("Радиус поиска", 24.0, 8.0, 48.0, 1.0));
   private final NumberSetting startRange = this.add(new NumberSetting("Стартовая дистанция", 20.0, 8.0, 32.0, 0.5));
   private final NumberSetting attackRange = this.add(new NumberSetting("Радиус удара", 3.8, 2.4, 4.5, 0.1));
   private final NumberSetting maxTilt = this.add(new NumberSetting("Макс. наклон взгляда", 12.0, 0.0, 45.0, 1.0));
   private final NumberSetting lead = this.add(new NumberSetting("Упреждение", 1.0, 0.0, 2.0, 0.1));
   private final NumberSetting minSpeed = this.add(new NumberSetting("Мин. скорость удара", 5.0, 4.6, 14.0, 0.1));
   private final BooleanSetting autoChargeDelay = this.add(new BooleanSetting("Авто-задержка заряда", true));
   private final NumberSetting chargeDelay = this.add(new NumberSetting("Задержка заряда", 9.0, 4.0, 20.0, 1.0));
   private final NumberSetting chargeHold = this.add(new NumberSetting("Держать заряд", 18.0, 8.0, 40.0, 1.0));
   private final BooleanSetting jumpBeforeDash = this.add(new BooleanSetting("Прыжок перед рывком", true));
   private final BooleanSetting forceSprint = this.add(new BooleanSetting("Держать спринт", true));
   private final BooleanSetting autoSwap = this.add(new BooleanSetting("Свап на копьё", true));
   private final BooleanSetting requireLunge = this.add(new BooleanSetting("Требовать Рывок", true));
   private final BooleanSetting requireFood = this.add(new BooleanSetting("Контроль еды", true));
   private final BooleanSetting notify = this.add(new BooleanSetting("Показывать урон", false));

   private SpearDashModule.State state = SpearDashModule.State.WAIT;
   private LivingEntity target;
   private long sequenceStartedAt;
   private int aimTicks;
   private int chargeTicks;
   private int cooldownTicks;
   private int chargeDelayReady = 9;
   private int lungeLevel;
   private float multiplier = 0.7F;
   private float baseDamage;
   private float aimYaw;
   private float aimPitch;
   private float rotationDelta = 360.0F;
   private boolean jumpKeyHeld;
   private boolean useKeyHeld;

   public SpearDashModule() {
      super("Spear Dash", "Рывок копьём (Lunge) + заряд: урон от скорости сближения", ModuleCategory.COMBAT);
      this.autoChargeDelay.visibleWhen(this::usesCharge);
      this.chargeDelay.visibleWhen(() -> {
         return this.usesCharge() && !this.autoChargeDelay.get();
      });
      this.chargeHold.visibleWhen(this::usesCharge);
      this.minSpeed.visibleWhen(this::usesCharge);
      this.jumpBeforeDash.visibleWhen(this::usesDash);
      this.requireLunge.visibleWhen(this::usesDash);
      this.requireFood.visibleWhen(this::usesDash);
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onMoveInput);
   }

   public static SpearDashModule get() {
      var optional = SkycoreClient.modules().get(SpearDashModule.class);
      return (SpearDashModule)optional.orElse(null);
   }

   @Override
   protected void onEnable() {
      this.reset();
      this.state = SpearDashModule.State.WAIT;
   }

   @Override
   protected void onDisable() {
      this.releaseUse();
      this.releaseJump();
      RotationManager.INSTANCE.stopRotation();
      this.reset();
      this.state = SpearDashModule.State.WAIT;
   }

   @Override
   protected void onTick() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (player == null || instance.level == null || instance.gameMode == null) {
         this.releaseUse();
         this.releaseJump();
         return;
      }

      if (this.jumpKeyHeld) {
         this.releaseJump();
      }

      if (this.state != SpearDashModule.State.WAIT
            && System.currentTimeMillis() - this.sequenceStartedAt >= SEQUENCE_TIMEOUT_MS) {
         this.abort();
         return;
      }

      switch (this.state) {
         case WAIT:
            this.handleWait(instance, player);
            break;
         case AIM:
            this.handleAim(instance, player);
            break;
         case JAB:
            this.handleJab(instance, player);
            break;
         case CHARGE:
            this.handleCharge(instance, player);
            break;
         case COOLDOWN:
            this.handleCooldown(player);
      }
   }

   private void handleWait(Minecraft instance, LocalPlayer player) {
      ItemStack mainHand = player.getMainHandItem();
      if (!SpearHelper.isSpear(mainHand)) {
         if (!this.autoSwap.get()) {
            return;
         }

         int slot = SpearHelper.findBestHotbarSlot(player);
         if (slot < 0) {
            return;
         }

         InventoryHelper.selectHotbarSlot(slot);
         mainHand = player.getMainHandItem();
         if (!SpearHelper.isSpear(mainHand)) {
            return;
         }
      }

      this.readSpearStats(mainHand);
      if (this.usesDash()) {
         if (this.requireLunge.get() && this.lungeLevel <= 0) {
            return;
         }

         if (this.requireFood.get() && player.getFoodData().getFoodLevel() < MIN_FOOD) {
            return;
         }

         if (player.getAttackStrengthScale(1.0F) < ATTACK_READY) {
            return;
         }
      }

      if (player.isUsingItem() || player.isInWater() || player.isFallFlying() || player.isPassenger()) {
         return;
      }

      LivingEntity entity = SpearHelper.findTarget(instance.level, this.searchRange.get());
      this.target = entity;
      if (entity == null) {
         return;
      }

      double distance = CriticalHelper.getStrictDistance(entity);
      if (distance > this.startRange.get() || distance < this.requiredRunway()) {
         return;
      }

      this.sequenceStartedAt = System.currentTimeMillis();
      this.aimTicks = 0;
      this.chargeTicks = 0;
      this.rotationDelta = 360.0F;
      this.state = SpearDashModule.State.AIM;
   }

   private void handleAim(Minecraft instance, LocalPlayer player) {
      if (!SpearHelper.isValidTarget(this.target)) {
         this.abort();
         return;
      }

      this.computeAim(player, this.target, this.lead.get() * this.ticksToContact(player));
      RotationManager.update(new Rotation(this.aimYaw, this.aimPitch), 360.0F, 360.0F, 1, ROTATION_PRIORITY);
      ++this.aimTicks;
      if (this.rotationDelta > AIM_TOLERANCE && this.aimTicks < AIM_TICKS) {
         return;
      }

      if (this.jumpBeforeDash.get() && this.usesDash() && player.onGround()) {
         this.performJump(instance);
      }

      if (this.usesJab()) {
         this.state = SpearDashModule.State.JAB;
      } else {
         this.beginCharge(instance, player);
      }
   }

   private void handleJab(Minecraft instance, LocalPlayer player) {
      AttackHelper.sendFixedAttack(player, this.target);
      if (this.usesCharge()) {
         this.chargeTicks = -1;
         this.state = SpearDashModule.State.CHARGE;
      } else {
         this.state = SpearDashModule.State.COOLDOWN;
         this.cooldownTicks = 0;
      }
   }

   private void handleCharge(Minecraft instance, LocalPlayer player) {
      if (!SpearHelper.isValidTarget(this.target)) {
         this.releaseUse();
         this.abort();
         return;
      }

      ++this.chargeTicks;
      if (this.chargeTicks <= 0) {
         this.beginCharge(instance, player);
         return;
      }

      if (this.chargeTicks == 1 && !player.isUsingItem()) {
         instance.gameMode.useItem(player, InteractionHand.MAIN_HAND);
      }

      this.computeAim(player, this.target, this.ticksToContact(player));
      RotationManager.update(new Rotation(this.aimYaw, this.aimPitch), 360.0F, 360.0F, 1, ROTATION_PRIORITY);
      double distance = CriticalHelper.getStrictDistance(this.target);
      boolean live = this.chargeTicks >= this.chargeDelayReady;
      boolean connected = this.target.hurtTime > 0;
      boolean timeout = this.chargeTicks >= this.chargeHold.get().intValue();
      if (!live && !timeout && distance >= SpearHelper.MIN_REACH) {
         return;
      }

      if (this.notify.get()) {
         ChatMessages.displayInfo(this.describeHit(this.closingSpeed(player), connected));
      }

      this.releaseUse();
      this.state = SpearDashModule.State.COOLDOWN;
      this.cooldownTicks = 0;
   }

   private void handleCooldown(LocalPlayer player) {
      ++this.cooldownTicks;
      if (this.cooldownTicks >= COOLDOWN_TICKS && player.getAttackStrengthScale(1.0F) > ATTACK_READY) {
         this.sequenceStartedAt = System.currentTimeMillis();
         this.state = SpearDashModule.State.WAIT;
      }
   }

   private void beginCharge(Minecraft instance, LocalPlayer player) {
      if (player.isBlocking()) {
         instance.gameMode.releaseUsingItem(player);
      }

      if (!player.isUsingItem()) {
         instance.options.keyUse.setDown(true);
         this.useKeyHeld = true;
      }

      this.state = SpearDashModule.State.CHARGE;
   }

   private void onMoveInput(PlayerInputEvent playerInputEvent) {
      if (this.isEnabled() && this.forceSprint.get() && this.state != SpearDashModule.State.WAIT) {
         playerInputEvent.setSprint(true);
      }
   }

   private void computeAim(LocalPlayer player, LivingEntity target, double leadTicks) {
      Rotation rotation = SpearHelper.aim(player, target, leadTicks, this.maxTilt.get());
      this.aimYaw = rotation.getYaw();
      this.aimPitch = rotation.getPitch();
      this.rotationDelta = SpearHelper.aimDelta(player, rotation);
   }

   private double ticksToContact(LocalPlayer player) {
      double distance = CriticalHelper.getStrictDistance(this.target);
      double speed = Math.max(1.0, this.closingSpeed(player));
      return Math.max(0.0, (distance - this.attackRange.get()) / speed);
   }

   /** Скорость сближения вдоль взгляда, блоков/сек — из неё сервер считает урон заряда. */
   private double closingSpeed(LocalPlayer player) {
      var movement = player.getDeltaMovement();
      double dx = movement.x;
      double dy = movement.y;
      double dz = movement.z;
      if (this.target != null) {
         var targetMovement = this.target.getDeltaMovement();
         dx -= targetMovement.x;
         dy -= targetMovement.y;
         dz -= targetMovement.z;
      }

      return SpearHelper.projectedSpeed(this.aimYaw, this.aimPitch, dx, dy, dz);
   }

   private String describeHit(double speed, boolean connected) {
      float predicted = SpearHelper.predictedDamage(this.multiplier, this.baseDamage,
            Math.max(speed, SpearHelper.lungeSpeed(this.lungeLevel) * LUNGE_FRICTION));
      if (connected) {
         return "Spear Dash: заряд попал, ~" + (int)predicted + " урона";
      } else if (speed < this.minSpeed.get()) {
         return "Spear Dash: скорость " + (int)speed + " бл/с мала, нужно " + this.minSpeed.get().intValue();
      } else {
         return "Spear Dash: заряд сброшен на скорости " + (int)speed + " бл/с, ~" + (int)predicted + " урона";
      }
   }

   private void readSpearStats(ItemStack stack) {
      String path = SpearHelper.path(stack);
      this.baseDamage = SpearHelper.baseDamage(stack);
      this.multiplier = SpearHelper.multiplier(path);
      this.lungeLevel = SpearHelper.lungeLevel(stack);
      this.chargeDelayReady = this.autoChargeDelay.get()
            ? SpearHelper.chargeDelayTicks(path)
            : this.chargeDelay.get().intValue();
   }

   /**
    * Сколько блоков нужно на разгон: скорость рывка гасится трением, поэтому берётся
    * средняя по окну задержки заряда плюс запас на сам удар.
    */
   private double requiredRunway() {
      if (!this.usesDash()) {
         return SpearHelper.MIN_REACH;
      }

      double averageBlocksPerTick = SpearHelper.LUNGE_BLOCKS_PER_TICK * (double)Math.max(1, this.lungeLevel) * LUNGE_FRICTION;
      return (double)this.chargeDelayReady * averageBlocksPerTick + this.attackRange.get();
   }

   private boolean usesJab() {
      return !this.mode.get().equals(MODE_CHARGE_ONLY);
   }

   private boolean usesCharge() {
      return !this.mode.get().equals(MODE_JAB_ONLY);
   }

   private boolean usesDash() {
      return this.usesJab();
   }

   private void performJump(Minecraft instance) {
      instance.options.keyJump.setDown(true);
      this.jumpKeyHeld = true;
      if (instance.player.onGround()) {
         ((LivingEntityGetNoJumpDelayAccessor)instance.player).skycore$setNoJumpDelay(0);
         instance.player.jumpFromGround();
      }
   }

   private void releaseJump() {
      Minecraft instance = Minecraft.getInstance();
      if (instance.options != null) {
         instance.options.keyJump.setDown(false);
      }

      this.jumpKeyHeld = false;
   }

   private void releaseUse() {
      Minecraft instance = Minecraft.getInstance();
      if (!this.useKeyHeld) {
         return;
      }

      if (instance.options != null) {
         instance.options.keyUse.setDown(false);
      }

      if (instance.player != null && instance.gameMode != null && instance.player.isUsingItem()) {
         instance.gameMode.releaseUsingItem(instance.player);
      }

      this.useKeyHeld = false;
   }

   private void abort() {
      this.releaseUse();
      this.reset();
      this.state = SpearDashModule.State.WAIT;
   }

   private void reset() {
      this.target = null;
      this.sequenceStartedAt = 0L;
      this.aimTicks = 0;
      this.chargeTicks = 0;
      this.cooldownTicks = 0;
      this.rotationDelta = 360.0F;
      this.jumpKeyHeld = false;
      this.useKeyHeld = false;
   }

   enum State {
      WAIT,
      AIM,
      JAB,
      CHARGE,
      COOLDOWN;
   }
}
