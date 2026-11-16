/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.rpc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.rpc.DiscordEventHandlers;
import ru.decide.rpc.DiscordRPC;
import ru.decide.rpc.DiscordRichPresence;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public class RPC
implements IMinecraft {
    public static DiscordRichPresence presence = new DiscordRichPresence();
    public static boolean started;
    private static Thread thread;

    public void startRpc() {
        if (!DiscordRPC.Loader.isAvailable()) {
            return;
        }
        DiscordRPC rpc = DiscordRPC.Loader.getInstance();
        if (!started) {
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize("1515740176549416970", handlers, true, "");
            RPC.presence.startTimestamp = System.currentTimeMillis() / 1000L;
            RPC.presence.largeImageText = "1.21.11";
            rpc.Discord_UpdatePresence(presence);
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();
                    String serverIp = "\u0412 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e";
                    if (RPC.mc.field_1687 != null) {
                        serverIp = mc.method_1562() != null && mc.method_1562().method_45734() != null ? RPC.mc.method_1562().method_45734().field_3761 : "\u041e\u0434\u0438\u043d\u043e\u0447\u043d\u044b\u0439 \u043c\u0438\u0440";
                    }
                    RPC.presence.details = "1.21.11";
                    RPC.presence.state = serverIp;
                    RPC.presence.button_label_1 = "Download";
                    RPC.presence.button_url_1 = "https://DecideVisuals.fun";
                    RPC.presence.button_label_2 = "Telegram";
                    RPC.presence.button_url_2 = "https://t.me/DecideVisuals";
                    RPC.presence.largeImageKey = "9809f2b0af4a1126b606ad55158d6dd74e608f5984f52bd7ed551f12e9741c55";
                    RPC.presence.largeImageText = "DecideVisuals";
                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }, "TH-RPC-Handler");
            thread.start();
        }
    }
}

