package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ModuleManager;
import swerve.modules.ModuleCategory;
import swerve.modules.impl.render.ViewModel;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

// ClickGuiScreen из исходного клиента (newgui), портирован на 1.21.4 + Mojang mappings.
// Режимы зафиксированы на дефолтах исходника: панели «Новые», настройки «Под модулем».
public class ClickGuiScreen extends Screen {

    private final ArrayList<Panel> panels = new ArrayList<>();
    private Panel themePanel;
    private Panel interfacePanel;
    private final DynamicIsland dynamicIsland;

    private float scale, scaleV;

    private float panelWidth;
    private float panelHeight;
    private float panelSpacing;

    private float resolutionScale = 1.0f;

    private int lastFramebufferWidth = -1;
    private int lastFramebufferHeight = -1;

    private boolean closing = false;
    private float guiFadeAlpha = 0.0f;

    private boolean isInitialOpen = true;

    private boolean ctrlPressed = false;
    private ViewMode currentViewMode = ViewMode.UI;

    public enum ViewMode {
        UI, CLIENT, HANDS, INTERFACE
    }

    // Категории, показывающиеся рядом в режиме UI (INTERFACE — отдельная вкладка).
    private static ModuleCategory[] uiCategories() {
        ModuleCategory[] all = ModuleCategory.values();
        ModuleCategory[] out = new ModuleCategory[all.length - 1];
        int index = 0;
        for (ModuleCategory category : all) {
            if (category != ModuleCategory.INTERFACE) {
                out[index++] = category;
            }
        }
        return out;
    }

    public ClickGuiScreen() {
        super(Component.literal(""));
        this.dynamicIsland = new DynamicIsland(this);
    }

    public void setCurrentViewMode(ViewMode mode) {
        this.currentViewMode = mode;
    }

    public DynamicIsland getDynamicIsland() {
        return dynamicIsland;
    }

    // Исходный метод во всех ветках возвращал 1, поэтому адаптивный масштаб вырожден — так и оставлено.
    public float getResolutionScale() {
        return resolutionScale;
    }

    public float getAdaptiveTextSize() {
        float widthScale = panelWidth / (122.0f * resolutionScale);
        return 7.0f * Math.max(0.8f, Math.min(1.3f, widthScale)) * resolutionScale;
    }

    public float getAdaptiveTitleSize() {
        float widthScale = panelWidth / (122.0f * resolutionScale);
        return 9.0f * Math.max(0.8f, Math.min(1.4f, widthScale)) * resolutionScale * GuiStyle.headerSize();
    }

    public float getAdaptiveModuleTextSize() {
        if (GuiStyle.fixedModuleTextSize()) {
            return 8.0f * resolutionScale * GuiStyle.moduleTextSize();
        }
        float widthScale = panelWidth / (122.0f * resolutionScale);
        return 8.0f * Math.max(0.7f, Math.min(1.2f, widthScale)) * resolutionScale * GuiStyle.moduleTextSize();
    }

    @Override
    protected void init() {
        Minecraft client = Minecraft.getInstance();
        int currentWidth = client.getWindow().getWidth();
        int currentHeight = client.getWindow().getHeight();

        if (lastFramebufferWidth != currentWidth || lastFramebufferHeight != currentHeight) {
            resolutionScale = getResolutionScale();
            lastFramebufferWidth = currentWidth;
            lastFramebufferHeight = currentHeight;
        }

        panelWidth = GuiStyle.panelWidth() * resolutionScale;
        panelHeight = GuiStyle.panelHeight() * resolutionScale;
        panelSpacing = GuiStyle.panelGap() * resolutionScale;

        if (panels.isEmpty() || themePanel == null) {
            updatePanelPositions();
        } else {
            ModuleCategory[] categories = uiCategories();
            float totalWidth = categories.length * panelWidth
                    + (categories.length - 1) * panelSpacing;
            float offsetX = client.getWindow().getGuiScaledWidth() / 2.0f - totalWidth / 2.0f;

            float centerX = client.getWindow().getGuiScaledWidth() / 2.0f;
            float centerY = client.getWindow().getGuiScaledHeight() / 2.0f;
            themePanel.setX(centerX - panelWidth / 2.0f);
            themePanel.setY(centerY - panelHeight / 2.0f);
            themePanel.setCustomSize(panelWidth, panelHeight);
            positionInterfacePanel(client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());

            for (Panel panel : panels) {
                panel.setX(offsetX);
                panel.setY(client.getWindow().getGuiScaledHeight() / 2.0f - panelHeight / 2.0f);
                panel.setCustomSize(panelWidth, panelHeight);
                offsetX += panelWidth + panelSpacing;
            }
        }

        if (isInitialOpen) {
            GuiStyle.playSound("guiopen.wav", 0.3f);
            scale = 1.0f;
            closing = false;
            if (themePanel != null) {
                themePanel.setupInitialAnimation();
            }
            if (interfacePanel != null) {
                interfacePanel.setupInitialAnimation();
            }
            for (Panel panel : panels) {
                panel.setupInitialAnimation();
            }
            isInitialOpen = false;
        }

        dynamicIsland.init();

        super.init();
    }

    private void updatePanelPositions() {
        Minecraft client = Minecraft.getInstance();
        panels.clear();

        float centerX = client.getWindow().getGuiScaledWidth() / 2.0f;
        float centerY = client.getWindow().getGuiScaledHeight() / 2.0f;
        themePanel = new Panel(this, null, centerX - panelWidth / 2.0f, centerY - panelHeight / 2.0f, -1, true);
        themePanel.setCustomSize(panelWidth, panelHeight);

        ModuleCategory[] categories = uiCategories();
        float totalWidth = categories.length * panelWidth
                + (categories.length - 1) * panelSpacing;
        float offsetX = client.getWindow().getGuiScaledWidth() / 2.0f - totalWidth / 2.0f;

        int idx = 0;
        for (ModuleCategory category : categories) {
            Panel panel = new Panel(this, category, offsetX,
                    client.getWindow().getGuiScaledHeight() / 2.0f - panelHeight / 2.0f, idx, false);
            panel.setCustomSize(panelWidth, panelHeight);
            panels.add(panel);
            offsetX += panelWidth + panelSpacing;
            idx++;
        }

        interfacePanel = new Panel(this, ModuleCategory.INTERFACE,
                client.getWindow().getGuiScaledWidth() / 2.0f - panelWidth / 2.0f,
                client.getWindow().getGuiScaledHeight() / 2.0f - panelHeight / 2.0f, 0, false);
        interfacePanel.setCustomSize(panelWidth, panelHeight);
        interfacePanel.setX(-1000.0f);
    }

    private void positionInterfacePanel(float scaledWidth, float scaledHeight) {
        if (interfacePanel != null) {
            interfacePanel.setX(scaledWidth / 2.0f - panelWidth / 2.0f);
            interfacePanel.setY(scaledHeight / 2.0f - panelHeight / 2.0f);
            interfacePanel.setCustomSize(panelWidth, panelHeight);
        }
    }

    public boolean isHandsTab() {
        return currentViewMode == ViewMode.HANDS;
    }

    public void onTabChanged(DynamicIsland.IslandTab tab) {
        Minecraft client = Minecraft.getInstance();

        if (tab != DynamicIsland.IslandTab.HANDS) {
            ViewModel vm = ModuleManager.get(ViewModel.class);
            if (vm != null) {
                vm.closeAnimMenu();
            }
        }

        switch (tab) {
            case UI -> {
                currentViewMode = ViewMode.UI;
                if (themePanel != null) {
                    float centerX = client.getWindow().getGuiScaledWidth() / 2.0f;
                    float centerY = client.getWindow().getGuiScaledHeight() / 2.0f;
                    themePanel.setX(centerX - panelWidth / 2.0f);
                    themePanel.setY(centerY - panelHeight / 2.0f);
                }
                if (interfacePanel != null) {
                    interfacePanel.setX(-1000.0f);
                }

                ModuleCategory[] categories = uiCategories();
                float totalWidth = categories.length * panelWidth
                        + (categories.length - 1) * panelSpacing;
                float offsetX = client.getWindow().getGuiScaledWidth() / 2.0f - totalWidth / 2.0f;
                for (Panel panel : panels) {
                    panel.setX(offsetX);
                    panel.setY(client.getWindow().getGuiScaledHeight() / 2.0f - panelHeight / 2.0f);
                    offsetX += panelWidth + panelSpacing;
                }
            }
            case CLIENT -> {
                currentViewMode = ViewMode.CLIENT;
                for (Panel panel : panels) {
                    panel.setX(-1000.0f);
                }
                if (themePanel != null) {
                    float centerX = client.getWindow().getGuiScaledWidth() / 2.0f;
                    float centerY = client.getWindow().getGuiScaledHeight() / 2.0f;
                    themePanel.setX(centerX - panelWidth / 2.0f);
                    themePanel.setY(centerY - panelHeight / 2.0f);
                }
                if (interfacePanel != null) {
                    interfacePanel.setX(-1000.0f);
                }
            }
            case HANDS -> {
                currentViewMode = ViewMode.HANDS;
                ViewModel vm = ModuleManager.get(ViewModel.class);
                if (vm != null && !vm.isEnable()) {
                    vm.setEnabled(true);
                }
                for (Panel panel : panels) {
                    panel.setX(-1000.0f);
                }
                if (themePanel != null) {
                    themePanel.setX(-1000.0f);
                }
                if (interfacePanel != null) {
                    interfacePanel.setX(-1000.0f);
                }
            }

            case INTERFACE -> {
                currentViewMode = ViewMode.INTERFACE;
                for (Panel panel : panels) {
                    panel.setX(-1000.0f);
                }
                if (themePanel != null) {
                    themePanel.setX(-1000.0f);
                }
                positionInterfacePanel(client.getWindow().getGuiScaledWidth(),
                        client.getWindow().getGuiScaledHeight());
            }
            default -> {
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        PoseStack stack = context.pose();
        long currentTime = System.currentTimeMillis();
        // Снапшот мира для блюра — до отрисовки GUI, иначе панели попадают в блюр друг друга.
        GuiBlur.beginFrame(GuiTheme.blurStrength());
        scaleV = Mathf.fast(scaleV, scale, GuiStyle.anim(16.0f));

        float maxPanelProgress = 0.0f;
        for (int i = 0; i < panels.size(); i++) {
            maxPanelProgress = Math.max(maxPanelProgress, panels.get(i).getPanelScale());
        }
        if (themePanel != null) {
            maxPanelProgress = Math.max(maxPanelProgress, themePanel.getPanelScale());
        }
        guiFadeAlpha = maxPanelProgress;

        if (scale == 0.0f && scaleV < 0.01f) {
            Minecraft.getInstance().setScreen(null);
            GuiBlur.endFrame();
            return;
        }

        if (GuiStyle.showIsland()) {
            dynamicIsland.render(stack, mouseX, mouseY, partialTicks, guiFadeAlpha);
        }

        if ((int) GuiStyle.backgroundDim() > 0 && guiFadeAlpha > 0.01f) {
            Minecraft dimClient = Minecraft.getInstance();
            GuiRender.roundedRect(stack, 0.0f, 0.0f,
                    dimClient.getWindow().getGuiScaledWidth(), dimClient.getWindow().getGuiScaledHeight(),
                    0.0f, ColorUtils.getColor(0, 0, 0, (int) (GuiStyle.backgroundDim() * guiFadeAlpha)));
        }

        if (GuiStyle.showHints()) {
            float hintX = 15.0f * resolutionScale;
            float hintY = 15.0f * resolutionScale;
            float hintSize = 7.0f * resolutionScale;
            int hintColor = ColorUtils.getColor(255, 255, 255, (int) (0.6f * guiFadeAlpha * 255));

            GuiFonts.SF_REGULAR.draw(stack, "CTRL - show binds", hintX, hintY, hintColor, hintSize);
            hintY += 12 * resolutionScale;
            GuiFonts.SF_REGULAR.draw(stack, "Middle Click (MOUSE3) - binding", hintX, hintY, hintColor, hintSize);
            hintY += 12 * resolutionScale;
            GuiFonts.SF_REGULAR.draw(stack, "RMB - open settings", hintX, hintY, hintColor, hintSize);
            hintY += 12 * resolutionScale;
            GuiFonts.SF_REGULAR.draw(stack, "LMB - enable module", hintX, hintY, hintColor, hintSize);
        }

        if (currentViewMode == ViewMode.UI) {
            renderUIPanels(stack, mouseX, mouseY, currentTime);
        } else if (currentViewMode == ViewMode.CLIENT) {
            renderClientPanel(stack, mouseX, mouseY, currentTime);
        } else if (currentViewMode == ViewMode.HANDS) {
            renderHandsHint(stack);
            ViewModel viewModel = ModuleManager.get(ViewModel.class);
            if (viewModel != null && viewModel.isAnimMenuOpen()) {
                viewModel.renderAnimMenu(stack, (float) mouseX, (float) mouseY);
            }
        } else if (currentViewMode == ViewMode.INTERFACE) {
            renderInterfacePanel(stack, mouseX, mouseY, currentTime);
        }

        Panel.DescriptionInfo bestDescription = null;
        float maxAlpha = 0.0f;

        if (currentViewMode == ViewMode.UI) {
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
        } else if (currentViewMode == ViewMode.INTERFACE) {
            if (interfacePanel != null && (interfacePanel.getAnimProgress() > 0.01f || !closing)) {
                Panel.DescriptionInfo info = interfacePanel.getDescriptionInfo((float) mouseX, (float) mouseY);
                if (info.alpha() > maxAlpha) {
                    maxAlpha = info.alpha();
                    bestDescription = info;
                }
            }
        }

        float scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        float scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (bestDescription != null && bestDescription.alpha() > 0.01f && !bestDescription.description().isEmpty()) {
            renderDescription(stack, bestDescription.description(),
                    bestDescription.alpha() * guiFadeAlpha * GuiStyle.descriptionAlpha(),
                    scaledWidth, scaledHeight);
        } else if (!lastDescription.isEmpty()) {
            lastDescription = "";
            descriptionTextLength = 0;
            descriptionTextStartTime = 0L;
        }

        super.render(context, mouseX, mouseY, partialTicks);
        GuiBlur.endFrame();
    }

    private void renderUIPanels(PoseStack stack, int mouseX, int mouseY, long currentTime) {
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.updateAnimation(currentTime, scaleV);
            if (panel.getAnimProgress() > 0.01f || !closing) {
                stack.pushPose();
                stack.translate(0.0f, 0.0f, i * 0.01f);
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
                stack.translate(0.0f, 0.0f, 100.0f);
                themePanel.render(stack, mouseX, mouseY);
                stack.popPose();
            }
        }
    }

    private void renderInterfacePanel(PoseStack stack, int mouseX, int mouseY, long currentTime) {
        if (interfacePanel != null) {
            interfacePanel.updateAnimation(currentTime, scaleV);
            if (interfacePanel.getAnimProgress() > 0.01f || !closing) {
                stack.pushPose();
                stack.translate(0.0f, 0.0f, 100.0f);
                interfacePanel.render(stack, mouseX, mouseY);
                stack.popPose();
            }
        }
    }

    // Вкладка Hands — живой редактор: панели нет, руки таскаются прямо за экраном.
    private void renderHandsHint(PoseStack stack) {
        float sScale = getResolutionScale();
        Minecraft client = Minecraft.getInstance();

        String text = "ЛКМ — перетащить · Колесо — размер · ПКМ — сброс · СКМ — анимации · Островок — сила и скорость";
        float textSize = 8.0f * sScale;
        float textWidth = GuiFonts.SF_REGULAR.getWidth(text, textSize);
        float padding = 10.0f * sScale;
        float plateW = textWidth + padding * 2.0f;
        float plateH = textSize + padding * 1.4f;
        float plateX = client.getWindow().getGuiScaledWidth() / 2.0f - plateW / 2.0f;
        float plateY = client.getWindow().getGuiScaledHeight() - plateH - 18.0f * sScale;

        GuiRender.roundedRect(stack, plateX, plateY, plateW, plateH, plateH / 2.0f,
                ColorUtils.getColor(0, 0, 0, (int) (0.45 * 255 * guiFadeAlpha)));
        GuiFonts.SF_REGULAR.draw(stack, text, plateX + padding,
                plateY + plateH / 2.0f - textSize / 2.0f,
                ColorUtils.multAlpha(GuiTheme.textColor(), 0.75f * guiFadeAlpha), textSize);
    }

    private float descriptionWidthAnim = 0.0f;
    private int descriptionTextLength = 0;
    private long descriptionTextStartTime = 0L;
    private String lastDescription = "";

    private void renderDescription(PoseStack stack, String description, float alpha,
                                   float scaledWidth, float scaledHeight) {
        if (!GuiStyle.showDescriptions()) {
            return;
        }
        float sScale = resolutionScale;
        float descFontSize = getAdaptiveTextSize() * 1.1f * GuiStyle.descriptionSize();
        float padding = 12.0f * sScale;

        if (!description.equals(lastDescription)) {
            lastDescription = description;
            descriptionTextLength = 0;
            descriptionTextStartTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - descriptionTextStartTime;
        int targetLength = description.length();
        int charsPerSecond = GuiStyle.descriptionSpeed();
        int newLength = (int) Math.min(targetLength, (elapsed / 1000.0) * charsPerSecond);

        if (newLength > descriptionTextLength) {
            descriptionTextLength = newLength;
        }

        String displayText = description.substring(0, Math.min(descriptionTextLength, description.length()));
        if (descriptionTextLength < targetLength && System.currentTimeMillis() % 500 < 250) {
            displayText += "|";
        }

        float textWidth = GuiFonts.SF_REGULAR.getWidth(displayText, descFontSize);
        float descHeight = 24.0f * sScale;

        descriptionWidthAnim = Mathf.fast(descriptionWidthAnim, textWidth + padding * 2.0f, 8.0f);

        float centerX = scaledWidth / 2.0f;
        float centerY = scaledHeight / 2.0f;
        float descX = GuiStyle.descriptionsRight()
                ? scaledWidth - descriptionWidthAnim - 10.0f * sScale
                : centerX - descriptionWidthAnim / 2.0f;
        float descY = centerY - panelHeight / 2.0f - 50.0f * sScale;

        GuiRender.drawGlass(stack, descX, descY, descriptionWidthAnim, descHeight, 12.0f * sScale,
                true, GuiTheme.blurStrength() * alpha);

        float textX = descX + descriptionWidthAnim / 2.0f - textWidth / 2.0f;
        float textY = descY + descHeight / 2.0f - GuiFonts.SF_REGULAR.getHeight(displayText, descFontSize) / 2.0f;

        GuiFonts.SF_REGULAR.draw(stack, displayText, textX, textY,
                ColorUtils.multAlpha(GuiTheme.textColor(), alpha), descFontSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (GuiStyle.showIsland() && dynamicIsland.mouseClicked((int) mouseX, (int) mouseY, button)) {
            return true;
        }

        if (currentViewMode == ViewMode.HANDS) {
            ViewModel viewModel = ModuleManager.get(ViewModel.class);
            if (viewModel != null) {
                if (viewModel.isAnimMenuOpen()) {
                    if (viewModel.animMenuClick((float) mouseX, (float) mouseY, button)) {
                        return true;
                    }
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE || button == 2) {
                    if (viewModel.editorMiddleClick((float) mouseX, (float) mouseY)) {
                        return true;
                    }
                }
                if (viewModel.editorReset(button)) {
                    return true;
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                        && viewModel.editorGrab((float) mouseX, (float) mouseY)) {
                    return true;
                }
            }
        }

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        } else if (currentViewMode == ViewMode.INTERFACE && interfacePanel != null) {
            interfacePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        } else if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (GuiStyle.showIsland() && dynamicIsland.mouseDragged((int) mouseX, (int) mouseY)) {
            return true;
        }
        if (currentViewMode == ViewMode.HANDS) {
            ViewModel viewModel = ModuleManager.get(ViewModel.class);
            if (viewModel != null && viewModel.isHolding()) {
                viewModel.editorDrag((float) mouseX, (float) mouseY);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (GuiStyle.showIsland()) {
            dynamicIsland.mouseReleased();
        }

        if (currentViewMode == ViewMode.HANDS) {
            ViewModel viewModel = ModuleManager.get(ViewModel.class);
            if (viewModel != null && viewModel.isHolding()) {
                viewModel.editorRelease();
                return true;
            }
        }

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        } else if (currentViewMode == ViewMode.INTERFACE && interfacePanel != null) {
            interfacePanel.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
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

        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.key(keyCode);
        } else if (currentViewMode == ViewMode.INTERFACE && interfacePanel != null) {
            interfacePanel.key(keyCode);
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
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
        if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.charTyped(codePoint);
        } else if (currentViewMode == ViewMode.INTERFACE && interfacePanel != null) {
            interfacePanel.charTyped(codePoint);
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.charTyped(codePoint);
            }
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (currentViewMode == ViewMode.HANDS) {
            ViewModel viewModel = ModuleManager.get(ViewModel.class);
            if (viewModel != null && viewModel.onScroll((float) mouseX, (float) mouseY, verticalAmount)) {
                return true;
            }
        }

        if (currentViewMode == ViewMode.UI) {
            for (Panel panel : panels) {
                panel.scroll((float) mouseX, (float) mouseY, (float) verticalAmount);
            }
        } else if (currentViewMode == ViewMode.CLIENT && themePanel != null) {
            themePanel.scroll((float) mouseX, (float) mouseY, (float) verticalAmount);
        } else if (currentViewMode == ViewMode.INTERFACE && interfacePanel != null) {
            interfacePanel.scroll((float) mouseX, (float) mouseY, (float) verticalAmount);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onClose() {
        if (closing) {
            return;
        }
        GuiStyle.playSound("guiclose.wav", 0.3f);
        closing = true;
        scale = 0.0f;
        ViewModel vm = ModuleManager.get(ViewModel.class);
        if (vm != null) {
            vm.closeAnimMenu();
        }
        if (themePanel != null) {
            themePanel.startClosingAnimation();
        }
        for (Panel panel : panels) {
            panel.startClosingAnimation();
        }
        isInitialOpen = true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
