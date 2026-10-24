package swerve.bot;

/**
 * Каталог шагов поведения бота.
 *
 * <p>Шаг описывается подписью с пустыми полями {@code {}} и списком
 * {@link Param}. Количество слов всегда на единицу больше количества полей —
 * по этому правилу строятся и палитра в студии, и редактор шага.
 *
 * <p>Шаги делятся на две группы по способу исполнения:
 * <ul>
 *   <li>сетевые — то, что headless-бот реально умеет без игрового мира:
 *       подключение, авторизация, чат, команды, поворот, движение, инвентарь
 *       (окно игрока), служебные пакеты;</li>
 *   <li>локальные — ожидания, условия и циклы: их считает {@link BotRunner}.</li>
 * </ul>
 *
 * <p>Шаги, которым нужен полноценный игровой мир (например, атака по сущности),
 * в список не входят: headless-клиент не знает сущностей вокруг и не должен
 * делать вид, что знает.
 */
public enum BotStepType {

    // ---- подключение
    CONNECT(Category.CONNECT, "подключиться к серверу"),
    WAIT_ONLINE(Category.CONNECT, "ждать подключения, не дольше {} сек", Param.number("30")),
    RECONNECT(Category.CONNECT, "переподключиться"),
    DISCONNECT(Category.CONNECT, "отключиться от сервера"),
    WAIT(Category.CONNECT, "ждать {} сек", Param.number("2")),
    WAIT_RANDOM(Category.CONNECT, "ждать от {} до {} сек", Param.number("1"), Param.number("3")),
    WAIT_DISCONNECT(Category.CONNECT, "ждать отключения, не дольше {} сек", Param.number("15")),
    SET_NICKNAME(Category.CONNECT, "сменить ник на {} (при следующем входе)", Param.text("Bot2")),

    // ---- авторизация
    LOGIN(Category.AUTH, "войти: /login {}", Param.text("пароль")),
    REGISTER(Category.AUTH, "зарегистрироваться: /reg {} {}", Param.text("пароль"), Param.text("пароль")),
    CUSTOM_COMMAND(Category.AUTH, "команда {}", Param.text("/help")),
    CHANGE_PASSWORD(Category.AUTH, "сменить пароль: /changepassword {} {}", Param.text("старый"), Param.text("новый")),

    // ---- чат
    CHAT(Category.CHAT, "написать в чат {}", Param.text("привет")),
    WAIT_CHAT(Category.CHAT, "ждать в чате «{}», не дольше {} сек", Param.text("текст"), Param.number("15")),

    // ---- действия
    LOOK(Category.ACTION, "повернуться: yaw {} pitch {}", Param.number("0"), Param.number("0")),
    MOVE(Category.ACTION, "переместиться к {} {} {}", Param.number("0"), Param.number("64"), Param.number("0")),
    SNEAK(Category.ACTION, "присесть: {}", Param.option("вкл", "вкл", "выкл")),
    SPRINT(Category.ACTION, "бежать: {}", Param.option("вкл", "вкл", "выкл")),
    JUMP(Category.ACTION, "прыгнуть"),
    SWING(Category.ACTION, "махнуть рукой"),
    USE_ITEM(Category.ACTION, "использовать предмет"),
    HELD_SLOT(Category.ACTION, "выбрать слот хотбара {}", Param.number("0")),
    RESPAWN(Category.ACTION, "возродиться"),
    LOOK_AT(Category.ACTION, "посмотреть на точку {} {} {}", Param.number("0"), Param.number("64"), Param.number("0")),
    LOOK_YAW(Category.ACTION, "повернуть корпус на {} градусов", Param.number("0")),
    SWAP_OFFHAND(Category.ACTION, "взять предмет в левую руку"),
    STOP_MOVEMENT(Category.ACTION, "остановить движение"),

    // ---- инвентарь (слоты окна игрока: 5–8 броня, 9–35 рюкзак, 36–44 хотбар, 45 левая рука)
    OPEN_INVENTORY(Category.INVENTORY, "открыть инвентарь"),
    CLOSE_INVENTORY(Category.INVENTORY, "закрыть инвентарь"),
    CLICK_SLOT(Category.INVENTORY, "нажать слот {} кнопкой {} режим {}",
            Param.number("36"), Param.number("0"), Param.number("0")),
    MOVE_SLOT(Category.INVENTORY, "переложить из слота {} в {}", Param.number("36"), Param.number("5")),
    DROP_SLOT(Category.INVENTORY, "выбросить из слота {}", Param.number("36")),
    QUICK_MOVE(Category.INVENTORY, "быстро перенести из слота {} (shift-клик)", Param.number("36")),
    SWAP_WITH_HOTBAR(Category.INVENTORY, "поменять слот {} с хотбаром {}", Param.number("36"), Param.number("0")),
    THROW_STACK(Category.INVENTORY, "выбросить стак из слота {}", Param.number("36")),
    EQUIP_ARMOR(Category.INVENTORY, "надеть из слота {} в ячейку брони {}",
            Param.number("9"), Param.option("шлем", "шлем", "нагрудник", "штаны", "ботинки")),

    // ---- логика
    LOOP(Category.LOGIC, "повторить {} раз (0 — всегда)", Param.number("3")),
    LOOP_END(Category.LOGIC, "конец повтора"),
    IF_CHAT(Category.LOGIC, "если в чате есть «{}»", Param.text("текст")),
    IF_ONLINE(Category.LOGIC, "если бот в сети"),
    IF_HEALTH_BELOW(Category.LOGIC, "если здоровье ниже {}", Param.number("10")),
    IF_FOOD_BELOW(Category.LOGIC, "если еда ниже {}", Param.number("15")),
    IF_DEAD(Category.LOGIC, "если бот мёртв"),
    IF_END(Category.LOGIC, "конец условия"),
    STOP(Category.LOGIC, "остановить бота"),

    // ---- прочее
    NOTIFY(Category.MISC, "уведомление «{}»", Param.text("готово")),
    LOG(Category.MISC, "запись в лог: {}", Param.text("шаг")),
    LOG_STATUS(Category.MISC, "записать состояние в журнал"),
    CLEAR_LOG(Category.MISC, "очистить журнал");

    /** Категории шагов в палитре студии. */
    public enum Category {

        CONNECT("Подключение", "подключение"),
        AUTH("Авторизация", "авторизация"),
        CHAT("Чат", "чат"),
        ACTION("Действия", "действие"),
        INVENTORY("Инвентарь", "инвентарь"),
        LOGIC("Логика", "логика"),
        MISC("Прочее", "прочее");

        private final String displayName;

        Category(String displayName, String ignored) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    /** Тип поля. */
    public enum Kind {
        TEXT,
        NUMBER,
        OPTION
    }

    /** Поле шага: тип, значение по умолчанию и варианты (для OPTION). */
    public static final class Param {

        private final Kind kind;
        private final String defaultValue;
        private final String[] options;
        private final String title;

        private Param(Kind kind, String defaultValue, String title, String... options) {
            this.kind = kind;
            this.defaultValue = defaultValue == null ? "" : defaultValue;
            this.title = title == null ? "" : title;
            this.options = options == null ? new String[0] : options;
        }

        public Kind kind() {
            return this.kind;
        }

        public String defaultValue() {
            return this.defaultValue;
        }

        public String title() {
            return this.title;
        }

        public String[] options() {
            return this.options;
        }

        public boolean dropdown() {
            return this.kind == Kind.OPTION;
        }

        public static Param text(String value) {
            return new Param(Kind.TEXT, value, "");
        }

        public static Param text(String value, String title) {
            return new Param(Kind.TEXT, value, title);
        }

        public static Param number(String value) {
            return new Param(Kind.NUMBER, value, "");
        }

        public static Param option(String value, String... options) {
            return new Param(Kind.OPTION, value, "", options);
        }
    }

    private final Category category;
    private final String[] words;
    private final Param[] params;

    BotStepType(Category category, String label, Param... params) {
        this.category = category;
        this.params = params == null ? new Param[0] : params;
        String[] parts = label.split("\\{\\}", -1);
        if (parts.length != this.params.length + 1) {
            throw new IllegalArgumentException("Шаг '" + name() + "': " + parts.length
                    + " слов при " + this.params.length + " полях");
        }
        this.words = parts;
    }

    public Category category() {
        return this.category;
    }

    public int paramCount() {
        return this.params.length;
    }

    public Param param(int index) {
        return index >= 0 && index < this.params.length ? this.params[index] : null;
    }

    public String word(int index) {
        return index >= 0 && index < this.words.length ? this.words[index] : "";
    }

    /** Полная подпись шага с подставленными значениями — для лога и списка. */
    public String describe(String[] values) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < this.words.length; i++) {
            out.append(this.words[i]);
            if (i < this.params.length) {
                String value = values != null && i < values.length ? values[i] : this.params[i].defaultValue();
                out.append(value);
            }
        }
        return out.toString();
    }

    public boolean opensBlock() {
        return this == LOOP || this == IF_CHAT || this == IF_ONLINE
                || this == IF_HEALTH_BELOW || this == IF_FOOD_BELOW || this == IF_DEAD;
    }

    public boolean closesBlock() {
        return this == LOOP_END || this == IF_END;
    }

    public static BotStepType byName(String name) {
        if (name == null) {
            return null;
        }
        for (BotStepType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
