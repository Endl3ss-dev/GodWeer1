package swerve.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

import swerve.Swerve;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Сохранение гардероба: config/swerve/cosmetics.json.
 *
 * <p>Запись идёт только при смене модели (не в кадре рендера).
 */
public final class CosmeticStore {

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/cosmetics.json");

    private static boolean loaded;
    private static boolean loading;

    private CosmeticStore() {
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        loading = true;
        if (!Files.isRegularFile(FILE)) {
            loading = false;
            return;
        }
        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }
            JsonObject o = root.getAsJsonObject();
            JsonElement worn = o.get("worn");
            if (worn != null && worn.isJsonArray() && worn.getAsJsonArray().size() == Wearables.SLOTS) {
                for (int i = 0; i < Wearables.SLOTS; i++) {
                    Wearables.set(i, worn.getAsJsonArray().get(i).getAsInt());
                }
            }
            JsonElement sync = o.get("sync");
            if (sync != null && sync.isJsonPrimitive()) {
                Wearables.sync(sync.getAsBoolean());
            }
        } catch (Exception e) {
            Swerve.LOGGER.warn("cosmetic store: {}", e.toString());
        } finally {
            loading = false;
        }
    }

    public static void save() {
        if (loading) {
            return;
        }
        JsonObject o = new JsonObject();
        com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
        for (int i = 0; i < Wearables.SLOTS; i++) {
            arr.add(Wearables.worn(i));
        }
        o.add("worn", arr);
        o.addProperty("sync", Wearables.sync());
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, o.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Swerve.LOGGER.warn("cosmetic store: {}", e.toString());
        }
    }
}
