package tech.lightclient.autobuy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

/**
 * Controls shown directly above the auction, matching the Winlend layout.
 * Click handling is forwarded here by {@code HandledScreenMixin}.
 */
public final class AutoBuyOverlay {
    private static final int BUTTON_HEIGHT = 16;
    private static final int GAP = 4;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PANEL_COLOR = 0xE8141720;
    private static final int HOVER_COLOR = 0xE83F315A;
    private static final int ENABLED_COLOR = 0xE8794FB5;

    private AutoBuyOverlay() {
    }

    public static boolean shouldShow(HandledScreen<?> screen) {
        return screen instanceof GenericContainerScreen gcs
                && AutoBuyEngine.isAuctionScreen(gcs);
    }

    /** Draws the two rows of controls above the auction container. */
    public static void render(DrawContext context, int gx, int gy, int gw, int gh,
                              int mouseX, int mouseY) {
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        MinecraftClient mc = MinecraftClient.getInstance();

        int firstWidth = Math.max(1, (gw - GAP * 2) / 3);
        int firstY = gy - BUTTON_HEIGHT - GAP;
        int secondWidth = Math.max(1, (gw - GAP) / 2);
        int secondY = firstY - BUTTON_HEIGHT - GAP;

        drawButton(context, mc, gx, firstY, firstWidth,
                cfg.enabled ? "AutoBuy ON" : "AutoBuy OFF",
                cfg.enabled, hovered(mouseX, mouseY, gx, firstY, firstWidth));
        drawButton(context, mc, gx + firstWidth + GAP, firstY, firstWidth,
                cfg.autoSell ? "AutoSell ON" : "AutoSell OFF",
                cfg.autoSell, hovered(mouseX, mouseY, gx + firstWidth + GAP, firstY, firstWidth));
        drawButton(context, mc, gx + (firstWidth + GAP) * 2, firstY, firstWidth,
                AutoBuyEngine.INSTANCE.parser.isActive() ? "Stop parse" : "Parse prices",
                AutoBuyEngine.INSTANCE.parser.isActive(),
                hovered(mouseX, mouseY, gx + (firstWidth + GAP) * 2, firstY, firstWidth));

        drawButton(context, mc, gx, secondY, secondWidth,
                cfg.autoRelist ? "Relist ON" : "Relist OFF",
                cfg.autoRelist, hovered(mouseX, mouseY, gx, secondY, secondWidth));
        drawButton(context, mc, gx + secondWidth + GAP, secondY, secondWidth,
                cfg.autoParse ? "AutoParse ON" : "AutoParse OFF",
                cfg.autoParse, hovered(mouseX, mouseY, gx + secondWidth + GAP, secondY, secondWidth));
    }

    private static void drawButton(DrawContext context, MinecraftClient mc, int x, int y, int width,
                                   String label, boolean enabled, boolean hovered) {
        int background = hovered ? HOVER_COLOR : (enabled ? ENABLED_COLOR : PANEL_COLOR);
        context.fill(x, y, x + width, y + BUTTON_HEIGHT, background);

        int textWidth = mc.textRenderer.getWidth(label);
        int textX = x + Math.max(2, (width - textWidth) / 2);
        int textY = y + (BUTTON_HEIGHT - 9) / 2;
        context.drawTextWithShadow(mc.textRenderer, Text.literal(label), textX, textY, TEXT_COLOR);
    }

    private static boolean hovered(double mouseX, double mouseY, int x, int y, int width) {
        return mouseX >= x && mouseX < x + width
                && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
    }

    /** Handles left clicks on the Winlend-style controls. */
    public static boolean mouseClicked(double mouseX, double mouseY, int button,
                                       int gx, int gy, int gw, int gh) {
        if (button != 0) return false;

        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        int firstWidth = Math.max(1, (gw - GAP * 2) / 3);
        int firstY = gy - BUTTON_HEIGHT - GAP;
        int secondWidth = Math.max(1, (gw - GAP) / 2);
        int secondY = firstY - BUTTON_HEIGHT - GAP;

        if (hovered(mouseX, mouseY, gx, firstY, firstWidth)) {
            cfg.enabled = !cfg.enabled;
            AutoBuyConfig.save();
            return true;
        }
        if (hovered(mouseX, mouseY, gx + firstWidth + GAP, firstY, firstWidth)) {
            cfg.autoSell = !cfg.autoSell;
            AutoBuyConfig.save();
            if (cfg.autoSell) {
                AutoBuyEngine.INSTANCE.seller.trigger();
            } else {
                AutoBuyEngine.INSTANCE.seller.stop();
            }
            return true;
        }
        if (hovered(mouseX, mouseY, gx + (firstWidth + GAP) * 2, firstY, firstWidth)) {
            if (AutoBuyEngine.INSTANCE.parser.isActive()) {
                AutoBuyEngine.INSTANCE.parser.stop();
            } else {
                AutoBuyEngine.INSTANCE.parser.start();
            }
            return true;
        }
        if (hovered(mouseX, mouseY, gx, secondY, secondWidth)) {
            cfg.autoRelist = !cfg.autoRelist;
            AutoBuyConfig.save();
            return true;
        }
        if (hovered(mouseX, mouseY, gx + secondWidth + GAP, secondY, secondWidth)) {
            cfg.autoParse = !cfg.autoParse;
            AutoBuyConfig.save();
            return true;
        }
        return false;
    }
}

