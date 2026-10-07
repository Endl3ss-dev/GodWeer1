package swerve.ui.newgui;

import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.utils.client.ClientSoundPlayer;

// Кастомизация ClickGui (категория Client). Каждая настройка реально влияет на
// рендер или поведение GUI — геттеры читаются напрямую из кода отрисовки.
public final class GuiStyle {

    private GuiStyle() {
    }

    // — Панели —
    private static final FloatSetting PANEL_WIDTH = new FloatSetting("Ширина панелей", 122.0f, 100.0f, 160.0f, 1.0f);
    private static final FloatSetting PANEL_HEIGHT = new FloatSetting("Высота панелей", 300.0f, 240.0f, 380.0f, 1.0f);
    private static final FloatSetting PANEL_GAP = new FloatSetting("Отступ панелей", 5.0f, 0.0f, 16.0f, 0.5f);
    private static final FloatSetting PANEL_RADIUS = new FloatSetting("Скругление панелей", 6.0f, 0.0f, 12.0f, 0.5f);
    private static final BooleanSetting PANEL_OUTLINE = new BooleanSetting("Обводка панелей", true);
    private static final FloatSetting OUTLINE_WIDTH = new FloatSetting("Толщина обводки", 0.5f, 0.25f, 3.0f, 0.25f);
    private static final FloatSetting OUTLINE_ALPHA = new FloatSetting("Альфа обводки", 15.0f, 0.0f, 255.0f, 1.0f);
    private static final BooleanSetting HEADER = new BooleanSetting("Заголовок панелей", true);
    private static final FloatSetting HEADER_SIZE = new FloatSetting("Размер заголовка", 1.0f, 0.7f, 1.4f, 0.05f);
    private static final FloatSetting HEADER_ALPHA = new FloatSetting("Альфа заголовка", 100.0f, 30.0f, 100.0f, 5.0f);
    private static final BooleanSetting HEADER_THEME = new BooleanSetting("Заголовки под цвет темы", true);
    private static final BooleanSetting CATEGORY_ICONS = new BooleanSetting("Иконки категорий", true);
    private static final FloatSetting ICON_SIZE = new FloatSetting("Размер иконок", 1.0f, 0.6f, 1.6f, 0.05f);
    private static final BooleanSetting CLIENT_ICON = new BooleanSetting("Иконка Client", true);
    private static final BooleanSetting HEADER_LINE = new BooleanSetting("Линия под заголовком", false);
    private static final FloatSetting CONTENT_OFFSET = new FloatSetting("Отступ контента", 4.5f, 0.0f, 10.0f, 0.5f);

    // — Фон —
    private static final BooleanSetting BG_GRADIENT = new BooleanSetting("Градиент фона", true);
    private static final FloatSetting BG_HUE = new FloatSetting("Тон фона", 225.0f, 0.0f, 360.0f, 1.0f);
    private static final FloatSetting BG_SATURATION = new FloatSetting("Насыщенность фона", 25.0f, 0.0f, 100.0f, 1.0f);
    private static final FloatSetting BG_BRIGHTNESS = new FloatSetting("Яркость фона", 20.0f, 5.0f, 45.0f, 1.0f);
    private static final ModeSetting BG_DIRECTION = new ModeSetting("Направление градиента", "Диагональ", "Диагональ", "Вертикаль", "Горизонталь");
    private static final FloatSetting BACKGROUND_DIM = new FloatSetting("Затемнение фона", 0.0f, 0.0f, 120.0f, 5.0f);
    private static final FloatSetting SETTINGS_BG_ALPHA = new FloatSetting("Альфа фона настроек", 95.0f, 0.0f, 100.0f, 5.0f);

    // — Модули —
    private static final FloatSetting MODULE_HEIGHT = new FloatSetting("Высота модуля", 14.0f, 10.0f, 20.0f, 0.5f);
    private static final FloatSetting MODULE_GAP = new FloatSetting("Отступ модулей", 3.0f, 1.0f, 8.0f, 0.5f);
    private static final FloatSetting MODULE_RADIUS = new FloatSetting("Радиус модулей", 4.0f, 2.0f, 8.0f, 0.5f);
    private static final FloatSetting MODULE_TEXT_SIZE = new FloatSetting("Размер текста модулей", 1.0f, 0.7f, 1.4f, 0.05f);
    private static final BooleanSetting FIXED_TEXT_SIZE = new BooleanSetting("Фиксированный размер текста", false);
    private static final FloatSetting DISABLED_ALPHA = new FloatSetting("Альфа выключенных", 100.0f, 20.0f, 100.0f, 5.0f);
    private static final ModeSetting ENABLED_STYLE = new ModeSetting("Стиль включённых", "Цвет текста", "Цвет текста", "Заливка", "Полоска");
    private static final BooleanSetting SHOW_DOTS = new BooleanSetting("Показ «...»", false);
    private static final BooleanSetting SHOW_BINDS = new BooleanSetting("Бинды по CTRL", true);
    private static final BooleanSetting HIDE_DISABLED = new BooleanSetting("Скрывать выключенные", false);
    private static final FloatSetting ANIM_SPEED = new FloatSetting("Скорость анимаций", 16.0f, 4.0f, 40.0f, 1.0f);

    // — Настройки —
    private static final BooleanSetting DESCRIPTIONS = new BooleanSetting("Показ описаний", true);
    private static final FloatSetting DESCRIPTION_SIZE = new FloatSetting("Размер описаний", 1.0f, 0.7f, 1.4f, 0.05f);
    private static final FloatSetting DESCRIPTION_ALPHA = new FloatSetting("Альфа описаний", 100.0f, 30.0f, 100.0f, 5.0f);
    private static final BooleanSetting DESCRIPTIONS_RIGHT = new BooleanSetting("Описания справа", false);
    private static final FloatSetting DESCRIPTION_SPEED = new FloatSetting("Скорость печати", 30.0f, 10.0f, 80.0f, 5.0f);
    private static final BooleanSetting SLIDER_THEME_VALUES = new BooleanSetting("Значения под цвет темы", true);
    private static final FloatSetting TOGGLE_RADIUS = new FloatSetting("Радиус тумблеров", 4.0f, 2.0f, 8.0f, 0.5f);
    private static final FloatSetting SETTINGS_BLUR_RADIUS = new FloatSetting("Радиус блюра настроек", 4.0f, 2.0f, 8.0f, 0.5f);

    // — Темы —
    private static final FloatSetting THEME_COLS = new FloatSetting("Колонок тем", 4.0f, 2.0f, 4.0f, 1.0f);
    private static final FloatSetting THEME_ROWS = new FloatSetting("Строк тем", 4.0f, 2.0f, 6.0f, 1.0f);
    private static final FloatSetting THEME_BOX_SIZE = new FloatSetting("Размер свотчей", 22.0f, 16.0f, 28.0f, 1.0f);
    private static final FloatSetting THEME_SCROLL_SPEED = new FloatSetting("Скорость скролла тем", 18.0f, 5.0f, 40.0f, 1.0f);
    private static final BooleanSetting THEME_SCROLLBAR = new BooleanSetting("Скроллбар тем", true);
    private static final BooleanSetting THEME_NAMES = new BooleanSetting("Названия тем", true);
    private static final FloatSetting THEME_GRID_ALPHA = new FloatSetting("Прозрачность сетки тем", 100.0f, 30.0f, 100.0f, 5.0f);
    private static final FloatSetting THEME_GRID_SPACING = new FloatSetting("Отступ свотчей", 4.0f, 2.0f, 8.0f, 0.5f);

    // — Звук и скролл —
    private static final BooleanSetting SOUNDS = new BooleanSetting("Звуки интерфейса", true);
    private static final FloatSetting SOUND_VOLUME = new FloatSetting("Громкость звуков", 100.0f, 10.0f, 300.0f, 5.0f);
    private static final BooleanSetting INVERT_SCROLL = new BooleanSetting("Инверсия скролла", false);
    private static final FloatSetting SCROLL_SPEED = new FloatSetting("Скорость скролла", 9.0f, 3.0f, 20.0f, 1.0f);
    private static final BooleanSetting SMOOTH_SCROLL = new BooleanSetting("Плавный скролл", true);

    // — Остров и подсказки —
    private static final BooleanSetting SHOW_ISLAND = new BooleanSetting("Показ острова", true);
    private static final BooleanSetting ISLAND_BLUR = new BooleanSetting("Блюр острова", true);
    private static final FloatSetting ISLAND_BUTTON_GAP = new FloatSetting("Отступ кнопок острова", 4.0f, 2.0f, 8.0f, 0.5f);
    private static final FloatSetting ISLAND_BUTTON_RADIUS = new FloatSetting("Скругление кнопок", 8.5f, 4.0f, 14.0f, 0.5f);
    private static final BooleanSetting SHOW_HINTS = new BooleanSetting("Показ подсказок", true);

    public static Setting[] settings() {
        return new Setting[] {
                PANEL_WIDTH, PANEL_HEIGHT, PANEL_GAP, PANEL_RADIUS, PANEL_OUTLINE, OUTLINE_WIDTH, OUTLINE_ALPHA,
                HEADER, HEADER_SIZE, HEADER_ALPHA, HEADER_THEME, CATEGORY_ICONS, ICON_SIZE, CLIENT_ICON, HEADER_LINE,
                CONTENT_OFFSET,
                BG_GRADIENT, BG_HUE, BG_SATURATION, BG_BRIGHTNESS, BG_DIRECTION, BACKGROUND_DIM, SETTINGS_BG_ALPHA,
                MODULE_HEIGHT, MODULE_GAP, MODULE_RADIUS, MODULE_TEXT_SIZE, FIXED_TEXT_SIZE, DISABLED_ALPHA,
                ENABLED_STYLE, SHOW_DOTS, SHOW_BINDS, ANIM_SPEED,
                DESCRIPTIONS, DESCRIPTION_SIZE, DESCRIPTION_ALPHA, DESCRIPTIONS_RIGHT, DESCRIPTION_SPEED,
                SLIDER_THEME_VALUES, TOGGLE_RADIUS, SETTINGS_BLUR_RADIUS,
                THEME_COLS, THEME_ROWS, THEME_BOX_SIZE, THEME_SCROLL_SPEED, THEME_SCROLLBAR, THEME_NAMES,
                THEME_GRID_ALPHA, THEME_GRID_SPACING,
                SOUNDS, SOUND_VOLUME, INVERT_SCROLL, SCROLL_SPEED, SMOOTH_SCROLL,
                SHOW_ISLAND, ISLAND_BLUR, ISLAND_BUTTON_GAP, ISLAND_BUTTON_RADIUS, SHOW_HINTS
        };
    }

    public static float panelWidth() {
        return PANEL_WIDTH.get();
    }

    public static float panelHeight() {
        return PANEL_HEIGHT.get();
    }

    public static float panelGap() {
        return PANEL_GAP.get();
    }

    public static float panelRadius() {
        return PANEL_RADIUS.get();
    }

    public static boolean panelOutline() {
        return PANEL_OUTLINE.get();
    }

    public static float outlineWidth() {
        return OUTLINE_WIDTH.get();
    }

    public static int outlineAlpha() {
        return (int) OUTLINE_ALPHA.get();
    }

    public static boolean showHeader() {
        return HEADER.get();
    }

    public static float headerSize() {
        return HEADER_SIZE.get();
    }

    public static float headerAlpha() {
        return HEADER_ALPHA.get() / 100.0f;
    }

    public static boolean headerThemeColor() {
        return HEADER_THEME.get();
    }

    public static boolean categoryIcons() {
        return CATEGORY_ICONS.get();
    }

    public static float iconSize() {
        return ICON_SIZE.get();
    }

    public static boolean clientIcon() {
        return CLIENT_ICON.get();
    }

    public static boolean headerLine() {
        return HEADER_LINE.get();
    }

    public static float contentOffset() {
        return CONTENT_OFFSET.get();
    }

    public static boolean backgroundGradient() {
        return BG_GRADIENT.get();
    }

    public static float backgroundHue() {
        return BG_HUE.get();
    }

    public static float backgroundSaturation() {
        return BG_SATURATION.get();
    }

    public static float backgroundBrightness() {
        return BG_BRIGHTNESS.get();
    }

    public static ModeSetting backgroundDirection() {
        return BG_DIRECTION;
    }

    public static int backgroundDim() {
        return (int) BACKGROUND_DIM.get();
    }

    public static float settingsBgAlpha() {
        return SETTINGS_BG_ALPHA.get() / 100.0f;
    }

    public static float moduleHeight() {
        return MODULE_HEIGHT.get();
    }

    public static float moduleGap() {
        return MODULE_GAP.get();
    }

    public static float moduleRadius() {
        return MODULE_RADIUS.get();
    }

    public static float moduleTextSize() {
        return MODULE_TEXT_SIZE.get();
    }

    public static boolean fixedModuleTextSize() {
        return FIXED_TEXT_SIZE.get();
    }

    public static float disabledAlpha() {
        return DISABLED_ALPHA.get() / 100.0f;
    }

    public static ModeSetting enabledStyle() {
        return ENABLED_STYLE;
    }

    public static boolean showDots() {
        return SHOW_DOTS.get();
    }

    public static boolean showBinds() {
        return SHOW_BINDS.get();
    }

    public static boolean isHideDisabled() {
        return HIDE_DISABLED.get();
    }

    // Базовые скорости анимаций исходника нормированы на 16.
    public static float anim(float baseSpeed) {
        return baseSpeed * ANIM_SPEED.get() / 16.0f;
    }

    public static boolean showDescriptions() {
        return DESCRIPTIONS.get();
    }

    public static float descriptionSize() {
        return DESCRIPTION_SIZE.get();
    }

    public static float descriptionAlpha() {
        return DESCRIPTION_ALPHA.get() / 100.0f;
    }

    public static boolean descriptionsRight() {
        return DESCRIPTIONS_RIGHT.get();
    }

    public static int descriptionSpeed() {
        return (int) DESCRIPTION_SPEED.get();
    }

    public static boolean sliderThemeValues() {
        return SLIDER_THEME_VALUES.get();
    }

    public static float toggleRadius() {
        return TOGGLE_RADIUS.get();
    }

    public static float settingsBlurRadius() {
        return SETTINGS_BLUR_RADIUS.get();
    }

    public static int themeCols() {
        return Math.max(2, (int) THEME_COLS.get());
    }

    public static int themeRows() {
        return Math.max(2, (int) THEME_ROWS.get());
    }

    public static float themeBoxSize() {
        return THEME_BOX_SIZE.get();
    }

    public static float themeScrollSpeed() {
        return THEME_SCROLL_SPEED.get();
    }

    public static boolean themeScrollbar() {
        return THEME_SCROLLBAR.get();
    }

    public static boolean themeNames() {
        return THEME_NAMES.get();
    }

    public static float themeGridAlpha() {
        return THEME_GRID_ALPHA.get() / 100.0f;
    }

    public static float themeGridSpacing() {
        return THEME_GRID_SPACING.get();
    }

    public static void playSound(String name, float volume) {
        if (SOUNDS.get()) {
            ClientSoundPlayer.playSound(name, volume * (SOUND_VOLUME.get() / 100.0), 1.0f);
        }
    }

    public static boolean invertScroll() {
        return INVERT_SCROLL.get();
    }

    public static float scrollSpeed() {
        return SCROLL_SPEED.get();
    }

    public static boolean smoothScroll() {
        return SMOOTH_SCROLL.get();
    }

    public static boolean showIsland() {
        return SHOW_ISLAND.get();
    }

    public static boolean islandBlur() {
        return ISLAND_BLUR.get();
    }

    public static float islandButtonGap() {
        return ISLAND_BUTTON_GAP.get();
    }

    public static float islandButtonRadius() {
        return ISLAND_BUTTON_RADIUS.get();
    }

    public static boolean showHints() {
        return SHOW_HINTS.get();
    }

    // ARGB из HSV — для тонировки фона панелей.
    public static int hsv(float hue, float saturation, float brightness, int alpha) {
        float h = (hue % 360.0f + 360.0f) % 360.0f / 60.0f;
        float s = Math.max(0.0f, Math.min(1.0f, saturation));
        float v = Math.max(0.0f, Math.min(1.0f, brightness));

        float c = v * s;
        float x = c * (1.0f - Math.abs(h % 2.0f - 1.0f));
        float m = v - c;

        float r, g, b;
        switch ((int) h) {
            case 0 -> { r = c; g = x; b = 0.0f; }
            case 1 -> { r = x; g = c; b = 0.0f; }
            case 2 -> { r = 0.0f; g = c; b = x; }
            case 3 -> { r = 0.0f; g = x; b = c; }
            case 4 -> { r = x; g = 0.0f; b = c; }
            default -> { r = c; g = 0.0f; b = x; }
        }

        return (alpha << 24)
                | ((int) ((r + m) * 255.0f) << 16)
                | ((int) ((g + m) * 255.0f) << 8)
                | (int) ((b + m) * 255.0f);
    }
}
