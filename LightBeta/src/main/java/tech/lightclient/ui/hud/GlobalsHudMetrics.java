package tech.lightclient.ui.hud;

import tech.lightclient.client.utils.render.drag.DraggingManager;

final class GlobalsHudMetrics {

    private static final String HUD_SCALE_KEY = "scale";
    private static final String HUD_ROUNDING_KEY = "rounding";

    private static final float DEFAULT_HUD_SCALE = 1.0f;
    private static final float MIN_HUD_SCALE = 0.5f;
    private static final float MAX_HUD_SCALE = 2.0f;

    private static final int ALPHA_DURATION_MS = 250;
    private static final int SCALE_DURATION_MS = 350;

    private GlobalsHudMetrics() {
    }

    static int getAlphaDurationMs() {
        return ALPHA_DURATION_MS;
    }

    static float getHudScale(String scope) {
        if (scope == null || scope.isBlank()) {
            return DEFAULT_HUD_SCALE;
        }
        float value = DraggingManager.getHudFloat(scope, HUD_SCALE_KEY, DEFAULT_HUD_SCALE);
        if (!Float.isFinite(value) || value <= 0f) {
            return DEFAULT_HUD_SCALE;
        }
        return Math.max(MIN_HUD_SCALE, Math.min(MAX_HUD_SCALE, value));
    }

    static float scalePos(String scope, float value) {
        float scale = getHudScale(scope);
        if (!Float.isFinite(scale) || scale <= 0f) {
            return value;
        }
        return value / scale;
    }

    static int getHudRoundedInt(String scope, float baseRounding) {
        if (!Float.isFinite(baseRounding) || baseRounding <= 0f) {
            return 0;
        }
        float roundingScale = getHudRoundingScale(scope);
        return Math.max(0, Math.round(baseRounding * roundingScale));
    }

    private static float getHudRoundingScale(String scope) {
        if (scope == null || scope.isBlank()) {
            return 1f;
        }
        float value = DraggingManager.getHudFloat(scope, HUD_ROUNDING_KEY, 1f);
        if (!Float.isFinite(value) || value <= 0f) {
            return 1f;
        }
        return value;
    }

    static float animatedScale(float startScale, float progress, boolean hiding) {
        float scaleProgress = progress;
        if (SCALE_DURATION_MS > 0f && ALPHA_DURATION_MS > 0f) {
            scaleProgress = Math.min(1f, progress * ((float) ALPHA_DURATION_MS / SCALE_DURATION_MS));
        }
        float t = Math.max(0f, Math.min(1f, scaleProgress));
        t = t * t * (3f - 2f * t);
        t = 1f - (float) Math.pow(1f - t, 2f);
        return startScale + (1f - startScale) * t;
    }
}
