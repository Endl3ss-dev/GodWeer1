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
 *  net.minecraft.class_12249
 *  net.minecraft.class_1799
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_9280
 *  net.minecraft.class_9334
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.itemskins;

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
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12249;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_9280;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.CosmeticType;
import ru.decide.cosmetics.itemskins.ItemSkin;

@Environment(value=EnvType.CLIENT)
public final class ItemSkinRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:ItemSkins");
    private static final String INDEX_RESOURCE = "/assets/decide/item_skins/index.json";
    private static final String INDEX_ROOT = "skins";
    private static final int COSMETIC_INDEX_BASE = 600000;
    private static final Map<String, ItemSkin> BY_KEY = new ConcurrentHashMap<String, ItemSkin>();
    private static final Map<Integer, ItemSkin> BY_INDEX = new HashMap<Integer, ItemSkin>();
    private static final Map<String, class_9280> COMPONENTS = new ConcurrentHashMap<String, class_9280>();
    private static final Map<String, class_1921> CRYSTAL_LAYERS = new ConcurrentHashMap<String, class_1921>();
    private static final List<ItemSkin> SKINS = ItemSkinRegistry.load();

    private ItemSkinRegistry() {
    }

    public static List<ItemSkin> all() {
        return SKINS;
    }

    public static ItemSkin byIndex(int index) {
        return BY_INDEX.get(index);
    }

    public static ItemSkin byKey(String key) {
        return key == null ? null : BY_KEY.get(key);
    }

    public static ItemSkin equipped(ItemSkin.Kind kind) {
        if (kind == null) {
            return null;
        }
        Integer index = CosmeticManager.getInstance().getSelectedByType().get(kind.type());
        return index == null ? null : BY_INDEX.get(index);
    }

    public static void registerCosmeticEntries() {
        int next = 600000;
        for (ItemSkin skin : SKINS) {
            int index = skin.getCosmeticIndex();
            if (index < 0) {
                index = next;
                skin.setCosmeticIndex(index);
            }
            next = Math.max(next, index + 1);
            BY_INDEX.put(index, skin);
            CosmeticManager.getInstance().addEntry(new CosmeticManager.CosmeticEntry(index, skin.getDisplayName(), ItemSkinRegistry.cosmeticType(skin.getKind()).getType(), false, skin.getIcon(), null));
        }
        LOGGER.info("Loaded item skins: {}", (Object)SKINS.size());
    }

    public static class_1799 getRenderStack(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return stack;
        }
        ItemSkin.Kind kind = ItemSkinRegistry.kindOf(stack);
        if (kind == null) {
            return stack;
        }
        ItemSkin skin = ItemSkinRegistry.equipped(kind);
        if (skin == null) {
            return stack;
        }
        class_9280 replacement = ItemSkinRegistry.componentFor(skin);
        class_9280 existing = (class_9280)stack.method_58694(class_9334.field_49637);
        if (existing != null && !existing.comp_3356().isEmpty() && replacement.method_65366(0).equals(existing.method_65366(0))) {
            return stack;
        }
        class_1799 copy = stack.method_7972();
        copy.method_57379(class_9334.field_49637, (Object)ItemSkinRegistry.mergeSelection(existing, replacement));
        return copy;
    }

    public static class_1921 getCrystalLayer() {
        ItemSkin skin = ItemSkinRegistry.equipped(ItemSkin.Kind.END_CRYSTAL);
        if (skin == null || skin.getEntityTexture() == null) {
            return null;
        }
        return CRYSTAL_LAYERS.computeIfAbsent(skin.getKey(), key -> class_12249.method_75994((class_2960)skin.getEntityTexture()));
    }

    private static CosmeticType cosmeticType(ItemSkin.Kind kind) {
        return switch (kind) {
            default -> throw new MatchException(null, null);
            case ItemSkin.Kind.BOW -> CosmeticType.BOWS;
            case ItemSkin.Kind.CROSSBOW -> CosmeticType.CROSSBOWS;
            case ItemSkin.Kind.END_CRYSTAL -> CosmeticType.END_CRYSTALS;
            case ItemSkin.Kind.GOLDEN_APPLE -> CosmeticType.GOLDEN_APPLES;
            case ItemSkin.Kind.ENCHANTED_GOLDEN_APPLE -> CosmeticType.ENCHANTED_GOLDEN_APPLES;
        };
    }

    private static ItemSkin.Kind kindOf(class_1799 stack) {
        for (ItemSkin.Kind kind : ItemSkin.Kind.values()) {
            if (!stack.method_31574(kind.item())) continue;
            return kind;
        }
        return null;
    }

    public static class_9280 componentForSkin(ItemSkin skin) {
        return ItemSkinRegistry.componentFor(skin);
    }

    private static class_9280 componentFor(ItemSkin skin) {
        return COMPONENTS.computeIfAbsent(skin.getKey(), key -> new class_9280(List.of(), List.of(), List.of(skin.renderKey()), List.of()));
    }

    private static class_9280 mergeSelection(class_9280 existing, class_9280 replacement) {
        if (existing == null) {
            return replacement;
        }
        String replacementKey = replacement.method_65366(0);
        ArrayList<String> newStrings = new ArrayList<String>(Math.max(1, existing.comp_3356().size()));
        newStrings.addAll(existing.comp_3356());
        if (newStrings.isEmpty()) {
            newStrings.add(replacementKey);
        } else {
            newStrings.set(0, replacementKey);
        }
        return new class_9280(existing.comp_3354(), existing.comp_3355(), List.copyOf(newStrings), existing.comp_3357());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<ItemSkin> load() {
        try (InputStream in = ItemSkinRegistry.class.getResourceAsStream(INDEX_RESOURCE);){
            if (in == null) {
                LOGGER.warn("Item skin index not found: {}", (Object)INDEX_RESOURCE);
                List<ItemSkin> list = List.of();
                return list;
            }
            JsonObject root = JsonParser.parseString((String)new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray array = root.getAsJsonArray(INDEX_ROOT);
            if (array == null) {
                List<ItemSkin> list = List.of();
                return list;
            }
            ArrayList<ItemSkin> skins = new ArrayList<ItemSkin>(array.size());
            for (JsonElement element2 : array) {
                ItemSkin skin = ItemSkinRegistry.readSkin(element2.getAsJsonObject());
                if (skin == null) continue;
                skins.add(skin);
                BY_KEY.put(skin.getKey(), skin);
            }
            List list = Collections.unmodifiableList(skins);
            return list;
        }
        catch (Exception e) {
            LOGGER.warn("Failed to read item skin index", (Throwable)e);
            return List.of();
        }
    }

    private static ItemSkin readSkin(JsonObject object) {
        class_2960 entityTexture;
        String kindName;
        String key = object.has("key") ? object.get("key").getAsString() : null;
        String string = kindName = object.has("kind") ? object.get("kind").getAsString() : null;
        if (key == null || key.isEmpty() || kindName == null) {
            return null;
        }
        ItemSkin.Kind kind = ItemSkin.Kind.byType(kindName);
        if (kind == null) {
            LOGGER.warn("Unknown item skin kind '{}' for {}", (Object)kindName, (Object)key);
            return null;
        }
        String name = object.has("name") ? object.get("name").getAsString() : key;
        int tint = object.has("tint") ? object.get("tint").getAsInt() : -1;
        int index = object.has("index") ? object.get("index").getAsInt() : -1;
        class_2960 icon = object.has("icon") ? class_2960.method_12829((String)object.get("icon").getAsString()) : null;
        class_2960 class_29602 = entityTexture = object.has("entityTexture") ? class_2960.method_12829((String)object.get("entityTexture").getAsString()) : null;
        if (icon == null) {
            LOGGER.warn("Item skin '{}' has invalid icon", (Object)key);
            return null;
        }
        ItemSkin skin = new ItemSkin(key, kind, name, tint, icon, entityTexture);
        if (index >= 0) {
            skin.setCosmeticIndex(index);
        }
        return skin;
    }
}

