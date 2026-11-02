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
import ru.decide.utils.math.MathUtil;

@Environment(value=EnvType.CLIENT)
public record Rect(float x, float y, float width, float height) {
    public static final Rect EMPTY = new Rect(0.0f, 0.0f, 0.0f, 0.0f);

    public boolean contains(float mouseX, float mouseY) {
        return MathUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    public float centerX() {
        return this.x + this.width / 2.0f;
    }

    public float centerY() {
        return this.y + this.height / 2.0f;
    }

    public float distanceSquaredToCenter(float mouseX, float mouseY) {
        float dx = mouseX - this.centerX();
        float dy = mouseY - this.centerY();
        return dx * dx + dy * dy;
    }
}

