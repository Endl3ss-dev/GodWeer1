package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "MinecraftSettings", category = Category.RENDER, description = "Настройки виджетов Minecraft")
public class MinecraftSettings extends Module {
    public static MinecraftSettings getInstance() {
        return Instance.get(MinecraftSettings.class);
    }

    private final BooleanSetting customWidget = new BooleanSetting(this, "Кастомные виджеты", true);

    private final BooleanSetting animation = new BooleanSetting(this, "Анимация", true)
            .setVisible(customWidget::getValue);
    private final ModeSetting animationType = new ModeSetting(this, "Тип анимации", "Плавная", "Bounce", "Elastic")
            .setVisible(() -> customWidget.getValue() && animation.getValue());
    private final SliderSetting animationSpeed = new SliderSetting(this, "Скорость анимации", 200F, 50F, 500F, 10F)
            .setVisible(() -> customWidget.getValue() && animation.getValue());

    private final BooleanSetting smoothOutline = new BooleanSetting(this, "Плавная обводка", true)
            .setVisible(customWidget::getValue);
    private final ColorSetting outlineColor = new ColorSetting(this, "Цвет обводки")
            .setVisible(() -> customWidget.getValue() && smoothOutline.getValue());

    private final BooleanSetting smoothRectColor = new BooleanSetting(this, "Плавный цвет фона", true)
            .setVisible(customWidget::getValue);
    private final ModeSetting rectColorMode = new ModeSetting(this, "Режим цвета фона", "Темнее", "Светлее", "Кастом")
            .setVisible(() -> customWidget.getValue() && smoothRectColor.getValue());
    private final ColorSetting customRectColor = new ColorSetting(this, "Кастомный цвет фона")
            .setVisible(() -> customWidget.getValue() && smoothRectColor.getValue() && rectColorMode.is("Кастом"));

    private final BooleanSetting smoothTextColor = new BooleanSetting(this, "Плавный цвет текста", true)
            .setVisible(customWidget::getValue);
    private final ModeSetting textColorMode = new ModeSetting(this, "Режим цвета текста", "Темнее", "Светлее", "Кастом")
            .setVisible(() -> customWidget.getValue() && smoothTextColor.getValue());
    private final ColorSetting customTextColor = new ColorSetting(this, "Кастомный цвет текста")
            .setVisible(() -> customWidget.getValue() && smoothTextColor.getValue() && textColorMode.is("Кастом"));

    private final BooleanSetting inventoryAnimation = new BooleanSetting(this, "Анимация инвентаря", true);
    private final ModeSetting inventoryAnimationType = new ModeSetting(this, "Тип анимации инв.", "Zoom", "Slide", "Fade", "Scale")
            .setVisible(inventoryAnimation::getValue);
    private final SliderSetting inventoryAnimationSpeed = new SliderSetting(this, "Скорость анимации инв.", 300F, 100F, 1000F, 50F)
            .setVisible(inventoryAnimation::getValue);

    private final BooleanSetting trashButton = new BooleanSetting(this, "Кнопка мусора", true);
}