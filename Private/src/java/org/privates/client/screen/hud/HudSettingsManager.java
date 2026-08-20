package org.privates.client.screen.hud;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.scroll.ScrollUtil;
import org.joml.Vector2f;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HudSettingsManager {
    private final Map<String, ElementSettings> elementSettings = new HashMap<>();
    private final Minecraft mc = Minecraft.getInstance();
    private String editingProperty = "";
    private boolean isEditingText = false;
    private ElementSettings currentlyEditing = null;
    private boolean showColorPicker = false;
    private String colorPickerProperty = "";

    public void ensureElementSettings(String id, String name) {
        if (!elementSettings.containsKey(id)) {
            elementSettings.put(id, new ElementSettings(id, name));
        }
    }

    public ElementSettings getSettings(String id) {
        return elementSettings.get(id);
    }

    public void resetSettings(String id) {
        ElementSettings settings = elementSettings.get(id);
        if (settings != null) {
            settings.reset();
        }
    }

    public void loadAllSettings() {
        try {
            File configFile = new File(mc.gameDir, "config/excellent/hud_settings.properties");
            if (!configFile.exists()) return;

            Properties props = new Properties();
            props.load(new FileInputStream(configFile));

            for (String key : props.stringPropertyNames()) {
                String[] parts = key.split("\\.");
                if (parts.length != 2) continue;

                String elementId = parts[0];
                String property = parts[1];
                String value = props.getProperty(key);

                ElementSettings settings = elementSettings.get(elementId);
                if (settings != null) {
                    settings.loadProperty(property, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAllSettings() {
        try {
            File configDir = new File(mc.gameDir, "config/excellent");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(configDir, "hud_settings.properties");
            Properties props = new Properties();

            for (Map.Entry<String, ElementSettings> entry : elementSettings.entrySet()) {
                String elementId = entry.getKey();
                ElementSettings settings = entry.getValue();
                settings.saveToProperties(props, elementId);
            }

            props.store(new FileOutputStream(configFile), "HUD Element Settings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderPropertiesPanel(MatrixStack matrix, HudCustomizeScreen.HudElement element, float x, float y, float w, float h, int mouseX, int mouseY, ScrollUtil scroll) {
        ElementSettings settings = elementSettings.get(element.id);
        if (settings == null) return;

        scroll.update();
        float totalHeight = calculateTotalHeight(settings);
        scroll.setMax(totalHeight, h);

        ScissorUtil.enableScissor(x, y, w, h);

        float currentY = y + scroll.getWheel();

        currentY += renderSection(matrix, "🎯 Основные настройки", x, currentY, w);
        currentY += renderSlider(matrix, "Масштаб", settings.scale, 0.3f, 3.0f, x, currentY, w, mouseX, mouseY, "scale", settings);
        currentY += renderToggle(matrix, "Показать элемент", settings.enabled, x, currentY, w, mouseX, mouseY, "enabled", settings);
        currentY += 10;

        currentY += renderSection(matrix, "📐 Размеры", x, currentY, w);
        currentY += renderSlider(matrix, "Ширина", element.dragSetting.size.x, 30, 500, x, currentY, w, mouseX, mouseY, "width", settings);
        currentY += renderSlider(matrix, "Высота", element.dragSetting.size.y, 15, 400, x, currentY, w, mouseX, mouseY, "height", settings);
        currentY += 10;

        currentY += renderSection(matrix, "🎨 Внешний вид", x, currentY, w);
        currentY += renderColorPicker(matrix, "Цвет текста", settings.textColor, x, currentY, w, mouseX, mouseY, "textColor", settings);
        currentY += renderSlider(matrix, "Размер шрифта", settings.fontSize, 4, 24, x, currentY, w, mouseX, mouseY, "fontSize", settings);
        currentY += renderTextInput(matrix, "Отображаемый текст", settings.displayText, x, currentY, w, mouseX, mouseY, "displayText", settings);
        currentY += 10;

        currentY += renderSection(matrix, "📐 Смещения текста", x, currentY, w);
        currentY += renderSlider(matrix, "Смещение X", settings.textOffsetX, -100, 100, x, currentY, w, mouseX, mouseY, "textOffsetX", settings);
        currentY += renderSlider(matrix, "Смещение Y", settings.textOffsetY, -100, 100, x, currentY, w, mouseX, mouseY, "textOffsetY", settings);
        currentY += 10;

        currentY += renderSection(matrix, "🎭 Настройки иконки", x, currentY, w);
        currentY += renderToggle(matrix, "Показать иконку", settings.showIcon, x, currentY, w, mouseX, mouseY, "showIcon", settings);
        if (settings.showIcon) {
            currentY += renderColorPicker(matrix, "Цвет иконки", settings.iconColor, x, currentY, w, mouseX, mouseY, "iconColor", settings);
            currentY += renderSlider(matrix, "Смещение иконки X", settings.iconOffsetX, -50, 50, x, currentY, w, mouseX, mouseY, "iconOffsetX", settings);
            currentY += renderSlider(matrix, "Смещение иконки Y", settings.iconOffsetY, -50, 50, x, currentY, w, mouseX, mouseY, "iconOffsetY", settings);
        }
        currentY += 10;

        currentY += renderSection(matrix, "⚡ Быстрые действия", x, currentY, w);
        currentY += renderActionButton(matrix, "🔄 Сбросить настройки", x, currentY, w, mouseX, mouseY, () -> {
            settings.reset();
        });
        currentY += renderActionButton(matrix, "🎯 Выровнять по центру", x, currentY, w, mouseX, mouseY, () -> {
            alignToCenter(element);
        });
        currentY += renderActionButton(matrix, "📋 Копировать настройки", x, currentY, w, mouseX, mouseY, () -> {
            copySettings(settings);
        });
        currentY += renderActionButton(matrix, "📄 Вставить настройки", x, currentY, w, mouseX, mouseY, () -> {
            pasteSettings(settings);
        });

        ScissorUtil.disableScissor();
        scroll.renderV(matrix, new Vector2f(x + w - 6, y), h, 0.6f);

        if (showColorPicker) {
            renderColorPickerPopup(matrix, mouseX, mouseY, settings);
        }
    }

    private float calculateTotalHeight(ElementSettings settings) {
        float height = 50;
        height += 100;
        height += 130;
        height += 90;
        if (settings.showIcon) {
            height += 90;
        }
        height += 120;
        return height;
    }

    private float renderSection(MatrixStack matrix, String title, float x, float y, float w) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, w, 25, ColorUtil.getColor(25, 35, 50, 180), Round.of(4));
        Fonts.SF_MEDIUM.draw(matrix, title, x + 12, y + 8, ColorUtil.getColor(220, 230, 250), 10);
        return 30;
    }

    private float renderSlider(MatrixStack matrix, String label, float value, float min, float max, float x, float y, float w, int mouseX, int mouseY, String property, ElementSettings settings) {
        Fonts.SF_MEDIUM.draw(matrix, label, x + 5, y + 2, ColorUtil.getColor(180, 190, 210), 8);

        float sliderW = w - 80;
        float sliderH = 4f;
        float sliderX = x + 5;
        float sliderY = y + 16;

        boolean hovered = isHovered(mouseX, mouseY, sliderX, sliderY - 2, sliderW, sliderH + 4);

        RenderUtil.Rounded.roundedRect(matrix, sliderX, sliderY, sliderW, sliderH, ColorUtil.getColor(40, 50, 70, 150), Round.of(sliderH / 2));

        float fillW = ((value - min) / (max - min)) * sliderW;
        int fillColor = hovered ? ColorUtil.getColor(120, 180, 255) : ColorUtil.getColor(100, 160, 240);
        RenderUtil.Rounded.roundedRect(matrix, sliderX, sliderY, fillW, sliderH, fillColor, Round.of(sliderH / 2));

        float handleX = sliderX + fillW - 3;
        int handleColor = hovered ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(240, 240, 240);
        RenderUtil.Rounded.roundedRect(matrix, handleX, sliderY - 2, 6, sliderH + 4, handleColor, Round.of(3));

        String valueText = String.format("%.2f", value);
        Fonts.SF_MEDIUM.draw(matrix, valueText, x + w - 65, y + 2, ColorUtil.getColor(200, 210, 230), 8);

        return 25;
    }

    private float renderToggle(MatrixStack matrix, String label, boolean value, float x, float y, float w, int mouseX, int mouseY, String property, ElementSettings settings) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, w, 20);

        Fonts.SF_MEDIUM.draw(matrix, label, x + 5, y + 5, ColorUtil.getColor(180, 190, 210), 9);

        float toggleW = 32f;
        float toggleH = 16f;
        float toggleX = x + w - toggleW - 5;
        float toggleY = y + 2;

        int bgColor = value ?
                (hovered ? ColorUtil.getColor(120, 200, 120) : ColorUtil.getColor(100, 180, 100)) :
                (hovered ? ColorUtil.getColor(80, 90, 110) : ColorUtil.getColor(60, 70, 90));

        RenderUtil.Rounded.roundedRect(matrix, toggleX, toggleY, toggleW, toggleH, bgColor, Round.of(toggleH / 2));

        float handleSize = toggleH - 4;
        float handleX = value ? toggleX + toggleW - handleSize - 2 : toggleX + 2;
        RenderUtil.Rounded.roundedRect(matrix, handleX, toggleY + 2, handleSize, handleSize,
                ColorUtil.getColor(255, 255, 255), Round.of(handleSize / 2));

        return 25;
    }

    private float renderColorPicker(MatrixStack matrix, String label, int color, float x, float y, float w, int mouseX, int mouseY, String property, ElementSettings settings) {
        Fonts.SF_MEDIUM.draw(matrix, label, x + 5, y + 2, ColorUtil.getColor(180, 190, 210), 8);

        float colorW = 60f;
        float colorH = 16f;
        float colorX = x + w - colorW - 5;
        float colorY = y + 16;

        boolean hovered = isHovered(mouseX, mouseY, colorX, colorY, colorW, colorH);

        RenderUtil.Rounded.roundedRect(matrix, colorX, colorY, colorW, colorH, color, Round.of(3));
        RenderUtil.Rounded.roundedOutline(matrix, colorX, colorY, colorW, colorH, 1.5f,
                hovered ? ColorUtil.getColor(255, 255, 255, 200) : ColorUtil.getColor(255, 255, 255, 100), Round.of(3));

        String hexColor = String.format("#%06X", color & 0xFFFFFF);
        Fonts.SF_MEDIUM.drawCenter(matrix, hexColor, colorX + colorW / 2, colorY + 5,
                getContrastColor(color), 7);

        return 35;
    }

    private int getContrastColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        return luminance > 0.5 ? ColorUtil.getColor(0, 0, 0) : ColorUtil.getColor(255, 255, 255);
    }

    private float renderTextInput(MatrixStack matrix, String label, String text, float x, float y, float w, int mouseX, int mouseY, String property, ElementSettings settings) {
        Fonts.SF_MEDIUM.draw(matrix, label, x + 5, y + 2, ColorUtil.getColor(180, 190, 210), 8);

        float inputW = w - 10;
        float inputH = 20f;
        float inputX = x + 5;
        float inputY = y + 16;

        boolean hovered = isHovered(mouseX, mouseY, inputX, inputY, inputW, inputH);
        boolean editing = isEditingText && editingProperty.equals(property) && currentlyEditing == settings;

        int bgColor = editing ? ColorUtil.getColor(30, 40, 60, 200) :
                hovered ? ColorUtil.getColor(25, 35, 55, 180) :
                        ColorUtil.getColor(20, 30, 50, 160);

        RenderUtil.Rounded.roundedRect(matrix, inputX, inputY, inputW, inputH, bgColor, Round.of(4));
        RenderUtil.Rounded.roundedOutline(matrix, inputX, inputY, inputW, inputH, 1f,
                editing ? ColorUtil.getColor(100, 160, 240, 200) : ColorUtil.getColor(60, 80, 120, 150), Round.of(4));

        String displayText = text.isEmpty() ? "Введите текст..." : text;
        int textColor = text.isEmpty() ? ColorUtil.getColor(120, 130, 150) : ColorUtil.getColor(220, 230, 250);

        Fonts.SF_MEDIUM.draw(matrix, displayText, inputX + 8, inputY + 6, textColor, 8);

        if (editing) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                float cursorX = inputX + 8 + Fonts.SF_MEDIUM.getWidth(text, 8);
                RenderUtil.Rounded.roundedRect(matrix, cursorX, inputY + 4, 1, 12, ColorUtil.getColor(255, 255, 255), Round.of(0));
            }
        }

        return 40;
    }

    private float renderActionButton(MatrixStack matrix, String text, float x, float y, float w, int mouseX, int mouseY, Runnable action) {
        float btnH = 25f;
        boolean hovered = isHovered(mouseX, mouseY, x + 5, y, w - 10, btnH);

        int bgColor = hovered ? ColorUtil.getColor(60, 100, 160, 200) : ColorUtil.getColor(40, 70, 120, 160);

        RenderUtil.Rounded.roundedRect(matrix, x + 5, y, w - 10, btnH, bgColor, Round.of(4));

        if (hovered) {
            RenderUtil.Shadow.drawShadow(matrix, x + 5, y, w - 10, btnH, 6f, 0.6f,
                    ColorUtil.getColor(60, 100, 160), Round.of(4));
        }

        Fonts.SF_MEDIUM.drawCenter(matrix, text, x + w / 2, y + 8, ColorUtil.getColor(255, 255, 255), 9);

        return 30;
    }

    private void renderColorPickerPopup(MatrixStack matrix, int mouseX, int mouseY, ElementSettings settings) {
        float pickerW = 200;
        float pickerH = 150;
        float pickerX = mouseX - pickerW / 2;
        float pickerY = mouseY - pickerH - 10;

        RenderUtil.Rounded.roundedRect(matrix, pickerX, pickerY, pickerW, pickerH,
                ColorUtil.getColor(15, 20, 30, 240), Round.of(8));
        RenderUtil.Rounded.roundedOutline(matrix, pickerX, pickerY, pickerW, pickerH, 2f,
                ColorUtil.getColor(60, 80, 120), Round.of(8));

        Fonts.SF_MEDIUM.drawCenter(matrix, "Выбор цвета", pickerX + pickerW / 2, pickerY + 10,
                ColorUtil.getColor(255, 255, 255), 10);

        int currentColor = getColorForProperty(colorPickerProperty, settings);
        renderColorGrid(matrix, pickerX + 10, pickerY + 30, pickerW - 20, pickerH - 60, currentColor);

        float btnY = pickerY + pickerH - 25;
        renderColorButton(matrix, "Применить", pickerX + 10, btnY, 80, 20, ColorUtil.getColor(60, 140, 80));
        renderColorButton(matrix, "Отмена", pickerX + pickerW - 90, btnY, 80, 20, ColorUtil.getColor(140, 60, 60));
    }

    private void renderColorGrid(MatrixStack matrix, float x, float y, float w, float h, int currentColor) {
        int[] presetColors = {
                0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF,
                0xFF8000, 0x8000FF, 0x0080FF, 0x80FF00, 0xFF0080, 0x00FF80,
                0xFFFFFF, 0xC0C0C0, 0x808080, 0x404040, 0x000000, 0x800000,
                0x008000, 0x000080, 0x808000, 0x800080, 0x008080, 0xFFC0C0
        };

        float colorSize = 15f;
        float spacing = 2f;
        int colsPerRow = (int) ((w + spacing) / (colorSize + spacing));

        for (int i = 0; i < presetColors.length; i++) {
            int row = i / colsPerRow;
            int col = i % colsPerRow;

            float colorX = x + col * (colorSize + spacing);
            float colorY = y + row * (colorSize + spacing);

            if (colorX + colorSize > x + w) break;

            boolean selected = presetColors[i] == (currentColor & 0xFFFFFF);

            RenderUtil.Rounded.roundedRect(matrix, colorX, colorY, colorSize, colorSize,
                    presetColors[i] | 0xFF000000, Round.of(3));

            if (selected) {
                RenderUtil.Rounded.roundedOutline(matrix, colorX - 1, colorY - 1, colorSize + 2, colorSize + 2,
                        2f, ColorUtil.getColor(255, 255, 255), Round.of(4));
            }
        }
    }

    private void renderColorButton(MatrixStack matrix, String text, float x, float y, float w, float h, int color) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, color, Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(matrix, text, x + w / 2, y + h / 2 - 3, ColorUtil.getColor(255, 255, 255), 8);
    }

    private int getColorForProperty(String property, ElementSettings settings) {
        return switch (property) {
            case "textColor" -> settings.textColor;
            case "iconColor" -> settings.iconColor;
            default -> ColorUtil.getColor(255, 255, 255);
        };
    }

    public boolean handlePropertyClicks(HudCustomizeScreen.HudElement element, double mouseX, double mouseY, float panelX, float panelY, float panelW) {
        if (element == null) return false;

        ElementSettings settings = elementSettings.get(element.id);
        if (settings == null) return false;

        if (showColorPicker) {
            return handleColorPickerClicks(mouseX, mouseY, settings);
        }

        float currentY = panelY;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("width", mouseX, panelX + 5, panelW - 80, 30, 500, settings);
            updateElementSize(element, "width", settings);
            return true;
        }
        currentY += 25;

        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("height", mouseX, panelX + 5, panelW - 80, 15, 400, settings);
            updateElementSize(element, "height", settings);
            return true;
        }
        currentY += 35;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("scale", mouseX, panelX + 5, panelW - 80, 0.3f, 3.0f, settings);
            return true;
        }
        currentY += 25;

        if (isHovered((int) mouseX, (int) mouseY, panelX, currentY, panelW, 20)) {
            settings.enabled = !settings.enabled;
            return true;
        }
        currentY += 35;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("fontSize", mouseX, panelX + 5, panelW - 80, 4, 24, settings);
            return true;
        }
        currentY += 25;

        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY + 16, panelW - 10, 20)) {
            startTextEditing("displayText", settings);
            return true;
        }
        currentY += 50;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX + panelW - 65, currentY, 60, 16)) {
            showColorPicker = true;
            colorPickerProperty = "textColor";
            currentlyEditing = settings;
            return true;
        }
        currentY += 35;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("textOffsetX", mouseX, panelX + 5, panelW - 80, -100, 100, settings);
            return true;
        }
        currentY += 25;

        if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
            handleSliderClick("textOffsetY", mouseX, panelX + 5, panelW - 80, -100, 100, settings);
            return true;
        }
        currentY += 35;

        currentY += 30;
        if (isHovered((int) mouseX, (int) mouseY, panelX, currentY, panelW, 20)) {
            settings.showIcon = !settings.showIcon;
            return true;
        }
        currentY += 25;

        if (settings.showIcon) {
            if (isHovered((int) mouseX, (int) mouseY, panelX + panelW - 65, currentY, 60, 16)) {
                showColorPicker = true;
                colorPickerProperty = "iconColor";
                currentlyEditing = settings;
                return true;
            }
            currentY += 35;

            if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
                handleSliderClick("iconOffsetX", mouseX, panelX + 5, panelW - 80, -50, 50, settings);
                return true;
            }
            currentY += 25;

            if (isHovered((int) mouseX, (int) mouseY, panelX + 5, currentY, panelW - 80, 20)) {
                handleSliderClick("iconOffsetY", mouseX, panelX + 5, panelW - 80, -50, 50, settings);
                return true;
            }
        }

        return false;
    }

    private boolean handleColorPickerClicks(double mouseX, double mouseY, ElementSettings settings) {
        float pickerW = 200;
        float pickerH = 150;
        float pickerX = (float) mouseX - pickerW / 2;
        float pickerY = (float) mouseY - pickerH - 10;

        if (!isHovered((int) mouseX, (int) mouseY, pickerX, pickerY, pickerW, pickerH)) {
            showColorPicker = false;
            return true;
        }

        float gridX = pickerX + 10;
        float gridY = pickerY + 30;
        float gridW = pickerW - 20;
        float gridH = pickerH - 60;

        if (isHovered((int) mouseX, (int) mouseY, gridX, gridY, gridW, gridH)) {
            handleColorGridClick(mouseX, mouseY, gridX, gridY, settings);
            return true;
        }

        float btnY = pickerY + pickerH - 25;
        if (isHovered((int) mouseX, (int) mouseY, pickerX + 10, btnY, 80, 20)) {
            showColorPicker = false;
            return true;
        }

        if (isHovered((int) mouseX, (int) mouseY, pickerX + pickerW - 90, btnY, 80, 20)) {
            showColorPicker = false;
            return true;
        }

        return true;
    }

    private void handleColorGridClick(double mouseX, double mouseY, float gridX, float gridY, ElementSettings settings) {
        int[] presetColors = {
                0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF,
                0xFF8000, 0x8000FF, 0x0080FF, 0x80FF00, 0xFF0080, 0x00FF80,
                0xFFFFFF, 0xC0C0C0, 0x808080, 0x404040, 0x000000, 0x800000,
                0x008000, 0x000080, 0x808000, 0x800080, 0x008080, 0xFFC0C0
        };

        float colorSize = 15f;
        float spacing = 2f;
        int colsPerRow = (int) ((180 + spacing) / (colorSize + spacing));

        for (int i = 0; i < presetColors.length; i++) {
            int row = i / colsPerRow;
            int col = i % colsPerRow;

            float colorX = gridX + col * (colorSize + spacing);
            float colorY = gridY + row * (colorSize + spacing);

            if (isHovered((int) mouseX, (int) mouseY, colorX, colorY, colorSize, colorSize)) {
                applyColorToProperty(colorPickerProperty, presetColors[i] | 0xFF000000, settings);
                showColorPicker = false;
                break;
            }
        }
    }

    private void applyColorToProperty(String property, int color, ElementSettings settings) {
        switch (property) {
            case "textColor" -> settings.textColor = color;
            case "iconColor" -> settings.iconColor = color;
        }
    }

    private void handleSliderClick(String property, double mouseX, float sliderX, float sliderW, float min, float max, ElementSettings settings) {
        float clickPos = (float) mouseX - sliderX;
        float percentage = Math.max(0, Math.min(1, clickPos / sliderW));
        float value = min + (max - min) * percentage;

        applySliderValue(property, value, settings);
    }

    private void applySliderValue(String property, float value, ElementSettings settings) {
        switch (property) {
            case "scale" -> settings.scale = value;
            case "fontSize" -> settings.fontSize = value;
            case "textOffsetX" -> settings.textOffsetX = value;
            case "textOffsetY" -> settings.textOffsetY = value;
            case "iconOffsetX" -> settings.iconOffsetX = value;
            case "iconOffsetY" -> settings.iconOffsetY = value;
            case "width" -> settings.tempWidth = value;
            case "height" -> settings.tempHeight = value;
        }
    }

    private void updateElementSize(HudCustomizeScreen.HudElement element, String property, ElementSettings settings) {
        switch (property) {
            case "width" -> element.dragSetting.size.x = settings.tempWidth;
            case "height" -> element.dragSetting.size.y = settings.tempHeight;
        }
    }

    private void startTextEditing(String property, ElementSettings settings) {
        isEditingText = true;
        editingProperty = property;
        currentlyEditing = settings;
    }

    public boolean handleTextInput(HudCustomizeScreen.HudElement element, char character) {
        if (!isEditingText || currentlyEditing == null) return false;

        if (character >= 32 && character <= 126) {
            switch (editingProperty) {
                case "displayText" -> {
                    if (currentlyEditing.displayText.length() < 50) {
                        currentlyEditing.displayText += character;
                    }
                }
            }
            return true;
        }

        return false;
    }

    private void alignToCenter(HudCustomizeScreen.HudElement element) {
        element.dragSetting.position.x = (mc.getMainWindow().getScaledWidth() - element.dragSetting.size.x) / 2f;
        element.dragSetting.position.y = (mc.getMainWindow().getScaledHeight() - element.dragSetting.size.y) / 2f;
    }

    private void copySettings(ElementSettings settings) {

    }

    private void pasteSettings(ElementSettings settings) {

    }

    private boolean isHovered(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static class ElementSettings {
        public final String id;
        public String displayText;
        public float scale = 1.0f;
        public float fontSize = 10f;
        public int textColor = ColorUtil.getColor(255, 255, 255);
        public int iconColor = ColorUtil.getColor(100, 150, 255);
        public float textOffsetX = 0f;
        public float textOffsetY = 0f;
        public float iconOffsetX = 0f;
        public float iconOffsetY = 0f;
        public boolean showIcon = true;
        public boolean enabled = true;
        public float tempWidth = 100f;
        public float tempHeight = 50f;

        public ElementSettings(String id, String defaultText) {
            this.id = id;
            this.displayText = defaultText;
        }

        public void reset() {
            scale = 1.0f;
            fontSize = 10f;
            textColor = ColorUtil.getColor(255, 255, 255);
            iconColor = ColorUtil.getColor(100, 150, 255);
            textOffsetX = 0f;
            textOffsetY = 0f;
            iconOffsetX = 0f;
            iconOffsetY = 0f;
            showIcon = true;
            enabled = true;
            tempWidth = 100f;
            tempHeight = 50f;
        }

        public void loadProperty(String property, String value) {
            try {
                switch (property) {
                    case "displayText" -> displayText = value;
                    case "scale" -> scale = Float.parseFloat(value);
                    case "fontSize" -> fontSize = Float.parseFloat(value);
                    case "textColor" -> textColor = Integer.parseInt(value);
                    case "iconColor" -> iconColor = Integer.parseInt(value);
                    case "textOffsetX" -> textOffsetX = Float.parseFloat(value);
                    case "textOffsetY" -> textOffsetY = Float.parseFloat(value);
                    case "iconOffsetX" -> iconOffsetX = Float.parseFloat(value);
                    case "iconOffsetY" -> iconOffsetY = Float.parseFloat(value);
                    case "showIcon" -> showIcon = Boolean.parseBoolean(value);
                    case "enabled" -> enabled = Boolean.parseBoolean(value);
                    case "tempWidth" -> tempWidth = Float.parseFloat(value);
                    case "tempHeight" -> tempHeight = Float.parseFloat(value);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        public void saveToProperties(Properties props, String elementId) {
            props.setProperty(elementId + ".displayText", displayText);
            props.setProperty(elementId + ".scale", String.valueOf(scale));
            props.setProperty(elementId + ".fontSize", String.valueOf(fontSize));
            props.setProperty(elementId + ".textColor", String.valueOf(textColor));
            props.setProperty(elementId + ".iconColor", String.valueOf(iconColor));
            props.setProperty(elementId + ".textOffsetX", String.valueOf(textOffsetX));
            props.setProperty(elementId + ".textOffsetY", String.valueOf(textOffsetY));
            props.setProperty(elementId + ".iconOffsetX", String.valueOf(iconOffsetX));
            props.setProperty(elementId + ".iconOffsetY", String.valueOf(iconOffsetY));
            props.setProperty(elementId + ".showIcon", String.valueOf(showIcon));
            props.setProperty(elementId + ".enabled", String.valueOf(enabled));
            props.setProperty(elementId + ".tempWidth", String.valueOf(tempWidth));
            props.setProperty(elementId + ".tempHeight", String.valueOf(tempHeight));
        }
    }
}