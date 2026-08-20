package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.lib.util.time.StopWatch;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HomeBackTP", category = Category.MISC, description = "Телепортирует вас на том месте где функция изначально поставила home")
public class TeleportBack extends Module {
    boolean dead;
    final StopWatch stopWatch = new StopWatch();

    @EventHandler
    private void onUpdate(UpdateEvent e) {
        if (!mc.player.isAlive() && !dead && stopWatch.finished(500)) {
            mc.player.sendChatMessage("/sethome privatedlc");
            mc.player.respawnPlayer();
            DelayUtil.addTask(50, (() -> {
                mc.player.sendChatMessage("/home privatedlc");
            }));
            DelayUtil.addTask(550, (() -> {
                mc.player.sendChatMessage("/spawn");
                dead = false;
            }));
            dead = true;
            stopWatch.reset();
        }
    }
}
