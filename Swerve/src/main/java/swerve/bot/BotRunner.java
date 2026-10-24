package swerve.bot;

import swerve.utils.client.NotificationManager;

import java.util.ArrayDeque;

/**
 * Исполнитель поведения бота: проходит список шагов и выполняет их.
 *
 * <p>Работает в клиентском тике, но ничего тяжёлого не делает: сетевые шаги —
 * это вызов одного метода {@link BotConnection} (сам пакет уходит в потоке
 * соединения), ожидания — отложенный индекс, а не {@code Thread.sleep}.
 * Поэтому десяток ботов с сотнями шагов не просаживает FPS.
 *
 * <p>Бюджет — {@value #STEPS_PER_TICK} шагов за тик: цепочка мгновенных шагов
 * без ожиданий не может занять кадр целиком. Петля «повторить» ограничена
 * {@link #MAX_LOOP}, чтобы сломанный профиль не крутился вечно внутри одного
 * тика.
 */
public final class BotRunner {

    private static final int STEPS_PER_TICK = 40;
    private static final int MAX_LOOP = 100_000;
    private static final int MAX_NESTING = 16;

    /** Кадр цикла: к какому шагу возвращаться и сколько итераций осталось. */
    private record LoopFrame(int startIndex, int remaining) {
    }

    private final BotProfile profile;
    private final BotLog log;
    private final BotConnection connection;
    private final ArrayDeque<LoopFrame> loops = new ArrayDeque<>();

    private volatile boolean running;
    private int index;
    private long resumeAt;
    private long startedAt;
    private int executed;

    // Два независимых ожидания: связь и строка в чате. Общий дедлайн смешивал
    // их — подключение сбрасывало срок ожидания чата.
    private long waitConnectUntil;
    private long waitOfflineUntil;
    private long waitChatUntil;
    private long waitingChatSince;
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
        this.waitingChat = null;
        this.waitConnectUntil = 0;
        this.waitOfflineUntil = 0;
        this.waitChatUntil = 0;
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
        this.waitingChat = null;
        this.waitConnectUntil = 0;
        this.waitOfflineUntil = 0;
        this.waitChatUntil = 0;
        this.log.info("бот остановлен");
    }

    /** Вызывается из клиентского тика. */
    public void tick() {
        if (!this.running) {
            return;
        }

        long now = System.currentTimeMillis();

        // Ожидание подключения: шаг не «съедает» тик, а просто висит до события.
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

        // Ожидание строки в чате.
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

        if (now < this.resumeAt) {
            return;
        }

        if (this.profile.getSteps().isEmpty()) {
            return;
        }

        int budget = STEPS_PER_TICK;
        while (budget-- > 0 && this.running) {
            if (this.waitConnectUntil > 0 || this.waitOfflineUntil > 0 || this.waitingChat != null
                    || System.currentTimeMillis() < this.resumeAt) {
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

    private boolean chatContains(String text) {
        String chat = this.connection.lastChat();
        return chat != null && !chat.isEmpty() && this.connection.lastChatAt() > this.waitingChatSince
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
                this.resumeAt = now + (long) (step.number(0) * 1000.0);
                return false;
            }
            case WAIT_RANDOM -> {
                double min = Math.min(step.number(0), step.number(1));
                double max = Math.max(step.number(0), step.number(1));
                double seconds = min + Math.random() * Math.max(0.0, max - min);
                this.resumeAt = now + (long) (seconds * 1000.0);
                return false;
            }
            case LOGIN, REGISTER, CUSTOM_COMMAND -> this.connection.command(step.get(0));
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
            case CHANGE_PASSWORD -> this.connection.command("changepassword "
                    + step.get(0) + " " + step.get(1));
            case LOOK -> this.connection.look((float) step.number(0), (float) step.number(1));
            case LOOK_AT -> this.connection.lookAt(step.number(0), step.number(1), step.number(2));
            case LOOK_YAW -> this.connection.look((float) step.number(0), this.connection.pitch());
            case SWAP_OFFHAND -> this.connection.swapOffhand();
            case STOP_MOVEMENT -> this.connection.stopMovement();
            case MOVE -> this.connection.moveTo(step.number(0), step.number(1), step.number(2));
            case SNEAK -> this.connection.setSneaking(isOn(step.get(0)));
            case SPRINT -> this.connection.setSprinting(isOn(step.get(0)));
            case JUMP -> this.connection.jump();
            case SWING -> this.connection.swing();
            case USE_ITEM -> this.connection.useItem();
            case HELD_SLOT -> this.connection.heldSlot(step.integer(0));
            case RESPAWN -> this.connection.respawn();
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
                    this.loops.push(new LoopFrame(stepIndex + 1, times));
                }
            }
            case LOOP_END -> {
                if (this.loops.isEmpty()) {
                    return true;
                }
                LoopFrame frame = this.loops.peek();
                int remaining = frame.remaining() < 0 ? -1 : frame.remaining() - 1;
                if (remaining == 0) {
                    this.loops.pop();
                } else {
                    this.loops.pop();
                    this.loops.push(new LoopFrame(frame.startIndex(), remaining));
                    this.index = frame.startIndex();
                }
            }
            case IF_CHAT -> {
                if (!chatContains(step.get(0))) {
                    this.index = skipBlock(this.index);
                }
            }
            case IF_ONLINE -> {
                if (!this.connection.isOnline()) {
                    this.index = skipBlock(this.index);
                }
            }
            case IF_HEALTH_BELOW -> {
                if (!(this.connection.health() < (float) step.number(0))) {
                    this.index = skipBlock(this.index);
                }
            }
            case IF_FOOD_BELOW -> {
                if (!(this.connection.food() < step.integer(0))) {
                    this.index = skipBlock(this.index);
                }
            }
            case IF_DEAD -> {
                if (this.connection.health() > 0.0f) {
                    this.index = skipBlock(this.index);
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
            case LOG_STATUS -> this.log.info(String.format("позиция %.1f %.1f %.1f · здоровье %.1f · еда %d",
                    this.connection.x(), this.connection.y(), this.connection.z(),
                    this.connection.health(), this.connection.food()));
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

    /** Индекс сразу после парного «конец условия» (с учётом вложенности). */
    private int skipBlock(int from) {
        int depth = 0;
        for (int i = from; i < this.profile.getSteps().size(); i++) {
            BotStepType type = this.profile.getSteps().get(i).getType();
            if (type == null) {
                continue;
            }
            if (type.opensBlock()) {
                depth++;
            } else if (type.closesBlock()) {
                if (depth == 0) {
                    return i + 1;
                }
                depth--;
            }
        }
        return this.profile.getSteps().size();
    }

    private static boolean isOn(String value) {
        String text = value == null ? "" : value.trim().toLowerCase();
        return text.equals("вкл") || text.equals("on") || text.equals("да") || text.equals("true");
    }
}
