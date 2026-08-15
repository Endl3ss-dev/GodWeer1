package godweer.ui;

import com.mojang.blaze3d.platform.Window;

import godweer.GodWeer;
import godweer.managers.ModuleManager;
import godweer.modules.ModuleCategory;
import godweer.ui.clickgui.ClickGuiInputHandler;
import godweer.ui.clickgui.ClickGuiRenderer;
import godweer.ui.clickgui.ClickGuiSettingRenderer;
import godweer.ui.clickgui.ClickGuiState;
import godweer.ui.clickgui.ClickGuiThemeSelector;
import godweer.utils.animation.AnimationUtils;
import godweer.utils.animation.Easings;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

public class MenuPanel extends Screen {

    private static final float SLIDE_SPEED = 8f;
    private static final float ALPHA_SPEED = 5f;
    private static final float OPEN_SPEED = 7.5f;
    private static final float SLIDE_OFFSET = 220f;
    private static final float PANEL_OFFSET_Y_MUL = 22.0f;

    // Состояние (скроллы, анимации, поиск) переживает закрытие экрана.
    private static final ClickGuiState SHARED_STATE = new ClickGuiState();

    private final int categoryCount = ModuleCategory.values().length;
    private final ClickGuiState state = SHARED_STATE;
    private final ClickGuiThemeSelector themeSelector = new ClickGuiThemeSelector();
    private final ClickGuiRenderer renderer =
            new ClickGuiRenderer(state, new ClickGuiSettingRenderer(), themeSelector, this);
    private final ClickGuiInputHandler inputHandler = new ClickGuiInputHandler(state, themeSelector);
    private final AnimationUtils openAnimation = new AnimationUtils(0f, OPEN_SPEED, Easings.CUBIC_OUT);

    private final Map<ModuleCategory, AnimationUtils> panelSlideX = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, AnimationUtils> panelSlideY = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, AnimationUtils> panelAlpha = new EnumMap<>(ModuleCategory.class);

    private static boolean diagnosticsLogged;

    private boolean closing;

    public MenuPanel() {
        super(Component.literal("ClickGui"));
        state.refreshModules();
        initPanelAnimations();
    }

    private void initPanelAnimations() {
        for (ModuleCategory category : ModuleCategory.values()) {
            panelSlideX.put(category, new AnimationUtils(getStartOffX(category), SLIDE_SPEED, Easings.QUINT_OUT));
            panelSlideY.put(category, new AnimationUtils(getStartOffY(category), SLIDE_SPEED, Easings.QUINT_OUT));
            panelAlpha.put(category, new AnimationUtils(0f, ALPHA_SPEED, Easings.BOUNCE_OUT));
        }
    }

    private void resetPanelSlides() {
        for (ModuleCategory category : ModuleCategory.values()) {
            AnimationUtils slideX = panelSlideX.get(category);
            AnimationUtils slideY = panelSlideY.get(category);
            AnimationUtils alpha = panelAlpha.get(category);
            if (slideX != null) {
                slideX.setValue(getStartOffX(category));
            }
            if (slideY != null) {
                slideY.setValue(getStartOffY(category));
            }
            if (alpha != null) {
                alpha.setValue(0f);
            }
        }
    }

    private float getStartOffX(ModuleCategory category) {
        return switch (category) {
            case COMBAT -> -SLIDE_OFFSET;
            case MISC -> SLIDE_OFFSET;
            default -> 0f;
        };
    }

    private float getStartOffY(ModuleCategory category) {
        return switch (category) {
            case MOVEMENT, PLAYER -> -SLIDE_OFFSET;
            case RENDER -> SLIDE_OFFSET;
            default -> 0f;
        };
    }

    public float getPanelSlideOffsetX(ModuleCategory category) {
        AnimationUtils slideX = panelSlideX.get(category);
        if (slideX == null) {
            return 0f;
        }
        if (!closing) {
            slideX.update(0f);
        }
        return slideX.getValue();
    }

    public float getPanelSlideOffsetY(ModuleCategory category) {
        AnimationUtils slideY = panelSlideY.get(category);
        if (slideY == null) {
            return 0f;
        }
        if (!closing) {
            slideY.update(0f);
        }
        return slideY.getValue();
    }

    public float getPanelAlpha(ModuleCategory category) {
        AnimationUtils alpha = panelAlpha.get(category);
        if (alpha == null) {
            return 1f;
        }
        if (!closing) {
            alpha.update(1f);
        }
        return Mth.clamp(alpha.getValue(), 0f, 1f);
    }

    private Window getClientWindow() {
        Minecraft mc = Minecraft.getInstance();
        return mc == null ? null : mc.getWindow();
    }

    private void syncLayout() {
        Window window = getClientWindow();
        if (window != null) {
            state.updatePosition(window, categoryCount);
        }
    }

    @Override
    protected void init() {
        super.init();
        resetPanelSlides();
        openAnimation.setValue(0f);
        openAnimation.setEasing(Easings.CUBIC_OUT);
        closing = false;
        logDiagnosticsOnce();
    }

    // Однократная диагностика: без неё пустой экран неотличим от «GUI не открылся».
    private void logDiagnosticsOnce() {
        if (diagnosticsLogged) {
            return;
        }
        diagnosticsLogged = true;

        int modules = ModuleManager.getModules().size();
        boolean shaderReady = RenderUtils.isRoundedRectShaderReady();
        boolean fontReady = Fonts.Font5.get(15) != null;

        GodWeer.LOGGER.info("ClickGui opened: modules={}, roundedRectShader={}, font(Font5)={}",
                modules, shaderReady ? "ok" : "MISSING", fontReady ? "ok" : "MISSING");
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Window window = getClientWindow();
        if (window == null) {
            return;
        }

        updateAnimation();
        float progress = getAnimationProgress();

        if (closing && progress <= 0.001f) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        state.updatePosition(window, categoryCount);
        state.setRenderOffsetY(getPanelOffsetY(progress));

        renderer.render(context, mouseX, mouseY, window, progress);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (closing) {
            return true;
        }
        syncLayout();
        state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
        return inputHandler.mouseClicked(mouseX, mouseY, button, getClientWindow())
                || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (closing) {
            return true;
        }
        syncLayout();
        return inputHandler.mouseReleased(button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (closing) {
            return true;
        }
        syncLayout();
        state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
        return inputHandler.mouseDragged(mouseX, mouseY, button)
                || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (closing) {
            return true;
        }
        syncLayout();
        state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
        return inputHandler.mouseScrolled(mouseX, mouseY, verticalAmount)
                || super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (closing) {
            return true;
        }
        if (inputHandler.keyPressed(keyCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            startClosing();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (closing) {
            return true;
        }
        return inputHandler.charTyped(chr) || super.charTyped(chr, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        startClosing();
    }

    private void startClosing() {
        if (closing) {
            return;
        }
        closing = true;
        openAnimation.setEasing(Easings.CUBIC_IN);
    }

    private void updateAnimation() {
        if (closing) {
            openAnimation.update(0.0f);
        } else {
            openAnimation.setEasing(Easings.CUBIC_OUT);
            openAnimation.update(1.0f);
        }
    }

    private float getAnimationProgress() {
        return Mth.clamp(openAnimation.getValue(), 0.0f, 1.0f);
    }

    private float getPanelOffsetY(float progress) {
        return (1.0f - progress) * PANEL_OFFSET_Y_MUL;
    }
}
