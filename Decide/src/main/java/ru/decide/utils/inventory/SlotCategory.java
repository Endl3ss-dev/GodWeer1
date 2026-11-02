/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 */
package ru.decide.utils.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import ru.decide.utils.inventory.SlotItem;

@Environment(value=EnvType.CLIENT)
public enum SlotCategory {
    POTION(SlotItem.Group.POTION, 0xFF3232);

    private final SlotItem.Group itemGroup;
    private final int defaultColor;

    private SlotCategory(SlotItem.Group itemGroup, int defaultColor) {
        this.itemGroup = itemGroup;
        this.defaultColor = defaultColor;
    }

    public SlotItem.Group getItemGroup() {
        return this.itemGroup;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public static SlotCategory classify(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return null;
        }
        if (SlotItem.match(stack, SlotItem.Group.POTION) != null) {
            return POTION;
        }
        return null;
    }
}

