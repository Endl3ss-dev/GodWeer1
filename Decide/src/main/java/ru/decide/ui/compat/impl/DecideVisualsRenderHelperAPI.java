/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.compat.impl;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.compat.RenderHelperAPI;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class DecideVisualsRenderHelperAPI
implements RenderHelperAPI {
    private static final float DEFAULT_RADIUS = 7.0f;

    @Override
    public void drawPanelBg(float x, float y, float w, float h, float tl, float tr, float br, float bl, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(40.0f * alpha)));
        if (a > 0) {
            Draw.rect(x, y, w, h, new Color(0, 0, 0, a).getRGB(), tl, tr, br, bl);
        }
    }

    @Override
    public void drawPanelBg(float x, float y, float w, float h, float alpha) {
        this.drawPanelBg(x, y, w, h, 7.0f, 7.0f, 7.0f, 7.0f, alpha);
    }

    @Override
    public void drawDropBackground(float x, float y, float w, float h, float alpha) {
        this.drawClientRect(x, y, w, h, 2.0f, alpha, 0.0f);
    }

    @Override
    public void drawClientRect(float x, float y, float w, float h, float radius, float opacity, float extraRadius) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, opacity));
        if (clampedAlpha <= 0.0f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        float r = Math.max(0.0f, Math.min(radius + extraRadius, Math.min(w, h) * 0.5f));
        float glowStrength = clampedAlpha * 0.5f;
        if (glowStrength > 0.004f) {
            int glowColor = ThemeManager.accent(Math.round(glowStrength * 255.0f));
            Draw.glow(x, y, w, h, glowColor, r, 10.0f, 0.55f, 0.8f);
        }
        Draw.blur(x, y, w, h, clampedAlpha, r, 0);
        float glassAlpha = clampedAlpha * 0.3f;
        int glassTint = ThemeManager.accent(Math.round(glassAlpha * 255.0f));
        Draw.glass(x, y, w, h, glassAlpha, r, r, r, r, glassTint);
        Draw.glassOutline(x, y, w, h, 0.65f, r, clampedAlpha * 0.35f, 0.2f);
    }

    @Override
    public void drawClientRectFixedRadius(float x, float y, float w, float h, float radius, float opacity, float extraRadius) {
        this.drawClientRect(x, y, w, h, radius, opacity, extraRadius);
    }

    @Override
    public float effectiveCornerRadius(float base, float extra, float maxRef) {
        float cap = Math.min(extra, maxRef) * 0.5f;
        return Math.min(cap, base);
    }

    @Override
    public float cornerEdgeInset(float radius, float thickness) {
        if (radius <= thickness) {
            return 0.0f;
        }
        float d = radius - thickness;
        return radius - (float)Math.sqrt(Math.max(0.0f, radius * radius - d * d));
    }
}

