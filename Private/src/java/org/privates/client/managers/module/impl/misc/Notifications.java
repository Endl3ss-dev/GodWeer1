package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Notifications", category = Category.MISC, description = "Звуковое уведомление о включение функции")
public class Notifications extends Module {
    public static Notifications getInstance() {
        return Instance.get(Notifications.class);
    }

    private final BooleanSetting sound = new BooleanSetting(this, "Звук", true);
    private final SliderSetting volume = new SliderSetting(this, "Громкость", 50, 1, 100, 1);
    private final SliderSetting scale = new SliderSetting(this, "Масштаб", 0.9f, 0.5f, 2.0f, 0.1f);
    private final BooleanSetting showSeparators = new BooleanSetting(this, "Разделители", true);
    private final BooleanSetting showIcons = new BooleanSetting(this, "Иконки", true);
    private final SliderSetting animationSpeed = new SliderSetting(this, "Скорость анимации", 15, 5, 30, 1);
}