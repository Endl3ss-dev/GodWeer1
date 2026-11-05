package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.util.Util;
import net.skycore.client.module.movement.FlightModule;
import net.skycore.client.module.movement.FreeCameraModule;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Nuclear — {@code Nuclear/src/main/java/nuclear/module/impl/combat/Velocity.java} (легаси-маппинги,
 * {@code SEntityVelocityPacket / SConfirmTransactionPacket / SPlayerPositionLookPacket}).
 * Настройка «Режим»: {@code Cancel / Grim / Grim Air}; булева настройка
 * «Вырубить после флага» (по умолчанию выключена).
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Cancel} — отмена пакета скорости своего игрока;</li>
 *   <li>{@code Grim} — отмена пакета скорости с меткой {@code toSkip = 8}; подтверждения
 *       ({@code SConfirmTransactionPacket} в источнике; в 26.2 пакета подтверждения транзакций
 *       больше нет, поэтому считается ближайший серверный ack — {@code ClientboundBlockChangedAckPacket})
 *       отменяются, пока метка больше единицы,
 *       и каждая отмена уменьшает её; телепорт сбрасывает метку в {@code -8};</li>
 *   <li>{@code Grim Air} — работает только в воздухе вне элитр/воды/лавы и при выключенных
 *       FreeCam/Flight: отменяет подтверждения транзакций и пакет скорости, если
 *       {@code fallDistance} больше 0.7 и меньше 2.</li>
 * </ul>
 *
 * <p>Настройка «Вырубить после флага»: пакет скорости при {@code hurtTime == 9} увеличивает
 * счётчик (в источнике он только увеличивается и нигде не читается), телепорт поднимает флаг
 * на 500 мс, и пока флаг поднят обработка пакетов не идёт.
 */
public final class NuclearVelocity implements VelocityHandler {
   private static final long FLAG_WINDOW_MS = 500L;

   private final VelocityModule module;
   private int toSkip;
   private int countLog;
   private boolean blocked;
   private long flagAt;

   public NuclearVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || Minecraft.getInstance().level == null) {
         return;
      }
      if (this.flagBlocked(player, true)) {
         return;
      }

      if (this.module.is(VelocityModule.NUCLEAR_CANCEL)) {
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.NUCLEAR_GRIM)) {
         if (this.toSkip < 0) {
            return;
         }
         this.toSkip = 8;
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.NUCLEAR_GRIM_AIR)) {
         if (this.grimAirGuard(player)) {
            return;
         }
         if (!player.isFallFlying() && player.fallDistance < 2.0F && player.fallDistance > 0.7F) {
            action.cancel();
         }
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || Minecraft.getInstance().level == null) {
         return false;
      }

      if (this.module.nuclearDisableOnFlag().get()
              && this.module.isOneOf(VelocityModule.NUCLEAR_CANCEL, VelocityModule.NUCLEAR_GRIM, VelocityModule.NUCLEAR_GRIM_AIR)
              && packet instanceof ClientboundPlayerPositionPacket) {
         this.blocked = true;
         this.flagAt = Util.getMillis();
      }

      if (this.flagBlocked(player, false)) {
         return false;
      }

      if (this.module.is(VelocityModule.NUCLEAR_GRIM)) {
         if (packet instanceof ClientboundPlayerPositionPacket) {
            this.toSkip = -8;
            return false;
         }
         if (packet instanceof ClientboundBlockChangedAckPacket) {
            if (this.toSkip < 0) {
               this.toSkip++;
            } else if (this.toSkip > 1) {
               this.toSkip--;
               return true;
            }
         }
         return false;
      }

      if (this.module.is(VelocityModule.NUCLEAR_GRIM_AIR)) {
         if (this.grimAirGuard(player)) {
            return false;
         }
         // Пакет телепорта в источнике применяет позицию вручную и выходит; ванильный
         // обработчик делает ровно то же самое, поэтому здесь достаточно не мешать ему.
         if (packet instanceof ClientboundPlayerPositionPacket) {
            return false;
         }
         if (!player.isFallFlying() && player.fallDistance < 2.0F) {
            if (packet instanceof ClientboundBlockChangedAckPacket && player.fallDistance > 0.7F) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Логика настройки «Вырубить после флага».
    *
    * @param velocity {@code true}, если вызов пришёл из пути пакета скорости — только там
    *                 увеличивается счётчик {@code countLog}
    */
   private boolean flagBlocked(LocalPlayer player, boolean velocity) {
      if (!this.module.nuclearDisableOnFlag().get()) {
         return false;
      }
      if (velocity && player.hurtTime == 9) {
         this.countLog++;
      }
      if (this.blocked && Util.getMillis() - this.flagAt >= FLAG_WINDOW_MS) {
         this.blocked = false;
      }
      return this.blocked;
   }

   private boolean grimAirGuard(LocalPlayer player) {
      return FreeCameraModule.INSTANCE.isEnabled()
              || FlightModule.INSTANCE.isEnabled()
              || player.onGround()
              || player.isFallFlying()
              || player.isInWater()
              || player.isInLava();
   }

   @Override
   public void reset() {
      this.toSkip = 0;
      this.countLog = 0;
      this.blocked = false;
      this.flagAt = 0L;
   }
}
