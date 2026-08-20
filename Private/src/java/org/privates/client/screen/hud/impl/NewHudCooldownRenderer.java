package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudCooldownRenderer implements IRenderer {
    private final CooldownRenderer delegate;

    public NewHudCooldownRenderer(DragSetting drag) {
        this.delegate = new CooldownRenderer(drag);
        this.delegate.setBlurEnabled(true);
        this.delegate.setBlurStrength(7.5f);
        this.delegate.scale = 0.72f;
        this.delegate.showTitle = true;
        this.delegate.showTitleIcon = true;
        this.delegate.showItemIcon = true;
        this.delegate.showCount = false;
        this.delegate.dynamicRects = false;
        this.delegate.newRect = true;
        this.delegate.newRender = false;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}
