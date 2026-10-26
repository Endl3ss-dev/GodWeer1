package swerve.acd;

import java.util.Locale;

/**
 * Приведение текста источника к виду, пригодному и для поиска, и для чтения.
 *
 * <p>Три отдельных шага — не педантизм, а защита от конкретных поломок:
 * <ul>
 *   <li>коды форматирования ({@code §}) и управляющие символы ломают выравнивание в
 *       отчёте и попадают в буфер обмена, поэтому вырезаются;</li>
 *   <li>поиск идёт по нижнему регистру {@link Locale#ROOT} и только по первым
 *       {@value #MAX_SCAN_CHARS} символам: сообщение на сотню тысяч символов не должно
 *       прогоняться через всю таблицу правил — это цена одного сообщения в чужом коде;</li>
 *   <li>в журнал идёт ужатый фрагмент, а не исходник целиком: {@link AcdEvent#raw()}
 *       сохраняет оригинал отдельно.</li>
 * </ul>
 */
public final class AcdClassifier {

    /** Сколько символов текста участвует в поиске по правилам. */
    public static final int MAX_SCAN_CHARS = 2048;

    /** Длина фрагмента, который попадает в нормализованное описание. */
    public static final int MAX_SNIPPET_CHARS = 400;

    private AcdClassifier() {
    }

    /**
     * Чистый текст для человекочитаемой части записи: без кодов форматирования,
     * со схлопнутыми пробелами и переносами, обрезанный по {@value #MAX_SNIPPET_CHARS}.
     *
     * @return пустая строка, если текста нет или он состоял из невидимых символов
     */
    public static String snippet(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(Math.min(raw.length(), MAX_SNIPPET_CHARS + 16));
        boolean pendingSpace = false;
        for (int i = 0, size = raw.length(); i < size && builder.length() < MAX_SNIPPET_CHARS; i++) {
            char chr = raw.charAt(i);
            // [!] Strip '§' and its following format code without clearing pendingSpace;
            // also drop a trailing lone '§' at the end of the string.
            if (chr == '\u00a7') {
                if (i + 1 < size) {
                    i++;
                }
                continue;
            }
            if (Character.isISOControl(chr) || Character.isWhitespace(chr)) {
                if (builder.length() > 0) {
                    pendingSpace = true;
                }
                continue;
            }
            if (pendingSpace && builder.length() > 0) {
                builder.append(' ');
            }
            pendingSpace = false;
            builder.append(chr);
        }
        return builder.toString().trim();
    }

    /** Ключ поиска по правилам: нижний регистр и ограничение по длине. */
    public static String searchKey(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String window = text.length() > MAX_SCAN_CHARS ? text.substring(0, MAX_SCAN_CHARS) : text;
        return window.toLowerCase(Locale.ROOT);
    }

    /**
     * @param searchKey уже подготовленный {@link #searchKey(String)}
     * @return первое подошедшее правило либо {@code null}
     */
    public static AcdRule classify(String searchKey) {
        return AcdRules.match(searchKey);
    }
}
