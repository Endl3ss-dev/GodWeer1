package minced.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/** Radial item selector used by Auto Swap. */
public class ScreenClass586 extends Screen {
    private final ItemStack[] items;
    private int hoveredSlot = -1;

    public ScreenClass586(ItemStack[] items) {
        super(Text.literal("Auto Swap"));
        this.items = items;
    }

    public int getHoveredSlot() {
        return hoveredSlot;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int cx = width / 2, cy = height / 2;
        double dx = mouseX - cx, dy = mouseY - cy;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 35 || distance > 125) {
            hoveredSlot = -1;
        } else {
            double angle = Math.atan2(dy, dx) + Math.PI / 2;
            if (angle < 0) angle += Math.PI * 2;
            hoveredSlot = MathHelper.floor(angle / (Math.PI * 2) * items.length) % items.length;
        }
        context.fill(cx - 140, cy - 140, cx + 140, cy + 140, 0x88000000);
        for (int i = 0; i < items.length; i++) {
            double a = i * Math.PI * 2 / items.length - Math.PI / 2;
            int x = cx + (int)(Math.cos(a) * 92) - 8;
            int y = cy + (int)(Math.sin(a) * 92) - 8;
            if (i == hoveredSlot) context.fill(x - 8, y - 8, x + 24, y + 24, 0x8844AAFF);
            context.drawItem(items[i], x, y);
        }
        context.drawCenteredTextWithShadow(textRenderer,
            hoveredSlot >= 0 ? items[hoveredSlot].getName() : Text.literal("Выберите предмет"),
            cx, cy - 4, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
