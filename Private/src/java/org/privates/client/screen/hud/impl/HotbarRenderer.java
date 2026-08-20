package org.privates.client.screen.hud.impl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.events.render.RenderHotbarEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class HotbarRenderer extends AbstractHud {

    public HotbarRenderer() {
    }

    public void renderHotbar(RenderHotbarEvent event) {
        MatrixStack matrix = event.getMatrix();
        PlayerEntity player = mc.player;
        if (player == null) return;

        int screenWidth = mc.getMainWindow().getScaledWidth();
        int screenHeight = mc.getMainWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int hotbarY = screenHeight - 22;

        // Vanilla hotbar dimensions: 182x22, each slot is 20x20 with 2px spacing
        int hotbarWidth = 182;
        int hotbarHeight = 22;
        int slotSize = 16;

        // Draw main hotbar background
        float bgX = centerX - 91;
        float bgY = hotbarY;
        RenderUtil.CustomStyled.draw(matrix, bgX, bgY, hotbarWidth, hotbarHeight, Round.of(3f), true, 10f);

        // Draw selected slot highlight
        int selectedSlot = player.inventory.currentItem;
        float selectedX = bgX + selectedSlot * 20 - 1;
        float selectedY = bgY - 1;
        int selectedColor = Theme.getInstance().clientColor();
        RenderUtil.Rounded.roundedOutline(matrix, selectedX, selectedY, 24, 24, 1.5f, selectedColor, Round.of(3f));

        // Render items
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (int i = 0; i < 9; i++) {
            int slotX = centerX - 90 + i * 20 + 2;
            int slotY = hotbarY + 3;
            ItemStack stack = player.inventory.mainInventory.get(i);
            renderHotbarItem(slotX, slotY, event.getPartialTicks(), player, stack);
        }

        // Render offhand
        ItemStack offhand = player.getHeldItemOffhand();
        if (!offhand.isEmpty()) {
            HandSide handside = player.getPrimaryHand().opposite();
            int offhandY = hotbarY + 3;

            if (handside == HandSide.LEFT) {
                float offhandBgX = bgX - 29;
                RenderUtil.CustomStyled.draw(matrix, offhandBgX, bgY - 1, 29, 24, Round.of(3f), true, 10f);
                renderHotbarItem((int) (offhandBgX + 6), offhandY, event.getPartialTicks(), player, offhand);
            } else {
                float offhandBgX = bgX + hotbarWidth;
                RenderUtil.CustomStyled.draw(matrix, offhandBgX, bgY - 1, 29, 24, Round.of(3f), true, 10f);
                renderHotbarItem((int) (offhandBgX + 10), offhandY, event.getPartialTicks(), player, offhand);
            }
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderHotbarItem(int x, int y, float partialTicks, PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            float animTime = (float) stack.getAnimationsToGo() - partialTicks;

            if (animTime > 0.0F) {
                RenderSystem.pushMatrix();
                float scale = 1.0F + animTime / 5.0F;
                RenderSystem.translatef((float) (x + 8), (float) (y + 12), 0.0F);
                RenderSystem.scalef(1.0F / scale, (scale + 1.0F) / 2.0F, 1.0F);
                RenderSystem.translatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
            }

            mc.getItemRenderer().renderItemAndEffectIntoGUI(player, stack, x, y);

            if (animTime > 0.0F) {
                RenderSystem.popMatrix();
            }

            mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
        }
    }
}
