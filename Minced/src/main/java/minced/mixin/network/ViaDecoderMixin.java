/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.DecoderException
 *  net.minecraft.network.ClientConnection
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.network;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import minced.core.LogUtil;
import minced.core.ViaLoaderCompat;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Pseudo
@Mixin(targets={"com.viaversion.vialoader.netty.ViaDecoder"}, remap=false)
public abstract class ViaDecoderMixin {
    private static String field12695 = "field12695";
    private static String field12696 = "field12696";
    private static String field12697 = "field12697";
    private static String field12698 = "field12698";
    private static String field12699 = "field12699";
    private static String field12700 = "field12700";
    private static String field12701 = "field12701";
    private static String field12702 = "field12702";
    private static String field12703 = "field12703";
    private static String field12704 = "field12704";
    private static String field12705 = "field12705";
    private static String field12706 = "field12706";
    private static String field12707 = "field12707";
    private static String field12708 = "field12708";
    private static String field12709 = "field12709";
    private static String field12710 = "field12710";
    private static String field12711 = "field12711";
    private static String field12712 = "field12712";
    private static String field12713 = "field12713";
    private static String field12714 = "field12714";
    private static String field12715 = "field12715";

    @Inject(method={"decode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$decodeBotPackets(ChannelHandlerContext param1, ByteBuf param2, List<Object> param3, CallbackInfo param4) {
        ViaLoaderCompat v6;
        ClientConnection v5 = ViaDecoderMixin.mincedbots$findClientConnection(param1);
        if (!(v5 instanceof ViaLoaderCompat) || !(v6 = (ViaLoaderCompat)v5).mincedbots$isBotConnection()) {
            return;
        }
        Object v7 = this.mincedbots$getViaConnection();
        if (v7 == null) {
            return;
        }
        if (!ViaDecoderMixin.mincedbots$invokeBoolean(v7, field12695)) {
            throw new DecoderException(field12696);
        }
        if (!ViaDecoderMixin.mincedbots$invokeBoolean(v7, field12697)) {
            param3.add(param2.retain());
            param4.cancel();
            return;
        }
        ByteBuf v8 = param1.alloc().buffer().writeBytes(param2);
        ByteBuf v9 = v8.copy();
        try {
            ViaDecoderMixin.mincedbots$transformIncoming(v7, v8);
            param3.add(v8.retain());
        } catch (Throwable v10) {
            if (!ViaDecoderMixin.mincedbots$isLoginState(v7)) {
                ViaDecoderMixin.mincedbots$throwUnchecked(v10);
            }
            String v11 = "Via login remap failed for bot=" + v6.mincedbots$getBotConnectionName() + ", " + ViaDecoderMixin.mincedbots$describeProtocols(v7) + ", packetId=" + ViaDecoderMixin.mincedbots$readPacketId(v9) + ", packetBytes=" + v9.readableBytes() + ", payloadHex=" + ViaDecoderMixin.mincedbots$describePayload(v9) + ", cause=" + ViaDecoderMixin.mincedbots$rootMessage(v10);
            v6.mincedbots$setLastNetworkFailure(v11);
            LogUtil.cfr_underscore(field12698, v11);
            System.err.println("[MincedBots/ViaBot] " + v11);
            param1.close();
        } finally {
            v9.release();
            v8.release();
        }
        param4.cancel();
    }

    @Unique
    private Object mincedbots$getViaConnection() {
        try {
            Field v1 = this.getClass().getDeclaredField(field12699);
            v1.setAccessible(true);
            return v1.get(this);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            return null;
        }
    }

    @Unique
    private static ClientConnection mincedbots$findClientConnection(ChannelHandlerContext param0) {
        ClientConnection v1 = (ClientConnection)param0.pipeline().get(ClientConnection.class);
        if (v1 != null) {
            return v1;
        }
        for (Map.Entry v3 : param0.pipeline().toMap().entrySet()) {
            Object v5 = v3.getValue();
            if (!(v5 instanceof ClientConnection)) continue;
            ClientConnection v4 = (ClientConnection)v5;
            return v4;
        }
        return null;
    }

    @Unique
    private static boolean mincedbots$isLoginState(Object param0) {
        Object v1 = ViaDecoderMixin.mincedbots$invoke(param0, field12700);
        if (v1 == null) {
            return false;
        }
        return field12701.equals(ViaDecoderMixin.mincedbots$enumName(ViaDecoderMixin.mincedbots$invoke(v1, field12702))) || field12703.equals(ViaDecoderMixin.mincedbots$enumName(ViaDecoderMixin.mincedbots$invoke(v1, field12704)));
    }

    @Unique
    private static String mincedbots$describeProtocols(Object param0) {
        Object v1 = ViaDecoderMixin.mincedbots$invoke(param0, field12705);
        if (v1 == null) {
            return field12706;
        }
        return "clientProtocol=" + String.valueOf(ViaDecoderMixin.mincedbots$invoke(v1, field12707)) + ", serverProtocol=" + String.valueOf(ViaDecoderMixin.mincedbots$invoke(v1, field12708)) + ", clientState=" + String.valueOf(ViaDecoderMixin.mincedbots$invoke(v1, field12709)) + ", serverState=" + String.valueOf(ViaDecoderMixin.mincedbots$invoke(v1, field12710));
    }

    @Unique
    private static void mincedbots$transformIncoming(Object param0, ByteBuf param1) throws Throwable {
        Method v2 = param0.getClass().getMethod(field12711, ByteBuf.class, Function.class);
        try {
            v2.invoke(param0, param1, (Function<Throwable, Exception>)DecoderException::new);
            return;
        } catch (ReflectiveOperationException v3) {
            Throwable v4 = v3.getCause();
            throw v4 != null ? v4 : v3;
        }
    }

    @Unique
    private static boolean mincedbots$invokeBoolean(Object param0, String param1) {
        Boolean v3;
        Object v2 = ViaDecoderMixin.mincedbots$invoke(param0, param1);
        return v2 instanceof Boolean && (v3 = (Boolean)v2) != false;
    }

    @Unique
    private static Object mincedbots$invoke(Object param0, String param1) {
        if (param0 == null) {
            return null;
        }
        try {
            return param0.getClass().getMethod(param1, new Class[0]).invoke(param0, new Object[0]);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            return null;
        }
    }

    @Unique
    private static String mincedbots$enumName(Object param0) {
        Enum v1;
        return param0 instanceof Enum && null != (v1 = (Enum)param0) ? v1.name() : String.valueOf(param0);
    }

    @Unique
    private static int mincedbots$readPacketId(ByteBuf param0) {
        if (!param0.isReadable()) {
            return -1;
        }
        ByteBuf v1 = param0.copy();
        try {
            int v2 = ViaDecoderMixin.mincedbots$readVarInt(v1);
            return v2;
        } catch (Exception exception) {
            return -1;
        } finally {
            v1.release();
        }
    }

    @Unique
    private static String mincedbots$describePayload(ByteBuf param0) {
        if (!param0.isReadable()) {
            return field12712;
        }
        ByteBuf v1 = param0.copy();
        try {
            try {
                ViaDecoderMixin.mincedbots$readVarInt(v1);
            } catch (Exception exception) {
                String v3 = ByteBufUtil.hexDump((ByteBuf)v1, (int)v1.readerIndex(), (int)Math.min(v1.readableBytes(), 96));
                v1.release();
                return v3;
            }
            String v2 = v1.isReadable() ? ByteBufUtil.hexDump((ByteBuf)v1, (int)v1.readerIndex(), (int)Math.min(v1.readableBytes(), 96)) : field12713;
            return v2;
        } finally {
            v1.release();
        }
    }

    @Unique
    private static int mincedbots$readVarInt(ByteBuf param0) {
        int v1 = 0;
        for (int v2 = 0; v2 < 35; v2 += 7) {
            if (!param0.isReadable()) {
                throw new IndexOutOfBoundsException(field12714);
            }
            byte v3 = param0.readByte();
            v1 |= (v3 & 0x7F) << v2;
            if ((v3 & 0x80) != 0) continue;
            return v1;
        }
        throw new IllegalArgumentException(field12715);
    }

    @Unique
    private static String mincedbots$rootMessage(Throwable param0) {
        Throwable v1 = param0;
        while (v1.getCause() != null) {
            v1 = v1.getCause();
        }
        String v2 = v1.getMessage();
        return v2 == null || v2.isBlank() ? v1.getClass().getSimpleName() : v2;
    }

    @Unique
    @SuppressWarnings(value={"unchecked"})
    private static <E extends Throwable> void mincedbots$throwUnchecked(Throwable param0) throws E {
        throw (E)param0;
    }

    static {
        VMBridge.identifyClass(ViaDecoderMixin.class, "OTbFvCGR");
    }
}



