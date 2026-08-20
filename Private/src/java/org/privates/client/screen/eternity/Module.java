// Module.java
package org.privates.client.screen.eternity;

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

import java.util.ArrayList;

@Getter
@Setter
public class Module extends Builder {

    private final Category category;
    private final org.privates.client.managers.module.Module module;
    private final ArrayList<Setting> settings = new ArrayList<>();

    private float delta;
    private float interpolate;
    private float hoverAnim = 0f;
    private float scaleAnim = 1f;
    private boolean input = false;

    public Module(Category category, org.privates.client.managers.module.Module module, float x, float y) {
        this.category = category;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = 133;
        this.height = 21;
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {

        float guiY = category.getGui().getY(), guiHeight = category.getGui().getHeight();

        if (!inRange(y + delta, height, guiY, guiHeight)) return;

        boolean hovered = isHovered(mouseX, mouseY, x, y + delta, width, height);
        hoverAnim = Mathf.fast(hoverAnim, hovered ? 1 : 0, 20);
        interpolate = Mathf.fast(interpolate, module.isEnabled() ? 1 : 0, 16);
        scaleAnim = Mathf.fast(scaleAnim, hovered ? 1.02f : 1f, 18);

        Theme theme = Theme.getInstance();

        float scaleX = x + width / 2f;
        float scaleY = y + delta + height / 2f;

        stack.push();
        stack.translate(scaleX, scaleY, 0);
        stack.scale(scaleAnim, scaleAnim, 1);
        stack.translate(-scaleX, -scaleY, 0);

        int bgAlpha = (int) (45 + hoverAnim * 15);
        int borderAlpha = (int) (80 + hoverAnim * 40);

        RenderUtil.Rounded.roundedWithOutline(stack, x, y + delta, width, height, 1,
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(20, 20, 20, bgAlpha),
                        ColorUtil.multAlpha(theme.clientColor(), .08f),
                        interpolate), alpha(1)),
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(40, 40, 40, borderAlpha),
                        ColorUtil.multAlpha(theme.clientColor(), .15f),
                        interpolate), alpha(1)),
                Round.of(4));

        String text = module.getName() + (input ? "[...]" : (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && module.getKey() > 0 ? "[" + Keyboard.keyName(module.getKey()) + "]" : ""));
        Fonts.SF_MEDIUM.draw(stack, text, x + 5.5f, y + delta + 10.5f - Fonts.SF_MEDIUM.getHeight(text, 8) / 2f,
                ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), interpolate), alpha(1)), 8);

        if (inRange(y + delta + 6.5f, 8, guiY, guiHeight)) {
            RenderUtil.Rounded.smooth(stack, x + width - 18.5f, y + delta + 6.5f, 14, 8,
                    ColorUtil.multAlpha(ColorUtil.overCol(
                            ColorUtil.getColor(30, 30, 30, 180),
                            ColorUtil.multDark(theme.clientColor(), .6f),
                            interpolate), alpha(1)),
                    Round.of(4));

            RenderUtil.Rounded.smooth(stack, x + width - 17.5f + interpolate * 6, y + delta + 7.5f, 6, 6,
                    ColorUtil.multAlpha(ColorUtil.overCol(
                            ColorUtil.getColor(60, 54, 69, 200),
                            ColorUtil.getColor(188, 167, 255),
                            interpolate), alpha(1)),
                    Round.of(3f));
        }

        stack.pop();

        for (Setting setting : settings)
            setting.render(stack, mouseX, mouseY);
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (isHovered(mouseX, mouseY, x + width - 18.5f, y + delta + 6.5f, 14, 8) && action == GLFW.GLFW_PRESS) {
            if (button == 0)
                module.toggle();
            else if (button == 2)
                input = !input;
        }

        for (Setting setting : settings)
            setting.mouse(mouseX, mouseY, button, action);
    }

    @Override
    void key(int key) {
        for (Setting setting : settings)
            setting.key(key);

        if (input) {
            module.setKey(key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE ? -1 : key);
            input = false;
        }
    }

    @Override
    void charTyped(char typed) {
        for (Setting setting : settings)
            setting.charTyped(typed);
    }

    public void scroll(double delta, float mouseX, float mouseY) {
        for (Setting setting : settings) {
            if (setting.isItemSelectorHovered(mouseX, mouseY)) {
                setting.scroll(delta);
                return;
            }
        }
    }

    public boolean isAnyItemSelectorHovered(float mouseX, float mouseY) {
        for (Setting setting : settings) {
            if (setting.isItemSelectorHovered(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    public float alpha(float alpha) {
        return category.alpha(alpha);
    }
}