package dev.swap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public final class InventorySearch {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private InventorySearch() {
	}

	public static int findRegularItem(Item item) {
		if (MC.player == null) {
			return -1;
		}
		for (int i = 0; i < 9; i++) {
			if (MC.player.getInventory().getStack(i).isOf(item)) {
				return i;
			}
		}
		for (int i = 9; i < 36; i++) {
			if (MC.player.getInventory().getStack(i).isOf(item)) {
				return i;
			}
		}
		return -1;
	}

	public static int findEnchantedTotemFirst() {
		if (MC.player == null) {
			return -1;
		}
		int enchantedSlot = -1;
		int regularSlot = -1;
		for (int i = 0; i < 36; i++) {
			ItemStack stack = MC.player.getInventory().getStack(i);
			if (!stack.isOf(Items.TOTEM_OF_UNDYING)) {
				continue;
			}
			if (isEnchanted(stack)) {
				enchantedSlot = i;
				break;
			}
			if (regularSlot == -1) {
				regularSlot = i;
			}
		}
		return enchantedSlot != -1 ? enchantedSlot : regularSlot;
	}

	public static int findBestItem(Item item) {
		if (item == Items.TOTEM_OF_UNDYING) {
			return findEnchantedTotemFirst();
		}
		return findRegularItem(item);
	}

	public static int findCerberusSphere() {
		if (MC.player == null) {
			return -1;
		}
		for (int i = 0; i < 36; i++) {
			if (isCerberusSphere(MC.player.getInventory().getStack(i))) {
				return i;
			}
		}
		return -1;
	}

	public static int findItemSlot(Item item) {
		if (MC.player == null) {
			return -1;
		}
		for (int i = 0; i < 36; i++) {
			if (MC.player.getInventory().getStack(i).isOf(item)) {
				return i;
			}
		}
		return -1;
	}

	public static int findElytraSlot() {
		if (MC.player == null) {
			return -1;
		}
		for (int i = 9; i < 36; i++) {
			if (MC.player.getInventory().getStack(i).isOf(Items.ELYTRA)) {
				return i;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (MC.player.getInventory().getStack(i).isOf(Items.ELYTRA)) {
				return i;
			}
		}
		return -1;
	}

	public static int findChestplateSlot() {
		if (MC.player == null) {
			return -1;
		}
		for (int i = 9; i < 36; i++) {
			if (isChestplate(MC.player.getInventory().getStack(i))) {
				return i;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (isChestplate(MC.player.getInventory().getStack(i))) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isChestplate(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return stack.isOf(Items.DIAMOND_CHESTPLATE)
				|| stack.isOf(Items.NETHERITE_CHESTPLATE)
				|| stack.isOf(Items.IRON_CHESTPLATE)
				|| stack.isOf(Items.GOLDEN_CHESTPLATE)
				|| stack.isOf(Items.CHAINMAIL_CHESTPLATE)
				|| stack.isOf(Items.LEATHER_CHESTPLATE);
	}

	public static boolean isCerberusSphere(ItemStack stack) {
		if (stack.isEmpty() || !stack.isOf(Items.PLAYER_HEAD)) {
			return false;
		}
		NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
		if (custom == null) return false;
		NbtCompound tag = custom.copyNbt();
		return tag.getCompound("SkullOwner")
				.flatMap(c -> c.getCompound("Properties"))
				.flatMap(c -> c.getList("textures"))
				.filter(list -> !list.isEmpty())
				.flatMap(list -> list.getCompound(0))
				.flatMap(c -> c.getString("Value"))
				.map(v -> v.equals(SphereTextures.CERBERUS))
				.orElse(false);
	}

	public static boolean isEnchanted(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		var enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
		var storedEnchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
		return (enchantments != null && !enchantments.isEmpty())
				|| (storedEnchantments != null && !storedEnchantments.isEmpty());
	}

	public static Text itemName(ItemStack stack) {
		return stack.getName();
	}
}
