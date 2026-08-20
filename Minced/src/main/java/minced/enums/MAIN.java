package minced.enums;

public enum MAIN {
    MAIN("Основной"),
    MODULE_VISUAL("Визуальные модули"),
    TEXT("Текст"),
    TEXT_INACTIVE("Неактивный текст"),
    HEADER("Цвет заголовков"),
    HEADER_TEXT("Текст заголовков"),
    SLIDER("Слайдер"),
    SLIDER_CIRCLE("Круг слайдера"),
    SLIDER_WINDOW("Окно слайдера"),
    INDICATOR("Индикатор"),
    INDICATOR_INACTIVE("Неактивный индикатор"),
    BUTTON("Кнопка"),
    BUTTON_INACTIVE("Неактивная кнопка"),
    OUTLINE("Обводка"),
    SEPARATOR("Разделитель"),
    SCROLL_BAR("Скролл бар"),
    FIELD("Поле"),
    FIELD_INACTIVE("Неактивное поле"),
    LOGO("Лого"),
    LOGO_TEXT("Текст в лого"),
    TOOLTIP_BG("Фон в подсказках"),
    TOOLTIP("Текст в подсказках"),
    WINDOW_BG("Фон окон"),
    WINDOW_TEXT("Текст окон");

    public final String field54;

    MAIN(String name) {
        this.field54 = name;
    }

    public String method33() {
        return this.field54;
    }

    public static void method34() {
        // Kept for compatibility with the decompiled API. Enum constants are initialized by Java.
    }
}


