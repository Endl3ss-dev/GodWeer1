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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.melee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.CosmeticType;
import ru.decide.cosmetics.melee.MeleeHandTransform;
import ru.decide.cosmetics.melee.MeleePreloader;
import ru.decide.cosmetics.melee.MeleeSkin;
import ru.decide.cosmetics.melee.MeleeVariant;

@Environment(value=EnvType.CLIENT)
public final class MeleeRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:Melee");
    private static final String INDEX_RESOURCE = "/assets/decide/cosmetic/melee/index.json";
    private static final String INDEX_ROOT = "melee";
    private static final String COSMETIC_TYPE = "knife";
    private static final int COSMETIC_INDEX_BASE = 500000;
    private static final List<MeleeSkin> SKINS = MeleeRegistry.load();
    private static final Map<Integer, MeleeSkin> BY_INDEX = new HashMap<Integer, MeleeSkin>();

    private MeleeRegistry() {
    }

    public static List<MeleeSkin> all() {
        return SKINS;
    }

    public static MeleeSkin byKey(String key) {
        if (key == null) {
            return null;
        }
        for (MeleeSkin skin : SKINS) {
            if (!skin.getKey().equals(key)) continue;
            return skin;
        }
        return null;
    }

    public static MeleeSkin equipped() {
        Integer index = CosmeticManager.getInstance().getSelectedByType().get(COSMETIC_TYPE);
        return index == null ? null : BY_INDEX.get(index);
    }

    public static MeleeSkin byIndex(int index) {
        return BY_INDEX.get(index);
    }

    public static void preloadAll() {
        for (MeleeSkin skin : SKINS) {
            MeleePreloader.preload(skin);
        }
    }

    public static boolean isEquipped(MeleeSkin skin) {
        return skin != null && skin == MeleeRegistry.equipped();
    }

    public static void registerCosmeticEntries() {
        int index = 500000;
        for (MeleeSkin skin : SKINS) {
            skin.setCosmeticIndex(index);
            BY_INDEX.put(index, skin);
            CosmeticManager.getInstance().addEntry(new CosmeticManager.CosmeticEntry(index, skin.getDisplayName(), CosmeticType.KNIVES.getType(), false, skin.getIcon(), null));
            ++index;
        }
        LOGGER.info("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043e \u043d\u043e\u0436\u0435\u0439: {}", (Object)SKINS.size());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<MeleeSkin> load() {
        try (InputStream in = MeleeRegistry.class.getResourceAsStream(INDEX_RESOURCE);){
            if (in == null) {
                List<MeleeSkin> list = List.of();
                return list;
            }
            JsonObject root = JsonParser.parseString((String)new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray array = root.getAsJsonArray(INDEX_ROOT);
            if (array == null) {
                List<MeleeSkin> list = List.of();
                return list;
            }
            ArrayList<MeleeSkin> skins = new ArrayList<MeleeSkin>(array.size());
            for (JsonElement element2 : array) {
                MeleeSkin skin = MeleeRegistry.readSkin(element2.getAsJsonObject());
                if (skin == null) continue;
                skins.add(skin);
            }
            List list = Collections.unmodifiableList(skins);
            return list;
        }
        catch (Exception e) {
            LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c index.json \u043d\u043e\u0436\u0435\u0439", (Throwable)e);
            return List.of();
        }
    }

    private static MeleeSkin readSkin(JsonObject object) {
        String key;
        String string = key = object.has("key") ? object.get("key").getAsString() : null;
        if (key == null || key.isEmpty()) {
            return null;
        }
        try {
            String name = object.has("name") ? object.get("name").getAsString() : key;
            String model = object.has("model") ? object.get("model").getAsString() : key;
            JsonObject transforms = object.getAsJsonObject("transforms");
            return new MeleeSkin(key, model, name, MeleeRegistry.readVariants(object), MeleeRegistry.readStrings(object, "holyBones"), MeleeRegistry.readTransform(transforms, "thirdperson_righthand"), MeleeRegistry.readTransform(transforms, "thirdperson_lefthand"));
        }
        catch (Exception e) {
            LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0441\u043a\u0438\u043d \u043d\u043e\u0436\u0430 {}", (Object)key, (Object)e);
            return null;
        }
    }

    private static List<MeleeVariant> readVariants(JsonObject object) {
        JsonArray array = object.getAsJsonArray("variants");
        if (array == null || array.isEmpty()) {
            return List.of(MeleeVariant.DEFAULT);
        }
        ArrayList<MeleeVariant> variants = new ArrayList<MeleeVariant>(array.size());
        for (JsonElement element2 : array) {
            JsonObject variant = element2.getAsJsonObject();
            String suffix = variant.has("suffix") ? variant.get("suffix").getAsString() : "";
            ArrayList<String> hidden = new ArrayList<String>();
            JsonArray hiddenArray = variant.getAsJsonArray("hidden");
            if (hiddenArray != null) {
                for (JsonElement hiddenElement : hiddenArray) {
                    hidden.add(hiddenElement.getAsString());
                }
            }
            variants.add(new MeleeVariant(suffix, List.copyOf(hidden)));
        }
        return List.copyOf(variants);
    }

    private static List<String> readStrings(JsonObject object, String key) {
        JsonArray array = object.getAsJsonArray(key);
        if (array == null || array.isEmpty()) {
            return List.of();
        }
        ArrayList<String> values = new ArrayList<String>(array.size());
        for (JsonElement element2 : array) {
            values.add(element2.getAsString());
        }
        return List.copyOf(values);
    }

    private static MeleeHandTransform readTransform(JsonObject transforms, String key) {
        if (transforms == null || !transforms.has(key)) {
            return MeleeHandTransform.IDENTITY;
        }
        JsonObject object = transforms.getAsJsonObject(key);
        return new MeleeHandTransform(MeleeRegistry.readVector(object, "rotation", 0.0f), MeleeRegistry.readVector(object, "translation", 0.0f), MeleeRegistry.readVector(object, "scale", 1.0f));
    }

    private static float[] readVector(JsonObject object, String key, float fallback) {
        float[] result = new float[]{fallback, fallback, fallback};
        if (object == null || !object.has(key) || !object.get(key).isJsonArray()) {
            return result;
        }
        JsonArray array = object.getAsJsonArray(key);
        for (int i = 0; i < 3 && i < array.size(); ++i) {
            result[i] = array.get(i).getAsFloat();
        }
        return result;
    }
}

