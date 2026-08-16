package godweer.ui.clickgui;

import com.mojang.blaze3d.platform.Window;

import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.Setting;
import godweer.ui.MenuPanel;
import godweer.utils.color.ColorUtils;
import godweer.utils.math.HoveringUtils;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.ScissorUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiRenderer {

    private static final ModuleCategory[] CATEGORIES = ModuleCategory.values();

    private final ClickGuiState state;
    private final ClickGuiSettingRenderer settingRenderer;
    private final ClickGuiThemeSelector themeSelector;
    private final MenuPanel menuPanel;
    private final List<String> descriptionLines = new ArrayList<>();

    private Module describedModule;
    private float descriptionWidth = -1.0f;

    public ClickGuiRenderer(ClickGuiState state, ClickGuiSettingRenderer settingRenderer,
                            ClickGuiThemeSelector themeSelector, MenuPanel menuPanel) {
        this.state = state;
        this.settingRenderer = settingRenderer;
        this.themeSelector = themeSelector;
        this.menuPanel = menuPanel;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, Window window, float animationProgress) {
        if (window == null) {
            return;
        }

        float alphaMul = Mth.clamp(animationProgress, 0.0f, 1.0f);
        int colorTheme = ThemeManager.getColor();
        Module hoveredModule = null;

        themeSelector.update(window, state);

        ModuleCategory[] categories = CATEGORIES;
        for (int i = 0; i < categories.length; i++) {
            ModuleCategory category = categories[i];
            float panelX = ClickGuiLayout.getCategoryPanelX(state.getX(), i);
            Module categoryHoveredModule = renderCategoryPanel(context, mouseX, mouseY, panelX, category,
                    colorTheme, alphaMul);
            if (categoryHoveredModule != null) {
                hoveredModule = categoryHoveredModule;
            }
        }

        renderSearch(context, colorTheme, alphaMul);
        themeSelector.render(context, window, state, mouseX, mouseY, alphaMul);
        renderDescription(context, hoveredModule, alphaMul);
    }

    private Module renderCategoryPanel(GuiGraphics context, int mouseX, int mouseY, float panelX,
                                       ModuleCategory category, int colorTheme, float alphaMul) {
        float slideOffX = menuPanel != null ? menuPanel.getPanelSlideOffsetX(category) : 0f;
        float slideOffY = menuPanel != null ? menuPanel.getPanelSlideOffsetY(category) : 0f;
        if (category == ModuleCategory.PLAYER) {
            slideOffY += themeSelector.getPlayerOffsetY();
        }
        float panelAlphaScale = menuPanel != null ? menuPanel.getPanelAlpha(category) : 1f;

        float actualPanelX = panelX + slideOffX;
        float panelY = state.getY() + state.getRenderOffsetY() + slideOffY;
        float effectiveAlpha = alphaMul * panelAlphaScale;

        RenderUtils.drawRoundedRect(context.pose(), actualPanelX, panelY, ClickGuiLayout.getWidth(),
                ClickGuiLayout.getHeight(), 9f, ColorUtils.rgba(0, 0, 0, 240));

        String categoryName = category.getName();
        FontRenderer iconFont = icons(14);
        FontRenderer titleFont = issue(15);

        if (iconFont != null && titleFont != null) {
            float titleCenterX = actualPanelX + ClickGuiLayout.getWidth() * 0.5f;
            iconFont.drawCenteredString(context.pose(), category.getIcons(),
                    titleCenterX - (titleFont.getWidth(categoryName) / 2f) - 4, panelY + 13,
                    alpha(colorTheme, effectiveAlpha));
            titleFont.drawCenteredString(context.pose(), categoryName, titleCenterX + 2, panelY + 12,
                    alpha(-1, effectiveAlpha));
        }

        float contentY = ClickGuiLayout.getContentY(panelY);
        float contentHeight = ClickGuiLayout.getContentHeight();
        state.clampScroll(category, contentHeight);
        float moduleY = contentY + state.getScroll(category);
        Module hoveredModule = null;

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(actualPanelX, contentY, ClickGuiLayout.getWidth(), contentHeight + 1);

        List<Module> modules = state.getModules(category);
        for (int i = 0, size = modules.size(); i < size; i++) {
            Module module = modules.get(i);
            float openProgress = state.getOpenProgress(module);
            float moduleHeight = ClickGuiLayout.getModuleHeight(module, openProgress);

            if (moduleY + moduleHeight + ClickGuiLayout.MODULE_GAP >= contentY && moduleY <= contentY + contentHeight) {
                Module moduleHover = renderModule(context, mouseX, mouseY, actualPanelX, moduleY, module,
                        openProgress, moduleHeight, colorTheme, effectiveAlpha);
                if (moduleHover != null) {
                    hoveredModule = moduleHover;
                }
            }

            moduleY += ClickGuiLayout.MODULE_GAP + moduleHeight;
        }

        ScissorUtils.pop();
        return hoveredModule;
    }

    private Module renderModule(GuiGraphics context, int mouseX, int mouseY, float panelX, float moduleY,
                                Module module, float openProgress, float moduleHeight, int colorTheme, float alphaMul) {
        List<Setting> settings = module.getSettings();
        renderModuleBackground(context, panelX, moduleY, moduleHeight);

        String moduleName = module.getDisplayName();
        String bindText = state.getBindingModule() == module ? " [...]" : "";

        int nameColor = module.isEnable()
                ? alpha(-1, alphaMul)
                : alpha(ColorUtils.rgba(255, 255, 255, 170), alphaMul);
        int bindColor = module.isEnable()
                ? alpha(ColorUtils.rgba(255, 255, 255, 150), alphaMul)
                : alpha(ColorUtils.rgba(255, 255, 255, 100), alphaMul);

        FontRenderer nameFont = issue(15);
        if (nameFont != null) {
            nameFont.draw(context.pose(), moduleName, panelX + ClickGuiLayout.SETTING_LEFT, moduleY + 6.5f, nameColor);
            FontRenderer bindFont = issue(11);
            if (!bindText.isEmpty() && bindFont != null) {
                float nameWidth = nameFont.getWidth(moduleName);
                bindFont.draw(context.pose(), bindText,
                        panelX + ClickGuiLayout.SETTING_LEFT + nameWidth, moduleY + 9, bindColor);
            }
        }

        if (!settings.isEmpty() && ClickGuiLayout.hasVisibleSettings(settings)) {
            renderModuleDots(context, panelX, moduleY, module, module.isEnable(), alphaMul);
        }

        if (!settings.isEmpty()) {
            settingRenderer.render(context, module, panelX, moduleY, openProgress, colorTheme, mouseX, mouseY, state);
        }

        if (HoveringUtils.isHovered(mouseX, mouseY, panelX + ClickGuiLayout.MODULE_PADDING, moduleY,
                ClickGuiLayout.getModuleInnerWidth(), moduleHeight)) {
            return module;
        }
        return null;
    }

    private void renderModuleBackground(GuiGraphics context, float panelX, float moduleY, float moduleHeight) {
        RenderUtils.drawRoundedRect(context.pose(), panelX + ClickGuiLayout.MODULE_PADDING, moduleY - 0.5f,
                ClickGuiLayout.getModuleInnerWidth(), moduleHeight + 1, 5f, ColorUtils.rgba(0, 0, 0, 30));
    }

    // Три точки, вращающиеся вокруг центра при раскрытии модуля.
    private void renderModuleDots(GuiGraphics context, float panelX, float moduleY, Module module,
                                  boolean enabled, float alphaMul) {
        int dotsColor = enabled
                ? alpha(ColorUtils.rgba(255, 255, 255, 220), alphaMul)
                : alpha(ColorUtils.rgba(255, 255, 255, 100), alphaMul);

        float dotsX = panelX + ClickGuiLayout.getWidth() - 12.5f;
        float baseY = moduleY + 10f;
        float spacing = 2f;
        float radius = 2.1f;
        float bottomXOffset = 2.1f;

        float angle = state.updateDotsRotation(module, module.isOpen() ? (float) (Math.PI / 2f) : 0f);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        drawDot(context, dotsX, baseY, 0f, -spacing, cos, sin, radius, dotsColor);
        drawDot(context, dotsX, baseY, -bottomXOffset, spacing, cos, sin, radius, dotsColor);
        drawDot(context, dotsX, baseY, bottomXOffset, spacing, cos, sin, radius, dotsColor);
    }

    private void drawDot(GuiGraphics context, float originX, float originY, float offX, float offY,
                         float cos, float sin, float radius, int color) {
        float rx = offX * cos - offY * sin;
        float ry = offX * sin + offY * cos;
        RenderUtils.drawRoundCircle(context.pose(), originX + rx, originY + ry, radius, color);
    }

    private void renderSearch(GuiGraphics context, int colorTheme, float alphaMul) {
        float searchY = ClickGuiLayout.getSearchY(state.getY() + state.getRenderOffsetY());
        float searchW = ClickGuiLayout.getWidth();
        float searchX = ClickGuiLayout.getSearchX(state.getX());
        float searchH = ClickGuiLayout.SEARCH_HEIGHT;
        float selectionPaddingLeft = 3.0f;
        float selectionPaddingRight = 1.5f;

        RenderUtils.drawRoundedRect(context.pose(), searchX, searchY, searchW, searchH, 6f,
                ColorUtils.rgba(0, 0, 0, 220));

        String query = state.getSearchText();
        String text = query.isEmpty() ? "Search..." : query;
        int textColor = query.isEmpty()
                ? alpha(ColorUtils.rgba(255, 255, 255, 110), alphaMul)
                : alpha(ColorUtils.rgba(255, 255, 255, 230), alphaMul);

        float iconX = searchX + ClickGuiLayout.SEARCH_ICON_X;
        float textX = searchX + ClickGuiLayout.SEARCH_TEXT_X;
        float textY = searchY + 6.2f;

        FontRenderer iconFont = iconsNew(18);
        if (iconFont != null) {
            iconFont.draw(context.pose(), "l", iconX + 2, searchY + 6.5f, alpha(colorTheme, alphaMul));
        }

        FontRenderer font = Fonts.Font5.get(14);
        if (font == null) {
            return;
        }

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(textX - selectionPaddingLeft, searchY,
                searchW - ClickGuiLayout.SEARCH_TEXT_X - ClickGuiLayout.SEARCH_RIGHT_PADDING + selectionPaddingLeft,
                searchH);

        if (!query.isEmpty() && state.hasSearchSelection()) {
            int selectionStart = state.getSearchSelectionStart();
            int selectionEnd = state.getSearchSelectionEnd();
            float selectedX = textX + font.getWidth(query.substring(0, selectionStart)) - selectionPaddingLeft;
            float selectedW = font.getWidth(query.substring(selectionStart, selectionEnd))
                    + selectionPaddingLeft + selectionPaddingRight;
            RenderUtils.drawRoundedRect(context.pose(), selectedX, searchY + 3.8f, selectedW, 10.5f, 1.5f,
                    alpha(ColorUtils.rgba(42, 115, 255, 155), alphaMul));
        }

        font.draw(context.pose(), text, textX, textY + 1, textColor);

        if (state.isSearchActive() && (System.currentTimeMillis() / 500L) % 2L == 0L) {
            float cursorX = textX + font.getWidth(
                    query.substring(0, Math.min(state.getSearchCursor(), query.length())));
            RenderUtils.drawRoundedRect(context.pose(), cursorX + 1f, searchY + 4.5f, 0.8f, 9f, 0f,
                    alpha(ColorUtils.applyAlpha(colorTheme, 0.9f), alphaMul));
        }

        ScissorUtils.pop();
    }

    private void renderDescription(GuiGraphics context, Module hoveredModule, float alphaMul) {
        if (hoveredModule == null) {
            return;
        }

        String description = hoveredModule.getDisplayDescription();
        if (description == null || description.isBlank()
                || "NULLABLE".equalsIgnoreCase(description) || "desc".equalsIgnoreCase(description)) {
            return;
        }

        FontRenderer descriptionFont = Fonts.Font5.get(13);
        if (descriptionFont == null) {
            return;
        }

        float maxWidth = ClickGuiLayout.getWidth() - 8.0f;
        if (describedModule != hoveredModule || descriptionWidth != maxWidth) {
            describedModule = hoveredModule;
            descriptionWidth = maxWidth;
            wrapDescription(descriptionFont, description, maxWidth);
        }
        if (descriptionLines.isEmpty()) {
            return;
        }

        float lineHeight = descriptionFont.getHeight() - 2.0f;
        float boxHeight = descriptionLines.size() * lineHeight;
        float visualPanelX = ClickGuiLayout.getCategoryPanelX(state.getX(), ModuleCategory.RENDER.ordinal());
        float centerX = visualPanelX + ClickGuiLayout.getWidth() * 0.5f;
        float panelY = state.getY() + state.getRenderOffsetY();
        float startY = panelY - boxHeight - 5.0f;

        for (int i = 0, size = descriptionLines.size(); i < size; i++) {
            descriptionFont.drawCenteredString(context.pose(), descriptionLines.get(i), centerX,
                    startY + i * lineHeight, ColorUtils.applyAlpha(-1, alphaMul));
        }
    }

    private void wrapDescription(FontRenderer font, String text, float maxWidth) {
        descriptionLines.clear();

        String[] words = text.trim().split("\\s+");
        if (words.length == 0) {
            return;
        }

        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (font.getWidth(candidate) <= maxWidth || currentLine.isEmpty()) {
                currentLine.setLength(0);
                currentLine.append(candidate);
                continue;
            }
            descriptionLines.add(currentLine.toString());
            currentLine.setLength(0);
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) {
            descriptionLines.add(currentLine.toString());
        }
    }

    private FontRenderer issue(int size) {
        return Fonts.Font2.get(size);
    }

    private FontRenderer icons(int size) {
        return Fonts.Icon.get(size);
    }

    private FontRenderer iconsNew(int size) {
        return Fonts.Icon1.get(size);
    }

    private int alpha(int color, float alphaMul) {
        return ColorUtils.applyAlpha(color, alphaMul);
    }

}
