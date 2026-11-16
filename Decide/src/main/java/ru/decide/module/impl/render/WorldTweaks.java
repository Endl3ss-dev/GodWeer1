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
import ru.decide.manager.event_impl.FogEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="World Tweaks", desc="\u041c\u0435\u043b\u043a\u0438\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f \u043c\u0438\u0440\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class WorldTweaks
extends Module {
    public BooleanSetting times = new BooleanSetting((Module)this, "\u041c\u0435\u043d\u044f\u0442\u044c \u0432\u0440\u0435\u043c\u044f", true);
    public BooleanSetting fogs = new BooleanSetting((Module)this, "\u041c\u0435\u043d\u044f\u0442\u044c \u0442\u0443\u043c\u0430\u043d", true);
    public SliderSetting time = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f", 12.0f, 0.0f, 24.0f, 1.0f).setVisible(() -> this.times.getValue());
    public SliderSetting fog = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0442\u0443\u043c\u0430\u043d\u0430", 100.0f, 2.0f, 200.0f, 1.0f).setVisible(() -> this.fogs.getValue());
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -16711681).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));

    public static WorldTweaks get() {
        return Instance.get(WorldTweaks.class);
    }

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @EventHandler
    public void onFog(FogEvent e) {
        if (this.fogs.getValue().booleanValue()) {
            e.setDistance(this.fog.getValue().floatValue());
            e.setColor(this.getColor());
            e.cancel();
        }
    }
}

