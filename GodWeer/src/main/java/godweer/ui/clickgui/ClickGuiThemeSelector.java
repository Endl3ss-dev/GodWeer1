package godweer.ui.clickgui;

import com.mojang.blaze3d.platform.Window;

import godweer.managers.ThemeManager;
import godweer.modules.ModuleCategory;
import godweer.utils.animation.AnimationUtils;
import godweer.utils.animation.Easings;
import godweer.utils.color.ColorUtils;
import godweer.utils.color.Theme;
import godweer.utils.math.HoveringUtils;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.ScissorUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.List;

public final class ClickGuiThemeSelector {

    private static final int PLAYER_CATEGORY_INDEX = ModuleCategory.PLAYER.ordinal();
    private static final int DRAG_NONE = 0;
    private static final int DRAG_OPEN = 1;
    private static final int DRAG_CLOSE = 2;

    private static final float PANEL_RADIUS = 9.0f;
    private static final float PANEL_PADDING = 4.0f;
    private static final float TITLE_HEIGHT = 25.0f;
    private static final float ROW_HEIGHT = 26.0f;
    private static final float ROW_GAP = 2.0f;
    private static final float COLOR_SIZE = 12.0f;
    private static final float SELECTED_MARK_WIDTH = 2.0f;
    private static final float SCROLLBAR_WIDTH = 2.0f;
    private static final float SCROLLBAR_GAP = 3.0f;
    private static final float SCROLLBAR_MIN_HEIGHT = 18.0f;
    private static final float PLAYER_VISIBLE_HEIGHT = 24.0f;
    private static final float OFFSCREEN_MARGIN = 4.0f;
    private static final float DRAG_THRESHOLD = 30.0f;
    private static final float GRIP_WIDTH = 28.0f;
    private static final float GRIP_HEIGHT = 3.0f;
    private static final float SCROLL_STEP = 24.0f;

    private final AnimationUtils transition = new AnimationUtils(0.0f, 7.5f, Easings.CUBIC_IN_OUT);
    private final AnimationUtils scrollAnimation = new AnimationUtils(0.0f, 10.0f, Easings.CUBIC_OUT);
    private final AnimationUtils[] hoverAnimations;

    private boolean themeOpen;
    private float transitionProgress;
    private float playerTransitionDistance;
    private float themeTransitionDistance;
    private float scrollTarget;
    private float scrollOffset;
    private int dragMode;
    private double dragStartY;
    private boolean dragTriggered;

    public ClickGuiThemeSelector() {
        int themeCount = ThemeManager.getThemes().size();
        hoverAnimations = new AnimationUtils[themeCount];
        for (int i = 0; i < themeCount; i++) {
            hoverAnimations[i] = new AnimationUtils(0.0f, 12.0f, Easings.CUBIC_OUT);
        }
    }

    public void update(Window window, ClickGuiState state) {
        float basePanelY = getBasePanelY(state);
        float panelHeight = ClickGuiLayout.getHeight();
        playerTransitionDistance = Math.max(0.0f,
                window.getGuiScaledHeight() - PLAYER_VISIBLE_HEIGHT - basePanelY);
        themeTransitionDistance = basePanelY + panelHeight + OFFSCREEN_MARGIN;

        transition.update(themeOpen ? 1.0f : 0.0f);
        transitionProgress = Mth.clamp(transition.getValue(), 0.0f, 1.0f);
        scrollAnimation.update(scrollTarget);
        scrollOffset = scrollAnimation.getValue();
    }

    public float getPlayerOffsetY() {
        return transitionProgress * playerTransitionDistance;
    }

    public boolean isPlayerInteractive() {
        return !themeOpen && transitionProgress <= 0.001f && dragMode == DRAG_NONE;
    }

    public void render(GuiGraphics context, Window window, ClickGuiState state,
                       double mouseX, double mouseY, float alphaMul) {
        if (context == null || window == null) {
            return;
        }

        if (!themeOpen && transitionProgress <= 0.001f) {
            renderGrip(context, getPanelX(state), getBasePanelY(state) - 7.0f, alphaMul);
            return;
        }

        float panelX = getPanelX(state);
        float panelY = getThemePanelY(state);
        float panelWidth = ClickGuiLayout.getWidth();
        float panelHeight = ClickGuiLayout.getHeight();

        RenderUtils.drawRoundedRect(context.pose(), panelX, panelY, panelWidth, panelHeight, PANEL_RADIUS,
                ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 240), alphaMul));

        FontRenderer titleFont = Fonts.Font5.get(14);
        if (titleFont != null) {
            titleFont.drawCenteredString(context.pose(), "Themes", panelX + panelWidth * 0.5f, panelY + 11.0f,
                    ColorUtils.applyAlpha(-1, alphaMul));
        }

        renderThemes(context, panelX, panelY, panelWidth, panelHeight, mouseX, mouseY, alphaMul);

        if (themeOpen && transitionProgress >= 0.98f) {
            renderGrip(context, panelX, getPlayerPanelY(state) + 5.0f, alphaMul);
        }
    }

    public boolean handleClick(Window window, ClickGuiState state, double mouseX, double mouseY, int button) {
        if (window == null || button != 0) {
            return false;
        }

        float panelX = getPanelX(state);
        float panelWidth = ClickGuiLayout.getWidth();

        if (!themeOpen && transitionProgress <= 0.001f) {
            float panelY = getBasePanelY(state);
            if (HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY - 16.0f, panelWidth, 24.0f)) {
                beginDrag(DRAG_OPEN, mouseY);
                return true;
            }
            return false;
        }

        if (!themeOpen || transitionProgress < 0.98f) {
            return false;
        }

        float playerPanelY = getPlayerPanelY(state);
        float visibleHeight = Math.max(0.0f, window.getGuiScaledHeight() - playerPanelY);
        if (visibleHeight > 0.0f && HoveringUtils.isHovered(mouseX, mouseY, panelX, playerPanelY - 4.0f,
                panelWidth, Math.min(36.0f, visibleHeight + 4.0f))) {
            beginDrag(DRAG_CLOSE, mouseY);
            return true;
        }

        float panelY = getThemePanelY(state);
        float panelHeight = ClickGuiLayout.getHeight();
        float listY = panelY + TITLE_HEIGHT;
        float listHeight = getListHeight(panelHeight);
        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX + PANEL_PADDING, listY,
                getRowWidth(panelWidth), listHeight)) {
            return HoveringUtils.isHovered(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight);
        }

        List<Theme> themes = ThemeManager.getThemes();
        for (int i = 0, size = themes.size(); i < size; i++) {
            float rowY = listY + i * (ROW_HEIGHT + ROW_GAP) - scrollOffset;
            if (rowY + ROW_HEIGHT < listY || rowY > listY + listHeight) {
                continue;
            }
            if (HoveringUtils.isHovered(mouseX, mouseY, panelX + PANEL_PADDING, rowY,
                    getRowWidth(panelWidth), ROW_HEIGHT)) {
                ThemeManager.setCurrent(themes.get(i));
                return true;
            }
        }

        return true;
    }

    public boolean handleDrag(double mouseY, int button) {
        if (button != 0 || dragMode == DRAG_NONE) {
            return false;
        }

        if (!dragTriggered) {
            if (dragMode == DRAG_OPEN && mouseY - dragStartY >= DRAG_THRESHOLD) {
                themeOpen = true;
                dragTriggered = true;
            } else if (dragMode == DRAG_CLOSE && dragStartY - mouseY >= DRAG_THRESHOLD) {
                themeOpen = false;
                dragTriggered = true;
            }
        }
        return true;
    }

    public boolean handleRelease(int button) {
        if (button != 0 || dragMode == DRAG_NONE) {
            return false;
        }

        dragMode = DRAG_NONE;
        dragTriggered = false;
        return true;
    }

    public boolean handleScroll(ClickGuiState state, double mouseX, double mouseY, double amount) {
        if (!themeOpen || transitionProgress < 0.98f) {
            return false;
        }

        float panelX = getPanelX(state);
        float panelY = getThemePanelY(state);
        float panelWidth = ClickGuiLayout.getWidth();
        float panelHeight = ClickGuiLayout.getHeight();
        float listY = panelY + TITLE_HEIGHT;
        float listHeight = getListHeight(panelHeight);
        if (!HoveringUtils.isHovered(mouseX, mouseY, panelX + PANEL_PADDING, listY,
                panelWidth - PANEL_PADDING * 2.0f, listHeight)) {
            return false;
        }

        float maxScroll = getMaxScroll(panelHeight, ThemeManager.getThemes().size());
        scrollTarget = Mth.clamp(scrollTarget - (float) amount * SCROLL_STEP, 0.0f, maxScroll);
        return true;
    }

    private void renderThemes(GuiGraphics context, float panelX, float panelY, float panelWidth,
                              float panelHeight, double mouseX, double mouseY, float alphaMul) {
        List<Theme> themes = ThemeManager.getThemes();
        if (themes.isEmpty()) {
            return;
        }

        FontRenderer nameFont = Fonts.Font5.get(12);
        FontRenderer codeFont = Fonts.Font5.get(11);
        if (nameFont == null || codeFont == null) {
            return;
        }

        float listY = panelY + TITLE_HEIGHT;
        float listHeight = getListHeight(panelHeight);
        float rowWidth = getRowWidth(panelWidth);
        float maxScroll = getMaxScroll(panelHeight, themes.size());
        if (scrollTarget > maxScroll) {
            scrollTarget = maxScroll;
        }
        Theme selected = ThemeManager.getCurrent();

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(panelX + PANEL_PADDING, listY,
                panelWidth - PANEL_PADDING * 2.0f, listHeight);

        for (int i = 0, size = themes.size(); i < size; i++) {
            Theme theme = themes.get(i);
            float rowY = listY + i * (ROW_HEIGHT + ROW_GAP) - scrollOffset;
            if (rowY + ROW_HEIGHT < listY || rowY > listY + listHeight) {
                continue;
            }

            boolean hovered = HoveringUtils.isHovered(mouseX, mouseY, panelX + PANEL_PADDING, rowY,
                    rowWidth, ROW_HEIGHT);
            AnimationUtils hoverAnimation = hoverAnimations[i];
            hoverAnimation.update(hovered ? 1.0f : 0.0f);
            float hoverProgress = hoverAnimation.getValue();
            boolean selectedTheme = theme == selected;
            int primaryColor = getThemeDisplayColor(theme, 0);

            renderThemeBackground(context, panelX, rowY, rowWidth, primaryColor,
                    selectedTheme, hoverProgress, alphaMul);

            renderThemeSwatch(context, panelX + PANEL_PADDING + 5.0f,
                    rowY + (ROW_HEIGHT - COLOR_SIZE) * 0.5f, primaryColor,
                    getThemeDisplayColor(theme, 1), alphaMul);

            float textCenterX = panelX + PANEL_PADDING + rowWidth * 0.5f;
            int nameColor = ColorUtils.applyAlpha(-1, alphaMul);
            int codeColor = selectedTheme
                    ? ColorUtils.applyAlpha(primaryColor, alphaMul)
                    : ColorUtils.applyAlpha(ColorUtils.rgba(155, 155, 165, 255), alphaMul);
            nameFont.drawCenteredString(context.pose(), theme.getName(), textCenterX, rowY + 5.0f, nameColor);
            codeFont.drawCenteredString(context.pose(), theme.getHexCode(), textCenterX, rowY + 16.5f, codeColor);

            if (selectedTheme) {
                float markX = panelX + PANEL_PADDING + rowWidth - 6.0f;
                RenderUtils.drawRoundedRect(context.pose(), markX, rowY + 7.0f,
                        SELECTED_MARK_WIDTH, ROW_HEIGHT - 14.0f, 1.0f,
                        ColorUtils.applyAlpha(primaryColor, alphaMul));
            }
        }

        renderScrollbar(context, panelX, panelWidth, listY, listHeight, maxScroll, themes.size(), alphaMul);
        ScissorUtils.pop();
    }

    private void renderThemeBackground(GuiGraphics context, float panelX, float rowY, float rowWidth,
                                       int primaryColor, boolean selected, float hoverProgress, float alphaMul) {
        int surfaceAlpha = selected ? 30 : 13 + Math.round(hoverProgress * 17.0f);
        RenderUtils.drawRoundedRect(context.pose(), panelX + PANEL_PADDING, rowY, rowWidth, ROW_HEIGHT,
                4.0f, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, surfaceAlpha), alphaMul));

        if (selected) {
            int borderAlpha = 135 + Math.round(hoverProgress * 60.0f);
            RenderUtils.drawRoundedRect(context.pose(), panelX + PANEL_PADDING, rowY, rowWidth, ROW_HEIGHT,
                    4.0f, ColorUtils.applyAlpha(ColorUtils.setAlphaColor(primaryColor, borderAlpha), alphaMul));
            RenderUtils.drawRoundedRect(context.pose(), panelX + PANEL_PADDING + 0.8f, rowY + 0.8f,
                    rowWidth - 1.6f, ROW_HEIGHT - 1.6f, 3.4f,
                    ColorUtils.applyAlpha(ColorUtils.rgba(12, 12, 15, 238), alphaMul));
        } else if (hoverProgress > 0.001f) {
            int accentAlpha = Math.max(1, Math.round(hoverProgress * 24.0f));
            RenderUtils.drawRoundedRect(context.pose(), panelX + PANEL_PADDING + 0.8f, rowY + 0.8f,
                    rowWidth - 1.6f, ROW_HEIGHT - 1.6f, 3.4f,
                    ColorUtils.applyAlpha(ColorUtils.setAlphaColor(primaryColor, accentAlpha), alphaMul));
        }
    }

    private void renderScrollbar(GuiGraphics context, float panelX, float panelWidth, float listY,
                                 float listHeight, float maxScroll, int themeCount, float alphaMul) {
        if (maxScroll <= 0.0f) {
            return;
        }

        float contentHeight = themeCount * ROW_HEIGHT + Math.max(0, themeCount - 1) * ROW_GAP;
        float thumbHeight = Math.max(SCROLLBAR_MIN_HEIGHT, listHeight * listHeight / contentHeight);
        float travel = listHeight - thumbHeight;
        float thumbY = listY + Mth.clamp(scrollOffset / maxScroll, 0.0f, 1.0f) * travel;
        float scrollbarX = panelX + panelWidth - PANEL_PADDING - SCROLLBAR_WIDTH;

        RenderUtils.drawRoundedRect(context.pose(), scrollbarX, listY, SCROLLBAR_WIDTH, listHeight,
                1.0f, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 18), alphaMul));
        RenderUtils.drawRoundedRect(context.pose(), scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight,
                1.0f, ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 105), alphaMul));
    }

    private void renderThemeSwatch(GuiGraphics context, float x, float y, int primaryColor,
                                   int secondaryColor, float alphaMul) {
        RenderUtils.drawRect(context.pose(), x - 0.5f, y - 0.5f, COLOR_SIZE + 1.0f, COLOR_SIZE + 1.0f,
                ColorUtils.applyAlpha(ColorUtils.rgba(255, 255, 255, 45), alphaMul));

        // Диагональный сплит: верхне-левый треугольник — основной цвет темы, нижне-правый — вторичный
        int primary = ColorUtils.applyAlpha(primaryColor, alphaMul);
        int secondary = ColorUtils.applyAlpha(secondaryColor, alphaMul);
        RenderUtils.drawTriangle(context.pose(), x, y, x + COLOR_SIZE, y, x, y + COLOR_SIZE, primary);
        RenderUtils.drawTriangle(context.pose(), x + COLOR_SIZE, y, x + COLOR_SIZE, y + COLOR_SIZE,
                x, y + COLOR_SIZE, secondary);
    }

    private void renderGrip(GuiGraphics context, float panelX, float gripY, float alphaMul) {
        float gripX = panelX + (ClickGuiLayout.getWidth() - GRIP_WIDTH) * 0.5f;
        RenderUtils.drawRoundedRect(context.pose(), gripX, gripY, GRIP_WIDTH, GRIP_HEIGHT, 1.5f,
                ColorUtils.applyAlpha(ColorUtils.rgba(220, 220, 225, 180), alphaMul));
    }

    private void beginDrag(int mode, double mouseY) {
        dragMode = mode;
        dragStartY = mouseY;
        dragTriggered = false;
    }

    private float getPanelX(ClickGuiState state) {
        return ClickGuiLayout.getCategoryPanelX(state.getX(), PLAYER_CATEGORY_INDEX);
    }

    private float getBasePanelY(ClickGuiState state) {
        return state.getY() + state.getRenderOffsetY();
    }

    private float getThemePanelY(ClickGuiState state) {
        return getBasePanelY(state) - (1.0f - transitionProgress) * themeTransitionDistance;
    }

    private float getPlayerPanelY(ClickGuiState state) {
        return getBasePanelY(state) + getPlayerOffsetY();
    }

    private float getListHeight(float panelHeight) {
        return panelHeight - TITLE_HEIGHT - PANEL_PADDING;
    }

    private float getRowWidth(float panelWidth) {
        return panelWidth - PANEL_PADDING * 2.0f - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
    }

    private float getMaxScroll(float panelHeight, int themeCount) {
        float contentHeight = themeCount * ROW_HEIGHT + Math.max(0, themeCount - 1) * ROW_GAP;
        return Math.max(0.0f, contentHeight - getListHeight(panelHeight));
    }

    private int getThemeDisplayColor(Theme theme, int colorIndex) {
        if (theme.isRainbow()) {
            return theme.getColor(colorIndex * 180);
        }

        int[] colors = theme.getColors();
        int color = colors.length == 0 ? -1 : colors[Math.min(colorIndex, colors.length - 1)];
        if (ColorUtils.alpha(color) == 0) {
            return ColorUtils.rgba(220, 220, 220, 180);
        }
        return color;
    }
}
