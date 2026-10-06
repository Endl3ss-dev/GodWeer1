package godweer.utils.item;

import godweer.modules.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ConsumableUseController {

    private static final int NO_SLOT = -1;
    private static final List<ConsumableUseController> CONTROLLERS = new ArrayList<>(3);

    private static Object lastLevel;
    private static Object lastOwner;
    private static long lastUseTick = Long.MIN_VALUE;

    private final Object owner;

    private boolean active;
    private int restoreMenuSlot = NO_SLOT;
    private int restoreButton = NO_SLOT;
    private int restoreSelectedSlot = NO_SLOT;
    private int temporarySelectedSlot = NO_SLOT;

    public ConsumableUseController(Object owner) {
        this.owner = owner;
        CONTROLLERS.add(this);
    }

    public static void clearAll() {
        for (int index = 0; index < CONTROLLERS.size(); index++) {
            CONTROLLERS.get(index).clearState();
        }
        CONTROLLERS.clear();
        lastLevel = null;
        lastOwner = null;
        lastUseTick = Long.MIN_VALUE;
    }

    public static void tickDisabledControllers(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        for (int index = 0, size = CONTROLLERS.size(); index < size; index++) {
            ConsumableUseController controller = CONTROLLERS.get(index);
            if (!controller.active || controller.owner instanceof Module module && module.isEnable()) {
                continue;
            }
            if (player == null || minecraft.level == null) {
                controller.clearState();
            } else {
                controller.tick(minecraft, player);
            }
        }
    }

    public boolean useInstant(Minecraft minecraft, LocalPlayer player, int inventorySlot, boolean swing) {
        if (active || !canStart(minecraft, player) || !claimUseTick(minecraft)) {
            return false;
        }

        InteractionHand hand = prepare(minecraft, player, inventorySlot);
        if (hand == null) {
            return false;
        }

        try {
            InteractionResult result = minecraft.gameMode.useItem(player, hand);
            if (swing && result.consumesAction()) {
                player.swing(hand);
            }
            return result.consumesAction();
        } finally {
            restore(minecraft, player);
        }
    }

    public boolean useAndHold(Minecraft minecraft, LocalPlayer player, int inventorySlot, boolean swing) {
        if (active || !canStart(minecraft, player) || !claimUseTick(minecraft)) {
            return false;
        }

        InteractionHand hand = prepare(minecraft, player, inventorySlot);
        if (hand == null) {
            return false;
        }

        boolean keepPrepared = false;
        try {
            InteractionResult result = minecraft.gameMode.useItem(player, hand);
            if (swing && result.consumesAction()) {
                player.swing(hand);
            }

            boolean accepted = result.consumesAction() || player.isUsingItem();
            if (accepted && player.isUsingItem()) {
                active = true;
                keepPrepared = true;
            }
            return accepted;
        } finally {
            if (!keepPrepared) {
                restore(minecraft, player);
            }
        }
    }

    public boolean tick(Minecraft minecraft, LocalPlayer player) {
        if (!active) {
            return false;
        }
        if (player.isUsingItem()
                || restoreMenuSlot != NO_SLOT && player.containerMenu != player.inventoryMenu) {
            return true;
        }

        restore(minecraft, player);
        return true;
    }

    public void cancel(Minecraft minecraft, LocalPlayer player) {
        if (!active && restoreMenuSlot == NO_SLOT && restoreSelectedSlot == NO_SLOT) {
            return;
        }
        if (player != null && (player.isUsingItem()
                || restoreMenuSlot != NO_SLOT && player.containerMenu != player.inventoryMenu)) {
            return;
        }
        restore(minecraft, player);
    }

    public void reset() {
        clearState();
    }

    private boolean canStart(Minecraft minecraft, LocalPlayer player) {
        return minecraft != null
                && player != null
                && minecraft.level != null
                && minecraft.gameMode != null
                && player.containerMenu == player.inventoryMenu;
    }

    private boolean claimUseTick(Minecraft minecraft) {
        Object level = minecraft.level;
        long tick = minecraft.level.getGameTime();
        if (level != lastLevel || tick != lastUseTick) {
            lastLevel = level;
            lastUseTick = tick;
            lastOwner = owner;
            return true;
        }
        return lastOwner == owner;
    }

    private InteractionHand prepare(Minecraft minecraft, LocalPlayer player, int inventorySlot) {
        Inventory inventory = player.getInventory();
        if (inventorySlot == ConsumableItemUtil.OFFHAND_SLOT) {
            return player.getOffhandItem().isEmpty() ? null : InteractionHand.OFF_HAND;
        }
        if (inventorySlot < 0 || inventorySlot >= 36) {
            return null;
        }

        ItemStack stack = inventory.getItem(inventorySlot);
        if (stack.isEmpty()) {
            return null;
        }

        if (inventorySlot < 9) {
            if (inventory.selected != inventorySlot) {
                restoreSelectedSlot = inventory.selected;
                temporarySelectedSlot = inventorySlot;
                selectSlot(player, inventory, inventorySlot);
            }
            return InteractionHand.MAIN_HAND;
        }

        restoreMenuSlot = inventorySlot;
        restoreButton = inventory.selected;
        clickSwap(minecraft, player, restoreMenuSlot, restoreButton);
        return InteractionHand.MAIN_HAND;
    }

    private void restore(Minecraft minecraft, LocalPlayer player) {
        if (minecraft != null && player != null && minecraft.gameMode != null) {
            if (restoreMenuSlot != NO_SLOT && player.containerMenu == player.inventoryMenu) {
                clickSwap(minecraft, player, restoreMenuSlot, restoreButton);
            }

            if (restoreSelectedSlot != NO_SLOT
                    && player.getInventory().selected == temporarySelectedSlot) {
                selectSlot(player, player.getInventory(), restoreSelectedSlot);
            }
        }
        clearState();
    }

    private void selectSlot(LocalPlayer player, Inventory inventory, int slot) {
        if (inventory.selected == slot) {
            return;
        }
        inventory.selected = slot;
        player.connection.send(new ServerboundSetCarriedItemPacket(slot));
    }

    private void clickSwap(Minecraft minecraft, LocalPlayer player, int inventorySlot, int button) {
        if (minecraft.gameMode == null || player.containerMenu != player.inventoryMenu) {
            return;
        }

        minecraft.gameMode.handleInventoryMouseClick(
                player.containerMenu.containerId,
                toMenuSlot(inventorySlot),
                button,
                ClickType.SWAP,
                player
        );
    }

    private int toMenuSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }

    private void clearState() {
        active = false;
        restoreMenuSlot = NO_SLOT;
        restoreButton = NO_SLOT;
        restoreSelectedSlot = NO_SLOT;
        temporarySelectedSlot = NO_SLOT;
    }
}
