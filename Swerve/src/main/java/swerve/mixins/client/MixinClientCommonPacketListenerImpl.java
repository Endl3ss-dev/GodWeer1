package swerve.mixins.client;

import swerve.acd.AcdCollector;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Античит-диагностика на уровне общего клиентского слушателя пакетов.
 *
 * <p>Отдельный миксин нужен потому, что {@code handleDisconnect}, {@code onDisconnect},
 * {@code onPacketError} и {@code handleCustomPayload(ClientboundCustomPayloadPacket)}
 * объявлены в {@link ClientCommonPacketListenerImpl}, а не в {@code ClientPacketListener}:
 * mixin ищет метод в целевом классе и не подмешивает унаследованные, поэтому
 * добавлять их в {@code MixinClientPacketListener} было бы тишиной вместо эффекта.
 * Родительский класс покрывает и игрового слушателя, и слушателей конфигурации/логина —
 * то есть отключение на этапе handshake тоже попадёт в журнал.
 *
 * <p>Все четыре обработчика читают то, что сервер уже прислал, и ничего не меняют:
 * {@code HEAD} без {@code cancellable}, отмены нет, задержки нет. {@code onDisconnect}
 * и {@code onPacketError} могут прийти из потока netty — {@link AcdCollector} и
 * {@link swerve.acd.AcdStore} на это рассчитаны (замок на короткую секцию, никаких
 * обращений к окну и очереди уведомлений из чужого потока).
 */
@Mixin(ClientCommonPacketListenerImpl.class)
public class MixinClientCommonPacketListenerImpl {

    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onCustomPayload(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleCustomPayload", exception);
        }
    }

    @Inject(method = "handleDisconnect(Lnet/minecraft/network/protocol/common/ClientboundDisconnectPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdKickReason(ClientboundDisconnectPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onDisconnectReason(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleDisconnect", exception);
        }
    }

    @Inject(method = "onDisconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"))
    private void swerve$acdDisconnect(DisconnectionDetails details, CallbackInfo ci) {
        try {
            AcdCollector.onDisconnect(details);
        } catch (Exception exception) {
            AcdCollector.disableCollection("onDisconnect", exception);
        }
    }

    @Inject(method = "onPacketError(Lnet/minecraft/network/protocol/Packet;Ljava/lang/Exception;)V",
            at = @At("HEAD"))
    private void swerve$acdPacketError(Packet<?> packet, Exception exception, CallbackInfo ci) {
        try {
            AcdCollector.onPacketError(packet, exception);
        } catch (Exception failure) {
            AcdCollector.disableCollection("onPacketError", failure);
        }
    }
}
