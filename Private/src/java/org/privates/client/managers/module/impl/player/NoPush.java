package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category1;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.utils.other.Instance;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoPush", category = Category.PLAYER, description = "Выключает колизию на виды блоков (вас не отталкивает в блоках)")
public class NoPush extends Module {
    public static NoPush getInstance() {
        return Instance.get(NoPush.class);
    }

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Не отталкиваться от",
            BooleanSetting.of("Игроков", true),
            BooleanSetting.of("Блоков", true),
            BooleanSetting.of("Воды", true)
    );
}
