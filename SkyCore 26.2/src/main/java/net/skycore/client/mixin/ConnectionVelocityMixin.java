package net.skycore.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.skycore.client.module.movement.VelocityModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Отдаёт модулю {@link VelocityModule} входящие пакеты на уровне сети.
 *
 * <p>Часть обходов из клиентов-источников работает не только с пакетом скорости: Nuclear и Minced
 * отменяют подтверждения транзакций, Phobia и Releon — пинги, LightBeta и Private — взрывы,
 * Sadnes — урон, обновления блоков и отложенные пакеты, а старый SkyCore — статус получения урона.
 * Все они перехватываются в одном месте — до обработчика пакетов, поэтому отмена здесь означает,
 * что пакет не дойдёт до ванильной логики вообще.
 *
 * <p>Вызов идёт из сетевого потока netty (так же, как существующий перехват
 * {@code ClientboundContainerClosePacket} в {@code ConnectionPrepareViaNativeConnectMixin}),
 * поэтому обходы в этом пути трогают только своё состояние и примитивы игрока, но никогда
 * не меняют позицию или скорость — это делается уже на клиентском потоке.
 */
@Mixin(Connection.class)
public abstract class ConnectionVelocityMixin {
   @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
           at = @At("HEAD"), cancellable = true)
   private void skycore$velocityInbound(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
      if (VelocityModule.INSTANCE.onInboundPacket(packet)) {
         callbackInfo.cancel();
      }
   }
}
