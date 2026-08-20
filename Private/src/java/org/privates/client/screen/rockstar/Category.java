package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
@Accessors(fluent = true)
public class Category extends Builder {

    private final RockstarScreen gui;
    private final org.privates.client.managers.module.Category category;
    private final ArrayList<Module> modules = new ArrayList<>();
    private final ThemeCategory themeCategory;

    private float selectedV;
    private float delta, deltaV;
    private Module selectedModule;

    public Category(RockstarScreen gui, org.privates.client.managers.module.Category category, float x, float y) {
        this.gui = gui;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        themeCategory = new ThemeCategory(gui, x, y);
        loadModules("");
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {
        selectedV = Mathf.fast(selectedV, gui.selectedCategory() == this ? 1 : 0, 16);
        deltaV = Mathf.fast(deltaV, delta, 16);

        float outlineAlpha = ((.05f + .17f * selectedV) * alpha);
        RenderUtil.Rounded.roundedWithOutline(stack, x, y, width, height, 1.5f,
                ColorUtil.getColor(14, 16, 20, alpha),
                ColorUtil.getColor(254, 254, 254, outlineAlpha),
                Round.of(2.5f));

        Fonts.ROCKSTAR.draw(stack, category.getIcon(), x + width / 2f - Fonts.ROCKSTAR.getWidth(category.getIcon(), 9) / 2f, y + height / 2f - Fonts.ROCKSTAR.getHeight(category.getIcon(), 9) / 2f, ColorUtil.getColor(245, 247, 249, alpha), 9);

    }

    public void scroll(float mx, float my, float delta) {
        if (selectedModule != null) {
            selectedModule.scroll(mx, my, delta);
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, boolean action) {
        
        if (isHovered(mouseX, mouseY, x, y, width, height) && action)
            gui.selectedCategory(this);
    }

    @Override
    void key(int key) {


    }

    @Override
    void charTyped(char typed) {
    }

    public void loadModules(final String key) {
        modules.clear();
        delta = 0;
        float offsetY = gui.y() + 29;
        char character = ' ';
        for (org.privates.client.managers.module.Module m : Excellent.inst().moduleManager().values().stream().filter(m -> (key.trim().isEmpty() && m.getCategory() == category) || (!key.trim().isEmpty() && m.getName().toLowerCase().contains(key.toLowerCase())))
                .sorted(Comparator.comparing(org.privates.client.managers.module.Module::getName)).toList()) {

            if (m.getName().charAt(0) != character) {
                offsetY += 10;
            }
            character = m.getName().charAt(0);

            Module module = new Module(this, m, x + width + 15, offsetY);
            modules.add(module);
            offsetY += 12;
        }
    }
}
