/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 */
package ru.decide.ui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class Render2DCoordinateSpace {
    private static final float DESIGN_GUI_SCALE = 2.0f;

    private Render2DCoordinateSpace() {
    }

    public static int guiScale() {
        class_310 minecraftClient = class_310.method_1551();
        if (minecraftClient == null) {
            return 1;
        }
        class_1041 window = minecraftClient.method_22683();
        return window == null ? 1 : Math.max(1, window.method_4495());
    }

    public static float guiIndependentScale() {
        return 2.0f / (float)Render2DCoordinateSpace.guiScale();
    }

    public static float designGuiScale() {
        return 2.0f;
    }

    public static float toGui(float f) {
        return f * Render2DCoordinateSpace.guiIndependentScale();
    }

    public static int toGuiInt(float f) {
        return Math.round(Render2DCoordinateSpace.toGui(f));
    }
}

