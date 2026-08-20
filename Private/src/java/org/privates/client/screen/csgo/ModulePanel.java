package org.privates.client.screen.csgo;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
@Setter
public class ModulePanel extends CsGoBuilder {

    private final CategoryPanel category;
    private Float scrollOffset = null;
    private final org.privates.
            client.managers.module.Module module;
    private final ArrayList<SettingPanel> settings = new ArrayList<>();

    private float enabledV;
    private float expandedV;
    private boolean expanded = false;
    private boolean input = false;

    private float moduleAlpha = 0, moduleAlphaV = 0;

    public ModulePanel(CategoryPanel category, org.privates.client.managers.module.Module module) {
        this.category = category;
        this.module = module;
        this.width = 280;
        this.height = 50;

        for (org.privates.client.managers.module.settings.Setting s : module.getSettings()) {
            SettingPanel setting = new SettingPanel(this, s);
            settings.add(setting);
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {
        moduleAlphaV = Mathf.fast(moduleAlphaV, moduleAlpha, 12);
        if (moduleAlphaV < 0.01f) return;

        enabledV = Mathf.fast(enabledV, module.isEnabled() ? 1 : 0, 14);
        expandedV = Mathf.fast(expandedV, expanded ? 1 : 0, 14);

        float targetHeight = 50;
        if (expanded && !settings.isEmpty()) {
            float settingsHeight = 0;
            for (SettingPanel setting : settings) {
                if ((boolean) setting.getSetting().getVisible().get()) {
                    settingsHeight += setting.getHeight() + 10;
                }
            }
            if (settingsHeight > 0) settingsHeight -= 10;
            targetHeight = 50 + 12 + settingsHeight + 12;
        }
        height = Mathf.fast(height, targetHeight, 14);

        int bgStartColor = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, module.isEnabled() ? 220 : 150), alpha(1));
        int bgEndColor = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, module.isEnabled() ? 220 : 150), alpha(1));
        RenderUtil.Rounded.gradientRect(stack, x, y, width, height, bgStartColor, bgEndColor, bgEndColor, bgStartColor, Round.of(8));

        int outlineColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, module.isEnabled() ? 100 : 40), alpha(1));
        RenderUtil.Rounded.roundedOutline(stack, x, y, width, height, 1, outlineColor, Round.of(8));

        renderHeader(stack, mouseX, mouseY);

        if (expandedV > 0.01f) {
            renderSettings(stack, mouseX, mouseY);
        }
    }

    private void renderHeader(MatrixStack stack, float mouseX, float mouseY) {
        String keyText = input ? " [...]" : (module.getKey() > -1 ? " [" + Keyboard.keyName(module.getKey()) + "]" : "");
        String text = module.getName() + keyText;

        int textColor = ColorUtil.multAlpha(ColorUtil.overCol(
                ColorUtil.getColor(140, 140, 160),
                ColorUtil.getColor(220, 220, 240),
                enabledV
        ), alpha(1));

        Fonts.SF_BOLD.draw(stack, text, x + 18, y + 15, textColor, 10);

        if (module.getModuleInfo().description() != null && !module.getModuleInfo().description().isEmpty()) {
            int descColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(100, 100, 120),
                    ColorUtil.getColor(140, 140, 160),
                    enabledV
            ), alpha(1));

            String description = module.getModuleInfo().description();
            float descWidth = Fonts.SF_REGULAR.getWidth(description, 7);
            float maxWidth = width - 36 - 50;

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + 18, y + 28, maxWidth, 14);

            if (descWidth > maxWidth) {
                if (scrollOffset == null) scrollOffset = 0f;

                float totalWidth = descWidth + 30;
                scrollOffset += 0.5f;
                if (scrollOffset > totalWidth) {
                    scrollOffset = -maxWidth;
                }

                Fonts.SF_REGULAR.draw(stack, description, x + 18 - scrollOffset, y + 28, descColor, 7);

                if (scrollOffset < descWidth - maxWidth + 30) {
                    Fonts.SF_REGULAR.draw(stack, description, x + 18 - scrollOffset + totalWidth, y + 28, descColor, 7);
                }
            } else {
                scrollOffset = null;
                Fonts.SF_REGULAR.draw(stack, description, x + 18, y + 28, descColor, 7);
            }

            ScissorExp.unset();
            ScissorExp.pop();
        }

        float toggleX = x + width - 35;
        float toggleY = y + 15;
        float toggleSize = 20;

        int circleBgStart = ColorUtil.multAlpha(ColorUtil.overCol(
                ColorUtil.getColor(30, 30, 40, 150),
                ColorUtil.getColor(138, 104, 193, 80),
                enabledV
        ), alpha(1));
        int circleBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 150), alpha(1));
        RenderUtil.Rounded.gradientRect(stack, toggleX, toggleY, toggleSize, toggleSize, circleBgStart, circleBgEnd, circleBgEnd, circleBgStart, Round.of(10));

        if (enabledV > 0.01f) {
            int innerCircle = ColorUtil.multAlpha(ColorUtil.getColor(200, 180, 230), enabledV * alpha(1));
            RenderUtil.Rounded.roundedRect(stack, toggleX + 5, toggleY + 5, 10, 10, innerCircle, Round.of(5));
        }
    }

    private float delta = 0;
    private float deltaV = 0;

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    private void renderSettings(MatrixStack stack, float mouseX, float mouseY) {
        if (settings.isEmpty()) return;

        float settingsY = y + 50 + 12;
        float settingsHeight = height - 50 - 24;

        if (settingsHeight <= 0) return;

        int settingsBgStart = ColorUtil.multAlpha(ColorUtil.getColor(28, 27, 35, (int) (200 * expandedV)), alpha(expandedV));
        int settingsBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(15, 15, 15, (int) (200 * expandedV)), alpha(expandedV));
        RenderUtil.Rounded.gradientRect(stack, x + 12, settingsY, width - 24, settingsHeight, settingsBgStart, settingsBgEnd, settingsBgEnd, settingsBgStart, Round.of(0));

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 12, settingsY, width - 24, settingsHeight);

        float offsetY = settingsY + 6;
        for (SettingPanel setting : settings) {
            if (!(boolean) setting.getSetting().getVisible().get()) continue;

            setting.setX(x + 18);
            setting.setY(offsetY);
            setting.setWidth(width - 36);
            setting.setAlphaV(expandedV);

            if (inRange(offsetY, setting.getHeight(), settingsY, settingsHeight)) {
                setting.render(stack, mouseX, mouseY);
            }

            offsetY += setting.getHeight() + 10;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (moduleAlphaV < 0.9f) return;

        if (action == GLFW.GLFW_PRESS) {
            if (isHovered(mouseX, mouseY, x, y, width, 50)) {
                float toggleX = x + width - 35;
                float toggleY = y + 15;

                if (isHovered(mouseX, mouseY, toggleX, toggleY, 20, 20) && button == 0) {
                    module.toggle();
                    return;
                }

                if (button == 1 && !settings.isEmpty()) {
                    expanded = !expanded;
                    if (!expanded) {
                        for (SettingPanel s : settings) s.setSelected(false);
                    }
                    return;
                }

                if (button == 2) {
                    input = !input;
                    return;
                }
            }
        }

        if (expanded) {
            float settingsY = y + 50 + 12;
            float settingsHeight = height - 50 - 24;
            if (isHovered(mouseX, mouseY, x + 12, settingsY, width - 24, settingsHeight)) {
                for (SettingPanel setting : settings) {
                    if ((boolean) setting.getSetting().getVisible().get()) {
                        setting.mouse(mouseX, mouseY, button, action);
                    }
                }
            }
        }
    }

    @Override
    void key(int key) {
        if (input) {
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE) {
                module.setKey(-1);
            } else {
                module.setKey(key);
            }
            input = false;
            return;
        }

        if (expanded) {
            for (SettingPanel setting : settings) {
                if ((boolean) setting.getSetting().getVisible().get()) {
                    setting.key(key);
                }
            }
        }
    }

    @Override
    void charTyped(char typed) {
        if (expanded) {
            for (SettingPanel setting : settings) {
                if ((boolean) setting.getSetting().getVisible().get()) {
                    setting.charTyped(typed);
                }
            }
        }
    }

    public float alpha(float alpha) {
        return category.alpha(alpha) * moduleAlphaV;
    }
}