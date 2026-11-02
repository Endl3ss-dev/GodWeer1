package swerve.script;

import swerve.Swerve;
import swerve.utils.client.NotificationManager;
import swerve.utils.input.KeyBoardUtils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Интерпретатор скриптов.
 *
 * <p>Исполнение не блокирует игру: каждый тик функция получает бюджет блоков
 * ({@value #BUDGET_PER_TICK}), а {@code ждать} просто откладывает продолжение до
 * нужного времени. Поэтому «повторять всегда» с задержкой внутри — обычный
 * скрипт, а не зависший клиент.
 *
 * <p>Стек кадров устроен как в Scratch: кадр {@code exec} — текущая позиция в
 * стеке блоков, кадр {@code loop} — счётчик цикла, который заново входит в тело.
 * Вложенные C-блоки дают вложенные кадры, а {@code остановить скрипт} очищает
 * весь стек функции.
 *
 * <p>Триггеры: «каждый тик», «когда заходишь в мир» (смена измерения или
 * подключение к серверу) и бинд функции. Пока открыт любой экран, бинды не
 * срабатывают — иначе набор текста в редакторе запускал бы скрипты.
 */
public final class ScriptEngine {

    private static final int BUDGET_PER_TICK = 400;
    private static final int MAX_CALL_DEPTH = 8;

    private static final List<Run> RUNS = new ArrayList<>();
    private static final Map<ScriptFunction, Run> PRIMARY = new IdentityHashMap<>();
    private static final Map<ScriptFunction, Boolean> KEY_DOWN = new IdentityHashMap<>();

    private static Level lastLevel;
    private static boolean initialized;

    private ScriptEngine() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTickEvents.END_CLIENT_TICK.register(ScriptEngine::tick);
    }

    // ---- управление запуском

    /** Ручной запуск: перезапускает функцию, если она уже работала. */
    public static void run(ScriptFunction function) {
        if (function == null || function.getHead() == null) {
            return;
        }
        stop(function);
        start(function, 0);
    }

    /** Запуск по имени — точка входа для других модулей клиента. */
    public static boolean runByName(String name) {
        ScriptFunction function = ScriptStore.find(name);
        if (function == null || function.getHead() == null) {
            return false;
        }
        run(function);
        return true;
    }

    public static void stop(ScriptFunction function) {
        Run run = PRIMARY.remove(function);
        if (run != null) {
            run.finished = true;
        }
        for (int i = RUNS.size() - 1; i >= 0; i--) {
            if (RUNS.get(i).function == function) {
                RUNS.get(i).finished = true;
                RUNS.remove(i);
            }
        }
    }

    public static void stopAll() {
        for (int i = 0, size = RUNS.size(); i < size; i++) {
            RUNS.get(i).finished = true;
        }
        RUNS.clear();
        PRIMARY.clear();
    }

    public static boolean isRunning(ScriptFunction function) {
        Run run = PRIMARY.get(function);
        return run != null && !run.finished;
    }

    public static int activeCount() {
        return RUNS.size();
    }

    private static Run start(ScriptFunction function, int depth) {
        Run run = new Run(function, depth);
        RUNS.add(run);
        PRIMARY.put(function, run);
        return run;
    }

    /** Вызов из блока «вызвать функцию»: отдельный запуск с защитой от рекурсии. */
    private static void call(String name, int depth) {
        if (depth > MAX_CALL_DEPTH) {
            Swerve.LOGGER.warn("Script call depth limit reached at '{}'", name);
            return;
        }
        ScriptFunction function = ScriptStore.find(name);
        if (function == null || function.getHead() == null) {
            return;
        }
        // Функция уже работает — второй параллельный экземпляр только раздувал бы стек.
        if (PRIMARY.containsKey(function)) {
            return;
        }
        Run child = start(function, depth);
        // Вызов выполняется сразу, а не следующим тиком: иначе «вызвать» в цикле
        // с задержкой терял бы порядок действий.
        step(child);
        if (child.finished) {
            RUNS.remove(child);
        }
    }

    // ---- тик

    private static void tick(Minecraft mc) {
        if (mc == null) {
            return;
        }

        if (mc.level == null) {
            lastLevel = null;
        } else if (mc.level != lastLevel) {
            lastLevel = mc.level;
            startJoinFunctions();
        }

        boolean inputAllowed = mc.screen == null;
        long now = System.currentTimeMillis();

        List<ScriptFunction> functions = ScriptStore.getFunctions();
        for (int i = 0, size = functions.size(); i < size; i++) {
            ScriptFunction function = functions.get(i);
            if (function.getHead() == null) {
                continue;
            }

            ScriptTrigger trigger = function.getTrigger();
            if (trigger == ScriptTrigger.TICK && !PRIMARY.containsKey(function)) {
                start(function, 0);
            }

            int key = function.getKey();
            if (key == -1) {
                KEY_DOWN.remove(function);
                continue;
            }

            boolean held = inputAllowed && KeyBoardUtils.isBindHeld(key);
            Boolean previous = KEY_DOWN.get(function);
            if (held && !Boolean.TRUE.equals(previous)) {
                run(function);
            }
            KEY_DOWN.put(function, held);
        }

        for (int i = RUNS.size() - 1; i >= 0; i--) {
            Run run = RUNS.get(i);
            if (run.finished) {
                RUNS.remove(i);
                continue;
            }
            if (now < run.resumeAt) {
                continue;
            }
            step(run);
            if (run.finished) {
                if (PRIMARY.get(run.function) == run) {
                    PRIMARY.remove(run.function);
                }
                RUNS.remove(i);
            }
        }
    }

    private static void startJoinFunctions() {
        List<ScriptFunction> functions = ScriptStore.getFunctions();
        for (int i = 0, size = functions.size(); i < size; i++) {
            ScriptFunction function = functions.get(i);
            if (function.getHead() != null && function.getTrigger() == ScriptTrigger.JOIN) {
                run(function);
            }
        }
    }

    // ---- интерпретация

    private static void step(Run run) {
        if (run.finished) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now < run.resumeAt) {
            return;
        }

        int budget = BUDGET_PER_TICK;

        while (budget-- > 0) {
            if (run.finished) {
                return;
            }

            Frame top = run.stack.peek();
            if (top == null) {
                finish(run);
                return;
            }

            if (top.loop) {
                if (top.remaining == 0) {
                    run.stack.pop();
                    continue;
                }
                if (top.remaining > 0) {
                    top.remaining--;
                }
                run.stack.push(Frame.exec(top.body));
                continue;
            }

            ScriptBlock block = top.cursor;
            if (block == null) {
                run.stack.pop();
                continue;
            }
            top.cursor = block.getNext();

            BlockType type = block.getType();
            if (type == null) {
                continue;
            }

            try {
                switch (type) {
                    case WAIT -> {
                        double seconds = ScriptActions.number(block, 0);
                        long millis = (long) (Math.max(0.0, Math.min(86_400.0, seconds)) * 1000.0);
                        if (millis > 0) {
                            run.resumeAt = now + millis;
                            return;
                        }
                    }
                    case REPEAT -> {
                        int times = (int) Math.max(0.0, Math.min(100_000.0, ScriptActions.number(block, 0)));
                        if (times > 0 && block.getInner() != null) {
                            run.stack.push(Frame.loop(block, times));
                        }
                    }
                    case FOREVER -> {
                        if (block.getInner() != null) {
                            run.stack.push(Frame.loop(block, -1));
                        }
                    }
                    case STOP -> {
                        finish(run);
                        return;
                    }
                    case CALL_FUNCTION -> call(ScriptActions.text(block, 0), run.depth + 1);
                    default -> {
                        BlockType triggerBlock = type;
                        if (triggerBlock.trigger() != null) {
                            // Шляпа — только метка триггера, действий у неё нет.
                            continue;
                        }
                        if (isCondition(type)) {
                            if (block.getInner() != null && ScriptActions.condition(block)) {
                                run.stack.push(Frame.exec(block.getInner()));
                            }
                        } else {
                            ScriptActions.apply(block);
                        }
                    }
                }
            } catch (Exception exception) {
                Swerve.LOGGER.error("Script '{}' failed on block {}", run.function.getName(), type.id(), exception);
                NotificationManager.error("Скрипт", run.function.getName() + ": ошибка блока " + type.id());
                finish(run);
                return;
            }
        }
    }

    private static boolean isCondition(BlockType type) {
        return switch (type) {
            case IF_MODULE, IF_MODULE_OFF, IF_HEALTH, IF_FOOD, IF_KEY, IF_VAR, IF_WORLD, IF_CHANCE -> true;
            default -> false;
        };
    }

    private static void finish(Run run) {
        run.finished = true;
        run.stack.clear();
        if (PRIMARY.get(run.function) == run) {
            PRIMARY.remove(run.function);
        }
    }

    private static final class Run {

        final ScriptFunction function;
        final int depth;
        final ArrayDeque<Frame> stack = new ArrayDeque<>();

        long resumeAt;
        boolean finished;

        Run(ScriptFunction function, int depth) {
            this.function = function;
            this.depth = depth;
            if (function.getHead() != null) {
                this.stack.push(Frame.exec(function.getHead()));
            }
        }
    }

    private static final class Frame {

        ScriptBlock cursor;
        ScriptBlock body;
        int remaining;
        boolean loop;

        static Frame exec(ScriptBlock cursor) {
            Frame frame = new Frame();
            frame.cursor = cursor;
            return frame;
        }

        static Frame loop(ScriptBlock header, int remaining) {
            Frame frame = new Frame();
            frame.loop = true;
            frame.body = header.getInner();
            frame.remaining = remaining;
            frame.cursor = null;
            return frame;
        }
    }
}
