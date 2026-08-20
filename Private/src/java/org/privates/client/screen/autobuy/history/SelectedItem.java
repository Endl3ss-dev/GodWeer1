package org.privates.client.screen.autobuy.history;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

@Getter
@Setter
public class SelectedItem {

    private final float width, height;
    private final String name;
    private final ItemStack itemStack;
    private final long time;

    private int price, sellPrice;
    private int anim;
    private float animV, selectedV, autoSellV, autoSetupV;
    private boolean selected = false, selectedBuy = false, selectedSell = false, autoSell = false, autoSetup = false;

    public SelectedItem(ItemStack itemStack, String name, int price, int sellPrice, long time) {
        this.itemStack = itemStack;
        this.name = name;
        this.price = price;
        this.sellPrice = sellPrice;
        this.width = 123 - 8; // 123 - 4x2
        this.height = 16;
        this.time = time;
    }
}
