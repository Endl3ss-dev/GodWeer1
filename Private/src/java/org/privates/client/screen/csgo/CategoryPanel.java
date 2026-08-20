package org.privates.client.screen.csgo;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.module.Category;
import org.privates.client.utils.math.Mathf;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
@Setter
public class CategoryPanel extends CsGoBuilder {

    private final CsGoScreen gui;
    private final Category category;
    private final ArrayList<ModulePanel> modules = new ArrayList<>();

    private float delta, deltaV;
    private float iconHoverV;

    public CategoryPanel(CsGoScreen gui, Category category, float x, float y) {
        this.gui = gui;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = 70;
        this.height = 32;

        for (org.privates.client.managers.module.Module m : Excellent.inst().moduleManager().get(category)) {
            ModulePanel module = new ModulePanel(this, m);
            modules.add(module);
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {
    }

    public void render(MatrixStack stack, float mouseX, float mouseY, float contentX, float contentY, float contentWidth, float contentHeight) {
        deltaV = Mathf.fast(deltaV, delta, 14);
        recalculate();

        ArrayList<ModulePanel> filteredModules = getFilteredModules();

        for (ModulePanel module : modules) {
            module.setModuleAlpha(filteredModules.contains(module) ? 1 : 0);
        }

        float moduleX = contentX + 15;
        float moduleY = contentY + 15 + deltaV;
        float moduleWidth = (contentWidth - 42) / 2f;
        int column = 0;

        for (int i = 0; i < filteredModules.size(); i++) {
            ModulePanel module = filteredModules.get(i);

            module.setX(moduleX + (column * (moduleWidth + 12)));
            module.setY(moduleY);
            module.setWidth(moduleWidth);

            if (inRange(module.getY(), module.getHeight(), contentY, contentHeight)) {
                module.render(stack, mouseX, mouseY);
            }

            column++;
            if (column >= 2) {
                column = 0;
                moduleY += module.getHeight() + 12;
            }
        }
    }

    private void recalculate() {
        ArrayList<ModulePanel> filteredModules = getFilteredModules();
        float moduleX = gui.getX() + gui.getWidth() + 27.5f;
        float moduleY = gui.getY() + 5; // Base Y
        float moduleWidth = (gui.getWidth() - 42) / 2f;
        int column = 0;

        for (int i = 0; i < filteredModules.size(); i++) {
            ModulePanel module = filteredModules.get(i);
            module.setX(moduleX + (column * (moduleWidth + 12)));
            module.setY(moduleY);

            module.setHeight(21);
            if (module.getSettings().isEmpty()) continue;

            float settingsOffsetY = module.getY() + 21.5f;
            for (int j = 0; j < module.getSettings().size(); j++) {
                module.getSettings().get(j).setY((j == 0) ? settingsOffsetY : module.getSettings().get(j - 1).getY() + module.getSettings().get(j - 1).getHeight() + 5);
                module.setHeight(module.getHeight() + module.getSettings().get(j).getHeight() + 5);
            }

            column++;
            if (column >= 2) {
                column = 0;
                moduleY += module.getHeight() + 9;
            }
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        for (ModulePanel module : getFilteredModules()) {
            module.mouse(mouseX, mouseY, button, action);
        }
    }

    @Override
    void key(int key) {
        for (ModulePanel module : getFilteredModules()) {
            module.key(key);
        }
    }

    @Override
    void charTyped(char typed) {
        for (ModulePanel module : getFilteredModules()) {
            module.charTyped(typed);
        }
    }

    public void scroll(float mouseX, float mouseY, float delta) {
        float totalHeight = calculateTotalHeight();
        float contentHeight = gui.getHeight() - 70 - 15;
        float maxScroll = Math.max(0, totalHeight - contentHeight);

        if (!(delta > 0 && this.delta >= 0) && !(delta < 0 && -this.delta >= maxScroll)) {
            this.delta += delta * 15;
            this.delta = Mathf.clamp(this.delta, -maxScroll, 0);
        }
    }

    private float calculateTotalHeight() {
        ArrayList<ModulePanel> filteredModules = getFilteredModules();
        if (filteredModules.isEmpty()) return 0;

        float column1Height = 0;
        float column2Height = 0;

        for (int i = 0; i < filteredModules.size(); i++) {
            ModulePanel module = filteredModules.get(i);
            if (i % 2 == 0) {
                column1Height += module.getHeight() + (i < filteredModules.size() - 2 ? 12 : 0);
            } else {
                column2Height += module.getHeight() + (i < filteredModules.size() - 2 ? 12 : 0);
            }
        }

        return Math.max(column1Height, column2Height);
    }

    public ArrayList<ModulePanel> getFilteredModules() {
        String searchQuery = gui.getSearchQuery().toLowerCase();
        if (searchQuery.isEmpty()) {
            return modules;
        }
        return modules.stream()
                .filter(m -> m.getModule().getName().toLowerCase().contains(searchQuery))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public float alpha(float alpha) {
        return gui.alpha(alpha);
    }
}