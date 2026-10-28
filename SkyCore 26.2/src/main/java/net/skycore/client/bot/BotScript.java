package net.skycore.client.bot;

import net.skycore.client.util.SelectAndSaveHelper;

/**
 * Константы сценария бота BedWars.
 *
 * <p>Здесь лежит <b>только описание</b> того, что бот должен сделать: адрес сервера,
 * номера ячеек в меню, состав хотбара лобби и хотбара игры, варианты голосований.
 * Никакой логики подключения, отправки пакетов или кликов в этом классе нет и не
 * планируется — он нужен интерфейсу (вкладка «Бот» в Workspace), чтобы показывать
 * сценарий пользователю, и будущей реализации бота, чтобы брать из одного места
 * номера ячеек вместо «магических» чисел в коде.
 *
 * <p>Сервер зафиксирован задачей ({@link #SERVER}) и не настраивается: в интерфейсе
 * поле сервера показывается заблокированным.
 *
 * <p>Класс не зависит от Minecraft — только строки и числа, поэтому его можно
 * использовать в проверках раскладки без запуска игры.
 */
public final class BotScript {

    /** Сервер, на который заходит бот. Зафиксирован, менять нельзя. */
    public static final String SERVER = "mc.musteryworld.net";

    /** Значения по умолчанию: префикс ника, пароль регистрации, ник владельца пати. */
    public static final String DEFAULT_NICK_PREFIX = "SwerveBot";
    public static final String DEFAULT_PASSWORD = "SwerveClient";
    public static final String DEFAULT_OWNER = "Swerve";

    /** Сколько ботов можно запустить одновременно. */
    public static final int MIN_BOTS = 1;
    public static final int MAX_BOTS = 8;
    public static final int DEFAULT_BOTS = 1;

    /** Меню «Выбор сервера»: сначала ячейка 8, затем ячейка 11. */
    public static final int SERVER_CELL_FIRST = 8;
    public static final int SERVER_CELL_SECOND = 11;

    /** Меню «Выбор категории»: сначала ячейка 11, затем ячейка 10. */
    public static final int CATEGORY_CELL_FIRST = 11;
    public static final int CATEGORY_CELL_SECOND = 10;

    /**
     * Слот хотбара лобби (1-based), по которому бот кликает ПКМ, чтобы открыть
     * «Выбор категории».
     */
    public static final int CATEGORY_HOTBAR_SLOT = 3;

    /** Ячейки голосований: 12 — первый вариант, 14 — второй. */
    public static final int VOTE_CELL_A = 12;
    public static final int VOTE_CELL_B = 14;

    /**
     * Текстовые номера ячеек голосований.
     *
     * <p>Подготовлены один раз при инициализации класса: отрисовка вкладок показывает
     * номер ячейки рядом с подписью варианта и не склеивает строки в кадре — это
     * правило кадра SkyCore (см. {@code docs/GUI-SELECTION.md}, раздел 3).
     */
    public static final String CELL_A_TEXT = Integer.toString(VOTE_CELL_A);
    public static final String CELL_B_TEXT = Integer.toString(VOTE_CELL_B);

    /** Предмет, который бот ищет в ячейке {@link #CATEGORY_CELL_SECOND}. */
    public static final String TARGET_ITEM_RU = "Око эндера";
    public static final String TARGET_ITEM_EN = "Eye of Ender";

    /**
     * Доступность элемента сценария.
     *
     * <p>Повторяет разметку из постановки задачи: что бот будет делать сейчас,
     * что появится позже и чего не будет вовсе. Интерфейс рисует по этому значению
     * цвет и подпись бейджа, а будущая реализация бота обязана пропускать элементы
     * {@link #LATER} и {@link #NEVER}.
     */
    public enum Availability {
        NOW("будет", "enabled"),
        LATER("позже", "later"),
        NEVER("не будет", "disabled"),
        EMPTY("пусто", "empty");

        private final String ru;
        private final String en;

        Availability(String ru, String en) {
            this.ru = ru;
            this.en = en;
        }

        /** Подпись бейджа на языке клиента. */
        public String label() {
            return SelectAndSaveHelper.isRussian() ? this.ru : this.en;
        }

        /** Доступен ли элемент уже сейчас. */
        public boolean usable() {
            return this == NOW;
        }
    }

    /**
     * Пункт хотбара.
     *
     * @param ru           название по-русски
     * @param en           название по-английски
     * @param availability доступность согласно постановке задачи
     */
    public record Slot(String ru, String en, Availability availability) {

        /** Название на языке клиента. */
        public String label() {
            return SelectAndSaveHelper.isRussian() ? this.ru : this.en;
        }

        /** Пустой слот хотбара (в задаче такие идут через один). */
        public boolean blank() {
            return this.availability == Availability.EMPTY;
        }
    }

    /**
     * Голосование с двумя ячейками на выбор.
     *
     * @param ru           название по-русски
     * @param en           название по-английски
     * @param optionRu     подпись ячейки 12 по-русски
     * @param optionEn     подпись ячейки 12 по-английски
     * @param altOptionRu  подпись ячейки 14 по-русски
     * @param altOptionEn  подпись ячейки 14 по-английски
     * @param availability доступность: у «позже» варианты не выбираются
     */
    public record Vote(String ru, String en, String optionRu, String optionEn,
                       String altOptionRu, String altOptionEn, Availability availability) {

        public String label() {
            return SelectAndSaveHelper.isRussian() ? this.ru : this.en;
        }

        /** Подпись варианта для ячейки {@code cell} (12 или 14). */
        public String optionLabel(int cell) {
            boolean first = cell == BotScript.VOTE_CELL_A;
            if (first) {
                return SelectAndSaveHelper.isRussian() ? this.optionRu : this.optionEn;
            }
            return SelectAndSaveHelper.isRussian() ? this.altOptionRu : this.altOptionEn;
        }
    }

    /**
     * Хотбар лобби: девять слотов в том порядке, в котором их перечислила задача.
     *
     * <pre>
     * 1 Быстро играть   — не будет
     * 2 (пусто)
     * 3 Выбор категории — будет, по нему бот кликает ПКМ
     * 4 (пусто)
     * 5 Статистика      — позже
     * 6 (пусто)
     * 7 Выбор лобби     — позже
     * 8 Скрыть игроков  — не будет
     * 9 Назад в лобби   — не будет
     * </pre>
     */
    public static final Slot[] LOBBY_HOTBAR = {
            new Slot("Быстро играть", "Quick play", Availability.NEVER),
            new Slot("", "", Availability.EMPTY),
            new Slot("Выбор категории", "Category select", Availability.NOW),
            new Slot("", "", Availability.EMPTY),
            new Slot("Статистика", "Statistics", Availability.LATER),
            new Slot("", "", Availability.EMPTY),
            new Slot("Выбор лобби", "Lobby select", Availability.LATER),
            new Slot("Скрыть игроков", "Hide players", Availability.NEVER),
            new Slot("Назад в лобби", "Back to lobby", Availability.NEVER),
    };

    /**
     * Хотбар в игре: семь пунктов в порядке из задачи.
     *
     * <p>«Статистика» и «Выбор лобби» сюда не входят — по постановке они недоступны.
     */
    public static final Slot[] GAME_HOTBAR = {
            new Slot("Выбор команды", "Team select", Availability.NOW),
            new Slot("Время суток", "Daytime vote", Availability.LATER),
            new Slot("Время игры", "Game time vote", Availability.LATER),
            new Slot("Элитры", "Elytra vote", Availability.NOW),
            new Slot("PvP", "PvP vote", Availability.NOW),
            new Slot("Ресурсы", "Resources vote", Availability.NOW),
            new Slot("Назад в лобби", "Back to lobby", Availability.LATER),
    };

    /**
     * Голосования в меню игры.
     *
     * <p>Ячейка 12 — первый вариант, ячейка 14 — второй:
     * элитры (с элитрами / без), PvP (1.8 / 1.12), ресурсы (виртуальный / классика).
     * Голосования за время суток и время игры в задаче помечены «позже».
     */
    public static final Vote[] VOTES = {
            new Vote("Элитры", "Elytra", "с элитрами", "with elytra", "без элитр", "no elytra",
                    Availability.NOW),
            new Vote("PvP", "PvP", "PvP 1.8", "PvP 1.8", "PvP 1.12", "PvP 1.12", Availability.NOW),
            new Vote("Ресурсы", "Resources", "Виртуальный", "Virtual", "Классика", "Classic", Availability.NOW),
            new Vote("Время суток", "Daytime", "—", "—", "—", "—", Availability.LATER),
            new Vote("Время игры", "Game time", "—", "—", "—", "—", Availability.LATER),
    };

    /**
     * Цвета шерсти в порядке красок Minecraft (ARGB).
     *
     * <p>В меню выбора команды команды показаны шерстью разных цветов, и по постановке
     * задачи бот обязан «просмотреть» это меню и вывести цвета. Здесь лежат сами цвета —
     * интерфейс рисует по ним сетку образцов ещё до того, как бот научится читать меню,
     * поэтому вкладка выглядит так, как будет выглядеть с реальными данными.
     */
    public static final int[] WOOL_COLORS = {
            0xFFF9FFFE, 0xFFF9801D, 0xFFC74EBD, 0xFF3AB3DA,
            0xFFFED83D, 0xFF80C71F, 0xFFF38BAA, 0xFF474F52,
            0xFF9D9D97, 0xFF169C9C, 0xFF8932B8, 0xFF3C44AA,
            0xFF835432, 0xFF5E7C16, 0xFFB02E26, 0xFF1D1D21,
    };

    /** Названия цветов шерсти на языке клиента (индекс совпадает с {@link #WOOL_COLORS}). */
    private static final String[] WOOL_RU = {
            "Белый", "Оранжевый", "Пурпурный", "Голубой",
            "Жёлтый", "Лаймовый", "Розовый", "Серый",
            "Светло-серый", "Бирюзовый", "Фиолетовый", "Синий",
            "Коричневый", "Зелёный", "Красный", "Чёрный",
    };

    private static final String[] WOOL_EN = {
            "White", "Orange", "Magenta", "Light blue",
            "Yellow", "Lime", "Pink", "Gray",
            "Light gray", "Cyan", "Purple", "Blue",
            "Brown", "Green", "Red", "Black",
    };

    /** Название цвета шерсти на языке клиента. */
    public static String woolName(int index) {
        if (index < 0 || index >= WOOL_RU.length) {
            return "";
        }
        return SelectAndSaveHelper.isRussian() ? WOOL_RU[index] : WOOL_EN[index];
    }

    /** Индекс голосования в {@link #VOTES}. */
    public static final int VOTE_ELYTRA = 0;
    public static final int VOTE_PVP = 1;
    public static final int VOTE_RESOURCES = 2;
    public static final int VOTE_DAYTIME = 3;
    public static final int VOTE_GAME_TIME = 4;

    private BotScript() {
    }

    /** Название мини-игры, в которую попадает бот после выбора сервера. */
    public static String minigame() {
        return "BedWars";
    }

    /** Название меню, которое открывается по первому слоту хотбара. */
    public static String serverSelectTitle() {
        return SelectAndSaveHelper.isRussian() ? "Выбор сервера" : "Server select";
    }

    /** Название меню, которое открывается по ПКМ на «Выбор категории». */
    public static String categoryTitle() {
        return SelectAndSaveHelper.isRussian() ? "Выбор категории" : "Category select";
    }
}
