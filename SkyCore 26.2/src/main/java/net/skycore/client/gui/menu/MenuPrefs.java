package net.skycore.client.gui.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.skycore.client.SkycoreClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Настройки меню ClickGUI.
 *
 * <p>Порт {@code swerve.ui.menu.MenuPrefs} (Swerve, порт Arcanic Visuals, GPL-3.0).
 *
 * <p>В источнике настройки жили только статическими полями и терялись при перезапуске.
 * Здесь они сохраняются в {@code skycore/clickgui-menu.json} отдельным файлом:
 * общий конфиг клиента ({@code ConfigManager}) описывает модули, их настройки и темы,
 * и подмешивать туда оформление меню значило бы менять формат пользовательских
 * конфигов и ломать совместимость со старыми профилями.
 *
 * <p>Запись выполняется из потока игры (закрытие экрана), поэтому она неблокирующая
 * по факту: файл маленький и пишется один раз за закрытие меню.
 */
public final class MenuPrefs {

    // ---- звук
    /** Звуки меню. */
    public static boolean sounds = true;
    /** Громкость звуков меню, 0 - 100. */
    public static float volume = 70.0F;
    /** Звук при наведении на элемент. */
    public static boolean hoverSounds = true;
    /** Щелчки ползунков. */
    public static boolean sliderSounds = true;

    // ---- вид
    /** Размытие мира под меню. */
    public static boolean blur = true;
    /** Затемнение мира, 0 - 90. */
    public static float dim = 40.0F;
    /** Градиенты в элементах меню: тумблеры, ползунки, плашки — во всех палитрах. */
    public static boolean gradient = true;

    // ---- поведение
    /** Плавные переходы. */
    public static boolean animations = true;
    /** Множитель скорости анимаций, 0.5 - 2. */
    public static float animSpeed = 1.0F;
    /** Размер меню, 70 - 130 (в процентах). */
    public static float scale = 100.0F;

    /** Индекс выбранной палитры (см. {@link MenuPalette}). */
    public static int palette = 0;

    /**
     * Последние выбранные цвета (порт {@code Setting.Colour.PRESETS} из Arcanic).
     *
     * <p>В источнике список жил в статике настройки и терялся при перезапуске; здесь
     * он сохраняется вместе с остальными настройками меню — отдельный файл для этого
     * заводить не нужно. Значение — ARGB без альфы в старшем байте у сравнимых цветов,
     * как в источнике.
     */
    public static final int RECENT_MAX = 10;

    private static final int[] RECENT = {
            0xFFFFFFFF, 0xFF111318, 0xFF8B5CF6, 0xFFC084FC, 0xFFF472B6,
            0xFFF43F5E, 0xFFFB923C, 0xFFFACC15, 0xFF4ADE80, 0xFF22D3EE,
    };

    private static int recentCount = RECENT.length;

    /** Копия списка последних цветов (для отрисовки и кликов). */
    public static int[] recents() {
        return RECENT;
    }

    public static int recentCount() {
        return recentCount;
    }

    /**
     * Добавляет цвет в начало списка последних (без дублей, максимум {@link #RECENT_MAX}).
     * Возвращает {@code true}, если список изменился и его нужно сохранить.
     */
    public static boolean rememberColour(int colour) {
        int rgb = colour & 0xFFFFFF;
        int found = -1;
        for (int i = 0; i < recentCount; i++) {
            if ((RECENT[i] & 0xFFFFFF) == rgb) {
                found = i;
                break;
            }
        }
        if (found == 0) {
            return false;
        }
        int start = found > 0 ? found : Math.min(recentCount, RECENT_MAX - 1);
        for (int i = start; i > 0; i--) {
            RECENT[i] = RECENT[i - 1];
        }
        RECENT[0] = colour | 0xFF000000;
        if (found < 0 && recentCount < RECENT_MAX) {
            recentCount++;
        }
        return true;
    }

    private static final String DIRECTORY = "skycore";
    private static final String FILE_NAME = "clickgui-menu.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private MenuPrefs() {
    }

    /** Масштаб меню в долях: 1.0 — как в оригинале. */
    public static float menuScale() {
        return scale / 100.0F;
    }

    /** Применяет настройки к анимациям (порт {@code MenuPrefs.sync}). */
    public static void sync() {
        MenuAnim.speedMul = animations ? Math.max(0.2F, animSpeed) : 1.0F;
    }

    /** Полный путь файла настроек меню. */
    public static Path file() {
        return Path.of(DIRECTORY, FILE_NAME);
    }

    /**
     * Читает настройки с диска. Любая ошибка — не повод падать: меню обязано
     * открыться с настройками по умолчанию, иначе сломанный файл заблокирует GUI.
     */
    public static void load() {
        Path path = file();
        if (!Files.isRegularFile(path)) {
            sync();
            return;
        }
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();

            sounds = bool(json, "sounds", sounds);
            volume = clamp(number(json, "volume", volume), 0.0F, 100.0F);
            hoverSounds = bool(json, "hoverSounds", hoverSounds);
            sliderSounds = bool(json, "sliderSounds", sliderSounds);
            blur = bool(json, "blur", blur);
            dim = clamp(number(json, "dim", dim), 0.0F, 90.0F);
            gradient = bool(json, "gradient", gradient);
            animations = bool(json, "animations", animations);
            animSpeed = clamp(number(json, "animSpeed", animSpeed), 0.5F, 2.0F);
            scale = clamp(number(json, "scale", scale), 70.0F, 130.0F);
            palette = Math.floorMod((int) number(json, "palette", palette), MenuPalette.PALETTES.length);
            loadRecents(json);
        } catch (IOException | RuntimeException failure) {
            SkycoreClient.LOGGER.warn("Failed to read menu preferences from {}, using defaults", path, failure);
        }
        MenuPalette.palette = palette;
        sync();
    }

    /** Записывает настройки на диск. Ошибка логируется и не влияет на игру. */
    public static void save() {
        palette = Math.floorMod(MenuPalette.palette, MenuPalette.PALETTES.length);
        JsonObject json = new JsonObject();
        json.addProperty("sounds", sounds);
        json.addProperty("volume", volume);
        json.addProperty("hoverSounds", hoverSounds);
        json.addProperty("sliderSounds", sliderSounds);
        json.addProperty("blur", blur);
        json.addProperty("dim", dim);
        json.addProperty("gradient", gradient);
        json.addProperty("animations", animations);
        json.addProperty("animSpeed", animSpeed);
        json.addProperty("scale", scale);
        json.addProperty("palette", palette);
        saveRecents(json);

        Path path = file();
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException failure) {
            SkycoreClient.LOGGER.warn("Failed to write menu preferences to {}", path, failure);
        }
    }

    /** Читает список последних цветов. Битые элементы пропускаются, список не обнуляется. */
    private static void loadRecents(JsonObject json) {
        if (!json.has("recentColours") || !json.get("recentColours").isJsonArray()) {
            return;
        }
        JsonArray array = json.getAsJsonArray("recentColours");
        int count = 0;
        for (JsonElement element : array) {
            if (count >= RECENT_MAX) {
                break;
            }
            if (element == null || !element.isJsonPrimitive()) {
                continue;
            }
            try {
                RECENT[count++] = element.getAsInt() | 0xFF000000;
            } catch (RuntimeException failure) {
                // нечисловое значение в файле — пропускаем, остальное читаем дальше
            }
        }
        recentCount = count;
    }

    /** Пишет только реально занятые слоты, чтобы файл не разрастался нулями. */
    private static void saveRecents(JsonObject json) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < recentCount; i++) {
            array.add(RECENT[i]);
        }
        json.add("recentColours", array);
    }

    private static boolean bool(JsonObject json, String key, boolean fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsBoolean() : fallback;
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

    private static float clamp(float value, float min, float max) {
        if (Float.isNaN(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}
