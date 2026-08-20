package org.privates.client.screen.autobuy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.misc.AutoBuySpooky;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyListScreen {

    private final Minecraft mc = Minecraft.getInstance();

    private float alphaV = 0f;
    private float scrollOffset = 0f;
    private float targetScrollOffset = 0f;

    private AutoBuySpooky.BuyedItem hoveredItem = null;
    private long hoverStartTime = 0L;

    private String currentDescription = "";
    private float descriptionAlpha = 0f;

    private final Map<AutoBuySpooky.BuyedItem, Float> rowHoverAnims = new HashMap<>();

    private static final float PANEL_WIDTH = 186f;
    private static final float HEADER_HEIGHT = 28f;
    private static final float FOOTER_HEIGHT = 24f;
    private static final float ROW_HEIGHT = 22f;
    private static final float PAD = 8f;
    private static final float RADIUS = 10f;

    private float panelX, panelY, panelH;

    public BuyListScreen() {
    }

    public void render(MatrixStack stack, int mouseX, int mouseY, int guiLeft, int guiTop, int ySize) {
        AutoBuySpooky module = AutoBuySpooky.getInstance();
        if (module == null) return;

        List<AutoBuySpooky.BuyedItem> items = new ArrayList<>(module.getBuyedItems());
        items.sort((a, b) -> b.time.compareTo(a.time));

        Theme theme = Theme.getInstance();
        int bgColor = theme.background().getValue();

        alphaV = Mathf.fast(alphaV, 1f, 20);
        scrollOffset = Mathf.fast(scrollOffset, targetScrollOffset, 16);

        float contentH = ySize - HEADER_HEIGHT - FOOTER_HEIGHT - PAD * 2;
        int visibleRows = (int) (contentH / ROW_HEIGHT);
        int maxScroll = Math.max(0, items.size() - visibleRows);
        targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));

        panelH = HEADER_HEIGHT + contentH + FOOTER_HEIGHT;
        panelX = guiLeft - PANEL_WIDTH - 12f;
        panelY = guiTop + (ySize - panelH) / 2f;

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(stack, panelX, panelY, PANEL_WIDTH, panelH,
                    ColorUtil.multAlpha(bgColor, 0.5f * alphaV), Round.of(RADIUS));
        });
        BlurRenderer2.draw(8);

        RenderUtil.Rounded.roundedRect(stack, panelX, panelY, PANEL_WIDTH, panelH,
                ColorUtil.multAlpha(bgColor, alphaV), Round.of(RADIUS));
        RenderUtil.Rounded.roundedOutline(stack, panelX, panelY, PANEL_WIDTH, panelH,
                1f, ColorUtil.multAlpha(new Color(255, 255, 255, 15).getRGB(), alphaV), Round.of(RADIUS));

        renderHeader(stack, items, theme);

        RenderUtil.Rounded.roundedRect(stack, panelX + 10f, panelY + HEADER_HEIGHT, PANEL_WIDTH - 20f, 1f,
                ColorUtil.multAlpha(new Color(255, 255, 255, 20).getRGB(), alphaV), Round.of(0.5f));

        renderRows(stack, mouseX, mouseY, items, visibleRows, contentH, theme);

        RenderUtil.Rounded.roundedRect(stack, panelX + 10f, panelY + panelH - FOOTER_HEIGHT, PANEL_WIDTH - 20f, 1f,
                ColorUtil.multAlpha(new Color(255, 255, 255, 20).getRGB(), alphaV), Round.of(0.5f));

        renderFooter(stack, mouseX, mouseY, theme);
        renderDescription(stack, theme);
    }

    private void renderHeader(MatrixStack stack, List<AutoBuySpooky.BuyedItem> items, Theme theme) {
        String title = "История покупок";
        float tw = Fonts.SF_MEDIUM.getWidth(title, 8.5f);
        Fonts.SF_MEDIUM.draw(stack, title,
                panelX + (PANEL_WIDTH - tw) / 2f,
                panelY + (HEADER_HEIGHT - Fonts.SF_MEDIUM.getHeight(title, 8.5f)) / 2f,
                ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 8.5f);

        String cnt = String.valueOf(items.size());
        float cw = Fonts.SF_MEDIUM.getWidth(cnt, 6.5f);
        float bw = cw + 10f, bh = 12f;
        float bx = panelX + PANEL_WIDTH - PAD - bw;
        float by = panelY + (HEADER_HEIGHT - bh) / 2f;
        int ac = theme.clientColor();

        RenderUtil.Rounded.roundedRect(stack, bx, by, bw, bh,
                ColorUtil.multAlpha(ac, 0.25f * alphaV), Round.of(6f));
        Fonts.SF_MEDIUM.draw(stack, cnt,
                bx + (bw - cw) / 2f,
                by + (bh - Fonts.SF_MEDIUM.getHeight(cnt, 6.5f)) / 2f,
                ColorUtil.multAlpha(ac, alphaV), 6.5f);
    }

    private void renderRows(MatrixStack stack, int mouseX, int mouseY,
                            List<AutoBuySpooky.BuyedItem> items, int visibleRows, float contentH, Theme theme) {
        float listY = panelY + HEADER_HEIGHT + 2f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(panelX, listY, PANEL_WIDTH, contentH);

        AutoBuySpooky.BuyedItem newHovered = null;
        float fracScroll = scrollOffset - (int) scrollOffset;

        for (int i = 0; i <= visibleRows; i++) {
            int idx = i + (int) scrollOffset;
            if (idx < 0 || idx >= items.size()) continue;

            AutoBuySpooky.BuyedItem item = items.get(idx);
            float rowY = listY + i * ROW_HEIGHT - fracScroll * ROW_HEIGHT + 2f;

            boolean hov = mouseX >= panelX + 4f && mouseX < panelX + PANEL_WIDTH - 4f &&
                    mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;

            float ha = rowHoverAnims.getOrDefault(item, 0f);
            ha = Mathf.fast(ha, hov ? 1f : 0f, 18);
            rowHoverAnims.put(item, ha);

            if (ha > 0.01f) {
                RenderUtil.Rounded.roundedRect(stack, panelX + 4f, rowY, PANEL_WIDTH - 8f, ROW_HEIGHT,
                        ColorUtil.multAlpha(
                                ColorUtil.overCol(new Color(30, 30, 35, 0).getRGB(),
                                        ColorUtil.multAlpha(theme.clientColor(), 0.12f), ha),
                                alphaV), Round.of(5f));
            }

            if (item.originalStack != null && !item.originalStack.isEmpty()) {
                RenderSystem.pushMatrix();
                RenderHelper.enableStandardItemLighting();
                mc.getItemRenderer().renderItemAndEffectIntoGUI(item.originalStack,
                        (int) (panelX + PAD + 2f), (int) (rowY + (ROW_HEIGHT - 16f) / 2f));
                RenderHelper.disableStandardItemLighting();
                RenderSystem.popMatrix();
            }

            float nameX = panelX + PAD + 22f;
            float nameY = rowY + (ROW_HEIGHT - Fonts.SF_MEDIUM.getHeight(item.abItem.displayName, 7f)) / 2f;
            Fonts.SF_MEDIUM.draw(stack, item.abItem.displayName, nameX, nameY,
                    ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 7f);

            String pt = item.price + " ¤";
            float pw = Fonts.SF_MEDIUM.getWidth(pt, 6.5f);
            float priceX = panelX + PANEL_WIDTH - PAD - pw - 2f;
            float priceY = rowY + (ROW_HEIGHT - Fonts.SF_MEDIUM.getHeight(pt, 6.5f)) / 2f;
            Fonts.SF_MEDIUM.draw(stack, pt, priceX, priceY,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV * 0.85f), 6.5f);

            if (hov) newHovered = item;
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (newHovered != hoveredItem) {
            hoveredItem = newHovered;
            hoverStartTime = System.currentTimeMillis();
            if (hoveredItem == null) {
                descriptionAlpha = 0f;
                currentDescription = "";
            }
        }

        if (hoveredItem != null && System.currentTimeMillis() - hoverStartTime >= 250L) {
            String desc = hoveredItem.abItem.displayName + " x" + hoveredItem.count
                    + " - " + hoveredItem.time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
            if (!currentDescription.equals(desc)) {
                currentDescription = desc;
            }
            descriptionAlpha = Mathf.fast(descriptionAlpha, 1f, 6);
        } else if (hoveredItem == null) {
            descriptionAlpha = Mathf.fast(descriptionAlpha, 0f, 6);
        }
    }

    private void renderFooter(MatrixStack stack, int mouseX, int mouseY, Theme theme) {
        float footerY = panelY + panelH - FOOTER_HEIGHT;
        float btnY = footerY + (FOOTER_HEIGHT - 14f) / 2f;
        float btnH = 14f;

        float clearW = Fonts.SF_MEDIUM.getWidth("Очистить", 7f) + 14f;
        float cx = panelX + (PANEL_WIDTH - clearW) / 2f;

        boolean clrHov = mouseX >= cx && mouseX < cx + clearW && mouseY >= btnY && mouseY < btnY + btnH;
        RenderUtil.Rounded.roundedRect(stack, cx, btnY, clearW, btnH,
                ColorUtil.multAlpha(clrHov ? new Color(140, 40, 40, 180).getRGB() : new Color(100, 35, 35, 150).getRGB(), alphaV),
                Round.of(5f));
        float clw = Fonts.SF_MEDIUM.getWidth("Очистить", 7f);
        Fonts.SF_MEDIUM.draw(stack, "Очистить",
                cx + (clearW - clw) / 2f,
                btnY + (btnH - Fonts.SF_MEDIUM.getHeight("Очистить", 7f)) / 2f,
                ColorUtil.multAlpha(new Color(255, 180, 180).getRGB(), alphaV), 7f);
    }

    private void renderDescription(MatrixStack stack, Theme theme) {
        if (descriptionAlpha <= 0.01f || currentDescription.isEmpty() || hoveredItem == null) return;

        float dw = Fonts.SF_MEDIUM.getWidth(currentDescription, 7f) + 16f;
        float dh = Fonts.SF_MEDIUM.getHeight(currentDescription, 7f) + 10f;
        float dx = panelX + PANEL_WIDTH + 8f;
        float dy = panelY + 15f;

        stack.push();
        stack.translate(dx + dw / 2f, dy + dh / 2f, 0);
        stack.scale(descriptionAlpha, descriptionAlpha, 1);
        stack.translate(-(dx + dw / 2f), -(dy + dh / 2f), 0);

        int ba = (int) ((35 + descriptionAlpha * 25) * descriptionAlpha);
        int bra = (int) ((70 + descriptionAlpha * 50) * descriptionAlpha);
        int bg = ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(20, 20, 20, ba),
                        ColorUtil.multAlpha(theme.clientColor(), .06f), descriptionAlpha),
                alphaV * descriptionAlpha);
        int br = ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(40, 40, 40, bra),
                        ColorUtil.multAlpha(theme.clientColor(), .12f), descriptionAlpha),
                alphaV * descriptionAlpha);
        int tc = ColorUtil.multAlpha(ColorUtil.WHITE, alphaV * descriptionAlpha);

        RenderUtil.Rounded.roundedRect(stack, dx, dy, dw, dh, bg, Round.of(6f));
        RenderUtil.Rounded.roundedOutline(stack, dx, dy, dw, dh, 0.65f, br, Round.of(6f));

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(dx + 3f, dy + 2f, dw - 6f, dh - 4f);
        Fonts.SF_MEDIUM.draw(stack, currentDescription, dx + 8f, dy + 5f, tc, 7f);
        ScissorExp.unset();
        ScissorExp.pop();
        stack.pop();
    }

    public boolean handleClick(double mouseX, double mouseY, int button, int guiLeft, int guiTop, int ySize) {
        if (mouseX < panelX || mouseX > panelX + PANEL_WIDTH || mouseY < panelY || mouseY > panelY + panelH)
            return false;

        float footerY = panelY + panelH - FOOTER_HEIGHT;
        float btnY = footerY + (FOOTER_HEIGHT - 14f) / 2f;
        float btnH = 14f;

        float clearW = Fonts.SF_MEDIUM.getWidth("Очистить", 7f) + 14f;
        float cx = panelX + (PANEL_WIDTH - clearW) / 2f;

        if (mouseX >= cx && mouseX < cx + clearW && mouseY >= btnY && mouseY < btnY + btnH) {
            AutoBuySpooky m = AutoBuySpooky.getInstance();
            if (m != null) m.clearBuyedItems();
            targetScrollOffset = 0;
            return true;
        }

        return true;
    }

    public boolean handleScroll(double mouseX, double mouseY, double delta, int guiLeft, int guiTop, int ySize) {
        if (mouseX < panelX || mouseX > panelX + PANEL_WIDTH || mouseY < panelY || mouseY > panelY + panelH)
            return false;

        AutoBuySpooky module = AutoBuySpooky.getInstance();
        if (module == null) return false;

        List<AutoBuySpooky.BuyedItem> items = module.getBuyedItems();
        float contentH = ySize - HEADER_HEIGHT - FOOTER_HEIGHT - PAD * 2;
        int visibleRows = (int) (contentH / ROW_HEIGHT);
        int maxScroll = Math.max(0, items.size() - visibleRows);

        targetScrollOffset -= delta;
        targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));
        return true;
    }
}