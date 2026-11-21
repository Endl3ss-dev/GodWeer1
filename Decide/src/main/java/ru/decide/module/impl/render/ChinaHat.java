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

@ModuleInfo(name="China Hat", desc="\u041a\u0438\u0442\u0430\u0439\u0441\u043a\u0430\u044f \u0448\u043b\u044f\u043f\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class ChinaHat
extends Module {
    public final SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 0.55f, 0.15f, 1.4f, 0.01f);
    public final SliderSetting apex = new SliderSetting(this, "\u041f\u043e\u0434\u044a\u0451\u043c \u0446\u0435\u043d\u0442\u0440\u0430", 0.31f, 0.0f, 1.6f, 0.01f);
    public final SliderSetting lift = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430", 0.5f, 0.0f, 1.2f, 0.01f);
    public final ColorSetting colorA = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 1", -50384);
    public final ColorSetting colorB = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 2", -10742);
    public final BooleanSetting themeColors = new BooleanSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", false);
    public final SliderSetting opacity = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 200.0f, 0.0f, 255.0f, 1.0f);
    public final SliderSetting speed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 5.0f, 0.0f, 20.0f, 0.5f);
    public final BooleanSetting outline = new BooleanSetting((Module)this, "\u041e\u0431\u0432\u043e\u0434\u043a\u0430", true);

    public ChinaHat() {
        this.colorA.setVisible(() -> this.themeColors.getValue() == false);
        this.colorB.setVisible(() -> this.themeColors.getValue() == false);
    }

    public static ChinaHat getInstance() {
        return Instance.get(ChinaHat.class);
    }
}

