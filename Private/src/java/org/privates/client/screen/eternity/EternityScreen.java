package org.privates.client.screen.eternity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.render.ClickGuiSettings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EternityScreen extends Screen implements IMinecraft {
    private final ArrayList<Category> categories = new ArrayList<>();
    private final ArrayList<ModuleButton> moduleButtons = new ArrayList<>();
    private float x, y, width, height;
    @Setter
    private Category selectedCategory;
    @Setter
    private ModuleButton selectedModule;
    private ModuleButton previousModule;
    private float alpha, alphaV;
    private String searchQuery = "";
    private boolean searchActive = false;
    private boolean searchExpanded = false;
    private float searchBarAlpha = 0f;
    private float searchExpandAnim = 0f;
    private float searchCaretAnim = 0f;
    private float moduleScrollOffset = 0f;
    private float settingsScrollOffset = 0f;
    private final Map<ModuleButton, Float> moduleAnimY = new HashMap<>();
    private final Map<ModuleButton, Float> moduleHeights = new HashMap<>();
    private final Map<ModuleButton, Integer> moduleColumns = new HashMap<>();
    private final Map<Setting, Float> settingAnimations = new HashMap<>();
    private final Map<org.privates.client.managers.module.settings.Setting, RockstarSettingAdapter> rockstarSettings = new HashMap<>();

    private float settingsSlideAnim = 0f;
    private float settingsOutAnim = 0f;
    private boolean settingsTransitioning = false;

    private final GifRender gifRender = new GifRender(new Namespaced("texture/xz.gif"));
    private float gifAppearAnim = 0f;

    private Category clientCategory;

    private final String currentDescription = "";
    private final float descriptionAlpha = 0f;

    private Category previousCategory;

    private boolean resizing = false;
    private ResizeType resizeType = ResizeType.NONE;
    private float startMouseX, startMouseY;
    private float startWidth, startHeight;

    private long openTime = 0L;

    private final Map<Category, Float> categoryHoverAnims = new HashMap<>();
    private final Map<ModuleButton, Float> moduleHoverAnims = new HashMap<>();
    private final Map<ModuleButton, Boolean> moduleWaitingForBind = new HashMap<>();

    private float selectedLinePosition = 0f;
    private float targetLinePosition = 0f;

    private enum ResizeType {
        NONE, WIDTH, HEIGHT, BOTH
    }

    public EternityScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        this.width = 470f;
        this.height = 319.6f;
        this.x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
        this.y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        if (categories.isEmpty()) {
            clientCategory = new Category(this, null);
            clientCategory.setName("Client");
            categories.add(clientCategory);

            for (org.privates.client.managers.module.Category c : org.privates.client.managers.module.Category.values()) {
                Category category = new Category(this, c);
                categories.add(category);
            }

            selectedCategory = categories.get(0);
            updateModuleButtons();
        }

        alpha = 1;
        openTime = System.currentTimeMillis();
        gifAppearAnim = 0f;

        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();
        alphaV = Mathf.fast(alphaV, alpha, 28);
        searchBarAlpha = Mathf.fast(searchBarAlpha, searchActive ? 1f : 0.6f, 16);
        searchExpandAnim = Mathf.fast(searchExpandAnim, searchExpanded ? 1f : 0f, 14);
        searchCaretAnim += searchActive ? 0.12f : 0f;
        if (searchCaretAnim > Math.PI * 2) {
            searchCaretAnim -= (float) (Math.PI * 2);
        }

        if (settingsTransitioning) {
            settingsOutAnim = Mathf.fast(settingsOutAnim, 1f, 16);
            if (settingsOutAnim >= 0.99f) {
                settingsTransitioning = false;
                settingsOutAnim = 0f;
                settingsSlideAnim = 0f;
            }
        } else {
            float settSpeed = settings.settingsSpeed().getValue().floatValue() / 200f;
            settingsSlideAnim = Mathf.fast(settingsSlideAnim, 1f, 12 * settSpeed);
        }

        gifAppearAnim = Mathf.fast(gifAppearAnim, (selectedModule == null || selectedModule.getSettings().isEmpty()) ? 1f : 0f, 10);

        if (alphaV <= .1f && alpha == 0) super.closeScreen();

        int bgColor = settings.getBackgroundColor();

        if (settings.enableBlur().getValue()) {
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.replAlpha(bgColor, 165), Round.of(5.2f)));
            BlurRenderer2.draw(settings.blurStrength().getValue().intValue());
        }

        RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, alphaV), Round.of(5.2f));

        int outlineCol = settings.getOutlineColor();
        RenderUtil.Rounded.roundedOutline(stack, x - 0.65f, y - 0.65f, width + 1.3f, height + 1.3f, 0.65f, ColorUtil.multAlpha(outlineCol, alpha(.7f)), Round.of(5.2f));

        renderLogoAndCategories(stack, mouseX, mouseY);

        if (!searchExpanded) {
            renderModuleButtons(stack, mouseX, mouseY);
            renderSettings(stack, mouseX, mouseY);
        } else {
            renderSearchWindow(stack, mouseX, mouseY);
        }

        renderSearch(stack, mouseX, mouseY);

        if (settings.enableResize().getValue() && settings.showResizeBars().getValue()) {
            renderResizeHandles(stack, mouseX, mouseY);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderResizeHandles(MatrixStack stack, float mouseX, float mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();
        float handleSize = 1.88f;
        float handleLength = 14.1f;

        int handleColor = ColorUtil.getColor(255, 255, 255, 180);
        int accentCol = settings.getAccentColor();

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        boolean leftHover = isHoveringHandle(mouseX, mouseY, x - 9.4f, centerY - handleLength / 2f, handleSize, handleLength);
        RenderUtil.Rounded.roundedRect(stack, x - 9.4f, centerY - handleLength / 2f, handleSize, handleLength, leftHover ? accentCol : handleColor, Round.of(3f));

        boolean rightHover = isHoveringHandle(mouseX, mouseY, x + width + 7.52f, centerY - handleLength / 2f, handleSize, handleLength);
        RenderUtil.Rounded.roundedRect(stack, x + width + 7.52f, centerY - handleLength / 2f, handleSize, handleLength, rightHover ? accentCol : handleColor, Round.of(3f));

        boolean topHover = isHoveringHandle(mouseX, mouseY, centerX - handleLength / 2f, y - 9.4f, handleLength, handleSize);
        RenderUtil.Rounded.roundedRect(stack, centerX - handleLength / 2f, y - 9.4f, handleLength, handleSize, topHover ? accentCol : handleColor, Round.of(3f));

        boolean bottomHover = isHoveringHandle(mouseX, mouseY, centerX - handleLength / 2f, y + height + 7.52f, handleLength, handleSize);
        RenderUtil.Rounded.roundedRect(stack, centerX - handleLength / 2f, y + height + 7.52f, handleLength, handleSize, bottomHover ? accentCol : handleColor, Round.of(3f));
    }

    private boolean isHoveringHandle(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderLogoAndCategories(MatrixStack stack, float mouseX, float mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        float logoSize = 24.04f;
        float logoTextGap = 5.64f;
        String logoText = "PrivateDlc Recode";
        float logoTextWidth = Fonts.SF_MEDIUM.getWidth(logoText, 6.58f);

        float logoX = x + 9.4f;
        float logoY = y + 9.4f + (26.32f - logoSize) / 2f;

        int accentCol = settings.getAccentColor();

        RenderUtil.drawImage(stack, new Namespaced("texture/logo.png"), logoX, logoY, logoSize, logoSize, true, ColorUtil.multAlpha(accentCol, alphaV));

        float textX = logoX + logoSize + logoTextGap;
        float textY = y + 9.4f + (26.32f - Fonts.SF_MEDIUM.getHeight(logoText, 6.58f)) / 2f;

        RenderUtil.Shadow.drawShadow(stack, textX, textY, logoTextWidth, Fonts.SF_MEDIUM.getHeight(logoText, 6.58f), 2.0f, 0.35f, ColorUtil.multAlpha(accentCol, alphaV * 0.5f), Round.of(2f));
        Fonts.SF_MEDIUM.draw(stack, logoText, textX, textY, ColorUtil.multAlpha(accentCol, alphaV), 6.58f);

        float catIconSize = 15.04f;
        float catSpacing = 7.52f;
        float totalCategoriesWidth = (categories.size() * catIconSize) + ((categories.size() - 1) * catSpacing);

        float barPadding = 9.4f;
        float totalBarWidth = totalCategoriesWidth + barPadding * 2;

        float catBarX = x + (width - totalBarWidth) / 2f;
        float catY = y + 9.4f;
        float catBarHeight = 26.32f;

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
            boolean hovered = Mathf.isHover(mouseX, mouseY, currentX - 3.76f, catY + (catBarHeight - catIconSize - 3.76f) / 2f, catIconSize + 7.52f, catIconSize + 3.76f);

            float hoverAnim = categoryHoverAnims.getOrDefault(category, 0f);
            hoverAnim = Mathf.fast(hoverAnim, (hovered || selected) ? 1f : 0f, 18);
            categoryHoverAnims.put(category, hoverAnim);

            float iconCenterX = currentX + catIconSize / 2f;
            float iconCenterY = catY + catBarHeight / 2f;

            if (selected) {
            }

            if (hoverAnim > 0.01f) {
                int shadowColor = ColorUtil.multAlpha(accentCol, hoverAnim * 0.5f * alphaV);
                RenderUtil.Shadow.drawShadow(stack, iconCenterX - 8.5f, iconCenterY - 9f, 17f, 17f, 3.5f, hoverAnim * 0.5f, shadowColor, Round.of(7f));
            }

            int textCol = settings.getTextColor();
            int iconColor = ColorUtil.multAlpha(ColorUtil.overCol(textCol, ColorUtil.getColor(255, 255, 255), hoverAnim * 0.8f), alphaV);

            renderCategoryIcon(stack, category == clientCategory ? "CLIENT" :
                            category.getCategory() == org.privates.client.managers.module.Category.COMBAT ? "COMBAT" :
                                    category.getCategory() == org.privates.client.managers.module.Category.MOVEMENT ? "MOVEMENT" :
                                            category.getCategory() == org.privates.client.managers.module.Category.PLAYER ? "PLAYER" :
                                                    category.getCategory() == org.privates.client.managers.module.Category.RENDER ? "RENDER" : "MISC",
                    iconCenterX, iconCenterY, 11.28f, iconColor);
        }

        float lineWidth = catIconSize * 0.65f;
        float lineHeight = 1.2f;
        float lineX = selectedLinePosition - lineWidth / 2f;
        float lineY = catY + catBarHeight - 3.5f;

        RenderUtil.Rounded.roundedRect(stack, lineX, lineY, lineWidth, lineHeight, ColorUtil.multAlpha(accentCol, alphaV * 0.8f), Round.of(0.6f));
    }

    private void renderSearch(MatrixStack stack, float mouseX, float mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        float searchIconSize = 13.16f;
        float searchIconX = x + width - searchIconSize - 9.4f;
        float searchIconY = y + 16.12f;

        float maxSearchWidth = 126.8f;
        float currentSearchWidth = 18.8f + (maxSearchWidth - 18.8f) * searchExpandAnim;
        float searchBarX = searchIconX - currentSearchWidth + searchIconSize;
        float searchBarY = searchIconY;
        float searchBarHeight = 13.16f;

        boolean iconHovered = Mathf.isHover(mouseX, mouseY, searchBarX, searchBarY, currentSearchWidth, searchBarHeight);
        searchBarAlpha = Mathf.fast(searchBarAlpha, (searchExpanded || iconHovered) ? 1f : 0.7f, 16);

        if (searchExpandAnim > 0.01f) {
            int bgAlpha = (int) ((35 + searchBarAlpha * 25) * searchExpandAnim);
            int borderAlpha = (int) ((70 + searchBarAlpha * 50) * searchExpandAnim);

            int accentCol = settings.getAccentColor();
            int bgColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(20, 20, 20, bgAlpha), ColorUtil.multAlpha(accentCol, searchActive ? .06f : .03f), searchActive ? 1f : 0f), alpha(searchExpandAnim));
            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(40, 40, 40, borderAlpha), ColorUtil.multAlpha(accentCol, searchActive ? .12f : .06f), searchActive ? 1f : 0f), alpha(searchExpandAnim));

            RenderUtil.Rounded.roundedWithOutline(stack, searchBarX, searchBarY, currentSearchWidth, searchBarHeight, 0.65f, bgColor, borderColor, Round.of(3.5f));

            if (searchExpandAnim > 0.3f) {
                String textToRender = searchQuery.isEmpty() && !searchActive ? "Search" : searchQuery;
                float textX = searchBarX + 7.52f;
                float textY = searchBarY + searchBarHeight / 2f - Fonts.SF_MEDIUM.getHeight(textToRender, 6.11f) / 2f;
                float textAreaW = currentSearchWidth - (searchQuery.isEmpty() ? 24.44f : 30.44f);

                int textColor = searchQuery.isEmpty() && !searchActive ? ColorUtil.getColor(140, 140, 140, alpha(searchBarAlpha * 0.7f * searchExpandAnim)) : ColorUtil.getColor(255, 255, 255, alpha(searchBarAlpha * searchExpandAnim));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(textX, searchBarY, textAreaW, searchBarHeight);
                Fonts.SF_MEDIUM.draw(stack, textToRender, textX, textY, textColor, 6.11f);

                if (searchActive) {
                    float cursorX = textX + Fonts.SF_MEDIUM.getWidth(searchQuery, 6.11f);
                    float maxCursorX = searchBarX + currentSearchWidth - 16f;
                    cursorX = Math.min(cursorX, maxCursorX);
                    float cursorAlpha = 0.45f + 0.55f * (float) Math.sin(searchCaretAnim);
                    Fonts.SF_MEDIUM.draw(stack, "|", cursorX, textY - 0.5f, ColorUtil.multAlpha(accentCol, alpha(cursorAlpha * searchBarAlpha * searchExpandAnim)), 6.11f);
                }
                ScissorExp.unset();
                ScissorExp.pop();

                if (!searchQuery.isEmpty()) {
                    float clearX = searchBarX + currentSearchWidth - 9.4f;
                    float clearY = searchBarY + searchBarHeight / 2f - Fonts.SF_MEDIUM.getHeight("x", 6.58f) / 2f;
                    Fonts.SF_MEDIUM.draw(stack, "x", clearX, clearY, ColorUtil.getColor(170, 170, 170, alpha(searchBarAlpha * searchExpandAnim)), 6.58f);
                }
            }
        }

        int textCol = settings.getTextColor();
        float iconDrawX = searchBarX + 4.2f;
        Fonts.icon.draw(stack, "G", iconDrawX, searchIconY + searchBarHeight / 2f - Fonts.icon.getHeight("G", 8.46f) / 2f, ColorUtil.multAlpha(textCol, alpha(searchBarAlpha)), 8.46f);
    }

    private void renderSearchWindow(MatrixStack stack, float mouseX, float mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        float searchWindowWidth = width * 0.5f;
        float searchWindowHeight = height * 0.6f;
        float searchWindowX = x + (width - searchWindowWidth) / 2f;
        float searchWindowY = y + (height - searchWindowHeight) / 2f;

        int accentCol = settings.getAccentColor();
        int bgColor = settings.getBackgroundColor();

        float centerX = searchWindowX + searchWindowWidth / 2f;
        float centerY = searchWindowY + searchWindowHeight / 2f;

        stack.push();
        stack.translate(centerX, centerY, 0);
        float easedScale = easeOutBack(searchExpandAnim);
        stack.scale(easedScale, easedScale, 1f);
        stack.translate(-centerX, -centerY, 0);

        if (settings.enableBlur().getValue()) {
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(stack, searchWindowX, searchWindowY, searchWindowWidth, searchWindowHeight, ColorUtil.replAlpha(bgColor, 165), Round.of(8f)));
            BlurRenderer2.draw(settings.blurStrength().getValue().intValue());
        }

        RenderUtil.Rounded.roundedRect(stack, searchWindowX, searchWindowY, searchWindowWidth, searchWindowHeight, ColorUtil.multAlpha(bgColor, alphaV * searchExpandAnim), Round.of(8f));

        int outlineCol = settings.getOutlineColor();
        RenderUtil.Rounded.roundedOutline(stack, searchWindowX - 0.65f, searchWindowY - 0.65f, searchWindowWidth + 1.3f, searchWindowHeight + 1.3f, 0.65f, ColorUtil.multAlpha(outlineCol, alphaV * searchExpandAnim * 0.7f), Round.of(8f));

        float closeButtonSize = 18.8f;
        float closeButtonX = searchWindowX + searchWindowWidth - closeButtonSize - 9.4f;
        float closeButtonY = searchWindowY + 9.4f;

        boolean closeHovered = Mathf.isHover(mouseX, mouseY, closeButtonX, closeButtonY, closeButtonSize, closeButtonSize);
        float closeHoverAnim = categoryHoverAnims.getOrDefault(null, 0f);
        closeHoverAnim = Mathf.fast(closeHoverAnim, closeHovered ? 1f : 0f, 18);
        categoryHoverAnims.put(null, closeHoverAnim);

        int closeBgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                ColorUtil.getColor(30, 30, 35, 100),
                ColorUtil.multAlpha(ColorUtil.getColor(255, 80, 80), 0.3f),
                closeHoverAnim
        ), alphaV * searchExpandAnim);

        RenderUtil.Rounded.roundedRect(stack, closeButtonX, closeButtonY, closeButtonSize, closeButtonSize, closeBgColor, Round.of(4f));

        int closeIconColor = ColorUtil.multAlpha(ColorUtil.overCol(
                ColorUtil.getColor(180, 180, 180),
                ColorUtil.getColor(255, 100, 100),
                closeHoverAnim
        ), alphaV * searchExpandAnim);

        float iconSize = 8f;
        Fonts.icon.draw(stack, "N", closeButtonX + closeButtonSize / 2f - Fonts.icon.getWidth("C", iconSize) / 2f, closeButtonY + closeButtonSize / 2f - Fonts.icon.getHeight("B", iconSize) / 2f, closeIconColor, iconSize);

        float titleY = searchWindowY + 14.1f;
        String titleText = "Поиск модулей";
        int titleColor = ColorUtil.multAlpha(accentCol, alphaV * searchExpandAnim);
        Fonts.SF_BOLD.draw(stack, titleText, searchWindowX + 14.1f, titleY, titleColor, 8.5f);

        float inputY = searchWindowY + 37.6f;
        float inputX = searchWindowX + 14.1f;
        float inputWidth = searchWindowWidth - 28.2f;
        float inputHeight = 28.2f;

        int inputBgColor = ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 30, 150), alphaV * searchExpandAnim);
        RenderUtil.Rounded.roundedRect(stack, inputX, inputY, inputWidth, inputHeight, inputBgColor, Round.of(6f));

        int inputBorderColor = ColorUtil.multAlpha(ColorUtil.overCol(
                ColorUtil.getColor(60, 60, 65, 100),
                ColorUtil.multAlpha(accentCol, 0.3f),
                searchActive ? 1f : 0f
        ), alphaV * searchExpandAnim);
        RenderUtil.Rounded.roundedOutline(stack, inputX, inputY, inputWidth, inputHeight, 0.8f, inputBorderColor, Round.of(6f));

        float textStartX = inputX + 9.4f;
        float fontSize = 7.5f;

        if (searchQuery.isEmpty()) {
            int placeholderColor = ColorUtil.multAlpha(ColorUtil.getColor(120, 120, 120), alphaV * searchExpandAnim);
            String placeholder = "Введите название модуля...";
            float phY = inputY + inputHeight / 2f - Fonts.SF_REGULAR.getHeight(placeholder, fontSize) / 2f;
            Fonts.SF_REGULAR.draw(stack, placeholder, textStartX, phY, placeholderColor, fontSize);
        } else {
            int textColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alphaV * searchExpandAnim);
            float tY = inputY + inputHeight / 2f - Fonts.SF_REGULAR.getHeight(searchQuery, fontSize) / 2f;

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(textStartX, inputY, inputWidth - 18.8f, inputHeight);
            Fonts.SF_REGULAR.draw(stack, searchQuery, textStartX, tY, textColor, fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
        }

        boolean showCursor = searchActive && ((System.currentTimeMillis() / 500) % 2 == 0);
        if (showCursor) {
            float cursorX = textStartX + (searchQuery.isEmpty() ? 0 : Fonts.SF_REGULAR.getWidth(searchQuery, fontSize)) + 1.5f;
            float cursorY2 = inputY + inputHeight / 2f - 7f;
            int cursorColor = ColorUtil.multAlpha(accentCol, alphaV * searchExpandAnim);
            RenderUtil.Rounded.roundedRect(stack, cursorX, cursorY2, 1f, 14f, cursorColor, Round.of(0.5f));
        }

        float listStartY = inputY + inputHeight + 9.4f;
        float listHeight = searchWindowHeight - (listStartY - searchWindowY) - 14.1f;
        float listX = searchWindowX + 14.1f;
        float listWidth = searchWindowWidth - 28.2f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(listX, listStartY, listWidth, listHeight);
        renderSearchResults(stack, listX, listStartY, listWidth, listHeight, mouseX, mouseY);
        ScissorExp.unset();
        ScissorExp.pop();

        stack.pop();
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    private void renderSearchResults(MatrixStack stack, float x, float y, float width, float height, float mouseX, float mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        if (searchQuery.isEmpty()) {
            String emptyText = "Начните вводить для поиска";
            float textWidth = Fonts.SF_MEDIUM.getWidth(emptyText, 7f);
            float textX = x + (width - textWidth) / 2f;
            float textY = y + height / 2f - Fonts.SF_MEDIUM.getHeight(emptyText, 7f) / 2f;

            int textColor = ColorUtil.multAlpha(ColorUtil.getColor(140, 140, 140), alphaV * searchExpandAnim);
            Fonts.SF_MEDIUM.draw(stack, emptyText, textX, textY, textColor, 7f);
            return;
        }

        ArrayList<org.privates.client.managers.module.Module> foundModules = new ArrayList<>();
        String lowerQuery = searchQuery.toLowerCase();

        for (org.privates.client.managers.module.Category cat : org.privates.client.managers.module.Category.values()) {
            for (org.privates.client.managers.module.Module module : Excellent.inst().moduleManager().get(cat)) {
                String lowerName = module.getName().toLowerCase();
                String lowerDesc = module.getModuleInfo().description().toLowerCase();

                if (lowerName.contains(lowerQuery) || lowerDesc.contains(lowerQuery)) {
                    foundModules.add(module);
                }
            }
        }

        if (foundModules.isEmpty()) {
            String noResultsText = "Ничего не найдено";
            float textWidth = Fonts.SF_MEDIUM.getWidth(noResultsText, 7f);
            float textX = x + (width - textWidth) / 2f;
            float textY = y + height / 2f - Fonts.SF_MEDIUM.getHeight(noResultsText, 7f) / 2f;

            int textColor = ColorUtil.multAlpha(ColorUtil.getColor(140, 140, 140), alphaV * searchExpandAnim);
            Fonts.SF_MEDIUM.draw(stack, noResultsText, textX, textY, textColor, 7f);
            return;
        }

        float itemHeight = 28.2f;
        float itemSpacing = 3.76f;
        float currentY = y + moduleScrollOffset;

        int accentCol = settings.getAccentColor();

        for (org.privates.client.managers.module.Module module : foundModules) {
            if (currentY + itemHeight < y || currentY > y + height) {
                currentY += itemHeight + itemSpacing;
                continue;
            }

            boolean hovered = Mathf.isHover(mouseX, mouseY, x, currentY, width, itemHeight);

            ModuleButton tempButton = null;
            for (ModuleButton mb : moduleButtons) {
                if (mb.getModule() == module) {
                    tempButton = mb;
                    break;
                }
            }
            if (tempButton == null) tempButton = new ModuleButton(this, module);

            float hoverAnim = moduleHoverAnims.getOrDefault(tempButton, 0f);
            hoverAnim = Mathf.fast(hoverAnim, hovered ? 1f : 0f, 16);
            moduleHoverAnims.put(tempButton, hoverAnim);

            if (hoverAnim > 0.01f) {
                int hoverBgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(255, 255, 255, 15),
                        ColorUtil.multAlpha(accentCol, 0.1f),
                        hoverAnim
                ), alphaV * searchExpandAnim);
                RenderUtil.Rounded.roundedRect(stack, x, currentY, width, itemHeight, hoverBgColor, Round.of(5f));
            }

            String moduleName = module.getName();
            String categoryName = module.getCategory().getName();
            String description = module.getModuleInfo().description();

            float textX = x + 9.4f;
            float textY = currentY + itemHeight / 2f;

            int nameColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alphaV * searchExpandAnim);
            int catColor = ColorUtil.multAlpha(accentCol, alphaV * searchExpandAnim * 0.7f);
            int descColor = ColorUtil.multAlpha(ColorUtil.getColor(160, 160, 160), alphaV * searchExpandAnim * 0.6f);
            int separatorColor = ColorUtil.multAlpha(ColorUtil.getColor(100, 100, 100), alphaV * searchExpandAnim * 0.5f);

            float nameSize = 7.5f;
            float catSize = 6.5f;
            float descSize = 6f;

            float nameWidth = Fonts.SF_BOLD.getWidth(moduleName, nameSize);
            float catWidth = Fonts.SF_MEDIUM.getWidth(categoryName, catSize);

            float currentX = textX;
            Fonts.SF_BOLD.draw(stack, moduleName, currentX, textY - Fonts.SF_BOLD.getHeight(moduleName, nameSize) / 2f, nameColor, nameSize);
            currentX += nameWidth + 5.64f;

            Fonts.SF_MEDIUM.draw(stack, "•", currentX, textY - Fonts.SF_MEDIUM.getHeight("•", catSize) / 2f, separatorColor, catSize);
            currentX += Fonts.SF_MEDIUM.getWidth("•", catSize) + 5.64f;

            Fonts.SF_MEDIUM.draw(stack, categoryName, currentX, textY - Fonts.SF_MEDIUM.getHeight(categoryName, catSize) / 2f, catColor, catSize);
            currentX += catWidth + 5.64f;

            Fonts.SF_MEDIUM.draw(stack, "•", currentX, textY - Fonts.SF_MEDIUM.getHeight("•", descSize) / 2f, separatorColor, descSize);
            currentX += Fonts.SF_MEDIUM.getWidth("•", descSize) + 5.64f;

            float remainingWidth = width - (currentX - x) - 9.4f;
            String truncatedDesc = description;
            if (Fonts.SF_MEDIUM.getWidth(truncatedDesc, descSize) > remainingWidth) {
                while (Fonts.SF_MEDIUM.getWidth(truncatedDesc + "...", descSize) > remainingWidth && truncatedDesc.length() > 0) {
                    truncatedDesc = truncatedDesc.substring(0, truncatedDesc.length() - 1);
                }
                truncatedDesc += "...";
            }

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(currentX, currentY, remainingWidth, itemHeight);
            Fonts.SF_MEDIUM.draw(stack, truncatedDesc, currentX, textY - Fonts.SF_MEDIUM.getHeight(truncatedDesc, descSize) / 2f, descColor, descSize);
            ScissorExp.unset();
            ScissorExp.pop();

            currentY += itemHeight + itemSpacing;
        }
    }

    private void renderCategoryIcon(MatrixStack stack, String categoryName, float centerX, float centerY, float iconSize, int color) {
        switch (categoryName) {
            case "CLIENT":
                float clientIconWidth = Fonts.iconf.getWidth("n", iconSize);
                float clientIconHeight = Fonts.iconf.getHeight("n", iconSize);
                Fonts.iconf.draw(stack, "n", centerX - 1 - clientIconWidth / 2f, centerY - 1f - clientIconHeight / 2f, color, iconSize);
                break;
            case "COMBAT":
                float combatIconWidth = Fonts.iconf.getWidth("c", iconSize);
                float combatIconHeight = Fonts.iconf.getHeight("c", iconSize);
                Fonts.iconf.draw(stack, "c", centerX - 1 - combatIconWidth / 2f, centerY - 0.75f - combatIconHeight / 2f, color, iconSize);
                break;
            case "MOVEMENT":
                float movementIconWidth = Fonts.iconf.getWidth("e", iconSize);
                float movementIconHeight = Fonts.iconf.getHeight("e", iconSize);
                Fonts.iconf.draw(stack, "e", centerX - 1 - movementIconWidth / 2f, centerY - 0.33f - movementIconHeight / 2f, color, iconSize);
                break;
            case "PLAYER":
                float playerIconWidth = Fonts.CLICKGUI.getWidth("c", iconSize);
                float playerIconHeight = Fonts.CLICKGUI.getHeight("c", iconSize);
                Fonts.CLICKGUI.draw(stack, "c", centerX + 0.5f - playerIconWidth / 2f, centerY - 0.5f - playerIconHeight / 2f, color, iconSize);
                break;
            case "RENDER":
                float renderIconWidth = Fonts.CLICKGUI.getWidth("d", iconSize);
                float renderIconHeight = Fonts.CLICKGUI.getHeight("d", iconSize);
                Fonts.CLICKGUI.draw(stack, "d", centerX - 0.15f - renderIconWidth / 2f, centerY - 0.2f - renderIconHeight / 2f, color, iconSize);
                break;
            case "MISC":
                float miscIconWidth = Fonts.CLICKGUI.getWidth("e", iconSize);
                float miscIconHeight = Fonts.CLICKGUI.getHeight("e", iconSize);
                Fonts.CLICKGUI.draw(stack, "e", centerX - 0.15f - miscIconWidth / 2f, centerY - 0.2f - miscIconHeight / 2f, color, iconSize);
                break;
        }
    }

    private float calculateModuleHeight(ModuleButton moduleButton, float modWidth) {
        org.privates.client.managers.module.Module module = moduleButton.getModule();
        String description = module.getModuleInfo().description();
        if (description == null || description.isEmpty()) description = "Нет описания";

        float descSize = 6.58f;
        float descWidth = modWidth - 11.28f;
        float lineHeight = Fonts.SF_BOLD.getHeight("A", descSize) + 0.752f;

        String[] words = description.split(" ");
        int lineCount = 0;
        int maxLines = 4;
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float testWidth = Fonts.SF_BOLD.getWidth(testLine, descSize);

            if (testWidth > descWidth && currentLine.length() > 0) {
                lineCount++;
                if (lineCount >= maxLines) break;
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0 && lineCount < maxLines) {
            lineCount++;
        }

        lineCount = Math.max(1, Math.min(lineCount, maxLines));

        float baseHeight = 30.08f;
        float descHeight = lineCount * lineHeight + 1.88f;

        return baseHeight + descHeight;
    }

    private void renderModuleButtons(MatrixStack stack, int mouseX, int mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        float modulesAreaWidth = (width - 18.8f) * 0.5f;
        float modStartX = x + 9.4f;
        float modStartY = y + 45.12f;
        float modWidth = (modulesAreaWidth - 4.7f) / 2f;
        float modSpacing = 2.82f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(modStartX, modStartY, modulesAreaWidth, height - 54.52f);

        float[] columnY = new float[2];
        columnY[0] = modStartY + moduleScrollOffset;
        columnY[1] = modStartY + moduleScrollOffset;

        for (int i = 0; i < moduleButtons.size(); i++) {
            ModuleButton moduleButton = moduleButtons.get(i);
            org.privates.client.managers.module.Module module = moduleButton.getModule();

            float modHeight = calculateModuleHeight(moduleButton, modWidth);
            moduleHeights.put(moduleButton, modHeight);

            int column = columnY[0] <= columnY[1] ? 0 : 1;
            moduleColumns.put(moduleButton, column);

            float modX = modStartX + (column * (modWidth + modSpacing));
            float targetY = columnY[column];

            float animY = moduleAnimY.getOrDefault(moduleButton, targetY);
            float smoothSpeed = 24;
            animY = Mathf.fast(animY, targetY, smoothSpeed);
            moduleAnimY.put(moduleButton, animY);

            columnY[column] += modHeight + modSpacing;

            if (animY + modHeight < modStartY || animY > modStartY + height - 54.52f) {
                continue;
            }

            boolean hovered = Mathf.isHover(mouseX, mouseY, modX, animY, modWidth, modHeight);
            boolean enabled = module.isEnabled();

            float hoverAnim = moduleHoverAnims.getOrDefault(moduleButton, 0f);
            hoverAnim = Mathf.fast(hoverAnim, hovered ? 1f : 0f, 16);
            moduleHoverAnims.put(moduleButton, hoverAnim);

            float enabledAnim = moduleButton.getEnabledAnim();
            enabledAnim = Mathf.fast(enabledAnim, enabled ? 1 : 0, 16);
            moduleButton.setEnabledAnim(enabledAnim);

            int bgAlpha = (int) (35 + hoverAnim * 10);
            int borderAlpha = (int) (60 + hoverAnim * 20);
            int accentCol = settings.getAccentColor();
            int modBgCol = settings.getModuleBgColor();

            int bgColor = ColorUtil.multAlpha(modBgCol, alphaV);
            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(60, 60, 65, borderAlpha), ColorUtil.multAlpha(accentCol, .5f), enabledAnim), alphaV);

            if (hoverAnim > 0.01f || enabledAnim > 0.01f) {
                int glowColor = ColorUtil.multAlpha(accentCol, (hoverAnim * 0.15f + enabledAnim * 0.25f) * alphaV);
                RenderUtil.Shadow.drawShadow(stack, modX - 1.41f, animY - 1.41f, modWidth + 2.82f, modHeight + 2.82f, 3.29f, (hoverAnim * 0.25f + enabledAnim * 0.35f), glowColor, Round.of(4f));
            }

            RenderUtil.Rounded.roundedWithOutline(stack, modX, animY, modWidth, modHeight, 0.8f, bgColor, borderColor, Round.of(4f));

            int titleColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(255, 255, 255), accentCol, enabledAnim), alphaV);

            String moduleName = module.getName();
            float titleSize = 7.5f;
            float titleWidth = modWidth - 15.04f;

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modX + 5.64f, animY + 4.7f, titleWidth, 8.46f);
            Fonts.SF_BOLD.draw(stack, moduleName, modX + 5.64f, animY + 4.7f, titleColor, titleSize);
            ScissorExp.unset();
            ScissorExp.pop();

            String description = module.getModuleInfo().description();
            if (description == null || description.isEmpty()) description = "Нет описания";
            int descColor = ColorUtil.multAlpha(ColorUtil.getColor(180, 180, 180), alphaV);
            float descSize = 6.58f;
            float descWidth = modWidth - 11.28f;
            float descStartY = animY + 16.45f;
            float lineHeight = Fonts.SF_BOLD.getHeight("A", descSize) + 0.752f;

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modX + 5.64f, descStartY, descWidth, modHeight - 26.32f);

            String[] words = description.split(" ");
            StringBuilder currentLine = new StringBuilder();
            float currentY = descStartY;
            int lineCount = 0;
            int maxLines = 4;

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                float testWidth = Fonts.SF_BOLD.getWidth(testLine, descSize);

                if (testWidth > descWidth && currentLine.length() > 0) {
                    if (lineCount < maxLines) {
                        Fonts.SF_BOLD.draw(stack, currentLine.toString(), modX + 5.64f, currentY, descColor, descSize);
                        currentY += lineHeight;
                        lineCount++;
                        currentLine = new StringBuilder(word);
                    } else {
                        break;
                    }
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }

            if (lineCount < maxLines && currentLine.length() > 0) {
                String finalLine = currentLine.toString();
                if (lineCount == maxLines - 1) {
                    float finalWidth = Fonts.SF_BOLD.getWidth(finalLine, descSize);
                    while (finalWidth + Fonts.SF_BOLD.getWidth("...", descSize) > descWidth && finalLine.length() > 0) {
                        finalLine = finalLine.substring(0, finalLine.length() - 1);
                        finalWidth = Fonts.SF_BOLD.getWidth(finalLine, descSize);
                    }
                    if (!finalLine.contentEquals(currentLine)) {
                        finalLine += "...";
                    }
                }
                Fonts.SF_BOLD.draw(stack, finalLine, modX + 5.64f, currentY, descColor, descSize);
            }

            ScissorExp.unset();
            ScissorExp.pop();

            int currentBind = module.getKey();
            if (currentBind != -1 || moduleWaitingForBind.getOrDefault(moduleButton, false)) {
                String bindText;
                if (moduleWaitingForBind.getOrDefault(moduleButton, false)) {
                    bindText = "...";
                } else if (currentBind >= 500) {
                    bindText = "M" + (currentBind - 500 + 1);
                } else {
                    bindText = Keyboard.keyName(currentBind);
                }

                float bindWidth = Fonts.SF_BOLD.getWidth(bindText, 5.17f) + 4.7f;
                float bindX = modX + modWidth - bindWidth - 4.7f;
                float bindY = animY + modHeight - 11.28f;

                RenderUtil.Rounded.roundedRect(stack, bindX, bindY, bindWidth, 8.46f,
                        ColorUtil.multAlpha(ColorUtil.getColor(30, 30, 35), alphaV * 0.6f), Round.of(2.5f));
                Fonts.SF_BOLD.draw(stack, bindText, bindX + 2.35f, bindY + 1.41f, ColorUtil.multAlpha(accentCol, alphaV), 5.17f);
            }

            RenderUtil.Rounded.smooth(stack, modX + modWidth - 18.8f, animY + 4.7f, 14.1f, 7.52f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(30, 30, 30, 180), ColorUtil.multDark(accentCol, .7f), enabledAnim), alphaV),
                    Round.of(4f));
            RenderUtil.Rounded.smooth(stack, modX + modWidth - 17.39f + enabledAnim * 6.58f, animY + 5.64f, 5.64f, 5.64f,
                    ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(80, 80, 80, 200), accentCol, enabledAnim), alphaV),
                    Round.of(3f));
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderSettings(MatrixStack stack, int mouseX, int mouseY) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();
        float modulesAreaWidth = (width - 18.8f) * 0.5f;
        float settingsX = x + modulesAreaWidth + 14.1f;
        float settingsY = y + 45.12f;
        float settingsWidth = (width - 18.8f) * 0.5f - 4.7f;
        float settingsHeight = height - 54.52f;

        if (settingsTransitioning && previousModule != null) {
            stack.push();
            String animType = settings.settingsAnimation().getValue();
            applySettingsAnimation(stack, animType, settingsOutAnim, settingsX, settingsY, settingsWidth, settingsHeight, true);

            if (settings.isRockstarMode()) {
                renderSettingsContentRockstar(stack, mouseX, mouseY, previousModule, settingsX, settingsY, settingsWidth, settingsHeight, 1f - settingsOutAnim);
            } else {
                renderSettingsContent(stack, mouseX, mouseY, previousModule, settingsX, settingsY, settingsWidth, settingsHeight, 1f - settingsOutAnim);
            }
            stack.pop();
        }

        stack.push();
        String animType = settings.settingsAnimation().getValue();
        applySettingsAnimation(stack, animType, 1f - settingsSlideAnim, settingsX, settingsY, settingsWidth, settingsHeight, false);

        if (selectedModule == null || selectedModule.getSettings().isEmpty()) {
            float gifSize = 65.8f;
            float gifX = settingsX + (settingsWidth - gifSize) / 2f;
            float gifY = settingsY + (settingsHeight - gifSize) / 2f - 16.92f;

            float currentAlpha = settingsTransitioning ? settingsSlideAnim * gifAppearAnim : gifAppearAnim;
            gifRender.draw(stack, gifX, gifY, gifSize, gifSize, currentAlpha, 14.1f, true);

            String text = "Пока что ничего не открыто";
            String text2 = "откройте модуль";

            int textCol = settings.getTextColor();

            float textY = gifY + gifSize + 11.28f;
            Fonts.SF_BOLD.draw(stack, text, settingsX + (settingsWidth - Fonts.SF_BOLD.getWidth(text, 7.05f)) / 2f, textY, ColorUtil.multAlpha(textCol, alpha(currentAlpha * 0.8f)), 7.05f);
            Fonts.SF_BOLD.draw(stack, text2, settingsX + (settingsWidth - Fonts.SF_BOLD.getWidth(text2, 6.11f)) / 2f, textY + 9.4f, ColorUtil.multAlpha(textCol, alpha(currentAlpha * 0.6f)), 6.11f);
        } else {
            if (settings.isRockstarMode()) {
                renderSettingsContentRockstar(stack, mouseX, mouseY, selectedModule, settingsX, settingsY, settingsWidth, settingsHeight, settingsSlideAnim * (1f - gifAppearAnim));
            } else {
                renderSettingsContent(stack, mouseX, mouseY, selectedModule, settingsX, settingsY, settingsWidth, settingsHeight, settingsSlideAnim * (1f - gifAppearAnim));
            }
        }

        stack.pop();
    }

    private void applySettingsAnimation(MatrixStack stack, String type, float progress, float settingsX, float settingsY, float settingsWidth, float settingsHeight, boolean reverse) {
        float offset = 37.6f * progress;
        switch (type) {
            case "Слева":
                stack.translate(reverse ? offset : -offset, 0, 0);
                break;
            case "Справа":
                stack.translate(reverse ? -offset : offset, 0, 0);
                break;
            case "Сверху":
                stack.translate(0, reverse ? offset : -offset, 0);
                break;
            case "Снизу":
                stack.translate(0, reverse ? -offset : offset, 0);
                break;
            case "Fade":
                break;
        }
    }

    private void renderSettingsContent(MatrixStack stack, float mouseX, float mouseY, ModuleButton module, float settingsX, float settingsY, float settingsWidth, float settingsHeight, float alphaMultiplier) {
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(settingsX, settingsY, settingsWidth, settingsHeight);

        ArrayList<Setting> settings = module.getSettings();
        float currentY = settingsY + settingsScrollOffset;

        for (Setting setting : settings) {
            // Skip hidden settings completely
            if (!(boolean) setting.getSetting().getVisible().get()) continue;

            float animY = settingAnimations.getOrDefault(setting, currentY);
            animY = Mathf.fast(animY, currentY, 18);
            settingAnimations.put(setting, animY);

            if (animY + setting.getHeight() < settingsY || animY > settingsY + settingsHeight) {
                currentY += setting.getHeight() + 2.82f;
                continue;
            }

            setting.setX(settingsX);
            setting.setY(animY);
            setting.setWidth(settingsWidth);
            setting.setDeltaV(0);
            setting.setAlphaV(alpha(alphaMultiplier));
            setting.render(stack, mouseX, mouseY);

            currentY += setting.getHeight() + 2.82f;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderSettingsContentRockstar(MatrixStack stack, float mouseX, float mouseY, ModuleButton module, float settingsX, float settingsY, float settingsWidth, float settingsHeight, float alphaMultiplier) {
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(settingsX, settingsY, settingsWidth, settingsHeight);

        ArrayList<Setting> moduleSettings = module.getSettings();
        float currentY = settingsY + settingsScrollOffset + 9.4f;
        int columnCount = 2;
        float columnWidth = (settingsWidth - 18.8f) / columnCount;
        float spacing = 7.52f;

        int column = 0;
        float[] columnHeights = new float[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnHeights[i] = currentY;
        }

        for (Setting setting : moduleSettings) {
            if (!(boolean) setting.getSetting().getVisible().get()) continue;

            RockstarSettingAdapter rockstarSetting = rockstarSettings.computeIfAbsent(setting.getSetting(), s ->
                    new RockstarSettingAdapter(setting.getSetting(), 0f, 0f)
            );

            float rockX = settingsX + 9.4f + (column * (columnWidth + spacing));
            float rockY = columnHeights[column];

            rockstarSetting.x(rockX);
            rockstarSetting.y(rockY);
            rockstarSetting.width(columnWidth);

            if (rockY + rockstarSetting.height() >= settingsY && rockY < settingsY + settingsHeight) {
                rockstarSetting.render(stack, mouseX, mouseY, alphaMultiplier * alphaV);
            }

            columnHeights[column] += rockstarSetting.height() + spacing;

            column++;
            if (column >= columnCount) {
                column = 0;
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void updateModuleButtons() {
        moduleButtons.clear();
        if (selectedCategory != null) {
            if (selectedCategory == clientCategory) {
                org.privates.client.managers.module.Module clickGuiSettingsModule = Excellent.inst().moduleManager().get(ClickGuiSettings.class);
                if (clickGuiSettingsModule != null) {
                    ModuleButton button = new ModuleButton(this, clickGuiSettingsModule);
                    moduleButtons.add(button);
                }
            } else {
                ArrayList<org.privates.client.managers.module.Module> modules = new ArrayList<>();
                for (org.privates.client.managers.module.Module module : Excellent.inst().moduleManager().get(selectedCategory.getCategory())) {
                    String lowerName = module.getName().toLowerCase();
                    String lowerQuery = searchQuery.toLowerCase();
                    if (searchQuery.isEmpty() || lowerName.contains(lowerQuery)) modules.add(module);
                }

                modules.sort(Comparator.comparing(m -> m.getName()));

                for (org.privates.client.managers.module.Module module : modules) {
                    ModuleButton button = new ModuleButton(this, module);
                    moduleButtons.add(button);
                }
            }
        }
        moduleAnimY.clear();
        moduleHeights.clear();
        moduleColumns.clear();
        moduleScrollOffset = 0;
        rockstarSettings.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        if (settings.enableResize().getValue()) {
            if (handleResizeClick(mouseX, mouseY)) return true;
        }

        float searchIconSize = 13.16f;
        float searchIconX = x + width - searchIconSize - 9.4f;
        float searchIconY = y + 16.12f;
        float searchBarHeight = 13.16f;

        float maxSearchWidth = 126.8f;
        float currentSearchWidth = 18.8f + (maxSearchWidth - 18.8f) * searchExpandAnim;
        float searchBarX = searchIconX - currentSearchWidth + searchIconSize;

        if (Mathf.isHover(mouseX, mouseY, searchBarX, searchIconY, currentSearchWidth, searchBarHeight)) {
            if (!searchExpanded) {
                searchExpanded = true;
            }
            if (!searchQuery.isEmpty() && mouseX >= searchBarX + currentSearchWidth - 13.16f) {
                searchQuery = "";
                moduleScrollOffset = 0;
            }
            searchActive = true;
            if (settings.sounds().getValue()) SoundUtil.playSound("guiopen.wav", 0.1f);
            return true;
        }

        if (searchExpanded) searchActive = false;

        if (searchExpanded) {
            float modulesAreaWidth = (width - 18.8f) * 0.5f;
            float searchAreaX = x + 9.4f;
            float searchAreaY = y + 45.12f;
            float searchAreaWidth = width - 18.8f;
            float searchAreaHeight = height - 54.52f;

            if (Mathf.isHover(mouseX, mouseY, searchAreaX, searchAreaY, searchAreaWidth, searchAreaHeight)) {
                ArrayList<org.privates.client.managers.module.Module> foundModules = new ArrayList<>();
                String lowerQuery = searchQuery.toLowerCase();

                for (org.privates.client.managers.module.Category cat : org.privates.client.managers.module.Category.values()) {
                    for (org.privates.client.managers.module.Module module : Excellent.inst().moduleManager().get(cat)) {
                        String lowerName = module.getName().toLowerCase();
                        String lowerDesc = module.getModuleInfo().description().toLowerCase();

                        if (lowerName.contains(lowerQuery) || lowerDesc.contains(lowerQuery)) {
                            foundModules.add(module);
                        }
                    }
                }

                float itemHeight = 35.2f;
                float itemSpacing = 4.7f;
                float searchWindowX = searchAreaX + 9.4f;
                float searchWindowY = searchAreaY + 9.4f;
                float currentY = searchWindowY + moduleScrollOffset;

                for (org.privates.client.managers.module.Module module : foundModules) {
                    if (Mathf.isHover(mouseX, mouseY, searchWindowX, currentY, searchAreaWidth - 18.8f, itemHeight)) {
                        for (Category cat : categories) {
                            if (cat.getCategory() == module.getCategory()) {
                                selectedCategory = cat;
                                updateModuleButtons();

                                for (ModuleButton mb : moduleButtons) {
                                    if (mb.getModule() == module) {
                                        previousModule = selectedModule;
                                        selectedModule = mb;
                                        settingsScrollOffset = 0;
                                        settingsTransitioning = true;
                                        settingsOutAnim = 0f;
                                        settingsSlideAnim = 0f;
                                        break;
                                    }
                                }

                                searchExpanded = false;
                                searchActive = false;
                                searchQuery = "";
                                moduleScrollOffset = 0;

                                if (settings.sounds().getValue()) SoundUtil.playSound("mode_open.wav", 0.1f);
                                return true;
                            }
                        }
                    }
                    currentY += itemHeight + itemSpacing;
                }
            }
            return true;
        }

        float catIconSize = 15.04f;
        float catSpacing = 7.52f;
        float totalCategoriesWidth = (categories.size() * catIconSize) + ((categories.size() - 1) * catSpacing);

        float barPadding = 9.4f;
        float totalBarWidth = totalCategoriesWidth + barPadding * 2;

        float catBarX = x + (width - totalBarWidth) / 2f;
        float catY = y + 9.4f;
        float catBarHeight = 26.32f;
        float catStartX = catBarX + barPadding;

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            float currentX = catStartX + (catIconSize + catSpacing) * i;

            if (Mathf.isHover(mouseX, mouseY, currentX - 3.76f, catY + (catBarHeight - catIconSize - 3.76f) / 2f, catIconSize + 7.52f, catIconSize + 3.76f)) {
                if (selectedCategory != category) {
                    selectedCategory = category;
                    moduleScrollOffset = 0;
                    settingsScrollOffset = 0;
                    updateModuleButtons();
                    if (settings.sounds().getValue()) SoundUtil.playSound("select.wav", 0.1f);
                }
                return true;
            }
        }

        float modulesAreaWidth = (width - 18.8f) * 0.5f;
        float modStartX = x + 9.4f;
        float modStartY = y + 45.12f;
        float modWidth = (modulesAreaWidth - 4.7f) / 2f;
        float modSpacing = 2.82f;

        for (ModuleButton moduleButton : moduleButtons) {
            float modHeight = moduleHeights.getOrDefault(moduleButton, calculateModuleHeight(moduleButton, modWidth));
            float animY = moduleAnimY.getOrDefault(moduleButton, modStartY);
            int column = moduleColumns.getOrDefault(moduleButton, 0);
            float modX = modStartX + (column * (modWidth + modSpacing));

            if (button == 2 && Mathf.isHover(mouseX, mouseY, modX, animY, modWidth, modHeight)) {
                for (ModuleButton mb : moduleButtons) moduleWaitingForBind.put(mb, false);
                moduleWaitingForBind.put(moduleButton, true);
                if (settings.sounds().getValue()) SoundUtil.playSound("bind_wait.wav", 0.1f);
                return true;
            }

            if (Mathf.isHover(mouseX, mouseY, modX + modWidth - 18.8f, animY + 4.7f, 14.1f, 7.52f)) {
                moduleButton.getModule().toggle();
                if (settings.sounds().getValue()) {
                    SoundUtil.playSound(moduleButton.getModule().isEnabled() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
                }
                return true;
            }

            if (Mathf.isHover(mouseX, mouseY, modX, animY, modWidth, modHeight)) {
                if (selectedModule != moduleButton) {
                    previousModule = selectedModule;
                    selectedModule = moduleButton;
                    settingsScrollOffset = 0;
                    settingsTransitioning = true;
                    settingsOutAnim = 0f;
                    settingsSlideAnim = 0f;
                    if (settings.sounds().getValue()) SoundUtil.playSound("mode_open.wav", 0.1f);
                }
                return true;
            }
        }

        if (selectedModule != null && !settingsTransitioning) {
            float settingsSlideOffset = 37.6f * (1f - settingsSlideAnim);

            if (settings.isRockstarMode()) {
                for (RockstarSettingAdapter rs : rockstarSettings.values()) {
                    rs.mouse((float) (mouseX - settingsSlideOffset), (float) mouseY, button, GLFW.GLFW_PRESS);
                }
            } else {
                for (Setting setting : selectedModule.getSettings()) {
                    setting.mouse((float) (mouseX - settingsSlideOffset), (float) mouseY, button, GLFW.GLFW_PRESS);
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleResizeClick(double mouseX, double mouseY) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float handleSize = 1.88f;
        float handleLength = 14.1f;

        startMouseX = (float) mouseX;
        startMouseY = (float) mouseY;
        startWidth = width;
        startHeight = height;

        if (isHoveringHandle((float) mouseX, (float) mouseY, x - 9.4f, centerY - handleLength / 2f, handleSize, handleLength)) {
            resizing = true;
            resizeType = ResizeType.WIDTH;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, x + width + 7.52f, centerY - handleLength / 2f, handleSize, handleLength)) {
            resizing = true;
            resizeType = ResizeType.WIDTH;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, centerX - handleLength / 2f, y - 9.4f, handleLength, handleSize)) {
            resizing = true;
            resizeType = ResizeType.HEIGHT;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, centerX - handleLength / 2f, y + height + 7.52f, handleLength, handleSize)) {
            resizing = true;
            resizeType = ResizeType.HEIGHT;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (resizing && button == 0) {
            float mouseDeltaX = (float) mouseX - startMouseX;
            float mouseDeltaY = (float) mouseY - startMouseY;

            switch (resizeType) {
                case WIDTH:
                    width = Math.max(376f, Math.min(846f, startWidth + mouseDeltaX * 0.5f));
                    x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
                    break;
                case HEIGHT:
                    height = Math.max(235f, Math.min(611f, startHeight + mouseDeltaY * 0.5f));
                    y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;
                    break;
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        if (resizing && button == 0) {
            resizing = false;
            resizeType = ResizeType.NONE;
            return true;
        }

        if (selectedModule != null && !settingsTransitioning) {
            float settingsSlideOffset = 37.6f * (1f - settingsSlideAnim);

            if (settings.isRockstarMode()) {
                for (RockstarSettingAdapter rs : rockstarSettings.values()) {
                    rs.mouse((float) (mouseX - settingsSlideOffset), (float) mouseY, button, GLFW.GLFW_RELEASE);
                }
            } else {
                for (Setting setting : selectedModule.getSettings()) {
                    setting.mouse((float) (mouseX - settingsSlideOffset), (float) mouseY, button, GLFW.GLFW_RELEASE);
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (searchExpanded) {
            float modulesAreaWidth = (width - 18.8f) * 0.5f;
            float searchAreaX = x + 9.4f;
            float searchAreaY = y + 45.12f;
            float searchAreaWidth = width - 18.8f;
            float searchAreaHeight = height - 54.52f;

            if (Mathf.isHover(mouseX, mouseY, searchAreaX, searchAreaY, searchAreaWidth, searchAreaHeight)) {
                ArrayList<org.privates.client.managers.module.Module> foundModules = new ArrayList<>();
                String lowerQuery = searchQuery.toLowerCase();

                for (org.privates.client.managers.module.Category cat : org.privates.client.managers.module.Category.values()) {
                    for (org.privates.client.managers.module.Module module : Excellent.inst().moduleManager().get(cat)) {
                        String lowerName = module.getName().toLowerCase();
                        String lowerDesc = module.getModuleInfo().description().toLowerCase();

                        if (lowerName.contains(lowerQuery) || lowerDesc.contains(lowerQuery)) {
                            foundModules.add(module);
                        }
                    }
                }

                float itemHeight = 35.2f;
                float itemSpacing = 4.7f;
                float totalHeight = foundModules.size() * (itemHeight + itemSpacing);
                float visibleHeight = searchAreaHeight - 18.8f;

                moduleScrollOffset += delta * 7.52f;
                float minScroll = Math.min(0, visibleHeight - totalHeight);
                moduleScrollOffset = MathHelper.clamp(moduleScrollOffset, minScroll, 0);
                return true;
            }
        }

        float modulesAreaWidth = (width - 18.8f) * 0.5f;
        float modStartX = x + 9.4f;
        float modStartY = y + 45.12f;

        if (Mathf.isHover(mouseX, mouseY, modStartX, modStartY, modulesAreaWidth, height - 54.52f)) {
            float modSpacing = 2.82f;
            float modWidth = (modulesAreaWidth - 4.7f) / 2f;

            float[] columnHeights = new float[2];
            columnHeights[0] = 0;
            columnHeights[1] = 0;

            for (ModuleButton mb : moduleButtons) {
                float modHeight = moduleHeights.getOrDefault(mb, calculateModuleHeight(mb, modWidth));
                int column = columnHeights[0] <= columnHeights[1] ? 0 : 1;
                columnHeights[column] += modHeight + modSpacing;
            }

            float maxColumnHeight = Math.max(columnHeights[0], columnHeights[1]);
            float visibleHeight = height - 54.52f;

            moduleScrollOffset += delta * 7.52f;
            float minScroll = Math.min(0, visibleHeight - maxColumnHeight);
            moduleScrollOffset = MathHelper.clamp(moduleScrollOffset, minScroll, 0);
            return true;
        }

        float settingsX = x + modulesAreaWidth + 14.1f;
        float settingsY = y + 45.12f;
        float settingsWidth = (width - 18.8f) * 0.5f - 4.7f;
        float settingsHeight = height - 54.52f;

        if (Mathf.isHover(mouseX, mouseY, settingsX, settingsY, settingsWidth, settingsHeight)) {
            if (selectedModule != null) {
                for (Setting setting : selectedModule.getSettings()) {
                    if (setting.isItemSelectorHovered((float) mouseX, (float) mouseY)) {
                        setting.scroll(delta);
                        return true;
                    }
                }
            }

            if (selectedModule != null && !selectedModule.getSettings().isEmpty()) {
                float totalSettingsHeight = 0;
                for (Setting setting : selectedModule.getSettings()) {
                    totalSettingsHeight += setting.getHeight() + 2.82f;
                }
                totalSettingsHeight -= 2.82f;

                settingsScrollOffset += delta * 7.52f;
                float minScroll = Math.min(0, settingsHeight - totalSettingsHeight);
                settingsScrollOffset = MathHelper.clamp(settingsScrollOffset, minScroll, 0);
            }

            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();

        for (ModuleButton moduleButton : moduleButtons) {
            if (moduleWaitingForBind.getOrDefault(moduleButton, false)) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                    moduleButton.getModule().setKey(-1);
                    if (settings.sounds().getValue()) SoundUtil.playSound("bind_remove.wav", 0.1f);
                } else {
                    moduleButton.getModule().setKey(keyCode);
                    if (settings.sounds().getValue()) SoundUtil.playSound("bind_succes.wav", 0.1f);
                }
                moduleWaitingForBind.put(moduleButton, false);
                return true;
            }
        }

        if (searchActive && searchExpanded) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0 && keyCode == GLFW.GLFW_KEY_F) {
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                moduleScrollOffset = 0;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    if (!searchQuery.isEmpty()) {
                        searchQuery = "";
                        moduleScrollOffset = 0;
                    } else {
                        searchExpanded = false;
                    }
                }
                searchActive = false;
            }
            return true;
        }

        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0 && keyCode == GLFW.GLFW_KEY_F) {
            searchExpanded = true;
            searchActive = true;
            return true;
        }

        if (selectedModule != null) {
            if (settings.isRockstarMode()) {
                for (RockstarSettingAdapter rs : rockstarSettings.values()) {
                    rs.key(keyCode);
                }
            } else {
                for (Setting setting : selectedModule.getSettings()) {
                    setting.key(keyCode);
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive && searchExpanded) {
            if (Character.isISOControl(codePoint) || searchQuery.length() >= 40) {
                return true;
            }
            searchQuery += codePoint;
            moduleScrollOffset = 0;
            return true;
        }

        if (selectedModule != null) {
            ClickGuiSettings settings = ClickGuiSettings.getInstance();
            if (!settings.isRockstarMode()) {
                for (Setting setting : selectedModule.getSettings()) {
                    setting.charTyped(codePoint);
                }
            }
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void closeScreen() {
        alpha = 0;
        if (selectedModule != null) {
            for (Setting setting : selectedModule.getSettings()) {
                setting.setSelected(false);
            }
        }
    }

    public float alpha(float alpha) {
        return alpha * alphaV;
    }
}
