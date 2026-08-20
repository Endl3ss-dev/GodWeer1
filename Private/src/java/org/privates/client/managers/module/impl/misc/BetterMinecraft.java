package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BetterMinecraft", category = Category.MISC, description = "Улучшает майнкрафт")
public class BetterMinecraft extends Module {
    public static BetterMinecraft getInstance() {
        return Instance.get(BetterMinecraft.class);
    }

    private final BooleanSetting simpleChat = new BooleanSetting(this, "Простой чат", true);
    private final BooleanSetting chatHistory = new BooleanSetting(this, "История чата", true);
    private final BooleanSetting antiSpam = new BooleanSetting(this, "АнтиСпам в чате", true);
}