package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;

@Getter
public class ColorPicker {
    private final ColorSetting colorSetting;
    private final SliderSetting hueSlider, alphaSlider;
    private final org.privates.client.screen.newgui.Setting settingNew;
    private final org.privates.client.screen.eternity.Setting settingEternity;
    @Getter
    @Setter
    private int color;

    @Getter
    @Setter
    private float scale, scaleV;
    private boolean isHueDragging;
    private int hueValue;
    private boolean drag = false;
    private boolean isFirst;
    private boolean isAlphaDrag = false;
    private float alphaValue;
    private final int selectedColor = 0;

    @Setter
    private float x, y, width, height;
    @Getter
    @Setter
    private float hueX, hueY;
    @Setter
    private int alpha;

    private final float resolutionScale = 1.0f;

    public ColorPicker(float x, float y, float width, float height, ColorSetting colorSetting) {
        scale = x;
        this.x = scale - width - 5;
        this.y = y;
        this.width = width;
        this.height = height;
        this.settingNew = null;
        this.settingEternity = null;

        this.hueSlider = new SliderSetting(colorSetting.getParent(), "Hue", 1.0F, 1.0F, 360.0F, 1.0F);
        hueSlider.setVisible(() -> false);
        this.alphaSlider = new SliderSetting(colorSetting.getParent(), "Alpha", 70, 0, 255, 1);
        alphaSlider.setVisible(() -> false);

        this.colorSetting = colorSetting;

        int initialColor = colorSetting.getValue();
        this.color = initialColor;

        float[] rgba = ColorUtil.getRGBAf(initialColor);
        float red = rgba[0];
        float green = rgba[1];
        float blue = rgba[2];
        float alpha = rgba[3];

        float[] hsb = Color.RGBtoHSB((int) (red * 255), (int) (green * 255), (int) (blue * 255), null);

        this.hueValue = (int) (hsb[0] * 360);
        this.alphaValue = alpha * 255;
        this.alpha = (int) alphaValue;

        float sScale = resolutionScale;

        this.hueX = Mathf.clamp(5 * sScale + (this.width - 10 * sScale) * hsb[1], 5 * sScale, this.width - 5 * sScale);
        this.hueY = Mathf.clamp(5 * sScale + (this.height - 30 * sScale) * (1 - hsb[2]), 5 * sScale, this.height - 25 * sScale);

        this.hueSlider.set((float) this.hueValue);
        this.alphaSlider.set(this.alphaValue);

        updateColorFromHSB();
    }

    public ColorPicker(float x, float y, float width, float height, Module parent, int color) {
        scale = x;
        this.x = scale - width - 5;
        this.y = y;
        this.width = width;
        this.height = height;
        this.settingNew = null;
        this.settingEternity = null;

        this.hueSlider = new SliderSetting(parent, "Hue", 1.0F, 1.0F, 360.0F, 1.0F);
        hueSlider.setVisible(() -> false);
        this.alphaSlider = new SliderSetting(parent, "Alpha", 70, 0, 255, 1);
        alphaSlider.setVisible(() -> false);

        this.colorSetting = new ColorSetting(parent, "Color").setVisible(() -> false);
        this.colorSetting.set(color);

        int initialColor = colorSetting.getValue();
        this.color = initialColor;

        float[] rgba = ColorUtil.getRGBAf(initialColor);
        float red = rgba[0];
        float green = rgba[1];
        float blue = rgba[2];
        float alpha = rgba[3];

        float[] hsb = Color.RGBtoHSB((int) (red * 255), (int) (green * 255), (int) (blue * 255), null);

        this.hueValue = (int) (hsb[0] * 360);
        this.alphaValue = alpha * 255;
        this.alpha = (int) alphaValue;

        float sScale = resolutionScale;
        this.hueX = Mathf.clamp(5 * sScale + (this.width - 10 * sScale) * hsb[1], 5 * sScale, this.width - 5 * sScale);
        this.hueY = Mathf.clamp(5 * sScale + (this.height - 30 * sScale) * (1 - hsb[2]), 5 * sScale, this.height - 25 * sScale);

        hueX = Math.max(5, Math.min(width - 5, hueX));
        hueY = Math.max(5, Math.min(height - 25, hueY));

        this.hueSlider.set((float) this.hueValue);
        this.alphaSlider.set(this.alphaValue);

        updateColorFromHSB();
    }

    private void updateColorFromHSB() {
        float sScale = resolutionScale;
        float saturation = Mathf.clamp((hueX - 5 * sScale) / (width - 10 * sScale), 0, 1);
        float brightness = Mathf.clamp(1 - (hueY - 5 * sScale) / (height - 30 * sScale), 0, 1);

        int rgb = Color.HSBtoRGB(hueValue / 360f, saturation, brightness);
        this.color = ColorUtil.replAlpha(rgb, alphaValue / 255f);
        this.colorSetting.set(color);
    }

    public void init() {
    }

    public void render(MatrixStack stack, int mouseX, int mouseY) {

        float sScale = scaleV;
        RenderUtil.Rounded.roundedRect(stack, this.x, this.y, this.width, this.height, ColorUtil.getColor(6, 8, 11, scaleV), Round.of(7));
        int hueColor = ColorUtil.HUEtoRGB(this.hueValue);
        RenderUtil.Rounded.roundedRect(stack, x + 5 * sScale, y + 5 * sScale, width - 10 * sScale, height - 30 * sScale, ColorUtil.getColor(0, 0, 0, scaleV), ColorUtil.getColor(255, 255, 255, scaleV), ColorUtil.getColor(0, 0, 0, scaleV), ColorUtil.multAlpha(hueColor, scaleV), Round.of(5));
        int pixelColor = 0;
        float inc = 0;
        float times = 0;
        this.alpha = 255;

        if (!this.isHueDragging && drag) {
            float clampedMouseX = Mathf.clamp((float) mouseX, x + 5 * sScale, x + width - 5 * sScale);
            float clampedMouseY = Mathf.clamp((float) mouseY, y + 5 * sScale, y + height - 25 * sScale);

            inc = (clampedMouseX - (this.x + 5.0F * sScale)) / (this.width - 10.0F * sScale);
            times = 1.0F - (clampedMouseY - (this.y + 5.0F * sScale)) / (this.height - 30 * sScale);

            inc = Mathf.clamp(inc, 0, 1);
            times = Mathf.clamp(times, 0, 1);

            pixelColor = Color.HSBtoRGB((float) this.hueValue / 360.0F, inc, times);

            hueX = Math.max(5, Math.min(clampedMouseX - x, width - 5));
            hueY = Math.max(5, Math.min(clampedMouseY - y, height - 25));
        }

        if (pixelColor != 0) {
            color = ColorUtil.replAlpha(pixelColor, alphaValue / 255f);
            this.colorSetting.set(color);
        }

        if (this.isHueDragging) {
            this.hueValue = Math.min(this.getSliderValue(this.hueSlider, this.x + 5.0F * sScale, mouseX), 360);

            inc = (hueX - (5.0F * sScale)) / (this.width - 10.0F * sScale);
            times = 1.0F - (hueY - (5.0F * sScale)) / (this.height - 30 * sScale);

            inc = Mathf.clamp(inc, 0, 1);
            times = Mathf.clamp(times, 0, 1);

            color = Color.HSBtoRGB((float) this.hueValue / 360.0F, inc, times);
            colorSetting.set(ColorUtil.replAlpha(color, alphaValue / 255f));
        }

        if (isAlphaDrag) {
            this.alphaValue = Math.min(getSliderValue(alphaSlider, x + 5 * sScale, mouseX), 255);
            colorSetting.set(ColorUtil.replAlpha(color, alphaValue / 255f));
        }

        inc = 0.2F;
        times = 1.0F / inc;
        float xHuePos = this.x + 5.0F * sScale;
        float size = (this.width - 10.0F * sScale) / times;

        for (int i = 0; (float) i < times; ++i) {
            boolean last = (float) i == times - 1.0F;
            if (last) {
                size -= sScale;
            }

            RenderUtil.Rounded.gradientRect(stack, xHuePos, this.y + this.height - 23.0F * sScale, size, 5.0F * sScale, ColorUtil.multAlpha(Color.HSBtoRGB(inc * (float) i, 1.0F, 1.0F), (alpha / 255f) * scaleV), ColorUtil.multAlpha(Color.HSBtoRGB(inc * (float) (i + 1), 1.0F, 1.0F), (alpha / 255f) * scaleV), ColorUtil.multAlpha(Color.HSBtoRGB(inc * (float) (i + 1), 1.0F, 1.0F), (alpha / 255f) * scaleV), ColorUtil.multAlpha(Color.HSBtoRGB(inc * (float) i, 1.0F, 1.0F), (alpha / 255f) * scaleV), Round.of(0));
            if (!last) {
                xHuePos += size;
            }
        }

        RenderUtil.Rounded.gradientRect(stack, x + 5 * sScale, y + height - 17 * sScale, width - 11 * sScale, 5 * sScale, ColorUtil.getColor(0, 0, 0, 0), ColorUtil.replAlpha(color, scaleV), ColorUtil.replAlpha(color, scaleV), ColorUtil.getColor(0, 0, 0, 0), Round.of(5));

        RenderUtil.Rounded.roundedRect(stack, x + hueX - 2 * sScale, y + hueY - 2 * sScale, 4 * sScale, 4 * sScale, ColorUtil.getColor(255, 255, 255, scaleV), Round.of(3 * sScale));

        RenderUtil.Shadow.drawShadow(stack, x + 4 * sScale + getPos(alphaSlider, (int) alphaValue), y + height - 18 * sScale, 2 * sScale, 7 * sScale, 2, ColorUtil.getColor(255, 255, 255, scaleV));
        RenderUtil.Rounded.roundedRect(stack, x + 4 * sScale + getPos(alphaSlider, (int) alphaValue), y + height - 18 * sScale, 2 * sScale, 7 * sScale, ColorUtil.getColor(255, 255, 255, scaleV), Round.of(2 * sScale));

        RenderUtil.Shadow.drawShadow(stack, x + 4 * sScale + (float) +getPos(hueSlider, hueValue), y + height - 24 * sScale, 2 * sScale, 7 * sScale, 2, ColorUtil.getColor(255, 255, 255, scaleV));
        RenderUtil.Rounded.roundedRect(stack, x + 4 * sScale + (float) +getPos(hueSlider, hueValue), y + height - 24 * sScale, 2 * sScale, 7 * sScale, ColorUtil.getColor(255, 255, 255, scaleV), Round.of(1 * sScale));

        float[] c = ColorUtil.getRGBAf(color);
        Fonts.SF_MEDIUM.draw(stack, String.format("r: %s g: %s b: %s a: %s", (int) (c[0] * 255), (int) (c[1] * 255), (int) (c[2] * 255), ((int) alphaValue)), x + 5 * sScale, y + height - 10 * sScale, ColorUtil.getColor(254, 254, 254, scaleV), 6 * sScale);
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        float sScale = resolutionScale;
        if (!Mathf.isHover((float) mouseX, (float) mouseY, this.x, this.y, this.width, this.height)) {
            if (settingNew != null) {
                scale -= (width + 10 * sScale);
                settingNew.setSelected(false);
            }
            if (settingEternity != null) {
                scale -= (width + 10 * sScale);
                settingEternity.setSelected(false);
            }
        }

        if (Mathf.isHover((float) mouseX, (float) mouseY, x + 5 * sScale, y + 5 * sScale, width - 10 * sScale, height - 30 * sScale) && mouseButton == 0)
            drag = true;

        if (Mathf.isHover((float) mouseX, (float) mouseY, this.x + 5.0F * sScale, this.y + this.height - 23.0F * sScale, this.width - 10.0F * sScale, 5.0F * sScale) && mouseButton == 0) {
            this.isHueDragging = true;
        }
        if (Mathf.isHover((float) mouseX, (float) mouseY, x + 5 * sScale, y + height - 17 * sScale, width - 11 * sScale, 5 * sScale) && mouseButton == 0)
            isAlphaDrag = true;
    }

    public void mouseReleased(double mouseX, double mouseY, int state) {
        this.isHueDragging = false;
        this.isAlphaDrag = false;
        drag = false;
    }

    public int getSliderValue(SliderSetting numberSetting, float posX, int mouseX) {
        int delta = (int) (numberSetting.max - numberSetting.min);
        float clickedX = (float) mouseX - posX;
        float sScale = resolutionScale;
        float value = clickedX / (this.width - 10.0F * sScale);
        float outValue = (float) (numberSetting.min + (double) ((float) delta * value));
        return (int) Mathf.clamp((float) ((int) Mathf.round(outValue, numberSetting.increment)), numberSetting.min, numberSetting.max);
    }

    public int getPos(SliderSetting numberSetting, int value) {
        int delta = (int) (numberSetting.max - numberSetting.min);
        float sScale = resolutionScale;
        return (int) ((double) (this.width - 10.0F * sScale) * ((double) value - numberSetting.min) / (double) delta);
    }
}