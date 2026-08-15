package antileak.base.client.modules.impl.player;

import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class ChestStealer extends Module {

    public static ChestStealer INSTANCE = new ChestStealer();

    private final FloatSetting delay = new FloatSetting("Задержка", 100.0f, 0.0f, 1000.0f, 1.0f);
    private final BooleanSetting randomize = new BooleanSetting("Рандомизация", false);

    private long lastStealTime;

    public ChestStealer() {
        super("ChestStealer", "Автоматически забирает предметы из сундуков", ModuleCategory.PLAYER);
        addSettings(delay, randomize);
    }

    @EventLink
    public void onUpdate(EventUpdate e) {
        if (mc.player == null || mc.interactionManager == null) return;

        ScreenHandler container = mc.player.currentScreenHandler;
        if (container == null || container == mc.player.playerScreenHandler) return;

        if (!(container instanceof GenericContainerScreenHandler) &&
            !(container instanceof HopperScreenHandler)) return;

        long currentTime = System.currentTimeMillis();
        long ms = (long) delay.get();

        if (currentTime - lastStealTime < ms) return;

        getValidSlot(container).ifPresent(slot -> {
            if (mc.player.currentScreenHandler == container) {
                mc.interactionManager.clickSlot(
                    container.syncId, slot.id, 0,
                    SlotActionType.QUICK_MOVE, mc.player
                );
                lastStealTime = currentTime;
            }
        });
    }

    private Optional<Slot> getValidSlot(ScreenHandler handler) {
        int containerSize = getContainerSize(handler);
        if (containerSize <= 0 || containerSize > handler.slots.size()) return Optional.empty();

        List<Slot> containerSlots = handler.slots.subList(0, containerSize);
        List<Slot> valid = new ArrayList<>();

        for (Slot slot : containerSlots) {
            if (slot.hasStack() && !slot.getStack().isEmpty()) {
                if (!mc.player.getItemCooldownManager().isCoolingDown(slot.getStack())) {
                    valid.add(slot);
                }
            }
        }

        if (valid.isEmpty()) return Optional.empty();

        if (randomize.isState()) {
            return Optional.of(valid.get(ThreadLocalRandom.current().nextInt(valid.size())));
        }
        return Optional.of(valid.get(0));
    }

    private int getContainerSize(ScreenHandler handler) {
        if (handler instanceof GenericContainerScreenHandler container) {
            return container.getInventory().size();
        }
        if (handler instanceof HopperScreenHandler) {
            return 5;
        }
        return 0;
    }

    @Override
    public void onDisable() {
        lastStealTime = 0;
        super.onDisable();
    }
}
