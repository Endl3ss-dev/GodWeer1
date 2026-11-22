package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.targethud.TargetHudFirst;

public final class TargetHud implements HudElement {
    public static final String SETTINGS_SCOPE = "TargetHud";
    public static final String SETTING_ADAPTIVE_BAR = "adaptiveBarColor";
    public static final String SETTING_SHOW_ON_HOVER = "showOnHover";

    private final TargetHudFirst first;

    public TargetHud(Dragging dragging) {
        this.first = new TargetHudFirst(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
