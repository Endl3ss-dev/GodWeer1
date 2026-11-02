package tech.lightclient.modules.api.settings.impl;

import tech.lightclient.modules.api.settings.api.Setting;

import java.util.function.Supplier;

public class BindSetting extends Setting<Integer> {

    public BindSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> bool) {
        return (BindSetting) super.setVisible(bool);
    }
}
