package mods.voicechat.eventforge;

import nuclear.control.events.Event;
import net.minecraft.server.MinecraftServer;

public class ServerLifecycleEvent extends Event {
    protected final MinecraftServer server;

    public ServerLifecycleEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}