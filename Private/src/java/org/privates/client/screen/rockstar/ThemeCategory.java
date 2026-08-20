package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.Excellent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.ThemeSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.HashMap;

@Getter
@Setter
@Accessors(fluent = true)
public class ThemeCategory extends Builder {

    private final HashMap<ThemeSetting.ThemePreset, Float> selectedV = new HashMap<>();
    private final HashMap<ThemeSetting.ThemePreset, ColorPicker> first = new HashMap<>(),
            second = new HashMap<>();
    private final RockstarScreen gui;

    private float delta, deltaV;
    private float lastY;

    public ThemeCategory(RockstarScreen gui, float x, float y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        final ThemeSetting themePreset = Theme.getInstance().themePreset();
        for (ThemeSetting.ThemePreset tp : Theme.getInstance().themePreset().getPresets()) {
            selectedV.put(tp, themePreset.is(tp.getName()) ? 1F : 0F);
            first.put(tp, new ColorPicker(0, 0, 90, 90, Excellent.inst().moduleManager().get(Theme.class), tp.getColor1()));
            second.put(tp, new ColorPicker(0, 0, 90, 90, Excellent.inst().moduleManager().get(Theme.class), tp.getColor2()));
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {
        deltaV = Mathf.fast(deltaV, delta, 16);
        float offsetX = this.x + width + 15, offsetY = gui.y() + deltaV + 29;
        if (lastY == 0)
            lastY = offsetY - deltaV + delta;
        final float themeWidth = 100, themeHeight = 33, space = 5;
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(gui.x(), gui.y() + 25.5f, gui.width(), gui.height() - 26f);
        for (ThemeSetting.ThemePreset theme : Theme.getInstance().themePreset().getPresets()) {

            if (offsetX + themeWidth + space >= gui.x() + gui.width() - 7) {
                offsetX = this.x + width + 15;
                offsetY += themeHeight + space;
                lastY = offsetY - deltaV + delta;
            }

            final float selected = selectedV.get(theme);
            selectedV.replace(theme, Mathf.fast(selected, Theme.getInstance().themePreset().getValue() == theme ? 1F : 0F, 16));
            theme.setGradientV(Mathf.fast(theme.getGradientV(), theme.isGradient() ? 1 : 0, 24));
            final ColorPicker first = this.first.get(theme),
                    second = this.second.get(theme);

            // offsetX + 7.5f, offsetY + themeHeight - 15.5f, 6
            first.setX(offsetX + 13.5f);
            first.setY(offsetY + themeHeight - 15.5f - first.getHeight());

            second.setX(offsetX + 23.5f);
            second.setY(offsetY + themeHeight - 15.5f - second.getHeight());

            if (inRange(offsetY, themeHeight, gui.y() + 25, gui.height() - 25)) {
                second.setScaleV(Mathf.fast(second.getScaleV(), second.getScale() * alpha, 16));
                first.setScaleV(Mathf.fast(first.getScaleV(), first.getScale() * alpha, 16));
                RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, themeWidth, themeHeight, ColorUtil.getColor(14, 16, 20, alpha), Round.of(3));
                if (selected >= .1f) {
                    RenderUtil.Rounded.roundedOutline(stack, offsetX, offsetY, themeWidth, themeHeight, 1, ColorUtil.getColor(35, 37, 41, selected * alpha), Round.of(3));
                }
                Fonts.SF_SEMIBOLD.draw(stack, theme.getName(), offsetX + 6, offsetY + 6, ColorUtil.getColor(238, 240, 244, (.78f + .22f * selected) * alpha), 7);

                RenderUtil.Rounded.roundedRect(stack, offsetX + 7.5f, offsetY + themeHeight - 15.5f, 6, 6, ColorUtil.multAlpha(theme.getColor1(), alpha), Round.of(4));
                RenderUtil.Rounded.roundedOutline(stack, offsetX + 6, offsetY + themeHeight - 17, 9, 9, 1, ColorUtil.multAlpha(theme.getColor1(), .5f * alpha), Round.of(3.5f));

                RenderUtil.Rounded.roundedRect(stack, offsetX + 17.5f, offsetY + themeHeight - 15.5f, 6, 6, ColorUtil.multAlpha(theme.getColor2(), alpha), Round.of(4));
                RenderUtil.Rounded.roundedOutline(stack, offsetX + 16f, offsetY + themeHeight - 17, 9, 9, 1, ColorUtil.multAlpha(theme.getColor2(), .5f * alpha), Round.of(3.5f));

                //Fonts.SF_SEMIBOLD.draw(stack, "gradient", offsetX + 6, offsetY + themeHeight - 9, ColorUtil.getColor(255, 255, 255, .4f), 5f);

                final float gradientV = theme.getGradientV();
                final int leftColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(255, 255, 255, .3f), ColorUtil.fade(7, 0, theme.getColor1(), theme.getColor2()), gradientV), alpha),
                        rightColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(255, 255, 255, .3f), ColorUtil.fade(7, 30, theme.getColor1(), theme.getColor2()), gradientV), alpha);
                RenderUtil.Rounded.roundedRect(stack, offsetX + themeWidth - 14, offsetY + 7, 8, 8, ColorUtil.getColor(0, 0, 0, .1f * alpha), Round.of(3));
                RenderUtil.Rounded.roundedOutline(stack, offsetX + themeWidth - 14, offsetY + 7, 8, 8, .75f, leftColor, leftColor, rightColor, rightColor, Round.of(1));
                //if(theme.isGradient()) {

                GlStateManager.pushMatrix();
                GlStateManager.translatef((offsetX + themeWidth - 10), (offsetY + 11), 1);
                if (gradientV <= .95f) {
                    GlStateManager.scalef(1 - gradientV, 1 - gradientV, 1 - gradientV);
                    GlStateManager.translatef(-(offsetX + themeWidth - 10), -(offsetY + 11), 1);
                    Fonts.ROCKSTAR.draw(stack, "j", offsetX + themeWidth - Fonts.ROCKSTAR.getWidth("j", 4) / 2f - 10, offsetY + 11 - Fonts.ROCKSTAR.getHeight("j", 4) / 2f, ColorUtil.getColor(255, 255, 255, .4f * alpha), 4);
                } else if (gradientV >= .05f) {
                    GlStateManager.scalef(gradientV, gradientV, gradientV);
                    GlStateManager.translatef(-(offsetX + themeWidth - 10), -(offsetY + 11), 1);
                    Fonts.ROCKSTAR.draw(stack, "i", offsetX + themeWidth - Fonts.ROCKSTAR.getWidth("i", 4) / 2f - 10, offsetY + 11 - Fonts.ROCKSTAR.getHeight("i", 4) / 2f, leftColor, 4);
                }
                GlStateManager.popMatrix();
            } else if (first.getScaleV() >= .1f) {
                first.setScaleV(Mathf.fast(first.getScaleV(), 0, 16));
            } else if (second.getScaleV() >= .1f) {
                second.setScaleV(Mathf.fast(second.getScaleV(), 0, 16));
            }

            offsetX += themeWidth + space;
        }
        ScissorExp.unset();
        ScissorExp.pop();

        offsetX = this.x + width + 15;
        offsetY = gui.y() + deltaV + 29;
        for (ThemeSetting.ThemePreset tp : Theme.getInstance().themePreset().getPresets()) {

            if (offsetX + themeWidth + space >= gui.x() + gui.width() - 7) {
                offsetX = this.x + width + 15;
                offsetY += themeHeight + space;
            }

            final ColorPicker first = this.first.get(tp),
                    second = this.second.get(tp);
            if (first != null && first.getScaleV() >= .1f) {
                first.render(stack, (int) mouseX, (int) mouseY);
                if ((first.isDrag() || first.isAlphaDrag() || first.isHueDragging()))
                    tp.setColor1(ColorUtil.replAlpha(first.getColor(), ((int) first.getAlphaValue())));
            }
            if (second != null && second.getScaleV() >= .1f) {
                second.render(stack, (int) mouseX, (int) mouseY);
                if ((second.isDrag() || second.isAlphaDrag() || second.isHueDragging()))

                    tp.setColor2(ColorUtil.replAlpha(second.getColor(), ((int) second.getAlphaValue())));
            }


            offsetX += themeWidth + space;
        }
    }

    public void scroll(float mx, float my, float delta) {
        if (isHovered(mx, my, x + width + 15, gui.y() + 29, gui.width() - (width + 30), gui.height() - 29) && !(this.delta >= 0 && delta > 0) && !(delta < 0 && lastY + 33 <= gui.y() + gui.height() - 5)) {
            this.delta += delta * 18;
            if (this.delta > 0)
                this.delta = 0;
            else if (lastY + 33 <= gui.y() + gui.height() + 5) {
                this.delta -= (lastY + 33) - (gui.y() + gui.height() + 5);
            }
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, boolean action) {
        if (button == 0 && action) {
            float offsetX = this.x + width + 15, offsetY = gui.y() + deltaV + 29;
            final float themeWidth = 100, themeHeight = 33, space = 5;

            for (ThemeSetting.ThemePreset theme : Theme.getInstance().themePreset().getPresets()) {
                final ColorPicker first = this.first.get(theme),
                        second = this.second.get(theme);
                if (second.getScaleV() >= .1f && isHovered(mouseX, mouseY, second.getX(), second.getY(), second.getWidth(), second.getHeight())) {
                    second.mouseClicked(mouseX, mouseY, button);
                    return;
                } else if (first.getScaleV() >= .1f && isHovered(mouseX, mouseY, first.getX(), first.getY(), first.getWidth(), first.getHeight())) {
                    first.mouseClicked(mouseX, mouseY, button);
                    return;
                }
            }

            for (ThemeSetting.ThemePreset theme : Theme.getInstance().themePreset().getPresets()) {

                if (offsetX + themeWidth + space >= gui.x() + gui.width() - 7) {
                    offsetX = this.x + width + 15;
                    offsetY += themeHeight + space;
                    lastY = offsetY - deltaV + delta;
                }

                final ColorPicker first = this.first.get(theme),
                        second = this.second.get(theme);

                if (isHovered(mouseX, mouseY, offsetX + themeWidth - 14, offsetY + 7, 8, 8)) {
                    theme.setGradient(!theme.isGradient());
                    return;
                } else if (isHovered(mouseX, mouseY, offsetX + 7.5f, offsetY + themeHeight - 15.5f, 6, 6)) {
                    first.setScale(first.getScale() == 0F ? 1F : 0F);
                    return;
                } else if (isHovered(mouseX, mouseY, offsetX + 17.5f, offsetY + themeHeight - 15.5f, 6, 6)) {
                    second.setScale(second.getScale() == 0F ? 1F : 0F);
                    return;
                } else if (isHovered(mouseX, mouseY, offsetX, offsetY, themeWidth, themeHeight)) {
                    Theme.getInstance().themePreset().set(theme);
                    return;
                }
                offsetX += themeWidth + space;
            }
        } else if (!action) {
            for (ThemeSetting.ThemePreset theme : Theme.getInstance().themePreset().getPresets()) {
                if (second.get(theme).getScaleV() >= .1f) {
                    second.get(theme).mouseReleased(mouseX, mouseY, button);
                } else if (first.get(theme).getScaleV() >= .1f) {
                    first.get(theme).mouseReleased(mouseX, mouseY, button);
                }
            }
        }
    }

    @Override
    void key(int key) {

    }

    @Override
    void charTyped(char typed) {

    }
}
