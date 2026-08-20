package org.privates.client.managers.module.impl.player;

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
@ModuleInfo(name = "CustomModel", category = Category.PLAYER, description = "Заменяет модель игрока на более новую")
public class CustomModel extends Module {
    public static CustomModel getInstance() {
        return Instance.get(CustomModel.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Модель",
            "Демон", "Кролик", "Амогус", "Джефф", "BabyBoy");
}