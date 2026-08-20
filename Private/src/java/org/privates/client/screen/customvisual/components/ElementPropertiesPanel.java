package org.privates.client.screen.customvisual.components;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.customvisual.CustomVisualScreen;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ElementPropertiesPanel {
    private final CustomVisualScreen screen;
    private final List<PropertyComponent> propertyComponents = new ArrayList<>();
    private float scrollOffset = 0;
    private float maxScrollOffset = 0;
    private boolean isEditingProperty = false; // Флаг для предотвращения закрытия при редактировании

    // Размеры панели
    private static final float PANEL_WIDTH = 260f;
    private static final float PANEL_PADDING = 10f;
    private static final float COMPONENT_HEIGHT = 25f;
    private static final float COMPONENT_SPACING = 5f;

    public ElementPropertiesPanel(CustomVisualScreen screen) {
        this.screen = screen;
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY) {
        HudElementWrapper element = screen.getSelectedElement();
        if (element == null) return;

        float panelY = 50f;
        float panelHeight = screen.height - panelY - 20f;

        // Заголовок панели
        String title = "Properties: " + element.getDisplayName();
        Fonts.SF_MEDIUM.draw(matrix, title, PANEL_PADDING, 20,
                ColorUtil.getColor(255, 255, 255), 9);

        // Разделительная линия
        RenderUtil.Rounded.roundedRect(matrix, PANEL_PADDING, 35, PANEL_WIDTH - PANEL_PADDING * 2, 1,
                ColorUtil.getColor(60, 60, 80), Round.of(0.5f));

        // Обновляем компоненты свойств только если не редактируем
        if (!isEditingProperty) {
            updatePropertyComponents(element);
        }

        // Включаем обрезку для скролла
        enableScissor(0, panelY, PANEL_WIDTH, panelHeight);

        // Рендерим компоненты свойств
        renderPropertyComponents(matrix, mouseX, mouseY, panelY);

        disableScissor();

        // Рендерим скроллбар если нужен
        if (maxScrollOffset > 0) {
            renderScrollbar(matrix, PANEL_WIDTH - 8, panelY, panelHeight);
        }

        // Кнопки действий внизу панели
        renderActionButtons(matrix, mouseX, mouseY, screen.height - 60);
    }

    private void updatePropertyComponents(HudElementWrapper element) {
        propertyComponents.clear();

        // Основные свойства (позиция и размер)
        propertyComponents.add(new CategoryComponent("Position & Size"));
        propertyComponents.add(new NumberPropertyComponent("X",
                () -> element.getX(),
                (value) -> element.setX(value),
                0, screen.width));
        propertyComponents.add(new NumberPropertyComponent("Y",
                () -> element.getY(),
                (value) -> element.setY(value),
                0, screen.height));

        if (element.isResizable()) {
            propertyComponents.add(new NumberPropertyComponent("Width",
                    () -> element.getWidth(),
                    (value) -> element.setWidth(value),
                    20, 500));
            propertyComponents.add(new NumberPropertyComponent("Height",
                    () -> element.getHeight(),
                    (value) -> element.setHeight(value),
                    10, 300));
        }

        // Общие свойства
        propertyComponents.add(new CategoryComponent("General"));
        propertyComponents.add(new BooleanPropertyComponent("Visible",
                () -> element.isVisible(),
                (value) -> {
                    element.setVisible(value);
                    // Обновляем видимость в HUD модуле
                }));
        propertyComponents.add(new BooleanPropertyComponent("Locked",
                () -> element.isLocked(),
                (value) -> element.setLocked(value)));


        // Специфичные свойства в зависимости от типа элемента
        addTypeSpecificProperties(element);

        // Вычисляем максимальный скролл
        float totalHeight = propertyComponents.size() * (COMPONENT_HEIGHT + COMPONENT_SPACING);
        float availableHeight = screen.height - 120f;
        maxScrollOffset = Math.max(0, totalHeight - availableHeight);
    }

    private boolean hasBlurSupport(HudElementWrapper element) {
        // Проверяем, поддерживает ли элемент blur
        String elementId = element.getId();
        return switch (elementId) {
            case "watermark", "watermark2", "arraylist", "targethud",
                 "keybinds", "potions", "information", "armorhud",
                 "minimap", "inventoryhud", "cooldowns", "scoreboard",
                 "stafflist", "itemassist", "keystroke", "nearbyplayers",
                 "motiongraph", "totemcount", "assist", "armorhud2" -> true;
            default -> false;
        };
    }

    private void addTypeSpecificProperties(HudElementWrapper element) {
        String elementId = element.getId();

        switch (elementId) {
            case "watermark":
            case "watermark2":
                addWatermarkProperties(element);
                break;

            case "arraylist":
                addArrayListProperties(element);
                break;

            case "targethud":
                addTargetHudProperties(element);
                break;

            case "keybinds":
                addKeybindsProperties(element);
                break;

            case "potions":
                addPotionsProperties(element);
                break;

            case "information":
                addInformationProperties(element);
                break;

            case "armorhud":
            case "armorhud2":
                addArmorHudProperties(element);
                break;

            case "minimap":
                addMinimapProperties(element);
                break;

            case "inventoryhud":
                addInventoryHudProperties(element);
                break;

            case "cooldowns":
                addCooldownsProperties(element);
                break;

            case "totemcount":
                addTotemCountProperties(element);
                break;
        }
    }

    private void addWatermarkProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Watermark Settings"));
        // Здесь можно добавить специфичные настройки для watermark
        // Например, цвет, размер шрифта, стиль и т.д.
        // propertyComponents.add(new ColorPropertyComponent("Text Color", ...)); // Удалено, т.к. нет методов
        // propertyComponents.add(new ColorPropertyComponent("Background Color", ...)); // Удалено
        // propertyComponents.add(new NumberPropertyComponent("Alpha", ...)); // Удалено
    }

    private void addArrayListProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("ArrayList Settings"));
        // propertyComponents.add(new ColorPropertyComponent("Text Color", ...)); // Удалено
        // propertyComponents.add(new ColorPropertyComponent("Background Color", ...)); // Удалено
        // propertyComponents.add(new NumberPropertyComponent("Alpha", ...)); // Удалено
    }

    private void addTargetHudProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Target HUD Settings"));
        // Настройки для TargetHudRenderer
        // Показ частиц, анимации здоровья и т.д.
    }

    private void addKeybindsProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Keybinds Settings"));
        // Настройки для KeybindsRenderer
        // Стиль отображения, фильтры и т.д.
    }

    private void addPotionsProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Potions Settings"));
        // Настройки для PotionsRenderer
        // Показ иконок, длительности и т.д.
    }

    private void addInformationProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Information Settings"));
        // Настройки для InformationRenderer
        // Выбор отображаемой информации
    }

    private void addArmorHudProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Armor HUD Settings"));
        // Настройки для ArmorHudRenderer
        // Стиль отображения брони
    }

    private void addMinimapProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Minimap Settings"));
        // Настройки для MinimapRenderer
        // Зум, показ сущностей и т.д.
    }

    private void addInventoryHudProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Inventory HUD Settings"));
        // Настройки для InventoryHudRenderer
    }

    private void addCooldownsProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Cooldowns Settings"));
        // Настройки для CooldownRenderer
    }

    private void addTotemCountProperties(HudElementWrapper element) {
        propertyComponents.add(new CategoryComponent("Totem Count Settings"));
        // Настройки для TotemCountRenderer
    }

    private void renderPropertyComponents(MatrixStack matrix, int mouseX, int mouseY, float startY) {
        float currentY = startY + scrollOffset;

        for (PropertyComponent component : propertyComponents) {
            if (currentY + COMPONENT_HEIGHT > startY && currentY < startY + screen.height - 120) {
                component.render(matrix, PANEL_PADDING, currentY,
                        PANEL_WIDTH - PANEL_PADDING * 2, COMPONENT_HEIGHT, mouseX, mouseY);
            }
            currentY += COMPONENT_HEIGHT + COMPONENT_SPACING;
        }
    }

    private void renderScrollbar(MatrixStack matrix, float x, float y, float height) {
        if (maxScrollOffset <= 0) return;

        float scrollbarWidth = 4f;
        float scrollProgress = -scrollOffset / maxScrollOffset;
        float thumbHeight = Math.max(20f, height * (height / (height + maxScrollOffset)));
        float thumbY = y + scrollProgress * (height - thumbHeight);

        // Фон скроллбара
        RenderUtil.Rounded.roundedRect(matrix, x, y, scrollbarWidth, height,
                ColorUtil.getColor(60, 60, 80, 100), Round.of(2));

        // Ползунок
        RenderUtil.Rounded.roundedRect(matrix, x, thumbY, scrollbarWidth, thumbHeight,
                Theme.getInstance().clientColor(), Round.of(2));
    }

    private void renderActionButtons(MatrixStack matrix, int mouseX, int mouseY, float y) {
        float buttonWidth = (PANEL_WIDTH - PANEL_PADDING * 3) / 2;
        float buttonHeight = 25f;

        // Кнопка "Reset Position"
        boolean resetHovered = isHovered(mouseX, mouseY, PANEL_PADDING, y, buttonWidth, buttonHeight);
        int resetColor = resetHovered ?
                ColorUtil.getColor(255, 193, 7, 200) : ColorUtil.getColor(255, 193, 7, 150);

        RenderUtil.Rounded.roundedRect(matrix, PANEL_PADDING, y, buttonWidth, buttonHeight,
                resetColor, Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(matrix, "Reset Position", PANEL_PADDING + buttonWidth / 2,
                y + buttonHeight / 2 - 4, ColorUtil.getColor(255, 255, 255), 8);

        // Кнопка "Close"
        float closeX = PANEL_PADDING * 2 + buttonWidth;
        boolean closeHovered = isHovered(mouseX, mouseY, closeX, y, buttonWidth, buttonHeight);
        int closeColor = closeHovered ?
                ColorUtil.getColor(220, 53, 69, 200) : ColorUtil.getColor(220, 53, 69, 150);

        RenderUtil.Rounded.roundedRect(matrix, closeX, y, buttonWidth, buttonHeight,
                closeColor, Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(matrix, "Close", closeX + buttonWidth / 2,
                y + buttonHeight / 2 - 4, ColorUtil.getColor(255, 255, 255), 8);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        // Проверяем клики по компонентам свойств
        float panelY = 50f;
        float currentY = panelY + scrollOffset;

        for (PropertyComponent component : propertyComponents) {
            if (currentY + COMPONENT_HEIGHT > panelY && currentY < panelY + screen.height - 120) {
                if (component.mouseClicked(mouseX, mouseY, PANEL_PADDING, currentY,
                        PANEL_WIDTH - PANEL_PADDING * 2, COMPONENT_HEIGHT)) {

                    // Устанавливаем флаг редактирования если это текстовый или числовой компонент
                    isEditingProperty = isEditableComponent(component);
                    return true;
                }
            }
            currentY += COMPONENT_HEIGHT + COMPONENT_SPACING;
        }

        // Проверяем клики по кнопкам действий
        float buttonY = screen.height - 60;
        float buttonWidth = (PANEL_WIDTH - PANEL_PADDING * 3) / 2;
        float buttonHeight = 25f;

        // Кнопка "Reset Position"
        if (isHovered(mouseX, mouseY, PANEL_PADDING, buttonY, buttonWidth, buttonHeight)) {
            HudElementWrapper element = screen.getSelectedElement();
            if (element != null) {
                // Сбрасываем позицию к центру экрана
                element.setPosition(screen.width / 2f - element.getWidth() / 2f,
                        screen.height / 2f - element.getHeight() / 2f);
            }
            return true;
        }

        // Кнопка "Close"
        float closeX = PANEL_PADDING * 2 + buttonWidth;
        if (isHovered(mouseX, mouseY, closeX, buttonY, buttonWidth, buttonHeight)) {
            screen.setSelectedElement(null); // Снимаем выделение вместо закрытия
            isEditingProperty = false;
            return true;
        }

        return false;
    }

    private boolean isEditableComponent(PropertyComponent component) {
        return component instanceof TextPropertyComponent ||
                component instanceof NumberPropertyComponent;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX <= PANEL_WIDTH) {
            scrollOffset += delta * 10f;
            scrollOffset = Math.max(-maxScrollOffset, Math.min(0, scrollOffset));
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Передаем нажатия клавиш активным компонентам
        for (PropertyComponent component : propertyComponents) {
            // Для текстовых компонентов вызываем keyPressed с char = 0
            if (component instanceof TextPropertyComponent textComponent) {
                if (textComponent.keyPressed(keyCode, (char) 0)) {
                    return true;
                }
            }
        }
        // Если нажали Enter или Escape, завершаем редактирование
        if (keyCode == 257 || keyCode == 256) { // Enter или Escape
            isEditingProperty = false;
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        // Передаем ввод символов только текстовым компонентам
        for (PropertyComponent component : propertyComponents) {
            if (component instanceof TextPropertyComponent textComponent) {
                if (textComponent.keyPressed(0, codePoint)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Освобождаем все компоненты от захвата мыши
        for (PropertyComponent component : propertyComponents) {
            if (component instanceof NumberPropertyComponent numberComponent) {
                numberComponent.mouseReleased();
            }
        }
        return false;
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void enableScissor(float x, float y, float width, float height) {
        double scale = net.minecraft.client.Minecraft.getInstance().getMainWindow().getScaleFactor();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(
                (int) (x * scale),
                (int) ((net.minecraft.client.Minecraft.getInstance().getMainWindow().getScaledHeight() - (y + height)) * scale),
                (int) (width * scale),
                (int) (height * scale)
        );
    }

    private void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }

    // Геттер для проверки состояния редактирования
    public boolean isEditingProperty() {
        return isEditingProperty;
    }

    // Метод для принудительного завершения редактирования
    public void finishEditing() {
        isEditingProperty = false;
    }
}