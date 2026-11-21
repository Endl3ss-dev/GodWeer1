package tech.lightclient.client.utils.player;

public class TimerController {
    private static float timerSpeed = 1.0f;

    public static void setTimerSpeed(float speed) {
        timerSpeed = Math.max(0.01f, speed);
    }

    public static float getTimerSpeed() {
        return timerSpeed;
    }

    public static void reset() {
        timerSpeed = 1.0f;
    }
}
