package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.SpeedHVHModule;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Minced — {@code Minced/src/main/java/minced/module/combat/NoVelocity.java} (MC 1.21.4,
 * декомпиляция CFR). Настройка «Мод»: {@code Обычный / Грим}; настройка
 * «Пропускать при активных спидах» (по умолчанию включена, видна только для «Грим»).
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Обычный} — отменяет пакет скорости своего игрока;</li>
 *   <li>{@code Грим} — отменяет пакет скорости <b>и пакет взрыва</b>, поднимая счётчик
 *       {@code field6127 = 2}; каждый исходящий пакет позиции уменьшает счётчик. Пока счётчик
 *       больше нуля, на тике вертикальная скорость обнуляется, а сохранённая прежняя скорость
 *       возвращается, когда счётчик дошёл до нуля. При включённом «Пропускать…» и включённом
 *       {@code Speed} модуль ничего не делает.</li>
 * </ul>
 *
 * <p>Настройка «Прыгать» в источнике видна только при значении «Легит», которого нет в списке
 * режимов, поэтому она недостижима и не переносилась.
 */
public final class MincedVelocity implements VelocityHandler {
   private final VelocityModule module;
   private Vec3 storedVelocity = Vec3.ZERO;
   private int velocityTicks;

   public MincedVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      if (Minecraft.getInstance().player == null) {
         return;
      }
      if (this.module.is(VelocityModule.MINCED_NORMAL)) {
         action.cancel();
         return;
      }
      if (this.module.is(VelocityModule.MINCED_GRIM)) {
         if (this.skippedBySpeed()) {
            return;
         }
         this.velocityTicks = 2;
         action.cancel();
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      if (!this.module.is(VelocityModule.MINCED_GRIM) || !(packet instanceof ClientboundExplodePacket)) {
         return false;
      }
      if (this.skippedBySpeed()) {
         return false;
      }
      this.velocityTicks = 2;
      return true;
   }

   @Override
   public boolean onOutboundPacket(Packet<?> packet) {
      if (this.module.is(VelocityModule.MINCED_GRIM) && packet instanceof ServerboundMovePlayerPacket && this.velocityTicks > 0) {
         this.velocityTicks--;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (!this.module.is(VelocityModule.MINCED_GRIM)) {
         if (!this.storedVelocity.equals(Vec3.ZERO)) {
            this.storedVelocity = Vec3.ZERO;
         }
         return;
      }

      if (this.velocityTicks > 0) {
         if (this.storedVelocity.equals(Vec3.ZERO)) {
            this.storedVelocity = player.getDeltaMovement();
         }
         player.setDeltaMovement(new Vec3(0.0, player.getDeltaMovement().y, 0.0));
      } else if (!this.storedVelocity.equals(Vec3.ZERO)) {
         player.setDeltaMovement(this.storedVelocity);
         this.storedVelocity = Vec3.ZERO;
      }
   }

   /** {@code Minced.getInstance().getModuleManager().get(Speed.class).isEnabled()} источника. */
   private boolean skippedBySpeed() {
      return this.module.mincedSkipSpeed().get() && SpeedHVHModule.INSTANCE.isEnabled();
   }

   @Override
   public void reset() {
      this.velocityTicks = 0;
      this.storedVelocity = Vec3.ZERO;
   }
}
