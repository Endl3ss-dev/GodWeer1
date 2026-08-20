package org.privates.client.screen.countergui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.ClipboardHelper;
import net.minecraft.client.Minecraft;
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
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class Setting extends Builder {

    private final Module module;
    private final org.privates.client.managers.module.settings.Setting setting;
    private final float fontSize = 8f;
    private final Font font = Fonts.SF_MEDIUM;
    private final ArrayList<Float> interpolateM = new ArrayList<>();
    private final ArrayList<Float> offsetXM = new ArrayList<>();
    private final ArrayList<Long> hoverTime = new ArrayList<>();

    private boolean selected = false;
    private float selectedV, offsetV;
    private float modeWidth, offsetX;
    private long time;
    private ColorPicker colorPicker;

    private boolean isInputMode = false;
    private String inputValueString = "";

    private float itemScrollY = 0;
    private float itemScrollYV = 0;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private Map<Item, Float> itemScaleAnimations = new HashMap<>();

    private static long lastBindClickTime = 0;
    private static final int BIND_CLICK_COOLDOWN_MS = 200;

    public Setting(Module module, org.privates.client.managers.module.settings.Setting setting, float x, float y) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.width = module.getWidth() - 9;
        this.height = 8;
        if (setting instanceof SliderSetting)
            height = 15;
        else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) {
            height = 12;
            selectedV = height;
            if (setting instanceof MultiBooleanSetting s) {
                for (BooleanSetting bool : s.getValues()) {
                    interpolateM.add((float) (bool.getValue() ? 1 : 0));
                    offsetXM.add(0f);
                    hoverTime.add(0L);
                }
            }
        } else if (setting instanceof StringSetting) {
            height = 20;
            interpolateM.add(0F);
        } else if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            height = 12;
            allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());
            filteredItems.addAll(allItems);
        } else if (setting instanceof ThemeSetting) {
            height = 12;
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY) {
        if (!(boolean) setting.getVisible().get()) {
            height = 0f;
            return;
        }

        if (!inRange(y, height, module.getCategory().getGui().getY(), module.getCategory().getGui().getHeight()))
            return;

        Theme theme = Theme.getInstance();
        int interpolate = ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), module.getInterpolate()), alpha(1));
        float centerY = y + 4 - font.getHeight(setting.getName(), fontSize) / 1.5f;

        if (setting instanceof BooleanSetting s) {
            height = 8;
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 16);

            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - font.getWidth(String.valueOf(s.getValue()), fontSize), fontSize) &&
                    font.getWidth(s.getName(), fontSize) > width - font.getWidth(String.valueOf(s.getValue()), fontSize)) ? 10 - ((width) - nameWidth) : 0;
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - 10, height + 2);
            font.draw(stack, s.getName(), x - offsetV, centerY, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            RenderUtil.Rounded.smooth(stack, x + width - 8, y, 8, 8, ColorUtil.overCol(ColorUtil.getColor(63, 57, 72, alpha(1)), ColorUtil.getColor(189, 169, 242, alpha(1)), selectedV), Round.of(2 + (selectedV)));
            if (selectedV >= .1f) {
                RenderUtil.Rounded.roundedOutline(stack, x + width - 8, y, 8, 8, selectedV * 1.5f, ColorUtil.multAlpha(ColorUtil.multDark(theme.clientColor(), .6f), alpha(1)), Round.of(1));
            }
        } else if (setting instanceof SliderSetting s) {
            height = 15;
            float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
            selectedV = Mathf.fast(selectedV, Math.max(x + 1, x + ((width) * progress) - .5f), 16);
            if (selected) {
                float step = s.increment <= 0 ? 0.1F : s.increment;
                s.set((float) MathHelper.clamp(
                        Mathf.step((mouseX - (x)) / width * (s.max - s.min) + s.min,
                                step), s.min, s.max));
            }

            float valueWidth = font.getWidth(String.valueOf(s.getValue()), fontSize) + 2;
            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - font.getWidth(String.valueOf(s.getValue()), fontSize), fontSize) &&
                    font.getWidth(s.getName(), fontSize) > width - font.getWidth(String.valueOf(s.getValue()), fontSize) - 2) ? valueWidth - ((width) - nameWidth) : 0;
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - valueWidth, height + 2);
            font.draw(stack, s.getName(), x - offsetV, y, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            font.draw(stack, String.valueOf(s.getValue()), x + width - font.getWidth(String.valueOf(s.getValue()), fontSize), y, interpolate, fontSize);

            RenderUtil.Rounded.smooth(stack, x, y + height - 4, width, 3, ColorUtil.overCol(ColorUtil.getColor(41, 38, 49, alpha(1)), ColorUtil.multAlpha(ColorUtil.multDark(theme.clientColor(), .42f), alpha(.6f)), module.getInterpolate()), Round.of(1.5f));
            RenderUtil.Rounded.smooth(stack, x, y + height - 4, selectedV - x, 3, interpolate, Round.of(1.5f));

        } else if (setting instanceof ModeSetting s) {
            modeWidth = Mathf.fast(modeWidth, maxMode(s), 16);
            selectedV = (selected) ? 16 + s.values.size() * 10 : 12;
            height = Mathf.fast(height, selectedV, 16);

            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - modeWidth, fontSize) &&
                    nameWidth > width - modeWidth - 2 ? modeWidth - (width - nameWidth) + 4 : 0);
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - modeWidth - 2, height);
            font.draw(stack, s.getName(), x - offsetV, centerY + 2, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            RenderUtil.Rounded.roundedWithOutline(stack, x + width - modeWidth, y, modeWidth, height, 1, ColorUtil.multAlpha(interpolate, .1f), ColorUtil.multAlpha(interpolate, .2f), Round.of(2));
            font.draw(stack, s.getValue(), x + width - modeWidth + 2, centerY + 2, interpolate, fontSize);

            if (height >= 12.1f) {
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, y, width, height);
                float offsetY = y + 12;
                for (String string : s.values) {
                    font.draw(stack, string, x + width - modeWidth + 2, offsetY + 5 - font.getHeight(string, fontSize) / 2f, ColorUtil.multAlpha(s.is(string) ? theme.enabledColor() : theme.disabledColor(), alpha(1)), fontSize);
                    offsetY += 10;
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof MultiBooleanSetting s) {
            String values = s.getValue().values().stream().filter(BooleanSetting::getValue).map(BooleanSetting::getName).collect(Collectors.joining(", "));
            modeWidth = Mathf.fast(modeWidth, maxMode(s), 16);
            selectedV = (selected) ? 16 + s.getValues().size() * 10 : 12;
            height = Mathf.fast(height, selectedV, 16);

            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - modeWidth, fontSize) &&
                    nameWidth > width - modeWidth - 2 ? modeWidth - (width - nameWidth) + 4 : 0);
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            if (font.getWidth(values, fontSize) >= modeWidth - 2 - Fonts.SF_MEDIUM.getWidth("Выбрано: ", fontSize)) {
                if (System.currentTimeMillis() - time >= 25L && this.offsetX != 0) {
                    time = System.currentTimeMillis();
                    this.offsetX = this.offsetX <= -font.getWidth(values, fontSize) ? (int) (modeWidth - 2 - font.getWidth("Выбрано: ", fontSize)) : this.offsetX - 1;
                } else if (this.offsetX == 0 && System.currentTimeMillis() - time >= 1000L) {
                    this.offsetX -= 1;
                    time = System.currentTimeMillis();
                }
            } else if (offsetX != 0 && System.currentTimeMillis() - time >= 25L) {
                this.offsetX = (this.offsetX > 0) ? this.offsetX - 1 : this.offsetX + 1;
                time = System.currentTimeMillis();
            }

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - modeWidth - 2, height);
            font.draw(stack, s.getName(), x - offsetV, centerY + 2, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            RenderUtil.Rounded.roundedWithOutline(stack, x + width - modeWidth, y, modeWidth, height, 1, ColorUtil.multAlpha(interpolate, .1f), ColorUtil.multAlpha(interpolate, .2f), Round.of(2));
            font.draw(stack, "Выбрано: ", x + width - modeWidth + 2, centerY + 2, interpolate, fontSize);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + width - modeWidth + 3 + font.getWidth("Выбрано: ", fontSize), y, modeWidth - 2 - font.getWidth("Выбрано: ", fontSize), height);
            font.draw(stack, values, x + width - modeWidth + 2 + font.getWidth("Выбрано: ", fontSize) + this.offsetX, centerY + 2, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f) {
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x + width - modeWidth, y, modeWidth, height);
                float offsetY = y + 12;
                int id = 0;
                for (BooleanSetting bool : s.getValues()) {
                    this.interpolateM.set(id, Mathf.fast(this.interpolateM.get(id), bool.getValue() ? 1 : 0, 16));

                    if (isHovered(mouseX, mouseY, x + width - modeWidth, offsetY, modeWidth, 10) && hoverTime.get(id) == 0L)
                        hoverTime.set(id, System.currentTimeMillis());
                    else if (!isHovered(mouseX, mouseY, x + width - modeWidth, offsetY, modeWidth, 10))
                        hoverTime.set(id, 0L);

                    float offsetXM = (isHovered(mouseX, mouseY, x + width - modeWidth, offsetY, modeWidth, 10) && System.currentTimeMillis() - hoverTime.get(id) >= 500L &&
                            font.getWidth(bool.getName(), fontSize) > modeWidth - 4 ? (font.getWidth(bool.getName(), fontSize) - modeWidth) + 4 : 0);
                    this.offsetXM.set(id, Mathf.fast(this.offsetXM.get(id), offsetXM, 16));
                    font.draw(stack, bool.getName(), x + width - modeWidth + 2 - this.offsetXM.get(id), offsetY + 5 - font.getHeight(bool.getName(), fontSize) / 2f, ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), interpolateM.get(id)), alpha(1)), fontSize);
                    offsetY += 10;
                    id++;
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof ColorSetting s) {
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - 10, height) &&
                    font.getWidth(s.getName(), fontSize) > width - 10) ? 10 - ((width) - Fonts.SF_MEDIUM.getWidth(s.getName(), fontSize)) : 0;
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 34);
            if (colorPicker != null) {
                height = Mathf.fast(height, 8 + (colorPicker.getHeight() + 2) * selectedV, 34);
            } else {
                height = 8;
            }

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - 10, height);
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x - offsetV, centerY, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            RenderUtil.Rounded.roundedRect(stack, x + width - 8, y, 8, 8, ColorUtil.multAlpha(s.getValue(), alpha(1)), Round.of(4));

            if (colorPicker != null && height >= 9f) {
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, y + 8, width, height - 8);
                colorPicker.render(stack, (int) mouseX, (int) mouseY);
                colorPicker.setX(x + width - 90);
                colorPicker.setY(y + 10);
                ScissorExp.unset();
                ScissorExp.pop();
            }
        } else if (setting instanceof BindSetting s) {
            height = 8;
            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, width - modeWidth, fontSize) &&
                    nameWidth > width - modeWidth - 2 ? modeWidth - (width - nameWidth) + 4 : 0);
            offsetV = Mathf.fast(offsetV, offsetX, 16);

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

            modeWidth = Mathf.fast(modeWidth, Fonts.SF_MEDIUM.getWidth(text, fontSize) + 4, 16);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width - modeWidth - 2, height);
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x - offsetV, centerY, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();

            RenderUtil.Rounded.roundedWithOutline(stack, x + width - modeWidth, y, modeWidth, height, 1, ColorUtil.multAlpha(interpolate, .1f), ColorUtil.multAlpha(interpolate, .2f), Round.of(2));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + width - modeWidth + 1, y, modeWidth - 2, height);
            Fonts.SF_MEDIUM.draw(stack, text, x + width - modeWidth + 2, centerY, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
        } else if (setting instanceof StringSetting s) {
            height = 20;
            float nameWidth = font.getWidth(s.getName(), fontSize);
            float offsetX = (isHovered(mouseX, mouseY, x, y, Math.min(width, nameWidth), fontSize) &&
                    nameWidth > width - 2 ? (width - nameWidth) : 0);
            offsetV = Mathf.fast(offsetV, offsetX, 16);

            String text = (selected || !s.getValue().isEmpty()) ? s.getValue() : s.getDescription();
            this.offsetX = Mathf.fast(this.offsetX, modeWidth, 16);

            if (time == 0)
                time = System.currentTimeMillis();

            if (selected && System.currentTimeMillis() - time >= 700L) {
                interpolateM.set(0, interpolateM.get(0) == 1 ? 0F : 1F);
                time = System.currentTimeMillis();
            } else if (!selected)
                interpolateM.set(0, 0F);

            selectedV = Mathf.fast(selectedV, interpolateM.get(0), 12);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y, width, fontSize);
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x + offsetV, y, interpolate, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
            RenderUtil.Rounded.roundedWithOutline(stack, x, y + 10, width, height - 10, 1, ColorUtil.multAlpha(interpolate, .1f), ColorUtil.multAlpha(interpolate, .2f), Round.of(2));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + 1, y, width - 2, height);
            Fonts.SF_MEDIUM.draw(stack, text, x + 2 - this.offsetX, y + 14 - Fonts.SF_MEDIUM.getHeight(text, fontSize) / 2f, interpolate, fontSize);
            RenderUtil.Rounded.roundedRect(stack, x + 4 + Fonts.SF_MEDIUM.getWidth(text, fontSize) - this.offsetX, y + 15 - (selectedV * 3.5f), 1, selectedV * 7, ColorUtil.multAlpha(interpolate, .2f), Round.of(2));
            ScissorExp.unset();
            ScissorExp.pop();
        } else if (setting instanceof ItemSetting s) {
            renderItemSetting(stack, mouseX, mouseY, s);
        } else if (setting instanceof MultiItemSetting s) {
            renderMultiItemSetting(stack, mouseX, mouseY, s);
        } else if (setting instanceof ThemeSetting s) {
            renderThemeSetting(stack, mouseX, mouseY, s);
        }
    }

    private void renderItemSetting(MatrixStack stack, float mouseX, float mouseY, ItemSetting s) {
        Theme theme = Theme.getInstance();
        int interpolate = ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), module.getInterpolate()), alpha(1));

        int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
        Item currentItem = s.getValue();
        int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;

        float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
        float targetHeight = selected ? closedHeight + 150f : closedHeight;
        height = Mathf.fast(height, targetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 12);

        float buttonWidth = 16f;
        float availableNameWidth = width - buttonWidth - 8f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, availableNameWidth, 12f);
        font.draw(stack, s.getName(), x, y + 2, interpolate, fontSize);
        ScissorExp.unset();
        ScissorExp.pop();

        float buttonX = x + width - buttonWidth - 2f;
        float buttonY = y + 1f;
        float buttonHeight = 10f;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha(1)),
                ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alpha(1)),
                Round.of(3f));

        Fonts.icons.draw(stack, "D", buttonX + buttonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                buttonY + buttonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                ColorUtil.multAlpha(theme.textColor(), alpha(1)), 7f);

        if (currentItem != null && currentItem != Items.AIR) {
            float itemsPreviewY = y + 14f;
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
            itemScaleAnimations.clear();
        }

        if (selected) {
            renderItemSelectorPanel(stack, mouseX, mouseY, s, null);
        }
    }

    public void setY(float y) {
        this.y = y;
    }

    private void renderMultiItemSetting(MatrixStack stack, float mouseX, float mouseY, MultiItemSetting s) {
        Theme theme = Theme.getInstance();
        int interpolate = ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), module.getInterpolate()), alpha(1));

        int multiItemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
        int selectedItemCount = s.getValue().size();
        int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / multiItemsPerRow) : 0;

        float multiClosedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
        float multiTargetHeight = selected ? multiClosedHeight + 150f : multiClosedHeight;
        height = Mathf.fast(height, multiTargetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 12);

        float multiButtonWidth = 16f;
        float multiAvailableNameWidth = width - multiButtonWidth - 8f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, multiAvailableNameWidth, 12f);
        font.draw(stack, s.getName(), x, y + 2, interpolate, fontSize);
        ScissorExp.unset();
        ScissorExp.pop();

        float multiButtonX = x + width - multiButtonWidth - 2f;
        float multiButtonY = y + 1f;
        float multiButtonHeight = 10f;
        boolean multiButtonHovered = isHovered(mouseX, mouseY, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, multiButtonX, multiButtonY, multiButtonWidth, multiButtonHeight, 1f,
                ColorUtil.multAlpha(multiButtonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha(1)),
                ColorUtil.multAlpha(multiButtonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alpha(1)),
                Round.of(3f));

        Fonts.icons.draw(stack, "D", multiButtonX + multiButtonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                multiButtonY + multiButtonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                ColorUtil.multAlpha(theme.textColor(), alpha(1)), 7f);

        if (selectedItemCount > 0) {
            float itemsPreviewY = y + 14f;
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, multiClosedHeight - 12f);

            int currentX = (int) (x + 4f);
            int currentY = (int) itemsPreviewY;

            for (Item item : s.getValue()) {
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

            itemScaleAnimations.keySet().removeIf(item -> !s.getValue().contains(item));
            ScissorExp.unset();
            ScissorExp.pop();
        } else {
            itemScaleAnimations.clear();
        }

        if (selected) {
            renderItemSelectorPanel(stack, mouseX, mouseY, null, s);
        }
    }

    private void renderThemeSetting(MatrixStack stack, float mouseX, float mouseY, ThemeSetting s) {
        Theme theme = Theme.getInstance();
        int interpolate = ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(), module.getInterpolate()), alpha(1));

        int presetCount = s.getPresets().size();
        float boxSize = 22f;
        float spacing = 4f;
        int cols = Math.max(1, (int) ((width - 8f) / (boxSize + spacing)));
        int rows = (int) Math.ceil((float) presetCount / cols);

        selectedV = selected ? (18f + rows * (boxSize + spacing)) : 12f;
        height = Mathf.fast(height, selectedV, 12f);
        float animV = Mathf.fast(this.offsetV, selected ? 1 : 0, 15);

        float buttonWidth = 50f;
        float availableNameWidth = width - buttonWidth - 8f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, availableNameWidth, 12f);
        font.draw(stack, s.getName(), x, y + 2, interpolate, fontSize);
        ScissorExp.unset();
        ScissorExp.pop();

        float buttonX = x + width - buttonWidth - 2f;
        float buttonY = y + 1f;
        float buttonHeight = 10f;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha(1)),
                ColorUtil.multAlpha(buttonHovered ? theme.clientColor() : ColorUtil.getColor(100, 100, 100, 60), alpha(1)),
                Round.of(3f));

        String buttonText = selected ? "Закрыть" : "Открыть";
        Fonts.SF_MEDIUM.draw(stack, buttonText, buttonX + buttonWidth / 2f - Fonts.SF_MEDIUM.getWidth(buttonText, 6f) / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_MEDIUM.getHeight(buttonText, 6f) / 2f,
                ColorUtil.multAlpha(theme.textColor(), alpha(1)), 6f);

        if (height >= 12.1f) {
            float panelY = y + 16f;
            float panelHeight = height - 16f;

            RenderUtil.Rounded.roundedWithOutline(stack, x, panelY - 1f, width, panelHeight, 1.5f,
                    ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alpha(1)),
                    ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alpha(1)),
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
                float alphaVal = animV * alpha(1);

                RenderUtil.Rounded.roundedRect(stack, currentX + 1f, currentY + 1f, boxSize, boxSize,
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), alphaVal), Round.of(3f));

                if (preset.isGradient()) {
                    int leftColor = ColorUtil.multAlpha(preset.getColor1(), alphaVal);
                    int rightColor = ColorUtil.multAlpha(preset.getColor2(), alphaVal);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                            leftColor, leftColor, rightColor, rightColor);
                } else {
                    int baseColor = ColorUtil.multAlpha(preset.getColor1(), alphaVal);
                    int darkColor = ColorUtil.multDark(baseColor, 0.7f);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                            baseColor, darkColor, darkColor, baseColor);
                }

                if (isSelected) {
                    int outlineStart = ColorUtil.multAlpha(theme.clientColor(), alphaVal);
                    int outlineEnd = ColorUtil.multDark(outlineStart, 0.5f);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX - 1.5f, currentY - 1.5f,
                            boxSize + 3f, boxSize + 3f, 4f,
                            outlineStart, outlineEnd, outlineEnd, outlineStart);
                }

                if (isHovered && !isSelected) {
                    int hoverColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 80), alphaVal);
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

    private void renderItemSelectorPanel(MatrixStack stack, float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        Theme theme = Theme.getInstance();

        float panelBaseY = y + 14f;
        float selectorHeight = 150f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
            panelBaseY = y + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
            panelBaseY = y + closedHeight;
        }

        float panelHeight = selectorHeight - 4f;
        RenderUtil.Rounded.roundedWithOutline(stack, x, panelBaseY, width, panelHeight, 1.5f,
                ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 30), alpha(1) * selectedV),
                ColorUtil.multAlpha(ColorUtil.getColor(80, 80, 80), alpha(1) * selectedV), Round.of(4f));

        float searchBarY = panelBaseY + 4f;
        float searchBarHeight = 12f;
        RenderUtil.Rounded.roundedRect(stack, x + 4f, searchBarY, width - 8f, searchBarHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), alpha(1) * selectedV), Round.of(3f));

        String searchHint = "Поиск...";
        String textToRender = inputValueString;
        if (isInputMode && System.currentTimeMillis() % 1000 < 500) textToRender += "_";

        Fonts.SF_MEDIUM.draw(stack, textToRender.isEmpty() && !isInputMode ? searchHint : textToRender,
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
                            ColorUtil.multAlpha(theme.clientColor(), 0.5f), Round.of(3f));
                }
                if (isHovered(mouseX, mouseY, itemX, itemY, 16f, 16f)) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1f, itemY - 1f, 18f, 18f,
                            ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), 0.2f), Round.of(3f));
                }
                stack.push();
                stack.translate(itemX + 8f, itemY + 8f, 0);
                stack.scale(selectedV, selectedV, selectedV);
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
            int buttonColor = buttonHovered ? ColorUtil.multBright(theme.clientColor(), 0.8f) : theme.clientColor();

            RenderUtil.Rounded.roundedRect(stack, buttonX, buttonY, buttonWidth, buttonHeight,
                    ColorUtil.multAlpha(buttonColor, alpha(1) * selectedV), Round.of(4f));
            Fonts.SF_MEDIUM.draw(stack, "Сохранить",
                    buttonX + buttonWidth / 2 - Fonts.SF_MEDIUM.getWidth("Сохранить", 7f) / 2,
                    buttonY + buttonHeight / 2 - Fonts.SF_MEDIUM.getHeight("Сохранить", 7f) / 2, -1, 7f);
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, int action) {
        if (!(boolean) setting.getVisible().get()) {
            return;
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

        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            if (action == GLFW.GLFW_PRESS && button == 0) {
                if (setting instanceof BooleanSetting s) {
                    s.set(!s.getValue());
                    SoundUtil.playSound(s.getValue() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
                    return;
                } else if ((setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) && isHovered(mouseX, mouseY, x + width - modeWidth, y, modeWidth, 12)) {
                    SoundUtil.playSound(selected ? "mode_close.wav" : "mode_open.wav", 0.1f);
                    selected = !selected;
                    selectedV = (selected) ? 16 + ((setting instanceof ModeSetting s) ? s.values.size() * 10 : ((MultiBooleanSetting) setting).getValues().size() * 10) : 12;
                    return;
                } else if (setting instanceof ModeSetting s && isHovered(mouseX, mouseY, x + width - modeWidth, y + 12, modeWidth, height - 12)) {
                    float offsetY = y + 12;
                    for (String string : s.values) {
                        if (isHovered(mouseX, mouseY, x + width - modeWidth, offsetY, modeWidth, 10)) {
                            selectedV = 12;
                            selected = false;
                            boolean wasSelected = s.is(string);
                            s.set(string);
                            if (!wasSelected) {
                                SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                            } else {
                                SoundUtil.playSound("mode_pick.wav", 0.1f);
                            }
                            return;
                        }
                        offsetY += 10;
                    }
                    return;
                } else if (setting instanceof MultiBooleanSetting s && isHovered(mouseX, mouseY, x + width - modeWidth, y + 12, modeWidth, height - 12)) {
                    float offsetY = y + 12;
                    for (BooleanSetting bool : s.getValues()) {
                        if (isHovered(mouseX, mouseY, x + width - modeWidth, offsetY, modeWidth, 10)) {
                            boolean wasSelected = bool.getValue();
                            bool.set(!bool.getValue());
                            if (!wasSelected) {
                                SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                            } else {
                                SoundUtil.playSound("mode_pick.wav", 0.1f);
                            }
                            return;
                        }
                        offsetY += 10;
                    }
                    return;
                } else if (setting instanceof ColorSetting s && isHovered(mouseX, mouseY, x + width - 10, y, 10, height)) {
                    selected = !selected;
                    colorPicker = new ColorPicker(x + width + 4, y + 10, 90, 100, s);
                    return;
                } else if (setting instanceof BindSetting) {
                    if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                        return;
                    }
                    SoundUtil.playSound("bind_wait.wav", 0.1f);
                    lastBindClickTime = System.currentTimeMillis();
                    selected = !selected;
                    time = System.currentTimeMillis();
                    return;
                } else if (setting instanceof StringSetting) {
                    selected = !selected;
                    time = System.currentTimeMillis();
                } else if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
                    float buttonWidth = 16f;
                    float buttonX = x + width - buttonWidth - 2f;
                    float buttonY = y + 1f;
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
                } else if (setting instanceof ThemeSetting s) {
                    float buttonWidth = 50f;
                    float buttonX = x + width - buttonWidth - 2f;
                    float buttonY = y + 1f;
                    float buttonHeight = 10f;
                    if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                        selected = !selected;
                        return;
                    }
                }
            }
            if (setting instanceof SliderSetting && button == 0 && action == 1) {
                SoundUtil.playSound("slider.wav", 0.1f);
                selected = true;
                return;
            } else if (setting instanceof ColorSetting && selected && button == 0 && action == 1 && !isHovered(mouseX, mouseY, colorPicker.getX(), colorPicker.getY(), colorPicker.getWidth(), colorPicker.getHeight())) {
                selected = false;
                return;
            } else if (setting instanceof ColorSetting && selected && button == 0) {
                if (action == 1)
                    colorPicker.mouseClicked(mouseX, mouseY, button);
                else
                    colorPicker.mouseReleased(mouseX, mouseY, button);
                return;
            }
        }

        if (setting instanceof ItemSetting itemSetting && selected && action == 0) {
            handleItemSelectorMouse(mouseX, mouseY, action, itemSetting, null);
        } else if (setting instanceof MultiItemSetting multiItemSetting && selected && action == 0) {
            handleItemSelectorMouse(mouseX, mouseY, action, null, multiItemSetting);
        } else if (setting instanceof ThemeSetting s && selected && action == 0 &&
                isHovered(mouseX, mouseY, x, y + 16f, width, height - 16f)) {
            float panelY = y + 16f;
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

        if (setting instanceof SliderSetting && action == 0) {
            selected = false;
        }
    }

    private void handleItemSelectorMouse(float mouseX, float mouseY, int action, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float panelBaseY = y + 14f;
        float selectorHeight = 150f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
            panelBaseY = y + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
            panelBaseY = y + closedHeight;
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

    @Override
    void key(int key) {
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

        if (selected && setting instanceof BindSetting s) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            selected = false;
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE) {
                s.set(-1);
                SoundUtil.playSound("bind_remove.wav", 0.1f);
            } else {
                s.set(key);
                SoundUtil.playSound("bind_succes.wav", 0.3f);
            }
            lastBindClickTime = System.currentTimeMillis();
        } else if (selected && setting instanceof StringSetting s) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER)
                selected = false;

            switch (key) {
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    s.set(s.getValue().isEmpty() ? "" : s.getValue().substring(0, s.getValue().length() - 1));
                    if (x - modeWidth + Fonts.SF_MEDIUM.getWidth(s.getValue(), fontSize) <= x + 4)
                        modeWidth = s.getValue().isEmpty() ? 0 : modeWidth - Fonts.SF_MEDIUM.getWidth(s.getValue().substring(s.getValue().length() - 1), fontSize);
                }
                case GLFW.GLFW_KEY_LEFT -> modeWidth = Math.max(0, modeWidth - 5);
                case GLFW.GLFW_KEY_RIGHT ->
                        modeWidth = (Fonts.SF_MEDIUM.getWidth(s.getValue(), fontSize) + 6 - modeWidth) >= width ? modeWidth + 5 : modeWidth;
                case GLFW.GLFW_KEY_C -> {
                    if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                        new ClipboardHelper().setClipboardString(Minecraft.getInstance().getMainWindow().getHandle(), s.getValue());
                    }
                }
                case GLFW.GLFW_KEY_V -> {
                    if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
                        s.set(s.getValue() + new ClipboardHelper().getClipboardString(Minecraft.getInstance().getMainWindow().getHandle(), null));
                }
            }
        }
    }

    @Override
    void charTyped(char typed) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            inputValueString += typed;
            updateItemFilter();
            return;
        }

        if (selected && setting instanceof StringSetting s) {
            s.set(s.getValue() + typed);
            if (Fonts.SF_MEDIUM.getWidth(s.getValue(), fontSize) + 6 - modeWidth >= width)
                modeWidth += Fonts.SF_MEDIUM.getWidth(String.valueOf(typed), fontSize);
        }
    }

    public void scroll(double d) {
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            this.itemScrollY += (float) (d * 10f);
        }
    }

    public boolean isItemSelectorHovered(float mouseX, float mouseY) {
        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            float panelBaseY = y + 14f;
            float selectorHeight = 150f;

            if (setting instanceof ItemSetting s) {
                Item currentItem = s.getValue();
                int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
                float closedHeight = 12f + (previewRows > 0 ? (previewRows * 18f) : 0);
                panelBaseY = y + closedHeight;
            } else if (setting instanceof MultiItemSetting s) {
                int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
                int selectedItemCount = s.getValue().size();
                int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
                float closedHeight = 12f + (requiredRows > 0 ? (requiredRows * 18f) : 0);
                panelBaseY = y + closedHeight;
            }

            float panelHeight = selectorHeight - 4f;
            return isHovered(mouseX, mouseY, x, panelBaseY, width, panelHeight);
        }
        return false;
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

    private float maxMode(ModeSetting s) {
        float max = font.getWidth(s.getValue(), fontSize);
        if (selected) {
            for (String string : s.values) {
                max = Math.max(max, font.getWidth(string, fontSize));
            }
        }
        return max + 4;
    }

    private float maxMode(MultiBooleanSetting s) {
        String values = s.getValue().values().stream().filter(BooleanSetting::getValue).map(BooleanSetting::getName).collect(Collectors.joining(", "));
        float max = Math.min(60, font.getWidth("Выбрано: " + values, fontSize));
        if (selected) {
            for (BooleanSetting bool : s.getValues()) {
                max = Math.max(max, font.getWidth(bool.getName(), fontSize));
            }
        }
        return Math.min(90, max);
    }

    private float alpha(float alpha) {
        return module.alpha(alpha);
    }
}