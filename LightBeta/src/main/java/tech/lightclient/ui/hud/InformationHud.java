package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.information.InformationFirst;
import tech.lightclient.ui.hud.information.InformationTwo;

public final class InformationHud implements HudElement {
    public static final String SETTINGS_SCOPE = "Information";

    private final InformationFirst first;
    private final InformationTwo two;

    public InformationHud(Dragging dragging) {
        this.first = new InformationFirst(dragging);
        this.two = new InformationTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
