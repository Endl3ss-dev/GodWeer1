package minced.core;

import net.minecraft.util.math.Vec3d;

/**
 * A short-lived marker for a detected firework launch.
 */
public class FireworkPoint {
    private static final long LIFETIME_MS = 5000L;
    private static final float FADE_SPEED = 4.0f;

    private final ClientState timer = new ClientState();
    private final Vec3d position;
    private final AnimatedFloat opacity =
        new AnimatedFloat(1.0f, FADE_SPEED, Easings.field11952);
    private boolean fading;

    public boolean method2624() {
        return fading;
    }

    public void method2625() {
        if (fading) {
            opacity.setTarget(0.0f);
        }
    }

    public void method2626() {
        fading = true;
        opacity.setTarget(0.0f);
    }

    public boolean method2627() {
        return fading && opacity.isFinished();
    }

    public float method2628() {
        return opacity.getValue();
    }

    public FireworkPoint(Vec3d position) {
        this.position = position;
    }

    public Vec3d method2629() {
        return position;
    }

    public ClientState method2630() {
        return timer;
    }

    public boolean method2631() {
        return !fading || opacity.getValue() > 0.01f;
    }

    public long method2632() {
        return LIFETIME_MS;
    }
}
