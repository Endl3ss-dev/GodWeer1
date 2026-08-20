package org.privates.client.screen.flatgui.impl.components;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatSliderSettingComponent extends AbstractPanel {
    private final SliderSetting value;
    private final SliderSetting localValue;
    private final float fontSize = 7F;
    private boolean drag;

    public FlatSliderSettingComponent(SliderSetting value) {
        this.value = this.localValue = value;
        size().set(100F, 20F);
        setVisible(() -> value.getVisible().get());
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!value.getVisible().get()) {
            return;
        }

        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + 2,
                ColorUtil.getColor(255, 255, 255, alpha()), fontSize);

        float sliderHeight = 2;
        float sliderY = position().y + fontSize + 8;
        float displayValue = value.getValue();

        float progress = (value.max == value.min) ? 0 : (displayValue - value.min) / (value.max - value.min);
        RenderUtil.Rounded.smooth(matrix, position().x + 5, sliderY, size().x - 10, sliderHeight,
                ColorUtil.getColor(32, 32, 32, alpha()), Round.of(sliderHeight / 2F));
        //Theme.getInstance().drawClientRectNoShadow(matrix, position().x + 5, sliderY,
        //        (size().x - 10) * progress, sliderHeight, 255, 2);

        float circleSize = drag ? 4 : 3;
        RenderUtil.Rounded.smooth(matrix, position().x + 5 + ((size().x - 10) * progress) - (circleSize / 2F),
                sliderY - (circleSize / 2F) + (sliderHeight / 2F), circleSize, circleSize,
                ColorUtil.getColor(255, 255, 255, alpha()), Round.of(circleSize / 2F));

        if (drag) {
            float denominator = size().x - 10;
            if (denominator <= 0) denominator = 1;
            float step = value.increment <= 0 ? 0.1F : value.increment;
            float newValue = (float) MathHelper.clamp(
                    Mathf.step((mouseX - (position().x + 5)) / denominator * (value.max - value.min) + value.min,
                            step), value.min, value.max);
            localValue.set(newValue);
        }

        Fonts.SF_MEDIUM.drawRight(matrix, displayValue + "/" + value.max,
                position().x + size().x - 5, sliderY + sliderHeight - 10,
                ColorUtil.getColor(128, 128, 128, alpha()), fontSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!value.getVisible().get()) {
            return false;
        }

        if (hovered(mouseX, mouseY, position().x + 5, position().y + fontSize + 8, size().x - 10, 2)) {
            drag = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        drag = false;
        value.set(localValue.getValue());
        return false;
    }

    @Override
    public void onClose() {
        drag = false;
        value.set(localValue.getValue());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
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
}