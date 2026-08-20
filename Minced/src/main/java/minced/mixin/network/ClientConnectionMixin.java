/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  io.netty.channel.ChannelHandlerContext
 *  minced.mixin.Г$Ч
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.NetworkPhase
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket
 *  net.minecraft.network.packet.s2c.play.BossBarS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  sg.ec.pb
 *  sg.ec.ТЖ$Ч
 *  sg.ec.chb
 */
package minced.mixin.network;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelHandlerContext;
import minced.core.ConnectionState;
import minced.core.PacketFlowState;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.LogUtil;
import minced.core.EventPacket;
import minced.core.ServerDetector;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.ViaLoaderCompat;
import minced.core.PacketQueue;
import minced.core.ConnectionUtil;
import minced.core.BotContext;
import minced.core.RecordClass229;
import minced.enums.RECEIVE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec.pb;
import sg.ec.tzh;
import sg.ec.chb;

@Mixin(value={ClientConnection.class})
public class ClientConnectionMixin
implements ViaLoaderCompat {
    @Unique
    private boolean mincedbots$botConnection;
    @Unique
    private String mincedbots$botConnectionName;
    @Unique
    private String mincedbots$lastNetworkFailure;
    private static final String field12158 = "";
    private static final String field12159 = "";
    private static final String field12160 = "";
    private static final String field12161 = "";
    private static final long field12162 = 0L;
    private static final String field12163 = "";
    private static final long field12164 = 0L;
    private static final String field12165 = "";
    private static final String field12166 = "";
    private static final long field12167 = 0L;
    private static final String field12168 = "";
    private static final String field12169 = "";
    private static final long field12170 = 0L;
    private static final String field12171 = "";
    private static final String field12172 = "";
    private static final long field12173 = 0L;
    private static final String field12174 = "";
    private static final String field12175 = "";
    private static final String field12176 = "";
    private static final String field12177 = "";
    private static final String field12178 = "";
    private static final String field12179 = "";
    private static final String field12180 = "";
    private static final String field12181 = "";
    private static final String field12182 = "";
    private static final String field12183 = "";
    private static final String field12184 = "";
    private static final String field12185 = "";
    private static final String field12186 = "";

    @Override
    public void handlerAdded(ChannelHandlerContext context) {}

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {}

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {}

    @WrapOperation(method={"handlePacket"}, at={@At(value="INVOKE", target="Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V")})
    private static <T extends PacketListener> void mincedbots$swapClientContextForPacket(Packet<T> param0, PacketListener param1, Operation<Void> param2) {
        g2.ch v3 = ClientConnectionMixin.mincedbots$beginContextSwap(param1);
        try {
            param2.call(new Object[]{param0, param1});
            return;
        } finally {
            ClientConnectionMixin.mincedbots$finishContextSwap(param1, v3);
        }
    }

    @Inject(method={"handlePacket"}, at={@At(value="HEAD")}, cancellable=true)
    private static <T extends PacketListener> void mincedbots$triggerReceivePacketEvent(Packet<T> param0, PacketListener param1, CallbackInfo param2) {
        LogUtil.cfr_underscore(field12158, "RECV " + LogUtil.method2277(param0) + " listener=" + LogUtil.method2271(param1) + ", client=" + LogUtil.method2269(MinecraftClient.getInstance()));
        if (param1 instanceof ConnectionUtil) {
            return;
        }
        // Login and configuration packets are required to advance the protocol state.
        // Gameplay modules must not be able to cancel or replace them.
        if (param1.getPhase() != NetworkPhase.PLAY) {
            return;
        }
        try {
            EventPacket v3 = new EventPacket(param0, RECEIVE.RECEIVE);
            EventBus.method3015(((Object)v3));
            if (v3.method2297()) {
                param2.cancel();
                return;
            }
            Packet v4 = v3.method2467();
            if (v4 instanceof BossBarS2CPacket) {
                BossBarS2CPacket v5 = (BossBarS2CPacket)v4;
                ServerDetector.method2686(v5);
            }
        } catch (Throwable t) {
            // Protect Netty pipeline from client-side event/packet processing exceptions
        }
    }

    @Inject(method={"send(Lnet/minecraft/network/packet/Packet;)V"}, at={@At(value="HEAD")})
    private void mincedbots$onSprintPacket(Packet<?> param1, CallbackInfo param2) {
        if (param1 instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket v3 = (ClientCommandC2SPacket)param1;
            ClientCommandC2SPacket.Mode v4 = v3.getMode();
            if (v4 == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                PacketFlowState.field8033 = true;
            } else if (v4 == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                PacketFlowState.field8033 = false;
            }
        }
    }

    @Inject(method={"send(Lnet/minecraft/network/packet/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void mincedbots$triggerSendPacketEvent(Packet<?> param1, CallbackInfo param2) {
        if (PacketQueue.method3504().contains(param1)) {
            PacketQueue.method3504().remove(param1);
            return;
        }
        PacketListener v3 = ((ClientConnection)(Object)this).getPacketListener();
        LogUtil.cfr_underscore(field12159, "SEND " + LogUtil.method2277(param1) + " listener=" + LogUtil.method2271(v3) + ", client=" + LogUtil.method2269(MinecraftClient.getInstance()));
        if (v3 instanceof ConnectionUtil) {
            if (v3.getPhase() != NetworkPhase.PLAY && ClientConnectionMixin.mincedbots$isUnexpectedBotPlayPacket(param1)) {
                LogUtil.cfr_underscore(field12160, "Blocked unexpected bot play packet outside PLAY: " + LogUtil.method2277(param1) + ", listener=" + LogUtil.method2271(v3));
                param2.cancel();
            }
            return;
        }
        // The login/configuration handshake must bypass gameplay packet listeners.
        if (v3 == null || v3.getPhase() != NetworkPhase.PLAY) {
            return;
        }
        EventPacket v4 = new EventPacket(param1, RECEIVE.SEND);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param2.cancel();
            return;
        }
        Packet v5 = v4.method2467();
        if (v5 != param1) {
            param2.cancel();
            ((ClientConnection)(Object)this).send(v5, null);
        }
    }

    @Inject(method={"exceptionCaught"}, at={@At(value="HEAD")})
    private void mincedbots$captureBotViaFailure(ChannelHandlerContext param1, Throwable param2, CallbackInfo param3) {
        if (!this.mincedbots$botConnection) {
            return;
        }
        PacketListener v4 = ((ClientConnection)(Object)this).getPacketListener();
        if (v4 == null || v4.getPhase() != NetworkPhase.LOGIN) {
            return;
        }
        if (!ClientConnectionMixin.mincedbots$isViaLoginFailure(param2)) {
            return;
        }
        String v5 = "Via login remap failed for bot=" + this.mincedbots$getBotConnectionName() + ", phase=" + String.valueOf(v4.getPhase()) + ", cause=" + ClientConnectionMixin.mincedbots$rootMessage(param2);
        this.mincedbots$lastNetworkFailure = v5;
        pb.t2((String)this.mincedbots$getBotConnectionName(), (String)v5);
        LogUtil.cfr_underscore(field12161, v5);
        System.err.println("[MincedBots/ViaBot] " + v5);
    }

    @Unique
    private static g2.ch mincedbots$beginContextSwap(PacketListener param0) {
        MinecraftClient v1 = MinecraftClient.getInstance();
        if (v1 == null) {
            return null;
        }
        if (param0 instanceof BotContext) {
            BotContext v2 = (BotContext)param0;
            if (!v1.isOnThread()) {
                LogUtil.method2275("net-bot-context-skip-off-thread-" + v2.getName(), field12162, field12163, "Skipping bot packet context swap off main thread for " + v2.getName() + ", client=" + LogUtil.method2269(v1));
                return null;
            }
            RecordClass229 v3 = chb.cfr_renamed_5((MinecraftClient)v1);
            if (ClientConnectionMixin.mincedbots$matchesContext(v3, v2)) {
                return null;
            }
            LogUtil.method2275("net-bot-context-swap-" + v2.getName(), field12164, field12165, "Applying bot packet context for " + v2.getName() + ", current=" + LogUtil.method2270(v3) + ", botPlayer=" + LogUtil.method2272(v2.getPlayer()));
            tzh.ch v4 = v2.isActiveView() ? tzh.ch.н : ConnectionState.method1883(v3);
            chb.Я((MinecraftClient)v1, (BotContext)v2);
            return new g2.ch(v3, v4);
        }
        BotView v2 = ServiceRegistry.method2119();
        if (v2 == null || !v2.method2198()) {
            return null;
        }
        RecordClass229 v3 = v2.method2220();
        if (!ClientConnectionMixin.mincedbots$isUsableSnapshot(v3)) {
            return null;
        }
        if (!v1.isOnThread()) {
            LogUtil.method2275(field12166, field12167, field12168, "Skipping main packet context swap off main thread during bot-view. main=" + LogUtil.method2270(v3) + ", client=" + LogUtil.method2269(v1));
            return null;
        }
        RecordClass229 v4 = chb.cfr_renamed_5((MinecraftClient)v1);
        if (ClientConnectionMixin.mincedbots$matchesSnapshot(v4, v3)) {
            return null;
        }
        LogUtil.method2275(field12169, field12170, field12171, "Applying main packet context during bot-view. main=" + LogUtil.method2270(v3) + ", current=" + LogUtil.method2270(v4));
        chb.Я((MinecraftClient)v1, (RecordClass229)v3);
        return new g2.ch(v4, tzh.ch.н);
    }

    @Unique
    private static void mincedbots$finishContextSwap(PacketListener param0, g2.ch param1) {
        BotView v3;
        if (param1 == null || param1.snapshot() == null) {
            return;
        }
        MinecraftClient v2 = MinecraftClient.getInstance();
        if (v2 == null) {
            param1.close();
            return;
        }
        if (!(param0 instanceof BotContext) && (v3 = ServiceRegistry.method2119()) != null && v3.method2205() && v2.isOnThread()) {
            v3.method2192(chb.cfr_renamed_5((MinecraftClient)v2));
        }
        LogUtil.method2275(field12172, field12173, field12174, "Restoring client context after packet. listener=" + LogUtil.method2271(param0) + ", restore=" + LogUtil.method2270(param1.snapshot()));
        chb.Я((MinecraftClient)v2, (RecordClass229)param1.snapshot());
        param1.close();
    }

    @Unique
    private static boolean mincedbots$isUsableSnapshot(RecordClass229 param0) {
        return param0 != null && param0.world() != null && param0.player() != null && param0.interactionManager() != null;
    }

    @Unique
    private static boolean mincedbots$matchesSnapshot(RecordClass229 param0, RecordClass229 param1) {
        return param0 != null && param1 != null && param0.world() == param1.world() && param0.player() == param1.player() && param0.interactionManager() == param1.interactionManager() && param0.cameraEntity() == param1.cameraEntity();
    }

    @Unique
    private static boolean mincedbots$matchesContext(RecordClass229 param0, BotContext param1) {
        return param0 != null && param1 != null && param0.world() == param1.method_2890() && param0.player() == param1.getPlayer() && param0.interactionManager() == param1.getInteractionManager() && param0.cameraEntity() == param1.method3584();
    }

    @Unique
    private static boolean mincedbots$isUnexpectedBotPlayPacket(Packet<?> param0) {
        if (param0 == null || param0 instanceof AcknowledgeReconfigurationC2SPacket) {
            return false;
        }
        if (param0 instanceof ClientTickEndC2SPacket) {
            return true;
        }
        Package v1 = param0.getClass().getPackage();
        String v2 = v1 == null ? "" : v1.getName();
        return v2.startsWith(field12175);
    }

    @Unique
    private static boolean mincedbots$isViaLoginFailure(Throwable param0) {
        for (Throwable v1 = param0; v1 != null; v1 = v1.getCause()) {
            String v2 = v1.getClass().getName();
            String v3 = v1.getMessage();
            if ((v2.startsWith(field12176) || v2.contains(field12177)) && v3 != null && (v3.contains(field12178) || v3.contains(field12179) || v3.contains(field12180))) {
                return true;
            }
            for (StackTraceElement v7 : v1.getStackTrace()) {
                String v8 = v7.getClassName();
                if (!v8.startsWith(field12181) || !v8.contains(field12182) && !v8.contains(field12183) && !v8.contains(field12184)) continue;
                return true;
            }
        }
        return false;
    }

    @Unique
    private static String mincedbots$rootMessage(Throwable param0) {
        Throwable v1;
        for (v1 = param0; v1 != null && v1.getCause() != null; v1 = v1.getCause()) {
        }
        if (v1 == null) {
            return field12185;
        }
        String v2 = v1.getMessage();
        return v2 == null || v2.isBlank() ? v1.getClass().getSimpleName() : v2;
    }

    @Override
    public void mincedbots$markAsBotConnection(String param1) {
        this.mincedbots$botConnection = true;
        this.mincedbots$botConnectionName = param1;
    }

    @Override
    public boolean mincedbots$isBotConnection() {
        return this.mincedbots$botConnection;
    }

    @Override
    public String mincedbots$getBotConnectionName() {
        return this.mincedbots$botConnectionName != null ? this.mincedbots$botConnectionName : field12186;
    }

    @Override
    public void mincedbots$setLastNetworkFailure(String param1) {
        this.mincedbots$lastNetworkFailure = param1;
    }

    @Override
    public String mincedbots$getLastNetworkFailure() {
        return this.mincedbots$lastNetworkFailure;
    }

    @Override
    public String mincedbots$consumeLastNetworkFailure() {
        String v1 = this.mincedbots$lastNetworkFailure;
        this.mincedbots$lastNetworkFailure = null;
        return v1;
    }

    static {
        VMBridge.identifyClass(ClientConnectionMixin.class, "VwLAzs8e");
    }
}



