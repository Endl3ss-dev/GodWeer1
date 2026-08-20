package org.privates.client.screen.csgo;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class SettingPanel extends CsGoBuilder {

    private final ModulePanel module;
    private final org.privates.client.managers.module.settings.Setting setting;

    private boolean selected = false;
    private float selectedV;
    private float alphaV = 1;
    private ArrayList<Float> interpolate = new ArrayList<>();
    private float animValue;

    private float nameScrollX = 0;
    private long lastScrollTime = 0;
    private boolean scrollingForward = true;
    private long scrollPauseUntil = 0;

    private boolean isInputMode = false;
    private String inputValueString = "";

    private float itemScrollY = 0;
    private float itemScrollYV = 0;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();

    public SettingPanel(ModulePanel module, org.privates.client.managers.module.settings.Setting setting) {
        this.module = module;
        this.setting = setting;
        this.width = 240;
        this.height = 18;

        if (setting instanceof MultiBooleanSetting s) {
            for (BooleanSetting bool : s.getValues()) {
                interpolate.add(bool.getValue() ? 1f : 0f);
            }
        }

        if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());
            filteredItems.addAll(allItems);
        }
    }

    private void drawScrollingName(MatrixStack stack, String name, float x, float y, float maxWidth, int color, float size) {
        float nameWidth = Fonts.SF_MEDIUM.getWidth(name, size);

        if (nameWidth > maxWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.5f;
                long updateRate = 20;

                if (currentTime - lastScrollTime > updateRate) {
                    if (scrollingForward) {
                        nameScrollX -= speed;
                        if (nameScrollX <= -(nameWidth - maxWidth)) {
                            nameScrollX = -(nameWidth - maxWidth);
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
        ScissorExp.setFromComponentCoordinates(x, y - 2, maxWidth, size + 4);
        Fonts.SF_MEDIUM.draw(stack, name, x + nameScrollX, y, color, size);
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

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();

        if (setting instanceof BooleanSetting s) {
            height = 18;
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 14);

            float controlWidth = 20 + 8;
            float availableNameWidth = width - controlWidth;
            float nameY = y + height / 2f - Fonts.SF_MEDIUM.getHeight(s.getName(), 8) / 2f;

            int nameColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(140, 140, 160),
                    ColorUtil.getColor(220, 220, 240),
                    selectedV
            ), alpha(alphaV));

            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, nameColor, 8);

            float toggleX = x + width - 20;
            float toggleY = y + (height - 20) / 2f;
            float toggleSize = 20;

            int circleBgStart = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(30, 30, 40, 150),
                    ColorUtil.getColor(138, 104, 193, 80),
                    selectedV
            ), alpha(alphaV));
            int circleBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 150), alpha(alphaV));
            RenderUtil.Rounded.gradientRect(stack, toggleX, toggleY, toggleSize, toggleSize, circleBgStart, circleBgEnd, circleBgEnd, circleBgStart, Round.of(10));

            if (selectedV > 0.01f) {
                int innerCircle = ColorUtil.multAlpha(ColorUtil.getColor(200, 180, 230), selectedV * alpha(alphaV));
                RenderUtil.Rounded.roundedRect(stack, toggleX + 5, toggleY + 5, 10, 10, innerCircle, Round.of(5));
            }

        } else if (setting instanceof SliderSetting s) {
            height = 32;

            float nameWidth = width * 0.5f;
            float sliderWidth = width - nameWidth - 8;

            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, y + 6, nameWidth, nameColor, 9);

            String valueText = isInputMode ? (inputValueString.isEmpty() ? "" : inputValueString) : String.format("%.2f", s.getValue());
            if (isInputMode && System.currentTimeMillis() % 1000 < 500) valueText += "_";
            int valueColor = ColorUtil.multAlpha(ColorUtil.getColor(160, 160, 180), alpha(alphaV));
            Fonts.SF_MEDIUM.draw(stack, valueText, x + nameWidth, y + 6, valueColor, 8);

            float sliderX = x + nameWidth;
            float sliderY = y + 22;
            float sliderHeight = 2;

            int sliderBgStart = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 40), alpha(alphaV));
            int sliderBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 40), alpha(alphaV));
            RenderUtil.Rounded.gradientRect(stack, sliderX, sliderY, sliderWidth, sliderHeight, sliderBgStart, sliderBgEnd, sliderBgEnd, sliderBgStart, Round.of(1));

            float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
            float targetKnobX = sliderX + ((sliderWidth - 4) * progress);
            animValue = Mathf.fast(animValue, targetKnobX, 14);

            int fillStart = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 180), alpha(alphaV));
            int fillEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 180), alpha(alphaV));
            if (animValue - sliderX + 2 > 0) {
                RenderUtil.Rounded.gradientRect(stack, sliderX, sliderY, animValue - sliderX + 2, sliderHeight, fillStart, fillEnd, fillEnd, fillStart, Round.of(1));
            }

            float knobSize = 4;
            int knobColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 200, 240), alpha(alphaV));
            RenderUtil.Rounded.roundedRect(stack, animValue, sliderY - 1, knobSize, knobSize, knobColor, Round.of(2));

            if (selected) {
                float step = s.increment <= 0 ? 0.1F : s.increment;
                s.set((float) MathHelper.clamp(
                        Mathf.step((mouseX - sliderX) / sliderWidth * (s.max - s.min) + s.min, step),
                        s.min, s.max));
            }

        } else if (setting instanceof ModeSetting s) {
            float targetHeight = selected ? 18 + 18 * s.values.size() : 18;
            height = Mathf.fast(height, targetHeight, 14);

            float nameWidth = width * 0.5f;
            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, y + 5, nameWidth, nameColor, 9);

            float dropdownWidth = 100;
            float dropdownX = x + width - dropdownWidth;

            int dropdownBgStart = ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 28, 180), alpha(alphaV));
            int dropdownBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 180), alpha(alphaV));
            RenderUtil.Rounded.gradientRect(stack, dropdownX, y, dropdownWidth, 18, dropdownBgStart, dropdownBgEnd, dropdownBgEnd, dropdownBgStart, Round.of(4));

            int borderColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 60), alpha(alphaV));
            RenderUtil.Rounded.roundedOutline(stack, dropdownX, y, dropdownWidth, 18, 1, borderColor, Round.of(4));

            int valueColor = ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 200), alpha(alphaV));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(dropdownX + 8, y, dropdownWidth - 22, 18);
            Fonts.SF_MEDIUM.draw(stack, s.getValue(), dropdownX + 8, y + 5, valueColor, 8);
            ScissorExp.unset();
            ScissorExp.pop();

            String arrow = selected ? "▴" : "▾";
            Fonts.SF_MEDIUM.draw(stack, arrow, dropdownX + dropdownWidth - 14, y + 5, valueColor, 8);

            if (height > 18.1f) {
                float optionsY = y + 20;
                float optionsHeight = height - 20;
                int optionsBgStart = ColorUtil.multAlpha(ColorUtil.getColor(15, 15, 22, 240), alpha(alphaV));
                int optionsBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 240), alpha(alphaV));
                RenderUtil.Rounded.gradientRect(stack, dropdownX, optionsY, dropdownWidth, optionsHeight, optionsBgStart, optionsBgEnd, optionsBgEnd, optionsBgStart, Round.of(4));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(dropdownX, optionsY, dropdownWidth, optionsHeight);

                float itemY = optionsY + 6;
                for (String value : s.values) {
                    boolean isSelectedValue = s.is(value);
                    int itemColor = isSelectedValue ?
                            ColorUtil.multAlpha(ColorUtil.getColor(200, 180, 230), alpha(alphaV)) :
                            ColorUtil.multAlpha(ColorUtil.getColor(160, 160, 180), alpha(alphaV));

                    Fonts.SF_MEDIUM.draw(stack, value, dropdownX + 10, itemY, itemColor, 8);
                    itemY += 18;
                }

                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof MultiBooleanSetting s) {
            String values = s.getValue().values().stream()
                    .filter(BooleanSetting::getValue)
                    .map(BooleanSetting::getName)
                    .collect(Collectors.joining(", "));

            float targetHeight = selected ? 18 + 18 * s.getValues().size() : 18;
            height = Mathf.fast(height, targetHeight, 14);

            float nameWidth = width * 0.5f;
            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, y + 5, nameWidth, nameColor, 9);

            float dropdownWidth = 120;
            float dropdownX = x + width - dropdownWidth;

            int dropdownBgStart = ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 28, 180), alpha(alphaV));
            int dropdownBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 180), alpha(alphaV));
            RenderUtil.Rounded.gradientRect(stack, dropdownX, y, dropdownWidth, 18, dropdownBgStart, dropdownBgEnd, dropdownBgEnd, dropdownBgStart, Round.of(4));

            int borderColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 60), alpha(alphaV));
            RenderUtil.Rounded.roundedOutline(stack, dropdownX, y, dropdownWidth, 18, 1, borderColor, Round.of(4));

            int valueColor = ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 200), alpha(alphaV));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(dropdownX + 8, y, dropdownWidth - 22, 18);
            Fonts.SF_MEDIUM.draw(stack, values.isEmpty() ? "None" : values, dropdownX + 8, y + 5, valueColor, 8);
            ScissorExp.unset();
            ScissorExp.pop();

            String arrow = selected ? "▴" : "▾";
            Fonts.SF_MEDIUM.draw(stack, arrow, dropdownX + dropdownWidth - 14, y + 5, valueColor, 8);

            if (height > 18.1f) {
                float optionsY = y + 20;
                float optionsHeight = height - 20;
                int optionsBgStart = ColorUtil.multAlpha(ColorUtil.getColor(15, 15, 22, 240), alpha(alphaV));
                int optionsBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 240), alpha(alphaV));
                RenderUtil.Rounded.gradientRect(stack, dropdownX, optionsY, dropdownWidth, optionsHeight, optionsBgStart, optionsBgEnd, optionsBgEnd, optionsBgStart, Round.of(4));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(dropdownX, optionsY, dropdownWidth, optionsHeight);

                float itemY = optionsY + 6;
                int id = 0;
                for (BooleanSetting bool : s.getValues()) {
                    interpolate.set(id, Mathf.fast(interpolate.get(id), bool.getValue() ? 1 : 0, 14));

                    int itemColor = ColorUtil.multAlpha(ColorUtil.overCol(
                            ColorUtil.getColor(160, 160, 180),
                            ColorUtil.getColor(200, 180, 230),
                            interpolate.get(id)
                    ), alpha(alphaV));
                    float checkX = dropdownX + 10;
                    float checkY = itemY + 2;
                    float checkSize = 10;
                    int checkBgStart = ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 40, 150), alpha(alphaV));
                    int checkBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 150), alpha(alphaV));
                    RenderUtil.Rounded.gradientRect(stack, checkX, checkY, checkSize, checkSize, checkBgStart, checkBgEnd, checkBgEnd, checkBgStart, Round.of(5));

                    if (interpolate.get(id) > 0.01f) {
                        int checkColor = ColorUtil.multAlpha(ColorUtil.getColor(200, 180, 230), interpolate.get(id) * alpha(alphaV));
                        RenderUtil.Rounded.roundedRect(stack, checkX + 2, checkY + 2, checkSize - 4, checkSize - 4, checkColor, Round.of(3));
                    }

                    Fonts.SF_MEDIUM.draw(stack, bool.getName(), checkX + checkSize + 6, itemY, itemColor, 8);
                    itemY += 18;
                    id++;
                }

                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof ColorSetting s) {
            float targetHeight = selected ? 18 + 110 : 18;
            height = Mathf.fast(height, targetHeight, 14);
            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 14);

            float nameWidth = width - 18 - 8;
            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, y + height / 2f - Fonts.SF_MEDIUM.getHeight(s.getName(), 9) / 2f, nameWidth, nameColor, 9);

            float colorBoxSize = 14;
            float colorBoxX = x + width - colorBoxSize;
            float colorBoxY = y + 2;

            int colorValue = ColorUtil.multAlpha(s.getValue(), alpha(alphaV));
            RenderUtil.Rounded.roundedRect(stack, colorBoxX, colorBoxY, colorBoxSize, colorBoxSize, colorValue, Round.of(3));

            int borderColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 100), alpha(alphaV));
            RenderUtil.Rounded.roundedOutline(stack, colorBoxX, colorBoxY, colorBoxSize, colorBoxSize, 1, borderColor, Round.of(3));

            if (selectedV > 0.01f) {
                float pickerY = y + 20;
                float pickerHeight = 110;

                stack.push();
                stack.translate(x + width / 2f, pickerY + pickerHeight / 2f, 0);
                stack.scale(1, selectedV, 1);
                stack.translate(-(x + width / 2f), -(pickerY + pickerHeight / 2f), 0);

                int pickerBgStart = ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 28, 200), alpha(alphaV) * selectedV);
                int pickerBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 200), alpha(alphaV) * selectedV);
                RenderUtil.Rounded.gradientRect(stack, x, pickerY, width, pickerHeight, pickerBgStart, pickerBgEnd, pickerBgEnd, pickerBgStart, Round.of(6));

                float[] rgba = ColorUtil.getRGBAf(s.getValue());
                float[] hsb = java.awt.Color.RGBtoHSB((int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255), null);

                float hueBarY = pickerY + 10;
                float hueBarHeight = 80;
                int steps = 7;
                float stepHeight = hueBarHeight / steps;

                for (int i = 0; i < steps; i++) {
                    int topColor = java.awt.Color.HSBtoRGB(i / (float) steps, 1f, 1f);
                    int bottomColor = java.awt.Color.HSBtoRGB((i + 1) / (float) steps, 1f, 1f);
                    RenderUtil.Rounded.gradientRect(stack, x + 10, hueBarY + i * stepHeight, 20, stepHeight,
                            ColorUtil.multAlpha(topColor, alpha(alphaV) * selectedV),
                            ColorUtil.multAlpha(bottomColor, alpha(alphaV) * selectedV),
                            ColorUtil.multAlpha(bottomColor, alpha(alphaV) * selectedV),
                            ColorUtil.multAlpha(topColor, alpha(alphaV) * selectedV), Round.of(0));
                }

                float hueIndicatorY = hueBarY + hsb[0] * hueBarHeight - 2;
                RenderUtil.Rounded.roundedRect(stack, x + 8, hueIndicatorY, 24, 4,
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha(alphaV) * selectedV), Round.of(2));

                float satBrightX = x + 40;
                float satBrightY = pickerY + 10;
                float satBrightSize = width - 50;

                int hueColor = java.awt.Color.HSBtoRGB(hsb[0], 1f, 1f);
                RenderUtil.Rounded.roundedRect(stack, satBrightX, satBrightY, satBrightSize, 80,
                        ColorUtil.multAlpha(hueColor, alpha(alphaV) * selectedV), Round.of(4));
                RenderUtil.Rounded.gradientRect(stack, satBrightX, satBrightY, satBrightSize, 80,
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 0), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 0), alpha(alphaV) * selectedV), Round.of(4));
                RenderUtil.Rounded.gradientRect(stack, satBrightX, satBrightY, satBrightSize, 80,
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 0), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 0), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0), alpha(alphaV) * selectedV),
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0), alpha(alphaV) * selectedV), Round.of(4));

                float markerX = satBrightX + hsb[1] * satBrightSize - 3;
                float markerY = satBrightY + (1 - hsb[2]) * 80 - 3;
                RenderUtil.Rounded.roundedRect(stack, markerX, markerY, 6, 6,
                        ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha(alphaV) * selectedV), Round.of(3));

                stack.pop();
            }

        } else if (setting instanceof ItemSetting s) {
            float targetHeight = selected ? 150 : 18;
            height = Mathf.fast(height, targetHeight, 14);
            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 14);

            String itemName = s.getValue().getTranslationKey().substring(s.getValue().getTranslationKey().lastIndexOf('.') + 1).replace('_', ' ');
            itemName = Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
            float itemDisplayWidth = 16 + Fonts.SF_MEDIUM.getWidth(itemName, 8) + 6;

            float availableNameWidth = width - itemDisplayWidth - 4;
            float nameY = y + 5;
            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, nameY, availableNameWidth, nameColor, 9);

            float itemDisplayX = x + width - itemDisplayWidth;
            int displayBgStart = ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 28, 180), alpha(alphaV));
            int displayBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 180), alpha(alphaV));
            RenderUtil.Rounded.gradientRect(stack, itemDisplayX, y, itemDisplayWidth, 18, displayBgStart, displayBgEnd, displayBgEnd, displayBgStart, Round.of(4));

            ItemStack itemStack = new ItemStack(s.getValue());
            mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, (int) itemDisplayX + 2, (int) y + 1);

            Fonts.SF_MEDIUM.draw(stack, itemName, itemDisplayX + 18, y + 5, ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 200), alpha(alphaV)), 8);

            if (selectedV > 0.01f) {
                renderItemSelector(stack, mouseX, mouseY, s, null);
            }

        } else if (setting instanceof MultiItemSetting s) {
            int itemsPerRow = Math.max(1, (int) ((width - 8) / 18));
            int selectedItemCount = s.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;

            float closedHeight = 18 + (requiredRows > 0 ? (requiredRows * 18 + 4) : 0);
            float targetHeight = selected ? closedHeight + 130 : closedHeight;
            height = Mathf.fast(height, targetHeight, 14);
            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 14);

            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alpha(alphaV));
            drawScrollingName(stack, s.getName(), x, y + 5, width - 10, nameColor, 9);

            if (selectedItemCount > 0) {
                float itemsPreviewY = y + 20;
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, closedHeight - 18);

                int currentX = (int) x + 4;
                int currentY = (int) itemsPreviewY;
                for (Item item : s.getValue()) {
                    mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), currentX, currentY);
                    currentX += 18;
                    if (currentX + 18 > x + width - 4) {
                        currentX = (int) x + 4;
                        currentY += 18;
                    }
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

            if (selectedV > 0.01f) {
                renderItemSelector(stack, mouseX, mouseY, null, s);
            }
        }
    }

    private void renderItemSelector(MatrixStack stack, float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float panelBaseY = y + (multiSetting != null ? height - 130 : 20);
        float selectorHeight = 130;

        stack.push();
        stack.translate(x + width / 2f, panelBaseY + selectorHeight / 2f, 0);
        stack.scale(1, selectedV, 1);
        stack.translate(-(x + width / 2f), -(panelBaseY + selectorHeight / 2f), 0);

        int panelBgStart = ColorUtil.multAlpha(ColorUtil.getColor(15, 15, 22, 240), alpha(alphaV) * selectedV);
        int panelBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 240), alpha(alphaV) * selectedV);
        RenderUtil.Rounded.gradientRect(stack, x, panelBaseY, width, selectorHeight, panelBgStart, panelBgEnd, panelBgEnd, panelBgStart, Round.of(6));

        float searchBarY = panelBaseY + 6;
        float searchBarHeight = 18;
        int searchBgStart = ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 32, 200), alpha(alphaV) * selectedV);
        int searchBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 200), alpha(alphaV) * selectedV);
        RenderUtil.Rounded.gradientRect(stack, x + 6, searchBarY, width - 12, searchBarHeight, searchBgStart, searchBgEnd, searchBgEnd, searchBgStart, Round.of(4));

        String searchHint = "Search...";
        String textToRender = inputValueString;
        if (isInputMode && System.currentTimeMillis() % 1000 < 500) textToRender += "_";

        int textColor = (textToRender.isEmpty() && !isInputMode) ?
                ColorUtil.multAlpha(ColorUtil.getColor(120, 120, 140), alpha(alphaV) * selectedV) :
                ColorUtil.multAlpha(ColorUtil.getColor(200, 200, 220), alpha(alphaV) * selectedV);
        Fonts.SF_MEDIUM.draw(stack, textToRender.isEmpty() && !isInputMode ? searchHint : textToRender,
                x + 12, searchBarY + 5, textColor, 8);

        float itemsListStartY = searchBarY + searchBarHeight + 6;
        float itemsListHeight = selectorHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 26 : 6);

        int itemsPerRow = Math.max(1, (int) ((width - 16) / 20));
        int totalRows = (int) Math.ceil((double) filteredItems.size() / itemsPerRow);
        int maxScroll = Math.max(0, totalRows * 20 - (int) itemsListHeight);
        itemScrollY = MathHelper.clamp(itemScrollY, -maxScroll, 0);
        itemScrollYV = Mathf.fast(itemScrollYV, itemScrollY, 14);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 4, itemsListStartY, width - 8, itemsListHeight);

        List<Item> sortedList = new ArrayList<>();
        if (multiSetting != null) {
            for (Item item : filteredItems) {
                if (multiSetting.contains(item)) sortedList.add(0, item);
                else sortedList.add(item);
            }
        } else {
            sortedList.addAll(filteredItems);
        }

        int itemX = (int) x + 8;
        int itemY = (int) (itemsListStartY + itemScrollYV);

        for (Item item : sortedList) {
            boolean isItemSelected = (multiSetting != null && multiSetting.contains(item));

            if (itemY + 16 >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                if (isItemSelected) {
                    int selStart = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 100), alpha(alphaV) * selectedV);
                    int selEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 100), alpha(alphaV) * selectedV);
                    RenderUtil.Rounded.gradientRect(stack, itemX - 1, itemY - 1, 18, 18,
                            selStart, selEnd, selEnd, selStart, Round.of(3));
                }
                if (isHovered(mouseX, mouseY, itemX, itemY, 16, 16)) {
                    int hoverStart = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 50), alpha(alphaV) * selectedV);
                    int hoverEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 50), alpha(alphaV) * selectedV);
                    RenderUtil.Rounded.gradientRect(stack, itemX - 1, itemY - 1, 18, 18,
                            hoverStart, hoverEnd, hoverEnd, hoverStart, Round.of(3));
                }
                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), itemX, itemY);
            }

            itemX += 20;
            if (itemX + 20 > x + width - 8) {
                itemX = (int) x + 8;
                itemY += 20;
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (multiSetting != null) {
            float buttonWidth = 60;
            float buttonHeight = 16;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + selectorHeight - buttonHeight - 5;

            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
            int buttonStart = buttonHovered ?
                    ColorUtil.multAlpha(ColorUtil.getColor(158, 124, 213), alpha(alphaV) * selectedV) :
                    ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193), alpha(alphaV) * selectedV);
            int buttonEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17), alpha(alphaV) * selectedV);
            RenderUtil.Rounded.gradientRect(stack, buttonX, buttonY, buttonWidth, buttonHeight, buttonStart, buttonEnd, buttonEnd, buttonStart, Round.of(4));
            Fonts.SF_MEDIUM.draw(stack, "Done",
                    buttonX + buttonWidth / 2 - Fonts.SF_MEDIUM.getWidth("Done", 8) / 2,
                    buttonY + buttonHeight / 2 - Fonts.SF_MEDIUM.getHeight("Done", 8) / 2,
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha(alphaV) * selectedV), 8);
        }

        stack.pop();
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (action == GLFW.GLFW_PRESS && button == 0) {
            if (setting instanceof BooleanSetting s) {
                float toggleX = x + width - 20;
                float toggleY = y + (height - 20) / 2f;
                if (isHovered(mouseX, mouseY, toggleX, toggleY, 20, 20)) {
                    s.set(!s.getValue());
                }
            } else if (setting instanceof SliderSetting s) {
                float nameWidth = width * 0.5f;
                float valueTextWidth = Fonts.SF_MEDIUM.getWidth(String.format("%.2f", s.getValue()), 8);
                if (isHovered(mouseX, mouseY, x + nameWidth, y, valueTextWidth + 8, 20)) {
                    isInputMode = true;
                    inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                    return;
                }
                float sliderWidth = width - nameWidth - 8;
                float sliderX = x + nameWidth;
                if (isHovered(mouseX, mouseY, sliderX, y, sliderWidth, height)) {
                    selected = true;
                }
            } else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) {
                float dropdownWidth = (setting instanceof ModeSetting) ? 100 : 120;
                float dropdownX = x + width - dropdownWidth;
                if (isHovered(mouseX, mouseY, dropdownX, y, dropdownWidth, 18)) {
                    selected = !selected;
                    return;
                } else if (selected) {
                    if (setting instanceof ModeSetting s) {
                        float optionsY = y + 20;
                        float itemY = optionsY + 6;
                        for (String value : s.values) {
                            if (isHovered(mouseX, mouseY, dropdownX, itemY - 6, dropdownWidth, 18)) {
                                s.set(value);
                                selected = false;
                                return;
                            }
                            itemY += 18;
                        }
                    } else if (setting instanceof MultiBooleanSetting s) {
                        float optionsY = y + 20;
                        float itemY = optionsY + 6;
                        for (BooleanSetting bool : s.getValues()) {
                            if (isHovered(mouseX, mouseY, dropdownX, itemY - 6, dropdownWidth, 18)) {
                                bool.set(!bool.getValue());
                                return;
                            }
                            itemY += 18;
                        }
                    }
                }
            } else if (setting instanceof ColorSetting s) {
                float pickerY = y + 20;
                float pickerHeight = 110;
                if (selected && selectedV > 0.5f && isHovered(mouseX, mouseY, x, pickerY, width, pickerHeight)) {
                    float hueBarY = pickerY + 10;
                    if (isHovered(mouseX, mouseY, x + 10, hueBarY, 20, 80)) {
                        float hue = Math.max(0, Math.min(1, (mouseY - hueBarY) / 80f));
                        float[] rgba = ColorUtil.getRGBAf(s.getValue());
                        float[] hsb = java.awt.Color.RGBtoHSB((int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255), null);
                        int newColor = java.awt.Color.HSBtoRGB(hue, hsb[1], hsb[2]);
                        s.set(ColorUtil.replAlpha(newColor, rgba[3]));
                        return;
                    }
                    float satBrightX = x + 40;
                    float satBrightY = pickerY + 10;
                    float satBrightSize = width - 50;
                    if (isHovered(mouseX, mouseY, satBrightX, satBrightY, satBrightSize, 80)) {
                        float sat = Math.max(0, Math.min(1, (mouseX - satBrightX) / satBrightSize));
                        float bright = Math.max(0, Math.min(1, 1 - (mouseY - satBrightY) / 80f));
                        float[] rgba = ColorUtil.getRGBAf(s.getValue());
                        float[] hsb = java.awt.Color.RGBtoHSB((int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255), null);
                        int newColor = java.awt.Color.HSBtoRGB(hsb[0], sat, bright);
                        s.set(ColorUtil.replAlpha(newColor, rgba[3]));
                        return;
                    }
                }
                if (isHovered(mouseX, mouseY, x, y, width, 18)) {
                    selected = !selected;
                    return;
                }
            } else if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && isHovered(mouseX, mouseY, x, y, width, 18)) {
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

            if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected && selectedV > 0.5f) {
                handleItemSelectorClick(mouseX, mouseY);
            }
        }

        if (action == GLFW.GLFW_RELEASE && button == 0 && setting instanceof SliderSetting) {
            selected = false;
        }
    }

    private void handleItemSelectorClick(double mouseX, double mouseY) {
        float panelBaseY = y + (setting instanceof MultiItemSetting ? height - 130 : 20);
        float searchBarY = panelBaseY + 6;
        float searchBarHeight = 18;

        if (isHovered((float) mouseX, (float) mouseY, x + 6, searchBarY, width - 12, searchBarHeight)) {
            isInputMode = true;
            return;
        }

        float itemsListStartY = searchBarY + searchBarHeight + 6;
        float itemsListHeight = 130 - (itemsListStartY - panelBaseY) - (setting instanceof MultiItemSetting ? 26 : 6);

        if (isHovered((float) mouseX, (float) mouseY, x + 4, itemsListStartY, width - 8, itemsListHeight)) {
            int itemsPerRow = Math.max(1, (int) ((width - 16) / 20));
            int itemX = (int) x + 8;
            int itemY = (int) (itemsListStartY + itemScrollYV);

            List<Item> sortedList = new ArrayList<>();
            if (setting instanceof MultiItemSetting multiSetting) {
                for (Item item : filteredItems) {
                    if (multiSetting.contains(item)) sortedList.add(0, item);
                    else sortedList.add(item);
                }
            } else {
                sortedList.addAll(filteredItems);
            }

            for (Item item : sortedList) {
                if (itemY + 16 >= itemsListStartY && itemY < itemsListStartY + itemsListHeight) {
                    if (isHovered((float) mouseX, (float) mouseY, itemX, itemY, 16, 16)) {
                        if (setting instanceof ItemSetting s) {
                            s.set(item);
                            selected = false;
                        } else if (setting instanceof MultiItemSetting s) {
                            s.toggle(item);
                        }
                        isInputMode = false;
                        inputValueString = "";
                        return;
                    }
                }
                itemX += 20;
                if (itemX + 20 > x + width - 8) {
                    itemX = (int) x + 8;
                    itemY += 20;
                }
            }
        }

        if (setting instanceof MultiItemSetting) {
            float buttonWidth = 60;
            float buttonHeight = 16;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + 130 - buttonHeight - 5;
            if (isHovered((float) mouseX, (float) mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                selected = false;
            }
        }
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

    @Override
    void key(int key) {
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
        }
    }

    @Override
    void charTyped(char typed) {
        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            inputValueString += typed;
            updateItemFilter();
            return;
        }

        if (isInputMode && setting instanceof SliderSetting) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains(".")) || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
        }
    }

    public void scroll(double delta) {
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            this.itemScrollY += (float) (delta * 12);
        }
    }

    public float alpha(float alpha) {
        return module.alpha(alpha) * alphaV;
    }
}