package org.privates.client.screen.hud;


import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;

public interface IRenderer extends IMinecraft {
    Font font = Fonts.SF_SEMIBOLD;
    float fontSize = 7;

    void render(Render2DEvent event);

    default Theme theme() {
        return Theme.getInstance();
    }

}
