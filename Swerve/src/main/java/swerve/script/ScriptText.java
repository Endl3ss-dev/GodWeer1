package swerve.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;
import java.util.Map;

/**
 * Подстановки в текстовых полях блоков.
 *
 * <p>В любом тексте можно писать {@code {имя}} — редактор заменит это на
 * переменную скрипта или на состояние клиента: {@code {player}}, {@code {health}},
 * {@code {food}}, {@code {x}}, {@code {y}}, {@code {z}}, {@code {fps}}, {@code {world}}.
 * Неизвестное имя остаётся как есть, чтобы опечатка была видна в уведомлении, а
 * не превращалась в пустую строку.
 */
public final class ScriptText {

    private ScriptText() {
    }

    public static String fill(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        Minecraft mc = Minecraft.getInstance();
        StringBuilder out = new StringBuilder(raw.length() + 16);
        int i = 0;
        while (i < raw.length()) {
            char chr = raw.charAt(i);
            if (chr == '{') {
                int end = raw.indexOf('}', i + 1);
                if (end > i + 1 && end - i <= 40) {
                    out.append(resolve(mc, raw.substring(i + 1, end)));
                    i = end + 1;
                    continue;
                }
            }
            out.append(chr);
            i++;
        }
        return out.toString();
    }

    private static String resolve(Minecraft mc, String key) {
        Map<String, String> variables = ScriptStore.getVariables();
        String variable = variables.get(key);
        if (variable != null) {
            return variable;
        }

        LocalPlayer player = mc == null ? null : mc.player;
        return switch (key) {
            case "player" -> player == null ? "" : player.getName().getString();
            case "health" -> player == null ? "0" : number(player.getHealth());
            case "food" -> player == null ? "0" : number(player.getFoodData().getFoodLevel());
            case "x" -> player == null ? "0" : number(player.getX());
            case "y" -> player == null ? "0" : number(player.getY());
            case "z" -> player == null ? "0" : number(player.getZ());
            case "fps" -> mc == null ? "0" : String.valueOf(mc.getFps());
            case "world" -> mc == null || mc.level == null ? "" : mc.level.dimension().location().toString();
            default -> "{" + key + "}";
        };
    }

    /** Разбор числа: запятая как разделитель тоже принимается. */
    public static double number(String raw) {
        String text = fill(raw).trim().replace(',', '.');
        if (text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    /** Обратное преобразование: целые без «.0», дробные — с двумя знаками. */
    public static String number(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        if (value == Math.rint(value) && Math.abs(value) < 1.0E15) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    /** Текст без подстановок — для полей, где важна ровно введённая строка. */
    public static String raw(String text) {
        return text == null ? "" : text;
    }
}
