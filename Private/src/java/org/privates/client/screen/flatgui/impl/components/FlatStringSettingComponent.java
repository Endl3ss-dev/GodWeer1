package org.privates.client.screen.flatgui.impl.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2f;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.text.TextAlign;
import org.privates.client.utils.render.text.TextBox;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatStringSettingComponent extends AbstractPanel {
    private final StringSetting value;
    private final StringSetting localValue;
    public final TextBox textBox;
    private final float fontSize = 7F;

    public FlatStringSettingComponent(StringSetting value) {
        this.value = this.localValue = value;
        textBox = new TextBox(new Vector2f(), Fonts.SF_MEDIUM, fontSize, ColorUtil.getColor(255, 255, 255), TextAlign.LEFT, "Введите текст", 0, false, value.isOnlyNumber());
        textBox.setText(localValue.getValue());
        textBox.setCursor(localValue.getValue().length());
        size().set(100F, 20F);
        setVisible(() -> value.getVisible().get());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        textBox.setText(localValue.getValue());
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!value.getVisible().get()) {
            return;
        }

        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + (size().y / 2F) - (fontSize / 2F),
                ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

        float textBoxWidth = 60F;
        float padding = 5F;
        float textBoxHeight = fontSize + 4F;
        float textBoxX = position().x + size().x - textBoxWidth - padding;
        float textBoxY = position().y + (size().y / 2F) - (textBoxHeight / 2F);

        RenderUtil.Rounded.smooth(matrix, textBoxX, textBoxY, textBoxWidth, textBoxHeight,
                ColorUtil.getColor(167, 167, 167, alpha() * 0.1f), Round.of(2));

        textBox.setColor(ColorUtil.getColor(255, 255, 255, alpha()));
        textBox.getPosition().set(textBoxX + 5, textBoxY + (textBoxHeight - fontSize) / 2);
        textBox.setWidth(textBoxWidth - 10);
        textBox.draw(matrix);
        localValue.set(textBox.getText());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!value.getVisible().get()) {
            return false;
        }

        float textBoxWidth = 60F;
        float padding = 5F;
        float textBoxHeight = fontSize + 4F;
        float textBoxX = position().x + size().x - textBoxWidth - padding;
        float textBoxY = position().y + (size().y / 2F) - (textBoxHeight / 2F);

        if (button == 0 && hovered(mouseX, mouseY, textBoxX, textBoxY, textBoxWidth, textBoxHeight)) {
            textBox.mouse(mouseX, mouseY, button);
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
        if (textBox.selected) {
            textBox.keyPressed(keyCode);
            if (Keyboard.KEY_ENTER.isKey(keyCode)) {
                value.set(localValue.getValue());
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
        if (textBox.selected) {
            textBox.charTyped(codePoint);
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        textBox.selected = false;
        value.set(localValue.getValue());
    }
}