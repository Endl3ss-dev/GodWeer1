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
 * Speed v5 — порт Nuclear «Speed / Old Grim Bypass»
 * (nuclear/module/impl/movement/Speed.java, вариация под старый Grim).
 *
 * Идея источника: старый Grim не прощает ни длинного окна буста, ни тем более продолжения
 * буста после отката, поэтому здесь всё короче и мягче, чем в Matrix-вариациях:
 * hop ровно на ванильной Y (0.42 — «точный ванильный hop», к нему Grim не придирается),
 * буст только в два первых воздушных тика (+7 % и +2 %), дальше идёт окно базовой скорости
 * (тики 3…5), затем самостоятельное торможение (0.97 на тиках 6…9) и почти ваниль с
 * принудительным трением 0.91 после 9-го тика. setback-кулдаун длиннее, чем у братьев —
 * 8 тиков на скорости 0.21 («почти полная ваниль»).
 *
 * Тайминг: PlayerTickStartEvent в SkyCore снимается в хвосте LocalPlayer#tick (после
 * travel/move), поэтому прыжок на земле в этом же тике игрока в воздух не переводит —
 * окно буста начинается следующим тиком с airTicks == 1, ровно как в источнике.
 *
 * Отличия от источника (адаптация под 26.2 и архитектуру SkyCore):
 * - «принудительный friction» в источнике был мёртвым кодом: множитель 0.91 применялся к
 *   deltaMovement ДО MoveUtil.setSpeed(...), а setSpeed перезаписывает x и z целиком. Здесь
 *   трение применено после setSpeed — так, как задумывалось, и вынесено в `Air Friction`;
 * - mc.timer.timerSpeed → {@link SpeedTimer} (тот же DeltaTracker-хук, что у Timer-мода);
 *   нейтральное 1.0 таймер не перехватывает, а освобождает, иначе модуль глушил Timer;
 * - onSetback() подключён к PacketReceiveEvent + ClientboundPlayerPositionPacket (сет сервера
 *   = откат позиции). В источнике стояло «пока можно вручную или потом привяжем к пакету»;
 * - в кулдауне setSpeed(0.21) вызывается только пока игрок реально движется: на месте он
 *   обнулил бы инерцию ровно там, где её нужно удержать;
 * - MobEffects.MOVEMENT_SPEED / MOVEMENT_SLOWDOWN → MobEffects.SPEED / SLOWNESS (имена 26.2),
 *   hasEffect + getEffect → один getEffect с проверкой на null;
 * - Hop Y в источнике 0.41999998688697815 — это float 0.42f, расширенный до double; в
 *   SkyCore слайдер живёт в double и шаг 0.0001, так что значение ровно 0.42;
 * - длительность кулдауна, «ванильная» скорость в кулдауне и трение за окном вынесены в
 *   настройки, дефолты совпадают с источником (8 тиков, 0.21, 0.91).
 */
public final class SpeedV5Module extends ClientModule {
   public static final SpeedV5Module INSTANCE = new SpeedV5Module();

   private final SliderSetting speed = this.add(new SliderSetting("Speed", 0.319, 0.27, 0.38, 0.001));
   private final SliderSetting hopY = this.add(new SliderSetting("Hop Y", 0.41999998688697815, 0.40, 0.42, 0.0001));
   private final SliderSetting timer = this.add(new SliderSetting("Timer", 1.0, 1.0, 1.06, 0.001));
   private final BooleanSetting autojump = this.add(new BooleanSetting("Авто-прыжок", true));
   private final BooleanSetting damageBoost = this.add(new BooleanSetting("Damage Boost", true));
   private final BooleanSetting grimMode = this.add(new BooleanSetting("Grim Mode", true));
   private final SliderSetting airFriction = this.add(new SliderSetting("Air Friction", 0.91, 0.85, 1.0, 0.001));
   private final SliderSetting setbackCooldown = this.add(new SliderSetting("Setback Cooldown", 8, 0, 16, 1));
   private final SliderSetting setbackSpeed = this.add(new SliderSetting("Setback Speed", 0.21, 0.10, 0.40, 0.005));

   private int airTicks;
   private int groundTicks;
   private int setbackCool;
   private boolean hopped;

   private SpeedV5Module() {
      super("Speed v5", "Nuclear Old Grim: ванильный hop, короткое окно буста, длинный setback-кулдаун",
         ModuleCategory.MOVEMENT);
      this.airFriction.visibleWhen(this.grimMode::get);
      this.setbackSpeed.visibleWhen(() -> this.setbackCooldown.get() > 0.0);

      SkycoreClient.events().subscribe(PlayerTickStartEvent.class, this::onTickStart);
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacketReceive);
   }

   @Override
   protected void onEnable() {
      this.airTicks = 0;
      this.groundTicks = 0;
      this.setbackCool = 0;
      this.hopped = false;
   }

   @Override
   protected void onDisable() {
      this.resetTimer();
      this.airTicks = 0;
      this.setbackCool = 0;
      this.hopped = false;
   }

   private void onTickStart(PlayerTickStartEvent event) {
      LocalPlayer player = event.player();
      if (this.isEnabled() && player != null) {
         this.tick(player);
      }
   }

   private void tick(LocalPlayer player) {
      if (player.level() == null || player.getAbilities().flying) {
         this.resetTimer();
         return;
      }

      if (player.isFallFlying()
         || player.isInWater()
         || player.isInLava()
         || player.onClimbable()
         || player.isEyeInFluid(FluidTags.WATER)
         || player.isEyeInFluid(FluidTags.LAVA)) {
         this.resetTimer();
         this.airTicks = 0;
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
         this.groundTicks++;
         this.hopped = false;

         if (this.autojump.get() && SpeedAction.isMoving() && this.groundTicks >= 1) {
            player.jumpFromGround();
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, this.hopY.get().floatValue(), motion.z);
            this.hopped = true;
         }
      } else {
         this.airTicks++;
         this.groundTicks = 0;
      }

      if (!onGround && SpeedAction.isMoving()) {
         float base = this.speed.get().floatValue();
         float apply = 0.0F;
         float timerValue = 1.0F;
         boolean beyondWindow = false;

         if (this.grimMode.get()) {
            if (this.airTicks == 1 && this.hopped) {
               apply = base * 1.07F;
               timerValue = this.timer.get().floatValue();
            } else if (this.airTicks == 2) {
               apply = base * 1.02F;
            } else if (this.airTicks <= 5) {
               apply = base;
            } else if (this.airTicks <= 9) {
               apply = base * 0.97F;
            } else {
               apply = base * 0.89F;
               beyondWindow = true;
            }
         } else {
            apply = base;
         }

         if (this.damageBoost.get() && player.hurtTime >= 1 && player.hurtTime <= 5) {
            apply *= 1.13F;
         }

         MobEffectInstance speedEffect = player.getEffect(MobEffects.SPEED);
         if (speedEffect != null) {
            apply *= 1.0F + 0.045F * (float)(speedEffect.getAmplifier() + 1);
         }

         if (player.getEffect(MobEffects.SLOWNESS) != null) {
            apply *= 0.87F;
         }

         if (apply > 0.0F) {
            SpeedAction.setSpeed(apply);

            // В источнике это стояло ПЕРЕД setSpeed и потому не работало — см. шапку класса.
            if (beyondWindow) {
               Vec3 motion = player.getDeltaMovement();
               double friction = this.airFriction.get();
               player.setDeltaMovement(motion.x * friction, motion.y, motion.z * friction);
            }
         }

         this.applyTimer(timerValue);
      } else {
         this.resetTimer();
      }
   }

   /**
    * Нейтральное 1.0 таймер не перехватывает: источник писал timerSpeed = tmr на каждом тике
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

   /** Сет сервера (ClientboundPlayerPositionPacket) — старый Grim злой на продолжение буста. */
   private void onPacketReceive(PacketReceiveEvent event) {
      if (this.isEnabled() && !event.isCancelled() && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.onSetback();
      }
   }

   /** Можно вызвать и вручную, если сет пойман вне пакетов. */
   public void onSetback() {
      this.setbackCool = this.setbackCooldown.get().intValue();
      this.airTicks = 0;
      this.hopped = false;
      this.resetTimer();
   }
}
