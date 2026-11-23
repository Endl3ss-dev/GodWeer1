// deobf by pusia
package su.catlean.mixins.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.network.AddHandlersEvent;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;

@Mixin({Connection.class})
public class ConnectionMixin {

   public ConnectionMixin() {
   }

   @Inject(
      method = {"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player != null && packet != null) {
         if (packet instanceof ClientboundBundlePacket clientboundBundlePacket) {
            clientboundBundlePacket.subPackets().forEach(packetx -> {
               if (new ReceivePacket(packetx).call()) {
                  callbackInfo.cancel();
               }
            });
         } else if (new ReceivePacket(packet).call()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/protocol/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacketPre(Packet<?> packet, CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player != null) {
         if (new SendPacket(packet).call()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/protocol/Packet;)V"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onSendPacketPost(Packet<?> packet, CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player != null) {
         if (new AfterSendPacket(packet).call()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"configureSerialization"},
      at = {@At("RETURN")}
   )
   private static void addHandlersHook(
      ChannelPipeline channelPipeline, PacketFlow packetFlow, boolean flag, BandwidthDebugMonitor bandwidthDebugMonitor, CallbackInfo callbackInfo
   ) {
      AddHandlersEvent.INSTANCE.call(new AtomicReference<>(channelPipeline), packetFlow, flag);
   }
}