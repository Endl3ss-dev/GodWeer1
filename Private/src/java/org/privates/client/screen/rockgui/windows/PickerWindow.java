package org.privates.client.screen.rockgui.windows;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;

public class PickerWindow extends Window {

    private final ColorSetting parent;
    private boolean dragPicker, dragHue;
    private float hue, brightness, saturation;

    public PickerWindow(ColorSetting parent, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        updateColor();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        int bgColor = ColorUtil.multAlpha(theme.backgroundColor(), opening.get());
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        RenderUtil.CustomStyled.drawGui(matrixStack, x, y, width, height, 7, true, 20f);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, width, height);

        Fonts.SF_MEDIUM.draw(matrixStack, "Color Picker", x + 5, y + 5, textColor, 12);

        float pickerSize = 108;
        int hueColor = ColorUtil.HUEtoRGB((int) (hue * 360));

        RenderUtil.Rounded.drawGradientRound(matrixStack,
                x + 5, y + 33,
                pickerSize, pickerSize,
                5,
                ColorUtil.getColor(255, 255, 255),
                hueColor,
                ColorUtil.getColor(0, 0, 0),
                ColorUtil.getColor(0, 0, 0)
        );

        if (dragPicker) {
            saturation = Mathf.clamp(0, 1, ((float) mouseX - (x + 5)) / pickerSize);
            brightness = Mathf.clamp(0, 1, ((float) mouseY - (y + 33)) / pickerSize);
            updateColor();
        }

        if (dragHue) {
            hue = Mathf.clamp(0, 1, ((float) mouseY - (y + 36)) / (pickerSize * 0.92f));
            updateColor();
        }

        float circleX = x + 5 + pickerSize * saturation;
        float circleY = y + 33 + pickerSize * brightness;

        RenderUtil.Rounded.roundedOutline(matrixStack,
                circleX - 3, circleY - 3,
                6, 6,
                1,
                ColorUtil.getColor(255, 255, 255),
                Round.of(3)
        );

        for (int i = 0; i < 10; i++) {
            int color1 = Color.HSBtoRGB(i / 10f, 1, 1);
            int color2 = Color.HSBtoRGB((i + 1) / 10f, 1, 1);

            RenderUtil.Rounded.roundedRect(matrixStack,
                    x + width - 18,
                    y + 33 + i * pickerSize / 10,
                    13,
                    pickerSize / 10 + 0.3f,
                    color1,
                    Round.of(i == 0 ? 5 : 0.1f, i == 0 ? 5 : 0.1f, i == 9 ? 5 : 0.1f, i == 9 ? 5 : 0.1f)
            );
        }

        float hueCircleY = y + 35 + pickerSize * hue;
        RenderUtil.Rounded.smooth(matrixStack,
                x + width - 14.6f, hueCircleY,
                6, 6,
                ColorUtil.getColor(255, 255, 255),
                Round.of(3)
        );

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public boolean clicked(double mouseX, double mouseY, int button) {
        if (!Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
            opening.setForward(false);
            return false;
        }

        float pickerSize = 108;

        if (Mathf.isHover((float) mouseX, (float) mouseY, x + 5, y + 33, pickerSize, pickerSize)) {
            dragPicker = true;
        }

        if (Mathf.isHover((float) mouseX, (float) mouseY, x + width - 18, y + 34, 13, pickerSize)) {
            dragHue = true;
        }

        return true;
    }

    @Override
    public boolean released(double mouseX, double mouseY, int button) {
        dragPicker = false;
        dragHue = false;
        return false;
    }

    private void updateColor() {
        int rgb = Color.HSBtoRGB(hue, saturation, 1 - brightness);
        parent.set(rgb);
    }
}