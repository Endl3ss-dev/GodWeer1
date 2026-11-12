package tech.lightclient.ui.gui;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public abstract class BaseClickGui extends Screen {
    protected static BaseClickGui INSTANCE = null;

    public static double rawMouseX = 0;
    public static double rawMouseY = 0;

    protected final SimpleLinearAnimation guiOpenAnimation = new SimpleLinearAnimation(240);
    protected boolean closing = false;
    protected long openStartMs = System.currentTimeMillis();
    protected long closeStartMs = 0L;
    protected double lastMouseX = 0;
    protected double lastMouseY = 0;
    protected boolean closeBindGuard = false;
    /** Runs once after the close animation finishes (e.g. open Script IDE). */
    private Runnable afterCloseAction = null;

    protected BaseClickGui() {
        super(Text.literal("ClickGui"));
        guiOpenAnimation.setEasing(Easings.EASE_OUT_CUBIC);
        guiOpenAnimation.show();
        CursorHelper.init();

        if (mc != null && mc.getWindow() != null) {
            closeBindGuard = tech.lightclient.modules.impl.render.ClickGui.isOpenBindPressed(mc.getWindow().getHandle());
        }
    }

    @EventHandler
    public static void onGuiRender(EventRender.Screen.Gui event) {
        if (!(mc.currentScreen instanceof BaseClickGui gui)) return;
        gui.render(event.getRenderer(), gui.width, gui.height);
    }

    private void render(Renderer2D r, int vw, int vh) {
        CursorHelper.ensureVisible();

        if (closeBindGuard && mc.getWindow() != null
                && !tech.lightclient.modules.impl.render.ClickGui.isOpenBindPressed(mc.getWindow().getHandle())) {
            closeBindGuard = false;
        }

        if (closing) {
            guiOpenAnimation.hide();
            if (guiOpenAnimation.isFinished()) {
                Runnable next = afterCloseAction;
                afterCloseAction = null;
                closing = false;
                if (next != null) {
                    next.run();
                } else {
                    mc.setScreen(null);
                }
                return;
            }
        }
        renderGui(r, vw, vh);
    }

    protected abstract void renderGui(Renderer2D r, int vw, int vh);

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        double sf = mc.getWindow().getScaleFactor();
        rawMouseX = mouseX * sf;
        rawMouseY = mouseY * sf;
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {}

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() { startClosing(); }

    protected void startClosing() {
        if (closing) return;
        closing = true;
        closeStartMs = System.currentTimeMillis();
        guiOpenAnimation.hide();
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.key();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || tech.lightclient.modules.impl.render.ClickGui.isOpenBindKeyboardKey(keyCode)) {
            if (tech.lightclient.modules.impl.render.ClickGui.isOpenBindKeyboardKey(keyCode) && closeBindGuard) return true;
            startClosing();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean mouseClicked(Click click, boolean isRepeated) {
        double scale = mc.getWindow().getScaleFactor();
        double mouseX = click.x() * scale;
        double mouseY = click.y() * scale;
        int button = click.button();
        if (handleMouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(click, isRepeated);
    }

    protected abstract boolean handleMouseClicked(double mouseX, double mouseY, int button);

    public static void open() {
        if (LightClient.isHide()) return;

        var freeLook = LightClient.getFunctionManager().getFreeLook();
        if (freeLook != null) {
            freeLook.suspendForScreen();
        }

        if (!(INSTANCE instanceof ClickGui)) {
            INSTANCE = new ClickGui();
        } else {
            INSTANCE.resetAnimations();
        }

        mc.setScreen(INSTANCE);
    }

    /**
     * Plays the ClickGUI close animation, then runs {@code next}
     * (used to open Script IDE only after the panel has fully disappeared).
     */
    public static void closeThen(Runnable next) {
        if (mc != null && mc.currentScreen instanceof BaseClickGui gui) {
            gui.afterCloseAction = next;
            gui.startClosing();
            return;
        }
        if (next != null) {
            next.run();
        }
    }

    protected void resetAnimations() {
        closing = false;
        afterCloseAction = null;
        openStartMs = System.currentTimeMillis();
        closeStartMs = 0L;
        // Force a fresh open tween even if the previous session left the
        // animation fully shown (e.g. IDE replaced this screen without hide()).
        guiOpenAnimation.setImmediate(false);
        guiOpenAnimation.show();

        if (mc != null && mc.getWindow() != null) {
            closeBindGuard = tech.lightclient.modules.impl.render.ClickGui.isOpenBindPressed(mc.getWindow().getHandle());
        }
    }

    protected int scaledMouseX() {
        return (int) Math.round(lastMouseX * mc.getWindow().getScaleFactor());
    }

    protected int scaledMouseY() {
        return (int) Math.round(lastMouseY * mc.getWindow().getScaleFactor());
    }

    protected static int fadeColor(int color, int alpha) {
        return ColorUtils.multAlpha(color, Math.max(0f, Math.min(1f, alpha / 255f)));
    }

    protected static int fadeColor(int color, float alphaProgress) {
        return ColorUtils.multAlpha(color, Math.max(0f, Math.min(1f, alphaProgress)));
    }
}

