package swerve.ui.menu;

/**
 * Анимации меню (порт Arcanic Visuals, GPL-3.0).
 *
 * <p>В оригинале значения лежат в картах по строковому ключу. Здесь индексация целочисленная:
 * в кадре рендера не создаётся ни строк, ни лямбд, ни боксинга — только примитивы.
 * Пружины (to) и тайм-твины (tween) делят один массив, но используют разные ключи.
 */
public final class MenuAnim {

    MenuAnim() {
    }

    // ---- глобальные ключи
    public static final int OPEN = 0;
    public static final int SFOCUS = 1;
    public static final int SHOVER = 2;
    public static final int THEME_HOVER = 3;
    public static final int THEME_KEY = 4;
    public static final int POPUP = 5;
    public static final int DOCK_X = 6;
    public static final int SAVE_BTN = 7;
    public static final int IMPORT_BTN = 8;

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

    public static final int MAX_MODULES = 64;

    // ---- настройки: по 6 значений на настройку (индекс = модуль * 16 + номер настройки)
    public static final int SETTING_BASE = MODULE_BASE + MAX_MODULES * MOD_STRIDE;
    public static final int SET_STRIDE = 7;
    public static final int MAX_SETTINGS = 1024;
    public static final int SET_ROW = 0;
    public static final int SET_BOOL = 1;
    public static final int SET_NUM = 2;
    public static final int SET_NUM_KNOB = 3;
    public static final int SET_SEGMENT = 4;
    public static final int SET_DROPDOWN = 5;
    /** Положение бегунка числовой строки (пружина). */
    public static final int SET_NUM_POS = 6;

    // ---- настройки меню: по 4 значения на пункт (подсветка, значение, позиция бегунка, бегунок)
    public static final int PREFS_BASE = SETTING_BASE + MAX_SETTINGS * SET_STRIDE;
    public static final int PREFS_SIZE = 32;

    // ---- прокрутка: по одному значению на контекст
    public static final int SCROLL_BASE = PREFS_BASE + PREFS_SIZE;
    /** значения прокрутки (по контексту) */
    public static final int SCROLL_VALUE = 0;
    /** подсветка элементов выпадающего списка режима */
    public static final int SCROLL_POPOVER = 16;
    /** подсветка ползунков прокрутки */
    public static final int SCROLL_BAR = 24;
    public static final int SCROLL_SIZE = 40;
    public static final int SIZE = SCROLL_BASE + SCROLL_SIZE;

    private final float[] spring = new float[SIZE];
    private final float[] tweenFrom = new float[SIZE];
    private final float[] tweenTo = new float[SIZE];
    private final int[] tweenDur = new int[SIZE];
    private final long[] tweenStart = new long[SIZE];
    private final boolean[] tweenSet = new boolean[SIZE];

    /** Множитель скорости анимаций (настройка меню). */
    public static float speedMul = 1f;

    private long last = System.nanoTime();
    private float dt = 1 / 60f;

    public void frame() {
        long now = System.nanoTime();
        dt = Math.min(.1f, (now - last) / 1e9f);
        last = now;
    }

    /** Пружина: значение подтягивается к цели экспоненциально, скорость задаётся вызывающим. */
    public float to(int key, float target, float speed) {
        float v = spring[key];
        if (!MenuPrefs.animations) {
            v = target;
        } else {
            v += (target - v) * (1 - (float) Math.exp(-dt * speed * speedMul));
        }
        if (Math.abs(target - v) < .0015f) {
            v = target;
        }
        spring[key] = v;
        return v;
    }

    /** Тайм-твин: линейная интерполяция с плавным входом и выходом за заданное время. */
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
        float cur = current(key, now);
        if (tweenTo[key] != target) {
            tweenFrom[key] = cur;
            tweenTo[key] = target;
            tweenDur[key] = ms;
            tweenStart[key] = now;
        }
        return cur;
    }

    private float current(int key, long now) {
        long dur = Math.max(1, (long) (tweenDur[key] / Math.max(.2f, speedMul)));
        double p = Math.min(1, (now - tweenStart[key]) / (double) dur);
        double e = p < .5 ? 4 * p * p * p : 1 - Math.pow(-2 * p + 2, 3) / 2;
        return (float) (tweenFrom[key] + (tweenTo[key] - tweenFrom[key]) * e);
    }

    public void set(int key, float value) {
        spring[key] = value;
    }

    public float get(int key, float def) {
        return spring[key];
    }

    public static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    public static float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }
}
