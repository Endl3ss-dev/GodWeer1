package swerve.utils.network;

import net.minecraft.util.Mth;

/**
 * Трекер TPS сервера: считает частоту пакетов времени (ClientboundSetTimePacket).
 *
 * <p>Адаптировано из Private/src/java/org/privates/client/utils/server/ServerTPS.java:
 * вместо событийной шины обновление вызывается напрямую из
 * MixinClientPacketListener.handleSetTime.
 *
 * <p>Частота пакетов — РАЗ В СЕКУНДУ, не каждый тик: в 1.21.4
 * {@code MinecraftServer.tickChildren} вызывает {@code synchronizeTime} под условием
 * {@code tickCount % 20 == 0} (проверено разбором байткода). Отсюда и множитель
 * MAX_TPS в формуле: один пакет = двадцать тиков. Значит измеримый диапазон —
 * 1..20 TPS, а порог «это пауза, а не лаг» обязан быть НЕ МЕНЬШЕ двадцати периодов
 * пакета, иначе отбрасывается ровно тот интервал, ради которого трекер и нужен:
 * сервер на 10 TPS присылает пакет через 2 с, и с порогом в 1 с оценка навсегда
 * залипала бы на 20 (коррекция темпа удара в KillAura не срабатывала бы никогда).
 */
public final class ServerTPS {

    /** Максимальный TPS сервера Minecraft. */
    private static final float MAX_TPS = 20.0F;

    /** Штатный период пакета времени: 20 тиков при 20 TPS = одна секунда. */
    private static final long PACKET_PERIOD_NS = 1_000_000_000L;

    /**
     * Задержка больше этого порога — не лаг, а пауза (меню одиночной игры, загрузка
     * мира, телепорт между измерениями): такой отсчёт просто забывается. Двадцать
     * периодов пакета = 1 TPS; медленнее сервер «лагает» уже не куда — мир стоит,
     * и темп удара надо держать обычный.
     */
    private static final long RESYNC_NS = PACKET_PERIOD_NS * (long) MAX_TPS;

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
        // Один пакет времени = двадцать тиков, значит tps = MAX_TPS * (период / задержка).
        float raw = MAX_TPS * ((float) PACKET_PERIOD_NS / delay);
        tps = Mth.clamp(Math.round(raw * 10.0F) / 10.0F, 0.0F, MAX_TPS);
    }

    /** Текущий TPS сервера (0..20). */
    public static float tps() {
        return tps;
    }
}
