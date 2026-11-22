/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.fonts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.render.font.Font;

@Environment(value=EnvType.CLIENT)
public class LvFont {
    private final Font delegate;

    public LvFont(Font delegate) {
        this.delegate = delegate;
    }

    public void draw(String text, float x, float y, float size, int color) {
        this.delegate.draw(text, x, y, size, color);
    }

    public float width(String text, float size) {
        return this.delegate.getWidth(text, size);
    }

    public float msdfWidth(String text, float size) {
        return this.delegate.getWidth(text, size);
    }

    public void msdf(String text, float x, float y, float size, int color) {
        this.delegate.draw(text, x, y, size, color);
    }

    public void msdfFade(String text, float x, float y, float size, int color, float clipStartX, float clipEndX, float fadeWidth, float fadeIn, float fadeOut) {
        this.delegate.drawFadingText(text, x, y, clipEndX - clipStartX, color, size);
    }

    public void drawCentered(String text, float x, float y, float size, int color) {
        this.delegate.drawCentered(text, x, y, size, color);
    }

    public void drawCenterGradient(String text, float x, float y, float size, int c1, int c2) {
        this.delegate.drawCenterGradient(text, x, y, size, c1, c2);
    }

    public void drawGRS(String text, double x, double y, int c1, int c2, float size) {
        this.delegate.drawGradientB(text, (float)x, (float)y, c1, c2, size);
    }

    public float getHeight(float size) {
        return this.delegate.getHeight(size);
    }

    public void drawFadingText(String text, float x, float y, float maxWidth, int color, float size) {
        this.delegate.drawFadingText(text, x, y, maxWidth, color, size);
    }

    public void drawFadingTextReverse(String text, float x, float y, float maxWidth, int color, float size) {
        this.delegate.drawFadingTextReverse(text, x, y, maxWidth, color, size);
    }

    public void drawWrappedText(String text, float x, float y, float maxWidth, int color, float size) {
        this.delegate.drawWrappedText(text, x, y, maxWidth, color, size);
    }

    public float getWrappedHeight(String text, float maxWidth, float size) {
        return this.delegate.getWrappedHeight(text, maxWidth, size);
    }

    public String getName() {
        return this.delegate.getName();
    }
}

