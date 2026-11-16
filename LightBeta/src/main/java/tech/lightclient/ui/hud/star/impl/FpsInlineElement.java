package tech.lightclient.ui.hud.star.impl;

import java.util.Locale;

import tech.lightclient.client.utils.client.other.ClientUtility;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.api.Function;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;

public class FpsInlineElement extends InlineElement {
    private final InlineValue fps = new InlineValue("fps", "FPS", "FPS");
    private final InlineValue ping = new InlineValue("ping", "Пинг", "MS");
    private final InlineValue bps = new InlineValue("bps", "Скорость", "BPS");
    private final SAnim fpsSmooth = new SAnim(300L, 60.0f, SEasing.SMOOTH_STEP);
    private final SAnim pingSmooth = new SAnim(300L, 0.0f, SEasing.SMOOTH_STEP);

    public FpsInlineElement(Function module) {
        super(module, "Star FPS", "icons/hud/clock.png", 10.0f, 52.0f);
        values.add(fps);
        values.add(ping);
        values.add(bps);
    }

    @Override
    protected void updateValues() {
        fps.update(String.valueOf(Math.round(fpsSmooth.update(mc.getCurrentFps()))));
        ping.update(String.valueOf(Math.round(pingSmooth.update(ClientUtility.getPing()))));

        String speed = String.format(Locale.US, "%.1f", PlayerUtils.getBPS());
        if (speed.endsWith(".0")) {
            speed = speed.substring(0, speed.length() - 2);
        }
        bps.update(speed);
    }

    @Override
    public String settingName() {
        return "FPS";
    }
}
