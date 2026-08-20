package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category1;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FullBright", category = Category.RENDER, description = "Осветляет мир")
public class FullBright extends Module {
    public static FullBright getInstance() {
        return Instance.get(FullBright.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Гамма", "Ночное Зрение");

}