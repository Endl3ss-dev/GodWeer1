package org.privates.client.screen.clickgui.component.setting.impl;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.alt.Scissor;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.screen.clickgui.component.setting.SettingComponent;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

public class BindSettingComponent extends SettingComponent {
    private final BindSetting value;

    public BindSettingComponent(BindSetting value) {
        super(value);
        this.value = value;
    }

    private boolean binding = false;

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
        String valueName = (binding ? "wait.." : "(" + Keyboard.keyName(value.getValue()) + ")").toLowerCase();
        Scissor.push();
        Scissor.setFromComponentCoordinates((int) (position.x + margin), (int) (position.y + 1), (int) (size.x - 10 - Fonts.SF_MEDIUM.getWidth(valueName, 7)), 10);
        stripFont().draw(Fonts.SF_BOLD, matrix, value.getName(), position.x + margin, position.y + 3.5F, size.x - 10 - Fonts.SF_MEDIUM.getWidth(valueName, 7), ColorUtil.replAlpha(getWhite(), alpha()), 7, 2.5F);
        Scissor.unset();
        Scissor.pop();
        float valueWidth = Fonts.SF_BOLD.getWidth(valueName, 7);
        float out = 1F;

        float valueHeight = 5;

        value.getAnimation().run(binding ? 1 : 0, 0.1, Easings.QUAD_OUT);

        int backColorDark = ColorUtil.multDark(accentColor(), 0.25F);
        int backColorBright = ColorUtil.multDark(accentColor(), 0.5F);
        int backColor = ColorUtil.overCol(backColorDark, backColorBright, value.getAnimation().get());

        RenderUtil.Rounded.smooth(matrix, position.x + size.x - valueWidth - out, position.y + margin, valueWidth + (out * 2), 7 + (out * 2), backColor, backColorDark, backColor, backColorDark, Round.of(2));

        Fonts.SF_BOLD.drawRight(matrix, valueName, position.x + size.x, position.y + margin + out, getWhite(), 7);

        size.y = margin + valueHeight + margin + margin - 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        String valueName = Keyboard.keyName(value.getValue());
        float valueWidth = Fonts.SF_BOLD.getWidth(valueName, 7);
        float out = 1;
        boolean valid = button != Keyboard.MOUSE_RIGHT.getKey() && button != Keyboard.MOUSE_LEFT.getKey();
        if (isHover(mouseX, mouseY, position.x + size.x - valueWidth - out, position.y + margin, valueWidth + (out * 2), 7 + (out * 2)) && !valid) {
            binding = !binding;
        } else {
            if (binding) {
                if (valid) {
                    value.set(button);
                }
                binding = false;
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
        boolean valid = keyCode != Keyboard.KEY_DELETE.getKey()
                && keyCode != Keyboard.KEY_ESCAPE.getKey()
                && keyCode != Keyboard.KEY_SPACE.getKey()
                && keyCode != value.getParent().getKey();
        if (binding && valid) {
            value.set(keyCode);
            binding = false;
        } else if (binding) {
            value.set(Keyboard.KEY_NONE.getKey());
            binding = false;
        }
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
        binding = false;
    }
}