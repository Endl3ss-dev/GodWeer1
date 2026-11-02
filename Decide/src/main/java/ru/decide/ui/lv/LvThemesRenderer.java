/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.lv;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.Theme;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.Scissor;

@Environment(value=EnvType.CLIENT)
public final class LvThemesRenderer {
    private static final float ROW_FADE_MS = 380.0f;
    private static final float SLIDE_PX = 6.0f;
    private static final float CARD_H = 35.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    private static final float CONTENT_HEIGHT = 280.0f;
    private final Map<Theme, Decelerate> selectAnims = new EnumMap<Theme, Decelerate>(Theme.class);
    private final Map<Theme, Float> hoverAnims = new EnumMap<Theme, Float>(Theme.class);
    private final Map<Integer, Decelerate> rowAppearAnims = new HashMap<Integer, Decelerate>();
    private int appearFadeMs = 320;
    private long appearBaseMs;
    private boolean appearInitialFrame;
    private boolean transitioning = false;
    private float fadeOutTime = 0.0f;
    private float scroll;
    private float scrollTarget;
    private float contentH;
    private boolean appearComposite;

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public void open() {
        this.open(false);
    }

    public void open(boolean bl) {
        this.rowAppearAnims.clear();
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.appearFadeMs = bl ? 320 : 380;
        this.appearBaseMs = System.currentTimeMillis();
        this.appearInitialFrame = true;
    }

    public void render(class_332 m, float x, float y, float panelW, float f14, float f15, float dt) {
        float f7 = 1.0f - (float)Math.exp(-dt * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * f7;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        this.renderCards(m, x, y, panelW, f14, f15, dt, true);
        this.appearInitialFrame = false;
    }

    public boolean click(float x, float y, float panelW, float mx, float my) {
        if (this.transitioning) {
            return false;
        }
        float f6 = x + 117.0f;
        float f7 = y + 5.0f;
        float f8 = panelW - 122.0f;
        float f9 = 3.0f;
        float f10 = 4.0f;
        float f11 = (f8 - f9 - f10 * 2.0f) * 0.5f;
        float f12 = f6 + f10;
        float f13 = f12 + f11 + f9;
        if (!(mx < f6 || mx > f6 + f8 || my < f7 || my > f7 + 280.0f)) {
            Theme[] themes = Theme.values();
            for (int i = 0; i < themes.length; ++i) {
                int n = i % 2;
                int n2 = i / 2;
                float fx = n == 0 ? f12 : f13;
                float fy = f7 + f10 + (float)n2 * (35.0f + f9) - this.scroll;
                if (!(mx >= fx) || !(mx <= fx + f11) || !(my >= fy) || !(my <= fy + 35.0f)) continue;
                ThemeManager.set(themes[i]);
                GuiSounds.theme(i, themes.length);
                return true;
            }
            return false;
        }
        return false;
    }

    private static Decelerate createAnim(int n) {
        Decelerate decelerate = new Decelerate().setMs(n).setValue(1.0);
        decelerate.setDirection(Direction.BACKWARDS);
        decelerate.counter.setTime(System.currentTimeMillis() - 10000L);
        return decelerate;
    }

    public void scroll(float amount, float h) {
        float f2 = Math.max(0.0f, this.contentH - h + 8.0f);
        this.scrollTarget = LvThemesRenderer.clamp(this.scrollTarget - amount * 18.0f, 0.0f, f2);
    }

    public void resetCardBlur() {
    }

    public void setAppearComposite(boolean b) {
        this.appearComposite = b;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadeOutTime = 0.0f;
    }

    public void resetScroll() {
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
    }

    private boolean hasAppearWork() {
        if (this.appearInitialFrame) {
            return true;
        }
        if (Math.abs(this.scrollTarget - this.scroll) > 0.05f) {
            return true;
        }
        for (Decelerate decelerate : this.rowAppearAnims.values()) {
            if (!(decelerate.getOutput().floatValue() < 0.999f)) continue;
            return true;
        }
        return false;
    }

    private void renderScrollBar(float fx, float fy, float fw, float fh, float f5, float alpha) {
        float f7 = 3.0f;
        float f8 = fx + fw + 0.25f;
        float f9 = fy + f7;
        float f10 = fh - f7 * 2.0f;
        float f11 = LvThemesRenderer.clamp(fh / Math.max(this.contentH, fh), 0.0f, 1.0f);
        float f12 = LvThemesRenderer.clamp(f10 * f11, 12.0f, f10);
        float f13 = Math.max(0.0f, f10 - f12);
        float f14 = LvThemesRenderer.clamp(this.scroll / Math.max(f5, 1.0f), 0.0f, 1.0f);
        float f15 = f9 + f13 * f14;
        Draw.rect(f8, f9, 1.25f, f10, ThemeManager.rgba(0xFFFFFF, 18.0f * alpha));
        AccentGradient.fillVertical(f8, f15, 1.25f, f12, 1.0f, 165.0f * alpha);
    }

    private void renderCards(class_332 m, float px, float py, float panelW, float alpha, float f15, float dt, boolean appear) {
        float f7 = px + 117.0f;
        float f8 = py + 5.0f;
        float f9 = panelW - 122.0f;
        float f10 = 3.0f;
        float f11 = 4.0f;
        float f12 = 4.0f;
        float f13 = (f9 - f10 - f11 * 2.0f) * 0.5f;
        float f14 = f7 + f11;
        float f15b = f14 + f13 + f10;
        float f16 = 280.0f;
        Scissor.enable(f7, f8, f9, f16, 2.0f);
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float f19 = (1.0f - alpha) * 8.0f;
        boolean hovered = mouseX >= f7 && mouseX <= f7 + f9 && mouseY >= f8 && mouseY <= f8 + f16;
        float f20 = 1.0f - (float)Math.exp(-dt * 16.0f);
        Theme[] themes = Theme.values();
        for (int n = 0; n < themes.length; ++n) {
            float f;
            float rowApp;
            Theme theme = themes[n];
            int col = n % 2;
            int row = n / 2;
            float fx = col == 0 ? f14 : f15b;
            float fy = f8 + f11 + (float)row * (35.0f + f10) + f19 - this.scroll;
            boolean hoverCard = hovered && mouseX >= fx && mouseX <= fx + f13 && mouseY >= fy && mouseY <= fy + 35.0f;
            float hov = this.hoverAnims.getOrDefault((Object)theme, Float.valueOf(0.0f)).floatValue();
            hov += ((hoverCard ? 1.0f : 0.0f) - hov) * f20;
            this.hoverAnims.put(theme, Float.valueOf(hov));
            if (fy + 35.0f < f8 - 5.0f || fy > f8 + f16 + 5.0f) continue;
            float f2 = rowApp = appear ? this.rowAppear(row) : 1.0f;
            if (!(rowApp >= 0.001f)) continue;
            float f23 = Math.min(1.0f, rowApp / 0.6f);
            float f24 = f23 * f23;
            float f25 = rowApp < 0.6f ? 0.0f : (rowApp - 0.6f) / 0.4f;
            float f26 = 1.0f - f25 * f25 * (3.0f - 2.0f * f25);
            fy += (1.0f - f23) * 6.0f;
            float f30 = alpha * (rowApp < 0.999f ? f24 : 1.0f);
            if (rowApp < 0.999f) {
                float f33 = 0.85f + 0.15f * f23;
                float cx = fx + f13 * 0.5f;
                float cy = fy + 17.5f;
                m.method_51448().pushMatrix();
                m.method_51448().translate(cx, cy);
                m.method_51448().scale(f33, f33);
                m.method_51448().translate(-cx, -cy);
            }
            Decelerate decelerate = this.selectAnims.computeIfAbsent(theme, t -> LvThemesRenderer.createAnim(220));
            decelerate.setDirection(theme == ThemeManager.current() ? Direction.FORWARDS : Direction.BACKWARDS);
            float f21 = decelerate.getOutput().floatValue();
            Draw.rect(fx, fy, f13, 35.0f, ThemeManager.rgba(0, (40.0f + 18.0f * hov) * f30), f12);
            if (hov > 0.01f) {
                Draw.rect(fx, fy, f13, 35.0f, ThemeManager.rgba(0xFFFFFF, 8.0f * hov * f30), f12);
            }
            float edge = (14.0f + 12.0f * hov) * (1.0f - f21) * f30;
            if (f > 0.5f) {
                Draw.outline(fx, fy, f13, 35.0f, 0.6f, ThemeManager.rgba(0xFFFFFF, edge), f12);
            }
            if (f21 > 0.01f) {
                int n5 = theme.gradientA();
                int n6 = theme.gradientB();
                int n7 = ThemeManager.mix(ThemeManager.rgba(n5, 255.0f), ThemeManager.rgba(n6, 255.0f), 0.5f) & 0xFFFFFF;
                Draw.gradientRect(fx + 2.0f, fy + 3.0f, 1.0f, 29.0f, new int[]{ThemeManager.rgba(n5, 185.0f * f21 * f30), ThemeManager.rgba(n5, 185.0f * f21 * f30), ThemeManager.rgba(n6, 185.0f * f21 * f30), ThemeManager.rgba(n6, 185.0f * f21 * f30)}, 6.0f);
                Draw.outline(fx, fy, f13, 35.0f, 0.7f, ThemeManager.rgba(n5, 110.0f * f21 * f30), f12);
            }
            float f34 = fx + 8.0f + f21 * 3.0f;
            FontsLV.MONTSERRAT_MEDIUM.draw(theme.displayName(), f34, fy + 6.0f, 7.0f, ThemeManager.rgba(0xFFFFFF, (200.0f + 35.0f * hov + 20.0f * f21) * f30));
            if (f21 > 0.01f) {
                float f35 = fx + f13 - 9.0f;
                Draw.rect(f35, fy + 8.5f, 3.0f, 3.0f, ThemeManager.rgba(theme.accentBrightRgb(), 220.0f * f21 * f30), 1.5f);
            }
            int[] palette = theme.palette().length >= 2 ? theme.palette() : theme.shades();
            float f36 = 9.0f;
            float f37 = 3.0f;
            float f38 = fy + 35.0f - f36 - 7.0f;
            int n8 = Math.min(palette.length, 5);
            for (int i = 0; i < n8; ++i) {
                float f39 = fx + 8.0f + (float)i * (f36 + f37);
                Draw.rect(f39, f38, f36, f36, ThemeManager.rgba(palette[i], 235.0f * f30), 3.0f);
            }
            if (!(rowApp < 0.999f)) continue;
            m.method_51448().popMatrix();
        }
        int var54 = (themes.length + 1) / 2;
        this.contentH = (float)var54 * (35.0f + f10) - f10;
        float f40 = Math.max(0.0f, this.contentH - f16 + f11 * 2.0f);
        this.scrollTarget = LvThemesRenderer.clamp(this.scrollTarget, 0.0f, f40);
        this.scroll = LvThemesRenderer.clamp(this.scroll, 0.0f, f40);
        Scissor.disable();
        this.renderScrollBar(f7, f8, f9, f16, f40, alpha);
    }

    private float rowAppear(int n) {
        Decelerate decelerate = this.rowAppearAnims.get(n);
        if (decelerate == null) {
            long l = this.appearInitialFrame ? this.appearBaseMs : System.currentTimeMillis();
            decelerate = new Decelerate().setMs(this.appearFadeMs).setValue(1.0);
            decelerate.counter.setTime(l);
            this.rowAppearAnims.put(n, decelerate);
        }
        float f = decelerate.getOutput().floatValue();
        return Math.max(0.0f, Math.min(1.0f, f));
    }
}

