package org.privates.client.screen.flatgui.impl.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatBindSettingComponent extends AbstractPanel {
    private final BindSetting value;
    private final float fontSize = 7F;
    private boolean binding = false;

    public FlatBindSettingComponent(BindSetting value) {
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
        if (!value.getVisible().get()) {
            return;
        }

        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + (size().y / 2F) - (fontSize / 2F),
                ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

        int keyValue = value.getValue();
        String valueName = binding ? "wait..." : (keyValue == Keyboard.KEY_NONE.getKey() ? "none" : Keyboard.keyName(keyValue).toLowerCase());
        float valueWidth = Fonts.SF_MEDIUM.getWidth(valueName, fontSize);
        float padding = 5F;
        float buttonWidth = valueWidth + 8F;
        float buttonHeight = fontSize + 4F;
        float buttonX = position().x + size().x - buttonWidth - padding;
        float buttonY = position().y + (size().y / 2F) - (buttonHeight / 2F);

        RenderUtil.Rounded.smooth(matrix, buttonX, buttonY, buttonWidth, buttonHeight,
                ColorUtil.getColor(32, 32, 32, alpha()), Round.of(2));

        Fonts.SF_MEDIUM.drawCenter(matrix, valueName, buttonX + buttonWidth / 2, buttonY + (buttonHeight - fontSize) / 2,
                ColorUtil.getColor(255, 255, 255, alpha()), fontSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!value.getVisible().get()) {
            return false;
        }

        int keyValue = value.getValue();
        String valueName = binding ? "wait..." : (keyValue == Keyboard.KEY_NONE.getKey() ? "none" : Keyboard.keyName(keyValue).toLowerCase());
        float valueWidth = Fonts.SF_MEDIUM.getWidth(valueName, fontSize);
        float padding = 5F;
        float buttonWidth = valueWidth + 8F;
        float buttonHeight = fontSize + 4F;
        float buttonX = position().x + size().x - buttonWidth - padding;
        float buttonY = position().y + (size().y / 2F) - (buttonHeight / 2F);

        if (hovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
            if (button == Keyboard.MOUSE_LEFT.getKey()) {
                binding = !binding;
                return true;
            }
        }

        if (binding && button == Keyboard.MOUSE_MIDDLE.getKey()) {
            value.set(button);
            binding = false;
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
        if (binding) {
            if (keyCode == Keyboard.KEY_ESCAPE.getKey() || keyCode == Keyboard.KEY_DELETE.getKey()) {
                value.set(Keyboard.KEY_NONE.getKey());
                binding = false;
            } else if (keyCode >= 0 && keyCode <= 348) {
                value.set(keyCode);
                binding = false;
            }
            return true;
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