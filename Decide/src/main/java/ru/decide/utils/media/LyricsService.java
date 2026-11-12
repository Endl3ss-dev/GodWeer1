/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 */
package ru.decide.utils.media;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import ru.decide.utils.media.Lyrics;
import ru.decide.utils.media.LyricsParser;
import ru.decide.utils.media.MediaLog;
import ru.decide.utils.media.MediaNative;
import ru.decide.utils.media.MediaTrack;

@Environment(value=EnvType.CLIENT)
public final class LyricsService {
    private static final Path DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("decide").resolve("lyrics");
    private static final Map<String, Lyrics> MEMORY = new ConcurrentHashMap<String, Lyrics>();
    private static final Map<String, long[]> FAILURES = new ConcurrentHashMap<String, long[]>();
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "white-lyrics");
        thread.setDaemon(true);
        return thread;
    });
    private static final String VERSION = "v1";
    private static final long[] RETRY_DELAYS_MS = new long[]{30000L, 120000L, 600000L, 3600000L};
    private static volatile boolean transcripts = true;
    private static volatile boolean swept;

    private LyricsService() {
    }

    public static void setTranscripts(boolean value) {
        transcripts = value;
    }

    public static CompletableFuture<Lyrics> request(MediaTrack track) {
        if (track == null || track.isEmpty() || !MediaNative.available()) {
            return CompletableFuture.completedFuture(Lyrics.EMPTY);
        }
        String key = LyricsService.key(track);
        Lyrics cached = MEMORY.get(key);
        if (cached != null && !cached.isEmpty()) {
            return CompletableFuture.completedFuture(cached);
        }
        long[] failure = FAILURES.get(key);
        if (failure != null && System.currentTimeMillis() < failure[1]) {
            return CompletableFuture.completedFuture(cached == null ? Lyrics.EMPTY : cached);
        }
        return CompletableFuture.supplyAsync(() -> LyricsService.resolve(key, track), WORKER);
    }

    public static void forget(MediaTrack track) {
        if (track == null) {
            return;
        }
        String key = LyricsService.key(track);
        MEMORY.remove(key);
        FAILURES.remove(key);
        try {
            Files.deleteIfExists(DIRECTORY.resolve(key + ".lrc"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static Lyrics resolve(String key, MediaTrack track) {
        LyricsService.sweep();
        Path file = DIRECTORY.resolve(key + ".lrc");
        Lyrics cached = LyricsParser.parse(LyricsService.read(file));
        if (cached.synced()) {
            MEMORY.put(key, cached);
            FAILURES.remove(key);
            return cached;
        }
        long started = System.currentTimeMillis();
        String content = MediaNative.fetchLyrics(track.title(), track.artist(), track.albumTitle(), track.durationSeconds());
        Lyrics fetched = LyricsParser.parse(content);
        MediaLog.note("fetch", "'" + track.display() + "' -> " + (content == null ? 0 : content.length()) + " chars in " + (System.currentTimeMillis() - started) + "ms, synced=" + fetched.synced());
        if (fetched.synced()) {
            LyricsService.write(file, content);
        } else {
            LyricsService.discard(file);
        }
        Lyrics resolved = fetched.isEmpty() ? cached : fetched;
        MEMORY.put(key, resolved);
        if (resolved.synced()) {
            FAILURES.remove(key);
        } else {
            long[] previous = FAILURES.get(key);
            int attempt = previous == null ? 0 : (int)Math.min(previous[0] + 1L, (long)(RETRY_DELAYS_MS.length - 1));
            FAILURES.put(key, new long[]{attempt, System.currentTimeMillis() + RETRY_DELAYS_MS[attempt]});
        }
        return resolved;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void sweep() {
        if (swept) {
            return;
        }
        Class<LyricsService> clazz = LyricsService.class;
        synchronized (LyricsService.class) {
            if (swept) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return;
            }
            swept = true;
            // ** MonitorExit[var0] (shouldn't be in output)
            try {
                Path marker = DIRECTORY.resolve("version");
                if (Files.exists(marker, new LinkOption[0]) && VERSION.equals(Files.readString(marker, StandardCharsets.UTF_8).trim())) {
                    return;
                }
                if (Files.isDirectory(DIRECTORY, new LinkOption[0])) {
                    try (DirectoryStream<Path> entries = Files.newDirectoryStream(DIRECTORY, "*.lrc");){
                        Iterator<Path> it = entries.iterator();
                        while (it.hasNext()) {
                            Files.deleteIfExists(it.next());
                        }
                    }
                }
                Files.createDirectories(DIRECTORY, new FileAttribute[0]);
                Files.writeString(marker, (CharSequence)VERSION, StandardCharsets.UTF_8, new OpenOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return;
        }
    }

    private static void discard(Path file) {
        try {
            Files.deleteIfExists(file);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static String read(Path file) {
        try {
            return Files.exists(file, new LinkOption[0]) ? Files.readString(file, StandardCharsets.UTF_8) : null;
        }
        catch (IOException failure) {
            return null;
        }
    }

    private static void write(Path file, String content) {
        try {
            Files.createDirectories(DIRECTORY, new FileAttribute[0]);
            Files.writeString(file, (CharSequence)content, StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static String key(MediaTrack track) {
        CRC32 checksum = new CRC32();
        String source = String.join((CharSequence)"|", track.title(), track.artist(), track.albumTitle(), String.valueOf(track.durationSeconds()), VERSION, transcripts ? "asr1" : "asr0");
        checksum.update(source.getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(checksum.getValue());
    }
}

