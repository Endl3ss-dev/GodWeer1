package org.privates.client.screen.countergui;

import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CounterGui extends Screen implements IMinecraft {

    private final ArrayList<Category> categories = new ArrayList<>();

    private float x, y, width, height;
    private Category selectedCategory;
    private Module hoveredModule;
    private Module highlightedModule;

    private boolean drag = false;
    private float startX, startY;
    private float alpha, alphaV;

    private String searchQuery = "";
    private boolean searchActive = false;
    private boolean searchExpanded = false;
    private float searchBarAlpha = 0f;
    private float searchExpandAnim = 0f;
    private float searchHighlightAnim = 0f;

    private String currentDescription = "";
    private float descriptionAlpha = 0f;
    private int typingProgress = 0;
    private long lastTypeTime = 0L;
    private float descBoxWidthAnim = 0f;

    private final Map<Category, Float> categoryHoverAnims = new HashMap<>();
    private float selectedLinePosition = 0f;
    private float targetLinePosition = 0f;

    public CounterGui() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        if (width == 0 || height == 0) {
            this.width = 471.912f;
            this.height = 291.2f;
            this.x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            this.y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;
        }

        if (categories.isEmpty()) {
            for (org.privates.client.managers.module.Category c : org.privates.client.managers.module.Category.values()) {
                Category category = new Category(this, c);
                categories.add(category);
            }
            selectedCategory = categories.get(0);
        }

        alpha = 1;
        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        alphaV = Mathf.fast(alphaV, alpha, 28);
        searchBarAlpha = Mathf.fast(searchBarAlpha, searchActive ? 1f : 0.6f, 16);
        searchExpandAnim = Mathf.fast(searchExpandAnim, searchExpanded ? 1f : 0f, 14);
        searchHighlightAnim = Mathf.fast(searchHighlightAnim, highlightedModule != null ? 1f : 0f, 14);

        if (alphaV <= .1f && alpha == 0)
            super.closeScreen();

        Theme theme = Theme.getInstance();
        int bgColor = theme.background().getValue();

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, 0.5f), Round.of(12));
        });
        BlurRenderer2.draw(8);

        RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, alphaV), Round.of(12));
        RenderUtil.Rounded.roundedOutline(stack, x, y, width, height, 1f, ColorUtil.multAlpha(new Color(255, 255, 255, 15).getRGB(), alphaV), Round.of(12));

        renderLogoAndCategories(stack, mouseX, mouseY);
        renderSearchIcon(stack, mouseX, mouseY);

        float lineY = y + 35;
        RenderUtil.Rounded.roundedRect(stack, x + 8, lineY, width - 16, 1f, ColorUtil.multAlpha(new Color(255, 255, 255, 20).getRGB(), alphaV), Round.of(0.5f));

        renderDescription(stack);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y + 41, width, height - 41);

        hoveredModule = null;
        for (Category category : categories) {
            category.render(stack, mouseX, mouseY);
            if (category.isSelected() && category.getHoveredModule() != null) {
                hoveredModule = category.getHoveredModule();
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (drag) {
            float diffX = mouseX - startX, diffY = mouseY - startY;

            float scaledWidth = mc.getMainWindow().getScaledWidth(), scaleHeight = mc.getMainWindow().getScaledHeight();
            diffX = (x + diffX + width) <= scaledWidth && (x + diffX) >= 0 ? diffX : 0;
            diffY = (y + diffY + height) <= scaleHeight && (y + diffY) >= 0 ? diffY : 0;

            x += diffX;
            y += diffY;
            for (Category category : categories) {
                for (Module module : category.getModules()) {
                    module.setX(module.getX() + diffX);
                    module.setY(module.getY() + diffY);
                    for (Setting setting : module.getSettings()) {
                        setting.setX(setting.getX() + diffX);
                        setting.setY(setting.getY() + diffY);
                        if (setting.getSetting() instanceof ColorSetting && setting.getColorPicker() != null) {
                            setting.getColorPicker().setHueX(setting.getColorPicker().getHueX() + diffX);
                            setting.getColorPicker().setHueY(setting.getColorPicker().getHueY() + diffY);
                        }
                        if (setting.getSetting() instanceof SliderSetting)
                            setting.setSelectedV(setting.getSelectedV() + diffX);
                    }
                }
            }

            if ((x + diffX + width) <= scaledWidth) {
                startX = mouseX;
                startY = mouseY;
            }
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderLogoAndCategories(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();

        float logoSize = 8.5f;
        float logoTextGap = 5.64f;
        String logoText = "PrivateDlc";
        float logoTextWidth = Fonts.SF_MEDIUM.getWidth(logoText, logoSize);

        float logoX = x + 12.4f;
        float logoY = y + 10.4f + (22.37f - logoSize) / 2f;

        int accentCol = theme.clientColor();

        Fonts.SF_MEDIUM.draw(stack, logoText, logoX, logoY, ColorUtil.multAlpha(accentCol, alphaV), logoSize);

        float catIconSize = 12.8f;
        float catSpacing = 6.4f;
        float totalCategoriesWidth = (categories.size() * catIconSize) + ((categories.size() - 1) * catSpacing);

        float barPadding = 8f;
        float totalBarWidth = totalCategoriesWidth + barPadding * 2;

        float catBarX = x + (width - totalBarWidth) / 2f;
        float catY = y + 8f;
        float catBarHeight = 22.37f;

        int bgAlpha = 45;
        int borderAlpha = 70;

        RenderUtil.Rounded.roundedWithOutline(stack, catBarX, catY, totalBarWidth, catBarHeight, 0.65f,
                ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 30, bgAlpha), alphaV),
                ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 65, borderAlpha), alphaV),
                Round.of(6f));

        float catStartX = catBarX + barPadding;

        int selectedIndex = categories.indexOf(selectedCategory);
        if (selectedIndex != -1) {
            targetLinePosition = catStartX + (catIconSize + catSpacing) * selectedIndex + catIconSize / 2f;
        }
        selectedLinePosition = Mathf.fast(selectedLinePosition, targetLinePosition, 18);

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            float currentX = catStartX + (catIconSize + catSpacing) * i;

            boolean selected = selectedCategory == category;
            boolean hovered = Mathf.isHover(mouseX, mouseY, currentX - 3.2f, catY + (catBarHeight - catIconSize - 3.2f) / 2f, catIconSize + 6.4f, catIconSize + 3.2f);

            float hoverAnim = categoryHoverAnims.getOrDefault(category, 0f);
            hoverAnim = Mathf.fast(hoverAnim, (hovered || selected) ? 1f : 0f, 18);
            categoryHoverAnims.put(category, hoverAnim);

            float iconCenterX = currentX + catIconSize / 2f;
            float iconCenterY = catY + catBarHeight / 2f;

            if (hoverAnim > 0.01f) {
                int shadowColor = ColorUtil.multAlpha(accentCol, hoverAnim * 0.5f * alphaV);
                RenderUtil.Shadow.drawShadow(stack, iconCenterX - 7.2f, iconCenterY - 7.6f, 14.4f, 14.4f, 3f, hoverAnim * 0.5f, shadowColor, Round.of(6f));
            }

            int textCol = theme.textColor();
            int iconColor = ColorUtil.multAlpha(ColorUtil.overCol(textCol, ColorUtil.getColor(255, 255, 255), hoverAnim * 0.8f), alphaV);

            renderCategoryIcon(stack, category.getCategory(), iconCenterX, iconCenterY, 9.6f, iconColor);
        }

        float lineWidth = catIconSize * 0.65f;
        float lineHeight = 1.02f;
        float lineX = selectedLinePosition - lineWidth / 2f;
        float lineY = catY + catBarHeight - 3f;

        RenderUtil.Rounded.roundedRect(stack, lineX, lineY, lineWidth, lineHeight, ColorUtil.multAlpha(accentCol, alphaV * 0.8f), Round.of(0.5f));
    }

    private void renderCategoryIcon(MatrixStack stack, org.privates.client.managers.module.Category category, float centerX, float centerY, float iconSize, int color) {
        switch (category) {
            case COMBAT:
                float combatIconWidth = Fonts.iconf.getWidth("c", iconSize);
                float combatIconHeight = Fonts.iconf.getHeight("c", iconSize);
                Fonts.iconf.draw(stack, "c", centerX - 0.85f - combatIconWidth / 2f, centerY - 0.64f - combatIconHeight / 2f, color, iconSize);
                break;
            case MOVEMENT:
                float movementIconWidth = Fonts.iconf.getWidth("e", iconSize);
                float movementIconHeight = Fonts.iconf.getHeight("e", iconSize);
                Fonts.iconf.draw(stack, "e", centerX - 0.85f - movementIconWidth / 2f, centerY - 0.28f - movementIconHeight / 2f, color, iconSize);
                break;
            case PLAYER:
                float playerIconWidth = Fonts.CLICKGUI.getWidth("c", iconSize);
                float playerIconHeight = Fonts.CLICKGUI.getHeight("c", iconSize);
                Fonts.CLICKGUI.draw(stack, "c", centerX + 0.42f - playerIconWidth / 2f, centerY - 0.42f - playerIconHeight / 2f, color, iconSize);
                break;
            case RENDER:
                float renderIconWidth = Fonts.CLICKGUI.getWidth("d", iconSize);
                float renderIconHeight = Fonts.CLICKGUI.getHeight("d", iconSize);
                Fonts.CLICKGUI.draw(stack, "d", centerX - 0.13f - renderIconWidth / 2f, centerY - 0.17f - renderIconHeight / 2f, color, iconSize);
                break;
            case MISC:
                float miscIconWidth = Fonts.CLICKGUI.getWidth("e", iconSize);
                float miscIconHeight = Fonts.CLICKGUI.getHeight("e", iconSize);
                Fonts.CLICKGUI.draw(stack, "e", centerX - 0.13f - miscIconWidth / 2f, centerY - 0.17f - miscIconHeight / 2f, color, iconSize);
                break;
        }
    }

    private void renderSearchIcon(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();

        float searchIconSize = 8f;
        float searchIconX = x + width - searchIconSize - 16f;
        float searchIconY = y + 17f;

        float maxSearchWidth = 120f;
        float currentSearchWidth = searchIconSize + 8f + (maxSearchWidth - (searchIconSize + 8f)) * searchExpandAnim;
        float searchBarX = searchIconX - currentSearchWidth + searchIconSize + 8f;

        boolean iconHovered = Mathf.isHover(mouseX, mouseY, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f);
        searchBarAlpha = Mathf.fast(searchBarAlpha, (searchExpanded || iconHovered) ? 1f : 0.7f, 16);

        if (searchExpandAnim > 0.01f) {
            int bgAlpha = (int) ((35 + searchBarAlpha * 25) * searchExpandAnim);
            int borderAlpha = (int) ((70 + searchBarAlpha * 50) * searchExpandAnim);

            int accentCol = theme.clientColor();
            int bgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(20, 20, 20, bgAlpha),
                    ColorUtil.multAlpha(accentCol, searchActive ? .06f : .03f),
                    searchActive ? 1f : 0f), alpha(searchExpandAnim));

            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(40, 40, 40, borderAlpha),
                    ColorUtil.multAlpha(accentCol, searchActive ? .12f : .06f),
                    searchActive ? 1f : 0f), alpha(searchExpandAnim));

            RenderUtil.Rounded.roundedWithOutline(stack, searchBarX, searchIconY - 4f, currentSearchWidth, searchIconSize + 8f, 0.65f,
                    bgColor, borderColor, Round.of(4f));

            if (searchExpandAnim > 0.3f) {
                String textToRender = searchQuery.isEmpty() && !searchActive ? "Search..." : searchQuery;
                float textX = searchBarX + 6f;
                float textY = searchIconY + (searchIconSize + 8f) / 2f - Fonts.SF_MEDIUM.getHeight(textToRender, 6.5f) / 2f - 4f;

                int textColor = searchQuery.isEmpty() && !searchActive ?
                        ColorUtil.getColor(140, 140, 140, alpha(searchBarAlpha * 0.7f * searchExpandAnim)) :
                        ColorUtil.getColor(255, 255, 255, alpha(searchBarAlpha * searchExpandAnim));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(textX, searchIconY - 4f, currentSearchWidth - (searchIconSize + 16f), searchIconSize + 8f);
                Fonts.SF_MEDIUM.draw(stack, textToRender, textX, textY, textColor, 6.5f);

                if (searchActive && System.currentTimeMillis() % 1000 > 500) {
                    float cursorX = textX + Fonts.SF_MEDIUM.getWidth(searchQuery, 6.5f);
                    float cursorAlpha = (float) Math.sin(System.currentTimeMillis() * 0.004) * 0.3f + 0.7f;
                    Fonts.SF_MEDIUM.draw(stack, "|", cursorX, textY - 0.5f,
                            ColorUtil.multAlpha(accentCol, alpha(cursorAlpha * searchBarAlpha * searchExpandAnim)), 6.5f);
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        }

        int bgAlpha = (int) (35 + searchBarAlpha * 25);
        int borderAlpha = (int) (70 + searchBarAlpha * 50);

        RenderUtil.Rounded.roundedWithOutline(stack, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f, 0.65f,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20, bgAlpha), alpha(searchBarAlpha)),
                ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, borderAlpha), alpha(searchBarAlpha)),
                Round.of(4f));

        int textCol = theme.textColor();
        Fonts.icon.draw(stack, "G", searchIconX, searchIconY, ColorUtil.multAlpha(textCol, alpha(searchBarAlpha)), searchIconSize);
    }

    private void renderDescription(MatrixStack stack) {
        if (hoveredModule != null) {
            long currentTime = System.currentTimeMillis();
            long hoverDuration = currentTime - hoveredModule.getHoverStartTime();

            if (hoverDuration >= 250L) {
                String description = hoveredModule.getModule().getModuleInfo().description();
                if (description == null || description.isEmpty()) {
                    description = "Описание отсутствует";
                }

                if (!currentDescription.equals(description)) {
                    currentDescription = description;
                    typingProgress = 0;
                    lastTypeTime = System.currentTimeMillis();
                    descBoxWidthAnim = 12f;
                }

                descriptionAlpha = Mathf.fast(descriptionAlpha, 1f, 6);
            }
        } else {
            descriptionAlpha = Mathf.fast(descriptionAlpha, 0f, 6);
        }

        if (descriptionAlpha > 0.01f && !currentDescription.isEmpty()) {
            Theme theme = Theme.getInstance();

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTypeTime > 30 && typingProgress < currentDescription.length()) {
                typingProgress++;
                lastTypeTime = currentTime;
            }

            String displayText = currentDescription.substring(0, Math.min(typingProgress, currentDescription.length()));

            float fullDescWidth = Fonts.SF_MEDIUM.getWidth(currentDescription, 7f) + 12f;
            float currentTextWidth = Fonts.SF_MEDIUM.getWidth(displayText, 7f) + 12f;
            float targetBoxWidth = currentTextWidth + 6f;

            descBoxWidthAnim = Mathf.fast(descBoxWidthAnim, targetBoxWidth, 20);

            float descWidth = Math.min(descBoxWidthAnim, fullDescWidth);
            float descHeight = Fonts.SF_MEDIUM.getHeight(currentDescription, 7f) + 8f;
            float descX = x + (width - fullDescWidth) / 2f;
            float descY = y - descHeight - 8f;

            stack.push();
            stack.translate(descX + descWidth / 2f, descY + descHeight / 2f, 0);
            stack.scale(descriptionAlpha, descriptionAlpha, 1);
            stack.translate(-(descX + descWidth / 2f), -(descY + descHeight / 2f), 0);

            int bgAlpha = (int) ((35 + descriptionAlpha * 25) * descriptionAlpha);
            int borderAlpha = (int) ((70 + descriptionAlpha * 50) * descriptionAlpha);

            int bgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(20, 20, 20, bgAlpha),
                    ColorUtil.multAlpha(theme.clientColor(), .06f),
                    descriptionAlpha), alphaV * descriptionAlpha);

            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(40, 40, 40, borderAlpha),
                    ColorUtil.multAlpha(theme.clientColor(), .12f),
                    descriptionAlpha), alphaV * descriptionAlpha);

            int textColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(180, 180, 180),
                    theme.textColor(),
                    descriptionAlpha * 0.8f), alphaV * descriptionAlpha);

            RenderUtil.Rounded.roundedWithOutline(stack, descX, descY, descWidth, descHeight, 0.65f,
                    bgColor, borderColor, Round.of(4f));

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(descX + 3f, descY + 2f, descWidth - 6f, descHeight - 4f);

            Fonts.SF_MEDIUM.draw(stack, displayText, descX + 6f, descY + 4f, textColor, 7f);

            if (typingProgress < currentDescription.length() && System.currentTimeMillis() % 600 < 300) {
                float cursorX = descX + 6f + Fonts.SF_MEDIUM.getWidth(displayText, 7f);
                Fonts.SF_MEDIUM.draw(stack, "_", cursorX, descY + 4f, textColor, 7f);
            }

            ScissorExp.unset();
            ScissorExp.pop();

            stack.pop();
        } else if (descriptionAlpha <= 0.01f) {
            typingProgress = 0;
            descBoxWidthAnim = 0f;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float searchIconSize = 8f;
        float searchIconX = x + width - searchIconSize - 16f;
        float searchIconY = y + 17f;

        if (Mathf.isHover(mouseX, mouseY, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f)) {
            searchExpanded = !searchExpanded;
            if (searchExpanded) {
                searchActive = true;
            }
            return true;
        }

        if (searchExpanded) {
            float maxSearchWidth = 120f;
            float currentSearchWidth = searchIconSize + 8f + (maxSearchWidth - (searchIconSize + 8f)) * searchExpandAnim;
            float searchBarX = searchIconX - currentSearchWidth + searchIconSize + 8f;

            if (Mathf.isHover(mouseX, mouseY, searchBarX, searchIconY - 4f, currentSearchWidth, searchIconSize + 8f)) {
                searchActive = true;
                return true;
            } else {
                searchActive = false;
            }
        }

        if (highlightedModule != null) {
            highlightedModule = null;
            searchActive = false;
            searchQuery = "";
            return true;
        }

        float catIconSize = 12.8f;
        float catSpacing = 6.4f;
        float totalCategoriesWidth = (categories.size() * catIconSize) + ((categories.size() - 1) * catSpacing);

        float barPadding = 8f;
        float totalBarWidth = totalCategoriesWidth + barPadding * 2;

        float catBarX = x + (width - totalBarWidth) / 2f;
        float catY = y + 8f;
        float catBarHeight = 22.37f;
        float catStartX = catBarX + barPadding;

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            float currentX = catStartX + (catIconSize + catSpacing) * i;

            if (Mathf.isHover(mouseX, mouseY, currentX - 3.2f, catY + (catBarHeight - catIconSize - 3.2f) / 2f, catIconSize + 6.4f, catIconSize + 3.2f)) {
                if (selectedCategory != category) {
                    selectedCategory = category;
                }
                return true;
            }
        }

        startX = (float) mouseX;
        startY = (float) mouseY;

        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);

        if (!anyHovered((float) mouseX, (float) mouseY) && Mathf.isHover(mouseX, mouseY, x, y, width, height))
            drag = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);

        drag = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (selectedCategory != null && Mathf.isHover(mouseX, mouseY, x, y, width, height))
            selectedCategory.scroll((float) mouseX, (float) mouseY, (float) delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                updateSearch();
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchQuery = "";
                highlightedModule = null;
                searchActive = false;
                searchExpanded = false;
            }
            return true;
        }

        if (selectedCategory != null)
            selectedCategory.key(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive) {
            searchQuery += codePoint;
            updateSearch();
            return true;
        }

        if (selectedCategory != null)
            selectedCategory.charTyped(codePoint);
        return super.charTyped(codePoint, modifiers);
    }

    private void updateSearch() {
        highlightedModule = null;
        if (selectedCategory != null && !searchQuery.isEmpty()) {
            for (Module module : selectedCategory.getModules()) {
                String query = searchQuery.toLowerCase();
                if (module.getModule().getName().toLowerCase().contains(query)) {
                    highlightedModule = module;
                    selectedCategory.setDelta(-(module.getY() - y - 80));
                    break;
                }
            }
        }
    }

    @Override
    public void closeScreen() {
        alpha = 0;
        if (selectedCategory != null) {
            for (Module module : selectedCategory.getModules()) {
                for (Setting setting : module.getSettings()) {
                    setting.setSelected(false);
                }
            }
        }
    }

    private boolean anyHovered(float mouseX, float mouseY) {
        if (selectedCategory == null) return false;
        for (Module module : selectedCategory.getModules()) {
            if (Mathf.isHover(mouseX, mouseY, module.getX(), module.getY() + module.getDelta(), module.getWidth(), module.getHeight()) && Mathf.isHover(mouseX, mouseY, x, y, width, height))
                return true;
        }
        return false;
    }

    public float alpha(float alpha) {
        return alpha * alphaV;
    }

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
    }

    public Module getHighlightedModule() {
        return highlightedModule;
    }

    public float getSearchHighlightAnim() {
        return searchHighlightAnim;
    }
}