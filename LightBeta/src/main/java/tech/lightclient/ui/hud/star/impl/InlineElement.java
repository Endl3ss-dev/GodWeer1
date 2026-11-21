package tech.lightclient.ui.hud.star.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.ChatScreen;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.StarUtil;

/**
 * Строчный элемент (мир / FPS) в стиле нового HUD: компактный бокс с акцентной
 * иконкой слева и отдельная «пилла» со значениями, разделёнными точками.
 * Значения — цветом текста, суффиксы — акцентом клиента.
 */
public abstract class InlineElement extends HudElement {
    private static final float BOX = 18.0f;
    private static final float GAP_X = 3.0f;
    private static final float PAD_X = 7.0f;
    /** Значения в пилле разделяет только блок с точкой — минимум пространства. */
    private static final float DOT_SIZE = 2.0f;
    private static final float DOT_GAP = 2.5f;
    /** Сдвиг значения под иконку копирования при наведении. */
    private static final float COPY_SHIFT = 4.0f;
    private static final float TEXT_SIZE = 8.0f;
    private static final float SUFFIX_SIZE = 7.0f;
    private static final int DOT_COLOR = 0xFF6E6E6E;

    protected final List<InlineValue> values = new ArrayList<>();

    protected InlineElement(Function module, String scope, String iconPath, float defaultX, float defaultY) {
        super(module, scope, iconPath, defaultX, defaultY);
        height = BOX;
    }

    protected InlineElement(Function module, Dragging dragging, String scope, String iconPath) {
        super(module, dragging, scope, iconPath);
        height = BOX;
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        updateValues();
        width = mainWidth();
        height = BOX;
        setSelfVisible(showInline());
    }

    protected boolean isSelected(InlineValue value) {
        return boolSetting(scope(), "val_" + value.id(), true);
    }

    protected abstract void updateValues();

    protected boolean showInline() {
        return mc.player != null || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public boolean show() {
        return showInline();
    }

    private List<InlineValue> selectedValues() {
        List<InlineValue> selected = new ArrayList<>();
        for (InlineValue value : values) {
            if (isSelected(value)) {
                selected.add(value);
            }
        }
        return selected;
    }

    private float valueWidth(InlineValue value) {
        float shift = COPY_SHIFT * value.copyAnim().getValue();
        float suffixWidth = value.suffix().isEmpty() ? 0.0f : FontRegistry.DELTA_HUD.getWidth(value.suffix(), SUFFIX_SIZE);
        return FontRegistry.DELTA_HUD.getWidth(value.text(), TEXT_SIZE) + shift + suffixWidth;
    }

    private float contentWidth(List<InlineValue> selected) {
        float content = 0.0f;
        int index = 0;
        for (InlineValue value : selected) {
            if (index > 0) {
                content += dotBlock();
            }
            content += valueWidth(value);
            index++;
        }
        return content;
    }

    private static float dotBlock() {
        return DOT_GAP * 2.0f + DOT_SIZE;
    }

    private float mainWidth() {
        List<InlineValue> selected = selectedValues();
        float content = contentWidth(selected);
        if (content <= 0.0f) {
            return BOX;
        }
        return BOX + GAP_X + PAD_X + content + PAD_X;
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        Renderer2D r = event.getRenderer();
        float rounding = rounding();

        // Компактный бокс с иконкой слева — без свечения кромок, чтобы
        // подсветка панели не выглядела свечением самой иконки.
        drawClientRect(r, scope(), x, y, BOX, BOX, rounding, alpha, dragging.isDragging(), false);
        r.pushRoundedClipRect((int) x, (int) y, (int) Math.ceil(BOX), (int) Math.ceil(BOX),
                rounding, rounding, rounding, rounding);
        try {
            float iconSize = 8.0f;
            float iconX = x + (BOX - iconSize) / 2.0f;
            float iconY = y + (BOX - iconSize) / 2.0f;

            // Ореол иконки повторяет стиль шапок биндов и эффектов,
            // но ограничен размерами чёрного бокса.
            drawIconGlow(r, iconX + iconSize / 2.0f, iconY + iconSize / 2.0f,
                    Math.min(iconSize * 0.85f, BOX / 2.0f - 0.5f), alpha);
            r.drawTexture(StarUtil.id(iconPath()), iconX, iconY, iconSize, iconSize, accent(alpha));
        } finally {
            r.popClipRect();
        }

        // Основная «пилла» со значениями.
        float mainX = x + BOX + GAP_X;
        float mainW = width - BOX - GAP_X;
        if (mainW < 20.0f) {
            return;
        }
        drawClientRect(r, scope(), mainX, y, mainW, BOX, rounding, alpha, dragging.isDragging());

        List<InlineValue> selected = selectedValues();
        r.pushRoundedClipRect((int) mainX, (int) y, (int) Math.ceil(mainW), (int) Math.ceil(BOX),
                rounding, rounding, rounding, rounding);
        try {
            float xOffset = 0.0f;
            int index = 0;
            for (InlineValue value : selected) {
                if (index > 0) {
                    drawDot(r, mainX + PAD_X + xOffset + (dotBlock() - DOT_SIZE) / 2.0f, y, alpha);
                    xOffset += dotBlock();
                }
                float shift = COPY_SHIFT * value.copyAnim().getValue();
                float textX = mainX + PAD_X + xOffset + shift;
                r.text(FontRegistry.DELTA_HUD, textX,
                        StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y + 1.0f, BOX - 2.0f, TEXT_SIZE),
                        TEXT_SIZE, value.text(), StarUtil.text(alpha));
                if (!value.suffix().isEmpty()) {
                    float mainWidth = FontRegistry.DELTA_HUD.getWidth(value.text(), TEXT_SIZE) + shift;
                    r.text(FontRegistry.DELTA_HUD, textX + mainWidth,
                            StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y + 1.0f, BOX - 2.0f, SUFFIX_SIZE),
                            SUFFIX_SIZE, value.suffix(), accent(alpha));
                }
                xOffset += valueWidth(value);
                index++;
            }

            // Второй проход: анимации копирования по наведению.
            xOffset = 0.0f;
            int dotIndex = 0;
            for (InlineValue value : selected) {
                if (dotIndex > 0) {
                    xOffset += dotBlock();
                }
                float hoverShift = COPY_SHIFT * value.copyAnim().getValue();
                float valueX = mainX + PAD_X + xOffset + hoverShift;
                boolean hoveredNow = mc.currentScreen instanceof ChatScreen
                        && mouseX() >= valueX && mouseX() <= valueX + FontRegistry.DELTA_HUD.getWidth(value.text(), TEXT_SIZE)
                        && mouseY() >= y + 4.0f && mouseY() <= y + height - 4.0f
                        && !value.copy().isEmpty();
                if (!hoveredNow || value.copyTimer().finished(1000L)) {
                    value.setCopied(false);
                }
                value.copyAnim().update(hoveredNow);
                value.successAnim().update(value.isCopied());

                r.drawTexture(StarUtil.id("icons/hud/copy.png"),
                        valueX, y + 5.0f, 6.0f, 6.0f,
                        StarUtil.text(alpha * value.copyAnim().getValue()));
                r.drawTexture(StarUtil.id("icons/hud/check.png"),
                        valueX, y + 5.0f, 6.0f, 6.0f,
                        ClientColors.applyAlpha(0xFF34D26B, alpha * value.copyAnim().getValue() * value.successAnim().getValue()));

                xOffset += valueWidth(value);
                dotIndex++;
            }
        } finally {
            r.popClipRect();
        }
    }

    private void drawDot(Renderer2D r, float dotX, float py, float alpha) {
        r.rect(dotX, py + BOX / 2.0f - DOT_SIZE / 2.0f, DOT_SIZE, DOT_SIZE, DOT_SIZE / 2.0f,
                ClientColors.applyAlpha(DOT_COLOR, 0.78f * alpha));
    }

    private static double mouseX() {
        return mc.mouse.getScaledX(mc.getWindow());
    }

    private static double mouseY() {
        return mc.mouse.getScaledY(mc.getWindow());
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        float mainX = x + BOX + GAP_X + PAD_X;
        float xOffset = 0.0f;
        List<InlineValue> selected = selectedValues();
        int index = 0;
        for (InlineValue value : selected) {
            if (index > 0) {
                xOffset += DOT_GAP * 2.0f + DOT_SIZE;
            }
            float textWidth = FontRegistry.DELTA_HUD.getWidth(value.text(), TEXT_SIZE);
            boolean hover = !value.copy().isEmpty()
                    && mouseX >= mainX + xOffset && mouseX <= mainX + xOffset + textWidth
                    && mouseY >= y + 4.0f && mouseY <= y + height - 4.0f;
            if (hover && button == 0) {
                try {
                    mc.keyboard.setClipboard(value.copy());
                } catch (Throwable ignored) {
                }
                value.copyTimer().reset();
                value.setCopied(true);
                return;
            }
            xOffset += valueWidth(value);
            index++;
        }
        super.onMouseClicked(mouseX, mouseY, button);
    }

    protected boolean chatOpen() {
        return mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        for (InlineValue value : values) {
            popup.checkbox(value.label(), () -> isSelected(value),
                    selected -> setBoolSetting(scope(), "val_" + value.id(), selected));
        }
    }
}
