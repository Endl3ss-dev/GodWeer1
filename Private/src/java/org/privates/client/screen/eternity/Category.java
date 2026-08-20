package org.privates.client.screen.eternity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private final EternityScreen gui;
    private final org.privates.client.managers.module.Category category;
    private String name;
    private float selectedAnim = 0f;
    private float hoverAnim = 0f;
    private float scaleAnim = 1f;
    private float appearAnim = 0f;
    private float iconBounceAnim = 0f;

    public Category(EternityScreen gui, org.privates.client.managers.module.Category category) {
        this.gui = gui;
        this.category = category;
        this.name = category != null ? category.getName() : "";
    }

    public void scroll(double delta, float mouseX, float mouseY) {
        for (ModuleButton module : gui.getModuleButtons()) {
            if (module.isAnyItemSelectorHovered(mouseX, mouseY)) {
                module.scroll(delta, mouseX, mouseY);
                return;
            }
        }
    }

    public boolean isAnyItemSelectorHovered(float mouseX, float mouseY) {
        for (ModuleButton module : gui.getModuleButtons()) {
            if (module.isAnyItemSelectorHovered(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelected() {
        return gui.getSelectedCategory() == this;
    }

    public float alpha(float alpha) {
        return gui.alpha(alpha);
    }
}