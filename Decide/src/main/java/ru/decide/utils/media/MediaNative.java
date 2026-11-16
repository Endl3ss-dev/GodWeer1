/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.sun.jna.Native
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jna.Native;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.media.DecideVocalLibrary;
import ru.decide.utils.media.MediaLog;
import ru.decide.utils.media.OptMediaArtist;
import ru.decide.utils.media.OptMediaLibrary;

@Environment(value=EnvType.CLIENT)
public final class MediaNative {
    private static final String MEDIA_RESOURCE = "/assets/decide/natives/OptMedia.dll";
    private static final String VOCAL_RESOURCE = "/assets/decide/natives/DecideVocal.dll";
    private static final long POLL_INTERVAL_MS = 100L;
    private static Boolean available;
    private static OptMediaLibrary media;
    private static DecideVocalLibrary vocal;
    private static volatile String currentTitle;
    private static volatile String currentArtist;
    private static volatile String currentAppId;
    private static volatile boolean isPlaying;
    private static volatile long currentDurationMs;
    private static volatile long currentPositionMs;
    private static volatile long revision;
    private static volatile int vocalProcessId;
    private static volatile boolean vocalRunning;
    private static volatile ScheduledExecutorService poller;
    private static final String[] BROWSER_PROCESSES;
    private static final int MIN_LYRIC_LINES = 3;
    private static final Pattern LINE_TIME;

    private MediaNative() {
    }

    public static synchronized boolean available() {
        if (available == null) {
            available = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") && MediaNative.loadMedia();
        }
        return available != null && available != false;
    }

    private static synchronized boolean loadMedia() {
        if (media != null) {
            return true;
        }
        try {
            Path dll = MediaNative.extract(MEDIA_RESOURCE, "OptMedia_", ".dll");
            HashMap<String, Boolean> options = new HashMap<String, Boolean>();
            options.put("allow-objects", Boolean.TRUE);
            media = (OptMediaLibrary)Native.load((String)dll.toAbsolutePath().toString(), OptMediaLibrary.class, options);
            return media != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static synchronized boolean loadVocal() {
        if (vocal != null) {
            return true;
        }
        try {
            Path dll = MediaNative.extract(VOCAL_RESOURCE, "DecideVocal_", ".dll");
            vocal = (DecideVocalLibrary)Native.load((String)dll.toAbsolutePath().toString(), DecideVocalLibrary.class);
            return vocal != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static Path extract(String resource, String prefix, String suffix) throws Exception {
        try (InputStream in = MediaNative.class.getResourceAsStream(resource);){
            if (in == null) {
                throw new IllegalStateException("\u043d\u0435\u0442 \u0440\u0435\u0441\u0443\u0440\u0441\u0430 " + resource);
            }
            Path temp = Files.createTempFile(prefix, suffix, new FileAttribute[0]);
            temp.toFile().deleteOnExit();
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            Path path = temp;
            return path;
        }
    }

    public static synchronized boolean init() {
        if (!MediaNative.available()) {
            return false;
        }
        if (poller == null || poller.isShutdown()) {
            poller = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "white-media-poller");
                thread.setDaemon(true);
                return thread;
            });
            poller.scheduleWithFixedDelay(MediaNative::poll, 0L, 100L, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    public static synchronized void shutdown() {
        if (poller != null) {
            poller.shutdownNow();
            poller = null;
        }
        if (media != null) {
            try {
                media.Deinitialize();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        MediaNative.stopVocalDetect();
        currentTitle = "";
        currentArtist = "";
        currentAppId = "";
        isPlaying = false;
        currentDurationMs = 0L;
        currentPositionMs = 0L;
    }

    private static void poll() {
        if (media == null) {
            return;
        }
        try {
            OptMediaArtist artist = new OptMediaArtist();
            artist.setAutoWrite(false);
            if (media.GetCurrentMediaInfo(artist)) {
                boolean changed;
                artist.read();
                String title = artist.getTitle();
                String name = artist.getArtist();
                String app = artist.getSource();
                boolean playing = artist.isPlaying();
                long duration = Math.max(0L, artist.durationMs);
                long position = Math.max(0L, artist.positionMs);
                boolean bl = changed = !Objects.equals(title, currentTitle) || !Objects.equals(name, currentArtist) || playing != isPlaying || duration != currentDurationMs;
                if (changed) {
                    currentTitle = title;
                    currentArtist = name;
                    currentAppId = app;
                    isPlaying = playing;
                    currentDurationMs = duration;
                    currentPositionMs = position;
                    ++revision;
                    MediaLog.note("media", "track='" + title + "' artist='" + name + "' app='" + app + "' playing=" + playing + " duration=" + duration + "ms pos=" + position + "ms");
                } else {
                    currentPositionMs = position;
                }
                media.FreeMediaInfo(artist);
            } else if (isPlaying) {
                isPlaying = false;
                ++revision;
                MediaLog.note("media", "session closed");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static long revision() {
        return revision;
    }

    public static String title() {
        return currentTitle;
    }

    public static String artist() {
        return currentArtist;
    }

    public static String appId() {
        return currentAppId;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static long durationMillis() {
        return currentDurationMs;
    }

    public static long positionMillis() {
        return currentPositionMs;
    }

    public static String status() {
        if (currentTitle.isEmpty()) {
            return "closed";
        }
        return isPlaying ? "playing" : "paused";
    }

    public static void play() {
        if (media != null) {
            media.Play();
        }
    }

    public static void pause() {
        if (media != null) {
            media.Pause();
        }
    }

    public static void toggle() {
        if (media != null) {
            media.TogglePlayPause();
        }
    }

    public static void next() {
        if (media != null) {
            media.SkipNext();
        }
    }

    public static void previous() {
        if (media != null) {
            media.SkipPrevious();
        }
    }

    public static void seek(long millis) {
        if (media != null) {
            media.SeekToMs(millis);
            currentPositionMs = millis;
        }
    }

    public static synchronized boolean startVocalDetect(String process) {
        boolean started;
        if (process == null || process.isEmpty() || !MediaNative.loadVocal()) {
            return false;
        }
        if (vocalRunning && process.hashCode() == vocalProcessId) {
            return true;
        }
        MediaNative.stopVocalDetect();
        boolean bl = started = vocal.VocalStart(process) != 0;
        if (started) {
            vocalProcessId = process.hashCode();
            vocalRunning = true;
        }
        return started;
    }

    public static synchronized void stopVocalDetect() {
        if (vocal != null) {
            try {
                vocal.VocalStop();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        vocalRunning = false;
        vocalProcessId = 0;
    }

    public static boolean vocalAvailable() {
        return MediaNative.loadVocal();
    }

    public static float vocalLevel() {
        if (vocal == null || !vocalRunning) {
            return -1.0f;
        }
        try {
            return vocal.VocalLevel();
        }
        catch (Throwable ignored) {
            return -1.0f;
        }
    }

    public static int vocalFrames() {
        if (vocal == null || !vocalRunning) {
            return -1;
        }
        try {
            return vocal.VocalFrames();
        }
        catch (Throwable ignored) {
            return -1;
        }
    }

    public static float vocalPeak() {
        if (vocal == null || !vocalRunning) {
            return 0.0f;
        }
        try {
            return vocal.VocalPeak();
        }
        catch (Throwable ignored) {
            return 0.0f;
        }
    }

    public static int vocalFlags() {
        if (vocal == null) {
            return 0;
        }
        try {
            return vocal.VocalFlags();
        }
        catch (Throwable ignored) {
            return 0;
        }
    }

    public static String processFor(String appId) {
        String[] candidates = MediaNative.processCandidates(appId);
        return candidates.length == 0 ? null : candidates[0];
    }

    private static String[] processCandidates(String appId) {
        if (appId == null || appId.isEmpty()) {
            return new String[0];
        }
        String lower = appId.toLowerCase(Locale.ROOT);
        if (lower.contains("spotify")) {
            return new String[]{"Spotify.exe"};
        }
        if (lower.contains("yandex") || lower.contains("music.desktop")) {
            return new String[]{"\u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430.exe", "YandexMusic.exe", "Yandex.Music.exe", "browser.exe"};
        }
        if (lower.contains("aimp")) {
            return new String[]{"AIMP.exe"};
        }
        if (lower.contains("foobar")) {
            return new String[]{"foobar2000.exe"};
        }
        if (lower.contains("deezer")) {
            return new String[]{"Deezer.exe"};
        }
        if (lower.contains("tidal")) {
            return new String[]{"TIDAL.exe"};
        }
        if (lower.contains("apple")) {
            return new String[]{"AppleMusic.exe"};
        }
        if (lower.contains("vlc")) {
            return new String[]{"vlc.exe"};
        }
        if (lower.contains("musicbee")) {
            return new String[]{"MusicBee.exe"};
        }
        if (lower.contains("winamp")) {
            return new String[]{"winamp.exe"};
        }
        if (lower.endsWith(".exe")) {
            return new String[]{appId};
        }
        for (String browser : BROWSER_PROCESSES) {
            if (!lower.contains(browser.replace(".exe", ""))) continue;
            return new String[]{browser};
        }
        if (lower.endsWith("browser.exe") || lower.contains("chromium")) {
            return new String[]{"chrome.exe"};
        }
        return new String[0];
    }

    public static String fetchLyrics(String title, String artist, String album, int durationSeconds) {
        if (title == null || title.isBlank()) {
            return null;
        }
        try {
            String[] parts;
            String result;
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4L)).followRedirects(HttpClient.Redirect.NORMAL).build();
            String cleanArtist = MediaNative.cleanArtistName(artist);
            String cleanTitle = MediaNative.cleanLyricsTitle(title, cleanArtist);
            if (!(cleanArtist.isBlank() || cleanArtist.equalsIgnoreCase("Mix") || cleanArtist.equalsIgnoreCase("YouTube"))) {
                String exact = "https://lrclib.net/api/get?track_name=" + MediaNative.encode(cleanTitle) + "&artist_name=" + MediaNative.encode(cleanArtist) + (String)(durationSeconds > 0 ? "&duration=" + durationSeconds : "");
                String result2 = MediaNative.query(client, exact, false, durationSeconds, cleanTitle, cleanArtist);
                if (result2 != null && !result2.isBlank()) {
                    return result2;
                }
                result2 = MediaNative.search(client, cleanTitle + " " + cleanArtist, durationSeconds, cleanTitle, cleanArtist);
                if (result2 != null && !result2.isBlank()) {
                    return result2;
                }
            }
            if ((result = MediaNative.search(client, cleanTitle, durationSeconds, cleanTitle, cleanArtist)) != null && !result.isBlank()) {
                return result;
            }
            if ((title.contains("-") || title.contains("\u2013") || title.contains("\u2014")) && (parts = title.split("[-\u2013\u2014]", 2)).length == 2) {
                String tail;
                String head = MediaNative.cleanArtistName(parts[0].trim());
                result = MediaNative.search(client, head + " " + (tail = MediaNative.cleanLyricsTitle(parts[1].trim(), null)), durationSeconds, tail, head);
                if (result != null && !result.isBlank()) {
                    return result;
                }
                result = MediaNative.query(client, "https://lrclib.net/api/get?track_name=" + MediaNative.encode(tail) + "&artist_name=" + MediaNative.encode(head), false, durationSeconds, tail, head);
                if (result != null && !result.isBlank()) {
                    return result;
                }
                if (!tail.isBlank() && tail.length() >= 3 && (result = MediaNative.search(client, tail, durationSeconds, tail, head)) != null && !result.isBlank()) {
                    return result;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static String search(HttpClient client, String term, int durationSeconds, String wantTitle, String wantArtist) {
        return MediaNative.query(client, "https://lrclib.net/api/search?q=" + MediaNative.encode(term), true, durationSeconds, wantTitle, wantArtist);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String query(HttpClient client, String url, boolean isArray, int durationSeconds, String wantTitle, String wantArtist) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "DecideClient/1.0").timeout(Duration.ofSeconds(5L)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                return null;
            }
            String body = response.body();
            if (body == null || body.isBlank()) {
                return null;
            }
            if (!isArray) {
                return MediaNative.accepted(JsonParser.parseString((String)body).getAsJsonObject(), durationSeconds, wantTitle, wantArtist);
            }
            JsonArray array = JsonParser.parseString((String)body).getAsJsonArray();
            JsonObject best = null;
            int bestScore = Integer.MIN_VALUE;
            for (JsonElement element2 : array) {
                JsonObject candidate;
                int score;
                if (!element2.isJsonObject() || (score = MediaNative.score(candidate = element2.getAsJsonObject(), durationSeconds, wantTitle, wantArtist)) <= bestScore) continue;
                bestScore = score;
                best = candidate;
            }
            return best == null ? null : MediaNative.accepted(best, durationSeconds, wantTitle, wantArtist);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static int score(JsonObject entry, int durationSeconds, String wantTitle, String wantArtist) {
        boolean hasPlain;
        String synced = MediaNative.stringOf(entry, "syncedLyrics");
        String plain = MediaNative.stringOf(entry, "plainLyrics");
        boolean hasSynced = synced != null && !synced.isBlank();
        boolean bl = hasPlain = plain != null && !plain.isBlank();
        if (!hasSynced && !hasPlain) {
            return -1;
        }
        int score = 0;
        if (hasSynced) {
            int lines = MediaNative.countTimedLines(synced);
            if (lines < 3) {
                return -1;
            }
            score = lines >= 8 ? (score += 30) : (score += 10);
        } else if (MediaNative.countTextLines(plain) < 3) {
            return -1;
        }
        if (durationSeconds > 0) {
            double resultDuration;
            double d = resultDuration = entry.has("duration") ? entry.get("duration").getAsDouble() : 0.0;
            if (resultDuration > 0.0) {
                double delta = Math.abs(resultDuration - (double)durationSeconds);
                score = delta <= 4.0 ? (score += 120) : (delta <= 12.0 ? (score += 55) : (delta <= 40.0 ? (score += 10) : (score -= 60)));
            }
        }
        String title = MediaNative.normalize(MediaNative.stringOf(entry, "trackName"));
        String expectedTitle = MediaNative.normalize(wantTitle);
        if (!title.isEmpty() && !expectedTitle.isEmpty()) {
            score = title.equals(expectedTitle) ? (score += 60) : (title.contains(expectedTitle) || expectedTitle.contains(title) ? (score += 25) : (score -= 30));
        }
        String entryArtist = MediaNative.normalize(MediaNative.stringOf(entry, "artistName"));
        String expectedArtist = MediaNative.normalize(wantArtist);
        if (!entryArtist.isEmpty() && !expectedArtist.isEmpty()) {
            if (entryArtist.equals(expectedArtist)) {
                score += 60;
            } else if (entryArtist.contains(expectedArtist) || expectedArtist.contains(entryArtist)) {
                score += 25;
            }
        }
        return score;
    }

    private static String accepted(JsonObject entry, int durationSeconds, String wantTitle, String wantArtist) {
        if (MediaNative.score(entry, durationSeconds, wantTitle, wantArtist) < 0) {
            return null;
        }
        String synced = MediaNative.stringOf(entry, "syncedLyrics");
        if (synced != null && !synced.isBlank()) {
            return synced;
        }
        String plain = MediaNative.stringOf(entry, "plainLyrics");
        if (plain != null && !plain.isBlank()) {
            return MediaNative.plainToLrc(plain, durationSeconds);
        }
        return null;
    }

    private static int countTimedLines(String lrc) {
        int count = 0;
        for (String line : lrc.split("\\R")) {
            if (!LINE_TIME.matcher(line.trim()).find()) continue;
            ++count;
        }
        return count;
    }

    private static int countTextLines(String text) {
        int count = 0;
        for (String line : text.split("\\R")) {
            if (line.isBlank()) continue;
            ++count;
        }
        return count;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
    }

    private static String stringOf(JsonObject object, String field) {
        if (object.has(field) && !object.get(field).isJsonNull()) {
            return object.get(field).getAsString();
        }
        return null;
    }

    private static String plainToLrc(String plain, int durationSeconds) {
        if (plain == null || plain.isBlank()) {
            return null;
        }
        Pattern section = Pattern.compile("^\\[[A-Za-z\\s0-9_\\-:]+\\]$");
        ArrayList<String> valid = new ArrayList<String>();
        for (String raw : plain.split("\\R")) {
            String trimmed = raw.trim();
            if (trimmed.isEmpty() || section.matcher(trimmed).matches()) continue;
            valid.add(trimmed);
        }
        if (valid.isEmpty()) {
            return null;
        }
        int duration = durationSeconds > 0 ? durationSeconds : Math.max(60, valid.size() * 4);
        double intro = Math.min(12.0, Math.max(4.0, (double)duration * 0.07));
        double outro = Math.min(12.0, Math.max(4.0, (double)duration * 0.07));
        double singTime = Math.max(10.0, (double)duration - intro - outro);
        int totalWeight = 0;
        int[] weights = new int[valid.size()];
        for (int i = 0; i < valid.size(); ++i) {
            weights[i] = Math.max(1, ((String)valid.get(i)).length());
            totalWeight += weights[i];
        }
        StringBuilder builder = new StringBuilder();
        int cumulative = 0;
        for (int i = 0; i < valid.size(); ++i) {
            double time = intro + (double)cumulative / (double)totalWeight * singTime;
            int minutes = (int)(time / 60.0);
            double seconds = time % 60.0;
            builder.append(String.format(Locale.ROOT, "[%02d:%05.2f]%s\n", minutes, seconds, valid.get(i)));
            cumulative += weights[i];
        }
        return builder.toString();
    }

    private static String cleanLyricsTitle(String title, String cleanArtist) {
        String stripped;
        if (title == null) {
            return "";
        }
        String result = title.replaceAll("[\u22c6\u2605\u2606\u2022\u00b7*]", " ").replaceAll("\\[[^\\]]*]", " ").replaceAll("\\([^\\)]*\\)", " ").replaceAll("(?i)\\b(official\\s+(video|audio|music\\s+video)|lyrics|lyric\\s+video|remix|hd|4k|ft\\.?.*|feat\\.?.*)\\b", " ").replaceAll("\\s+", " ").trim();
        if (cleanArtist != null && !cleanArtist.isBlank() && !(stripped = result.replaceFirst("(?i)^" + Pattern.quote(cleanArtist) + "\\s*[-\u2013\u2014:]\\s*", "").trim()).isEmpty()) {
            result = stripped;
        }
        return result.isEmpty() ? title.trim() : result;
    }

    private static String cleanArtistName(String artist) {
        if (artist == null) {
            return "";
        }
        String result = artist.replaceAll("[\u22c6\u2605\u2606\u2022\u00b7*]", " ").replaceAll("(?i)\\s*-\\s*topic$", "").replaceAll("(?i)\\s+topic$", "").replaceAll("(?i)\\s*vevo$", "").replaceAll("\\[[^\\]]*]", " ").replaceAll("\\([^\\)]*\\)", " ").replaceAll("\\s+", " ").trim();
        return result.isEmpty() ? artist.trim() : result;
    }

    static {
        currentTitle = "";
        currentArtist = "";
        currentAppId = "";
        revision = 1L;
        vocalProcessId = 0;
        BROWSER_PROCESSES = new String[]{"chrome.exe", "msedge.exe", "firefox.exe", "opera.exe", "browser.exe", "yandex.exe"};
        LINE_TIME = Pattern.compile("\\[(\\d+):(\\d{1,2})(?:[.:](\\d{1,3}))?]");
    }
}

