package org.privates.client.utils.autobuy;

import lombok.Getter;
import net.minecraft.item.ItemStack;


@Getter
public class HistoryItem {
    public int price;
    public ItemStack stack;
    public boolean purchased = true;

    public HistoryItem(int price, ItemStack stack) {
        this.price = price;
        this.stack = stack;
    }
}