package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.AirItem;
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

public class SInventory implements IRenderer {
    private final DragSetting drag;
    private final Minecraft mc = Minecraft.getInstance();
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SInventory(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();
        if (mc.player == null || mc.world == null) return;

        scaleAnimation.update();
        boolean shouldShow = !isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float x = drag.position.x;
        float y = drag.position.y;
        final float width = 96f; // 48 * 2 (2x width)
        final float titleHeight = 16f;
        final float inventoryHeight = 40f;
        final float spacing = 4f;
        final float totalHeight = titleHeight + spacing + inventoryHeight;

        if (x == 0 || y == 0) {
            drag.position.set(mc.getMainWindow().getScaledWidth() / 2 - width / 2,
                    mc.getMainWindow().getScaledHeight() - totalHeight - 60);
            x = mc.getMainWindow().getScaledWidth() / 2 - width / 2;
            y = mc.getMainWindow().getScaledHeight() - totalHeight - 60;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(width, totalHeight);

        matrix.push();
        matrix.translate(finalX + width / 2, finalY + totalHeight / 2, 0);
        matrix.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        matrix.translate(-(finalX + width / 2), -(finalY + totalHeight / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                RenderUtil.Rounded.roundedRect(matrix, finalX, finalY + titleHeight + spacing, width, inventoryHeight,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(3));
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));

        String title = "Inventory";
        Fonts.SF_MEDIUM.drawCenter(matrix, title, finalX + width / 2, finalY + 6,
                ColorUtil.getColor(255, 255, 255), 6);

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY + titleHeight + spacing, width, inventoryHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(3));

        float itemSize = 8f;
        float itemSpacing = 1f;
        float startX = finalX + 4f;
        float startY = finalY + titleHeight + spacing + 4f;

        int itemsPerRow = 5;
        int rows = 4;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < itemsPerRow; col++) {
                int slot = 9 + row * 9 + col;
                if (slot >= mc.player.inventory.mainInventory.size()) break;

                float itemX = startX + col * (itemSize + itemSpacing);
                float itemY = startY + row * (itemSize + itemSpacing);

                GlStateManager.pushMatrix();
                GlStateManager.translatef(itemX, itemY, 1);
                GlStateManager.scalef(0.5f, 0.5f, 0.5f);
                mc.getItemRenderer().renderItemIntoGUI(
                        mc.player.inventory.mainInventory.get(slot), 0, 0);
                GlStateManager.popMatrix();
            }
        }

        matrix.pop();
    }

    private boolean isEmpty() {
        if (mc.player == null) return true;
        for (int i = 9; i < mc.player.inventory.mainInventory.size(); i++) {
            if (!(mc.player.inventory.mainInventory.get(i).getItem() instanceof AirItem))
                return false;
        }
        return true;
    }
}