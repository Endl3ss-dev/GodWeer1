package org.privates.client.screen.flatgui.impl.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.DelimiterSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatDelimiterSettingComponent extends AbstractPanel {
    private final DelimiterSetting value;
    private final float fontSize = 7F;

    public FlatDelimiterSettingComponent(DelimiterSetting value) {
        this.value = value;
        size().set(100F, 20F);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        float lineWidth = 5;
        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x, position().y + (size().y / 2F) - (fontSize / 2F), ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

        float textWidth = Fonts.SF_MEDIUM.getWidth(value.getName(), fontSize);
        float line = 0.5f;
        boolean lineCheck = textWidth > (size().x - 5 - lineWidth - 5);

        RectUtil.drawRect(matrix, position().x + size().x - 5 - (lineCheck ? lineWidth : (size().x - 5 - textWidth - 5)), position().y + (size().y / 2F) - (line / 2F), (lineCheck ? lineWidth : (size().x - 5 - textWidth - 5)) + 5, line, ColorUtil.getColor(128, 128, 128, alpha()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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