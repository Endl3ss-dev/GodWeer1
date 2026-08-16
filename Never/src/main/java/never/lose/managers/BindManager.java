package never.lose.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import never.lose.NeverLose;
import never.lose.modules.api.Module;
import never.lose.utils.client.ClientFiles;
import never.lose.utils.input.KeyStorage;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BindManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean suppressSave;

    private BindManager() {
    }

    public static void init() {
        load();
    }

    public static boolean setBind(Module module, int key) {
        if (module == null) {
            return false;
        }
        suppressSave = true;
        try {
            module.setKeySilent(key);
        } finally {
            suppressSave = false;
        }
        save();
        CfgManager.markDirty();
        return true;
    }

    public static boolean removeBind(Module module) {
        if (module == null) {
            return false;
        }
        if (module.getKey() == -1) {
            return false;
        }
        return setBind(module, -1);
    }

    public static void clearAll() {
        suppressSave = true;
        try {
            for (Module module : ModuleManager.get().getModules()) {
                module.setKeySilent(-1);
            }
        } finally {
            suppressSave = false;
        }
        save();
        CfgManager.markDirty();
    }

    public static List<BindEntry> list() {
        List<BindEntry> result = new ArrayList<>();
        for (Module module : ModuleManager.get().getModules()) {
            if (module.getKey() == -1) {
                continue;
            }
            String key = displayKey(module.getKey());
            if (key.isEmpty()) {
                continue;
            }
            result.add(new BindEntry(module.getName(), key));
        }
        return result;
    }

    public static void onModuleKeyChanged() {
        if (suppressSave) {
            return;
        }
        save();
        CfgManager.markDirty();
    }

    public static void load() {
        File file = ClientFiles.bindsFile();
        if (!file.exists()) {
            save();
            return;
        }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            suppressSave = true;
            try {
                for (Module module : ModuleManager.get().getModules()) {
                    module.setKeySilent(-1);
                }
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    Module module = ModuleManager.get().get(entry.getKey());
                    if (module == null || entry.getValue() == null || !entry.getValue().isJsonPrimitive()) {
                        continue;
                    }
                    int key = parseKey(entry.getValue().getAsString());
                    if (key != -1) {
                        module.setKeySilent(key);
                    }
                }
            } finally {
                suppressSave = false;
            }
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to load binds: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            JsonObject json = new JsonObject();
            for (Module module : ModuleManager.get().getModules()) {
                if (module.getKey() == -1) {
                    continue;
                }
                String key = displayKey(module.getKey());
                if (!key.isEmpty()) {
                    json.addProperty(module.getName(), key);
                }
            }
            Files.writeString(ClientFiles.bindsFile().toPath(), GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            NeverLose.LOG.warn("[NeverLose] Failed to save binds: {}", e.getMessage());
        }
    }

    public static void applyKeys(Map<String, String> keys) {
        suppressSave = true;
        try {
            for (Module module : ModuleManager.get().getModules()) {
                String raw = keys.get(module.getName());
                module.setKeySilent(raw == null ? -1 : parseKey(raw));
            }
        } finally {
            suppressSave = false;
        }
        save();
    }

    public static Map<String, String> snapshot() {
        Map<String, String> result = new LinkedHashMap<>();
        for (Module module : ModuleManager.get().getModules()) {
            if (module.getKey() == -1) {
                continue;
            }
            String key = displayKey(module.getKey());
            if (!key.isEmpty()) {
                result.put(module.getName(), key);
            }
        }
        return result;
    }

    public static String displayKey(int key) {
        if (key == -1) {
            return "";
        }
        String name = KeyStorage.getKey(key);
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return Integer.toString(key);
    }

    public static int parseKey(String raw) {
        if (raw == null || raw.isBlank()) {
            return -1;
        }
        String key = raw.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(" ", "")
                .replace("KEY_", "")
                .replace("KEYBOARD.", "");
        if (key.isEmpty() || key.equals("NONE") || key.equals("NULL")) {
            return -1;
        }

        Integer mapped = KeyStorage.getKey(key);
        if (mapped != null && mapped != -1) {
            return mapped;
        }
        if (key.matches("\\d{2,}")) {
            try {
                return Integer.parseInt(key);
            } catch (NumberFormatException ignored) {
            }
        }
        return switch (key) {
            case "SHIFT", "L_SHIFT", "LEFTSHIFT", "LEFT_SHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "R_SHIFT", "RIGHTSHIFT", "RIGHT_SHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "CTRL", "CONTROL", "LCTRL", "LEFTCTRL", "LEFT_CONTROL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL", "RIGHTCTRL", "RIGHT_CONTROL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "ALT", "LALT", "LEFTALT", "LEFT_ALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "RALT", "RIGHTALT", "RIGHT_ALT" -> GLFW.GLFW_KEY_RIGHT_ALT;
            case "ESC", "ESCAPE" -> GLFW.GLFW_KEY_ESCAPE;
            case "RETURN", "ENTER" -> GLFW.GLFW_KEY_ENTER;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "SPACEBAR", "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "TILDE", "GRAVE", "`" -> GLFW.GLFW_KEY_GRAVE_ACCENT;
            case "PLUS" -> GLFW.GLFW_KEY_EQUAL;
            case "MINUS", "_" -> GLFW.GLFW_KEY_MINUS;
            case "DOT", "PERIOD", "." -> GLFW.GLFW_KEY_PERIOD;
            case "COMMA", "," -> GLFW.GLFW_KEY_COMMA;
            case "SLASH", "/" -> GLFW.GLFW_KEY_SLASH;
            case "LMB", "MOUSELEFT", "M1", "BUTTON1" -> 0;
            case "RMB", "MOUSERIGHT", "M2", "BUTTON2" -> 1;
            case "MMB", "MOUSEMIDDLE", "M3", "BUTTON3" -> 2;
            case "M4", "BUTTON4" -> 3;
            case "M5", "BUTTON5" -> 4;
            case "KP1", "NUMPAD_1" -> GLFW.GLFW_KEY_KP_1;
            case "KP2", "NUMPAD_2" -> GLFW.GLFW_KEY_KP_2;
            case "KP3", "NUMPAD_3" -> GLFW.GLFW_KEY_KP_3;
            case "KP4", "NUMPAD_4" -> GLFW.GLFW_KEY_KP_4;
            case "KP5", "NUMPAD_5" -> GLFW.GLFW_KEY_KP_5;
            case "KP6", "NUMPAD_6" -> GLFW.GLFW_KEY_KP_6;
            case "KP7", "NUMPAD_7" -> GLFW.GLFW_KEY_KP_7;
            case "KP8", "NUMPAD_8" -> GLFW.GLFW_KEY_KP_8;
            case "KP9", "NUMPAD_9" -> GLFW.GLFW_KEY_KP_9;
            case "KP0", "NUMPAD_0", "NUMPAD0" -> GLFW.GLFW_KEY_KP_0;
            default -> -1;
        };
    }

    public record BindEntry(String module, String key) {
    }
}
