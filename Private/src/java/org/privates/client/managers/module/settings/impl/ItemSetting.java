package org.privates.client.managers.module.settings.impl;

import net.minecraft.item.Item;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.function.Supplier;

public class ItemSetting extends Setting<Item> {
    private Item cachedValue;

    public ItemSetting(Module parent, String name, Item value) {
        super(parent, name, value);
        this.cachedValue = value;
    }

    @Override
    public ItemSetting set(Item value) {
        ItemSetting set = (ItemSetting) super.set(value);
        this.cachedValue = super.getValue();
        return set;
    }

    @Override
    public ItemSetting setVisible(Supplier<Boolean> value) {
        return (ItemSetting) super.setVisible(value);
    }

    @Override
    public ItemSetting onAction(Runnable action) {
        return (ItemSetting) super.onAction(() -> {
            action.run();
            this.cachedValue = super.getValue();
        });
    }

    @Override
    public ItemSetting onSetVisible(Runnable action) {
        return (ItemSetting) super.onSetVisible(action);
    }

    @Override
    public Item getValue() {
        if (cachedValue == null) {
            cachedValue = super.getValue();
        }
        return cachedValue;
    }
}