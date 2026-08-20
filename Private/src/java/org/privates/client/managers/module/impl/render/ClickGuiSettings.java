package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickGuiSettings", category = Category.RENDER, description = "Настройки ClickGui")
public class ClickGuiSettings extends Module {
    public static ClickGuiSettings getInstance() {
        return Instance.get(ClickGuiSettings.class);
    }

    final BooleanSetting sounds = new BooleanSetting(this, "Звуки", true);

    final BooleanSetting useThemeColors = new BooleanSetting(this, "Использовать тему", true);

    final ColorSetting backgroundColor = new ColorSetting(this, "Фон", ColorUtil.getColor(15, 20, 30)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting backgroundAlpha = new SliderSetting(this, "Прозрачность фона", 165F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting categoryBgColor = new ColorSetting(this, "Фон категорий", ColorUtil.getColor(20, 25, 35)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting categoryBgAlpha = new SliderSetting(this, "Прозрачность категорий", 60F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting moduleBgColor = new ColorSetting(this, "Фон модулей", ColorUtil.getColor(20, 25, 35)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting moduleBgAlpha = new SliderSetting(this, "Прозрачность модулей", 60F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting settingsBgColor = new ColorSetting(this, "Фон настроек", ColorUtil.getColor(20, 25, 35)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting settingsBgAlpha = new SliderSetting(this, "Прозрачность настроек", 60F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting outlineColor = new ColorSetting(this, "Обводка", ColorUtil.getColor(40, 50, 65)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting outlineAlpha = new SliderSetting(this, "Прозрачность обводки", 180F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting textColor = new ColorSetting(this, "Текст", ColorUtil.getColor(129, 146, 252)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting textAlpha = new SliderSetting(this, "Прозрачность текста", 255F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final ColorSetting accentColor = new ColorSetting(this, "Акцент", ColorUtil.getColor(101, 121, 252)).setVisible(() -> !useThemeColors.getValue());
    final SliderSetting accentAlpha = new SliderSetting(this, "Прозрачность акцента", 255F, 0F, 255F, 1F).setVisible(() -> !useThemeColors.getValue());

    final BooleanSetting enableBlur = new BooleanSetting(this, "Блюр", true);
    final SliderSetting blurStrength = new SliderSetting(this, "Сила блюра", 15F, 0F, 30F, 1F).setVisible(enableBlur::getValue);

    final BooleanSetting useCustomStyle = new BooleanSetting(this, "Кастомный стиль", false);
    final BooleanSetting customStyleCategories = new BooleanSetting(this, "Стиль категорий", true).setVisible(useCustomStyle::getValue);
    final BooleanSetting customStyleModules = new BooleanSetting(this, "Стиль модулей", true).setVisible(useCustomStyle::getValue);
    final BooleanSetting customStyleSettings = new BooleanSetting(this, "Стиль настроек", true).setVisible(useCustomStyle::getValue);
    final SliderSetting customRadius = new SliderSetting(this, "Радиус", 4F, 0F, 15F, 0.5F).setVisible(useCustomStyle::getValue);

    final BooleanSetting enableSettingsOutline = new BooleanSetting(this, "Обводка настроек", false);
    final SliderSetting settingsOutlineWidth = new SliderSetting(this, "Толщина обводки", 0.8F, 0.3F, 2F, 0.1F).setVisible(enableSettingsOutline::getValue);
    final SliderSetting settingsOutlineAlpha = new SliderSetting(this, "Прозрачность обводки настроек", 40F, 0F, 255F, 1F).setVisible(enableSettingsOutline::getValue);

    final ModeSetting settingsRenderMode = new ModeSetting(this, "Рендер настроек", "Eternity", "НЕ ВКЛЮЧАТЬ");

    final ModeSetting openAnimation = new ModeSetting(this, "Анимация открытия", "Плавная", "Bounce", "Elastic", "Scale", "Fade");
    final SliderSetting openSpeed = new SliderSetting(this, "Скорость открытия", 300F, 100F, 1000F, 50F);

    final ModeSetting settingsAnimation = new ModeSetting(this, "Анимация настроек", "Слева", "Справа", "Сверху", "Снизу", "Fade");
    final SliderSetting settingsSpeed = new SliderSetting(this, "Скорость настроек", 200F, 50F, 500F, 25F);

    final BooleanSetting enableResize = new BooleanSetting(this, "Ресайз", false);
    final BooleanSetting showResizeBars = new BooleanSetting(this, "Полоски ресайза", true).setVisible(enableResize::getValue);

    public int getBackgroundColor() {
        if (useThemeColors.getValue()) return Theme.getInstance().backgroundColor();
        return ColorUtil.replAlpha(backgroundColor.getValue(), backgroundAlpha.getValue() / 255f);
    }

    public int getCategoryBgColor() {
        if (useThemeColors.getValue()) return ColorUtil.getColor(20, 20, 20, 60);
        return ColorUtil.replAlpha(categoryBgColor.getValue(), categoryBgAlpha.getValue() / 255f);
    }

    public int getModuleBgColor() {
        if (useThemeColors.getValue()) return ColorUtil.getColor(20, 20, 20, 60);
        return ColorUtil.replAlpha(moduleBgColor.getValue(), moduleBgAlpha.getValue() / 255f);
    }

    public int getSettingsBgColor() {
        if (useThemeColors.getValue()) return ColorUtil.getColor(20, 20, 20, 60);
        return ColorUtil.replAlpha(settingsBgColor.getValue(), settingsBgAlpha.getValue() / 255f);
    }

    public int getOutlineColor() {
        if (useThemeColors.getValue()) return ColorUtil.getColor(40, 40, 40);
        return ColorUtil.replAlpha(outlineColor.getValue(), outlineAlpha.getValue() / 255f);
    }

    public int getTextColor() {
        if (useThemeColors.getValue()) return Theme.getInstance().textColor();
        return ColorUtil.replAlpha(textColor.getValue(), textAlpha.getValue() / 255f);
    }

    public int getAccentColor() {
        if (useThemeColors.getValue()) return Theme.getInstance().clientColor();
        return ColorUtil.replAlpha(accentColor.getValue(), accentAlpha.getValue() / 255f);
    }

    public float getCustomRadius() {
        return useCustomStyle.getValue() ? customRadius.getValue() : 6f;
    }

    public boolean shouldUseCustomStyleFor(String element) {
        if (!useCustomStyle.getValue()) return false;
        switch (element) {
            case "categories":
                return customStyleCategories.getValue();
            case "modules":
                return customStyleModules.getValue();
            case "settings":
                return customStyleSettings.getValue();
            default:
                return false;
        }
    }

    public boolean isRockstarMode() {
        return settingsRenderMode.is("Rockstar");
    }

    public int getSettingsOutlineColor() {
        if (!enableSettingsOutline.getValue()) return 0;
        return ColorUtil.getColor(255, 255, 255, settingsOutlineAlpha.getValue().intValue());
    }

    public float getSettingsOutlineWidth() {
        return enableSettingsOutline.getValue() ? settingsOutlineWidth.getValue() : 0f;
    }
}