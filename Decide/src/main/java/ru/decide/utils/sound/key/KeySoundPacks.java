/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.minecraft.class_156
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.sound.key;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_156;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.sound.key.KeySoundDecoder;
import ru.decide.utils.sound.key.KeySoundEngine;

@Environment(value=EnvType.CLIENT)
public final class KeySoundPacks {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:KeySounds");
    private static final String FOLDER_NAME = "decide_keyboards";
    private static final String RESOURCE_ROOT = "assets/decide/key_sounds";
    private static final String INDEX_FILE = "index.txt";
    private static final String SINGLE_PACK = "single";
    private static final String HOW_TO_ADD = "DecideVisuals Key Sounds\n\nPut .ogg, .wav or .mp3 files here to create the 'single' sound pack.\nOr create folders and put sounds inside them. Subfolders are supported.\nOGG must contain Vorbis audio. WAV supports mono/stereo PCM or 32-bit float.\nMP3 is supported through JLayer.\n\nAdd index.txt inside a pack folder to control sound order.\nEach line is a relative path to a sound file. Lines starting with # are comments.\nExtra sounds not listed in index.txt are still included.\n\nUse 'Rescan packs' in Sounds key after changing files.\n";
    public static final String DEFAULT_KEYBOARD_PACK = "cherrymx_black_pbt";
    public static final String DEFAULT_MOUSE_PACK = "cherrymx_blue_abs_2";
    private static final List<String> BUILTIN_PACKS = List.of("cherrymx_blue_abs", "cherrymx_blue_abs_2", "cherrymx_black_abs", "cherrymx_black_pbt", "cherrymx_brown_abs", "cherrymx_brown_pbt", "cherrymx_red_abs", "hit", "hit1", "hit2", "hit3", "hit4", "hit5", "hit6", "hit7", "hit8");
    private static volatile Map<String, List<Path>> packs = Collections.emptyMap();
    private static volatile List<String> packNames = Collections.emptyList();
    private static boolean initialized;
    private static boolean bootstrapped;

    private KeySoundPacks() {
    }

    public static synchronized void ensureReady() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            KeySoundPacks.bootstrap();
            KeySoundPacks.scan();
        }
        catch (RuntimeException e) {
            LOGGER.warn("Could not initialize key sound packs", (Throwable)e);
        }
    }

    public static synchronized List<String> rescan() {
        initialized = true;
        bootstrapped = false;
        KeySoundEngine.clearCache();
        KeySoundPacks.bootstrap();
        KeySoundPacks.scan();
        return packNames;
    }

    public static Path getFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(FOLDER_NAME).toAbsolutePath().normalize();
    }

    public static List<String> packNames() {
        KeySoundPacks.ensureReady();
        List<String> names = packNames;
        return names.isEmpty() ? BUILTIN_PACKS : names;
    }

    public static List<String> valuesFor(String preferred) {
        KeySoundPacks.ensureReady();
        List<String> names = KeySoundPacks.packNames();
        ArrayList<String> values = new ArrayList<String>(names.size() + 1);
        if (preferred != null && names.contains(preferred)) {
            values.add(preferred);
        }
        for (String name : names) {
            if (values.contains(name)) continue;
            values.add(name);
        }
        if (values.isEmpty()) {
            values.addAll(BUILTIN_PACKS);
        }
        return List.copyOf(values);
    }

    public static Path pickSound(String pack) {
        KeySoundPacks.ensureReady();
        Map<String, List<Path>> current = packs;
        if (current.isEmpty()) {
            return null;
        }
        List<Path> sounds = current.get(pack != null && !pack.isBlank() ? pack : SINGLE_PACK);
        if (sounds == null || sounds.isEmpty()) {
            sounds = current.get(SINGLE_PACK);
        }
        if (sounds == null || sounds.isEmpty()) {
            sounds = current.values().iterator().next();
        }
        return sounds == null || sounds.isEmpty() ? null : sounds.get(ThreadLocalRandom.current().nextInt(sounds.size()));
    }

    public static void warmup(String keyboardPack, int keyboardAmplify, String mousePack, int mouseAmplify) {
        List<Path> mouse;
        KeySoundPacks.ensureReady();
        Map<String, List<Path>> current = packs;
        List<Path> keyboard = current.get(keyboardPack);
        if (keyboard != null && !keyboard.isEmpty()) {
            KeySoundEngine.warmup(keyboard, keyboardAmplify);
        }
        if ((mouse = current.get(mousePack)) != null && !mouse.isEmpty()) {
            KeySoundEngine.warmup(mouse, mouseAmplify);
        }
    }

    public static void openFolder() {
        KeySoundPacks.ensureReady();
        try {
            Files.createDirectories(KeySoundPacks.getFolder(), new FileAttribute[0]);
            class_156.method_668().method_672(KeySoundPacks.getFolder().toFile());
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("Could not open key sound folder {}", (Object)KeySoundPacks.getFolder(), (Object)e);
        }
    }

    private static void bootstrap() {
        Path folder = KeySoundPacks.getFolder();
        if (bootstrapped && Files.isDirectory(folder, new LinkOption[0])) {
            return;
        }
        try {
            Files.createDirectories(folder, new FileAttribute[0]);
        }
        catch (IOException e) {
            LOGGER.error("Could not create key sound folder {}", (Object)folder, (Object)e);
            return;
        }
        Path howTo = folder.resolve("HOW_TO_ADD.txt");
        if (!Files.exists(howTo, new LinkOption[0])) {
            try {
                Files.writeString(howTo, (CharSequence)HOW_TO_ADD, new OpenOption[0]);
            }
            catch (IOException e) {
                LOGGER.warn("Could not write {}", (Object)howTo, (Object)e);
            }
        }
        KeySoundPacks.installBundled(folder, "keyboards");
        KeySoundPacks.installBundled(folder, "bundled_keyboards");
        KeySoundPacks.writeIndexes(folder);
        bootstrapped = true;
    }

    private static void installBundled(Path folder, String rootName) {
        Optional container = FabricLoader.getInstance().getModContainer("decide");
        if (container.isEmpty()) {
            return;
        }
        Optional resourceRoot = ((ModContainer)container.get()).findPath("assets/decide/key_sounds/" + rootName);
        if (resourceRoot.isEmpty()) {
            LOGGER.warn("Bundled key sound resources are missing at {}/{}", (Object)RESOURCE_ROOT, (Object)rootName);
            return;
        }
        Path sourceRoot = (Path)resourceRoot.get();
        try (Stream<Path> files = Files.walk(sourceRoot, new FileVisitOption[0]);){
            files.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).forEach(source -> {
                Path relative = sourceRoot.relativize((Path)source);
                Path output = folder.resolve(relative.toString());
                if (Files.exists(output, new LinkOption[0])) {
                    return;
                }
                try {
                    Files.createDirectories(output.getParent(), new FileAttribute[0]);
                    Files.copy(source, output, new CopyOption[0]);
                }
                catch (IOException e) {
                    LOGGER.warn("Could not copy bundled key sound {}", (Object)output, (Object)e);
                }
            });
        }
        catch (IOException | UncheckedIOException e) {
            LOGGER.warn("Could not install bundled key sounds from {}", (Object)sourceRoot, (Object)e);
        }
    }

    private static void writeIndexes(Path folder) {
        for (String pack : BUILTIN_PACKS) {
            Path index;
            Path source = folder.resolve(pack);
            if (!Files.isDirectory(source, new LinkOption[0]) || Files.exists(index = source.resolve(INDEX_FILE), new LinkOption[0])) continue;
            ArrayList<String> lines = new ArrayList<String>();
            for (int i = 1; i <= 40; ++i) {
                String file = String.format(Locale.ROOT, "key_%02d.ogg", i);
                if (!Files.isRegularFile(source.resolve(file), new LinkOption[0])) continue;
                lines.add(file);
            }
            if (lines.isEmpty()) continue;
            try {
                Files.write(index, lines, new OpenOption[0]);
            }
            catch (IOException e) {
                LOGGER.warn("Could not write key sound index {}", (Object)index, (Object)e);
            }
        }
    }

    private static void scan() {
        Path folder = KeySoundPacks.getFolder();
        LinkedHashMap<String, List<Path>> found = new LinkedHashMap<String, List<Path>>();
        List<Path> single = KeySoundPacks.loadPack(folder, false);
        if (!single.isEmpty()) {
            found.put(SINGLE_PACK, single);
        }
        try (Stream<Path> stream = Files.list(folder);){
            for (Path packDir : stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> !path.getFileName().toString().startsWith(".")).sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList())) {
                List<Path> sounds = KeySoundPacks.loadPack(packDir, true);
                if (sounds.isEmpty()) continue;
                found.put(packDir.getFileName().toString(), sounds);
            }
        }
        catch (IOException | UncheckedIOException e) {
            LOGGER.warn("Could not scan key sound folder {}", (Object)folder, (Object)e);
        }
        packs = Collections.unmodifiableMap(found);
        packNames = Collections.unmodifiableList(new ArrayList(found.keySet()));
    }

    private static List<Path> loadPack(Path root, boolean recursive) {
        List<Path> all = KeySoundPacks.listSounds(root, recursive);
        if (all.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Path> ordered = new ArrayList<Path>();
        HashSet<Path> seen = new HashSet<Path>();
        Path index = root.resolve(INDEX_FILE);
        if (Files.isRegularFile(index, new LinkOption[0])) {
            try {
                for (String raw : Files.readAllLines(index)) {
                    Path path = KeySoundPacks.indexedSound(root, raw, recursive);
                    if (path == null || !seen.add(path)) continue;
                    ordered.add(path);
                }
            }
            catch (IOException e) {
                LOGGER.warn("Could not read key sound index {}", (Object)index, (Object)e);
            }
        }
        for (Path path : all) {
            if (!seen.add(path)) continue;
            ordered.add(path);
        }
        return Collections.unmodifiableList(ordered);
    }

    private static Path indexedSound(Path root, String raw, boolean recursive) {
        Path candidate;
        if (raw == null) {
            return null;
        }
        String line = raw.replace("\ufeff", "").trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }
        if (line.length() >= 2 && line.startsWith("\"") && line.endsWith("\"")) {
            line = line.substring(1, line.length() - 1);
        }
        try {
            candidate = root.resolve(line.replace('\\', '/')).normalize();
        }
        catch (InvalidPathException e) {
            LOGGER.warn("Invalid sound path in {}", (Object)root.resolve(INDEX_FILE));
            return null;
        }
        if (!Files.isRegularFile(candidate, new LinkOption[0]) && line.contains(" #")) {
            candidate = root.resolve(line.substring(0, line.indexOf(" #")).trim().replace('\\', '/')).normalize();
        }
        if (!(candidate.startsWith(root) && Files.isRegularFile(candidate, new LinkOption[0]) && KeySoundDecoder.isSupported(candidate))) {
            return null;
        }
        return !recursive && !root.equals(candidate.getParent()) ? null : candidate;
    }

    private static List<Path> listSounds(Path root, boolean recursive) {
        List<Path> list;
        block8: {
            Stream<Path> stream = recursive ? Files.walk(root, new FileVisitOption[0]) : Files.list(root);
            try {
                list = stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(KeySoundDecoder::isSupported).sorted(Comparator.comparing(path -> path.toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException | UncheckedIOException e) {
                    LOGGER.warn("Could not list key sounds in {}", (Object)root, (Object)e);
                    return Collections.emptyList();
                }
            }
            stream.close();
        }
        return list;
    }
}

