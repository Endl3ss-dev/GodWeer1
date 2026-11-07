package net.skycore.client.module.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PlayerTickStartEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.movement.speed.SpeedAction;
import net.skycore.client.movement.speed.SpeedTimer;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.util.SliderSetting;

/**
 * Speed v4 — порт Nuclear «Speed / Matrix Smooth Silent»
 * (nuclear/module/impl/movement/Speed.java, вариация с плавным трением вместо жёсткого спуфа).
 *
 * В отличие от Matrix Silent здесь ничего не подменяется в наземном трении и не форсируется
 * таймер на всём прыжке: на земле — обычный авто-прыжок с Hop Y, в воздухе горизонтальная
 * скорость ставится один раз в первый тик после отрыва (с лёгким бустом и tiny-таймером),
 * а дальше Smooth Friction не дописывает скорость, а МЯГКО СТЯГИВАЕТ её к целевой, если
 * фактический модуль больше цели (диагональный ввод даёт speed * sqrt(2), вот этот хвост
 * и срезается). За счёт этого движение выглядит как ванильное и не высовывается за
 * предсказание сервера.
 *
 * Тайминг: PlayerTickStartEvent в SkyCore снимается в хвосте LocalPlayer#tick (после
 * travel/move), поэтому прыжок на земле в этом тике ещё не переводит игрока в воздух —
 * буст стартанёт на следующем тике с airTicks == 1, ровно как в источнике.
 *
 * Отличия от источника (адаптация под 26.2 и архитектуру SkyCore):
 * - mc.timer.timerSpeed → {@link SpeedTimer} (тот же DeltaTracker-хук, что у Timer-мода).
 *   Нейтральное 1.0 таймер не перехватывает, а освобождает: у источника запись
 *   timerSpeed = 1.0 на каждом тике глушила Timer-модуль;
 * - setback-кулдаун подключён к настоящему источнику: PacketReceiveEvent +
 *   ClientboundPlayerPositionPacket (сет сервера = откат позиции). В источнике стоял
 *   комментарий «пока без packet detect» и только public-метод;
 * - во время кулдауна setSpeed(0.23) вызывается только пока игрок реально движется,
 *   иначе на месте он обнулил бы инерцию ровно в тот момент, когда её нужно сохранить;
 * - MobEffects.MOVEMENT_SPEED / MOVEMENT_SLOWDOWN → MobEffects.SPEED / SLOWNESS (имена 26.2),
 *   hasEffect + getEffect заменены одним getEffect с проверкой на null (как в остальном коде
 *   SkyCore);
 * - lastMotionX / lastMotionZ из источника удалены — поля только записывались и ни на что
 *   не влияли;
 * - длительность кулдауна, «ванильная» скорость в кулдауне и порог Smooth Friction вынесены
 *   в настройки, дефолты совпадают с источником (6 тиков, 0.23, 0.02).
 */
public final class SpeedV4Module extends ClientModule {
   public static final SpeedV4Module INSTANCE = new SpeedV4Module();

   private final SliderSetting speed = this.add(new SliderSetting("Speed", 0.322, 0.26, 0.42, 0.001));
   private final SliderSetting hopY = this.add(new SliderSetting("Hop Y", 0.405, 0.38, 0.42, 0.001));
   private final SliderSetting timerBoost = this.add(new SliderSetting("Timer", 1.0, 1.0, 1.08, 0.001));
   private final BooleanSetting autojump = this.add(new BooleanSetting("Авто-прыжок", true));
   private final BooleanSetting damageBoost = this.add(new BooleanSetting("Damage Boost", true));
   private final BooleanSetting smooth = this.add(new BooleanSetting("Smooth Friction", true));
   private final SliderSetting smoothEpsilon = this.add(new SliderSetting("Smooth Epsilon", 0.02, 0.0, 0.1, 0.001));
   private final SliderSetting setbackCooldown = this.add(new SliderSetting("Setback Cooldown", 6, 0, 12, 1));
   private final SliderSetting setbackSpeed = this.add(new SliderSetting("Setback Speed", 0.23, 0.10, 0.40, 0.005));

   private int airTicks;
   private int setbackCool;

   private SpeedV4Module() {
      super("Speed v4", "Nuclear Matrix Smooth Silent: мягкое трение, первый тик буста, setback-кулдаун",
         ModuleCategory.MOVEMENT);
      this.smoothEpsilon.visibleWhen(this.smooth::get);
      this.setbackSpeed.visibleWhen(() -> this.setbackCooldown.get() > 0.0);

      SkycoreClient.events().subscribe(PlayerTickStartEvent.class, this::onTickStart);
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacketReceive);
   }

   @Override
   protected void onEnable() {
      this.airTicks = 0;
      this.setbackCool = 0;
   }

   @Override
   protected void onDisable() {
      this.reset();
      this.setbackCool = 0;
   }

   private void onTickStart(PlayerTickStartEvent event) {
      LocalPlayer player = event.player();
      if (this.isEnabled() && player != null) {
         this.tick(player);
      }
   }

   private void tick(LocalPlayer player) {
      if (player.level() == null || player.getAbilities().flying) {
         this.reset();
         return;
      }

      if (player.isFallFlying()
         || player.isInWater()
         || player.isInLava()
         || player.onClimbable()
         || player.isEyeInFluid(FluidTags.WATER)
         || player.isEyeInFluid(FluidTags.LAVA)) {
         this.reset();
         return;
      }

      if (this.setbackCool > 0) {
         this.setbackCool--;
         if (SpeedAction.isMoving()) {
            SpeedAction.setSpeed(this.setbackSpeed.get().floatValue());
         }

         this.resetTimer();
         return;
      }

      boolean onGround = player.onGround();
      if (onGround) {
         this.airTicks = 0;

         if (this.autojump.get() && SpeedAction.isMoving()) {
            player.jumpFromGround();
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, this.hopY.get().floatValue(), motion.z);
         }
      } else {
         this.airTicks++;
      }

      if (!onGround && SpeedAction.isMoving()) {
         float base = this.speed.get().floatValue();
         float apply = base;

         if (this.airTicks == 1) {
            apply = base * 1.06F;
            this.applyTimer(this.timerBoost.get().floatValue());
         } else {
            this.resetTimer();
         }

         if (this.damageBoost.get() && player.hurtTime > 0 && player.hurtTime < 7) {
            apply *= 1.14F;
         }

         MobEffectInstance speedEffect = player.getEffect(MobEffects.SPEED);
         if (speedEffect != null) {
            apply *= 1.0F + 0.05F * (float)(speedEffect.getAmplifier() + 1);
         }

         if (player.getEffect(MobEffects.SLOWNESS) != null) {
            apply *= 0.86F;
         }

         SpeedAction.setSpeed(apply);

         if (this.smooth.get() && this.airTicks > 2) {
            Vec3 motion = player.getDeltaMovement();
            double current = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            double epsilon = this.smoothEpsilon.get();

            if (current > (double)apply + epsilon) {
               double factor = (double)apply / current;
               player.setDeltaMovement(motion.x * factor, motion.y, motion.z * factor);
            }
         }
      } else {
         this.resetTimer();
      }
   }

   /**
    * Нейтральное 1.0 таймер не перехватывает: источник писал timerSpeed = 1.0 на каждом тике
    * и тем самым глушил Timer-модуль, здесь в этом случае скорость просто освобождается.
    */
   private void applyTimer(float multiplier) {
      if (Math.abs(multiplier - 1.0F) < 1.0E-4F) {
         SpeedTimer.clear();
      } else {
         SpeedTimer.set(multiplier);
      }
   }

   private void resetTimer() {
      SpeedTimer.clear();
   }

   private void reset() {
      this.resetTimer();
      this.airTicks = 0;
   }

   /** Сет сервера (ClientboundPlayerPositionPacket) — откат позиции: тики почти ваниля. */
   private void onPacketReceive(PacketReceiveEvent event) {
      if (this.isEnabled() && !event.isCancelled() && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.notifySetback();
      }
   }

   /** Можно вызвать вручную (например, из макроса или команды), если сет пойман вне пакетов. */
   public void notifySetback() {
      this.setbackCool = this.setbackCooldown.get().intValue();
      this.airTicks = 0;
      this.resetTimer();
   }
}
