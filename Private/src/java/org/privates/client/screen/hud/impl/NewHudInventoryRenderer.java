package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudInventoryRenderer implements IRenderer {
    private final InventoryHudRenderer delegate;

    public NewHudInventoryRenderer(DragSetting drag) {
        this.delegate = new InventoryHudRenderer(drag);
        this.delegate.setBlurEnabled(true);
        this.delegate.setBlurStrength(7.5f);
        this.delegate.scale = 0.72f;
        this.delegate.showTitle = true;
        this.delegate.showTitleIcon = true;
        this.delegate.showCount = true;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}

