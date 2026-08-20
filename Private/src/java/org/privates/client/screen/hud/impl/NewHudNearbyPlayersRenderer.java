package org.privates.client.screen.hud.impl;

import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;

public class NewHudNearbyPlayersRenderer implements IRenderer {
    private final NearbyPlayersRenderer delegate;

    public NewHudNearbyPlayersRenderer(DragSetting drag) {
        this.delegate = new NearbyPlayersRenderer(drag);
        this.delegate.scale = 0.72f;
        this.delegate.showTitle = true;
        this.delegate.showTitleIcon = true;
        this.delegate.showDistance = true;
        this.delegate.showHealth = true;
        this.delegate.showHealthBar = true;
        this.delegate.showArmor = false;
        this.delegate.showTrackedItems = false;
        this.delegate.dynamicRects = false;
        this.delegate.multiColumn = false;
        this.delegate.compactMode = false;
        this.delegate.setEnableOutline(false);
    }

    @Override
    public void render(Render2DEvent event) {
        delegate.render(event);
    }
}
