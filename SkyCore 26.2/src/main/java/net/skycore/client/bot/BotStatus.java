package net.skycore.client.bot;

import net.skycore.client.util.SelectAndSaveHelper;

/**
 * Состояние одного бота.
 *
 * <p>Набор состояний описывает жизненный цикл из постановки задачи: бот подключается,
 * регистрируется, проходит меню сервера, попадает в BedWars, вступает в пати, выбирает
 * категорию и голосования, после чего ждёт начала партии.
 *
 * <p>Сейчас состояния переключает только интерфейс-заглушка ({@link BedWarsBot}):
 * реальное подключение их будет менять по факту событий. Набор при этом окончательный —
 * новой логике не придётся добавлять сюда значения ради отображения.
 */
public enum BotStatus {

    IDLE("Остановлен", "Idle", 0xFF8A93A6),
    QUEUED("В очереди", "Queued", 0xFFE0A63C),
    CONNECTING("Подключение", "Connecting", 0xFF4FA3E3),
    IN_MENU("В меню", "In menu", 0xFF9A6BE0),
    IN_PARTY("В пати", "In party", 0xFF49C98A),
    IN_GAME("В игре", "In game", 0xFF49C98A),
    WAITING("Ожидание", "Waiting", 0xFF6FA8DC),
    ERROR("Ошибка", "Error", 0xFFFF5C72);

    private final String ru;
    private final String en;
    private final int color;

    BotStatus(String ru, String en, int color) {
        this.ru = ru;
        this.en = en;
        this.color = color;
    }

    /** Подпись на языке клиента. */
    public String label() {
        return SelectAndSaveHelper.isRussian() ? this.ru : this.en;
    }

    /** Цвет индикатора состояния (ARGB). */
    public int color() {
        return this.color;
    }

    /** Состояние, при котором индикатор в интерфейсе «живой» (пульсирует). */
    public boolean active() {
        return this == CONNECTING || this == IN_MENU || this == QUEUED;
    }
}
