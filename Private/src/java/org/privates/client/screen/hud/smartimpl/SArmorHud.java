package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.AirItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Collections;

public class SArmorHud implements IRenderer {
    private final DragSetting drag;
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SArmorHud(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack stack = event.getMatrix();

        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack item : Minecraft.getInstance().player.getArmorInventoryList()) {
            if (!(item.getItem() instanceof AirItem) &&
                    (item.getItem() instanceof ArmorItem || item.getItem() instanceof ElytraItem)) {
                items.add(item);
            }
        }
        Collections.reverse(items);

        scaleAnimation.update();
        boolean shouldShow = !items.isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float x = drag.position.x;
        float y = drag.position.y;

        final float width = 52f; // 26 * 2 (2x width)
        final float titleHeight = 16f;
        final float itemHeight = 14f;
        final float spacing = 4f;
        final float totalHeight = titleHeight + spacing + items.size() * itemHeight;

        if (x == 0 || y == 0) {
            drag.position.set(10, 300);
            x = 10;
            y = 300;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(width, totalHeight);

        stack.push();
        stack.translate(finalX + width / 2, finalY + totalHeight / 2, 0);
        stack.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        stack.translate(-(finalX + width / 2), -(finalY + totalHeight / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(stack, finalX, finalY, width, titleHeight,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));

                float offsetY = finalY + titleHeight + spacing;
                for (int i = 0; i < items.size(); i++) {
                    RenderUtil.Rounded.roundedRect(stack, finalX, offsetY, width, itemHeight,
                            ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(3));
                    offsetY += itemHeight;
                }
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(stack, finalX, finalY, width, titleHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));

        String title = "Armour";
        Fonts.SF_MEDIUM.drawCenter(stack, title, finalX + width / 2, finalY + 6,
                ColorUtil.getColor(255, 255, 255), 6);

        float offsetY = finalY + titleHeight + spacing;
        for (ItemStack itemStack : items) {
            renderArmorItem(stack, itemStack, finalX, offsetY, width, itemHeight);
            offsetY += itemHeight;
        }

        stack.pop();
    }

    private void renderArmorItem(MatrixStack stack, ItemStack itemStack, float x, float y, float width, float height) {
        RenderUtil.Rounded.roundedRect(stack, x, y, width, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(3));

        float percent = ((float) (itemStack.getMaxDamage() - itemStack.getDamage()) / itemStack.getMaxDamage() * 100);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x + 2, y + 1, 1);
        GlStateManager.scalef(0.6f, 0.6f, 0.6f);
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(itemStack, 0, 0);
        GlStateManager.popMatrix();

        float barX = x + 18; // adjusted for wider component
        float barY = y + height / 2 - 1;
        float barWidth = width - 20; // adjusted for wider component
        float barHeight = 2f;

        RenderUtil.Rounded.roundedRect(stack, barX, barY, barWidth, barHeight,
                ColorUtil.getColor(30, 30, 30, 120), Round.of(1));

        int barColor = ColorUtil.overCol(ColorUtil.getColor(255, 50, 50),
                ColorUtil.getColor(50, 255, 50), percent / 100f);
        RenderUtil.Rounded.roundedRect(stack, barX, barY, (percent / 100f) * barWidth, barHeight,
                barColor, Round.of(1));

        String percentText = ((int) percent) + "%";
        Fonts.SF_MEDIUM.drawCenter(stack, percentText, x + width / 2, y + height - 7,
                ColorUtil.getColor(255, 255, 255), 6); // 4 * 1.5
    }
}