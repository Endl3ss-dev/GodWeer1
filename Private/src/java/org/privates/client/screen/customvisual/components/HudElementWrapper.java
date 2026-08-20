package org.privates.client.screen.customvisual.components;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

@Getter
@Setter
public class HudElementWrapper {
    private final String id;
    private final String displayName;
    private final IRenderer renderer;
    private final DragSetting dragSetting;
    private boolean visible = true;
    private boolean locked = false;

    public HudElementWrapper(String id, String displayName, IRenderer renderer, DragSetting dragSetting) {
        this.id = id;
        this.displayName = displayName;
        this.renderer = renderer;
        this.dragSetting = dragSetting;
    }

    // Методы для работы с позицией и размером через DragSetting
    public float getX() {
        return dragSetting.position.x;
    }

    public float getY() {
        return dragSetting.position.y;
    }

    public float getWidth() {
        return dragSetting.size.x;
    }

    public float getHeight() {
        return dragSetting.size.y;
    }

    public void setX(float x) {
        dragSetting.position.x = x;
    }

    public void setY(float y) {
        dragSetting.position.y = y;
    }

    public void setPosition(float x, float y) {
        dragSetting.position.set(x, y);
    }

    public void setSize(float width, float height) {
        dragSetting.size.set(width, height);
    }

    public void setWidth(float width) {
        dragSetting.size.x = width;
    }

    public void setHeight(float height) {
        dragSetting.size.y = height;
    }

    // Метод для рендера элемента с использованием оригинального рендерера
    public void renderInEditor(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            net.minecraft.client.renderer.ActiveRenderInfo info = mc.gameRenderer.getActiveRenderInfo();
            net.minecraft.client.MainWindow window = mc.getMainWindow();
            org.privates.client.managers.events.render.Render2DEvent fakeEvent = new org.privates.client.managers.events.render.Render2DEvent(matrix, info, window, partialTicks);
            boolean originalActive = dragSetting.active;
            dragSetting.active = false;
            renderer.render(fakeEvent);
            dragSetting.active = originalActive;
        } catch (Exception e) {
            renderPlaceholder(matrix);
        }
    }

    // Fallback рендер если оригинальный не работает
    private void renderPlaceholder(MatrixStack matrix) {
        org.privates.client.utils.render.draw.RenderUtil.Rounded.roundedRect(
                matrix, getX(), getY(), getWidth(), getHeight(),
                org.privates.client.utils.render.color.ColorUtil.getColor(50, 50, 50, 150),
                org.privates.client.utils.render.draw.Round.of(4)
        );

        org.privates.client.utils.render.font.Fonts.SF_MEDIUM.draw(
                matrix, displayName, getX() + 5, getY() + 5,
                org.privates.client.utils.render.color.ColorUtil.getColor(255, 255, 255), 8
        );
    }

    // Проверка коллизий
    public boolean contains(float px, float py) {
        return px >= getX() && px <= getX() + getWidth() &&
                py >= getY() && py <= getY() + getHeight();
    }

    // Получение центра элемента
    public float getCenterX() {
        return getX() + getWidth() / 2f;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2f;
    }

    // Привязка к сетке
    public void snapToGrid(int gridSize) {
        float newX = Math.round(getX() / gridSize) * gridSize;
        float newY = Math.round(getY() / gridSize) * gridSize;
        setPosition(newX, newY);
    }

    // Проверка, может ли элемент быть изменен в размере
    public boolean isResizable() {
        return true;
    }

    // Проверка, может ли элемент быть удален (кастомные элементы)
    public boolean isDeletable() {
        // Базовые HUD элементы нельзя удалять, только скрывать
        return id.startsWith("custom_");
    }

    // Получение типа элемента для иконок и категорий
    public ElementType getElementType() {
        switch (id) {
            case "watermark":
            case "watermark2":
                return ElementType.TEXT;
            case "arraylist":
                return ElementType.MODULE_LIST;
            case "targethud":
                return ElementType.PLAYER_INFO;
            case "keybinds":
                return ElementType.KEYBIND_DISPLAY;
            case "potions":
                return ElementType.EFFECT_LIST;
            case "information":
                return ElementType.INFO_BAR;
            case "armorhud":
            case "armorhud2":
                return ElementType.EQUIPMENT;
            case "minimap":
                return ElementType.MAP;
            case "inventoryhud":
                return ElementType.INVENTORY_PREVIEW;
            case "cooldowns":
                return ElementType.TIME_INFO;
            case "scoreboard":
                return ElementType.INFO_BAR;
            case "stafflist":
                return ElementType.TEXT;
            case "itemassist":
                return ElementType.EQUIPMENT;
            case "keystroke":
                return ElementType.KEYBIND_DISPLAY;
            case "nearbyplayers":
                return ElementType.RADAR;
            case "motiongraph":
                return ElementType.GRAPH;
            case "totemcount":
                return ElementType.INVENTORY_PREVIEW;
            case "assist":
                return ElementType.NOTIFICATION;
            default:
                return ElementType.TEXT;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}