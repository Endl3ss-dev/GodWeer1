package net.skycore.client.util;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

/** Reads the local presence snapshot only; no remote counter is queried. */
public final class ReadBodyHelper {
    private ReadBodyHelper() {}

    public static boolean isAllowedUser(String username) {
        return username != null && !username.isBlank();
    }

    public static int getCount() {
        return Math.max(0, ShouldShowSkycoreTagHelper.getOnlineCount());
    }

    public static void refreshIfNeeded() {}

    public static void fetchAsync(Consumer<Integer> consumer) {
        if (consumer == null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        Runnable callback = () -> consumer.accept(getCount());
        if (client == null || client.isSameThread()) {
            callback.run();
        } else {
            client.execute(callback);
        }
    }
}
