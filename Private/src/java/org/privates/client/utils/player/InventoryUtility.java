package org.privates.client.utils.player;

import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.Hand;

public class InventoryUtility {
    private static final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();

    public static void clickSlotId(int slotId, int button, ClickType clickType, boolean shift) {
        if (mc.player == null || mc.player.openContainer == null) return;

        Container container = mc.player.openContainer;
        if (slotId < 0 || slotId >= container.inventorySlots.size()) return;

        mc.playerController.windowClick(
                container.windowId,
                slotId,
                button,
                clickType,
                mc.player
        );
    }

    public static void clickSlot(Slot slot, int button, ClickType clickType, boolean shift) {
        if (slot == null || mc.player == null) return;
        clickSlotId(slot.slotNumber, button, clickType, shift);
    }

    public static void swapHand(Slot slot, Hand hand, boolean shift) {
        if (slot == null || mc.player == null) return;

        int hotbarSlot = hand == Hand.OFF_HAND ? 40 : mc.player.inventory.currentItem + 36;
        clickSlot(slot, hotbarSlot, ClickType.SWAP, shift);
    }

    public static void swapHand(int slotNumber, Hand hand, boolean shift) {
        if (mc.player == null || mc.player.openContainer == null) return;

        Container container = mc.player.openContainer;
        if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) return;

        Slot slot = container.getSlot(slotNumber);
        swapHand(slot, hand, shift);
    }
}