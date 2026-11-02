package net.skycore.client.bot;

import net.skycore.client.util.SelectAndSaveHelper;

/**
 * Этапы сценария бота BedWars.
 *
 * <p>Порядок элементов — ровно тот, в котором бот проходит путь от подключения до
 * ожидания старта партии. Список нужен интерфейсу (вкладка «Бот» в Workspace):
 * он рисует его как степпер, а {@link BedWarsBot} отмечает текущий этап.
 *
 * <p>Реальной логики за этапами пока нет: {@link BedWarsBot} — заглушка, которая
 * только переключает этап и пишет строку в журнал. Когда бота будут реализовывать,
 * каждому этапу соответствует свой метод-заглушка в {@link BedWarsBot}, и его тело
 * заполнят настоящей логикой, не меняя ни этот enum, ни интерфейс.
 */
public enum BotStage {

    IDLE("Ожидание", "Idle", "бот не запущен", "bot is not running"),
    CONNECT("Подключение", "Connect", BotScript.SERVER, BotScript.SERVER),
    LOGIN("Вход", "Login", "ник = префикс + номер", "nick = prefix + number"),
    REGISTER("Регистрация", "Register", "/reg <пароль>", "/reg <password>"),
    SERVER_SELECT("Выбор сервера", "Server select", "ячейка 8, затем ячейка 11", "cell 8, then cell 11"),
    JOIN_GAME("Вход в BedWars", "Join BedWars", "мини-игра BedWars", "BedWars minigame"),
    PARTY_CREATE("Создание пати", "Party create", "/party accept", "/party accept"),
    PARTY_INVITE("Приглашение", "Party invite", "/party invite <ник>", "/party invite <nick>"),
    PARTY_ACCEPT("Приём пати", "Party accept", "/party accept <ник бота>", "/party accept <bot nick>"),
    CATEGORY_OPEN("Выбор категории", "Category select", "слот 3 хотбара, ПКМ", "hotbar slot 3, RMB"),
    CATEGORY_PICK("Ячейки категории", "Category cells", "ячейка 11, затем ячейка 10", "cell 11, then cell 10"),
    ENDER_EYE("Поиск предмета", "Item search", "повтор, пока не появится Око эндера", "retry until Eye of Ender"),
    TEAM_COLORS("Цвета команд", "Team colors", "чтение шерсти в меню команд", "read wool colors in team menu"),
    VOTE_ELYTRA("Голосование: элитры", "Vote: elytra", "ячейка 12 или 14", "cell 12 or 14"),
    VOTE_PVP("Голосование: PvP", "Vote: PvP", "ячейка 12 или 14", "cell 12 or 14"),
    VOTE_RESOURCES("Голосование: ресурсы", "Vote: resources", "ячейка 12 или 14", "cell 12 or 14"),
    WAIT("Ожидание старта", "Waiting", "бот ждёт начала партии", "bot waits for the match");

    private static final BotStage[] FLOW = values();

    private final String ru;
    private final String en;
    private final String hintRu;
    private final String hintEn;

    BotStage(String ru, String en, String hintRu, String hintEn) {
        this.ru = ru;
        this.en = en;
        this.hintRu = hintRu;
        this.hintEn = hintEn;
    }

    /** Название этапа на языке клиента. */
    public String label() {
        return SelectAndSaveHelper.isRussian() ? this.ru : this.en;
    }

    /** Короткая подсказка (команда или номера ячеек) на языке клиента. */
    public String hint() {
        return SelectAndSaveHelper.isRussian() ? this.hintRu : this.hintEn;
    }

    /** Этапы в порядке прохождения, включая стартовое «Ожидание». */
    public static BotStage[] flow() {
        return FLOW;
    }

    /** Последний этап сценария: после него бот остаётся в ожидании. */
    public static BotStage last() {
        BotStage[] all = FLOW;
        return all[all.length - 1];
    }

    /** Следующий этап сценария; для последнего возвращает его же. */
    public BotStage next() {
        BotStage[] all = FLOW;
        int index = ordinal();
        return index + 1 < all.length ? all[index + 1] : this;
    }
}
