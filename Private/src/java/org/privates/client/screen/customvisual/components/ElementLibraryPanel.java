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

import java.util.Arrays;
import java.util.List;

@Getter
public class ElementLibraryPanel {
    private final CustomVisualScreen screen;
    private float scrollOffset = 0;
    private float maxScrollOffset = 0;

    // Размеры панели
    private static final float PANEL_WIDTH = 260f;
    private static final float PANEL_PADDING = 10f;
    private static final float CATEGORY_HEIGHT = 30f;
    private static final float ELEMENT_HEIGHT = 35f;
    private static final float ELEMENT_SPACING = 3f;

    public ElementLibraryPanel(CustomVisualScreen screen) {
        this.screen = screen;
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        float panelY = 50f;
        float panelHeight = screen.height - panelY - 20f;

        // Заголовок панели
        String title = "Element Library";
        Fonts.SF_MEDIUM.draw(matrix, title, PANEL_PADDING, 20,
                ColorUtil.getColor(255, 255, 255), 9);

        // Разделительная линия
        RenderUtil.Rounded.roundedRect(matrix, PANEL_PADDING, 35, PANEL_WIDTH - PANEL_PADDING * 2, 1,
                ColorUtil.getColor(60, 60, 80), Round.of(0.5f));

        // Включаем обрезку для скролла
        enableScissor(0, panelY, PANEL_WIDTH, panelHeight);

        // Рендерим категории и элементы
        renderCategories(matrix, mouseX, mouseY, panelY);

        disableScissor();

        // Рендерим скроллбар если нужен
        if (maxScrollOffset > 0) {
            renderScrollbar(matrix, PANEL_WIDTH - 8, panelY, panelHeight);
        }
    }

    private void renderCategories(MatrixStack matrix, int mouseX, int mouseY, float startY) {
        float currentY = startY + scrollOffset;

        // Группируем элементы по категориям
        ElementType.ElementCategory[] categories = ElementType.ElementCategory.values();

        for (ElementType.ElementCategory category : categories) {
            // Рендерим заголовок категории
            renderCategoryHeader(matrix, category, PANEL_PADDING, currentY,
                    PANEL_WIDTH - PANEL_PADDING * 2, CATEGORY_HEIGHT);
            currentY += CATEGORY_HEIGHT + ELEMENT_SPACING;

            // Получаем элементы этой категории
            List<ElementType> categoryElements = Arrays.stream(ElementType.values())
                    .filter(type -> type.getCategory() == category)
                    .toList();

            // Рендерим элементы категории
            for (ElementType elementType : categoryElements) {
                if (currentY + ELEMENT_HEIGHT > startY && currentY < startY + screen.height - 120) {
                    renderElementItem(matrix, elementType, PANEL_PADDING, currentY,
                            PANEL_WIDTH - PANEL_PADDING * 2, ELEMENT_HEIGHT, mouseX, mouseY);
                }
                currentY += ELEMENT_HEIGHT + ELEMENT_SPACING;
            }

            currentY += 10; // Дополнительный отступ между категориями
        }

        // Вычисляем максимальный скролл
        float totalHeight = currentY - startY - scrollOffset;
        float availableHeight = screen.height - 120f;
        maxScrollOffset = Math.max(0, totalHeight - availableHeight);
    }

    private void renderCategoryHeader(MatrixStack matrix, ElementType.ElementCategory category,
                                      float x, float y, float width, float height) {
        // Фон категории
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                ColorUtil.getColor(50, 50, 60, 180), Round.of(4));

        // Название категории
        Fonts.SF_MEDIUM.draw(matrix, category.getDisplayName(), x + 10, y + height / 2 - 4,
                Theme.getInstance().clientColor(), 9);
    }

    private void renderElementItem(MatrixStack matrix, ElementType elementType,
                                   float x, float y, float width, float height, int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон элемента
        int bgColor = hovered ? ColorUtil.getColor(255, 255, 255, 30) : ColorUtil.getColor(40, 40, 50, 120);
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(4));

        // Граница при наведении
        if (hovered) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 1f,
                    Theme.getInstance().clientColor(), Round.of(4));
        }

        // Иконка элемента
        String icon = elementType.getIcon();
        Fonts.HUD.draw(matrix, icon, x + 8, y + height / 2 - 6,
                Theme.getInstance().clientColor(), 12);

        // Название элемента
        Fonts.SF_MEDIUM.draw(matrix, elementType.getDisplayName(), x + 30, y + 8,
                ColorUtil.getColor(255, 255, 255), 8);

        // Описание элемента
        String description = elementType.getDescription();
        if (description.length() > 30) {
            description = description.substring(0, 27) + "...";
        }
        Fonts.SF_MEDIUM.draw(matrix, description, x + 30, y + 20,
                ColorUtil.getColor(150, 150, 150), 7);

        // Индикаторы свойств элемента
        renderElementIndicators(matrix, elementType, x + width - 60, y + 5, 55, height - 10);
    }

    private void renderElementIndicators(MatrixStack matrix, ElementType elementType,
                                         float x, float y, float width, float height) {
        float indicatorSize = 6f;
        float currentX = x;

        // Индикатор "Кастомный" (можно удалить)
        if (elementType.isCustom()) {
            RenderUtil.Rounded.roundedRect(matrix, currentX, y + 2, indicatorSize, indicatorSize,
                    ColorUtil.getColor(255, 193, 7), Round.of(3)); // Желтый
            currentX += indicatorSize + 3;
        }

        // Индикатор "Изменяемый размер"
        if (elementType.isResizable()) {
            RenderUtil.Rounded.roundedRect(matrix, currentX, y + 2, indicatorSize, indicatorSize,
                    ColorUtil.getColor(40, 167, 69), Round.of(3)); // Зеленый
            currentX += indicatorSize + 3;
        }

        // Индикатор "Интерактивный"
        if (elementType.isInteractive()) {
            RenderUtil.Rounded.roundedRect(matrix, currentX, y + 2, indicatorSize, indicatorSize,
                    ColorUtil.getColor(0, 123, 255), Round.of(3)); // Синий
            currentX += indicatorSize + 3;
        }

        // Легенда внизу (маленькими буквами)
        if (elementType.isCustom()) {
            Fonts.SF_MEDIUM.draw(matrix, "C", x, y + height - 10,
                    ColorUtil.getColor(255, 193, 7), 5);
        }
        if (elementType.isResizable()) {
            Fonts.SF_MEDIUM.draw(matrix, "R", x + (elementType.isCustom() ? 8 : 0), y + height - 10,
                    ColorUtil.getColor(40, 167, 69), 5);
        }
        if (elementType.isInteractive()) {
            int offset = (elementType.isCustom() ? 8 : 0) + (elementType.isResizable() ? 8 : 0);
            Fonts.SF_MEDIUM.draw(matrix, "I", x + offset, y + height - 10,
                    ColorUtil.getColor(0, 123, 255), 5);
        }
    }

    private void renderScrollbar(MatrixStack matrix, float x, float y, float height) {
        if (maxScrollOffset <= 0) return;

        float scrollbarHeight = 4f;
        float scrollProgress = -scrollOffset / maxScrollOffset;
        float thumbHeight = Math.max(20f, height * (height / (height + maxScrollOffset)));
        float thumbY = y + scrollProgress * (height - thumbHeight);

        // Фон скроллбара
        RenderUtil.Rounded.roundedRect(matrix, x, y, scrollbarHeight, height,
                ColorUtil.getColor(60, 60, 80, 100), Round.of(2));

        // Ползунок
        RenderUtil.Rounded.roundedRect(matrix, x, thumbY, scrollbarHeight, thumbHeight,
                Theme.getInstance().clientColor(), Round.of(2));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || mouseX > PANEL_WIDTH) return false;

        float panelY = 50f;
        float currentY = panelY + scrollOffset;

        ElementType.ElementCategory[] categories = ElementType.ElementCategory.values();

        for (ElementType.ElementCategory category : categories) {
            currentY += CATEGORY_HEIGHT + ELEMENT_SPACING; // Пропускаем заголовок

            // Получаем элементы этой категории
            List<ElementType> categoryElements = Arrays.stream(ElementType.values())
                    .filter(type -> type.getCategory() == category)
                    .toList();

            // Проверяем клики по элементам
            for (ElementType elementType : categoryElements) {
                if (currentY + ELEMENT_HEIGHT > panelY && currentY < panelY + screen.height - 120) {
                    if (isHovered(mouseX, mouseY, PANEL_PADDING, currentY,
                            PANEL_WIDTH - PANEL_PADDING * 2, ELEMENT_HEIGHT)) {
                        // Добавляем новый элемент
                        screen.addCustomElement(elementType);
                        return true;
                    }
                }
                currentY += ELEMENT_HEIGHT + ELEMENT_SPACING;
            }

            currentY += 10; // Дополнительный отступ между категориями
        }

        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX <= PANEL_WIDTH) {
            scrollOffset += delta * 10f;
            scrollOffset = Math.max(-maxScrollOffset, Math.min(0, scrollOffset));
            return true;
        }
        return false;
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void enableScissor(float x, float y, float width, float height) {
        double scale = Minecraft.getInstance().getMainWindow().getScaleFactor();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(
                (int) (x * scale),
                (int) ((Minecraft.getInstance().getMainWindow().getScaledHeight() - (y + height)) * scale),
                (int) (width * scale),
                (int) (height * scale)
        );
    }

    private void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
}