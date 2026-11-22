package tech.lightclient.client.utils.player.swap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;

public final class SwapInventory {
    private static final MinecraftClient MC = MinecraftClient.getInstance();

    private SwapInventory() {
    }

    public static void closeScreen() {
        if (MC.player == null) {
            return;
        }
        PlayerInventoryUtil.closeScreen(MC.player.currentScreenHandler instanceof PlayerScreenHandler);
    }

    public static void swapHand(Slot slot, Hand hand, boolean updateInventory) {
        if (slot == null || slot.id == -1) {
            return;
        }
        PlayerInventoryUtil.swapHand(slot, hand, false, updateInventory);
    }

    /** Мгновенное перемещение предмета между слотами (без остановки движения). */
    public static void swapSlots(int from, int to, boolean updateInventory) {
        if (from == -1 || to == -1 || from == to) {
            return;
        }
        PlayerInventoryUtil.swapItemsDirect(from, to, updateInventory);
    }
}
