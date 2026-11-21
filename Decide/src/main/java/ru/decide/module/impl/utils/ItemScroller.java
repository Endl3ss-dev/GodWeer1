/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Item Scroller", desc="\u041f\u043e\u043c\u043e\u0433\u0430\u0435\u0442 \u0441\u043a\u0440\u043e\u043b\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class ItemScroller
extends Module {
    public SliderSetting delay = new SliderSetting(this, "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 50.0f, 0.0f, 100.0f, 1.0f);

    public static ItemScroller getInstance() {
        return Instance.get(ItemScroller.class);
    }
}

