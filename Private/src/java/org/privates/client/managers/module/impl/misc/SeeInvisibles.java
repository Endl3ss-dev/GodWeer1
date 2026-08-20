package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category2;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SeeInvisibles", category = Category.MISC, description = "Ты теперь видишь невидимых игроков")
public class SeeInvisibles extends Module {
    public static SeeInvisibles getInstance() {
        return Instance.get(SeeInvisibles.class);
    }

    private final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.5F, 0.0F, 1.0F, 0.1F);
}