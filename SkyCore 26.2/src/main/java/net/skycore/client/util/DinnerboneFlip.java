package net.skycore.client.util;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Состояние «переворота» (Dinnerbone) — мост между модулем и миксинами рендера.
 *
 * <p><b>Что происходит в ваниле.</b> Модель игрока переворачивается, если его имя —
 * {@code Dinnerbone} или {@code Grumm}: проверка живёт в рендерере сущностей и
 * складывает результат в рендер-состояние (флаг «вверх ногами»), а рендер по этому
 * флагу делает {@code translate(0, рост, 0) + rotate(180°, Z)}. Переворот —
 * <i>чисто клиентский рендер</i>: каждый клиент решает сам, переворачивать ли
 * игрока, опираясь только на имя, которое ему прислал сервер. Никакого пакета
 * «переверни меня» в протоколе нет.
 *
 * <p>Отсюда два пути, которые реализует {@code DinnerboneModule}:
 * <ul>
 *   <li><b>Видно всем</b> — только если имя игрока на сервере станет
 *       {@code Dinnerbone}/{@code Grumm} (ник через {@code /nick} и подобные). Клиент
 *       имя поменять не может: это серверные данные;</li>
 *   <li><b>Видно только вам</b> — выставить флаг рендер-состояния локально. Так
 *       делает этот класс: переворот совпадает с ванильным «Dinnerbone» один в один,
 *       потому что дальше работает обычный ванильный код.</li>
 * </ul>
 *
 * <p>Имя поля флага в разных версиях отличается ({@code isUpsideDown},
 * {@code flipUpsideDown}…), поэтому оно ищется рефлексией один раз и кэшируется:
 * если в вашей сборке 26.2 поле переименовали, модуль честно сообщит, что локальный
 * переворот недоступен, вместо тихой поломки рендера.
 */
public final class DinnerboneFlip {

    /** Имена, которые ванильный клиент переворачивает сам. */
    private static final String[] VANILLA_NAMES = {"Dinnerbone", "Grumm"};

    /** Известные имена поля флага, от самого вероятного к остальным. */
    private static final String[] FLAG_NAMES = {"isUpsideDown", "flipUpsideDown", "upsideDown"};

    private static volatile boolean localEnabled;
    private static volatile boolean flipOthers;

    /** Найденное поле флага; {@code null} — в сборке его нет. */
    private static Field stateFlag;
    private static boolean flagSearched;
    private static volatile boolean unsupported;
    private static volatile boolean warnedUnsupported;

    private DinnerboneFlip() {
    }

    /** Включает/выключает локальный переворот (пишет модуль). */
    public static void configure(boolean enabled, boolean others) {
        localEnabled = enabled;
        flipOthers = others;
    }

    /** Нужно ли переворачивать эту сущность локально. */
    public static boolean shouldFlip(Entity entity) {
        if (!localEnabled || !(entity instanceof Player)) {
            return false;
        }
        return flipOthers || entity == Minecraft.getInstance().player;
    }

    /** Ванильное правило: имя {@code Dinnerbone}/{@code Grumm} (регистр не важен). */
    public static boolean isVanillaName(Entity entity) {
        String name = entity == null ? null : entity.getName().getString();
        if (name == null) {
            return false;
        }
        for (String candidate : VANILLA_NAMES) {
            if (candidate.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вызывается из миксина на выходе из {@code extractRenderState}: ставит флаг
     * переворота в рендер-состоянии, если этого игрока нужно перевернуть.
     */
    public static void markState(Object state, boolean flip) {
        if (!flip || state == null) {
            return;
        }
        Field field = flagField(state.getClass());
        if (field == null) {
            unsupported = true;
            return;
        }
        try {
            field.setBoolean(state, true);
        } catch (Throwable ignored) {
            // ничего не ломаем: просто не перевернём
        }
    }

    /**
     * Точно ли известно, что локальный переворот в этой сборке не работает.
     *
     * <p>Возвращает {@code true} только после того, как рендер реально попробовал
     * поставить флаг и не нашёл поля — до первого кадра ответ «неизвестно».
     */
    public static boolean knownUnsupported() {
        return unsupported;
    }

    /** Однократное предупреждение «локальный переворот недоступен». */
    public static boolean takeUnsupportedWarning() {
        if (warnedUnsupported) {
            return false;
        }
        warnedUnsupported = true;
        return true;
    }

    private static Field flagField(Class<?> type) {
        if (flagSearched) {
            return stateFlag;
        }
        flagSearched = true;
        for (String name : FLAG_NAMES) {
            Field field = find(type, name);
            if (field != null) {
                stateFlag = field;
                return field;
            }
        }
        // запасной поиск: любое boolean-поле с «upside»/«flip» в имени
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getType() != boolean.class) {
                    continue;
                }
                String lower = field.getName().toLowerCase();
                if (lower.contains("upside") || lower.contains("flip")) {
                    field.setAccessible(true);
                    stateFlag = field;
                    return field;
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static Field find(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(name);
                if (field.getType() == boolean.class) {
                    field.setAccessible(true);
                    return field;
                }
            } catch (NoSuchFieldException ignored) {
                // ищем выше по иерархии
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
