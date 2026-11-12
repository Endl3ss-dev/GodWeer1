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

@Environment(value=EnvType.CLIENT)
public class ListSetting<T>
extends Setting<T> {
    public List<T> values;
    private T cachedValue;

    @SafeVarargs
    public ListSetting(Module parent, String name, T ... values) {
        super(parent, name, values[0]);
        this.values = List.of(values);
        this.set((Object)values[0]);
        this.cachedValue = values[0];
    }

    public int getIndex() {
        int index = 0;
        for (T value : this.values) {
            if (value.equals(this.getValue())) {
                return index;
            }
            ++index;
        }
        return 0;
    }

    public boolean is(T value) {
        return this.getValue().equals(value);
    }

    @Override
    public ListSetting<T> set(T value) {
        ListSetting set = (ListSetting)super.set(value);
        this.cachedValue = super.getValue();
        return set;
    }

    public ListSetting<T> setAsObject(Object value) {
        ListSetting set = (ListSetting)super.set(value);
        this.cachedValue = super.getValue();
        return set;
    }

    @Override
    public ListSetting<T> setVisible(Supplier<Boolean> value) {
        return (ListSetting)super.setVisible(value);
    }

    @Override
    public ListSetting<T> onAction(Runnable action) {
        return (ListSetting)super.onAction(() -> {
            action.run();
            this.cachedValue = super.getValue();
        });
    }

    @Override
    public ListSetting<T> onSetVisible(Runnable action) {
        return (ListSetting)super.onSetVisible(action);
    }

    @Override
    public T getValue() {
        if (this.cachedValue == null) {
            this.cachedValue = super.getValue();
        }
        return this.cachedValue;
    }
}

