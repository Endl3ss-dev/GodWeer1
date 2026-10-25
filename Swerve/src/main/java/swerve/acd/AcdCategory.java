package swerve.acd;

/**
 * Категория диагностического события.
 *
 * <p>Категория описывает <b>тип источника</b>, а не вывод о том, что на сервере
 * стоит античит: «античит-предупреждением» сообщение делает правило-эвристика, и
 * такое событие помечается через {@link AcdConfidence#HEURISTIC}.
 *
 * <p>Новая категория добавляется одним элементом здесь: {@link AcdRules} связывает
 * с неё правила, {@link AcdStore} фильтрует по {@link #ordinal()}, а окно настроек
 * модуля строит список категорий из {@link #values()}, поэтому ни сборщик, ни
 * хранилище, ни GUI переписывать не нужно.
 */
public enum AcdCategory {

    /** Явное предупреждение, которое сервер отправил именно как предупреждение защиты. */
    ANTI_CHEAT_WARNING("Предупреждение", AcdSeverity.WARNING),

    /** Сообщение о флаге: найденное по шаблону в чате или системном сообщении. */
    FLAG_NOTIFICATION("Флаг", AcdSeverity.WARNING),

    /** Отключение от сервера вместе с текстом причины. */
    KICK_DISCONNECT("Отключение", AcdSeverity.CRITICAL),

    /** Системное сообщение сервера, не разобранное как что-то более конкретное. */
    SERVER_MESSAGE("Сообщение сервера", AcdSeverity.INFO),

    /** Состояние и ошибки соединения: вход, выход, неизвестные каналы полезной нагрузки. */
    NETWORK_DIAGNOSTIC("Сеть", AcdSeverity.NOTICE),

    /** Серверная коррекция нашей позиции (setback) и связанные с движением пакеты. */
    MOVEMENT_DIAGNOSTIC("Движение", AcdSeverity.NOTICE),

    /** Скорость, выданная сервером нашему игроку, и другие боевые наблюдения. */
    COMBAT_DIAGNOSTIC("Бой", AcdSeverity.INFO),

    /** Ошибка обработки пакета или внутренний сбой самого модуля. */
    CLIENT_ERROR("Ошибка клиента", AcdSeverity.CRITICAL),

    /** Всё, что дошло до клиента, но не отнесено никуда уверенно. */
    UNKNOWN_EVENT("Неизвестное", AcdSeverity.NOTICE);

    private final String label;
    private final AcdSeverity defaultSeverity;

    AcdCategory(String label, AcdSeverity defaultSeverity) {
        this.label = label;
        this.defaultSeverity = defaultSeverity;
    }

    /** Русское имя: используется в списке категорий и в заголовках отчёта. */
    public String label() {
        return this.label;
    }

    /** Стабильный ASCII-код для экспорта — не меняется при переименовании {@link #label()}. */
    public String code() {
        return this.name();
    }

    public AcdSeverity defaultSeverity() {
        return this.defaultSeverity;
    }
}
