package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.modules.ModuleCategory;
import godweer.utils.color.ColorUtils;
import godweer.utils.math.Mathf;
import godweer.utils.render.draw.RenderDraw;
import godweer.utils.render.draw.Round;
import godweer.utils.render.draw.ScissorExp;
import godweer.utils.render.font.GuiFonts;
import godweer.utils.render.font.GuiTheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ClickGuiScreen extends Screen {

    private static final Minecraft mc = Minecraft.getInstance();

    private final ArrayList<Panel> panels = new ArrayList<>();
    private Panel themePanel;
    private final SearchWindow searchWindow;
    private final DynamicIsland dynamicIsland;

    private float scale, scaleV;
    private float panelWidth;
    private float panelHeight;
    private float panelSpacing;
    private float baseFontSize;
    private float baseIconSize;

    private float resolutionScale = 1f;

    private boolean closing = false;
    private float guiFadeAlpha = 0f;

    private boolean isInitialOpen = true;

    private boolean ctrlPressed = false;
    private ViewMode currentViewMode = ViewMode.UI;

    private float descriptionWidthAnim = 0f;
    private int descriptionTextLength = 0;
    private long descriptionTextStartTime = 0L;
    private String lastDescription = "";

<<<<<<< HEAD
<<<<<<< HEAD
    private final int accentColor = ColorUtils.getColor(120, 180, 255);
=======
    private final int accentColor = ColorUtils.getColor(120, 180, 255, 255);
>>>>>>> be39d49e669e2142432a36b1bc0484f06041f13b
=======
    private final int accentColor = ColorUtils.getColor(120, 180, 255, 255);
>>>>>>> 42510f564926933ae9da905274ef1c63749450a2
    private final int backgroundColor = ColorUtils.getColor(10, 10, 15, 140);

    public enum ViewMode {
        UI, CLIENT, HUD, SEARCH
    }

    public ClickGuiScreen() {
        super(Component.empty());
        this.searchWindow = new SearchWindow(this);
        this.dynamicIsland = new DynamicIsland(this);
    }

    public float getResolutionScale() {
        return 1f;
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
        resolutionScale = getResolutionScale();

        panelWidth = 122f * resolutionScale;
        panelHeight = 300f * resolutionScale;
        panelSpacing = 5f * resolutionScale;

        baseFontSize = 12f * resolutionScale;
        baseIconSize = 40f * resolutionScale;

        if (panels.isEmpty() || themePanel == null) {
            updatePanelPositions();
        } else {
            float totalWidth = ModuleCategory.values().length * panelWidth + (ModuleCategory.values().length - 1) * panelSpacing;
            float offsetX = mc.getWindow().getGuiScaledWidth() / 2f - totalWidth / 2f;

            float centerX = mc.getWindow().getGuiScaledWidth() / 2f;
            float centerY = mc.getWindow().getGuiScaledHeight() / 2f;
            themePanel.setX(centerX - panelWidth / 2f);
            themePanel.setY(centerY - panelHeight / 2f);
            themePanel.setCustomSize(panelWidth, panelHeight);

            for (Panel panel : panels) {
                panel.setX(offsetX);
                panel.setY(mc.getWindow().getGuiScaledHeight() / 2f - panelHeight / 2f);
                panel.setCustomSize(panelWidth, panelHeight);
                offsetX += panelWidth + panelSpacing;
            }
        }

        if (isInitialOpen) {
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

        float centerX = mc.getWindow().getGuiScaledWidth() / 2f;
        float centerY = mc.getWindow().getGuiScaledHeight() / 2f;
        themePanel = new Panel(this, null, centerX - panelWidth / 2f, centerY - panelHeight / 2f, -1, true);
        themePanel.setCustomSize(panelWidth, panelHeight);

        float totalWidth = ModuleCategory.values().length * panelWidth + (ModuleCategory.values().length - 1) * panelSpacing;
        float offsetX = mc.getWindow().getGuiScaledWidth() / 2f - totalWidth / 2f;

        int idx = 0;
        for (ModuleCategory category : ModuleCategory.values()) {
            Panel panel = new Panel(this, category, offsetX, mc.getWindow().getGuiScaledHeight() / 2f - panelHeight / 2f, idx, false);
            panel.setCustomSize(panelWidth, panelHeight);
            panels.add(panel);
            offsetX += panelWidth + panelSpacing;
            idx++;
        }
    }

    public void onTabChanged(DynamicIsland.IslandTab tab) {
        switch (tab) {
            case UI:
                currentViewMode = ViewMode.UI;
                searchWindow.setOpen(false);

                float totalWidth = ModuleCategory.values().length * panelWidth + (ModuleCategory.values().length - 1) * panelSpacing;
                float offsetX = mc.getWindow().getGuiScaledWidth() / 2f - totalWidth / 2f;
                for (Panel panel : panels) {
                    panel.setX(offsetX);
                    panel.setY(mc.getWindow().getGuiScaledHeight() / 2f - panelHeight / 2f);
                    offsetX += panelWidth + panelSpacing;
                }

                if (themePanel != null) {
                    float centerX = mc.getWindow().getGuiScaledWidth() / 2f;
                    float centerY = mc.getWindow().getGuiScaledHeight() / 2f;
                    themePanel.setX(centerX - panelWidth / 2f);
                    themePanel.setY(centerY - panelHeight / 2f);
                }
                break;
            case CLIENT:
                currentViewMode = ViewMode.CLIENT;
                searchWindow.setOpen(false);
                for (Panel panel : panels) {
                    panel.setX(-1000);
                }
                if (themePanel != null) {
                    float centerX = mc.getWindow().getGuiScaledWidth() / 2f;
                    float centerY = mc.getWindow().getGuiScaledHeight() / 2f;
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = guiGraphics.pose();
        long currentTime = System.currentTimeMillis();
        scaleV = Mathf.fast(scaleV, scale, 16);

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

        float scaledWidth = mc.getWindow().getGuiScaledWidth();
        float scaledHeight = mc.getWindow().getGuiScaledHeight();

        dynamicIsland.render(matrixStack, mouseX, mouseY, partialTicks, guiFadeAlpha);

        float hintX = 15 * resolutionScale;
        float hintY = 15 * resolutionScale;
        float hintSize = 7f * resolutionScale;
        int hintColor = ColorUtils.getColor(255, 255, 255, (int) (0.6f * guiFadeAlpha * 255));

        GuiFonts.SF_REGULAR.draw(matrixStack, "CTRL - show binds", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        GuiFonts.SF_REGULAR.draw(matrixStack, "CTRL + F - search", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        GuiFonts.SF_REGULAR.draw(matrixStack, "RMB - open settings", hintX, hintY, hintColor, hintSize);
        hintY += 12 * resolutionScale;
        GuiFonts.SF_REGULAR.draw(matrixStack, "LMB - enable module", hintX, hintY, hintColor, hintSize);

        if (currentViewMode == ViewMode.SEARCH) {
            godweer.modules.Module highlightedModule = searchWindow.getHighlightedModule();
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
            for (Panel panel : panels) {
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

        if (currentViewMode == ViewMode.SEARCH) {
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 1000);
            searchWindow.render(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderUIWithDimming(PoseStack stack, int mouseX, int mouseY, long currentTime, godweer.modules.Module highlightedModule) {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.updateAnimation(currentTime, scaleV);
            if (panel.getAnimProgress() > 0.01f || !closing) {
                stack.pushPose();
                stack.translate(0, 0, i * 0.01f);

                for (Module guiModule : panel.getModules()) {
                    guiModule.setHighlighted(guiModule.getModule() == highlightedModule);
                }

                panel.render(stack, mouseX, mouseY);

                boolean hasHighlightedModule = false;
                for (Module guiModule : panel.getModules()) {
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

                    int dimColor = ColorUtils.getColor(0, 0, 0, 120);
                    RenderDraw.Rounded.roundedRect(stack, dimX, dimY, dimW, dimH, dimColor, Round.of(6 * panel.getGui().getResolutionScale()));
                }

                stack.popPose();
            }
        }
    }

    private void renderUIPanels(PoseStack stack, int mouseX, int mouseY, long currentTime) {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.updateAnimation(currentTime, scaleV);
            if (panel.getAnimProgress() > 0.01f || !closing) {
                stack.pushPose();
                stack.translate(0, 0, i * 0.01f);
                panel.render(stack, mouseX, mouseY);
                stack.popPose();
            }
        }
    }

    private void renderClientPanel(PoseStack stack, int mouseX, int mouseY, long currentTime) {
        if (themePanel != null) {
            themePanel.updateAnimation(currentTime, scaleV);
            if (themePanel.getAnimProgress() > 0.01f || !closing) {
                stack.pushPose();
                stack.translate(0, 0, 100f);
                themePanel.render(stack, mouseX, mouseY);
                stack.popPose();
            }
        }
    }

    private void renderHUDSettings(PoseStack stack, int mouseX, int mouseY) {
        float sScale = getResolutionScale();
        float centerX = mc.getWindow().getGuiScaledWidth() / 2f;
        float centerY = mc.getWindow().getGuiScaledHeight() / 2f;

        String text = "HUD Settings";
        float textSize = 14f * sScale;
        float textWidth = GuiFonts.SF_BOLD.getWidth(text, textSize);
        int textColor = ColorUtils.multAlpha(GuiTheme.getInstance().textColor(), 0.8f);

        GuiFonts.SF_BOLD.draw(stack, text, centerX - textWidth / 2f, centerY - textSize / 2f, textColor, textSize);

        String subText = "Coming soon...";
        float subSize = 9f * sScale;
        float subWidth = GuiFonts.SF_REGULAR.getWidth(subText, subSize);
        int subColor = ColorUtils.multAlpha(GuiTheme.getInstance().textColor(), 0.5f);

        GuiFonts.SF_REGULAR.draw(stack, subText, centerX - subWidth / 2f, centerY + 10 * sScale, subColor, subSize);
    }

    private void renderDescription(PoseStack matrixStack, String description, float alpha, float scaledWidth, float scaledHeight) {
        GuiTheme theme = GuiTheme.getInstance();
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

        float textWidth = GuiFonts.SF_REGULAR.getWidth(displayText, descFontSize);
        float descHeight = 24 * sScale;

        descriptionWidthAnim = Mathf.fast(descriptionWidthAnim, textWidth + padding * 2, 8);

        float centerX = scaledWidth / 2f;
        float centerY = scaledHeight / 2f;
        float descX = centerX - descriptionWidthAnim / 2f;
        float descY = centerY - panelHeight / 2f - 50 * sScale;

        RenderDraw.CustomStyled.drawGui(matrixStack, descX, descY, descriptionWidthAnim, descHeight, 12 * sScale, true, (int) (theme.blurStrength() * alpha));

        float textX = descX + descriptionWidthAnim / 2f - textWidth / 2f;
        float textY = descY + descHeight / 2f - GuiFonts.SF_REGULAR.getHeight(displayText, descFontSize) / 2f;

        GuiFonts.SF_REGULAR.draw(matrixStack, displayText, textX, textY, ColorUtils.multAlpha(theme.textColor(), alpha), descFontSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentViewMode == ViewMode.SEARCH) {
            searchWindow.mouseClicked((int) mouseX, (int) mouseY, button);

            godweer.modules.Module highlighted = searchWindow.getHighlightedModule();
            if (highlighted == null) {
                for (Panel panel : panels) {
                    for (Module guiModule : panel.getModules()) {
                        guiModule.setHighlighted(false);
                    }
                }
                currentViewMode = ViewMode.UI;
                dynamicIsland.setSelectedTab(DynamicIsland.IslandTab.UI);
            }
            return true;
        }

        dynamicIsland.mouseClicked((int) mouseX, (int) mouseY, button);

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        } else if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setCurrentViewMode(ViewMode mode) {
        this.currentViewMode = mode;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (currentViewMode == ViewMode.SEARCH) {
            return true;
        }

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        } else if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
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
                    for (Module guiModule : panel.getModules()) {
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

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.key(keyCode);
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                for (Module module : panel.getModules())
                    if (module.getSelectedV() >= .1f && keyCode == GLFW.GLFW_KEY_ESCAPE) {
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
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double delta = verticalAmount;
        if (currentViewMode == ViewMode.SEARCH) {
            searchWindow.scroll(delta);
            return true;
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.scroll((float) mouseX, (float) mouseY, (float) delta);
            }
        } else if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.scroll((float) mouseX, (float) mouseY, (float) delta);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onClose() {
        closing = true;
        scale = 0;
        if (themePanel != null) {
            themePanel.startClosingAnimation();
        }
        for (Panel panel : panels) {
            panel.startClosingAnimation();
        }
        isInitialOpen = true;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public ArrayList<Panel> getPanels() {
        return panels;
    }

    public DynamicIsland getDynamicIsland() {
        return dynamicIsland;
    }

    public SearchWindow getSearchWindow() {
        return searchWindow;
    }

    public float getScale() {
        return scale;
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
}
