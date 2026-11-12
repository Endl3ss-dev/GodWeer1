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
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public class ColorSetting
extends Setting<Integer> {
    private Integer cachedValue;
    public boolean pickerOpen = false;
    public Animation pickerAnim = new EaseInOutQuad(220, 1.0);

    public ColorSetting(Module parent, String name) {
        super(parent, name, ColorUtil.RED);
        this.cachedValue = ColorUtil.RED;
    }

    public ColorSetting(Module parent, String name, Integer value) {
        super(parent, name, value);
        this.cachedValue = value;
    }

    public ColorSetting set(Integer value) {
        ColorSetting set = (ColorSetting)super.set(value);
        this.cachedValue = (Integer)super.getValue();
        return set;
    }

    public ColorSetting setVisible(Supplier<Boolean> value) {
        return (ColorSetting)super.setVisible(value);
    }

    public ColorSetting onAction(Runnable action) {
        return (ColorSetting)super.onAction(() -> {
            action.run();
            this.cachedValue = (Integer)super.getValue();
        });
    }

    public ColorSetting onSetVisible(Runnable action) {
        return (ColorSetting)super.onSetVisible(action);
    }

    @Override
    public Integer getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = (Integer)super.getValue();
        }
        return this.cachedValue;
    }
}

