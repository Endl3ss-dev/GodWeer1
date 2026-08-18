package nuclear.module.impl.combat;

import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.TypeList;
import nuclear.module.api.Annotation;
import nuclear.module.api.Module;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.utils.ClientUtils;
import nuclear.utils.StopWatch;
import nuclear.utils.world.InventoryUtils;

@Annotation(name = "ArrowSwap", type = TypeList.Combat, desc = "Swaps arrow types between inventory slots")
public class ArrowSwap extends Module {

    public static final BooleanSetting swapEnabled = new BooleanSetting("Swap", true);

    public final ModeSetting swapTo = new ModeSetting("Swap To", "Tipped", "Normal", "Tipped", "Spectral");
    public final ModeSetting swapFrom = new ModeSetting("Swap From", "Normal", "Normal", "Tipped", "Spectral");
    public final BooleanSetting notification = new BooleanSetting("Swap Notification", true);

    private final StopWatch timer = new StopWatch();
    private static final long SWAP_DELAY_MS = 120L;

    public ArrowSwap() {
        addSettings(swapTo, swapFrom, swapEnabled, notification);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null) {
            return false;
        }

        if (!swapEnabled.get() || !(mc.player.getHeldItemMainhand().getItem() instanceof BowItem)) {
            return false;
        }

        if (!timer.isReached(SWAP_DELAY_MS)) {
            return false;
        }

        String toType = swapTo.get();
        String fromType = swapFrom.get();

        int prioritySlot = findSlot(toType, -1);
        int targetSlot = toType.equals(fromType)
                ? findSlot(fromType, prioritySlot)
                : findSlot(fromType, -1);

        if (prioritySlot == -1 || targetSlot == -1 || prioritySlot == targetSlot) {
            return false;
        }

        swapInventorySlots(prioritySlot, targetSlot);
        timer.reset();

        if (notification.get()) {
            ClientUtils.sendMessage("ArrowSwap: arrows swapped.");
        }

        return false;
    }

    private int findSlot(String arrowType, int skipSlot) {
        Item targetItem = arrowItemForType(arrowType);
        if (targetItem == Items.AIR) {
            return -1;
        }

        for (int i = 0; i < 36; i++) {
            if (i == skipSlot) {
                continue;
            }

            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem) {
                return i;
            }
        }

        return -1;
    }

    private Item arrowItemForType(String arrowType) {
        return switch (arrowType) {
            case "Normal" -> Items.ARROW;
            case "Spectral" -> Items.SPECTRAL_ARROW;
            case "Tipped" -> Items.TIPPED_ARROW;
            default -> Items.AIR;
        };
    }

    private void swapInventorySlots(int firstSlot, int secondSlot) {
        int first = toWindowSlot(firstSlot);
        int second = toWindowSlot(secondSlot);

        InventoryUtils.pickupItem(first, 0);
        InventoryUtils.pickupItem(second, 0);
        InventoryUtils.pickupItem(first, 0);
    }

    private int toWindowSlot(int slot) {
        return slot < 9 ? slot + 36 : slot;
    }
}
