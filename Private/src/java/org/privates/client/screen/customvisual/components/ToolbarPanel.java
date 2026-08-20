package org.privates.client.screen.customvisual.components;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.customvisual.CustomVisualScreen;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
public class ToolbarPanel {
    private final CustomVisualScreen screen;

    // Размеры тулбара
    private static final float TOOLBAR_HEIGHT = 40f;
    private static final float BUTTON_SIZE = 30f;
    private static final float BUTTON_SPACING = 5f;
    private static final float TOOLBAR_PADDING = 5f;

    public ToolbarPanel(CustomVisualScreen screen) {
        this.screen = screen;
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        float sidebarWidth = screen.isSidebarOpen() ? 280f * screen.getSidebarAnimation().get() : 0;
        float toolbarX = sidebarWidth;
        float toolbarWidth = screen.getWidth() - sidebarWidth;

        // Фон тулбара
        RenderUtil.Rounded.roundedRect(matrix, toolbarX, 0, toolbarWidth, TOOLBAR_HEIGHT,
                ColorUtil.getColor(35, 35, 45, 200), Round.of(0, 0, 6, 6));

        // Разделительная линия
        RenderUtil.Rounded.roundedRect(matrix, toolbarX, TOOLBAR_HEIGHT - 1, toolbarWidth, 1,
                ColorUtil.getColor(60, 60, 80), Round.of(0));

        // Заголовок
        String title = "Visual Editor";
        float titleX = toolbarX + TOOLBAR_PADDING;
        Fonts.SF_MEDIUM.draw(matrix, title, titleX, TOOLBAR_HEIGHT / 2 - 4,
                ColorUtil.getColor(255, 255, 255), 10);

        // Кнопки справа
        float buttonX = toolbarX + toolbarWidth - TOOLBAR_PADDING;

        // Кнопка "Save & Close"
        buttonX -= 100;
        renderToolbarButton(matrix, "Save & Close", buttonX, TOOLBAR_PADDING, 100, BUTTON_SIZE,
                mouseX, mouseY, ColorUtil.getColor(40, 167, 69), "save_close");

        // Кнопка "Reset Positions"
        buttonX -= 110;
        renderToolbarButton(matrix, "Reset Positions", buttonX, TOOLBAR_PADDING, 105, BUTTON_SIZE,
                mouseX, mouseY, ColorUtil.getColor(255, 193, 7), "reset_positions");

        // Кнопка "Grid Snap"
        buttonX -= 80;
        renderToolbarButton(matrix, "Grid Snap", buttonX, TOOLBAR_PADDING, 75, BUTTON_SIZE,
                mouseX, mouseY, ColorUtil.getColor(108, 117, 125), "grid_snap");

        // Кнопка переключения сайдбара
        buttonX -= 40;
        String sidebarIcon = screen.isSidebarOpen() ? "◀" : "▶";
        renderToolbarButton(matrix, sidebarIcon, buttonX, TOOLBAR_PADDING, 35, BUTTON_SIZE,
                mouseX, mouseY, Theme.getInstance().clientColor(), "toggle_sidebar");
    }

    private void renderToolbarButton(MatrixStack matrix, String text, float x, float y, float width, float height,
                                     int mouseX, int mouseY, int baseColor, String buttonId) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Цвет кнопки
        int buttonColor = hovered ? ColorUtil.multAlpha(baseColor, 0.8f) : ColorUtil.multAlpha(baseColor, 0.6f);

        // Фон кнопки
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, buttonColor, Round.of(4));

        // Граница при наведении
        if (hovered) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 1f,
                    ColorUtil.multAlpha(baseColor, 1f), Round.of(4));
        }

        // Текст кнопки
        float textWidth = Fonts.SF_MEDIUM.getWidth(text, 8);
        float textX = x + (width - textWidth) / 2;
        float textY = y + height / 2 - 4;

        // Тень для текста
        Fonts.SF_MEDIUM.draw(matrix, text, textX + 0.5f, textY + 0.5f,
                ColorUtil.getColor(0, 0, 0, 100), 8);
        Fonts.SF_MEDIUM.draw(matrix, text, textX, textY,
                ColorUtil.getColor(255, 255, 255), 8);

        // Дополнительные визуальные эффекты для специальных кнопок
        switch (buttonId) {
            case "grid_snap":
                // Иконка сетки
                renderGridIcon(matrix, x + width - 20, y + 5, 12);
                break;
            case "save_close":
                // Иконка сохранения
                Fonts.HUD.draw(matrix, "💾", x + 5, y + height / 2 - 6,
                        ColorUtil.getColor(255, 255, 255), 10);
                break;
            case "reset_positions":
                // Иконка сброса
                Fonts.HUD.draw(matrix, "🔄", x + 5, y + height / 2 - 6,
                        ColorUtil.getColor(255, 255, 255), 10);
                break;
        }
    }

    private void renderGridIcon(MatrixStack matrix, float x, float y, float size) {
        int gridColor = ColorUtil.getColor(255, 255, 255, 150);
        float cellSize = size / 3;

        // Рисуем мини-сетку
        for (int i = 0; i <= 3; i++) {
            // Вертикальные линии
            RenderUtil.Rounded.roundedRect(matrix, x + i * cellSize, y, 0.5f, size, gridColor, Round.of(0));
            // Горизонтальные линии
            RenderUtil.Rounded.roundedRect(matrix, x, y + i * cellSize, size, 0.5f, gridColor, Round.of(0));
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || mouseY > TOOLBAR_HEIGHT) return false;

        float sidebarWidth = screen.isSidebarOpen() ? 280f * screen.getSidebarAnimation().get() : 0;
        float toolbarX = sidebarWidth;
        float toolbarWidth = screen.getWidth() - sidebarWidth;

        if (mouseX < toolbarX) return false;

        float buttonX = toolbarX + toolbarWidth - TOOLBAR_PADDING;

        // Проверяем клик по кнопке "Save & Close"
        buttonX -= 100;
        if (isHovered(mouseX, mouseY, buttonX, TOOLBAR_PADDING, 100, BUTTON_SIZE)) {
            screen.onClose();
            return true;
        }

        // Проверяем клик по кнопке "Reset Positions"
        buttonX -= 110;
        if (isHovered(mouseX, mouseY, buttonX, TOOLBAR_PADDING, 105, BUTTON_SIZE)) {
            screen.resetElementPositions();
            return true;
        }

        // Проверяем клик по кнопке "Grid Snap"
        buttonX -= 80;
        if (isHovered(mouseX, mouseY, buttonX, TOOLBAR_PADDING, 75, BUTTON_SIZE)) {
            snapElementsToGrid();
            return true;
        }

        // Проверяем клик по кнопке переключения сайдбара
        buttonX -= 40;
        if (isHovered(mouseX, mouseY, buttonX, TOOLBAR_PADDING, 35, BUTTON_SIZE)) {
            screen.toggleSidebar();
            return true;
        }

        return false;
    }

    private void snapElementsToGrid() {
        int gridSize = 10; // Размер сетки

        // Привязываем все HUD элементы к сетке
        for (HudElementWrapper element : screen.getAllHudElements()) {
            element.snapToGrid(gridSize);
        }

        // Привязываем все кастомные элементы к сетке
        for (CustomizableElement element : screen.getAllCustomElements()) {
            element.snapToGrid(gridSize);
        }
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // Дополнительные утилитарные методы для тулбара
    public void showTooltip(MatrixStack matrix, String text, float x, float y) {
        float tooltipWidth = Fonts.SF_MEDIUM.getWidth(text, 8) + 8;
        float tooltipHeight = 16;

        // Фон тултипа
        RenderUtil.Rounded.roundedRect(matrix, x, y - tooltipHeight - 5, tooltipWidth, tooltipHeight,
                ColorUtil.getColor(0, 0, 0, 200), Round.of(4));

        // Текст тултипа
        Fonts.SF_MEDIUM.draw(matrix, text, x + 4, y - tooltipHeight + 4,
                ColorUtil.getColor(255, 255, 255), 8);
    }

    public void renderStatusBar(MatrixStack matrix) {
        float sidebarWidth = screen.isSidebarOpen() ? 280f * screen.getSidebarAnimation().get() : 0;
        float statusBarX = sidebarWidth;
        float statusBarWidth = screen.getWidth() - sidebarWidth;
        float statusBarY = screen.getHeight() - 20;
        float statusBarHeight = 20;

        // Фон статус-бара
        RenderUtil.Rounded.roundedRect(matrix, statusBarX, statusBarY, statusBarWidth, statusBarHeight,
                ColorUtil.getColor(25, 25, 35, 180), Round.of(6, 6, 0, 0));

        // Информация о выбранном элементе
        HudElementWrapper selectedElement = screen.getSelectedElement();
        if (selectedElement != null) {
            String info = String.format("Selected: %s | Position: (%.0f, %.0f) | Size: (%.0f, %.0f)",
                    selectedElement.getDisplayName(), selectedElement.getX(), selectedElement.getY(),
                    selectedElement.getWidth(), selectedElement.getHeight());

            Fonts.SF_MEDIUM.draw(matrix, info, statusBarX + 10, statusBarY + 6,
                    ColorUtil.getColor(200, 200, 200), 7);
        } else {
            String info = String.format("Elements: %d | Click to select, Right-click for properties",
                    screen.getAllHudElements().size());

            Fonts.SF_MEDIUM.draw(matrix, info, statusBarX + 10, statusBarY + 6,
                    ColorUtil.getColor(150, 150, 150), 7);
        }

        // Информация о производительности справа
        String perfInfo = String.format("FPS: %d", Minecraft.getDebugFPS());
        float perfWidth = Fonts.SF_MEDIUM.getWidth(perfInfo, 7);
        Fonts.SF_MEDIUM.draw(matrix, perfInfo, statusBarX + statusBarWidth - perfWidth - 10, statusBarY + 6,
                ColorUtil.getColor(100, 200, 100), 7);
    }
}