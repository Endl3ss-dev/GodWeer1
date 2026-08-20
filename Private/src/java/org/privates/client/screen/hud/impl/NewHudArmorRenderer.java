package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudArmorRenderer implements IRenderer {
    private final ArmorSmartRenderer delegate;

    public NewHudArmorRenderer(DragSetting drag) {
        this.delegate = new ArmorSmartRenderer(drag);
        this.delegate.setBlurEnabled(true);
        this.delegate.setBlurStrength(8.7f);
        this.delegate.scale = 0.72f;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}
