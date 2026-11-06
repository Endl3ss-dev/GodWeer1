/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;

@Environment(value=EnvType.CLIENT)
public class ModeSetting
extends Setting<String> {
    public List<String> values;
    private String cachedValue;
    public Animation animation = new EaseInOutQuad(300, 1.0);
    public Animation animation2 = new EaseInOutQuad(300, 1.0);
    public Animation animation3 = new EaseInOutQuad(300, 1.0);
    public boolean opened;

    public ModeSetting(Module parent, String name, String ... values) {
        this(parent, name, ModeSetting.dedupe(values));
    }

    private ModeSetting(Module parent, String name, List<String> values) {
        super(parent, name, values.get(0));
        this.values = values;
        this.set(values.get(0));
        this.cachedValue = values.get(0);
    }

    static List<String> dedupe(String[] values) {
        ArrayList<String> out = new ArrayList<String>(values.length);
        for (String value : values) {
            if (value == null) continue;
            boolean duplicate = false;
            for (String existing : out) {
                if (!existing.equalsIgnoreCase(value)) continue;
                duplicate = true;
                break;
            }
            if (duplicate) continue;
            out.add(value);
        }
        if (out.isEmpty()) {
            throw new IllegalArgumentException("ModeSetting requires at least one value");
        }
        return List.copyOf(out);
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

    public ModeSetting set(String value) {
        this.cachedValue = value;
        ModeSetting set = (ModeSetting)super.set(value);
        this.cachedValue = (String)super.getValue();
        return set;
    }

    public ModeSetting setVisible(Supplier<Boolean> value) {
        return (ModeSetting)super.setVisible(value);
    }

    public ModeSetting onAction(Runnable action) {
        return (ModeSetting)super.onAction(() -> {
            action.run();
            this.cachedValue = (String)super.getValue();
        });
    }

    public ModeSetting onSetVisible(Runnable action) {
        return (ModeSetting)super.onSetVisible(action);
    }

    @Override
    public String getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = (String)super.getValue();
        }
        return this.cachedValue;
    }
}

