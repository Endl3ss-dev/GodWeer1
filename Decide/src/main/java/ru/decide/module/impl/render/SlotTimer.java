/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Slot Timer", desc="\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u043d\u0430 \u0441\u043b\u043e\u0442\u0430\u0445", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class SlotTimer
extends Module {
    public final BooleanSetting numbers = new BooleanSetting((Module)this, "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0446\u0438\u0444\u0440\u044b", true);
    public final BooleanSetting convertDelays = new BooleanSetting((Module)this, "\u0412 \u0441\u0435\u043a\u0443\u043d\u0434\u0430\u0445", true);
    public final BooleanSetting fill = new BooleanSetting((Module)this, "\u0417\u0430\u043b\u0438\u0432\u043a\u0430 \u043a\u0443\u043b\u0434\u0430\u0443\u043d\u0430", true);
    public final SliderSetting fontSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0442\u0435\u043a\u0441\u0442\u0430", 1.0f, 0.5f, 2.0f, 0.1f);
    public final BooleanSetting healTracking = new BooleanSetting((Module)this, "\u041a\u0443\u043b\u0434\u0430\u0443\u043d \u0445\u0438\u043b\u043e\u043a (18.5 \u0441)", true);

    public static SlotTimer getInstance() {
        return Instance.get(SlotTimer.class);
    }
}

