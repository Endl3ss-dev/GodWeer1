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
public class BooleanSetting
extends Setting<Boolean> {
    private Boolean cachedValue;
    private boolean gated = true;
    public Animation animation = new EaseInOutQuad(300, 1.0);
    public Animation animation2 = new EaseInOutQuad(300, 1.0);
    public Animation animation3 = new EaseInOutQuad(300, 1.0);

    public BooleanSetting(String name, Boolean value) {
        super(name, value);
        this.cachedValue = value;
    }

    public BooleanSetting(Module parent, String name, Boolean value) {
        super(parent, name, value);
        this.cachedValue = value;
    }

    public static BooleanSetting of(String name, Boolean value) {
        return new BooleanSetting(name, value);
    }

    public BooleanSetting setVisible(Supplier<Boolean> value) {
        return (BooleanSetting)super.setVisible(value);
    }

    public BooleanSetting ungated() {
        this.gated = false;
        return this;
    }

    public BooleanSetting set(Boolean value) {
        BooleanSetting set = (BooleanSetting)super.set(value);
        this.cachedValue = (Boolean)super.getValue() != false && this.getVisible().get() != false;
        return set;
    }

    public BooleanSetting onAction(Runnable action) {
        return (BooleanSetting)super.onAction(() -> {
            action.run();
            this.cachedValue = (Boolean)super.getValue() != false && this.getVisible().get() != false;
        });
    }

    public BooleanSetting onSetVisible(Runnable action) {
        return (BooleanSetting)super.onSetVisible(action);
    }

    @Override
    public Boolean getValue() {
        if (!this.gated) {
            return (Boolean)super.getValue();
        }
        if (this.cachedValue == null) {
            this.cachedValue = (Boolean)super.getValue();
        }
        return this.cachedValue != false && this.getVisible().get() != false;
    }
}

