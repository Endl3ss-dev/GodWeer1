// deobf by pusia
package su.catlean.client.services.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kotlin.Deprecated;
import kotlin.ReplaceWith;

public final class ProxyCheckResponseHandler extends ChannelInboundHandlerAdapter {
   public ProxyCheckResponseHandler() {
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      super.channelRead(ctx, msg);
   }

   /** @deprecated */
   @Deprecated(
      message = "Deprecated in Java",
      replaceWith = @ReplaceWith(
         expression = "ctx.close()",
         imports = {}
      )
   )
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      ctx.close();
   }
}