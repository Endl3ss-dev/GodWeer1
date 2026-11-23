// deobf by pusia
package su.catlean.client.services.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import net.minecraft.network.UnconfiguredPipelineHandler.Inbound;
import net.minecraft.network.UnconfiguredPipelineHandler.Outbound;
import su.catlean.api.data.Proxy;

public final class ProxyCheckInitializer extends ChannelInitializer {
   public final Proxy proxy;

   public ProxyCheckInitializer(Proxy proxy) {
      this.proxy = proxy;
   }

   public void applyAddLast(SocketChannel ch) {
      ChannelPipeline pipeline = ch.pipeline();
      ChannelHandler[] channelHandlers = new ChannelHandler[]{new FlowControlHandler()};
      pipeline = pipeline.addLast(channelHandlers)
         .addLast("timeout_proxy_checker_", (ChannelHandler)(new ReadTimeoutHandler(8)))
         .addLast("inbound_proxy_checker_", (ChannelHandler)(new Inbound()))
         .addLast("outbound_proxy_checker_", (ChannelHandler)(new Outbound()));
      channelHandlers = new ChannelHandler[]{
         this.proxy.isFlag()
            ? (ProxyHandler)(new Socks4ProxyHandler(new InetSocketAddress(this.proxy.getIp(), this.proxy.getValue()), this.proxy.getUser()))
            : (ProxyHandler)(
               new Socks5ProxyHandler(new InetSocketAddress(this.proxy.getIp(), this.proxy.getValue()), this.proxy.getUser(), this.proxy.getPassword())
            )
      };
      pipeline = pipeline.addLast(channelHandlers);
      channelHandlers = new ChannelHandler[]{new ProxyCheckResponseHandler()};
      pipeline.addLast(channelHandlers);
   }

   public void initChannel(Channel channel) {
      this.applyAddLast((SocketChannel)channel);
   }
}