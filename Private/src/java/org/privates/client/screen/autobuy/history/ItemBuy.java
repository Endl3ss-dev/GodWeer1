package org.privates.client.screen.autobuy.history;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

@Getter
@Setter
public class ItemBuy {

    private final float width, height;

    private float x, y;
    private String name;
    private ItemStack itemStack;

    public ItemBuy(ItemStack itemStack, String name, float x, float y) {
        this.itemStack = itemStack;
        this.name = name;
        this.x = x;
        this.y = y;
        this.height = 16;
        this.width = 123 - 8; // 123 - 4x2
    }
}
