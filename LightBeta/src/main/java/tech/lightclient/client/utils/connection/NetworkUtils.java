package tech.lightclient.client.utils.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NetworkUtils {
    private static final List<Packet<?>> SILENT = new CopyOnWriteArrayList<>();
    private static volatile long swapGuardUntil = 0L;

    private NetworkUtils() {}

    public static void beginSwapGuard() {
        swapGuardUntil = System.currentTimeMillis() + 500L;
    }

    public static boolean isSwapGuarded() {
        return System.currentTimeMillis() < swapGuardUntil;
    }

    public static List<Packet<?>> getSilentPackets() {
        return SILENT;
    }

    public static void sendSilentPacket(Packet<?> packet) {
        if (packet == null) return;
        SILENT.add(packet);
        sendPacket(packet);
    }

    public static void sendPacket(Packet<?> packet) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.networkHandler != null) {
            mc.player.networkHandler.sendPacket(packet);
        }
    }
}