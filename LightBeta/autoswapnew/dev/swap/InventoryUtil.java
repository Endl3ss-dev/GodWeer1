package dev.swap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class InventoryUtil {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private InventoryUtil() {
	}

	public static void closeScreen() {
		if (MC.player == null) {
			return;
		}
		if (MC.player.currentScreenHandler instanceof PlayerScreenHandler) {
			MC.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(MC.player.currentScreenHandler.syncId));
		} else {
			MC.player.closeHandledScreen();
		}
	}

	public static void swapHand(Slot slot, net.minecraft.util.Hand hand, boolean updateInventory) {
		if (slot == null || slot.id == -1) {
			return;
		}
		int button = hand == net.minecraft.util.Hand.MAIN_HAND ? MC.player.getInventory().getSelectedSlot() : 40;
		swapHand(slot, button, updateInventory);
	}

	public static void swapHand(Slot slot, int button, boolean updateInventory) {
		clickSlot(slot, button, SlotActionType.SWAP, false);
		if (updateInventory) {
			updateSlots();
		}
	}

	public static void clickSlot(Slot slot, int button, SlotActionType clickType, boolean silent) {
		if (slot != null) {
			clickSlot(slot.id, button, clickType, silent);
		}
	}

	public static void clickSlot(int slotId, int buttonId, SlotActionType clickType, boolean silent) {
		if (MC.player == null || MC.interactionManager == null) {
			return;
		}
		clickSlot(MC.player.currentScreenHandler.syncId, slotId, buttonId, clickType, silent);
	}

	public static void clickSlot(int windowId, int slotId, int buttonId, SlotActionType clickType, boolean silent) {
		if (MC.player == null || MC.interactionManager == null) {
			return;
		}
		MC.interactionManager.clickSlot(windowId, slotId, buttonId, clickType, MC.player);
		if (silent) {
			MC.player.currentScreenHandler.onSlotClick(slotId, buttonId, clickType, MC.player);
		}
	}

	public static void updateSlots() {
	}

	public static Slot getSlot(Item item, Predicate<Slot> stackFilter) {
		return getSlot(item, Comparator.comparingInt(slot -> slot.id), stackFilter);
	}

	public static Slot getSlot(Item item, Comparator<Slot> comparator, Predicate<Slot> filter) {
		return slots()
				.filter(slot -> slot.getStack().isOf(item))
				.filter(filter)
				.max(comparator)
				.orElse(null);
	}

	public static Slot getCerberusSlot() {
		return getSlot(Items.PLAYER_HEAD, slot -> {
			ItemStack stack = slot.getStack();
			if (stack.isEmpty()) {
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
		});
	}

	public static Slot getSphereSlot(SphereMode mode) {
		if (mode == SphereMode.TALISMAN) {
			return getTalismanTotemSlot();
		}
		if (mode == SphereMode.NONE) {
			return null;
		}
		String key = SphereTextures.keyFor(mode);
		return getSlot(Items.PLAYER_HEAD, slot -> {
			ItemStack stack = slot.getStack();
			NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
			if (custom == null) return false;
			NbtCompound tag = custom.copyNbt();
			return tag.getCompound("SkullOwner")
					.flatMap(c -> c.getCompound("Properties"))
					.flatMap(c -> c.getList("textures"))
					.filter(list -> !list.isEmpty())
					.flatMap(list -> list.getCompound(0))
					.flatMap(c -> c.getString("Value"))
					.map(v -> v.equals(key))
					.orElse(false);
		});
	}

	public static Slot getTalismanTotemSlot() {
		if (MC.player == null) {
			return null;
		}
		return MC.player.currentScreenHandler.slots.stream()
				.filter(slot -> slot.getStack().isOf(Items.TOTEM_OF_UNDYING))
				.max(totemComparator())
				.orElse(null);
	}

	public static Comparator<Slot> preferUnenchanted() {
		return Comparator.comparing(slot -> isEnchanted(slot.getStack()));
	}

	public static Predicate<Slot> excludeCerberusAndArmorSlots(Slot cerb) {
		return slot -> slot != cerb && slot.id != 45 && slot.id != 46;
	}

	public static boolean isSkorka(ItemStack stack) {
		NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
		if (custom == null) return false;
		NbtCompound tag = custom.copyNbt();
		return tag.getCompound("SkullOwner")
				.flatMap(c -> c.getCompound("Properties"))
				.flatMap(c -> c.getList("textures"))
				.filter(list -> !list.isEmpty())
				.flatMap(list -> list.getCompound(0))
				.flatMap(c -> c.getString("Value"))
				.map(v -> v.equals(SphereTextures.LEGENDARY))
				.orElse(false);
	}

	public static boolean isPlayerMoving() {
		if (MC.player == null || MC.options == null) {
			return false;
		}
		return MC.options.forwardKey.isPressed()
				|| MC.options.backKey.isPressed()
				|| MC.options.leftKey.isPressed()
				|| MC.options.rightKey.isPressed()
				|| MC.options.jumpKey.isPressed()
				|| MC.player.isSprinting();
	}

	public static Stream<Slot> slots() {
		if (MC.player == null) {
			return Stream.empty();
		}
		return MC.player.currentScreenHandler.slots.stream();
	}

	public static NbtCompound getCustomDataTag(ItemStack stack) {
		NbtComponent custom = stack.get(DataComponentTypes.CUSTOM_DATA);
		return custom == null ? null : custom.copyNbt();
	}

	private static Comparator<Slot> totemComparator() {
		return Comparator.<Slot>comparingInt(slot -> isEnchanted(slot.getStack()) ? 1 : 0)
				.thenComparing((Slot o1, Slot o2) -> compareSphereEffect(o1.getStack(), o2.getStack()))
				.thenComparingInt(slot -> slot.id)
				.reversed();
	}

	private static boolean isEnchanted(ItemStack stack) {
		var enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
		var stored = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
		return (enchantments != null && !enchantments.isEmpty())
				|| (stored != null && !stored.isEmpty());
	}

	private static int compareSphereEffect(ItemStack o1, ItemStack o2) {
		NbtComponent c1 = o1.get(DataComponentTypes.CUSTOM_DATA);
		NbtComponent c2 = o2.get(DataComponentTypes.CUSTOM_DATA);
		NbtCompound nbt1 = c1 == null ? null : c1.copyNbt();
		NbtCompound nbt2 = c2 == null ? null : c2.copyNbt();
		if (nbt1 == null && nbt2 != null) {
			return -1;
		}
		if (nbt1 != null && nbt2 == null) {
			return 1;
		}
		if (nbt1 == null) {
			return 0;
		}
		boolean has1 = nbt1.getCompound("SkullOwner").flatMap(c -> c.getCompound("sphereEffect")).isPresent();
		boolean has2 = nbt2.getCompound("SkullOwner").flatMap(c -> c.getCompound("sphereEffect")).isPresent();
		if (has1 && !has2) {
			return 1;
		}
		if (!has1 && has2) {
			return -1;
		}
		if (!has1) {
			return 0;
		}
		String s1 = nbt1.getCompound("SkullOwner").flatMap(c -> c.getString("sphereEffect")).orElse("");
		String s2 = nbt2.getCompound("SkullOwner").flatMap(c -> c.getString("sphereEffect")).orElse("");
		return Integer.compare(s2.length(), s1.length());
	}
}
