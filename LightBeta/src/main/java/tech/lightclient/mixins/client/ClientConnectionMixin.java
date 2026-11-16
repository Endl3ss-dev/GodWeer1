package tech.lightclient.mixins.client;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.utils.connection.NetworkUtils;
import tech.lightclient.client.utils.proxy.Proxy;
import tech.lightclient.client.utils.proxy.ProxyConfig;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacketPre(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        EventPacket eventPacket = new EventPacket(packet, true);
        LightClient.getEventBus().post(eventPacket);
        if (eventPacket.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPre(Packet<?> packet, CallbackInfo info) {

        EventPacket eventPacket = new EventPacket(packet, false);
        LightClient.getEventBus().post(eventPacket);
        if (eventPacket.isCancelled()) {
            info.cancel();
            return;
        }

        if (packet instanceof AcknowledgeChunksC2SPacket) {
            ClientConnection conn = (ClientConnection) (Object) this;
            PacketListener listener = conn.getPacketListener();
            if (!(listener instanceof ClientPlayNetworkHandler)) {
                info.cancel();
            }
        }
    }

    @Inject(method = "addHandlers", at = @At("RETURN"))
    private static void addHandlersHook(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        ProxyConfig config = ProxyConfig.getInstance();
        Proxy proxy = config.getDefaultProxy();

        if (proxy != null && config.isProxyEnabled() && !proxy.isEmpty() && side == NetworkSide.CLIENTBOUND && !local) {
            InetSocketAddress proxyAddress = new InetSocketAddress(proxy.getIp(), proxy.getPort());

            if (proxy.type == Proxy.ProxyType.SOCKS4) {
                pipeline.addFirst("light_socks4_proxy", new Socks4ProxyHandler(proxyAddress, proxy.username));
            } else {
                pipeline.addFirst("light_socks5_proxy", new Socks5ProxyHandler(proxyAddress, proxy.username, proxy.password));
            }

            config.setLastUsedProxy(new Proxy(proxy));
            config.save();
        }
    }
}
