package tech.lightclient.ui.hud.star;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.render.main.core.Renderer2D;

public final class ItemDraw implements IMinecraft {
    private ItemDraw() {
    }

    public static void drawItem(Renderer2D r, ItemStack stack, float x, float y, float size, float alpha) {
        if (stack == null || stack.isEmpty() || alpha <= 0f) {
            return;
        }
        DrawContext context = LightClient.getCurrentDrawContext();
        if (context == null || size <= 0f) {
            return;
        }
        r.flush();
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        try {
            // Hotbar/HUD geometry is in Minecraft's logical GUI coordinates.
            // DrawContext also consumes logical coordinates, so applying the
            // window scale here makes items both smaller and displaced.
            matrices.translate(x, y);
            matrices.scale(size / 16f, size / 16f);
            context.drawItem(stack, 0, 0);
        } catch (Throwable ignored) {
        } finally {
            matrices.popMatrix();
            r.resetPipelineState();
        }
    }
}
