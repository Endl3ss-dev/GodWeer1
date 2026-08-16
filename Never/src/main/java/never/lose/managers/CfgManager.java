package never.lose.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import never.lose.NeverLose;
import never.lose.modules.api.Module;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeListSetting;
import never.lose.modules.api.setting.Setting;
import never.lose.modules.impl.Render.ClickGui;
import never.lose.modules.impl.Render.Cosmetics;
import never.lose.utils.client.ClientFiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class CfgManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MM.dd.yyyy");
    private static final long AUTO_SAVE_INTERVAL = 15_000L;
    private static final String DEFAULT_AUTOSAVE = "autosave";

    private static boolean applying;
    private static boolean dirty;
    private static long lastSave;

    private CfgManager() {
    }

    public static void init() {
        ClientFiles.configsDir();
        if (isAutoSave()) {
            String last = ClientPrefixManager.getLastConfig();
            String name = last.isEmpty() ? DEFAULT_AUTOSAVE : last;
            if (exists(name)) {
                load(name);
            } else {
                BindManager.load();
            }
        } else {
            BindManager.load();
        }
        lastSave = System.currentTimeMillis();
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> flush());
    }

    public static void markDirty() {
        if (!applying) {
            dirty = true;
        }
    }

    public static boolean isAutoSave() {
        return ClientPrefixManager.isAutoSave();
    }

    public static void setAutoSave(boolean value) {
        ClientPrefixManager.setAutoSave(value);
        if (value) {
            dirty = true;
        }
    }

    public static boolean save(String name) {
        String cfg = sanitizeName(name);
        if (cfg.isEmpty()) {
            return false;
        }
        if (!write(cfg, today())) {
            return false;
        }
        ClientPrefixManager.setLastConfig(cfg);
        dirty = false;
        lastSave = System.currentTimeMillis();
        return true;
    }

    public static boolean delete(String name) {
        String cfg = sanitizeName(name);
        if (cfg.isEmpty()) {
            return false;
        }
        File file = ClientFiles.configFile(cfg);
        if (!file.exists()) {
            return false;
        }
        boolean removed = file.delete();
        if (removed && cfg.equalsIgnoreCase(ClientPrefixManager.getLastConfig())) {
            ClientPrefixManager.setLastConfig("");
        }
        return removed;
    }

    public static boolean load(String name) {
        String cfg = sanitizeName(name);
        if (cfg.isEmpty()) {
            return false;
        }
        File file = ClientFiles.configFile(cfg);
        if (!file.exists()) {
            return false;
        }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            apply(json);
            ClientPrefixManager.setLastConfig(cfg);
            dirty = false;
            lastSave = System.currentTimeMillis();
            return true;
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to load cfg {}: {}", cfg, e.getMessage());
            return false;
        }
    }

    public static boolean exists(String name) {
        String cfg = sanitizeName(name);
        return !cfg.isEmpty() && ClientFiles.configFile(cfg).exists();
    }

    public static List<ConfigEntry> list() {
        List<ConfigEntry> result = new ArrayList<>();
        File dir = ClientFiles.configsDir();
        File[] files = dir.listFiles((file, fileName) -> fileName.toLowerCase(Locale.ROOT).endsWith(".json"));
        if (files == null) {
            return result;
        }
        for (File file : files) {
            String raw = file.getName();
            String name = raw.substring(0, raw.length() - 5);
            result.add(new ConfigEntry(name, readDate(file)));
        }
        result.sort(Comparator.comparing(ConfigEntry::name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public static void openFolder() {
        ClientFiles.openFolder(ClientFiles.configsDir());
    }

    public static File folder() {
        return ClientFiles.configsDir();
    }

    private static void tick() {
        if (!isAutoSave() || !dirty) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastSave < AUTO_SAVE_INTERVAL) {
            return;
        }
        flush();
    }

    public static void flush() {
        if (!isAutoSave()) {
            return;
        }
        String last = ClientPrefixManager.getLastConfig();
        String name = last.isEmpty() ? DEFAULT_AUTOSAVE : last;
        String date = existingDate(name);
        if (write(name, date)) {
            if (last.isEmpty()) {
                ClientPrefixManager.setLastConfig(name);
            }
            dirty = false;
            lastSave = System.currentTimeMillis();
        }
    }

    private static boolean write(String name, String date) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("name", name);
            root.addProperty("date", date == null || date.isBlank() ? today() : date);

            JsonObject modules = new JsonObject();
            for (Module module : ModuleManager.get().getModules()) {
                JsonObject object = new JsonObject();
                object.addProperty("enabled", module.isEnabled() && !opensScreen(module));
                if (module.getKey() != -1) {
                    String key = BindManager.displayKey(module.getKey());
                    if (!key.isEmpty()) {
                        object.addProperty("key", key);
                    }
                }
                JsonObject settings = new JsonObject();
                JsonObject settingBinds = new JsonObject();
                for (Setting setting : module.getSettings()) {
                    settings.addProperty(setting.getName(), setting.getValueAsString());
                    if (setting instanceof BooleanSetting booleanSetting && booleanSetting.getKey() != -1) {
                        String key = BindManager.displayKey(booleanSetting.getKey());
                        if (!key.isEmpty()) {
                            settingBinds.addProperty(setting.getName(), key);
                        }
                    }
                    if (setting instanceof ModeListSetting modeList) {
                        JsonObject children = new JsonObject();
                        for (BooleanSetting child : modeList.getSettings()) {
                            children.addProperty(child.getName(), child.getValue());
                        }
                        object.add(setting.getName() + "$list", children);
                    }
                }
                if (settings.size() > 0) {
                    object.add("settings", settings);
                }
                if (settingBinds.size() > 0) {
                    object.add("settingBinds", settingBinds);
                }
                modules.add(module.getName(), object);
            }
            root.add("modules", modules);
            Files.writeString(ClientFiles.configFile(name).toPath(), GSON.toJson(root), StandardCharsets.UTF_8);
            BindManager.save();
            return true;
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to save cfg {}: {}", name, e.getMessage());
            return false;
        }
    }

    private static void apply(JsonObject root) {
        applying = true;
        try {
            JsonObject modules = root.has("modules") && root.get("modules").isJsonObject()
                    ? root.getAsJsonObject("modules")
                    : new JsonObject();
            for (Module module : ModuleManager.get().getModules()) {
                JsonObject object = modules.has(module.getName()) && modules.get(module.getName()).isJsonObject()
                        ? modules.getAsJsonObject(module.getName())
                        : null;
                if (object == null) {
                    continue;
                }
                if (object.has("key")) {
                    module.setKeySilent(BindManager.parseKey(object.get("key").getAsString()));
                } else {
                    module.setKeySilent(-1);
                }
                if (object.has("settings") && object.get("settings").isJsonObject()) {
                    JsonObject settings = object.getAsJsonObject("settings");
                    for (Setting setting : module.getSettings()) {
                        if (!settings.has(setting.getName())) {
                            continue;
                        }
                        try {
                            setting.setValueFromString(settings.get(setting.getName()).getAsString());
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (object.has("settingBinds") && object.get("settingBinds").isJsonObject()) {
                    JsonObject binds = object.getAsJsonObject("settingBinds");
                    for (Setting setting : module.getSettings()) {
                        if (setting instanceof BooleanSetting booleanSetting && binds.has(setting.getName())) {
                            booleanSetting.setKey(BindManager.parseKey(binds.get(setting.getName()).getAsString()));
                        }
                    }
                }
                for (Setting setting : module.getSettings()) {
                    if (!(setting instanceof ModeListSetting modeList)) {
                        continue;
                    }
                    String extra = setting.getName() + "$list";
                    if (!object.has(extra) || !object.get(extra).isJsonObject()) {
                        continue;
                    }
                    JsonObject children = object.getAsJsonObject(extra);
                    for (BooleanSetting child : modeList.getSettings()) {
                        if (children.has(child.getName())) {
                            child.setValue(children.get(child.getName()).getAsBoolean());
                        }
                    }
                }
                boolean enabled = object.has("enabled") && object.get("enabled").getAsBoolean();
                if (opensScreen(module)) {
                    enabled = false;
                }
                if (module.isEnabled() != enabled) {
                    module.setEnabled(enabled);
                }
            }
            BindManager.save();
        } finally {
            applying = false;
        }
    }

    private static boolean opensScreen(Module module) {
        return module instanceof ClickGui || module instanceof Cosmetics;
    }

    private static String readDate(File file) {
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (json.has("date")) {
                String date = json.get("date").getAsString();
                if (date != null && !date.isBlank()) {
                    return date;
                }
            }
        } catch (Exception ignored) {
        }
        return LocalDate.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()).format(DATE);
    }

    private static String existingDate(String name) {
        File file = ClientFiles.configFile(name);
        if (!file.exists()) {
            return today();
        }
        return readDate(file);
    }

    private static String sanitizeName(String name) {
        if (name == null) {
            return "";
        }
        return ClientFiles.sanitize(name.trim());
    }

    private static String today() {
        return LocalDate.now().format(DATE);
    }

    public record ConfigEntry(String name, String date) {
    }
}
