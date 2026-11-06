/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.itemskins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2960;

@Environment(value=EnvType.CLIENT)
public final class ItemSkin {
    private final String key;
    private final Kind kind;
    private final String displayName;
    private final int tint;
    private final class_2960 icon;
    private final class_2960 entityTexture;
    private int cosmeticIndex = -1;

    public ItemSkin(String key, Kind kind, String displayName, int tint, class_2960 icon, class_2960 entityTexture) {
        this.key = key;
        this.kind = kind;
        this.displayName = displayName;
        this.tint = tint;
        this.icon = icon;
        this.entityTexture = entityTexture;
    }

    public String getKey() {
        return this.key;
    }

    public Kind getKind() {
        return this.kind;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getTint() {
        return this.tint;
    }

    public class_2960 getIcon() {
        return this.icon;
    }

    public class_2960 getEntityTexture() {
        return this.entityTexture;
    }

    public int getCosmeticIndex() {
        return this.cosmeticIndex;
    }

    void setCosmeticIndex(int cosmeticIndex) {
        this.cosmeticIndex = cosmeticIndex;
    }

    public String renderKey() {
        return "decide:item_skin:" + this.key;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Kind {
        BOW("bow", class_1802.field_8102),
        CROSSBOW("crossbow", class_1802.field_8399),
        END_CRYSTAL("end_crystal", class_1802.field_8301),
        GOLDEN_APPLE("golden_apple", class_1802.field_8463),
        ENCHANTED_GOLDEN_APPLE("enchanted_golden_apple", class_1802.field_8367);

        private final String type;
        private final class_1792 item;

        private Kind(String type, class_1792 item) {
            this.type = type;
            this.item = item;
        }

        public String type() {
            return this.type;
        }

        public class_1792 item() {
            return this.item;
        }

        public static Kind byType(String type) {
            for (Kind kind : Kind.values()) {
                if (!kind.type.equalsIgnoreCase(type)) continue;
                return kind;
            }
            return null;
        }
    }
}

