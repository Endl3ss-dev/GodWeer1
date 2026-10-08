package swerve.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class FriendManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> FRIENDS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private static final Set<String> VIEW = Collections.unmodifiableSet(FRIENDS);
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/friends.json");

    private FriendManager() {
    }

    public static void init() {
        load();
    }

    public static boolean addFriend(String name) {
        String sanitizedName = sanitize(name);
        if (sanitizedName.isEmpty() || !FRIENDS.add(sanitizedName)) {
            return false;
        }
        save();
        return true;
    }

    public static boolean removeFriend(String name) {
        String sanitizedName = sanitize(name);
        if (sanitizedName.isEmpty() || !FRIENDS.remove(sanitizedName)) {
            return false;
        }
        save();
        return true;
    }

    public static boolean isFriend(String name) {
        return name != null && !name.isEmpty() && FRIENDS.contains(name);
    }

    public static boolean isFriend(Player player) {
        return player != null && isFriend(player.getGameProfile().getName());
    }

    public static Set<String> getFriends() {
        return VIEW;
    }

    public static void load() {
        FRIENDS.clear();
        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonArray()) {
                return;
            }

            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                String name = readName(element);
                String sanitizedName = sanitize(name);
                if (!sanitizedName.isEmpty()) {
                    FRIENDS.add(sanitizedName);
                }
            }
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load friends", exception);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonArray array = new JsonArray();
            for (String friend : FRIENDS) {
                JsonObject object = new JsonObject();
                object.addProperty("name", friend);
                array.add(object);
            }
            Files.writeString(FILE, GSON.toJson(array), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to save friends", exception);
        }
    }

    private static String readName(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsString();
        }
        if (!element.isJsonObject()) {
            return "";
        }

        JsonObject object = element.getAsJsonObject();
        JsonElement name = object.get("name");
        return name == null || !name.isJsonPrimitive() ? "" : name.getAsString();
    }

    private static String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String trimmed = name.trim();
        int formattingIndex = trimmed.indexOf('§');
        if (formattingIndex < 0) {
            return trimmed;
        }

        StringBuilder builder = new StringBuilder(trimmed.length());
        for (int i = 0, length = trimmed.length(); i < length; i++) {
            char character = trimmed.charAt(i);
            if (character == '§' && i + 1 < length) {
                i++;
                continue;
            }
            builder.append(character);
        }
        return builder.toString().trim();
    }
}
