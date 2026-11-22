package tech.lightclient.ui.hud.cooldowns;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.CooldownsHud;

public class CooldownsHudTwo extends CooldownsHud {

    public CooldownsHudTwo(Dragging dragging) {
        super(dragging, false);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        renderHud(event);
    }
}
