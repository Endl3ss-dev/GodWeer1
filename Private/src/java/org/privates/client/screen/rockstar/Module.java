package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
@Setter
@Accessors(fluent = true)
public class Module extends Builder {

    private final org.privates.client.managers.module.Module module;
    private final Category category;
    private final float fontSize = 7f;
    private final ArrayList<Setting> settings = new ArrayList<>();

    private float animV, selectedV;
    private float delta, deltaS, deltaSV;

    public Module(Category category, org.privates.client.managers.module.Module module, float x, float y) {
        this.category = category;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = 115;
        this.height = 10;
        int id = 1;
        int iteration = 0;
        float offsetX = x + width + 7, offsetY = category.gui().y() + 40;
        for (org.privates.client.managers.module.settings.Setting s : module.getSettings()) {
            Setting setting = new Setting(this, s, offsetX, offsetY, id);
            settings.add(setting);

            if (id == 1) {
                offsetX += setting.width() + 8;
                id = 2;
            } else {
                offsetX = x + width + 7;
                offsetY += settings.get(iteration - 1).height() + 7;
                id = 1;
            }
            iteration++;
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {

        animV = Mathf.fast(animV, module.isEnabled() ? 1 : 0, 16);
        selectedV = Mathf.fast(selectedV, category.selectedModule() == this ? 1 : 0, 16);
        deltaSV = Mathf.fast(deltaSV, deltaS, 16);

        final float y = this.y + delta;

        Fonts.SF_BOLD.draw(stack, module.getName(), x + selectedV * 5, y + height / 2f - Fonts.SF_REGULAR.getHeight(module.getName(), fontSize) / 2f, ColorUtil.getColor(254, 254, 254, (.5f + .2f * animV) * alpha), fontSize);

        if (selectedV >= .1f) {

            float offsetY = category.gui().y() + 35;
            final float textWidth = Math.max(Fonts.SF_SEMIBOLD.getWidth("Настройки " + module.getName(), fontSize + 2), Fonts.SF_MEDIUM.getWidth(module.getModuleInfo().description(), fontSize));
            float offsetX = x + width + 7 - textWidth + textWidth * selectedV;
            final float textHeight = Fonts.SF_SEMIBOLD.getHeight("Настройки " + module.getName(), fontSize + 2) + 2;
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + width, offsetY, (category.gui().x() + category.gui().width()) - (x + width), category.gui().height() - 35);
            Fonts.SF_SEMIBOLD.draw(stack, "Настройки " + module.getName(), offsetX, offsetY, ColorUtil.getColor(254, 254, 254, selectedV * alpha), fontSize + 2);
            Fonts.SF_MEDIUM.draw(stack, module.getModuleInfo().description(), offsetX, offsetY + textHeight, ColorUtil.getColor(166, 167, 174, selectedV * alpha), fontSize);
            ScissorExp.unset();
            ScissorExp.setFromComponentCoordinates(category.gui().x() + 158, offsetY + textHeight + Fonts.SF_MEDIUM.getHeight(module.getModuleInfo().description(), fontSize) + 1, category.gui().width() - 158, (category.gui().y() + category.gui().height()) - (offsetY + textHeight + Fonts.SF_MEDIUM.getHeight(module.getModuleInfo().description(), fontSize) + 1));
            int id = 0;
            for (Setting setting : settings) {

                //if (setting.height() != 0) {

                setting.visibleV(Mathf.fast(setting.visibleV(), ((boolean) setting.setting().getVisible().get() && category.selectedModule() == this) ? 1 : 0, 24));

                setting.x(id % 2 == 0 ? x + width + 7 : settings.get(id - 1).x + settings.get(id - 1).width() + 8);
                setting.y(id < 2 ? category.gui().y() + 60 : settings.get(id - 2).y() + (settings.get(id - 2).height() + 7) * settings.get(id - 2).visibleV());

                setting.delta(deltaSV);

                id++;
                if (setting.visibleV() <= .1f) continue;

                setting.render(stack, mouseX, mouseY, selectedV * alpha);
                //}
            }
            ScissorExp.unset();
            ScissorExp.pop();
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, boolean action) {
        if (isHovered(mouseX, mouseY, x, y + delta, width, height) && action) {
            if (button == 0)
                module.toggle();
            else if (button == 1)
                category.selectedModule(category().selectedModule() == this ? null : this);
        } else if (category.selectedModule() == this) {
            for (Setting setting : settings)
                setting.mouse(mouseX, mouseY, button, action);
        }
    }

    public void scroll(float mx, float my, float delta) {
        if (category.selectedModule() == this && !settings.isEmpty()) {
            for (Setting setting : settings) {
                if (setting.selected() && setting.itemSetting() != null && isHovered(mx, my, setting.itemSetting().x(), setting.itemSetting().y(), setting.itemSetting().width(), setting.itemSetting().height())) {
                    setting.itemSetting().scroll(mx, my, delta);
                    return;
                }
            }
        }
        if (isHovered(mx, my, x + width + 1, category.gui().y() + 40, (category.gui().x() + category.gui().width()) - (x + width + 1), category.gui().height() - 40)
                && !(this.deltaS >= 0 && delta > 0) && !(delta < 0 && maxHeight() <= category().gui().y() + category().gui().height() - 7)) {
            this.deltaS += delta * 12;
            if (this.deltaS > 0)
                deltaS = 0;
            else if (maxHeight() <= category().gui().y() + category().gui().height() - 7)
                deltaS -= maxHeight() - (category().gui().y() + category().gui().height() - 7);
        }
    }

    @Override
    void key(int key) {
        if (category.selectedModule() == this) {
            for (Setting setting : settings)
                setting.key(key);
        }
    }

    @Override
    void charTyped(char typed) {
        if (category.selectedModule() == this) {
            for (Setting setting : settings)
                setting.charTyped(typed);
        }
    }

    private float maxHeight() {
        if (settings.isEmpty()) return 0;
        float height = settings.get(0).y() + settings.get(0).height();
        for (Setting setting : settings) {
            if (setting.visibleV() <= .1f) continue;
            height = Math.max(height, setting.y() + deltaS + setting.height());
        }
        return height;
    }
}
