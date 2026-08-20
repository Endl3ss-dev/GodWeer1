package org.privates.client.managers.component.impl.other;

import net.minecraft.client.settings.KeyBinding;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.Component;
import org.privates.client.managers.events.world.WorldLoadEvent;

public class SyncFixComponent extends Component {
    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        KeyBinding.unPressAllKeys();
        for (KeyBinding binding : mc.gameSettings.keyBindings) {
            binding.setPressed(false);
        }
    }
}
