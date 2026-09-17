package godweer.utils.movement;

import net.minecraft.client.player.LocalPlayer;

public final class MotionHooks {

    public interface Listener {

        default void preMotion(LocalPlayer player) {
        }

        default void positionCorrection() {
        }
    }

    private static volatile boolean hooksLoaded;
    private static Listener listener;

    private MotionHooks() {
    }

    // Вызывается из static-блока миксина: класс миксина грузится вместе с трансформацией LocalPlayer,
    // значит флаг гарантированно поднят до первого player.tick() в мире.
    public static void markHooksLoaded() {
        hooksLoaded = true;
    }

    public static boolean hooksLoaded() {
        return hooksLoaded;
    }

    public static void subscribe(Listener next) {
        listener = next;
    }

    public static void unsubscribe(Listener expected) {
        if (listener == expected) {
            listener = null;
        }
    }

    public static void preMotion(LocalPlayer player) {
        Listener current = listener;
        if (current != null) {
            current.preMotion(player);
        }
    }

    public static void positionCorrection() {
        Listener current = listener;
        if (current != null) {
            current.positionCorrection();
        }
    }
}
