package swerve.bot;

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
import java.util.List;

/**
 * Хранилище профилей ботов: {@code config/swerve/bots.json}.
 *
 * <p>Формат плоский, файл читается глазами:
 *
 * <pre>
 * {
 *   "format": 1,
 *   "bots": [
 *     { "name": "Ферма", "host": "play.example.ru", "port": 25565,
 *       "nickname": "Farmer1", "protocol": 769,
 *       "autoReconnect": true, "reconnectDelay": 5.0, "stepDelay": 0.4,
 *       "steps": [ { "type": "CONNECT", "values": [], "enabled": true } ] }
 *   ]
 * }
 * </pre>
 *
 * <p>Запись атомарная (через {@code .tmp}), как у остальных конфигов клиента.
 * Битый файл не мешает запуску: список остаётся пустым, в лог уходит warn.
 */
public final class BotStore {

    public static final int FORMAT = 1;

    private static final long SAVE_INTERVAL_MS = 2_000L;
    private static final int MAX_STEPS = 500;
    private static final int MAX_BOTS = 64;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/bots.json");

    private static final List<BotProfile> BOTS = new ArrayList<>();
    private static final List<BotProfile> VIEW = Collections.unmodifiableList(BOTS);

    private static boolean loaded;
    private static boolean dirty;
    private static boolean hooks;
    private static long lastSaveMs;
    private static String lastWritten;

    private BotStore() {
    }

    public static void init() {
        if (!hooks) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
            ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveIfDirty());
            hooks = true;
        }
        load();
    }

    public static List<BotProfile> bots() {
        return VIEW;
    }

    public static BotProfile create() {
        if (BOTS.size() >= MAX_BOTS) {
            return null;
        }
        BotProfile profile = new BotProfile();
        profile.setName(uniqueName("бот"));
        profile.setNickname("Bot" + (BOTS.size() + 1));
        BotStep connect = new BotStep(BotStepType.CONNECT);
        BotStep wait = new BotStep(BotStepType.WAIT);
        wait.set(0, "2");
        BotStep login = new BotStep(BotStepType.LOGIN);
        login.set(0, "пароль");
        profile.addStep(connect);
        profile.addStep(wait);
        profile.addStep(login);
        BOTS.add(profile);
        markDirty();
        return profile;
    }

    public static void duplicate(BotProfile source) {
        if (source == null || BOTS.size() >= MAX_BOTS) {
            return;
        }
        BotProfile copy = source.copy(uniqueName(source.getName() + " копия"));
        BOTS.add(copy);
        markDirty();
    }

    public static void remove(BotProfile profile) {
        if (profile == null) {
            return;
        }
        BotManager.stop(profile);
        BOTS.remove(profile);
        markDirty();
    }

    public static String uniqueName(String base) {
        String root = base == null || base.isBlank() ? "бот" : base.trim();
        if (find(root) == null) {
            return root;
        }
        for (int i = 2; i < 500; i++) {
            String candidate = root + " " + i;
            if (find(candidate) == null) {
                return candidate;
            }
        }
        return root + " " + System.currentTimeMillis();
    }

    public static BotProfile find(String name) {
        if (name == null) {
            return null;
        }
        for (int i = 0, size = BOTS.size(); i < size; i++) {
            BotProfile profile = BOTS.get(i);
            if (profile.getName().equalsIgnoreCase(name.trim())) {
                return profile;
            }
        }
        return null;
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
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                Swerve.LOGGER.warn("Bots {} is not a JSON object, list left empty", FILE);
                return;
            }
            JsonObject object = root.getAsJsonObject();
            int format = object.has("format") ? object.get("format").getAsInt() : FORMAT;
            if (format != FORMAT) {
                Swerve.LOGGER.warn("Bots format {} is not supported (expected {})", format, FORMAT);
                return;
            }

            JsonElement bots = object.get("bots");
            if (bots == null || !bots.isJsonArray()) {
                return;
            }
            for (JsonElement element : bots.getAsJsonArray()) {
                if (!element.isJsonObject() || BOTS.size() >= MAX_BOTS) {
                    continue;
                }
                BOTS.add(decode(element.getAsJsonObject()));
            }
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load bots {}", FILE, exception);
        }
    }

    private static BotProfile decode(JsonObject data) {
        BotProfile profile = new BotProfile();
        profile.setName(string(data, "name", "бот"));
        profile.setHost(string(data, "host", "localhost"));
        profile.setPort(intValue(data, "port", 25565));
        profile.setNickname(string(data, "nickname", "SwerveBot"));
        profile.setProtocol(intValue(data, "protocol", BotProfile.PROTOCOL_1_21_4));
        profile.setAutoReconnect(bool(data, "autoReconnect", true));
        profile.setReconnectDelay((float) number(data, "reconnectDelay", 5.0));
        profile.setStepDelay((float) number(data, "stepDelay", 0.4));
        profile.setLoopForever(bool(data, "loopForever", true));
        profile.setPassword(string(data, "password", "пароль"));

        JsonElement steps = data.get("steps");
        if (steps != null && steps.isJsonArray()) {
            for (JsonElement element : steps.getAsJsonArray()) {
                if (!element.isJsonObject() || profile.getSteps().size() >= MAX_STEPS) {
                    continue;
                }
                JsonObject stepData = element.getAsJsonObject();
                BotStepType type = BotStepType.byName(string(stepData, "type", ""));
                if (type == null) {
                    continue;
                }
                BotStep step = new BotStep(type);
                JsonElement values = stepData.get("values");
                if (values != null && values.isJsonArray()) {
                    JsonArray array = values.getAsJsonArray();
                    for (int i = 0; i < step.valueCount() && i < array.size(); i++) {
                        JsonElement value = array.get(i);
                        if (value != null && value.isJsonPrimitive()) {
                            step.set(i, value.getAsString());
                        }
                    }
                }
                step.setEnabled(bool(stepData, "enabled", true));
                profile.addStep(step);
            }
        }
        return profile;
    }

    public static void save() {
        lastSaveMs = System.currentTimeMillis();
        try {
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT);
            root.addProperty("client", Swerve.MOD_NAME);

            JsonArray bots = new JsonArray();
            for (int i = 0, size = BOTS.size(); i < size; i++) {
                BotProfile profile = BOTS.get(i);
                JsonObject data = new JsonObject();
                data.addProperty("name", profile.getName());
                data.addProperty("host", profile.getHost());
                data.addProperty("port", profile.getPort());
                data.addProperty("nickname", profile.getNickname());
                data.addProperty("protocol", profile.getProtocol());
                data.addProperty("autoReconnect", profile.isAutoReconnect());
                data.addProperty("reconnectDelay", profile.getReconnectDelay());
                data.addProperty("stepDelay", profile.getStepDelay());
                data.addProperty("loopForever", profile.isLoopForever());
                data.addProperty("password", profile.getPassword());

                JsonArray steps = new JsonArray();
                for (int s = 0, count = profile.getSteps().size(); s < count; s++) {
                    BotStep step = profile.getSteps().get(s);
                    JsonObject stepData = new JsonObject();
                    stepData.addProperty("type", step.getType() == null ? "" : step.getType().name());
                    JsonArray values = new JsonArray();
                    for (int v = 0; v < step.valueCount(); v++) {
                        values.add(step.get(v));
                    }
                    stepData.add("values", values);
                    stepData.addProperty("enabled", step.isEnabled());
                    steps.add(stepData);
                }
                data.add("steps", steps);
                bots.add(data);
            }
            root.add("bots", bots);

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
            Swerve.LOGGER.warn("Failed to save bots {}", FILE, exception);
        }
    }

    private static String string(JsonObject data, String key, String fallback) {
        JsonElement value = data.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : fallback;
    }

    private static int intValue(JsonObject data, String key, int fallback) {
        JsonElement value = data.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
                ? value.getAsInt() : fallback;
    }

    private static double number(JsonObject data, String key, double fallback) {
        JsonElement value = data.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
                ? value.getAsDouble() : fallback;
    }

    private static boolean bool(JsonObject data, String key, boolean fallback) {
        JsonElement value = data.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()
                ? value.getAsBoolean() : fallback;
    }
}
