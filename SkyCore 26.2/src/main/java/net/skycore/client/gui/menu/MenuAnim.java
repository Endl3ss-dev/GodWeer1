package net.skycore.client.gui.menu;

/**
 * Анимации меню.
 *
 * <p>Порт {@code swerve.ui.menu.MenuAnim} (Swerve, порт Arcanic Visuals, GPL-3.0).
 * Индексация целочисленная: в кадре отрисовки не создаётся ни строк, ни лямбд,
 * ни боксинга — только примитивы. Пружины ({@link #to}) и тайм-твины ({@link #tween})
 * делят один массив, но используют разные ключи.
 */
public final class MenuAnim {

    // ---- глобальные ключи
    public static final int OPEN = 0;
    public static final int SFOCUS = 1;
    public static final int SHOVER = 2;
    public static final int THEME_HOVER = 3;
    public static final int THEME_KEY = 4;
    public static final int POPUP = 5;
    public static final int DOCK_X = 6;

    // ---- вкладки категорий
    public static final int TAB_HOVER = 12;
    public static final int TAB_ACTIVE = 20;
    public static final int PLATE_X = 28;
    public static final int PLATE_W = 29;

    // ---- пункты дока
    public static final int DOCK_HOVER = 32;

    // ---- модули: по 6 значений на модуль
    public static final int MODULE_BASE = 40;
    public static final int MOD_STRIDE = 6;
    public static final int MOD_HOVER = 0;
    public static final int MOD_ON = 1;
    public static final int MOD_SELECTED = 2;
    public static final int MOD_KEY_WIDTH = 3;
    public static final int MOD_GEAR = 4;
    public static final int MOD_KEY_WAIT = 5;

    public static final int MAX_MODULES = 128;

    // ---- настройки: по 8 значений на настройку (индекс = модуль * SET_PER_MODULE + номер)
    public static final int SETTING_BASE = MODULE_BASE + MAX_MODULES * MOD_STRIDE;
    public static final int SET_STRIDE = 8;
    public static final int MAX_SETTINGS = 2048;
    public static final int SET_ROW = 0;
    public static final int SET_BOOL = 1;
    public static final int SET_NUM = 2;
    public static final int SET_NUM_KNOB = 3;
    public static final int SET_SEGMENT = 4;
    public static final int SET_DROPDOWN = 5;
    /** Положение бегунка числовой строки (пружина). */
    public static final int SET_NUM_POS = 6;

    // ---- настройки меню: по 4 значения на пункт
    public static final int PREFS_BASE = SETTING_BASE + MAX_SETTINGS * SET_STRIDE;
    public static final int PREFS_SIZE = 64;

    // ---- прокрутка: по одному значению на контекст плюс служебные ключи
    public static final int SCROLL_BASE = PREFS_BASE + PREFS_SIZE;
    public static final int SCROLL_POPOVER = 16;
    public static final int SCROLL_BAR = 24;
    public static final int SCROLL_SIZE = 40;

    /** Контекст прокрутки окна настроек модуля ({@link ModulePopup}). */
    public static final int POPUP_SCROLL = 7;

    public static final int SIZE = SCROLL_BASE + SCROLL_SIZE;

    private final float[] spring = new float[SIZE];
    private final float[] tweenFrom = new float[SIZE];
    private final float[] tweenTo = new float[SIZE];
    private final int[] tweenDur = new int[SIZE];
    private final long[] tweenStart = new long[SIZE];
    private final boolean[] tweenSet = new boolean[SIZE];

    /** Множитель скорости анимаций (настройка меню). */
    public static float speedMul = 1.0F;

    private long last = System.nanoTime();
    private float dt = 1.0F / 60.0F;

    /** Отмечает начало кадра: считается dt между кадрами, но не больше 100 мс. */
    public void frame() {
        long now = System.nanoTime();
        dt = Math.min(0.1F, (now - last) / 1.0E9F);
        last = now;
    }

    /** Пружина: значение подтягивается к цели экспоненциально, скорость задаёт вызывающий. */
    public float to(int key, float target, float speed) {
        float value = spring[key];
        if (!MenuPrefs.animations) {
            value = target;
        } else {
            value += (target - value) * (1.0F - (float) Math.exp(-dt * speed * speedMul));
        }
        if (Math.abs(target - value) < 0.0015F) {
            value = target;
        }
        spring[key] = value;
        return value;
    }

    /** Тайм-твин: интерполяция с плавным входом и выходом за заданное время. */
    public float tween(int key, float target, int ms) {
        long now = System.currentTimeMillis();
        if (!tweenSet[key] || !MenuPrefs.animations) {
            tweenSet[key] = true;
            tweenFrom[key] = target;
            tweenTo[key] = target;
            tweenDur[key] = ms;
            tweenStart[key] = now;
            return target;
        }
        float current = current(key, now);
        if (tweenTo[key] != target) {
            tweenFrom[key] = current;
            tweenTo[key] = target;
            tweenDur[key] = ms;
            tweenStart[key] = now;
        }
        return current;
    }

    private float current(int key, long now) {
        long duration = Math.max(1L, (long) (tweenDur[key] / Math.max(0.2F, speedMul)));
        double progress = Math.min(1.0, (now - tweenStart[key]) / (double) duration);
        double eased = progress < 0.5
                ? 4.0 * progress * progress * progress
                : 1.0 - Math.pow(-2.0 * progress + 2.0, 3.0) / 2.0;
        return (float) (tweenFrom[key] + (tweenTo[key] - tweenFrom[key]) * eased);
    }

    /** Ставит значение пружины напрямую (сброс при открытии окна). */
    public void set(int key, float value) {
        spring[key] = value;
    }

    /** Текущее значение пружины. */
    public float get(int key) {
        return spring[key];
    }

    /**
     * Возвращает пружину в исходное состояние.
     *
     * <p>Нужен при открытии меню: значения прошлой сессии (например, «окно настроек
     * открыто») иначе дали бы рывок на первом кадре.
     */
    public void reset() {
        java.util.Arrays.fill(spring, 0.0F);
        java.util.Arrays.fill(tweenSet, false);
    }


    public static float easeOutCubic(float t) {
        return 1.0F - (float) Math.pow(1.0F - t, 3.0);
    }
}
