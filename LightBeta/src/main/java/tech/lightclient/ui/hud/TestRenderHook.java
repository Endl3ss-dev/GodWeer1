package tech.lightclient.ui.hud;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

public class TestRenderHook {
    @EventHandler
    public static void onRenderGui(EventRender.Screen.Hud event) {
        var r = event.getRenderer();

        float width = 250;
        float height = 36;


        r.blur(10, 10, width, height, 10, 1);

        r.rect(10, 10, width, height, 10, ColorUtils.rgba(0,0,0,175));

        r.text(FontRegistry.DELTA_HUD, 22, 35, 19, "Light v1.21.11", ClientColors.TEXT.getRGB());
    }
}
