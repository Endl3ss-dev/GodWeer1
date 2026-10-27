package net.skycore.client.gui.menu;

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
    /**
     * Сила размытия, 0 - 100 (в процентах).
     *
     * <p>Значение по умолчанию 60 даёт множитель радиуса ровно 1.0
     * ({@link #blurFactor()}), то есть совпадает с прежним жёстко заданным радиусом.
     */
    public static float blurStrength = 60.0F;
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

    private static final String DIRECTORY = "skycore";
    private static final String FILE_NAME = "clickgui-menu.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private MenuPrefs() {
    }

    /** Масштаб меню в долях: 1.0 — как в оригинале. */
    public static float menuScale() {
        return scale / 100.0F;
    }

    /**
     * Множитель радиуса фонового размытия.
     *
     * <p>60% силы (значение по умолчанию) дают 1.0 — прежнее поведение. Диапазон
     * множителя 0.25..1.5: на нуле размытие остаётся лёгким, а не пропадает совсем
     * (полностью отключается отдельным переключателем {@link #blur}).
     */
    public static float blurFactor() {
        float normalized = Math.max(0.0F, Math.min(100.0F, blurStrength)) / 100.0F;
        return 0.25F + normalized * 1.25F;
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
            blurStrength = clamp(number(json, "blurStrength", blurStrength), 0.0F, 100.0F);
            dim = clamp(number(json, "dim", dim), 0.0F, 90.0F);
            gradient = bool(json, "gradient", gradient);
            animations = bool(json, "animations", animations);
            animSpeed = clamp(number(json, "animSpeed", animSpeed), 0.5F, 2.0F);
            scale = clamp(number(json, "scale", scale), 70.0F, 130.0F);
            palette = Math.floorMod((int) number(json, "palette", palette), MenuPalette.PALETTES.length);
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
        json.addProperty("blurStrength", blurStrength);
        json.addProperty("dim", dim);
        json.addProperty("gradient", gradient);
        json.addProperty("animations", animations);
        json.addProperty("animSpeed", animSpeed);
        json.addProperty("scale", scale);
        json.addProperty("palette", palette);

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
