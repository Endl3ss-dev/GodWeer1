package swerve.utils.client;

import swerve.modules.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Очередь уведомлений: хранилище, пул объектов, анти-спам по одинаковому тексту и
// отсечение по времени жизни. Анимациями занимается рендер (модуль Notifications),
// как и в исходном клиенте (Private: NotificationManager — просто список).
public final class NotificationManager {

    public static final int MAX_VISIBLE = 6;

    private static final long INFO_DURATION_MS = 2200L;
    private static final long WARNING_DURATION_MS = 3200L;
    private static final long ERROR_DURATION_MS = 4200L;

    private static final List<Notification> VISIBLE = new ArrayList<>(MAX_VISIBLE);
    private static final List<Notification> VIEW = Collections.unmodifiableList(VISIBLE);
    private static final List<Notification> POOL = new ArrayList<>(MAX_VISIBLE);

    private static int revision;
    private static long lastArrivalMs;

    private NotificationManager() {
    }

    public static List<Notification> getVisible() {
        return VIEW;
    }

    // Растёт только на действительно новом уведомлении: по нему рендер понимает, когда играть звук.
    public static int getRevision() {
        return revision;
    }

    public static long getLastArrivalMs() {
        return lastArrivalMs;
    }

    public static void info(String text, String highlight) {
        send(text, highlight, Notification.Type.INFO);
    }

    public static void success(String text, String highlight) {
        send(text, highlight, Notification.Type.SUCCESS);
    }

    public static void warning(String text, String highlight) {
        send(text, highlight, Notification.Type.WARNING);
    }

    public static void error(String text, String highlight) {
        send(text, highlight, Notification.Type.ERROR);
    }

    public static void toggle(Module module, boolean enabled) {
        if (module == null || !module.notifiesOnToggle()) {
            return;
        }
        send(module.getName(), enabled ? "включен" : "выключен", Notification.Type.MODULE, enabled);
    }

    public static void send(String text, String highlight, Notification.Type type) {
        send(text, highlight, type, true);
    }

    public static void send(String text, String highlight, Notification.Type type, boolean positive) {
        String safeText = text == null ? "" : text;
        String safeHighlight = highlight == null ? "" : highlight;
        long now = System.currentTimeMillis();
        long duration = duration(type);

        for (int i = 0, size = VISIBLE.size(); i < size; i++) {
            Notification existing = VISIBLE.get(i);
            if (existing.matches(safeText, safeHighlight)) {
                existing.refresh(now, duration, positive);
                lastArrivalMs = now;
                return;
            }
        }

        Notification notification = obtain();
        notification.apply(safeText, safeHighlight, type, duration, now, positive);
        VISIBLE.add(notification);
        while (VISIBLE.size() > MAX_VISIBLE) {
            release(VISIBLE.remove(0));
        }

        lastArrivalMs = now;
        revision++;
    }

    // Вызывается рендером один раз за кадр: помечает истёкшие и освобождает отыгравшие.
    public static void update() {
        long now = System.currentTimeMillis();
        for (int i = VISIBLE.size() - 1; i >= 0; i--) {
            Notification notification = VISIBLE.get(i);
            if (notification.isExpired(now)) {
                notification.markReleased();
            }
            if (notification.isReleased()) {
                VISIBLE.remove(i);
                release(notification);
            }
        }
    }

    public static void clear() {
        for (int i = VISIBLE.size() - 1; i >= 0; i--) {
            release(VISIBLE.remove(i));
        }
    }

    private static Notification obtain() {
        int poolSize = POOL.size();
        return poolSize > 0 ? POOL.remove(poolSize - 1) : new Notification();
    }

    private static void release(Notification notification) {
        notification.reset();
        if (POOL.size() < MAX_VISIBLE) {
            POOL.add(notification);
        }
    }

    private static long duration(Notification.Type type) {
        return switch (type) {
            case ERROR -> ERROR_DURATION_MS;
            case WARNING -> WARNING_DURATION_MS;
            default -> INFO_DURATION_MS;
        };
    }
}
