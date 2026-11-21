package tech.lightclient.autobuy;

import java.text.Normalizer;
import java.util.Locale;

/** Normalizes auction text for robust matching. */
public final class TextNormalizer {
    private TextNormalizer() {}
    public static String normalize(String s) {
        if (s == null) return "";
        String r = Normalizer.normalize(s, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        r = r.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
        return r.replaceAll("\\s+", " ").trim();
    }
    public static boolean nameMatches(String expected, String actual) {
        if (expected == null || expected.isBlank()) return true;
        String e = normalize(expected), a = normalize(actual);
        return !e.isEmpty() && (e.equals(a) || wholeWord(a, e));
    }
    public static boolean keywordIn(String haystack, String needle) {
        if (needle == null || needle.isBlank()) return true;
        return wholeWord(normalize(haystack), normalize(needle));
    }
    private static boolean wholeWord(String text, String sub) {
        if (text.equals(sub)) return true;
        return text.contains(" " + sub + " ") || text.startsWith(sub + " ") || text.endsWith(" " + sub);
    }
}
