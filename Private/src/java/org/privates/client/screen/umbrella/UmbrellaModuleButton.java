package org.privates.client.screen.umbrella;

import lombok.Getter;
import lombok.Setter;
import org.privates.client.managers.module.Module;

import java.util.ArrayList;

@Getter
@Setter
public class UmbrellaModuleButton {
    private final UmbrellaScreen gui;
    private final Module module;
    private final ArrayList<UmbrellaSetting> settings = new ArrayList<>();

    private float enabledAnim = 0f;
    private float selectedAnim = 0f;
    private float hoverAnim = 0f;
    private float scaleAnim = 1f;
    private float appearAnim = 0f;

    private boolean waitingForBind = false;
    private float bindWaitAnim = 0f;

    public UmbrellaModuleButton(UmbrellaScreen gui, Module module) {
        this.gui = gui;
        this.module = module;

        for (org.privates.client.managers.module.settings.Setting s : module.getSettings()) {
            UmbrellaSetting setting = new UmbrellaSetting(this, s);
            settings.add(setting);
        }
    }

    public void scroll(double delta, float mouseX, float mouseY) {
        for (UmbrellaSetting setting : settings) {
            if (setting.isItemSelectorHovered(mouseX, mouseY)) {
                setting.scroll(delta);
                return;
            }
        }
    }

    public boolean isAnyItemSelectorHovered(float mouseX, float mouseY) {
        for (UmbrellaSetting setting : settings) {
            if (setting.isItemSelectorHovered(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }
}