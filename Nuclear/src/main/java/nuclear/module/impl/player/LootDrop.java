package nuclear.module.impl.player;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.module.settings.imp.TextSetting;
import nuclear.utils.world.InventoryUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ModuleDescriptor(
    name = "LootDrop",
    type = ModuleCategory.Player,
    desc = "Drops low-value inventory items when a configured valuable drop is nearby"
)
public class LootDrop extends Module {
    private final TextSetting keepList = new TextSetting("Keep names", "totem,elytra,crystal,shulker,diamond,netherite");
    private final SliderSetting pickupRange = new SliderSetting("Pickup range", 6.0f, 2.0f, 16.0f, 0.5f);
    private final SliderSetting delayMs = new SliderSetting("Delay", 250.0f, 50.0f, 2000.0f, 50.0f);
    private final BooleanSetting onlyWhenFull = new BooleanSetting("Only when full", true);
    private final BooleanSetting keepEnchanted = new BooleanSetting("Keep enchanted", true);

    private long lastDropTime;

    public LootDrop() {
        addSettings(keepList, pickupRange, delayMs, onlyWhenFull, keepEnchanted);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }

        if (onlyWhenFull.get() && mc.player.inventory.getFirstEmptyStack() != -1) {
            return false;
        }
        if (System.currentTimeMillis() - lastDropTime < delayMs.getValue().longValue()) {
            return false;
        }
        if (!hasValuableDropNearby()) {
            return false;
        }

        int trashSlot = findTrashSlot();
        if (trashSlot == -1) {
            return false;
        }

        InventoryUtils.dropItem(toContainerSlot(trashSlot));
        lastDropTime = System.currentTimeMillis();
        return false;
    }

    private boolean hasValuableDropNearby() {
        double maxDistance = pickupRange.getValue().doubleValue();
        for (ItemEntity entity : mc.world.getEntitiesWithinAABB(ItemEntity.class, mc.player.getBoundingBox().grow(maxDistance))) {
            if (entity == null || entity.getItem().isEmpty()) {
                continue;
            }
            if (matchesKeepRule(entity.getItem())) {
                return true;
            }
        }
        return false;
    }

    private int findTrashSlot() {
        for (int slot = 9; slot < 36; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (isTrash(stack)) {
                return slot;
            }
        }
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (slot != mc.player.inventory.currentItem && isTrash(stack)) {
                return slot;
            }
        }
        return -1;
    }

    private boolean isTrash(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (keepEnchanted.get() && stack.isEnchanted()) {
            return false;
        }
        return !matchesKeepRule(stack);
    }

    private boolean matchesKeepRule(ItemStack stack) {
        String displayName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        String registryName = stack.getItem().getRegistryName() == null
            ? ""
            : stack.getItem().getRegistryName().toString().toLowerCase(Locale.ROOT);

        for (String token : getKeepTokens()) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && (displayName.contains(trimmed) || registryName.contains(trimmed))) {
                return true;
            }
        }
        return false;
    }

    private List<String> getKeepTokens() {
        return Arrays.asList(keepList.get().toLowerCase(Locale.ROOT).split("[,;]"));
    }

    private int toContainerSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }
}
