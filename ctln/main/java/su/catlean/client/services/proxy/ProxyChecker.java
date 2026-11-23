// deobf by pusia
package su.catlean.client.services.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.util.List;
import kotlin.collections.CollectionsKt;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Proxy;
import su.catlean.api.event.events.network.AddHandlersEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.ui.panel.PanelScreen;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerUtil;

public final class ProxyChecker {
   @NotNull
   public static final ProxyChecker INSTANCE = new ProxyChecker();
   @Nullable
   private static Proxy proxy;
   @NotNull
   private static PlayerUtil playerUtil = new PlayerUtil();

   private ProxyChecker() {
   }

   @NotNull
   public final List getItems() {
      return ProxyManager.INSTANCE.getProxyDataSource().getProxyConfig().getProxies();
   }

   @Nullable
   public final Proxy getProxy() {
      return proxy;
   }

   public final void setProxy(Proxy proxy) {
      ProxyChecker.proxy = proxy;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() instanceof PanelScreen && playerUtil.testValue(600)) {
         this.update();
         playerUtil.update();
      }
   }

   @Flow
   private final void onAddHandlers(AddHandlersEvent addHandlersEvent) {
      if (proxy != null) {
         Proxy proxy2 = proxy;
         Proxy proxy3 = proxy;
         if (addHandlersEvent.getSide() == PacketFlow.CLIENTBOUND && !addHandlersEvent.getLocal()) {
            ChannelPipeline pipelineItem = addHandlersEvent.getPipeline().get();
            if (pipelineItem != null) {
               ChannelHandler[] channelHandlers = new ChannelHandler[]{
                  proxy3.isFlag()
                     ? (ProxyHandler)(new Socks4ProxyHandler(new InetSocketAddress(proxy3.getIp(), proxy3.getValue()), proxy3.getUser()))
                     : (ProxyHandler)(new Socks5ProxyHandler(new InetSocketAddress(proxy3.getIp(), proxy3.getValue()), proxy3.getUser(), proxy3.getPassword()))
               };
               pipelineItem.addFirst(channelHandlers);
            }
         }
      }
   }

   public final boolean checkRemove(Proxy proxy) {
      return this.getItems().remove(proxy);
   }

   public final boolean testProxy(Proxy proxy) {
      return this.getItems().add(proxy);
   }

   public final void update() {
      Iterable items = this.getItems();
      int value = 0;

      for (Object object : items) {
         int value2 = value++;
         if (value2 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Proxy proxy2 = (Proxy)object;
         INSTANCE.applyCurrentTimeMillis(proxy2, value2);
      }
   }

   private final void applyCurrentTimeMillis(Proxy proxy, int value) {
      long currentTimeMillis = System.currentTimeMillis();
      proxy.setValue2(-2);
      new Thread(
            () -> {
               try {
                  Thread.sleep(2000L * value);
               } catch (InterruptedException exception) {
                  Thread.currentThread().interrupt();
                  return;
               }

               try {
                  NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
                  Bootstrap bootstrap = new Bootstrap();
                  ((Bootstrap)((Bootstrap)bootstrap.group((EventLoopGroup)nioEventLoopGroup)).channel(NioSocketChannel.class))
                     .handler((ChannelHandler)(new ProxyCheckInitializer(proxy)));
                  ChannelFuture sync = bootstrap.connect("crystalpvp.cc", 25565).sync();
                  sync.await();
                  if (sync.isSuccess()) {
                     proxy.setValue2((int)(MinecraftHolder.getTime() - currentTimeMillis));
                  } else {
                     proxy.setValue2(-1);
                  }

                  nioEventLoopGroup.shutdownGracefully();
               } catch (Exception exception) {
                  proxy.setValue2(-1);
               }
            }
         )
         .start();
   }
}