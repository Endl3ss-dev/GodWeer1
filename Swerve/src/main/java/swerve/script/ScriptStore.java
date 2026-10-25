package swerve.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище скриптов: список функций и глобальные переменные.
 *
 * <p>Файл — {@code config/swerve/scripts.json}. Формат плоский и читаемый:
 *
 * <pre>
 * {
 *   "format": 1,
 *   "variables": { "counter": "0" },
 *   "functions": [
 *     {
 *       "name": "Авто-еда",
 *       "key": 82,
 *       "stack": { "type": "trigger_tick", "values": [], "next": { … } }
 *     }
 *   ]
 * }
 * </pre>
 *
 * <p>Запись атомарная (через {@code .tmp}), как в {@link swerve.config.ConfigManager}:
 * падение игры во время сохранения не оставит половину JSON. Битый файл не мешает
 * запуску — клиент стартует с пустым проектом и пишет об этом в лог.
 */
public final class ScriptStore {

    public static final int FORMAT = 1;

    private static final long SAVE_INTERVAL_MS = 2_000L;
    private static final int MAX_BLOCKS = 4_000;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/scripts.json");

    private static final List<ScriptFunction> FUNCTIONS = new ArrayList<>();
    private static final List<ScriptFunction> VIEW = Collections.unmodifiableList(FUNCTIONS);
    private static final Map<String, String> VARIABLES = new LinkedHashMap<>();

    private static boolean loaded;
    private static boolean dirty;
    private static boolean hooksRegistered;
    private static long lastSaveMs;
    private static String lastWritten;

    private ScriptStore() {
    }

    public static void init() {
        if (!hooksRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
            ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveIfDirty());
            hooksRegistered = true;
        }
        load();
    }

    // ---- чтение

    public static List<ScriptFunction> getFunctions() {
        return VIEW;
    }

    public static Map<String, String> getVariables() {
        return VARIABLES;
    }

    public static ScriptFunction find(String name) {
        if (name == null) {
            return null;
        }
        for (int i = 0, size = FUNCTIONS.size(); i < size; i++) {
            ScriptFunction function = FUNCTIONS.get(i);
            if (function.getName().equalsIgnoreCase(name.trim())) {
                return function;
            }
        }
        return null;
    }

    /** Имя, которого ещё нет в проекте: «функция», «функция 2», … */
    public static String uniqueName(String base) {
        String root = base == null || base.isBlank() ? "функция" : base.trim();
        if (find(root) == null) {
            return root;
        }
        for (int i = 2; i < 1_000; i++) {
            String candidate = root + " " + i;
            if (find(candidate) == null) {
                return candidate;
            }
        }
        return root + " " + System.currentTimeMillis();
    }

    public static ScriptFunction create() {
        ScriptFunction function = new ScriptFunction(uniqueName("функция"));
        function.setHead(defaultHat());
        FUNCTIONS.add(function);
        markDirty();
        return function;
    }

    private static ScriptBlock defaultHat() {
        return new ScriptBlock(BlockType.TRIGGER_MANUAL);
    }

    public static void remove(ScriptFunction function) {
        if (function != null && FUNCTIONS.remove(function)) {
            ScriptEngine.stop(function);
            markDirty();
        }
    }

    public static void markDirty() {
        dirty = true;
    }

    public static void saveIfDirty() {
        if (dirty) {
            save();
        }
    }

    private static void tick() {
        if (!dirty || System.currentTimeMillis() - lastSaveMs < SAVE_INTERVAL_MS) {
            return;
        }
        save();
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        if (!Files.isRegularFile(FILE)) {
            createStarter();
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                Swerve.LOGGER.warn("Scripts {} is not a JSON object, project left empty", FILE);
                return;
            }

            JsonObject object = root.getAsJsonObject();
            int format = readInt(object, "format", FORMAT);
            if (format != FORMAT) {
                Swerve.LOGGER.warn("Scripts format {} is not supported (expected {}), project left empty", format, FORMAT);
                return;
            }

            JsonElement variables = object.get("variables");
            if (variables != null && variables.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : variables.getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        VARIABLES.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }

            JsonElement functions = object.get("functions");
            if (functions != null && functions.isJsonArray()) {
                for (JsonElement element : functions.getAsJsonArray()) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject data = element.getAsJsonObject();
                    String name = data.has("name") ? data.get("name").getAsString() : "функция";
                    ScriptFunction function = new ScriptFunction(name);
                    function.setKey(readInt(data, "key", -1));
                    function.setHead(decodeChain(data.get("stack"), 0));
                    if (find(function.getName()) == null) {
                        FUNCTIONS.add(function);
                    }
                }
            }
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load scripts {}", FILE, exception);
        }
    }

    /** Первый запуск: маленький пример, чтобы редактор не был пустым. */
    private static void createStarter() {
        ScriptFunction example = new ScriptFunction(uniqueName("Пример"));
        ScriptBlock hat = new ScriptBlock(BlockType.TRIGGER_MANUAL);

        ScriptBlock notify = new ScriptBlock(BlockType.NOTIFY);
        notify.set(0, "Привет! Это скрипт Swerve");

        ScriptBlock wait = new ScriptBlock(BlockType.WAIT);
        wait.set(0, "1");

        ScriptBlock hint = new ScriptBlock(BlockType.NOTIFY);
        hint.set(0, "Привяжи функцию к клавише и нажми её в игре");

        hat.setNext(notify);
        notify.setNext(wait);
        wait.setNext(hint);

        example.setHead(hat);
        FUNCTIONS.add(example);
        markDirty();
    }

    // ---- запись

    public static void save() {
        lastSaveMs = System.currentTimeMillis();

        try {
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT);
            root.addProperty("client", Swerve.MOD_NAME);

            JsonObject variables = new JsonObject();
            for (Map.Entry<String, String> entry : VARIABLES.entrySet()) {
                variables.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("variables", variables);

            JsonArray functions = new JsonArray();
            for (int i = 0, size = FUNCTIONS.size(); i < size; i++) {
                ScriptFunction function = FUNCTIONS.get(i);
                JsonObject data = new JsonObject();
                data.addProperty("name", function.getName());
                data.addProperty("key", function.getKey());
                if (function.getHead() != null) {
                    data.add("stack", encodeChain(function.getHead()));
                }
                functions.add(data);
            }
            root.add("functions", functions);

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
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
            lastWritten = text;
            dirty = false;
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to save scripts {}", FILE, exception);
        }
    }

    private static JsonObject encodeChain(ScriptBlock head) {
        ScriptBlock block = head;
        JsonObject first = null;
        JsonObject previous = null;

        while (block != null) {
            JsonObject data = new JsonObject();
            data.addProperty("type", block.getType() == null ? "unknown" : block.getType().id());

            JsonArray values = new JsonArray();
            for (int i = 0; i < block.slotCount(); i++) {
                values.add(block.get(i));
            }
            data.add("values", values);

            if (block.getInner() != null) {
                data.add("inner", encodeChain(block.getInner()));
            }

            if (first == null) {
                first = data;
            } else {
                previous.add("next", data);
            }
            previous = data;
            block = block.getNext();
        }
        return first;
    }

    private static ScriptBlock decodeChain(JsonElement element, int depth) {
        if (element == null || !element.isJsonObject() || depth > 200) {
            return null;
        }

        ScriptBlock head = null;
        ScriptBlock previous = null;
        JsonElement current = element;
        int guard = 0;

        while (current != null && current.isJsonObject() && depth + guard < MAX_BLOCKS) {
            JsonObject data = current.getAsJsonObject();
            BlockType type = BlockType.byId(data.has("type") ? data.get("type").getAsString() : null);
            if (type == null) {
                break;
            }

            ScriptBlock block = new ScriptBlock(type);
            JsonElement values = data.get("values");
            if (values != null && values.isJsonArray()) {
                JsonArray array = values.getAsJsonArray();
                for (int i = 0; i < block.slotCount() && i < array.size(); i++) {
                    JsonElement value = array.get(i);
                    if (value != null && value.isJsonPrimitive()) {
                        block.set(i, value.getAsString());
                    }
                }
            }

            if (type.container()) {
                block.setInner(decodeChain(data.get("inner"), depth + 1));
            }

            if (head == null) {
                head = block;
            } else {
                previous.setNext(block);
            }
            previous = block;
            current = data.get("next");
            guard++;
        }
        return head;
    }

    private static int readInt(JsonObject data, String name, int fallback) {
        JsonElement value = data.get(name);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return value.getAsInt();
        }
        return fallback;
    }

    /** Редактор меняет структуру напрямую; вызов напоминает сохранить изменения. */
    public static void touch() {
        markDirty();
    }
}
