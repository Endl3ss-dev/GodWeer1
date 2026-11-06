package tech.lightclient.autobuy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Встроенная база известных кастомных предметов сервера.
 * Загружается из ресурса {@code assets/winlend/autobuy-items.json}
 * (порт {@code holy-autobuy-items.json} + загрузчика {@code a.a}).
 */
public final class ItemDatabase {
    public static final List<ItemEntry> LIST = new ArrayList<>();

    private ItemDatabase() {
    }

    /** Перечитывает базу из ресурса. Вызывается один раз при старте. */
    public static void reload() {
        LIST.clear();
        LIST.addAll(loadBuiltin());
    }

    private static List<ItemEntry> loadBuiltin() {
        List<ItemEntry> out = new ArrayList<>();
        try (InputStream in = ItemDatabase.class.getResourceAsStream("/assets/light/autobuy-items.json")) {
            if (in == null) {
                return out;
            }
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("items") || !root.get("items").isJsonArray()) {
                return out;
            }
            JsonArray items = root.getAsJsonArray("items");
            for (JsonElement el : items) {
                JsonObject o = el.getAsJsonObject();
                ItemEntry entry = new ItemEntry();
                entry.itemId = str(o, "itemId", "");
                entry.displayName = str(o, "displayName", null);
                entry.searchQuery = str(o, "searchQuery", null);
                entry.loreKeyword = str(o, "loreKeyword", null);
                entry.requireCustom = bool(o, "custom");
                entry.matchAnySword = bool(o, "matchAnySword");
                if (o.has("markers") && o.get("markers").isJsonArray()) {
                    for (JsonElement me : o.getAsJsonArray("markers")) {
                        JsonObject mo = me.getAsJsonObject();
                        String type = str(mo, "type", "");
                        String key = str(mo, "key", "");
                        String value = str(mo, "value", "");
                        switch (type) {
                            case "PBV" -> entry.markers.add(Marker.pbv(key, value));
                            case "CUSTOM_DATA" -> entry.markers.add(Marker.customData(key, value));
                            case "CUSTOM_ENCH" -> entry.markers.add(Marker.customEnch(key));
                            default -> {
                            }
                        }
                    }
                }
                out.add(entry);
            }
        } catch (Throwable t) {
            return new ArrayList<>();
        }
        return out;
    }

    private static String str(JsonObject o, String key, String def) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : def;
    }

    private static boolean bool(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() && o.get(key).getAsBoolean();
    }
}
