package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Beta
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ArmorDurability", category = Category.MISC, description = "Отоброжает прочность брони опа")
public class ArmorDurability extends Module {
    public static ArmorDurability getInstance() {
        return Instance.get(ArmorDurability.class);
    }

    private final BooleanSetting state = new BooleanSetting(this, "Включено", false);

    public boolean isState() {
        return state.getValue();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }
}