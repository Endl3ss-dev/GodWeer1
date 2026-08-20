package minced.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Hold-to-select emotion wheel.
 */
public class ScreenClass621 extends Screen {
    public static String[] I = {
        "Приветствие",
        "Танец",
        "Дрочка",
        "Альфа ходьба",
        "Альфа-Мужик"
    };

    private static final double INNER_RADIUS = 28.0;
    private static final double OUTER_RADIUS = 105.0;
    private int hoveredSlot = -2;

    public ScreenClass621() {
        super(Text.literal("Emotions"));
    }

    public int getHoveredSlot() {
        return hoveredSlot;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = width / 2;
        int centerY = height / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < INNER_RADIUS || distance > OUTER_RADIUS) {
            hoveredSlot = -2;
        } else {
            double angle = Math.atan2(dy, dx) + Math.PI / 2.0;
            if (angle < 0.0) angle += Math.PI * 2.0;
            hoveredSlot = MathHelper.floor(angle / (Math.PI * 2.0) * I.length) % I.length;
        }

        context.fill(centerX - 115, centerY - 115, centerX + 115, centerY + 115, 0x88000000);
        for (int index = 0; index < I.length; index++) {
            double angle = (Math.PI * 2.0 * index / I.length) - Math.PI / 2.0;
            int x = centerX + (int)(Math.cos(angle) * 72.0);
            int y = centerY + (int)(Math.sin(angle) * 72.0);
            int color = index == hoveredSlot ? 0xFFFFAA00 : 0xFFFFFFFF;
            context.drawCenteredTextWithShadow(textRenderer, I[index], x, y - 4, color);
        }
        context.drawCenteredTextWithShadow(textRenderer, "Отмена", centerX, centerY - 4,
            hoveredSlot == -2 ? 0xFFFF5555 : 0xFFAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
