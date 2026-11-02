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
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Hit Color", desc="\u0426\u0432\u0435\u0442 \u0432\u0441\u043f\u044b\u0448\u043a\u0438 \u043f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0443\u0440\u043e\u043d\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class HitColor
extends Module {
    public final ColorSetting color = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0432\u0441\u043f\u044b\u0448\u043a\u0438", -65536);
    public final SliderSetting strength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0432\u0441\u043f\u044b\u0448\u043a\u0438", 70.0f, 5.0f, 100.0f, 1.0f);
    public final BooleanSetting armor = new BooleanSetting((Module)this, "\u041a\u0440\u0430\u0441\u0438\u0442\u044c \u0431\u0440\u043e\u043d\u044e", true);

    public int overlayColor() {
        int alpha = Math.round(178.0f * (this.strength.getValue().floatValue() / 100.0f));
        return alpha << 24 | this.color.getValue() & 0xFFFFFF;
    }

    public static HitColor getInstance() {
        return Instance.get(HitColor.class);
    }
}

