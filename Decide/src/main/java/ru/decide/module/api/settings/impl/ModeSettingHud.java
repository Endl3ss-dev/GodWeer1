/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings.impl;

import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;

@Environment(value=EnvType.CLIENT)
public class ModeSettingHud
extends Setting<String> {
    public List<String> values;
    private String cachedValue;
    public Animation animation = new EaseInOutQuad(300, 1.0);
    public boolean opened;

    public ModeSettingHud(Module parent, String name, String ... values) {
        this(parent, name, ModeSetting.dedupe(values));
    }

    private ModeSettingHud(Module parent, String name, List<String> values) {
        super(parent, name, values.get(0));
        this.values = values;
        this.set(values.get(0));
        this.cachedValue = values.get(0);
    }

    public int getIndex() {
        int index = 0;
        for (String value : this.values) {
            if (value.equalsIgnoreCase(this.getValue())) {
                return index;
            }
            ++index;
        }
        return 0;
    }

    public boolean is(String value) {
        return this.getValue().equalsIgnoreCase(value) && this.getVisible().get() != false;
    }

    public ModeSettingHud set(String value) {
        ModeSettingHud set = (ModeSettingHud)super.set(value);
        this.cachedValue = (String)super.getValue();
        return set;
    }

    public ModeSettingHud setVisible(Supplier<Boolean> value) {
        return (ModeSettingHud)super.setVisible(value);
    }

    public ModeSettingHud onAction(Runnable action) {
        return (ModeSettingHud)super.onAction(() -> {
            action.run();
            this.cachedValue = (String)super.getValue();
        });
    }

    public ModeSettingHud onSetVisible(Runnable action) {
        return (ModeSettingHud)super.onSetVisible(action);
    }

    @Override
    public String getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = (String)super.getValue();
        }
        return this.cachedValue;
    }
}

