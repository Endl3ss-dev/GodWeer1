package tech.lightclient.ui.hud.api;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;

public interface HudElement extends IMinecraft {
    void render(EventRender.Screen.Hud event);
}