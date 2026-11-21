package dev.swap;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum SwapItem {
	SHIELD("Shield", Items.SHIELD),
	GOLDEN_APPLE("Gapple", Items.ENCHANTED_GOLDEN_APPLE),
	TOTEM("Totem", Items.TOTEM_OF_UNDYING),
	PLAYER_HEAD("PlayerHead", Items.PLAYER_HEAD),
	FIREWORK("Firework", Items.FIREWORK_ROCKET);

	private final String id;
	private final Item item;

	SwapItem(String id, Item item) {
		this.id = id;
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public String toString() {
		return id;
	}
}
