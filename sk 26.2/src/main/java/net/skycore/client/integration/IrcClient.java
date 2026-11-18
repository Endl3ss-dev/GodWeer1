package net.skycore.client.integration;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.skycore.client.util.SendDonationHelper;

/** Command compatibility for the removed shared-chat service. */
public final class IrcClient {
    private static volatile String currentPrefix = "user";

    private IrcClient() {}

    public static void init() {}

    public static void connect() {
        unavailable();
    }

    public static void disconnect() {}

    public static boolean isConnected() {
        return false;
    }

    public static boolean isUserEnabled() {
        return false;
    }

    public static void turnOn() {
        unavailable();
    }

    public static void turnOff() {}

    public static String getCurrentPrefix() {
        return currentPrefix;
    }

    public static void setPrefix(String prefix) {
        if (prefix != null && !prefix.isBlank()) {
            currentPrefix = prefix.trim();
        }
    }

    public static void ensureRecentMessagesLoaded() {}

    public static void sendMessage(String message) {
        unavailable();
    }

    public static void sendMessage(String username, String message) {
        unavailable();
    }

    public static void sendMessage(String username, String prefix, String message) {
        unavailable();
    }

    public static void sendDm(String recipient, String message) {
        unavailable();
    }

    public static void sendDm(String username, String recipient, String message) {
        unavailable();
    }

    private static void unavailable() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        Runnable message = () -> SendDonationHelper.sendIrc(
                Component.literal("Общий чат отключён в офлайн-сборке.").withStyle(ChatFormatting.GRAY));
        if (client.isSameThread()) {
            message.run();
        } else {
            client.execute(message);
        }
    }
}
