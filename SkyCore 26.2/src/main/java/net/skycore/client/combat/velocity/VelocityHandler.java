package net.skycore.client.combat.velocity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.skycore.client.event.PlayerInputEvent;

/**
 * Один источник обхода отбрасывания. Каждый клиент-источник живёт в своём классе, поэтому
 * состояние разных обходов не смешивается: переключение режима вызывает {@link #reset()}
 * у предыдущего обхода, и его счётчики/флаги не переезжают в новый.
 *
 * <p>Все методы вызываются только для текущего выбранного режима, кроме {@link #onVelocityPacket},
 * который вызывается для любого включённого режима (сам обход решает, его это пакет или нет).
 */
public interface VelocityHandler {

   /**
    * Входящий пакет на уровне {@code Connection} (взрыв, пинг, подтверждение транзакции,
    * обновление блока, урон). Возврат {@code true} отменяет пакет целиком.
    */
   default boolean onInboundPacket(Packet<?> packet) {
      return false;
   }

   /** Исходящий пакет на уровне {@code Connection}. Возврат {@code true} отменяет пакет. */
   default boolean onOutboundPacket(Packet<?> packet) {
      return false;
   }

   /** Пакет скорости своего игрока (ванилла уже применила его, правки — через {@link VelocityAction}). */
   default void onVelocityPacket(VelocityAction action) {
   }

   /** Тик игрока до расчёта движения — аналог {@code UpdateEvent} исходных клиентов. */
   default void onTick(LocalPlayer player) {
   }

   /** Ввод игрока: аналог {@code MoveInputEvent}/{@code InputEvent} исходных клиентов. */
   default void onInput(PlayerInputEvent event, LocalPlayer player) {
   }

   /** Сброс состояния при смене режима и выключении модуля. */
   default void reset() {
   }
}
