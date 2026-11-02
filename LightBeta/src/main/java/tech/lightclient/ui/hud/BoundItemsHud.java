package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.bounditems.BoundItemsFirst;
import tech.lightclient.ui.hud.bounditems.BoundItemsTwo;

public final class BoundItemsHud implements HudElement {
    public static final String SETTINGS_SCOPE = "BoundItems";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    private final BoundItemsFirst first;
    private final BoundItemsTwo two;

    public BoundItemsHud(Dragging dragging) {
        this.first = new BoundItemsFirst(dragging);
        this.two = new BoundItemsTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
