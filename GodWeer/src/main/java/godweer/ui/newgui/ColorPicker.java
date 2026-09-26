package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.modules.api.setting.ColorSetting;
import godweer.utils.color.ColorUtils;
import godweer.utils.math.Mathf;
import godweer.utils.render.draw.RenderDraw;
import godweer.utils.render.draw.Round;
import godweer.utils.render.font.GuiFonts;

import java.awt.Color;

public class ColorPicker {

    private static final float PICKER_WIDTH_RATIO = 0.82f;
    private static final float PADDING = 6f;
    private static final float BOTTOM_PADDING = 22f;
    private static final float HUE_BAR_WIDTH = 5.5f;
    private static final float HUE_BAR_OFFSET = 19f;
    private static final float ALPHA_BAR_OFFSET = 12f;
    private static final float KNOB_SIZE = 7f;
    private static final float SLIDER_HEIGHT = 2.5f;
    private static final float TEXT_HEIGHT = 9f;
    private static final float TEXT_BOTTOM_OFFSET = 11f;
    private static final int HUE_SEGMENTS = 360;
    private static final int ALPHA_SEGMENTS = 80;

    private final ColorSetting colorSetting;
    private final Setting settingNew;

    private float parentX;
    private int color, alpha;
    private float x, y, width, height;
    private float hueX, hueY;

    private boolean isHueDragging, isPickerDragging, isAlphaDrag;
    private float hueValue, alphaValue, saturation, brightness;
    private float hueAnimX, hueAnimY, huePointerAnim, alphaPointerAnim;
    private float fadeAlpha;
    private float knobFadeAlpha, sliderFadeAlpha;
    private boolean isOpen;

    public ColorPicker(float x, float y, float width, float height, ColorSetting colorSetting, Setting setting) {
        this.parentX = x;
        this.x = x - width - 5;
        this.y = y;
        this.width = width;
        this.height = height;
        this.settingNew = setting;
        this.colorSetting = colorSetting;
        this.isOpen = true;
        this.fadeAlpha = 0f;
        this.knobFadeAlpha = 0f;
        this.sliderFadeAlpha = 0f;
        initializeFromColor();
    }

    public void setScale(float scale) {
        this.parentX = scale;
        this.x = scale - width - 5;
    }

    public float getScale() {
        return this.parentX;
    }

    private void initializeFromColor() {
        this.color = colorSetting.getValue();
        float[] rgba = ColorUtils.getColorA(color);
        float[] hsb = Color.RGBtoHSB((int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255), null);

        this.hueValue = Mathf.clamp(hsb[0], 0f, 1f);
        this.saturation = Mathf.clamp(hsb[1], 0f, 1f);
        this.brightness = Mathf.clamp(hsb[2], 0f, 1f);
        this.alphaValue = Mathf.clamp(rgba[3] * 255f, 0f, 255f);
        this.alpha = (int) alphaValue;

        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();
        this.hueX = pickerW * saturation + PADDING;
        this.hueY = pickerH * (1 - brightness) + PADDING;
        this.hueAnimX = hueX;
        this.hueAnimY = hueY;
        this.huePointerAnim = hueValue * pickerH;
        this.alphaPointerAnim = (alphaValue / 255f) * pickerH;
    }

    private void updateColorFromHSB() {
        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();
        this.saturation = Mathf.clamp((hueX - PADDING) / pickerW, 0f, 1f);
        this.brightness = Mathf.clamp(1 - (hueY - PADDING) / pickerH, 0f, 1f);
        int rgb = Color.HSBtoRGB(hueValue, saturation, brightness);
        this.color = ColorUtils.replAlpha(rgb, (int) (alphaValue / 255f * 255));
        colorSetting.set(color);
    }

    private void updateAlphaValue() {
        this.color = ColorUtils.setAlphaColor(color, (int) alphaValue);
        this.alpha = (int) alphaValue;
        colorSetting.set(color);
    }

    private float getPickerWidth() {
        return (width * PICKER_WIDTH_RATIO) - PADDING * 2;
    }

    private float getPickerHeight() {
        return height - BOTTOM_PADDING;
    }

    private void updateAnimations() {
        float targetFade = isOpen ? 1.0f : 0.0f;
        fadeAlpha = Mathf.fast(fadeAlpha, targetFade, 15);

        float targetKnobFade = (fadeAlpha > 0.5f) ? 1.0f : 0.0f;
        knobFadeAlpha = Mathf.fast(knobFadeAlpha, targetKnobFade, 12);

        float targetSliderFade = (fadeAlpha > 0.5f) ? 1.0f : 0.0f;
        sliderFadeAlpha = Mathf.fast(sliderFadeAlpha, targetSliderFade, 12);

        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();
        float knobHalfSize = KNOB_SIZE / 2f;

        hueAnimX = Mathf.fast(hueAnimX, Mathf.clamp(hueX, PADDING + knobHalfSize, PADDING + pickerW - knobHalfSize), isPickerDragging ? 25 : 18);
        hueAnimY = Mathf.fast(hueAnimY, Mathf.clamp(hueY, PADDING + knobHalfSize, PADDING + pickerH - knobHalfSize), isPickerDragging ? 25 : 18);

        float targetHuePos = hueValue * pickerH;
        float targetAlphaPos = (alphaValue / 255f) * pickerH;
        float sliderHalfHeight = SLIDER_HEIGHT / 2f;

        huePointerAnim = Mathf.fast(huePointerAnim, Mathf.clamp(targetHuePos, sliderHalfHeight, pickerH - sliderHalfHeight), isHueDragging ? 25 : 16);
        alphaPointerAnim = Mathf.fast(alphaPointerAnim, Mathf.clamp(targetAlphaPos, sliderHalfHeight, pickerH - sliderHalfHeight), isAlphaDrag ? 25 : 16);
    }

    private void handleDragging(int mouseX, int mouseY) {
        if (isPickerDragging) handlePickerDrag(mouseX, mouseY);
        if (isHueDragging) handleHueDrag(mouseY);
        if (isAlphaDrag) handleAlphaDrag(mouseY);
    }

    private void handlePickerDrag(int mouseX, int mouseY) {
        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();
        float relativeX = mouseX - x;
        float relativeY = mouseY - y;

        hueX = Mathf.clamp(relativeX, PADDING, PADDING + pickerW);
        hueY = Mathf.clamp(relativeY, PADDING, PADDING + pickerH);
        updateColorFromHSB();
    }

    private void handleHueDrag(int mouseY) {
        float pickerH = getPickerHeight();
        float relativeY = mouseY - y - PADDING;
        hueValue = Mathf.clamp(relativeY / pickerH, 0f, 1f);
        updateColorFromHSB();
    }

    private void handleAlphaDrag(int mouseY) {
        float pickerH = getPickerHeight();
        float relativeY = mouseY - y - PADDING;
        alphaValue = Mathf.clamp((relativeY / pickerH) * 255f, 0f, 255f);
        updateAlphaValue();
    }

    public void render(PoseStack stack, int mouseX, int mouseY) {
        updateAnimations();
        handleDragging(mouseX, mouseY);

        if (fadeAlpha < 0.01f) return;

        renderBackground(stack);
        renderColorPicker(stack);
        renderKnob(stack);
        renderHueBar(stack);
        renderAlphaBar(stack);
        renderSliders(stack);
        renderRGBAText(stack);
    }

    private void renderBackground(PoseStack stack) {
        int shadowAlpha = (int) (120 * fadeAlpha);
        int bgAlpha = (int) (250 * fadeAlpha);
        int outlineAlpha = (int) (255 * fadeAlpha);

        RenderDraw.Shadow.drawShadow(stack, x, y, width, height, 10, 0.7f, ColorUtils.getColor(0, 0, 0, shadowAlpha));
        RenderDraw.Rounded.roundedRect(stack, x, y, width, height, ColorUtils.getColor(16, 18, 22, bgAlpha), Round.of(10));
        RenderDraw.Rounded.roundedOutline(stack, x, y, width, height, 1.5f, ColorUtils.getColor(45, 50, 55, outlineAlpha), Round.of(10));
    }

    private void renderColorPicker(PoseStack stack) {
        int hueColor = Color.HSBtoRGB(hueValue, 1.0f, 1.0f);
        float pickerX = x + PADDING;
        float pickerY = y + PADDING;
        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();

        int a1 = (int) (255 * fadeAlpha);

        RenderDraw.Rounded.roundedRect(stack, pickerX, pickerY, pickerW, pickerH,
                ColorUtils.getColor(0, 0, 0, a1),
<<<<<<< HEAD
<<<<<<< HEAD
                ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255), fadeAlpha),
=======
                ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), fadeAlpha),
>>>>>>> be39d49e669e2142432a36b1bc0484f06041f13b
=======
                ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), fadeAlpha),
>>>>>>> 42510f564926933ae9da905274ef1c63749450a2
                ColorUtils.getColor(0, 0, 0, a1),
                ColorUtils.multAlpha(hueColor, fadeAlpha), Round.of(7));
    }

    private void renderKnob(PoseStack stack) {
        if (knobFadeAlpha < 0.01f) return;

        float knobX = x + hueAnimX - KNOB_SIZE / 2f;
        float knobY = y + hueAnimY - KNOB_SIZE / 2f;

        int shadowAlpha = (int) (180 * knobFadeAlpha);
        int ringAlpha = (int) (200 * knobFadeAlpha);
        int innerAlpha = (int) (alpha * knobFadeAlpha);

        RenderDraw.Shadow.drawShadow(stack, knobX - 1.5f, knobY - 1.5f, KNOB_SIZE + 3, KNOB_SIZE + 3, 4, 0.9f, ColorUtils.getColor(0, 0, 0, shadowAlpha));
        RenderDraw.Rounded.roundedRect(stack, knobX - 1.5f, knobY - 1.5f, KNOB_SIZE + 3, KNOB_SIZE + 3, ColorUtils.getColor(40, 40, 40, ringAlpha), Round.of((KNOB_SIZE + 3) / 2f));
        RenderDraw.Rounded.roundedRect(stack, knobX, knobY, KNOB_SIZE, KNOB_SIZE, ColorUtils.getColor(255, 255, 255, innerAlpha), Round.of(KNOB_SIZE / 2f));
    }

    private void renderHueBar(PoseStack stack) {
        float hueBarX = x + width - HUE_BAR_OFFSET;
        float hueBarY = y + PADDING;
        float pickerH = getPickerHeight();
        float radius = HUE_BAR_WIDTH / 2f;

        float inc = 1.0f / HUE_SEGMENTS;
        float segmentHeight = pickerH / HUE_SEGMENTS;
        for (int i = 0; i < HUE_SEGMENTS; i++) {
            float yPos = hueBarY + (pickerH * i / HUE_SEGMENTS);
            int color1 = ColorUtils.multAlpha(Color.HSBtoRGB(i * inc, 1.0F, 1.0F), fadeAlpha);
            int color2 = ColorUtils.multAlpha(Color.HSBtoRGB((i + 1) * inc, 1.0F, 1.0F), fadeAlpha);

            RenderDraw.Rounded.gradientRect(stack, hueBarX, yPos, HUE_BAR_WIDTH, segmentHeight,
                    color1, color1, color2, color2,
                    i == 0 ? Round.of(radius, radius, 0, 0) :
                            i == HUE_SEGMENTS - 1 ? Round.of(0, 0, radius, radius) : Round.of(0));
        }

        int outlineAlpha = (int) (255 * fadeAlpha);
        RenderDraw.Rounded.roundedOutline(stack, hueBarX, hueBarY, HUE_BAR_WIDTH, pickerH, 1.5f,
                ColorUtils.getColor(35, 40, 45, outlineAlpha), Round.of(radius));
    }

    private void renderAlphaBar(PoseStack stack) {
        float alphaBarX = x + width - ALPHA_BAR_OFFSET;
        float alphaBarY = y + PADDING;
        float pickerH = getPickerHeight();
        float radius = HUE_BAR_WIDTH / 2f;

        float segmentHeight = pickerH / ALPHA_SEGMENTS;
        for (int i = 0; i < ALPHA_SEGMENTS; i++) {
            float segY = alphaBarY + (i * segmentHeight);
            int baseColor = (i % 2 == 0) ? 55 : 75;
            int checkAlpha = (int) (255 * fadeAlpha);
            int checkColor = ColorUtils.getColor(baseColor, baseColor, baseColor, checkAlpha);

            RenderDraw.Rounded.roundedRect(stack, alphaBarX, segY, HUE_BAR_WIDTH, segmentHeight, checkColor,
                    i == 0 ? Round.of(radius, radius, 0, 0) :
                            i == ALPHA_SEGMENTS - 1 ? Round.of(0, 0, radius, radius) : Round.of(0));
        }

        int topColor = ColorUtils.multAlpha(ColorUtils.setAlphaColor(color, 0), fadeAlpha);
        int bottomColor = ColorUtils.multAlpha(ColorUtils.setAlphaColor(color, 255), fadeAlpha);

        RenderDraw.Rounded.gradientRect(stack, alphaBarX, alphaBarY, HUE_BAR_WIDTH, pickerH,
                topColor, topColor, bottomColor, bottomColor, Round.of(radius));

        int outlineAlpha = (int) (255 * fadeAlpha);
        RenderDraw.Rounded.roundedOutline(stack, alphaBarX, alphaBarY, HUE_BAR_WIDTH, pickerH, 1.5f,
                ColorUtils.getColor(35, 40, 45, outlineAlpha), Round.of(radius));
    }

    private void renderSliders(PoseStack stack) {
        if (sliderFadeAlpha < 0.01f) return;
        renderSlider(stack, x + width - HUE_BAR_OFFSET, y + PADDING + huePointerAnim);
        renderSlider(stack, x + width - ALPHA_BAR_OFFSET, y + PADDING + alphaPointerAnim);
    }

    private void renderSlider(PoseStack stack, float sliderX, float sliderY) {
        float adjustedY = sliderY - SLIDER_HEIGHT / 2f;

        int shadowAlpha = (int) (160 * sliderFadeAlpha);
        int ringAlpha = (int) (220 * sliderFadeAlpha);
        int innerAlpha = (int) (alpha * sliderFadeAlpha);

        RenderDraw.Shadow.drawShadow(stack, sliderX - 0.75f, adjustedY - 0.75f, HUE_BAR_WIDTH + 1.5f, SLIDER_HEIGHT + 1.5f, 3, 0.8f, ColorUtils.getColor(0, 0, 0, shadowAlpha));
        RenderDraw.Rounded.roundedRect(stack, sliderX - 0.75f, adjustedY - 0.75f, HUE_BAR_WIDTH + 1.5f, SLIDER_HEIGHT + 1.5f, ColorUtils.getColor(40, 40, 40, ringAlpha), Round.of((SLIDER_HEIGHT + 1.5f) / 2f));
        RenderDraw.Rounded.roundedRect(stack, sliderX, adjustedY, HUE_BAR_WIDTH, SLIDER_HEIGHT, ColorUtils.getColor(255, 255, 255, innerAlpha), Round.of(SLIDER_HEIGHT / 2f));
    }

    private void renderRGBAText(PoseStack stack) {
        if (fadeAlpha < 0.5f) return;

        float[] c = ColorUtils.getColorA(color);
        String rgbaText = String.format("R:%d G:%d B:%d A:%d", (int) (c[0] * 255), (int) (c[1] * 255), (int) (c[2] * 255), (int) alphaValue);
        float textY = y + height - TEXT_BOTTOM_OFFSET;

        int bgAlpha = (int) (200 * fadeAlpha);
        int textAlpha = (int) (255 * fadeAlpha);

        RenderDraw.Rounded.roundedRect(stack, x + PADDING, textY, width - PADDING * 2, TEXT_HEIGHT, ColorUtils.getColor(10, 12, 16, bgAlpha), Round.of(4.5f));
        GuiFonts.SF_MEDIUM.drawCenter(stack, rgbaText, x + width / 2f, textY + 1.5f, ColorUtils.getColor(190, 195, 205, textAlpha), 7f);
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
            resetDragFlags();
            isOpen = false;
            if (settingNew != null) settingNew.setSelected(false);
            return;
        }

        if (mouseButton != 0) return;

        float pickerX = x + PADDING;
        float pickerY = y + PADDING;
        float pickerW = getPickerWidth();
        float pickerH = getPickerHeight();

        if (Mathf.isHover((float) mouseX, (float) mouseY, pickerX, pickerY, pickerW, pickerH)) {
            isPickerDragging = true;
            return;
        }

        float hueBarX = x + width - HUE_BAR_OFFSET;
        float hueBarY = y + PADDING;

        if (Mathf.isHover((float) mouseX, (float) mouseY, hueBarX, hueBarY, HUE_BAR_WIDTH, pickerH)) {
            isHueDragging = true;
            return;
        }

        float alphaBarX = x + width - ALPHA_BAR_OFFSET;
        float alphaBarY = y + PADDING;

        if (Mathf.isHover((float) mouseX, (float) mouseY, alphaBarX, alphaBarY, HUE_BAR_WIDTH, pickerH)) {
            isAlphaDrag = true;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int state) {
        resetDragFlags();
    }

    private void resetDragFlags() {
        isHueDragging = false;
        isAlphaDrag = false;
        isPickerDragging = false;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
