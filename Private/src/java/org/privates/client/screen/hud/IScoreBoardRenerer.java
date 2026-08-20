package org.privates.client.screen.hud;

import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.events.render.RenderScoreBoardEvent;

public interface IScoreBoardRenerer extends IMinecraft {
    void renderScoreBoard(RenderScoreBoardEvent event);
}
