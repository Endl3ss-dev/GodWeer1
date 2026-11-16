package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.stafflist.StaffListFirst;
import tech.lightclient.ui.hud.stafflist.StaffListTwo;

public final class StaffList implements HudElement {
    public static final String SETTINGS_SCOPE = "StaffList";
    public static final String SETTING_ALWAYS_SHOW = "alwaysShow";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    private final StaffListFirst first;
    private final StaffListTwo two;

    public StaffList(Dragging dragging) {
        this.first = new StaffListFirst(dragging);
        this.two = new StaffListTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
