package swerve.script;

/**
 * Категории блоков и их цвета.
 *
 * <p>Палитра взята у Scratch 3: по цвету шапки блока пользователь узнаёт,
 * что перед ним — событие, цикл, действие клиента или переменная.
 */
public enum BlockCategory {

    EVENT("События", "событие", 0xFFFFBF00),
    CONTROL("Управление", "управление", 0xFFFFAB19),
    ACTION("Действия", "действие", 0xFF4C97FF),
    VARIABLE("Переменные", "переменная", 0xFFFF8C1A),
    FUNCTION("Мои блоки", "функция", 0xFFFF6680);

    private final String displayName;
    private final String hint;
    private final int color;

    BlockCategory(String displayName, String hint, int color) {
        this.displayName = displayName;
        this.hint = hint;
        this.color = color;
    }

    public String displayName() {
        return this.displayName;
    }

    /** Подсказка для пустого поля: «модуль», «функция», … */
    public String hint() {
        return this.hint;
    }

    /** ARGB-цвет блока. Непрозрачность задаётся при отрисовке. */
    public int color() {
        return this.color;
    }

    /** Шапки палитры рисуются светлее самого блока. */
    public int headerColor() {
        return darken(this.color, 0.86f);
    }

    public static int darken(int argb, float factor) {
        int a = (argb >>> 24) & 0xFF;
        int r = (int) (((argb >> 16) & 0xFF) * factor);
        int g = (int) (((argb >> 8) & 0xFF) * factor);
        int b = (int) ((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
