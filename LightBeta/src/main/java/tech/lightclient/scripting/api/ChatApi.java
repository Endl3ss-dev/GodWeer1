package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/** Chat helpers, mirroring the "Сообщения" docs. */
public final class ChatApi {

    private final String scriptId;

    public ChatApi(String scriptId) {
        this.scriptId = scriptId;
    }

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static Text asText(Object message) {
        if (message instanceof Text t) return t;
        if (message instanceof ScriptText st) return st.build();
        return Text.literal(String.valueOf(message));
    }

    /** Client-side message, visible only to you. */
    public void print(Object message) {
        Text text = asText(message);
        mc().execute(() -> {
            MinecraftClient c = mc();
            if (c.inGameHud != null) c.inGameHud.getChatHud().addMessage(text);
        });
    }

    public void printPrefixed(String prefix, Object message) {
        MutableText line = Text.literal(prefix + " ").append(asText(message));
        print(line);
    }

    /** Sends a real chat message to the server. */
    public void sendToServer(String message) {
        if (message == null || message.isBlank()) return;
        mc().execute(() -> {
            MinecraftClient c = mc();
            if (c.player != null && c.player.networkHandler != null) {
                c.player.networkHandler.sendChatMessage(message);
            }
        });
    }

    /** Runs a server command; the leading slash is optional. */
    public void runCommand(String command) {
        if (command == null || command.isBlank()) return;
        String cmd = command.startsWith("/") ? command.substring(1) : command;
        mc().execute(() -> {
            MinecraftClient c = mc();
            if (c.player != null && c.player.networkHandler != null) {
                c.player.networkHandler.sendChatCommand(cmd);
            }
        });
    }

    public String scriptId() {
        return scriptId;
    }
}
