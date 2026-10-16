package swerve.utils.render.spatial;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import swerve.mixins.accessor.MouseHandlerAccessor;
import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

import org.lwjgl.glfw.GLFW;

/**
 * Курсор на панели: физическая позиция мыши превращается лучом в точку на экране-текстуре,
 * а в режиме прицела мышь остаётся захваченной и крутит камеру.
 */
public final class SpatialMouse {

    private static final double HOVER_DEADBAND_PX = 1.5;
    private static final double[] CURSOR_X = new double[1];
    private static final double[] CURSOR_Y = new double[1];

    private static boolean weGrabbedMouse;
    private static double physicalX = Double.NaN;
    private static double physicalY = Double.NaN;
    private static double lastPhysicalX = Double.NaN;
    private static double lastPhysicalY = Double.NaN;
    private static double mappedX = Double.NaN;
    private static double mappedY = Double.NaN;
    private static int heldHoverX = Integer.MIN_VALUE;
    private static int heldHoverY = Integer.MIN_VALUE;
    private static double freeLookDeltaX;
    private static double freeLookDeltaY;

    /** Выход captureMove(). */
    public static double deltaX;
    public static double deltaY;
    /** Выход consumeFreeLook(). */
    public static double freeLookX;
    public static double freeLookY;
    /** Выход remapMove(): координаты, которые получает ванильный onMove. */
    public static double moveX;
    public static double moveY;

    private SpatialMouse() {
    }

    public static void resetState() {
        Minecraft mc = Minecraft.getInstance();
        mappedX = Double.NaN;
        mappedY = Double.NaN;
        heldHoverX = Integer.MIN_VALUE;
        heldHoverY = Integer.MIN_VALUE;
        freeLookDeltaX = 0.0;
        freeLookDeltaY = 0.0;
        if (((MouseHandlerAccessor) mc.mouseHandler).swerve$isMouseGrabbed()) {
            physicalX = centerX();
            physicalY = centerY();
        } else {
            GLFW.glfwGetCursorPos(mc.getWindow().getWindow(), CURSOR_X, CURSOR_Y);
            physicalX = CURSOR_X[0];
            physicalY = CURSOR_Y[0];
        }
        lastPhysicalX = physicalX;
        lastPhysicalY = physicalY;
    }

    public static void captureMove(double x, double y, boolean mouseGrabbed) {
        deltaX = x - lastPhysicalX;
        deltaY = y - lastPhysicalY;
        lastPhysicalX = x;
        lastPhysicalY = y;
        if (!mouseGrabbed) {
            physicalX = x;
            physicalY = y;
        }
    }

    private static double centerX() {
        return Minecraft.getInstance().getWindow().getScreenWidth() / 2.0;
    }

    private static double centerY() {
        return Minecraft.getInstance().getWindow().getScreenHeight() / 2.0;
    }

    public static double sourceX() {
        return SpatialState.isCrosshairMode() ? centerX() : physicalX(centerX());
    }

    public static double sourceY() {
        return SpatialState.isCrosshairMode() ? centerY() : physicalY(centerY());
    }

    public static double physicalX(double fallback) {
        return Double.isNaN(physicalX) ? fallback : physicalX;
    }

    public static double physicalY(double fallback) {
        return Double.isNaN(physicalY) ? fallback : physicalY;
    }

    /** Луч курсора → точка на панели; запоминает позицию в GUI-единицах панели. */
    public static boolean mapMousePosition(double sourceX, double sourceY, double guiScale) {
        boolean hit = ScreenSurface.cylinderValid
                ? SpatialMath.hitCylinder(sourceX, sourceY, ScreenSurface.CYLINDER_AXIS, ScreenSurface.CYLINDER_DIR,
                        ScreenSurface.CYLINDER_REF, ScreenSurface.CYLINDER_CENTER, ScreenSurface.cylinderRadius,
                        ScreenSurface.cylinderArc, ScreenSurface.cylinderHalfHeight)
                : SpatialMath.hitQuad(sourceX, sourceY, ScreenSurface.QUAD_CENTER, ScreenSurface.QUAD_RIGHT,
                        ScreenSurface.QUAD_UP, ScreenSurface.QUAD_NORMAL, ScreenSurface.quadHalfWidth,
                        ScreenSurface.quadHalfHeight);
        if (!hit) {
            return false;
        }
        mappedX = SpatialMath.hitX / guiScale;
        mappedY = SpatialMath.hitY / guiScale;
        return true;
    }

    /** Та же точка в пикселях окна (система координат ванильного MouseHandler). */
    public static boolean updateMousePosition(double sourceX, double sourceY) {
        if (!ScreenSurface.ensureBases()) {
            return false;
        }
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = SpatialScale.effective(window);
        if (!mapMousePosition(sourceX, sourceY, guiScale)) {
            return false;
        }
        moveX = SpatialMath.hitX / guiScale * window.getScreenWidth() / window.getGuiScaledWidth();
        moveY = SpatialMath.hitY / guiScale * window.getScreenHeight() / window.getGuiScaledHeight();
        return true;
    }

    private static double fallback(boolean isX) {
        Window window = Minecraft.getInstance().getWindow();
        if (window == null) {
            return 0.0;
        }
        return isX ? window.getGuiScaledWidth() / 2.0 : window.getGuiScaledHeight() / 2.0;
    }

    public static double lastPos(boolean isX, double fallback) {
        double value = isX ? mappedX : mappedY;
        return Double.isNaN(value) ? fallback : value;
    }

    public static int hoverX() {
        double value = lastPos(true, fallback(true));
        if (heldHoverX != Integer.MIN_VALUE && Math.abs(value - heldHoverX) < HOVER_DEADBAND_PX) {
            return heldHoverX;
        }
        return heldHoverX = (int) value;
    }

    public static int hoverY() {
        double value = lastPos(false, fallback(false));
        if (heldHoverY != Integer.MIN_VALUE && Math.abs(value - heldHoverY) < HOVER_DEADBAND_PX) {
            return heldHoverY;
        }
        return heldHoverY = (int) value;
    }

    public static void addFreeLookDelta(double dx, double dy) {
        freeLookDeltaX += dx;
        freeLookDeltaY += dy;
    }

    public static void consumeFreeLook() {
        freeLookX = freeLookDeltaX;
        freeLookY = freeLookDeltaY;
        freeLookDeltaX = 0.0;
        freeLookDeltaY = 0.0;
    }

    /** Мышь должна быть свободна, пока открыт экран без режима прицела. */
    public static void syncCursorGrab() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null || SpatialState.hooked == null || SpatialState.isCrosshairMode()) {
            return;
        }
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;
        long window = mc.getWindow().getWindow();
        boolean cursorFree = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED;
        if (!accessor.swerve$isMouseGrabbed() && cursorFree) {
            return;
        }
        accessor.swerve$setMouseGrabbed(false);
        double cx = centerX();
        double cy = centerY();
        InputConstants.grabOrReleaseMouse(window, InputConstants.CURSOR_NORMAL, cx, cy);
        accessor.swerve$setXpos(cx);
        accessor.swerve$setYpos(cy);
        resetState();
        weGrabbedMouse = false;
    }

    private static void grabForFirstPerson() {
        Minecraft mc = Minecraft.getInstance();
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;
        long window = mc.getWindow().getWindow();
        boolean needsGrab = !accessor.swerve$isMouseGrabbed()
                || GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED;
        if (needsGrab) {
            accessor.swerve$setMouseGrabbed(true);
            InputConstants.grabOrReleaseMouse(window, InputConstants.CURSOR_DISABLED, centerX(), centerY());
            mc.mouseHandler.setIgnoreFirstMove();
        }
        weGrabbedMouse = true;
    }

    public static void releaseFromFirstPerson() {
        if (!weGrabbedMouse) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) mc.mouseHandler;
        if (accessor.swerve$isMouseGrabbed()) {
            accessor.swerve$setMouseGrabbed(false);
            InputConstants.grabOrReleaseMouse(mc.getWindow().getWindow(), InputConstants.CURSOR_NORMAL, centerX(), centerY());
        }
        weGrabbedMouse = false;
    }

    public static void updateGrabForFirstPerson(boolean firstPerson) {
        SpatialGui module = SpatialGui.get();
        if (!firstPerson || module == null || !module.crosshairMode()) {
            releaseFromFirstPerson();
        } else {
            grabForFirstPerson();
        }
    }

    /** Нужно ли подменять координаты мыши для ванильного MouseHandler. */
    public static boolean overrideActive() {
        if (!SpatialState.active()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        return SpatialState.hookable(mc.screen) || SpatialState.hookable(SpatialState.hooked);
    }

    /** GUI-единицы панели → пиксели окна, как их ждёт MouseHandler перед своим делением. */
    public static double toRaw(boolean isX, double scaled) {
        Window window = Minecraft.getInstance().getWindow();
        return isX
                ? scaled * window.getScreenWidth() / (double) window.getGuiScaledWidth()
                : scaled * window.getScreenHeight() / (double) window.getGuiScaledHeight();
    }

    public static void remapMove(MouseHandlerAccessor accessor, double x, double y) {
        boolean grabbed = accessor.swerve$isMouseGrabbed();
        captureMove(x, y, grabbed);
        moveX = x;
        moveY = y;
        if (!overrideActive()) {
            return;
        }
        if (SpatialState.isCrosshairMode() && !accessor.swerve$isIgnoreFirstMove()) {
            addFreeLookDelta(deltaX, deltaY);
        }
        if (!updateMousePosition(sourceX(), sourceY())) {
            moveX = accessor.swerve$getXpos();
            moveY = accessor.swerve$getYpos();
        }
    }

    /** InputConstants.grabOrReleaseMouse: в режиме прицела ванильный releaseMouse при открытии экрана игнорируется. */
    public static boolean shouldKeepGrabbed() {
        SpatialGui module = SpatialGui.get();
        if (module == null || !module.isEnable() || !module.crosshairMode()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        Screen screen = mc.screen;
        return screen != null && module.shouldHookScreen(screen) && module.useFirstPerson();
    }

    public static void onPress(int action) {
        if (!SpatialGui.SWING_ON_CLICK || action != 1 || !SpatialState.active() || !SpatialState.effectiveFirstPerson) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null || mc.screen != SpatialState.hooked) {
            return;
        }
        LocalPlayer player = mc.player;
        if (player != null) {
            player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
