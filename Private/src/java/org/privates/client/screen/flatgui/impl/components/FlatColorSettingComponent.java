package org.privates.client.screen.flatgui.impl.components;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Namespaced;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatColorSettingComponent extends AbstractPanel {
    private final ColorSetting value;
    private final Namespaced hueTexture = new Namespaced("texture/hue.png");
    private boolean huePickerDown;
    private boolean saturationDown;
    private boolean lightnessDown;
    private float huePointer = 0;
    private float saturationPointer = 0;
    private float lightnessPointer = 0;
    private final float sliderHeight = 4;
    private int hueSelectorColor;
    private float hue;
    private float saturation;
    private float lightness;
    private final float fontSize = 7F;

    public FlatColorSettingComponent(ColorSetting value) {
        this.value = value;
        size().set(100F, 0F);
        setVisible(() -> value.getVisible().get());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        Color color = new Color(value.getValue());
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        huePointer = hsb[0] * size().x;
        saturationPointer = hsb[1] * size().x;
        lightnessPointer = hsb[2] * size().x;
        hue = Mathf.clamp(0.01F, 0.99F, hsb[0]);
        saturation = Mathf.clamp(0.01F, 0.99F, hsb[1]);
        lightness = Mathf.clamp(0.01F, 0.99F, hsb[2]);
        hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        if (!value.getVisible().get()) {
            size().y = 0;
            return;
        }


        Fonts.SF_MEDIUM.draw(matrix, value.getName(), position().x + 5, position().y + 2, ColorUtil.getColor(255, 255, 255, alpha()), fontSize);


        float startY = position().y + fontSize + 5;
        float sliderWidth = size().x - 10;
        float spacing = 5F;
        float pickerWidth = 2;

        float hueY = startY;
        RenderUtil.bindTexture(hueTexture);
        RenderUtil.Texture.smooth(matrix, position().x + 5, hueY, sliderWidth, sliderHeight, alpha(), Round.of(2));
        RenderUtil.Rounded.smooth(matrix, position().x + 5 + huePointer - (pickerWidth / 2F), hueY, pickerWidth, sliderHeight, ColorUtil.getColor(255, 255, 255, alpha()), Round.of(pickerWidth / 2F));


        float saturationY = hueY + sliderHeight + spacing;
        int white = ColorUtil.getColor(255, alpha());
        int hueColor = ColorUtil.replAlpha(hueSelectorColor, alpha());
        RenderUtil.Rounded.smooth(matrix, position().x + 5, saturationY, sliderWidth, sliderHeight, white, white, hueColor, hueColor, Round.of(2));
        RenderUtil.Rounded.smooth(matrix, position().x + 5 + saturationPointer - (pickerWidth / 2F), saturationY, pickerWidth, sliderHeight, ColorUtil.getColor(255, 255, 255, alpha()), Round.of(pickerWidth / 2F));


        float lightnessY = saturationY + sliderHeight + spacing;
        int black = ColorUtil.getColor(0, alpha());
        RenderUtil.Rounded.smooth(matrix, position().x + 5, lightnessY, sliderWidth, sliderHeight, black, black, hueColor, hueColor, Round.of(2));
        RenderUtil.Rounded.smooth(matrix, position().x + 5 + lightnessPointer - (pickerWidth / 2F), lightnessY, pickerWidth, sliderHeight, ColorUtil.getColor(255, 255, 255, alpha()), Round.of(pickerWidth / 2F));


        float previewWidth = 14;
        float previewX = position().x + size().x - previewWidth - 5;
        float previewY = position().y + 2;
        int bgColor = ColorUtil.replAlpha(ColorUtil.multDark(value.getValue(), 0.8f), alpha());
        int mainColor = ColorUtil.replAlpha(value.getValue(), alpha());
        RenderUtil.Rounded.smooth(matrix, previewX, previewY, previewWidth, fontSize, bgColor, mainColor, mainColor, bgColor, Round.of(fontSize * 0.4f));


        if (huePickerDown) {
            huePointer = (mouseX - (position().x + 5));
            huePointer = Mathf.clamp(0F, sliderWidth, huePointer);
            hue = Mathf.clamp(0.01F, 0.99F, (huePointer / sliderWidth));
            hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
        }
        if (saturationDown) {
            saturationPointer = (mouseX - (position().x + 5));
            saturationPointer = Mathf.clamp(0F, sliderWidth, saturationPointer);
            saturation = Mathf.clamp(0.01F, 0.99F, (saturationPointer / sliderWidth));
        }
        if (lightnessDown) {
            lightnessPointer = (mouseX - (position().x + 5));
            lightnessPointer = Mathf.clamp(0F, sliderWidth, lightnessPointer);
            lightness = Mathf.clamp(0.01F, 0.99F, (lightnessPointer / sliderWidth));
        }
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));


        size().y = lightnessY + sliderHeight + 5 - position().y;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!value.getVisible().get()) {
            return false;
        }

        float sliderWidth = size().x - 10;
        float startY = position().y + fontSize + 5;
        float spacing = 5F;

        if (button == 0) {
            huePickerDown = hovered(mouseX, mouseY, position().x + 5, startY, sliderWidth, sliderHeight);
            saturationDown = hovered(mouseX, mouseY, position().x + 5, startY + sliderHeight + spacing, sliderWidth, sliderHeight);
            lightnessDown = hovered(mouseX, mouseY, position().x + 5, startY + (sliderHeight + spacing) * 2, sliderWidth, sliderHeight);
            return huePickerDown || saturationDown || lightnessDown;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        huePickerDown = saturationDown = lightnessDown = false;
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));
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
        huePickerDown = saturationDown = lightnessDown = false;
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));
    }
}