package never.lose.utils.input;

public final class CursorManager {

    private static boolean hand;
    private static boolean iBeam;
    private static boolean click;

    private CursorManager() {}

    public static void requestHand() {
        hand = true;
    }

    public static void requestIBeam() {
        iBeam = true;
    }

    public static void requestClick() {
        click = true;
    }

    public static boolean shouldBeHand() {
        return hand;
    }

    public static boolean shouldIBeam() {
        return iBeam;
    }

    public static boolean shouldClick() {
        return click;
    }

    public static void reset() {
        hand = false;
    }

    public static void resetIBeam() {
        iBeam = false;
    }

    public static void resetClick() {
        click = false;
    }
}
