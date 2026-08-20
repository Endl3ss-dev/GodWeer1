package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.MultiItemSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Accessors(fluent = true)
public class ItemSetting extends Builder implements IMinecraft {

    private final Setting setting;
    private final org.privates.client.managers.module.settings.impl.ItemSetting itemSetting;
    private final MultiItemSetting multiItemSetting;
    private final HashMap<Item, Float> selectedV = new HashMap<>();
    private final ArrayList<Item> items = new ArrayList<>();
    private final HashMap<Item, Float> hoverV = new HashMap<>();
    private final HashMap<Item, Long> hoverTime = new HashMap<>();
    private final HashMap<Item, Boolean> hoverBoolean = new HashMap<>();

    private float delta, deltaV;
    private boolean input;
    private String find = "";
    private float lastY;

    public ItemSetting(Setting setting, org.privates.client.managers.module.settings.Setting itemSetting, float x, float y) {
        this.setting = setting;
        if (itemSetting instanceof org.privates.client.managers.module.settings.impl.ItemSetting s) {
            this.itemSetting = s;
            this.multiItemSetting = null;
        } else {
            this.itemSetting = null;
            this.multiItemSetting = ((MultiItemSetting) itemSetting);
        }
        this.x = x;
        this.y = y;
        this.width = 147;
        this.height = 151;
        loadItems(true);
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {

        deltaV = Mathf.fast(deltaV, delta, 16);

        RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.getColor(14, 16, 20, alpha), Round.of(6));

        RenderUtil.Rounded.roundedRect(stack, x + 9, y + 7, width - 18, 10, ColorUtil.getColor(28, 30, 36, alpha), Round.of(6));
        final String text = input ? find : "Поиск по названию";
        Fonts.SF_MEDIUM.draw(stack, text, x + 12.5f, y + 11 - Fonts.SF_MEDIUM.getHeight(text, 6) / 2f, ColorUtil.getColor(159, 161, 165, alpha), 6);

        float x = this.x + 9, y = this.y + deltaV + 24;
        final float itemSize = 12;
        lastY = y + itemSize + 4 - deltaV + delta;
        final Theme theme = Theme.getInstance();
        int id = 1;
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, this.y + 17, width, height - 17);
        for (Item item : items) {

            final float interpolate = selectedV.get(item);
            final int leftGradient = ColorUtil.fade(7, (id * 60), theme.clientColor(), theme.darkColor()),
                    rightGradient = ColorUtil.fade(7, 30 + (id * 60), theme.clientColor(), theme.darkColor());
            final int leftColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), leftGradient, interpolate), alpha),
                    rightColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), rightGradient, interpolate), alpha);

            if (x + itemSize + 4 > this.x + width - 8) {
                y += itemSize + 6;
                lastY = y + itemSize + 4 - deltaV + delta;
                x = this.x + 9;
                id = 1;
            }

            final boolean currentHovered = isHovered(mouseX, mouseY, x, y, itemSize + 4, itemSize + 4);

            if (currentHovered != hoverBoolean.get(item)) {
                this.hoverTime.replace(item, currentHovered ? System.currentTimeMillis() : 0L);
                hoverBoolean.replace(item, currentHovered);
            }

            final float hoverV = this.hoverV.get(item);
            final long hoverStartTime = this.hoverTime.get(item);

            final boolean shouldShow = currentHovered && hoverStartTime != 0 &&
                    (System.currentTimeMillis() - hoverStartTime) >= 400L;

            this.hoverV.replace(item, Mathf.fast(hoverV, shouldShow ? 1 : 0, 16));
            selectedV.replace(item, Mathf.fast(selectedV.get(item), (itemSetting != null && item == itemSetting.getValue()) || (multiItemSetting != null && multiItemSetting.getValue().contains(item)) ? 1F : 0F, 16));

            if (inRange(y, itemSize + 4, this.y + 17, height - 17)) {
                RenderUtil.Rounded.roundedRect(stack, x, y, itemSize + 4, itemSize + 4, leftColor, leftColor, rightColor, rightColor, Round.of(3));
                RectUtil.drawItemStack(item.getDefaultInstance(), x + 2, y + 2, false, itemSize, itemSize);
            }
            x += itemSize + 7;
            id++;
        }
        ScissorExp.unset();
        ScissorExp.pop();
        x = this.x + 9;
        y = this.y + deltaV + 24;
        if (isHovered(mouseX, mouseY, x + 9, this.y + 17, width - 18, height - 17)) {
            for (Item item : items) {
                if (x + itemSize + 4 > this.x + width - 8) {
                    y += itemSize + 6;
                    x = this.x + 9;
                }
                final float hoverV = this.hoverV.get(item);
                if (hoverV >= .1f) {
                    final String itemName = item.getName().getString();
                    final float nameWidth = Fonts.SF_MEDIUM.getWidth(itemName, 7);
                    RenderUtil.Rounded.roundedRect(stack, x + (itemSize + 4) / 2f - nameWidth / 2f - 2, y - 13 * hoverV, nameWidth + 4, 10, ColorUtil.getColor(14, 16, 20, hoverV * alpha), Round.of(3));
                    Fonts.SF_MEDIUM.draw(stack, itemName, x + (itemSize + 4) / 2f - nameWidth / 2f, y - 9 * hoverV - Fonts.SF_MEDIUM.getHeight(itemName, 7) / 2f, ColorUtil.getColor(159, 161, 165, hoverV * alpha), 7);
                }

                x += itemSize + 7;
            }
        }
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, boolean action) {
        if (action && button == 0) {
            if (isHovered(mouseX, mouseY, x + 7, y + 7, width - 14, 10)) {
                input = !input;
            } else if (isHovered(mouseX, mouseY, x + 7, y + 24, width - 14, height - 24)) {
                float x = this.x + 9, y = this.y + deltaV + 24;
                final float itemSize = 12;
                for (Item item : items) {

                    if (x + itemSize + 4 > this.x + width - 8) {
                        y += itemSize + 6;
                        x = this.x + 9;
                    }

                    if (isHovered(mouseX, mouseY, x, y, itemSize + 4, itemSize + 4)) {
                        if (itemSetting != null)
                            itemSetting.set(item);
                        else if (multiItemSetting.contains(item)) {
                            multiItemSetting.removeItem(item);
                            break;
                        } else
                            multiItemSetting.addItem(item);
                    }
                    x += itemSize + 7;
                }
            }
        }
    }

    public void scroll(float mx, float my, float delta) {
        if (!(this.delta >= 0 && delta > 0) && !(lastY <= this.y + height - 5 && delta < 0)) {
            this.delta += delta * 32;
            if (this.delta > 0)
                this.delta = 0;
            if (lastY <= this.y + height + 30) {
                this.delta -= lastY - (this.y + height + 30);
            }
        }
    }

    @Override
    void key(int key) {
        if (input) {
            boolean empty = find.isEmpty();
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
                find = "";
                input = false;
            } else if (key == GLFW.GLFW_KEY_DELETE) {
                find = "";
            } else if (key == GLFW.GLFW_KEY_BACKSPACE)
                find = find.isEmpty() ? "" : find.substring(0, find.length() - 1);
            if (empty != find.isEmpty())
                loadItems(true);
        }
    }

    @Override
    void charTyped(char typed) {
        if (input) {
            find += typed;
            loadItems(true);
        }
    }

    private void loadItems(boolean clear) {
        if (clear) {
            selectedV.clear();
            items.clear();
        }
        delta = 0;
        for (Item item : Registry.ITEM.stream().filter(i -> i.getName().getString().toLowerCase().contains(find.toLowerCase())).toList()) {
            items.add(item);
            selectedV.put(item, (itemSetting != null && itemSetting.getValue() == item) || (multiItemSetting != null && multiItemSetting.contains(item)) ? 1F : 0F);
            hoverV.put(item, 0F);
            hoverTime.put(item, 0L);
            hoverBoolean.put(item, false);
        }
    }
}
