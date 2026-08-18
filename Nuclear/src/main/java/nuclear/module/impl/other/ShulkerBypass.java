package nuclear.module.impl.other;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventClickWindow;
import nuclear.control.events.impl.player.EventWindowClick;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;

@ModuleDescriptor(
    name = "ShulkerBypass",
    type = ModuleCategory.Other,
    desc = "Routes shulker clicks through a hotbar swap to avoid simple server-side restrictions"
)
public class ShulkerBypass extends Module {
    private final BooleanSetting nestedOnly = new BooleanSetting("Only nested shulkers", true);
    private final SliderSetting hotbarSlot = new SliderSetting("Bypass hotbar slot", 8.0f, 0.0f, 8.0f, 1.0f);

    public ShulkerBypass() {
        addSettings(nestedOnly, hotbarSlot);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || !(mc.player.openContainer instanceof ShulkerBoxContainer)) {
            return false;
        }

        if (event instanceof EventClickWindow) {
            handleClickWindow((EventClickWindow) event);
        } else if (event instanceof EventWindowClick) {
            handleWindowClick((EventWindowClick) event);
        }
        return false;
    }

    private void handleClickWindow(EventClickWindow event) {
        if (!isHandledClick(event.type, event.slotId)) {
            return;
        }

        event.cancel();
        bypassSlot(event.windowId, event.slotId);
    }

    private void handleWindowClick(EventWindowClick event) {
        if (event.getClickStage() != EventWindowClick.ClickStage.PRE) {
            return;
        }
        if (!isHandledClick(event.getClickType(), event.getSlot())) {
            return;
        }

        event.cancel();
        bypassSlot(event.getWindowId(), event.getSlot());
    }

    private boolean isHandledClick(ClickType clickType, int slot) {
        if (slot < 0 || slot >= mc.player.openContainer.inventorySlots.size()) {
            return false;
        }
        if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
            return false;
        }

        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();
        if (stack.isEmpty() || !isShulker(stack)) {
            return false;
        }

        return !nestedOnly.get() || mc.player.openContainer instanceof ShulkerBoxContainer;
    }

    private boolean isShulker(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem)) {
            return false;
        }
        Block block = ((BlockItem) stack.getItem()).getBlock();
        return block instanceof ShulkerBoxBlock;
    }

    private void bypassSlot(int windowId, int slot) {
        int button = hotbarSlot.getValue().intValue();
        mc.playerController.windowClick(windowId, slot, button, ClickType.SWAP, mc.player);
    }
}
