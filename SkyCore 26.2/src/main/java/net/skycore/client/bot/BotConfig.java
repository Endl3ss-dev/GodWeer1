package net.skycore.client.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.skycore.client.SkycoreClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Настройки бота BedWars.
 *
 * <p>Устроен по образцу {@code gui.menu.MenuPrefs}: те же Gson-чтение и запись в
 * отдельный файл {@code skycore/bot.json}, та же устойчивость к ошибкам (сломанный
 * файл не мешает открыть меню — берутся значения по умолчанию).
 *
 * <p>Отдельный файл, а не общий {@code ConfigManager}, выбран сознательно: общий
 * конфиг описывает модули и их настройки, а бот модулем не является. Подмешивать его
 * в формат пользовательских конфигов значило бы ломать совместимость со старыми
 * профилями ради сущности, которая к модулям не относится.
 *
 * <p>Адрес сервера здесь не хранится: он зафиксирован задачей
 * ({@link BotScript#SERVER}) и не настраивается.
 */
public final class BotConfig {

    /** Префикс ника: первый бот — {@code SwerveBot1}, второй — {@code SwerveBot2}. */
    public static String nickPrefix = BotScript.DEFAULT_NICK_PREFIX;
    /** Сколько ботов запускать одновременно. */
    public static int botCount = BotScript.DEFAULT_BOTS;
    /** Пароль для {@code /reg}. */
    public static String password = BotScript.DEFAULT_PASSWORD;
    /** Ник, которому бот отправляет {@code /party invite}. */
    public static String ownerNick = BotScript.DEFAULT_OWNER;

    /** Ячейка голосования за элитры: 12 или 14. */
    public static int daytimeCell = 11;
    public static int gameTimeCell = 11;
    public static int elytraCell = BotScript.VOTE_CELL_A;
    /** Ячейка голосования за PvP: 12 (1.8) или 14 (1.12). */
    public static int pvpCell = BotScript.VOTE_CELL_A;
    /** Ячейка голосования за ресурсы: 12 (1.8) или 14 (1.12). */
    public static int resourcesCell = BotScript.VOTE_CELL_A;

    /**
     * Ники ботов по текущему префиксу и количеству.
     *
     * <p>Кэш пересчитывается только при изменении префикса или числа ботов, поэтому
     * отрисовка списка ботов не склеивает строки в кадре — она читает готовые значения.
     */
    private static final String[] NICKNAMES = new String[BotScript.MAX_BOTS];

    /**
     * Число ботов текстом — кэш для отрисовки.
     *
     * <p>Пересчитывается в {@link #refreshNicknames()}, то есть только при изменении
     * префикса или числа ботов и при чтении конфига. В кадре интерфейс читает эту
     * готовую строку вместо {@code Integer.toString}.
     */
    private static String nickPreview = "";
    public static String nicknamePreview() { return nickPreview; }

    private static String COUNT_TEXT = Integer.toString(botCount);

    private static final String DIRECTORY = "skycore";
    private static final String FILE_NAME = "bot.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        refreshNicknames();
    }

    private BotConfig() {
    }

    /** Полный путь файла настроек бота. */
    public static Path file() {
        return Path.of(DIRECTORY, FILE_NAME);
    }

    /**
     * Читает настройки с диска.
     *
     * <p>Любая ошибка — не повод падать: вкладка бота обязана открыться со значениями
     * по умолчанию, иначе повреждённый файл заблокировал бы часть меню.
     */
    public static void load() {
        Path path = file();
        if (!Files.isRegularFile(path)) {
            refreshNicknames();
            return;
        }
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();

            nickPrefix = sanitizePrefix(string(json, "nickPrefix", nickPrefix));
            botCount = clampCount((int) number(json, "botCount", botCount));
            password = sanitize(string(json, "password", password), 32);
            ownerNick = sanitizeNick(string(json, "ownerNick", ownerNick));
            elytraCell = normalizeCell((int) number(json, "elytraCell", elytraCell));
            pvpCell = normalizeCell((int) number(json, "pvpCell", pvpCell));
            daytimeCell = normalizeTime((int) number(json, "daytimeCell", 11));
            gameTimeCell = normalizeTime((int) number(json, "gameTimeCell", 11));
            resourcesCell = normalizeCell((int) number(json, "resourcesCell", resourcesCell));
            for (int i = 0; i < BotScript.MAX_BOTS; i++) {
                String key = "appearance" + i;
                if (json.has(key) && json.get(key).isJsonObject()) {
                    JsonObject a = json.getAsJsonObject(key);
                    BotAppearance.restore(i, (int) number(a, "color", 0),
                            Boolean.parseBoolean(string(a, "compact", "false")),
                            Boolean.parseBoolean(string(a, "notifications", "true")));
                }
            }
        } catch (IOException | RuntimeException failure) {
            SkycoreClient.LOGGER.warn("Failed to read bot settings from {}, using defaults", path, failure);
        }
        refreshNicknames();
    }

    /** Записывает настройки на диск. Ошибка логируется и не влияет на игру. */
    public static void save() {
        JsonObject json = new JsonObject();
        json.addProperty("nickPrefix", nickPrefix);
        json.addProperty("botCount", botCount);
        json.addProperty("password", password);
        json.addProperty("ownerNick", ownerNick);
        json.addProperty("elytraCell", elytraCell);
        json.addProperty("pvpCell", pvpCell);
        json.addProperty("resourcesCell", resourcesCell);
        json.addProperty("daytimeCell", daytimeCell);
        json.addProperty("gameTimeCell", gameTimeCell);
        for (int i = 0; i < BotScript.MAX_BOTS; i++) {
            JsonObject a = new JsonObject();
            a.addProperty("color", BotAppearance.colorIndex(i));
            a.addProperty("compact", BotAppearance.compact(i));
            a.addProperty("notifications", BotAppearance.notifications(i));
            json.add("appearance" + i, a);
        }

        Path path = file();
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException failure) {
            SkycoreClient.LOGGER.warn("Failed to write bot settings to {}", path, failure);
            BedWarsBot.INSTANCE.notifyResult("[bot.json] Не удалось сохранить настройки / Save failed", true);
        }
    }

    /**
     * Устанавливает префикс ника и пересчитывает кэш ников.
     *
     * <p>Из ника убираются пробелы и слеши — с ними команда {@code /party invite}
     * и вход на сервер не работают.
     */
    public static void setNickPrefix(String value) {
        nickPrefix = sanitizePrefix(value);
        refreshNicknames();
    }

    /** Устанавливает число ботов и пересчитывает кэш ников. */
    public static void setBotCount(int value) {
        int clamped = clampCount(value);
        if (clamped != botCount) {
            botCount = clamped;
            refreshNicknames();
        }
    }

    public static void setPassword(String value) {
        password = sanitize(value, 32);
    }

    public static void setOwnerNick(String value) {
        ownerNick = sanitizeNick(value);
    }

    /**
     * Ячейка голосования по индексу из {@link BotScript}.
     *
     * @return 12 или 14; для голосований «позже» всегда 12, они не настраиваются
     */
    private static int normalizeTime(int cell) { return cell == 13 || cell == 15 ? cell : 11; }

    public static int voteCell(int voteIndex) {
        return switch (voteIndex) {
            case BotScript.VOTE_DAYTIME -> normalizeTime(daytimeCell);
            case BotScript.VOTE_GAME_TIME -> normalizeTime(gameTimeCell);
            case BotScript.VOTE_ELYTRA -> normalizeCell(elytraCell);
            case BotScript.VOTE_PVP -> normalizeCell(pvpCell);
            case BotScript.VOTE_RESOURCES -> normalizeCell(resourcesCell);
            default -> BotScript.VOTE_CELL_A;
        };
    }

    /** Сохраняет выбор ячейки голосования; для недоступных голосований игнорируется. */
    public static void setVoteCell(int voteIndex, int cell) {
        if (voteIndex == BotScript.VOTE_DAYTIME) { daytimeCell = normalizeTime(cell); return; }
        if (voteIndex == BotScript.VOTE_GAME_TIME) { gameTimeCell = normalizeTime(cell); return; }
        int normalized = normalizeCell(cell);
        switch (voteIndex) {
            case BotScript.VOTE_ELYTRA -> elytraCell = normalized;
            case BotScript.VOTE_PVP -> pvpCell = normalized;
            case BotScript.VOTE_RESOURCES -> resourcesCell = normalized;
            default -> {
                // «Время суток» и «Время игры» по постановке появятся позже
            }
        }
    }

    /** Ник бота с номером {@code index} (0-based): {@code SwerveBot1}, {@code SwerveBot2}, … */
    public static String nickname(int index) {
        if (index < 0 || index >= NICKNAMES.length) {
            return nickPrefix;
        }
        String cached = NICKNAMES[index];
        return cached == null ? nickPrefix : cached;
    }

    /** Сколько ников сейчас в кэше (равно {@link #botCount}). */
    public static int nicknameCount() {
        return botCount;
    }

    /**
     * Число ботов текстом.
     *
     * <p>Кэш пересчитывается только в {@link #setBotCount} и при чтении конфига,
     * поэтому интерфейс не вызывает {@code Integer.toString} в кадре отрисовки.
     */
    public static String botCountText() {
        return COUNT_TEXT;
    }

    /**
     * Команда приглашения владельца пати: {@code /party invite Swerve}.
     *
     * <p>Собирается по требованию, а не в кадре: вызывается из действий интерфейса
     * и из будущей реализации бота.
     */
    public static String inviteCommand() {
        return "/party invite " + ownerNick;
    }

    /** Команда регистрации: {@code /reg SwerveClient}. */
    public static String registerCommand() {
        return "/reg " + password;
    }

    /** Команда приёма пати первым ботом: {@code /party accept SwerveBot1}. */
    public static String acceptCommand() {
        return "/party accept " + nickname(0);
    }

    // ---- внутреннее

    /** Пересчитывает кэш ников и текста числа ботов (вызывается вне кадра отрисовки). */
    private static void refreshNicknames() {
        for (int i = 0; i < NICKNAMES.length; i++) {
            NICKNAMES[i] = nickPrefix + (i + 1);
        }
        COUNT_TEXT = Integer.toString(botCount);
        nickPreview = botCount > 1 ? NICKNAMES[0] + " … " + NICKNAMES[botCount - 1] : NICKNAMES[0];
    }

    private static int clampCount(int value) {
        return Math.max(BotScript.MIN_BOTS, Math.min(BotScript.MAX_BOTS, value));
    }

    /** Приводит произвольное число к допустимой ячейке голосования: 12 или 14. */
    private static int normalizeCell(int cell) {
        return cell == BotScript.VOTE_CELL_B ? BotScript.VOTE_CELL_B : BotScript.VOTE_CELL_A;
    }

    private static String sanitizeNick(String value) {
        return sanitize(value, 16).replaceAll("[^A-Za-z0-9_]", "");
    }

    private static String sanitizePrefix(String value) {
        String cleaned = sanitize(value, 15).replaceAll("[^A-Za-z0-9_]", "");
        return cleaned.isEmpty() ? BotScript.DEFAULT_NICK_PREFIX : cleaned;
    }

    /** Убирает пробелы, слеши и управляющие символы; ограничивает длину. */
    private static String sanitize(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length() && builder.length() < maxLength; i++) {
            char character = value.charAt(i);
            if (character > ' ' && character != '/' && character != '\\') {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    private static String string(JsonObject json, String key, String fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : fallback;
    }

    private static float number(JsonObject json, String key, float fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return json.get(key).getAsFloat();
        } catch (RuntimeException failure) {
            return fallback;
        }
    }
}
