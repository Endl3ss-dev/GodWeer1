package godweer.utils.render.draw;

import godweer.utils.render.ScissorUtils;

public final class ScissorExp {

    private ScissorExp() {
    }

    public static void push() {
        ScissorUtils.push();
    }

    public static void pop() {
        ScissorUtils.pop();
    }

    public static void unset() {
        ScissorUtils.unset();
    }

    public static void setFromComponentCoordinates(float x, float y, float width, float height) {
        ScissorUtils.setFromComponentCoordinates(x, y, width, height);
    }

    public static void setFromComponentCoordinates(int x, int y, int width, int height) {
        ScissorUtils.setFromComponentCoordinates(x, y, width, height);
    }
}
