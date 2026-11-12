package tech.lightclient.scripting.mcp;

import net.minecraft.client.MinecraftClient;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ButtonSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.RangeSliderSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;
import tech.lightclient.scripting.api.Key;
import tech.lightclient.scripting.api.ScriptSettings;
import tech.lightclient.scripting.runtime.ScriptConsole;
import tech.lightclient.scripting.runtime.ScriptInstance;
import tech.lightclient.scripting.runtime.ScriptManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * The eleven MCP tools described in the docs.
 *
 * <p>Anything that mutates game state is pushed onto the client thread and waited for, so an agent
 * sees a consistent answer rather than racing the game loop.</p>
 */
final class McpTools {

    private McpTools() {}

    static JSONObject initializeResult() {
        return new JSONObject()
                .put("protocolVersion", "2024-11-05")
                .put("serverInfo", new JSONObject().put("name", "light-client-scripts").put("version", "1"))
                .put("capabilities", new JSONObject().put("tools", new JSONObject()));
    }

    static JSONArray listTools() {
        JSONArray tools = new JSONArray();
        tools.put(tool("scripts_list", "All scripts with id, name, enabled, status and error.", new JSONObject()));
        tools.put(tool("script_source", "Current source of a script.",
                new JSONObject().put("id", stringProp("script id"))));
        tools.put(tool("script_deploy", "Writes <name>.kts into the scripts folder; it recompiles at once.",
                new JSONObject().put("name", stringProp("file name without extension"))
                        .put("source", stringProp("full Kotlin source"))));
        tools.put(tool("script_delete", "Unloads a script and deletes its file.",
                new JSONObject().put("id", stringProp("script id"))));
        tools.put(tool("script_enable", "Switches a script on.",
                new JSONObject().put("id", stringProp("script id"))));
        tools.put(tool("script_disable", "Switches a script off.",
                new JSONObject().put("id", stringProp("script id"))));
        tools.put(tool("script_settings", "Load status, bind and every setting with type, value, bounds and options.",
                new JSONObject().put("id", stringProp("script id"))));
        tools.put(tool("script_setting_set", "Writes one setting; for a button setting, presses it.",
                new JSONObject().put("id", stringProp("script id"))
                        .put("key", stringProp("setting key, dotted for nested settings"))
                        .put("value", new JSONObject().put("description", "value matching the setting kind"))));
        tools.put(tool("script_bind_set", "Rebinds a script. Use UNKNOWN to clear.",
                new JSONObject().put("id", stringProp("script id"))
                        .put("key", stringProp("key name, e.g. G"))
                        .put("mods", new JSONObject().put("type", "integer").put("description", "modifier mask"))));
        tools.put(tool("console_tail", "Console lines, newest last, plus lastId for the next call.",
                new JSONObject().put("sinceId", new JSONObject().put("type", "integer").put("description", "cursor from the previous call"))
                        .put("limit", new JSONObject().put("type", "integer").put("description", "maximum lines"))));
        tools.put(tool("game_state", "inGame, singleplayer, dimension and the player position.", new JSONObject()));
        return tools;
    }

    private static JSONObject tool(String name, String description, JSONObject properties) {
        return new JSONObject()
                .put("name", name)
                .put("description", description)
                .put("inputSchema", new JSONObject()
                        .put("type", "object")
                        .put("properties", properties));
    }

    private static JSONObject stringProp(String description) {
        return new JSONObject().put("type", "string").put("description", description);
    }

    // ---- dispatch --------------------------------------------------------------------------

    static JSONObject call(String tool, JSONObject args) {
        return switch (tool) {
            case "scripts_list" -> scriptsList();
            case "script_source" -> scriptSource(args.optString("id"));
            case "script_deploy" -> scriptDeploy(args.optString("name"), args.optString("source"));
            case "script_delete" -> scriptDelete(args.optString("id"));
            case "script_enable" -> setEnabled(args.optString("id"), true);
            case "script_disable" -> setEnabled(args.optString("id"), false);
            case "script_settings" -> scriptSettings(args.optString("id"));
            case "script_setting_set" -> settingSet(args.optString("id"), args.optString("key"), args.opt("value"));
            case "script_bind_set" -> bindSet(args.optString("id"), args.optString("key"), args.optInt("mods", 0));
            case "console_tail" -> consoleTail(args.optLong("sinceId", 0L), args.optInt("limit", 200));
            case "game_state" -> gameState();
            default -> new JSONObject().put("error", "unknown tool: " + tool);
        };
    }

    // ---- tools -----------------------------------------------------------------------------

    private static JSONObject scriptsList() {
        JSONArray out = new JSONArray();
        for (ScriptInstance s : ScriptManager.get().all()) {
            JSONObject o = new JSONObject()
                    .put("id", s.id)
                    .put("name", s.displayName())
                    .put("enabled", s.isEnabled())
                    .put("status", s.status.name());
            if (s.error != null) o.put("error", s.error);
            out.put(o);
        }
        return new JSONObject().put("scripts", out);
    }

    private static JSONObject scriptSource(String id) {
        ScriptInstance s = ScriptManager.get().byId(id);
        if (s == null) return new JSONObject().put("error", "no script with id " + id);
        try {
            return new JSONObject()
                    .put("id", s.id)
                    .put("source", Files.readString(s.file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new JSONObject().put("error", "could not read the file: " + e.getMessage());
        }
    }

    private static JSONObject scriptDeploy(String name, String source) {
        if (name == null || name.isBlank()) return new JSONObject().put("error", "name is required");
        try {
            var file = ScriptManager.get().deploy(name, source);
            // Deploy only writes the file; compilation follows on its own, so the agent is told
            // to check scripts_list next, exactly as the docs prescribe.
            return new JSONObject()
                    .put("ok", true)
                    .put("file", file.getName())
                    .put("id", file.getName().replaceAll("\\.kts$", ""))
                    .put("note", "compiling; call scripts_list and check status");
        } catch (IOException e) {
            return new JSONObject().put("error", "could not write the file: " + e.getMessage());
        }
    }

    private static JSONObject scriptDelete(String id) {
        boolean removed = ScriptManager.get().delete(id);
        return new JSONObject().put("ok", removed);
    }

    private static JSONObject setEnabled(String id, boolean enabled) {
        ScriptInstance s = ScriptManager.get().byId(id);
        if (s == null) return new JSONObject().put("error", "no script with id " + id);
        if (enabled && s.status == ScriptInstance.Status.ERROR) {
            return new JSONObject().put("error", "the script did not compile: " + s.error);
        }
        onClientAndWait(() -> s.setEnabled(enabled));
        return new JSONObject().put("ok", true).put("enabled", s.isEnabled());
    }

    private static JSONObject scriptSettings(String id) {
        ScriptInstance s = ScriptManager.get().byId(id);
        if (s == null) return new JSONObject().put("error", "no script with id " + id);

        JSONArray settings = new JSONArray();
        for (ScriptSettings.Base<?> wrapper : s.settings) {
            Setting<?> raw = wrapper.raw();
            JSONObject o = new JSONObject()
                    .put("key", raw.getName())
                    .put("visible", safeVisible(raw));

            if (raw instanceof BooleanSetting b) {
                o.put("kind", "checkbox").put("value", b.get());
            } else if (raw instanceof SliderSetting sl) {
                o.put("kind", "slider").put("value", sl.get()).put("min", sl.min).put("max", sl.max)
                        .put("step", sl.increment);
                if (!sl.getPostfix().isEmpty()) o.put("postfix", sl.getPostfix());
            } else if (raw instanceof RangeSliderSetting rs) {
                o.put("kind", "rangeSlider")
                        .put("value", new JSONObject().put("min", rs.from()).put("max", rs.to()))
                        .put("min", rs.min).put("max", rs.max).put("step", rs.increment);
            } else if (raw instanceof StringSetting st) {
                o.put("kind", "input").put("value", st.get());
            } else if (raw instanceof ModeSetting ms) {
                o.put("kind", "selectable").put("value", ms.get()).put("options", new JSONArray(List.of(ms.strings)));
            } else if (raw instanceof ModeListSetting ml) {
                JSONArray selected = new JSONArray();
                JSONArray options = new JSONArray();
                for (BooleanSetting b : ml.get()) {
                    options.put(b.getName());
                    if (b.get()) selected.put(b.getName());
                }
                o.put("kind", "combo").put("value", selected).put("options", options);
            } else if (raw instanceof ColorSetting cs) {
                o.put("kind", "colorPicker").put("value", cs.get());
            } else if (raw instanceof BindSetting bs) {
                o.put("kind", "hotkey").put("value", Key.nameOf(bs.get()));
            } else if (raw instanceof ButtonSetting) {
                o.put("kind", "button");
            } else {
                o.put("kind", "unknown");
            }

            // Nested settings are reported with a dotted key and a parent field.
            if (wrapper.parentName() != null) {
                o.put("parent", wrapper.parentName());
                o.put("key", wrapper.parentName() + "." + raw.getName());
            }
            settings.put(o);
        }

        return new JSONObject()
                .put("id", s.id)
                .put("status", s.status.name())
                .put("bind", Key.nameOf(s.bind))
                .put("settings", settings);
    }

    private static boolean safeVisible(Setting<?> setting) {
        try {
            return setting.isVisible();
        } catch (Throwable t) {
            return true;
        }
    }

    private static JSONObject settingSet(String id, String key, Object value) {
        ScriptInstance s = ScriptManager.get().byId(id);
        if (s == null) return new JSONObject().put("error", "no script with id " + id);
        if (key == null || key.isBlank()) return new JSONObject().put("error", "key is required");

        // Accept both the dotted form and the bare setting name.
        String leaf = key.contains(".") ? key.substring(key.lastIndexOf('.') + 1) : key;

        for (ScriptSettings.Base<?> wrapper : s.settings) {
            Setting<?> raw = wrapper.raw();
            if (!raw.getName().equalsIgnoreCase(leaf)) continue;
            try {
                applyValue(wrapper, raw, value);
                return new JSONObject().put("ok", true);
            } catch (Throwable t) {
                return new JSONObject().put("error", t.getMessage() == null ? t.toString() : t.getMessage());
            }
        }
        return new JSONObject().put("error", "no setting called " + key);
    }

    private static void applyValue(ScriptSettings.Base<?> wrapper, Setting<?> raw, Object value) {
        if (wrapper instanceof ScriptSettings.Check c) {
            c.value(asBoolean(value));
        } else if (wrapper instanceof ScriptSettings.Slide sl) {
            sl.value((float) asDouble(value));
        } else if (wrapper instanceof ScriptSettings.Range rs) {
            JSONObject o = asObject(value);
            rs.value((float) o.optDouble("min", rs.from()), (float) o.optDouble("max", rs.to()));
        } else if (wrapper instanceof ScriptSettings.Input in) {
            in.value(String.valueOf(value));
        } else if (wrapper instanceof ScriptSettings.Select sel) {
            sel.select(String.valueOf(value));
        } else if (wrapper instanceof ScriptSettings.Combo combo) {
            JSONArray arr = asArray(value);
            java.util.List<String> want = new java.util.ArrayList<>();
            for (int i = 0; i < arr.length(); i++) want.add(arr.getString(i));
            combo.value(want);
        } else if (wrapper instanceof ScriptSettings.Color col) {
            col.value((int) asDouble(value));
        } else if (wrapper instanceof ScriptSettings.Hotkey hk) {
            hk.value(parseKey(value));
        } else if (wrapper instanceof ScriptSettings.Button btn) {
            onClientAndWait(btn::press);
        } else {
            throw new IllegalArgumentException("this setting cannot be written");
        }
    }

    /** Accepts "G" as well as {"key": "G", "mods": 2}. */
    private static int parseKey(Object value) {
        if (value instanceof JSONObject o) return Key.byName(o.optString("key", "UNKNOWN"));
        String s = String.valueOf(value).trim();
        if (s.startsWith("{")) return Key.byName(new JSONObject(s).optString("key", "UNKNOWN"));
        return Key.byName(s);
    }

    private static JSONObject bindSet(String id, String key, int mods) {
        ScriptInstance s = ScriptManager.get().byId(id);
        if (s == null) return new JSONObject().put("error", "no script with id " + id);
        int code = Key.byName(key);
        s.bind = code <= 0 ? -1 : code;
        return new JSONObject().put("ok", true).put("bind", Key.nameOf(s.bind));
    }

    private static JSONObject consoleTail(long sinceId, int limit) {
        // A cursor ahead of our newest line means the numbering restarted with the game.
        boolean reset = ScriptConsole.isReset(sinceId);
        long from = reset ? 0L : sinceId;
        List<ScriptConsole.Line> lines = ScriptConsole.since(from, limit <= 0 ? 200 : limit);

        JSONArray arr = new JSONArray();
        for (ScriptConsole.Line line : lines) {
            arr.put(new JSONObject()
                    .put("id", line.id)
                    .put("time", line.time)
                    .put("level", line.level.name())
                    .put("script", line.script)
                    .put("text", line.text));
        }
        JSONObject out = new JSONObject()
                .put("lines", arr)
                .put("lastId", ScriptConsole.lastId());
        if (reset) out.put("reset", true);
        return out;
    }

    private static JSONObject gameState() {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean inGame = mc.player != null && mc.world != null;
        JSONObject out = new JSONObject()
                .put("inGame", inGame)
                .put("singleplayer", mc.isInSingleplayer());
        if (inGame) {
            out.put("dimension", mc.world.getRegistryKey().getValue().toString());
            var pos = mc.player.getEntityPos();
            out.put("position", new JSONObject().put("x", pos.x).put("y", pos.y).put("z", pos.z));
        }
        return out;
    }

    // ---- helpers ---------------------------------------------------------------------------

    /** Runs on the client thread and waits, so the reply reflects the change. */
    private static void onClientAndWait(Runnable action) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.isOnThread()) {
            action.run();
            return;
        }
        java.util.concurrent.CompletableFuture<Void> done = new java.util.concurrent.CompletableFuture<>();
        mc.execute(() -> {
            try {
                action.run();
            } finally {
                done.complete(null);
            }
        });
        try {
            done.get(3, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Throwable ignored) {
        }
    }

    private static boolean asBoolean(Object value) {
        if (value instanceof Boolean b) return b;
        String s = String.valueOf(value).trim().toLowerCase();
        return s.equals("true") || s.equals("on") || s.equals("yes") || s.equals("1");
    }

    private static double asDouble(Object value) {
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(value).trim());
    }

    /** JSON objects arrive either parsed or as a string, depending on the client. */
    private static JSONObject asObject(Object value) {
        if (value instanceof JSONObject o) return o;
        return new JSONObject(String.valueOf(value));
    }

    private static JSONArray asArray(Object value) {
        if (value instanceof JSONArray a) return a;
        return new JSONArray(String.valueOf(value));
    }
}
