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

@Environment(value=EnvType.CLIENT)
public class DelimiterSetting
extends Setting<String> {
    public DelimiterSetting(Module parent, String name) {
        super(parent, name, "");
    }

    public DelimiterSetting setVisible(Supplier<Boolean> value) {
        return (DelimiterSetting)super.setVisible(value);
    }

    public DelimiterSetting onAction(Runnable action) {
        return (DelimiterSetting)super.onAction(action);
    }

    public DelimiterSetting onSetVisible(Runnable action) {
        return (DelimiterSetting)super.onSetVisible(action);
    }

    @Override
    public String getValue() {
        return (String)super.getValue();
    }
}

