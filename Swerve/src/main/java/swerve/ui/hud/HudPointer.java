package swerve.ui.hud;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

// Состояние курсора для HUD-слоя: обновляется один раз за кадр из HudManager,
// поэтому элементы не дёргают glfw и не пересчитывают масштаб окна каждый сам.
public final class HudPointer {

    private static double x;
    private static double y;
    private static boolean leftDown;
    private static boolean pressPending;

    private HudPointer() {
    }

    public static void update(Minecraft minecraft) {
        double windowScale = Math.max(1.0, minecraft.getWindow().getGuiScale());
        x = minecraft.mouseHandler.xpos() / windowScale;
        y = minecraft.mouseHandler.ypos() / windowScale;

        boolean down = GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        pressPending = down && !leftDown;
        leftDown = down;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }

    public static boolean isLeftDown() {
        return leftDown;
    }

    // true ровно один кадр — на фронте нажатия. Сброс флага защищает от повторного захвата элемента.
    public static boolean consumePress() {
        boolean pending = pressPending;
        pressPending = false;
        return pending;
    }
}
