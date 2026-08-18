package mods.voicechat.eventforge;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.server.MinecraftServer;

public class ServerLifecycleEvent implements Event {
    protected final MinecraftServer server;

    public ServerLifecycleEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}