package godweer.utils.movement;

public final class TimerManager {

    private static volatile float speed = 1.0F;

    private TimerManager() {
    }

    public static void set(float value) {
        speed = Float.isFinite(value) ? Math.max(0.05F, value) : 1.0F;
    }

    public static float get() {
        return speed;
    }

    public static void reset() {
        speed = 1.0F;
    }
}
