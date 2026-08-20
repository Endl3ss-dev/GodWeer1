package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudItemCountRenderer implements IRenderer {
    private final ItemCountRenderer delegate;

    public NewHudItemCountRenderer(DragSetting drag) {
        this.delegate = new ItemCountRenderer(drag);
        this.delegate.setBlurEnabled(true);
        this.delegate.setBlurStrength(7.5f);
        this.delegate.scale = 0.72f;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}
