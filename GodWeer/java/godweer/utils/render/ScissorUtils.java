package godweer.utils.render;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL30;

public final class ScissorUtils {

    private static final int MAX_DEPTH = 32;

    // Стек предвыделен: push/pop вызываются каждый кадр, аллокации и clone() здесь недопустимы.
    private static final boolean[] STACK_ENABLED = new boolean[MAX_DEPTH];
    private static final int[] STACK_TRANS_X = new int[MAX_DEPTH];
    private static final int[] STACK_TRANS_Y = new int[MAX_DEPTH];
    private static final int[] STACK_X = new int[MAX_DEPTH];
    private static final int[] STACK_Y = new int[MAX_DEPTH];
    private static final int[] STACK_WIDTH = new int[MAX_DEPTH];
    private static final int[] STACK_HEIGHT = new int[MAX_DEPTH];

    private static int depth;

    private static boolean enabled;
    private static int transX;
    private static int transY;
    private static int x;
    private static int y;
    private static int width;
    private static int height;

    private ScissorUtils() {
    }

    public static void push() {
        if (depth >= MAX_DEPTH) {
            return;
        }
        STACK_ENABLED[depth] = enabled;
        STACK_TRANS_X[depth] = transX;
        STACK_TRANS_Y[depth] = transY;
        STACK_X[depth] = x;
        STACK_Y[depth] = y;
        STACK_WIDTH[depth] = width;
        STACK_HEIGHT[depth] = height;
        depth++;
    }

    public static void pop() {
        if (depth == 0) {
            return;
        }
        depth--;
        enabled = STACK_ENABLED[depth];
        transX = STACK_TRANS_X[depth];
        transY = STACK_TRANS_Y[depth];
        x = STACK_X[depth];
        y = STACK_Y[depth];
        width = STACK_WIDTH[depth];
        height = STACK_HEIGHT[depth];

        if (enabled) {
            GL30.glEnable(GL30.GL_SCISSOR_TEST);
            GL30.glScissor(x, y, width, height);
        } else {
            GL30.glDisable(GL30.GL_SCISSOR_TEST);
        }
    }

    public static void unset() {
        GL30.glDisable(GL30.GL_SCISSOR_TEST);
        enabled = false;
    }

    public static void reset() {
        depth = 0;
        unset();
        transX = 0;
        transY = 0;
    }

    public static void set(int scissorX, int scissorY, int scissorWidth, int scissorHeight) {
        Window window = getWindow();
        if (window == null) {
            return;
        }

        int screenWidth = window.getWidth();
        int screenHeight = window.getHeight();

        int currentX = enabled ? x : 0;
        int currentY = enabled ? y : 0;
        int currentRight = enabled ? x + width : screenWidth;
        int currentBottom = enabled ? y + height : screenHeight;

        int targetX = scissorX + transX;
        int targetY = scissorY + transY;

        // Пересечение текущей области, запрошенной и экрана — без java.awt.Rectangle.
        int resultX = Math.max(Math.max(currentX, targetX), 0);
        int resultY = Math.max(Math.max(currentY, targetY), 0);
        int resultRight = Math.min(Math.min(currentRight, targetX + scissorWidth), screenWidth);
        int resultBottom = Math.min(Math.min(currentBottom, targetY + scissorHeight), screenHeight);

        int resultWidth = resultRight - resultX;
        int resultHeight = resultBottom - resultY;
        if (resultWidth < 0) {
            resultWidth = 0;
        }
        if (resultHeight < 0) {
            resultHeight = 0;
        }

        enabled = true;
        x = resultX;
        y = resultY;
        width = resultWidth;
        height = resultHeight;

        GL30.glEnable(GL30.GL_SCISSOR_TEST);
        GL30.glScissor(resultX, resultY, resultWidth, resultHeight);
    }

    public static void setFromComponentCoordinates(int componentX, int componentY, int componentWidth, int componentHeight) {
        setFromComponentCoordinates((double) componentX, componentY, componentWidth, componentHeight);
    }

    public static void setFromComponentCoordinates(float componentX, float componentY, float componentWidth, float componentHeight) {
        setFromComponentCoordinates((double) componentX, componentY, componentWidth, componentHeight);
    }

    public static void setFromComponentCoordinates(double componentX, double componentY, double componentWidth, double componentHeight) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        double scaleFactor = window.getGuiScale();

        int screenX = (int) (componentX * scaleFactor);
        int screenY = (int) (componentY * scaleFactor);
        int screenWidth = (int) (componentWidth * scaleFactor);
        int screenHeight = (int) (componentHeight * scaleFactor);

        set(screenX, window.getHeight() - screenY - screenHeight, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double componentX, double componentY, double componentWidth, double componentHeight, float scale) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        double scaleFactor = window.getGuiScale();

        float halfRest = (1.0f - scale) / 2.0f;
        double animatedX = componentX + componentWidth * halfRest;
        double animatedY = componentY + componentHeight * halfRest;
        double animatedWidth = componentWidth * scale;
        double animatedHeight = componentHeight * scale;

        animatedX = animatedX * scale + (window.getGuiScaledWidth() - animatedWidth) * halfRest;

        int screenX = (int) (animatedX * scaleFactor);
        int screenY = (int) (animatedY * scaleFactor);
        int screenWidth = (int) (animatedWidth * scaleFactor);
        int screenHeight = (int) (animatedHeight * scaleFactor);

        set(screenX, window.getHeight() - screenY - screenHeight, screenWidth, screenHeight);
    }

    public static void translate(int translateX, int translateY) {
        transX = translateX;
        transY = translateY;
    }

    public static void translateFromComponentCoordinates(int componentX, int componentY) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        double scaleFactor = window.getGuiScale();
        int totalHeight = window.getGuiScaledHeight();

        int screenX = (int) (componentX * scaleFactor);
        int screenY = (int) (totalHeight * scaleFactor) - (int) (componentY * scaleFactor);
        translate(screenX, screenY);
    }

    private static Window getWindow() {
        Minecraft client = Minecraft.getInstance();
        return client == null ? null : client.getWindow();
    }
}
