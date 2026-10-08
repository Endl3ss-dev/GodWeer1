package never.lose.utils.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import java.awt.Rectangle;
import java.util.List;

public final class Scissor {

    private static class State implements Cloneable {
        public boolean enabled;
        public int transX;
        public int transY;
        public int x;
        public int y;
        public int width;
        public int height;

        @Override
        public State clone() {
            try {
                return (State) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }

    private static State state = new State();
    private static final List<State> STATE_STACK = Lists.newArrayList();

    private static float guiScale = 1f;
    private static float guiCenterX = 0f;
    private static float guiCenterY = 0f;

    private Scissor() {}

    public static void setGuiTransform(float scale, float centerX, float centerY) {
        guiScale = scale;
        guiCenterX = centerX;
        guiCenterY = centerY;
    }

    public static void resetGuiTransform() {
        guiScale = 1f;
        guiCenterX = 0f;
        guiCenterY = 0f;
    }

    public static void push() {
        STATE_STACK.add(state.clone());
    }

    public static void pop() {
        state = STATE_STACK.remove(STATE_STACK.size() - 1);
    }

    public static void unset() {
        RenderSystem.disableScissor();
        state.enabled = false;
    }

    public static void setFromComponentCoordinates(int x, int y, int width, int height) {
        setFromComponentCoordinates((double) x, (double) y, (double) width, (double) height);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height) {
        Window window = MinecraftClient.getInstance().getWindow();
        int scaleFactor = (int) window.getScaleFactor();

        if (guiScale != 1f) {
            x = guiCenterX + (x - guiCenterX) * guiScale;
            y = guiCenterY + (y - guiCenterY) * guiScale;
            width *= guiScale;
            height *= guiScale;
        }

        int screenX = (int) (x * scaleFactor);
        int screenY = (int) (y * scaleFactor);
        int screenWidth = (int) (width * scaleFactor);
        int screenHeight = (int) (height * scaleFactor);
        screenY = window.getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void set(int x, int y, int width, int height) {
        Window window = MinecraftClient.getInstance().getWindow();
        Rectangle screen = new Rectangle(0, 0, window.getWidth(), window.getHeight());

        Rectangle current = state.enabled
                ? new Rectangle(state.x, state.y, state.width, state.height)
                : screen;

        Rectangle target = new Rectangle(x + state.transX, y + state.transY, width, height);
        Rectangle result = current.intersection(target).intersection(screen);

        if (result.width < 0) result.width = 0;
        if (result.height < 0) result.height = 0;

        state.enabled = true;
        state.x = result.x;
        state.y = result.y;
        state.width = result.width;
        state.height = result.height;

        RenderSystem.enableScissor(result.x, result.y, result.width, result.height);
    }
}
