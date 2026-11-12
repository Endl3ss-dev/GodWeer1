package tech.lightclient.ui.gui.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tech.lightclient.LightClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Косметика на базе мода Figura. Модели живут в assets/light/cosmetics,
 * список — в cosmetics/index.json. Выбор косметики экспортирует её файлы в
 * &lt;gameDir&gt;/figura/avatars/Light/Light_Loadout и подгружает через рефлексию
 * в Figura (org.figuramc.figura.*). Без Figura просто ничего не грузится.
 * Облако не используется — только локальное применение к своему аватару.
 */
public final class CosmeticsManager {

    public enum Section {
        MODELS("Модели"),
        HEAD("Аксессуары");

        private final String label;

        Section(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    /** name — отображаемое имя, preview/root — ресурсы, files — относительные пути файлов пака, head — раздел. */
    public record CosmeticItem(String name, Identifier preview, String root, List<String> files, boolean head) {}

    private static final CosmeticsManager INSTANCE = new CosmeticsManager();

    private static final Identifier INDEX = Identifier.of("light", "cosmetics/index.json");
    private static final String RESOURCE_PREFIX = "assets/light/";
    private static final String HEAD_TYPE = "head";

    private static final String FIGURA_AVATARS_FOLDER = "figura/avatars";
    private static final String EXPORT_FOLDER = "Light";
    private static final String LOADOUT_FOLDER = "Light_Loadout";

    private static final AtomicInteger PREVIEW_COUNTER = new AtomicInteger();

    private final List<CosmeticItem> cosmetics = new ArrayList<>();
    private final Map<String, Identifier> previewTextures = new HashMap<>();
    private final Map<String, float[]> previewDims = new HashMap<>();
    private final Set<String> previewFailed = new HashSet<>();

    private String selectedModel = "";
    private boolean loaded = false;
    private boolean applyError = false;

    private static UUID lastStartupPlayer = null;
    private static Object lastStartupWorld = null;

    private CosmeticsManager() {
    }

    public static CosmeticsManager get() {
        return INSTANCE;
    }

    // ── загрузка ─────────────────────────────────────────────────────────────

    public void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        try {
            String json = readResourceText(RESOURCE_PREFIX + INDEX.getPath());
            if (json != null) {
                while (!json.isEmpty() && (json.charAt(0) == '﻿' || Character.isWhitespace(json.charAt(0)))) {
                    json = json.substring(1);
                }
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement element : array) {
                    try {
                        JsonObject o = element.getAsJsonObject();
                        String name = o.get("name").getAsString();
                        String path = o.get("path").getAsString();
                        String preview = o.has("preview") ? o.get("preview").getAsString() : path;
                        String root = o.has("root") ? o.get("root").getAsString() : resolveRoot(preview);
                        String type = o.has("type") ? o.get("type").getAsString() : "models";
                        // Аксессуары (head) отключены: в порте Figura на 1.21.11 они ломают первое лицо.
                        if (HEAD_TYPE.equalsIgnoreCase(type)) continue;
                        List<String> files = readFiles(o);
                        Identifier previewId = safeId(preview);
                        cosmetics.add(new CosmeticItem(name, previewId, root, files, false));
                    } catch (RuntimeException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            LightClient.LOGGER.warn("[Cosmetics] failed to load index", e);
        }
        loadState();
    }

    private static Identifier safeId(String path) {
        try {
            return Identifier.of("light", path);
        } catch (RuntimeException e) {
            return Identifier.of("light", "icon.png");
        }
    }

    private static String resolveRoot(String previewPath) {
        int slash = previewPath.lastIndexOf('/');
        return slash > 0 ? previewPath.substring(0, slash) : previewPath;
    }

    private static List<String> readFiles(JsonObject object) {
        List<String> files = new ArrayList<>();
        if (!object.has("files") || !object.get("files").isJsonArray()) return files;
        for (JsonElement el : object.get("files").getAsJsonArray()) {
            String f = el.getAsString();
            if (f != null && !f.isBlank()) files.add(f);
        }
        return files;
    }

    // ── доступ для GUI ───────────────────────────────────────────────────────

    public List<CosmeticItem> visible(Section section) {
        ensureLoaded();
        List<CosmeticItem> list = new ArrayList<>();
        boolean head = section == Section.HEAD;
        for (CosmeticItem item : cosmetics) {
            if (item.head() == head) list.add(item);
        }
        return list;
    }

    public boolean isSelected(CosmeticItem item) {
        return item.name().equals(selectedModel);
    }

    public boolean hasApplyError() {
        return applyError;
    }

    public void toggle(CosmeticItem item) {
        boolean enable = !item.name().equals(selectedModel);
        selectedModel = enable ? item.name() : "";
        saveState();
        applySelected();
    }

    // ── превью-текстуры (ленивая загрузка avatar.png из classpath) ───────────

    public Identifier previewTexture(CosmeticItem item) {
        String path = item.preview().getPath();
        Identifier cached = previewTextures.get(path);
        if (cached != null) return cached;
        if (previewFailed.contains(path)) return null;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return null;
        try (InputStream in = openResource(RESOURCE_PREFIX + path)) {
            if (in == null) {
                previewFailed.add(path);
                return null;
            }
            NativeImage image = NativeImage.read(in);
            Identifier id = Identifier.of("light", "cosmetic_preview/" + PREVIEW_COUNTER.getAndIncrement());
            TextureManager tm = mc.getTextureManager();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(() -> "light_cosmetic_preview", image);
            tm.registerTexture(id, tex);
            previewTextures.put(path, id);
            previewDims.put(path, new float[]{image.getWidth(), image.getHeight()});
            return id;
        } catch (Exception e) {
            previewFailed.add(path);
            return null;
        }
    }

    public float previewAspect(CosmeticItem item) {
        float[] dims = previewDims.get(item.preview().getPath());
        if (dims == null || dims[1] <= 0f) return 0f;
        return dims[0] / dims[1];
    }

    // ── применение через Figura ──────────────────────────────────────────────

    private void applySelected() {
        applyError = false;
        try {
            CosmeticItem selected = selectedItem();
            recreateDirectory(exportRoot());
            if (selected == null) {
                clearFiguraAvatar();
                reloadFiguraAvatarList();
                return;
            }
            Path loadout = loadoutPath();
            Files.createDirectories(loadout);
            copyResourceDirectory(selected, loadout);
            writeAppliedMarker(loadout, selected);
            reloadFiguraAvatars(loadout);
        } catch (Exception e) {
            applyError = true;
            LightClient.LOGGER.warn("[Cosmetics] apply failed", e);
        }
    }

    private CosmeticItem selectedItem() {
        for (CosmeticItem item : cosmetics) {
            if (isSelected(item)) return item;
        }
        return null;
    }

    private Path exportRoot() {
        return MinecraftClient.getInstance().runDirectory.toPath()
                .resolve(FIGURA_AVATARS_FOLDER).resolve(EXPORT_FOLDER);
    }

    private Path loadoutPath() {
        return exportRoot().resolve(LOADOUT_FOLDER);
    }

    private void recreateDirectory(Path directory) throws Exception {
        if (Files.exists(directory)) {
            try (Stream<Path> paths = Files.walk(directory)) {
                List<Path> toDelete = paths.sorted((a, b) -> b.getNameCount() - a.getNameCount()).toList();
                for (Path p : toDelete) Files.deleteIfExists(p);
            }
        }
        Files.createDirectories(directory);
    }

    private void copyResourceDirectory(CosmeticItem item, Path target) throws Exception {
        Files.createDirectories(target);
        String root = item.root().endsWith("/") ? item.root() : item.root() + "/";
        for (String relative : item.files()) {
            String resourcePath = RESOURCE_PREFIX + root + relative;
            Path out = target.resolve(relative);
            Files.createDirectories(out.getParent());
            try (InputStream in = openResource(resourcePath)) {
                if (in == null) throw new Exception("Missing cosmetic resource: " + resourcePath);
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void writeAppliedMarker(Path avatarPath, CosmeticItem item) throws Exception {
        String json = "{\n"
                + "  \"light_applied\": true,\n"
                + "  \"cosmetic\": \"" + item.name().replace("\\", "\\\\").replace("\"", "\\\"") + "\"\n"
                + "}\n";
        Files.writeString(avatarPath.resolve("light.json"), json);
    }

    // ── рефлексия в Figura ───────────────────────────────────────────────────

    private void reloadFiguraAvatars(Path loadout) {
        try {
            Class<?> manager = Class.forName("org.figuramc.figura.avatar.AvatarManager");
            Method loadLocalAvatar = manager.getMethod("loadLocalAvatar", Path.class);
            loadLocalAvatar.invoke(null, loadout);
            return;
        } catch (ReflectiveOperationException ignored) {
        }
        reloadFiguraAvatarList();
    }

    private void clearFiguraAvatar() {
        try {
            Class<?> manager = Class.forName("org.figuramc.figura.avatar.AvatarManager");
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                Method clearAvatars = manager.getMethod("clearAvatars", UUID.class);
                clearAvatars.invoke(null, mc.player.getUuid());
            }
            Method clearAllAvatars = manager.getMethod("clearAllAvatars");
            clearAllAvatars.invoke(null);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private void reloadFiguraAvatarList() {
        try {
            Class<?> fetcher = Class.forName("org.figuramc.figura.avatar.local.LocalAvatarFetcher");
            Method reloadAvatars = fetcher.getMethod("reloadAvatars");
            reloadAvatars.invoke(null);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    // ── состояние (персист) ──────────────────────────────────────────────────

    private Path stateFile() {
        return MinecraftClient.getInstance().runDirectory.toPath()
                .resolve(EXPORT_FOLDER).resolve("cosmetics.json");
    }

    private void loadState() {
        try {
            Path file = stateFile();
            if (!Files.exists(file)) return;
            JsonObject o = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            selectedModel = o.has("model") ? o.get("model").getAsString() : "";
            sanitizeState();
            applySelected();
        } catch (Exception e) {
            selectedModel = "";
        }
    }

    private void saveState() {
        try {
            Path file = stateFile();
            Files.createDirectories(file.getParent());
            String json = "{\n"
                    + "  \"model\": \"" + selectedModel.replace("\\", "\\\\").replace("\"", "\\\"") + "\"\n"
                    + "}\n";
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    private void sanitizeState() {
        if (!knownSelection(selectedModel)) selectedModel = "";
    }

    private boolean knownSelection(String name) {
        if (name == null || name.isEmpty()) return true;
        for (CosmeticItem item : cosmetics) {
            if (!item.head() && item.name().equals(name)) return true;
        }
        return false;
    }

    // ── старт: применить сохранённую косметику при заходе в мир ───────────────

    private static int reapplyDelay = -1;

    public static void applySavedOnStartup() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        UUID playerId = client.player.getUuid();
        if (!playerId.equals(lastStartupPlayer) || client.world != lastStartupWorld) {
            lastStartupPlayer = playerId;
            lastStartupWorld = client.world;
            INSTANCE.ensureLoaded();
            reapplyDelay = 20;
        }
        if (reapplyDelay > 0 && --reapplyDelay == 0) {
            INSTANCE.applySelected();
        }
    }

    // ── ресурсы ──────────────────────────────────────────────────────────────

    private static InputStream openResource(String path) {
        ClassLoader ctx = Thread.currentThread().getContextClassLoader();
        InputStream in = ctx != null ? ctx.getResourceAsStream(path) : null;
        if (in != null) return in;
        return CosmeticsManager.class.getClassLoader().getResourceAsStream(path);
    }

    private static String readResourceText(String path) throws Exception {
        try (InputStream in = openResource(path)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
