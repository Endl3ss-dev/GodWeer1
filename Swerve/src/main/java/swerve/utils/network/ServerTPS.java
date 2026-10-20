package swerve.utils.network;

import net.minecraft.util.Mth;

/**
 * Трекер TPS сервера: считает частоту пакетов времени (ClientboundSetTimePacket).
 *
 * <p>Адаптировано из Private/src/java/org/privates/client/utils/server/ServerTPS.java:
 * вместо событийной шины обновление вызывается напрямую из
 * MixinClientPacketListener.handleSetTime.
 */
public final class ServerTPS {

    /** Максимальный TPS сервера Minecraft. */
    private static final float MAX_TPS = 20.0F;

    /** Задержка больше секунды — это не лаг, а пауза (мир, загрузка) — просто ресинхронизация. */
    private static final long RESYNC_NS = 1_000_000_000L;

    private static float tps = MAX_TPS;
    private static long timestamp;

    private ServerTPS() {
    }

    /** Вызывается из миксина при получении пакета времени. */
    public static void onTimePacket() {
        long now = System.nanoTime();
        long delay = now - timestamp;
        timestamp = now;
        if (delay <= 0L || delay > RESYNC_NS) {
            // Первый пакет или пауза между пакетами — не портим оценку.
            return;
        }
        float raw = MAX_TPS * (1.0E9F / delay);
        tps = Mth.clamp(Math.round(raw * 10.0F) / 10.0F, 0.0F, MAX_TPS);
    }

    /** Текущий TPS сервера (0..20). */
    public static float tps() {
        return tps;
    }
}
