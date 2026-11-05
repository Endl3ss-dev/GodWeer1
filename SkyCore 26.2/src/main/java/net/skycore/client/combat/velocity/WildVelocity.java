package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Wild — {@code 3/src/main/java/ru/wild/modules/combat/Velocity.java} (MC 1.21.8; папка {@code Wild}
 * содержит ту же копию модуля). Настройка «Обход»: {@code Vanilla / Lag / Funtime}.
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Vanilla} — отменяет пакет скорости своего игрока;</li>
 *   <li>{@code Funtime} — то же, но только пока игрок в рыхлом сне ({@code player.isInPowderSnow});</li>
 *   <li>{@code Lag} — отменяет пакет скорости и, дополнительно, <b>все исходящие пакеты</b>:
 *       в источнике после отмены пакета скорости стоит {@code if (event.compute()) event.process()},
 *       где {@code compute()} — это {@code mode == SEND}. Клиент замирает на месте, пока режим включён.</li>
 * </ul>
 *
 * <p>Проверка {@code LocalhostHelper.handle()} в источнике — это просто
 * {@code player == null || world == null}, она перенесена как проверка игрока.
 */
public final class WildVelocity implements VelocityHandler {
   private final VelocityModule module;

   public WildVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return;
      }
      if (this.module.is(VelocityModule.WILD_VANILLA)) {
         action.cancel();
         return;
      }
      if (this.module.is(VelocityModule.WILD_LAG)) {
         action.cancel();
         return;
      }
      if (this.module.is(VelocityModule.WILD_FUNTIME) && player.isInPowderSnow) {
         action.cancel();
      }
   }

   @Override
   public boolean onOutboundPacket(Packet<?> packet) {
      // Lag: отмена всех исходящих пакетов, как в источнике.
      return this.module.is(VelocityModule.WILD_LAG);
   }
}
