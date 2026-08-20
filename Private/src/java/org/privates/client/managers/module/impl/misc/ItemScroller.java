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
import org.privates.lib.util.time.StopWatch;
@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ItemScroller", category = Category.MISC, description = "Быстре перелаживаются предметы из сундука или другого контейнера")
public class ItemScroller extends Module {
    public static ItemScroller getInstance() {
        return Instance.get(ItemScroller.class);
    }

    private final SliderSetting delay = new SliderSetting(this, "Задержка", 100, 0, 1000, 1);
    private final StopWatch time = new StopWatch();
}