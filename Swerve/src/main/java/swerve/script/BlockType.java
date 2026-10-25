package swerve.script;

import java.util.HashMap;
import java.util.Map;

/**
 * Каталог блоков: каждый блок, который можно перетащить в скрипт.
 *
 * <p>Блок описывается подписью с полями-заглушками {@code {}} и списком
 * {@link Slot}. Количество слов всегда на единицу больше количества полей,
 * поэтому подпись можно рисовать как «слово — поле — слово — поле — слово».
 *
 * <p>Это единственное место, где перечислены возможности редактора: палитра
 * строится по категориям этого перечисления, а интерпретатор
 * ({@link ScriptEngine}) разбирает те же константы.
 */
public enum BlockType {

    // ---- события (шляпы): с них начинается функция
    TRIGGER_MANUAL("trigger_manual", BlockCategory.EVENT, BlockShape.HAT, "когда функция вызвана"),
    TRIGGER_TICK("trigger_tick", BlockCategory.EVENT, BlockShape.HAT, "каждый тик"),
    TRIGGER_JOIN("trigger_join", BlockCategory.EVENT, BlockShape.HAT, "когда заходишь в мир"),

    // ---- управление
    WAIT("wait", BlockCategory.CONTROL, BlockShape.STACK, "ждать {} сек", Slot.number("1")),
    REPEAT("repeat", BlockCategory.CONTROL, BlockShape.C, "повторить {} раз", Slot.number("3")),
    FOREVER("forever", BlockCategory.CONTROL, BlockShape.C, "повторять всегда"),
    STOP("stop", BlockCategory.CONTROL, BlockShape.STACK, "остановить скрипт"),

    IF_MODULE("if_module", BlockCategory.CONTROL, BlockShape.C, "если модуль {} включён", Slot.module()),
    IF_MODULE_OFF("if_module_off", BlockCategory.CONTROL, BlockShape.C, "если модуль {} выключен", Slot.module()),
    IF_HEALTH("if_health", BlockCategory.CONTROL, BlockShape.C, "если здоровье < {}", Slot.number("10")),
    IF_FOOD("if_food", BlockCategory.CONTROL, BlockShape.C, "если голод < {}", Slot.number("10")),
    IF_KEY("if_key", BlockCategory.CONTROL, BlockShape.C, "если клавиша {} зажата", Slot.key()),
    IF_VAR("if_var", BlockCategory.CONTROL, BlockShape.C, "если {} {} {}", Slot.text("x"), Slot.comparison(">"), Slot.text("0")),
    IF_WORLD("if_world", BlockCategory.CONTROL, BlockShape.C, "если мир загружен"),
    IF_CHANCE("if_chance", BlockCategory.CONTROL, BlockShape.C, "если шанс {} %", Slot.number("50")),

    // ---- действия клиента
    TOGGLE_MODULE("toggle_module", BlockCategory.ACTION, BlockShape.STACK, "переключить модуль {}", Slot.module()),
    ENABLE_MODULE("enable_module", BlockCategory.ACTION, BlockShape.STACK, "включить модуль {}", Slot.module()),
    DISABLE_MODULE("disable_module", BlockCategory.ACTION, BlockShape.STACK, "выключить модуль {}", Slot.module()),
    SET_SETTING("set_setting", BlockCategory.ACTION, BlockShape.STACK, "настройка {} {} = {}",
            Slot.module(), Slot.setting(), Slot.text("1")),
    NOTIFY("notify", BlockCategory.ACTION, BlockShape.STACK, "уведомление {}", Slot.text("Привет из скрипта")),
    CHAT("chat", BlockCategory.ACTION, BlockShape.STACK, "сообщение в чат {}", Slot.text("Привет")),
    COMMAND("command", BlockCategory.ACTION, BlockShape.STACK, "команда {}", Slot.text("/spawn")),
    SOUND("sound", BlockCategory.ACTION, BlockShape.STACK, "звук {}", Slot.option("mode_open.wav",
            "mode_open.wav", "mode_close.wav", "mode_pick.wav", "mode_pick_on.wav",
            "cb_enabled.wav", "cb_disabled.wav", "guiopen.wav", "guiclose.wav",
            "bind_success.wav", "bind_remove.wav")),

    // ---- переменные
    SET_VAR("set_var", BlockCategory.VARIABLE, BlockShape.STACK, "задать {} = {}", Slot.text("x"), Slot.text("0")),
    CHANGE_VAR("change_var", BlockCategory.VARIABLE, BlockShape.STACK, "изменить {} на {}", Slot.text("x"), Slot.number("1")),

    // ---- свои функции
    CALL_FUNCTION("call_function", BlockCategory.FUNCTION, BlockShape.STACK, "вызвать {}", Slot.function());

    private static final Map<String, BlockType> BY_ID = new HashMap<>();

    static {
        for (BlockType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    /** Поле блока: тип, значение по умолчанию и (для списков) варианты. */
    public static final class Slot {

        private final SlotType type;
        private final String defaultValue;
        private final String[] options;

        private Slot(SlotType type, String defaultValue, String... options) {
            this.type = type;
            this.defaultValue = defaultValue == null ? "" : defaultValue;
            this.options = options == null ? new String[0] : options;
        }

        public SlotType type() {
            return this.type;
        }

        public String defaultValue() {
            return this.defaultValue;
        }

        public String[] options() {
            return this.options;
        }

        public static Slot text(String value) {
            return new Slot(SlotType.TEXT, value);
        }

        public static Slot number(String value) {
            return new Slot(SlotType.NUMBER, value);
        }

        public static Slot yesNo(boolean value) {
            return new Slot(SlotType.BOOL, value ? "да" : "нет", "да", "нет");
        }

        public static Slot comparison(String value) {
            return new Slot(SlotType.OP, value, "=", "≠", "<", "≤", ">", "≥");
        }

        public static Slot module() {
            return new Slot(SlotType.MODULE, "");
        }

        public static Slot setting() {
            return new Slot(SlotType.SETTING, "");
        }

        public static Slot function() {
            return new Slot(SlotType.FUNCTION, "");
        }

        public static Slot key() {
            return new Slot(SlotType.KEY, "-1");
        }

        public static Slot option(String value, String... options) {
            return new Slot(SlotType.OPTION, value, options);
        }
    }

    private final String id;
    private final BlockCategory category;
    private final BlockShape shape;
    private final String[] words;
    private final Slot[] slots;

    BlockType(String id, BlockCategory category, BlockShape shape, String label, Slot... slots) {
        this.id = id;
        this.category = category;
        this.shape = shape;
        this.slots = slots == null ? new Slot[0] : slots;

        String[] parts = label.split("\\{\\}", -1);
        if (parts.length != this.slots.length + 1) {
            throw new IllegalArgumentException("Block '" + id + "': " + parts.length
                    + " words for " + this.slots.length + " slots");
        }
        this.words = parts;
    }

    public String id() {
        return this.id;
    }

    public BlockCategory category() {
        return this.category;
    }

    public BlockShape shape() {
        return this.shape;
    }

    public int slotCount() {
        return this.slots.length;
    }

    public Slot slot(int index) {
        return index >= 0 && index < this.slots.length ? this.slots[index] : null;
    }

    /** Слово подписи под номером {@code index} (0..slotCount). */
    public String word(int index) {
        return index >= 0 && index < this.words.length ? this.words[index] : "";
    }

    public String label() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.words.length; i++) {
            if (i > 0) {
                builder.append("{}");
            }
            builder.append(this.words[i]);
        }
        return builder.toString();
    }

    public boolean hat() {
        return this.shape == BlockShape.HAT;
    }

    public boolean container() {
        return this.shape == BlockShape.C;
    }

    /** Событие, с которого начинается функция; {@code null} для обычных блоков. */
    public ScriptTrigger trigger() {
        return switch (this) {
            case TRIGGER_TICK -> ScriptTrigger.TICK;
            case TRIGGER_JOIN -> ScriptTrigger.JOIN;
            case TRIGGER_MANUAL -> ScriptTrigger.MANUAL;
            default -> null;
        };
    }

    public static BlockType byId(String id) {
        return id == null ? null : BY_ID.get(id);
    }
}
