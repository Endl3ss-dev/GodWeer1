package org.privates.client.screen.eternity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.newgui.ColorPicker;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class Setting {
    private final ModuleButton module;
    private final org.privates.client.managers.module.settings.Setting setting;
    private float x, y, width, height;

    private boolean selected = false;
    private float selectedV = 0f;
    private float animV = 0f;
    private float deltaV = 0f;
    private float alphaV = 1f;
    private long time = 0;
    private ColorPicker colorPicker;

    private ArrayList<Float> interpolate = new ArrayList<>();
    private int offsetX;

    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;

    private boolean isInputMode = false;
    private String inputValueString = "";
    private boolean isEditingMax = false;

    private float itemScrollY = 0;
    private float itemScrollYV = 0;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private Map<Item, Float> itemScaleAnimations = new HashMap<>();

    private static long lastBindClickTime = 0;
    private static final int BIND_CLICK_COOLDOWN_MS = 200;

    public Setting(ModuleButton module, org.privates.client.managers.module.settings.Setting setting) {
        this.module = module;
        this.setting = setting;

        if (setting instanceof SliderSetting) {
            this.height = 20f;
        } else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) {
            this.height = 12f;
            if (setting instanceof MultiBooleanSetting s) {
                for (BooleanSetting booleanSetting : s.getValues()) {
                    interpolate.add((float) (booleanSetting.getValue() ? 1 : 0));
                }
                time = System.currentTimeMillis();
            }
        } else {
            this.height = 12f;
        }

        if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());
            filteredItems.addAll(allItems);
        }
    }

    private void drawScrollingName(MatrixStack stack, String name, float x, float y, float availableWidth, int color, float size) {
        float nameWidth = Fonts.SF_BOLD.getWidth(name, size);

        if (nameWidth > availableWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.5f;
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
        ScissorExp.setFromComponentCoordinates(x, y - 2, availableWidth, size + 4);
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
                    s.set(current);
                }
            } else {
                if (newValue <= current.getMaxValue()) {
                    current.setMinValue(newValue);
                    s.set(current);
                }
            }
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY) {
        if (!(boolean) setting.getVisible().get()) {
            height = 0f;
            return;
        }

        Theme theme = Theme.getInstance();

        if (setting instanceof BooleanSetting s) {
            height = 12f;
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 16);

            float controlWidth = 20f;
            float availableNameWidth = width - controlWidth;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            int bgAlpha = (int) (50 + selectedV * 20);
            int borderAlpha = (int) (80 + selectedV * 40);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - 16f, y + deltaV + 2f, 16f, height - 4f, 1f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, bgAlpha), ColorUtil.multAlpha(theme.clientColor(), .08f), selectedV), alphaV),
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, borderAlpha), ColorUtil.multAlpha(theme.clientColor(), .15f), selectedV), alphaV),
                    Round.of(4));
            RenderUtil.Rounded.roundedRect(stack, x + width - 16f + (selectedV * 8f + 1f), y + deltaV + 3f, 6f, 6f,
                    ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, alphaV), ColorUtil.getColor(220, 220, 220, alphaV), selectedV),
                    Round.of(4));

        } else if (setting instanceof SliderSetting s) {
            height = 20f;

            float nameY = y + deltaV + 2f;
            String valueText = String.format("%.2f", s.getValue());
            float valueTextWidth = Fonts.SF_BOLD.getWidth(isInputMode ? inputValueString + "_" : valueText, 7);
            float boxWidth = valueTextWidth + 4f;
            float boxHeight = Fonts.SF_BOLD.getHeight(valueText, 7) + 4f;
            float boxX = x + width - boxWidth - 2f;
            float boxY = nameY - 2f;

            float availableNameWidth = boxX - x - 2f;
            drawScrollingName(stack, s.getName(), x + 0.5f, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            RenderUtil.Rounded.roundedRect(stack, boxX, boxY, boxWidth, boxHeight,
                    ColorUtil.multAlpha(theme.clientColor(), .08f * alphaV), Round.of(3));

            if (isInputMode) {
                String renderString = inputValueString;
                if (System.currentTimeMillis() % 1000 < 500) {
                    renderString += "_";
                }
                Fonts.SF_BOLD.draw(stack, renderString, boxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            } else {
                Fonts.SF_BOLD.draw(stack, valueText, boxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            }

            float sliderWidth = width - 4f;
            float sliderX = x + 0.5f;
            float sliderY = y + deltaV + height - 5f;

            if (selected) {
                float newProgress = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0.0f, 1.0f);
                float step = s.increment <= 0 ? 0.1F : s.increment;
                s.set((float) Mathf.step(s.min + (s.max - s.min) * newProgress, step));
            }

            float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
            float targetKnobX = sliderX + ((sliderWidth - 6.5f) * progress) + 1.75f;
            animV = Mathf.fast(animV, targetKnobX, 16);

            RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, sliderWidth, 4f,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV / 12f), Round.of(3));
            RenderUtil.Rounded.roundedWithOutline(stack, animV, sliderY - 1f + 1f, 4f, 3.75f, 1f,
                    ColorUtil.multAlpha(theme.clientColor(), .3f * alphaV),
                    ColorUtil.multAlpha(theme.clientColor(), .15f * alphaV), Round.of(1.75f));

        } else if (setting instanceof SliderSettingRand s) {
            height = 28f;

            float nameY = y + deltaV + 2f;
            String minValueText = String.format("%.2f", s.getValue().getMinValue());
            String maxValueText = String.format("%.2f", s.getValue().getMaxValue());

            float minValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && !isEditingMax ? inputValueString + "_" : minValueText, 7);
            float maxValueWidth = Fonts.SF_BOLD.getWidth(isInputMode && isEditingMax ? inputValueString + "_" : maxValueText, 7);
            float minBoxWidth = minValueWidth + 4f;
            float maxBoxWidth = maxValueWidth + 4f;
            float boxHeight = Fonts.SF_BOLD.getHeight(minValueText, 7) + 4f;

            float minBoxX = x + width - minBoxWidth - maxBoxWidth - 6f;
            float maxBoxX = x + width - maxBoxWidth - 2f;
            float boxY = nameY - 2f;

            float availableNameWidth = minBoxX - x - 2f;
            drawScrollingName(stack, s.getName(), x + 0.5f, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            RenderUtil.Rounded.roundedRect(stack, minBoxX, boxY, minBoxWidth, boxHeight,
                    ColorUtil.multAlpha(theme.clientColor(), .08f * alphaV), Round.of(3));
            RenderUtil.Rounded.roundedRect(stack, maxBoxX, boxY, maxBoxWidth, boxHeight,
                    ColorUtil.multAlpha(theme.clientColor(), .08f * alphaV), Round.of(3));

            if (isInputMode && !isEditingMax) {
                String renderString = inputValueString;
                if (System.currentTimeMillis() % 1000 < 500) {
                    renderString += "_";
                }
                Fonts.SF_BOLD.draw(stack, renderString, minBoxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            } else {
                Fonts.SF_BOLD.draw(stack, minValueText, minBoxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            }

            if (isInputMode && isEditingMax) {
                String renderString = inputValueString;
                if (System.currentTimeMillis() % 1000 < 500) {
                    renderString += "_";
                }
                Fonts.SF_BOLD.draw(stack, renderString, maxBoxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            } else {
                Fonts.SF_BOLD.draw(stack, maxValueText, maxBoxX + 2f, boxY + 2f, ColorUtil.getColor(255, 255, 255, alphaV), 7);
            }

            float sliderWidth = width - 4f;
            float sliderX = x + 0.5f;
            float sliderY = y + deltaV + height - 10f;

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

            float minKnobX = sliderX + ((sliderWidth - 6.5f) * minProgress) + 1.75f;
            float maxKnobX = sliderX + ((sliderWidth - 6.5f) * maxProgress) + 1.75f;

            RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, sliderWidth, 4f,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV / 12f), Round.of(3));
            RenderUtil.Rounded.roundedRect(stack, minKnobX, sliderY, maxKnobX - minKnobX + 4f, 4f,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV / 6f), Round.of(3));

            RenderUtil.Rounded.roundedWithOutline(stack, minKnobX, sliderY - 1f + 1f, 4f, 3.75f, 1f,
                    ColorUtil.multAlpha(theme.clientColor(), .3f * alphaV),
                    ColorUtil.multAlpha(theme.clientColor(), .15f * alphaV), Round.of(1.75f));
            RenderUtil.Rounded.roundedWithOutline(stack, maxKnobX, sliderY - 1f + 1f, 4f, 3.75f, 1f,
                    ColorUtil.multAlpha(theme.clientColor(), .3f * alphaV),
                    ColorUtil.multAlpha(theme.clientColor(), .15f * alphaV), Round.of(1.75f));

        } else if (setting instanceof ModeSetting s) {
            selectedV = (selected) ? 18f + s.values.size() * 10f : 12f;
            height = Mathf.fast(height, selectedV, 12f);
            animV = Mathf.fast(animV, selectedV / (18f + s.values.size() * 10f), 15);

            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;
            float availableNameWidth = width - modeWidth - 4f;
            float nameY = y + deltaV + 6f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            int bgAlpha = (int) (45 + animV * 25);
            int borderAlpha = (int) (70 + animV * 50);

            RenderUtil.Rounded.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12f, 1f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, bgAlpha), ColorUtil.multAlpha(theme.clientColor(), .08f), animV), alphaV),
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, borderAlpha), ColorUtil.multAlpha(theme.clientColor(), .15f), animV), alphaV),
                    Round.of(3));

            Fonts.SF_BOLD.draw(stack, "Выбрано: ", modeX + 2f, y + deltaV + 2f, ColorUtil.getColor(160, 160, 160, alphaV), 7);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2f + Fonts.SF_BOLD.getWidth("Выбрано: ", 7), y + deltaV, modeWidth - (2f + Fonts.SF_BOLD.getWidth("Выбрано: ", 7)), 12f);
            Fonts.SF_BOLD.draw(stack, s.getValue(), modeX + 2f + Fonts.SF_BOLD.getWidth("Выбрано: ", 7), y + deltaV + 2f, ColorUtil.getColor(160, 160, 160, alphaV), 7);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f) {
                float offsetY = y + 16f + deltaV;
                RenderUtil.Rounded.roundedWithOutline(stack, modeX, offsetY - 1f, modeWidth, height - 16f, 1.5f,
                        ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 50), alphaV),
                        ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 70), alphaV), Round.of(3));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14f);
                for (String string : s.values) {
                    if (s.is(string))
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f, offsetY + 2.5f, .5f, 4f,
                                ColorUtil.multAlpha(theme.textColor(), animV * alphaV), Round.of(1));
                    Fonts.SF_BOLD.draw(stack, string, modeX + 2f + (s.is(string) ? 3f : 0), offsetY,
                            s.is(string) ? ColorUtil.multAlpha(theme.textColor(), animV * alphaV) : ColorUtil.getColor(160, 160, 160, animV * alphaV), 7);
                    offsetY += 10f * (height / (18f + s.values.size() * 10f));
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof MultiBooleanSetting s) {
            String values = s.getValue().values().stream().filter(BooleanSetting::getValue).map(BooleanSetting::getName).collect(Collectors.joining(", "));
            selectedV = (selected) ? 18f + s.getValues().size() * 10f : 12f;
            height = Mathf.fast(height, selectedV, 12f);
            animV = Mathf.fast(animV, selectedV / (18f + s.getValues().size() * 10f), 15);
            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;

            float availableNameWidth = width - modeWidth - 4f;
            float nameY = y + deltaV + 6f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            if (Fonts.SF_BOLD.getWidth(values, 7) >= modeWidth - 2f - Fonts.SF_BOLD.getWidth("Выбрано: ", 7)) {
                if (System.currentTimeMillis() - time >= 25L && offsetX != 0) {
                    time = System.currentTimeMillis();
                    offsetX = offsetX <= -Fonts.SF_BOLD.getWidth(values, 7) ? (int) (modeWidth - 2f - Fonts.SF_BOLD.getWidth("Выбрано: ", 7)) : offsetX - 1;
                } else if (offsetX == 0 && System.currentTimeMillis() - time >= 1000L) {
                    offsetX -= 1;
                    time = System.currentTimeMillis();
                }
            } else if (offsetX != 0 && System.currentTimeMillis() - time >= 25L) {
                offsetX = (offsetX > 0) ? offsetX - 1 : offsetX + 1;
                time = System.currentTimeMillis();
            }

            int bgAlpha = (int) (45 + animV * 25);
            int borderAlpha = (int) (70 + animV * 50);

            RenderUtil.Rounded.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12f, 1f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, bgAlpha), ColorUtil.multAlpha(theme.clientColor(), .08f), animV), alphaV),
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, borderAlpha), ColorUtil.multAlpha(theme.clientColor(), .15f), animV), alphaV),
                    Round.of(3));

            Fonts.SF_BOLD.draw(stack, "Выбрано: ", modeX + 2f, y + deltaV + 2f, ColorUtil.getColor(160, 160, 160, alphaV), 7);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2f + Fonts.SF_BOLD.getWidth("Выбрано: ", 7), y + deltaV, modeWidth - 2f - Fonts.SF_BOLD.getWidth("Выбрано: ", 7), 12f);
            Fonts.SF_BOLD.draw(stack, values, modeX + 2f + Fonts.SF_BOLD.getWidth("Выбрано: ", 7) + offsetX, y + deltaV + 2f, ColorUtil.getColor(160, 160, 160, alphaV), 7);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f) {
                float offsetY = y + 16f + deltaV;
                RenderUtil.Rounded.roundedWithOutline(stack, modeX, offsetY - 1f, modeWidth, height - 16f, 1.5f,
                        ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 50), alphaV),
                        ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 70), alphaV), Round.of(3));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14f);
                int id = 0;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    interpolate.set(id, Mathf.fast(interpolate.get(id), booleanSetting.getValue() ? 1 : 0, 16));
                    if (interpolate.get(id) >= .1f)
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f, offsetY + 2.5f, .5f, 4f,
                                ColorUtil.multAlpha(theme.textColor(), animV * interpolate.get(id) * alphaV), Round.of(1));
                    Fonts.SF_BOLD.draw(stack, booleanSetting.getName(), modeX + 2f + interpolate.get(id) * 3f, offsetY,
                            ColorUtil.overCol(ColorUtil.getColor(160, 160, 160, animV * alphaV), ColorUtil.multAlpha(theme.textColor(), animV), interpolate.get(id) * alphaV), 7);
                    offsetY += 10f * (height / (18f + s.getValues().size() * 10f));
                    id++;
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof ColorSetting s) {
            float targetHeight = selected ? 12f + 95f : 12f;
            height = Mathf.fast(height, targetHeight, 12f);
            animV = Mathf.fast(animV, selected ? 1 : 0, 15);

            float[] c = ColorUtil.getRGBAf(s.getValue());
            float controlWidth = 14f;
            float availableNameWidth = width - controlWidth;
            float nameY = y + deltaV + 12f / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            RenderUtil.Rounded.roundedRect(stack, x + width - 10f, y + deltaV + 1f, 10f, 10f,
                    ColorUtil.multAlpha(s.getValue(), c[3] * alphaV), Round.of(4));
            RenderUtil.Rounded.roundedOutline(stack, x + width - 10f, y + deltaV + 1f, 10f, 10f, 1f,
                    ColorUtil.multAlpha(s.getValue(), (c[3] + .2f) * alphaV), Round.of(3));

            if (selected || animV > 0.01f) {
                if (colorPicker == null) {
                    colorPicker = new ColorPicker(x, y + deltaV + 14f, width, 90f, s, this);
                }
                colorPicker.setX(x);
                colorPicker.setY(y + deltaV + 14f);
                colorPicker.setWidth(width);
                colorPicker.setHeight(90f);

                if (animV > 0.01f) {
                    stack.push();
                    stack.translate(x + width / 2f, y + deltaV + 14f + 45f, 0);
                    stack.scale(1, animV, 1);
                    stack.translate(-(x + width / 2f), -(y + deltaV + 14f + 45f), 0);
                    colorPicker.render(stack, (int) mouseX, (int) mouseY);
                    stack.pop();
                }
            } else {
                colorPicker = null;
            }

        } else if (setting instanceof BindSetting s) {
            height = 12f;

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

            float bindWidth = Fonts.SF_BOLD.getWidth(text, 7) + 4f;
            float availableNameWidth = width - bindWidth - 4f;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - bindWidth, y + deltaV + 1f, bindWidth, height - 2f, 1f,
                    ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 50), alphaV),
                    ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 70), alphaV), Round.of(3));
            Fonts.SF_BOLD.draw(stack, text, x + width - bindWidth + 2f, y + deltaV + 2f,
                    ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

        } else if (setting instanceof StringSetting s) {
            height = 12f;
            float stringWidth = width * 0.55f;
            animV = Mathf.fast(animV, selected ? 1 : 0, 16f);

            float availableNameWidth = width - stringWidth - 4f;
            float nameY = y + deltaV + height / 2f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            int bgAlpha = (int) (45 + animV * 25);
            int borderAlpha = (int) (70 + animV * 50);

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - stringWidth, y + deltaV + 1f, stringWidth, height - 2f, 1f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, bgAlpha), ColorUtil.multAlpha(theme.clientColor(), .08f), animV), alphaV),
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(100, 100, 100, borderAlpha), ColorUtil.multAlpha(theme.clientColor(), .15f), animV), alphaV),
                    Round.of(3));
            Fonts.SF_BOLD.draw(stack, (selected || !s.getValue().isEmpty()) ? s.getValue() : s.getDescription(),
                    x + width - stringWidth + 2f, y + deltaV + 2f, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);
        } else if (setting instanceof ItemSetting s) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            Item currentItem = s.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;

            float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
            float targetHeight = selected ? closedHeight + 150f : closedHeight;
            height = Mathf.fast(height, targetHeight, 12f);
            animV = Mathf.fast(animV, selected ? 1 : 0, 12);

            float buttonWidth = 16f;
            float availableNameWidth = width - buttonWidth - 8f;
            drawScrollingName(stack, s.getName(), x, y + deltaV + 6f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            float buttonX = x + width - buttonWidth - 2f;
            float buttonY = y + deltaV + 1f;
            float buttonHeight = 10f;
            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                    ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3f));

            Fonts.icons.draw(stack, "D", buttonX + buttonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                    buttonY + buttonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 7f);

            if (currentItem != null && currentItem != Items.AIR) {
                float itemsPreviewY = y + deltaV + 14f;

                // Анимация масштаба для preview айтема
                float targetScale = 1f;
                float currentScale = itemScaleAnimations.getOrDefault(currentItem, 0f);
                currentScale = Mathf.fast(currentScale, targetScale, 14f);
                itemScaleAnimations.put(currentItem, currentScale);

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, closedHeight - 12f);

                int itemX = (int) (x + 4f);
                int itemY = (int) itemsPreviewY;

                stack.push();
                stack.translate(itemX + 8f, itemY + 8f, 0);
                stack.scale(currentScale, currentScale, currentScale);
                stack.translate(-(itemX + 8f), -(itemY + 8f), 0);

                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(currentItem), itemX, itemY);

                stack.pop();

                ScissorExp.unset();
                ScissorExp.pop();
            } else {
                // Очистка анимаций если айтем убрали
                itemScaleAnimations.clear();
            }

            if (selected) {
                renderItemSelectorPanel(stack, mouseX, mouseY, s, null);
            }
        } else if (setting instanceof MultiItemSetting s) {
            int multiItemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = s.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / multiItemsPerRow) : 0;

            float multiClosedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
            float multiTargetHeight = selected ? multiClosedHeight + 150f : multiClosedHeight;
            height = Mathf.fast(height, multiTargetHeight, 12f);
            animV = Mathf.fast(animV, selected ? 1 : 0, 12);

            float multiButtonWidth = 16f;
            float multiAvailableNameWidth = width - multiButtonWidth - 8f;
            drawScrollingName(stack, s.getName(), x, y + deltaV + 6f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f, multiAvailableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            float multiButtonX = x + width - multiButtonWidth - 2f;
            float multiButtonY = y + deltaV + 1f;
            float multiButtonHeight = 10f;
            boolean multiButtonHovered = isHovered(mouseX, mouseY, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight, 1f,
                    ColorUtil.multAlpha(multiButtonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(multiButtonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3f));

            Fonts.icons.draw(stack, "D", multiButtonX + multiButtonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                    multiButtonY + multiButtonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 7f);

            if (selectedItemCount > 0) {
                float itemsPreviewY = y + deltaV + 14f;
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, multiClosedHeight - 12f);

                int currentX = (int) (x + 4f);
                int currentY = (int) itemsPreviewY;

                for (Item item : s.getValue()) {
                    // Анимация масштаба для каждого preview айтема
                    float targetScale = 1f;
                    float currentScale = itemScaleAnimations.getOrDefault(item, 0f);
                    currentScale = Mathf.fast(currentScale, targetScale, 14f);
                    itemScaleAnimations.put(item, currentScale);

                    stack.push();
                    stack.translate(currentX + 8f, currentY + 8f, 0);
                    stack.scale(currentScale, currentScale, currentScale);
                    stack.translate(-(currentX + 8f), -(currentY + 8f), 0);

                    mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), currentX, currentY);

                    stack.pop();

                    currentX += 18;
                    if (currentX + 18 > x + width - 4f) {
                        currentX = (int) (x + 4f);
                        currentY += 18;
                    }
                }

                // Удаляем анимации для айтемов, которые больше не выбраны
                itemScaleAnimations.keySet().removeIf(item -> !s.getValue().contains(item));

                ScissorExp.unset();
                ScissorExp.pop();
            } else {
                // Очистка анимаций если айтемы убрали
                itemScaleAnimations.clear();
            }

            if (selected) {
                renderItemSelectorPanel(stack, mouseX, mouseY, null, s);
            }
        } else if (setting instanceof ThemeSetting s) {
            int presetCount = s.getPresets().size();
            float boxSize = 22f;
            float spacing = 4f;
            int cols = Math.max(1, (int) ((width - 8f) / (boxSize + spacing)));
            int rows = (int) Math.ceil((float) presetCount / cols);

            selectedV = selected ? (18f + rows * (boxSize + spacing)) : 12f;
            height = Mathf.fast(height, selectedV, 12f);
            animV = Mathf.fast(animV, selected ? 1 : 0, 15);

            float buttonWidth = 50f;
            float availableNameWidth = width - buttonWidth - 8f;
            float nameY = y + deltaV + 6f - Fonts.SF_BOLD.getHeight(s.getName(), 7) / 1.5f;
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, ColorUtil.multAlpha(theme.disabledColor(), alphaV), 7);

            float buttonX = x + width - buttonWidth - 2f;
            float buttonY = y + deltaV + 1f;
            float buttonHeight = 10f;
            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

            RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                    ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alphaV),
                    ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alphaV),
                    Round.of(3f));

            String buttonText = selected ? "Закрыть" : "Открыть";
            Fonts.SF_BOLD.draw(stack, buttonText, buttonX + buttonWidth / 2f - Fonts.SF_BOLD.getWidth(buttonText, 6f) / 2f,
                    buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(buttonText, 6f) / 2f,
                    ColorUtil.multAlpha(theme.textColor(), alphaV), 6f);

            if (height >= 12.1f) {
                float panelY = y + deltaV + 16f;
                float panelHeight = height - 16f;

                RenderUtil.Rounded.roundedWithOutline(stack, x, panelY - 1f, width, panelHeight, 1.5f,
                        ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alphaV),
                        ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alphaV),
                        Round.of(3f));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, panelY, width, panelHeight - 2f);

                float startX = x + 4f;
                float currentX = startX;
                float currentY = panelY + 2f;

                int currentCol = 0;
                ThemeSetting.ThemePreset currentTheme = s.getValue();

                for (ThemeSetting.ThemePreset preset : s.getPresets()) {
                    boolean isSelected = preset == currentTheme;
                    boolean isHovered = isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize);
                    float alpha = animV * alphaV;

                    RenderUtil.Rounded.roundedRect(stack, currentX + 1f, currentY + 1f, boxSize, boxSize,
                            ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), alpha), Round.of(3f));

                    if (preset.isGradient()) {
                        int leftColor = ColorUtil.multAlpha(preset.getColor1(), alpha);
                        int rightColor = ColorUtil.multAlpha(preset.getColor2(), alpha);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                                leftColor, leftColor, rightColor, rightColor);
                    } else {
                        int baseColor = ColorUtil.multAlpha(preset.getColor1(), alpha);
                        int darkColor = ColorUtil.multDark(baseColor, 0.7f);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                                baseColor, darkColor, darkColor, baseColor);
                    }

                    if (isSelected) {
                        int outlineStart = ColorUtil.multAlpha(theme.clientColor(), alpha);
                        int outlineEnd = ColorUtil.multDark(outlineStart, 0.5f);
                        RenderUtil.Rounded.drawGradientRound(stack, currentX - 1.5f, currentY - 1.5f,
                                boxSize + 3f, boxSize + 3f, 4f,
                                outlineStart, outlineEnd, outlineEnd, outlineStart);
                    }

                    if (isHovered && !isSelected) {
                        int hoverColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 80), alpha);
                        RenderUtil.Rounded.roundedOutline(stack, currentX, currentY, boxSize, boxSize, 1.5f, hoverColor, Round.of(3f));
                    }

                    currentCol++;
                    if (currentCol >= cols) {
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
        }
    }

    private void renderItemSelectorPanel(MatrixStack stack, float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float panelBaseY = y + deltaV + 14f;
        float selectorHeight = 150f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
            panelBaseY = y + deltaV + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
            panelBaseY = y + deltaV + closedHeight;
        }

        float panelHeight = selectorHeight - 4f;
        RenderUtil.Rounded.roundedWithOutline(stack, x, panelBaseY, width, panelHeight, 1.5f,
                ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 30), alphaV * animV),
                ColorUtil.multAlpha(ColorUtil.getColor(80, 80, 80), alphaV * animV), Round.of(4f));

        float searchBarY = panelBaseY + 4f;
        float searchBarHeight = 12f;
        RenderUtil.Rounded.roundedRect(stack, x + 4f, searchBarY, width - 8f, searchBarHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), alphaV * animV), Round.of(3f));

        String searchHint = "Поиск...";
        String textToRender = inputValueString;
        if (isInputMode && System.currentTimeMillis() % 1000 < 500) textToRender += "_";

        Fonts.SF_BOLD.draw(stack, textToRender.isEmpty() && !isInputMode ? searchHint : textToRender,
                x + 8f, searchBarY + 2.5f,
                textToRender.isEmpty() && !isInputMode ? ColorUtil.getColor(120, 120, 120) : -1, 7f);

        float itemsListStartY = searchBarY + searchBarHeight + 4f;
        float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 20f : 4f);

        int itemsPerRow = Math.max(1, (int) ((width - 16f) / 20f));
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int maxScroll = (int) Math.max(0, totalRows * 20f - (int) itemsListHeight);
        itemScrollY = MathHelper.clamp(itemScrollY, -maxScroll, 0);
        itemScrollYV = Mathf.fast(itemScrollYV, itemScrollY, 12);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 2f, itemsListStartY, width - 4f, itemsListHeight);

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

        int itemX = (int) (x + 8f);
        int itemY = (int) (itemsListStartY + itemScrollYV);

        for (Item item : sortedList) {
            boolean isItemSelected = (multiSetting != null && multiSetting.contains(item)) ||
                    (singleSetting != null && singleSetting.getValue() == item);

            if (itemY + 16f >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                if (isItemSelected) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1f, itemY - 1f, 18f, 18f,
                            ColorUtil.multAlpha(Theme.getInstance().clientColor(), 0.5f), Round.of(3f));
                }
                if (isHovered(mouseX, mouseY, itemX, itemY, 16f, 16f)) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1f, itemY - 1f, 18f, 18f,
                            ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), 0.2f), Round.of(3f));
                }
                stack.push();
                stack.translate(itemX + 8f, itemY + 8f, 0);
                stack.scale(animV, animV, animV);
                stack.translate(-(itemX + 8f), -(itemY + 8f), 0);

                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), itemX, itemY);

                stack.pop();
            }

            itemX += 20;
            if (itemX + 20 > x + width - 8f) {
                itemX = (int) (x + 8f);
                itemY += 20;
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (multiSetting != null) {
            float buttonWidth = 50f;
            float buttonHeight = 14f;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + panelHeight - buttonHeight - 3f;

            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
            int buttonColor = buttonHovered ? ColorUtil.multBright(Theme.getInstance().clientColor(), 0.8f) : Theme.getInstance().clientColor();

            RenderUtil.Rounded.roundedRect(stack, buttonX, buttonY, buttonWidth, buttonHeight,
                    ColorUtil.multAlpha(buttonColor, alphaV * animV), Round.of(4f));
            Fonts.SF_BOLD.draw(stack, "Сохранить",
                    buttonX + buttonWidth / 2 - Fonts.SF_BOLD.getWidth("Сохранить", 7f) / 2,
                    buttonY + buttonHeight / 2 - Fonts.SF_BOLD.getHeight("Сохранить", 7f) / 2, -1, 7f);
        }
    }

    public void mouse(float mouseX, float mouseY, int button, int action) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (setting instanceof ColorSetting s && selected && colorPicker != null && action == 0) {
            boolean clickedOutside = !isHovered(mouseX, mouseY, x, y + deltaV, width, height);

            if (clickedOutside) {
                selected = false;
                return;
            }
        }

        if (isInputMode && action == 0 && !(setting instanceof ItemSetting) && !(setting instanceof MultiItemSetting)) {
            if (setting instanceof SliderSetting) applyInputValue((SliderSetting) setting);
            else if (setting instanceof SliderSettingRand) applyInputValueRand((SliderSettingRand) setting);
            else isInputMode = false;
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
            if ((setting instanceof ModeSetting || setting instanceof MultiBooleanSetting || setting instanceof BindSetting ||
                    setting instanceof StringSetting || setting instanceof ItemSetting || setting instanceof MultiItemSetting ||
                    setting instanceof ThemeSetting || setting instanceof ColorSetting) &&
                    isHovered(mouseX, mouseY, x, y, width, 12f)) {
                if (setting instanceof BindSetting) {
                    if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                        return;
                    }
                    SoundUtil.playSound("bind_wait.wav", 0.1f);
                    lastBindClickTime = System.currentTimeMillis();
                }

                if (setting instanceof ColorSetting) {
                    selected = !selected;
                    if (colorPicker == null && selected) {
                        colorPicker = new ColorPicker(x, y + deltaV + 14f, width, 90f, (ColorSetting) setting, this);
                    }
                    return;
                } else if (setting instanceof ThemeSetting s) {
                    float buttonWidth = 50f;
                    float buttonX = x + width - buttonWidth - 2f;
                    float buttonY = y + deltaV + 1f;
                    float buttonHeight = 10f;

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
        } else if (action == 0 && setting instanceof ThemeSetting s && selected &&
                isHovered(mouseX, mouseY, x, y + deltaV + 16f, width, height - 16f)) {
            float panelY = y + deltaV + 16f;
            float boxSize = 22f;
            float spacing = 4f;
            int maxCols = Math.max(1, (int) ((width - 8f) / (boxSize + spacing)));
            float startX = x + 4f;
            float currentX = startX;
            float currentY = panelY + 2f;

            int currentCol = 0;

            for (ThemeSetting.ThemePreset preset : s.getPresets()) {
                if (isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize)) {
                    s.set(preset);
                    selected = false;
                    SoundUtil.playSound("cb_enabled.wav", 0.1f);
                    break;
                }

                currentCol++;
                if (currentCol >= maxCols) {
                    currentCol = 0;
                    currentX = startX;
                    currentY += boxSize + spacing;
                } else {
                    currentX += boxSize + spacing;
                }
            }
        }

        if (button == 0) {
            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;

            if (action == 0 && setting instanceof BooleanSetting s && isHovered(mouseX, mouseY, x, y, width, height)) {
                s.set(!s.getValue());
                SoundUtil.playSound(s.getValue() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
            } else if (setting instanceof SliderSetting s) {
                float sliderWidth = width - 4f;
                float sliderX = x + 0.5f;
                float sliderY = y + deltaV + height - 5f;

                String valueText = String.format("%.2f", s.getValue());
                float valueTextWidth = Fonts.SF_BOLD.getWidth(valueText, 7);
                float valueBoxWidth = valueTextWidth + 4f;
                float valueBoxX = x + width - valueBoxWidth - 2f;
                float valueBoxY = y + deltaV + 2f - 2f;
                float valueBoxHeight = Fonts.SF_BOLD.getHeight(valueText, 7) + 4f;

                if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                    isInputMode = true;
                    inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                    selected = false;
                    return;
                }

                if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX - 2f, sliderY - 3f, sliderWidth + 4f, 10f)) {
                    selected = true;
                    SoundUtil.playSound("slider.wav", 0.1f);
                }
            } else if (setting instanceof SliderSettingRand s) {
                float sliderWidth = width - 4f;
                float sliderX = x + 0.5f;
                float sliderY = y + deltaV + height - 10f;

                String minValueText = String.format("%.2f", s.getValue().getMinValue());
                String maxValueText = String.format("%.2f", s.getValue().getMaxValue());
                float minValueWidth = Fonts.SF_BOLD.getWidth(minValueText, 7);
                float maxValueWidth = Fonts.SF_BOLD.getWidth(maxValueText, 7);
                float minBoxWidth = minValueWidth + 4f;
                float maxBoxWidth = maxValueWidth + 4f;
                float minBoxX = x + width - minBoxWidth - maxBoxWidth - 6f;
                float maxBoxX = x + width - maxBoxWidth - 2f;
                float boxY = y + deltaV + 2f - 2f;
                float boxHeight = Fonts.SF_BOLD.getHeight(minValueText, 7) + 4f;

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

                if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX - 2f, sliderY - 3f, sliderWidth + 4f, 10f)) {
                    float minProgress = (s.getValue().getMinValue() - s.min) / (s.max - s.min);
                    float maxProgress = (s.getValue().getMaxValue() - s.min) / (s.max - s.min);
                    float minKnobX = sliderX + ((sliderWidth - 6.5f) * minProgress) + 1.75f;
                    float maxKnobX = sliderX + ((sliderWidth - 6.5f) * maxProgress) + 1.75f;

                    float distToMin = Math.abs(mouseX - minKnobX);
                    float distToMax = Math.abs(mouseX - maxKnobX);

                    isEditingMax = distToMax < distToMin;
                    selected = true;
                    SoundUtil.playSound("slider.wav", 0.1f);
                }
            } else if (action == 0 && setting instanceof ModeSetting s && selected && isHovered(mouseX, mouseY, modeX, y + 16f, modeWidth, height - 16f)) {
                float offsetY = y + 16f + deltaV;
                for (String string : s.values) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10f)) {
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
                    offsetY += 10f * (height / (16f + s.values.size() * 10f));
                }
            } else if (action == 0 && setting instanceof MultiBooleanSetting s && selected && isHovered(mouseX, mouseY, modeX, y + 16f, modeWidth, height - 16f)) {
                float offsetY = y + 16f + deltaV;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10f)) {
                        boolean wasSelected = booleanSetting.getValue();
                        booleanSetting.set(!booleanSetting.getValue());
                        if (!wasSelected) {
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        } else {
                            SoundUtil.playSound("mode_pick.wav", 0.1f);
                        }
                        break;
                    }
                    offsetY += 10f * (height / (18f + s.getValues().size() * 10f));
                }
            } else if (setting instanceof ColorSetting s) {
                if (selected && colorPicker != null) {
                    if (isHovered(mouseX, mouseY, x, y + deltaV + 14f, width, 90f) && action == GLFW.GLFW_PRESS) {
                        colorPicker.mouseClicked(mouseX, mouseY, button);
                    }
                }
            } else if (setting instanceof ItemSetting itemSetting) {
                if (action == GLFW.GLFW_PRESS && button == 0) {
                    float buttonWidth = 16f;
                    float buttonX = x + width - buttonWidth - 2f;
                    float buttonY = y + deltaV + 1f;
                    float buttonHeight = 10f;

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
                    float buttonWidth = 16f;
                    float buttonX = x + width - buttonWidth - 2f;
                    float buttonY = y + deltaV + 1f;
                    float buttonHeight = 10f;

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
            }
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (selected && (setting instanceof SliderSetting || setting instanceof SliderSettingRand)) {
                selected = false;
            }
            if (setting instanceof ColorSetting s && colorPicker != null) {
                colorPicker.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    private void handleItemSelectorMouse(float mouseX, float mouseY, int action, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float panelBaseY = y + deltaV + 14f;
        float selectorHeight = 150f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
            panelBaseY = y + deltaV + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
            panelBaseY = y + deltaV + closedHeight;
        }

        float panelHeight = selectorHeight - 4f;
        float searchBarY = panelBaseY + 4f;
        float searchBarHeight = 12f;

        if (action == 0 && isHovered(mouseX, mouseY, x + 4f, searchBarY, width - 8f, searchBarHeight)) {
            isInputMode = true;
            return;
        }

        if (action == 0) {
            float itemsListStartY = searchBarY + searchBarHeight + 4f;
            float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 20f : 4f);

            if (isHovered(mouseX, mouseY, x + 2f, itemsListStartY, width - 4f, itemsListHeight)) {
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

                int itemsPerRow = Math.max(1, (int) ((width - 16f) / 20f));
                int itemX = (int) (x + 8f);
                int itemY = (int) (itemsListStartY + itemScrollYV);

                for (Item item : sortedList) {
                    if (itemY + 16f >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                        if (isHovered(mouseX, mouseY, itemX, itemY, 16f, 16f)) {
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
                    itemX += 20;
                    if (itemX + 20 > x + width - 8f) {
                        itemX = (int) (x + 8f);
                        itemY += 20;
                    }
                }
                return;
            }

            if (multiSetting != null) {
                float buttonWidth = 50f;
                float buttonHeight = 14f;
                float buttonX = x + width / 2 - buttonWidth / 2;
                float buttonY = panelBaseY + panelHeight - buttonHeight - 3f;
                if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                    selected = false;
                    isInputMode = false;
                    inputValueString = "";
                }
            }
        }
    }

    public void key(int key) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

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

    public void charTyped(char typed) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

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
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            float panelBaseY = y + deltaV + 14f;
            float selectorHeight = 150f;

            if (setting instanceof ItemSetting s) {
                Item currentItem = s.getValue();
                int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
                float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
                panelBaseY = y + deltaV + closedHeight;
            } else if (setting instanceof MultiItemSetting s) {
                int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
                int selectedItemCount = s.getValue().size();
                int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
                float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
                panelBaseY = y + deltaV + closedHeight;
            }

            float panelHeight = selectorHeight - 4f;
            float searchBarY = panelBaseY + 4f;
            float searchBarHeight = 12f;
            float itemsListStartY = searchBarY + searchBarHeight + 4f;
            float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (setting instanceof MultiItemSetting ? 20f : 4f);

            this.itemScrollY += (float) (d * 10f);
        }
    }

    public boolean isItemSelectorHovered(float mouseX, float mouseY) {
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            float panelBaseY = y + deltaV + 14f;
            float selectorHeight = 150f;

            if (setting instanceof ItemSetting s) {
                Item currentItem = s.getValue();
                int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
                float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
                panelBaseY = y + deltaV + closedHeight;
            } else if (setting instanceof MultiItemSetting s) {
                int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
                int selectedItemCount = s.getValue().size();
                int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
                float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
                panelBaseY = y + deltaV + closedHeight;
            }

            float panelHeight = selectorHeight - 4f;
            return isHovered(mouseX, mouseY, x, panelBaseY, width, panelHeight);
        }
        return false;
    }

    private boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}