package net.skycore.client.combat.velocity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Просьба активного обхода к пакету {@code ClientboundSetEntityMotionPacket} своего игрока.
 *
 * <p>Пакет отменяется целиком — ровно так же, как это делали клиенты-источники
 * ({@code event.cancel()} / {@code packetEvent.setCancelled(true)}), поэтому ванильный
 * {@code ClientPlayerEntity#setVelocity} не выполняется вообще. Если обходу нужно не убрать
 * скорость, а изменить её (как правка {@code motionX/motionY/motionZ} у пакета в источниках),
 * {@link #scale(double, double, double)} и {@link #set(double, double, double)} выставляют
 * новую скорость вместо пакетной.
 *
 * <p>Объект один на модуль и переиспользуется: в пути пакета нет ни одной аллокации, кроме
 * {@link Vec3} там, где обход выставляет своё значение.
 */
public final class VelocityAction {
   private Vec3 before = Vec3.ZERO;
   private Vec3 packetVelocity = Vec3.ZERO;
   private boolean cancel;
   private boolean scaling;
   private boolean override;
   private double scaleX = 1.0;
   private double scaleY = 1.0;
   private double scaleZ = 1.0;
   private double overrideX;
   private double overrideY;
   private double overrideZ;

   /** Скорость игрока до пакета (для обходов, которые правят {@code getMotion()}). */
   public Vec3 before() {
      return this.before;
   }

   /** Скорость из пакета. */
   public Vec3 packetVelocity() {
      return this.packetVelocity;
   }

   /** Отмена пакета: ванилла не применяет скорость, скорость игрока не меняется. */
   public void cancel() {
      this.cancel = true;
   }

   /** Умножение компонент пакетной скорости (аналог {@code motionX *= k} в источниках). */
   public void scale(double x, double y, double z) {
      this.scaling = true;
      this.scaleX = x;
      this.scaleY = y;
      this.scaleZ = z;
   }

   /** Абсолютная скорость вместо пакетной (аналог {@code setMotion(...)} в источниках). */
   public void set(double x, double y, double z) {
      this.override = true;
      this.overrideX = x;
      this.overrideY = y;
      this.overrideZ = z;
   }

   public void begin(Vec3 before, Vec3 packetVelocity) {
      this.before = before;
      this.packetVelocity = packetVelocity;
   }

   /**
    * Применяет решение обхода и возвращает {@code true}, если пакет надо отменить.
    *
    * <p>Проверка «переопределить → масштабировать → просто отменить» повторяет порядок
    * источников: там сначала правили поля пакета, а затем отменяли событие.
    */
   public boolean resolve(LocalPlayer player) {
      if (this.override) {
         player.setDeltaMovement(new Vec3(this.overrideX, this.overrideY, this.overrideZ));
         return true;
      }
      if (this.scaling) {
         player.setDeltaMovement(new Vec3(
                 this.packetVelocity.x * this.scaleX,
                 this.packetVelocity.y * this.scaleY,
                 this.packetVelocity.z * this.scaleZ));
         return true;
      }
      return this.cancel;
   }

   /**
    * Старое поведение «применить пакет, затем вернуть прежнюю скорость». В модуле больше не
    * используется — оставлено, чтобы не ломать совместимость с версией модуля, где решение
    * принималось после ванильного обработчика.
    */
   public void apply(LocalPlayer player) {
      if (this.override) {
         player.setDeltaMovement(new Vec3(this.overrideX, this.overrideY, this.overrideZ));
      } else if (this.cancel) {
         player.setDeltaMovement(this.before);
      } else if (this.scaling) {
         player.setDeltaMovement(new Vec3(
                 this.packetVelocity.x * this.scaleX,
                 this.packetVelocity.y * this.scaleY,
                 this.packetVelocity.z * this.scaleZ));
      }
   }

   public void reset() {
      this.before = Vec3.ZERO;
      this.packetVelocity = Vec3.ZERO;
      this.cancel = false;
      this.scaling = false;
      this.override = false;
      this.scaleX = 1.0;
      this.scaleY = 1.0;
      this.scaleZ = 1.0;
      this.overrideX = 0.0;
      this.overrideY = 0.0;
      this.overrideZ = 0.0;
   }
}
