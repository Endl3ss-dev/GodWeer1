/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings.impl;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;

@Environment(value=EnvType.CLIENT)
public class SliderSetting
extends Setting<Float> {
    public float min;
    public float max;
    public float increment;
    private Float cachedValue;
    public Animation animation = new EaseInOutQuad(300, 1.0);
    public Animation animation2 = new EaseInOutQuad(300, 1.0);
    public Animation animation3 = new EaseInOutQuad(300, 1.0);

    public SliderSetting(Module parent, String name, float value, float min, float max, float increment) {
        super(parent, name, Float.valueOf(value));
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.cachedValue = Float.valueOf(value);
    }

    public SliderSetting set(Float value) {
        SliderSetting set = (SliderSetting)super.set(value);
        this.cachedValue = (Float)super.getValue();
        return set;
    }

    public SliderSetting setVisible(Supplier<Boolean> value) {
        return (SliderSetting)super.setVisible(value);
    }

    public SliderSetting onAction(Runnable action) {
        return (SliderSetting)super.onAction(() -> {
            action.run();
            this.cachedValue = (Float)super.getValue();
        });
    }

    public SliderSetting onSetVisible(Runnable action) {
        return (SliderSetting)super.onSetVisible(action);
    }

    @Override
    public Float getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = (Float)super.getValue();
        }
        return this.cachedValue;
    }
}

