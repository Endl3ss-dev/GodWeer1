package swerve.acd;

import java.util.Locale;

/**
 * Правило распознавания: по каким маркерам в тексте сервера понимать событие
 * конкретной категорией и с какой важностью.
 *
 * <p>Совпадение ищет в нижнем регистре по {@link Locale#ROOT} (не по локали системы:
 * в турецкой {@code 'I'} → {@code 'ı'}, и маркер «grim» перестал бы находиться в
 * «GRIM»). Маркеры — подстроки, поэтому у правила есть три списка:
 * {@code allOf} (должны встретиться все), {@code anyOf} (достаточно одного) и
 * {@code noneOf} (не должно встретиться ни одного). Двойная проверка нужна потому,
 * что названия защит часто совпадают с обычными словами: «matrix», «hawk», «polar»
 * сервер может писать в совсем другом контексте, и одинокого слова здесь мало.
 *
 * <p>Правило никогда не делает вывод «на сервере стоит античит X»: оно говорит лишь,
 * что текст от сервера содержит такое название. Достоверность такой классификации —
 * {@link AcdConfidence#HEURISTIC}, и {@link AcdFormatter} печатает это у каждой записи.
 */
public final class AcdRule {

    private final String id;
    private final AcdCategory target;
    private final AcdSeverity severity;
    private final String[] allOf;
    private final String[] anyOf;
    private final String[] noneOf;
    private final String reason;

    /** Только «или»: хотя бы один из маркеров должен встретиться. */
    public static AcdRule of(String id, AcdCategory target, AcdSeverity severity, String reason, String... anyOf) {
        return new AcdRule(id, target, severity, reason, null, anyOf, null);
    }

    /** «Или» плюс исключения. */
    public static AcdRule excluding(String id, AcdCategory target, AcdSeverity severity, String reason,
                                     String[] anyOf, String[] noneOf) {
        return new AcdRule(id, target, severity, reason, null, anyOf, noneOf);
    }

    /** «И» плюс исключения: все маркеры обязаны быть в тексте. */
    public static AcdRule conjunct(String id, AcdCategory target, AcdSeverity severity, String reason,
                                    String[] allOf, String[] noneOf) {
        return new AcdRule(id, target, severity, reason, allOf, null, noneOf);
    }

    private AcdRule(String id, AcdCategory target, AcdSeverity severity, String reason,
                    String[] allOf, String[] anyOf, String[] noneOf) {
        boolean empty = (allOf == null || allOf.length == 0) && (anyOf == null || anyOf.length == 0);
        if (empty) {
            throw new IllegalArgumentException("Правило " + id + " обязано иметь хотя бы один маркер");
        }
        this.id = id;
        this.target = target;
        this.severity = severity;
        this.reason = reason;
        this.allOf = lower(allOf);
        this.anyOf = lower(anyOf);
        this.noneOf = lower(noneOf);
    }

    public String id() {
        return this.id;
    }

    public AcdCategory target() {
        return this.target;
    }

    public AcdSeverity severity() {
        return this.severity;
    }

    /** Почему сработало правило — попадает в {@code Details:} каждой записи. */
    public String reason() {
        return this.reason;
    }

    /**
     * Какой именно маркер встретился — нужен, чтобы в отчёте было написано
     * фактическое совпадение («grim»), а не пересказ всего списка правил.
     *
     * @return маркер либо {@code null}, если правило не сработало
     */
    public String matchedMarker(String lowerText) {
        if (lowerText == null || lowerText.isEmpty() || !matches(lowerText)) {
            return null;
        }
        for (int i = 0; i < this.anyOf.length; i++) {
            if (lowerText.contains(this.anyOf[i])) {
                return this.anyOf[i];
            }
        }
        // в конъюнктивном правиле значимы все маркеры — перечисляем их целиком
        return this.allOf.length > 0 ? String.join("+", this.allOf) : null;
    }

    /**
     * @param lowerText текст источника, уже переведённый в нижний регистр
     */
    public boolean matches(String lowerText) {
        if (lowerText == null || lowerText.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.allOf.length; i++) {
            if (!lowerText.contains(this.allOf[i])) {
                return false;
            }
        }
        if (this.anyOf.length > 0) {
            boolean hit = false;
            for (int i = 0; i < this.anyOf.length; i++) {
                if (lowerText.contains(this.anyOf[i])) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                return false;
            }
        }
        for (int i = 0; i < this.noneOf.length; i++) {
            if (lowerText.contains(this.noneOf[i])) {
                return false;
            }
        }
        return true;
    }

    private static String[] lower(String[] values) {
        if (values == null) {
            return new String[0];
        }
        String[] copy = new String[values.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = values[i] == null ? "" : values[i].toLowerCase(Locale.ROOT);
        }
        return copy;
    }
}
