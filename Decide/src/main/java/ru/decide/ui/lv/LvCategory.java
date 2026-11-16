/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;

@Environment(value=EnvType.CLIENT)
public enum LvCategory {
    VISUALS("Visuals", 'p'),
    HUD("Display", 'j'),
    UTILITIES("Utilities", 'r'),
    EVENTS("Information", 'i'),
    THEMES("Themes", 'B');

    private final String displayName;
    private final char icon;

    private LvCategory(String displayName, char icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public char getIcon() {
        return this.icon;
    }

    public static LvCategory getCategoryForModule(Category c) {
        if (c == null) {
            return null;
        }
        return switch (c) {
            case Category.VISUALS -> VISUALS;
            case Category.HUD -> HUD;
            case Category.UTILITIES -> UTILITIES;
            default -> null;
        };
    }
}

