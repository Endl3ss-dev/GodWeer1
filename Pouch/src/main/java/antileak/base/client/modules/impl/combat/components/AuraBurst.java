package antileak.base.client.modules.impl.combat.components;

import antileak.base.client.modules.settings.Setting;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

/**
 * Пачки ударов 1.8 для AttackAura.
 *
 * <p>Адаптировано из SkyCore 26.2:
 * {@code sk 26.2/src/main/java/net/skycore/client/module/combat/AuraBurst.java} — тот самый
 * режим {@code 1.8 NoDelay}, который в SkyCore назван отдельной строкой
 * ({@code AuraClicks.NO_DELAY}). Логика донора — MotherHack {@code Aura#attack}
 * ({@code attacksPerTick}, {@code antiKick}, {@code antiKickDelay}). В Pouch режим назван
 * просто {@code 1.8}.
 *
 * <p>В отличие от интервального темпа (`1.9`, ванильная перезарядка) здесь ванильный кулдаун
 * оружия и окно крита не проверяются вовсе: пачка из {@code Ударов за тик} ударов уходит
 * одним пакетом, а {@code Анти-кик} разносит пачки по времени. Пачка не чаще одного раза
 * за игровой тик, даже если логика вызвана и из тика, и из подшага кадра.
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

    public final ModeSetting mode = new ModeSetting("Режим кликов", LEGACY, LEGACY, BURST_18);
    public final FloatSetting attacksPerTick = new FloatSetting("Ударов за тик", 5f, 1f, MAX_ATTACKS_PER_TICK, 1f);
    public final BooleanSetting antiKick = new BooleanSetting("Анти-кик", true);
    public final FloatSetting antiKickDelay = new FloatSetting("Задержка анти-кика", 50f, 10f, 200f, 10f);
    public final Setting[] settings = {mode, attacksPerTick, antiKick, antiKickDelay};

    private boolean scheduled;
    private long nextBurst;
    private int lastBurstTick = Integer.MIN_VALUE;

    public AuraBurst() {
        attacksPerTick.visible(this::usesBurst);
        antiKick.visible(this::usesBurst);
        antiKickDelay.visible(() -> usesBurst() && antiKick.isState());
    }

    /** Включены пачки 1.8: перезарядка оружия и криты ауры не проверяются. */
    public boolean usesBurst() {
        return mode.is(BURST_18);
    }

    /** Сколько ударов отправить одной пачкой. */
    public int attacks() {
        return Math.round(attacksPerTick.get());
    }

    /**
     * Готовность к следующей пачке: не чаще одного раза за игровой тик и не раньше окна анти-кика.
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

    /** Фиксирует отправленную пачку и открывает окно анти-кика. */
    public void recordBurst(long now, int tick) {
        lastBurstTick = tick;
        scheduled = antiKick.isState();
        nextBurst = now + Math.round(antiKickDelay.get() * 1_000_000f);
    }

    /** Сброс при выключении модуля, включении, смене мира и загрузке конфигурации. */
    public void reset() {
        scheduled = false;
        lastBurstTick = Integer.MIN_VALUE;
    }
}
