package tech.lightclient.ui.hud;

import tech.lightclient.client.utils.render.main.core.Renderer2D;

public final class HudShadowRenderer {

    // Едва заметная тень: низкая непрозрачность, мягкое размытие без чёткой
    // границы и без расширения (spread = 0), чтобы тень оставалась под самим
    // элементом и не перекрывала соседние элементы HUD.
    private static final float SHADOW_ALPHA = 0.275f;
    private static final float BLUR_STRENGTH = 3.5f;
    private static final float SPREAD = 0.0f;

    private HudShadowRenderer() {
    }

    public static void render(Renderer2D renderer, String scope, float x, float y, float w, float h,
                              float rounding, float alpha, float scaleFactor) {
        render(renderer, scope, x, y, w, h, rounding, rounding, rounding, rounding, alpha, scaleFactor);
    }

    public static void render(Renderer2D renderer, String scope, float x, float y, float w, float h,
                              float roundTL, float roundTR, float roundBR, float roundBL,
                              float alpha, float scaleFactor) {
        if (renderer == null || w <= 0f || h <= 0f || alpha <= 0f) {
            return;
        }

        float clampedAlpha = Math.max(0f, Math.min(1f, alpha));
        int shadowAlpha = Math.round(clampedAlpha * SHADOW_ALPHA * 255f);
        if (shadowAlpha <= 0) {
            return;
        }

        // Как в NameTags "Новый": фиксированное размытие без учёта масштаба HUD,
        // чтобы тень везде выглядела одинаково мягкой.
        int color = (shadowAlpha & 0xFF) << 24; // чёрная тень, RGB = 0
        renderer.shadow(x, y, w, h, roundTL, roundTR, roundBR, roundBL, BLUR_STRENGTH, SPREAD, color);
    }
}
