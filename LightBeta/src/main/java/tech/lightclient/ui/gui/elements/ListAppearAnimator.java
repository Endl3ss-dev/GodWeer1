package tech.lightclient.ui.gui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Поэтапные (каскадные) анимации списка: новые элементы плавно выдвигаются,
 * пропавшие плавно тают и удаляются только после конца затухания.
 * Элементы сопоставляются по строковому ключу, а не по индексу, поэтому
 * подходит и для списков, которые целиком пересоздаются при обновлении данных.
 */
public final class ListAppearAnimator<T> {

    /** Длительность появления/исчезновения одного элемента, сек. */
    private static final float DURATION = 0.28f;
    /** Задержка между соседними элементами каскада, сек. */
    private static final float STAGGER = 0.05f;
    /** Ограничение задержки, чтобы длинный список не выезжал бесконечно. */
    private static final float MAX_DELAY = 0.45f;
    /** Скорость переезда элемента на новое место (при смене порядка). */
    private static final float MOVE_SPEED = 12f;

    public static final class Entry<T> {
        private final String key;
        private T value;
        private float progress;
        private float delay;
        private boolean leaving;
        private boolean placed;
        private float x;
        private float y;

        private Entry(String key, T value, float delay) {
            this.key = key;
            this.value = value;
            this.delay = delay;
        }

        public T value() {
            return value;
        }

        /** 0 — элемента не видно, 1 — полностью на месте. */
        public float progress() {
            return progress;
        }

        public boolean leaving() {
            return leaving;
        }

        public float x() {
            return x;
        }

        /** Позиция в координатах контента (без учёта скролла). */
        public float y() {
            return y;
        }
    }

    private final Function<T, String> keyFn;
    private final List<Entry<T>> entries = new ArrayList<>();

    public ListAppearAnimator(Function<T, String> keyFn) {
        this.keyFn = keyFn;
    }

    /** Живые и ещё тающие элементы: живые идут в порядке исходного списка. */
    public List<Entry<T>> entries() {
        return entries;
    }

    /** Сопоставляет актуальный список с анимируемым состоянием. */
    public void sync(List<T> fresh) {
        List<Entry<T>> old = new ArrayList<>(entries);
        boolean[] used = new boolean[old.size()];
        List<Entry<T>> result = new ArrayList<>(fresh.size() + old.size());

        int added = 0;
        for (T value : fresh) {
            String key = keyFn.apply(value);
            Entry<T> match = null;
            for (int i = 0; i < old.size(); i++) {
                Entry<T> candidate = old.get(i);
                if (used[i] || candidate.leaving || !candidate.key.equals(key)) continue;
                used[i] = true;
                match = candidate;
                break;
            }
            if (match == null) {
                result.add(new Entry<>(key, value, Math.min(MAX_DELAY, added++ * STAGGER)));
            } else {
                match.value = value;
                result.add(match);
            }
        }

        int removed = 0;
        for (int i = 0; i < old.size(); i++) {
            Entry<T> entry = old.get(i);
            if (used[i]) continue;
            if (!entry.leaving) {
                entry.leaving = true;
                entry.delay = Math.min(MAX_DELAY, removed++ * STAGGER);
            }
            result.add(entry);
        }

        entries.clear();
        entries.addAll(result);
    }

    /** Продвигает анимации и выбрасывает полностью исчезнувшие элементы. */
    public void update(float dt) {
        if (dt <= 0f) return;
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry<T> entry = entries.get(i);
            if (entry.delay > 0f) {
                entry.delay = Math.max(0f, entry.delay - dt);
                if (entry.delay > 0f) continue;
            }
            float step = dt / DURATION;
            entry.progress = clamp01(entry.progress + (entry.leaving ? -step : step));
            if (entry.leaving && entry.progress <= 0f) {
                entries.remove(i);
            }
        }
    }

    /**
     * Задаёт место элемента в координатах контента (без скролла): первый кадр —
     * мгновенно, дальше плавный переезд, чтобы смена порядка не дёргала карточки.
     */
    public void place(Entry<T> entry, float targetX, float targetY, float dt) {
        if (!entry.placed) {
            entry.x = targetX;
            entry.y = targetY;
            entry.placed = true;
            return;
        }
        float k = Math.min(1f, Math.max(0f, dt) * MOVE_SPEED);
        entry.x += (targetX - entry.x) * k;
        entry.y += (targetY - entry.y) * k;
    }

    /** Заново проигрывает каскад появления (например, при открытии GUI). */
    public void restart() {
        entries.removeIf(entry -> entry.leaving);
        for (int i = 0; i < entries.size(); i++) {
            Entry<T> entry = entries.get(i);
            entry.progress = 0f;
            entry.delay = Math.min(MAX_DELAY, i * STAGGER);
            entry.placed = false;
        }
    }

    private static float clamp01(float value) {
        return value < 0f ? 0f : Math.min(value, 1f);
    }
}
