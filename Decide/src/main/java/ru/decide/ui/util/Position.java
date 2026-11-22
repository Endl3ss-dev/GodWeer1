/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package ru.decide.ui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import ru.decide.ui.util.Render2DCoordinateSpace;

@Environment(value=EnvType.CLIENT)
public final class Position {
    public static final float SCREEN_MARGIN = 5.0f;
    private float x;
    private float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return this.x;
    }

    public void set(float x, float y, float width, float height) {
        this.x = Position.clampX(x, width);
        this.y = Position.clampY(y, height);
    }

    public float y() {
        return this.y;
    }

    public static float clampX(float x, float width) {
        float max = Math.max(5.0f, Position.screenWidth() - width - 5.0f);
        return Math.max(5.0f, Math.min(max, x));
    }

    public static float clampY(float y, float height) {
        float max = Math.max(5.0f, Position.screenHeight() - height - 5.0f);
        return Math.max(5.0f, Math.min(max, y));
    }

    public void setRaw(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static float mouseX() {
        class_310 minecraftClient = class_310.method_1551();
        if (minecraftClient == null) {
            return 0.0f;
        }
        double d = minecraftClient.field_1729.method_68879(minecraftClient.method_22683());
        return (float)(d / (double)Render2DCoordinateSpace.guiIndependentScale());
    }

    public static float mouseY() {
        class_310 minecraftClient = class_310.method_1551();
        if (minecraftClient == null) {
            return 0.0f;
        }
        double d = minecraftClient.field_1729.method_68883(minecraftClient.method_22683());
        return (float)(d / (double)Render2DCoordinateSpace.guiIndependentScale());
    }

    public static float screenWidth() {
        class_310 minecraftClient = class_310.method_1551();
        return minecraftClient != null && minecraftClient.method_22683() != null ? (float)minecraftClient.method_22683().method_4480() / Render2DCoordinateSpace.designGuiScale() : 960.0f;
    }

    public static float screenHeight() {
        class_310 minecraftClient = class_310.method_1551();
        return minecraftClient != null && minecraftClient.method_22683() != null ? (float)minecraftClient.method_22683().method_4507() / Render2DCoordinateSpace.designGuiScale() : 540.0f;
    }
}

