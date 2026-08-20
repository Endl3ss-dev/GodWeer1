package fun.volon.client.modules.impl.misc;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class UnhookHelper {

    private static ScheduledExecutorService baritoneBlockerThread;

    // ── Mod JAR move ─────────────────────────────────────────────────────────

    /**
     * hide=true  → переносит все .jar из mods/ которые содержат "evel" в имени в C:\GtaIV\mods_hidden\
     * hide=false → возвращает их обратно в mods/
     */
    public static void moveModJar(boolean hide, File runDirectory) {
        try {
            File modsDir = new File(runDirectory, "mods");
            if (!modsDir.exists() || !modsDir.isDirectory()) return;

            File hiddenDir = new File("C:\\GtaIV\\mods_hidden");

            if (hide) {
                hiddenDir.mkdirs();
                File[] jars = modsDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jar") &&
                        (name.toLowerCase().contains("evel") ||
                         name.toLowerCase().contains("volon") ||
                         name.toLowerCase().contains("evelbeta")));
                if (jars == null) return;
                for (File jar : jars) {
                    File dest = new File(hiddenDir, jar.getName());
                    jar.renameTo(dest);
                }
            } else {
                // Возвращаем из GtaIV обратно
                File restoreDir = new File("C:\\GtaIV\\mods_hidden");
                if (!restoreDir.exists()) return;
                File[] jars = restoreDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
                if (jars == null) return;
                modsDir.mkdirs();
                for (File jar : jars) {
                    File dest = new File(modsDir, jar.getName());
                    jar.renameTo(dest);
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Config folder rename ──────────────────────────────────────────────────

    /**
     * hide=true  → VolonClient → GtaIV, volon → torrent
     * hide=false → GtaIV       → VolonClient, torrent → volon
     */
    public static void renameConfigFolder(boolean hide) {
        try {
            if (hide) {
                // Сначала переименовываем подпапку volon → torrent
                File volonDir = new File("C:\\VolonClient", "volon");
                File torrentDir = new File("C:\\VolonClient", "torrent");
                if (volonDir.exists()) {
                    volonDir.renameTo(torrentDir);
                }
                // Потом переименовываем VolonClient → GtaIV
                File from = new File("C:\\VolonClient");
                File to   = new File("C:\\GtaIV");
                if (from.exists()) {
                    from.renameTo(to);
                }
            } else {
                // Сначала переименовываем GtaIV → VolonClient
                File from = new File("C:\\GtaIV");
                File to   = new File("C:\\VolonClient");
                if (from.exists()) {
                    from.renameTo(to);
                }
                // Потом переименовываем torrent → volon
                File torrentDir = new File("C:\\VolonClient", "torrent");
                File volonDir   = new File("C:\\VolonClient", "volon");
                if (torrentDir.exists()) {
                    torrentDir.renameTo(volonDir);
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Baritone blocker ─────────────────────────────────────────────────────

    public static void startBaritoneBlocker(File runDirectory) {
        if (baritoneBlockerThread != null && !baritoneBlockerThread.isShutdown()) return;

        baritoneBlockerThread = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Baritone-Blocker");
            t.setDaemon(true); // daemon — не мешает JVM завершиться
            return t;
        });
        baritoneBlockerThread.scheduleAtFixedRate(() -> {
            try {
                File baritoneDir = new File(runDirectory, "baritone");
                if (baritoneDir.exists() && baritoneDir.isDirectory()) {
                    deleteDirectory(baritoneDir.toPath());
                }
                if (!baritoneDir.exists()) {
                    // Создаём файл вместо папки — блокирует mkdir
                    baritoneDir.createNewFile();
                    baritoneDir.setReadOnly();
                }
            } catch (Exception ignored) {}
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    public static void stopBaritoneBlocker(File runDirectory) {
        if (baritoneBlockerThread != null && !baritoneBlockerThread.isShutdown()) {
            baritoneBlockerThread.shutdownNow();
            baritoneBlockerThread = null;
        }
        // Убираем файл-заглушку, чтобы baritone мог создать папку снова
        try {
            File baritoneStub = new File(runDirectory, "baritone");
            if (baritoneStub.exists() && baritoneStub.isFile()) {
                baritoneStub.delete();
            }
        } catch (Exception ignored) {}
    }

    // ── Deep clean entry point ────────────────────────────────────────────────

    public static void performDeepClean() {
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("win")) return;
            cleanWindowsRegistry();
            cleanUSNJournal();
            cleanShellBags();
            cleanEverythingIndex();
            cleanMFTRecords();
            cleanTempFiles();
            cleanDNSCache();
            cleanWindowsEvents();
        } catch (Exception ignored) {}
    }

    // ── Registry ──────────────────────────────────────────────────────────────

    private static void cleanWindowsRegistry() {
        String[] keys = {
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RecentDocs",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\OpenSavePidlMRU",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\ComDlg32\\LastVisitedPidlMRU",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Search\\RecentApps",
            "HKCU\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\MuiCache",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\TypedPaths",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\RunMRU"
        };
        for (String key : keys) {
            runCmd("reg delete \"" + key + "\" /f", 5);
        }
    }

    // ── USN Journal ───────────────────────────────────────────────────────────

    private static void cleanUSNJournal() {
        String drive = System.getenv("SystemDrive");
        if (drive == null) drive = "C:";
        runCmd("fsutil usn deletejournal /D " + drive, 10);
    }

    // ── ShellBags ─────────────────────────────────────────────────────────────

    /**
     * Чистит записи ShellBag за последние 48 часов через reg-фильтрацию по LastWrite.
     * Если получить timestamp невозможно (нет прав / старая OS) — удаляем полностью.
     */
    private static void cleanShellBags() {
        String[] keys = {
            "HKCU\\Software\\Microsoft\\Windows\\Shell\\Bags",
            "HKCU\\Software\\Microsoft\\Windows\\Shell\\BagMRU",
            "HKCU\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\Bags",
            "HKCU\\Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\BagMRU"
        };

        long cutoffEpochMs = System.currentTimeMillis() - 48L * 60 * 60 * 1000;

        for (String key : keys) {
            try {
                // Пробуем получить список подключей и их LastWriteTime через reg query
                Process p = new ProcessBuilder("reg", "query", key, "/s").start();
                List<String> subkeys = new ArrayList<>();
                try (BufferedReader br =
                        new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("HKCU") || line.startsWith("HKEY")) {
                            subkeys.add(line.trim());
                        }
                    }
                }
                p.waitFor(5, TimeUnit.SECONDS);

                if (subkeys.isEmpty()) {
                    // Ключ пустой или недоступен — пропускаем
                    continue;
                }

                boolean anyDeleted = false;
                for (String subkey : subkeys) {
                    // Получаем LastWriteTime через reg query с флагом /t REG_SZ не даст timestamp,
                    // используем PowerShell для точного времени
                    long lastWrite = getRegKeyLastWriteTime(subkey);
                    if (lastWrite < 0 || lastWrite >= cutoffEpochMs) {
                        // Неизвестное время или свежая запись — удаляем
                        runCmd("reg delete \"" + subkey + "\" /f", 5);
                        anyDeleted = true;
                    }
                }

                if (!anyDeleted) {
                    // Все записи старше 48 часов — оставляем
                }
            } catch (Exception e) {
                // Fallback: удаляем весь ключ если не удалось разобрать
                runCmd("reg delete \"" + key + "\" /f", 5);
            }
        }
    }

    /**
     * Возвращает время последней записи реестрового ключа в миллисекундах epoch UTC.
     * Использует PowerShell Get-Item. Возвращает -1 если не удалось.
     */
    private static long getRegKeyLastWriteTime(String regKey) {
        try {
            // Преобразуем путь: HKCU → HKCU:, пробелы экранируем
            String psPath = regKey
                .replace("HKCU\\", "HKCU:\\")
                .replace("HKLM\\", "HKLM:\\")
                .replace("HKCU\\\\", "HKCU:\\");

            String psCmd = "(Get-Item -LiteralPath '" + psPath + "' -ErrorAction SilentlyContinue)"
                         + ".LastWriteTime.ToUniversalTime()"
                         + ".ToString('yyyy-MM-ddTHH:mm:ss')";

            Process p = new ProcessBuilder(
                    "powershell.exe", "-NoProfile", "-NonInteractive", "-Command", psCmd)
                    .start();

            String output;
            try (BufferedReader br =
                    new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                output = br.readLine();
            }
            p.waitFor(4, TimeUnit.SECONDS);

            if (output == null || output.isBlank()) return -1;

            LocalDateTime ldt = LocalDateTime.parse(output.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
        } catch (Exception e) {
            return -1;
        }
    }

    // ── Everything index ──────────────────────────────────────────────────────

    private static void cleanEverythingIndex() {
        try {
            runCmd("taskkill /F /IM Everything.exe", 3);
            String appData = System.getenv("APPDATA");
            if (appData == null) return;
            for (String name : new String[]{
                "Everything.db", "Everything.ini",
                "Search History.csv", "Run History.csv"}) {
                secureDelete(new File(appData + "\\Everything\\" + name));
            }
        } catch (Exception ignored) {}
    }

    // ── MFT / free space wipe ─────────────────────────────────────────────────

    private static void cleanMFTRecords() {
        try {
            File tmp = File.createTempFile("fsclean_", ".tmp");
            tmp.deleteOnExit();
            byte[] buf = new byte[1024 * 1024];
            new Random().nextBytes(buf);
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                for (int i = 0; i < 50; i++) {
                    try { fos.write(buf); } catch (IOException e) { break; }
                }
            }
            secureDelete(tmp);
        } catch (Exception ignored) {}
    }

    // ── Temp files ────────────────────────────────────────────────────────────

    private static void cleanTempFiles() {
        String[] dirs = {
            System.getProperty("java.io.tmpdir"),
            System.getenv("TEMP"),
            System.getenv("TMP")
        };
        String[] keywords = {"volon", "evel", "minecraft", "fabric"};
        for (String dirPath : dirs) {
            if (dirPath == null) continue;
            File dir = new File(dirPath);
            File[] files = dir.listFiles();
            if (files == null) continue;
            for (File f : files) {
                String name = f.getName().toLowerCase();
                for (String kw : keywords) {
                    if (name.contains(kw)) {
                        try {
                            if (f.isFile()) secureDelete(f);
                            else deleteDirectory(f.toPath());
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }
        }
    }

    // ── DNS cache ─────────────────────────────────────────────────────────────

    private static void cleanDNSCache() {
        runCmd("ipconfig /flushdns", 5);
    }

    // ── Windows Event Log ─────────────────────────────────────────────────────

    private static void cleanWindowsEvents() {
        for (String log : new String[]{"Application", "System", "Security"}) {
            runCmd("wevtutil cl " + log, 5);
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private static void secureDelete(File file) {
        if (file == null || !file.exists() || !file.isFile()) return;
        try {
            long size = file.length();
            Random rnd = new Random();
            for (int pass = 0; pass < 3; pass++) {
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                    raf.seek(0);
                    byte[] buf = new byte[4096];
                    long remaining = size;
                    while (remaining > 0) {
                        int n = (int) Math.min(buf.length, remaining);
                        rnd.nextBytes(buf);
                        raf.write(buf, 0, n);
                        remaining -= n;
                    }
                }
            }
        } catch (Exception ignored) {}
        file.delete();
    }

    static void deleteDirectory(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path f, BasicFileAttributes a) {
                    try { Files.delete(f); } catch (IOException ignored) {}
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path d, IOException e) {
                    try { Files.delete(d); } catch (IOException ignored) {}
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ignored) {}
    }

    private static void runCmd(String cmd, int timeoutSeconds) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception ignored) {}
    }
}
