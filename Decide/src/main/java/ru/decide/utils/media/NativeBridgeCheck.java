/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.media.Lyrics;
import ru.decide.utils.media.LyricsParser;
import ru.decide.utils.media.MediaNative;
import ru.decide.utils.media.MediaPlayer;

@Environment(value=EnvType.CLIENT)
public final class NativeBridgeCheck {
    public static void main(String[] args) throws Exception {
        String[] probe;
        System.out.println("os.name = " + System.getProperty("os.name"));
        System.out.println("OptMedia.available()  = " + MediaNative.available());
        System.out.println("MediaNative.init()    = " + MediaNative.init());
        System.out.println("vocal loaded          = " + MediaNative.vocalAvailable());
        System.out.println("\n--- 5 \u0441\u0435\u043a\u0443\u043d\u0434 \u043e\u043f\u0440\u043e\u0441\u0430 \u043c\u0435\u0434\u0438\u0430-\u0441\u0435\u0441\u0441\u0438\u0438 ---");
        for (int i = 0; i < 25; ++i) {
            Thread.sleep(200L);
            System.out.printf("title='%s' artist='%s' appId='%s' playing=%s duration=%dms position=%dms rev=%d%n", MediaNative.title(), MediaNative.artist(), MediaNative.appId(), MediaNative.isPlaying(), MediaNative.durationMillis(), MediaNative.positionMillis(), MediaNative.revision());
        }
        System.out.println("\n--- \u043f\u0440\u043e\u0446\u0435\u0441\u0441 \u0434\u043b\u044f \u0434\u0435\u0442\u0435\u043a\u0442\u043e\u0440\u0430 \u0432\u043e\u043a\u0430\u043b\u0430 ---");
        System.out.println("vocalProcess() = " + MediaPlayer.vocalProcess());
        System.out.println("\n--- \u0434\u0435\u0442\u0435\u043a\u0442\u043e\u0440 \u0432\u043e\u043a\u0430\u043b\u0430 ---");
        String process = MediaPlayer.vocalProcess();
        boolean started = MediaNative.startVocalDetect(process);
        System.out.println("startVocalDetect('" + process + "') = " + started);
        for (int i = 0; i < 15; ++i) {
            Thread.sleep(200L);
            System.out.printf("level=%.4f peak=%.4f frames=%d flags=0x%x%n", Float.valueOf(MediaNative.vocalLevel()), Float.valueOf(MediaNative.vocalPeak()), MediaNative.vocalFrames(), MediaNative.vocalFlags());
        }
        MediaNative.stopVocalDetect();
        System.out.println("\u043f\u043e\u0441\u043b\u0435 stop: level = " + MediaNative.vocalLevel() + " (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f -1)");
        System.out.println("\n--- \u043f\u043e\u0438\u0441\u043a \u0442\u0435\u043a\u0441\u0442\u0430 (lrclib) ---");
        for (String probeLine : probe = new String[]{"Never Gonna Give You Up|Rick Astley|213", "Smells Like Teen Spirit|Nirvana|301", "Smells Like Teen Spirit|Nirvana|0", "Yellow|Coldplay|269", "\u041a\u0438\u043d\u043e \u2014 \u0413\u0440\u0443\u043f\u043f\u0430 \u043a\u0440\u043e\u0432\u0438|\u041a\u0438\u043d\u043e|331", "\u041d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442 \u0442\u0430\u043a\u043e\u0439 \u043f\u0435\u0441\u043d\u0438 \u043d\u0430 \u0441\u0432\u0435\u0442\u0435|\u041d\u0438\u043a\u0442\u043e|200"}) {
            String[] parts = probeLine.split("\\|", 3);
            String title = parts[0];
            String artist = parts[1];
            int duration = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            long startedAt = System.currentTimeMillis();
            String lrc = MediaNative.fetchLyrics(title, artist, "", duration);
            long took = System.currentTimeMillis() - startedAt;
            System.out.println("'" + title + "' (" + duration + "s) -> " + (String)(lrc == null ? "null" : lrc.length() + " chars") + " \u0437\u0430 " + took + "ms");
            if (lrc == null) continue;
            Lyrics parsed = LyricsParser.parse(lrc);
            System.out.println("   synced=" + parsed.synced() + " lines=" + parsed.lines().size() + (String)(parsed.lines().isEmpty() ? "" : " first='" + parsed.lines().get(0).text() + "'"));
        }
        MediaNative.shutdown();
    }
}

