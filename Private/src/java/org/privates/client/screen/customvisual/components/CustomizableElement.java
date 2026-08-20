package org.privates.client.screen.customvisual.components;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CustomizableElement {
    private final String id;
    private String displayName;
    private float x, y;
    private float width, height;
    private final ElementType type;
    private boolean visible = true;
    private boolean locked = false;

    // Кастомные свойства для гибкой настройки
    private final Map<String, String> customProperties = new HashMap<>();

    public CustomizableElement(String id, String displayName, float x, float y, ElementType type) {
        this.id = id;
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.type = type;

        // Устанавливаем размеры по умолчанию в зависимости от типа
        setDefaultDimensions();
        setDefaultProperties();
    }

    private void setDefaultDimensions() {
        switch (type) {
            case TEXT:
            case CUSTOM_TEXT:
                this.width = 100;
                this.height = 20;
                break;
            case MODULE_LIST:
                this.width = 120;
                this.height = 80;
                break;
            case PLAYER_INFO:
                this.width = 100;
                this.height = 40;
                break;
            case EFFECT_LIST:
                this.width = 140;
                this.height = 60;
                break;
            case INFO_BAR:
                this.width = 200;
                this.height = 14;
                break;
            case EQUIPMENT:
                this.width = 92;
                this.height = 60;
                break;
            case MAP:
                this.width = 128;
                this.height = 128;
                break;
            case CUSTOM_IMAGE:
                this.width = 64;
                this.height = 64;
                break;
            case PERFORMANCE_INFO:
                this.width = 80;
                this.height = 16;
                break;
        }
    }

    private void setDefaultProperties() {
        switch (type) {
            case TEXT:
            case CUSTOM_TEXT:
                customProperties.put("text", displayName);
                customProperties.put("fontSize", "10");
                customProperties.put("color", String.valueOf(0xFFFFFFFF));
                customProperties.put("background", "false");
                customProperties.put("backgroundColor", String.valueOf(0x80000000));
                customProperties.put("shadow", "true");
                customProperties.put("bold", "false");
                customProperties.put("italic", "false");
                customProperties.put("alignment", "left");
                break;

            case MODULE_LIST:
                customProperties.put("fontSize", "8");
                customProperties.put("spacing", "2");
                customProperties.put("showBackground", "true");
                customProperties.put("backgroundColor", String.valueOf(0x80000000));
                customProperties.put("sortBy", "alphabetical");
                customProperties.put("maxItems", "10");
                customProperties.put("colorMode", "theme");
                break;

            case PLAYER_INFO:
                customProperties.put("showHealth", "true");
                customProperties.put("showArmor", "true");
                customProperties.put("showDistance", "true");
                customProperties.put("animateHealth", "true");
                customProperties.put("showParticles", "true");
                customProperties.put("cornerRadius", "5");
                break;

            case EFFECT_LIST:
                customProperties.put("fontSize", "8");
                customProperties.put("showIcons", "true");
                customProperties.put("showDuration", "true");
                customProperties.put("verticalSpacing", "15");
                customProperties.put("maxEffects", "8");
                customProperties.put("hideVanilla", "false");
                break;

            case INFO_BAR:
                customProperties.put("showCoords", "true");
                customProperties.put("showFPS", "true");
                customProperties.put("showPing", "true");
                customProperties.put("showBPS", "true");
                customProperties.put("spacing", "4");
                customProperties.put("fontSize", "8");
                break;

            case EQUIPMENT:
                customProperties.put("showDurability", "true");
                customProperties.put("showPercentage", "true");
                customProperties.put("verticalLayout", "true");
                customProperties.put("itemSize", "16");
                customProperties.put("spacing", "2");
                break;

            case MAP:
                customProperties.put("zoom", "1.0");
                customProperties.put("showEntities", "true");
                customProperties.put("showWaypoints", "true");
                customProperties.put("rotateWithPlayer", "true");
                customProperties.put("showBorder", "true");
                customProperties.put("transparentBackground", "false");
                break;

            case CUSTOM_IMAGE:
                customProperties.put("imagePath", "");
                customProperties.put("opacity", "1.0");
                customProperties.put("maintainAspectRatio", "true");
                customProperties.put("rotation", "0");
                break;

            case PERFORMANCE_INFO:
                customProperties.put("showFPS", "true");
                customProperties.put("showTPS", "false");
                customProperties.put("showMemory", "false");
                customProperties.put("updateInterval", "1000");
                customProperties.put("warningThreshold", "30");
                break;
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = Math.max(10, width); // Минимальный размер
        this.height = Math.max(10, height);
    }

    public void setProperty(String key, String value) {
        customProperties.put(key, value);
    }

    public String getProperty(String key) {
        return customProperties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        return customProperties.getOrDefault(key, defaultValue);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = customProperties.get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = customProperties.get(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloatProperty(String key, float defaultValue) {
        String value = customProperties.get(key);
        try {
            return value != null ? Float.parseFloat(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void setBooleanProperty(String key, boolean value) {
        customProperties.put(key, String.valueOf(value));
    }

    public void setIntProperty(String key, int value) {
        customProperties.put(key, String.valueOf(value));
    }

    public void setFloatProperty(String key, float value) {
        customProperties.put(key, String.valueOf(value));
    }

    // Проверка, можно ли изменить размер элемента
    public boolean isResizable() {
        switch (type) {
            case CUSTOM_TEXT:
            case CUSTOM_IMAGE:
            case MAP:
                return true;
            default:
                return false;
        }
    }

    // Проверка, можно ли удалить элемент
    public boolean isDeletable() {
        return id.startsWith("custom_");
    }

    // Получение границ элемента для проверки коллизий
    public boolean intersects(CustomizableElement other) {
        return !(x + width < other.x || other.x + other.width < x ||
                y + height < other.y || other.y + other.height < y);
    }

    // Проверка, находится ли точка внутри элемента
    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    // Получение центра элемента
    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    // Привязка к сетке
    public void snapToGrid(int gridSize) {
        x = Math.round(x / gridSize) * gridSize;
        y = Math.round(y / gridSize) * gridSize;
    }

    // Клонирование элемента
    public CustomizableElement clone() {
        CustomizableElement copy = new CustomizableElement(
                id + "_copy", displayName + " Copy", x + 20, y + 20, type
        );
        copy.width = this.width;
        copy.height = this.height;
        copy.visible = this.visible;
        copy.locked = this.locked;
        copy.customProperties.putAll(this.customProperties);
        return copy;
    }
}