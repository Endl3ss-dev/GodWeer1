package tech.lightclient.scripting.runtime;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Ring buffer of script console lines, shown in the Scripts console window and served
 * incrementally over MCP through {@code console_tail}.
 */
public final class ScriptConsole {

    public enum Level { INFO, WARN, ERROR }

    public static final class Line {
        public final long id;
        public final Level level;
        public final String script;
        public final String text;
        public final String time;

        Line(long id, Level level, String script, String text) {
            this.id = id;
            this.level = level;
            this.script = script;
            this.text = text;
            this.time = LocalTime.now().format(FMT);
        }
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX = 1000;
    private static final Deque<Line> LINES = new ArrayDeque<>();
    private static long counter = 0;

    public static synchronized void add(Level level, String script, String text) {
        String safe = text == null ? "null" : text;
        // Keep single lines bounded so a runaway script cannot eat all the memory.
        if (safe.length() > 4000) safe = safe.substring(0, 4000) + "...";
        LINES.addLast(new Line(++counter, level, script == null ? "?" : script, safe));
        while (LINES.size() > MAX) LINES.removeFirst();
        mirrorToGameLog(level, script, safe);
    }

    /**
     * Also writes to the ordinary game log.
     *
     * <p>Without this, anything that goes wrong before the player opens the Scripts console -
     * a compile error on startup, for instance - leaves no trace at all in latest.log, which makes
     * the whole thing undiagnosable.
     */
    private static void mirrorToGameLog(Level level, String script, String text) {
        try {
            String line = "[Scripts] [" + (script == null ? "?" : script) + "] " + text;
            switch (level) {
                case WARN -> tech.lightclient.LightClient.LOGGER.warn(line);
                case ERROR -> tech.lightclient.LightClient.LOGGER.error(line);
                default -> tech.lightclient.LightClient.LOGGER.info(line);
            }
        } catch (Throwable ignored) {
            // No logger available (the standalone verification harness); the ring buffer is enough.
        }
    }

    public static void info(String script, String text)  { add(Level.INFO,  script, text); }
    public static void warn(String script, String text)  { add(Level.WARN,  script, text); }
    public static void error(String script, String text) { add(Level.ERROR, script, text); }

    public static synchronized List<Line> snapshot() {
        return new ArrayList<>(LINES);
    }

    /** Lines newer than {@code sinceId}, oldest first, capped at {@code limit}. */
    public static synchronized List<Line> since(long sinceId, int limit) {
        List<Line> out = new ArrayList<>();
        for (Line line : LINES) {
            if (line.id > sinceId) out.add(line);
        }
        if (limit > 0 && out.size() > limit) {
            out = new ArrayList<>(out.subList(out.size() - limit, out.size()));
        }
        return out;
    }

    /** Id of the newest line, so callers can resume from the tail. */
    public static synchronized long lastId() {
        return counter;
    }

    /**
     * True when the caller's cursor sits ahead of our newest line, which means the numbering
     * restarted (the game was relaunched) rather than the console having gone quiet.
     */
    public static synchronized boolean isReset(long sinceId) {
        return sinceId > counter;
    }

    public static synchronized void clear() {
        LINES.clear();
    }

    private ScriptConsole() {}
}
