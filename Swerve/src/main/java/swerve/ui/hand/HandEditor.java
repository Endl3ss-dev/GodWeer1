package swerve.ui.hand;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.impl.render.SwingAnimation;
import swerve.modules.impl.render.ViewModel;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.HandProjection;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;

import org.lwjgl.glfw.GLFW;

// Редактор рук в чате: 2×ЛКМ — захват руки + панель взмаха над ней, ПКМ — меню анимаций,
// колесо — масштаб. Работает через Fabric Screen API, поэтому миксины в ChatScreen не нужны.
public final class HandEditor {

    static final int BACKGROUND = ColorUtils.rgba(16, 16, 20, 235);
    static final int OUTLINE = ColorUtils.rgba(255, 255, 255, 30);
    static final int HOVER = ColorUtils.rgba(255, 255, 255, 16);
    static final int TRACK = ColorUtils.rgba(255, 255, 255, 30);
    static final int WHITE = ColorUtils.rgba(255, 255, 255, 255);
    static final int MUTED = ColorUtils.rgba(170, 175, 188, 255);

    private static final long DOUBLE_CLICK_MS = 300L;
    private static final float MAX_FRAME_DELTA = 0.1F;
    private static final float OVERLAY_Z = 400.0F;

    private static final HandAnimationMenu MENU = new HandAnimationMenu();
    private static final HandSwingPanel PANEL = new HandSwingPanel(MENU);

    private static boolean initialized;
    private static long lastHandClickMs;
    private static long lastFrameNanos;

    private HandEditor() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        // Fabric пересоздаёт события экрана на каждом init/resize, поэтому подписка здесь не дублируется.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof ChatScreen) {
                attach(screen);
            }
        });
    }

    private static void attach(Screen screen) {
        ScreenMouseEvents.allowMouseClick(screen).register((s, mouseX, mouseY, button) -> !mouseClicked((float) mouseX, (float) mouseY, button));
        ScreenMouseEvents.allowMouseRelease(screen).register((s, mouseX, mouseY, button) -> {
            mouseReleased(button);
            return true;
        });
        ScreenMouseEvents.allowMouseScroll(screen).register((s, mouseX, mouseY, horizontal, vertical) -> !mouseScrolled((float) mouseX, (float) mouseY, vertical));
        ScreenKeyboardEvents.allowKeyPress(screen).register((s, key, scancode, modifiers) -> !keyPressed(key));
        ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, tickDelta) -> render(graphics, mouseX, mouseY));
        ScreenEvents.remove(screen).register(s -> close());
    }

    private static boolean mouseClicked(float mouseX, float mouseY, int button) {
        ViewModel viewModel = ViewModel.get();
        if (viewModel == null || !viewModel.isEnable()) {
            return false;
        }

        boolean onHand = HandProjection.isHandUnderCursor();
        boolean consumed = false;

        if (MENU.isOpen()) {
            if (MENU.contains(mouseX, mouseY)) {
                MENU.mouseClicked(mouseX, mouseY, button);
                return true;
            }
            MENU.close();
            consumed = true;
            if (!onHand) {
                return true;
            }
        }

        if (PANEL.contains(mouseX, mouseY)) {
            PANEL.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        if (!onHand) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                PANEL.close();
            }
            lastHandClickMs = 0L;
            return consumed;
        }

        InteractionHand hand = HandProjection.pickHand(mouseX, mouseY);
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            MENU.open(mouseX, mouseY);
            return true;
        }
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return consumed;
        }

        long now = System.currentTimeMillis();
        boolean doubleClick = lastHandClickMs != 0L && now - lastHandClickMs <= DOUBLE_CLICK_MS;
        lastHandClickMs = doubleClick ? 0L : now;
        if (!doubleClick) {
            return consumed;
        }

        viewModel.beginDrag(hand);
        PANEL.open(hand, mouseX, mouseY);
        return true;
    }

    private static void mouseReleased(int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        PANEL.mouseReleased();
        ViewModel viewModel = ViewModel.get();
        if (viewModel != null) {
            viewModel.endDrag();
        }
    }

    private static boolean mouseScrolled(float mouseX, float mouseY, double amount) {
        ViewModel viewModel = ViewModel.get();
        if (viewModel == null || !viewModel.isEnable() || amount == 0.0) {
            return false;
        }
        if (MENU.contains(mouseX, mouseY)) {
            return true;
        }
        if (PANEL.contains(mouseX, mouseY)) {
            PANEL.mouseScrolled(mouseY, amount);
            return true;
        }

        InteractionHand hand = viewModel.getDragHand();
        if (hand == null && HandProjection.isHandUnderCursor()) {
            hand = HandProjection.pickHand(mouseX, mouseY);
        }
        if (hand == null) {
            return false;
        }
        viewModel.scroll(hand, amount);
        return true;
    }

    private static boolean keyPressed(int key) {
        if (key != GLFW.GLFW_KEY_ESCAPE || (!MENU.isOpen() && !PANEL.isOpen())) {
            return false;
        }
        MENU.close();
        PANEL.close();
        return true;
    }

    private static void render(GuiGraphics graphics, int mouseX, int mouseY) {
        HandProjection.endUiFrame();
        float delta = nextFrameDelta();

        ViewModel viewModel = ViewModel.get();
        if (viewModel == null || !viewModel.isEnable()) {
            MENU.close();
            PANEL.close();
        }
        if (!MENU.isVisible() && !PANEL.isVisible()) {
            return;
        }
        if (MENU.isOpen() || PANEL.isOpen()) {
            SwingAnimation.previewTick();
        }

        // Ванильный GUI копит геометрию в буфере: сбрасываем его, иначе текст чата ляжет поверх панели.
        graphics.flush();
        PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.translate(0.0F, 0.0F, OVERLAY_Z);
        RenderSystem.disableDepthTest();
        PANEL.render(stack, mouseX, mouseY, delta);
        MENU.render(stack, mouseX, mouseY);
        RenderSystem.enableDepthTest();
        stack.popPose();
    }

    private static void close() {
        MENU.reset();
        PANEL.reset();
        lastHandClickMs = 0L;
        ViewModel viewModel = ViewModel.get();
        if (viewModel != null) {
            viewModel.endDrag();
        }
    }

    private static float nextFrameDelta() {
        long now = System.nanoTime();
        float delta = lastFrameNanos == 0L ? 0.0F : Math.min(MAX_FRAME_DELTA, (now - lastFrameNanos) / 1.0E9F);
        lastFrameNanos = now;
        return delta;
    }
}
