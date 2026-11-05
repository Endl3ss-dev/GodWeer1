package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Rockstar — {@code Rockstar/src/main/java/moscow/rockstar/module/combat/Velocity.java}
 * (MC 1.21.4). Настройка «Режим»: {@code Отмена / Компенсация / Изменение};
 * слайдеры «Скорость по X / Y / Z» (в процентах, по умолчанию 50) видны только для «Изменение».
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Отмена} — отменяет пакет скорости своего игрока;</li>
 *   <li>{@code Изменение} — умножает компоненты пакета на проценты слайдеров
 *       (в источнике — целочисленная правка полей пакета {@code velocityX * 8000 * value / 100});
 *       у локального игрока тот же результат получается умножением применённой скорости;</li>
 *   <li>{@code Компенсация} — запоминает скорость из пакета и, когда {@code hurtTime} вернулся к нулю,
 *       прыгает (если на земле) и выставляет скорость {@code -0.2} от направления удара.</li>
 * </ul>
 *
 * <p>В источнике {@code super.tick()} вызывается внутри ветки «Компенсация»; в SkyCore базовый
 * модуль тика не имеет, поэтому вызов опущен.
 */
public final class RockstarVelocity implements VelocityHandler {
   private final VelocityModule module;
   private Vec3 lastMotion = Vec3.ZERO;
   private boolean gotVelocity;
   private boolean wasHurt;
   private boolean jumped;

   public RockstarVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || player.isDeadOrDying()) {
         return;
      }

      if (this.module.is(VelocityModule.ROCKSTAR_CANCEL)) {
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.ROCKSTAR_MODIFY)) {
         action.scale(
                 this.module.rockstarX().get() / 100.0,
                 this.module.rockstarY().get() / 100.0,
                 this.module.rockstarZ().get() / 100.0);
         return;
      }

      if (this.module.is(VelocityModule.ROCKSTAR_COMPENSATION)) {
         this.lastMotion = action.packetVelocity();
         this.gotVelocity = true;
      }
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (!this.module.is(VelocityModule.ROCKSTAR_COMPENSATION) || !this.gotVelocity || player == null) {
         return;
      }

      if (player.hurtTime > 0) {
         this.wasHurt = true;
      }

      if (this.wasHurt && player.hurtTime == 0) {
         if (player.onGround()) {
            this.jump(player);
            this.jumped = true;
         }

         Vec3 moveDirection = this.lastMotion.normalize();
         Vec3 updatedMotion = moveDirection.scale(-0.2);
         player.setDeltaMovement(updatedMotion);
         this.wasHurt = false;
         this.gotVelocity = false;
      }

      if (this.jumped && player.onGround()) {
         this.jumped = false;
      }
   }

   /**
    * Прыжок вместо {@code mc.player.jump()}: в 26.2 {@code LivingEntity#jumpFromGround()}
    * недоступен извне, поэтому повторён ванильный расчёт вертикальной скорости прыжка
    * (0.42 плюс бонус эффекта прыгучести).
    */
   private void jump(LocalPlayer player) {
      Vec3 motion = player.getDeltaMovement();
      player.setDeltaMovement(new Vec3(motion.x, 0.42 + player.getJumpBoostPower(), motion.z));
   }

   @Override
   public void reset() {
      this.lastMotion = Vec3.ZERO;
      this.gotVelocity = false;
      this.wasHurt = false;
      this.jumped = false;
   }
}
