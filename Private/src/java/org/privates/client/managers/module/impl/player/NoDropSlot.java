package org.privates.client.managers.module.impl.player;

import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.EventSlot;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;

@Category2
@ModuleInfo(name = "NoDropSlot", category = Category.PLAYER, description = "Не дает выбрасывать предметы из выбранных слотов")
public class NoDropSlot extends Module {

    private final MultiBooleanSetting slots = new MultiBooleanSetting(this, "Блок. слот",
            new BooleanSetting("Слот 1", false),
            new BooleanSetting("Слот 2", false),
            new BooleanSetting("Слот 3", false),
            new BooleanSetting("Слот 4", false),
            new BooleanSetting("Слот 5", false),
            new BooleanSetting("Слот 6", false),
            new BooleanSetting("Слот 7", false),
            new BooleanSetting("Слот 8", false),
            new BooleanSetting("Слот 9", false)
    );

    @EventHandler
    public void onSlot(EventSlot event) {
        int currentSlot = event.getSlot();
        for (int i = 0; i < 9; i++) {
            if (slots.getValue("Слот " + (i + 1)) && currentSlot == i) {
                Excellent.inst().notifManager().add(new Notification(NotificationType.INFO,
                        "\"" + getName() + "\" не дал выбросить предмет!", 1500));
                event.cancel();
                return;
            }
        }
    }
}

