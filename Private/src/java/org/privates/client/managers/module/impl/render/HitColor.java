package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HitColor", category = Category.RENDER)
public class HitColor extends Module {
    public static HitColor getInstance() {
        return Instance.get(HitColor.class);
    }

    private final ColorSetting color = new ColorSetting(this, "Цвет");
}