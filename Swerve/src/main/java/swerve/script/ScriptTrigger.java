package swerve.script;

/**
 * Когда запускается функция. Значение берётся из блока-шляпы наверху скрипта.
 */
public enum ScriptTrigger {

    /** Только вручную: кнопка «Запустить», бинд функции или блок «вызвать». */
    MANUAL("вручную"),

    /** Каждый клиентский тик. */
    TICK("каждый тик"),

    /** Один раз после входа в мир. */
    JOIN("заход в мир");

    private final String displayName;

    ScriptTrigger(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }
}
