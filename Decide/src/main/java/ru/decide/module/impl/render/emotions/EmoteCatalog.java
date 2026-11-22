/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.module.impl.render.emotions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.module.impl.render.emotions.LegacyEmotion;

@Environment(value=EnvType.CLIENT)
public final class EmoteCatalog {
    private static final String CATALOG_PATH = "/assets/decide/emotes/catalog.json";
    private static EmoteCatalog instance;
    private final List<Entry> entries;
    private final Map<Integer, Entry> byId;
    private String error = "";

    private EmoteCatalog() {
        Entry entry;
        ArrayList<Entry> out = new ArrayList<Entry>();
        LinkedHashMap<Integer, Entry> map = new LinkedHashMap<Integer, Entry>();
        try (InputStream stream = EmoteCatalog.class.getResourceAsStream(CATALOG_PATH);){
            if (stream == null) {
                throw new IllegalStateException("catalog missing");
            }
            JsonArray array = JsonParser.parseReader((Reader)new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement element2 : array) {
                JsonObject object = element2.getAsJsonObject();
                entry = new Entry(object.get("id").getAsInt(), object.get("name").getAsString(), object.get("duration").getAsDouble(), object.get("loop").getAsBoolean(), object.get("hasProps").getAsBoolean());
                out.add(entry);
                map.put(entry.id(), entry);
            }
            out.sort(Comparator.comparing(Entry::name));
        }
        catch (Exception e) {
            this.error = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u044d\u043c\u043e\u0446\u0438\u0438";
            System.err.println("[DecideVisual Emotions] " + this.error + ": " + e.getMessage());
        }
        ArrayList<Entry> combined = new ArrayList<Entry>();
        for (LegacyEmotion legacy : LegacyEmotion.values()) {
            entry = new Entry(legacy.id(), legacy.localized(), legacy.durationTicks(), false, false);
            combined.add(entry);
            map.put(entry.id(), entry);
        }
        combined.addAll(out);
        this.entries = List.copyOf(combined);
        this.byId = map;
    }

    public static EmoteCatalog instance() {
        if (instance == null) {
            instance = new EmoteCatalog();
        }
        return instance;
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public Entry byId(int id) {
        return this.byId.get(id);
    }

    public String error() {
        return this.error;
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(int id, String name, double duration, boolean loop, boolean props) {
        public boolean isLegacy() {
            return this.id >= 100000;
        }

        public class_2960 preview() {
            return class_2960.method_60655((String)"decide", (String)("dynamic/emote_preview_" + this.id));
        }

        public double seconds() {
            return this.duration / 20.0;
        }
    }
}

