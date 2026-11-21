package tech.lightclient.scripting.runtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.eventbus.EventPriority;
import tech.lightclient.client.utils.render.easy.RenderUtil;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.scripting.api.ScriptCommand;
import tech.lightclient.scripting.api.ScriptEvents;
import tech.lightclient.scripting.api.ScriptPackets;
import tech.lightclient.scripting.api.ScriptRender;
import tech.lightclient.scripting.api.ScriptSettings;
import tech.lightclient.scripting.api.ScriptShader;
import tech.lightclient.scripting.api.ScriptTask;
import tech.lightclient.scripting.api.WorldApi;

import javax.script.Bindings;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Discovers, compiles, reloads and drives every script.
 *
 * <p>Compilation happens on a background thread because the Kotlin compiler takes a noticeable
 * moment, and freezing the game for it would be unacceptable. Everything that touches the game -
 * running the script body, firing handlers, ticking tasks - is marshalled back onto the client
 * thread.</p>
 */
public final class ScriptManager {

    /** Script API version implemented by this build. */
    public static final int API_VERSION = 1;

    private static final ScriptManager INSTANCE = new ScriptManager();

    public static ScriptManager get() {
        return INSTANCE;
    }

    private final Map<String, ScriptInstance> scripts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastModified = new ConcurrentHashMap<>();
    private final Map<String, ScriptCommand> commands = new ConcurrentHashMap<>();
    private final List<ScriptShader> shaders = new CopyOnWriteArrayList<>();

    private final ExecutorService compiler = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "light-script-compiler");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean initialised = false;
    private static volatile long clientTicks = 0L;

    /** Entity ids seen last tick, used to synthesise spawn/remove events. */
    private final Set<Integer> knownEntities = new HashSet<>();
    private String lastDimension = "";
    private int rescanCounter = 0;

    private ScriptManager() {}

    /** Ticks since the client started; the basis for script timers. */
    public static long clientTick() {
        return clientTicks;
    }

    public File directory() {
        File dir = new File(MinecraftClient.getInstance().runDirectory, "light" + File.separator + "scripts");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    // ---- lifecycle -------------------------------------------------------------------------

    public void init() {
        if (initialised) return;
        initialised = true;
        try {
            var bus = LightClient.getEventBus();
            if (bus != null) bus.subscribe(this);
            File dir = directory();
            ScriptConsole.info("scripts", "script folder: " + dir.getAbsolutePath());
            compiler.submit(this::selfTest);
            reloadAll();
        } catch (Throwable t) {
            LightClient.LOGGER.error("[Scripts] initialisation failed", t);
        }
    }

    /**
     * Compiles a trivial script once at startup to prove the whole chain works: the engine, the
     * resolved classpath, and the prelude's ability to see the scripting API.
     *
     * <p>Without this, a broken classpath shows up only as every script failing with a confusing
     * "unresolved reference" message, which points the finger at the script rather than the setup.
     */
    private void selfTest() {
        try {
            if (!ScriptEngineHost.available()) {
                ScriptConsole.error("scripts", "the Kotlin engine did not start: " + ScriptEngineHost.initError());
                return;
            }
            File probe = new File(System.getProperty("java.io.tmpdir"), "light-script-selftest.kts");
            ScriptInstance dummy = new ScriptInstance(probe);
            ScriptHost host = new ScriptHost(dummy);
            Bindings bindings = ScriptEngineHost.newBindings();
            bindings.put("__host", host);

            long start = System.currentTimeMillis();
            // Touches the host handle and one API object, which is what proves the classpath is
            // right; it deliberately does nothing observable in the game.
            ScriptEngineHost.eval(ScriptPrelude.SOURCE + "val __selfTestOk = client.apiVersion()\n", bindings);
            ScriptConsole.info("scripts", "self-test passed in " + (System.currentTimeMillis() - start)
                    + " ms, scripting is ready");
        } catch (Throwable t) {
            String detail = ScriptEngineHost.describe(t);
            ScriptConsole.error("scripts", "SELF-TEST FAILED, no script will load: " + detail);
            LightClient.LOGGER.error("[Scripts] self-test failed. Compiler classpath was:\n{}",
                    System.getProperty("kotlin.script.classpath", "<unset>"));
        }
    }

    /** Rescans the folder, loading new files, reloading changed ones and dropping deleted ones. */
    public void reloadAll() {
        File dir = directory();
        File[] files = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".kts"));
        Set<String> present = new HashSet<>();

        if (files != null) {
            for (File f : files) {
                String id = idOf(f);
                present.add(id);
                Long known = lastModified.get(id);
                if (known == null || known != f.lastModified()) {
                    load(f);
                }
            }
        }

        // Anything that disappeared from disk gets unloaded.
        for (String id : new ArrayList<>(scripts.keySet())) {
            if (!present.contains(id)) remove(id);
        }
    }

    private static String idOf(File f) {
        String n = f.getName();
        int dot = n.lastIndexOf('.');
        return dot > 0 ? n.substring(0, dot) : n;
    }

    /** Compiles one file, replacing any previous version of it. */
    public void load(File file) {
        String id = idOf(file);
        lastModified.put(id, file.lastModified());

        ScriptInstance previous = scripts.get(id);
        boolean wasEnabled = previous != null && previous.isEnabled();
        int keptBind = previous != null ? previous.bind : -1;
        Map<String, Object> keptValues = previous != null ? captureSettings(previous) : Map.of();
        if (previous != null) {
            previous.unload();
            unregisterCommands(previous);
        }

        ScriptInstance script = new ScriptInstance(file);
        script.status = ScriptInstance.Status.LOADING;
        if (keptBind > 0) script.bind = keptBind;
        scripts.put(id, script);

        compiler.submit(() -> {
            // A task submitted to an executor that throws leaves no trace, and the card would sit
            // on LOADING for ever with nothing in the log. Catch everything, including Errors such
            // as NoClassDefFoundError from the compiler's own dependencies.
            try {
                compile(script, file, wasEnabled, keptValues);
            } catch (Throwable t) {
                fail(script, "internal failure while loading: " + describeThrowable(t));
                LightClient.LOGGER.error("[Scripts] unexpected failure loading {}", script.id, t);
            } finally {
                // Whatever happened, the card must not be left pretending to compile.
                if (script.status == ScriptInstance.Status.LOADING) {
                    fail(script, "compilation ended without a result, see the log");
                }
            }
        });
    }

    private static String describeThrowable(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String message = root.getMessage();
        return root.getClass().getSimpleName() + (message != null ? ": " + message : "");
    }

    private void compile(ScriptInstance script, File file, boolean enableAfter, Map<String, Object> keptValues) {
        String source;
        try {
            source = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail(script, "could not read the file: " + e.getMessage());
            return;
        }

        // Reject forbidden APIs before the compiler ever sees the source.
        ScriptSandbox.Violation violation = ScriptSandbox.check(source);
        if (violation != null) {
            fail(script, file.getName() + ":" + violation.line + ": " + violation.symbol + " is not allowed in scripts");
            return;
        }

        if (!ScriptEngineHost.available()) {
            fail(script, "the Kotlin script engine is unavailable: " + ScriptEngineHost.initError());
            return;
        }

        ScriptHost host = new ScriptHost(script);
        Bindings bindings = ScriptEngineHost.newBindings();
        bindings.put("__host", host);

        long start = System.currentTimeMillis();
        try {
            // The prelude sits above the author's code, so reported line numbers need shifting
            // back by however many lines it occupies.
            ScriptEngineHost.eval(ScriptPrelude.SOURCE + source, bindings);
            host.finishLoading();

            long took = System.currentTimeMillis() - start;
            // Deliberately not enforcing the 5 s load limit on this number: it includes Kotlin
            // compilation, which legitimately takes seconds on a cold JIT and has nothing to do
            // with the script misbehaving. Runaway script logic is caught by the watchdog on the
            // handlers instead. A slow load is worth a warning, not a rejection.
            if (took > ScriptWatchdog.LOAD_LIMIT_MS) {
                ScriptConsole.warn(script.id, "took " + took + " ms to compile and load");
            }

            restoreSettings(script, keptValues);
            script.status = ScriptInstance.Status.READY;
            script.error = null;
            ScriptConsole.info(script.id, "loaded in " + took + " ms");

            if (enableAfter) {
                MinecraftClient.getInstance().execute(() -> script.setEnabled(true));
            }
        } catch (Throwable t) {
            fail(script, describeCompileError(t, file.getName()));
        }
    }

    /** Turns a compiler message into one that points at the author's own line numbers. */
    private String describeCompileError(Throwable t, String fileName) {
        String message = ScriptEngineHost.describe(t);
        if (message == null) return "failed to compile";

        // Messages look like "... (ScriptingHost123_Line_0.kts:42:5)". Rewrite the position so it
        // refers to the user's file and their line, discounting the prelude.
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\(([A-Za-z0-9_]+\\.kts):(\\d+):(\\d+)\\)")
                .matcher(message);
        StringBuilder out = new StringBuilder();
        int last = 0;
        while (m.find()) {
            int absolute = Integer.parseInt(m.group(2));
            int userLine = absolute - ScriptPrelude.LINE_COUNT;
            out.append(message, last, m.start());
            if (userLine >= 1) {
                out.append("(").append(fileName).append(":").append(userLine).append(":").append(m.group(3)).append(")");
            } else {
                // An error inside the prelude itself is our bug, not the author's.
                out.append("(internal prelude:").append(absolute).append(")");
            }
            last = m.end();
        }
        out.append(message.substring(last));
        return out.toString();
    }

    private void fail(ScriptInstance script, String message) {
        script.status = ScriptInstance.Status.ERROR;
        script.error = message;
        ScriptConsole.error(script.id, message);
    }

    public void remove(String id) {
        ScriptInstance script = scripts.remove(id);
        lastModified.remove(id);
        if (script != null) {
            script.unload();
            unregisterCommands(script);
            ScriptConsole.info(id, "unloaded");
        }
    }

    /** Deletes the file as well as unloading it. */
    public boolean delete(String id) {
        ScriptInstance script = scripts.get(id);
        File file = script != null ? script.file : new File(directory(), id + ".kts");
        remove(id);
        return file.exists() && file.delete();
    }

    /** Writes a script file and lets the folder watcher pick it up. */
    public File deploy(String name, String source) throws IOException {
        String safe = name.endsWith(".kts") ? name : name + ".kts";
        safe = safe.replaceAll("[^A-Za-z0-9_.-]", "_");
        File target = new File(directory(), safe);
        Files.writeString(target.toPath(), source == null ? "" : source, StandardCharsets.UTF_8);
        load(target);
        return target;
    }

    // ---- setting persistence across reloads -------------------------------------------------

    private Map<String, Object> captureSettings(ScriptInstance script) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (ScriptSettings.Base<?> s : script.settings) {
            Setting<?> raw = s.raw();
            out.put(raw.getName(), raw.get());
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private void restoreSettings(ScriptInstance script, Map<String, Object> values) {
        if (values == null || values.isEmpty()) return;
        for (ScriptSettings.Base<?> s : script.settings) {
            Setting<?> raw = s.raw();
            Object kept = values.get(raw.getName());
            if (kept == null) continue;
            try {
                // Only restore when the type still matches; a script that changed a setting's
                // kind between edits should get its new default.
                if (raw.get() != null && kept.getClass() == raw.get().getClass()) {
                    ((Setting<Object>) raw).set(kept);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    // ---- commands --------------------------------------------------------------------------

    void registerCommand(ScriptInstance script, ScriptCommand.Builder builder) {
        String name = builder.name();
        ScriptCommand existing = commands.get(name);
        if (existing != null) {
            throw new IllegalStateException("the command ." + name + " is already taken");
        }
        var repo = LightClient.getCommandRepository();
        if (repo != null && repo.getCommand(name) != null) {
            throw new IllegalStateException("the command ." + name + " is already taken by the client");
        }
        ScriptCommand command = new ScriptCommand(script, builder);
        commands.put(name, command);
        script.addCommandName(name);
        if (repo != null) repo.getRegistry().register(command);
    }

    private void unregisterCommands(ScriptInstance script) {
        var repo = LightClient.getCommandRepository();
        for (String name : script.commandNames()) {
            ScriptCommand command = commands.remove(name);
            if (command != null && repo != null) repo.getRegistry().unregister(command);
        }
        script.commandNames().clear();
    }

    // ---- shaders ---------------------------------------------------------------------------

    ScriptShader createShader(String name, String fragmentSource, String vertexSource) {
        ScriptShader shader = new ScriptShader(name, fragmentSource, vertexSource);
        shaders.add(shader);
        return shader;
    }

    // ---- queries for the GUI and MCP --------------------------------------------------------

    public List<ScriptInstance> all() {
        List<ScriptInstance> out = new ArrayList<>(scripts.values());
        out.sort(Comparator.comparing(ScriptInstance::displayName, String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    public ScriptInstance byId(String id) {
        return scripts.get(id);
    }

    // ---- driving the scripts ---------------------------------------------------------------

    @EventHandler
    public void onTick(TickEvent event) {
        clientTicks++;

        // Pick up edits made while the game is running, a few times a second rather than every
        // tick, since it touches the filesystem.
        if (++rescanCounter >= 20) {
            rescanCounter = 0;
            compiler.submit(() -> {
                try {
                    reloadAll();
                } catch (Throwable t) {
                    LightClient.LOGGER.warn("[Scripts] rescan failed", t);
                }
            });
        }

        tickTasks();
        detectWorldChange();
        detectEntityChanges();
    }

    private void tickTasks() {
        for (ScriptInstance script : scripts.values()) {
            if (!script.isEnabled()) continue;
            for (ScriptTask task : script.tasks()) {
                if (task.done()) {
                    script.tasks().remove(task);
                    continue;
                }
                if (task.tick()) {
                    try {
                        task.action.run();
                    } catch (Throwable t) {
                        script.reportFailure("task", t);
                    }
                }
            }
        }
    }

    private void detectWorldChange() {
        MinecraftClient mc = MinecraftClient.getInstance();
        String dimension = mc.world == null ? "" : mc.world.getRegistryKey().getValue().toString();
        if (!dimension.equals(lastDimension)) {
            lastDimension = dimension;
            knownEntities.clear();
            if (!dimension.isEmpty()) {
                ScriptEvents.post(new ScriptEvents.WorldLoad(dimension));
            }
        }
    }

    private void detectEntityChanges() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            knownEntities.clear();
            return;
        }
        // Only bother when at least one script cares, since this walks every entity.
        if (!anyoneListensForEntities()) {
            knownEntities.clear();
            return;
        }

        Set<Integer> current = new HashSet<>();
        for (var entity : mc.world.getEntities()) {
            current.add(entity.getId());
            if (knownEntities.add(entity.getId())) {
                ScriptEvents.post(new ScriptEvents.EntitySpawn(new WorldApi.ScriptEntity(entity)));
            }
        }
        knownEntities.removeIf(id -> {
            if (current.contains(id)) return false;
            var gone = mc.world.getEntityById(id);
            if (gone != null) {
                ScriptEvents.post(new ScriptEvents.EntityRemove(new WorldApi.ScriptEntity(gone)));
            }
            return true;
        });
    }

    private boolean anyoneListensForEntities() {
        var bus = LightClient.getEventBus();
        return bus != null && (bus.isListening(ScriptEvents.EntitySpawn.class)
                || bus.isListening(ScriptEvents.EntityRemove.class));
    }

    /** Re-publishes client packet events as the split receive/send events scripts subscribe to. */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacket(EventPacket event) {
        var bus = LightClient.getEventBus();
        if (bus == null) return;
        if (event.isReceive()) {
            if (!bus.isListening(ScriptPackets.Receive.class)) return;
            bus.post(new ScriptPackets.Receive(event.getPacket(), event));
        } else {
            if (!bus.isListening(ScriptPackets.Send.class)) return;
            bus.post(new ScriptPackets.Send(event.getPacket(), event));
        }
    }

    /** Runs script binds and hotkeys. */
    @EventHandler
    public void onKey(EventKey event) {
        if (event.getAction() != org.lwjgl.glfw.GLFW.GLFW_PRESS) return;
        if (MinecraftClient.getInstance().currentScreen != null) return;
        int pressed = event.getConvertedKey();
        if (pressed <= 0) return;

        for (ScriptInstance script : scripts.values()) {
            if (script.bind > 0 && script.bind == pressed && script.status != ScriptInstance.Status.ERROR) {
                script.toggle();
                ScriptEvents.post(new ScriptEvents.ModuleToggle(script.displayName(), script.isEnabled(), true));
            }
            if (!script.isEnabled()) continue;
            for (ScriptSettings.Base<?> setting : script.settings) {
                if (setting instanceof ScriptSettings.Hotkey hk && hk.value() == pressed) {
                    hk.trigger();
                }
            }
        }
    }

    /** Publishes the 2D render event scripts draw their HUD in. */
    @EventHandler
    public void onRender2D(EventRender.Screen.Hud event) {
        var bus = LightClient.getEventBus();
        if (bus == null || !bus.isListening(ScriptRender.Event2D.class)) return;
        bus.post(new ScriptRender.Event2D(event.getRenderer(),
                event.getViewportWidth(), event.getViewportHeight(),
                MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false)));
    }

    /** Publishes the 3D render event, setting up a world renderer for scripts to draw with. */
    @EventHandler
    public void onRender3D(EventRender.World event) {
        var bus = LightClient.getEventBus();
        if (bus == null || !bus.isListening(ScriptRender.Event3D.class)) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) return;

        try {
            Camera camera = mc.gameRenderer.getCamera();
            float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, event.getTicks(), true);
            Matrix4f position = new Matrix4f(event.getMatrixStack().peek().getPositionMatrix());
            Matrix4f projection = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov));

            try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera, position, projection)) {
                bus.post(new ScriptRender.Event3D(renderer, camera, RenderUtil.getTickDelta()));
            }
        } catch (Throwable t) {
            LightClient.LOGGER.warn("[Scripts] 3D render dispatch failed", t);
        }
    }

    /** Opens the script folder in the OS file manager. */
    public void openFolder() {
        try {
            Util.getOperatingSystem().open(directory());
        } catch (Throwable ignored) {
        }
    }
}
