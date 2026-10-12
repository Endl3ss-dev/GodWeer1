package swerve.ui.hud;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

// Состояние курсора для HUD-слоя: обновляется один раз за кадр из HudManager,
// поэтому элементы не дёргают glfw и не пересчитывают масштаб окна каждый сам.
public final class HudPointer {

    private static double x;
    private static double y;
    private static boolean leftDown;
    private static boolean rightDown;
    private static boolean pressPending;

    private HudPointer() {
    }

    public static void update(Minecraft minecraft) {
        double windowScale = Math.max(1.0, minecraft.getWindow().getGuiScale());
        x = minecraft.mouseHandler.xpos() / windowScale;
        y = minecraft.mouseHandler.ypos() / windowScale;

        long window = minecraft.getWindow().getWindow();
        boolean down = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        pressPending = down && !leftDown;
        leftDown = down;
        // [+] ПКМ: элемент по ней открывает своё меню настроек (ПКМ в чате).
        rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
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

    // Состояние ПКМ за текущий кадр: фронт нажатия элементы вычисляют сами,
    // чтобы не отбирать событие у арбитража перетаскивания HudManager.
    public static boolean isRightDown() {
        return rightDown;
    }

    // true ровно один кадр — на фронте нажатия. Сброс флага защищает от повторного захвата элемента.
    public static boolean consumePress() {
        boolean pending = pressPending;
        pressPending = false;
        return pending;
    }

    // [+] Клик, сделанный по GUI (например, в чате), не должен доживать до первого кадра
    // без экрана и запускать перетаскивание HUD-элемента.
    public static void discardPress() {
        pressPending = false;
    }
}
