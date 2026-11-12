package tech.lightclient.scripting.runtime;


import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

/**
 * Owns the embedded Kotlin scripting engine.
 *
 * <p>Two things here are load-bearing and were established by testing rather than assumption:</p>
 * <ul>
 *   <li>The compilation classpath must be handed over explicitly through
 *       {@code kotlin.script.classpath}, because under Fabric neither Minecraft nor this mod
 *       appears on {@code java.class.path}. See {@link ScriptClasspath}.</li>
 *   <li>The engine has to be created with our own class loader as the context loader, otherwise
 *       the {@code ScriptEngineManager} service lookup finds nothing and returns null.</li>
 * </ul>
 */
public final class ScriptEngineHost {

    private static volatile ScriptEngine engine;
    private static volatile String initError;
    private static volatile boolean attempted = false;

    private ScriptEngineHost() {}

    /** True when the engine is up; false means {@link #initError()} explains why not. */
    public static boolean available() {
        return engine() != null;
    }

    public static String initError() {
        return initError;
    }

    public static ScriptEngine engine() {
        ScriptEngine e = engine;
        if (e != null || attempted) return e;
        synchronized (ScriptEngineHost.class) {
            if (engine != null || attempted) return engine;
            attempted = true;
            engine = create();
            return engine;
        }
    }

    private static ScriptEngine create() {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader ours = ScriptEngineHost.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(ours);

            // Respect a classpath supplied by the environment (the verifyScripts harness does
            // this); otherwise work it out from Fabric's view of the world.
            String existing = System.getProperty("kotlin.script.classpath");
            if (existing == null || existing.isBlank()) {
                System.setProperty("kotlin.script.classpath", ScriptClasspath.resolve());
            }
            // The compiler asks the JDK for its version and, on very new JDKs, chokes on the
            // string. Pinning the target keeps it on a code path it understands.
        // Kotlin 2.0.21 does not recognize JVM target 25; the host itself still runs on Java 25.
        System.setProperty("kotlin.script.base.compiler.arguments", "-jvm-target=21");

            long start = System.currentTimeMillis();
            ScriptEngine created = new ScriptEngineManager(ours).getEngineByExtension("kts");
            if (created == null) {
                initError = "the Kotlin .kts script engine is not on the classpath";
                log("[Scripts] " + initError, null);
                return null;
            }
            log("[Scripts] Kotlin script engine ready in " + (System.currentTimeMillis() - start) + " ms", null);
            return created;
        } catch (Throwable t) {
            initError = describe(t);
            log("[Scripts] could not start the Kotlin script engine", t);
            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /**
     * Logs without forcing {@code LightClient} to initialise, so this class also works in the
     * standalone verification harness where Minecraft is absent.
     */
    private static void log(String message, Throwable t) {
        try {
            if (t == null) tech.lightclient.LightClient.LOGGER.info(message);
            else tech.lightclient.LightClient.LOGGER.error(message, t);
        } catch (Throwable ignored) {
            System.out.println(message);
            if (t != null) t.printStackTrace(System.out);
        }
    }

    /**
     * Compiles and runs one script's source.
     *
     * <p>Each script gets a fresh binding set and context so that top-level declarations in one
     * script cannot be seen by another.</p>
     */
    public static void eval(String source, Bindings bindings) throws Exception {
        ScriptEngine e = engine();
        if (e == null) throw new IllegalStateException(initError == null ? "script engine unavailable" : initError);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ScriptEngineHost.class.getClassLoader());
            ScriptContext context = new SimpleScriptContext();
            context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            e.eval(source, context);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    public static Bindings newBindings() {
        ScriptEngine e = engine();
        return e == null ? new javax.script.SimpleBindings() : e.createBindings();
    }

    static String describe(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String message = root.getMessage();
        return message != null && !message.isBlank() ? message : root.getClass().getSimpleName();
    }
}
