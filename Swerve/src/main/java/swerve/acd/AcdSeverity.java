package swerve.acd;

/**
 * Уровень важности записи. Порядок значим: фильтр «минимальная важность» сравнивает
 * {@link #ordinal()} и держит всё не ниже выбранного уровня.
 */
public enum AcdSeverity {

    /** Полезно знать, но само по себе ни о чём не говорит. */
    INFO("Инфо"),

    /** Заметное событие: реакция сервера на наше поведение, смена соединения. */
    NOTICE("Заметка"),

    /** Требует внимания: вероятный флаг или предупреждение защиты. */
    WARNING("Предупреждение"),

    /** Разрыв соединения или ошибка — то, что обычно и хочет разобрать пользователь. */
    CRITICAL("Критично");

    private final String label;

    AcdSeverity(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }

    /** Не ниже ли этот уровень заданного порога (сравнение по {@link #ordinal()}). */
    public boolean atLeast(AcdSeverity floor) {
        return this.ordinal() >= floor.ordinal();
    }
}
