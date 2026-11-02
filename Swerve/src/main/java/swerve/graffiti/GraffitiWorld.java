package swerve.graffiti;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

import java.net.SocketAddress;
import java.util.Locale;

/**
 * [+] В каком мире и измерении сейчас игрок — контекст для {@link GraffitiStore}.
 *
 * <p>Граффити привязаны к миру: рисунок, нанесённый на сервере, не должен всплывать
 * в одиночной игре и наоборот. Ключ мира — адрес сервера, а если его нет (интегрированный
 * сервер, LAN) — строка {@code singleplayer}.
 *
 * <p>Значения кэшируются на {@value #REFRESH_MS} мс: рендер спрашивает их каждый кадр,
 * а {@code getRemoteAddress().toString()} каждый кадр — лишняя строка в куче.
 */
public final class GraffitiWorld {

    public static final String SINGLEPLAYER = "singleplayer";
    public static final String UNKNOWN = "unknown";
    public static final String NO_WORLD = "menu";

    private static final long REFRESH_MS = 500L;

    private static String world = UNKNOWN;
    private static String dimension = "";
    private static long refreshedAt;

    private GraffitiWorld() {
    }

    public static String world() {
        ensureFresh();
        return world;
    }

    public static String dimension() {
        ensureFresh();
        return dimension;
    }

    /** Пересчитывает контекст (зовётся из тика; геттеры делают это сами по таймауту). */
    public static void refresh() {
        refreshedAt = System.currentTimeMillis();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null) {
            world = NO_WORLD;
            dimension = "";
            return;
        }

        dimension = minecraft.level.dimension().location().toString();
        world = resolveWorld(minecraft);
    }

    private static String resolveWorld(Minecraft minecraft) {
        if (minecraft.getSingleplayerServer() != null) {
            return SINGLEPLAYER;
        }

        ServerData server = minecraft.getCurrentServer();
        if (server != null && server.ip != null && !server.ip.isBlank()) {
            return "mp:" + server.ip.trim().toLowerCase(Locale.ROOT);
        }

        // getCurrentServer() пуст на этапе входа: пробуем адрес соединения.
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) {
            SocketAddress remote = connection.getConnection().getRemoteAddress();
            if (remote != null) {
                return "mp:" + remote.toString().toLowerCase(Locale.ROOT);
            }
        }
        return SINGLEPLAYER;
    }

    private static void ensureFresh() {
        if (System.currentTimeMillis() - refreshedAt >= REFRESH_MS) {
            refresh();
        }
    }
}
