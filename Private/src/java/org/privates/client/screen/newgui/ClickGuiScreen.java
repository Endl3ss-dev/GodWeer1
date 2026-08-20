package org.privates.client.screen.newgui;

import lombok.Getter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
public class ClickGuiScreen extends Screen implements IMinecraft {

    private final ArrayList<Panel> panels = new ArrayList<>();
    private Panel themePanel;
    private final SearchWindow searchWindow;
    private final DynamicIsland dynamicIsland;

    private boolean input = false;
    private float inputV;
    private String find = "";
    private float scale, scaleV;

    private float panelWidth;
    private float panelHeight;
    private float panelSpacing;
    private float baseFontSize;
    private float baseIconSize;

    private float resolutionScale = 1f;

    private int lastFramebufferWidth = -1;
    private int lastFramebufferHeight = -1;

    private boolean resizing = false;
    private ResizeType resizeType = ResizeType.NONE;
    private float startMouseX, startMouseY;
    private float startValue;

    private final float handleSize = 2f;
    private final float handleLength = 15f;

    private final boolean settingsOpen = false;
    private float settingsAnimation = 0f;

    private final int accentColor = ColorUtil.getColor(120, 180, 255);
    private final int backgroundColor = ColorUtil.getColor(10, 10, 15, 140);
    private boolean closing = false;
    private float guiFadeAlpha = 0f;

    private boolean isInitialOpen = true;

    private boolean ctrlPressed = false;
    private Panel clientPanel;
    private ViewMode currentViewMode = ViewMode.UI;

    public enum ViewMode {
        UI, CLIENT, HUD, SEARCH
    }

    public ClickGuiScreen() {
        super(new StringTextComponent(""));
        this.searchWindow = new SearchWindow(this);
        this.dynamicIsland = new DynamicIsland(this);
    }

    public float getResolutionScale() {
        int width = mc.getMainWindow().getFramebufferWidth();
        int height = mc.getMainWindow().getFramebufferHeight();

        if (width >= 3840 && height >= 2160) {
            return 1f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 && height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    public float getAdaptiveFontSize() {
        float widthScale = panelWidth / (122f * resolutionScale);
        return baseFontSize * Math.max(0.7f, Math.min(1.5f, widthScale)) * resolutionScale;
    }

    public float getAdaptiveIconSize() {
        float averageScale = ((panelWidth / (122f * resolutionScale)) + (panelHeight / (300f * resolutionScale))) / 2f;
        return baseIconSize * Math.max(0.6f, Math.min(1.8f, averageScale)) * resolutionScale;
    }

    public float getAdaptiveTextSize() {
        float widthScale = panelWidth / (122f * resolutionScale);
        return 7f * Math.max(0.8f, Math.min(1.3f, widthScale)) * resolutionScale;
    }

    public float getAdaptiveTitleSize() {
        float widthScale = panelWidth / (122f * resolutionScale);
        return 9f * Math.max(0.8f, Math.min(1.4f, widthScale)) * resolutionScale;
    }

    public float getAdaptiveModuleTextSize() {
        float widthScale = panelWidth / (122f * resolutionScale);
        return 8f * Math.max(0.7f, Math.min(1.2f, widthScale)) * resolutionScale;
    }

    @Override
    protected void init() {
        int currentWidth = mc.getMainWindow().getFramebufferWidth();
        int currentHeight = mc.getMainWindow().getFramebufferHeight();

        if (lastFramebufferWidth != currentWidth || lastFramebufferHeight != currentHeight) {
            resolutionScale = getResolutionScale();
            lastFramebufferWidth = currentWidth;
            lastFramebufferHeight = currentHeight;
        }

        if (Excellent.inst().configManager().hasSavedDimensions()) {
            panelWidth = Excellent.inst().configManager().getSavedPanelWidth();
            panelHeight = Excellent.inst().configManager().getSavedPanelHeight();
            panelSpacing = Excellent.inst().configManager().getSavedPanelSpacing();
        } else {
            panelWidth = 122f * resolutionScale;
            panelHeight = 300f * resolutionScale;
            panelSpacing = 5f * resolutionScale;
        }

        baseFontSize = 12f * resolutionScale;
        baseIconSize = 40f * resolutionScale;

        if (panels.isEmpty() || themePanel == null) {
            updatePanelPositions();
        } else {
            float sScale = getResolutionScale();
            float totalWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
            float offsetX = mc.getMainWindow().getScaledWidth() / 2f - totalWidth / 2f;

            float centerX = mc.getMainWindow().getScaledWidth() / 2f;
            float centerY = mc.getMainWindow().getScaledHeight() / 2f;
            themePanel.setX(centerX - panelWidth / 2f);
            themePanel.setY(centerY - panelHeight / 2f);
            themePanel.setCustomSize(panelWidth, panelHeight);

            int idx = 0;
            for (Panel panel : panels) {
                panel.setX(offsetX);
                panel.setY(mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f);
                panel.setCustomSize(panelWidth, panelHeight);
                offsetX += panelWidth + panelSpacing;
                idx++;
            }
        }

        if (isInitialOpen) {
            SoundUtil.playSound("guiopen.wav", 0.3f);
            scale = 1;
            closing = false;
            if (themePanel != null) {
                themePanel.setupInitialAnimation();
            }
            for (Panel panel : panels) {
                panel.setupInitialAnimation();
            }
            isInitialOpen = false;
        }

        dynamicIsland.init();
        searchWindow.setOpen(false);

        super.init();
    }

    private void updatePanelPositions() {
        panels.clear();
        float sScale = getResolutionScale();

        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        float centerX = mc.getMainWindow().getScaledWidth() / 2f;
        float centerY = mc.getMainWindow().getScaledHeight() / 2f;
        themePanel = new Panel(this, null, centerX - panelWidth / 2f, centerY - panelHeight / 2f, -1, true);
        themePanel.setCustomSize(panelWidth, panelHeight);

        if (isOldMode) {
            // Старый режим - панели без ограничений, горизонтальный скролл
            float startX = 0;
            int idx = 0;
            for (Category category : Category.values()) {
                Panel panel = new Panel(this, category, startX, mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f, idx, false);
                panel.setCustomSize(panelWidth, panelHeight);
                panels.add(panel);
                startX += panelWidth + panelSpacing;
                idx++;
            }
        } else {
            // Новый режим - 5 панелей с ограничением
            float totalWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
            float offsetX = mc.getMainWindow().getScaledWidth() / 2f - totalWidth / 2f;

            int idx = 0;
            for (Category category : Category.values()) {
                Panel panel = new Panel(this, category, offsetX, mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f, idx, false);
                panel.setCustomSize(panelWidth, panelHeight);
                panels.add(panel);
                offsetX += panelWidth + panelSpacing;
                idx++;
            }
        }
    }

    public void onTabChanged(DynamicIsland.IslandTab tab) {
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        switch (tab) {
            case UI:
                currentViewMode = ViewMode.UI;
                searchWindow.setOpen(false);
                if (themePanel != null) {
                    float sScale = getResolutionScale();
                    float centerX = mc.getMainWindow().getScaledWidth() / 2f;
                    float centerY = mc.getMainWindow().getScaledHeight() / 2f;
                    themePanel.setX(centerX - panelWidth / 2f);
                    themePanel.setY(centerY - panelHeight / 2f);
                }

                if (isOldMode) {
                    // Старый режим - просто позиционируем панели горизонтально
                    float startX = 0;
                    for (Panel panel : panels) {
                        panel.setX(startX);
                        panel.setY(mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f);
                        startX += panelWidth + panelSpacing;
                    }
                } else {
                    // Новый режим - центрируем панели
                    float totalWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
                    float offsetX = mc.getMainWindow().getScaledWidth() / 2f - totalWidth / 2f;
                    for (Panel panel : panels) {
                        panel.setX(offsetX);
                        panel.setY(mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f);
                        offsetX += panelWidth + panelSpacing;
                    }
                }
                break;
            case CLIENT:
                currentViewMode = ViewMode.CLIENT;
                searchWindow.setOpen(false);
                if (clientPanel == null) {
                    clientPanel = themePanel;
                }
                for (Panel panel : panels) {
                    panel.setX(-1000);
                }
                if (themePanel != null) {
                    float centerX = mc.getMainWindow().getScaledWidth() / 2f;
                    float centerY = mc.getMainWindow().getScaledHeight() / 2f;
                    themePanel.setX(centerX - panelWidth / 2f);
                    themePanel.setY(centerY - panelHeight / 2f);
                }
                break;
            case HUD:
                currentViewMode = ViewMode.HUD;
                searchWindow.setOpen(false);
                for (Panel panel : panels) {
                    panel.setX(-1000);
                }
                if (themePanel != null) {
                    themePanel.setX(-1000);
                }
                break;
            case SEARCH:
                currentViewMode = ViewMode.SEARCH;
                searchWindow.setOpen(true);
                searchWindow.init();
                break;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        long currentTime = System.currentTimeMillis();
        scaleV = Mathf.fast(scaleV, scale, 16);
        settingsAnimation = Mathf.fast(settingsAnimation, settingsOpen ? 1 : 0, 16);

        float maxPanelProgress = 0f;
        if (!panels.isEmpty()) {
            for (Panel panel : panels) {
                maxPanelProgress = Math.max(maxPanelProgress, panel.getPanelScale());
            }
        }
        if (themePanel != null) {
            maxPanelProgress = Math.max(maxPanelProgress, themePanel.getPanelScale());
        }
        guiFadeAlpha = maxPanelProgress;

        if (scale == 0 && scaleV < 0.01f) {
            super.closeScreen();
        }

        MainWindow mw = mc.getMainWindow();
        float scaledWidth = mw.getScaledWidth(), scaledHeight = mw.getScaledHeight();

        inputV = Mathf.fast(inputV, input ? 15 : 0, 24);

        LiquidGlassRenderer.captureBackground();

        dynamicIsland.render(matrixStack, mouseX, mouseY, partialTicks, guiFadeAlpha);

        float hintX = 15 * resolutionScale;
        float hintY = 15 * resolutionScale;
        float hintSize = 7f * resolutionScale;
        int hintColor = ColorUtil.getColor(255, 255, 255, (int) (0.6f * guiFadeAlpha * 255));

        Fonts.SF_REGULAR.draw(matrixStack, "CTRL - show binds", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        Fonts.SF_REGULAR.draw(matrixStack, "CTRL + F - search", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        Fonts.SF_REGULAR.draw(matrixStack, "Middle Click (MOUSE3) - binding", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        Fonts.SF_REGULAR.draw(matrixStack, "RMB - open settings", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        Fonts.SF_REGULAR.draw(matrixStack, "LMB - enable module", hintX, hintY, hintColor, hintSize);

        // Show scroll hint for old panel mode
        if (FlatGui.getInstance().panelRenderMode().is("Старый") && currentViewMode == ViewMode.UI) {
            hintY += 12 * resolutionScale;
            Fonts.SF_REGULAR.draw(matrixStack, "Scroll - move vertical", hintX, hintY, hintColor, hintSize);
            hintY += 12 * resolutionScale;
            Fonts.SF_REGULAR.draw(matrixStack, "SHIFT + Scroll - move horizontal", hintX, hintY, hintColor, hintSize);
        }

        if (currentViewMode == ViewMode.SEARCH) {
            org.privates.client.managers.module.Module highlightedModule = searchWindow.getHighlightedModule();
            if (highlightedModule != null) {
                renderUIWithDimming(matrixStack, mouseX, mouseY, currentTime, highlightedModule);
            } else {
                renderUIPanels(matrixStack, mouseX, mouseY, currentTime);
            }
        } else if (currentViewMode == ViewMode.UI) {
            renderUIPanels(matrixStack, mouseX, mouseY, currentTime);
        } else if (currentViewMode == ViewMode.CLIENT) {
            renderClientPanel(matrixStack, mouseX, mouseY, currentTime);
        } else if (currentViewMode == ViewMode.HUD) {
            renderHUDSettings(matrixStack, mouseX, mouseY);
        }

        Panel.DescriptionInfo bestDescription = null;
        float maxAlpha = 0f;

        if (currentViewMode == ViewMode.UI || currentViewMode == ViewMode.SEARCH) {
            for (int i = 0; i < panels.size(); i++) {
                Panel panel = panels.get(i);
                if (panel.getAnimProgress() > 0.01f || !closing) {
                    Panel.DescriptionInfo info = panel.getDescriptionInfo((float) mouseX, (float) mouseY);
                    if (info.alpha() > maxAlpha) {
                        maxAlpha = info.alpha();
                        bestDescription = info;
                    }
                }
            }
        } else if (currentViewMode == ViewMode.CLIENT) {
            if (themePanel != null && (themePanel.getAnimProgress() > 0.01f || !closing)) {
                Panel.DescriptionInfo info = themePanel.getDescriptionInfo((float) mouseX, (float) mouseY);
                if (info.alpha() > maxAlpha) {
                    maxAlpha = info.alpha();
                    bestDescription = info;
                }
            }
        }

        if (bestDescription != null && bestDescription.alpha() > 0.01f && !bestDescription.description().isEmpty()) {
            renderDescription(matrixStack, bestDescription.description(), bestDescription.alpha() * guiFadeAlpha, scaledWidth, scaledHeight);
        } else {
            if (!lastDescription.isEmpty()) {
                lastDescription = "";
                descriptionTextLength = 0;
                descriptionTextStartTime = 0L;
            }
        }

        if (currentViewMode == ViewMode.UI) {
            renderResizeHandles(matrixStack, mouseX, mouseY);
        }

        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();
        if (displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") ||
                displayMode.equals("Модулем") || displayMode.equals("Сверху GUI")) {
            renderSeparateSettingsWindow(matrixStack, mouseX, mouseY, scaledWidth, scaledHeight);
        }

        if (currentViewMode == ViewMode.SEARCH) {
            matrixStack.push();
            matrixStack.translate(0, 0, 1000);
            searchWindow.render(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.pop();
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderUIWithDimming(MatrixStack stack, int mouseX, int mouseY, long currentTime, org.privates.client.managers.module.Module highlightedModule) {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.updateAnimation(currentTime, scaleV);
            if (panel.getAnimProgress() > 0.01f || !closing) {
                stack.push();
                stack.translate(0, 0, i * 0.01f);

                for (org.privates.client.screen.newgui.Module guiModule : panel.getModules()) {
                    guiModule.setHighlighted(guiModule.getModule() == highlightedModule);
                }

                panel.render(stack, mouseX, mouseY);

                boolean hasHighlightedModule = false;
                for (org.privates.client.screen.newgui.Module guiModule : panel.getModules()) {
                    if (guiModule.getModule() == highlightedModule) {
                        hasHighlightedModule = true;
                        break;
                    }
                }

                if (!hasHighlightedModule) {
                    float dimX = panel.getX();
                    float dimY = panel.getAnimatedY();
                    float dimW = panel.getWidth();
                    float dimH = panel.getHeight();

                    int dimColor = ColorUtil.getColor(0, 0, 0, 120);
                    RenderUtil.Rounded.roundedRect(stack, dimX, dimY, dimW, dimH, dimColor, Round.of(6 * panel.getGui().getResolutionScale()));
                }

                stack.pop();
            }
        }
    }

    private void renderUIPanels(MatrixStack stack, int mouseX, int mouseY, long currentTime) {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.updateAnimation(currentTime, scaleV);
            if (panel.getAnimProgress() > 0.01f || !closing) {
                stack.push();
                stack.translate(0, 0, i * 0.01f);
                panel.render(stack, mouseX, mouseY);
                stack.pop();
            }
        }
    }

    private void renderClientPanel(MatrixStack stack, int mouseX, int mouseY, long currentTime) {
        if (themePanel != null) {
            themePanel.updateAnimation(currentTime, scaleV);
            if (themePanel.getAnimProgress() > 0.01f || !closing) {
                stack.push();
                stack.translate(0, 0, 100f);
                themePanel.render(stack, mouseX, mouseY);
                stack.pop();
            }
        }
    }

    private void renderHUDSettings(MatrixStack stack, int mouseX, int mouseY) {
        float sScale = getResolutionScale();
        float centerX = mc.getMainWindow().getScaledWidth() / 2f;
        float centerY = mc.getMainWindow().getScaledHeight() / 2f;

        String text = "HUD Settings";
        float textSize = 14f * sScale;
        float textWidth = Fonts.SF_BOLD.getWidth(text, textSize);
        int textColor = ColorUtil.multAlpha(Theme.getInstance().textColor(), 0.8f);

        Fonts.SF_BOLD.draw(stack, text, centerX - textWidth / 2f, centerY - textSize / 2f, textColor, textSize);

        String subText = "Coming soon...";
        float subSize = 9f * sScale;
        float subWidth = Fonts.SF_REGULAR.getWidth(subText, subSize);
        int subColor = ColorUtil.multAlpha(Theme.getInstance().textColor(), 0.5f);

        Fonts.SF_REGULAR.draw(stack, subText, centerX - subWidth / 2f, centerY + 10 * sScale, subColor, subSize);
    }

    private float descriptionWidthAnim = 0f;
    private int descriptionTextLength = 0;
    private long descriptionTextStartTime = 0L;
    private String lastDescription = "";

    private void renderDescription(MatrixStack matrixStack, String description, float alpha, float scaledWidth, float scaledHeight) {
        Theme theme = Theme.getInstance();
        float sScale = resolutionScale;
        float descFontSize = getAdaptiveTextSize() * 1.1f;
        float padding = 12 * sScale;

        if (!description.equals(lastDescription)) {
            lastDescription = description;
            descriptionTextLength = 0;
            descriptionTextStartTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - descriptionTextStartTime;
        int targetLength = description.length();
        int charsPerSecond = 30;
        int newLength = (int) Math.min(targetLength, (elapsed / 1000.0) * charsPerSecond);

        if (newLength > descriptionTextLength) {
            descriptionTextLength = newLength;
        }

        String displayText = description.substring(0, Math.min(descriptionTextLength, description.length()));
        if (descriptionTextLength < targetLength && System.currentTimeMillis() % 500 < 250) {
            displayText += "|";
        }

        float textWidth = Fonts.SF_REGULAR.getWidth(displayText, descFontSize);
        float descHeight = 24 * sScale;

        descriptionWidthAnim = Mathf.fast(descriptionWidthAnim, textWidth + padding * 2, 8);

        float centerX = scaledWidth / 2f;
        float centerY = scaledHeight / 2f;
        float descX = centerX - descriptionWidthAnim / 2f;
        float descY = centerY - panelHeight / 2f - 50 * sScale;

        RenderUtil.CustomStyled.drawGui(matrixStack, descX, descY, descriptionWidthAnim, descHeight, 12 * sScale, true, (int) (theme.blurStrength().getValue() * alpha));

        float textX = descX + descriptionWidthAnim / 2f - textWidth / 2f;
        float textY = descY + descHeight / 2f - Fonts.SF_REGULAR.getHeight(displayText, descFontSize) / 2f;

        Fonts.SF_REGULAR.draw(matrixStack, displayText, textX, textY, ColorUtil.multAlpha(theme.textColor(), alpha), descFontSize);
    }

    private void renderResizeHandles(MatrixStack matrixStack, float mouseX, float mouseY) {
        if (guiFadeAlpha < 0.01f) return;
        
        // Hide resize handles in old panel mode
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");
        if (isOldMode) return;

        float centerX = mc.getMainWindow().getScaledWidth() / 2f;
        float centerY = mc.getMainWindow().getScaledHeight() / 2f;

        float totalWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
        float totalHeight = panelHeight;

        float left = centerX - totalWidth / 2f - 20 * resolutionScale;
        float right = centerX + totalWidth / 2f + 20 * resolutionScale;
        float top = centerY - totalHeight / 2f - 20 * resolutionScale;
        float bottom = centerY + totalHeight / 2f + 20 * resolutionScale;

        float hSize = handleSize * resolutionScale;
        float hLength = handleLength * resolutionScale;

        int handleColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 180), guiFadeAlpha);
        int hoverColor = ColorUtil.multAlpha(accentColor, guiFadeAlpha);

        boolean leftHover = isHoveringHandle(mouseX, mouseY, left, centerY - hLength / 2f, hSize, hLength);
        RenderUtil.Rounded.roundedRect(matrixStack, left, centerY - hLength / 2f, hSize, hLength, leftHover ? hoverColor : handleColor, Round.of(3 * resolutionScale));

        boolean rightHover = isHoveringHandle(mouseX, mouseY, right - hSize, centerY - hLength / 2f, hSize, hLength);
        RenderUtil.Rounded.roundedRect(matrixStack, right - hSize, centerY - hLength / 2f, hSize, hLength, rightHover ? hoverColor : handleColor, Round.of(3 * resolutionScale));

        boolean topHover = isHoveringHandle(mouseX, mouseY, centerX - hLength / 2f, top, hLength, hSize);
        RenderUtil.Rounded.roundedRect(matrixStack, centerX - hLength / 2f, top, hLength, hSize, topHover ? hoverColor : handleColor, Round.of(3 * resolutionScale));

        boolean bottomHover = isHoveringHandle(mouseX, mouseY, centerX - hLength / 2f, bottom - hSize, hLength, hSize);
        RenderUtil.Rounded.roundedRect(matrixStack, centerX - hLength / 2f, bottom - hSize, hLength, hSize, bottomHover ? hoverColor : handleColor, Round.of(3 * resolutionScale));

        boolean centerHover = isHoveringHandle(mouseX, mouseY, centerX - hSize / 2f, centerY - hSize / 2f, hSize, hSize);
        RenderUtil.Rounded.roundedRect(matrixStack, centerX - hSize / 2f, centerY - hSize / 2f, hSize, hSize, centerHover ? hoverColor : handleColor, Round.of(3 * resolutionScale));
    }

    private boolean isHoveringHandle(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderSeparateSettingsWindow(MatrixStack matrixStack, float mouseX, float mouseY, float scaledWidth, float scaledHeight) {
        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();

        if (displayMode.equals("Отдельно")) {
            if (!anySelected()) return;
            renderOtdelnoMode(matrixStack, mouseX, mouseY, scaledWidth, scaledHeight);
        } else if (displayMode.equals("Рядом с модулем")) {
            renderRyadomMode(matrixStack, mouseX, mouseY, scaledWidth, scaledHeight);
        } else if (displayMode.equals("Модулем")) {
            renderModulemMode(matrixStack, mouseX, mouseY, scaledWidth, scaledHeight);
        } else if (displayMode.equals("Сверху GUI")) {
            if (!anySelected()) return;
            renderSverkhuMode(matrixStack, mouseX, mouseY, scaledWidth, scaledHeight);
        }
    }

    private void renderOtdelnoMode(MatrixStack matrixStack, float mouseX, float mouseY, float scaledWidth, float scaledHeight) {
        float halfW = 140 * resolutionScale;
        float halfH = 200 * resolutionScale;
        float winW = 280 * resolutionScale;
        float winH = 400 * resolutionScale;
        float scY = 30 * resolutionScale;

        for (Panel panel : panels) {
            for (Module module : panel.getModules()) {
                if (module.getSelectedV() >= .1f) {
                    module.recalculate();
                    float x = scaledWidth / 2f - halfW, y = scaledHeight / 2f - halfH;
                    float width = winW, height = winH;
                    final float selectedV = module.getSelectedV();

                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((scaledWidth) / 2f, (scaledHeight) / 2f, 1);
                    RenderSystem.scalef(selectedV, selectedV, selectedV);
                    RenderSystem.translatef(-(scaledWidth) / 2f, -(scaledHeight) / 2f, 1);
                    LiquidGlassRenderer.captureBackground();
                    LiquidGlassRenderer.drawLiquidGlass(matrixStack, x, y, width, height, 6 * resolutionScale / 6f,
                            Excellent.inst().moduleManager().get(Theme.class).liquidGlassStrength().getValue() * 1000f,
                            Excellent.inst().moduleManager().get(Theme.class).blurStrength().getValue() / 33f,
                            ColorUtil.getColor(255, 255, 255, .3f));

                    RenderUtil.Rounded.roundedOutline(matrixStack, x, y, width, height, 1.5f * resolutionScale,
                            ColorUtil.multAlpha(accentColor, 0.7f), Round.of(6 * resolutionScale));

                    Fonts.SF_REGULAR.draw(matrixStack, module.getModule().getName(),
                            x + width / 2f - Fonts.SF_REGULAR.getWidth(module.getModule().getName(), getAdaptiveFontSize()) / 2f,
                            y + 10 * resolutionScale, ColorUtil.getColor(255, 255, 255), getAdaptiveFontSize());

                    Setting prevSetting = null;
                    ScissorExp.push();
                    ScissorExp.setFromComponentCoordinates(x, y + scY, width, height - scY, selectedV);
                    for (Setting setting : module.getSettings()) {
                        setting.setOffsetY(Mathf.fast(setting.getOffsetY(), ((boolean) setting.getSetting().getVisible().get()) ? 1 : 0, 3));
                        setting.setAlphaV(Mathf.fast(setting.getAlphaV(), ((boolean) setting.getSetting().getVisible().get()) ? 1 : 0, 3));

                        setting.setDeltaV(Mathf.fast(setting.getDeltaV(), setting.getDelta(), 16));
                        if (setting.getOffsetY() >= .1f && Mathf.inRange(setting.getY() + setting.getDeltaV(), setting.getHeight(), y + scY, height - scY)) {
                            if (prevSetting != null && setting.getOffsetY() <= .9f) {
                                ScissorExp.push();
                                ScissorExp.setFromComponentCoordinates(setting.getX(), prevSetting.getY() + prevSetting.getHeight(), setting.getWidth(), setting.getHeight() + 6 * resolutionScale);
                            }
                            setting.render(matrixStack, mouseX, mouseY);
                            if (prevSetting != null && setting.getOffsetY() <= .9f) {
                                ScissorExp.unset();
                                ScissorExp.pop();
                            }
                        }
                        prevSetting = setting;
                    }
                    ScissorExp.unset();
                    ScissorExp.pop();

                    ScissorExp.push();
                    ScissorExp.setFromComponentCoordinates(x + width, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
                    for (Setting setting : module.getSettings()) {
                        if (setting.getSetting() instanceof ColorSetting && setting.getColorPicker() != null) {
                            setting.getColorPicker().setX(Mathf.fast(setting.getColorPicker().getX(), setting.getColorPicker().getScale(), 16));
                            if (setting.getColorPicker().getX() + setting.getColorPicker().getWidth() >= x + width)
                                setting.getColorPicker().render(matrixStack, (int) mouseX, (int) mouseY);
                        }
                    }
                    ScissorExp.unset();
                    ScissorExp.pop();
                    RenderSystem.popMatrix();
                }
            }
        }
    }

    private void renderRyadomMode(MatrixStack matrixStack, float mouseX, float mouseY, float scaledWidth, float scaledHeight) {
        for (Panel panel : panels) {
            for (Module module : panel.getModules()) {
                if (module.getSelectedV() >= .1f) {
                    float moduleX = module.getX();
                    float moduleY = module.getY() + module.getDelta();

                    int visibleSettingsCount = 0;
                    float totalSettingsHeight = 0;
                    for (Setting s : module.getSettings()) {
                        if ((boolean) s.getSetting().getVisible().get()) {
                            visibleSettingsCount++;
                            totalSettingsHeight += s.getHeight() + 3 * resolutionScale;
                        }
                    }

                    if (visibleSettingsCount == 0) continue;

                    float winW = 140 * resolutionScale;
                    float winH = Math.min(totalSettingsHeight + 30 * resolutionScale, 300 * resolutionScale);
                    float winX = moduleX + module.getWidth() + 8 * resolutionScale;
                    float winY = moduleY;

                    if (winX + winW > scaledWidth) {
                        winX = moduleX - winW - 8 * resolutionScale;
                    }

                    final float alpha = module.getSelectedV();
                    final float finalWinX = winX;
                    final float finalWinY = winY;
                    final float finalWinW = winW;
                    final float finalWinH = winH;

                    float targetPanelX = panel.getX();
                    if (winX < moduleX) {
                        targetPanelX = panel.getX() + (winW + 16 * resolutionScale) * alpha;
                    } else {
                        targetPanelX = panel.getX() - (winW + 16 * resolutionScale) * alpha;
                    }
                    panel.setX(Mathf.fast(panel.getX(), targetPanelX, 12));

                    BlurRenderer2.registerRenderCall(() -> {
                        RenderUtil.Rounded.roundedRect(matrixStack, finalWinX, finalWinY, finalWinW, finalWinH,
                                ColorUtil.multAlpha(backgroundColor, 0.9f * alpha), Round.of(6 * resolutionScale));
                    });
                    BlurRenderer2.draw(6);
                    LiquidGlassRenderer.captureBackground();

                    LiquidGlassRenderer.drawLiquidGlass(matrixStack, finalWinX, finalWinY, finalWinW, finalWinH, 6 * resolutionScale / 6f,
                            Excellent.inst().moduleManager().get(Theme.class).liquidGlassStrength().getValue() * 1000f,
                            Excellent.inst().moduleManager().get(Theme.class).blurStrength().getValue() / 33f,
                            ColorUtil.getColor(255, 255, 255, .3f));

                    Fonts.SF_MEDIUM.draw(matrixStack, module.getModule().getName(),
                            finalWinX + finalWinW / 2f - Fonts.SF_MEDIUM.getWidth(module.getModule().getName(), getAdaptiveTitleSize()) / 2f,
                            finalWinY + 8 * resolutionScale, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), getAdaptiveTitleSize());

                    float settingsStartY = finalWinY + 25 * resolutionScale;
                    float settingsHeight = finalWinH - 25 * resolutionScale;

                    ScissorExp.push();
                    ScissorExp.setFromComponentCoordinates(finalWinX, settingsStartY, finalWinW, settingsHeight);

                    float settingsY = settingsStartY;
                    for (Setting setting : module.getSettings()) {
                        if (!((boolean) setting.getSetting().getVisible().get())) continue;

                        setting.setX(finalWinX + 5 * resolutionScale);
                        setting.setY(settingsY);
                        setting.setWidth(finalWinW - 10 * resolutionScale);
                        setting.setAlphaV(alpha);
                        setting.setDeltaV(module.getSettingsDeltaV());
                        setting.render(matrixStack, mouseX, mouseY);
                        settingsY += setting.getHeight() + 3 * resolutionScale;
                    }

                    ScissorExp.unset();
                    ScissorExp.pop();
                }
            }
        }
    }

    private void renderModulemMode(MatrixStack matrixStack, float mouseX, float mouseY, float scaledWidth, float scaledHeight) {
        for (Panel panel : panels) {
            for (Module module : panel.getModules()) {
                if (module.getSelectedV() >= .1f) {
                    float moduleX = module.getX();
                    float moduleY = module.getY() + module.getDelta();
                    float moduleWidth = module.getWidth();

                    float alpha = module.getSelectedV();

                    float settingsStartY = moduleY + 16 * resolutionScale;
                    float totalSettingsHeight = 0;

                    for (Setting setting : module.getSettings()) {
                        if ((boolean) setting.getSetting().getVisible().get()) {
                            totalSettingsHeight += setting.getHeight() + 3 * resolutionScale;
                        }
                    }

                    if (totalSettingsHeight > 0) {
                        LiquidGlassRenderer.captureBackground();

                        LiquidGlassRenderer.drawLiquidGlass(matrixStack, moduleX, settingsStartY, moduleWidth, totalSettingsHeight, 4 * resolutionScale / 6f,
                                Excellent.inst().moduleManager().get(Theme.class).liquidGlassStrength().getValue() * 1000f,
                                Excellent.inst().moduleManager().get(Theme.class).blurStrength().getValue() / 33f,
                                ColorUtil.getColor(255, 255, 255, .3f));

                        ScissorExp.push();
                        ScissorExp.setFromComponentCoordinates(moduleX, settingsStartY, moduleWidth, totalSettingsHeight);

                        float settingY = settingsStartY + 3 * resolutionScale;
                        for (Setting setting : module.getSettings()) {
                            if (!((boolean) setting.getSetting().getVisible().get())) continue;

                            setting.setX(moduleX + 5 * resolutionScale);
                            setting.setY(settingY);
                            setting.setWidth(moduleWidth - 10 * resolutionScale);
                            setting.setAlphaV(alpha);
                            setting.setDeltaV(0);
                            setting.render(matrixStack, mouseX, mouseY);
                            settingY += setting.getHeight() + 3 * resolutionScale;
                        }

                        ScissorExp.unset();
                        ScissorExp.pop();
                    }
                }
            }
        }
    }

    private void renderSverkhuMode(MatrixStack matrixStack, float mouseX, float mouseY, float scaledWidth, float scaledHeight) {
        if (!anySelected()) return;

        Module selectedModule = null;
        for (Panel panel : panels) {
            for (Module module : panel.getModules()) {
                if (module.getSelectedV() >= .1f) {
                    selectedModule = module;
                    break;
                }
            }
            if (selectedModule != null) break;
        }

        if (selectedModule == null) return;

        int visibleSettingsCount = 0;
        float totalSettingsHeight = 0;
        for (Setting s : selectedModule.getSettings()) {
            if ((boolean) s.getSetting().getVisible().get()) {
                visibleSettingsCount++;
                totalSettingsHeight += s.getHeight() + 3 * resolutionScale;
            }
        }

        if (visibleSettingsCount == 0) return;

        float totalPanelsWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
        float winW = Math.min(totalPanelsWidth, scaledWidth * 0.8f);
        float winH = Math.min(totalSettingsHeight + 40 * resolutionScale, panelHeight * 0.5f);
        float winX = scaledWidth / 2f - winW / 2f;
        float winY = scaledHeight / 2f - panelHeight / 2f - winH - 15 * resolutionScale;

        float alpha = selectedModule.getSelectedV();

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(matrixStack, winX, winY, winW, winH,
                    ColorUtil.multAlpha(backgroundColor, 0.9f * alpha), Round.of(6 * resolutionScale));
        });
        BlurRenderer2.draw(6);
        LiquidGlassRenderer.captureBackground();

        LiquidGlassRenderer.drawLiquidGlass(matrixStack, winX, winY, winW, winH, 6 * resolutionScale / 6f,
                Excellent.inst().moduleManager().get(Theme.class).liquidGlassStrength().getValue() * 1000f,
                Excellent.inst().moduleManager().get(Theme.class).blurStrength().getValue() / 33f,
                ColorUtil.getColor(255, 255, 255, .3f));

        Fonts.SF_REGULAR.draw(matrixStack, selectedModule.getModule().getName(),
                winX + 10 * resolutionScale, winY + 8 * resolutionScale,
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), getAdaptiveTitleSize());

        float settingsStartY = winY + 28 * resolutionScale;
        float settingsHeight = winH - 32 * resolutionScale;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(winX, settingsStartY, winW, settingsHeight);

        float columnWidth = winW / 2f - 15 * resolutionScale;
        float leftX = winX + 10 * resolutionScale;
        float rightX = winX + winW / 2f + 5 * resolutionScale;
        float leftY = settingsStartY + selectedModule.getSettingsDeltaV();
        float rightY = settingsStartY + selectedModule.getSettingsDeltaV();

        boolean useLeftColumn = true;
        for (Setting setting : selectedModule.getSettings()) {
            if (!((boolean) setting.getSetting().getVisible().get())) continue;

            if (useLeftColumn) {
                setting.setX(leftX);
                setting.setY(leftY);
                setting.setWidth(columnWidth);
                setting.setAlphaV(alpha);
                setting.setDeltaV(0);
                setting.render(matrixStack, mouseX, mouseY);
                leftY += setting.getHeight() + 3 * resolutionScale;
            } else {
                setting.setX(rightX);
                setting.setY(rightY);
                setting.setWidth(columnWidth);
                setting.setAlphaV(alpha);
                setting.setDeltaV(0);
                setting.render(matrixStack, mouseX, mouseY);
                rightY += setting.getHeight() + 3 * resolutionScale;
            }

            useLeftColumn = !useLeftColumn;
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentViewMode == ViewMode.SEARCH) {
            searchWindow.mouseClicked((int) mouseX, (int) mouseY, button);

            org.privates.client.managers.module.Module highlighted = searchWindow.getHighlightedModule();
            if (highlighted == null) {
                for (Panel panel : panels) {
                    for (org.privates.client.screen.newgui.Module guiModule : panel.getModules()) {
                        guiModule.setHighlighted(false);
                    }
                }
                currentViewMode = ViewMode.UI;
                dynamicIsland.setSelectedTab(DynamicIsland.IslandTab.UI);
            }
            return true;
        }

        dynamicIsland.mouseClicked((int) mouseX, (int) mouseY, button);

        if (button == 0 && currentViewMode == ViewMode.UI) {
            if (handleResizeClick(mouseX, mouseY)) {
                return true;
            }
        }

        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();

        if (!anySelected() || displayMode.equals("Модулем")) {
            if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
                themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
            } else if (currentViewMode == ViewMode.UI) {
                for (Panel panel : panels) {
                    panel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
                }
            }
        } else if (displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") || displayMode.equals("Сверху GUI")) {
            for (Panel panel : panels) {
                for (org.privates.client.screen.newgui.Module module : panel.getModules()) {
                    if (module.isSelected()) {
                        // Transform mouse coordinates for scaled window in "Отдельно" mode
                        float transformedMouseX = (float) mouseX;
                        float transformedMouseY = (float) mouseY;
                        
                        if (displayMode.equals("Отдельно") && module.getSelectedV() > 0.01f) {
                            float centerX = mc.getMainWindow().getScaledWidth() / 2f;
                            float centerY = mc.getMainWindow().getScaledHeight() / 2f;
                            float scale = module.getSelectedV();
                            
                            // Inverse transform: reverse the scale transformation
                            transformedMouseX = (transformedMouseX - centerX) / scale + centerX;
                            transformedMouseY = (transformedMouseY - centerY) / scale + centerY;
                            
                            // Calculate visible area bounds for "Отдельно" mode
                            float halfW = 140 * resolutionScale;
                            float halfH = 200 * resolutionScale;
                            float winW = 280 * resolutionScale;
                            float winH = 400 * resolutionScale;
                            float scY = 30 * resolutionScale;
                            float scaledWidth = mc.getMainWindow().getScaledWidth();
                            float scaledHeight = mc.getMainWindow().getScaledHeight();
                            float x = scaledWidth / 2f - halfW;
                            float y = scaledHeight / 2f - halfH;
                            
                            // Only process clicks for settings that are in visible area
                            for (org.privates.client.screen.newgui.Setting setting : module.getSettings()) {
                                if ((boolean) setting.getSetting().getVisible().get()) {
                                    // Check if setting is in visible Y range (same as render check)
                                    float settingY = setting.getY() + setting.getDeltaV();
                                    if (Mathf.inRange(settingY, setting.getHeight(), y + scY, winH - scY)) {
                                        setting.mouse(transformedMouseX, transformedMouseY, button, GLFW.GLFW_PRESS);
                                    }
                                }
                            }
                        } else {
                            // For other modes, process all visible settings
                            for (org.privates.client.screen.newgui.Setting setting : module.getSettings()) {
                                if ((boolean) setting.getSetting().getVisible().get())
                                    setting.mouse(transformedMouseX, transformedMouseY, button, GLFW.GLFW_PRESS);
                            }
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setCurrentViewMode(ViewMode mode) {
        this.currentViewMode = mode;
    }

    private boolean handleResizeClick(double mouseX, double mouseY) {
        float centerX = mc.getMainWindow().getScaledWidth() / 2f;
        float centerY = mc.getMainWindow().getScaledHeight() / 2f;

        float totalWidth = Category.values().length * panelWidth + (Category.values().length - 1) * panelSpacing;
        float totalHeight = panelHeight;

        float left = centerX - totalWidth / 2f - 20 * resolutionScale;
        float right = centerX + totalWidth / 2f + 20 * resolutionScale;
        float top = centerY - totalHeight / 2f - 20 * resolutionScale;
        float bottom = centerY + totalHeight / 2f + 20 * resolutionScale;

        float hSize = handleSize * resolutionScale;
        float hLength = handleLength * resolutionScale;

        startMouseX = (float) mouseX;
        startMouseY = (float) mouseY;

        if (isHoveringHandle((float) mouseX, (float) mouseY, left, centerY - hLength / 2f, hSize, hLength)) {
            resizing = true;
            resizeType = ResizeType.PANEL_WIDTH;
            startValue = panelWidth;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, right - hSize, centerY - hLength / 2f, hSize, hLength)) {
            resizing = true;
            resizeType = ResizeType.PANEL_WIDTH;
            startValue = panelWidth;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, centerX - hLength / 2f, top, hLength, hSize)) {
            resizing = true;
            resizeType = ResizeType.PANEL_HEIGHT;
            startValue = panelHeight;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, centerX - hLength / 2f, bottom - hSize, hLength, hSize)) {
            resizing = true;
            resizeType = ResizeType.PANEL_HEIGHT;
            startValue = panelHeight;
            return true;
        }

        if (isHoveringHandle((float) mouseX, (float) mouseY, centerX - hSize / 2f, centerY - hSize / 2f, hSize, hSize)) {
            resizing = true;
            resizeType = ResizeType.PANEL_SPACING;
            startValue = panelSpacing;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (currentViewMode == ViewMode.SEARCH) {
            return true;
        }

        if (resizing && button == 0) {
            float mouseDeltaX = (float) mouseX - startMouseX;
            float mouseDeltaY = (float) mouseY - startMouseY;

            switch (resizeType) {
                case PANEL_WIDTH:
                    panelWidth = Math.max(80f * resolutionScale, Math.min(200f * resolutionScale, startValue + mouseDeltaX * 0.5f));
                    updatePanelPositions();
                    Excellent.inst().configManager().savePanelDimensions(panelWidth, panelHeight, panelSpacing);
                    break;
                case PANEL_HEIGHT:
                    panelHeight = Math.max(200f * resolutionScale, Math.min(500f * resolutionScale, startValue + mouseDeltaY * 0.5f));
                    updatePanelPositions();
                    Excellent.inst().configManager().savePanelDimensions(panelWidth, panelHeight, panelSpacing);
                    break;
                case PANEL_SPACING:
                    panelSpacing = Math.max(0f, Math.min(20f * resolutionScale, startValue + mouseDeltaX * 0.1f));
                    updatePanelPositions();
                    Excellent.inst().configManager().savePanelDimensions(panelWidth, panelHeight, panelSpacing);
                    break;
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (currentViewMode == ViewMode.SEARCH) {
            return true;
        }

        if (resizing && button == 0) {
            resizing = false;
            resizeType = ResizeType.NONE;
            return true;
        }

        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();

        if (!anySelected() || displayMode.equals("Модулем")) {
            if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
                themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
            } else if (currentViewMode == ViewMode.UI) {
                for (Panel panel : panels) {
                    panel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
                }
            }
        } else if (displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") || displayMode.equals("Сверху GUI")) {
            for (Panel panel : panels) {
                for (Module module : panel.getModules()) {
                    if (module.isSelected()) {
                        // Transform mouse coordinates for scaled window in "Отдельно" mode
                        float transformedMouseX = (float) mouseX;
                        float transformedMouseY = (float) mouseY;
                        
                        if (displayMode.equals("Отдельно") && module.getSelectedV() > 0.01f) {
                            float centerX = mc.getMainWindow().getScaledWidth() / 2f;
                            float centerY = mc.getMainWindow().getScaledHeight() / 2f;
                            float scale = module.getSelectedV();
                            
                            // Inverse transform: reverse the scale transformation
                            transformedMouseX = (transformedMouseX - centerX) / scale + centerX;
                            transformedMouseY = (transformedMouseY - centerY) / scale + centerY;
                            
                            // Calculate visible area bounds for "Отдельно" mode
                            float halfW = 140 * resolutionScale;
                            float halfH = 200 * resolutionScale;
                            float winW = 280 * resolutionScale;
                            float winH = 400 * resolutionScale;
                            float scY = 30 * resolutionScale;
                            float scaledWidth = mc.getMainWindow().getScaledWidth();
                            float scaledHeight = mc.getMainWindow().getScaledHeight();
                            float x = scaledWidth / 2f - halfW;
                            float y = scaledHeight / 2f - halfH;
                            
                            // Only process clicks for settings that are in visible area
                            for (Setting setting : module.getSettings()) {
                                if ((boolean) setting.getSetting().getVisible().get()) {
                                    // Check if setting is in visible Y range (same as render check)
                                    float settingY = setting.getY() + setting.getDeltaV();
                                    if (Mathf.inRange(settingY, setting.getHeight(), y + scY, winH - scY)) {
                                        setting.mouse(transformedMouseX, transformedMouseY, button, GLFW.GLFW_RELEASE);
                                    }
                                }
                            }
                        } else {
                            // For other modes, process all visible settings
                            for (Setting setting : module.getSettings()) {
                                if ((boolean) setting.getSetting().getVisible().get())
                                    setting.mouse(transformedMouseX, transformedMouseY, button, GLFW.GLFW_RELEASE);
                            }
                        }
                    }
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            ctrlPressed = true;
            return false;
        }

        if (ctrlPressed && keyCode == GLFW.GLFW_KEY_F) {
            dynamicIsland.setSelectedTab(DynamicIsland.IslandTab.SEARCH);
            onTabChanged(DynamicIsland.IslandTab.SEARCH);
            return true;
        }

        if (currentViewMode == ViewMode.SEARCH) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchWindow.setOpen(false);
                for (Panel panel : panels) {
                    for (org.privates.client.screen.newgui.Module guiModule : panel.getModules()) {
                        guiModule.setHighlighted(false);
                    }
                }
                currentViewMode = ViewMode.UI;
                dynamicIsland.setSelectedTab(DynamicIsland.IslandTab.UI);
                return true;
            }
            searchWindow.keyTyped((char) 0, keyCode);
            return true;
        }

        if (input) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                input = false;
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    find = "";
                    for (Panel panel : panels)
                        panel.loadModules(find);
                }
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                find = find.isEmpty() ? "" : find.substring(0, find.length() - 1);
                for (Panel panel : panels)
                    panel.loadModules(find);
            }
            return false;
        }

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.key(keyCode);
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                for (org.privates.client.screen.newgui.Module module : panel.getModules())
                    if (module.getSelectedV() >= .1f && keyCode == GLFW.GLFW_KEY_ESCAPE && FlatGui.getInstance().settingsDisplay().is("Отдельно")) {
                        module.setSelected(false);
                        return false;
                    }
                panel.key(keyCode);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            ctrlPressed = false;
            return false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (currentViewMode == ViewMode.SEARCH) {
            searchWindow.keyTyped(codePoint, 0);
            return true;
        }

        if (input) {
            find += codePoint;
            for (Panel panel : panels)
                panel.loadModules(find);
            return false;
        }

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.charTyped(codePoint);
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels)
                panel.charTyped(codePoint);
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (currentViewMode == ViewMode.SEARCH) {
            searchWindow.scroll(delta);
            return true;
        }

        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();
        boolean isOldMode = FlatGui.getInstance().panelRenderMode().is("Старый");

        if (displayMode.equals("Отдельно")) {
            float winW = 280 * resolutionScale;
            float winH = 400 * resolutionScale;

            if (currentViewMode == ViewMode.UI) {
                if (isOldMode) {
                    boolean shiftHeld = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                            || org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
                    float scrollAmount = (float) delta * 20f;
                    if (shiftHeld) {
                        for (Panel panel : panels) {
                            panel.setX(panel.getX() + scrollAmount);
                            panel.setTargetX(panel.getX());
                        }
                    } else {
                        for (Panel panel : panels) {
                            panel.setY(panel.getY() + scrollAmount);
                            panel.setTargetY(panel.getY());
                        }
                    }
                } else {
                    for (Panel panel : panels) {
                        if (!anySelected()) {
                            panel.scroll((float) mouseX, (float) mouseY, (float) delta);
                        } else {
                            for (Module module : panel.getModules()) {
                                if (module.isSelected()) {
                                    for (Setting setting : module.getSettings()) {
                                        if (Mathf.isHover(mouseX, mouseY, mc.getMainWindow().getScaledWidth() / 2f - 140 * resolutionScale, mc.getMainWindow().getScaledHeight() / 2f - 200 * resolutionScale, winW, winH) &&
                                                !(setting.getDelta() >= 0 && delta > 0) && !(getHeight(module) + setting.getDelta() <= 365 * resolutionScale && delta < 0))
                                            setting.setDelta((float) (setting.getDelta() + delta * 9));
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
                themePanel.scroll((float) mouseX, (float) mouseY, (float) delta);
            }
        } else {
            if (currentViewMode == ViewMode.UI) {
                if (isOldMode) {
                    boolean shiftHeld = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
                            || org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
                    float scrollAmount = (float) delta * 20f;
                    if (shiftHeld) {
                        for (Panel panel : panels) {
                            panel.setX(panel.getX() + scrollAmount);
                            panel.setTargetX(panel.getX());
                        }
                    } else {
                        for (Panel panel : panels) {
                            panel.setY(panel.getY() + scrollAmount);
                            panel.setTargetY(panel.getY());
                        }
                    }
                } else {
                    for (Panel panel : panels) {
                        panel.scroll((float) mouseX, (float) mouseY, (float) delta);
                    }
                }
            } else if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
                themePanel.scroll((float) mouseX, (float) mouseY, (float) delta);
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private float getHeight(Module module) {
        float height = 0;
        float sScale = module.getPanel().getGui().getResolutionScale();
        Setting prevSetting = module.getSettings().get(0);
        for (Setting setting : module.getSettings()) {
            height += prevSetting.getHeight() * setting.getOffsetY() + 3 * sScale;
            if (setting.getOffsetY() >= .1f)
                prevSetting = setting;
        }
        return height;
    }

    @Override
    public void closeScreen() {
        SoundUtil.playSound("guiclose.wav", 0.3f);
        closing = true;
        scale = 0;
        if (themePanel != null) {
            themePanel.startClosingAnimation();
        }
        for (Panel panel : panels) {
            panel.startClosingAnimation();
        }
        isInitialOpen = true;
        Excellent.inst().configManager().set();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean anySelected() {
        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();
        if (!displayMode.equals("Отдельно") && !displayMode.equals("Рядом с модулем") &&
                !displayMode.equals("Модулем") && !displayMode.equals("Сверху GUI")) {
            return false;
        }
        for (Panel panel : panels) {
            for (Module module : panel.getModules()) {
                if (module.getSelectedV() >= .1f) {
                    return true;
                }
            }
        }
        return false;
    }

    public float getPanelWidth() {
        return panelWidth;
    }

    public float getPanelHeight() {
        return panelHeight;
    }

    public float getFontSize() {
        return getAdaptiveFontSize();
    }

    public float getIconSize() {
        return getAdaptiveIconSize();
    }

    private enum ResizeType {
        NONE, PANEL_WIDTH, PANEL_HEIGHT, PANEL_SPACING
    }
}