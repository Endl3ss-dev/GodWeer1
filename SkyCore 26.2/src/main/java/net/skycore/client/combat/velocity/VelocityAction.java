package net.skycore.client.combat.velocity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Просьба активного обхода к пакету {@code ClientboundSetEntityMotionPacket} своего игрока.
 *
 * <p>Пакет не отменяется на уровне сети: SkyCore даёт ванильному обработчику применить скорость,
 * после чего {@link #apply(LocalPlayer)} возвращает прежнюю скорость (полная отмена), масштабирует
 * применённую или подставляет своё значение. Для локального игрока результат совпадает с отменой
 * или правкой пакета, но не требует доступа к полям пакета: в 26.2 у
 * {@code ClientboundSetEntityMotionPacket} остались только аксессоры вида {@code id()}, а поля,
 * которые правили исходные клиенты ({@code motionX/motionY/motionZ}, {@code getMotionX()}),
 * закрыты.
 *
 * <p>Объект один на модуль и переиспользуется, поэтому в пути пакета нет ни одной аллокации.
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

   /** Скорость игрока до применения пакета. */
   public Vec3 before() {
      return this.before;
   }

   /** Скорость из пакета (то, что ванилла только что применила игроку). */
   public Vec3 packetVelocity() {
      return this.packetVelocity;
   }

   /** Полная отмена: скорость возвращается к состоянию до пакета. */
   public void cancel() {
      this.cancel = true;
   }

   /** Умножение компонент скорости из пакета. */
   public void scale(double x, double y, double z) {
      this.scaling = true;
      this.scaleX = x;
      this.scaleY = y;
      this.scaleZ = z;
   }

   /** Абсолютная скорость вместо пакетной (используется режимами, которые правят {@code getMotion()}). */
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
