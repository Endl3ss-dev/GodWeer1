package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudInformationRenderer implements IRenderer {
    private final InformationRenderer delegate;

    public NewHudInformationRenderer(DragSetting drag) {
        this.delegate = new InformationRenderer(drag);
        this.delegate.setBlurEnabled(true);
        this.delegate.setBlurStrength(8.7f);
        this.delegate.setScale(0.72f);
        this.delegate.dynamicRects = true;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}

