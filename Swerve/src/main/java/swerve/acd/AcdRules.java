package swerve.acd;

/**
 * Таблица правил распознавания. Один проход по списку, первое совпадение выигрывает,
 * поэтому порядок — от частных формулировок к общим.
 *
 * <h2>Как добавить категорию или распознавание</h2>
 * <ol>
 *   <li>новая категория — один элемент в {@link AcdCategory};</li>
 *   <li>новое распознавание — один элемент в {@link #TABLE} здесь;</li>
 *   <li>ничего в {@link AcdCollector}, {@link AcdStore}, {@link AcdFormatter} и окне
 *       настроек менять не нужно: категории и правила читаются списком.</li>
 * </ol>
 *
 * <h2>Чего здесь сознательно нет</h2>
 * Маркеров, по которым можно было бы «подстроить поведение под античит»: таблица только
 * называет то, что сервер уже написал текстом. Одиночные спорные слова не используются —
 * «matrix», «hawk» и «polar» сервер может писать про ресурсы, мобов и биомы, поэтому
 * такие названия требуют второго слова рядом ({@link AcdRule#conjunct}).
 */
public final class AcdRules {

    /** Названия защит, которые встречаются в игровом тексте почти только как названия защит. */
    private static final String[] AC_NAMES = {
            "grimac", "grim ac", "grim", "vulcan", "verus", "karhu", "ghostfish",
            "anticheat", "anti-cheat", "anti cheat", "античит",
    };

    /** Формулировки, которыми античиты описывают срабатывание. */
    private static final String[] FLAG_WORDS = {
            "flagged", "violation", "violated", "нарушение", "подозрительн",
    };

    /** Формулировки ванильного и античит-предупреждений о скорости. */
    private static final String[] SPEED_WORDS = {
            "moved too quickly", "too quickly", "speed check", "speedch", "слишком быстро",
    };

    private static final AcdRule[] TABLE = {
            // [!] Keep rule reason strings free of their own trigger markers so self-scan checks pass.
            AcdRule.of("ac:name", AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                    "в тексте, который прислал сервер, встретилось имя известной системы защиты; "
                            + "внутренний флаг защиты клиенту не передаётся, так что это совпадение, а не доказательство",
                    AC_NAMES),

            AcdRule.excluding("flag:violation", AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                    "текст похож на уведомление о срабатывании проверки; такие же формулировки бывают у плагинов и в переписке админов",
                    FLAG_WORDS, new String[] {"no violation", "without violation"}),

            AcdRule.of("packet:bad", AcdCategory.ANTI_CHEAT_WARNING, AcdSeverity.WARNING,
                    "сервер жалуется на порядок или корректность пакетов",
                    "badpackets", "bad packet", "illegal packet", "packet order", "некорректный пакет"),

            AcdRule.of("move:speed", AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                    "формулировка про превышение скорости движения; ванильный сервер пишет такое в свой лог, "
                            + "в чат оно попадает только от плагина",
                    SPEED_WORDS),

            AcdRule.of("move:flight", AcdCategory.FLAG_NOTIFICATION, AcdSeverity.WARNING,
                    "текст про полёт или невозможность летать",
                    "flying is not enabled", "cannot fly", "flight detected", "aircheck", "полёт не разрешён"),

            AcdRule.conjunct("move:setback", AcdCategory.MOVEMENT_DIAGNOSTIC, AcdSeverity.NOTICE,
                    "сервер сообщает, что вернул игрока на свою позицию",
                    new String[] {"teleport"}, new String[] {"teleport potion", "end portal"}),

            AcdRule.excluding("kick:ban", AcdCategory.KICK_DISCONNECT, AcdSeverity.CRITICAL,
                    "текст похож на объявление блокировки; само отключение видно только по пакету причины",
                    new String[] {"banned", "permban", "tempban", "забанен", "blacklisted"},
                    new String[] {"banned items", "banned commands", "ban list"}),

            AcdRule.of("net:timeout", AcdCategory.NETWORK_DIAGNOSTIC, AcdSeverity.NOTICE,
                    "текст про таймаут или потерю соединения",
                    "timed out", "timeout", "connection lost", "время ожидания"),
    };

    private AcdRules() {
    }

    /** Полный список правил — нужен проверке {@code tests/check_acd.py} и отчёту о покрытии. */
    public static AcdRule[] all() {
        return TABLE.clone();
    }

    /**
     * Первое подошедшее правило.
     *
     * @param lowerText текст источника в нижнем регистре (см. {@link AcdRule#matches(String)})
     * @return правило либо {@code null}, если совпадений нет — тогда категоризацию
     *         даёт источник, а не текст
     */
    public static AcdRule match(String lowerText) {
        if (lowerText == null || lowerText.isEmpty()) {
            return null;
        }
        for (int i = 0, size = TABLE.length; i < size; i++) {
            if (TABLE[i].matches(lowerText)) {
                return TABLE[i];
            }
        }
        return null;
    }
}
