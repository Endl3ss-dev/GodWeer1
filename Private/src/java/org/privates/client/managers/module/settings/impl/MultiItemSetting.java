package org.privates.client.managers.module.settings.impl;

import net.minecraft.item.Item;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MultiItemSetting extends Setting<List<Item>> {

    public MultiItemSetting(Module parent, String name, List<Item> value) {
        super(parent, name, value);
    }

    public boolean contains(Item item) {
        return getValue().contains(item);
    }

    public void addItem(Item item) {
        if (!contains(item)) {
            List<Item> updatedList = new ArrayList<>(getValue());
            updatedList.add(item);
            set(updatedList);
        }
    }

    public void removeItem(Item item) {
        if (contains(item)) {
            List<Item> updatedList = new ArrayList<>(getValue());
            updatedList.remove(item);
            set(updatedList);
        }
    }

    public void toggle(Item item) {
        if (contains(item)) {
            removeItem(item);
        } else {
            addItem(item);
        }
    }

    @Override
    public MultiItemSetting setVisible(Supplier<Boolean> value) {
        return (MultiItemSetting) super.setVisible(value);
    }

    @Override
    public MultiItemSetting onAction(Runnable action) {
        return (MultiItemSetting) super.onAction(action);
    }
}