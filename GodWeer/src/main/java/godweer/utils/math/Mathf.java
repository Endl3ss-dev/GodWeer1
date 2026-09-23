package godweer.utils.math;

public final class Mathf {

    private Mathf() {
    }

    public static float fast(float current, float target, float speed) {
        float delta = target - current;
        if (Math.abs(delta) < 0.001f) {
            return target;
        }
        return current + delta * Math.min(1.0f, speed / 100.0f);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float step(float value, float step) {
        if (step <= 0.0f) {
            return value;
        }
        return Math.round(value / step) * step;
    }

    public static boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return y <= rangeY + rangeHeight && y + height >= rangeY;
    }

    public static boolean isHover(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
