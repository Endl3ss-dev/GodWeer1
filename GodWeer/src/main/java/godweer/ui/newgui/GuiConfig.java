package godweer.ui.newgui;

import godweer.modules.api.setting.FloatSetting;

// Настройки самого GUI (в исходнике — модуль Theme): сила блюра панелей,
// сила блюра под настройками модулей и прозрачность фона.
public final class GuiConfig {

    private static final FloatSetting BLUR_STRENGTH = new FloatSetting("Сила Blur", 8.5f, 0.0f, 66.0f, 1.5f);
    private static final FloatSetting SETTINGS_BLUR_STRENGTH = new FloatSetting("Сила Blur настроек", 8.5f, 0.0f, 66.0f, 1.5f);
    private static final FloatSetting BACKGROUND_ALPHA = new FloatSetting("Прозрачность ректа", 0.0f, 0.0f, 255.0f, 1.0f);

    private GuiConfig() {
    }

    public static FloatSetting blurStrength() {
        return BLUR_STRENGTH;
    }

    public static FloatSetting settingsBlurStrength() {
        return SETTINGS_BLUR_STRENGTH;
    }

    public static FloatSetting backgroundAlpha() {
        return BACKGROUND_ALPHA;
    }
}
