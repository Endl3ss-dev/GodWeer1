package net.skycore.client.bot;

/** Local card appearance only; never changes a server-side skin or nickname. */
public final class BotAppearance {
    private static final int[] COLORS = {0xFF9A78EF, 0xFF49C9AC, 0xFF5CA6EB, 0xFFE5A753};
    private static final int[] colors = new int[BotScript.MAX_BOTS];
    private static final boolean[] compact = new boolean[BotScript.MAX_BOTS];
    private static final boolean[] notifications = new boolean[BotScript.MAX_BOTS];
    static { java.util.Arrays.fill(notifications, true); }
    private BotAppearance() {}
    public static int color(int index) { return COLORS[colors[index]]; }
    public static boolean compact(int index) { return compact[index]; }
    public static boolean notifications(int index) { return notifications[index]; }
    public static int colorIndex(int index) { return colors[index]; }
    public static void restore(int index, int color, boolean small, boolean notify) {
        colors[index] = Math.floorMod(color, COLORS.length);
        compact[index] = small;
        notifications[index] = notify;
    }
    public static void cycleColor(int index) { colors[index] = (colors[index] + 1) % COLORS.length; }
    public static void toggleCompact(int index) { compact[index] = !compact[index]; }
    public static void toggleNotifications(int index) { notifications[index] = !notifications[index]; }
}
