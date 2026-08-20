package org.privates.client.screen.umbrella;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmbrellaCategory {
    private final UmbrellaScreen gui;
    private final org.privates.client.managers.module.Category category;
    private String name;
    private float selectedAnim = 0f;
    private float hoverAnim = 0f;
    private float scaleAnim = 1f;
    private float appearAnim = 0f;
    private float iconBounceAnim = 0f;

    public UmbrellaCategory(UmbrellaScreen gui, org.privates.client.managers.module.Category category) {
        this.gui = gui;
        this.category = category;
        this.name = category != null ? category.getName() : "";
    }

    public boolean isSelected() {
        return gui.getSelectedCategory() == this;
    }

    public float alpha(float alpha) {
        return gui.alpha(alpha);
    }
}