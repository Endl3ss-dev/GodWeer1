/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.LvRectUtil;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class LvRenderHelper {
    private static final float REFERENCE_RADIUS = 7.0f;

    private LvRenderHelper() {
    }

    public static float effectiveCornerRadius(float f, float f2, float f3) {
        float f4 = Math.min(f2, f3) * 0.5f;
        return Math.min(f4, f * LvRenderHelper.radiusScale());
    }

    public static void drawPanelBg(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        int n = Math.max(0, Math.min(255, Math.round(40.0f * f9)));
        if (n > 0) {
            float f10 = LvRenderHelper.radiusScale();
            float f11 = Math.min(f3, f4) * 0.5f;
            float f12 = Math.min(f11, f5 * f10);
            float f13 = Math.min(f11, f6 * f10);
            float f14 = Math.min(f11, f7 * f10);
            float f15 = Math.min(f11, f8 * f10);
            Draw.rect(f, f2, f3, f4, LvRenderHelper.rgba(0, 0, 0, n), f12, f13, f14, f15);
        }
    }

    public static void drawPanelBg(float f, float f2, float f3, float f4, float f5, float f6) {
        LvRenderHelper.drawPanelBg(f, f2, f3, f4, f5, f5, f5, f5, f6);
    }

    public static float cornerEdgeInset(float f, float f2) {
        if (f <= f2) {
            return 0.0f;
        }
        float f3 = f - f2;
        return f - (float)Math.sqrt(Math.max(0.0f, f * f - f3 * f3));
    }

    public static void drawDropBackground(float f, float f2, float f3, float f4, float f5) {
        LvRectUtil.drawClientRectNoGlow(f, f2, f3, f4, 2.0f, f5);
    }

    private static float radiusScale() {
        return 1.0f;
    }

    public static void drawName(String string, float f, float f2, float f3, float f4) {
        int n = LvRenderHelper.rgba(255, 255, 255, (int)(200.0f * f4));
        LvRenderHelper.drawScrollingText(string, f + 6.0f, f2 + 5.0f, f3, 6.5f, n);
    }

    private static float easeInOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = f2 * 1.525f;
        if (f < 0.5f) {
            float f4 = 2.0f * f;
            return f4 * f4 * ((f3 + 1.0f) * f4 - f3) * 0.5f;
        }
        float f5 = 2.0f * f - 2.0f;
        return (f5 * f5 * ((f3 + 1.0f) * f5 + f3) + 2.0f) * 0.5f;
    }

    public static void drawScrollingText(String string, float f, float f2, float f3, float f4, int n) {
        float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, f4);
        float f6 = f5 - f3;
        if (f6 <= 1.0f) {
            FontsLV.MONTSERRAT_MEDIUM.draw(string, f, f2, f4, n);
        } else {
            float f7 = 11.0f;
            float f8 = 1.2f;
            float f9 = Math.max(0.35f, f6 / f7);
            double d = (double)(f8 + f9) * 2.0;
            double d2 = (double)System.nanoTime() / 1.0E9 % d;
            float f10 = d2 < (double)f8 ? 0.0f : (d2 < (double)(f8 + f9) ? f6 * LvRenderHelper.easeInOutBack((float)((d2 - (double)f8) / (double)f9)) : (d2 < (double)f8 * 2.0 + (double)f9 ? f6 : f6 * (1.0f - LvRenderHelper.easeInOutBack((float)((d2 - (double)f8 * 2.0 - (double)f9) / (double)f9)))));
            float f11 = Math.max(0.0f, Math.min(1.0f, f10 / 4.0f));
            float f12 = Math.max(0.0f, Math.min(1.0f, (f6 - f10) / 4.0f));
            FontsLV.MONTSERRAT_MEDIUM.msdfFade(string, f - f10, f2, f4, n, f, f + f3, 5.0f, f11, f12);
        }
    }

    public static void drawBtn(float f, float f2, float f3, float f4, String string, float f5) {
        LvRenderHelper.drawBtn(f, f2, f3, f4, string, f5, 0.0f);
    }

    public static void drawBtn(float f, float f2, float f3, float f4, String string, float f5, float f6) {
        LvRenderHelper.drawPanelBg(f, f2, f3, f4, 3.0f, f5);
        float f7 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f + (f3 - f7) * 0.5f + f6, f2 + (f4 - 7.0f) * 0.5f, 6.0f, ThemeManager.accentSoft(220.0f * f5));
    }

    private static int rgba(int n, int n2, int n3, int n4) {
        return n4 << 24 | n << 16 | n2 << 8 | n3;
    }
}

