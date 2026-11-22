/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.rpc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.rpc.DiscordEventHandlers;
import ru.decide.rpc.DiscordRichPresence;

@Environment(value=EnvType.CLIENT)
public interface DiscordRPC
extends Library {
    public void Discord_UpdateHandlers(DiscordEventHandlers var1);

    public void Discord_UpdatePresence(DiscordRichPresence var1);

    public void Discord_Respond(String var1, int var2);

    public void Discord_Register(String var1, String var2);

    public void Discord_Shutdown();

    public void Discord_UpdateConnection();

    public void Discord_RegisterSteamGame(String var1, String var2);

    public void Discord_RunCallbacks();

    public void Discord_Initialize(String var1, DiscordEventHandlers var2, boolean var3, String var4);

    public void Discord_ClearPresence();

    @Environment(value=EnvType.CLIENT)
    public static class Loader {
        private static DiscordRPC instance;
        private static boolean loaded;

        public static DiscordRPC getInstance() {
            if (!loaded) {
                loaded = true;
                try {
                    instance = (DiscordRPC)Native.loadLibrary((String)"discord-rpc", DiscordRPC.class);
                }
                catch (UnsatisfiedLinkError e) {
                    System.err.println("[NightDLC] Discord RPC \u0431\u0438\u0431\u043b\u0438\u043e\u0442\u0435\u043a\u0430 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430: " + e.getMessage());
                    instance = null;
                }
            }
            return instance;
        }

        public static boolean isAvailable() {
            return Loader.getInstance() != null;
        }

        static {
            loaded = false;
        }
    }
}

