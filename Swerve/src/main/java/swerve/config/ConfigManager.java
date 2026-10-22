package swerve.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.Setting;
import swerve.ui.menu.MenuPrefs;
import swerve.ui.menu.MenuTheme;
import swerve.ui.newgui.GuiConfig;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Конфиг клиента: состояния модулей, бинды, значения настроек, настройки GUI и меню.
 *
 * <p>Файл: {@code config/swerve/config.json}, формат — см. {@link #FORMAT}.
 * Другое хранилище рядом не дублируется: друзья лежат в {@code swerve/friends.json},
 * позиции HUD — в {@code swerve/hud.json}, косметика — в {@code swerve/cosmetics.json},
 * настройки ViewModel — в {@code swerve/viewmodel.json} (модуль помечает себя через
 * {@link Module#usesOwnConfig()}, и тогда здесь остаются только его включённость и бинд).
 *
 * <h2>Когда пишется на диск</h2>
 * <ul>
 *   <li>через {@value #SAVE_INTERVAL_MS} мс после правки, но не чаще одного раза в этот
 *       интервал — {@link #tick()} на конце клиентского тика;</li>
 *   <li>при закрытии меню — {@link #saveIfDirty()};</li>
 *   <li>при выходе из игры — {@link ClientLifecycleEvents#CLIENT_STOPPING}.</li>
 * </ul>
 *
 * <p>Запись идёт только когда конфиг помечен изменённым ({@link #markDirty()}),
 * а сама сериализация сравнивает результат с последним записанным текстом: клик по
 * вкладке меню, который ничего не поменял, файл не перезаписывает. В кадре (render)
 * обращений к файлу нет; в тике — только чтение двух полей.
 *
 * <h2>Устойчивость</h2>
 * <ul>
 *   <li>файла нет, он пуст, побит или от другой версии формата — клиент стартует
 *       со значениями по умолчанию, конфиг не перезаписывается до первой правки;</li>
 *   <li>модуль или настройка из файла, которых больше нет в клиенте, игнорируются
 *       и исчезают при следующем сохранении;</li>
 *   <li>значения неверного типа игнорируются, числа обрезаются по границам настройки —
 *       см. {@link SettingCodec};</li>
 *   <li>запись атомарная: сначала {@code config.json.tmp}, затем {@code move} —
 *       падение игры во время сохранения не оставит половину JSON.</li>
 * </ul>
 */
public final class ConfigManager {

    /** Версия формата файла. Несовпадение — файл игнорируется целиком. */
    public static final int FORMAT = 1;

    private static final long SAVE_INTERVAL_MS = 2_000L;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/config.json");

    private static boolean loaded;
    private static boolean dirty;
    private static long lastSaveMs;
    private static String lastWritten;

    private ConfigManager() {
    }

    /**
     * Загрузка конфига и подписка на автосохранение.
     *
     * <p>Вызывать строго после {@link ModuleManager#init()}: восстанавливать нечего,
     * пока модули не созданы, а ключ модуля — имя его класса.
     */
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveIfDirty());
        load();
    }

    // ---- чтение

    /** Читает файл и применяет значения. Повторный вызов ничего не делает. */
    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                Swerve.LOGGER.warn("Config {} is not a JSON object, using defaults", FILE);
                return;
            }

            JsonObject object = root.getAsJsonObject();
            int format = readInt(object, "format", FORMAT);
            if (format != FORMAT) {
                Swerve.LOGGER.warn("Config format {} is not supported (expected {}), using defaults", format, FORMAT);
                return;
            }

            applyModules(object.get("modules"));
            applyGui(object.get("gui"));
            applyMenu(object.get("menu"));

            // Анимации меню кэшируют множитель скорости — после загрузки его нужно пересчитать.
            MenuPrefs.sync();
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load config {}", FILE, exception);
        }
    }

    private static void applyModules(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject modules = element.getAsJsonObject();
        List<Module> all = ModuleManager.getModules();
        for (int i = 0, size = all.size(); i < size; i++) {
            Module module = all.get(i);
            JsonElement stored = modules.get(key(module));
            if (stored == null || !stored.isJsonObject()) {
                continue;
            }

            JsonObject data = stored.getAsJsonObject();
            try {
                // Порядок важен: настройки до включения, иначе onEnable увидит значения по умолчанию.
                // Модуль со своим файлом настроек (ViewModel) значения берёт оттуда.
                if (!module.usesOwnConfig()) {
                    applySettings(module, data.get("settings"));
                }
                module.setKey(readInt(data, "key", module.getKey()));

                JsonElement enabled = data.get("enabled");
                if (enabled != null && enabled.isJsonPrimitive() && enabled.getAsJsonPrimitive().isBoolean()) {
                    module.setEnabledQuiet(enabled.getAsBoolean());
                }
            } catch (Exception exception) {
                // Один проблемный модуль не должен отменять восстановление остальных.
                Swerve.LOGGER.warn("Failed to restore module {} from config", key(module), exception);
            }
        }
    }

    private static void applySettings(Module module, JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject values = element.getAsJsonObject();
        List<Setting> settings = module.getSettings();
        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            JsonElement value = values.get(SettingCodec.keyOf(setting));
            if (value != null) {
                SettingCodec.decode(setting, value);
            }
        }
    }

    private static void applyGui(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject gui = element.getAsJsonObject();
        applyFloat(GuiConfig.blurStrength(), gui, "blur");
        applyFloat(GuiConfig.settingsBlurStrength(), gui, "settingsBlur");
        applyFloat(GuiConfig.backgroundAlpha(), gui, "backgroundAlpha");
    }

    private static void applyMenu(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject menu = element.getAsJsonObject();
        MenuPrefs.sounds = readBoolean(menu, "sounds", MenuPrefs.sounds);
        MenuPrefs.hoverSounds = readBoolean(menu, "hoverSounds", MenuPrefs.hoverSounds);
        MenuPrefs.sliderSounds = readBoolean(menu, "sliderSounds", MenuPrefs.sliderSounds);
        MenuPrefs.blur = readBoolean(menu, "blur", MenuPrefs.blur);
        MenuPrefs.gradient = readBoolean(menu, "gradient", MenuPrefs.gradient);
        MenuPrefs.animations = readBoolean(menu, "animations", MenuPrefs.animations);

        // Границы повторяют ползунки меню: иначе правка файла руками выводит UI за экран.
        MenuPrefs.volume = clamp(readFloat(menu, "volume", MenuPrefs.volume), 0.0f, 100.0f);
        MenuPrefs.dim = clamp(readFloat(menu, "dim", MenuPrefs.dim), 0.0f, 90.0f);
        MenuPrefs.animSpeed = clamp(readFloat(menu, "animSpeed", MenuPrefs.animSpeed), 0.5f, 2.0f);
        MenuPrefs.scale = clamp(readFloat(menu, "scale", MenuPrefs.scale), 70.0f, 130.0f);

        MenuTheme.setPalette(readInt(menu, "palette", MenuTheme.palette));
    }

    // ---- запись

    /** Помечает конфиг изменённым; сама запись произойдёт в {@link #tick()} или при закрытии. */
    public static void markDirty() {
        dirty = true;
    }

    /** Сохраняет конфиг, если были правки. */
    public static void saveIfDirty() {
        if (dirty) {
            save();
        }
    }

    private static void tick() {
        if (!dirty) {
            return;
        }
        if (System.currentTimeMillis() - lastSaveMs < SAVE_INTERVAL_MS) {
            return;
        }
        save();
    }

    /** Сериализует и записывает конфиг. Содержимое, совпавшее с последней записью, не пишется. */
    public static void save() {
        lastSaveMs = System.currentTimeMillis();

        try {
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT);
            root.addProperty("client", Swerve.MOD_NAME);
            root.add("modules", encodeModules());
            root.add("gui", encodeGui());
            root.add("menu", encodeMenu());

            String text = GSON.toJson(root);
            if (text.equals(lastWritten)) {
                dirty = false;
                return;
            }

            Files.createDirectories(FILE.getParent());
            Path temp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temp, text, StandardCharsets.UTF_8);
            try {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception atomicUnsupported) {
                // Некоторые ФС (сетевые диски Windows) не умеют ATOMIC_MOVE — обычная замена.
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
            lastWritten = text;
            dirty = false;
        } catch (Exception exception) {
            // Флаг остаётся поднятым: следующая попытка через SAVE_INTERVAL_MS,
            // иначе правка потерялась бы до следующего действия пользователя.
            Swerve.LOGGER.warn("Failed to save config {}", FILE, exception);
        }
    }

    private static JsonObject encodeModules() {
        JsonObject modules = new JsonObject();
        List<Module> all = ModuleManager.getModules();
        for (int i = 0, size = all.size(); i < size; i++) {
            Module module = all.get(i);
            JsonObject data = new JsonObject();
            data.addProperty("enabled", module.isEnable());
            data.addProperty("key", module.getKey());
            if (!module.usesOwnConfig()) {
                data.add("settings", encodeSettings(module));
            }
            modules.add(key(module), data);
        }
        return modules;
    }

    private static JsonObject encodeSettings(Module module) {
        JsonObject settings = new JsonObject();
        List<Setting> list = module.getSettings();
        for (int j = 0, count = list.size(); j < count; j++) {
            Setting setting = list.get(j);
            JsonElement value = SettingCodec.encode(setting);
            if (value != null) {
                settings.add(SettingCodec.keyOf(setting), value);
            }
        }
        return settings;
    }

    private static JsonObject encodeGui() {
        JsonObject gui = new JsonObject();
        gui.addProperty("blur", GuiConfig.blurStrength().get());
        gui.addProperty("settingsBlur", GuiConfig.settingsBlurStrength().get());
        gui.addProperty("backgroundAlpha", GuiConfig.backgroundAlpha().get());
        return gui;
    }

    private static JsonObject encodeMenu() {
        JsonObject menu = new JsonObject();
        menu.addProperty("sounds", MenuPrefs.sounds);
        menu.addProperty("volume", MenuPrefs.volume);
        menu.addProperty("hoverSounds", MenuPrefs.hoverSounds);
        menu.addProperty("sliderSounds", MenuPrefs.sliderSounds);
        menu.addProperty("blur", MenuPrefs.blur);
        menu.addProperty("dim", MenuPrefs.dim);
        menu.addProperty("gradient", MenuPrefs.gradient);
        menu.addProperty("animations", MenuPrefs.animations);
        menu.addProperty("animSpeed", MenuPrefs.animSpeed);
        menu.addProperty("scale", MenuPrefs.scale);
        menu.addProperty("palette", MenuTheme.palette);
        return menu;
    }

    // ---- разбор значений

    /**
     * Ключ модуля — простое имя класса. Оно не зависит от отображаемого имени и от
     * локали, поэтому переименование модуля в UI не теряет его настройки.
     */
    private static String key(Module module) {
        return module.getClass().getSimpleName();
    }

    private static void applyFloat(FloatSetting setting, JsonObject data, String name) {
        JsonElement value = data.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            setting.setValue(value.getAsFloat());
        }
    }

    private static int readInt(JsonObject data, String name, int fallback) {
        JsonElement value = data.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return value.getAsInt();
        }
        return fallback;
    }

    private static float readFloat(JsonObject data, String name, float fallback) {
        JsonElement value = data.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return value.getAsFloat();
        }
        return fallback;
    }

    private static boolean readBoolean(JsonObject data, String name, boolean fallback) {
        JsonElement value = data.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return value.getAsBoolean();
        }
        return fallback;
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }
}
