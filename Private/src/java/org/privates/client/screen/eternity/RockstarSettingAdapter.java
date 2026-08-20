package org.privates.client.screen.eternity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.managers.module.impl.render.ClickGuiSettings;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.newgui.ColorPicker;
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
@Accessors(fluent = true)
public class RockstarSettingAdapter {
    private float x, y, width, height;
    private final org.privates.client.managers.module.settings.Setting setting;
    private boolean selected;
    private float selectedV;
    private float delta;
    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;
    private float appearAnim = 0f;
    private ColorPicker colorPicker;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private String inputValueString = "";
    private boolean isInputMode = false;
    private float itemScrollY = 0;
    private float itemScrollYV = 0;
    private Map<Item, Float> itemScaleAnimations = new HashMap<>();

    public RockstarSettingAdapter(org.privates.client.managers.module.settings.Setting setting, float x, float y) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = 150;

        if (setting instanceof BooleanSetting) {
            height = 24;
        } else if (setting instanceof SliderSetting) {
            height = 34;
        } else if (setting instanceof ColorSetting) {
            height = 18f;
        } else if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            height = 24;
            allItems = Registry.ITEM.stream().filter(item -> item != Items.AIR).collect(Collectors.toList());
            filteredItems.addAll(allItems);
        } else if (setting instanceof ThemeSetting) {
            height = 18f;
        } else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) {
            calculateModeHeight();
        } else {
            height = 24;
        }
    }

    private void calculateModeHeight() {
        float titleHeight = 24f;
        float contentStartY = 6f;
        float maxLineWidth = width - 14f;
        float currentLineWidth = 0f;
        float currentY = titleHeight + contentStartY;
        float pillSpacing = 4f;

        if (setting instanceof ModeSetting s) {
            for (String string : s.values) {
                float textWidth = Fonts.SF_BOLD.getWidth(string, 7.84f);
                float pillWidth = textWidth + 8f;

                if (currentLineWidth + pillWidth > maxLineWidth && currentLineWidth > 0) {
                    currentLineWidth = 0f;
                    currentY += 12f;
                }

                currentLineWidth += pillWidth + pillSpacing;
            }
            if (currentLineWidth > 0) {
                currentY += 12f;
            }
        } else if (setting instanceof MultiBooleanSetting s) {
            for (BooleanSetting bs : s.getValues()) {
                float textWidth = Fonts.SF_BOLD.getWidth(bs.getName(), 7.84f);
                float pillWidth = textWidth + 8f;

                if (currentLineWidth + pillWidth > maxLineWidth && currentLineWidth > 0) {
                    currentLineWidth = 0f;
                    currentY += 12f;
                }

                currentLineWidth += pillWidth + pillSpacing;
            }
            if (currentLineWidth > 0) {
                currentY += 12f;
            }
        }

        height = currentY + 6f;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {
        if (!(boolean) setting.getVisible().get()) {
            height = 0;
            return;
        }

        appearAnim = Mathf.fast(appearAnim, 1f, 12);

        stack.push();
        stack.translate(x + width / 2f, y + delta + height / 2f, 0);
        stack.scale(appearAnim, appearAnim, 1);
        stack.translate(-(x + width / 2f), -(y + delta + height / 2f), 0);

        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();
        float customRadius = guiSettings.getCustomRadius();
        float outlineWidth = guiSettings.getSettingsOutlineWidth();
        int outlineColor = guiSettings.getSettingsOutlineColor();

        if (outlineWidth > 0 && outlineColor != 0) {
            RenderUtil.Rounded.roundedWithOutline(stack, x, y + delta, width, height, outlineWidth,
                    ColorUtil.multAlpha(guiSettings.getSettingsBgColor(), alpha * appearAnim),
                    ColorUtil.multAlpha(outlineColor, alpha * appearAnim),
                    Round.of(customRadius));
        } else {
            RenderUtil.Rounded.roundedRect(stack, x, y + delta, width, height,
                    ColorUtil.multAlpha(guiSettings.getSettingsBgColor(), alpha * appearAnim),
                    Round.of(customRadius));
        }

        if (setting instanceof ColorSetting s) {
            renderColorSetting(stack, s, mouseX, mouseY, alpha);
        } else if (setting instanceof ItemSetting s) {
            renderItemSetting(stack, s, mouseX, mouseY, alpha);
        } else if (setting instanceof MultiItemSetting s) {
            renderMultiItemSetting(stack, s, mouseX, mouseY, alpha);
        } else if (setting instanceof ThemeSetting s) {
            renderThemeSetting(stack, s, mouseX, mouseY, alpha);
        } else {
            float nameY = y + delta + 12f - Fonts.SF_BOLD.getHeight(setting.getName(), 7.84f) / 2f;
            drawScrollingName(stack, setting.getName(), x + 7, nameY, width - 40,
                    ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7.84f);

            if (setting instanceof BooleanSetting s) {
                renderBooleanSetting(stack, s, alpha);
            } else if (setting instanceof SliderSetting s) {
                renderSliderSetting(stack, s, mouseX, alpha);
            } else if (setting instanceof ModeSetting s) {
                renderModeSetting(stack, s, mouseX, mouseY, alpha);
            } else if (setting instanceof MultiBooleanSetting s) {
                renderMultiBooleanSetting(stack, s, mouseX, mouseY, alpha);
            }
        }

        stack.pop();
    }

    private void drawScrollingName(MatrixStack stack, String name, float x, float y, float availableWidth, int color, float size) {
        float nameWidth = Fonts.SF_BOLD.getWidth(name, size);

        if (nameWidth > availableWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.3f;
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

    private void renderBooleanSetting(MatrixStack stack, BooleanSetting s, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();
        selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 16);

        RenderUtil.Rounded.roundedRect(stack, x + width - 18, y + delta + 12f - 5, 10, 10,
                ColorUtil.multAlpha(ColorUtil.overCol(guiSettings.getAccentColor(), ColorUtil.getColor(24, 26, 30), 1f - selectedV), alpha * appearAnim),
                Round.of(3));

        GlStateManager.pushMatrix();
        GlStateManager.translatef((x + width - 13), (y + delta + 12f), 1);
        if (selectedV <= .95f) {
            GlStateManager.scalef(1 - selectedV, 1 - selectedV, 1 - selectedV);
            GlStateManager.translatef(-(x + width - 13), -(y + delta + 12f), 1);
            Fonts.ROCKSTAR.draw(stack, "i", x + width - 13 - Fonts.ROCKSTAR.getWidth("i", 6f) / 2f, y + delta + 12f - Fonts.ROCKSTAR.getHeight("i", 6f) / 2f - .5f, ColorUtil.getColor(189, 191, 195, (int) ((1 - selectedV) * alpha * appearAnim * 255)), 6f);
        }
        if (selectedV >= .05f) {
            GlStateManager.scalef(selectedV, selectedV, selectedV);
            GlStateManager.translatef(-(x + width - 13), -(y + delta + 12f), 1);
            Fonts.ROCKSTAR.draw(stack, "j", x + width - 13 - Fonts.ROCKSTAR.getWidth("j", 6f) / 2f, y + delta + 12f - Fonts.ROCKSTAR.getHeight("j", 6f) / 2f, ColorUtil.getColor(189, 191, 195, (int) (selectedV * alpha * appearAnim * 255)), 6f);
        }
        GlStateManager.popMatrix();
    }

    private void renderSliderSetting(MatrixStack stack, SliderSetting s, float mouseX, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();
        float offsetY = y + delta + height - 13;

        RenderUtil.Rounded.roundedRect(stack, x + 7, offsetY, width - 14, 5,
                ColorUtil.multAlpha(ColorUtil.getColor(27, 29, 35), alpha * appearAnim), Round.of(3));

        float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
        selectedV = Mathf.fast(selectedV, Math.max(3f, ((width - 14) * progress)), 16);

        int accentCol = guiSettings.getAccentColor();
        RenderUtil.Rounded.roundedRect(stack, x + 7, offsetY, selectedV, 5,
                ColorUtil.multAlpha(ColorUtil.multDark(accentCol, 0.3f), alpha * appearAnim),
                ColorUtil.multAlpha(ColorUtil.multDark(accentCol, 0.3f), alpha * appearAnim),
                ColorUtil.multAlpha(accentCol, alpha * appearAnim),
                ColorUtil.multAlpha(accentCol, alpha * appearAnim),
                Round.of(3));
        RenderUtil.Rounded.roundedRect(stack, x + 5f + selectedV, offsetY - 1f, 6, 7, ColorUtil.multAlpha(accentCol, alpha * appearAnim), Round.of(4));

        String text = String.valueOf(s.getValue());
        float textWidth = Fonts.SF_BOLD.getWidth(text, 7.84f) + 4;
        RenderUtil.Rounded.roundedRect(stack, x + width - textWidth - 7, y + delta + 7f, textWidth, Fonts.SF_BOLD.getHeight(text, 7.84f) + 3, ColorUtil.getColor(27, 29, 33, (int) (alpha * appearAnim * 255)), Round.of(3));
        Fonts.SF_BOLD.draw(stack, text, x + width - textWidth - 5f, y + delta + 8.5f, ColorUtil.getColor(138, 140, 144, (int) (alpha * appearAnim * 255)), 7.84f);

        if (selected) {
            float step = s.increment <= 0 ? 0.1F : s.increment;
            s.set((float) MathHelper.clamp(
                    Mathf.step((mouseX - (x + 7)) / (width - 14) * (s.max - s.min) + s.min,
                            step), s.min, s.max));
        }
    }

    private void renderModeSetting(MatrixStack stack, ModeSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();
        float startY = y + delta + 24f + 6f;
        float currentX = x + 7f;
        float currentY = startY;
        float maxLineWidth = width - 14f;
        float pillSpacing = 4f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 5f, startY - 2f, width - 10f, height - 30f);

        for (String string : s.values) {
            float textWidth = Fonts.SF_BOLD.getWidth(string, 7.84f);
            float pillWidth = textWidth + 8f;

            if (currentX + pillWidth > x + 7f + maxLineWidth && currentX != x + 7f) {
                currentX = x + 7f;
                currentY += 12f;
            }

            float pillCenterY = currentY + 5f;
            float textY = pillCenterY - Fonts.SF_BOLD.getHeight(string, 7.84f) / 2f;

            RenderUtil.Rounded.roundedRect(stack, currentX, currentY, pillWidth, 10,
                    s.is(string) ? ColorUtil.multAlpha(guiSettings.getAccentColor(), alpha * appearAnim) : ColorUtil.getColor(26, 28, 32, (int) (alpha * appearAnim * 255)),
                    Round.of(3));

            Fonts.SF_BOLD.draw(stack, string, currentX + 4, textY,
                    ColorUtil.getColor(254, 254, 254, (int) ((.36f + (s.is(string) ? .56f : 0f)) * alpha * appearAnim * 255)), 7.84f);

            currentX += pillWidth + pillSpacing;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderMultiBooleanSetting(MatrixStack stack, MultiBooleanSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();
        float startY = y + delta + 24f + 6f;
        float currentX = x + 7f;
        float currentY = startY;
        float maxLineWidth = width - 14f;
        float pillSpacing = 4f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 5f, startY - 2f, width - 10f, height - 30f);

        for (BooleanSetting string : s.getValues()) {
            float textWidth = Fonts.SF_BOLD.getWidth(string.getName(), 7.84f);
            float pillWidth = textWidth + 8f;

            if (currentX + pillWidth > x + 7f + maxLineWidth && currentX != x + 7f) {
                currentX = x + 7f;
                currentY += 12f;
            }

            float pillCenterY = currentY + 5f;
            float textY = pillCenterY - Fonts.SF_BOLD.getHeight(string.getName(), 7.84f) / 2f;

            RenderUtil.Rounded.roundedRect(stack, currentX, currentY, pillWidth, 10,
                    string.getValue() ? ColorUtil.multAlpha(guiSettings.getAccentColor(), alpha * appearAnim) : ColorUtil.getColor(26, 28, 32, (int) (alpha * appearAnim * 255)),
                    Round.of(3));

            Fonts.SF_BOLD.draw(stack, string.getName(), currentX + 4, textY,
                    ColorUtil.getColor(254, 254, 254, (int) ((.36f + (string.getValue() ? .56f : 0f)) * alpha * appearAnim * 255)), 7.84f);

            currentX += pillWidth + pillSpacing;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderColorSetting(MatrixStack stack, ColorSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();

        float targetHeight = selected ? 18f + 95f : 18f;
        height = Mathf.fast(height, targetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 15);

        float[] c = ColorUtil.getRGBAf(s.getValue());
        float controlWidth = 18f;
        float availableNameWidth = width - controlWidth - 14f;
        float nameY = y + delta + 9f - Fonts.SF_BOLD.getHeight(s.getName(), 7.84f) / 2f;
        drawScrollingName(stack, s.getName(), x + 7, nameY, availableNameWidth, ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7.84f);

        RenderUtil.Rounded.roundedRect(stack, x + width - 14f, y + delta + 4f, 12f, 10f,
                ColorUtil.multAlpha(s.getValue(), c[3] * alpha * appearAnim), Round.of(3));
        RenderUtil.Rounded.roundedOutline(stack, x + width - 14f, y + delta + 4f, 12f, 10f, 1f,
                ColorUtil.multAlpha(s.getValue(), (c[3] + .2f) * alpha * appearAnim), Round.of(3));

        if (selected || selectedV > 0.01f) {
            if (colorPicker == null) {
                colorPicker = new ColorPicker(x, y + delta + 20f, width, 90f, s);
            }
            colorPicker.setX(x);
            colorPicker.setY(y + delta + 20f);
            colorPicker.setWidth(width);
            colorPicker.setHeight(90f);

            if (selectedV > 0.01f) {
                stack.push();
                stack.translate(x + width / 2f, y + delta + 20f + 45f, 0);
                stack.scale(1, selectedV, 1);
                stack.translate(-(x + width / 2f), -(y + delta + 20f + 45f), 0);
                colorPicker.render(stack, (int) mouseX, (int) mouseY);
                stack.pop();
            }
        } else {
            colorPicker = null;
        }
    }

    private void renderItemSetting(MatrixStack stack, ItemSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();

        Item currentItem = s.getValue();
        float previewHeight = currentItem != null && currentItem != Items.AIR ? 20f : 0f;
        float targetHeight = selected ? 24f + previewHeight + 150f : 24f + previewHeight;
        height = Mathf.fast(height, targetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 12);

        float buttonWidth = 18f;
        float availableNameWidth = width - buttonWidth - 14f;
        float nameY = y + delta + 12f - Fonts.SF_BOLD.getHeight(s.getName(), 7.84f) / 2f;
        drawScrollingName(stack, s.getName(), x + 7, nameY, availableNameWidth, ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7.84f);

        float buttonX = x + width - buttonWidth - 2f;
        float buttonY = y + delta + 7f;
        float buttonHeight = 10f;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(guiSettings.getAccentColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha * appearAnim),
                ColorUtil.multAlpha(buttonHovered ? guiSettings.getAccentColor() : ColorUtil.getColor(100, 100, 100, 60), alpha * appearAnim),
                Round.of(3f));

        Fonts.icons.draw(stack, "D", buttonX + buttonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                buttonY + buttonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7f);

        if (currentItem != null && currentItem != Items.AIR) {
            float itemsPreviewY = y + delta + 26f;
            float currentScale = itemScaleAnimations.getOrDefault(currentItem, 0f);
            currentScale = Mathf.fast(currentScale, 1f, 14f);
            itemScaleAnimations.put(currentItem, currentScale);

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, previewHeight);

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
        }

        if (selected && selectedV > 0.01f) {
            renderItemSelectorPanel(stack, mouseX, mouseY, s, null, alpha);
        }
    }

    private void renderMultiItemSetting(MatrixStack stack, MultiItemSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();

        int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
        int selectedItemCount = s.getValue().size();
        int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
        float previewHeight = requiredRows > 0 ? (requiredRows * 20f) : 0f;

        float targetHeight = selected ? 24f + previewHeight + 150f : 24f + previewHeight;
        height = Mathf.fast(height, targetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 12);

        float buttonWidth = 18f;
        float availableNameWidth = width - buttonWidth - 14f;
        float nameY = y + delta + 12f - Fonts.SF_BOLD.getHeight(s.getName(), 7.84f) / 2f;
        drawScrollingName(stack, s.getName(), x + 7, nameY, availableNameWidth, ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7.84f);

        float buttonX = x + width - buttonWidth - 2f;
        float buttonY = y + delta + 7f;
        float buttonHeight = 10f;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(guiSettings.getAccentColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha * appearAnim),
                ColorUtil.multAlpha(buttonHovered ? guiSettings.getAccentColor() : ColorUtil.getColor(100, 100, 100, 60), alpha * appearAnim),
                Round.of(3f));

        Fonts.icons.draw(stack, "D", buttonX + buttonWidth / 2f - Fonts.icons.getWidth("D", 7f) / 2f,
                buttonY + buttonHeight / 2f - Fonts.icons.getHeight("D", 7f) / 2f,
                ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7f);

        if (selectedItemCount > 0) {
            float itemsPreviewY = y + delta + 26f;
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, itemsPreviewY, width, previewHeight);

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
                    currentY += 20;
                }
            }

            itemScaleAnimations.keySet().removeIf(item -> !s.getValue().contains(item));

            ScissorExp.unset();
            ScissorExp.pop();
        }

        if (selected && selectedV > 0.01f) {
            renderItemSelectorPanel(stack, mouseX, mouseY, null, s, alpha);
        }
    }

    private void renderItemSelectorPanel(MatrixStack stack, float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();

        float panelBaseY = y + delta + 24f;
        float selectorHeight = 150f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 24f + (previewRows > 0 ? 20f : 0);
            panelBaseY = y + delta + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 24f + (requiredRows > 0 ? (requiredRows * 20f) : 0);
            panelBaseY = y + delta + closedHeight;
        }

        float panelHeight = selectorHeight - 4f;

        stack.push();
        stack.translate(x + width / 2f, panelBaseY + panelHeight / 2f, 0);
        stack.scale(1, selectedV, 1);
        stack.translate(-(x + width / 2f), -(panelBaseY + panelHeight / 2f), 0);

        RenderUtil.Rounded.roundedWithOutline(stack, x, panelBaseY, width, panelHeight, 1.5f,
                ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 30), alpha * selectedV * appearAnim),
                ColorUtil.multAlpha(ColorUtil.getColor(80, 80, 80), alpha * selectedV * appearAnim), Round.of(4f));

        float searchBarY = panelBaseY + 4f;
        float searchBarHeight = 14f;
        RenderUtil.Rounded.roundedRect(stack, x + 4f, searchBarY, width - 8f, searchBarHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), alpha * selectedV * appearAnim), Round.of(3f));

        String searchHint = "Поиск...";
        String textToRender = inputValueString;
        if (isInputMode && System.currentTimeMillis() % 1000 < 500) textToRender += "_";

        Fonts.SF_BOLD.draw(stack, textToRender.isEmpty() && !isInputMode ? searchHint : textToRender,
                x + 8f, searchBarY + 3.5f,
                textToRender.isEmpty() && !isInputMode ? ColorUtil.getColor(120, 120, 120) : -1, 7f);

        float itemsListStartY = searchBarY + searchBarHeight + 4f;
        float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 22f : 4f);

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
                            ColorUtil.multAlpha(guiSettings.getAccentColor(), 0.5f * selectedV), Round.of(3f));
                }
                if (isHovered(mouseX, mouseY, itemX, itemY, 16f, 16f)) {
                    RenderUtil.Rounded.roundedRect(stack, itemX - 1f, itemY - 1f, 18f, 18f,
                            ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), 0.2f * selectedV), Round.of(3f));
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
            float buttonHeight = 16f;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + panelHeight - buttonHeight - 3f;

            boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
            int buttonColor = buttonHovered ? ColorUtil.multBright(guiSettings.getAccentColor(), 0.8f) : guiSettings.getAccentColor();

            RenderUtil.Rounded.roundedRect(stack, buttonX, buttonY, buttonWidth, buttonHeight,
                    ColorUtil.multAlpha(buttonColor, alpha * selectedV * appearAnim), Round.of(4f));
            Fonts.SF_BOLD.draw(stack, "Сохранить",
                    buttonX + buttonWidth / 2 - Fonts.SF_BOLD.getWidth("Сохранить", 7f) / 2,
                    buttonY + buttonHeight / 2 - Fonts.SF_BOLD.getHeight("Сохранить", 7f) / 2, -1, 7f);
        }

        stack.pop();
    }

    private void renderThemeSetting(MatrixStack stack, ThemeSetting s, float mouseX, float mouseY, float alpha) {
        ClickGuiSettings guiSettings = ClickGuiSettings.getInstance();

        int presetCount = s.getPresets().size();
        float boxSize = 24f;
        float spacing = 4f;
        int cols = Math.max(1, (int) ((width - 8f) / (boxSize + spacing)));
        int rows = (int) Math.ceil((float) presetCount / cols);

        float targetHeight = selected ? (22f + rows * (boxSize + spacing)) : 18f;
        height = Mathf.fast(height, targetHeight, 12f);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 15);

        float buttonWidth = 52f;
        float availableNameWidth = width - buttonWidth - 14f;
        float nameY = y + delta + 9f - Fonts.SF_BOLD.getHeight(s.getName(), 7.84f) / 2f;
        drawScrollingName(stack, s.getName(), x + 7, nameY, availableNameWidth, ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 7.84f);

        float buttonX = x + width - buttonWidth - 2f;
        float buttonY = y + delta + 4f;
        float buttonHeight = 10f;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        RenderUtil.Rounded.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, 1f,
                ColorUtil.multAlpha(buttonHovered ? ColorUtil.multBright(guiSettings.getAccentColor(), 0.8f) : ColorUtil.getColor(40, 40, 40, 60), alpha * appearAnim),
                ColorUtil.multAlpha(buttonHovered ? guiSettings.getAccentColor() : ColorUtil.getColor(100, 100, 100, 60), alpha * appearAnim),
                Round.of(3f));

        String buttonText = selected ? "Закрыть" : "Открыть";
        Fonts.SF_BOLD.draw(stack, buttonText, buttonX + buttonWidth / 2f - Fonts.SF_BOLD.getWidth(buttonText, 6f) / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(buttonText, 6f) / 2f,
                ColorUtil.multAlpha(guiSettings.getTextColor(), alpha * appearAnim), 6f);

        if (height >= 18.1f) {
            float panelY = y + delta + 20f;
            float panelHeight = height - 20f;

            RenderUtil.Rounded.roundedWithOutline(stack, x, panelY - 1f, width, panelHeight, 1.5f,
                    ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, 60), alpha * selectedV * appearAnim),
                    ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100, 60), alpha * selectedV * appearAnim),
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

                RenderUtil.Rounded.roundedRect(stack, currentX + 1f, currentY + 1f, boxSize, boxSize,
                        ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 20), selectedV * alpha * appearAnim), Round.of(3f));

                if (preset.isGradient()) {
                    int leftColor = ColorUtil.multAlpha(preset.getColor1(), selectedV * alpha * appearAnim);
                    int rightColor = ColorUtil.multAlpha(preset.getColor2(), selectedV * alpha * appearAnim);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                            leftColor, leftColor, rightColor, rightColor);
                } else {
                    int baseColor = ColorUtil.multAlpha(preset.getColor1(), selectedV * alpha * appearAnim);
                    int darkColor = ColorUtil.multDark(baseColor, 0.7f);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3f,
                            baseColor, darkColor, darkColor, baseColor);
                }

                if (isSelected) {
                    int outlineStart = ColorUtil.multAlpha(guiSettings.getAccentColor(), selectedV * alpha * appearAnim);
                    int outlineEnd = ColorUtil.multDark(outlineStart, 0.5f);
                    RenderUtil.Rounded.drawGradientRound(stack, currentX - 1.5f, currentY - 1.5f,
                            boxSize + 3f, boxSize + 3f, 4f,
                            outlineStart, outlineEnd, outlineEnd, outlineStart);
                }

                if (isHovered && !isSelected) {
                    int hoverColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 80), selectedV * alpha * appearAnim);
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

    public void mouse(float mouseX, float mouseY, int button, int action) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        float finalY = y + delta;

        if (setting instanceof ColorSetting s && selected && colorPicker != null) {
            if (action == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                if (isHovered(mouseX, mouseY, x, finalY + 20f, width, 90f)) {
                    colorPicker.mouseClicked(mouseX, mouseY, button);
                    return;
                } else {
                    selected = false;
                    return;
                }
            }
            if (action == org.lwjgl.glfw.GLFW.GLFW_RELEASE) {
                colorPicker.mouseReleased(mouseX, mouseY, button);
                return;
            }
        }

        if (isHovered(mouseX, mouseY, x, finalY, width, height)) {
            if (action == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                if (button == 0) {
                    if (setting instanceof BooleanSetting s) {
                        s.set(!s.getValue());
                        SoundUtil.playSound(s.getValue() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
                    } else if (setting instanceof SliderSetting) {
                        selected = true;
                        SoundUtil.playSound("slider.wav", 0.1f);
                    } else if (setting instanceof ColorSetting) {
                        selected = !selected;
                        if (colorPicker == null && selected) {
                            colorPicker = new ColorPicker(x, finalY + 20f, width, 90f, (ColorSetting) setting);
                        }
                    } else if (setting instanceof ThemeSetting s) {
                        float buttonWidth = 52f;
                        float buttonX = x + width - buttonWidth - 2f;
                        float buttonY = finalY + 4f;
                        float buttonHeight = 10f;

                        if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                            selected = !selected;
                            SoundUtil.playSound(selected ? "mode_open.wav" : "mode_close.wav", 0.1f);
                        } else if (selected) {
                            handleThemeSettingClick(s, mouseX, mouseY, finalY);
                        }
                    } else if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
                        float buttonWidth = 18f;
                        float buttonX = x + width - buttonWidth - 2f;
                        float buttonY = finalY + 7f;
                        float buttonHeight = 10f;

                        if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                            selected = !selected;
                            SoundUtil.playSound(selected ? "mode_open.wav" : "mode_close.wav", 0.1f);
                            if (selected) {
                                isInputMode = false;
                                inputValueString = "";
                                filteredItems.clear();
                                filteredItems.addAll(allItems);
                                itemScrollY = 0;
                            }
                        } else if (selected) {
                            if (setting instanceof ItemSetting itemSetting) {
                                handleItemSelectorMouse(mouseX, mouseY, itemSetting, null);
                            } else if (setting instanceof MultiItemSetting multiItemSetting) {
                                handleItemSelectorMouse(mouseX, mouseY, null, multiItemSetting);
                            }
                        }
                    } else if (setting instanceof ModeSetting s) {
                        handleModeSettingClick(s, mouseX, mouseY, finalY);
                    } else if (setting instanceof MultiBooleanSetting s) {
                        handleMultiBooleanSettingClick(s, mouseX, mouseY, finalY);
                    }
                }
            } else if (action == org.lwjgl.glfw.GLFW.GLFW_RELEASE) {
                if (setting instanceof SliderSetting) {
                    selected = false;
                }
            }
        }
    }

    private void handleModeSettingClick(ModeSetting s, float mouseX, float mouseY, float finalY) {
        float startY = finalY + 24f + 6f;
        float currentX = x + 7f;
        float currentY = startY;
        float maxLineWidth = width - 14f;
        float pillSpacing = 4f;

        for (String string : s.values) {
            float textWidth = Fonts.SF_BOLD.getWidth(string, 7.84f);
            float pillWidth = textWidth + 8f;

            if (currentX + pillWidth > x + 7f + maxLineWidth && currentX != x + 7f) {
                currentX = x + 7f;
                currentY += 12f;
            }

            if (isHovered(mouseX, mouseY, currentX, currentY, pillWidth, 10)) {
                s.set(string);
                SoundUtil.playSound(s.is(string) ? "mode_pick.wav" : "mode_pick_on.wav", 0.1f);
                break;
            }

            currentX += pillWidth + pillSpacing;
        }
    }

    private void handleMultiBooleanSettingClick(MultiBooleanSetting s, float mouseX, float mouseY, float finalY) {
        float startY = finalY + 24f + 6f;
        float currentX = x + 7f;
        float currentY = startY;
        float maxLineWidth = width - 14f;
        float pillSpacing = 4f;

        for (BooleanSetting string : s.getValues()) {
            float textWidth = Fonts.SF_BOLD.getWidth(string.getName(), 7.84f);
            float pillWidth = textWidth + 8f;

            if (currentX + pillWidth > x + 7f + maxLineWidth && currentX != x + 7f) {
                currentX = x + 7f;
                currentY += 12f;
            }

            if (isHovered(mouseX, mouseY, currentX, currentY, pillWidth, 10)) {
                boolean wasSelected = string.getValue();
                string.set(!string.getValue());
                SoundUtil.playSound(wasSelected ? "mode_pick.wav" : "mode_pick_on.wav", 0.1f);
                break;
            }

            currentX += pillWidth + pillSpacing;
        }
    }

    private void handleThemeSettingClick(ThemeSetting s, float mouseX, float mouseY, float finalY) {
        float panelY = finalY + 20f;
        float boxSize = 24f;
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

    private void handleItemSelectorMouse(float mouseX, float mouseY, ItemSetting singleSetting, MultiItemSetting multiSetting) {
        float panelBaseY = y + delta + 24f;

        if (singleSetting != null) {
            Item currentItem = singleSetting.getValue();
            int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
            float closedHeight = 24f + (previewRows > 0 ? 20f : 0);
            panelBaseY = y + delta + closedHeight;
        } else if (multiSetting != null) {
            int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
            int selectedItemCount = multiSetting.getValue().size();
            int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
            float closedHeight = 24f + (requiredRows > 0 ? (requiredRows * 20f) : 0);
            panelBaseY = y + delta + closedHeight;
        }

        float selectorHeight = 150f;
        float panelHeight = selectorHeight - 4f;
        float searchBarY = panelBaseY + 4f;
        float searchBarHeight = 14f;

        if (isHovered(mouseX, mouseY, x + 4f, searchBarY, width - 8f, searchBarHeight)) {
            isInputMode = true;
            return;
        }

        float itemsListStartY = searchBarY + searchBarHeight + 4f;
        float itemsListHeight = panelHeight - (itemsListStartY - panelBaseY) - (multiSetting != null ? 22f : 4f);

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
            float buttonHeight = 16f;
            float buttonX = x + width / 2 - buttonWidth / 2;
            float buttonY = panelBaseY + panelHeight - buttonHeight - 3f;
            if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                selected = false;
                isInputMode = false;
                inputValueString = "";
                SoundUtil.playSound("mode_close.wav", 0.1f);
            }
        }
    }

    public void key(int key) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                isInputMode = false;
                inputValueString = "";
                updateItemFilter();
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                if (!inputValueString.isEmpty()) {
                    inputValueString = inputValueString.substring(0, inputValueString.length() - 1);
                    updateItemFilter();
                }
            }
        }
    }

    public void charTyped(char typed) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (isInputMode && (setting instanceof ItemSetting || setting instanceof MultiItemSetting)) {
            inputValueString += typed;
            updateItemFilter();
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

    public void scroll(double delta) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            this.itemScrollY += (float) (delta * 10f);
        }
    }

    public boolean isItemSelectorHovered(float mouseX, float mouseY) {
        if (!(boolean) setting.getVisible().get()) {
            return false;
        }

        if ((setting instanceof ItemSetting || setting instanceof MultiItemSetting) && selected) {
            float panelBaseY = y + delta + 24f;

            if (setting instanceof ItemSetting s) {
                Item currentItem = s.getValue();
                int previewRows = currentItem != null && currentItem != Items.AIR ? 1 : 0;
                float closedHeight = 24f + (previewRows > 0 ? 20f : 0);
                panelBaseY = y + delta + closedHeight;
            } else if (setting instanceof MultiItemSetting s) {
                int itemsPerRow = Math.max(1, (int) ((width - 8f) / 18f));
                int selectedItemCount = s.getValue().size();
                int requiredRows = selectedItemCount > 0 ? (int) Math.ceil((double) selectedItemCount / itemsPerRow) : 0;
                float closedHeight = 24f + (requiredRows > 0 ? (requiredRows * 20f) : 0);
                panelBaseY = y + delta + closedHeight;
            }

            float panelHeight = 150f - 4f;
            return isHovered(mouseX, mouseY, x, panelBaseY, width, panelHeight);
        }
        return false;
    }

    private boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}