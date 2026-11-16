/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1844
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 *  net.minecraft.class_9334
 */
package ru.decide.inventorypreset;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1844;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class InventoryPreset {
    public static final int INVENTORY_SLOTS = 36;
    public static final int ARMOR_SLOTS = 4;
    public static final int OFFHAND_SLOT = 40;
    public static final int SLOT_COUNT = 41;
    private final String name;
    private final long createdAt;
    private final List<Entry> slots;

    public InventoryPreset(String name, long createdAt, List<Entry> slots) {
        this.name = name;
        this.createdAt = createdAt;
        ArrayList<Entry> normalized = new ArrayList<Entry>(41);
        for (int i = 0; i < 41; ++i) {
            Entry entry = i < slots.size() ? slots.get(i) : Entry.EMPTY;
            normalized.add(entry == null ? Entry.EMPTY : entry);
        }
        this.slots = Collections.unmodifiableList(normalized);
    }

    public String name() {
        return this.name;
    }

    public long createdAt() {
        return this.createdAt;
    }

    public List<Entry> slots() {
        return this.slots;
    }

    public Entry slot(int logicalSlot) {
        return logicalSlot >= 0 && logicalSlot < this.slots.size() ? this.slots.get(logicalSlot) : Entry.EMPTY;
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(String itemId, int count, String displayName, String potionSignature, String potionData) {
        public static final Entry EMPTY = new Entry("", 0, "", "", "");
        public static final String LEGACY_NAME_PREFIX = "legacy-name:";

        public Entry {
            itemId = itemId == null ? "" : itemId;
            count = Math.max(0, count);
            displayName = displayName == null ? "" : displayName;
            potionSignature = potionSignature == null ? "" : potionSignature;
            potionData = potionData == null ? "" : potionData;
        }

        public static Entry fromStack(class_1799 stack) {
            if (stack == null || stack.method_7960()) {
                return EMPTY;
            }
            class_2960 id = class_7923.field_41178.method_10221((Object)stack.method_7909());
            class_1844 potionContents = (class_1844)stack.method_58694(class_9334.field_49651);
            Object signature = potionContents != null ? "potion:" + String.valueOf(potionContents) : (id.method_12832().endsWith("bundle") ? "bundle:" + String.valueOf(stack.method_57353()) : "");
            return new Entry(id.toString(), stack.method_7947(), stack.method_7964().getString(), (String)signature, Entry.encodePotion(potionContents));
        }

        private static String encodePotion(class_1844 contents) {
            if (contents == null) {
                return "";
            }
            return class_1844.field_49275.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)contents).result().map(JsonElement::toString).orElse("");
        }

        private static class_1844 decodePotion(String json) {
            if (json == null || json.isBlank()) {
                return null;
            }
            try {
                return class_1844.field_49275.parse((DynamicOps)JsonOps.INSTANCE, (Object)JsonParser.parseString((String)json)).result().orElse(null);
            }
            catch (Exception e) {
                return null;
            }
        }

        public boolean isEmpty() {
            return this.itemId.isBlank() || this.count <= 0;
        }

        public boolean matches(class_1799 stack) {
            if (this.isEmpty()) {
                return stack == null || stack.method_7960();
            }
            if (stack == null || stack.method_7960() || stack.method_7947() < this.count) {
                return false;
            }
            return this.sameItem(stack);
        }

        public boolean sameItem(class_1799 stack) {
            if (this.isEmpty() || stack == null || stack.method_7960()) {
                return false;
            }
            if (!class_7923.field_41178.method_10221((Object)stack.method_7909()).toString().equals(this.itemId)) {
                return false;
            }
            if (this.potionSignature.isBlank()) {
                return true;
            }
            if (this.potionSignature.startsWith(LEGACY_NAME_PREFIX)) {
                return this.potionSignature.substring(LEGACY_NAME_PREFIX.length()).equals(stack.method_7964().getString());
            }
            if (this.potionSignature.startsWith("bundle:")) {
                return this.potionSignature.equals("bundle:" + String.valueOf(stack.method_57353()));
            }
            Object potionContents = stack.method_58694(class_9334.field_49651);
            if (potionContents == null) {
                return false;
            }
            return this.potionSignature.equals(potionContents.toString()) || this.potionSignature.equals("potion:" + String.valueOf(potionContents));
        }

        public String matchKey() {
            return this.itemId + "\u0000" + this.potionSignature;
        }

        public static boolean canHavePotionContents(String itemId) {
            return itemId.equals("minecraft:potion") || itemId.equals("minecraft:splash_potion") || itemId.equals("minecraft:lingering_potion") || itemId.equals("minecraft:tipped_arrow");
        }

        public class_1799 toStack() {
            if (this.isEmpty()) {
                return class_1799.field_8037;
            }
            class_2960 id = class_2960.method_12829((String)this.itemId);
            if (id == null) {
                return class_1799.field_8037;
            }
            class_1799 stack = class_7923.field_41178.method_17966(id).map(item -> new class_1799((class_1935)item, this.count)).orElse(class_1799.field_8037);
            if (stack.method_7960()) {
                return stack;
            }
            class_1844 contents = Entry.decodePotion(this.potionData);
            if (contents != null) {
                stack.method_57379(class_9334.field_49651, (Object)contents);
            }
            if (!this.displayName.isBlank() && !this.displayName.equals(stack.method_7964().getString())) {
                stack.method_57379(class_9334.field_49631, (Object)class_2561.method_43470((String)this.displayName));
            }
            return stack;
        }
    }
}

