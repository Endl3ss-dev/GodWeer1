package org.privates.client.screen.clickgui.component.setting.impl;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.alt.Scissor;
import org.privates.client.managers.module.settings.impl.ThemeSetting;
import org.privates.client.screen.clickgui.component.setting.SettingComponent;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

public class ThemeSettingComponent extends SettingComponent {
    private final ThemeSetting value;
    private boolean expanded = false;
    private float scrollOffset = 0f;

    public ThemeSettingComponent(ThemeSetting value) {
        super(value);
        this.value = value;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        value.getAnimation().update();

        float valueHeight = drawName(matrix, mouseX, mouseY, size.x - 40);

        ThemeSetting.ThemePreset currentPreset = value.getValue();
        String presetName = currentPreset != null ? "Theme " + (value.getPresets().indexOf(currentPreset) + 1) : "None";

        Fonts.SF_MEDIUM.drawRight(matrix, presetName, position.x + size.x - margin - 20,
                position.y + margin, ColorUtil.replAlpha(getWhite(), alpha()), 7);

        value.getAnimation().run(expanded ? 1 : 0, 0.25, Easings.SINE_OUT, true);
        float expandAnim = value.getAnimation().get();

        float buttonWidth = 16f;
        float buttonX = position.x + size.x - buttonWidth - margin;
        float buttonY = position.y + margin;

        RenderUtil.Rounded.roundedWithOutline(matrix, buttonX, buttonY, buttonWidth, 10f, 1f,
                ColorUtil.multAlpha(accentColor(), 0.15f * alphaPC()),
                ColorUtil.multAlpha(accentColor(), 0.25f * alphaPC()),
                Round.of(3f));

        Fonts.SF_BOLD.drawRight(matrix, expanded ? "-" : "+", position.x + size.x - margin, buttonY + 1.5f,
                ColorUtil.replAlpha(getWhite(), alpha()), 7);

        if (expandAnim > 0.01f) {
            float boxSize = 22f;
            float spacing = 3f;
            int cols = Math.max(1, (int) ((size.x - 8f) / (boxSize + spacing)));
            int rows = (int) Math.ceil((float) value.getPresets().size() / cols);

            float panelY = position.y + margin + valueHeight + margin;
            float panelHeight = (rows * (boxSize + spacing)) * expandAnim;

            RenderUtil.Rounded.roundedWithOutline(matrix, position.x, panelY, size.x, panelHeight, 1f,
                    ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), expandAnim * alphaPC()),
                    ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40), expandAnim * alphaPC()),
                    Round.of(3f));

            Scissor.push();
            Scissor.setFromComponentCoordinates((int) position.x, (int) panelY, (int) size.x, (int) panelHeight);

            float startX = position.x + 4f;
            float currentX = startX;
            float currentY = panelY + 2f + scrollOffset;
            int currentCol = 0;

            for (ThemeSetting.ThemePreset preset : value.getPresets()) {
                boolean isSelected = preset == currentPreset;
                boolean isHovered = isHover(mouseX, mouseY, currentX, currentY, boxSize, boxSize);

                if (isSelected) {
                    RenderUtil.Rounded.roundedOutline(matrix, currentX - 1.5f, currentY - 1.5f,
                            boxSize + 3f, boxSize + 3f, 1.5f,
                            ColorUtil.multAlpha(accentColor(), expandAnim * alphaPC()),
                            Round.of(4f));
                }

                if (preset.isGradient()) {
                    int leftColor = ColorUtil.multAlpha(preset.getColor1(), expandAnim * alphaPC());
                    int rightColor = ColorUtil.multAlpha(preset.getColor2(), expandAnim * alphaPC());
                    RenderUtil.Rounded.drawGradientRound(matrix, currentX, currentY, boxSize, boxSize, 3f,
                            leftColor, leftColor, rightColor, rightColor);
                } else {
                    int baseColor = ColorUtil.multAlpha(preset.getColor1(), expandAnim * alphaPC());
                    int darkColor = ColorUtil.multDark(baseColor, 0.7f);
                    RenderUtil.Rounded.drawGradientRound(matrix, currentX, currentY, boxSize, boxSize, 3f,
                            baseColor, darkColor, darkColor, baseColor);
                }

                if (isHovered && !isSelected) {
                    RenderUtil.Rounded.roundedRect(matrix, currentX, currentY, boxSize, boxSize,
                            ColorUtil.getColor(255, 255, 255, (int) (30 * expandAnim * alphaPC())),
                            Round.of(3f));
                }

                currentCol++;
                if (currentCol >= cols) {
                    currentCol = 0;
                    currentX = startX;
                    currentY += boxSize + spacing;
                } else {
                    currentX += boxSize + spacing;
                }
            }

            Scissor.unset();
            Scissor.pop();
        }

        size.y = margin + valueHeight + margin + (expandAnim > 0.01f ?
                ((int) Math.ceil((float) value.getPresets().size() / Math.max(1, (int) ((size.x - 8f) / 25f))) * 25f) * expandAnim : 0f) + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float valueHeight = valueHeight();
        float buttonWidth = 16f;
        float buttonX = position.x + size.x - buttonWidth - margin;
        float buttonY = position.y + margin;

        if (isLClick(button) && isHover(mouseX, mouseY, buttonX, buttonY, buttonWidth, 10f)) {
            expanded = !expanded;
            return true;
        }

        if (expanded && value.getAnimation().get() > 0.8f) {
            float boxSize = 22f;
            float spacing = 3f;
            int cols = Math.max(1, (int) ((size.x - 8f) / (boxSize + spacing)));

            float panelY = position.y + margin + valueHeight + margin;
            float startX = position.x + 4f;
            float currentX = startX;
            float currentY = panelY + 2f + scrollOffset;
            int currentCol = 0;

            for (ThemeSetting.ThemePreset preset : value.getPresets()) {
                if (isLClick(button) && isHover(mouseX, mouseY, currentX, currentY, boxSize, boxSize)) {
                    value.set(preset);
                    expanded = false;
                    return true;
                }

                currentCol++;
                if (currentCol >= cols) {
                    currentCol = 0;
                    currentX = startX;
                    currentY += boxSize + spacing;
                } else {
                    currentX += boxSize + spacing;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public void onClose() {
        expanded = false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (expanded) {
            scrollOffset += (float) (delta * 5f);
            float maxScroll = Math.max(0, ((int) Math.ceil((float) value.getPresets().size() /
                    Math.max(1, (int) ((size.x - 8f) / 25f))) * 25f) - 100f);
            scrollOffset = org.privates.client.utils.math.Mathf.clamp(scrollOffset, -maxScroll, 0);
            return true;
        }
        return false;
    }
}