package org.privates.client.screen.countergui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Module extends Builder {

    private final Category category;
    private final org.privates.client.managers.module.Module module;
    private final ArrayList<Setting> settings = new ArrayList<>();
    private boolean visible = true;

    private float delta;
    private float interpolate;
    private long hoverStartTime = 0L;
    private float alpha = 1.0f;
    private boolean waitingForBind = false;

    public Module(Category category, org.privates.client.managers.module.Module module, float x, float y) {
        this.category = category;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = 145;
        this.height = 20;
        float offsetY = y + 19f;
        for (org.privates.client.managers.module.settings.Setting s : module.getSettings()) {
            Setting setting = new Setting(this, s, x + 4.5f, offsetY);
            settings.add(setting);
            offsetY += setting.getHeight() + 4.5f;
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {

        if (!visible) return;

        float guiY = category.getGui().getY(), guiHeight = category.getGui().getHeight();

        if (!inRange(y + delta, height, guiY + 41, guiHeight - 41)) return;

        boolean hovered = isHovered(mouseX, mouseY, x, y + delta, width, height);

        if (hovered) {
            if (hoverStartTime == 0L) {
                hoverStartTime = System.currentTimeMillis();
            }
        } else {
            hoverStartTime = 0L;
        }

        interpolate = Mathf.fast(interpolate, module.isEnabled() ? 1 : 0, 16);

        Theme theme = Theme.getInstance();

        int bgColor = ColorUtil.multAlpha(new Color(25, 25, 25, 200).getRGB(), alpha(alpha));
        int outlineColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(255, 255, 255, 10).getRGB(), ColorUtil.multAlpha(theme.clientColor(), 0.3f), interpolate), alpha(alpha));

        RenderUtil.Rounded.roundedRect(stack, x, y + delta, width, height, bgColor, Round.of(6));
        RenderUtil.Rounded.roundedOutline(stack, x, y + delta, width, height, 0.8f, outlineColor, Round.of(6));

        String displayText = module.getName();
        if (waitingForBind) {
            displayText += " [...]";
        } else if (module.getKey() != -1) {
            int key = module.getKey();
            if (key >= 500) {
                displayText += " [M" + (key - 500 + 1) + "]";
            } else {
                displayText += " [" + Keyboard.keyName(key) + "]";
            }
        }

        int textColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(150, 150, 150).getRGB(), new Color(220, 220, 220).getRGB(), interpolate), alpha(alpha));
        Fonts.SF_MEDIUM.draw(stack, displayText, x + 6f, y + delta + 10f - Fonts.SF_MEDIUM.getHeight(displayText, 7.5f) / 2f, textColor, 7.5f);

        if (inRange(y + delta + 6f, 8, guiY + 41, guiHeight - 41)) {
            int toggleBgColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(40, 40, 40).getRGB(), ColorUtil.multDark(theme.clientColor(), .7f), interpolate), alpha(alpha));
            int toggleKnobColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(80, 80, 80).getRGB(), theme.clientColor(), interpolate), alpha(alpha));

            RenderUtil.Rounded.roundedRect(stack, x + width - 17f, y + delta + 6f, 13, 8, toggleBgColor, Round.of(4));
            RenderUtil.Rounded.roundedRect(stack, x + width - 16f + interpolate * 5.5f, y + delta + 7f, 6, 6, toggleKnobColor, Round.of(3f));
        }

        for (Setting setting : settings)
            setting.render(stack, mouseX, mouseY);
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (isHovered(mouseX, mouseY, x + width - 17f, y + delta + 6f, 13, 8) && action == GLFW.GLFW_PRESS && button == 0)
            module.toggle();

        if (isHovered(mouseX, mouseY, x, y + delta, width, height) && action == GLFW.GLFW_PRESS && button == 2) {
            waitingForBind = true;
        }

        for (Setting setting : settings)
            setting.mouse(mouseX, mouseY, button, action);
    }

    @Override
    void key(int key) {
        if (waitingForBind) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE) {
                module.setKey(-1);
            } else {
                module.setKey(key);
            }
            waitingForBind = false;
            return;
        }

        for (Setting setting : settings)
            setting.key(key);
    }

    @Override
    void charTyped(char typed) {
        for (Setting setting : settings)
            setting.charTyped(typed);
    }

    public float alpha(float alpha) {
        return category.alpha(alpha) * this.alpha;
    }
}