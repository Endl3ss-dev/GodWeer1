package swerve.mixins.network;

import swerve.events.network.PacketSendEvent;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Исходящие пакеты: переключение предмета и свинг руки сбрасывают
 * PerfectDelay KillAura (адаптировано из Private, PacketEvent → прямой хук).
 *
 * <p>Фильтр по типу пакета стоит до dispatch: обычные пакеты движения
 * не платят за поиск подписчика.
 */
@Mixin(Connection.class)
public class MixinConnection {

    // Все публичные send(...) в 1.21.4 стекаются в приватный sendPacket.
    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void swerve$onSendPacket(Packet<?> packet, PacketSendListener listener, boolean flush,
                                     CallbackInfo ci) {
        if (packet instanceof ServerboundSetCarriedItemPacket || packet instanceof ServerboundSwingPacket) {
            PacketSendEvent.dispatch(packet);
        }
    }
}
