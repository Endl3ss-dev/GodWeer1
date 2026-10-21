package swerve.events.network;

import net.minecraft.network.protocol.Packet;

import swerve.managers.ModuleManager;
import swerve.modules.impl.combat.Aura;

/**
 * Доставка исходящих пакетов подписчикам без событийной шины: миксин
 * Connection вызывает dispatch напрямую. Аналог PacketEvent из Private,
 * адаптированный под стиль Swerve (статический хук, как MoveInputEvent).
 */
public final class PacketSendEvent {

    private PacketSendEvent() {
    }

    /**
     * Вызывается из MixinConnection.sendPacket. Приходят все исходящие пакеты;
     * фильтрацию по типу миксин делает до вызова, чтобы общий путь был бесплатным.
     */
    public static void dispatch(Packet<?> packet) {
        Aura aura = ModuleManager.get(Aura.class);
        if (aura != null) {
            aura.onPacketSend(packet);
        }
    }
}
