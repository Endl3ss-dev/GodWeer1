/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.screen.editor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface OverlayEditor {
    public boolean isActive();

    public void render(float var1, float var2, float var3, float var4, float var5);

    public boolean mouseClicked(float var1, float var2, int var3);

    public boolean mouseReleased(int var1);

    public boolean mouseScrolled(float var1, float var2, double var3);

    public void saveAndExit();

    public void closeFromMenuRemoval();

    default public void rawMouseButton(float mouseX, float mouseY, int button, boolean pressed) {
    }

    default public void rawMouseMoved(float mouseX, float mouseY) {
    }

    default public boolean mouseDragged(float deltaX, float deltaY) {
        return this.isActive();
    }
}

