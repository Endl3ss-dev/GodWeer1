package never.lose.cosmetics;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Раскладывает аватары в .minecraft/figura/avatars из нескольких источников:
 *   1. ресурсы jar-а -> figura_avatars/**   (если собран с -PbundleCosmetics=true)
 *   2. папки рядом с игрой, см. listCandidateDirs():
 *        .minecraft/endless/cosmetics   (основная)
 *        .minecraft/cosmetics
 *        .minecraft/mods/cosmetics
 *        .minecraft/figura_assets
 *      Лишний уровень вложенности (…/cosmetics/figura_assets/Name) распознаётся сам.
 *
 * Файл пропускается, если размер совпадает с уже установленным, поэтому повторный
 * запуск дешёвый и не трогает изменённые пользователем аватары по размеру.
 */
public final class CosmeticsInstaller {

    public static final String JAR_RESOURCE_ROOT = "figura_avatars";
    public static final String LOCAL_PACK_DIR = "endless/cosmetics";

    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private static final AtomicInteger INSTALLED = new AtomicInteger();
    private static final AtomicInteger REPLACED = new AtomicInteger();
    private static final AtomicInteger SKIPPED = new AtomicInteger();

    private static volatile boolean finished;
    private static volatile String lastError = "";

    private CosmeticsInstaller() {
    }

    public static boolean isRunning() {
        return RUNNING.get();
    }

    public static boolean isFinished() {
        return finished;
    }

    public static int getInstalled() {
        return INSTALLED.get();
    }

    public static int getReplaced() {
        return REPLACED.get();
    }

    public static int getSkipped() {
        return SKIPPED.get();
    }

    public static String getLastError() {
        return lastError;
    }

    public static Path getAvatarsDir() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc == null || mc.runDirectory == null) {
            return null;
        }

        return mc.runDirectory.toPath().resolve("figura").resolve("avatars").normalize();
    }

    public static Path getLocalPackDir() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc == null || mc.runDirectory == null) {
            return null;
        }

        return mc.runDirectory.toPath().resolve("neverlose").resolve("cosmetics").normalize();
    }

    /**
     * Разрешает реальный корень с аватарами.
     * Пользователь часто копирует папку целиком, получая лишний уровень
     * (endless/cosmetics/figura_assets/01a_Shizuko) - спускаемся на уровень ниже,
     * пока не найдём каталог, прямые подпапки которого содержат avatar.json.
     */
    private static Path resolveSourceRoot(Path candidate) {
        if (candidate == null || !Files.isDirectory(candidate)) {
            return null;
        }

        if (containsAvatarFolders(candidate)) {
            return candidate;
        }

        try (Stream<Path> stream = Files.list(candidate)) {
            for (Path child : stream.filter(Files::isDirectory).toList()) {
                if (containsAvatarFolders(child)) {
                    return child;
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static boolean containsAvatarFolders(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(Files::isDirectory)
                    .anyMatch(path -> Files.isRegularFile(path.resolve("avatar.json")));
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Места, где ищем ассеты. Порядок задаёт приоритет.
     * Всё, чего нет, молча пропускается.
     */
    private static List<Path> listCandidateDirs() {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<Path> dirs = new ArrayList<>(4);

        if (mc == null || mc.runDirectory == null) {
            return dirs;
        }

        Path run = mc.runDirectory.toPath().normalize();

        dirs.add(run.resolve("neverlose").resolve("cosmetics"));
        dirs.add(run.resolve("cosmetics"));
        dirs.add(run.resolve("mods").resolve("cosmetics"));
        dirs.add(run.resolve("figura_assets"));

        return dirs;
    }

    public static boolean hasAnySource() {
        for (Path candidate : listCandidateDirs()) {
            if (resolveSourceRoot(candidate) != null) {
                return true;
            }
        }

        try {
            return CosmeticsInstaller.class.getClassLoader().getResources(JAR_RESOURCE_ROOT).hasMoreElements();
        } catch (Throwable t) {
            return false;
        }
    }

    public static void installAsync(Runnable onDone) {
        if (!RUNNING.compareAndSet(false, true)) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                resetCounters();
                installNow();
                finished = true;
                lastError = "";
            } catch (Throwable t) {
                finished = false;
                lastError = t.getClass().getSimpleName();
            } finally {
                RUNNING.set(false);

                if (onDone != null) {
                    onDone.run();
                }
            }
        }, "Endless-Cosmetics-Installer");

        thread.setDaemon(true);
        thread.start();
    }

    private static void resetCounters() {
        finished = false;
        lastError = "";
        INSTALLED.set(0);
        REPLACED.set(0);
        SKIPPED.set(0);
    }

    private static void installNow() throws Exception {
        Path avatarsDir = getAvatarsDir();

        if (avatarsDir == null) {
            throw new IllegalStateException("no run directory");
        }

        Files.createDirectories(avatarsDir);

        for (Path candidate : listCandidateDirs()) {
            Path pack = resolveSourceRoot(candidate);

            if (pack != null) {
                copyDirectory(pack, avatarsDir);
            }
        }

        ClassLoader loader = CosmeticsInstaller.class.getClassLoader();
        Enumeration<URL> roots = loader.getResources(JAR_RESOURCE_ROOT);
        boolean found = false;

        while (roots.hasMoreElements()) {
            URL root = roots.nextElement();
            found = true;
            copyResourceRoot(root, avatarsDir);
        }

        if (!found) {
            copyFromCodeSourceJar(avatarsDir);
        }
    }

    private static void copyResourceRoot(URL root, Path avatarsDir) throws Exception {
        String protocol = root.getProtocol();

        if ("file".equalsIgnoreCase(protocol)) {
            copyDirectory(Path.of(root.toURI()).normalize(), avatarsDir);
            return;
        }

        if ("jar".equalsIgnoreCase(protocol)) {
            JarURLConnection connection = (JarURLConnection) root.openConnection();
            String entryName = connection.getEntryName();

            if (entryName == null || entryName.isEmpty()) {
                entryName = JAR_RESOURCE_ROOT;
            }

            try (JarFile jar = connection.getJarFile()) {
                copyFromJar(jar, entryName, avatarsDir);
            }
        }
    }

    private static void copyFromCodeSourceJar(Path avatarsDir) throws Exception {
        URL location = CosmeticsInstaller.class.getProtectionDomain().getCodeSource().getLocation();

        if (location == null) {
            return;
        }

        URI uri = location.toURI();
        Path path = Path.of(uri).normalize();

        if (Files.isDirectory(path)) {
            Path root = path.resolve(JAR_RESOURCE_ROOT).normalize();

            if (Files.isDirectory(root)) {
                copyDirectory(root, avatarsDir);
            }

            return;
        }

        if (Files.isRegularFile(path)) {
            try (JarFile jar = new JarFile(path.toFile())) {
                copyFromJar(jar, JAR_RESOURCE_ROOT, avatarsDir);
            }
        }
    }

    private static void copyDirectory(Path root, Path avatarsDir) throws IOException {
        if (!Files.isDirectory(root)) {
            return;
        }

        Path normalizedRoot = root.normalize();
        Path normalizedTarget = avatarsDir.normalize();

        try (Stream<Path> stream = Files.walk(normalizedRoot)) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    Path relative = normalizedRoot.relativize(file.normalize()).normalize();

                    if (relative.isAbsolute() || isTraversal(relative)) {
                        return;
                    }

                    Path output = normalizedTarget.resolve(relative).normalize();

                    if (!output.startsWith(normalizedTarget)) {
                        return;
                    }

                    Files.createDirectories(output.getParent());

                    long size = Files.size(file);

                    if (Files.exists(output) && Files.size(output) == size) {
                        SKIPPED.incrementAndGet();
                        return;
                    }

                    if (Files.exists(output)) {
                        REPLACED.incrementAndGet();
                    } else {
                        INSTALLED.incrementAndGet();
                    }

                    Files.copy(file, output, StandardCopyOption.REPLACE_EXISTING);
                } catch (Throwable ignored) {
                }
            });
        }
    }

    private static void copyFromJar(JarFile jar, String rootEntry, Path avatarsDir) throws IOException {
        Path normalizedTarget = avatarsDir.normalize();
        String prefix = rootEntry.endsWith("/") ? rootEntry : rootEntry + "/";
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry == null || entry.isDirectory()) {
                continue;
            }

            String name = entry.getName();

            if (!name.startsWith(prefix)) {
                continue;
            }

            String relativeName = name.substring(prefix.length());

            if (relativeName.isEmpty() || relativeName.contains("\\")) {
                continue;
            }

            Path relative = Path.of(relativeName).normalize();

            if (relative.isAbsolute() || isTraversal(relative)) {
                continue;
            }

            Path output = normalizedTarget.resolve(relative).normalize();

            if (!output.startsWith(normalizedTarget)) {
                continue;
            }

            Files.createDirectories(output.getParent());

            long size = entry.getSize();

            if (Files.exists(output) && size >= 0L && Files.size(output) == size) {
                SKIPPED.incrementAndGet();
                continue;
            }

            if (Files.exists(output)) {
                REPLACED.incrementAndGet();
            } else {
                INSTALLED.incrementAndGet();
            }

            try (InputStream input = jar.getInputStream(entry)) {
                Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static boolean isTraversal(Path path) {
        return path.getNameCount() > 0 && "..".equals(path.getName(0).toString());
    }
}
