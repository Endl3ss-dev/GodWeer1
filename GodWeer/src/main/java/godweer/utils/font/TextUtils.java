package godweer.utils.font;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Порт {@code org.privates.client.utils.render.text.TextUtils} из Private.
 * Различия: ширина символа берётся напрямую из атласа ({@link Font#getCharWidth(char, float)}),
 * поэтому перенос строки не создаёт ни одной строки-мусора и не тянет HashMap на каждый вызов.
 */
public final class TextUtils {

    public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            + "ʙғɢʜɪʟɴǫʀsxʏ"
            + "0123456789"
            + "!?@#$%^&*()-_=+[]{}|\\;:'\"<>,./`~"
            + "©™® ";

    public static final String ALLOWED_TO_SESSION = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            + "0123456789_";

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "1234567890";
    public static final String EMPTY = "";

    private TextUtils() {
    }

    public static String removeForbiddenCharacters(String input, String allowedCharacters) {
        if (input == null || input.isEmpty()) {
            return EMPTY;
        }

        StringBuilder result = new StringBuilder(input.length());
        int allowedLength = allowedCharacters.length();

        for (int i = 0, length = input.length(); i < length; i++) {
            char character = input.charAt(i);
            for (int j = 0; j < allowedLength; j++) {
                if (allowedCharacters.charAt(j) == character) {
                    result.append(character);
                    break;
                }
            }
        }

        return result.toString();
    }

    public static <T> boolean containsAll(List<T> mainList, List<T> findList) {
        return new HashSet<>(mainList).containsAll(findList);
    }

    public static List<String> splitLine(String text, Font font, float fontSize, float maxWidth, String splitter) {
        List<String> splitLines = new ArrayList<>(2);
        if (text == null || text.isEmpty()) {
            return splitLines;
        }

        StringBuilder currentLine = new StringBuilder(text.length());
        float currentLineWidth = 0.0F;
        float splitterWidth = font.getWidth(splitter, fontSize);

        for (String line : text.trim().split("\n")) {
            for (int i = 0, length = line.length(); i < length; i++) {
                char character = line.charAt(i);
                float charWidth = font.getCharWidth(character, fontSize);

                if (currentLineWidth + charWidth + splitterWidth > maxWidth && currentLine.length() > 0) {
                    splitLines.add(currentLine.toString());
                    currentLine.setLength(0);
                    currentLineWidth = 0.0F;
                }

                if (character != ' ' || currentLine.length() > 0) {
                    currentLine.append(character);
                    currentLineWidth += charWidth;
                }
            }

            if (currentLine.length() > 0) {
                splitLines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLineWidth = 0.0F;
            }
        }

        return splitLines;
    }

    public static int splitLineHeight(String text, Font font, float fontSize, float maxWidth, String splitter) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int lines = 0;
        StringBuilder currentLine = new StringBuilder(text.length());
        float currentLineWidth = 0.0F;
        float splitterWidth = font.getWidth(splitter, fontSize);

        for (String line : text.trim().split("\n")) {
            for (int i = 0, length = line.length(); i < length; i++) {
                char character = line.charAt(i);
                float charWidth = font.getCharWidth(character, fontSize);

                if (currentLineWidth + charWidth + splitterWidth > maxWidth && currentLine.length() > 0) {
                    lines++;
                    currentLine.setLength(0);
                    currentLineWidth = 0.0F;
                }

                if (character != ' ' || currentLine.length() > 0) {
                    currentLine.append(character);
                    currentLineWidth += charWidth;
                }
            }

            if (currentLine.length() > 0) {
                lines++;
                currentLine.setLength(0);
                currentLineWidth = 0.0F;
            }
        }

        return lines;
    }
}
