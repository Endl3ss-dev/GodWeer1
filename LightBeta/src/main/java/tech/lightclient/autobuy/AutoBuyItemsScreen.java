package tech.lightclient.autobuy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Dedicated item editor opened from the AutoBuy ClickGUI module. */
public final class AutoBuyItemsScreen extends Screen {
    private final Screen parent;
    private int scroll;

    public AutoBuyItemsScreen(Screen parent) {
        super(Text.literal("AutoBuy Items"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xCC0D0F14);
        var tr = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(tr, Text.literal("AutoBuy — предметы и лимиты"), width / 2, 18, 0xFFFFFFFF);
        int y = 42 - scroll;
        var cfg = AutoBuyConfig.cfg();
        for (ItemEntry entry : cfg.entries) {
            if (y > 30 && y < height - 20) {
                int color = entry.enabled ? 0xFF7CFF9A : 0xFFB6BBC8;
                String name = entry.displayName == null || entry.displayName.isBlank() ? entry.itemId : entry.displayName;
                context.drawTextWithShadow(tr, Text.literal((entry.enabled ? "[ON] " : "[OFF] ") + name), 24, y, color);
                context.drawTextWithShadow(tr, Text.literal("лимит: " + entry.maxUnitPrice), width - 180, y, 0xFFDDE1EA);
            }
            y += 18;
        }
        context.drawTextWithShadow(tr, Text.literal("ЛКМ — переключить предмет   ESC — назад"), 24, height - 20, 0xFF9AA3B5);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scroll = Math.max(0, scroll - (int) (vertical * 24));
        return true;
    }

    @Override public boolean mouseClicked(Click click, boolean repeated) {
        if (click.button() != 0) return true;
        int index = (int) ((click.y() + scroll - 42) / 18);
        if (index >= 0 && index < AutoBuyConfig.cfg().entries.size()) {
            AutoBuyConfig.cfg().entries.get(index).enabled = !AutoBuyConfig.cfg().entries.get(index).enabled;
            AutoBuyConfig.save();
        }
        return true;
    }

    @Override public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) { MinecraftClient.getInstance().setScreen(parent); return true; }
        return super.keyPressed(input);
    }
}
