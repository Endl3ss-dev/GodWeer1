package swerve.ui.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// Позиции HUD-элементов: config/swerve/hud.json.
// Запись на диск — только по окончании перетаскивания, в кадре обращений к файлу нет.
public final class HudPositionStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/hud.json");
    private static final Map<String, float[]> POSITIONS = new HashMap<>();

    private static boolean loaded;
    private static boolean dirty;

    private HudPositionStore() {
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        POSITIONS.clear();

        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }

            JsonObject object = root.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                JsonElement value = entry.getValue();
                if (value == null || !value.isJsonObject()) {
                    continue;
                }

                JsonObject position = value.getAsJsonObject();
                JsonElement storedX = position.get("x");
                JsonElement storedY = position.get("y");
                if (storedX == null || storedY == null
                        || !storedX.isJsonPrimitive() || !storedY.isJsonPrimitive()) {
                    continue;
                }
                POSITIONS.put(entry.getKey(), new float[]{storedX.getAsFloat(), storedY.getAsFloat()});
            }
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load HUD positions", exception);
        }
    }

    // {x, y} из хранилища либо null, если элемент ещё ни разу не двигали.
    public static float[] get(String key) {
        return POSITIONS.get(key);
    }

    public static void put(String key, float x, float y) {
        float[] position = POSITIONS.get(key);
        if (position == null) {
            POSITIONS.put(key, new float[]{x, y});
        } else {
            position[0] = x;
            position[1] = y;
        }
        dirty = true;
    }

    public static void remove(String key) {
        if (POSITIONS.remove(key) != null) {
            dirty = true;
        }
    }

    public static void save() {
        if (!dirty) {
            return;
        }
        dirty = false;

        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            for (Map.Entry<String, float[]> entry : POSITIONS.entrySet()) {
                float[] position = entry.getValue();
                JsonObject object = new JsonObject();
                object.addProperty("x", position[0]);
                object.addProperty("y", position[1]);
                root.add(entry.getKey(), object);
            }
            Files.writeString(FILE, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to save HUD positions", exception);
        }
    }
}
