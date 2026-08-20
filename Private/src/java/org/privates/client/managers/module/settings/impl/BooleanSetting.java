package org.privates.client.managers.module.settings.impl;

import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {
    private Boolean cachedValue;

    public BooleanSetting(String name, Boolean value) {
        super(name, value);
        cachedValue = value;
    }

    private BooleanSetting(String name, Boolean value, String description) {
        super(name, value, description);
        cachedValue = value;
    }

    public BooleanSetting(Module parent, String name, Boolean value) {
        super(parent, name, value);
        cachedValue = value;
    }

    public BooleanSetting(Module parent, String name, Boolean value, String description) {
        super(parent, name, value, description);
        cachedValue = value;
    }

    public static BooleanSetting of(String name, Boolean value) {
        return new BooleanSetting(name, value);
    }

    public static BooleanSetting of(String name, Boolean value, String description) {
        return new BooleanSetting(name, value, description);
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> value) {
        return (BooleanSetting) super.setVisible(value);
    }

    public BooleanSetting setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public BooleanSetting set(Boolean value) {
        BooleanSetting set = (BooleanSetting) super.set(value);
        this.cachedValue = super.getValue() && getVisible().get();
        return set;
    }

    @Override
    public BooleanSetting onAction(Runnable action) {
        return (BooleanSetting) super.onAction(() -> {
            action.run();
            this.cachedValue = super.getValue() && getVisible().get();
        });
    }

    @Override
    public BooleanSetting onSetVisible(Runnable action) {
        return (BooleanSetting) super.onSetVisible(action);
    }

    @Override
    public Boolean getValue() {
        if (cachedValue == null) {
            cachedValue = super.getValue();
        }
        return cachedValue && getVisible().get();
    }
}