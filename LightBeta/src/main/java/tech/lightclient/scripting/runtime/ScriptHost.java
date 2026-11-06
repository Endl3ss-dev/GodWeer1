package tech.lightclient.scripting.runtime;

import tech.lightclient.client.utils.eventbus.EventPriority;
import tech.lightclient.scripting.api.ChatApi;
import tech.lightclient.scripting.api.ClientApi;
import tech.lightclient.scripting.api.LogApi;
import tech.lightclient.scripting.api.PlayerApi;
import tech.lightclient.scripting.api.RaycastApi;
import tech.lightclient.scripting.api.ScriptActions;
import tech.lightclient.scripting.api.ScriptCommand;
import tech.lightclient.scripting.api.ScriptFilters;
import tech.lightclient.scripting.api.ScriptGame;
import tech.lightclient.scripting.api.ScriptNotify;
import tech.lightclient.scripting.api.ScriptRotations;
import tech.lightclient.scripting.api.ScriptSettings;
import tech.lightclient.scripting.api.ScriptShader;
import tech.lightclient.scripting.api.ScriptStorage;
import tech.lightclient.scripting.api.ScriptTask;
import tech.lightclient.scripting.api.ScriptText;

import java.util.List;
import java.util.function.Consumer;

/**
 * The single object a script talks to.
 *
 * <p>Everything the prelude exposes as a top-level function funnels through here, which keeps the
 * generated Kotlin small and means a script never holds a reference to the runtime internals.</p>
 */
public final class ScriptHost {

    private final ScriptInstance script;

    private final PlayerApi player = new PlayerApi();
    private final tech.lightclient.scripting.api.WorldApi world = new tech.lightclient.scripting.api.WorldApi();
    private final ClientApi client = new ClientApi();
    private final ChatApi chat;
    private final LogApi log;
    private final RaycastApi raycast = new RaycastApi();
    private final ScriptActions.Control control = new ScriptActions.Control();
    private final ScriptActions.Interaction interaction = new ScriptActions.Interaction();
    private final ScriptActions.Inventory inventory = new ScriptActions.Inventory();
    private final ScriptRotations rotations = new ScriptRotations();
    private final ScriptText.Factory text = new ScriptText.Factory();
    private final ScriptGame game = new ScriptGame();
    private final ScriptFilters filters = new ScriptFilters();
    private final ScriptStorage.Configs configs;

    /** True only while the script's top-level code is running. */
    private boolean loading = true;

    ScriptHost(ScriptInstance script) {
        this.script = script;
        this.chat = new ChatApi(script.id);
        this.log = new LogApi(script.id);
        this.configs = new ScriptStorage.Configs(script.id);
    }

    void finishLoading() {
        loading = false;
    }

    // ---- api objects -----------------------------------------------------------------------

    public PlayerApi player() { return player; }

    public tech.lightclient.scripting.api.WorldApi world() { return world; }

    public ClientApi client() { return client; }

    public ChatApi chat() { return chat; }

    public LogApi log() { return log; }

    public RaycastApi raycast() { return raycast; }

    public ScriptActions.Control control() { return control; }

    public ScriptActions.Interaction interaction() { return interaction; }

    public ScriptActions.Inventory inventory() { return inventory; }

    public ScriptRotations rotations() { return rotations; }

    public ScriptText.Factory text() { return text; }

    public ScriptGame game() { return game; }

    public ScriptFilters filters() { return filters; }

    public ScriptStorage storage() { return script.storage("storage"); }

    public ScriptStorage config(String name) { return script.storage(name); }

    public ScriptStorage.Configs configs() { return configs; }

    // ---- metadata --------------------------------------------------------------------------

    public void name(String value) {
        requireLoading("name");
        if (value != null && !value.isBlank()) script.name = value.trim();
    }

    public void description(String value) {
        requireLoading("description");
        if (value != null) script.description = value.trim();
    }

    public void key(int value) {
        requireLoading("key");
        script.defaultKey = value;
        // Only take the default if the player has not chosen a bind of their own.
        if (script.bind <= 0) script.bind = value;
    }

    public void requireApi(int version) {
        script.requiredApiVersion = version;
        if (version > ScriptManager.API_VERSION) {
            throw new IllegalStateException("this script needs script API " + version
                    + ", but this client provides " + ScriptManager.API_VERSION);
        }
    }

    private void requireLoading(String what) {
        if (!loading) {
            throw new IllegalStateException(what + "(...) may only be called at the top level of the script");
        }
    }

    // ---- lifecycle -------------------------------------------------------------------------

    public void onEnable(Runnable body) { script.onEnable = body; }

    public void onDisable(Runnable body) { script.onDisable = body; }

    public void onUnload(Runnable body) { script.onUnload = body; }

    public boolean isEnabled() { return script.isEnabled(); }

    public void setEnabled(boolean value) { script.setEnabled(value); }

    public boolean inGame() { return client.inGame(); }

    // ---- events ----------------------------------------------------------------------------

    /**
     * Subscribes to an event. Each call is wrapped so that a throwing handler is reported against
     * the script rather than taking down the event bus, and so the time limit is enforced.
     */
    public <T> ScriptSubscription on(Class<T> eventClass, int priority, boolean ignoreCancelled, Consumer<T> body) {
        return script.subscribe(eventClass, priority, event -> {
            // Cancelled events are hidden unless the script explicitly asked to see them.
            if (!ignoreCancelled && event instanceof tech.lightclient.client.utils.eventbus.ICancellable c && c.isCancelled()) {
                return;
            }
            ScriptWatchdog.runGuarded(script, "on<" + eventClass.getSimpleName() + ">", () -> {
                try {
                    body.accept(event);
                    script.noteSuccess();
                } catch (Throwable t) {
                    script.reportFailure("on<" + eventClass.getSimpleName() + ">", t);
                }
            });
        });
    }

    // ---- settings --------------------------------------------------------------------------

    private <T extends ScriptSettings.Base<?>> T register(ScriptSettings.Check parent, T setting) {
        requireLoading("settings");
        if (parent != null) setting.nestUnder(parent.raw());
        script.settings.add(setting);
        return setting;
    }

    public ScriptSettings.Check checkBox(ScriptSettings.Check parent, String name, boolean def) {
        return register(parent, ScriptSettings.checkBox(name, def));
    }

    public ScriptSettings.Slide slider(ScriptSettings.Check parent, String name, float def, float min, float max, float step) {
        return register(parent, ScriptSettings.slider(name, def, min, max, step));
    }

    public ScriptSettings.Slide sliderInt(ScriptSettings.Check parent, String name, int def, int min, int max) {
        return register(parent, ScriptSettings.sliderInt(name, def, min, max));
    }

    public ScriptSettings.Range rangeSlider(ScriptSettings.Check parent, String name, float from, float to, float min, float max, float step) {
        return register(parent, ScriptSettings.rangeSlider(name, from, to, min, max, step));
    }

    public ScriptSettings.Input input(ScriptSettings.Check parent, String name, String def) {
        return register(parent, ScriptSettings.input(name, def));
    }

    public ScriptSettings.Select selectable(ScriptSettings.Check parent, String name, List<String> options, String selected) {
        return register(parent, ScriptSettings.selectable(name, options, selected));
    }

    public ScriptSettings.Combo combo(ScriptSettings.Check parent, String name, List<String> options, List<String> selected) {
        return register(parent, ScriptSettings.combo(name, options, selected));
    }

    public ScriptSettings.Color colorPicker(ScriptSettings.Check parent, String name, int argb) {
        return register(parent, ScriptSettings.colorPicker(name, argb));
    }

    public ScriptSettings.Hotkey hotkey(String name, int key, Runnable action) {
        return register(null, ScriptSettings.hotkey(name, key, () ->
                ScriptWatchdog.runGuarded(script, "hotkey " + name, () -> {
                    try {
                        action.run();
                        script.noteSuccess();
                    } catch (Throwable t) {
                        script.reportFailure("hotkey " + name, t);
                    }
                })));
    }

    public ScriptSettings.Button button(String name, Runnable action) {
        return register(null, ScriptSettings.button(name, () ->
                ScriptWatchdog.runGuarded(script, "button " + name, () -> {
                    try {
                        action.run();
                        script.noteSuccess();
                    } catch (Throwable t) {
                        script.reportFailure("button " + name, t);
                    }
                })));
    }

    // ---- tasks -----------------------------------------------------------------------------

    public ScriptTask afterTicks(int ticks, Runnable body) {
        ScriptTask task = new ScriptTask(wrap("afterTicks", body), ticks, false);
        script.addTask(task);
        return task;
    }

    public ScriptTask everyTicks(int ticks, Runnable body) {
        ScriptTask task = new ScriptTask(wrap("everyTicks", body), Math.max(1, ticks), true);
        script.addTask(task);
        return task;
    }

    private Runnable wrap(String phase, Runnable body) {
        return () -> ScriptWatchdog.runGuarded(script, phase, () -> {
            try {
                body.run();
                script.noteSuccess();
            } catch (Throwable t) {
                script.reportFailure(phase, t);
            }
        });
    }

    /** Runs the block on the client thread, immediately if we are already on it. */
    public void onClientThread(Runnable body) {
        Runnable wrapped = wrap("onClientThread", body);
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.isOnThread()) wrapped.run();
        else mc.execute(wrapped);
    }

    // ---- messages --------------------------------------------------------------------------

    public void notify(Object message, ScriptNotify.Kind kind) {
        ScriptNotify.push(message, kind);
    }

    // ---- commands --------------------------------------------------------------------------

    public void command(String name, Consumer<ScriptCommand.Builder> body) {
        requireLoading("command");
        ScriptCommand.Builder builder = ScriptCommand.builder(name);
        body.accept(builder);
        ScriptManager.get().registerCommand(script, builder);
    }

    // ---- shaders ---------------------------------------------------------------------------

    public ScriptShader shader(String name, String fragmentSource, String vertexSource) {
        return ScriptManager.get().createShader(name, fragmentSource, vertexSource);
    }
}
