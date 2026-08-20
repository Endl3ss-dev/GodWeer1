/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.handler.DecoderHandler
 *  net.minecraft.network.handler.NetworkStateTransitionHandler
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.PacketType
 *  net.minecraft.network.packet.s2c.common.DisconnectS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package minced.mixin.network;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import minced.core.LogUtil;
import minced.core.ViaLoaderCompat;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.handler.NetworkStateTransitionHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={DecoderHandler.class})
public abstract class DecoderHandlerMixin {
    private static String field12717 = "field12717";

    @Inject(method={"decode"}, at={@At(value="INVOKE", target="Ljava/io/IOException;<init>(Ljava/lang/String;)V")}, cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
    private void mincedbots$allowTrailingBytesForBotDisconnect(ChannelHandlerContext param1, ByteBuf param2, List<Object> param3, CallbackInfo param4, int param5, Packet<?> param6, PacketType<?> param7) {
        Object v9;
        java.util.Iterator v13;
        if (!(param6 instanceof LoginDisconnectS2CPacket) && !(param6 instanceof DisconnectS2CPacket)) {
            return;
        }
        ClientConnection v8 = (ClientConnection)param1.pipeline().get(ClientConnection.class);
        if (v8 == null) {
            v13 = param1.pipeline().toMap().entrySet().iterator();
            while (v13.hasNext()) {
                ClientConnection v11;
                Map.Entry v10 = (Map.Entry)v13.next();
                Object v12 = v10.getValue();
                if (!(v12 instanceof ClientConnection)) continue;
                v8 = v11 = (ClientConnection)v12;
                break;
            }
        }
        if (!(v8 instanceof ViaLoaderCompat) || !((ViaLoaderCompat)(v9 = (ViaLoaderCompat)v8)).mincedbots$isBotConnection()) {
            return;
        }
        int v10 = param2.readableBytes();
        if (v10 <= 0) {
            return;
        }
        param2.skipBytes(v10);
        param3.add(param6);
        NetworkStateTransitionHandler.onDecoded((ChannelHandlerContext)param1, param6);
        LogUtil.cfr_underscore(field12717, "Ignored " + v10 + " trailing bytes after disconnect packet for bot=" + ((ViaLoaderCompat)v9).mincedbots$getBotConnectionName() + ", packetType=" + String.valueOf(param7));
        param4.cancel();
    }

    static {
        VMBridge.identifyClass(DecoderHandlerMixin.class, "JC8DJq0i");
    }
}



