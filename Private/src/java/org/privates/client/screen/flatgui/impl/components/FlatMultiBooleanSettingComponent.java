package org.privates.client.screen.flatgui.impl.components;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatMultiBooleanSettingComponent extends AbstractPanel {
    private final MultiBooleanSetting value;
    private final float baseFontSize = 7F;
    private final float minFontSize = 4F;
    private final float maxButtonWidth = 80F;

    public FlatMultiBooleanSettingComponent(MultiBooleanSetting value) {
        this.value = value;
        size().set(100F, 0);
        setVisible(() -> value.getVisible().get());
    }

    private float getAdjustedFontSize(String text, float maxWidth) {
        float fontSize = baseFontSize;
        float textWidth = Fonts.SF_MEDIUM.getWidth(text, fontSize);

        while (textWidth > maxWidth && fontSize > minFontSize) {
            fontSize -= 0.5F;
            textWidth = Fonts.SF_MEDIUM.getWidth(text, fontSize);
        }

        return fontSize;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!value.getVisible().get()) {
            size().y = 0;
            return;
        }

        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + 2,
                ColorUtil.getColor(255, 255, 255, alpha()), baseFontSize);

        float maxWidth = size().x;
        float wOffset = 0;
        float startY = position().y + baseFontSize + 5;
        float currentY = startY;
        int rowCount = 1;
        float maxButtonHeightInRow = baseFontSize + 4;

        for (BooleanSetting setting : value.getValues()) {
            if (!setting.getVisible().get()) continue;

            String text = setting.getName();
            float fontSize = getAdjustedFontSize(text, maxButtonWidth - 8);
            float tOffset = Fonts.SF_MEDIUM.getWidth(text, fontSize) + 8;
            float buttonHeight = fontSize + 4;

            if (wOffset + tOffset > maxWidth - 5) {
                wOffset = 0;
                currentY += maxButtonHeightInRow + 5;
                rowCount++;
                maxButtonHeightInRow = buttonHeight;
            } else {
                maxButtonHeightInRow = Math.max(maxButtonHeightInRow, buttonHeight);
            }

            if (setting.getValue()) {

            } else {
                RenderUtil.Rounded.smooth(matrix, position().x + 5 + wOffset, currentY,
                        tOffset, buttonHeight, ColorUtil.getColor(169, 169, 169, alpha() * 0.05f), Round.of(2));
            }

            Fonts.SF_MEDIUM.drawCenter(matrix, text, position().x + 5 + wOffset + tOffset / 2,
                    currentY + (buttonHeight - fontSize) / 2, ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

            wOffset += tOffset + 5;
        }

        size().y = 5F + baseFontSize + 5 + (maxButtonHeightInRow + 5) * (rowCount - 1) + maxButtonHeightInRow + 3;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!value.getVisible().get()) {
            return false;
        }

        float maxWidth = size().x;
        float wOffset = 0;
        float startY = position().y + baseFontSize + 5;
        float currentY = startY;
        float maxButtonHeightInRow = baseFontSize + 4;

        for (BooleanSetting setting : value.getValues()) {
            if (!setting.getVisible().get()) continue;

            String text = setting.getName();
            float fontSize = getAdjustedFontSize(text, maxButtonWidth - 8);
            float tOffset = Fonts.SF_MEDIUM.getWidth(text, fontSize) + 8;
            float buttonHeight = fontSize + 4;

            if (wOffset + tOffset > maxWidth - 5) {
                wOffset = 0;
                currentY += maxButtonHeightInRow + 5;
                maxButtonHeightInRow = buttonHeight;
            } else {
                maxButtonHeightInRow = Math.max(maxButtonHeightInRow, buttonHeight);
            }

            if (button == 0 && hovered(mouseX, mouseY, position().x + 5 + wOffset,
                    currentY, tOffset, buttonHeight)) {
                value.get(setting.getName()).set(!setting.getValue());
                return true;
            }
            wOffset += tOffset + 5;
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
    }
}