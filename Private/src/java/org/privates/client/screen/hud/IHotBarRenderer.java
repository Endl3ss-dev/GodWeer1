package org.privates.client.screen.hud;

import org.privates.client.api.interfaces.IMinecraft;

public interface IHotBarRenderer extends IMinecraft {
    void renderHotBar(RenderHotBarEvent event);
}
