/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.NetworkThreadUtils
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket
 *  net.minecraft.util.thread.ThreadExecutor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  sg.ec.ТЖ$Ч
 *  sg.ec.chb
 */
package minced.mixin.network;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import minced.core.ConnectionState;
import minced.core.HudAccess;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.LogUtil;
import minced.core.ConnectionUtil;
import minced.core.BotContext;
import minced.core.RecordClass229;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec.tzh;
import sg.ec.chb;

@Mixin(value={NetworkThreadUtils.class})
public abstract class NetworkThreadUtilsMixin {
    private static final String field12680 = "net-main-context-scheduled";
    private static final long field12681 = 0L;
    private static final String field12682 = "";
    private static final String field12683 = "net-main-context-scheduled-restore";
    private static final long field12684 = 0L;
    private static final String field12685 = "";
    private static final String field12686 = "net-main-context-scheduled-error";
    private static final long field12687 = 0L;
    private static final String field12688 = "";
    private static final long field12689 = 0L;
    private static final String field12690 = "";
    private static final long field12691 = 0L;
    private static final String field12692 = "";
    private static final long field12693 = 0L;
    private static final String field12694 = "";

    @WrapOperation(method={"forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/thread/ThreadExecutor;executeSync(Ljava/lang/Runnable;)V")})
    private static <T extends PacketListener> void mincedbots$wrapScheduledPacketExecution(ThreadExecutor<?> executor, Runnable task, Operation<Void> operation, Packet<T> packet, T listener, ThreadExecutor<?> packetExecutor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            operation.call(executor, task);
            return;
        }
        if (listener instanceof ConnectionUtil) {
            if (!(listener instanceof BotContext bot) || !NetworkThreadUtilsMixin.mincedbots$isUsableContext(bot)) {
                operation.call(executor, task);
                return;
            }
            Runnable botTask = () -> {
                RecordClass229 snapshot = chb.cfr_renamed_5(client);
                if (NetworkThreadUtilsMixin.mincedbots$matchesContext(snapshot, bot)) {
                    task.run();
                    return;
                }
                LogUtil.method2275("net-bot-context-scheduled-" + bot.getName(), field12689, field12690, "Applying scheduled bot packet context. packet=" + LogUtil.method2277(packet) + ", bot=" + bot.getName() + ", current=" + LogUtil.method2270(snapshot));
                tzh.ch contextScope = bot.isActiveView() ? tzh.ch.н : ConnectionState.method1883(snapshot);
                try {
                    chb.Я(client, bot);
                    task.run();
                } catch (Throwable throwable) {
                    chb.Я(client, snapshot);
                    contextScope.close();
                    LogUtil.method2275("net-bot-context-scheduled-restore-" + bot.getName(), field12693, field12694, "Restored client context after scheduled bot packet. packet=" + LogUtil.method2277(packet) + ", restore=" + LogUtil.method2270(snapshot));
                    throw new RuntimeException(throwable);
                }
                chb.Я(client, snapshot);
                contextScope.close();
                LogUtil.method2275("net-bot-context-scheduled-restore-" + bot.getName(), field12691, field12692, "Restored client context after scheduled bot packet. packet=" + LogUtil.method2277(packet) + ", restore=" + LogUtil.method2270(snapshot));
            };
            operation.call(executor, botTask);
            return;
        }
        BotView manager = (BotView)ServiceRegistry.method2118(BotView.class);
        RecordClass229 mainSnapshot = manager != null ? manager.method2220() : null;
        if (manager == null || !manager.method2198() || !NetworkThreadUtilsMixin.mincedbots$isUsableSnapshot(mainSnapshot)) {
            operation.call(executor, task);
            return;
        }
        if (NetworkThreadUtilsMixin.mincedbots$shouldSuppressMainChatPacket(packet)) {
            return;
        }
        boolean affectsChatHud = NetworkThreadUtilsMixin.mincedbots$affectsChatHud(packet);
        Runnable mainTask = () -> {
            RecordClass229 currentSnapshot = chb.cfr_renamed_5(client);
            boolean swapped = !NetworkThreadUtilsMixin.mincedbots$matchesSnapshot(currentSnapshot, mainSnapshot);
            if (swapped) {
                LogUtil.method2275(field12680, field12681, field12682, "Applying scheduled main packet context during bot-view. packet=" + LogUtil.method2277(packet) + ", main=" + LogUtil.method2270(mainSnapshot) + ", current=" + LogUtil.method2270(currentSnapshot));
                chb.Я(client, mainSnapshot);
            }
            HudAccess hudState = manager.method2196(affectsChatHud);
            try {
                task.run();
                manager.method2192(chb.cfr_renamed_5(client));
                manager.method2219(hudState, affectsChatHud);
            } catch (Throwable throwable) {
                manager.method2219(hudState, affectsChatHud);
                if (swapped) {
                    chb.Я(client, currentSnapshot);
                    LogUtil.method2275(field12686, field12687, field12688, "Restored client context after scheduled main packet. packet=" + LogUtil.method2277(packet) + ", restore=" + LogUtil.method2270(currentSnapshot));
                }
                throw new RuntimeException(throwable);
            }
            if (swapped) {
                chb.Я(client, currentSnapshot);
                LogUtil.method2275(field12683, field12684, field12685, "Restored client context after scheduled main packet. packet=" + LogUtil.method2277(packet) + ", restore=" + LogUtil.method2270(currentSnapshot));
            }
        };
        operation.call(executor, mainTask);
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
    private static boolean mincedbots$isUsableContext(BotContext param0) {
        return param0 != null && param0.method_2890() != null && param0.getPlayer() != null && param0.getInteractionManager() != null;
    }

    @Unique
    private static boolean mincedbots$matchesContext(RecordClass229 param0, BotContext param1) {
        return param0 != null && param1 != null && param0.world() == param1.method_2890() && param0.player() == param1.getPlayer() && param0.interactionManager() == param1.getInteractionManager() && param0.cameraEntity() == param1.method3584();
    }

    @Unique
    private static boolean mincedbots$affectsChatHud(Packet<?> param0) {
        return param0 instanceof GameMessageS2CPacket || param0 instanceof ChatMessageS2CPacket || param0 instanceof ProfilelessChatMessageS2CPacket || param0 instanceof DeathMessageS2CPacket;
    }

    @Unique
    private static boolean mincedbots$shouldSuppressMainChatPacket(Packet<?> param0) {
        return NetworkThreadUtilsMixin.mincedbots$affectsChatHud(param0);
    }

    static {
        VMBridge.identifyClass(NetworkThreadUtilsMixin.class, "AOzvtOH6");
    }
}



