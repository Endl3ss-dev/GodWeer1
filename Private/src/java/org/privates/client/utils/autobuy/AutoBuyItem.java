package org.privates.client.utils.autobuy;

import net.minecraft.item.Item;


public abstract class AutoBuyItem {
    public Item item;
    public int price;
    public int sellPrice;
    protected int savedPrice;


    public String getName() {
        return item.getTranslationKey();
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public void savePrice() {
        this.savedPrice = this.price;
    }

    public int getSavedPrice() {
        return savedPrice;
    }
}