package org.privates.client.screen.csgo;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Getter
public class CsGoScreen extends Screen implements IMinecraft {
    private final ArrayList<CategoryPanel> categories = new ArrayList<>();
    private final Namespaced logoLocation = new Namespaced("texture/logo.png");
    private float x, y, width, height;
    @Setter
    private CategoryPanel selectedCategory;
    private float alpha, alphaV;
    private String searchQuery = "";
    private boolean searchActive = false;
    private float searchBarAlpha = 0f;
    private float blurProgress = 0f;
    private float scaleProgress = 0f;
    private boolean closing = false;

    public CsGoScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        this.width = 650;
        this.height = 400;
        this.x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
        this.y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        if (categories.isEmpty()) {
            for (Category c : Category.values()) {
                CategoryPanel category = new CategoryPanel(this, c, x + 11, 0);
                categories.add(category);
            }
            selectedCategory = categories.get(0);
        }
        alpha = 1;
        closing = false;
        blurProgress = 0f;
        scaleProgress = 0f;
        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        alphaV = Mathf.fast(alphaV, alpha, 12);
        searchBarAlpha = Mathf.fast(searchBarAlpha, searchActive ? 1f : 0.6f, 12);
        blurProgress = Mathf.fast(blurProgress, closing ? 0f : 1f, 10);
        scaleProgress = Mathf.fast(scaleProgress, closing ? 0f : 1f, 14);

        if (alphaV <= .01f && alpha == 0) {
            super.closeScreen();
            return;
        }

        float finalScale = 0.85f + (scaleProgress * 0.15f);

        stack.push();
        stack.translate(mc.getMainWindow().getScaledWidth() / 2f, mc.getMainWindow().getScaledHeight() / 2f, 0);
        stack.scale(finalScale, finalScale, 1);
        stack.translate(-mc.getMainWindow().getScaledWidth() / 2f, -mc.getMainWindow().getScaledHeight() / 2f, 0);

        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.getColor(255, 255, 255, alpha(.5f)), Round.of(8)));
        BlurRenderer2.draw((int) (25 * blurProgress));

        int bgStartColor = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, 250), alphaV);
        int bgEndColor = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 250), alphaV);
        RenderUtil.Rounded.gradientRect(stack, x, y, width, height, bgStartColor, bgEndColor, bgEndColor, bgStartColor, Round.of(8));

        int borderColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 60), alphaV);
        RenderUtil.Rounded.roundedOutline(stack, x, y, width, height, 1, borderColor, Round.of(8));

        renderSidebar(stack, mouseX, mouseY);
        renderDividers(stack);
        renderHeader(stack, mouseX, mouseY);
        renderContent(stack, mouseX, mouseY);

        stack.pop();

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderSidebar(MatrixStack stack, float mouseX, float mouseY) {
        float sidebarX = x + 1;
        float sidebarY = y + 1;
        float sidebarWidth = 70;
        float sidebarHeight = height - 2;

        int sidebarBgStart = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, 200), alphaV);
        int sidebarBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 200), alphaV);
        RenderUtil.Rounded.gradientRect(stack, sidebarX, sidebarY, sidebarWidth, sidebarHeight, sidebarBgStart, sidebarBgEnd, sidebarBgEnd, sidebarBgStart, Round.of(8, 0, 0, 8));

        int sidebarBorder = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 40), alphaV);
        RenderUtil.Rounded.roundedOutline(stack, sidebarX, sidebarY, sidebarWidth, sidebarHeight, 1, sidebarBorder, Round.of(8, 0, 0, 8));

        float logoSize = 32;
        float logoX = sidebarX + (sidebarWidth - logoSize) / 2f;
        float logoY = sidebarY + 20;
        mc.getTextureManager().bindTexture(logoLocation);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha(1));
        blit(stack, (int) logoX, (int) logoY, (int) logoSize, (int) logoSize, 0, 0, 256, 256, 256, 256);
        RenderSystem.disableBlend();

        float iconAreaY = logoY + logoSize + 30;
        float iconAreaHeight = (sidebarY + sidebarHeight) - iconAreaY - 20;

        float iconHeight = 32;
        float iconGap = 10;
        float totalIconsHeight = categories.size() * (iconHeight + iconGap) - iconGap;
        float startIconY = iconAreaY + (iconAreaHeight - totalIconsHeight) / 2f;

        float currentIconY = startIconY;
        for (CategoryPanel category : categories) {
            category.setX(sidebarX);
            category.setY(currentIconY);

            boolean isSelected = category == selectedCategory;
            boolean isHovered = Mathf.isHover(mouseX, mouseY, sidebarX, currentIconY, sidebarWidth, iconHeight);

            float hoverV = Mathf.fast(category.getIconHoverV(), (isSelected || isHovered) ? 1 : 0, 14);
            category.setIconHoverV(hoverV);

            if (hoverV > 0.01f) {
                int highlightStart = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, 100), hoverV * alphaV);
                int highlightEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 100), hoverV * alphaV);
                RenderUtil.Rounded.gradientRect(stack, sidebarX + 10, currentIconY, sidebarWidth - 20, iconHeight, highlightStart, highlightEnd, highlightEnd, highlightStart, Round.of(6));
            }

            String icon = category.getCategory().getIcon();
            float iconSize = 14;
            int iconColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(120, 120, 140),
                    ColorUtil.getColor(200, 180, 230),
                    hoverV
            ), alphaV);

            Fonts.CLICKGUI.draw(stack, icon,
                    sidebarX + sidebarWidth / 2f - Fonts.CLICKGUI.getWidth(icon, iconSize) / 2f,
                    currentIconY + iconHeight / 2f - iconSize / 2f,
                    iconColor, iconSize);

            currentIconY += iconHeight + iconGap;
        }
    }

    private void renderDividers(MatrixStack stack) {
        float sidebarWidth = 70;
        int dividerColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 30), alphaV);

        RenderUtil.Rounded.roundedRect(stack, x + sidebarWidth + 1, y + 1, 0.5f, height - 2, dividerColor, Round.of(0));

        float headerHeight = 70;
        RenderUtil.Rounded.roundedRect(stack, x + sidebarWidth + 1, y + headerHeight, width - sidebarWidth - 2, 0.5f, dividerColor, Round.of(0));
    }

    private void renderHeader(MatrixStack stack, float mouseX, float mouseY) {
        float sidebarWidth = 70;
        float headerX = x + sidebarWidth + 1;
        float headerY = y + 1;
        float headerWidth = width - sidebarWidth - 2;
        float headerHeight = 70;

        int headerBgStart = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, 200), alphaV);
        int headerBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 200), alphaV);
        RenderUtil.Rounded.gradientRect(stack, headerX, headerY, headerWidth, headerHeight, headerBgStart, headerBgEnd, headerBgEnd, headerBgStart, Round.of(0, 8, 0, 0));

        if (selectedCategory != null) {
            String categoryName = selectedCategory.getCategory().getName();
            int moduleCount = selectedCategory.getFilteredModules().size();
            String title = categoryName + " (" + moduleCount + ")";

            Fonts.SF_BOLD.draw(stack, title, headerX + 25, headerY + headerHeight / 2f - Fonts.SF_BOLD.getHeight(title, 12) / 2f,
                    ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), alphaV), 12);
        }

        float searchBarWidth = 220;
        float searchBarHeight = 32;
        float searchBarX = headerX + headerWidth - searchBarWidth - 25;
        float searchBarY = headerY + (headerHeight - searchBarHeight) / 2f;

        int searchBgStart = ColorUtil.multAlpha(ColorUtil.getColor(33, 32, 40, 180), searchBarAlpha * alphaV);
        int searchBgEnd = ColorUtil.multAlpha(ColorUtil.getColor(17, 17, 17, 180), searchBarAlpha * alphaV);
        RenderUtil.Rounded.gradientRect(stack, searchBarX, searchBarY, searchBarWidth, searchBarHeight, searchBgStart, searchBgEnd, searchBgEnd, searchBgStart, Round.of(6));

        if (searchActive || !searchQuery.isEmpty()) {
            int outlineColor = ColorUtil.multAlpha(ColorUtil.getColor(138, 104, 193, 120), searchBarAlpha * alphaV);
            RenderUtil.Rounded.roundedOutline(stack, searchBarX, searchBarY, searchBarWidth, searchBarHeight, 1.5f, outlineColor, Round.of(6));
        }

        String searchIcon = "G";
        float iconSize = 8;
        Fonts.icon.draw(stack, searchIcon, searchBarX + 12, searchBarY + searchBarHeight / 2f - iconSize / 2f,
                ColorUtil.multAlpha(ColorUtil.getColor(160, 160, 180), searchBarAlpha * alphaV), iconSize);

        String textToRender = searchQuery;
        if (searchQuery.isEmpty() && !searchActive) {
            textToRender = "Search modules...";
        }

        float textX = searchBarX + 28;
        float textY = searchBarY + searchBarHeight / 2f - Fonts.SF_MEDIUM.getHeight(textToRender, 8) / 2f;
        int textColor = (searchQuery.isEmpty() && !searchActive) ?
                ColorUtil.multAlpha(ColorUtil.getColor(140, 140, 160), searchBarAlpha * alphaV) :
                ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), searchBarAlpha * alphaV);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(textX, searchBarY, searchBarWidth - 35, searchBarHeight);
        Fonts.SF_MEDIUM.draw(stack, textToRender, textX, textY, textColor, 8);

        if (searchActive && System.currentTimeMillis() % 1000 > 500) {
            float cursorX = textX + Fonts.SF_MEDIUM.getWidth(searchQuery, 8);
            Fonts.SF_MEDIUM.draw(stack, "|", cursorX, textY - 0.5f,
                    ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 240), searchBarAlpha * alphaV), 8);
        }
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderContent(MatrixStack stack, float mouseX, float mouseY) {
        float sidebarWidth = 70;
        float headerHeight = 70;
        float contentX = x + sidebarWidth + 1;
        float contentY = y + headerHeight + 1;
        float contentWidth = width - sidebarWidth - 2;
        float contentHeight = height - headerHeight - 2;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(contentX, contentY, contentWidth, contentHeight);

        if (selectedCategory != null) {
            selectedCategory.render(stack, mouseX, mouseY, contentX, contentY, contentWidth, contentHeight);
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float sidebarWidth = 70;
        float searchBarWidth = 220;
        float searchBarHeight = 32;
        float searchBarX = x + width - searchBarWidth - 25;
        float searchBarY = y + (70 - searchBarHeight) / 2f;

        if (Mathf.isHover(mouseX, mouseY, searchBarX, searchBarY, searchBarWidth, searchBarHeight)) {
            searchActive = true;
            return true;
        } else {
            if (button == 0) {
                searchActive = false;
            }
        }

        for (CategoryPanel category : categories) {
            if (Mathf.isHover(mouseX, mouseY, category.getX(), category.getY(), 70, 32) && button == 0) {
                if (selectedCategory != category) {
                    if (selectedCategory != null) {
                        selectedCategory.setDelta(0);
                    }
                    selectedCategory = category;
                }
                return true;
            }
        }

        boolean clickOnOpenPanel = false;
        if (selectedCategory != null && button == 0) {
            for (ModulePanel module : selectedCategory.getModules()) {
                if (!module.isExpanded()) continue;
                for (SettingPanel setting : module.getSettings()) {
                    if (setting.isSelected()) {
                        float panelX = setting.getX();
                        float panelY = setting.getY();
                        float panelWidth = setting.getWidth();
                        float panelHeight = setting.getHeight();

                        if (Mathf.isHover(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight)) {
                            clickOnOpenPanel = true;
                            break;
                        }
                    }
                }
                if (clickOnOpenPanel) break;
            }

            if (!clickOnOpenPanel) {
                for (ModulePanel module : selectedCategory.getModules()) {
                    for (SettingPanel setting : module.getSettings()) {
                        setting.setSelected(false);
                    }
                }
            }
        }

        if (selectedCategory != null) {
            selectedCategory.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selectedCategory != null) {
            selectedCategory.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (selectedCategory != null) {
            float sidebarWidth = 70;
            float headerHeight = 70;
            float contentX = x + sidebarWidth + 1;
            float contentY = y + headerHeight + 1;
            float contentWidth = width - sidebarWidth - 2;
            float contentHeight = height - headerHeight - 2;

            if (Mathf.isHover(mouseX, mouseY, contentX, contentY, contentWidth, contentHeight)) {
                selectedCategory.scroll((float) mouseX, (float) mouseY, (float) delta);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (searchActive) {
                searchQuery = "";
                searchActive = false;
                return true;
            }
            this.closeScreen();
            return true;
        }

        if (searchActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
                searchActive = false;
            }
            return true;
        }

        if (selectedCategory != null) {
            selectedCategory.key(keyCode);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive) {
            if (String.valueOf(codePoint).matches("[ -~]")) {
                searchQuery += codePoint;
                return true;
            }
        }

        if (selectedCategory != null) {
            selectedCategory.charTyped(codePoint);
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void closeScreen() {
        closing = true;
        alpha = 0;
    }

    public float alpha(float alpha) {
        return alpha * alphaV;
    }
}