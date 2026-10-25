package swerve.acd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Превращает снимок журнала в текст, который можно вставить в чат модели или отдать
 * разработчику. Два формата: человекочитаемый ({@link #text}) и машиночитаемый
 * ({@link #json}).
 *
 * <p>Формат выбран такой не случайно: у каждой записи явно подписаны «что сказал
 * источник» ({@code Message:} и {@code Raw:}), «как это истолковали» ({@code Details:}
 * с причиной) и «насколько это надёжно» ({@code Confidence:}). Разборщик
 * не должен угадывать, где факт, а где догадка клиента:
 * иначе читатель сам достроит вывод «античит точно такой-то», и ответить за него будет нечем.
 *
 * <p>Методы только читают {@link AcdReport}; блокировку {@link AcdStore} не берут и
 * ничего не пишут на диск.
 */
public final class AcdFormatter {

    private static final String LINE = "==================================================";
    private static final String THIN_LINE = "--------------------------------------------------";

    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private AcdFormatter() {
    }

    /** Что печатать в деталях; экономит место в отчёте, когда записей десятки тысяч. */
    public record Options(boolean rawText, boolean source, boolean sessionContext) {

        /** Всё, что умеет форматтер: значение по умолчанию. */
        public static final Options FULL_DETAIL = new Options(true, true, true);
    }

    /**
     * Отчёт в выбранном формате. Выбор передаётся булевой меткой, а не строкой из
     * настройки: иначе {@code AcdFormatter} зависел бы от {@code AcdExporter}, где живёт
     * текст значений {@code ModeSetting}, — получился бы цикл пакетов.
     */
    public static String render(AcdReport report, Options options, boolean asJson) {
        return asJson ? json(report, options) : text(report, options);
    }

    public static String text(AcdReport report, Options options) {
        Options flags = options == null ? Options.FULL_DETAIL : options;
        StringBuilder builder = new StringBuilder(Math.max(1024, report.size() * 96));
        builder.append(LINE).append('\n');
        builder.append("AntiCheatDetector - Diagnostic Report").append('\n');
        builder.append(LINE).append('\n');
        builder.append("Generated: ").append(stamp(report.takenMs(), FULL)).append('\n');

        builder.append("\nSession:\n");
        builder.append("  Id:      ").append(report.sessionId())
                .append(" (наблюдено сессий: ").append(report.sessionsObserved()).append(")\n");
        builder.append("  Server:  ").append(emptyToDash(report.sessionServer())).append('\n');
        builder.append("  Started: ").append(report.sessionStartMs() == 0L ? "не зафиксировано"
                : stamp(report.sessionStartMs(), FULL)).append('\n');
        builder.append("  Ended:   ").append(report.sessionEndMs() == 0L ? "сессия ещё открыта"
                : stamp(report.sessionEndMs(), FULL)).append('\n');

        builder.append("\nSummary:\n");
        builder.append("  Total events: ").append(report.size()).append('\n');
        builder.append("  Confirmed events: ").append(report.confirmedCount()).append('\n');
        builder.append("  Observed by client: ").append(report.observedCount()).append('\n');
        builder.append("  Heuristic events: ").append(report.heuristicCount()).append('\n');
        builder.append("  Buffer: ").append(report.size()).append(" / ").append(report.capacity());
        if (report.dropped() > 0) {
            builder.append(", вытеснено лимитом: ").append(report.dropped());
        }
        if (report.merged() > 0) {
            builder.append(", слито повторов: ").append(report.merged());
        }
        builder.append('\n');
        builder.append("  By category:");
        boolean anyCategory = false;
        for (AcdCategory category : AcdCategory.values()) {
            int count = report.count(category);
            if (count > 0) {
                anyCategory = true;
                builder.append(' ').append(category.code()).append('=').append(count);
            }
        }
        if (!anyCategory) {
            builder.append(" пусто");
        }
        builder.append('\n');

        if (report.heuristicCount() > 0) {
            builder.append("\nВнимание: ").append(report.heuristicCount())
                    .append(" записей — предположения по шаблону текста, а не сообщения античита ")
                    .append("клиенту. Внутренние флаги защиты на сторону клиента не отправляются.\n");
        }

        builder.append(THIN_LINE).append('\n');
        if (report.size() == 0) {
            builder.append("(записей нет: модуль ничего не собрал либо история была очищена)\n");
        }
        long origin = report.sessionStartMs() != 0L ? report.sessionStartMs()
                : (report.size() > 0 ? report.events()[0].timeMs() : 0L);
        AcdEvent[] events = report.events();
        for (int i = 0; i < events.length; i++) {
            appendEntry(builder, events[i], i + 1, origin, flags);
        }

        builder.append(LINE).append('\n');
        builder.append("End of report").append('\n');
        builder.append(LINE).append('\n');
        return builder.toString();
    }

    /**
     * JSON-массив записей + служебная шапка. Поля всегда на месте (пустые — пустой
     * строкой), чтобы читающая сторона не обрабатывала два разных формата.
     */
    public static String json(AcdReport report, Options options) {
        Options flags = options == null ? Options.FULL_DETAIL : options;
        JsonObject root = new JsonObject();
        root.addProperty("report", "AntiCheatDetector");
        root.addProperty("generated", stamp(report.takenMs(), FULL));

        JsonObject session = new JsonObject();
        session.addProperty("id", report.sessionId());
        // Ключ называется sessionsObserved, а не observed: в этом же документе рядом лежит
        // summary.observed — число записей с достоверностью OBSERVED. Реальный прогон
        // показал, как это читается: «session.observed: 2» против «summary.observed: 33»
        // выглядит как противоречие, хотя первое — счётчик переподключений.
        session.addProperty("sessionsObserved", report.sessionsObserved());
        session.addProperty("server", emptyToDash(report.sessionServer()));
        session.addProperty("started", report.sessionStartMs() == 0L ? "" : stamp(report.sessionStartMs(), FULL));
        session.addProperty("ended", report.sessionEndMs() == 0L ? "" : stamp(report.sessionEndMs(), FULL));
        root.add("session", session);

        JsonObject summary = new JsonObject();
        summary.addProperty("total", report.size());
        summary.addProperty("confirmed", report.confirmedCount());
        summary.addProperty("observed", report.observedCount());
        summary.addProperty("heuristic", report.heuristicCount());
        summary.addProperty("capacity", report.capacity());
        summary.addProperty("dropped", report.dropped());
        summary.addProperty("merged", report.merged());
        JsonObject byCategory = new JsonObject();
        for (AcdCategory category : AcdCategory.values()) {
            byCategory.addProperty(category.code(), report.count(category));
        }
        summary.add("byCategory", byCategory);
        root.add("summary", summary);

        JsonArray entries = new JsonArray();
        AcdEvent[] events = report.events();
        for (int i = 0; i < events.length; i++) {
            AcdEvent event = events[i];
            JsonObject entry = new JsonObject();
            entry.addProperty("index", i + 1);
            entry.addProperty("time", stamp(event.timeMs(), CLOCK));
            entry.addProperty("timeMs", event.timeMs());
            entry.addProperty("category", event.category().code());
            entry.addProperty("categoryLabel", event.category().label());
            entry.addProperty("severity", event.severity().name());
            entry.addProperty("confidence", event.confidence().name());
            entry.addProperty("classification", event.message());
            if (flags.source()) {
                entry.addProperty("source", event.source());
            }
            if (event.component().length() > 0) {
                entry.addProperty("clientState", event.component());
            }
            if (flags.rawText() && event.raw().length() > 0) {
                entry.addProperty("raw", event.raw());
                if (event.truncated()) {
                    entry.addProperty("rawTruncated", true);
                }
            }
            if (event.detail().length() > 0) {
                entry.addProperty("details", event.detail());
            }
            entry.addProperty("repeats", event.repeats());
            if (event.variants() > 0) {
                entry.addProperty("variants", event.variants());
            }
            if (flags.sessionContext()) {
                entry.addProperty("sessionId", event.sessionId());
            }
            entries.add(entry);
        }
        root.add("events", entries);
        return GSON.toJson(root);
    }

    /**
     * Разбивает готовый текст на куски для вывода в чат: не длиннее {@code maxChars}
     * символов, по границам строк, не больше {@code maxChunks} кусков.
     *
     * <p>Отдельный метод он не для красоты: {@code AcdExporter} обязан отрезать
     * «остальное не показали» честно, а не молча потерять хвост. Отметка об опущенных
     * строках занимает последний кусок, поэтому сумма длин всех кусков больше не
     * зависит от того, повезло ли с разбивкой.
     *
     * @param text      полный текст отчёта (пустые строки выбрасываются)
     * @param maxChars  предельная длина одного куска
     * @param maxChunks предельное число кусков
     * @return куски в исходном порядке; пустой список, если показывать нечего
     */
    public static List<String> paginate(String text, int maxChars, int maxChunks) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty() || maxChars < 16 || maxChunks < 1) {
            return chunks;
        }
        List<String> lines = splitLines(text, maxChars);
        if (lines.isEmpty()) {
            return chunks;
        }
        List<String> packed = new ArrayList<>();
        List<Integer> linesPerChunk = new ArrayList<>();
        StringBuilder builder = new StringBuilder(Math.min(maxChars, 256));
        int linesInChunk = 0;
        int totalChars = 0;
        for (int i = 0, size = lines.size(); i < size; i++) {
            String line = lines.get(i);
            if (builder.length() > 0 && builder.length() + 1 + line.length() > maxChars) {
                totalChars += builder.length();
                packed.add(builder.toString());
                linesPerChunk.add(Integer.valueOf(linesInChunk));
                builder.setLength(0);
                linesInChunk = 0;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
            linesInChunk++;
        }
        if (builder.length() > 0) {
            totalChars += builder.length();
            packed.add(builder.toString());
            linesPerChunk.add(Integer.valueOf(linesInChunk));
        }
        if (packed.size() <= maxChunks) {
            chunks.addAll(packed);
            return chunks;
        }
        // последнее место отдаём отметке об опущенном: молча терять хвост нельзя
        int keep = maxChunks - 1;
        int keptLines = 0;
        int keptChars = 0;
        for (int i = 0; i < keep; i++) {
            chunks.add(packed.get(i));
            keptLines += linesPerChunk.get(i).intValue();
            keptChars += packed.get(i).length();
        }
        chunks.add(truncationNotice(lines.size() - keptLines, lines.size(), totalChars - keptChars));
        return chunks;
    }

    /** Строка, которой {@link #paginate} заменяет опущенный хвост. */
    static String truncationNotice(int droppedLines, int totalLines, int droppedChars) {
        return "…[опущено строк: " + droppedLines + " из " + totalLines
                + ", символов: " + droppedChars + "] — полный отчёт: «Скопировать все логи»";
    }

    /** Текст построчно, с длинных строк — жёсткая резка по {@code maxChars}. */
    private static List<String> splitLines(String text, int maxChars) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        int length = text.length();
        while (start < length) {
            int newline = text.indexOf('\n', start);
            int end = newline < 0 ? length : newline;
            for (int from = start; from < end; from += maxChars) {
                int to = Math.min(end, from + maxChars);
                String line = text.substring(from, to).trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            start = end + 1;
        }
        return lines;
    }

    /** Метка для имени файла отчёта. */
    public static String fileStamp(long timeMs) {
        return stamp(timeMs, FILE_STAMP);
    }

    private static void appendEntry(StringBuilder builder, AcdEvent event, int index, long originMs, Options flags) {
        builder.append('\n').append('[').append(index).append("] ")
                .append('[').append(stamp(event.timeMs(), CLOCK)).append(']')
                .append(" [").append(event.severity().name()).append(']')
                .append(" [").append(event.category().code()).append(']');
        if (event.repeats() > 1) {
            builder.append(" x").append(event.repeats());
            if (event.variants() > 0) {
                builder.append(" (вариантов текста: ").append(event.variants()).append(')');
            }
        }
        if (originMs > 0L) {
            builder.append("  t+").append((event.timeMs() - originMs) / 1000.0D).append("s");
        }
        builder.append('\n');
        if (flags.source()) {
            builder.append("Source: ").append(emptyToDash(event.source())).append('\n');
        }
        builder.append("Confidence: ").append(event.confidence().label()).append('\n');
        builder.append("Message: ").append(event.message().isEmpty() ? "—" : event.message()).append('\n');
        if (flags.rawText() && event.raw().length() > 0) {
            builder.append("Raw: ").append(event.raw());
            if (event.truncated()) {
                builder.append(" …[обрезано по ").append(AcdEvent.MAX_RAW_CHARS).append(" символам]");
            }
            builder.append('\n');
        }
        if (event.detail().length() > 0) {
            builder.append("Details: ").append(event.detail()).append('\n');
        }
        if (event.component().length() > 0) {
            builder.append("ClientState: ").append(event.component()).append('\n');
        }
        if (flags.sessionContext()) {
            builder.append("Session: #").append(event.sessionId()).append('\n');
        }
    }

    private static String stamp(long timeMs, DateTimeFormatter formatter) {
        return formatter.format(Instant.ofEpochMilli(timeMs).atZone(ZoneId.systemDefault()));
    }

    private static String emptyToDash(String value) {
        return value == null || value.isEmpty() ? "—" : value;
    }
}
