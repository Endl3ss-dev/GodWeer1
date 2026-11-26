package antileak.base.client.modules.impl.combat.components;

import antileak.base.client.modules.settings.Setting;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

/**
 * Пачки ударов 1.8 для AttackAura.
 *
 * <p>Адаптировано из SkyCore 26.2:
 * {@code sk 26.2/src/main/java/net/skycore/client/module/combat/AuraBurst.java} — режим
 * {@code 1.8 NoDelay} (в SkyCore {@code AuraClicks.NO_DELAY}). Логика донора — MotherHack
 * {@code Aura#attack} ({@code attacksPerTick}, {@code antiKick}, {@code antiKickDelay}).
 * В Pouch режим назван просто {@code 1.8}.
 *
 * <p>В отличие от интервального темпа ({@code 1.9}, ванильная перезарядка) здесь ванильный
 * кулдаун оружия и окно крита не проверяются вовсе: пачка ударов уходит одним пакетом, а
 * {@code Анти-кик} разносит пачки по времени. Пачка не чаще одного раза за игровой тик,
 * даже если логика вызвана и из тика, и из подшага кадра.
 *
 * <p>Число ударов в пачке выбирается из {@code Мин./Макс. ударов} после каждой пачки —
 * ровно так же, как интервальный режим выбирал CPS. {@code Мин./Макс. CPS} задают целевой
 * темп: интервал до следующей пачки равен {@code удары × 1000 / CPS}, где CPS тоже
 * выбирается из диапазона. {@code Анти-кик} работает как нижняя граница интервала —
 * он может только замедлить пачки, но не ускорить их.
 *
 * <p>Состояние не выделяет память в горячем пути: вычисления на примитивах и наносекундах.
 */
public final class AuraBurst {

    /** Прежний темп Pouch: ванильная перезарядка оружия. */
    public static final String LEGACY = "1.9";
    /** Пачки ударов 1.8 (в SkyCore — {@code 1.8 NoDelay}). */
    public static final String BURST_18 = "1.8";

    /** MotherHack позволяет 500; такие пачки кикают сервер и подвешивают клиент. */
    public static final int MAX_ATTACKS_PER_TICK = 50;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    public final ModeSetting mode = new ModeSetting("Режим кликов", LEGACY, LEGACY, BURST_18);
    public final FloatSetting minAttacks = new FloatSetting("Мин. ударов", 5f, 1f, MAX_ATTACKS_PER_TICK, 1f);
    public final FloatSetting maxAttacks = new FloatSetting("Макс. ударов", 5f, 1f, MAX_ATTACKS_PER_TICK, 1f);
    public final FloatSetting minCps = new FloatSetting("Мин. CPS", 20f, 1f, 100f, 1f);
    public final FloatSetting maxCps = new FloatSetting("Макс. CPS", 40f, 1f, 100f, 1f);
    public final BooleanSetting antiKick = new BooleanSetting("Анти-кик", true);
    public final FloatSetting antiKickDelay = new FloatSetting("Задержка анти-кика", 50f, 5f, 200f, 5f);
    public final Setting[] settings = {
            mode, minAttacks, maxAttacks, minCps, maxCps, antiKick, antiKickDelay
    };

    private final RandomGenerator random;
    private int burstSize;
    private boolean scheduled;
    private long nextBurst;
    private int lastBurstTick = Integer.MIN_VALUE;

    public AuraBurst() {
        this(ThreadLocalRandom.current());
    }

    AuraBurst(RandomGenerator random) {
        this.random = random;
        minAttacks.visible(this::usesBurst);
        maxAttacks.visible(this::usesBurst);
        minCps.visible(this::usesBurst);
        maxCps.visible(this::usesBurst);
        antiKick.visible(this::usesBurst);
        antiKickDelay.visible(() -> usesBurst() && antiKick.isState());
    }

    /** Включены пачки 1.8: перезарядка оружия и криты ауры не проверяются. */
    public boolean usesBurst() {
        return mode.is(BURST_18);
    }

    /**
     * Сколько ударов отправить этой пачкой: случайное значение из {@code Мин./Макс. ударов},
     * включительно, пересчитывается после каждой пачки — как CPS в интервальном режиме.
     */
    public int attacks() {
        if (burstSize <= 0) {
            burstSize = sampleBurstSize();
        }
        return burstSize;
    }

    private int sampleBurstSize() {
        return Math.max(1, randomBetween(Math.round(minAttacks.get()), Math.round(maxAttacks.get())));
    }

    /** Целевой темп в ударах в секунду: случайное значение из {@code Мин./Макс. CPS}. */
    private int sampleCps() {
        return Math.max(1, randomBetween(Math.round(minCps.get()), Math.round(maxCps.get())));
    }

    /**
     * Готовность к следующей пачке: не чаще одного раза за игровой тик и не раньше интервала,
     * рассчитанного по CPS и окну анти-кика.
     *
     * @param now  текущее время в наносекундах
     * @param tick номер игрового тика (счётчик вызовов логики ауры на тике)
     */
    public boolean ready(long now, int tick) {
        if (!usesBurst()) {
            return false;
        }
        if (tick == lastBurstTick) {
            return false;
        }
        return !scheduled || now - nextBurst >= 0;
    }

    /**
     * Фиксирует отправленную пачку, выбирает размер следующей и открывает окно анти-кика.
     *
     * @param sent сколько ударов реально ушло в этой пачке (влияет на интервал до следующей)
     */
    public void recordBurst(long now, int tick, int sent) {
        sent = Math.max(1, sent);
        lastBurstTick = tick;
        burstSize = sampleBurstSize();

        // Целевой темп: пачка из N ударов повторяется так, чтобы вышло выбранное CPS.
        long window = Math.round(NANOS_PER_SECOND * (double) sent / sampleCps());
        if (antiKick.isState()) {
            // Анти-кик — нижняя граница интервала: он только замедляет, никогда не ускоряет.
            window = Math.max(window, Math.round(antiKickDelay.get() * 1_000_000f));
        }

        if (window > 0L) {
            scheduled = true;
            nextBurst = now + window;
        } else {
            scheduled = false;
        }
    }

    /** Сброс при выключении модуля, включении, смене мира и загрузке конфигурации. */
    public void reset() {
        burstSize = 0;
        scheduled = false;
        lastBurstTick = Integer.MIN_VALUE;
    }

    /** Текущий размер следующей пачки; 0 — ещё не выбран. Используется проверками. */
    int pendingBurstSize() {
        return burstSize;
    }

    private int randomBetween(int a, int b) {
        int low = Math.min(a, b);
        int high = Math.max(a, b);
        return low == high ? low : random.nextInt(low, high + 1);
    }
}
