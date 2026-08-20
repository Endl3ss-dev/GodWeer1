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
@ModuleInfo(name = "ChatSettings", category = Category.RENDER, description = "Настройки чата")
public class ChatSettings extends Module {
    public static ChatSettings getInstance() {
        return Instance.get(ChatSettings.class);
    }

    private final BooleanSetting animation = new BooleanSetting(this, "Анимация", true);
    private final ModeSetting animationType = new ModeSetting(this, "Тип анимации", "Слева", "Справа", "Сверху", "Снизу")
            .setVisible(animation::getValue);
    private final SliderSetting animationSpeed = new SliderSetting(this, "Скорость", 150F, 50F, 500F, 10F)
            .setVisible(animation::getValue);

    private final ModeSetting font = new ModeSetting(this, "Шрифт", "Default", "SF_BOLD", "SF_MEDIUM");

    private final BooleanSetting highlightName = new BooleanSetting(this, "Подсветка ника", true);
    private final ColorSetting highlightColor = new ColorSetting(this, "Цвет подсветки")
            .setVisible(highlightName::getValue);

    private final ModeSetting position = new ModeSetting(this, "Позиция", "Слева снизу", "Слева сверху", "Справа сверху", "Справа снизу", "Кастом");
    private final SliderSetting customX = new SliderSetting(this, "X позиция", 2F, 0F, 1050F, 1F)
            .setVisible(() -> position.is("Кастом"));
    private final SliderSetting customY = new SliderSetting(this, "Y позиция", 0F, -585F, 100F, 1F)
            .setVisible(() -> position.is("Кастом"));

    private final BooleanSetting background = new BooleanSetting(this, "Фон чата", true);
}