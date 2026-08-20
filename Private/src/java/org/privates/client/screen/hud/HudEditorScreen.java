package org.privates.client.screen.hud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;

public class HudEditorScreen extends Screen {
    private final Hud hudModule;
    private final List<HudElement> hudElements;
    private HudElement selectedElement = null;
    private boolean dragging = false;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    public HudEditorScreen() {
        super(new StringTextComponent("HUD Editor"));
        this.hudModule = Hud.getInstance();
        this.hudElements = new ArrayList<>();

        // Инициализируем все HUD элементы
        initializeHudElements();
    }

    private void initializeHudElements() {
        // Добавляем все доступные HUD элементы

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Рендерим полупрозрачный фон
        this.fillGradient(matrixStack, 0, 0, this.width, this.height,
                ColorUtil.getColor(0, 0, 0, 100), ColorUtil.getColor(0, 0, 0, 50));

        // Рендерим заголовок
        String title = "HUD Editor - Перетаскивайте элементы для их позиционирования";
        int titleWidth = (int) Fonts.SF_MEDIUM.getWidth(title, 12);
        Fonts.SF_MEDIUM.draw(matrixStack, title, (this.width - titleWidth) / 2f, 10, ColorUtil.WHITE, 12);

        // Рендерим инструкции
        String instruction1 = "ЛКМ - выбрать и перетащить элемент";
        String instruction2 = "ПКМ - сбросить позицию элемента";
        String instruction3 = "ESC - выйти из редактора";

        Fonts.SF_MEDIUM.draw(matrixStack, instruction1, 10, this.height - 60, ColorUtil.LIGHT_GRAY, 9);
        Fonts.SF_MEDIUM.draw(matrixStack, instruction2, 10, this.height - 45, ColorUtil.LIGHT_GRAY, 9);
        Fonts.SF_MEDIUM.draw(matrixStack, instruction3, 10, this.height - 30, ColorUtil.LIGHT_GRAY, 9);

        // Рендерим все HUD элементы
        for (HudElement element : hudElements) {
            renderHudElement(matrixStack, element, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderHudElement(MatrixStack matrixStack, HudElement element, int mouseX, int mouseY) {
        DragSetting drag = element.dragSetting;
        float x = drag.position.x;
        float y = drag.position.y;
        float width = element.width;
        float height = element.height;

        // Проверяем, наведена ли мышь на элемент
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        boolean selected = element == selectedElement;

        // Цвета для разных состояний
        int borderColor;
        int backgroundColor;

        if (selected) {
            borderColor = ColorUtil.getColor(0, 255, 0, 200); // Зеленый для выбранного
            backgroundColor = ColorUtil.getColor(0, 255, 0, 30);
        } else if (hovered) {
            borderColor = ColorUtil.getColor(255, 255, 0, 150); // Желтый при наведении
            backgroundColor = ColorUtil.getColor(255, 255, 0, 20);
        } else {
            borderColor = ColorUtil.getColor(255, 255, 255, 100); // Белый по умолчанию
            backgroundColor = ColorUtil.getColor(255, 255, 255, 10);
        }

        // Рендерим фон элемента
        RectUtil.drawRect(matrixStack, x, y, width, height, backgroundColor);

        // Рендерим рамку
        drawBorder(matrixStack, x, y, width, height, borderColor, 1);

        // Рендерим название элемента
        String name = element.name;
        float nameWidth = Fonts.SF_MEDIUM.getWidth(name, 8);
        float nameX = x + (width - nameWidth) / 2f;
        float nameY = y + 2;

        // Фон для текста
        RectUtil.drawRect(matrixStack, nameX - 2, nameY - 1, nameWidth + 4, 10,
                ColorUtil.getColor(0, 0, 0, 150));

        Fonts.SF_MEDIUM.draw(matrixStack, name, nameX, nameY, ColorUtil.WHITE, 8);

        // Рендерим описание элемента (если есть место)
        if (height > 25) {
            String description = element.description;
            float descWidth = Fonts.SF_MEDIUM.getWidth(description, 6);
            if (descWidth <= width - 4) {
                float descX = x + (width - descWidth) / 2f;
                float descY = y + height - 10;
                Fonts.SF_MEDIUM.draw(matrixStack, description, descX, descY, ColorUtil.LIGHT_GRAY, 6);
            }
        }

        // Рендерим координаты для выбранного элемента
        if (selected) {
            String coords = String.format("X: %.0f, Y: %.0f", x, y);
            float coordsWidth = Fonts.SF_MEDIUM.getWidth(coords, 7);
            float coordsX = x + (width - coordsWidth) / 2f;
            float coordsY = y + height + 5;

            // Фон для координат
            RectUtil.drawRect(matrixStack, coordsX - 2, coordsY - 1, coordsWidth + 4, 9,
                    ColorUtil.getColor(0, 0, 0, 180));

            Fonts.SF_MEDIUM.draw(matrixStack, coords, coordsX, coordsY, ColorUtil.YELLOW, 7);
        }
    }

    private void drawBorder(MatrixStack matrixStack, float x, float y, float width, float height, int color, float thickness) {
        // Верхняя линия
        RectUtil.drawRect(matrixStack, x, y, width, thickness, color);
        // Нижняя линия
        RectUtil.drawRect(matrixStack, x, y + height - thickness, width, thickness, color);
        // Левая линия
        RectUtil.drawRect(matrixStack, x, y, thickness, height, color);
        // Правая линия
        RectUtil.drawRect(matrixStack, x + width - thickness, y, thickness, height, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // ЛКМ
            // Ищем элемент под курсором
            for (HudElement element : hudElements) {
                DragSetting drag = element.dragSetting;
                float x = drag.position.x;
                float y = drag.position.y;
                float width = element.width;
                float height = element.height;

                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    selectedElement = element;
                    dragging = true;
                    dragOffsetX = mouseX - x;
                    dragOffsetY = mouseY - y;
                    return true;
                }
            }

            // Если не попали ни в один элемент, снимаем выделение
            selectedElement = null;
        } else if (button == 1 && selectedElement != null) { // ПКМ - сброс позиции
            resetElementPosition(selectedElement);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && selectedElement != null) {
            DragSetting drag = selectedElement.dragSetting;

            // Вычисляем новую позицию
            float newX = (float) (mouseX - dragOffsetX);
            float newY = (float) (mouseY - dragOffsetY);

            // Ограничиваем позицию границами экрана
            newX = Math.max(0, Math.min(newX, this.width - selectedElement.width));
            newY = Math.max(0, Math.min(newY, this.height - selectedElement.height));

            // Обновляем позицию в DragSetting
            drag.position.x = newX;
            drag.position.y = newY;

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC - закрыть редактор
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.onClose();
            return true;
        }

        // R - сбросить позицию выбранного элемента
        if (keyCode == 82 && selectedElement != null) { // GLFW.GLFW_KEY_R
            resetElementPosition(selectedElement);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void resetElementPosition(HudElement element) {
        DragSetting drag = element.dragSetting;

        // Сбрасываем в начальные позиции (можно настроить под свои нужды)
        switch (element.name) {
            case "Watermark":
                drag.position.x = 5;
                drag.position.y = 5;
                break;
            case "ArrayList":
                drag.position.x = this.width - element.width - 5;
                drag.position.y = 5;
                break;
            case "TargetHud":
                drag.position.x = (this.width - element.width) / 2f;
                drag.position.y = this.height - element.height - 80;
                break;
            case "Information":
                drag.position.x = 5;
                drag.position.y = this.height - element.height - 5;
                break;
            case "Minimap":
                drag.position.x = this.width - element.width - 5;
                drag.position.y = this.height - element.height - 5;
                break;
            default:
                // Позиция по умолчанию
                drag.position.x = 50 + (hudElements.indexOf(element) * 20);
                drag.position.y = 50 + (hudElements.indexOf(element) * 15);
                break;
        }
    }

    @Override
    public void onClose() {
        // Сохраняем настройки и закрываем экран
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Не ставим игру на паузу
    }

    // Внутренний класс для представления HUD элемента
    private static class HudElement {
        public final String name;
        public final DragSetting dragSetting;
        public final String description;
        public final float width;
        public final float height;

        public HudElement(String name, DragSetting dragSetting, String description, float width, float height) {
            this.name = name;
            this.dragSetting = dragSetting;
            this.description = description;
            this.width = width;
            this.height = height;
        }
    }
}