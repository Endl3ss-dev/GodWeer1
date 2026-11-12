package tech.lightclient.client.utils.window;

import net.minecraft.client.texture.GlTexture;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Portals;

final class WorldWindowChrome {

    private static final float BACK_X = WorldWindowChromeLayout.BACK_X;
    private static final float FWD_X = WorldWindowChromeLayout.FWD_X;
    private static final float RELOAD_X = WorldWindowChromeLayout.RELOAD_X;
    private static final float ADDR_X = WorldWindowChromeLayout.ADDR_X;
    private static final float ADDR_R = WorldWindowChromeLayout.ADDR_R;
    private static final float PLUS_X = WorldWindowChromeLayout.PLUS_X;

    private WorldWindowChrome() {
    }

    static void draw(Renderer2D renderer, WorldWindow window, int pw, int ph) {
        float round = Math.min(pw, ph) * 0.03f;
        float tabH = (float) (ph * WorldWindow.TAB_FRAC);
        float toolH = (float) (ph * WorldWindow.TOOLBAR_FRAC);
        float barH = (float) (ph * WorldWindow.BAR_FRAC);
        float contentY = tabH + toolH;
        float contentH = ph - tabH - toolH - barH;

        renderer.blur(0, 0, pw, ph, round, 1f);
        renderer.rect(0, 0, pw, ph, round, ColorUtils.rgba(14, 16, 22, 150));

        if (window.browser != null) {
            try {
                var texture = window.browser.getTexture();
                if (texture instanceof GlTexture gl && gl.getGlId() > 0) {
                    renderer.pushClipRect(0, (int) contentY, pw, (int) contentH);
                    renderer.drawRgbaTexture(gl.getGlId(), 0, contentY, pw, contentH,
                            ColorUtils.rgba(255, 255, 255, 255), false);
                    renderer.popClipRect();
                }
            } catch (Throwable ignored) {
            }
        } else {
            String message = "Загрузка браузера…";
            float size = ph * 0.04f;
            float width = FontRegistry.SF_MEDIUM.getWidth(message, size);
            renderer.text(FontRegistry.SF_MEDIUM, (pw - width) / 2f, contentY + contentH / 2f, size, message,
                    ColorUtils.rgba(150, 158, 175, 255));
        }

        int frame = ColorUtils.rgba(18, 20, 28, 242);
        int surface = ColorUtils.rgba(33, 37, 50, 255);
        int inactive = ColorUtils.rgba(25, 28, 38, 255);
        renderer.rect(0, 0, pw, tabH, round, frame);
        int count = Math.max(1, window.tabs.size());
        float tabsAreaW = pw * PLUS_X;
        float tw = tabsAreaW / count;
        float trad = Math.min(tabH * 0.28f, tw * 0.12f);
        float tfs = tabH * 0.40f;
        for (int i = 0; i < window.tabs.size(); i++) {
            float tx = i * tw;
            float pad = 2f;
            boolean active = i == window.active;
            float top = active ? 2f : 4f;
            roundTop(renderer, tx + pad, top, tw - pad * 2f, tabH - top + 2f, trad, active ? surface : inactive);
            float fr = tabH * 0.11f;
            float fcx = tx + pad + tabH * 0.32f;
            float fcy = tabH * 0.5f + 1f;
            renderer.rect(fcx - fr, fcy - fr, fr * 2f, fr * 2f, fr,
                    ColorUtils.rgba(active ? 96 : 70, active ? 150 : 110, active ? 240 : 170, 255));
            String title = window.tabs.get(i).title;
            if (title == null || title.isBlank()) {
                title = "Вкладка";
            }
            float titleX = fcx + fr + 6f;
            float closeX = tx + tw - pad - tabH * 0.5f;
            title = clip(title, tfs, closeX - titleX - 4f);
            renderer.text(FontRegistry.SF_MEDIUM, titleX, tabH * 0.5f + tfs * 0.36f + 1f, tfs, title,
                    ColorUtils.rgba(active ? 232 : 168, active ? 236 : 176, active ? 245 : 194, 255));
            renderer.text(FontRegistry.SF_MEDIUM, closeX, tabH * 0.5f + tfs * 0.4f + 1f, tfs, "✕",
                    ColorUtils.rgba(190, 120, 120, active ? 220 : 150));
        }

        float plusCx = pw * (PLUS_X + (1 - PLUS_X) * 0.5f);
        float plusS = tabH * 0.28f;
        renderer.rect(plusCx - tabH * 0.35f, tabH * 0.5f - tabH * 0.35f, tabH * 0.7f, tabH * 0.7f, tabH * 0.2f,
                ColorUtils.rgba(40, 44, 58, 200));
        renderer.text(FontRegistry.SF_SEMIBOLD, plusCx - plusS * 0.34f, tabH * 0.5f + plusS * 0.55f, plusS * 1.7f, "+",
                ColorUtils.rgba(205, 214, 232, 255));

        float ty = tabH;
        float tcy = tabH + toolH * 0.5f;
        float ifs = toolH * 0.5f;
        renderer.rect(0, ty, pw, toolH, 0f, surface);
        int iconColor = ColorUtils.rgba(200, 210, 230, 255);
        drawBackIcon(renderer, pw * BACK_X, tcy, ifs * 0.34f, iconColor);
        drawForwardIcon(renderer, pw * FWD_X, tcy, ifs * 0.34f, iconColor);
        drawReloadIcon(renderer, pw * RELOAD_X, tcy, ifs * 0.30f, iconColor);

        float ax = pw * ADDR_X;
        float aw = pw * ADDR_R - ax;
        float ah = toolH * 0.7f;
        float aY = ty + (toolH - ah) / 2f;
        renderer.rect(ax, aY, aw, ah, ah * 0.35f, ColorUtils.rgba(12, 14, 20, 235));
        if (window.addressFocused) {
            renderer.rectOutline(ax, aY, aw, ah, ah * 0.35f, ColorUtils.rgba(90, 140, 255, 220), 1.5f);
        }
        String address = window.address == null ? "" : window.address;
        float afs = ah * 0.5f;
        String shown = clip(address, afs, aw - 24f);
        boolean empty = shown.isEmpty();
        renderer.text(FontRegistry.SF_MEDIUM, ax + 10f, tcy + afs * 0.36f, afs,
                empty && !window.addressFocused ? "адрес…" : shown,
                ColorUtils.rgba(empty ? 120 : 220, empty ? 128 : 226, empty ? 145 : 238, 255));
        if (window.addressFocused) {
            float cw = FontRegistry.SF_MEDIUM.getWidth(shown, afs);
            renderer.rect(ax + 10f + cw + 1f, tcy - afs * 0.5f, 1.6f, afs, 0f,
                    ColorUtils.rgba(150, 190, 255, 230));
        }

        float barY = ph - barH;
        renderer.rect(0, barY, pw, barH, round, ColorUtils.rgba(24, 27, 37, 235));
        renderer.rect(0, barY, pw, 2f, 0f, ColorUtils.rgba(90, 140, 255, 150));
        float by = barY + barH * 0.5f;
        float bs = barH * 0.34f;
        int label = ColorUtils.rgba(198, 208, 226, 255);
        renderer.rect(pw * 0.375f, barY + barH * 0.22f, 1.5f, barH * 0.56f, 0f,
                ColorUtils.rgba(255, 255, 255, 22));
        renderer.rect(pw * 0.825f, barY + barH * 0.22f, 1.5f, barH * 0.56f, 0f,
                ColorUtils.rgba(255, 255, 255, 22));
        drawDragIcon(renderer, pw * 0.04f, by, bs * 0.62f, label);
        renderer.text(FontRegistry.SF_MEDIUM, pw * 0.04f + bs * 0.85f, by + bs * 0.35f, bs, "перетащить", label);
        drawResizeIcon(renderer, pw * 0.44f, by, bs * 0.70f, label);
        renderer.text(FontRegistry.SF_MEDIUM, pw * 0.44f + bs * 0.90f, by + bs * 0.35f, bs, "размер", label);
        drawCloseIcon(renderer, pw * 0.90f, by, bs * 0.70f, ColorUtils.rgba(235, 120, 120, 255));

        if (Portals.keyboardTarget() == window && !window.addressFocused) {
            String hint = "● набор — ESC";
            float hs = tabH * 0.42f;
            float hw = FontRegistry.SF_MEDIUM.getWidth(hint, hs);
            renderer.text(FontRegistry.SF_MEDIUM, pw - hw - 10f, contentY + contentH - 8f, hs, hint,
                    ColorUtils.rgba(120, 210, 140, 230));
        }

        if (window.curX >= 0 && window.curY >= 0) {
            float cx = window.curX;
            float cy = contentY + window.curY;
            float cr = Math.max(3f, ph * 0.006f);
            renderer.rect(cx - cr, cy - cr, cr * 2f, cr * 2f, cr, ColorUtils.rgba(255, 90, 90, 235));
            renderer.rect(cx - cr * 0.4f, cy - cr * 0.4f, cr * 0.8f, cr * 0.8f, cr,
                    ColorUtils.rgba(255, 255, 255, 255));
        }

        renderer.rectOutline(0, 0, pw, ph, round, ColorUtils.rgba(255, 255, 255, 30), 2f);
    }

    private static void roundTop(Renderer2D renderer, float x, float y, float w, float h, float round, int color) {
        renderer.rect(x, y, w, h, round, color);
        renderer.rect(x, y + h - round, w, round, 0f, color);
    }

    private static String clip(String value, float size, float maxW) {
        if (value == null) {
            return "";
        }
        if (FontRegistry.SF_MEDIUM.getWidth(value, size) <= maxW) {
            return value;
        }
        String clipped = value;
        while (clipped.length() > 1 && FontRegistry.SF_MEDIUM.getWidth(clipped + "…", size) > maxW) {
            clipped = clipped.substring(0, clipped.length() - 1);
        }
        return clipped + "…";
    }

    private static void drawBackIcon(Renderer2D r, float x, float y, float s, int color) {
        r.rect(x + s * 0.25f, y - s * 0.07f, s * 0.62f, s * 0.14f, s * 0.07f, color);
        r.rect(x + s * 0.08f, y - s * 0.07f, s * 0.22f, s * 0.14f, s * 0.07f, color);
        r.rect(x + s * 0.08f, y - s * 0.07f, s * 0.14f, s * 0.14f, s * 0.07f, color);
    }

    private static void drawForwardIcon(Renderer2D r, float x, float y, float s, int color) {
        r.rect(x - s * 0.82f, y - s * 0.07f, s * 0.62f, s * 0.14f, s * 0.07f, color);
        r.rect(x + s * 0.62f, y - s * 0.07f, s * 0.22f, s * 0.14f, s * 0.07f, color);
        r.rect(x + s * 0.70f, y - s * 0.07f, s * 0.14f, s * 0.14f, s * 0.07f, color);
    }

    private static void drawReloadIcon(Renderer2D r, float x, float y, float s, int color) {
        r.circleOutline(x, y, s * 0.72f, -45f, 0.78f, color, Math.max(1f, s * 0.14f));
        r.rect(x + s * 0.32f, y - s * 0.72f, s * 0.38f, s * 0.20f, s * 0.05f, color);
    }

    private static void drawDragIcon(Renderer2D r, float x, float y, float s, int color) {
        float dot = Math.max(1.2f, s * 0.12f);
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                r.circle(x + col * s * 0.25f, y + row * s * 0.25f, dot, 0f, 1f, color);
            }
        }
    }

    private static void drawResizeIcon(Renderer2D r, float x, float y, float s, int color) {
        float t = Math.max(1f, s * 0.12f);
        r.rect(x - s * 0.40f, y + s * 0.28f, s * 0.80f, t, t * 0.5f, color);
        r.rect(x + s * 0.28f, y - s * 0.40f, t, s * 0.80f, t * 0.5f, color);
        r.rect(x + s * 0.22f, y + s * 0.22f, s * 0.20f, s * 0.20f, t * 0.5f, color);
    }

    private static void drawCloseIcon(Renderer2D r, float x, float y, float s, int color) {
        float t = Math.max(1.2f, s * 0.15f);
        r.rect(x - s * 0.42f, y - t * 0.5f, s * 0.84f, t, t * 0.5f, color);
        r.rect(x - t * 0.5f, y - s * 0.42f, t, s * 0.84f, t * 0.5f, color);
    }
}
