package tech.lightclient.scripting.api;

import tech.lightclient.ui.hud.NotificationsOverlay;

/** Corner pop-ups, mirroring the {@code notify(...)} docs. */
public final class ScriptNotify {

    /** Notification flavours available to scripts. */
    public enum Kind {
        /** Ordinary, the default. */
        OK,
        /** A warning. */
        WARN,
        /** Something failed. */
        FAIL,
        /** Just make it stand out. */
        ACCENT;

        NotificationsOverlay.Kind toClient() {
            return switch (this) {
                case WARN -> NotificationsOverlay.Kind.COOLDOWN;
                case FAIL -> NotificationsOverlay.Kind.OFF;
                case ACCENT -> NotificationsOverlay.Kind.INFORMATION;
                default -> NotificationsOverlay.Kind.INFO;
            };
        }
    }

    public static void push(Object message, Kind kind) {
        String text = message instanceof ScriptText st ? st.string() : String.valueOf(message);
        Kind k = kind == null ? Kind.OK : kind;
        net.minecraft.client.MinecraftClient.getInstance().execute(() -> {
            try {
                NotificationsOverlay.push(text, 1800, k.toClient());
            } catch (Throwable ignored) {
            }
        });
    }

    private ScriptNotify() {}
}
