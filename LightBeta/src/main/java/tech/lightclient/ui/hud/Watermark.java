package tech.lightclient.ui.hud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.watermark.WatermarkFirst;
import tech.lightclient.ui.hud.watermark.WatermarkTwo;

public final class Watermark implements HudElement {
    public static final String SETTINGS_SCOPE = "Watermark";
    public static final String SETTING_SHOW_NICKNAME = "showNickname";
    public static final String SETTING_SHOW_FPS = "showFps";
    public static final String SETTING_SHOW_PING = "showPing";
    public static final String SETTING_SHOW_TIME = "showTime";
    public static final String SETTING_SHOW_TPS = "showTps";
    public static final String SETTING_SHOW_SERVER_IP = "showServerIp";
    public static final String SETTING_CENTERING = "centering";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    private final WatermarkFirst first;
    private final WatermarkTwo two;

    public Watermark(Dragging dragging) {
        this.first = new WatermarkFirst(dragging);
        this.two = new WatermarkTwo(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        first.render(event);
    }
}
