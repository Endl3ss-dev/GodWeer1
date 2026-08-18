package nuclear.module.impl.other;

import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventDropItem;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.MultiBoxSetting;
import nuclear.utils.ClientUtils;

@ModuleDescriptor(name = "LockSlot", type = ModuleCategory.Other, desc = "Отключает выброс предметов из выбранных слотов")
public class LockSlot extends Module {
    private static final long MESSAGE_DELAY_MS = 500L;

    private final MultiBoxSetting lockedSlots = new MultiBoxSetting(
        "Слоты",
        new BooleanSetting("1", false),
        new BooleanSetting("2", false),
        new BooleanSetting("3", false),
        new BooleanSetting("4", false),
        new BooleanSetting("5", false),
        new BooleanSetting("6", false),
        new BooleanSetting("7", false),
        new BooleanSetting("8", false),
        new BooleanSetting("9", false)
    );
    private final BooleanSetting notify = new BooleanSetting("Уведомлять", true);

    private long lastMessageTime;

    public LockSlot() {
        addSettings(lockedSlots, notify);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventDropItem) || mc.player == null) {
            return false;
        }

        int slot = mc.player.inventory.currentItem;
        if (!lockedSlots.get(Integer.toString(slot + 1))) {
            return false;
        }

        event.cancel();
        if (notify.get() && System.currentTimeMillis() - lastMessageTime > MESSAGE_DELAY_MS) {
            lastMessageTime = System.currentTimeMillis();
            ClientUtils.sendMessage("Слот " + (slot + 1) + " заблокирован от выброса");
        }
        return false;
    }
}
