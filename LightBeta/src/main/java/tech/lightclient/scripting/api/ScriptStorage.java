package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import tech.lightclient.LightClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Per-script key/value store, mirroring the {@code storage} / {@code config(name)} docs.
 *
 * <p>Scripts cannot touch the filesystem themselves, so this is the only way for them to keep
 * anything across sessions. Files live under {@code light/scripts/data/<script>/<config>.properties}.</p>
 */
public final class ScriptStorage {

    private static final Pattern VALID_NAME = Pattern.compile("[a-z0-9_-]{1,32}");
    private static final int MAX_KEYS = 4096;
    private static final int MAX_VALUE_BYTES = 64 * 1024;

    private final String scriptId;
    private final String configName;
    private final Map<String, String> values = new LinkedHashMap<>();
    private boolean dirty = false;
    private boolean loaded = false;

    public ScriptStorage(String scriptId, String configName) {
        this.scriptId = scriptId;
        this.configName = configName;
    }

    // ---- directories -----------------------------------------------------------------------

    private static File baseDir() {
        return new File(MinecraftClient.getInstance().runDirectory, "light" + File.separator + "scripts" + File.separator + "data");
    }

    private File dir() {
        File d = new File(baseDir(), sanitiseScriptId(scriptId));
        if (!d.exists()) d.mkdirs();
        return d;
    }

    private File file() {
        return new File(dir(), configName + ".properties");
    }

    private static String sanitiseScriptId(String id) {
        return id == null ? "unknown" : id.replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    public static void validateName(String name) {
        if (name == null || !VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("config name must be 1-32 characters of a-z, 0-9, _ or - (got: " + name + ")");
        }
    }

    // ---- reads -----------------------------------------------------------------------------

    private void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        load();
    }

    public String get(String key, String def) {
        ensureLoaded();
        String v = values.get(key);
        return v == null ? def : v;
    }

    public int getInt(String key, int def) {
        try {
            return Integer.parseInt(get(key, String.valueOf(def)).trim());
        } catch (Throwable t) {
            return def;
        }
    }

    public long getLong(String key, long def) {
        try {
            return Long.parseLong(get(key, String.valueOf(def)).trim());
        } catch (Throwable t) {
            return def;
        }
    }

    public double getDouble(String key, double def) {
        try {
            return Double.parseDouble(get(key, String.valueOf(def)).trim());
        } catch (Throwable t) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        String v = get(key, null);
        if (v == null) return def;
        String s = v.trim().toLowerCase();
        if (s.equals("true") || s.equals("on") || s.equals("yes") || s.equals("1")) return true;
        if (s.equals("false") || s.equals("off") || s.equals("no") || s.equals("0")) return false;
        return def;
    }

    public boolean has(String key) {
        ensureLoaded();
        return values.containsKey(key);
    }

    public List<String> keys() {
        ensureLoaded();
        return new ArrayList<>(values.keySet());
    }

    public boolean dirty() {
        return dirty;
    }

    // ---- writes ----------------------------------------------------------------------------

    public void put(String key, String value) {
        ensureLoaded();
        if (key == null) return;
        String v = value == null ? "" : value;
        if (v.getBytes(StandardCharsets.UTF_8).length > MAX_VALUE_BYTES) {
            throw new IllegalArgumentException("value for '" + key + "' is larger than 64 KiB");
        }
        if (!values.containsKey(key) && values.size() >= MAX_KEYS) {
            throw new IllegalStateException("config '" + configName + "' already holds " + MAX_KEYS + " keys");
        }
        if (v.equals(values.get(key))) return;
        values.put(key, v);
        dirty = true;
    }

    public void put(String key, int value) { put(key, String.valueOf(value)); }

    public void put(String key, long value) { put(key, String.valueOf(value)); }

    public void put(String key, double value) { put(key, String.valueOf(value)); }

    public void put(String key, boolean value) { put(key, String.valueOf(value)); }

    public void remove(String key) {
        ensureLoaded();
        if (values.remove(key) != null) dirty = true;
    }

    public void clear() {
        ensureLoaded();
        if (!values.isEmpty()) {
            values.clear();
            dirty = true;
        }
    }

    // ---- persistence -----------------------------------------------------------------------

    /** Writes to disk immediately. Do not call this every tick. */
    public void save() {
        try {
            Properties props = new Properties();
            props.putAll(values);
            File target = file();
            Path tmp = Files.createTempFile(target.getParentFile().toPath(), configName, ".tmp");
            try (var out = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                props.store(out, "light-client script storage: " + scriptId + "/" + configName);
            }
            Files.move(tmp, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dirty = false;
        } catch (IOException e) {
            LightClient.LOGGER.warn("[Scripts] could not save storage {}/{}", scriptId, configName, e);
        }
    }

    /** Re-reads from disk, discarding anything unsaved. */
    public void load() {
        loaded = true;
        values.clear();
        dirty = false;
        File f = file();
        if (!f.exists()) return;
        try (var in = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(in);
            for (String name : props.stringPropertyNames()) {
                values.put(name, props.getProperty(name));
            }
        } catch (Throwable e) {
            LightClient.LOGGER.warn("[Scripts] could not read storage {}/{}", scriptId, configName, e);
        }
    }

    public void delete() {
        values.clear();
        dirty = false;
        File f = file();
        if (f.exists()) f.delete();
    }

    /** Flushes if there is anything pending; called when a script unloads. */
    public void flushIfDirty() {
        if (dirty) save();
    }

    // ---- the "configs" object --------------------------------------------------------------

    /** Directory-level operations, exposed to scripts as {@code configs}. */
    public static final class Configs {
        private final String scriptId;

        public Configs(String scriptId) {
            this.scriptId = scriptId;
        }

        public List<String> names() {
            List<String> out = new ArrayList<>();
            File d = new File(baseDir(), sanitiseScriptId(scriptId));
            File[] files = d.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile() && f.getName().endsWith(".properties")) {
                        out.add(f.getName().substring(0, f.getName().length() - ".properties".length()));
                    }
                }
            }
            return out;
        }

        public boolean exists(String name) {
            validateName(name);
            return new File(new File(baseDir(), sanitiseScriptId(scriptId)), name + ".properties").exists();
        }

        public boolean delete(String name) {
            validateName(name);
            File f = new File(new File(baseDir(), sanitiseScriptId(scriptId)), name + ".properties");
            return f.exists() && f.delete();
        }
    }
}
