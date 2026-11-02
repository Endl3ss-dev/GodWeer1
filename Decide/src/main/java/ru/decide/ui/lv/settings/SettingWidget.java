/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.lv.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;

@Environment(value=EnvType.CLIENT)
public abstract class SettingWidget {
    public abstract String name();

    public abstract float height();

    public void renderOverlay(class_332 drawContext, float f, float f2, float f3, float f4) {
    }

    public abstract void render(float var1, float var2, float var3, float var4);

    public void render(class_332 drawContext, float f, float f2, float f3, float f4) {
        this.render(f, f2, f3, f4);
    }

    public boolean isVisible() {
        return true;
    }

    public abstract boolean click(float var1, float var2, float var3, float var4, float var5);

    public void releaseDrag() {
    }

    public boolean middleClick(float f, float f2, float f3, float f4, float f5) {
        return false;
    }

    public void closeOverlay() {
    }

    public boolean isOverlayOpen() {
        return false;
    }

    public boolean scrollOverlay(float f, float f2, float f3, float f4, float f5, double d) {
        return false;
    }

    public boolean clickOverlay(float f, float f2, float f3, float f4, float f5) {
        return false;
    }

    public boolean hasOverlay() {
        return false;
    }

    public float preferredWidth() {
        return 120.0f;
    }

    public boolean mouseClicked(float f, float f2, float f3) {
        return false;
    }

    public void mouseReleased(float f) {
    }

    public boolean keyPressed(int n) {
        return false;
    }

    public boolean typeKey(int n) {
        return false;
    }

    public void typeChar(char c) {
    }

    public boolean isMouseOver(float f, float f2) {
        return false;
    }

    public float getHeight() {
        return this.height();
    }

    public String getTooltip() {
        return null;
    }
}

