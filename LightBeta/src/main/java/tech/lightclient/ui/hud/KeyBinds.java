package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.keybinds.KeyBindsFirst;
import tech.lightclient.ui.hud.keybinds.KeyBindsTwo;

public final class KeyBinds implements HudElement {
    public static final String SETTINGS_SCOPE = "KeyBinds";
    public static final String SETTING_ALWAYS_SHOW = "alwaysShow";
    public static final String SETTING_LINE_BETWEEN_TEXT_AND_BIND = "lineBetweenTextAndBind";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    private final KeyBindsFirst first;
    private final KeyBindsTwo two;

    public KeyBinds(Dragging dragging) {
        this.first = new KeyBindsFirst(dragging);
        this.two = new KeyBindsTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
