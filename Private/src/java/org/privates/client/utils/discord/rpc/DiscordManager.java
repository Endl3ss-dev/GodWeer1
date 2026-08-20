package org.privates.client.utils.discord.rpc;

import com.sun.jna.NativeLibrary;
import net.minecraft.client.Minecraft;
import org.privates.client.utils.discord.rpc.utils.DiscordEventHandlers;
import org.privates.client.utils.discord.rpc.utils.DiscordRPC;
import org.privates.client.utils.discord.rpc.utils.DiscordRichPresence;
import org.privates.client.utils.discord.rpc.utils.RPCButton;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DiscordManager {

    private static final String APPLICATION_ID = "1446998114488815669";
    private static final String LARGE_IMAGE_KEY = "logo";
    private static final String BUTTON_1_LABEL = "Telegram";
    private static final String BUTTON_1_URL = "https://t.me/bravo317st";
    private static final String BUTTON_2_LABEL = "YouTube";
    private static final String BUTTON_2_URL = "https://www.youtube.com/@bravoprod";

    private Thread callbackThread;
    private volatile boolean running;
    private long startTimestamp;

    public synchronized void start() {
        if (running) return;
        try {
            prepareNativeLibrary();
            startTimestamp = System.currentTimeMillis() / 1000L;

            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
            DiscordRPC.INSTANCE.Discord_Initialize(APPLICATION_ID, handlers, true, "");
            updatePresence();

            running = true;
            callbackThread = new Thread(() -> {
                while (running) {
                    try {
                        DiscordRPC.INSTANCE.Discord_RunCallbacks();
                        updatePresence();
                        Thread.sleep(5000L);
                    } catch (Throwable t) {
                        stop();
                    }
                }
            }, "Discord-RPC");
            callbackThread.setDaemon(true);
            callbackThread.start();
        } catch (Throwable ignored) {
            running = false;
        }
    }

    public synchronized void stop() {
        if (!running && callbackThread == null) return;
        running = false;

        try {
            DiscordRPC.INSTANCE.Discord_ClearPresence();
            DiscordRPC.INSTANCE.Discord_Shutdown();
        } catch (Throwable ignored) {
        }

        if (callbackThread != null) {
            callbackThread.interrupt();
            callbackThread = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void updatePresence() {
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder();
        builder.setStartTimestamp(startTimestamp);
        builder.setDetails(buildDetails());
        builder.setState(buildState());
        builder.setLargeImage(LARGE_IMAGE_KEY, getUsernameSafe());
        builder.setButtons(
                RPCButton.create(BUTTON_1_LABEL, BUTTON_1_URL),
                RPCButton.create(BUTTON_2_LABEL, BUTTON_2_URL)
        );
        DiscordRPC.INSTANCE.Discord_UpdatePresence(builder.build());
    }

    private String buildDetails() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return "In menu";
        return "Playing as " + getUsernameSafe();
    }

    private String buildState() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return "Excellent Client";
        if (mc.isSingleplayer()) return "Singleplayer";
        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            return "Server: " + mc.getCurrentServerData().serverIP;
        }
        return "Multiplayer";
    }

    private String getUsernameSafe() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getName() != null) {
            return mc.player.getName().getString();
        }
        return "Unknown";
    }

    private void prepareNativeLibrary() throws IOException {
        Path libDir = Path.of(System.getProperty("user.home"), ".excellent-client", "discord");
        Files.createDirectories(libDir);

        Path dllPath = libDir.resolve("discord-rpc.dll");
        if (!Files.exists(dllPath)) {
            Path jarPath = Path.of("workspace", "client-libs", "discord-lib.jar");
            if (!Files.exists(jarPath)) {
                throw new IOException("discord-lib.jar not found: " + jarPath.toAbsolutePath());
            }
            try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarPath.toFile())) {
                java.util.jar.JarEntry entry = jar.getJarEntry("win32-x86-64/discord-rpc.dll");
                if (entry == null) {
                    entry = jar.getJarEntry("win32-x86/discord-rpc.dll");
                }
                if (entry == null) {
                    throw new IOException("discord-rpc.dll not found inside discord-lib.jar");
                }
                try (InputStream in = jar.getInputStream(entry)) {
                    Files.copy(in, dllPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        NativeLibrary.addSearchPath("discord-rpc", libDir.toAbsolutePath().toString());
    }
}

