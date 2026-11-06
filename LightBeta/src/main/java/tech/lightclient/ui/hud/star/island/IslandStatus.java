package tech.lightclient.ui.hud.star.island;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;

public abstract class IslandStatus {
    public float width = 48.0f;
    public float height = 15.0f;
    protected final SAnim animation = new SAnim(500L, 0.0f, SEasing.BAKEK_SIZE);
    private final String id;
    private final String label;

    protected IslandStatus(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public SAnim getAnimation() {
        return animation;
    }

    public void draw(EventRender.Screen.Hud event, Renderer2D r, IslandElement island, float x, float y, float alpha) {
    }

    public boolean canShow() {
        return true;
    }

    public int getColorArgb() {
        return tech.lightclient.client.ClientColors.BACKGROUND.getRGB();
    }
}
