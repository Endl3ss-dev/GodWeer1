package tech.lightclient.ui.gui.elements;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.config.Config;
import tech.lightclient.client.utils.config.ConfigStorage;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsConfigWidget {

    private static final float WIDTH = 720f;
    private static final float HEIGHT = 600f;
    private static final float PADDING = 18f;
    private static final float HEADER_HEIGHT = 82f;
    private static final float CARD_H = 56f;
    private static final float CARD_GAP = 8f;
    private static final float CARD_ROUNDING = 7f;
    private static final int COLUMNS = 4;
    private static final float BOTTOM_BAR_H = 126f;
    private static final float INPUT_H = 44f;
    private static final float BTN_H = 38f;
    private static final float BTN_GAP = 8f;
    private static final float ROUNDING = 16f;

    private float scroll = 0f;
    private float targetScroll = 0f;
    private long lastAnimTime = System.currentTimeMillis();

    private final TextInputField nameInput = new TextInputField()
            .setFontSize(18f)
            .setPadding(14f, 0f)
            .setPlaceholder("Название конфига")
            .setMaxLength(40);

    private final List<ConfigCard> cards = new ArrayList<>();
    private final Map<String, Float> hoverAnims = new HashMap<>();
    private float saveHover = 0f;
    private float openHover = 0f;
    private long savePulseMs = 0L;

    private float lastX;
    private float lastY;
    private float lastScreenW;
    private float lastScreenH;
    private float listTopY;
    private float listBottomY;
    private float saveX, saveY, saveW;
    private float openX, openY, openW;
    private float inputX, inputY, inputW;
    private float entranceProgress = 1f;

    private static int withAlpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0x00FFFFFF);
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }

    public void setEntranceProgress(float progress) {
        entranceProgress = Math.max(0f, Math.min(1f, progress));
    }

    public void draw(Renderer2D render, float x, float y, float alpha, int mouseX, int mouseY, float screenW, float screenH) {
        lastX = x;
        lastY = y;
        lastScreenW = screenW;
        lastScreenH = screenH;

        float dt = updateAnimTime();
        float alphaFactor = Math.max(0f, Math.min(1f, alpha / 255f));

        int textBright = ColorUtils.rgba(228, 228, 234, (int) alpha);
        int textDim = ColorUtils.rgba(161, 161, 170, (int) alpha);
        int textFaint = ColorUtils.rgba(113, 113, 122, (int) alpha);
        int cardBg = ColorUtils.rgba(9, 9, 12, (int) (110 * alphaFactor));
        int borderCol = ColorUtils.rgba(39, 39, 42, (int) (190 * alphaFactor));
        int accent = withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor));
        float cardsProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, entranceProgress / 0.45f)));
        // Configs order: cards first, then the category title, then controls.
        float titleProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, (entranceProgress - 0.45f) / 0.25f)));
        float controlsProgress = Easings.EASE_IN_OUT_CUBIC.ease(Math.max(0f, Math.min(1f, (entranceProgress - 0.70f) / 0.30f)));

        if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null && titleProgress > 0.001f) {
            float titleSize = 22f;
            float titleX = x + PADDING;
            float titleY = y + 34f;
            render.pushAlpha(titleProgress);
            render.pushTranslation(0f, -24f * (1f - titleProgress));
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, titleX, titleY, titleSize, "Configs", textBright);
            render.popTransform();
            render.popAlpha();
        }

        rebuildCards();

        float contentTop = y + HEADER_HEIGHT + PADDING;
        float bottomBarTop = y + getHeight() - BOTTOM_BAR_H;
        listTopY = contentTop;
        listBottomY = bottomBarTop - PADDING;

        float cardW = (WIDTH - PADDING * 2f - CARD_GAP * (COLUMNS - 1)) / COLUMNS;
        float listH = listBottomY - listTopY;

        float totalContentH = cards.isEmpty() ? 0f : ((cards.size() + COLUMNS - 1) / COLUMNS) * (CARD_H + CARD_GAP) - CARD_GAP;
        float maxScroll = Math.max(0, totalContentH - listH);
        targetScroll = Math.max(0, Math.min(targetScroll, maxScroll));
        float lerpFactor = ClickGui_isMotionBlur() ? 0.10f : 0.18f;
        scroll += (targetScroll - scroll) * lerpFactor;
        if (Math.abs(targetScroll - scroll) < 0.1f) scroll = targetScroll;

        render.pushClipRect((int) x, (int) listTopY, (int) WIDTH, (int) listH);
        render.pushTranslation(0f, -90f * (1f - cardsProgress));

        if (cards.isEmpty()) {
            if (FontRegistry.DELTA_CLICKGUI != null) {
                float emptyY = listTopY + listH * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', 15f);
                render.text(FontRegistry.DELTA_CLICKGUI, x + (WIDTH - FontRegistry.DELTA_CLICKGUI.getWidth("Нет сохранённых конфигов", 15f)) * 0.5f,
                        emptyY, 15f, "Нет сохранённых конфигов", textFaint);
            }
        }

        for (int i = 0; i < cards.size(); i++) {
            ConfigCard card = cards.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            float cardX = x + PADDING + col * (cardW + CARD_GAP);
            float cardY = listTopY + row * (CARD_H + CARD_GAP) - scroll;

            if (cardY + CARD_H < listTopY - 5f || cardY > listBottomY + 5f) continue;

            boolean hovered = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + CARD_H;
            if (hovered) CursorHelper.setHand();

            float hovP = approach(getHover(card.name), hovered ? 1f : 0f, 12f, dt);
            hoverAnims.put(card.name, hovP);

            if (tech.lightclient.modules.impl.render.ClickGui.isSimpleBlurEnabled()) {
                render.prepareBlur(tech.lightclient.modules.impl.render.ClickGui.getSimpleBlurStrength());
                render.blur(cardX, cardY, cardW, CARD_H, CARD_ROUNDING, alphaFactor);
            }
            render.rect(cardX, cardY, cardW, CARD_H, CARD_ROUNDING, cardBg);
            render.rectOutline(cardX, cardY, cardW, CARD_H, CARD_ROUNDING, borderCol, 1f);
            if (hovP > 0.01f) {
                int hoverOverlay = ColorUtils.rgba(255, 255, 255, (int) (12 * alphaFactor * hovP));
                render.rect(cardX, cardY, cardW, CARD_H, CARD_ROUNDING, hoverOverlay);
            }

            if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null) {
                float nameSize = 14f;
                float nameLineH = FontRegistry.DELTA_CLICKGUI_MEDIUM.getLineHeight(nameSize);
                float metaLineH = FontRegistry.DELTA_CLICKGUI.getLineHeight(10f);
                float textBlockH = nameLineH + 4f + metaLineH * 2f;
                float textBlockTop = cardY + (CARD_H - textBlockH) * 0.5f;
                float nameY = textBlockTop + nameLineH * 0.5f
                        + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', nameSize);
                render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, cardX + (cardW - FontRegistry.DELTA_CLICKGUI_MEDIUM.getWidth(card.name, nameSize)) * 0.5f, nameY, nameSize, card.name, textBright);
                if (FontRegistry.DELTA_CLICKGUI != null) {
                    float metaSize = 10f;
                    float metaY = textBlockTop + nameLineH + 4f + metaLineH * 0.5f
                            + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', metaSize);
                    render.text(FontRegistry.DELTA_CLICKGUI, cardX + (cardW - FontRegistry.DELTA_CLICKGUI.getWidth("ЛКМ — загрузить", metaSize)) * 0.5f, metaY, metaSize, "ЛКМ — загрузить", textFaint);
                    render.text(FontRegistry.DELTA_CLICKGUI, cardX + (cardW - FontRegistry.DELTA_CLICKGUI.getWidth("ПКМ — удалить", metaSize)) * 0.5f, metaY + metaLineH, metaSize, "ПКМ — удалить", textFaint);
                }
            } else if (FontRegistry.DELTA_CLICKGUI != null) {
                float metaSize = 12f;
                float metaLineH = FontRegistry.DELTA_CLICKGUI.getLineHeight(metaSize);
                float metaY = cardY + (CARD_H - metaLineH * 2f) * 0.5f + metaLineH * 0.5f
                        + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI, 'H', metaSize);
                render.text(FontRegistry.DELTA_CLICKGUI, cardX + (cardW - FontRegistry.DELTA_CLICKGUI.getWidth("ЛКМ — загрузить", metaSize)) * 0.5f, metaY, metaSize, "ЛКМ — загрузить", textFaint);
                render.text(FontRegistry.DELTA_CLICKGUI, cardX + (cardW - FontRegistry.DELTA_CLICKGUI.getWidth("ПКМ — удалить", metaSize)) * 0.5f, metaY + metaLineH, metaSize, "ПКМ — удалить", textFaint);
            }
        }

        render.popTransform();
        render.popClipRect();

        render.pushClipRect((int) x, (int) bottomBarTop, (int) WIDTH, (int) BOTTOM_BAR_H);
        render.pushAlpha(controlsProgress);
        render.pushTranslation(0f, -40f * (1f - controlsProgress));
        render.rect(x, bottomBarTop, WIDTH, 1f, borderCol);

        float centerY = bottomBarTop + BOTTOM_BAR_H * 0.5f;
        float inputY = centerY - INPUT_H * 0.5f;
        float btnY = inputY - BTN_H - 4f;

        inputW = 440f;
        inputX = x + (WIDTH - inputW) * 0.5f;
        this.inputY = inputY;

        float btnW = (inputW - BTN_GAP) * 0.5f;
        openX = inputX;
        saveX = inputX + btnW + BTN_GAP;
        openW = btnW;
        saveW = btnW;
        this.saveY = btnY;
        this.openY = btnY;

        boolean openHovered = mouseX >= openX && mouseX <= openX + openW && mouseY >= openY && mouseY <= openY + BTN_H;
        if (openHovered) CursorHelper.setHand();
        openHover = approach(openHover, openHovered ? 1f : 0f, 12f, dt);

        int openBg = ColorUtils.interpolate(cardBg,
                ColorUtils.rgba(255, 255, 255, (int) (24 * alphaFactor)), openHover);
        int openBorder = ColorUtils.interpolate(borderCol,
                ColorUtils.rgba(228, 228, 234, (int) (180 * alphaFactor)), openHover);
        render.rect(openX, openY, openW, BTN_H, 8f, openBg);
        render.rectOutline(openX, openY, openW, BTN_H, 8f, openBorder, 1f);
        if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null) {
            String openLabel = "Открыть папку";
            float openSize = 16f;
            float openLabelY = openY + BTN_H * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', openSize);
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, openX + (openW - FontRegistry.DELTA_CLICKGUI_MEDIUM.getWidth(openLabel, openSize)) * 0.5f,
                    openLabelY, openSize, openLabel, textBright);
        }

        boolean saveHovered = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + BTN_H;
        if (saveHovered) CursorHelper.setHand();
        saveHover = approach(saveHover, saveHovered ? 1f : 0f, 12f, dt);

        float savePulse = 0f;
        long sincePulse = System.currentTimeMillis() - savePulseMs;
        if (savePulseMs > 0 && sincePulse < 400) {
            savePulse = 1f - sincePulse / 400f;
        } else {
            savePulseMs = 0;
        }

        int saveBase = withAlpha(ClientColors.ICON.getRGB(), (int) (180 * alphaFactor));
        int saveHighlight = ColorUtils.interpolate(
                withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor)),
                ColorUtils.rgba(255, 255, 255, (int) (255 * alphaFactor)),
                0.22f);
        int saveBg = ColorUtils.interpolate(saveBase, saveHighlight,
                saveHover * 0.5f + savePulse * 0.5f);
        int saveBorder = withAlpha(ClientColors.ICON.getRGB(), (int) (255 * alphaFactor));
        render.rect(saveX, saveY, saveW, BTN_H, 8f, saveBg);
        render.rectOutline(saveX, saveY, saveW, BTN_H, 8f, saveBorder, 1f);
        if (FontRegistry.DELTA_CLICKGUI_MEDIUM != null) {
            String label = "Сохранить конфиг";
            float labelSize = 16f;
            float labelY = saveY + BTN_H * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_CLICKGUI_MEDIUM, 'H', labelSize);
            render.text(FontRegistry.DELTA_CLICKGUI_MEDIUM, saveX + (saveW - FontRegistry.DELTA_CLICKGUI_MEDIUM.getWidth(label, labelSize)) * 0.5f,
                    labelY, labelSize, label, ColorUtils.rgba(255, 255, 255, (int) alpha));
        }

        int inputBorder = nameInput.isFocused() ? accent : borderCol;
        render.rect(inputX, inputY, inputW, INPUT_H, 8f, cardBg);
        render.rectOutline(inputX, inputY, inputW, INPUT_H, 8f, inputBorder, 1f);
        nameInput.render(render, inputX, inputY, inputW, INPUT_H, alphaFactor);
        render.popTransform();
        render.popAlpha();
        render.popClipRect();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, float x, float y, float screenW, float screenH) {
        if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + BTN_H) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                String name = nameInput.getText().trim();
                if (!name.isEmpty()) {
                    ConfigStorage.saveConfiguration(name);
                    savePulseMs = System.currentTimeMillis();
                    rebuildCards();
                }
            }
            return true;
        }

        if (mouseX >= openX && mouseX <= openX + openW && mouseY >= openY && mouseY <= openY + BTN_H) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                ConfigStorage storage = LightClient.getConfigStorage();
                if (storage != null && storage.CONFIG_DIR.exists()) {
                    try {
                        Util.getOperatingSystem().open(storage.CONFIG_DIR);
                    } catch (Throwable ignored) {}
                }
            }
            return true;
        }

        if (mouseX >= inputX && mouseX <= inputX + inputW && mouseY >= inputY && mouseY <= inputY + INPUT_H) {
            nameInput.mouseClicked(mouseX, mouseY, button, inputX, inputY, inputW, INPUT_H);
            return true;
        } else if (nameInput.isFocused() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            nameInput.blur();
        }

        float cardW = (WIDTH - PADDING * 2f - CARD_GAP * (COLUMNS - 1)) / COLUMNS;
        if (mouseY >= listTopY && mouseY <= listBottomY) {
            for (int i = 0; i < cards.size(); i++) {
                ConfigCard card = cards.get(i);
                int col = i % COLUMNS;
                int row = i / COLUMNS;
                float cardX = x + PADDING + col * (cardW + CARD_GAP);
                float cardY = listTopY + row * (CARD_H + CARD_GAP) - scroll;
                if (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + CARD_H) {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        ConfigStorage storage = LightClient.getConfigStorage();
                        if (storage != null) storage.loadConfiguration(card.name);
                    } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        ConfigStorage storage = LightClient.getConfigStorage();
                        if (storage != null) storage.deleteConfiguration(card.name);
                        rebuildCards();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount, float x, float y, float screenW, float screenH) {
        if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= listTopY && mouseY <= listBottomY) {
            targetScroll -= (float) verticalAmount * 40f;
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode) {
        if (nameInput.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                String name = nameInput.getText().trim();
                if (!name.isEmpty()) {
                    ConfigStorage.saveConfiguration(name);
                    savePulseMs = System.currentTimeMillis();
                    rebuildCards();
                }
                nameInput.blur();
                return true;
            }
            long handle = MinecraftClient.getInstance().getWindow().getHandle();
            int mods = 0;
            if (GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS) mods |= GLFW.GLFW_MOD_CONTROL;
            if (GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) mods |= GLFW.GLFW_MOD_SHIFT;
            nameInput.keyPressed(keyCode, mods);
            return true;
        }
        return false;
    }

    public boolean charTyped(char c) {
        if (nameInput.isFocused()) {
            nameInput.charTyped(c);
            return true;
        }
        return false;
    }

    public void mouseReleased(int button) {
        nameInput.mouseReleased(button);
    }

    public void tickDrag(double mx, double my) {
        nameInput.tickDrag(mx, my);
    }

    private void rebuildCards() {
        cards.clear();
        ConfigStorage storage = LightClient.getConfigStorage();
        if (storage != null) {
            for (Config cfg : storage.getConfigs()) {
                cards.add(new ConfigCard(cfg.getName()));
            }
        }
    }

    private float getHover(String name) {
        Float v = hoverAnims.get(name);
        return v == null ? 0f : v;
    }

    private long updateAnimTime() {
        long now = System.currentTimeMillis();
        long dt = now - lastAnimTime;
        lastAnimTime = now;
        return Math.min(dt, 50);
    }

    private static float approach(float current, float target, float speed, float dtMs) {
        float delta = speed * dtMs / 1000f;
        if (current < target) return Math.min(current + delta, target);
        return Math.max(current - delta, target);
    }

    private static boolean ClickGui_isMotionBlur() {
        try {
            return tech.lightclient.modules.impl.render.ClickGui.isCsMotionBlurEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    private static final class ConfigCard {
        final String name;
        ConfigCard(String name) { this.name = name; }
    }
}
