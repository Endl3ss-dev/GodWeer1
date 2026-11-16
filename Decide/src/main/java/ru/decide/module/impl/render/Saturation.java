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
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.render.SaturationPipeline;

@ModuleInfo(name="Saturation", desc="\u041f\u043e\u0441\u0442\u043e\u0431\u0440\u0430\u0431\u043e\u0442\u043a\u0430 \u0446\u0432\u0435\u0442\u0430: \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c, \u043e\u0442\u0442\u0435\u043d\u043e\u043a, \u043a\u043e\u043d\u0442\u0440\u0430\u0441\u0442", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class Saturation
extends Module {
    public SliderSetting brightness = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c", 0.0f, -1.0f, 1.0f, 0.01f);
    public SliderSetting saturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.0f, 2.0f, 0.01f);
    public SliderSetting contrast = new SliderSetting(this, "\u041a\u043e\u043d\u0442\u0440\u0430\u0441\u0442", 1.0f, 0.0f, 2.0f, 0.01f);
    public SliderSetting hue = new SliderSetting(this, "\u041e\u0442\u0442\u0435\u043d\u043e\u043a", 0.0f, -180.0f, 180.0f, 1.0f);

    @EventHandler
    public void onDisplay(EventDisplay e) {
        boolean identity;
        if (Saturation.mc.field_1724 == null || Saturation.mc.field_1687 == null) {
            return;
        }
        float b = this.brightness.getValue().floatValue();
        float s = this.saturation.getValue().floatValue();
        float c = this.contrast.getValue().floatValue();
        float h = this.hue.getValue().floatValue();
        boolean bl = identity = Math.abs(b) < 0.001f && Math.abs(s - 1.0f) < 0.001f && Math.abs(c - 1.0f) < 0.001f && Math.abs(h) < 0.001f;
        if (identity) {
            return;
        }
        SaturationPipeline.draw(b, s, c, h);
    }
}

