package tech.lightclient.ui.hud.star;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.hud.HudShadowRenderer;

public abstract class HudElement implements IMinecraft {
    public static final float HEADER_HEIGHT = 18.0f;
    public static final float ROW_HEIGHT = 18.0f;

    private static final long SHIMMER_CYCLE_MS = 1200L;
    private static final float SHIMMER_BAND_RATIO = 0.8f;

    protected final Dragging dragging;
    private final String scope;
    private final String iconPath;

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    private boolean externalVisible = true;
    private boolean selfVisible = true;
    private float alpha;
    private float selecting;

    protected HudElement(Function module, String scope, String iconPath, float defaultX, float defaultY) {
        this.scope = scope;
        this.iconPath = iconPath;
        this.dragging = LightClient.createDrag(module, scope, defaultX, defaultY);
    }

    protected HudElement(Function module, Dragging dragging, String scope, String iconPath) {
        this.scope = scope;
        this.iconPath = iconPath;
        this.dragging = dragging;
    }

    public void setVisible(boolean value) {
        this.externalVisible = value;
    }

    public boolean externalVisibleRaw() {
        return externalVisible;
    }

    public void moveTo(float newX, float newY) {
        dragging.setX(newX);
        dragging.setY(newY);
        DraggingManager.saveNow();
    }

    public String settingName() {
        return scope;
    }

    protected void setSelfVisible(boolean value) {
        this.selfVisible = value;
    }

    public String scope() {
        return scope;
    }

    public String iconPath() {
        return iconPath;
    }

    public Dragging dragging() {
        return dragging;
    }

    public boolean isShowing() {
        return alpha > 0.02f;
    }

    public void setSelecting(float value) {
        this.selecting = value;
    }

    public void render(EventRender.Screen.Hud event) {
        // Keep geometry and dynamic content up to date even while the element
        // is fully transparent. Otherwise lists which become empty once can
        // never wake up again, because their update() method stops running.
        position(event);
        update(event);

        float target = externalVisible && selfVisible ? 1.0f : 0.0f;
        alpha += (target - alpha) * 0.18f;
        if (alpha < 0.02f) {
            alpha = target < alpha ? 0.0f : alpha;
            updateDraggingBounds();
            return;
        }
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }

        drawScaled(event);

        // Dragging stores final logical GUI-space dimensions. Use the same
        // minimum panel size as the renderer, otherwise an empty/list header
        // gets a smaller hitbox than the visible panel.
        updateDraggingBounds();
    }

    private void drawScaled(EventRender.Screen.Hud event) {
        float scaleFactor = Interface.getHudScale(scope);
        float guiScale = getGuiScale(event);
        Renderer2D r = event.getRenderer();
        r.pushScale(guiScale * scaleFactor);
        try {
            float w = width > 0 ? Math.max(width, 20.0f) : width;
            float h = height > 0 ? Math.max(height, 20.0f) : height;
            if (width > 0 && height > 0) {
                float scaleIn = 0.5f + alpha * 0.5f - 0.05f * selecting;
                r.pushScale(scaleIn, x + width / 2.0f, y + h / 2.0f);
                try {
                    draw(event, alpha);
                    if (selecting > 0.01f) {
                        r.rectOutline(x, y, w, h, rounding(),
                                StarUtil.white(0.55f * selecting * (0.6f + 0.4f * (float) Math.sin(System.currentTimeMillis() / 180.0))), 1.0f);
                        drawShimmer(r, x, y, w, h, rounding(), selecting * alpha);
                    }
                } finally {
                    r.popScale();
                }
            }
        } finally {
            r.popScale();
        }
    }

    private void updateDraggingBounds() {
        float scaleFactor = Interface.getHudScale(scope);
        dragging.setWidth(Math.max(20.0f, width) * scaleFactor);
        dragging.setHeight(Math.max(20.0f, height) * scaleFactor);
    }

    public float getHudWidth() {
        return Math.max(20.0f, width) * Interface.getHudScale(scope);
    }

    public float getHudHeight() {
        return Math.max(20.0f, height) * Interface.getHudScale(scope);
    }

    protected void position(EventRender.Screen.Hud event) {
        x = Interface.scalePos(scope, dragging.getX());
        y = Interface.scalePos(scope, dragging.getY());
    }

    /**
     * Star HUD geometry is authored in Minecraft's logical GUI coordinates,
     * while the shared renderer targets the framebuffer. Keep the conversion
     * here so every HUD element, including animations and textures, uses the
     * same scale.
     */
    public static float getGuiScale(EventRender.Screen event) {
        if (event == null || mc == null || mc.getWindow() == null) {
            return 1.0f;
        }

        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();
        if (scaledWidth <= 0 || scaledHeight <= 0 || event.getViewportWidth() <= 0 || event.getViewportHeight() <= 0) {
            return 1.0f;
        }

        float scaleX = (float) event.getViewportWidth() / scaledWidth;
        float scaleY = (float) event.getViewportHeight() / scaledHeight;
        float scale = (scaleX + scaleY) * 0.5f;
        return Float.isFinite(scale) && scale > 0.0f ? scale : 1.0f;
    }

    private static void drawShimmer(Renderer2D r, float x, float y, float w, float h, float rounding, float alpha) {
        if (alpha <= 0.01f || !(w > 0.0f) || !(h > 0.0f)) {
            return;
        }

        float cx = x + w * 0.5f;
        float cy = y + h * 0.5f;
        float diag = (float) Math.hypot(w, h);
        float cycleProgress = (System.currentTimeMillis() % SHIMMER_CYCLE_MS) / (float) SHIMMER_CYCLE_MS;

        r.pushRoundedClipRect(x, y, w, h, rounding, rounding, rounding, rounding);
        r.pushTranslation(cx, cy);
        r.pushRotation(-45.0f);
        try {
            float bandSpan = diag * 2.5f;
            float bandPos = -diag * 1.25f + cycleProgress * bandSpan;
            float bandWidth = Math.min(w, h) * SHIMMER_BAND_RATIO;
            int centerColor = (Math.round(110.0f * alpha) << 24) | 0xFFFFFF;
            int sideColor = (Math.round(45.0f * alpha) << 24) | 0xFFFFFF;
            r.rect(bandPos - bandWidth * 0.12f, -diag, bandWidth * 0.24f, diag * 2.0f, 0, centerColor);
            r.rect(bandPos - bandWidth * 0.5f, -diag, bandWidth * 0.38f, diag * 2.0f, 0, sideColor);
            r.rect(bandPos + bandWidth * 0.12f, -diag, bandWidth * 0.38f, diag * 2.0f, 0, sideColor);
        } finally {
            r.popRotation();
            r.popTransform();
            r.popClipRect();
        }
    }

    protected abstract void update(EventRender.Screen.Hud event);

    protected abstract void draw(EventRender.Screen.Hud event, float alpha);

    protected float rounding() {
        return 5.3f;
    }

    public boolean show() {
        return true;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        // x/y/width/height are the coordinates used by the renderer before
        // the element scale is pushed. Convert them back to final logical
        // GUI coordinates so the hitbox follows the pixels on screen. This
        // also covers center-positioned elements such as the dynamic island,
        // whose Dragging position is not the source of truth.
        float scaleFactor = Interface.getHudScale(scope);
        float left = x * scaleFactor;
        float top = y * scaleFactor;
        float hitWidth = hitboxWidth();
        float hitHeight = hitboxHeight();
        return mouseX >= left
                && mouseY >= top
                && mouseX <= left + hitWidth
                && mouseY <= top + hitHeight;
    }

    protected float hitboxWidth() {
        return Math.max(20.0f, width) * Interface.getHudScale(scope);
    }

    protected float hitboxHeight() {
        return Math.max(20.0f, height) * Interface.getHudScale(scope);
    }

    public void onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging.onClick(mouseX, mouseY, 0);
        }
    }

    public void onMouseReleased(double mouseX, double mouseY, int button) {
        dragging.onRelease(button);
    }

    public void fillSettingsPopup(PopupMenu popup) {
    }

    protected void updateDragFix(double mouseX, double mouseY, float maxWidth, float maxHeight) {
        dragging.onFix(mouseX, mouseY, maxWidth, maxHeight);
    }

    public static void drawClientRect(Renderer2D r, String scope, float x, float y, float w, float h, float radius, float alpha, boolean draggingNow) {
        drawClientRect(r, scope, x, y, w, h, radius, alpha, draggingNow, true);
    }

    /**
     * Базовая панель нового HUD: тень и жидкое стекло проекта, подложка,
     * тонкая обводка. Свечение кромок — опционально (выключено у компактных
     * квадратов под иконки, где оно выглядит как свечение самой иконки).
     */
    public static void drawClientRect(Renderer2D r, String scope, float x, float y, float w, float h,
                                      float radius, float alpha, boolean draggingNow, boolean accentGlow) {
        float ph = h;
        HudShadowRenderer.render(r, scope, x, y, w, ph, radius, alpha, 1f);
        Interface.renderHudBackground(r, x, y, w, ph, radius, alpha);
        if (!Interface.isHudBlurBackground()) {
            r.rect(x, y, w, ph, radius, ClientColors.applyAlpha(0xFF0C0C0F, 0.85f * alpha));
        }
        r.rectOutline(x, y, w, ph, radius, ClientColors.applyAlpha(0xFFFFFFFF, 0.12f * alpha), 1.0f);
        if (accentGlow) {
            drawAccentGlow(r, x, y, w, ph, radius, alpha);
        }
    }

    /**
     * Мягкое свечение фирменного цвета вдоль нижней кромки панели — фирменный
     * приём нового HUD. Каждый прямоугольник свечения обрезается по границам
     * самой панели, поэтому свечение не выходит за её рамки.
     */
    public static void drawAccentGlow(Renderer2D r, float x, float y, float w, float h, float radius, float alpha) {
        if (alpha <= 0.01f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        int glowColor = ClientColors.GRADIENT_START.getRGB();
        // Нижняя кромка — основное свечение.
        drawPanelBottomGlow(r, x, y, w, h, 0.0225f * alpha, glowColor);
        // Верхняя кромка — слабее.
        drawContainedGlow(r, x, y, w, h, x + w / 2.0f, y + 1.6f, 10.0f,
                Math.min(70.0f, Math.max(38.0f, w * 1.02f)), 0.015f * alpha, glowColor);
    }

    /** Нижнее свечение панели (используется в том числе хотбаром). */
    public static void drawPanelBottomGlow(Renderer2D r, float x, float y, float w, float h,
                                           float alpha, int glowColor) {
        drawContainedGlow(r, x, y, w, h, x + w / 2.0f, y + h - 2.2f, 12.0f,
                Math.min(78.0f, Math.max(38.0f, w * 1.05f)), alpha, glowColor);
    }

    /**
     * Стек скруглённых прямоугольников с затуханием. Прямоугольники заранее
     * пересекаются с границами панели, так что свечение остаётся строго
     * внутри неё независимо от состояния клипов рендера.
     */
    public static void drawContainedGlow(Renderer2D r, float px, float py, float pw, float ph,
                                         float centerX, float centerY,
                                         float minSize, float maxSize, float maxAlpha, int glowColor) {
        if (maxAlpha <= 0.0005f || pw <= 0.0f || ph <= 0.0f) {
            return;
        }
        int steps = 30;
        for (int i = 0; i < steps; i++) {
            float t = i / (float) (steps - 1);
            float s = minSize + (maxSize - minSize) * t;
            float centerMul = 0.4f + (0.6f * t);
            float a = maxAlpha * centerMul * (1.0f - t);

            float left = Math.max(centerX - s / 2.0f, px);
            float top = Math.max(centerY - s / 2.0f, py);
            float right = Math.min(centerX + s / 2.0f, px + pw);
            float bottom = Math.min(centerY + s / 2.0f, py + ph);
            float cw = right - left;
            float ch = bottom - top;
            if (cw <= 0.5f || ch <= 0.5f) {
                continue;
            }
            float round = Math.min(s / 2.0f, Math.min(cw, ch) / 2.0f);
            r.rect(left, top, cw, ch, round, ClientColors.applyAlpha(glowColor, a));
        }
    }

    /** Цветовой акцент клиента (для суффиксов, иконок и таймеров нового HUD). */
    protected static int accent(float alpha) {
        return ClientColors.applyAlpha(ClientColors.GRADIENT_START.getRGB(), alpha);
    }

    /**
     * Яркое радиальное свечение под иконкой — гладкая текстура свечения,
     * окрашенная цветом выбранной темы клиента. Рисуется строго до иконки,
     * чтобы иконка оставалась сверху.
     */
    public static void drawIconGlow(Renderer2D r, float cx, float cy, float radius, float alpha, int glowColor) {
        if (alpha <= 0.01f || radius <= 0.5f) {
            return;
        }
        float size = radius * 2.0f;
        r.drawTexture(StarUtil.id("images/etc/glow.png"),
                cx - radius, cy - radius, size, size,
                ClientColors.applyAlpha(glowColor, 0.55f * alpha));
    }

    /** Радиальное свечение под иконкой цветом темы клиента. */
    protected static void drawIconGlow(Renderer2D r, float cx, float cy, float radius, float alpha) {
        drawIconGlow(r, cx, cy, radius, alpha, ClientColors.GRADIENT_START.getRGB());
    }

    /** Цвет тонкой обводки панелей нового HUD. */
    protected static int outlineColor(float alpha) {
        return ClientColors.applyAlpha(0xFFFFFFFF, 0.12f * alpha);
    }

    protected void drawClientPanel(EventRender.Screen.Hud event, float panelHeight, float radius, float alpha) {
        drawClientRect(event.getRenderer(), scope, x, y, width, Math.max(20.0f, panelHeight), radius, alpha, dragging.isDragging());
    }

    protected static boolean boolSetting(String scope, String key, boolean def) {
        return DraggingManager.getHudBoolean(scope, key, def);
    }

    protected static void setBoolSetting(String scope, String key, boolean value) {
        DraggingManager.setHudBoolean(scope, key, value);
    }

    protected static float floatSetting(String scope, String key, float def) {
        return DraggingManager.getHudFloat(scope, key, def);
    }

    protected static float baselineMedium(float topY, float boxHeight, float size) {
        return StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', topY, boxHeight, size);
    }

    protected static void setFloatSetting(String scope, String key, float value) {
        DraggingManager.setHudFloat(scope, key, value);
    }
}
