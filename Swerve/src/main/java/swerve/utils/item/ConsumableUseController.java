package swerve.utils.item;

import swerve.modules.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ConsumableUseController {

    private static final int NO_SLOT = -1;
    private static final int EAT_FINISH_TICKS = 30;
    private static final int MAX_RESUME_ATTEMPTS = 5;
    private static final long RESTART_COOLDOWN_MS = 250L;
    private static final List<ConsumableUseController> CONTROLLERS = new ArrayList<>(4);

    private static Object lastLevel;
    private static Object lastOwner;
    private static long lastUseTick = Long.MIN_VALUE;

    private final Object owner;

    private boolean active;
    private int restoreMenuSlot = NO_SLOT;
    private int restoreButton = NO_SLOT;
    private int restoreSelectedSlot = NO_SLOT;
    private int temporarySelectedSlot = NO_SLOT;

    private Item holdItem;
    private InteractionHand holdHand = InteractionHand.MAIN_HAND;
    private int holdHotbarSlot = NO_SLOT;
    private boolean holdSwing;
    private int holdElapsed;
    private int resumeAttempts;
    private long nextStartTimeMs;

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

    public static boolean isAnyActive() {
        for (int index = 0, size = CONTROLLERS.size(); index < size; index++) {
            if (CONTROLLERS.get(index).active) {
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLockedByOther() {
        return !active && isAnyActive();
    }

    public boolean useInstant(Minecraft minecraft, LocalPlayer player, int inventorySlot, boolean swing) {
        if (active || isLockedByOther() || !canStart(minecraft, player) || !claimUseTick(minecraft)) {
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
        if (active || isLockedByOther() || !canStart(minecraft, player)
                || System.currentTimeMillis() < nextStartTimeMs || !claimUseTick(minecraft)) {
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
                holdItem = sourceItem(player, inventorySlot);
                holdHand = hand;
                holdHotbarSlot = hand == InteractionHand.OFF_HAND ? NO_SLOT : player.getInventory().selected;
                holdSwing = swing;
                holdElapsed = 0;
                resumeAttempts = 0;
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
        if (player.isUsingItem()) {
            holdElapsed++;
            return true;
        }
        if (restoreMenuSlot != NO_SLOT && player.containerMenu != player.inventoryMenu) {
            return true;
        }

        if (shouldResume(minecraft, player)) {
            resumeAttempts++;
            if (claimUseTick(minecraft) && minecraft.gameMode != null
                    && minecraft.gameMode.useItem(player, holdHand).consumesAction() && holdSwing) {
                player.swing(holdHand);
            }
            if (player.isUsingItem()) {
                holdElapsed = 0;
                resumeAttempts = 0;
            }
            return true;
        }
        if (resumeAttempts > 0) {
            nextStartTimeMs = System.currentTimeMillis() + RESTART_COOLDOWN_MS;
        }

        restore(minecraft, player);
        return true;
    }

    public void cancel(Minecraft minecraft, LocalPlayer player) {
        nextStartTimeMs = 0L;
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
        nextStartTimeMs = 0L;
        clearState();
    }

    // Ванила доедает любой расходник за 32 тика: остановка раньше — внешнее прерывание, а не финал.
    private boolean shouldResume(Minecraft minecraft, LocalPlayer player) {
        return holdItem != null
                && holdElapsed < EAT_FINISH_TICKS
                && resumeAttempts < MAX_RESUME_ATTEMPTS
                && isOwnerEnabled()
                && minecraft.gameMode != null
                && player.isAlive()
                && player.containerMenu == player.inventoryMenu
                && stillHolding(player);
    }

    private boolean stillHolding(LocalPlayer player) {
        if (holdHand == InteractionHand.OFF_HAND) {
            return player.getOffhandItem().is(holdItem);
        }
        Inventory inventory = player.getInventory();
        return inventory.selected == holdHotbarSlot && inventory.getItem(inventory.selected).is(holdItem);
    }

    private boolean isOwnerEnabled() {
        return !(owner instanceof Module module) || module.isEnable();
    }

    private static Item sourceItem(LocalPlayer player, int inventorySlot) {
        ItemStack stack = inventorySlot == ConsumableItemUtil.OFFHAND_SLOT
                ? player.getOffhandItem()
                : player.getInventory().getItem(inventorySlot);
        return stack.getItem();
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
        holdItem = null;
        holdHand = InteractionHand.MAIN_HAND;
        holdHotbarSlot = NO_SLOT;
        holdSwing = false;
        holdElapsed = 0;
        resumeAttempts = 0;
    }
}