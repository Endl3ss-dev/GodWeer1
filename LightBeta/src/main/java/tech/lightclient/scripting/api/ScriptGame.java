package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

/** Screens, the server you are on, and packet sending; exposed as {@code game}. */
public final class ScriptGame {

    private final ScriptPackets.Sender packets = new ScriptPackets.Sender();

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    public ScriptPackets.Sender packets() {
        return packets;
    }

    public boolean screenOpen() {
        return mc().currentScreen != null;
    }

    /** NONE, CHAT, INVENTORY, CONTAINER or OTHER. */
    public String screenKind() {
        var screen = mc().currentScreen;
        if (screen == null) return "NONE";
        if (screen instanceof ChatScreen) return "CHAT";
        if (screen instanceof InventoryScreen) return "INVENTORY";
        if (screen instanceof HandledScreen<?>) return "CONTAINER";
        return "OTHER";
    }

    /** Title of the open screen, or null. */
    public ScriptText screenTitle() {
        var screen = mc().currentScreen;
        if (screen == null) return null;
        return new ScriptText.Factory().literal(screen.getTitle().getString());
    }

    public void closeScreen() {
        mc().execute(() -> {
            if (mc().currentScreen != null) mc().player.closeHandledScreen();
        });
    }

    // ---- server ----------------------------------------------------------------------------

    /** Address of the server you are on, or "" in singleplayer. */
    public String serverAddress() {
        var entry = mc().getCurrentServerEntry();
        return entry == null ? "" : entry.address;
    }

    public String serverName() {
        var entry = mc().getCurrentServerEntry();
        return entry == null ? "" : entry.name;
    }

    public boolean singleplayer() {
        return mc().isInSingleplayer();
    }
}
