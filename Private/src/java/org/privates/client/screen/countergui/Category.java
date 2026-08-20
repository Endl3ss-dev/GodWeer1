package org.privates.client.screen.countergui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.utils.math.Mathf;

import java.util.ArrayList;

@Getter
@Setter
public class Category extends Builder {

    private final org.privates.client.managers.module.Category category;
    private final CounterGui gui;
    private final ArrayList<Module> modules = new ArrayList<>();

    private float selectedV;
    private boolean selected = false;
    private float delta, deltaV;
    private Module hoveredModule;

    public Category(CounterGui gui, org.privates.client.managers.module.Category category) {
        this.gui = gui;
        this.category = category;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;

        float offsetX = gui.getX() + 10f;
        float offsetY = gui.getY() + 44;
        int id = 1;

        for (org.privates.client.managers.module.Module m : Excellent.inst().moduleManager().get(category)) {
            Module module = new Module(this, m, offsetX, offsetY);
            modules.add(module);
            if (id == 1) {
                offsetX += module.getWidth() + 7;
                id = 2;
            } else if (id == 2) {
                offsetX += module.getWidth() + 7;
                id = 3;
            } else {
                offsetX = gui.getX() + 10f;
                offsetY += module.getHeight() + 8;
                id = 1;
            }
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {
        selected = gui.getSelectedCategory() == this;
        deltaV = Mathf.fast(deltaV, delta, 16);

        if (selected) {
            hoveredModule = null;
            for (Module module : modules) {
                if (!module.isVisible()) {
                    continue;
                }

                module.setDelta(deltaV);

                Module highlightedModule = gui.getHighlightedModule();
                if (highlightedModule != null && highlightedModule != module) {
                    module.setAlpha(1.0f - gui.getSearchHighlightAnim() * 0.6f);
                } else {
                    module.setAlpha(1.0f);
                }

                module.render(stack, mouseX, mouseY);
                if (module.isHovered(mouseX, mouseY, module.getX(), module.getY() + module.getDelta(), module.getWidth(), module.getHeight())) {
                    hoveredModule = module;
                }
            }
        }
        recalculate();
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (selected) {
            for (Module module : modules)
                module.mouse(mouseX, mouseY, button, action);
        }
    }

    public void scroll(float mouseX, float mouseY, float delta) {
        float max = maxHeight();
        if (!(delta > 0 && this.delta >= 0) && !(delta < 0 && max + this.delta <= gui.getHeight() - 44)) {
            this.delta += delta * 12;
            if (this.delta + max < gui.getHeight() - 44) {
                this.delta += (gui.getHeight() - 44) - (this.delta + max);
            }
        }
    }

    @Override
    void key(int key) {
        if (selected) {
            for (Module module : modules)
                module.key(key);
        }
    }

    @Override
    void charTyped(char typed) {
        if (selected) {
            for (Module module : modules)
                module.charTyped(typed);
        }
    }

    private void recalculate() {
        int visibleIndex = 0;
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            if (!module.isVisible()) {
                continue;
            }

            if (visibleIndex % 3 == 0) {
                module.setX(gui.getX() + 10f);
            } else if (visibleIndex % 3 == 1) {
                module.setX(modules.get(getPreviousVisibleIndex(i)).getX() + modules.get(getPreviousVisibleIndex(i)).getWidth() + 7);
            } else {
                module.setX(modules.get(getPreviousVisibleIndex(i)).getX() + modules.get(getPreviousVisibleIndex(i)).getWidth() + 7);
            }

            if (visibleIndex >= 3) {
                module.setY(modules.get(getVisibleIndexAt(visibleIndex - 3)).getY() + modules.get(getVisibleIndexAt(visibleIndex - 3)).getHeight() + 8);
            } else {
                module.setY(gui.getY() + 44);
            }

            module.setHeight(20);
            if (modules.get(i).getSettings().isEmpty()) {
                visibleIndex++;
                continue;
            }

            float currentY = module.getY() + module.getDelta() + 19f;
            for (int j = 0; j < module.getSettings().size(); j++) {
                Setting setting = module.getSettings().get(j);

                if (!(boolean) setting.getSetting().getVisible().get()) {
                    continue;
                }

                setting.setY(currentY);
                currentY += setting.getHeight() + 4.5f;
                module.setHeight(module.getHeight() + setting.getHeight() + 4.5f);
            }
            visibleIndex++;
        }
    }

    private int getPreviousVisibleIndex(int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (modules.get(i).isVisible()) {
                return i;
            }
        }
        return 0;
    }

    private int getVisibleIndexAt(int targetVisibleIndex) {
        int visibleCount = 0;
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).isVisible()) {
                if (visibleCount == targetVisibleIndex) {
                    return i;
                }
                visibleCount++;
            }
        }
        return 0;
    }

    private float maxHeight() {
        float col1 = 0, col2 = 0, col3 = 0;
        int visibleIndex = 0;
        for (int i = 0; i < modules.size(); i++) {
            if (!modules.get(i).isVisible()) {
                continue;
            }

            if (visibleIndex % 3 == 0)
                col1 += modules.get(i).getHeight() + 8;
            else if (visibleIndex % 3 == 1)
                col2 += modules.get(i).getHeight() + 8;
            else
                col3 += modules.get(i).getHeight() + 8;

            visibleIndex++;
        }
        return Math.max(Math.max(col1, col2), col3);
    }

    public boolean isSelected() {
        return selected;
    }

    public float alpha(float alpha) {
        return gui.alpha(alpha);
    }
}