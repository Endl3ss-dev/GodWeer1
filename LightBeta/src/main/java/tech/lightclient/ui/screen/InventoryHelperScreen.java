package tech.lightclient.ui.screen;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.client.utils.config.InventoryHelperStorage;
import tech.lightclient.modules.impl.utils.InventoryHelper;

import java.util.List;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

/** Layout manager rendered with the same renderer and typography as ClickGui. */
public final class InventoryHelperScreen extends Screen {
    private static final float PANEL_W = 452f;
    private static final float PANEL_H = 258f;
    private static final float ROUNDING = 6f;
    private static final float ROW_H = 24f;

    private boolean subscribed;
    private boolean nameFocused;
    private String nameInput = "";
    private String selectedName;
    private String status = "Сохраните текущую раскладку или выберите готовую.";
    private int statusColor = 0xFF929AAF;
    private float panelX, panelY, scale = 1f;
    private final SimpleLinearAnimation openAnimation = new SimpleLinearAnimation(240, Easings.EASE_OUT_CUBIC);
    private final SimpleLinearAnimation contentAnimation = new SimpleLinearAnimation(360, Easings.EASE_OUT_CUBIC);
    private boolean closing;

    public InventoryHelperScreen() {
        super(Text.literal("InventoryHelper"));
        InventoryHelperStorage.Layout selected = InventoryHelperStorage.getSelectedLayout();
        selectedName = selected == null ? null : selected.getName();
        nameInput = selectedName == null ? "" : selectedName;
    }

    @Override
    protected void init() {
        if (!subscribed) {
            LightClient.getEventBus().subscribe(this);
            subscribed = true;
        }
        closing = false;
        // Screen instances are recreated by Minecraft, so explicitly reset both
        // animations to zero before starting them; otherwise show() is a no-op
        // after a previous visit and the transition is invisible.
        openAnimation.setImmediate(false);
        contentAnimation.setImmediate(false);
        openAnimation.show();
        contentAnimation.show();
    }

    @Override
    public void removed() {
        if (subscribed) {
            LightClient.getEventBus().unsubscribe(this);
            subscribed = false;
        }
        super.removed();
    }

    @EventHandler
    public void renderMenu(EventRender.Screen.Gui event) {
        if (mc.currentScreen != this) return;
        Renderer2D r = event.getRenderer();
        if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
            r.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
        }
    }

    @EventHandler
    public void renderMenuForeground(EventRender.Screen.OverGui event) {
        if (mc.currentScreen != this || mc.getWindow() == null) return;
        Renderer2D r = event.getRenderer();
        scale = (float) mc.getWindow().getScaleFactor();
        float viewportW = event.getViewportWidth();
        float viewportH = event.getViewportHeight();
        float progress = openAnimation.getProgress();
        if (closing && openAnimation.isFinished()) {
            mc.setScreen(null);
            return;
        }
        if (progress <= 0.001f) return;
        float w = PANEL_W * scale;
        float h = PANEL_H * scale;
        panelX = (viewportW - w) * 0.5f;
        panelY = (viewportH - h) * 0.5f;
        float round = ROUNDING * scale;
        float mx = rawMouseX();
        float my = rawMouseY();
        FontObject font = FontRegistry.DELTA_CLICKGUI;
        int accent = getAccentColor();

        float openScale = 0.92f + 0.08f * progress;
        r.pushScale(openScale, openScale, viewportW * 0.5f, viewportH * 0.5f);
        r.pushAlpha(progress);
        try {
        if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
            r.blur(panelX, panelY, w, h, round, 0.95f);
        }
        r.rect(panelX, panelY, w, h, round, ColorUtils.rgba(28, 28, 31, 199));
        r.rect(panelX + 9 * scale, panelY + 27 * scale, w - 18 * scale, scale, 0f, ColorUtils.rgba(255, 255, 255, 16));

        float contentProgress = contentAnimation.getProgress();
        r.pushAlpha(contentProgress);
        try {
        float contentOffset = (1f - contentProgress) * 10f * scale;
        text(r, font, panelX + 13 * scale, panelY + 16 * scale + contentOffset, 9f * scale, "InventoryHelper", 0xFFF4F5FA);
        text(r, font, panelX + 13 * scale, panelY + 31 * scale + 9f * scale, 6.6f * scale,
                "Помощник в раскладке инвентаря", 0xFF929AAF);

        float leftX = panelX + 13 * scale;
        float listY = panelY + 53 * scale + contentOffset;
        float listW = 278 * scale;
        float listH = 146 * scale;
        r.rect(leftX, listY, listW, listH, 5 * scale, ColorUtils.rgba(255, 255, 255, 8));
        text(r, font, leftX + 9 * scale, listY + 14 * scale, 6.6f * scale, "СОХРАНЁННЫЕ РАСКЛАДКИ", 0xFF8992A8);
        renderLayouts(r, font, leftX + 5 * scale, listY + 25 * scale, listW - 10 * scale, mx, my, accent);

        float sideX = leftX + listW + 10 * scale;
        float sideW = 138 * scale;
        text(r, font, sideX, listY + 10 * scale, 6.8f * scale, "НАЗВАНИЕ", 0xFF8992A8);
        float inputY = listY + 18 * scale;
        boolean inputHover = inside(mx, my, sideX, inputY, sideW, 23 * scale);
        r.rect(sideX, inputY, sideW, 23 * scale, 4 * scale, ColorUtils.rgba(255, 255, 255, nameFocused ? 24 : 13));
        if (nameFocused) {
            r.rect(sideX, inputY + 22 * scale, sideW, scale, scale * 0.5f, ColorUtils.injectAlpha(accent, 160));
        }
        text(r, font, sideX + 7 * scale, inputY + 14 * scale, 7f * scale,
                nameInput.isBlank() ? "Введите название" : nameInput, nameInput.isBlank() ? 0xFF757E91 : 0xFFE2E5ED);
        if (nameFocused && (System.currentTimeMillis() / 450L) % 2 == 0) {
            float cursor = font.getWidth(nameInput, 7f * scale);
            r.rect(sideX + 7 * scale + cursor + scale, inputY + 6 * scale, scale, 11 * scale, 0f, 0xFFEAF0FF);
        }

        text(r, font, sideX, inputY + 43 * scale, 6.4f * scale,
                "Сохранение запоминает все", 0xFF8C94A7);
        text(r, font, sideX, inputY + 52 * scale, 6.4f * scale,
                "36 слотов игрока.", 0xFF8C94A7);
        drawButton(r, font, sideX, inputY + 69 * scale, sideW, 22 * scale, "Сохранить", true, mx, my, accent);
        drawButton(r, font, sideX, inputY + 96 * scale, sideW, 22 * scale, "Применить", false, mx, my, accent);

        float footerY = panelY + h - 39 * scale;
        r.rect(panelX + 9 * scale, footerY - 9 * scale, w - 18 * scale, scale, 0f, ColorUtils.rgba(255, 255, 255, 16));
        text(r, font, leftX, footerY + 4 * scale, 6.5f * scale, status, statusColor);
        drawButton(r, font, panelX + w - 165 * scale, footerY - 3 * scale, 70 * scale, 21 * scale, "Удалить", false, mx, my, accent);
        drawButton(r, font, panelX + w - 87 * scale, footerY - 3 * scale, 74 * scale, 21 * scale, "Закрыть", false, mx, my, accent);
        } finally {
            r.popAlpha();
        }
        } finally {
            r.popAlpha();
            r.popScale();
        }
    }

    private void renderLayouts(Renderer2D r, FontObject font, float x, float y, float w, float mx, float my, int accent) {
        List<InventoryHelperStorage.Layout> layouts = InventoryHelperStorage.getLayouts();
        if (layouts.isEmpty()) {
            text(r, font, x + 7 * scale, y + 23 * scale, 7f * scale, "Нет сохранённых раскладок", 0xFF7E8799);
            return;
        }
        for (int i = 0; i < Math.min(5, layouts.size()); i++) {
            float rowProgress = Math.max(0f, Math.min(1f, (contentAnimation.getProgress() - 0.12f - i * 0.09f) / 0.55f));
            if (rowProgress <= 0.001f) continue;
            InventoryHelperStorage.Layout layout = layouts.get(i);
            float rowY = y + i * ROW_H * scale + (1f - rowProgress) * 8f * scale;
            boolean selected = layout.getName().equalsIgnoreCase(selectedName == null ? "" : selectedName);
            boolean hover = inside(mx, my, x, rowY, w, ROW_H * scale - 2 * scale);
            r.pushAlpha(rowProgress);
            try {
            r.rect(x, rowY, w, ROW_H * scale - 2 * scale, 4 * scale,
                    selected ? ColorUtils.injectAlpha(accent, 65) : ColorUtils.rgba(255, 255, 255, hover ? 18 : 7));
            text(r, font, x + 7 * scale, rowY + 14 * scale, 7.1f * scale, layout.getName(), selected ? 0xFFF6F8FF : 0xFFD1D6E0);
            String state = selected ? "ВЫБРАНО" : "ВЫБРАТЬ";
            float stateW = font.getWidth(state, 5.8f * scale);
            text(r, font, x + w - 8 * scale - stateW, rowY + 14 * scale, 5.8f * scale, state,
                    selected ? ColorUtils.injectAlpha(accent, 255) : 0xFF7E8799);
            } finally {
                r.popAlpha();
            }
        }
    }

    private void drawButton(Renderer2D r, FontObject font, float x, float y, float w, float h, String label,
                            boolean primary, float mx, float my, int accent) {
        boolean hover = inside(mx, my, x, y, w, h);
        int fill = primary ? ColorUtils.injectAlpha(accent, hover ? 210 : 170)
                : ColorUtils.rgba(255, 255, 255, hover ? 25 : 13);
        r.rect(x, y, w, h, 4 * scale, fill);
        float size = 6.7f * scale;
        float tw = font.getWidth(label, size);
        text(r, font, x + (w - tw) * 0.5f, y + h * 0.5f + FontRegistry.centeredBaselineOffset(font, 'H', size), size, label, 0xFFF2F4FA);
    }

    @Override
    public boolean mouseClicked(Click click, boolean repeated) {
        if (click.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return true;
        updatePanelLayout();
        float mx = (float) (click.x() * scale), my = (float) (click.y() * scale);
        float leftX = panelX + 13 * scale;
        float listY = panelY + 53 * scale;
        float listW = 278 * scale;
        float sideX = leftX + listW + 10 * scale;
        float sideW = 138 * scale;
        float inputY = listY + 18 * scale;
        nameFocused = inside(mx, my, sideX, inputY, sideW, 23 * scale);

        if (inside(mx, my, leftX + 5 * scale, listY + 25 * scale, listW - 10 * scale, 5 * ROW_H * scale)) {
            int row = (int) ((my - (listY + 25 * scale)) / (ROW_H * scale));
            List<InventoryHelperStorage.Layout> layouts = InventoryHelperStorage.getLayouts();
            if (row >= 0 && row < Math.min(5, layouts.size())) {
                selectedName = layouts.get(row).getName();
                nameInput = selectedName;
                InventoryHelper.selectLayout(selectedName);
                status("Выбрана раскладка: " + selectedName, getAccentColor());
                return true;
            }
        }
        if (inside(mx, my, sideX, inputY + 69 * scale, sideW, 22 * scale)) { save(); return true; }
        if (inside(mx, my, sideX, inputY + 96 * scale, sideW, 22 * scale)) { apply(); return true; }
        float footerY = panelY + PANEL_H * scale - 39 * scale;
        if (inside(mx, my, panelX + PANEL_W * scale - 165 * scale, footerY - 3 * scale, 70 * scale, 21 * scale)) { delete(); return true; }
        if (inside(mx, my, panelX + PANEL_W * scale - 87 * scale, footerY - 3 * scale, 74 * scale, 21 * scale)) { close(); return true; }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        if (nameFocused && key == GLFW.GLFW_KEY_BACKSPACE && !nameInput.isEmpty()) {
            nameInput = nameInput.substring(0, nameInput.offsetByCodePoints(nameInput.length(), -1));
            return true;
        }
        if (nameFocused && (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)) { save(); return true; }
        return true;
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (nameFocused && input.codepoint() >= 32 && nameInput.codePointCount(0, nameInput.length()) < 32) {
            nameInput += new String(Character.toChars(input.codepoint()));
        }
        return true;
    }

    @Override public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
    @Override protected void renderDarkening(DrawContext context) {}
    @Override protected void applyBlur(DrawContext context) {}
    @Override public boolean shouldPause() { return false; }
    @Override public void close() {
        if (closing) return;
        closing = true;
        openAnimation.hide();
        contentAnimation.hide();
    }

    private void save() {
        if (InventoryHelper.saveLayout(nameInput)) {
            selectedName = nameInput.trim();
            status("Раскладка сохранена: " + selectedName, 0xFF8BDBAE);
        } else status("Введите название до 32 символов.", 0xFFFF8D9B);
    }

    private void apply() {
        String name = selectedName == null ? nameInput.trim() : selectedName;
        if (name.isEmpty() || !InventoryHelper.selectLayout(name)) { status("Сначала выберите раскладку.", 0xFFFF8D9B); return; }
        if (InventoryHelper.sortSelectedLayout()) close();
        else status("Инвентарь сейчас недоступен.", 0xFFFF8D9B);
    }

    private void delete() {
        String name = selectedName == null ? nameInput.trim() : selectedName;
        if (name.isEmpty() || !InventoryHelperStorage.delete(name)) { status("Выберите раскладку для удаления.", 0xFFFF8D9B); return; }
        selectedName = null; nameInput = ""; status("Раскладка удалена.", 0xFFFFC181);
    }

    private void status(String text, int color) { status = text; statusColor = color; }
    private float rawMouseX() { return (float) (mc.mouse.getX() * mc.getWindow().getFramebufferWidth() / mc.getWindow().getWidth()); }
    private float rawMouseY() { return (float) (mc.mouse.getY() * mc.getWindow().getFramebufferHeight() / mc.getWindow().getHeight()); }
    private void updatePanelLayout() {
        scale = (float) mc.getWindow().getScaleFactor();
        float w = PANEL_W * scale;
        float h = PANEL_H * scale;
        panelX = (mc.getWindow().getFramebufferWidth() - w) * 0.5f;
        panelY = (mc.getWindow().getFramebufferHeight() - h) * 0.5f;
    }
    private static boolean inside(float mx, float my, float x, float y, float w, float h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
    private static void text(Renderer2D r, FontObject font, float x, float y, float size, String text, int color) { r.text(font, x, y, size, text, color); }
    private static int getAccentColor() { Integer color = tech.lightclient.modules.impl.render.ClickGui.getColorSetting().get(); return color == null ? 0xFF5690FF : color; }
}
