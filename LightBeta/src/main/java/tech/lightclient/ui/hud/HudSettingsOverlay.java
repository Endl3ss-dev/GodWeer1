package tech.lightclient.ui.hud;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.modules.impl.render.Notifications;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public final class HudSettingsOverlay implements IMinecraft {
    private static final String EMPTY_SETTINGS_TEXT = "Нет настроек";

    private static final float TOGGLE_W = 22f;
    private static final float TOGGLE_H = 22f;
    private static final float TOGGLE_PAD = 2f;
    private static final float MODE_MIN_W = 78f;
    private static final float MODE_H = 18f;
    private static final float SLIDER_W = 112f;
    private static final float SLIDER_H = 18f;
    private static final float SLIDER_STEP_PAD = 8f;
    private static final float PANEL_GAP = 4f;

    private static final Map<String, SimpleLinearAnimation> toggleAnimations = new HashMap<>();
    private static final SettingEntry[] COMMON_HUD_OPTIONS = new SettingEntry[0];
    private static final SettingEntry[] NOTIFICATIONS_OPTIONS = mergeEntries(COMMON_HUD_OPTIONS, new SettingEntry[]{
            new ToggleEntry(Notifications.SETTING_ALWAYS_SHOW, "Показывать всегда", false),
            new ToggleEntry(Notifications.SETTING_MODULE_ON, "Включение модулей", true),
            new ToggleEntry(Notifications.SETTING_MODULE_OFF, "Выключение модулей", true),
            new ToggleEntry(Notifications.SETTING_ONLY_DON_ITEMS, "Только донатные предметы", false),
            new ToggleEntry(Notifications.SETTING_ITEM_PICKUP, "Подбор предметов", false),
            new ToggleEntry(Notifications.SETTING_ITEM_PICKUP_SHULKER, "Подбор предмета в шалкер", false),
            new ToggleEntry(Notifications.SETTING_SHOW_BANS, "Предупреждение о банах", true)
    });

    private static final SimpleLinearAnimation visibilityAnim = new SimpleLinearAnimation();
    private static final SimpleLinearAnimation scaleAnim = new SimpleLinearAnimation(350);

    private static boolean requestedVisible;
    private static String draggingName;
    private static String activeSliderKey;
    private static int popupX;
    private static int popupY;
    private static PopupAnchor notificationsPopupAnchor;
    private static boolean closing = false;

    private HudSettingsOverlay() {
    }

    public static boolean isOpen() {
        if (draggingName == null) return false;
        return requestedVisible;
    }

    public static void open(String name, double mouseX, double mouseY) {
        requestedVisible = true;
        closing = false;
        draggingName = name;
        popupX = (int) Math.round(mouseX);
        popupY = (int) Math.round(mouseY);
        notificationsPopupAnchor = null;
        visibilityAnim.setDuration(Interface.getAlphaDurationMs());
        visibilityAnim.show();
        scaleAnim.setEasing(Easings.EASE_OUT_BACK);
        scaleAnim.show();
    }

    public static void close() {
        if (closing) return;
        closing = true;
        requestedVisible = false;
        activeSliderKey = null;
        notificationsPopupAnchor = null;
        scaleAnim.setEasing(Easings.EASE_IN_OUT_QUAD);
        visibilityAnim.setDuration(400);
        scaleAnim.hide();
        visibilityAnim.hide();
    }

    public static boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (draggingName == null) {
            return false;
        }
        if (!requestedVisible && !closing && visibilityAnim.getProgress() <= 0f) {
            return false;
        }

        Layout layout = layoutFor(draggingName, popupX, popupY);
        float scaleProgress = scaleAnim.getProgress();
        float centerX = layout.x + layout.width * 0.5f;
        float centerY = layout.y + layout.height * 0.5f;

        float unscaleX = (float) ((mouseX - centerX) / (0.8f + 0.2f * scaleProgress)) + centerX;
        float unscaleY = (float) ((mouseY - centerY) / (0.8f + 0.2f * scaleProgress)) + centerY;

        boolean inside = unscaleX >= layout.x && unscaleX <= layout.x + layout.width
                && unscaleY >= layout.y && unscaleY <= layout.y + layout.height;

        if (!inside) {
            if (button == 0 || button == 1) {
                close();
            }
            return true;
        }

        SettingEntry[] options = optionsFor(draggingName);
        if ((button == 0 || button == 1) && options.length > 0) {
            float rowTop = layout.contentStartY;
            for (SettingEntry option : options) {
                float rowBottom = rowTop + layout.rowHeight;
                if (unscaleY >= rowTop && unscaleY <= rowBottom) {
                    float rowX = layout.x + layout.padding;
                    float rowW = layout.width - layout.padding * 2f;
                    if (option instanceof ToggleEntry toggle && button == 0) {
                        float toggleX = rowX + rowW - TOGGLE_W - TOGGLE_PAD;
                        if (unscaleX >= toggleX && unscaleX <= toggleX + TOGGLE_W) {
                            boolean current = DraggingManager.getHudBoolean(draggingName, toggle.key, toggle.defaultValue);
                            DraggingManager.setHudBoolean(draggingName, toggle.key, !current);
                            return true;
                        }
                    }
                    if (option instanceof ModeEntry mode) {
                        float modeW = MODE_MIN_W * layout.uiScale;
                        float modeX = rowX + rowW - modeW - TOGGLE_PAD;
                        if (unscaleX >= modeX && unscaleX <= modeX + modeW) {
                            cycleMode(mode, button == 1);
                            return true;
                        }
                    }
                    if (option instanceof SliderEntry slider) {
                        float sliderW = SLIDER_W * layout.uiScale;
                        float sliderX = rowX + rowW - sliderW - TOGGLE_PAD;
                        if (unscaleX >= sliderX && unscaleX <= sliderX + sliderW) {
                            if (button == 0) {
                                activeSliderKey = slider.key;
                                updateSlider(layout, slider, unscaleX, false);
                            } else {
                                activeSliderKey = null;
                                updateSlider(layout, slider, unscaleX, true);
                            }
                            return true;
                        }
                    }
                }
                rowTop += layout.rowHeight;
            }
        }

        return true;
    }

    public static boolean handleMouseDrag(double mouseX, double mouseY, int button) {
        if (button != 0 || draggingName == null || activeSliderKey == null) {
            return false;
        }
        if (!requestedVisible && !closing && visibilityAnim.getProgress() <= 0f) {
            return false;
        }

        Layout layout = layoutFor(draggingName, popupX, popupY);
        float scaleProgress = scaleAnim.getProgress();
        float centerX = layout.x + layout.width * 0.5f;
        float centerY = layout.y + layout.height * 0.5f;

        float unscaleX = (float) ((mouseX - centerX) / (0.8f + 0.2f * scaleProgress)) + centerX;

        for (SettingEntry option : optionsFor(draggingName)) {
            if (option instanceof SliderEntry slider && slider.key.equals(activeSliderKey)) {
                updateSlider(layout, slider, unscaleX, false);
                return true;
            }
        }
        return false;
    }

    public static boolean handleMouseRelease(int button) {
        if (button != 0 || activeSliderKey == null) {
            return false;
        }
        activeSliderKey = null;
        return true;
    }

    public static void render(EventRender.Screen.Gui event) {
        if (draggingName == null) {
            return;
        }
        if (!(mc.currentScreen instanceof ChatScreen)) {
            draggingName = null;
            closing = false;
            requestedVisible = false;
            activeSliderKey = null;
            notificationsPopupAnchor = null;
            return;
        }

        float alphaProgress = visibilityAnim.getProgress();
        float scaleProgress = scaleAnim.getProgress();

        if (alphaProgress <= 0f) {
            draggingName = null;
            closing = false;
            requestedVisible = false;
            activeSliderKey = null;
            notificationsPopupAnchor = null;
            return;
        }

        Layout layout = layoutFor(draggingName, popupX, popupY);
        popupX = Math.round(layout.x);
        popupY = Math.round(layout.y);

        if (activeSliderKey != null) {
            long handle = mc.getWindow().getHandle();
            if (GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                handleMouseDrag(mc.mouse.getX(), mc.mouse.getY(), 0);
            } else {
                activeSliderKey = null;
            }
        }

        float currentScale = 0.8f + 0.2f * scaleProgress;
        float centerX = layout.x + layout.width * 0.5f;
        float centerY = layout.y + layout.height * 0.5f;

        Renderer2D renderer = event.getRenderer();
        renderer.pushScale(currentScale, currentScale, centerX, centerY);

        int panelColor = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), 0.94f * alphaProgress);
        int titleColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        int secondaryTextColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress * 0.75f);

        // Заголовок и содержимое рисуются отдельными карточками, как у
        // "Активных биндов" и "Активных эффектов".
        renderer.blur(layout.x, layout.y, layout.width, layout.headerHeight, layout.rounding, alphaProgress);
        renderer.rect(layout.x, layout.y, layout.width, layout.headerHeight, layout.rounding, panelColor);
        renderer.blur(layout.x, layout.contentPanelY, layout.width, layout.contentPanelHeight, layout.rounding, alphaProgress);
        renderer.rect(layout.x, layout.contentPanelY, layout.width, layout.contentPanelHeight, layout.rounding, panelColor);

        String title = "Настройки: " + displayName(draggingName);
        float titleTextY = centeredTextY(layout.y, layout.headerHeight, layout.textSize, 0.15f * layout.uiScale) + 0.33f;
        renderer.text(FontRegistry.DELTA_HUD, layout.x + layout.padding + 5, titleTextY, layout.textSize, title, titleColor, "l");

        SettingEntry[] options = optionsFor(draggingName);
        if (options.length == 0) {
            float maxTextWidth = Math.max(0f, layout.width - layout.padding * 2f - (4f * layout.uiScale));
            String text = fitTextToWidth(renderer, EMPTY_SETTINGS_TEXT, layout.textSize, maxTextWidth);
            float textWidth = FontRegistry.DELTA_HUD.getWidth(text, layout.textSize);
            float textX = layout.x + (layout.width - textWidth) / 2f;
            float textY = centeredTextY(layout.contentStartY, layout.emptyRowHeight, layout.textSize, 0.15f * layout.uiScale);
            renderer.text(FontRegistry.DELTA_HUD, textX, textY, layout.textSize, text, secondaryTextColor);
            renderer.popScale();
            return;
        }

        float rowTop = layout.contentStartY;
        for (SettingEntry option : options) {
            float rowBoxHeight = layout.rowHeight - (1.8f * layout.uiScale);
            float rowRounding = 5.4f * layout.uiScale;
            float rowY = rowTop;
            float rowX = layout.x + layout.padding;
            float rowW = layout.width - layout.padding * 2f;
            float rowH = rowBoxHeight;
            float labelX = rowX + 6.0f * layout.uiScale;

            float controlLeft = rowX + rowW;
            if (option instanceof ToggleEntry toggle) {
                float toggleX = rowX + rowW - TOGGLE_W - TOGGLE_PAD;
                float toggleY = rowY + (rowH - TOGGLE_H) * 0.5f;
                renderToggle(renderer, toggle, toggleX, toggleY, alphaProgress);
                controlLeft = toggleX;
            } else if (option instanceof ModeEntry mode) {
                float controlTextSize = layout.textSize - (1.5f * layout.uiScale);
                String currentMode = currentMode(mode);
                float modeW = Math.max(MODE_MIN_W * layout.uiScale, renderer.measureText(FontRegistry.DELTA_HUD, currentMode, controlTextSize).width + 18f * layout.uiScale);
                float modeH = MODE_H * layout.uiScale;
                float modeX = rowX + rowW - modeW - TOGGLE_PAD;
                float modeY = rowY + (rowH - modeH) * 0.5f;
                renderMode(renderer, currentMode, modeX, modeY, modeW, modeH, controlTextSize, alphaProgress);
                controlLeft = modeX;
            } else if (option instanceof SliderEntry slider) {
                float sliderW = SLIDER_W * layout.uiScale;
                float sliderH = SLIDER_H * layout.uiScale;
                float sliderX = rowX + rowW - sliderW - TOGGLE_PAD;
                float sliderY = rowY + (rowH - sliderH) * 0.5f;
                renderSlider(renderer, slider, sliderX, sliderY, sliderW, sliderH, layout, alphaProgress);
                controlLeft = sliderX;
            }

            float labelGap = 2f * layout.uiScale;
            float maxLabelWidth = Math.max(0f, controlLeft - labelX - labelGap);
            String labelText = fitTextToWidth(renderer, option.label(), layout.textSize, maxLabelWidth);
            float rowTextY = centeredTextY(rowY, rowBoxHeight, layout.textSize, 0.55f * layout.uiScale);
            renderer.text(FontRegistry.DELTA_HUD, labelX, rowTextY, layout.textSize, labelText, titleColor);
            rowTop += layout.rowHeight;
        }

        renderer.popScale();
    }

    private static void renderToggle(Renderer2D renderer, ToggleEntry option, float toggleX, float toggleY, float alphaProgress) {
        boolean enabled = DraggingManager.getHudBoolean(draggingName, option.key, option.defaultValue);

        SimpleLinearAnimation toggleAnim = toggleAnimations.computeIfAbsent(animationKey(option),
                k -> {
                    SimpleLinearAnimation a = new SimpleLinearAnimation(390);
                    a.setEasing(Easings.EASE_OUT_BACK);
                    if (enabled) a.show();
                    else a.hide();
                    return a;
                });

        if (enabled) toggleAnim.show();
        else toggleAnim.hide();

        float p = toggleAnim.getProgress();
        int alpha = (int) (alphaProgress * 255);

        int bgOff = withAlpha(0xFFFFFF, (int) (alpha * 0.08f));
        int bgOn = scaleAlpha(ClientColors.ICON.getRGB(), (int) (alpha * 0.95f));
        renderer.rect(toggleX, toggleY, TOGGLE_W, TOGGLE_H, 6f, blend(bgOff, bgOn, p));
        if (p > 0.02f) {
            drawCheckMark(renderer, toggleX, toggleY, TOGGLE_W, TOGGLE_H, withAlpha(0xFFFFFF, (int) (alpha * p)), p, 2f);
        }
    }

    private static int withAlpha(int argb, int a) {
        int clamped = Math.max(0, Math.min(255, a));
        return (clamped << 24) | (argb & 0x00FFFFFF);
    }

    private static int scaleAlpha(int argb, int alpha) {
        int baseA = (argb >>> 24) & 0xFF;
        int finalA = (int) (baseA * (alpha / 255f));
        return withAlpha(argb, finalA);
    }

    private static int blend(int from, int to, float progress) {
        float t = Math.max(0f, Math.min(1f, progress));
        int fa = (from >>> 24) & 0xFF;
        int fr = (from >>> 16) & 0xFF;
        int fg = (from >>> 8) & 0xFF;
        int fb = from & 0xFF;
        int ta = (to >>> 24) & 0xFF;
        int tr = (to >>> 16) & 0xFF;
        int tg = (to >>> 8) & 0xFF;
        int tb = to & 0xFF;
        int a = Math.round(fa + (ta - fa) * t);
        int r = Math.round(fr + (tr - fr) * t);
        int g = Math.round(fg + (tg - fg) * t);
        int b = Math.round(fb + (tb - fb) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void drawCheckMark(Renderer2D render, float bx, float by, float bw, float bh, int color, float scale, float sizeMul) {
        float s = 0.6f + 0.4f * Math.max(0f, Math.min(1f, scale));
        float base = Math.min(bw, bh);
        float arm1 = 1.7f * s * sizeMul;
        float arm2 = 3.8f * s * sizeMul;
        float thick = Math.max(0.7f, base * 0.045f * sizeMul);

        float leftDX = arm1 * 0.85f;
        float leftDY = arm1 * 0.85f;
        float rightDX = arm2 * 0.78f;
        float rightDY = arm2 * 0.95f;

        float pStartX = -leftDX;
        float pStartY = -leftDY;
        float pEndX = rightDX;
        float pEndY = -rightDY;
        float pMidX = 0f;
        float pMidY = 0f;

        float minX = Math.min(Math.min(pStartX, pMidX), pEndX);
        float maxX = Math.max(Math.max(pStartX, pMidX), pEndX);
        float minY = Math.min(Math.min(pStartY, pMidY), pEndY);
        float maxY = Math.max(Math.max(pStartY, pMidY), pEndY);

        float pivotX = bx + bw * 0.5f - (minX + maxX) * 0.5f;
        float pivotY = by + bh * 0.5f - (minY + maxY) * 0.5f;

        int steps = (int) Math.max(10, 14 * sizeMul);
        for (int i = 0; i < steps; i++) {
            float t = i / (float) (steps - 1);
            float lx = pivotX + pStartX + (pMidX - pStartX) * t;
            float ly = pivotY + pStartY + (pMidY - pStartY) * t;
            render.rect(lx - thick * 0.5f, ly - thick * 0.5f, thick, thick, 0f, color);
        }
        for (int i = 0; i < steps; i++) {
            float t = i / (float) (steps - 1);
            float rx = pivotX + pMidX + (pEndX - pMidX) * t;
            float ry = pivotY + pMidY + (pEndY - pMidY) * t;
            render.rect(rx - thick * 0.5f, ry - thick * 0.5f, thick, thick, 0f, color);
        }
    }

    private static void renderMode(Renderer2D renderer, String mode, float x, float y, float w, float h, float textSize, float alphaProgress) {
        float rounding = h * 0.5f;
        int bg = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), alphaProgress * 0.42f);
        int outline = ClientColors.applyAlpha(ClientColors.ICON.getRGB(), alphaProgress * 0.75f);
        int textColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);

        String modeText = fitTextToWidth(renderer, mode, textSize, Math.max(1f, w - 14f));
        renderer.text(FontRegistry.DELTA_HUD, x + 15f, centeredTextY(y, h, textSize, 0.25f), textSize, modeText, textColor);
    }

    private static void renderSlider(Renderer2D renderer, SliderEntry slider, float x, float y, float w, float h, Layout layout, float alphaProgress) {
        float value = DraggingManager.getHudFloat(draggingName, slider.key, slider.defaultValue);
        float progress = (value - slider.min) / Math.max(0.0001f, slider.max - slider.min);
        progress = Math.max(0f, Math.min(1f, progress));

        float valueSize = layout.textSize - (2f * layout.uiScale);
        String valueText = formatSliderValue(value);
        float valueWidth = renderer.measureText(FontRegistry.DELTA_HUD, valueText, valueSize).width;
        float maxValueWidth = maxSliderValueWidth(slider, valueSize);
        int textColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        int trackColor = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), alphaProgress * 0.65f);
        int fillColor = ClientColors.applyAlpha(ClientColors.ICON.getRGB(), alphaProgress * 0.95f);
        int knobColor = ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress);

        float trackTopPad = 10f * layout.uiScale;
        float trackH = 4f * layout.uiScale;
        float trackW = Math.max(8f * layout.uiScale, w - maxValueWidth - 8f * layout.uiScale);
        float trackY = y + trackTopPad;
        renderer.rect(x, trackY, trackW, trackH, trackH * 0.5f, trackColor);
        if (progress > 0f) {
            renderer.rect(x, trackY, trackW * progress, trackH, trackH * 0.5f, fillColor);
        }

        float knobSize = 8f * layout.uiScale;
        float knobX = x + trackW * progress - knobSize * 0.5f;
        knobX = Math.max(x - knobSize * 0.5f, Math.min(x + trackW - knobSize * 0.5f, knobX));
        renderer.rect(knobX, trackY - (knobSize - trackH) * 0.5f, knobSize, knobSize, knobSize * 0.5f, knobColor);
        renderer.text(FontRegistry.DELTA_HUD, x + w - valueWidth, centeredTextY(y, h, valueSize, 0.35f * layout.uiScale), valueSize, valueText, textColor);
    }

    private static void updateSlider(Layout layout, SliderEntry slider, double mouseX, boolean reverseStep) {
        float rowX = layout.x + layout.padding;
        float rowW = layout.width - layout.padding * 2f;
        float sliderW = SLIDER_W * layout.uiScale;
        float sliderX = rowX + rowW - sliderW - TOGGLE_PAD;

        float current = DraggingManager.getHudFloat(draggingName, slider.key, slider.defaultValue);
        float next;
        if (reverseStep) {
            next = current - slider.step;
        } else {
            float valueSize = layout.textSize - (2f * layout.uiScale);
            float maxValueWidth = maxSliderValueWidth(slider, valueSize);
            float trackW = Math.max(8f * layout.uiScale, sliderW - maxValueWidth - 8f * layout.uiScale);

            float clampedX = Math.max(sliderX, Math.min(sliderX + trackW, (float) mouseX));
            float progress = (clampedX - sliderX) / Math.max(1f, trackW);
            next = slider.min + (slider.max - slider.min) * progress;
        }
        next = roundToStep(next, slider.step);
        next = Math.max(slider.min, Math.min(slider.max, next));
        DraggingManager.setHudFloat(draggingName, slider.key, next);
    }

    private static void cycleMode(ModeEntry mode, boolean reverse) {
        String current = currentMode(mode);
        int currentIndex = 0;
        for (int i = 0; i < mode.values.length; i++) {
            if (mode.values[i].equalsIgnoreCase(current)) {
                currentIndex = i;
                break;
            }
        }
        int offset = reverse ? -1 : 1;
        int nextIndex = (currentIndex + offset + mode.values.length) % mode.values.length;
        DraggingManager.setHudMode(draggingName, mode.key, mode.values[nextIndex]);
    }

    private static String currentMode(ModeEntry mode) {
        return DraggingManager.getHudMode(draggingName, mode.key, mode.defaultValue);
    }

    private static String animationKey(ToggleEntry option) {
        return draggingName + "_" + option.key;
    }

    private static Layout layoutFor(String name, int requestedX, int requestedY) {
        float uiScale = Math.max(0.5f, Interface.getInterfaceScale());

        float padding = 5.0f * uiScale;
        float textSize = 16.2f * uiScale;
        float rowHeight = 27f * uiScale;
        float headerHeight = 32.4f * uiScale;
        float emptyRowHeight = 24f * uiScale;
        float rounding = 9f * uiScale;

        SettingEntry[] options = optionsFor(name);
        String title = "Настройки: " + displayName(name);

        float titleWidth = FontRegistry.DELTA_HUD.getWidth(title, textSize);
        float titleRequired = (padding * 2f) + titleWidth;

        float width;
        if (options.length == 0) {
            float emptyWidth = FontRegistry.DELTA_HUD.getWidth(EMPTY_SETTINGS_TEXT, textSize);
            float emptyRequired = (padding * 2f) + emptyWidth;
            width = Math.max(170f * uiScale, Math.max(titleRequired, emptyRequired));
        } else {
            float longestLabelWidth = 0f;
            float maxControlWidth = TOGGLE_W + TOGGLE_PAD;
            for (SettingEntry option : options) {
                longestLabelWidth = Math.max(longestLabelWidth, FontRegistry.DELTA_HUD.getWidth(option.label(), textSize));
                if (option instanceof ModeEntry mode) {
                    float modeTextSize = textSize - (1.5f * uiScale);
                    for (String value : mode.values) {
                        float modeWidth = FontRegistry.DELTA_HUD.getWidth(value, modeTextSize) + 18f * uiScale + TOGGLE_PAD;
                        maxControlWidth = Math.max(maxControlWidth, modeWidth);
                    }
                } else if (option instanceof SliderEntry) {
                    maxControlWidth = Math.max(maxControlWidth, SLIDER_W * uiScale + TOGGLE_PAD);
                }
            }
            maxControlWidth = Math.max(0f, maxControlWidth - 25f);
            float contentRequired = (padding * 2f) + (4.5f * uiScale) + longestLabelWidth + (2f * uiScale) + maxControlWidth;
            width = Math.max(180f * uiScale, Math.max(contentRequired, titleRequired));
        }

        width += 5;

        float panelGap = PANEL_GAP * uiScale;
        float contentHeight = options.length > 0 ? options.length * rowHeight : emptyRowHeight;
        float contentPanelHeight = padding + contentHeight + padding;
        float height = headerHeight + panelGap + contentPanelHeight;

        float windowW = mc.getWindow().getWidth();
        float windowH = mc.getWindow().getHeight();
        float margin = 7.2f * uiScale;
        PopupAnchor anchor = popupAnchorFor(name, requestedX, requestedY, width, height, margin, windowW);
        float x = anchor.x;
        float y = anchor.y;

        if (x + width > windowW - margin) {
            x = windowW - width - margin;
        }
        if (y + height > windowH - margin) {
            y = windowH - height - margin;
        }
        if (x < margin) {
            x = margin;
        }
        if (y < margin) {
            y = margin;
        }

        float contentPanelY = y + headerHeight + panelGap;
        float contentStartY = contentPanelY + padding;
        return new Layout(x, y, width, height, padding, textSize, rowHeight, headerHeight, emptyRowHeight, rounding, contentPanelY, contentPanelHeight, contentStartY, uiScale);
    }

    private static PopupAnchor popupAnchorFor(String name, int requestedX, int requestedY, float width, float height, float margin, float windowW) {
        if (!Notifications.SETTINGS_SCOPE.equals(name)) {
            return new PopupAnchor(requestedX, requestedY);
        }

        if (notificationsPopupAnchor != null) {
            return notificationsPopupAnchor;
        }

        if (notificationsPopupAnchor == null) {
            notificationsPopupAnchor = resolveNotificationsPopupAnchor(requestedX, requestedY, width, height);
        }
        return notificationsPopupAnchor;
    }

    private static PopupAnchor resolveNotificationsPopupAnchor(int requestedX, int requestedY, float width, float height) {
        float windowW = mc.getWindow().getWidth();
        float margin = 7.2f * Math.max(0.5f, Interface.getInterfaceScale());

        Dragging dragging = DraggingManager.draggables.get(Notifications.SETTINGS_SCOPE);
        if (dragging == null) {
            return new PopupAnchor(requestedX, requestedY);
        }

        float popupGap = 8f * Math.max(0.5f, Interface.getInterfaceScale());
        float previewLeft = dragging.getX();
        float previewTop = dragging.getY();
        float previewRight = previewLeft + dragging.getWidth();
        float previewBottom = previewTop + dragging.getHeight();

        float x = previewRight + popupGap;
        if (x + width > windowW - margin) {
            x = previewLeft - width - popupGap;
        }
        if (x < margin) {
            x = previewLeft + Math.max(0f, (dragging.getWidth() - width) * 0.5f);
        }

        float y = previewTop + Math.max(0f, (dragging.getHeight() - height) * 0.5f);
        if (previewBottom - previewTop < height) {
            y = previewTop;
        }

        return new PopupAnchor(x, y);
    }

    private static SettingEntry[] optionsFor(String name) {
        if (Notifications.SETTINGS_SCOPE.equals(name)) {
            return NOTIFICATIONS_OPTIONS;
        }
        return COMMON_HUD_OPTIONS;
    }

    private static String displayName(String name) {
        if (name == null) {
            return "";
        }
        if (Notifications.SETTINGS_SCOPE.equals(name)) {
            return "Notifications";
        }
        if (name.startsWith("Rs ")) {
            return name.substring(3);
        }
        return name;
    }

    private static float centeredTextY(float y, float height, float size, float shiftUp) {
        return y + height * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', size) - shiftUp;
    }

    private static String fitTextToWidth(Renderer2D renderer, String text, float size, float maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (renderer.measureText(FontRegistry.DELTA_HUD, text, size).width <= maxWidth) {
            return text;
        }
        String suffix = "...";
        float suffixWidth = renderer.measureText(FontRegistry.DELTA_HUD, suffix, size).width;
        if (suffixWidth > maxWidth) {
            return "";
        }
        String trimmed = text;
        while (!trimmed.isEmpty()) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
            String candidate = trimmed + suffix;
            if (renderer.measureText(FontRegistry.DELTA_HUD, candidate, size).width <= maxWidth) {
                return candidate;
            }
        }
        return suffix;
    }

    private static SettingEntry[] mergeEntries(SettingEntry[] first, SettingEntry[] second) {
        SettingEntry[] merged = new SettingEntry[first.length + second.length];
        System.arraycopy(first, 0, merged, 0, first.length);
        System.arraycopy(second, 0, merged, first.length, second.length);
        return merged;
    }

    private static String formatSliderValue(float value) {
        return String.format(java.util.Locale.US, "%.1f", value);
    }

    private static float maxSliderValueWidth(SliderEntry slider, float valueSize) {
        return Math.max(
                FontRegistry.DELTA_HUD.getWidth(formatSliderValue(slider.min), valueSize),
                FontRegistry.DELTA_HUD.getWidth(formatSliderValue(slider.max), valueSize)
        );
    }

    private static float roundToStep(float value, float step) {
        if (!Float.isFinite(step) || step <= 0f) return value;
        return Math.round(value / step) * step;
    }

    private interface SettingEntry {
        String label();
    }

    private static final class ToggleEntry implements SettingEntry {
        private final String key;
        private final String label;
        private final boolean defaultValue;

        private ToggleEntry(String key, String label, boolean defaultValue) {
            this.key = key;
            this.label = label;
            this.defaultValue = defaultValue;
        }

        @Override
        public String label() {
            return label;
        }
    }

    private static final class ModeEntry implements SettingEntry {
        private final String key;
        private final String label;
        private final String defaultValue;
        private final String[] values;

        private ModeEntry(String key, String label, String defaultValue, String... values) {
            this.key = key;
            this.label = label;
            this.defaultValue = defaultValue;
            this.values = values == null || values.length == 0 ? new String[]{defaultValue} : values;
        }

        @Override
        public String label() {
            return label;
        }
    }

    private static final class SliderEntry implements SettingEntry {
        private final String key;
        private final String label;
        private final float defaultValue;
        private final float min;
        private final float max;
        private final float step;

        private SliderEntry(String key, String label, float defaultValue, float min, float max, float step) {
            this.key = key;
            this.label = label;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public String label() {
            return label;
        }
    }

    private static final class Layout {
        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final float padding;
        private final float textSize;
        private final float rowHeight;
        private final float headerHeight;
        private final float emptyRowHeight;
        private final float rounding;
        private final float contentPanelY;
        private final float contentPanelHeight;
        private final float contentStartY;
        private final float uiScale;

        private Layout(float x, float y, float width, float height, float padding, float textSize, float rowHeight, float headerHeight, float emptyRowHeight, float rounding, float contentPanelY, float contentPanelHeight, float contentStartY, float uiScale) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.padding = padding;
            this.textSize = textSize;
            this.rowHeight = rowHeight;
            this.headerHeight = headerHeight;
            this.emptyRowHeight = emptyRowHeight;
            this.rounding = rounding;
            this.contentPanelY = contentPanelY;
            this.contentPanelHeight = contentPanelHeight;
            this.contentStartY = contentStartY;
            this.uiScale = uiScale;
        }
    }

    private static final class PopupAnchor {
        private final float x;
        private final float y;

        private PopupAnchor(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
