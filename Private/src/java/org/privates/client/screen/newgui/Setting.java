package org.privates.client.screen.newgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class Setting extends Builder {

    private final Module module;
    private final org.privates.client.managers.module.settings.Setting setting;

    private boolean selected = false;
    private float selectedV, animV, offsetY, alphaV;
    private ArrayList<Float> interpolate = new ArrayList<>();
    private float delta, deltaV;
    private long time;
    private int offsetX;
    private ColorPicker colorPicker;

    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;

    private boolean isInputMode = false;
    private String inputValueString = "";

    private float itemScrollY = 0;
    private float itemScrollYV = 0;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();

    private float hoverAnim = 0.0f;
    private int typingProgress = 0;
    private long lastTypeTime = 0;
    private boolean wasHovered = false;

    private static long lastBindClickTime = 0;
    private static final int BIND_CLICK_COOLDOWN_MS = 200;

    private boolean isEditingMax = false;

    public Setting(Module module, org.privates.client.managers.module.settings.Setting setting, float x, float y) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.y = y;
        float sScale = module.getPanel().getGui().getResolutionScale();
        this.width = 270 * sScale;
        height = 12 * sScale;
        offsetY = ((boolean) setting.getVisible().get()) ? 1 : 0;
        if (setting instanceof MultiBooleanSetting s) {
            for (BooleanSetting booleanSetting : s.getValues()) {
                interpolate.add((float) (booleanSetting.getValue() ? 1 : 0));
            }
            time = System.currentTimeMillis();
        }
        if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());
            filteredItems.addAll(allItems);
        }
    }

    private float getResolutionScale() {
        return module.getPanel().getGui().getResolutionScale();
    }

    private void drawScrollingName(MatrixStack stack, String name, float x, float y, float availableWidth, int color, float size) {
        float sScale = getResolutionScale();
        float nameWidth = Fonts.SF_BOLD.getWidth(name, size);

        if (nameWidth > availableWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.5f * sScale;
                long updateRate = 20;

                if (currentTime - lastScrollTime > updateRate) {
                    if (scrollingForward) {
                        nameScrollX -= speed;
                        if (nameScrollX <= -(nameWidth - availableWidth)) {
                            nameScrollX = -(nameWidth - availableWidth);
                            scrollingForward = false;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    } else {
                        nameScrollX += speed;
                        if (nameScrollX >= 0) {
                            nameScrollX = 0;
                            scrollingForward = true;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    }
                    lastScrollTime = currentTime;
                }
            }
        } else {
            nameScrollX = 0;
        }

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - 2 * sScale, availableWidth, size + 4 * sScale);
        Fonts.SF_BOLD.draw(stack, name, x + nameScrollX, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void applyInputValue(SliderSetting s) {
        try {
            float newValue = Float.parseFloat(inputValueString);
            newValue = MathHelper.clamp(newValue, s.min, s.max);
            s.set(newValue);
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
    }

    private void applyInputValueRand(SliderSettingRand s) {
        try {
            float newValue = Float.parseFloat(inputValueString);
            newValue = MathHelper.clamp(newValue, s.min, s.max);
            SliderSettingRand.RangeValue current = s.getValue();
            if (isEditingMax) {
                if (newValue >= current.getMinValue()) {
                    current.setMaxValue(newValue);
                }
            } else {
                if (newValue <= current.getMaxValue()) {
                    current.setMinValue(newValue);
                }
            }
            s.set(current);
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
        isEditingMax = false;
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        float sScale = getResolutionScale();
        Theme theme = Theme.getInstance();

        boolean isCurrentlyHovered = isHovered(mouseX, mouseY, x, y + deltaV, width, 12 * sScale) && setting.getDescription() != null && !setting.getDescription().isEmpty();
        hoverAnim = Mathf.fast(hoverAnim, isCurrentlyHovered ? 1.0f : 0.0f, 10);

        if (isCurrentlyHovered && !wasHovered) {
            typingProgress = 0;
            lastTypeTime = System.currentTimeMillis();
        }
        wasHovered = isCurrentlyHovered;

        if (setting instanceof BooleanSetting s) {
            height = 12 * sScale;
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 16);

            float controlWidth = 16 * sScale + 4 * sScale;
            float availableNameWidth = width - controlWidth;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - 16 * sScale, y + deltaV + 2 * sScale, 16 * sScale, height - 4 * sScale, sScale, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, 60), ColorUtil.multAlpha(theme.clientColor(), .1f), selectedV), alphaV), ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, 60), ColorUtil.multAlpha(theme.clientColor(), .2f), selectedV), alphaV), Round.of(4 * sScale));
            RenderUtil.Rounded.roundedRect(stack, x + width - 16 * sScale + (selectedV * 8 * sScale + 1 * sScale), y + deltaV + 3 * sScale, 6 * sScale, 6 * sScale, ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, alphaV), ColorUtil.getColor(220, 220, 220, alphaV), selectedV), Round.of(4 * sScale));
        } else if (setting instanceof SliderSetting s) {
            if (FlatGui.getInstance().settingsDisplay().is("Под модулем")) {
                height = 20 * sScale;

                float nameY = y + deltaV + 2 * sScale;
                String valueText = String.format("%.2f", s.getValue());
                float valueTextWidth = Fonts.SF_BOLD.getWidth(isInputMode ? inputValueString + "_" : valueText, 7 * sScale);
                float boxWidth = valueTextWidth + 4 * sScale;
                float boxHeight = Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale;
                float boxX = x + width - boxWidth - 2 * sScale;
                float boxY = nameY - 2 * sScale;

                float availableNameWidth = boxX - x - 2 * sScale;
                drawScrollingName(stack, s.getName(), x + 0.5f * sScale, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                RenderUtil.Rounded.roundedRect(stack, boxX, boxY, boxWidth, boxHeight, ColorUtil.multAlpha(isInputMode ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));

                if (isInputMode) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, boxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, valueText, boxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }

                float sliderWidth = width - 4 * sScale;
                float sliderX = x + 0.5f * sScale;
                float sliderY = y + deltaV + height - 5 * sScale;

                if (selected) {
                    float sliderMinX = sliderX;
                    float sliderMaxX = sliderX + sliderWidth + 2;
                    float sliderMinY = sliderY - 3 * sScale;
                    float sliderMaxY = sliderY + 7 * sScale + 2;

                    if (mouseX >= sliderMinX && mouseX <= sliderMaxX && mouseY >= sliderMinY && mouseY <= sliderMaxY) {
                        float newProgress = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0.0f, 1.0f);
                        float step = s.increment <= 0 ? 0.1F : s.increment;
                        s.set((float) Mathf.step(s.min + (s.max - s.min) * newProgress, step));
                    } else {
                        selected = false;
                    }
                }

                float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
                float targetKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * progress) + 1.75f * sScale;
                animV = Mathf.fast(animV, targetKnobX, 16);

                RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, sliderWidth, 4 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(3 * sScale));
                RenderUtil.Rounded.roundedWithOutline(
                        stack, animV, sliderY - 1f + sScale, 4f * sScale, 3.75f * sScale, 1 * sScale,
                        ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV),
                        ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(1.75f * sScale));
            } else {
                height = 12 * sScale;
                float sliderWidth = 138 * sScale;
                float sliderX = x + width - sliderWidth;

                String valueText = String.format("%.2f", s.getValue());
                float valueTextWidth = Fonts.SF_BOLD.getWidth(isInputMode ? inputValueString + "_" : valueText, 7 * sScale);

                float availableNameWidth = sliderX - (valueTextWidth + 7 * sScale) - x - 4 * sScale;
                float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;

                drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                if (selected) {
                    float sliderMinX = sliderX;
                    float sliderMaxX = sliderX + sliderWidth;
                    float sliderMinY = y + deltaV;
                    float sliderMaxY = y + deltaV + height;

                    if (mouseX >= sliderMinX && mouseX <= sliderMaxX && mouseY >= sliderMinY && mouseY <= sliderMaxY) {
                        float denominator = sliderWidth;
                        if (denominator <= 0) denominator = 1 * sScale;
                        float step = s.increment <= 0 ? 0.1f : s.increment;
                        s.set((float) MathHelper.clamp(
                                Mathf.step((mouseX - sliderX) / denominator * (s.max - s.min) + s.min, step),
                                s.min, s.max));
                    } else {
                        selected = false;
                    }
                }

                float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
                float targetKnobX = sliderX + ((sliderWidth - 8 * sScale) * progress) + 2 * sScale;
                animV = Mathf.fast(animV, targetKnobX, 16);

                RenderUtil.Rounded.roundedRect(stack, sliderX, y + deltaV + 5.5f * sScale, sliderWidth, 5 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(5 * sScale));
                RenderUtil.Rounded.roundedWithOutline(
                        stack, animV, y + deltaV + 6 * sScale, 4 * sScale, 4 * sScale, 1.5f * sScale,
                        ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV),
                        ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(2 * sScale));

                float valueBoxWidth = valueTextWidth + 4 * sScale;
                float valueBoxX = sliderX - valueBoxWidth - 3 * sScale;
                float valueBoxY = y + deltaV + height / 2f - (Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale) / 2f;

                RenderUtil.Rounded.roundedRect(stack, valueBoxX, valueBoxY, valueBoxWidth, Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale, ColorUtil.multAlpha(isInputMode ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));

                if (isInputMode) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, valueBoxX + 2 * sScale, valueBoxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, valueText, valueBoxX + 2 * sScale, valueBoxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }
            }
        } else if (setting instanceof SliderSettingRand s) {
            if (FlatGui.getInstance().settingsDisplay().is("Под модулем")) {
                height = 28 * sScale;

                float nameY = y + deltaV + 2 * sScale;
                String minValueText = String.format("%.2f", s.getValue().getMinValue());
                String maxValueText = String.format("%.2f", s.getValue().getMaxValue());

                float minValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && !isEditingMax ? inputValueString + "_" : minValueText, 7 * sScale);
                float maxValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && isEditingMax ? inputValueString + "_" : maxValueText, 7 * sScale);
                float minBoxWidth = minValueWidth + 4 * sScale;
                float maxBoxWidth = maxValueWidth + 4 * sScale;
                float boxHeight = Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale;

                float minBoxX = x + width - minBoxWidth - maxBoxWidth - 6 * sScale;
                float maxBoxX = x + width - maxBoxWidth - 2 * sScale;
                float boxY = nameY - 2 * sScale;

                float availableNameWidth = minBoxX - x - 2 * sScale;
                drawScrollingName(stack, s.getName(), x + 0.5f * sScale, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                RenderUtil.Rounded.roundedRect(stack, minBoxX, boxY, minBoxWidth, boxHeight, ColorUtil.multAlpha(isInputMode && !isEditingMax ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));
                RenderUtil.Rounded.roundedRect(stack, maxBoxX, boxY, maxBoxWidth, boxHeight, ColorUtil.multAlpha(isInputMode && isEditingMax ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));

                if (isInputMode && !isEditingMax) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, minBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, minValueText, minBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }

                if (isInputMode && isEditingMax) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, maxBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, maxValueText, maxBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }

                float sliderWidth = width - 4 * sScale;
                float sliderX = x + 0.5f * sScale;
                float sliderY = y + deltaV + height - 10 * sScale;

                if (selected) {
                    float newProgress = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0.0f, 1.0f);
                    float step = s.increment <= 0 ? 0.1F : s.increment;
                    float newValue = (float) Mathf.step(s.min + (s.max - s.min) * newProgress, step);

                    SliderSettingRand.RangeValue current = s.getValue();
                    if (isEditingMax) {
                        if (newValue >= current.getMinValue()) {
                            current.setMaxValue(newValue);
                            s.set(current);
                        }
                    } else {
                        if (newValue <= current.getMaxValue()) {
                            current.setMinValue(newValue);
                            s.set(current);
                        }
                    }
                }

                float minProgress = (s.max == s.min) ? 0 : (s.getValue().getMinValue() - s.min) / (s.max - s.min);
                float maxProgress = (s.max == s.min) ? 0 : (s.getValue().getMaxValue() - s.min) / (s.max - s.min);

                float minKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * minProgress) + 1.75f * sScale;
                float maxKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * maxProgress) + 1.75f * sScale;

                RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, sliderWidth, 4 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(3 * sScale));
                RenderUtil.Rounded.roundedRect(stack, minKnobX, sliderY, maxKnobX - minKnobX + 4f * sScale, 4 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 5f), Round.of(3 * sScale));

                RenderUtil.Rounded.roundedWithOutline(stack, minKnobX, sliderY - 1f + sScale, 4f * sScale, 3.75f * sScale, 1 * sScale, ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV), ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(1.75f * sScale));
                RenderUtil.Rounded.roundedWithOutline(stack, maxKnobX, sliderY - 1f + sScale, 4f * sScale, 3.75f * sScale, 1 * sScale, ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV), ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(1.75f * sScale));
            } else {
                height = 12 * sScale;
                float sliderWidth = 138 * sScale;
                float sliderX = x + width - sliderWidth;

                String minValueText = String.format("%.2f", s.getValue().getMinValue());
                String maxValueText = String.format("%.2f", s.getValue().getMaxValue());
                float minValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && !isEditingMax ? inputValueString + "_" : minValueText, 7 * sScale);
                float maxValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && isEditingMax ? inputValueString + "_" : maxValueText, 7 * sScale);

                float availableNameWidth = sliderX - (minValueWidth + maxValueWidth + 14 * sScale) - x - 4 * sScale;
                float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;

                drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                if (selected) {
                    float denominator = sliderWidth;
                    if (denominator <= 0) denominator = 1 * sScale;
                    float step = s.increment <= 0 ? 0.1f : s.increment;
                    float newValue = (float) MathHelper.clamp(Mathf.step((mouseX - sliderX) / denominator * (s.max - s.min) + s.min, step), s.min, s.max);

                    SliderSettingRand.RangeValue current = s.getValue();
                    if (isEditingMax) {
                        if (newValue >= current.getMinValue()) {
                            current.setMaxValue(newValue);
                            s.set(current);
                        }
                    } else {
                        if (newValue <= current.getMaxValue()) {
                            current.setMinValue(newValue);
                            s.set(current);
                        }
                    }
                }

                float minProgress = (s.max == s.min) ? 0 : (s.getValue().getMinValue() - s.min) / (s.max - s.min);
                float maxProgress = (s.max == s.min) ? 0 : (s.getValue().getMaxValue() - s.min) / (s.max - s.min);

                float minKnobX = sliderX + ((sliderWidth - 8 * sScale) * minProgress) + 2 * sScale;
                float maxKnobX = sliderX + ((sliderWidth - 8 * sScale) * maxProgress) + 2 * sScale;

                RenderUtil.Rounded.roundedRect(stack, sliderX, y + deltaV + 5.5f * sScale, sliderWidth, 5 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(5 * sScale));
                RenderUtil.Rounded.roundedRect(stack, minKnobX, y + deltaV + 5.5f * sScale, maxKnobX - minKnobX + 4 * sScale, 5 * sScale, ColorUtil.multAlpha(theme.clientColor(), alphaV / 5f), Round.of(5 * sScale));

                RenderUtil.Rounded.roundedWithOutline(stack, minKnobX, y + deltaV + 6 * sScale, 4 * sScale, 4 * sScale, 1.5f * sScale, ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV), ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(2 * sScale));
                RenderUtil.Rounded.roundedWithOutline(stack, maxKnobX, y + deltaV + 6 * sScale, 4 * sScale, 4 * sScale, 1.5f * sScale, ColorUtil.multAlpha(theme.clientColor(), .4f * alphaV), ColorUtil.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(2 * sScale));

                float minBoxWidth = minValueWidth + 4 * sScale;
                float maxBoxWidth = maxValueWidth + 4 * sScale;
                float minBoxX = sliderX - minBoxWidth - maxBoxWidth - 6 * sScale;
                float maxBoxX = sliderX - maxBoxWidth - 3 * sScale;
                float boxY = y + deltaV + height / 2f - (Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale) / 2f;

                RenderUtil.Rounded.roundedRect(stack, minBoxX, boxY, minBoxWidth, Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale, ColorUtil.multAlpha(isInputMode && !isEditingMax ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));
                RenderUtil.Rounded.roundedRect(stack, maxBoxX, boxY, maxBoxWidth, Fonts.SF_BOLD.getHeight(maxValueText, 7 * sScale) + 4 * sScale, ColorUtil.multAlpha(isInputMode && isEditingMax ? theme.clientColor() : Theme.getInstance().clientColor(), .1f * alphaV), Round.of(3 * sScale));

                if (isInputMode && !isEditingMax) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, minBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, minValueText, minBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }

                if (isInputMode && isEditingMax) {
                    String renderString = inputValueString;
                    if (System.currentTimeMillis() % 1000 < 500) {
                        renderString += "_";
                    }
                    Fonts.SF_BOLD.draw(stack, renderString, maxBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                } else {
                    Fonts.SF_BOLD.draw(stack, maxValueText, maxBoxX + 2 * sScale, boxY + 2 * sScale, ColorUtil.getColor(255, 255, 255, alphaV), 7 * sScale);
                }
            }
        } else if (setting instanceof ModeSetting s) {
            selectedV = (selected) ? 18 * sScale + s.values.size() * 10 * sScale : 12 * sScale;
            height = Mathf.fast(height, selectedV, 12 * sScale);
            animV = Mathf.fast(animV, selectedV / (18 * sScale + s.values.size() * 10 * sScale), 15);

            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;
            float availableNameWidth = width - modeWidth - 4 * sScale;
            float nameY = y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderUtil.Rounded.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12 * sScale, sScale, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, 60), ColorUtil.multAlpha(theme.clientColor(), .1f), animV), alphaV), ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, 60), ColorUtil.multAlpha(theme.clientColor(), .2f), animV), alphaV), Round.of(3 * sScale));
            String prefix = FlatGui.getInstance().settingsDisplay().is("Отдельно") ? "Выбрано: " : "";
            Fonts.SF_BOLD.draw(stack, prefix, modeX + 2 * sScale, y + deltaV + 2 * sScale, ColorUtil.getColor(160, 160, 160, alphaV), 7 * sScale);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2 * sScale + Fonts.SF_BOLD.getWidth(prefix, 7 * sScale), y + deltaV, modeWidth - (2 * sScale + Fonts.SF_BOLD.getWidth(prefix, 7 * sScale)), 12 * sScale);
            Fonts.SF_BOLD.draw(stack, s.getValue(), modeX + 2 * sScale + Fonts.SF_BOLD.getWidth(prefix, 7 * sScale), y + deltaV + 2 * sScale, ColorUtil.getColor(160, 160, 160, alphaV), 7 * sScale);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f * sScale) {
                float offsetY = y + 16 * sScale + deltaV;
                RenderUtil.Rounded.roundedWithOutline(stack, modeX, offsetY - 1 * sScale, modeWidth, height - 16 * sScale, 1.5f * sScale, ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV), ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14 * sScale);
                for (String string : s.values) {
                    if (s.is(string))
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f * sScale, offsetY + 2.5f * sScale, .5f * sScale, 4 * sScale, ColorUtil.multAlpha(theme.textColor(), animV * alphaV), Round.of(1 * sScale));
                    Fonts.SF_BOLD.draw(stack, string, modeX + 2 * sScale + (s.is(string) ? 3 * sScale : 0), offsetY, s.is(string) ? ColorUtil.multAlpha(theme.textColor(), animV * alphaV) : ColorUtil.getColor(160, 160, 160, animV * alphaV), 7 * sScale);
                    offsetY += 10 * sScale * (height / (18 * sScale + s.values.size() * 10 * sScale));
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof MultiBooleanSetting s) {
            String values = s.getValue().values().stream().filter(BooleanSetting::getValue).map(BooleanSetting::getName).collect(Collectors.joining(", "));
            selectedV = (selected) ? 18 * sScale + s.getValues().size() * 10 * sScale : 12 * sScale;
            height = Mathf.fast(height, selectedV, 12 * sScale);
            animV = Mathf.fast(animV, selectedV / (18 * sScale + s.getValues().size() * 10 * sScale), 15);
            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;

            float availableNameWidth = width - modeWidth - 4 * sScale;
            float nameY = y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            if (Fonts.SF_BOLD.getWidth(values, 7 * sScale) >= modeWidth - 2 * sScale - Fonts.SF_BOLD.getWidth("Выбрано: ", 7 * sScale)) {
                if (System.currentTimeMillis() - time >= 25L && offsetX != 0) {
                    time = System.currentTimeMillis();
                    offsetX = offsetX <= -Fonts.SF_BOLD.getWidth(values, 7 * sScale) ? (int) (modeWidth - 2 * sScale - Fonts.SF_BOLD.getWidth("Выбрано: ", 7 * sScale)) : offsetX - 1;
                } else if (offsetX == 0 && System.currentTimeMillis() - time >= 1000L) {
                    offsetX -= 1;
                    time = System.currentTimeMillis();
                }
            } else if (offsetX != 0 && System.currentTimeMillis() - time >= 25L) {
                offsetX = (offsetX > 0) ? offsetX - 1 : offsetX + 1;
                time = System.currentTimeMillis();
            }

            RenderUtil.Rounded.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12 * sScale, sScale, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, 60), ColorUtil.multAlpha(theme.clientColor(), .1f), animV), alphaV), ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, 60), ColorUtil.multAlpha(theme.clientColor(), .2f), animV), alphaV), Round.of(3 * sScale));
            String prefix = FlatGui.getInstance().settingsDisplay().is("Отдельно") ? "Выбрано: " : "";
            Fonts.SF_BOLD.draw(stack, prefix, modeX + 2 * sScale, y + deltaV + 2 * sScale, ColorUtil.getColor(160, 160, 160, alphaV), 7 * sScale);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2 * sScale + Fonts.SF_BOLD.getWidth(prefix, 7 * sScale), y + deltaV, modeWidth - 2 * sScale - Fonts.SF_BOLD.getWidth(prefix, 7 * sScale), 12 * sScale);
            Fonts.SF_BOLD.draw(stack, values, modeX + 2 * sScale + Fonts.SF_BOLD.getWidth(prefix, 7 * sScale) + offsetX, y + deltaV + 2 * sScale, ColorUtil.getColor(160, 160, 160, alphaV), 7 * sScale);

            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f * sScale) {
                float offsetY = y + 16 * sScale + deltaV;
                RenderUtil.Rounded.roundedWithOutline(stack, modeX, offsetY - 1 * sScale, modeWidth, height - 16 * sScale, 1.5f * sScale, ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV), ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14 * sScale);
                int id = 0;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    interpolate.set(id, Mathf.fast(interpolate.get(id), booleanSetting.getValue() ? 1 : 0, 16));
                    if (interpolate.get(id) >= .1f)
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f * sScale, offsetY + 2.5f * sScale, .5f * sScale, 4 * sScale, ColorUtil.multAlpha(theme.textColor(), animV * interpolate.get(id) * alphaV), Round.of(1 * sScale));
                    Fonts.SF_BOLD.draw(stack, booleanSetting.getName(), modeX + 2 * sScale + interpolate.get(id) * 3 * sScale, offsetY, ColorUtil.overCol(ColorUtil.getColor(160, 160, 160, animV * alphaV), ColorUtil.multAlpha(theme.textColor(), animV), interpolate.get(id) * alphaV), 7 * sScale);
                    offsetY += 10 * sScale * (height / (18 * sScale + s.getValues().size() * 10 * sScale));
                    id++;
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof ColorSetting s) {
            if (FlatGui.getInstance().settingsDisplay().is("Под модулем")) {
                float targetHeight = selected ? 12 * sScale + 100 * sScale : 12 * sScale;
                height = Mathf.fast(height, targetHeight, 12 * sScale);
                animV = Mathf.fast(animV, selected ? 1 : 0, 15);

                float[] c = ColorUtil.getRGBAf(s.getValue());
                float controlSize = 12 * sScale;
                float controlX = x + width - controlSize - 3 * sScale;
                float controlY = y + deltaV + (12 * sScale - controlSize) / 2f;

                float availableNameWidth = width - controlSize - 10 * sScale;
                float nameY = y + deltaV + 12 * sScale / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
                drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                RenderUtil.Shadow.drawShadow(stack, controlX, controlY, controlSize, controlSize, 3 * sScale, 0.6f, ColorUtil.getColor(0, 0, 0, 120));
                RenderUtil.Rounded.roundedRect(stack, controlX, controlY, controlSize, controlSize, ColorUtil.multAlpha(s.getValue(), c[3] * alphaV), Round.of(controlSize / 2f));
                RenderUtil.Rounded.roundedOutline(stack, controlX, controlY, controlSize, controlSize, 1.5f * sScale, ColorUtil.multAlpha(ColorUtil.multBright(s.getValue(), 0.8f), (c[3] + .2f) * alphaV), Round.of(controlSize / 2f));

                if (selected || animV > 0.01f) {
                    if (colorPicker == null) {
                        colorPicker = new ColorPicker(x, y + deltaV + 14 * sScale, width, 95 * sScale, s, this);
                    }
                    colorPicker.setX(x);
                    colorPicker.setY(y + deltaV + 14 * sScale);
                    colorPicker.setWidth(width);
                    colorPicker.setHeight(95 * sScale);

                    if (animV > 0.01f) {
                        stack.push();
                        stack.translate(x + width / 2f, y + deltaV + 14 * sScale + 47.5f * sScale, 0);
                        stack.scale(1, animV, 1);
                        stack.translate(-(x + width / 2f), -(y + deltaV + 14 * sScale + 47.5f * sScale), 0);
                        colorPicker.render(stack, (int) mouseX, (int) mouseY);
                        stack.pop();
                    }
                } else {
                    colorPicker = null;
                }
            } else {
                height = 12 * sScale;
                float[] c = ColorUtil.getRGBAf(s.getValue());
                float controlSize = 12 * sScale;
                float controlX = x + width - controlSize - 3 * sScale;
                float controlY = y + deltaV + (12 * sScale - controlSize) / 2f;

                float availableNameWidth = width - controlSize - 10 * sScale;
                float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
                drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

                RenderUtil.Shadow.drawShadow(stack, controlX, controlY, controlSize, controlSize, 3 * sScale, 0.6f, ColorUtil.getColor(0, 0, 0, 120));
                RenderUtil.Rounded.roundedRect(stack, controlX, controlY, controlSize, controlSize, ColorUtil.multAlpha(s.getValue(), c[3] * alphaV), Round.of(controlSize / 2f));
                RenderUtil.Rounded.roundedOutline(stack, controlX, controlY, controlSize, controlSize, 1.5f * sScale, ColorUtil.multAlpha(ColorUtil.multBright(s.getValue(), 0.8f), (c[3] + .2f) * alphaV), Round.of(controlSize / 2f));
            }
        } else if (setting instanceof BindSetting s) {
            height = 12 * sScale;

            String text;
            if (selected) {
                text = "...";
            } else {
                int currentBind = s.getValue();
                if (currentBind >= 500) {
                    text = "M" + (currentBind - 500 + 1);
                } else {
                    text = (currentBind == -1 ? "нема" : Keyboard.keyName(currentBind));
                }
            }

            float bindWidth = Fonts.SF_BOLD.getWidth(text, 7 * sScale) + 4 * sScale;
            float availableNameWidth = width - bindWidth - 4 * sScale;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - bindWidth, y + deltaV + 1 * sScale, bindWidth, height - 2 * sScale, sScale, ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV), ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));
            Fonts.SF_BOLD.draw(stack, text, x + width - bindWidth + 2 * sScale, y + deltaV + 2 * sScale, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);
        } else if (setting instanceof StringSetting s) {
            height = 12 * sScale;
            float stringWidth = width * 0.55f;
            animV = Mathf.fast(animV, selected ? 1 : 0, 16 * sScale);

            float availableNameWidth = width - stringWidth - 4 * sScale;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - stringWidth, y + deltaV + 1 * sScale, stringWidth, height - 2 * sScale, sScale, ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, 60), ColorUtil.multAlpha(theme.clientColor(), .1f), animV), alphaV), ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, 60), ColorUtil.multAlpha(theme.clientColor(), .2f), animV), alphaV), Round.of(3 * sScale));
            Fonts.SF_BOLD.draw(stack, (selected || !s.getValue().isEmpty()) ? s.getValue() : s.getDescription(), x + width - stringWidth + 2 * sScale, y + deltaV + 2 * sScale, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);
        } else if (setting instanceof SwingSetting s) {
            selectedV = (selected) ? 220 * sScale : 12 * sScale;
            height = Mathf.fast(height, selectedV, 12 * sScale);
            animV = Mathf.fast(animV, selectedV / 220f * sScale, 15);

            Fonts.SF_BOLD.draw(stack, s.getName(), x, y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            if (height >= 12.1f * sScale) {
                float panelWidth = width;
                float panelHeight = height - 16 * sScale;
                float panelX = x;
                float panelY = y + deltaV + 16 * sScale;

                RenderUtil.Rounded.roundedWithOutline(stack, panelX, panelY - 1 * sScale, panelWidth, panelHeight, 1.5f * sScale,
                        ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV),
                        ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(panelX, panelY, panelWidth, panelHeight - 2 * sScale);

                float tabY = panelY + 5 * sScale;
                float tabHeight = 15 * sScale;
                float tabWidth = panelWidth / 2 - 5 * sScale;

                int leftTabColor = ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 60, 100), animV * alphaV);
                int rightTabColor = ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 60, 100), animV * alphaV);

                RenderUtil.Rounded.roundedRect(stack, panelX + 5 * sScale, tabY, tabWidth, tabHeight, leftTabColor, Round.of(3 * sScale));
                RenderUtil.Rounded.roundedRect(stack, panelX + tabWidth + 10 * sScale, tabY, tabWidth, tabHeight, rightTabColor, Round.of(3 * sScale));

                Fonts.SF_BOLD.draw(stack, "Левая рука", panelX + 5 * sScale + tabWidth / 2 - Fonts.SF_BOLD.getWidth("Левая рука", 6 * sScale) / 2,
                        tabY + 5 * sScale, ColorUtil.multAlpha(theme.textColor(), animV * alphaV), 6 * sScale);
                Fonts.SF_BOLD.draw(stack, "Правая рука", panelX + tabWidth + 10 * sScale + tabWidth / 2 - Fonts.SF_BOLD.getWidth("Правая рука", 6 * sScale) / 2,
                        tabY + 5 * sScale, ColorUtil.multAlpha(theme.textColor(), animV * alphaV), 6 * sScale);

                float settingY = panelY + 25 * sScale;
                float settingSpacing = 12 * sScale;

                String[] labels = {"Опора X", "Опора Y", "Опора Z", "Отклонение X", "Отклонение Y", "Отклонение Z",
                        "Поворот X", "Поворот Y", "Поворот Z"};

                SwingSetting.SwingData data = s.getValue();
                float[] leftValues = {data.getLeftArmSupportX(), data.getLeftArmSupportY(), data.getLeftArmSupportZ(),
                        data.getLeftArmDeviationX(), data.getLeftArmDeviationY(), data.getLeftArmDeviationZ(),
                        data.getLeftArmRotationX(), data.getLeftArmRotationY(), data.getLeftArmRotationZ()};
                float[] rightValues = {data.getRightArmSupportX(), data.getRightArmSupportY(), data.getRightArmSupportZ(),
                        data.getRightArmDeviationX(), data.getRightArmDeviationY(), data.getRightArmDeviationZ(),
                        data.getRightArmRotationX(), data.getRightArmRotationY(), data.getRightArmRotationZ()};

                for (int i = 0; i < labels.length; i++) {
                    if (settingY + settingSpacing > panelY + panelHeight - 10 * sScale) break;

                    float leftSliderX = panelX + 5 * sScale;
                    float rightSliderX = panelX + tabWidth + 10 * sScale;
                    float sliderWidth = tabWidth - 20 * sScale;
                    float sliderY = settingY + 3 * sScale;

                    Fonts.SF_BOLD.draw(stack, labels[i], leftSliderX, settingY,
                            ColorUtil.multAlpha(theme.disabledColor(), animV * alphaV * 0.8f), 5 * sScale);
                    Fonts.SF_BOLD.draw(stack, labels[i], rightSliderX, settingY,
                            ColorUtil.multAlpha(theme.disabledColor(), animV * alphaV * 0.8f), 5 * sScale);

                    RenderUtil.Rounded.roundedRect(stack, leftSliderX + sliderWidth - 35 * sScale, sliderY, 35 * sScale, 4 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(2 * sScale));
                    RenderUtil.Rounded.roundedRect(stack, rightSliderX + sliderWidth - 35 * sScale, sliderY, 35 * sScale, 4 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(2 * sScale));

                    float leftProgress = (leftValues[i] + 10f) / 20f;
                    float rightProgress = (rightValues[i] + 10f) / 20f;
                    float leftKnobX = leftSliderX + sliderWidth - 35 * sScale + (31 * sScale * leftProgress);
                    float rightKnobX = rightSliderX + sliderWidth - 35 * sScale + (31 * sScale * rightProgress);

                    RenderUtil.Rounded.roundedRect(stack, leftKnobX, sliderY - 1 * sScale, 3 * sScale, 6 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), .6f * alphaV), Round.of(1 * sScale));
                    RenderUtil.Rounded.roundedRect(stack, rightKnobX, sliderY - 1 * sScale, 3 * sScale, 6 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), .6f * alphaV), Round.of(1 * sScale));

                    String leftValueText = String.format("%.1f", leftValues[i]);
                    String rightValueText = String.format("%.1f", rightValues[i]);
                    Fonts.SF_BOLD.draw(stack, leftValueText, leftSliderX + sliderWidth - 50 * sScale, settingY,
                            ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 180), animV * alphaV), 4 * sScale);
                    Fonts.SF_BOLD.draw(stack, rightValueText, rightSliderX + sliderWidth - 50 * sScale, settingY,
                            ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 180), animV * alphaV), 4 * sScale);

                    settingY += settingSpacing;
                }

                float generalY = settingY + 5 * sScale;
                if (generalY < panelY + panelHeight - 15 * sScale) {
                    Fonts.SF_BOLD.draw(stack, "Общие", panelX + panelWidth / 2 - Fonts.SF_BOLD.getWidth("Общие", 6 * sScale) / 2,
                            generalY, ColorUtil.multAlpha(theme.textColor(), animV * alphaV), 6 * sScale);

                    generalY += 15 * sScale;

                    float generalSliderX = panelX + 10 * sScale;
                    float generalSliderWidth = panelWidth - 50 * sScale;

                    Fonts.SF_BOLD.draw(stack, "Скорость взмаха", generalSliderX, generalY,
                            ColorUtil.multAlpha(theme.disabledColor(), animV * alphaV * 0.8f), 5 * sScale);

                    RenderUtil.Rounded.roundedRect(stack, generalSliderX + generalSliderWidth - 35 * sScale, generalY + 3 * sScale, 35 * sScale, 4 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(2 * sScale));

                    float speedProgress = Math.min(1f, data.getSwingSpeed() / 5f);
                    float generalKnobX = generalSliderX + generalSliderWidth - 35 * sScale + (31 * sScale * speedProgress);
                    RenderUtil.Rounded.roundedRect(stack, generalKnobX, generalY + 2 * sScale, 3 * sScale, 6 * sScale,
                            ColorUtil.multAlpha(theme.clientColor(), .6f * alphaV), Round.of(1 * sScale));

                    String speedText = String.format("%.1f", data.getSwingSpeed());
                    Fonts.SF_BOLD.draw(stack, speedText, generalSliderX + generalSliderWidth - 50 * sScale, generalY,
                            ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 180), animV * alphaV), 4 * sScale);

                    generalY += 12 * sScale;

                    float toggleY = generalY + 5 * sScale;
                    if (toggleY < panelY + panelHeight - 10 * sScale) {
                        Fonts.SF_BOLD.draw(stack, "Возвращать", panelX + 10 * sScale, toggleY,
                                ColorUtil.multAlpha(theme.disabledColor(), animV * alphaV), 5 * sScale);

                        float toggleWidth = 20 * sScale;
                        float toggleHeight = 8 * sScale;
                        float toggleX = panelX + panelWidth - toggleWidth - 10 * sScale;

                        int toggleBgColor = data.isReturnToOriginal() ?
                                ColorUtil.multAlpha(theme.clientColor(), .6f * alphaV) :
                                ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 60), .6f * alphaV);

                        RenderUtil.Rounded.roundedRect(stack, toggleX, toggleY - 1 * sScale, toggleWidth, toggleHeight,
                                toggleBgColor, Round.of(toggleHeight / 2f));

                        float knobSize = toggleHeight - 2 * sScale;
                        float knobX = data.isReturnToOriginal() ?
                                toggleX + toggleWidth - knobSize - 1 * sScale : toggleX + 1 * sScale;
                        RenderUtil.Rounded.roundedRect(stack, knobX, toggleY, knobSize, knobSize,
                                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alphaV), Round.of(knobSize / 2f));
                    }
                }

                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof ThemeSetting s) {
            int presetCount = s.getPresets().size();
            int cols = 5;
            int rows = (int) Math.ceil((float) presetCount / cols);

            selectedV = selected ? (18 * sScale + rows * 24 * sScale) : 12 * sScale;
            height = Mathf.fast(height, selectedV, 12 * sScale);
            animV = Mathf.fast(animV, selected ? 1 : 0, 15);

            float buttonWidth = 50 * sScale;
            float availableNameWidth = width - buttonWidth - 8 * sScale;
            float nameY = y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            float buttonX = x + width - buttonWidth - 2 * sScale;
            float buttonY = y + deltaV + 1 * sScale;
            float buttonHeight = 10 * sScale;
            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, sScale,
                    ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3 * sScale));

            String buttonText = selected ? "Закрыть" : "Открыть";
            Fonts.SF_BOLD.draw(stack, buttonText, buttonX + buttonWidth / 2f - Fonts.SF_BOLD.getWidth(buttonText, 6 * sScale) / 2f,
                    buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(buttonText, 6 * sScale) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 6 * sScale);

            if (height >= 12.1f * sScale) {
                float panelY = y + deltaV + 16 * sScale;
                float panelHeight = height - 16 * sScale;

                RenderUtil.Rounded.roundedWithOutline(stack, x, panelY - 1 * sScale, width, panelHeight, 1.5f * sScale,
                        ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV),
                        ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV),
                        Round.of(3 * sScale));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, panelY, width, panelHeight - 2 * sScale);

                float boxSize = 22 * sScale;
                float spacing = 4 * sScale;
                float startX = x + 4 * sScale;
                float currentX = startX;
                float currentY = panelY + 2 * sScale;

                int currentCol = 0;
                ThemeSetting.ThemePreset currentTheme = s.getValue();

                for (ThemeSetting.ThemePreset preset : s.getPresets()) {
                    boolean isSelected = preset == currentTheme;
                    boolean isHovered = isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize);
                    float alpha = animV * alphaV;

                    RenderUtil.Rounded.roundedRect(stack, currentX + 1 * sScale, currentY + 1 * sScale, boxSize, boxSize,
                            ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), alpha), Round.of(3 * sScale));

                    if (preset.isGradient()) {
                        int leftColor = ColorUtil.multAlpha(preset.getColor1(), alpha);
                        int rightColor = ColorUtil.multAlpha(preset.getColor2(), alpha);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3 * sScale,
                                leftColor, leftColor, rightColor, rightColor);
                    } else {
                        int baseColor = ColorUtil.multAlpha(preset.getColor1(), alpha);
                        int darkColor = ColorUtil.multDark(baseColor, 0.7f);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3 * sScale,
                                baseColor, darkColor, darkColor, baseColor);
                    }

                    if (isSelected) {
                        int outlineStart = ColorUtil.multAlpha(theme.clientColor(), alpha);
                        int outlineEnd = ColorUtil.multDark(outlineStart, 0.5f);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX - 1.5f * sScale, currentY - 1.5f * sScale,
                                boxSize + 3f * sScale, boxSize + 3f * sScale, 4 * sScale,
                                outlineStart, outlineEnd, outlineEnd, outlineStart);
                    }

                    if (isHovered && !isSelected) {
                        int hoverColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 80), alpha);
                        RenderUtil.Rounded.roundedOutline(stack, currentX, currentY, boxSize, boxSize, 1.5f * sScale, hoverColor, Round.of(3 * sScale));
                    }

                    currentCol++;
                    if (currentCol >= 5) {
                        currentCol = 0;
                        currentX = startX;
                        currentY += boxSize + spacing;
                    } else {
                        currentX += boxSize + spacing;
                    }
                }

                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof ItemSetting s) {
            int itemsPerRow = Math.max(1, (int) ((width - 8 * sScale) / 18 * sScale));
            Item currentItem = s.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;

            float closedHeight = 12 * sScale + (previewRows > 0 ? (previewRows * 18 * sScale) : 0);
            float targetHeight = selected ? closedHeight + 150 * sScale : closedHeight;
            height = Mathf.fast(height, targetHeight, 12 * sScale);
            animV = Mathf.fast(animV, selected ? 1 : 0, 12);

            float buttonWidth = 16 * sScale;
            float availableNameWidth = width - buttonWidth - 8 * sScale;
            drawScrollingName(stack, s.getName(), x, y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            float buttonX = x + width - buttonWidth - 2 * sScale;
            float buttonY = y + deltaV + 1 * sScale;
            float buttonHeight = 10 * sScale;
            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, sScale,
                    ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3 * sScale));

            Fonts.icons.draw(stack, "D", buttonX + buttonWidth / 2f - Fonts.icons.getWidth("D", 7 * sScale) / 2f,
                    buttonY + buttonHeight / 2f - Fonts.icons.getHeight("D", 7 * sScale) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 7 * sScale);

            if (currentItem != null && currentItem != Items.AIR) {
                float itemsPreviewY = y + deltaV + 14 * sScale;
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, closedHeight - 12 * sScale);

                int itemX = (int) (x + 4 * sScale);
                int itemY = (int) itemsPreviewY;
                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(currentItem), itemX, itemY);

                ScissorExp.unset();
                ScissorExp.pop();
            }

            if (selected) {
                renderItemSelectorPanel(stack, mouseX, mouseY, s, null);
            }
        } else if (setting instanceof MultiItemSetting s) {
            int multiItemsPerRow = Math.max(1, (int) ((width - 8 * sScale) / 18 * sScale));
            int selectedItemCount = s.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / multiItemsPerRow) : 0;

            float multiClosedHeight = 12 * sScale + (requiredRows > 0 ? (requiredRows * 18 * sScale) : 0);
            float multiTargetHeight = selected ? multiClosedHeight + 150 * sScale : multiClosedHeight;
            height = Mathf.fast(height, multiTargetHeight, 12 * sScale);
            animV = Mathf.fast(animV, selected ? 1 : 0, 12);

            float multiButtonWidth = 16 * sScale;
            float multiAvailableNameWidth = width - multiButtonWidth - 8 * sScale;
            drawScrollingName(stack, s.getName(), x, y + deltaV + 6 * sScale - Fonts.SF_BOLD.getHeight(s.getName(), 7 * sScale) / 1.5f, multiAvailableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            float multiButtonX = x + width - multiButtonWidth - 2 * sScale;
            float multiButtonY = y + deltaV + 1 * sScale;
            float multiButtonHeight = 10 * sScale;
            boolean multiButtonHovered = isHovered(mouseX, mouseY, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight, sScale,
                    ColorUtil.multAlpha(multiButtonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(multiButtonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3 * sScale));

            Fonts.icons.draw(stack, "D", multiButtonX + multiButtonWidth / 2f - Fonts.icons.getWidth("D", 7 * sScale) / 2f,
                    multiButtonY + multiButtonHeight / 2f - Fonts.icons.getHeight("D", 7 * sScale) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 7 * sScale);

            if (selectedItemCount > 0) {
                float itemsPreviewY = y + deltaV + 14 * sScale;
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, multiClosedHeight - 12 * sScale);

                int currentX = (int) (x + 4 * sScale);
                int currentY = (int) itemsPreviewY;
                for (Item item : s.getValue()) {
                    mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), currentX, currentY);
                    currentX += 18 * sScale;
                    if (currentX + 18 * sScale > x + width - 4 * sScale) {
                        currentX = (int) (x + 4 * sScale);
                        currentY += 18 * sScale;
                    }
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

            if (selected) {
                renderItemSelectorPanel(stack, mouseX, mouseY, null, s);
            }
        }

        if (hoverAnim > 0.01f) {
            String description = setting.getDescription();
            if (description != null && !description.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (typingProgress < description.length() && currentTime - lastTypeTime > 25) {
                    typingProgress++;
                    lastTypeTime = currentTime;
                }
                String visibleText = description.substring(0, Math.min(typingProgress, description.length()));

                float textWidth = Fonts.SF_BOLD.getWidth(description, 7 * sScale);
                float tooltipWidth = textWidth + 8 * sScale;
                float tooltipHeight = Fonts.SF_BOLD.getHeight(description, 7 * sScale) + 8 * sScale;

                float tooltipX = x + width + 5 * sScale;
                float tooltipY = y + deltaV + (12 * sScale / 2f) - (tooltipHeight / 2f);

                stack.push();
                stack.translate(tooltipX + tooltipWidth / 2f, tooltipY + tooltipHeight / 2f, 0);
                stack.scale(hoverAnim, hoverAnim, 1);
                stack.translate(-(tooltipX + tooltipWidth / 2f), -(tooltipY + tooltipHeight / 2f), 0);

                int backgroundColor = ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 30), hoverAnim * alphaV);
                int textColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 220), hoverAnim * alphaV);

                RenderUtil.Rounded.roundedRect(stack, tooltipX, tooltipY, tooltipWidth, tooltipHeight, backgroundColor, Round.of(4 * sScale));
                Fonts.SF_BOLD.draw(stack, visibleText, tooltipX + 4 * sScale, tooltipY + 4 * sScale, textColor, 7 * sScale);

                stack.pop();
            }
        }
    }

    private void renderItemSelectorPanel(MatrixStack stack, float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float sScale = getResolutionScale();
        float panelBaseY = y + deltaV + 14 * sScale;
        float selectorHeight = 150 * sScale;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12 * sScale + (previewRows > 0 ? (previewRows * 18 * sScale) : 0);
            panelBaseY = y + deltaV + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8 * sScale) / 18 * sScale));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12 * sScale + (requiredRows > 0 ? (requiredRows * 18 * sScale) : 0);
            panelBaseY = y + deltaV + closedHeight;
        }

        float panelHeight = selectorHeight - 4 * sScale;
        RenderUtil.Rounded.roundedWithOutline(stack, x, panelBaseY, width, panelHeight, 1.5f * sScale, ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 30), alphaV * animV), ColorUtil.multAlpha(ColorUtil.getColor(80, 80, 80), alphaV * animV), Round.of(4 * sScale));

        float searchBarY = panelBaseY + 4 * sScale;
        float searchBarHeight = 12 * sScale;
        RenderUtil.Rounded.roundedRect(stack, x + 4 * sScale, searchBarY, width - 8 * sScale, searchBarHeight, ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), alphaV * animV), Round.of(3 * sScale));
        String searchHint = "Поиск...";
        String textToRender = inputValueString;
        if (isInputMode && System.currentTimeMillis() % 1000 < 500) textToRender += "_";

        Fonts.SF_BOLD.draw(stack, textToRender.isEmpty() && !isInputMode ? searchHint : textToRender, x + 8 * sScale, searchBarY + 2.5f * sScale, textToRender.isEmpty() && !isInputMode ? ColorUtil.getColor(120, 120, 120) : -1, 7 * sScale);

        float itemsListStartY = searchBarY + searchBarHeight + 4 * sScale;
        float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 20 * sScale : 4 * sScale);

        int itemsPerRow = Math.max(1, (int) ((width - 16 * sScale) / 20 * sScale));
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int maxScroll = (int) Math.max(0, totalRows * 20 * sScale - (int) itemsListHeight);
        itemScrollY = MathHelper.clamp(itemScrollY, -maxScroll, 0);
        itemScrollYV = Mathf.fast(itemScrollYV, itemScrollY, 12);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 2 * sScale, itemsListStartY, width - 4 * sScale, itemsListHeight);

        List<Item> sortedList = new ArrayList<>();
        if (multiSetting != null) {
            List<Item> selectedFiltered = new ArrayList<>();
            List<Item> unselectedFiltered = new ArrayList<>();
            for (Item item : filteredItems) {
                if (multiSetting.contains(item)) selectedFiltered.add(item);
                else unselectedFiltered.add(item);
            }
            sortedList.addAll(selectedFiltered);
            sortedList.addAll(unselectedFiltered);
        } else {
            sortedList.addAll(filteredItems);
        }

        int itemX = (int) (x + 8 * sScale);
        int itemY = (int) (itemsListStartY + itemScrollYV);

        for (Item item : sortedList) {
            boolean isItemSelected = (multiSetting != null && multiSetting.contains(item)) || (singleSetting != null && singleSetting.getValue() == item);

            if (itemY + 16 * sScale >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                if (isItemSelected) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1 * sScale, itemY - 1 * sScale, 18 * sScale, 18 * sScale, ColorUtil.multAlpha(Theme.getInstance().clientColor(), 0.5f), Round.of(3 * sScale));
                }
                if (isHovered(mouseX, mouseY, itemX, itemY, 16 * sScale, 16 * sScale)) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1 * sScale, itemY - 1 * sScale, 18 * sScale, 18 * sScale, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), 0.2f), Round.of(3 * sScale));
                }
                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), itemX, itemY);
            }

            itemX += 20 * sScale;
            if (itemX + 20 * sScale > x + width - 8 * sScale) {
                itemX = (int) (x + 8 * sScale);
                itemY += 20 * sScale;
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (multiSetting != null) {
            float buttonWidth = 50 * sScale;
            float buttonHeight = 14 * sScale;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + panelHeight - buttonHeight - 3 * sScale;

            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
            int buttonColor = buttonHovered ? ColorUtil.multBright(Theme.getInstance().clientColor(), 0.8f) : Theme.getInstance().clientColor();

            RenderUtil.Rounded.roundedRect(stack, buttonX, buttonY, buttonWidth, buttonHeight, ColorUtil.multAlpha(buttonColor, alphaV * animV), Round.of(4 * sScale));
            Fonts.SF_BOLD.draw(stack, "Сохранить", buttonX + buttonWidth / 2 - Fonts.SF_BOLD.getWidth("Сохранить", 7 * sScale) / 2, buttonY + buttonHeight / 2 - Fonts.SF_BOLD.getHeight("Сохранить", 7 * sScale) / 2, -1, 7 * sScale);
        }
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = getResolutionScale();
        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();

        float effectiveX = x;
        // Always use deltaV for click detection to match rendering position
        float effectiveY = y + deltaV;
        float effectiveWidth = width;
        float effectiveHeight = height;

        if (isInputMode && action == 0 && !(setting instanceof ItemSetting) && !(setting instanceof MultiItemSetting)) {
            if (setting instanceof SliderSetting) applyInputValue((SliderSetting) setting);
            else if (setting instanceof SliderSettingRand) applyInputValueRand((SliderSettingRand) setting);
            else isInputMode = false;
        }

        if (setting instanceof ColorSetting s && selected && colorPicker != null && action == 0) {
            boolean clickedOutside = true;
            if (displayMode.equals("Под модулем")) {
                if (isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, effectiveHeight)) {
                    clickedOutside = false;
                }
            } else {
                if (isHovered(mouseX, mouseY, colorPicker.getX(), colorPicker.getY(), colorPicker.getWidth(), colorPicker.getHeight()) ||
                        isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, 12 * sScale)) {
                    clickedOutside = false;
                }
            }

            if (clickedOutside) {
                selected = false;
                return;
            }
        }

        if (setting instanceof BindSetting bindSetting && selected && action == 0) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            bindSetting.set(button + 500);
            selected = false;
            SoundUtil.playSound("bind_succes.wav", 0.3f);
            lastBindClickTime = System.currentTimeMillis();
            return;
        }

        if (action == GLFW.GLFW_PRESS && button == 0) {
            if ((setting instanceof ModeSetting || setting instanceof MultiBooleanSetting || setting instanceof BindSetting || setting instanceof StringSetting || setting instanceof SwingSetting || setting instanceof ColorSetting || setting instanceof ItemSetting || setting instanceof MultiItemSetting || setting instanceof ThemeSetting) && isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, 12 * sScale)) {
                if (setting instanceof BindSetting) {
                    if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                        return;
                    }
                    SoundUtil.playSound("bind_wait.wav", 0.1f);
                    lastBindClickTime = System.currentTimeMillis();
                }

                if (setting instanceof ColorSetting && (displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") || displayMode.equals("Модулем") || displayMode.equals("Сверху GUI"))) {
                    selected = !selected;
                    if (colorPicker == null)
                        colorPicker = new ColorPicker(effectiveX + effectiveWidth + 10 * sScale, effectiveY, 90 * sScale, 90 * sScale, (ColorSetting) setting, this);
                    else
                        colorPicker.setScale(selected ? effectiveX + effectiveWidth + 10 * sScale : effectiveX + effectiveWidth - 90 * sScale);
                } else if (setting instanceof ThemeSetting s) {
                    float buttonWidth = 50 * sScale;
                    float buttonX = effectiveX + effectiveWidth - buttonWidth - 2 * sScale;
                    float buttonY = effectiveY + 1 * sScale;
                    float buttonHeight = 10 * sScale;

                    if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                        selected = !selected;
                    }
                } else if (setting instanceof ModeSetting) {
                    SoundUtil.playSound(selected ? "mode_close.wav" : "mode_open.wav", 0.1f);
                    selected = !selected;
                    if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
                        isInputMode = false;
                        inputValueString = "";
                        filteredItems.clear();
                        filteredItems.addAll(allItems);
                        itemScrollY = 0;
                    }
                } else {
                    selected = !selected;
                    if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
                        isInputMode = false;
                        inputValueString = "";
                        filteredItems.clear();
                        filteredItems.addAll(allItems);
                        itemScrollY = 0;
                    }
                }
                return;
            }
        } else if (action == 0 && setting instanceof ThemeSetting s && selected && isHovered(mouseX, mouseY, effectiveX, effectiveY + 16 * sScale, effectiveWidth, effectiveHeight - 16 * sScale)) {
            float panelY = effectiveY + 16 * sScale;
            float boxSize = 22 * sScale;
            float spacing = 4 * sScale;
            float startX = effectiveX + 4 * sScale;
            float currentX = startX;
            float currentY = panelY + 2 * sScale;

            int currentCol = 0;

            for (ThemeSetting.ThemePreset preset : s.getPresets()) {
                if (isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize)) {
                    s.set(preset);
                    selected = false;
                    SoundUtil.playSound("cb_enabled.wav", 0.1f);
                    break;
                }

                currentCol++;
                if (currentCol >= 5) {
                    currentCol = 0;
                    currentX = startX;
                    currentY += boxSize + spacing;
                } else {
                    currentX += boxSize + spacing;
                }
            }
        }

        if (button == 0) {
            float modeWidth = effectiveWidth * 0.55f;
            float modeX = effectiveX + effectiveWidth - modeWidth;

            if (action == 0 && setting instanceof BooleanSetting s && isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, effectiveHeight)) {
                s.set(!s.getValue());
                SoundUtil.playSound(s.getValue() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
            } else if (setting instanceof SliderSetting s) {
                if (displayMode.equals("Под модулем")) {
                    float sliderWidth = effectiveWidth - 4 * sScale;
                    float sliderX = effectiveX + 0.5f * sScale;
                    float sliderY = effectiveY + effectiveHeight - 5 * sScale;

                    String valueText = String.format("%.2f", s.getValue());
                    float valueTextWidth = Fonts.SF_BOLD.getWidth(valueText, 7 * sScale);
                    float valueBoxWidth = valueTextWidth + 4 * sScale;
                    float valueBoxX = effectiveX + effectiveWidth - valueBoxWidth - 2 * sScale;
                    float valueBoxY = effectiveY + 2 * sScale - 2 * sScale;
                    float valueBoxHeight = Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale;

                    if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                        isInputMode = true;
                        inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX - 2 * sScale, sliderY - 3 * sScale, sliderWidth + 4 * sScale, 10 * sScale)) {
                        selected = true;
                        SoundUtil.playSound("slider.wav", 0.1f);
                    }
                } else {
                    float sliderWidth = 138 * sScale;
                    float sliderX = effectiveX + effectiveWidth - sliderWidth;

                    String valueText = String.format("%.2f", s.getValue());
                    float valueTextWidth = Fonts.SF_BOLD.getWidth(valueText, 7 * sScale);
                    float valueBoxWidth = valueTextWidth + 4 * sScale;
                    float valueBoxX = sliderX - valueBoxWidth - 3 * sScale;
                    float valueBoxY = effectiveY + effectiveHeight / 2f - (Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale) / 2f;
                    float valueBoxHeight = Fonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale;

                    if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                        isInputMode = true;
                        inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX, effectiveY, sliderWidth, effectiveHeight)) {
                        selected = true;
                        SoundUtil.playSound("slider.wav", 0.1f);
                    }
                }
            } else if (setting instanceof SliderSettingRand s) {
                if (displayMode.equals("Под модулем")) {
                    String minValueText = String.format("%.2f", s.getValue().getMinValue());
                    String maxValueText = String.format("%.2f", s.getValue().getMaxValue());
                    float minValueWidth = Fonts.SF_BOLD.getWidth(minValueText, 7 * sScale);
                    float maxValueWidth = Fonts.SF_BOLD.getWidth(maxValueText, 7 * sScale);
                    float minBoxWidth = minValueWidth + 4 * sScale;
                    float maxBoxWidth = maxValueWidth + 4 * sScale;
                    float minBoxX = effectiveX + effectiveWidth - minBoxWidth - maxBoxWidth - 6 * sScale;
                    float maxBoxX = effectiveX + effectiveWidth - maxBoxWidth - 2 * sScale;
                    float boxY = effectiveY + 2 * sScale - 2 * sScale;
                    float boxHeight = Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale;

                    if (action == 0 && isHovered(mouseX, mouseY, minBoxX, boxY, minBoxWidth, boxHeight)) {
                        isInputMode = true;
                        isEditingMax = false;
                        inputValueString = String.format("%.2f", s.getValue().getMinValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    if (action == 0 && isHovered(mouseX, mouseY, maxBoxX, boxY, maxBoxWidth, boxHeight)) {
                        isInputMode = true;
                        isEditingMax = true;
                        inputValueString = String.format("%.2f", s.getValue().getMaxValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    float sliderWidth = effectiveWidth - 4 * sScale;
                    float sliderX = effectiveX + 0.5f * sScale;
                    float sliderY = effectiveY + effectiveHeight - 10 * sScale;

                    if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX - 2 * sScale, sliderY - 3 * sScale, sliderWidth + 4 * sScale, 10 * sScale)) {
                        float minProgress = (s.getValue().getMinValue() - s.min) / (s.max - s.min);
                        float maxProgress = (s.getValue().getMaxValue() - s.min) / (s.max - s.min);
                        float minKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * minProgress) + 1.75f * sScale;
                        float maxKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * maxProgress) + 1.75f * sScale;

                        float distToMin = Math.abs(mouseX - minKnobX);
                        float distToMax = Math.abs(mouseX - maxKnobX);

                        isEditingMax = distToMax < distToMin;
                        selected = true;
                        delta = mouseX;
                        SoundUtil.playSound("slider.wav", 0.1f);
                    }
                } else {
                    float sliderWidth = 138 * sScale;
                    float sliderX = effectiveX + effectiveWidth - sliderWidth;

                    String minValueText = String.format("%.2f", s.getValue().getMinValue());
                    String maxValueText = String.format("%.2f", s.getValue().getMaxValue());
                    float minValueWidth = Fonts.SF_BOLD.getWidth(minValueText, 7 * sScale);
                    float maxValueWidth = Fonts.SF_BOLD.getWidth(maxValueText, 7 * sScale);
                    float minBoxWidth = minValueWidth + 4 * sScale;
                    float maxBoxWidth = maxValueWidth + 4 * sScale;
                    float minBoxX = sliderX - minBoxWidth - maxBoxWidth - 6 * sScale;
                    float maxBoxX = sliderX - maxBoxWidth - 3 * sScale;
                    float boxY = effectiveY + effectiveHeight / 2f - (Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale) / 2f;
                    float boxHeight = Fonts.SF_BOLD.getHeight(minValueText, 7 * sScale) + 4 * sScale;

                    if (action == 0 && isHovered(mouseX, mouseY, minBoxX, boxY, minBoxWidth, boxHeight)) {
                        isInputMode = true;
                        isEditingMax = false;
                        inputValueString = String.format("%.2f", s.getValue().getMinValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    if (action == 0 && isHovered(mouseX, mouseY, maxBoxX, boxY, maxBoxWidth, boxHeight)) {
                        isInputMode = true;
                        isEditingMax = true;
                        inputValueString = String.format("%.2f", s.getValue().getMaxValue()).replace(',', '.');
                        selected = false;
                        return;
                    }

                    if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX, effectiveY, sliderWidth, effectiveHeight)) {
                        float minProgress = (s.getValue().getMinValue() - s.min) / (s.max - s.min);
                        float maxProgress = (s.getValue().getMaxValue() - s.min) / (s.max - s.min);
                        float minKnobX = sliderX + ((sliderWidth - 8 * sScale) * minProgress) + 2 * sScale;
                        float maxKnobX = sliderX + ((sliderWidth - 8 * sScale) * maxProgress) + 2 * sScale;

                        float distToMin = Math.abs(mouseX - minKnobX);
                        float distToMax = Math.abs(mouseX - maxKnobX);

                        isEditingMax = distToMax < distToMin;
                        selected = true;
                        delta = mouseX;
                        SoundUtil.playSound("slider.wav", 0.1f);
                    }
                }
            } else if (action == 0 && setting instanceof ModeSetting s && selected && isHovered(mouseX, mouseY, modeX, effectiveY + 16 * sScale, modeWidth, effectiveHeight - 16 * sScale)) {
                float offsetY = effectiveY + 16 * sScale;
                for (String string : s.values) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10 * sScale)) {
                        selected = false;
                        boolean wasSelected = s.is(string);
                        s.set(string);
                        if (!wasSelected) {
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        } else {
                            SoundUtil.playSound("mode_pick.wav", 0.1f);
                        }
                        break;
                    }
                    offsetY += 10 * sScale * (effectiveHeight / (16 * sScale + s.values.size() * 10 * sScale));
                }
            } else if (action == 0 && setting instanceof MultiBooleanSetting s && selected && isHovered(mouseX, mouseY, modeX, effectiveY + 16 * sScale, modeWidth, effectiveHeight - 16 * sScale)) {
                float offsetY = effectiveY + 16 * sScale;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10 * sScale)) {
                        boolean wasSelected = booleanSetting.getValue();
                        booleanSetting.set(!booleanSetting.getValue());
                        if (!wasSelected) {
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        } else {
                            SoundUtil.playSound("mode_pick.wav", 0.1f);
                        }
                        break;
                    }
                    offsetY += 10 * sScale * (effectiveHeight / (18 * sScale + s.getValues().size() * 10 * sScale));
                }
            } else if (setting instanceof ColorSetting s) {
                if (selected && colorPicker != null) {
                    boolean hoveringPicker;
                    if (displayMode.equals("Под модулем")) {
                        hoveringPicker = isHovered(mouseX, mouseY, effectiveX, effectiveY + 14 * sScale, effectiveWidth, 90 * sScale);
                    } else {
                        hoveringPicker = isHovered(mouseX, mouseY, colorPicker.getX(), colorPicker.getY(), colorPicker.getWidth(), colorPicker.getHeight());
                    }

                    if (hoveringPicker && action == GLFW.GLFW_PRESS) {
                        colorPicker.mouseClicked(mouseX, mouseY, button);
                    }
                }
            } else if (setting instanceof ItemSetting itemSetting) {
                if (action == GLFW.GLFW_PRESS && button == 0) {
                    float buttonWidth = 16 * sScale;
                    float buttonX = effectiveX + effectiveWidth - buttonWidth - 2 * sScale;
                    float buttonY = effectiveY + 1 * sScale;
                    float buttonHeight = 10 * sScale;

                    if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                        selected = !selected;
                        if (selected) {
                            isInputMode = false;
                            inputValueString = "";
                            filteredItems.clear();
                            filteredItems.addAll(allItems);
                            itemScrollY = 0;
                        }
                        return;
                    }
                }

                if (selected && action == 0) {
                    handleItemSelectorMouse(mouseX, mouseY, action, itemSetting, null);
                }
            } else if (setting instanceof MultiItemSetting multiItemSetting) {
                if (action == GLFW.GLFW_PRESS && button == 0) {
                    float buttonWidth = 16 * sScale;
                    float buttonX = effectiveX + effectiveWidth - buttonWidth - 2 * sScale;
                    float buttonY = effectiveY + 1 * sScale;
                    float buttonHeight = 10 * sScale;

                    if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                        selected = !selected;
                        if (selected) {
                            isInputMode = false;
                            inputValueString = "";
                            filteredItems.clear();
                            filteredItems.addAll(allItems);
                            itemScrollY = 0;
                        }
                        return;
                    }
                }

                if (selected && action == 0) {
                    handleItemSelectorMouse(mouseX, mouseY, action, null, multiItemSetting);
                }
            } else if (action == 0 && setting instanceof SwingSetting s && selected && isHovered(mouseX, mouseY, effectiveX, effectiveY + 16 * sScale, effectiveWidth, effectiveHeight - 16 * sScale)) {
                float panelX = effectiveX;
                float panelY = effectiveY + 16 * sScale;
                float tabWidth = effectiveWidth / 2 - 5 * sScale;
                SwingSetting.SwingData data = s.getValue();

                if (isHovered(mouseX, mouseY, panelX + 5 * sScale, panelY + 25 * sScale, effectiveWidth, effectiveHeight - 45 * sScale)) {
                    float settingY = panelY + 25 * sScale;
                    float settingSpacing = 12 * sScale;

                    for (int i = 0; i < 9; i++) {
                        if (settingY + settingSpacing > panelY + effectiveHeight - 20 * sScale) break;

                        float leftSliderX = panelX + 5 * sScale;
                        float rightSliderX = panelX + tabWidth + 10 * sScale;
                        float sliderWidth = tabWidth - 20 * sScale;
                        float sliderY = settingY + 3 * sScale;

                        if (isHovered(mouseX, mouseY, leftSliderX + sliderWidth - 35 * sScale, sliderY - 2 * sScale, 35 * sScale, 8 * sScale)) {
                            float progress = Math.max(0, Math.min(1, (mouseX - (leftSliderX + sliderWidth - 35 * sScale)) / 35f * sScale));
                            float value = (progress * 20f) - 10f;
                            switch (i) {
                                case 0:
                                    data.setLeftArmSupportX(value);
                                    break;
                                case 1:
                                    data.setLeftArmSupportY(value);
                                    break;
                                case 2:
                                    data.setLeftArmSupportZ(value);
                                    break;
                                case 3:
                                    data.setLeftArmDeviationX(value);
                                    break;
                                case 4:
                                    data.setLeftArmDeviationY(value);
                                    break;
                                case 5:
                                    data.setLeftArmDeviationZ(value);
                                    break;
                                case 6:
                                    data.setLeftArmRotationX(value);
                                    break;
                                case 7:
                                    data.setLeftArmRotationY(value);
                                    break;
                                case 8:
                                    data.setLeftArmRotationZ(value);
                                    break;
                            }
                            s.set(data);
                            return;
                        }

                        if (isHovered(mouseX, mouseY, rightSliderX + sliderWidth - 35 * sScale, sliderY - 2 * sScale, 35 * sScale, 8 * sScale)) {
                            float progress = Math.max(0, Math.min(1, (mouseX - (rightSliderX + sliderWidth - 35 * sScale)) / 35f * sScale));
                            float value = (progress * 20f) - 10f;
                            switch (i) {
                                case 0:
                                    data.setRightArmSupportX(value);
                                    break;
                                case 1:
                                    data.setRightArmSupportY(value);
                                    break;
                                case 2:
                                    data.setRightArmSupportZ(value);
                                    break;
                                case 3:
                                    data.setRightArmDeviationX(value);
                                    break;
                                case 4:
                                    data.setRightArmDeviationY(value);
                                    break;
                                case 5:
                                    data.setRightArmDeviationZ(value);
                                    break;
                                case 6:
                                    data.setRightArmRotationX(value);
                                    break;
                                case 7:
                                    data.setRightArmRotationY(value);
                                    break;
                                case 8:
                                    data.setRightArmRotationZ(value);
                                    break;
                            }
                            s.set(data);
                            return;
                        }

                        settingY += settingSpacing;
                    }

                    float generalY = settingY + 20 * sScale;
                    if (isHovered(mouseX, mouseY, panelX + 10 * sScale + effectiveWidth - 70 * sScale, generalY + 3 * sScale - 2 * sScale, 35 * sScale, 8 * sScale)) {
                        float progress = Math.max(0, Math.min(1, (mouseX - (panelX + 10 * sScale + effectiveWidth - 70 * sScale)) / 35f * sScale));
                        float value = progress * 5f;
                        data.setSwingSpeed(value);
                        s.set(data);
                        return;
                    }

                    float toggleY = generalY + 17 * sScale;
                    if (isHovered(mouseX, mouseY, panelX + effectiveWidth - 30 * sScale, toggleY - 1 * sScale, 20 * sScale, 8 * sScale)) {
                        data.setReturnToOriginal(!data.isReturnToOriginal());
                        s.set(data);
                    }
                }
            }
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (selected && (setting instanceof SliderSetting || setting instanceof SliderSettingRand)) {
                selected = false;
            }
            if (setting instanceof ColorSetting s && colorPicker != null) {
                if (displayMode.equals("Под модулем")) {
                    if (isHovered(mouseX, mouseY, effectiveX, effectiveY + 14 * sScale, effectiveWidth, 90 * sScale)) {
                        colorPicker.mouseReleased(mouseX, mouseY, button);
                    }
                } else {
                    colorPicker.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
    }

    private void handleItemSelectorMouse(float mouseX, float mouseY, int action, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float sScale = getResolutionScale();
        float panelBaseY = y + deltaV + 14 * sScale;
        float selectorHeight = 150 * sScale;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12 * sScale + (previewRows > 0 ? (previewRows * 18 * sScale) : 0);
            panelBaseY = y + deltaV + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8 * sScale) / 18 * sScale));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12 * sScale + (requiredRows > 0 ? (requiredRows * 18 * sScale) : 0);
            panelBaseY = y + deltaV + closedHeight;
        }

        float panelHeight = selectorHeight - 4 * sScale;
        float searchBarY = panelBaseY + 4 * sScale;
        float searchBarHeight = 12 * sScale;

        if (action == 0 && isHovered(mouseX, mouseY, x + 4 * sScale, searchBarY, width - 8 * sScale, searchBarHeight)) {
            isInputMode = true;
            return;
        }

        if (action == 0) {
            float itemsListStartY = searchBarY + searchBarHeight + 4 * sScale;
            float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 20 * sScale : 4 * sScale);

            if (isHovered(mouseX, mouseY, x + 2 * sScale, itemsListStartY, width - 4 * sScale, itemsListHeight)) {
                List<Item> sortedList = new ArrayList<>();
                if (multiSetting != null) {
                    List<Item> selectedFiltered = new ArrayList<>();
                    List<Item> unselectedFiltered = new ArrayList<>();
                    for (Item item : filteredItems) {
                        if (multiSetting.contains(item)) selectedFiltered.add(item);
                        else unselectedFiltered.add(item);
                    }
                    sortedList.addAll(selectedFiltered);
                    sortedList.addAll(unselectedFiltered);
                } else {
                    sortedList.addAll(filteredItems);
                }

                int itemsPerRow = Math.max(1, (int) ((width - 16 * sScale) / 20 * sScale));
                int itemX = (int) (x + 8 * sScale);
                int itemY = (int) (itemsListStartY + itemScrollYV);

                for (Item item : sortedList) {
                    if (itemY + 16 * sScale >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                        if (isHovered(mouseX, mouseY, itemX, itemY, 16 * sScale, 16 * sScale)) {
                            if (singleSetting != null) {
                                singleSetting.set(item);
                                selected = false;
                                SoundUtil.playSound("select.wav", 0.1f);
                            }
                            if (multiSetting != null) {
                                multiSetting.toggle(item);
                                SoundUtil.playSound("select.wav", 0.1f);
                            }
                            isInputMode = false;
                            inputValueString = "";
                            return;
                        }
                    }
                    itemX += 20 * sScale;
                    if (itemX + 20 * sScale > x + width - 8 * sScale) {
                        itemX = (int) (x + 8 * sScale);
                        itemY += 20 * sScale;
                    }
                }
                return;
            }

            if (multiSetting != null) {
                float buttonWidth = 50 * sScale;
                float buttonHeight = 14 * sScale;
                float buttonX = x + width / 2 - buttonWidth / 2;
                float buttonY = panelBaseY + panelHeight - buttonHeight - 3 * sScale;
                if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                    selected = false;
                    isInputMode = false;
                    inputValueString = "";
                }
            }
        }
    }

    @Override
    protected void key(int key) {
        float sScale = getResolutionScale();
        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                isInputMode = false;
                inputValueString = "";
                updateItemFilter();
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!inputValueString.isEmpty()) {
                    inputValueString = inputValueString.substring(0, inputValueString.length() - 1);
                    updateItemFilter();
                }
            }
            return;
        }

        if (isInputMode && setting instanceof SliderSetting s) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER) {
                applyInputValue(s);
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!inputValueString.isEmpty()) {
                    inputValueString = inputValueString.substring(0, inputValueString.length() - 1);
                }
            }
            return;
        }

        if (isInputMode && setting instanceof SliderSettingRand s) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER) {
                applyInputValueRand(s);
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!inputValueString.isEmpty()) {
                    inputValueString = inputValueString.substring(0, inputValueString.length() - 1);
                }
            }
            return;
        }

        if (setting instanceof BindSetting s && selected) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            selected = false;
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE) {
                s.set(-1);
                SoundUtil.playSound("bind_remove.wav", 0.1f);
            } else {
                s.set(key);
                SoundUtil.playSound("bind_succes.wav", 0.1f);
            }
            lastBindClickTime = System.currentTimeMillis();
        } else if (setting instanceof StringSetting s && selected) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE)
                selected = false;
            else if (key == GLFW.GLFW_KEY_BACKSPACE)
                s.set(s.getValue().isEmpty() ? "" : s.getValue().substring(0, s.getValue().length() - 1));
        }
    }

    @Override
    public void charTyped(char typed) {
        float sScale = getResolutionScale();
        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            inputValueString += typed;
            updateItemFilter();
            return;
        }

        if (isInputMode && setting instanceof SliderSetting) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains(".")) || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
            return;
        }

        if (isInputMode && setting instanceof SliderSettingRand) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains(".")) || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
            return;
        }

        if (selected && setting instanceof StringSetting s)
            s.set(s.getValue() + typed);
    }

    private void updateItemFilter() {
        float sScale = getResolutionScale();
        if (inputValueString.isEmpty()) {
            filteredItems.clear();
            filteredItems.addAll(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(i -> {
                        String translatedName = i.getTranslationKey();
                        String simpleName = translatedName.substring(translatedName.lastIndexOf('.') + 1);
                        return simpleName.replace('_', ' ').toLowerCase().contains(inputValueString.toLowerCase());
                    })
                    .collect(Collectors.toList());
        }
        itemScrollY = 0;
    }

    public void scroll(double d) {
        float sScale = getResolutionScale();
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            this.itemScrollY += (float) (d * 10 * sScale);
        }
    }
}