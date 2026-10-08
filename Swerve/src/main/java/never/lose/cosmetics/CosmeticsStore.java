package never.lose.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Реестр установленных аватаров + кэш превью-текстур + выбранный/избранное.
 *
 * Сканирование диска только в фоновом потоке и не чаще раза в 10 секунд.
 * GUI читает готовый снимок списка, в рендере файловых операций нет.
 */
public final class CosmeticsStore {

    private static final long RESCAN_INTERVAL_MS = 10_000L;
    private static final CosmeticsStore INSTANCE = new CosmeticsStore();

    private final Object lock = new Object();
    private final List<CosmeticEntry> entries = new ArrayList<>();
    private final Set<String> favorites = new LinkedHashSet<>();
    private final Map<String, Identifier> previewTextures = new ConcurrentHashMap<>();
    private final Set<String> previewRequests = ConcurrentHashMap.newKeySet();

    private volatile int revision;
    private volatile boolean scanning;
    private volatile String selectedFolder = "";
    private volatile String appliedFolder = "";

    private long lastScan;

    private CosmeticsStore() {
    }

    public static CosmeticsStore get() {
        return INSTANCE;
    }

    public int getRevision() {
        return revision;
    }

    public boolean isScanning() {
        return scanning;
    }

    public String getSelectedFolder() {
        return selectedFolder;
    }

    public String getAppliedFolder() {
        return appliedFolder;
    }

    public void setSelectedFolder(String folder) {
        selectedFolder = folder == null ? "" : folder;
    }

    public void setAppliedFolder(String folder) {
        appliedFolder = folder == null ? "" : folder;
    }

    public boolean isFavorite(String folder) {
        synchronized (lock) {
            return favorites.contains(folder);
        }
    }

    public void toggleFavorite(String folder) {
        synchronized (lock) {
            if (!favorites.remove(folder)) {
                favorites.add(folder);
            }

            revision++;
        }
    }

    /** Копирует снимок текущего списка в переданный буфер (буфер переиспользуется GUI). */
    public void copyEntries(List<CosmeticEntry> out) {
        out.clear();

        synchronized (lock) {
            out.addAll(entries);
        }
    }

    public int size() {
        synchronized (lock) {
            return entries.size();
        }
    }

    public CosmeticEntry findByFolder(String folder) {
        if (folder == null || folder.isEmpty()) {
            return null;
        }

        synchronized (lock) {
            for (int i = 0; i < entries.size(); i++) {
                CosmeticEntry entry = entries.get(i);

                if (entry.getFolder().equals(folder)) {
                    return entry;
                }
            }
        }

        return null;
    }

    public void requestScan(boolean force) {
        long now = System.currentTimeMillis();

        synchronized (lock) {
            if (scanning) {
                return;
            }

            if (!force && !entries.isEmpty() && now - lastScan < RESCAN_INTERVAL_MS) {
                return;
            }

            lastScan = now;
            scanning = true;
        }

        Thread thread = new Thread(this::scanNow, "Endless-Cosmetics-Scan");
        thread.setDaemon(true);
        thread.start();
    }

    private void scanNow() {
        List<CosmeticEntry> found = new ArrayList<>();

        try {
            Path avatarsDir = CosmeticsInstaller.getAvatarsDir();

            if (avatarsDir != null && Files.isDirectory(avatarsDir)) {
                try (Stream<Path> stream = Files.list(avatarsDir)) {
                    stream.filter(Files::isDirectory).forEach(path -> {
                        if (!Files.exists(path.resolve("avatar.json"))) {
                            return;
                        }

                        String folder = path.getFileName().toString();
                        String name = readDisplayName(path, folder);
                        found.add(new CosmeticEntry(name, folder, path,
                                detectSection(folder, name), directorySize(path)));
                    });
                }
            }
        } catch (Throwable ignored) {
        }

        found.sort(Comparator
                .comparingInt((CosmeticEntry e) -> e.getSection().ordinal())
                .thenComparing(e -> e.getName().toLowerCase(Locale.ROOT)));

        synchronized (lock) {
            entries.clear();
            entries.addAll(found);
            revision++;
            scanning = false;
        }
    }

    /** Достаёт "name" из avatar.json без полноценного json-парсера: файл читается один раз при скане. */
    private static String readDisplayName(Path folder, String fallback) {
        Path json = folder.resolve("avatar.json");

        try (BufferedReader reader = Files.newBufferedReader(json, StandardCharsets.UTF_8)) {
            String line;
            int guard = 0;

            while ((line = reader.readLine()) != null && guard++ < 40) {
                int keyIndex = line.indexOf("\"name\"");

                if (keyIndex < 0) {
                    continue;
                }

                int colon = line.indexOf(':', keyIndex);

                if (colon < 0) {
                    continue;
                }

                int first = line.indexOf('"', colon + 1);

                if (first < 0) {
                    continue;
                }

                int second = line.indexOf('"', first + 1);

                if (second <= first) {
                    continue;
                }

                String value = line.substring(first + 1, second).trim();

                if (!value.isEmpty()) {
                    return value;
                }
            }
        } catch (Throwable ignored) {
        }

        return cleanName(fallback);
    }

    private static String cleanName(String folder) {
        String value = folder.replace('_', ' ').replace('-', ' ').trim();

        while (value.length() > 1 && Character.isDigit(value.charAt(0))) {
            value = value.substring(1);
        }

        if (!value.isEmpty() && Character.isLetter(value.charAt(0)) && value.length() > 1
                && Character.isUpperCase(value.charAt(1))) {
            value = value.substring(1);
        }

        return value.trim();
    }

    private static long directorySize(Path folder) {
        long[] total = new long[1];

        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    total[0] += Files.size(file);
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable ignored) {
        }

        return total[0];
    }

    private static CosmeticSection detectSection(String folder, String name) {
        String value = (folder + " " + name).toLowerCase(Locale.ROOT);

        if (contains(value, "wing", "крыл", "elytra", "wiiiings")) {
            return CosmeticSection.WINGS;
        }

        if (contains(value, "pet", "turtle", "axolotl", "fox", "питом")) {
            return CosmeticSection.PETS;
        }

        if (contains(value, "hat", "cap", "crown", "beret", "mask", "glasses", "coat",
                "cape", "scythe", "axe", "leaf", "asset", "шляп", "маск")) {
            return CosmeticSection.ACCESSORIES;
        }

        return CosmeticSection.AVATARS;
    }

    private static boolean contains(String value, String... keywords) {
        for (int i = 0; i < keywords.length; i++) {
            if (value.contains(keywords[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Превью из avatar.png. Регистрация текстуры идёт на рендер-треде, чтение файла - в фоне.
     * Повторные запросы одного и того же аватара отсекаются множеством previewRequests.
     */
    public Identifier getPreviewTexture(CosmeticEntry entry) {
        if (entry == null) {
            return null;
        }

        String folder = entry.getFolder();
        Identifier cached = previewTextures.get(folder);

        if (cached != null) {
            return cached;
        }

        if (!previewRequests.add(folder)) {
            return null;
        }

        Path preview = entry.getPath().resolve("avatar.png");

        if (!Files.isRegularFile(preview)) {
            return null;
        }

        Thread thread = new Thread(() -> {
            NativeImage image = null;

            try (InputStream input = Files.newInputStream(preview)) {
                image = NativeImage.read(input);
            } catch (Throwable ignored) {
                return;
            }

            NativeImage finalImage = image;
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc == null || finalImage == null) {
                return;
            }

            mc.execute(() -> {
                try {
                    Identifier id = Identifier.of("n2ver",
                            "cosmetics/preview_" + Integer.toHexString(folder.hashCode()));
                    mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(finalImage));
                    previewTextures.put(folder, id);
                } catch (Throwable ignored) {
                    finalImage.close();
                }
            });
        }, "Endless-Cosmetics-Preview");

        thread.setDaemon(true);
        thread.start();
        return null;
    }
}
