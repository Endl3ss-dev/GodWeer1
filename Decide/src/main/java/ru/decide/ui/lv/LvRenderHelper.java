/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.LvRectUtil;
import ru.decide.ui.theme.ThemeManager;

@Environment(value=EnvType.CLIENT)
public final class LvRenderHelper {
    private static final float REFERENCE_RADIUS = 7.0f;

    private LvRenderHelper() {
    }

    public static float effectiveCornerRadius(float radius, float w, float maxRef) {
        return LvRectUtil.effectiveCornerRadius(radius, w, maxRef);
    }

    public static float cornerEdgeInset(float radius, float thickness) {
        return LvRectUtil.cornerEdgeInset(radius, thickness);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawPanelBg(x, y, w, h, alpha);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float radius, float alpha) {
        LvRectUtil.drawPanelBg(x, y, w, h, radius, alpha);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float tl, float tr, float br, float bl, float alpha) {
        LvRectUtil.drawPanelBg(x, y, w, h, tl, tr, br, bl, alpha);
    }

    public static void drawDropBackground(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawDropBackground(x, y, w, h, alpha);
    }

    public static void drawName(String text, float x, float y, float maxW, float alpha) {
        int color = new Color(255, 255, 255, (int)(200.0f * alpha)).getRGB();
        LvRenderHelper.drawScrollingText(text, x + 6.0f, y + 5.0f, maxW, 6.5f, color);
    }

    public static void drawScrollingText(String text, float x, float y, float maxW, float size, int color) {
        float tw = FontsLV.MONTSERRAT_MEDIUM.width(text, size);
        float overflow = tw - maxW;
        if (overflow <= 1.0f) {
            FontsLV.MONTSERRAT_MEDIUM.draw(text, x, y, size, color);
        } else {
            float pauseDur = 1.2f;
            float scrollDur = Math.max(0.35f, overflow / 11.0f);
            double cycle = (double)(pauseDur + scrollDur) * 2.0;
            double t = (double)System.nanoTime() / 1.0E9 % cycle;
            float offset = t < (double)pauseDur ? 0.0f : (t < (double)(pauseDur + scrollDur) ? overflow * LvRenderHelper.easeInOutBack((float)((t - (double)pauseDur) / (double)scrollDur)) : (t < (double)pauseDur * 2.0 + (double)scrollDur ? overflow : overflow * (1.0f - LvRenderHelper.easeInOutBack((float)((t - (double)pauseDur * 2.0 - (double)scrollDur) / (double)scrollDur)))));
            float fadeIn = Math.max(0.0f, Math.min(1.0f, offset / 4.0f));
            float fadeOut = Math.max(0.0f, Math.min(1.0f, (overflow - offset) / 4.0f));
            FontsLV.MONTSERRAT_MEDIUM.msdfFade(text, x - offset, y, size, color, x, x + maxW, 5.0f, fadeIn, fadeOut);
        }
    }

    public static void drawBtn(float x, float y, float w, float h, String text, float alpha) {
        LvRenderHelper.drawBtn(x, y, w, h, text, alpha, 0.0f);
    }

    public static void drawBtn(float x, float y, float w, float h, String text, float alpha, float xOff) {
        LvRenderHelper.drawPanelBg(x, y, w, h, 3.0f, alpha);
        float tw = FontsLV.MONTSERRAT_MEDIUM.width(text, 6.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(text, x + (w - tw) * 0.5f + xOff, y + (h - 7.0f) * 0.5f, 6.0f, ThemeManager.accentSoft(220.0f * alpha));
    }

    private static float easeInOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 * 1.525f;
        if (t < 0.5f) {
            float f = 2.0f * t;
            return f * f * ((c3 + 1.0f) * f - c3) * 0.5f;
        }
        float f = 2.0f * t - 2.0f;
        return (f * f * ((c3 + 1.0f) * f + c3) + 2.0f) * 0.5f;
    }
}

