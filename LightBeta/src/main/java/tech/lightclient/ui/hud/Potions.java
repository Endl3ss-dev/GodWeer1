package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.potions.PotionsFirst;
import tech.lightclient.ui.hud.potions.PotionsTwo;

public final class Potions implements HudElement {
    public static final String SETTINGS_SCOPE = "Potions";
    public static final String SETTING_ALWAYS_SHOW = "alwaysShow";
    public static final String SETTING_SHOW_EFFECT_ICON = "showEffectIcon";
    public static final String SETTING_GREEN_TEXT = "greenText";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    private final PotionsFirst first;
    private final PotionsTwo two;

    public Potions(Dragging dragging) {
        this.first = new PotionsFirst(dragging);
        this.two = new PotionsTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
