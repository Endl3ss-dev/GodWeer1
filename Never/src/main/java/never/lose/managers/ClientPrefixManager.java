package never.lose.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import never.lose.NeverLose;
import never.lose.utils.client.ClientFiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class ClientPrefixManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEFAULT_PREFIX = "#";

    private static String prefix = DEFAULT_PREFIX;
    private static boolean autoSave = true;
    private static String lastConfig = "";

    private ClientPrefixManager() {
    }

    public static void init() {
        load();
    }

    public static String getPrefix() {
        return prefix == null || prefix.isEmpty() ? DEFAULT_PREFIX : prefix;
    }

    public static boolean setPrefix(String value) {
        if (value == null) {
            return false;
        }
        String next = value.trim();
        if (next.isEmpty() || next.length() > 3) {
            return false;
        }
        prefix = next;
        save();
        return true;
    }

    public static boolean isAutoSave() {
        return autoSave;
    }

    public static void setAutoSave(boolean value) {
        if (autoSave == value) {
            return;
        }
        autoSave = value;
        save();
    }

    public static String getLastConfig() {
        return lastConfig == null ? "" : lastConfig;
    }

    public static void setLastConfig(String name) {
        lastConfig = name == null ? "" : name.trim();
        save();
    }

    public static void load() {
        File file = ClientFiles.clientFile();
        if (!file.exists()) {
            save();
            return;
        }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (json.has("prefix")) {
                String value = json.get("prefix").getAsString();
                if (value != null && !value.isBlank() && value.length() <= 3) {
                    prefix = value;
                }
            }
            if (json.has("autoSave")) {
                autoSave = json.get("autoSave").getAsBoolean();
            }
            if (json.has("lastConfig")) {
                lastConfig = json.get("lastConfig").getAsString();
            }
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to load client.json: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("prefix", getPrefix());
            json.addProperty("autoSave", autoSave);
            json.addProperty("lastConfig", getLastConfig());
            Files.writeString(ClientFiles.clientFile().toPath(), GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to save client.json: {}", e.getMessage());
        }
    }
}
