package tech.lightclient.scripting.runtime;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import tech.lightclient.LightClient;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Works out the classpath the embedded Kotlin compiler must see in order to resolve the
 * scripting API (and Minecraft itself) from inside a script.
 *
 * <p>This exists because Fabric does <em>not</em> put mods or the game on {@code java.class.path};
 * everything lives inside Knot's own class loader. The Kotlin JSR-223 engine, on the other hand,
 * defaults to {@code java.class.path} for its compilation classpath, so without this a script
 * would fail with "unresolved reference" for every single API symbol.</p>
 *
 * <p>The resolved entries are handed to the compiler through the {@code kotlin.script.classpath}
 * system property, which the scripting host honours.</p>
 */
public final class ScriptClasspath {

    private static volatile String cached;

    private ScriptClasspath() {}

    /** Returns the compiler classpath as a path-separator delimited string. */
    public static String resolve() {
        String c = cached;
        if (c != null) return c;
        synchronized (ScriptClasspath.class) {
            if (cached != null) return cached;
            cached = build();
            return cached;
        }
    }

    private static String build() {
        Set<String> entries = new LinkedHashSet<>();

        // 1. Whatever is genuinely on the JVM classpath (usually just the loader under Fabric,
        //    but the whole thing in a dev environment).
        String jvmCp = System.getProperty("java.class.path", "");
        for (String part : jvmCp.split(File.pathSeparator)) {
            addIfUsable(entries, part);
        }

        // 2. Our own code location. In dev this is a classes directory, in production the mod jar.
        addCodeSource(entries, ScriptClasspath.class);
        // The Kotlin runtime pieces the compiler itself needs to see: stdlib and the script
        // runtime, which is where the compiled script's own supertypes come from.
        addCodeSource(entries, kotlin.Unit.class);
        addByName(entries, "kotlin.script.dependencies.ScriptContents");
        addByName(entries, "kotlin.script.experimental.api.ScriptCompilationConfiguration");

        // 3. Minecraft plus every loaded mod, straight out of Fabric's metadata. This is what makes
        //    net.minecraft.* and our own tech.lightclient.* resolvable inside scripts.
        try {
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                for (Path root : mod.getRootPaths()) {
                    addModRoot(entries, root);
                }
            }
        } catch (Throwable t) {
            LightClient.LOGGER.warn("[Scripts] could not enumerate Fabric mods for the script classpath", t);
        }

        // 4. Anything reachable through URL class loaders in our ancestry.
        try {
            ClassLoader loader = ScriptClasspath.class.getClassLoader();
            while (loader != null) {
                if (loader instanceof URLClassLoader url) {
                    for (URL u : url.getURLs()) addUrl(entries, u);
                }
                loader = loader.getParent();
            }
        } catch (Throwable ignored) {
        }

        List<String> ordered = new ArrayList<>(entries);
        String result = String.join(File.pathSeparator, ordered);
        LightClient.LOGGER.info("[Scripts] script compiler classpath resolved with {} entries", ordered.size());
        verify(ordered);
        return result;
    }

    /**
     * Checks that the entries actually contain the classes a script must resolve, and says so in
     * the log when they do not.
     *
     * <p>A long list of paths is no guarantee: under Fabric the interesting jars are remapped
     * copies in odd locations, and if the ones holding Minecraft or this mod are missing then every
     * script fails with "unresolved reference", which reads like the script's fault rather than a
     * setup problem. Naming the missing piece here turns that into a one-line diagnosis.
     */
    private static void verify(List<String> entries) {
        boolean hasOurApi = false;
        boolean hasMinecraft = false;
        boolean hasKotlinStdlib = false;

        for (String entry : entries) {
            if (hasOurApi && hasMinecraft && hasKotlinStdlib) break;
            try {
                File f = new File(entry);
                if (f.isDirectory()) {
                    if (new File(f, "tech/lightclient/scripting/api/PlayerApi.class").exists()) hasOurApi = true;
                    if (new File(f, "net/minecraft/client/MinecraftClient.class").exists()) hasMinecraft = true;
                    if (new File(f, "kotlin/Unit.class").exists()) hasKotlinStdlib = true;
                    continue;
                }
                if (!entry.endsWith(".jar") && !entry.endsWith(".zip")) continue;
                try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(f)) {
                    if (zip.getEntry("tech/lightclient/scripting/api/PlayerApi.class") != null) hasOurApi = true;
                    if (zip.getEntry("net/minecraft/client/MinecraftClient.class") != null) hasMinecraft = true;
                    if (zip.getEntry("kotlin/Unit.class") != null) hasKotlinStdlib = true;
                }
            } catch (Throwable ignored) {
            }
        }

        if (hasOurApi && hasMinecraft && hasKotlinStdlib) {
            LightClient.LOGGER.info("[Scripts] classpath check passed: scripting API, Minecraft and Kotlin stdlib all present");
            return;
        }
        StringBuilder missing = new StringBuilder();
        if (!hasOurApi) missing.append(" scripting-api");
        if (!hasMinecraft) missing.append(" minecraft");
        if (!hasKotlinStdlib) missing.append(" kotlin-stdlib");
        LightClient.LOGGER.error("[Scripts] classpath is missing:{} - scripts will fail to compile until this is fixed", missing);
        ScriptConsole.error("scripts", "compiler classpath is missing:" + missing);
    }

    /**
     * Fabric exposes mod roots as paths inside its own virtual file systems. A jar-backed root has
     * to be turned back into the real jar file, because the Kotlin compiler can only read plain
     * files and directories.
     */
    private static void addModRoot(Set<String> entries, Path root) {
        if (root == null) return;
        try {
            // Directory on disk (dev environment / extracted mods).
            if (Files.isDirectory(root) && root.getFileSystem() == java.nio.file.FileSystems.getDefault()) {
                addIfUsable(entries, root.toAbsolutePath().toString());
                return;
            }
            // Inside a zip/jar file system: recover the backing archive.
            URI uri = root.toUri();
            String s = uri.toString();
            int bang = s.indexOf("!/");
            if (bang > 0) {
                String outer = s.substring(0, bang);
                if (outer.startsWith("jar:")) outer = outer.substring(4);
                try {
                    addIfUsable(entries, java.nio.file.Paths.get(URI.create(outer)).toAbsolutePath().toString());
                    return;
                } catch (Throwable ignored) {
                }
            }
            if (root.getFileSystem() != java.nio.file.FileSystems.getDefault()) {
                // e.g. Fabric's own memory file system - try the file store name, which for
                // jar file systems is the archive path.
                try {
                    Object store = root.getFileSystem().getFileStores().iterator().next();
                    addIfUsable(entries, String.valueOf(store));
                } catch (Throwable ignored) {
                }
                return;
            }
            addIfUsable(entries, root.toAbsolutePath().toString());
        } catch (Throwable ignored) {
        }
    }

    private static void addCodeSource(Set<String> entries, Class<?> type) {
        try {
            CodeSource cs = type.getProtectionDomain().getCodeSource();
            if (cs != null && cs.getLocation() != null) addUrl(entries, cs.getLocation());
        } catch (Throwable ignored) {
        }
    }

    /** Same as {@link #addCodeSource} but tolerates the class not being present at all. */
    private static void addByName(Set<String> entries, String className) {
        try {
            addCodeSource(entries, Class.forName(className, false, ScriptClasspath.class.getClassLoader()));
        } catch (Throwable ignored) {
        }
    }

    private static void addUrl(Set<String> entries, URL url) {
        try {
            String s = url.toString();
            int bang = s.indexOf("!/");
            if (bang > 0) {
                if (s.startsWith("jar:")) s = s.substring(4);
                s = s.substring(0, bang);
                url = new URI(s).toURL();
            }
            if (!"file".equalsIgnoreCase(url.getProtocol())) return;
            addIfUsable(entries, java.nio.file.Paths.get(url.toURI()).toAbsolutePath().toString());
        } catch (Throwable ignored) {
        }
    }

    private static void addIfUsable(Set<String> entries, String path) {
        if (path == null || path.isBlank()) return;
        try {
            File f = new File(path);
            if (!f.exists()) return;
            if (f.isDirectory() || path.endsWith(".jar") || path.endsWith(".zip")) {
                entries.add(f.getAbsolutePath());
            }
        } catch (Throwable ignored) {
        }
    }
}
