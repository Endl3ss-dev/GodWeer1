package tech.lightclient.scripting.runtime;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.eventbus.EventBus;
import tech.lightclient.client.utils.eventbus.listeners.ConsumerListener;
import tech.lightclient.client.utils.eventbus.listeners.IListener;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.scripting.api.ScriptSettings;
import tech.lightclient.scripting.api.ScriptStorage;
import tech.lightclient.scripting.api.ScriptTask;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * One loaded script: metadata, declared settings, event subscriptions and lifecycle state.
 *
 * <p>Subscriptions, scheduled tasks and commands are only live while the script is switched on,
 * exactly as the docs describe: toggling off detaches everything without the script author having
 * to clean up by hand.</p>
 */
public class ScriptInstance {

    public enum Status { LOADING, READY, ERROR, DISABLED }

    public final File file;
    public final String id;                 // file name without extension

    public String name;                     // display name, from name(...) or the file id
    public String description = "";
    public int defaultKey = -1;             // key(...) default bind
    public int bind = -1;                   // active bind, persisted
    public int requiredApiVersion = 0;

    public volatile Status status = Status.LOADING;
    public volatile String error = null;

    /** Declared settings in declaration order, which is also the order they are drawn in. */
    public final List<ScriptSettings.Base<?>> settings = new CopyOnWriteArrayList<>();

    // lifecycle callbacks declared by the script
    volatile Runnable onEnable;
    volatile Runnable onDisable;
    volatile Runnable onUnload;

    private final List<IListener> subscriptions = new CopyOnWriteArrayList<>();
    private final List<ScriptTask> tasks = new CopyOnWriteArrayList<>();
    private final List<String> commandNames = new CopyOnWriteArrayList<>();
    private final Map<String, ScriptStorage> storages = new LinkedHashMap<>();

    private volatile boolean enabled = false;
    private int consecutiveFailures = 0;

    public ScriptInstance(File file) {
        this.file = file;
        String n = file.getName();
        int dot = n.lastIndexOf('.');
        this.id = dot > 0 ? n.substring(0, dot) : n;
        this.name = this.id;
    }

    public String displayName() {
        return name != null && !name.isEmpty() ? name : id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** Native settings for the GUI, which renders them with the standard components. */
    public List<Setting<?>> nativeSettings() {
        List<Setting<?>> out = new ArrayList<>();
        for (ScriptSettings.Base<?> s : settings) out.add(s.raw());
        return out;
    }

    // ---- storage ---------------------------------------------------------------------------

    public ScriptStorage storage(String configName) {
        ScriptStorage.validateName(configName);
        synchronized (storages) {
            return storages.computeIfAbsent(configName, n -> new ScriptStorage(id, n));
        }
    }

    // ---- subscriptions ---------------------------------------------------------------------

    /** Registers an event handler that stays attached while the script is enabled. */
    public <T> ScriptSubscription subscribe(Class<T> eventClass, int priority, java.util.function.Consumer<T> consumer) {
        ConsumerListener<T> listener = new ConsumerListener<>(eventClass, priority, consumer);
        subscriptions.add(listener);
        if (enabled) {
            EventBus bus = LightClient.getEventBus();
            if (bus != null) bus.subscribe(listener);
        }
        return new ScriptSubscription(this, listener);
    }

    void removeSubscription(IListener listener) {
        subscriptions.remove(listener);
        EventBus bus = LightClient.getEventBus();
        if (bus != null) bus.unsubscribe(listener);
    }

    public void addTask(ScriptTask task) {
        tasks.add(task);
    }

    public List<ScriptTask> tasks() {
        return tasks;
    }

    public void addCommandName(String name) {
        commandNames.add(name.toLowerCase());
    }

    public List<String> commandNames() {
        return commandNames;
    }

    // ---- lifecycle -------------------------------------------------------------------------

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        if (value && status == Status.ERROR) return;   // a broken script cannot be switched on

        EventBus bus = LightClient.getEventBus();
        if (value) {
            // onEnable runs before handlers attach, matching the documented ordering: a packet
            // sent from onEnable is deliberately not visible to this script's own listener.
            enabled = true;
            safe(onEnable, "onEnable");
            if (bus != null) {
                for (IListener l : subscriptions) bus.subscribe(l);
            }
        } else {
            if (bus != null) {
                for (IListener l : subscriptions) bus.unsubscribe(l);
            }
            safe(onDisable, "onDisable");
            enabled = false;
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    /** Called when the file is reloaded or removed. */
    public void unload() {
        if (enabled) setEnabled(false);
        safe(onUnload, "onUnload");
        EventBus bus = LightClient.getEventBus();
        if (bus != null) {
            for (IListener l : subscriptions) bus.unsubscribe(l);
        }
        subscriptions.clear();
        for (ScriptTask t : tasks) t.cancel();
        tasks.clear();
        synchronized (storages) {
            for (ScriptStorage s : storages.values()) s.flushIfDirty();
        }
    }

    private void safe(Runnable r, String phase) {
        if (r == null) return;
        try {
            ScriptWatchdog.runGuarded(this, phase, r);
            consecutiveFailures = 0;
        } catch (Throwable t) {
            reportFailure(phase, t);
        }
    }

    /**
     * Records a failure. Five in a row switches the script off so a broken handler cannot
     * flood the console every tick; one clean run resets the counter.
     */
    public void reportFailure(String phase, Throwable t) {
        consecutiveFailures++;
        ScriptConsole.error(id, phase + " failed - " + describe(t));
        LightClient.LOGGER.error("[Scripts] {} {} failed", id, phase, t);
        if (consecutiveFailures >= 5) {
            ScriptConsole.error(id, "disabled after 5 consecutive failures");
            consecutiveFailures = 0;
            setEnabled(false);
        }
    }

    public void noteSuccess() {
        consecutiveFailures = 0;
    }

    /** Formats a throwable as "file.kts:line: Exception: message" where possible. */
    private String describe(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String message = root.getMessage();
        String head = root.getClass().getSimpleName() + (message != null ? ": " + message : "");

        // Find the frame belonging to the compiled script so the author gets a line number.
        for (StackTraceElement e : root.getStackTrace()) {
            String cls = e.getClassName();
            if (cls.contains("Line_") || cls.startsWith("Script_") || cls.contains(id)) {
                if (e.getLineNumber() > 0) {
                    return file.getName() + ":" + e.getLineNumber() + ": " + head;
                }
            }
        }
        return head;
    }
}
