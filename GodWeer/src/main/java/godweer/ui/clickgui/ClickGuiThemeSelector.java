package godweer.ui.clickgui;

import com.mojang.blaze3d.platform.Window;

import godweer.managers.ThemeManager;
import godweer.utils.color.ColorUtils;
import godweer.utils.color.Theme;
import godweer.utils.math.HoveringUtils;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.ScissorUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ClickGuiThemeSelector {

    private static final float PANEL_W = 36f;
    private static final float PANEL_H = 110f;
    private static final float PANEL_RADIUS = 5f;
    private static final float BOX_SIZE = 8f;
    private static final float BOX_GAP = 3f;
    private static final float BOX_RADIUS = 2.5f;
    private static final int COLS = 3;
    private static final float PAD_X = 3f;
    private static final float PAD_Y = 4f;
    private static final float TITLE_H = 11f;

    private float scrollOffset;
    private float maxScroll;

    public float getScrollOffset() {
        return this.scrollOffset;
    }

    public void addScroll(double amount, float max) {
        this.scrollOffset = Math.max(0f, Math.min(max, this.scrollOffset - (float) amount * 6f));
    }

    public void render(GuiGraphics context, Window window, float offsetY, float alphaMul, int shadeColor) {
        if (context == null || window == null) {
            return;
        }

        List<Theme> themes = ThemeManager.getThemes();
        if (themes.isEmpty()) {
            return;
        }

        float panelX = window.getGuiScaledWidth() - PANEL_W - 4f;
        float panelY = window.getGuiScaledHeight() - PANEL_H - 4f;

        float rows = (float) Math.ceil(themes.size() / (float) COLS);
        float contentH = rows * (BOX_SIZE + BOX_GAP);
        this.maxScroll = Math.max(0f, contentH - (PANEL_H - TITLE_H - PAD_Y * 2f));

        RenderUtils.drawRoundedRect(context.pose(), panelX, panelY, PANEL_W, PANEL_H, PANEL_RADIUS,
                ColorUtils.rgba(0, 0, 0, 200));

        FontRenderer font = Fonts.Font5.get(10);
        if (font != null) {
            font.draw(context.pose(), "Themes", panelX + PAD_X, panelY + 9f,
                    ColorUtils.rgba(255, 255, 255, (int) (200 * alphaMul)));
        }

        float startX = panelX + PAD_X;
        float startY = panelY + TITLE_H + PAD_Y - this.scrollOffset;
        float clipY = panelY + TITLE_H;
        float clipH = PANEL_H - TITLE_H - PAD_Y;

        context.pose().pushPose();
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(panelX, clipY, PANEL_W, clipH);

        Theme selected = ThemeManager.getCurrent();
        for (int i = 0, size = themes.size(); i < size; i++) {
            Theme theme = themes.get(i);
            int col = i % COLS;
            int row = i / COLS;
            float bx = startX + col * (BOX_SIZE + BOX_GAP);
            float by = startY + row * (BOX_SIZE + BOX_GAP);

            if (by + BOX_SIZE < clipY || by > clipY + clipH) {
                continue;
            }

            if (theme == selected) {
                RenderUtils.drawRoundedRect(context.pose(), bx - 1f, by - 1f, BOX_SIZE + 2f, BOX_SIZE + 2f,
                        BOX_RADIUS + 0.5f, ColorUtils.setAlphaColor(-1, Math.max(1, (int) (200 * alphaMul))));
            }
            RenderUtils.drawRoundedRect(context.pose(), bx, by, BOX_SIZE, BOX_SIZE, BOX_RADIUS,
                    ColorUtils.applyAlpha(getThemeDisplayColor(theme), Math.max(0.55f, alphaMul)));
        }

        ScissorUtils.pop();
        context.pose().popPose();
    }

    public boolean handleClick(Window window, double mouseX, double mouseY, int button, float offsetY) {
        if (window == null || button != 0) {
            return false;
        }

        List<Theme> themes = ThemeManager.getThemes();
        if (themes.isEmpty()) {
            return false;
        }

        float panelX = window.getGuiScaledWidth() - PANEL_W - 4f;
        float panelY = window.getGuiScaledHeight() - PANEL_H - 4f;

        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, PANEL_W, PANEL_H)) {
            return false;
        }

        float startX = panelX + PAD_X;
        float startY = panelY + TITLE_H + PAD_Y - this.scrollOffset;

        for (int i = 0, size = themes.size(); i < size; i++) {
            int col = i % COLS;
            int row = i / COLS;
            float bx = startX + col * (BOX_SIZE + BOX_GAP);
            float by = startY + row * (BOX_SIZE + BOX_GAP);
            if (HoveringUtils.isHovered(mouseX, mouseY, bx, by, BOX_SIZE, BOX_SIZE)) {
                ThemeManager.setCurrent(themes.get(i));
                return true;
            }
        }
        return false;
    }

    public boolean handleScroll(Window window, double mouseX, double mouseY, double amount) {
        if (window == null) {
            return false;
        }

        float panelX = window.getGuiScaledWidth() - PANEL_W - 4f;
        float panelY = window.getGuiScaledHeight() - PANEL_H - 4f;

        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, PANEL_W, PANEL_H)) {
            return false;
        }

        addScroll(amount, this.maxScroll);
        return true;
    }

    private int getThemeDisplayColor(Theme theme) {
        int color = theme.getColor(0);
        if (ColorUtils.alpha(color) == 0) {
            return ColorUtils.rgba(220, 220, 220, 180);
        }
        return color;
    }
}
