/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.media.LyricLine;
import ru.decide.utils.media.LyricWord;
import ru.decide.utils.media.Lyrics;

@Environment(value=EnvType.CLIENT)
public final class LyricsParser {
    private static final Pattern LINE_TIME = Pattern.compile("\\[(\\d+):(\\d{1,2})(?:[.:](\\d{1,3}))?]");
    private static final Pattern WORD_TIME = Pattern.compile("<(\\d+):(\\d{1,2})(?:[.:](\\d{1,3}))?>");
    private static final Pattern OFFSET = Pattern.compile("\\[offset:\\s*([+-]?\\d+)]", 2);
    private static final Pattern TAG = Pattern.compile("\\[[a-zA-Z#]+:.*]");
    private static final long TAIL_MILLIS = 5000L;
    private static final long MILLIS_PER_SYLLABLE = 300L;
    private static final long MIN_SUNG_MILLIS = 1500L;
    private static final long FULL_SPAN_MILLIS = 7000L;
    private static final String CYRILLIC_VOWELS = "\u0430\u0435\u0451\u0438\u043e\u0443\u044b\u044d\u044e\u044f\u0456\u0457\u0454";
    private static final String LATIN_VOWELS = "aeiouy";

    private LyricsParser() {
    }

    public static Lyrics parse(String content) {
        ArrayList<LyricLine> lines;
        if (content == null || content.isBlank()) {
            return Lyrics.EMPTY;
        }
        long offset = 0L;
        Matcher offsetMatcher = OFFSET.matcher(content);
        if (offsetMatcher.find()) {
            offset = Long.parseLong(offsetMatcher.group(1));
        }
        ArrayList<long[]> stamps = new ArrayList<long[]>();
        ArrayList<String> bodies = new ArrayList<String>();
        ArrayList<String> plain = new ArrayList<String>();
        for (String raw : content.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            Matcher matcher = LINE_TIME.matcher(line);
            int consumed = 0;
            int first = stamps.size();
            while (matcher.find(consumed) && matcher.start() == consumed) {
                stamps.add(new long[]{LyricsParser.timestamp(matcher, 1) - offset, bodies.size()});
                consumed = matcher.end();
            }
            if (stamps.size() == first) {
                if (TAG.matcher(line).matches()) continue;
                plain.add(line);
                continue;
            }
            bodies.add(line.substring(consumed).trim());
        }
        if (stamps.isEmpty()) {
            if (plain.isEmpty()) {
                return Lyrics.EMPTY;
            }
            lines = new ArrayList<LyricLine>(plain.size());
            for (String plainLine : plain) {
                lines.add(new LyricLine(0L, 0L, plainLine, Collections.emptyList()));
            }
            return new Lyrics(List.copyOf(lines), false);
        }
        stamps.sort(Comparator.comparingLong(a -> a[0]));
        lines = new ArrayList(stamps.size());
        for (int index = 0; index < stamps.size(); ++index) {
            long[] stamp = (long[])stamps.get(index);
            long start = Math.max(0L, stamp[0]);
            long end = index + 1 < stamps.size() ? Math.max(start, ((long[])stamps.get(index + 1))[0]) : start + 5000L;
            String body = (String)bodies.get((int)stamp[1]);
            lines.add(WORD_TIME.matcher(body).find() ? LyricsParser.enhanced(body, start, end, offset) : LyricsParser.plainLine(body, start, end));
        }
        return new Lyrics(List.copyOf(lines), true);
    }

    private static LyricLine plainLine(String body, long start, long end) {
        long span = Math.max(1L, end - start);
        long natural = Math.max((long)LyricsParser.syllables(body, 0, body.length()) * 300L, 1500L);
        long sung = span <= 7000L ? span : Math.min(span, natural);
        return new LyricLine(start, end, body, LyricsParser.distribute(body, start, start + sung));
    }

    private static LyricLine enhanced(String body, long start, long end, long offset) {
        StringBuilder text = new StringBuilder();
        ArrayList<LyricWord> words = new ArrayList<LyricWord>();
        ArrayList<long[]> pending = new ArrayList<long[]>();
        Matcher matcher = WORD_TIME.matcher(body);
        int cursor = 0;
        long current = -1L;
        while (matcher.find()) {
            String chunk = body.substring(cursor, matcher.start());
            if (current >= 0L) {
                pending.add(new long[]{current, text.length(), text.length() + chunk.length()});
            }
            text.append(chunk);
            current = Math.max(0L, LyricsParser.timestamp(matcher, 1) - offset);
            cursor = matcher.end();
        }
        String tail = body.substring(cursor);
        if (current >= 0L) {
            pending.add(new long[]{current, text.length(), text.length() + tail.length()});
        }
        text.append(tail);
        for (int index = 0; index < pending.size(); ++index) {
            int begin;
            long[] span = (long[])pending.get(index);
            long wordStart = span[0];
            long wordEnd = index + 1 < pending.size() ? Math.max(wordStart, ((long[])pending.get(index + 1))[0]) : end;
            int finish = (int)span[2];
            for (begin = (int)span[1]; begin < finish && Character.isWhitespace(text.charAt(begin)); ++begin) {
            }
            while (finish > begin && Character.isWhitespace(text.charAt(finish - 1))) {
                --finish;
            }
            if (finish <= begin) continue;
            words.add(new LyricWord(wordStart, wordEnd, text.substring(begin, finish), begin, finish));
        }
        return new LyricLine(start, end, text.toString(), List.copyOf(words));
    }

    private static List<LyricWord> distribute(String text, long start, long end) {
        ArrayList<int[]> spans = new ArrayList<int[]>();
        int total = 0;
        int cursor = 0;
        while (cursor < text.length()) {
            while (cursor < text.length() && Character.isWhitespace(text.charAt(cursor))) {
                ++cursor;
            }
            int begin = cursor;
            while (cursor < text.length() && !Character.isWhitespace(text.charAt(cursor))) {
                ++cursor;
            }
            if (cursor <= begin) continue;
            int weight = LyricsParser.syllables(text, begin, cursor);
            spans.add(new int[]{begin, cursor, weight});
            total += weight;
        }
        if (spans.isEmpty() || total == 0) {
            return Collections.emptyList();
        }
        long duration = Math.max(0L, end - start);
        ArrayList<LyricWord> words = new ArrayList<LyricWord>(spans.size());
        long accumulated = 0L;
        for (int[] span : spans) {
            long wordStart = start + duration * accumulated / (long)total;
            long wordEnd = start + duration * (accumulated += (long)span[2]) / (long)total;
            words.add(new LyricWord(wordStart, wordEnd, text.substring(span[0], span[1]), span[0], span[1]));
        }
        return List.copyOf(words);
    }

    private static int syllables(String text, int begin, int end) {
        int count = 0;
        boolean spoken = false;
        boolean latinRun = false;
        for (int index = begin; index < end; ++index) {
            char symbol = Character.toLowerCase(text.charAt(index));
            if (Character.isLetterOrDigit(symbol)) {
                spoken = true;
            }
            if (CYRILLIC_VOWELS.indexOf(symbol) >= 0) {
                ++count;
                latinRun = false;
                continue;
            }
            if (LATIN_VOWELS.indexOf(symbol) >= 0) {
                if (!latinRun) {
                    ++count;
                }
                latinRun = true;
                continue;
            }
            latinRun = false;
        }
        return count > 0 ? count : (spoken ? 1 : 0);
    }

    private static long timestamp(Matcher matcher, int group) {
        long minutes = Long.parseLong(matcher.group(group));
        long seconds = Long.parseLong(matcher.group(group + 1));
        String fraction = matcher.group(group + 2);
        long millis = 0L;
        if (fraction != null && !fraction.isEmpty()) {
            millis = switch (fraction.length()) {
                case 1 -> Long.parseLong(fraction) * 100L;
                case 2 -> Long.parseLong(fraction) * 10L;
                default -> Long.parseLong(fraction);
            };
        }
        return minutes * 60000L + seconds * 1000L + millis;
    }
}

