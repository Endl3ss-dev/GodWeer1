package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.ClickType;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

@Category3
@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "KTLeave", category = Category.MISC)
public class KTLeave extends Module {
    final StopWatch clickTimer = new StopWatch();
    boolean isHolding = false;

    @Override
    public void toggle() {
        super.toggle();
        if (isEnabled()) {
            mc.player.sendChatMessage("/darena");
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (!isEnabled()) return;

        if (mc.player.getHealth() < 6.0F) {
            if (!isHolding && clickTimer.finished(500)) {
                InvUtil.clickSlotId(24, 0, ClickType.PICKUP, true);
                isHolding = true;
                clickTimer.reset();
            }
        }


        if (isHolding && clickTimer.finished(200)) {
            InvUtil.clickSlotId(24, 0, ClickType.PICKUP, true);
            isHolding = false;
            clickTimer.reset();
        }
    }
}