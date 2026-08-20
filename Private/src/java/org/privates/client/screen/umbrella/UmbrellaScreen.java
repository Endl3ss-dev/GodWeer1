package org.privates.client.screen.umbrella;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.render.ClickGuiSettings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
public class UmbrellaScreen extends Screen implements IMinecraft {
    private final ArrayList<UmbrellaCategory> categories = new ArrayList<>();
    private final ArrayList<UmbrellaModuleButton> moduleButtons = new ArrayList<>();
    private float x, y, width, height;

    @Setter
    private UmbrellaCategory selectedCategory;
    @Setter
    private UmbrellaModuleButton selectedModule;

    private float alpha, alphaV;
    private String searchQuery = "";
    private boolean searchActive = false;
    private float searchFocusAnim = 0f;
    private float searchHoverAnim = 0f;
    private float searchCaretAnim = 0f;
    private float searchX, searchY, searchW, searchH;
    private boolean searchHovered = false;

    private float moduleScrollOffset = 0f;
    private float settingsScrollOffset = 0f;

    private final Map<UmbrellaModuleButton, Float> moduleAnimY = new HashMap<>();
    private final Map<UmbrellaCategory, Float> categoryHoverAnims = new HashMap<>();
    private final Map<UmbrellaModuleButton, Float> moduleHoverAnims = new HashMap<>();

    private float selectedCategoryIndicator = 0f;
    private float targetCategoryIndicator = 0f;

    private UmbrellaCategory clientCategory;

    public UmbrellaScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        this.width = 418.1f;
        this.height = 308.12f;
        this.x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
        this.y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;

        if (categories.isEmpty()) {
            clientCategory = new UmbrellaCategory(this, null);
            clientCategory.setName("Client");
            categories.add(clientCategory);

            for (org.privates.client.managers.module.Category c : org.privates.client.managers.module.Category.values()) {
                UmbrellaCategory category = new UmbrellaCategory(this, c);
                categories.add(category);
            }

            selectedCategory = categories.get(0);
            updateModuleButtons();
        }

        alpha = 1;
        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();
        alphaV = Mathf.fast(alphaV, alpha, 28);

        if (alphaV <= .1f && alpha == 0) super.closeScreen();

        int bgColor = ColorUtil.rgba(17, 17, 17, 199);

        if (settings.enableBlur().getValue()) {
            BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.replAlpha(bgColor, 165), Round.of(10f)));
            BlurRenderer2.draw(settings.blurStrength().getValue().intValue());
        }

        RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, alphaV), Round.of(10f));

        int shadowColor = ColorUtil.rgba(0, 0, 0, 64);
        RenderUtil.Shadow.drawShadow(stack, x, y, width, height, 4f, 0.25f, shadowColor, Round.of(10f));

        renderSidebar(stack, mouseX, mouseY);
        renderTopBar(stack, mouseX, mouseY);
        renderModulesList(stack, mouseX, mouseY);
        renderSettingsPanel(stack, mouseX, mouseY);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderSidebar(MatrixStack stack, float mouseX, float mouseY) {
        float sidebarWidth = 56f;
        float sidebarX = x;
        float sidebarY = y;
        float sidebarHeight = height;

        int sidebarBg = ColorUtil.rgba(17, 17, 17, 156);
        RenderUtil.Rounded.roundedRect(stack, sidebarX, sidebarY, sidebarWidth, sidebarHeight, ColorUtil.multAlpha(sidebarBg, alphaV), Round.of(10f, 0, 0, 10f));

        int separatorColor = ColorUtil.rgba(28, 27, 26, 255);
        RenderUtil.Rounded.roundedRect(stack, sidebarX + sidebarWidth, sidebarY, 1f, sidebarHeight, ColorUtil.multAlpha(separatorColor, alphaV), Round.of(0));

        float logoSize = 30f;
        float logoX = sidebarX + (sidebarWidth - logoSize) / 2f;
        float logoY = sidebarY + 19f;

        int accentColor = ColorUtil.rgba(220, 35, 64, 255);
        Fonts.icons.draw(stack, "Q", logoX + logoSize / 2f - Fonts.icons.getWidth("Q", logoSize) / 2f, logoY, ColorUtil.multAlpha(accentColor, alphaV), logoSize);

        float categoryStartY = logoY + logoSize + 50f;
        float categorySpacing = 38f;
        float iconSize = 15f;

        int selectedIndex = categories.indexOf(selectedCategory);
        if (selectedIndex != -1) {
            targetCategoryIndicator = categoryStartY + (selectedIndex * categorySpacing);
        }
        selectedCategoryIndicator = Mathf.fast(selectedCategoryIndicator, targetCategoryIndicator, 18);

        int indicatorColor = ColorUtil.rgba(220, 35, 64, 255);
        RenderUtil.Rounded.roundedRect(stack, sidebarX + 14f, selectedCategoryIndicator + (iconSize - 19f) / 2f, 2f, 19f, ColorUtil.multAlpha(indicatorColor, alphaV), Round.of(10f));

        for (int i = 0; i < categories.size(); i++) {
            UmbrellaCategory category = categories.get(i);
            float currentY = categoryStartY + (i * categorySpacing);
            float iconX = sidebarX + (sidebarWidth - iconSize) / 2f;
            float iconY = currentY;

            boolean selected = selectedCategory == category;
            boolean hovered = Mathf.isHover(mouseX, mouseY, sidebarX, iconY - 10f, sidebarWidth, iconSize + 20f);

            float hoverAnim = categoryHoverAnims.getOrDefault(category, 0f);
            hoverAnim = Mathf.fast(hoverAnim, (hovered || selected) ? 1f : 0f, 18);
            categoryHoverAnims.put(category, hoverAnim);

            int iconColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(146, 147, 150), ColorUtil.getColor(220, 35, 64), hoverAnim), alphaV);

            renderCategoryIcon(stack, category, iconX + iconSize / 2f, iconY + iconSize / 2f, iconSize, iconColor);
        }

        float profileSize = 32f;
        float profileX = sidebarX + (sidebarWidth - profileSize) / 2f;
        float profileY = sidebarY + sidebarHeight - profileSize - 13f;

        mc.getTextureManager().bindTexture(mc.player.getLocationSkin());
        RenderUtil.Texture.roundedHead(mc.player.getLocationSkin(), profileX, profileY, profileSize, profileSize, profileSize / 6.4f, alphaV, 0);
    }

    private void renderCategoryIcon(MatrixStack stack, UmbrellaCategory category, float centerX, float centerY, float iconSize, int color) {
        String icon = "";
        if (category == clientCategory) {
            icon = "n";
        } else if (category.getCategory() == org.privates.client.managers.module.Category.COMBAT) {
            icon = "c";
        } else if (category.getCategory() == org.privates.client.managers.module.Category.MOVEMENT) {
            icon = "e";
        } else if (category.getCategory() == org.privates.client.managers.module.Category.PLAYER) {
            Fonts.CLICKGUI.draw(stack, "c", centerX - Fonts.CLICKGUI.getWidth("c", iconSize) / 2f, centerY - Fonts.CLICKGUI.getHeight("c", iconSize) / 2f, color, iconSize);
            return;
        } else if (category.getCategory() == org.privates.client.managers.module.Category.RENDER) {
            Fonts.CLICKGUI.draw(stack, "d", centerX - Fonts.CLICKGUI.getWidth("d", iconSize) / 2f, centerY - Fonts.CLICKGUI.getHeight("d", iconSize) / 2f, color, iconSize);
            return;
        } else if (category.getCategory() == org.privates.client.managers.module.Category.MISC) {
            Fonts.CLICKGUI.draw(stack, "e", centerX - Fonts.CLICKGUI.getWidth("e", iconSize) / 2f, centerY - Fonts.CLICKGUI.getHeight("e", iconSize) / 2f, color, iconSize);
            return;
        }

        if (!icon.isEmpty()) {
            Fonts.iconf.draw(stack, icon, centerX - Fonts.iconf.getWidth(icon, iconSize) / 2f, centerY - Fonts.iconf.getHeight(icon, iconSize) / 2f, color, iconSize);
        }
    }

    private void renderTopBar(MatrixStack stack, float mouseX, float mouseY) {
        float topBarHeight = 28f;
        float topBarX = x + 56f;
        float topBarY = y;
        float topBarWidth = 145f;

        int topBarBg = ColorUtil.rgba(16, 16, 16, 255);
        RenderUtil.Rounded.roundedRect(stack, topBarX, topBarY, topBarWidth, topBarHeight, ColorUtil.multAlpha(topBarBg, alphaV), Round.of(0, 10f, 5f, 0));

        String categoryName = selectedCategory.getName();
        float categoryFontSize = 16f;
        float categoryTextX = topBarX + 8f;
        float categoryTextY = topBarY + (topBarHeight - Fonts.SF_MEDIUM.getHeight(categoryName, categoryFontSize)) / 2f;

        int textColor = ColorUtil.rgba(146, 147, 150, 255);
        Fonts.SF_MEDIUM.draw(stack, categoryName, categoryTextX, categoryTextY, ColorUtil.multAlpha(textColor, alphaV), categoryFontSize);

        searchW = 78f;
        searchH = 18f;
        searchX = topBarX + topBarWidth - searchW - 6f;
        searchY = topBarY + (topBarHeight - searchH) / 2f;
        searchHovered = Mathf.isHover(mouseX, mouseY, searchX, searchY, searchW, searchH);

        searchHoverAnim = Mathf.fast(searchHoverAnim, searchHovered ? 1f : 0f, 16);
        searchFocusAnim = Mathf.fast(searchFocusAnim, searchActive ? 1f : 0f, 16);
        searchCaretAnim += searchActive ? 0.12f : 0f;
        if (searchCaretAnim > Math.PI * 2) {
            searchCaretAnim -= (float) (Math.PI * 2);
        }

        int fieldBase = ColorUtil.overCol(ColorUtil.getColor(25, 25, 25), ColorUtil.getColor(34, 34, 34), searchHoverAnim * 0.8f);
        int fieldStroke = ColorUtil.overCol(ColorUtil.getColor(45, 45, 45), ColorUtil.getColor(220, 35, 64), searchFocusAnim);
        RenderUtil.Rounded.roundedRect(stack, searchX, searchY, searchW, searchH, ColorUtil.multAlpha(fieldBase, alphaV), Round.of(5f));
        RenderUtil.Rounded.roundedOutline(stack, searchX, searchY, searchW, searchH, 0.8f, ColorUtil.multAlpha(fieldStroke, alphaV), Round.of(5f));

        float iconX = searchX + 5f;
        float iconY = searchY + (searchH - Fonts.icon.getHeight("G", 9f)) / 2f + 0.5f;
        Fonts.icon.draw(stack, "G", iconX, iconY, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 9f);

        String text = searchQuery.isEmpty() ? "Search" : searchQuery;
        int searchTextColor = searchQuery.isEmpty()
                ? ColorUtil.multAlpha(ColorUtil.getColor(120, 120, 120), alphaV)
                : ColorUtil.multAlpha(ColorUtil.getColor(230, 230, 230), alphaV);
        float textX = searchX + 16f;
        float textY = searchY + (searchH - Fonts.SF_MEDIUM.getHeight(text, 7.4f)) / 2f;
        Fonts.SF_MEDIUM.draw(stack, text, textX, textY, searchTextColor, 7.4f);

        if (searchActive) {
            float textW = Fonts.SF_MEDIUM.getWidth(searchQuery, 7.4f);
            float caretX = Math.min(searchX + searchW - 8f, textX + textW + 1f);
            float caretAlpha = 0.4f + 0.6f * (float) Math.sin(searchCaretAnim);
            RenderUtil.Rounded.roundedRect(stack, caretX, searchY + 4.2f, 1f, searchH - 8.4f, ColorUtil.multAlpha(ColorUtil.WHITE, caretAlpha * alphaV), Round.zero());
        }

        if (!searchQuery.isEmpty()) {
            float clearX = searchX + searchW - 10f;
            float clearY = searchY + (searchH - Fonts.SF_MEDIUM.getHeight("x", 8f)) / 2f - 0.5f;
            Fonts.SF_MEDIUM.draw(stack, "x", clearX, clearY, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 8f);
        }
    }

    private void renderModulesList(MatrixStack stack, float mouseX, float mouseY) {
        float modulesX = x + 56f;
        float modulesY = y + 28f;
        float modulesWidth = 145f;
        float modulesHeight = height - 28f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(modulesX, modulesY, modulesWidth, modulesHeight);

        float currentY = modulesY + moduleScrollOffset;

        for (UmbrellaModuleButton moduleButton : moduleButtons) {
            float modHeight = 21f;
            float targetY = currentY;

            float animY = moduleAnimY.getOrDefault(moduleButton, targetY);
            animY = Mathf.fast(animY, targetY, 24);
            moduleAnimY.put(moduleButton, animY);

            if (animY + modHeight < modulesY || animY > modulesY + modulesHeight) {
                currentY += modHeight;
                continue;
            }

            boolean hovered = Mathf.isHover(mouseX, mouseY, modulesX, animY, modulesWidth, modHeight);
            boolean enabled = moduleButton.getModule().isEnabled();

            float hoverAnim = moduleHoverAnims.getOrDefault(moduleButton, 0f);
            hoverAnim = Mathf.fast(hoverAnim, hovered ? 1f : 0f, 16);
            moduleHoverAnims.put(moduleButton, hoverAnim);

            float enabledAnim = moduleButton.getEnabledAnim();
            enabledAnim = Mathf.fast(enabledAnim, enabled ? 1 : 0, 16);
            moduleButton.setEnabledAnim(enabledAnim);

            if (hoverAnim > 0.01f) {
                int hoverBg = ColorUtil.rgba(29, 29, 29, 255);
                RenderUtil.Rounded.roundedRect(stack, modulesX, animY, modulesWidth, modHeight, ColorUtil.multAlpha(hoverBg, hoverAnim * alphaV), Round.of(5f));
            }

            if (moduleButton == selectedModule) {
                int selectedBg = ColorUtil.rgba(220, 35, 64, 255);
                RenderUtil.Rounded.roundedRect(stack, modulesX + 14f, animY + (modHeight - 19f) / 2f, 2f, 19f, ColorUtil.multAlpha(selectedBg, alphaV), Round.of(10f));
            }

            String moduleName = moduleButton.getModule().getName();
            float nameFontSize = 15f;
            float nameX = modulesX + 8f;
            float nameY = animY + (modHeight - Fonts.SF_MEDIUM.getHeight(moduleName, nameFontSize)) / 2f;

            int moduleTextColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(146, 147, 150), ColorUtil.getColor(220, 35, 64), enabledAnim), alphaV);

            Fonts.SF_MEDIUM.draw(stack, moduleName, nameX, nameY, moduleTextColor, nameFontSize);

            int currentBind = moduleButton.getModule().getKey();
            if (currentBind != -1) {
                String bindText;
                if (currentBind >= 500) {
                    bindText = "M" + (currentBind - 500 + 1);
                } else {
                    bindText = org.privates.client.utils.keyboard.Keyboard.keyName(currentBind);
                }

                float bindFontSize = 5f;
                float bindWidth = Fonts.SF_MEDIUM.getWidth(bindText, bindFontSize) + 4f;
                float bindX = modulesX + modulesWidth - bindWidth - 3f;
                float bindY = animY + (modHeight - 8f) / 2f;

                int bindBg = ColorUtil.rgba(217, 217, 217, 20);
                RenderUtil.Rounded.roundedRect(stack, bindX, bindY, bindWidth, 8f, ColorUtil.multAlpha(bindBg, alphaV), Round.of(2f));
                Fonts.SF_MEDIUM.draw(stack, bindText, bindX + 2f, bindY + 1f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), bindFontSize);
            }

            currentY += modHeight;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderSettingsPanel(MatrixStack stack, float mouseX, float mouseY) {
        float panelX = x + 56f + 145f;
        float panelY = y;
        float panelWidth = width - 56f - 145f;
        float panelHeight = height;

        if (selectedModule == null || selectedModule.getSettings().isEmpty()) {
            String text = "Выберите модуль";
            float textSize = 15f;
            float textX = panelX + (panelWidth - Fonts.SF_MEDIUM.getWidth(text, textSize)) / 2f;
            float textY = panelY + (panelHeight - Fonts.SF_MEDIUM.getHeight(text, textSize)) / 2f;

            int textColor = ColorUtil.rgba(146, 147, 150, 255);
            Fonts.SF_MEDIUM.draw(stack, text, textX, textY, ColorUtil.multAlpha(textColor, alphaV * 0.5f), textSize);
            return;
        }

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(panelX, panelY, panelWidth, panelHeight);

        float currentY = panelY + settingsScrollOffset + 10f;

        for (UmbrellaSetting setting : selectedModule.getSettings()) {
            if (!(boolean) setting.getSetting().getVisible().get()) continue;

            setting.setX(panelX + 10f);
            setting.setY(currentY);
            setting.setWidth(panelWidth - 20f);
            setting.setAlphaV(alphaV);
            setting.render(stack, mouseX, mouseY);

            currentY += setting.getHeight() + 5f;
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
                    UmbrellaModuleButton button = new UmbrellaModuleButton(this, clickGuiSettingsModule);
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
                    UmbrellaModuleButton button = new UmbrellaModuleButton(this, module);
                    moduleButtons.add(button);
                }
            }
        }
        moduleAnimY.clear();
        moduleScrollOffset = 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClickGuiSettings settings = ClickGuiSettings.getInstance();
        if (button == 0) {
            boolean insideSearch = Mathf.isHover(mouseX, mouseY, searchX, searchY, searchW, searchH);
            if (insideSearch) {
                if (!searchQuery.isEmpty() && mouseX >= searchX + searchW - 13f) {
                    searchQuery = "";
                    updateModuleButtons();
                }
                searchActive = true;
                return true;
            } else if (searchActive) {
                searchActive = false;
            }
        }

        float sidebarWidth = 56f;
        float categoryStartY = y + 19f + 30f + 50f;
        float categorySpacing = 38f;
        float iconSize = 15f;

        for (int i = 0; i < categories.size(); i++) {
            UmbrellaCategory category = categories.get(i);
            float currentY = categoryStartY + (i * categorySpacing);

            if (Mathf.isHover(mouseX, mouseY, x, currentY - 10f, sidebarWidth, iconSize + 20f)) {
                if (selectedCategory != category) {
                    selectedCategory = category;
                    moduleScrollOffset = 0;
                    settingsScrollOffset = 0;
                    selectedModule = null;
                    updateModuleButtons();
                    if (settings.sounds().getValue()) SoundUtil.playSound("select.wav", 0.1f);
                }
                return true;
            }
        }

        float modulesX = x + 56f;
        float modulesY = y + 28f;
        float modulesWidth = 145f;

        for (UmbrellaModuleButton moduleButton : moduleButtons) {
            float modHeight = 21f;
            float animY = moduleAnimY.getOrDefault(moduleButton, modulesY);

            if (Mathf.isHover(mouseX, mouseY, modulesX, animY, modulesWidth, modHeight)) {
                if (button == 0) {
                    selectedModule = moduleButton;
                    settingsScrollOffset = 0;
                    if (settings.sounds().getValue()) SoundUtil.playSound("mode_open.wav", 0.1f);
                } else if (button == 1) {
                    moduleButton.getModule().toggle();
                    if (settings.sounds().getValue()) {
                        SoundUtil.playSound(moduleButton.getModule().isEnabled() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
                    }
                }
                return true;
            }
        }

        if (selectedModule != null) {
            for (UmbrellaSetting setting : selectedModule.getSettings()) {
                setting.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selectedModule != null) {
            for (UmbrellaSetting setting : selectedModule.getSettings()) {
                setting.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float modulesX = x + 56f;
        float modulesY = y + 28f;
        float modulesWidth = 145f;
        float modulesHeight = height - 28f;

        if (Mathf.isHover(mouseX, mouseY, modulesX, modulesY, modulesWidth, modulesHeight)) {
            moduleScrollOffset += delta * 10f;
            moduleScrollOffset = Math.min(0, moduleScrollOffset);
            return true;
        }

        float panelX = x + 56f + 145f;
        float panelY = y;
        float panelWidth = width - 56f - 145f;
        float panelHeight = height;

        if (Mathf.isHover(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight)) {
            settingsScrollOffset += delta * 10f;
            settingsScrollOffset = Math.min(0, settingsScrollOffset);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchActive) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                if (!searchQuery.isEmpty()) {
                    searchQuery = "";
                    updateModuleButtons();
                } else {
                    searchActive = false;
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchQuery.isEmpty()) {
                    searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                    updateModuleButtons();
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                searchActive = false;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_SPACE) {
                if (searchQuery.length() < 32) {
                    searchQuery += " ";
                    updateModuleButtons();
                }
                return true;
            }
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0 || (modifiers & GLFW.GLFW_MOD_ALT) != 0) {
                return true;
            }
        }

        if (selectedModule != null) {
            for (UmbrellaSetting setting : selectedModule.getSettings()) {
                setting.key(keyCode);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive) {
            if (Character.isISOControl(codePoint)) {
                return true;
            }
            if (searchQuery.length() >= 32) {
                return true;
            }
            searchQuery += codePoint;
            updateModuleButtons();
            return true;
        }

        if (selectedModule != null) {
            for (UmbrellaSetting setting : selectedModule.getSettings()) {
                setting.charTyped(codePoint);
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void closeScreen() {
        alpha = 0;
        if (selectedModule != null) {
            for (UmbrellaSetting setting : selectedModule.getSettings()) {
                setting.setSelected(false);
            }
        }
    }

    public float alpha(float alpha) {
        return alpha * alphaV;
    }
}
