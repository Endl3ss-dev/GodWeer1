package fun.volon.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fun.volon.api.events.EventInvoker;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.utils.network.NetworkUtils;

import java.lang.reflect.InvocationTargetException;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        EventPacket eventReceive = new EventPacket(packet, EventPacket.Type.RECEIVE);
        EventInvoker.invoke(eventReceive);
        if (eventReceive.isCancelled()) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (NetworkUtils.getSilentPackets().contains(packet)) {
            NetworkUtils.getSilentPackets().remove(packet);
            return;
        }

        EventPacket eventSend = new EventPacket(packet, EventPacket.Type.SEND);
        EventInvoker.invoke(eventSend);
        if (eventSend.isCancelled()) ci.cancel();
    }
}
