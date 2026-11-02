package swerve.acd;

import swerve.Swerve;
import swerve.utils.client.NotificationManager;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Экспорт журнала: буфер обмена, файл и локальный чат.
 *
 * <p>Всё, что связано с выдачей истории, живёт здесь, а не в модуле: кнопка обязана
 * работать и при выключенном {@code AntiCheatDetector} (пункт 7 задачи), а значит точка
 * входа не должна зависеть от {@code Module.onTick}, который вызывается только для
 * включённых модулей.
 *
 * <p>На сервер не уходит ничего. В чат пишутся строки через {@code ChatComponent.addMessage}
 * — это локальная отрисовка, пакет не создаётся и чат сервера не читает.
 */
public final class AcdExporter {

    /** Ограничение на размер текста в буфере обмена: гигабайт никуда не вставляется. */
    public static final int MAX_CLIPBOARD_CHARS = 512_000;

    /**
     * Предельная длина одного сообщения в чат. Это ограничение читаемости и отрисовки,
     * а не протокола: в чат ничего не отправляется, {@code ChatComponent.addMessage}
     * рисует текст локально. Держим 240 символа, чтобы кусок влезал в окно чата без
     * длинного переноса и чтобы одно нажатие кнопки не превращало чат в ленту отчёта.
     */
    public static final int MAX_CHAT_LINE_CHARS = 240;

    /** Потолок числа сообщений за один вызов — защита от «затопить чат отчётом». */
    public static final int MAX_CHAT_MESSAGES = 60;

    public static final int MIN_CHAT_LINES = 1;
    public static final int MAX_CHAT_LINES = 40;
    public static final int DEFAULT_CHAT_LINES = 12;

    /** Формат отчёта: значения совпадают с {@code ModeSetting} модуля. */
    public static final String FORMAT_TEXT = "Текст";
    public static final String FORMAT_JSON = "JSON";

    private static final Object FILE_LOCK = new Object();

    private static volatile boolean fileWriteRunning;

    private AcdExporter() {
    }

    /** Результат попытки экспорта: чтобы модуль показывал правду, а не «готово». */
    public record Result(int entries, int chars, boolean truncated, String error) {

        static Result ok(int entries, int chars, boolean truncated) {
            return new Result(entries, chars, truncated, null);
        }

        static Result failed(String error) {
            return new Result(0, 0, false, error);
        }

        public boolean success() {
            return this.error == null;
        }
    }

    /**
     * Копирует весь отчёт в системный буфер обмена.
     *
     * <p>Вызывать из игрового потока: {@code KeyboardHandler.setClipboard} — это GLFW,
     * и вызов не из потока окна кончается падением. Построение отчёта — дорогая часть,
     * и оно сознательно осталось в том же вызове: буфер обязан заполниться в кадре
     * клика, иначе между «нажал» и «в буфере» появилась бы гонка с другим приложением.
     *
     * @param windowMs  временное окно отчёта (0 — без окна): фильтр применяется к
     *                  выгрузке, а не к хранилищу, поэтому история остаётся полной
     * @return итог: сколько записей и символов ушло, было ли обрезание, что сломалось
     */
    public static Result copyToClipboard(String format, AcdFormatter.Options options, long windowMs) {
        try {
            AcdReport report = windowed(windowMs);
            String text = AcdFormatter.render(report, options, FORMAT_JSON.equals(format));
            int full = text.length();
            if (report.size() == 0) {
                NotificationManager.info("AntiCheatDetector", windowMs > 0L
                        ? "в выбранном окне времени записей нет — копировать нечего"
                        : "записей нет — копировать нечего");
                return Result.ok(0, full, false);
            }
            boolean truncated = full > MAX_CLIPBOARD_CHARS;
            if (truncated) {
                text = cutOnLine(text, MAX_CLIPBOARD_CHARS);
            }
            Minecraft client = Minecraft.getInstance();
            if (client.keyboardHandler == null) {
                return notifyFailure("копирование", "буфер обмена ещё не готов (окно не создано)");
            }
            client.keyboardHandler.setClipboard(text);
            if (truncated) {
                NotificationManager.warning("AntiCheatDetector", "обрезано до "
                        + kilo(text.length()) + " из " + kilo(full) + " символов — сохраните отчёт в файл");
            } else {
                NotificationManager.success("AntiCheatDetector",
                        "скопировано записей: " + report.size() + " (" + kilo(full) + ")");
            }
            return Result.ok(report.size(), text.length(), truncated);
        } catch (RuntimeException exception) {
            return fail("копирование", exception);
        }
    }

    /**
     * Пишет отчёт в {@code config/swerve/acd/report-<время>.<txt|json>}.
     *
     * <p>Файл — единственный способ вынести больше, чем влезает в буфер обмена, и запись
     * асинхронная: игровой кадр не должен ждать диск. Итог приходит уведомлением,
     * поэтому {@link Result} здесь описывает намерение (сколько записей ушло в запись),
     * а не факт успешной записи на ФС.
     */
    public static Result exportToFile(String format, AcdFormatter.Options options, long windowMs) {
        synchronized (FILE_LOCK) {
            if (fileWriteRunning) {
                NotificationManager.warning("AntiCheatDetector", "предыдущая запись ещё идёт");
                return Result.failed("предыдущая запись ещё идёт");
            }
            fileWriteRunning = true;
        }

        String extension = FORMAT_JSON.equals(format) ? "json" : "txt";
        try {
            AcdReport report = windowed(windowMs);
            String text = AcdFormatter.render(report, options, FORMAT_JSON.equals(format));
            Path file = FabricLoader.getInstance().getConfigDir()
                    .resolve("swerve/acd/report-" + AcdFormatter.fileStamp(report.takenMs()) + "." + extension);
            NotificationManager.info("AntiCheatDetector", "сохраняю " + report.size() + " записей…");
            writeAsync(file, text, report.size());
            return Result.ok(report.size(), text.length(), false);
        } catch (RuntimeException exception) {
            fileWriteRunning = false;
            return fail("запись файла", exception);
        }
    }

    /**
     * Печатает последние записи в локальный чат, разбивая текст на безопасные куски.
     *
     * @param lines сколько последних записей показать
     */
    public static Result printToChat(int lines, long windowMs) {
        int count = Math.max(MIN_CHAT_LINES, Math.min(MAX_CHAT_LINES, lines));
        Minecraft client = Minecraft.getInstance();
        if (client.gui == null) {
            return notifyFailure("вывод в чат", "интерфейс ещё не готов");
        }
        try {
            AcdReport report = AcdStore.snapshot(count);
            if (windowMs > 0L) {
                report = report.since(System.currentTimeMillis() - windowMs);
            }
            AcdEvent[] events = report.events();
            if (events.length == 0) {
                client.gui.getChat().addMessage(Component.literal("§d[ACD] §7записей нет"));
                return Result.ok(0, 0, false);
            }
            StringBuilder builder = new StringBuilder(1024);
            for (int i = 0; i < events.length; i++) {
                appendChatLine(builder, events[i]);
            }
            // Разбивкой занимается форматтер: он же ставит отметку, если хвост не влез.
            List<String> chunks = AcdFormatter.paginate(builder.toString(),
                    MAX_CHAT_LINE_CHARS, MAX_CHAT_MESSAGES);
            for (int i = 0, size = chunks.size(); i < size; i++) {
                client.gui.getChat().addMessage(Component.literal("§d[ACD] §f" + chunks.get(i)));
            }
            boolean clipped = chunks.size() >= MAX_CHAT_MESSAGES;
            NotificationManager.info("AntiCheatDetector", "в чат выведено записей: " + events.length
                    + (AcdStore.size() > events.length || clipped ? " (остальное — в полном отчёте)" : ""));
            return Result.ok(events.length, builder.length(), clipped);
        } catch (RuntimeException exception) {
            return fail("вывод в чат", exception);
        }
    }

    /** Одна запись в компактном виде: только для вывода в чат. */
    private static void appendChatLine(StringBuilder builder, AcdEvent event) {
        builder.append('[').append(event.severity().name()).append("] ")
                .append(event.category().code()).append(": ")
                .append(event.message().isEmpty() ? event.source() : event.message());
        if (event.repeats() > 1) {
            builder.append(" x").append(event.repeats());
        }
        builder.append('\n');
    }

    /** Снимок буфера, обрезанный по окну времени (0 — без обрезки). */
    private static AcdReport windowed(long windowMs) {
        AcdReport report = AcdStore.snapshot();
        return windowMs > 0L ? report.since(System.currentTimeMillis() - windowMs) : report;
    }

    /** Ручная очистка истории; экспорт при этом ничего не стирает — только по этой кнопке. */
    public static void clearHistory() {
        int before = AcdStore.size();
        AcdStore.clear();
        NotificationManager.info("AntiCheatDetector", "история очищена (было записей: " + before + ")");
    }

    private static void writeAsync(Path file, String text, int entries) {
        CompletableFuture.runAsync(() -> {
            String error = null;
            try {
                Files.createDirectories(file.getParent());
                Path temp = file.resolveSibling(file.getFileName() + ".tmp");
                Files.writeString(temp, text, StandardCharsets.UTF_8);
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException | RuntimeException exception) {
                error = exception.getClass().getSimpleName() + ": " + exception.getMessage();
                Swerve.LOGGER.error("AntiCheatDetector не смог записать отчёт {}", file, exception);
            } finally {
                fileWriteRunning = false;
            }
            reportWriteResult(file, entries, error);
        });
    }

    // Уведомление о результате — в игровой поток: очередь уведомлений не потокобезопасна.
    private static void reportWriteResult(Path file, int entries, String error) {
        Runnable action = () -> {
            if (error == null) {
                NotificationManager.success("AntiCheatDetector",
                        "сохранено записей: " + entries + " → " + file.getFileName());
            } else {
                NotificationManager.error("AntiCheatDetector", "файл не записан: " + error);
            }
        };
        try {
            Minecraft client = Minecraft.getInstance();
            if (client != null) {
                client.execute(action);
            }
        } catch (RuntimeException exception) {
            // Клиент уже закрывается — уведомление не нужно, ошибку видно в логе.
            Swerve.LOGGER.warn("AntiCheatDetector: не удалось показать итог сохранения", exception);
        }
    }

    private static Result fail(String what, Throwable failure) {
        String message = failure.getClass().getSimpleName() + ": " + failure.getMessage();
        Swerve.LOGGER.error("AntiCheatDetector: ошибка при {}", what, failure);
        AcdCollector.onInternalError(what, failure);
        NotificationManager.error("AntiCheatDetector", "не удалось " + what + ": " + message);
        return Result.failed(message);
    }

    private static Result notifyFailure(String what, String reason) {
        NotificationManager.error("AntiCheatDetector", "не удалось " + what + ": " + reason);
        return Result.failed(reason);
    }

    private static String cutOnLine(String text, int limit) {
        int cut = text.lastIndexOf('\n', limit);
        if (cut <= 0) {
            cut = limit;
        }
        return text.substring(0, cut) + "\n…[обрезано: не все записи поместились в буфер обмена]\n";
    }

    private static String kilo(int chars) {
        return Math.round(chars / 1024.0D * 10.0D) / 10.0D + " КБ";
    }
}
