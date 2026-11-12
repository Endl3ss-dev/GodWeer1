package tech.lightclient.modules.impl.utils;

import tech.lightclient.client.utils.config.InventoryHelperStorage;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

@FunctionAdd(name = "InventoryHelper", alias = "Inventory Helper", category = Category.Utilities,
        description = "Saves inventory layouts and restores them with one click")
public class InventoryHelper extends Function {
    private static final int FIRST_PLAYER_SLOT = 9;
    private static final int LAST_PLAYER_SLOT = 44;
    /** The active operation re-checks inventory state after every server-acknowledged move. */
    private static SortSession activeSort;
    public static final ModeSetting speed = new ModeSetting(
            "Скорость", "Быстрая", "Медленная", "Быстрая", "Очень быстрая"
    );

    public InventoryHelper() {
        addSettings(speed);
    }

    /** Creates/updates a layout from the current player inventory. */
    public static boolean saveLayout(String name) {
        List<Slot> slots = getPlayerSlots();
        return InventoryHelperStorage.saveFromInventory(name, slots) != null;
    }

    /** Applies the selected layout using normal inventory swaps. */
    public static boolean sortSelectedLayout() {
        if (mc.player == null || mc.interactionManager == null) return false;
        InventoryHelperStorage.Layout layout = InventoryHelperStorage.getSelectedLayout();
        if (layout == null || layout.getItems().size() != 36) return false;
        if (getPlayerSlots().size() != 36 || !mc.player.currentScreenHandler.getCursorStack().isEmpty()) return false;

        // Do not batch a plan based on stale server inventory data. A session picks a source for
        // the required target slot only after the preceding three-click swap has been confirmed.
        PlayerUtils.script.cleanup();
        activeSort = new SortSession(layout.getItems(), mc.player.currentScreenHandler.syncId, getClickPause(), getConfirmPause());
        return true;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (activeSort != null && !activeSort.tick()) {
            activeSort = null;
        }
    }

    @Override
    public void onDisable() {
        activeSort = null;
        super.onDisable();
    }

    /** Selects a saved layout, making it the target for the Sort button. */
    public static boolean selectLayout(String name) {
        return InventoryHelperStorage.select(name);
    }

    private static int getClickPause() {
        return switch (speed.get()) {
            case "Медленная" -> 8;
            case "Очень быстрая" -> 1;
            default -> 4;
        };
    }

    private static int getConfirmPause() {
        return switch (speed.get()) {
            case "Медленная" -> 12;
            case "Очень быстрая" -> 2;
            default -> 6;
        };
    }

    private static List<Slot> getPlayerSlots() {
        if (mc.player == null || mc.player.currentScreenHandler == null) return List.of();
        List<Slot> result = new ArrayList<>(36);
        for (int id = FIRST_PLAYER_SLOT; id <= LAST_PLAYER_SLOT; id++) {
            if (id >= mc.player.currentScreenHandler.slots.size()) return List.of();
            result.add(mc.player.currentScreenHandler.getSlot(id));
        }
        return result;
    }

    private static String itemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        return net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString();
    }

    private static boolean sameItem(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }

    /** Follows the displaced-item chain until every item reaches its configured destination. */
    private static final class SortSession {
        private final List<InventoryHelperStorage.SlotItem> desired;
        private final int syncId;
        private final int clickPauseTicks;
        private final int confirmPauseTicks;
        private int targetSlotId;
        private int pause;
        private Stage stage = Stage.FIND_TARGET;

        private SortSession(List<InventoryHelperStorage.SlotItem> desired, int syncId, int clickPauseTicks, int confirmPauseTicks) {
            this.desired = desired;
            this.syncId = syncId;
            this.clickPauseTicks = clickPauseTicks;
            this.confirmPauseTicks = confirmPauseTicks;
        }

        /** @return true while the sort is still running. */
        private boolean tick() {
            if (mc.player == null || mc.interactionManager == null || mc.player.currentScreenHandler.syncId != syncId) {
                return false;
            }
            if (pause-- > 0) return true;

            List<Slot> slots = getPlayerSlots();
            if (slots.size() != 36) return false;

            switch (stage) {
                case FIND_TARGET -> {
                    // A sort never starts with a held stack: that stack belongs to the player.
                    if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) return false;
                    int targetIndex = findFirstMissingTarget(slots);
                    if (targetIndex < 0) return false;
                    int sourceIndex = findMovableSource(slots, targetIndex);
                    if (sourceIndex < 0) return false;
                    // Pick only the item that is needed, then start a deterministic chain.
                    PlayerInventoryUtil.clickSlot(slots.get(sourceIndex).id, 0, SlotActionType.PICKUP);
                    targetSlotId = slots.get(targetIndex).id;
                    stage = Stage.PLACE_CURSOR;
                    pause = clickPauseTicks;
                }
                case PLACE_CURSOR -> {
                    // Place the held item directly into its saved slot. This first click is
                    // never a temporary move; the configured target is used immediately.
                    PlayerInventoryUtil.clickSlot(targetSlotId, 0, SlotActionType.PICKUP);
                    stage = Stage.FOLLOW_CURSOR;
                    pause = clickPauseTicks;
                }
                case FOLLOW_CURSOR -> {
                    ItemStack cursor = mc.player.currentScreenHandler.getCursorStack();
                    if (cursor.isEmpty()) {
                        // The final placement was into an empty configured slot.
                        PlayerInventoryUtil.updateSlots();
                        stage = Stage.FIND_TARGET;
                        pause = confirmPauseTicks;
                        return true;
                    }

                    int nextTarget = findDestinationForCursor(slots);
                    if (nextTarget < 0) return false;
                    // The displaced item goes straight to the slot where that item belongs.
                    // Its former occupant becomes the next cursor item, continuing the chain.
                    targetSlotId = slots.get(nextTarget).id;
                    PlayerInventoryUtil.clickSlot(targetSlotId, 0, SlotActionType.PICKUP);
                    pause = clickPauseTicks;
                }
            }
            return true;
        }

        private int findFirstMissingTarget(List<Slot> slots) {
            for (int index = 0; index < desired.size(); index++) {
                if (desired.get(index).isEmpty()) continue;
                if (!matchesDesired(slots.get(index).getStack(), desired.get(index))) return index;
            }
            return -1;
        }

        private int findMovableSource(List<Slot> slots, int target) {
            for (int candidate = 0; candidate < slots.size(); candidate++) {
                if (candidate == target || !matchesDesired(slots.get(candidate).getStack(), desired.get(target))) continue;
                // Do not remove an item which is already occupying its own configured slot.
                if (desired.get(candidate).isEmpty() || !matchesDesired(slots.get(candidate).getStack(), desired.get(candidate))) return candidate;
            }
            return -1;
        }

        private int findDestinationForCursor(List<Slot> slots) {
            // First, give the displaced item its exact configured slot.
            for (int index = 0; index < desired.size(); index++) {
                if (!desired.get(index).isEmpty() && matchesDesired(mc.player.currentScreenHandler.getCursorStack(), desired.get(index))
                        && !matchesDesired(slots.get(index).getStack(), desired.get(index))) {
                    return index;
                }
            }
            // Items which are not in the layout are kept in any currently empty slot that
            // the saved layout leaves free; they are never discarded or left on the cursor.
            for (int index = 0; index < desired.size(); index++) {
                if (desired.get(index).isEmpty() && slots.get(index).getStack().isEmpty()) return index;
            }
            return -1;
        }

        /**
         * All normal items are compared by item id. Potions share the same id despite having
         * different effects, so their visible potion name is part of their layout identity.
         */
        private static boolean matchesDesired(ItemStack stack, InventoryHelperStorage.SlotItem wanted) {
            if (wanted == null || wanted.isEmpty()) return stack == null || stack.isEmpty();
            if (stack == null || stack.isEmpty() || !wanted.getItemId().equals(itemId(stack))) return false;
            if (!isPotion(wanted.getItemId())) return true;
            return wanted.getDisplayName().equals(stack.getName().getString());
        }

        private static boolean isPotion(String itemId) {
            return "minecraft:potion".equals(itemId)
                    || "minecraft:splash_potion".equals(itemId)
                    || "minecraft:lingering_potion".equals(itemId);
        }

        private enum Stage { FIND_TARGET, PLACE_CURSOR, FOLLOW_CURSOR }
    }
}
