/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 */
package ru.decide.module.impl.render.emotions;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import ru.decide.module.impl.render.emotions.EmoteCatalog;
import ru.decide.module.impl.render.emotions.LegacyEmotion;

@Environment(value=EnvType.CLIENT)
public final class EmotionSlots {
    public static final int SLOT_COUNT = 7;
    private static final int EMPTY = -1;
    private static final String FILE_NAME = "decide_emotions.json";
    private static EmotionSlots instance;
    private final int[] slotIds = new int[7];
    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("decide_emotions.json");

    private EmotionSlots() {
        this.load();
        this.migrateDefaults();
    }

    public static EmotionSlots instance() {
        if (instance == null) {
            instance = new EmotionSlots();
        }
        return instance;
    }

    public EmoteCatalog.Entry get(int slot) {
        if (slot < 0 || slot >= 7 || this.slotIds[slot] == -1) {
            return null;
        }
        return EmoteCatalog.instance().byId(this.slotIds[slot]);
    }

    public int getId(int slot) {
        return slot >= 0 && slot < 7 ? this.slotIds[slot] : -1;
    }

    public void set(int slot, EmoteCatalog.Entry entry) {
        if (slot < 0 || slot >= 7) {
            return;
        }
        this.slotIds[slot] = entry == null ? -1 : entry.id();
        this.save();
    }

    public void clear(int slot) {
        this.set(slot, null);
    }

    public int indexOf(EmoteCatalog.Entry entry) {
        if (entry == null) {
            return -1;
        }
        for (int i = 0; i < 7; ++i) {
            if (this.slotIds[i] != entry.id()) continue;
            return i;
        }
        return -1;
    }

    public boolean contains(EmoteCatalog.Entry entry) {
        return this.indexOf(entry) >= 0;
    }

    public boolean remove(EmoteCatalog.Entry entry) {
        int index = this.indexOf(entry);
        if (index < 0) {
            return false;
        }
        this.slotIds[index] = -1;
        this.save();
        return true;
    }

    private void load() {
        for (int i = 0; i < 7; ++i) {
            this.slotIds[i] = -1;
        }
        try {
            if (!Files.exists(this.file, new LinkOption[0])) {
                this.fillDefaults();
                this.save();
                return;
            }
            JsonObject root = JsonParser.parseString((String)Files.readString(this.file, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("slots") || !root.get("slots").isJsonArray()) {
                this.fillDefaults();
                return;
            }
            JsonArray arr = root.getAsJsonArray("slots");
            boolean any = false;
            for (int i = 0; i < 7; ++i) {
                this.slotIds[i] = -1;
                if (i >= arr.size() || arr.get(i).isJsonNull()) continue;
                try {
                    int id = arr.get(i).getAsInt();
                    if (EmoteCatalog.instance().byId(id) == null) continue;
                    this.slotIds[i] = id;
                    any = true;
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (!any) {
                this.fillDefaults();
            }
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Emotions] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043d\u0430\u0431\u043e\u0440 \u044d\u043c\u043e\u0446\u0438\u0439: " + e.getMessage());
            this.fillDefaults();
        }
    }

    private void fillDefaults() {
        List<EmoteCatalog.Entry> all = EmoteCatalog.instance().entries();
        for (int i = 0; i < 7; ++i) {
            this.slotIds[i] = i < all.size() ? all.get(i).id() : -1;
        }
    }

    private void migrateDefaults() {
        List<EmoteCatalog.Entry> all = EmoteCatalog.instance().entries();
        for (int i = 0; i < 7; ++i) {
            if (this.slotIds[i] != -1 || i >= all.size()) continue;
            this.slotIds[i] = all.get(i).id();
        }
        if (this.slotIds.length > 6 && this.slotIds[6] != LegacyEmotion.SIX_SEVEN.id()) {
            this.slotIds[6] = LegacyEmotion.SIX_SEVEN.id();
        }
        this.save();
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray arr = new JsonArray();
            for (int id : this.slotIds) {
                arr.add((Number)id);
            }
            root.add("slots", (JsonElement)arr);
            JsonObject element2 = root;
            Files.createDirectories(this.file.getParent(), new FileAttribute[0]);
            Files.writeString(this.file, (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)element2), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Emotions] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043d\u0430\u0431\u043e\u0440 \u044d\u043c\u043e\u0446\u0438\u0439: " + e.getMessage());
        }
    }
}

