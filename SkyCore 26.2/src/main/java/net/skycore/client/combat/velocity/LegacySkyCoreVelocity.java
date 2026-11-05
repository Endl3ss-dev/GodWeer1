package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.FreeCameraModule;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Старый SkyCore — {@code SkyCore/src/main/java/sky/core/modules/impl/combat/NoVelocity.java}
 * (легаси-маппинги; класс называется {@code NoVelocity}, в списке модулей — «Velocity»).
 * Настройка «Мод»: {@code Обычный / RW}; «Прыгать» (по умолчанию включена, видна только для RW).
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Обычный} — отмена пакета скорости своего игрока;</li>
 *   <li>{@code RW} — {@code DamageUtil} источника перенесён как {@link DamageTracker}: урон от
 *       падения/стрелы/энд-перла/кактуса и взрывы выключают «обычный» урон, статус {@code hurt}
 *       (id 2) включает его. На каждом пакете вызывается {@code time(1L)}: если «обычный» урон
 *       помечен и прошла хотя бы 1 мс, флаг снимается — поэтому скорость удара запоминается только
 *       тогда, когда пакет успел прийти в ту же миллисекунду. На тике при {@code hurtTime > 0}
 *       и запомненной скорости ввод обнуляется (в источнике ветвление относительных углов даёт
 *       ровно нулевые {@code forward}/{@code strafe}) и выполняется прыжок, если включена настройка
 *       и игрок на земле. FreeCam сбрасывает запомненную скорость.</li>
 * </ul>
 *
 * <p>Из-за окна в 1 мс режим {@code RW} в источнике срабатывает редко — это сохранено как есть.
 * В источнике есть ещё {@code EventDamage} со статусом получения урона; в 26.2 ближайший эквивалент —
 * {@code ClientboundEntityEventPacket} с идентификатором 2 (анимация получения урона).
 */
public final class LegacySkyCoreVelocity implements VelocityHandler {
   private final VelocityModule module;
   private final DamageTracker damage = new DamageTracker();
   private Vec3 knockbackVelocity = Vec3.ZERO;

   public LegacySkyCoreVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (this.module.is(VelocityModule.LEGACY_NORMAL)) {
         if (player != null) {
            action.cancel();
         }
         return;
      }
      if (!this.module.is(VelocityModule.LEGACY_RW) || player == null) {
         return;
      }

      this.damage.time();
      if (this.damage.isNormalDamage()) {
         this.knockbackVelocity = action.packetVelocity();
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      if (!this.module.is(VelocityModule.LEGACY_RW)) {
         return false;
      }
      if (packet instanceof ClientboundExplodePacket) {
         this.damage.onExplosion();
      } else if (packet instanceof ClientboundDamageEventPacket damageEvent) {
         this.damage.onDamageEvent(damageEvent);
      } else if (packet instanceof ClientboundEntityEventPacket entityEvent) {
         this.damage.onEntityEvent(entityEvent);
      }
      return false;
   }

   @Override
   public void onInput(PlayerInputEvent event, LocalPlayer player) {
      if (!this.module.is(VelocityModule.LEGACY_RW)) {
         return;
      }

      if (FreeCameraModule.INSTANCE.isEnabled()) {
         this.knockbackVelocity = Vec3.ZERO;
         return;
      }

      if (player.hurtTime > 0 && this.knockbackVelocity.lengthSqr() > 0.0) {
         double relativeAngle = this.relativeAngle(player);
         boolean zeroForward = relativeAngle > 45.0 || relativeAngle < -0.0;
         boolean zeroStrafe = relativeAngle >= 0.0 && relativeAngle <= 0.0;
         float forward = zeroForward ? -0.0F : 0.0F;
         float strafe = zeroStrafe ? -0.0F : 0.0F;

         Input current = event.keyPresses() == null ? Input.EMPTY : event.keyPresses();
         event.setKeyPresses(new Input(false, false, false, false,
                 this.module.legacyJump().get() && player.onGround(), current.shift(), current.sprint()));
         event.setMoveVector(forward != 0.0F || strafe != 0.0F ? new Vec2(strafe, forward) : Vec2.ZERO);
      } else {
         this.knockbackVelocity = Vec3.ZERO;
      }
   }

   /** {@code getRelativeAngle()} источника. */
   private double relativeAngle(LocalPlayer player) {
      double dx = -this.knockbackVelocity.x;
      double dz = -this.knockbackVelocity.z;
      double attackerYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
      return Mth.wrapDegrees(attackerYaw - player.getYRot());
   }

   @Override
   public void reset() {
      this.knockbackVelocity = Vec3.ZERO;
      this.damage.reset();
   }

   /** Перенос {@code sky.core.utils.player.DamageUtil} старого SkyCore. */
   private static final class DamageTracker {
      private static final long TIME_WINDOW_MS = 1L;

      private boolean normalDamage;
      private boolean fallDamage;
      private boolean explosionDamage;
      private boolean arrowDamage;
      private boolean pearlDamage;
      private boolean blockDamage;
      private long timeAt;

      /** {@code time(1)} источника. */
      void time() {
         if (this.normalDamage) {
            if (Util.getMillis() - this.timeAt >= TIME_WINDOW_MS) {
               this.normalDamage = false;
               this.timeAt = Util.getMillis();
            }
         } else {
            this.timeAt = Util.getMillis();
         }
      }

      boolean isNormalDamage() {
         return this.normalDamage;
      }

      void onExplosion() {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null || this.hasBadEffects(player)) {
            return;
         }
         this.explosionDamage = true;
      }

      void onDamageEvent(ClientboundDamageEventPacket damage) {
         if (damage.sourceType().is(DamageTypes.FALL)) {
            this.fallDamage = true;
         } else if (damage.sourceType().is(DamageTypes.ARROW)) {
            this.arrowDamage = true;
         } else if (damage.sourceType().is(DamageTypes.ENDER_PEARL)) {
            this.pearlDamage = true;
         } else if (damage.sourceType().is(DamageTypes.CACTUS)) {
            this.blockDamage = true;
         }
         this.normalDamage = false;
      }

      void onEntityEvent(ClientboundEntityEventPacket event) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null || Minecraft.getInstance().level == null || this.hasBadEffects(player)) {
            return;
         }

         boolean damageTaken = this.fallDamage || this.arrowDamage || this.explosionDamage
                 || this.pearlDamage || this.blockDamage;
         if (!damageTaken) {
            if (event.getEventId() == 2 && event.getEntity(Minecraft.getInstance().level) == player) {
               this.normalDamage = true;
            }
         } else if (player.hurtTime > 0) {
            this.normalDamage = false;
            this.reset();
         }
      }

      private boolean hasBadEffects(LocalPlayer player) {
         return player.hasEffect(MobEffects.POISON)
                 || player.hasEffect(MobEffects.WITHER)
                 || player.hasEffect(MobEffects.INSTANT_DAMAGE);
      }

      void reset() {
         this.normalDamage = false;
         this.fallDamage = false;
         this.explosionDamage = false;
         this.arrowDamage = false;
         this.pearlDamage = false;
         this.blockDamage = false;
      }
   }
}
