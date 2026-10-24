package swerve.bot;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Жизненный цикл ботов: кто запущен, кто остановлен и когда их тикать.
 *
 * <p>Студия и модуль не хранят состояние соединений — они просят менеджер.
 * Так профиль можно удалить, переименовать или скопировать, не задев живой
 * рантайм: связь держится по ссылке на профиль, а не по имени.
 *
 * <p>Тик идёт в {@code END_CLIENT_TICK}: раннер выполняет шаги порциями и
 * никогда не спит, поэтому все боты делят один кадр.
 */
public final class BotManager {

    private static final Map<BotProfile, BotRunner> RUNNERS = new IdentityHashMap<>();
    private static final List<BotRunner> SNAPSHOT = new ArrayList<>();
    /** Заглушка журнала для профилей без рантайма: одна на всех, чтобы не мусорить. */
    private static final BotLog EMPTY_LOG = new BotLog();
    private static boolean hooks;

    private BotManager() {
    }

    public static void init() {
        if (hooks) {
            return;
        }
        hooks = true;
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> stopAll());
    }

    /** Запускает поведение профиля. Повторный вызов ничего не делает. */
    public static BotRunner start(BotProfile profile) {
        if (profile == null) {
            return null;
        }
        BotRunner existing = RUNNERS.get(profile);
        if (existing != null) {
            if (existing.isRunning()) {
                return existing;
            }
            // Прежний запуск мог закончить шаги, но остаться в сети: закрываем
            // его соединение, иначе повторный запуск оставил бы «бота-призрака».
            existing.stop();
        }
        BotRunner runner = new BotRunner(profile, logFor(profile));
        RUNNERS.put(profile, runner);
        runner.start();
        return runner;
    }

    public static void stop(BotProfile profile) {
        if (profile == null) {
            return;
        }
        BotRunner runner = RUNNERS.get(profile);
        if (runner != null) {
            runner.stop();
        }
    }

    public static void toggle(BotProfile profile) {
        if (profile == null) {
            return;
        }
        if (isRunning(profile)) {
            stop(profile);
        } else {
            start(profile);
        }
    }

    public static BotRunner runner(BotProfile profile) {
        return profile == null ? null : RUNNERS.get(profile);
    }

    public static boolean isRunning(BotProfile profile) {
        BotRunner runner = runner(profile);
        return runner != null && runner.isRunning();
    }

    /** Бот держит соединение, даже если шаги закончились. */
    public static boolean isOnline(BotProfile profile) {
        BotRunner runner = runner(profile);
        return runner != null && runner.connection().isOnline();
    }

    public static BotConnection connection(BotProfile profile) {
        BotRunner runner = runner(profile);
        return runner == null ? null : runner.connection();
    }

    public static BotLog logFor(BotProfile profile) {
        BotRunner runner = RUNNERS.get(profile);
        return runner == null ? EMPTY_LOG : runner.log();
    }

    public static void stopAll() {
        SNAPSHOT.clear();
        SNAPSHOT.addAll(RUNNERS.values());
        for (int i = 0, size = SNAPSHOT.size(); i < size; i++) {
            SNAPSHOT.get(i).stop();
        }
        SNAPSHOT.clear();
        RUNNERS.clear();
    }

    /** Счётчик запущенных — для значка в доке. */
    public static int runningCount() {
        int count = 0;
        for (BotRunner runner : RUNNERS.values()) {
            if (runner.isRunning()) {
                count++;
            }
        }
        return count;
    }

    private static void tick() {
        if (RUNNERS.isEmpty()) {
            return;
        }
        SNAPSHOT.clear();
        SNAPSHOT.addAll(RUNNERS.values());
        for (int i = 0, size = SNAPSHOT.size(); i < size; i++) {
            BotRunner runner = SNAPSHOT.get(i);
            if (runner.isRunning()) {
                runner.tick();
            }
        }
        SNAPSHOT.clear();
    }
}
