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
import ru.decide.module.api.settings.impl.BooleanSetting;

@Environment(value=EnvType.CLIENT)
public class BooleanSettingHud
extends Setting<Boolean> {
    private Boolean cachedValue;

    public BooleanSettingHud(String name, Boolean value) {
        super(name, value);
        this.cachedValue = value;
    }

    public BooleanSettingHud(Module parent, String name, Boolean value) {
        super(parent, name, value);
        this.cachedValue = value;
    }

    public static BooleanSettingHud of(String name, Boolean value) {
        return new BooleanSettingHud(name, value);
    }

    public BooleanSettingHud setVisible(Supplier<Boolean> value) {
        return (BooleanSettingHud)super.setVisible(value);
    }

    public BooleanSettingHud set(Boolean value) {
        BooleanSettingHud set = (BooleanSettingHud)super.set(value);
        this.cachedValue = (Boolean)super.getValue() != false && this.getVisible().get() != false;
        return set;
    }

    public BooleanSettingHud onAction(Runnable action) {
        return (BooleanSettingHud)super.onAction(() -> {
            action.run();
            this.cachedValue = (Boolean)super.getValue() != false && this.getVisible().get() != false;
        });
    }

    public BooleanSetting onSetVisible(Runnable action) {
        return (BooleanSetting)super.onSetVisible(action);
    }

    @Override
    public Boolean getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = (Boolean)super.getValue();
        }
        return this.cachedValue != false && this.getVisible().get() != false;
    }
}

