package org.privates.client.screen.flatgui.impl.components;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatBooleanSettingComponent extends AbstractPanel {
    private final BooleanSetting value;
    private final float fontSize = 7F;

    public FlatBooleanSettingComponent(BooleanSetting value) {
        this.value = value;
        size().set(100F, 20F);
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
        if (!visible()) {
            return;
        }

        value.getAnimation().update();
        float checkboxWidth = 12;
        float checkboxSize = 6;
        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + (size().y / 2F) - (fontSize / 2F), ColorUtil.getColor(255, 255, 255, alpha()), fontSize);
        value.getAnimation().run(value.getValue() ? 1 : 0, 0.25, Easings.BACK_OUT, true);
        int backColorDark = ColorUtil.multDark(accentColor(), 0.25F);
        int backColor = ColorUtil.overCol(backColorDark, accentColor(), value.getAnimation().get());
        RenderUtil.Rounded.smooth(matrix, position.x + size.x - checkboxWidth - 6, position.y + 8, checkboxWidth, checkboxSize, backColor, backColor, backColorDark, backColorDark, Round.of(checkboxSize / 2F));
        RenderUtil.Rounded.smooth(matrix, position.x + size.x - checkboxWidth + (value.getAnimation().get() * (checkboxWidth - checkboxSize)) + 0.5F - 6, position.y + 8 + 0.5F, checkboxSize - 1, checkboxSize - 1, new Color(255, 255, 255).hashCode(), Round.of(checkboxSize / 2F - 0.5F));
    }

    public int accentColor() {
        return ColorUtil.multAlpha(-1, 255);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible()) {
            return false;
        }

        float checkboxWidth = 12;
        float checkboxSize = 6;
        float checkboxX = position().x + size().x - checkboxWidth;
        float checkboxY = position().y + 4;

        if (button == 0 && hovered(mouseX, mouseY, position.x + size.x - checkboxWidth - 6, position.y + 8, checkboxWidth, checkboxSize)) {
            value.set(!value.getValue());
            return true;
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