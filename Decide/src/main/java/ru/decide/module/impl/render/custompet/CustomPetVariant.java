/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render.custompet;

import java.util.ArrayList;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum CustomPetVariant {
    DEFAULT("\u041e\u0431\u044b\u0447\u043d\u0430\u044f"),
    NITWIT("\u041d\u0438\u0442\u0432\u0438\u0442"),
    GARDENER("\u0424\u0435\u0440\u043c\u0435\u0440"),
    FISHERMAN("\u0420\u044b\u0431\u0430\u043a"),
    MERCHANT("\u041f\u0443\u0442\u0435\u0448\u0435\u0441\u0442\u0432\u0435\u043d\u043d\u0438\u043a"),
    SORCERER("\u0412\u0435\u0434\u044c\u043c\u0430"),
    ROBOT("\u0420\u043e\u0431\u043e\u0442");

    private final String settingValue;

    private CustomPetVariant(String settingValue) {
        this.settingValue = settingValue;
    }

    public boolean isRobot() {
        return this == ROBOT;
    }

    public static String[] settingValues() {
        ArrayList<String> values = new ArrayList<String>();
        for (CustomPetVariant variant : CustomPetVariant.values()) {
            if (variant == DEFAULT || variant == ROBOT) continue;
            values.add(variant.settingValue);
        }
        return values.toArray(new String[0]);
    }

    public String getSettingValue() {
        return this.settingValue;
    }

    public static CustomPetVariant fromSettingValue(String value) {
        if (value != null && !value.isBlank()) {
            for (CustomPetVariant variant : CustomPetVariant.values()) {
                if (!variant.settingValue.equalsIgnoreCase(value)) continue;
                return variant;
            }
            return switch (value.trim().toLowerCase(Locale.ROOT)) {
                case "default", "\u043e\u0431\u044b\u0447\u043d\u0430\u044f" -> NITWIT;
                case "nitwit" -> NITWIT;
                case "gardener", "farmer", "\u0441\u0430\u0434\u043e\u0432\u043d\u0438\u043a" -> GARDENER;
                case "fisherman" -> FISHERMAN;
                case "merchant", "traveler", "traveller", "\u0442\u043e\u0440\u0433\u043e\u0432\u0435\u0446" -> MERCHANT;
                case "sorcerer", "witch", "\u043a\u043e\u043b\u0434\u0443\u043d" -> SORCERER;
                default -> NITWIT;
            };
        }
        return NITWIT;
    }

    public static CustomPetVariant fromSerializedName(String name) {
        if (name != null && !name.isBlank()) {
            try {
                CustomPetVariant variant = CustomPetVariant.valueOf(name.toUpperCase());
                return variant == DEFAULT ? NITWIT : variant;
            }
            catch (IllegalArgumentException ignored) {
                return CustomPetVariant.fromSettingValue(name);
            }
        }
        return NITWIT;
    }

    public boolean usesMerchantBody() {
        return this == MERCHANT;
    }

    public boolean usesMerchantLeaf() {
        return this == MERCHANT;
    }

    public boolean usesSorcererHat() {
        return this == SORCERER;
    }

    public boolean usesFishermanGear() {
        return this == FISHERMAN;
    }

    public boolean usesGardenerGear() {
        return this == GARDENER;
    }
}

