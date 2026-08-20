package org.privates.client.screen.hud;

import org.privates.client.managers.component.impl.drag.DragComponent;

import java.util.List;

public interface IConfigurableHud {
    List<DragComponent.HudSetting> getHudSettings();
}