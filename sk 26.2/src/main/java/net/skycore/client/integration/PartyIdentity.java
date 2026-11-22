package net.skycore.client.integration;

import java.net.HttpURLConnection;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.skycore.client.profile.LocalProfile;

/** Local profile identity with no presence publishing or polling. */
public final class PartyIdentity {
    private static final String CLIENT_ID = UUID.randomUUID().toString().replace("-", "");
    private static volatile String lastUsername = "";

    private PartyIdentity() {}

    public static void init() {
        goOnline();
    }

    public static void goOnline() {
        rememberUsername(resolveUsername());
    }

    public static void goOffline() {}

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getLastUsername() {
        return lastUsername;
    }

    public static void rememberUsername(String username) {
        if (username != null && !username.isBlank()) {
            lastUsername = username.trim();
        }
    }

    public static String resolveUsername() {
        String username = LocalProfile.getInstance().profile("username");
        return username != null && !username.isBlank() ? username.trim() : resolveGameNick();
    }

    public static String resolveGameNick() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return "";
        }
        if (client.player != null) {
            String name = client.player.getGameProfile().name();
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }
        if (client.getUser() != null) {
            String name = client.getUser().getName();
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }
        return "";
    }

    public static String resolveTitleUsername() {
        return "";
    }

    public static String appendGameNickQuery(String query) {
        return query;
    }

    public static String appendTitleUserQuery(String query) {
        return query;
    }

    public static String appendPresenceQuery(String query) {
        return query;
    }

    public static String readBody(HttpURLConnection ignored) {
        return "";
    }
}
