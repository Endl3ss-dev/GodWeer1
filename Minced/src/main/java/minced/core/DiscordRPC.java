package minced.core;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import net.minecraft.client.MinecraftClient;

/**
 * Discord Rich Presence. Оригинальная реализация жила внутри VM-защиты dreamix и не
 * восстановима из client.jar, поэтому написана заново поверх бандлящейся библиотеки
 * meteordevelopment:discord-ipc (она уже подключена в build.gradle).
 *
 * ВАЖНО: оригинальный Discord application id клиента утерян. Чтобы презенс отображался,
 * создай приложение с именем "Minced" на https://discord.com/developers/applications и
 * вставь его Application ID в APP_ID. Картинка большой иконки берётся из ассета "logo"
 * этого приложения (Rich Presence -> Art Assets). При APP_ID = 0 RPC просто не стартует.
 */
public final class DiscordRPC {
    private static final long APP_ID = 1528693450457874513L;
    private static final long UPDATE_INTERVAL_MS = 4000L;

    private static volatile boolean running;
    private static long startTimestamp;

    private DiscordRPC() {
    }

    public static void start() {
        if (running) {
            return;
        }
        if (APP_ID == 0L) {
            System.out.println("[Minced] Discord RPC: APP_ID не задан, презенс отключён (см. minced/core/DiscordRPC.java)");
            return;
        }
        if (!DiscordIPC.start(APP_ID, () -> System.out.println("[Minced] Discord RPC connected"))) {
            System.out.println("[Minced] Discord RPC: Discord не запущен, презенс отключён");
            return;
        }
        running = true;
        startTimestamp = System.currentTimeMillis() / 1000L;
        Thread v0 = new Thread(DiscordRPC::updateLoop, "Minced-DiscordRPC");
        v0.setDaemon(true);
        v0.start();
        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::stop, "Minced-DiscordRPC-Shutdown"));
    }

    public static void stop() {
        if (!running) {
            return;
        }
        running = false;
        DiscordIPC.stop();
    }

    private static void updateLoop() {
        while (running) {
            try {
                DiscordIPC.setActivity(buildPresence());
            } catch (Throwable v0) {
                // Discord мог закрыться на середине сессии — не роняем клиент из-за презенса.
            }
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
            } catch (InterruptedException v0) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static RichPresence buildPresence() {
        MinecraftClient v0 = MinecraftClient.getInstance();
        RichPresence v1 = new RichPresence();
        v1.setDetails("Source by VIBE");
        if (v0.world == null) {
            v1.setState("В главном меню");
        } else if (v0.getCurrentServerEntry() != null) {
            v1.setState("Играет на " + v0.getCurrentServerEntry().address);
        } else {
            v1.setState("Одиночная игра");
        }
        v1.setStart(startTimestamp);
        v1.setLargeImage("logo", "Minced");
        return v1;
    }
}


