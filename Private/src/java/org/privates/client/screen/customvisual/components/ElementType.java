package org.privates.client.screen.customvisual.components;

import lombok.Getter;

@Getter
public enum ElementType {
    // Базовые типы элементов HUD
    TEXT("Text", "Простой текстовый элемент"),
    MODULE_LIST("Module List", "Список активных модулей"),
    PLAYER_INFO("Player Info", "Информация о цели"),
    EFFECT_LIST("Effect List", "Список активных эффектов"),
    INFO_BAR("Info Bar", "Панель с информацией"),
    EQUIPMENT("Equipment", "Отображение экипировки"),
    MAP("Map", "Мини-карта"),

    // Кастомные типы
    CUSTOM_TEXT("Custom Text", "Настраиваемый текст"),
    CUSTOM_IMAGE("Custom Image", "Кастомное изображение"),
    PERFORMANCE_INFO("Performance", "Информация о производительности"),

    // Дополнительные типы для будущего расширения
    GRAPH("Graph", "График данных"),
    PROGRESS_BAR("Progress Bar", "Полоса прогресса"),
    BUTTON("Button", "Интерактивная кнопка"),
    CONTAINER("Container", "Контейнер для группировки"),
    SEPARATOR("Separator", "Разделительная линия"),

    // Специализированные элементы
    CROSSHAIR("Crosshair", "Кастомный прицел"),
    RADAR("Radar", "Радар игроков"),
    INVENTORY_PREVIEW("Inventory", "Превью инвентаря"),
    HOTBAR("Hotbar", "Панель быстрого доступа"),

    // Информационные элементы
    COORDINATES("Coordinates", "Координаты игрока"),
    DIRECTION("Direction", "Направление взгляда"),
    BIOME_INFO("Biome", "Информация о биоме"),
    TIME_INFO("Time", "Игровое время"),

    // Боевые элементы
    DAMAGE_INDICATOR("Damage", "Индикатор урона"),
    KILL_COUNTER("Kills", "Счетчик убийств"),
    COMBO_COUNTER("Combo", "Счетчик комбо"),

    // Элементы для PvP
    CRYSTAL_COUNT("Crystals", "Количество кристаллов"),
    TOTEM_COUNT("Totems", "Количество тотемов"),
    ARMOR_DAMAGE("Armor Damage", "Урон по броне"),

    // Утилитарные элементы
    NOTIFICATION("Notification", "Область уведомлений"),
    CHAT_PREVIEW("Chat", "Превью чата"),
    KEYBIND_DISPLAY("Keybinds", "Отображение привязок клавиш"),

    // Декоративные элементы
    LOGO("Logo", "Логотип клиента"),
    BACKGROUND_SHAPE("Shape", "Декоративная фигура"),
    GRADIENT_OVERLAY("Gradient", "Градиентное наложение");

    private final String displayName;
    private final String description;

    ElementType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // Категории элементов для группировки в UI
    public ElementCategory getCategory() {
        return switch (this) {
            case TEXT, CUSTOM_TEXT, LOGO -> ElementCategory.TEXT;
            case MODULE_LIST, KEYBIND_DISPLAY -> ElementCategory.MODULES;
            case PLAYER_INFO, DAMAGE_INDICATOR, KILL_COUNTER, COMBO_COUNTER -> ElementCategory.COMBAT;
            case EFFECT_LIST, COORDINATES, DIRECTION, BIOME_INFO, TIME_INFO, PERFORMANCE_INFO ->
                    ElementCategory.INFORMATION;
            case EQUIPMENT, INVENTORY_PREVIEW, HOTBAR, CRYSTAL_COUNT, TOTEM_COUNT, ARMOR_DAMAGE ->
                    ElementCategory.INVENTORY;
            case MAP, RADAR, CROSSHAIR -> ElementCategory.NAVIGATION;
            case CUSTOM_IMAGE, BACKGROUND_SHAPE, GRADIENT_OVERLAY -> ElementCategory.DECORATION;
            case GRAPH, PROGRESS_BAR, INFO_BAR -> ElementCategory.WIDGETS;
            case BUTTON, CONTAINER, SEPARATOR -> ElementCategory.LAYOUT;
            case NOTIFICATION, CHAT_PREVIEW -> ElementCategory.COMMUNICATION;
        };
    }

    // Проверка, является ли элемент интерактивным
    public boolean isInteractive() {
        return switch (this) {
            case BUTTON, MAP, INVENTORY_PREVIEW, HOTBAR -> true;
            default -> false;
        };
    }

    // Проверка, может ли элемент быть изменен в размере
    public boolean isResizable() {
        return switch (this) {
            case CUSTOM_TEXT, CUSTOM_IMAGE, MAP, CONTAINER, BACKGROUND_SHAPE,
                 GRADIENT_OVERLAY, GRAPH, PROGRESS_BAR -> true;
            default -> false;
        };
    }

    // Проверка, является ли элемент кастомным (может быть удален)
    public boolean isCustom() {
        return switch (this) {
            case CUSTOM_TEXT, CUSTOM_IMAGE, GRAPH, PROGRESS_BAR, BUTTON,
                 CONTAINER, SEPARATOR, BACKGROUND_SHAPE, GRADIENT_OVERLAY -> true;
            default -> false;
        };
    }

    // Получение иконки для элемента (символ из HUD шрифта)
    public String getIcon() {
        return switch (this) {
            case TEXT, CUSTOM_TEXT -> "a";
            case MODULE_LIST -> "b";
            case PLAYER_INFO -> "c";
            case EFFECT_LIST -> "d";
            case INFO_BAR -> "e";
            case EQUIPMENT -> "f";
            case MAP -> "g";
            case CUSTOM_IMAGE -> "h";
            case PERFORMANCE_INFO -> "i";
            case GRAPH -> "j";
            case PROGRESS_BAR -> "k";
            case BUTTON -> "l";
            case CONTAINER -> "m";
            case SEPARATOR -> "n";
            case CROSSHAIR -> "o";
            case RADAR -> "p";
            case INVENTORY_PREVIEW -> "q";
            case HOTBAR -> "r";
            case COORDINATES -> "s";
            case DIRECTION -> "t";
            case BIOME_INFO -> "u";
            case TIME_INFO -> "v";
            case DAMAGE_INDICATOR -> "w";
            case KILL_COUNTER -> "x";
            case COMBO_COUNTER -> "y";
            case CRYSTAL_COUNT -> "z";
            case TOTEM_COUNT -> "A";
            case ARMOR_DAMAGE -> "B";
            case NOTIFICATION -> "C";
            case CHAT_PREVIEW -> "D";
            case KEYBIND_DISPLAY -> "E";
            case LOGO -> "F";
            case BACKGROUND_SHAPE -> "G";
            case GRADIENT_OVERLAY -> "H";
        };
    }

    public enum ElementCategory {
        TEXT("Text Elements"),
        MODULES("Module Elements"),
        COMBAT("Combat Elements"),
        INFORMATION("Information"),
        INVENTORY("Inventory"),
        NAVIGATION("Navigation"),
        DECORATION("Decoration"),
        WIDGETS("Widgets"),
        LAYOUT("Layout"),
        COMMUNICATION("Communication");

        @Getter
        private final String displayName;

        ElementCategory(String displayName) {
            this.displayName = displayName;
        }
    }
}