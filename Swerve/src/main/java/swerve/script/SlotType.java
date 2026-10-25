package swerve.script;

/**
 * Тип поля внутри блока.
 *
 * <p>От типа зависит и то, как поле редактируется, и то, как его значение
 * разбирает интерпретатор.
 */
public enum SlotType {

    /** Свободный текст; поддерживает подстановки вида {@code {player}}. */
    TEXT(false),

    /** Число; тоже поддерживает подстановки, но пустое поле читается как 0. */
    NUMBER(false),

    /** Выбор из двух значений: да / нет. */
    BOOL(true),

    /** Оператор сравнения: = ≠ &lt; ≤ &gt; ≥. */
    OP(true),

    /** Выбор модуля клиента. */
    MODULE(true),

    /** Выбор настройки у модуля из предыдущего поля. */
    SETTING(true),

    /** Вызов другой функции проекта. */
    FUNCTION(true),

    /** Клавиша или кнопка мыши (код GLFW, у мыши +1000). */
    KEY(true),

    /** Фиксированный список значений (например, встроенные звуки). */
    OPTION(true);

    private final boolean dropdown;

    SlotType(boolean dropdown) {
        this.dropdown = dropdown;
    }

    /** Открывается ли по клику список вместо текстового ввода. */
    public boolean dropdown() {
        return this.dropdown;
    }

    public boolean numeric() {
        return this == NUMBER;
    }
}
