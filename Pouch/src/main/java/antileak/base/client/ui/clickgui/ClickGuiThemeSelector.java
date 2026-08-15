package antileak.base.client.ui.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import antileak.base.Punch;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.math.HoveringUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.api.utils.scissor.ScissorUtils;
import antileak.base.api.storages.implement.ThemeStorage.Themes;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.api.storages.implement.ThemeStorage.Themes;

import java.util.List;

public class ClickGuiThemeSelector {

    private static final float PANEL_W = 36f;
    private static final float PANEL_H = 110f;
    private static final float PANEL_RADIUS = 5f;
    private static final float BOX_SIZE = 8f;
    private static final float BOX_GAP = 3f;
    private static final float BOX_RADIUS = 2.5f;
    private static final float COLS = 3f;
    private static final float PAD_X = 3f;
    private static final float PAD_Y = 4f;
    private static final float TITLE_H = 11f;

    private float scrollOffset = 0f;
    private float maxScroll = 0f;

    public float getScrollOffset() { return scrollOffset; }
    public void addScroll(double amount, float max) {
        scrollOffset = Math.max(0, Math.min(max, scrollOffset - (float)amount * 6f));
    }

    public void render(DrawContext context, Window window, float offsetY, float alphaMul, int shadeColor) {
        if (context == null || window == null) return;

        List<Themes> themes = Punch.INSTANCE.themeStorage.getThemeList();
        if (themes == null || themes.isEmpty()) return;

        float panelX = window.getScaledWidth() - PANEL_W - 4f;
        float panelY = window.getScaledHeight() - PANEL_H - 4f;

        float rows = (float)Math.ceil(themes.size() / COLS);
        float contentH = rows * (BOX_SIZE + BOX_GAP);
        maxScroll = Math.max(0, contentH - (PANEL_H - TITLE_H - PAD_Y * 2f));

        RenderUtils.drawRoundedRect(context.getMatrices(), panelX, panelY, PANEL_W, PANEL_H, PANEL_RADIUS, ColorUtils.rgba(0, 0, 0, 200));

        Font font = Fonts.getFont("suisse", 10);
        if (font != null) {
            font.draw(context.getMatrices(), "Themes", panelX + PAD_X, panelY + 9f, ColorUtils.rgba(255, 255, 255, (int)(200 * alphaMul)));
        }

        float startX = panelX + PAD_X;
        float startY = panelY + TITLE_H + PAD_Y - scrollOffset;
        float clipY = panelY + TITLE_H;
        float clipH = PANEL_H - TITLE_H - PAD_Y;

        context.getMatrices().push();
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates((int)panelX, (int)clipY, (int)PANEL_W, (int)clipH);

        Themes selected = Punch.INSTANCE.themeStorage.getThemes();
        for (int i = 0; i < themes.size(); i++) {
            Themes theme = themes.get(i);
            int col = i % (int)COLS;
            int row = i / (int)COLS;
            float bx = startX + col * (BOX_SIZE + BOX_GAP);
            float by = startY + row * (BOX_SIZE + BOX_GAP);

            if (by + BOX_SIZE < clipY || by > clipY + clipH) continue;

            if (theme == selected) {
                RenderUtils.drawRoundedRect(context.getMatrices(), bx - 1f, by - 1f, BOX_SIZE + 2f, BOX_SIZE + 2f, BOX_RADIUS + 0.5f, ColorUtils.setAlphaColor(-1, Math.max(1, (int)(200 * alphaMul))));
            }
            RenderUtils.drawRoundedRect(context.getMatrices(), bx, by, BOX_SIZE, BOX_SIZE, BOX_RADIUS, ColorUtils.applyAlpha(getThemeDisplayColor(theme), Math.max(0.55f, alphaMul)));
        }

        ScissorUtils.pop();
        context.getMatrices().pop();
    }

    public boolean handleClick(Window window, double mouseX, double mouseY, int button, float offsetY) {
        if (window == null || button != 0) return false;

        List<Themes> themes = Punch.INSTANCE.themeStorage.getThemeList();
        if (themes == null || themes.isEmpty()) return false;

        float panelX = window.getScaledWidth() - PANEL_W - 4f;
        float panelY = window.getScaledHeight() - PANEL_H - 4f;

        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, PANEL_W, PANEL_H)) return false;

        float startX = panelX + PAD_X;
        float startY = panelY + TITLE_H + PAD_Y - scrollOffset;

        for (int i = 0; i < themes.size(); i++) {
            int col = i % (int)COLS;
            int row = i / (int)COLS;
            float bx = startX + col * (BOX_SIZE + BOX_GAP);
            float by = startY + row * (BOX_SIZE + BOX_GAP);
            if (HoveringUtils.isHovered(mouseX, mouseY, bx, by, BOX_SIZE, BOX_SIZE)) {
                Punch.INSTANCE.themeStorage.setThemes(themes.get(i));
                return true;
            }
        }
        return false;
    }

    public boolean handleScroll(Window window, double mouseX, double mouseY, double amount) {
        if (window == null) return false;

        List<Themes> themes = Punch.INSTANCE.themeStorage.getThemeList();
        if (themes == null || themes.isEmpty()) return false;

        float panelX = window.getScaledWidth() - PANEL_W - 4f;
        float panelY = window.getScaledHeight() - PANEL_H - 4f;

        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, PANEL_W, PANEL_H)) return false;

        addScroll(amount, maxScroll);
        return true;
    }

    private int getThemeDisplayColor(Themes theme) {
        int color = theme.getTheme().getColor(0);
        if (ColorUtils.alpha(color) == 0) {
            return ColorUtils.rgba(220, 220, 220, 180);
        }
        return color;
    }
}
