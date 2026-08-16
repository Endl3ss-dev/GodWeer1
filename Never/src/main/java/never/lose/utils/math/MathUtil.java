package never.lose.utils.math;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public final class MathUtil {

    private MathUtil() {}

    public static final float DEG2RAD = (float) (Math.PI / 180.0F);

    private static final Random          RAND          = new Random();
    private static final SecureRandom    SECURE_RANDOM = new SecureRandom();

    // ── базовые ───────────────────────────────────────────────────────────

    public static boolean isInPosition(float x, float y, float w, float h, float mx, float my) {
        return mx > x && mx < x + w && my > y && my < y + h;
    }

    public static float deltaTime() {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        return fps > 0 ? 1f / fps : 1f;
    }

    /**
     * Линейная интерполяция с учётом delta-time.
     * Используется для анимаций hover/unhover без привязки к FPS.
     */
    public static float fastAnim(float current, float target, float speed) {
        float dt = MathHelper.clamp(deltaTime() * speed, 0.01f, 1f);
        return (1f - dt) * current + dt * target;
    }

    public static int interpolate(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    public static double interpolate(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static Vec3d interpolate(Vec3d a, Vec3d b, float t) {
        return new Vec3d(
                interpolate(a.x, b.x, t),
                interpolate(a.y, b.y, t),
                interpolate(a.z, b.z, t)
        );
    }

    public static boolean range(double v, double min, double max) {
        return v >= min && v <= max;
    }

    public static float round(double num, double step) {
        double v = Math.round(num / step) * step;
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public static float clampScroll(float contentH, float viewH, float scroll) {
        if (contentH < viewH - 30) return 0;
        return MathHelper.clamp(scroll, -(contentH - (viewH - 30)), 0);
    }

    // ── случайные числа ───────────────────────────────────────────────────

    public static float random(float min, float max) {
        return SECURE_RANDOM.nextFloat() * (max - min) + min;
    }

    public static double random(double min, double max) {
        return SECURE_RANDOM.nextDouble() * (max - min) + min;
    }

    // ── easing ────────────────────────────────────────────────────────────

    public static double easeInOutSine(double t, double start, double end) {
        return start + (end - start) * (1.0 - Math.cos(t * Math.PI)) / 2.0;
    }

    public static float easeInOutQuad(float current, float target, float speed) {
        float dt = MathHelper.clamp(deltaTime() * speed, 0f, 1f);
        float t  = dt < 0.5f
                ? 2f * dt * dt
                : 1f - (-2f * (dt - 1f) + 1f) * (-2f * (dt - 1f) + 1f) / 2f;
        return (1f - t) * current + t * target;
    }

    // ── геометрия ─────────────────────────────────────────────────────────

    public static boolean between(Vec3d point, Vec3d start, Vec3d end, double thick) {
        double minX = Math.min(start.x, end.x) - thick;
        double minY = Math.min(start.y, end.y) - thick;
        double minZ = Math.min(start.z, end.z) - thick;
        double maxX = Math.max(start.x, end.x) + thick;
        double maxY = Math.max(start.y, end.y) + thick;
        double maxZ = Math.max(start.z, end.z) + thick;

        if (point.x < minX || point.x > maxX || point.y < minY ||
            point.y > maxY || point.z < minZ || point.z > maxZ) return false;

        Vec3d dir  = end.subtract(start);
        double len = dir.length();
        double proj = point.subtract(start).dotProduct(dir.normalize());
        return proj >= -thick && proj <= len + thick;
    }
}