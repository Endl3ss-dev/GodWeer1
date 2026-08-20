package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.DeathEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoRespawn", category = Category.PLAYER, description = "Не прийдется нажимать кнопку возродится после смерти")
public class AutoRespawn extends Module {
    public static AutoRespawn getInstance() {
        return Instance.get(AutoRespawn.class);
    }

    @EventHandler
    public void onEvent(DeathEvent event) {
        if (mc.player == null) return;
        mc.player.respawnPlayer();
        mc.displayScreen(null);
    }
}