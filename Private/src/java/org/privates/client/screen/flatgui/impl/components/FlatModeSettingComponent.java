package org.privates.client.screen.flatgui.impl.components;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatModeSettingComponent extends AbstractPanel {
    private final ModeSetting value;
    private final float fontSize = 7F;

    public FlatModeSettingComponent(ModeSetting value) {
        this.value = value;
        size().set(100F, 0);
        setVisible(() -> value.getVisible().get());
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

        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + 2, ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

        float maxWidth = size().x;
        float wOffset = 0;
        float startY = position().y + fontSize + 5;
        float buttonHeight = fontSize + 4;
        float currentY = startY;
        int rowCount = 1;

        for (String setting : value.values) {
            float tOffset = Fonts.SF_MEDIUM.getWidth(setting, fontSize) + 8;

            if (wOffset + tOffset > maxWidth - 5) {
                wOffset = 0;
                currentY += buttonHeight + 5;
                rowCount++;
            }
            if (value.getValue().equals(setting)) {

            } else {
                RenderUtil.Rounded.smooth(matrix, position().x + 5 + wOffset, currentY,
                        tOffset, buttonHeight, ColorUtil.getColor(169, 169, 169, alpha() * 0.05f), Round.of(2));
            }

            Fonts.SF_MEDIUM.drawCenter(matrix, setting, position().x + 5 + wOffset + tOffset / 2, currentY + (buttonHeight / 2F) - (fontSize / 2F), ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

            wOffset += tOffset + 5;
        }

        size().y = 5F + (buttonHeight + 5) * rowCount + 5;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (!value.getVisible().get()) {
            return false;
        }

        float maxWidth = size().x;
        float wOffset = 0;
        float startY = position().y + fontSize + 5;
        float buttonHeight = fontSize + 4;
        float currentY = startY;

        for (String setting : value.values) {
            float tOffset = Fonts.SF_MEDIUM.getWidth(setting, fontSize) + 8;

            if (wOffset + tOffset > maxWidth - 5) {
                wOffset = 0;
                currentY += buttonHeight + 5;
            }

            if (button == 0 && hovered(mouseX, mouseY, position().x + 5 + wOffset, currentY, tOffset, buttonHeight)) {
                value.set(setting);
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