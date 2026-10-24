package swerve.bot;

import swerve.utils.client.NotificationManager;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Исполнитель поведения бота: проходит список шагов и выполняет их.
 *
 * <p>Работает в клиентском тике и ничего тяжёлого не делает: сетевые шаги —
 * это вызов одного метода {@link BotConnection} (пакет уходит в потоке
 * соединения), ожидания — отложенный индекс, а не {@code Thread.sleep}.
 * Поэтому десяток ботов с сотнями шагов не просаживает FPS.
 *
 * <p>Бюджет — {@value #STEPS_PER_TICK} шагов за тик: цепочка мгновенных шагов
 * без ожиданий не может занять кадр целиком. Цикл ограничен
 * {@link #MAX_LOOP}, вложенность — {@link #MAX_NESTING}, чтобы сломанный
 * профиль не крутился вечно внутри одного тика.
 *
 * <p>У поведения есть собственные переменные ({@code задать переменную},
 * {@code увеличить}, {@code случайное}, условия по ним). Они живут до
 * перезапуска бота и не пишутся в конфиг — это счётчики и случайные значения,
 * а не настройки.
 */
public final class BotRunner {

    private static final int STEPS_PER_TICK = 40;
    private static final int MAX_LOOP = 100_000;
    private static final int MAX_NESTING = 16;

    /** Кадр цикла: куда возвращаться, сколько итераций осталось и где конец. */
    private record LoopFrame(int startIndex, int remaining, int endIndex) {
    }

    private final BotProfile profile;
    private final BotLog log;
    private final BotConnection connection;
    private final ArrayDeque<LoopFrame> loops = new ArrayDeque<>();
    private final Map<String, Double> variables = new HashMap<>();
    /** Счётчики многошаговых действий (спам в чат, анти-АФК) по номеру шага. */
    private final Map<Integer, Integer> progress = new HashMap<>();

    private volatile boolean running;
    private int index;
    private long resumeAt;
    private long startedAt;
    private int executed;

    // Ожидания идут параллельно и не смешивают дедлайны: связь, разрыв, строка
    // в чате и «любое сообщение» — четыре независимых условия.
    private long waitConnectUntil;
    private long waitOfflineUntil;
    private long waitChatUntil;
    private long waitAnyUntil;
    private long waitingChatSince;
    private long waitAnySince;
    private String waitingChat;
    private String waitingChatText = "";

    public BotRunner(BotProfile profile, BotLog log) {
        this.profile = profile;
        this.log = log;
        this.connection = new BotConnection(profile, log);
    }

    public BotLog log() {
        return this.log;
    }

    public BotConnection connection() {
        return this.connection;
    }

    public boolean isRunning() {
        return this.running;
    }

    public int currentStep() {
        return this.index;
    }

    public int executedSteps() {
        return this.executed;
    }

    public long startedAt() {
        return this.startedAt;
    }

    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.index = 0;
        this.executed = 0;
        this.resumeAt = 0;
        this.startedAt = System.currentTimeMillis();
        this.loops.clear();
        this.variables.clear();
        this.progress.clear();
        clearWaits();
        this.log.ok("бот запущен: " + this.profile.getAddressLine());
        this.connection.start();
    }

    public void stop() {
        if (!this.running) {
            this.connection.stop();
            return;
        }
        this.running = false;
        this.connection.stop();
        this.loops.clear();
        this.progress.clear();
        clearWaits();
        this.log.info("бот остановлен");
    }

    private void clearWaits() {
        this.waitingChat = null;
        this.waitConnectUntil = 0;
        this.waitOfflineUntil = 0;
        this.waitChatUntil = 0;
        this.waitAnyUntil = 0;
    }

    /** Вызывается из клиентского тика. */
    public void tick() {
        if (!this.running) {
            return;
        }

        long now = System.currentTimeMillis();

        // Ожидание подключения: шаг не «съедает» тик, а висит до события.
        if (this.waitConnectUntil > 0) {
            if (this.connection.isPlaying()) {
                this.waitConnectUntil = 0;
            } else if (now > this.waitConnectUntil) {
                this.waitConnectUntil = 0;
                this.log.warn("не дождались подключения, продолжаем");
            } else {
                return;
            }
        }

        // Ожидание разрыва соединения (шаг «ждать отключения»).
        if (this.waitOfflineUntil > 0) {
            if (!this.connection.isOnline()) {
                this.waitOfflineUntil = 0;
            } else if (now > this.waitOfflineUntil) {
                this.waitOfflineUntil = 0;
                this.log.warn("соединение не закрылось само, продолжаем");
            } else {
                return;
            }
        }

        // Ожидание конкретной строки в чате.
        if (this.waitingChat != null) {
            if (chatContains(this.waitingChat)) {
                this.log.ok("дождались в чате: " + this.waitingChat);
                this.waitingChat = null;
            } else if (now > this.waitChatUntil) {
                this.log.warn("в чате не появилось «" + this.waitingChat + "», идём дальше");
                this.waitingChat = null;
            } else {
                return;
            }
        }

        // Ожидание любого нового сообщения.
        if (this.waitAnyUntil > 0) {
            String chat = this.connection.lastChat();
            if (this.connection.lastChatAt() > this.waitAnySince && chat != null && !chat.isEmpty()) {
                this.log.ok("новое сообщение: " + chat);
                this.waitAnyUntil = 0;
            } else if (now > this.waitAnyUntil) {
                this.log.warn("сообщений не было, продолжаем");
                this.waitAnyUntil = 0;
            } else {
                return;
            }
        }

        if (now < this.resumeAt) {
            return;
        }

        if (this.profile.getSteps().isEmpty()) {
            return;
        }

        int budget = STEPS_PER_TICK;
        while (budget-- > 0 && this.running) {
            if (waiting() || System.currentTimeMillis() < this.resumeAt) {
                return;
            }
            if (this.index >= this.profile.getSteps().size()) {
                if (!this.profile.isLoopForever()) {
                    this.log.ok("поведение выполнено, бот остаётся в сети");
                    this.running = false;
                    return;
                }
                this.index = 0;
                this.loops.clear();
                this.progress.clear();
                this.log.info("круг шагов завершён, повтор");
            }

            BotStep step = this.profile.getSteps().get(this.index);
            int current = this.index;
            this.index++;

            if (!step.isEnabled() || step.getType() == null) {
                continue;
            }
            if (!execute(step, current)) {
                return;
            }
            this.executed++;
        }
    }

    private boolean waiting() {
        return this.waitConnectUntil > 0 || this.waitOfflineUntil > 0
                || this.waitingChat != null || this.waitAnyUntil > 0;
    }

    private boolean chatContains(String text) {
        String chat = this.connection.lastChat();
        return chat != null && !chat.isEmpty() && text != null && !text.isEmpty()
                && this.connection.lastChatAt() > this.waitingChatSince
                && chat.toLowerCase().contains(text.toLowerCase());
    }

    /**
     * Выполняет шаг.
     *
     * @return {@code false}, если дальше в этом тике идти нельзя (ожидание/стоп)
     */
    private boolean execute(BotStep step, int stepIndex) {
        BotStepType type = step.getType();
        long now = System.currentTimeMillis();

        switch (type) {
            case CONNECT -> {
                if (!this.connection.isOnline()) {
                    this.log.info("подключаюсь к " + this.profile.getAddressLine());
                    this.connection.start();
                }
            }
            case WAIT_ONLINE -> {
                if (!this.connection.isPlaying()) {
                    this.waitConnectUntil = now + (long) (Math.max(1.0, step.number(0)) * 1000.0);
                    this.log.info("ждём подключения до " + step.get(0) + " с");
                    return false;
                }
            }
            case WAIT_DISCONNECT -> {
                if (this.connection.isOnline()) {
                    this.waitOfflineUntil = now + (long) (Math.max(1.0, step.number(0)) * 1000.0);
                    this.log.info("ждём отключения до " + step.get(0) + " с");
                    return false;
                }
            }
            case SET_NICKNAME -> {
                this.profile.setNickname(step.get(0));
                BotStore.markDirty();
                this.log.info("ник профиля: " + this.profile.getNickname()
                        + " (применится при следующем входе)");
            }
            case RECONNECT -> this.connection.reconnect();
            case DISCONNECT -> {
                this.connection.stop();
                this.log.info("отключились по шагу");
            }
            case WAIT -> {
                this.resumeAt = now + (long) (Math.max(0.0, step.number(0)) * 1000.0);
                return false;
            }
            case WAIT_RANDOM -> {
                double min = Math.min(step.number(0), step.number(1));
                double max = Math.max(step.number(0), step.number(1));
                double seconds = min + Math.random() * Math.max(0.0, max - min);
                this.resumeAt = now + (long) (seconds * 1000.0);
                return false;
            }
            case LOGIN -> this.connection.command("login " + stripCommand(step.get(0), "login"));
            case REGISTER -> this.connection.command("reg " + stripCommand(step.get(0), "reg")
                    + " " + stripCommand(step.get(1), "reg"));
            case AUTO_LOGIN -> this.connection.command("login " + password());
            case AUTO_REGISTER -> this.connection.command("reg " + password() + " " + password());
            case CUSTOM_COMMAND -> this.connection.command(step.get(0));
            case CHANGE_PASSWORD -> this.connection.command("changepassword "
                    + step.get(0) + " " + step.get(1));
            case CHAT -> this.connection.chat(step.get(0));
            case WAIT_CHAT -> {
                this.waitingChatText = step.get(0);
                if (chatContains(this.waitingChatText)) {
                    this.log.ok("строка уже есть в чате: " + this.waitingChatText);
                } else {
                    this.waitingChat = this.waitingChatText;
                    this.waitingChatSince = now;
                    this.waitChatUntil = now + (long) (Math.max(1.0, step.number(1)) * 1000.0);
                    this.log.info("ждём в чате: " + this.waitingChatText);
                    return false;
                }
            }
            case WAIT_CHAT_ANY -> {
                this.waitAnySince = now;
                this.waitAnyUntil = now + (long) (Math.max(1.0, step.number(0)) * 1000.0);
                this.log.info("ждём любое сообщение до " + step.get(0) + " с");
                return false;
            }
            case REPLY_CHAT -> {
                if (chatContains(step.get(0))) {
                    this.connection.chat(step.get(1));
                }
            }
            case SPAM_CHAT -> {
                int times = Math.max(1, step.integer(1));
                int done = this.progress.getOrDefault(stepIndex, 0);
                if (done < times) {
                    this.connection.chat(step.get(0));
                    this.progress.put(stepIndex, done + 1);
                    this.resumeAt = now + (long) (Math.max(0.2, step.number(2)) * 1000.0);
                    return false;
                }
                this.progress.remove(stepIndex);
            }
            case LOOK -> this.connection.look((float) step.number(0), (float) step.number(1));
            case LOOK_AT -> this.connection.lookAt(step.number(0), step.number(1), step.number(2));
            case LOOK_YAW -> this.connection.look((float) step.number(0), this.connection.pitch());
            case LOOK_RANDOM -> {
                float range = (float) Math.max(1.0, step.number(0));
                this.connection.look((float) (Math.random() * 2.0 - 1.0) * range,
                        (float) (Math.random() * 40.0 - 20.0));
            }
            case MOVE -> this.connection.moveTo(step.number(0), step.number(1), step.number(2));
            case SNEAK -> this.connection.setSneaking(isOn(step.get(0)));
            case SPRINT -> this.connection.setSprinting(isOn(step.get(0)));
            case JUMP -> this.connection.jump();
            case SWING -> this.connection.swing();
            case USE_ITEM -> this.connection.useItem();
            case HELD_SLOT -> this.connection.heldSlot(step.integer(0));
            case RESPAWN -> this.connection.respawn();
            case SWAP_OFFHAND -> this.connection.swapOffhand();
            case STOP_MOVEMENT -> this.connection.stopMovement();
            case ANTI_AFK -> {
                int movements = Math.max(1, step.integer(0));
                int done = this.progress.getOrDefault(stepIndex, 0);
                if (done < movements) {
                    this.connection.jump();
                    this.connection.swing();
                    // Приседание чередуется, чтобы поза менялась заметно.
                    if (done % 2 == 0) {
                        this.connection.setSneaking(true);
                    } else {
                        this.connection.setSneaking(false);
                    }
                    this.progress.put(stepIndex, done + 1);
                    this.resumeAt = now + 500L;
                    return false;
                }
                this.progress.remove(stepIndex);
            }
            case OPEN_INVENTORY -> this.connection.openInventory();
            case CLOSE_INVENTORY -> this.connection.closeInventory();
            case CLICK_SLOT -> this.connection.clickSlot(step.integer(0), step.integer(1), step.integer(2));
            case MOVE_SLOT -> this.connection.moveSlot(step.integer(0), step.integer(1));
            case DROP_SLOT -> this.connection.dropSlot(step.integer(0));
            case EQUIP_ARMOR -> this.connection.equipArmor(step.integer(0), step.get(1));
            case QUICK_MOVE -> this.connection.quickMove(step.integer(0));
            case SWAP_WITH_HOTBAR -> this.connection.swapWithHotbar(step.integer(0), step.integer(1));
            case THROW_STACK -> this.connection.throwStack(step.integer(0));
            case LOOP -> {
                int times = step.integer(0);
                if (times == 0) {
                    times = -1;
                }
                if (times > MAX_LOOP) {
                    times = MAX_LOOP;
                }
                if (this.loops.size() >= MAX_NESTING) {
                    this.log.warn("слишком глубокая вложенность циклов, шаг пропущен");
                } else {
                    this.loops.push(new LoopFrame(stepIndex + 1, times, findBlockEnd(stepIndex)));
                }
            }
            case LOOP_END -> {
                if (this.loops.isEmpty()) {
                    return true;
                }
                LoopFrame frame = this.loops.peek();
                int remaining = frame.remaining() < 0 ? -1 : frame.remaining() - 1;
                this.loops.pop();
                if (remaining != 0) {
                    this.loops.push(new LoopFrame(frame.startIndex(), remaining, frame.endIndex()));
                    this.index = frame.startIndex();
                }
            }
            case BREAK_LOOP -> {
                if (!this.loops.isEmpty()) {
                    LoopFrame frame = this.loops.pop();
                    this.index = frame.endIndex() >= 0 ? frame.endIndex() + 1
                            : this.profile.getSteps().size();
                    this.log.info("вышли из повтора");
                }
            }
            case SET_VAR -> {
                setVariable(step.get(0), number(step.get(1)));
                this.log.info("переменная " + step.get(0) + " = " + format(variable(step.get(0))));
            }
            case ADD_VAR -> {
                setVariable(step.get(0), variable(step.get(0)) + number(step.get(1)));
                this.log.info("переменная " + step.get(0) + " = " + format(variable(step.get(0))));
            }
            case RANDOM_VAR -> {
                double min = Math.min(step.number(1), step.number(2));
                double max = Math.max(step.number(1), step.number(2));
                double value = min + Math.random() * Math.max(0.0, max - min);
                setVariable(step.get(0), Math.round(value));
                this.log.info("переменная " + step.get(0) + " = " + format(variable(step.get(0))));
            }
            case RESET_VARS -> {
                this.variables.clear();
                this.log.info("переменные сброшены");
            }
            case IF_VAR_EQ -> {
                if (Math.abs(variable(step.get(0)) - number(step.get(1))) > 1.0E-6) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_VAR_BELOW -> {
                if (!(variable(step.get(0)) < number(step.get(1)))) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case CHANCE -> {
                if (Math.random() * 100.0 >= step.number(0)) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_CHAT -> {
                if (!chatContains(step.get(0))) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_ONLINE -> {
                if (!this.connection.isOnline()) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_HEALTH_BELOW -> {
                if (!(this.connection.health() < (float) step.number(0))) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_FOOD_BELOW -> {
                if (!(this.connection.food() < step.integer(0))) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_DEAD -> {
                if (this.connection.health() > 0.0f) {
                    this.index = skipBlock(stepIndex);
                }
            }
            case IF_END -> {
                // конец условия: просто идём дальше
            }
            case STOP -> {
                this.log.ok("остановка по шагу");
                this.running = false;
                return false;
            }
            case NOTIFY -> NotificationManager.info(step.get(0), "Бот " + this.profile.getName());
            case LOG -> this.log.info(step.get(0));
            case LOG_STATUS -> this.log.info(String.format(
                    "позиция %.1f %.1f %.1f · здоровье %.1f · еда %d · состояние %s",
                    this.connection.x(), this.connection.y(), this.connection.z(),
                    this.connection.health(), this.connection.food(),
                    this.connection.state().displayName()));
            case CLEAR_LOG -> this.log.clear();
            default -> {
                // новых шагов без обработчика быть не должно
            }
        }

        float delay = this.profile.getStepDelay();
        if (delay > 0.0f) {
            this.resumeAt = Math.max(this.resumeAt, now + (long) (delay * 1000.0f));
        }
        return true;
    }

    /** Индекс парного «конец блока» для шага-открывашки, -1 если пары нет. */
    private int findBlockEnd(int openerIndex) {
        int depth = 0;
        int size = this.profile.getSteps().size();
        for (int i = openerIndex + 1; i < size; i++) {
            BotStepType type = this.profile.getSteps().get(i).getType();
            if (type == null) {
                continue;
            }
            if (type.opensBlock()) {
                depth++;
            } else if (type.closesBlock()) {
                if (depth == 0) {
                    return i;
                }
                depth--;
            }
        }
        return -1;
    }

    /** Индекс сразу после «конца условия». */
    private int skipBlock(int openerIndex) {
        int end = findBlockEnd(openerIndex);
        return end < 0 ? this.profile.getSteps().size() : end + 1;
    }

    private String password() {
        String value = this.profile.getPassword();
        return value == null || value.isBlank() ? "пароль" : value;
    }

    /** Принимает и «пароль», и «/login пароль» — обе формы одинаково понятны. */
    private static String stripCommand(String value, String command) {
        String text = value == null ? "" : value.trim();
        while (text.startsWith("/")) {
            text = text.substring(1).trim();
        }
        if (text.regionMatches(true, 0, command + " ", 0, command.length() + 1)) {
            text = text.substring(command.length() + 1).trim();
        }
        return text;
    }

    private double variable(String name) {
        String key = name == null ? "" : name.trim();
        return this.variables.getOrDefault(key, 0.0);
    }

    private void setVariable(String name, double value) {
        String key = name == null ? "" : name.trim();
        if (!key.isEmpty()) {
            this.variables.put(key, value);
        }
    }

    private static double number(String text) {
        String value = text == null ? "" : text.trim().replace(',', '.');
        if (value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private static String format(double value) {
        return Math.abs(value - Math.rint(value)) < 1.0E-6
                ? String.valueOf((long) Math.rint(value))
                : String.format("%.2f", value);
    }

    private static boolean isOn(String value) {
        String text = value == null ? "" : value.trim().toLowerCase();
        return text.equals("вкл") || text.equals("on") || text.equals("да") || text.equals("true");
    }
}
