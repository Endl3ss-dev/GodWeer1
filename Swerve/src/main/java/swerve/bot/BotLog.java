package swerve.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Журнал одного бота: последние события для панели в студии.
 *
 * <p>Записи делятся на три канала, потому что смешивать их неудобно: в чате
 * сервера тонет всё остальное, а команды нужно видеть отдельно от событий
 * соединения. Студия показывает каналы вкладками.
 *
 * <ul>
 *   <li>{@link Channel#CHAT} — входящие сообщения сервера;</li>
 *   <li>{@link Channel#COMMAND} — то, что бот отправил сам (чат и команды);</li>
 *   <li>{@link Channel#OUTPUT} — полный вывод без чата: соединение, шаги, ошибки.</li>
 * </ul>
 *
 * <p>Кольцевой буфер: старые записи вытесняются, память не растёт. Пишут сюда
 * из потока соединения и из клиентского тика, читает отрисовка — поэтому доступ
 * синхронизирован, а рендер работает по снимку ({@link #snapshot(Channel)}).
 */
public final class BotLog {

    public enum Level {
        INFO,
        OK,
        WARN,
        ERROR
    }

    /** Канал записи: чат, отправленные команды или общий вывод. */
    public enum Channel {
        CHAT("Чат"),
        COMMAND("Команды"),
        OUTPUT("Полный вывод");

        private final String displayName;

        Channel(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    /** Одна запись журнала. */
    public record Entry(long time, Level level, Channel channel, String text) {
    }

    private static final int CAPACITY = 400;

    private final List<Entry> entries = new ArrayList<>(CAPACITY);

    public void info(String text) {
        add(Level.INFO, Channel.OUTPUT, text);
    }

    public void ok(String text) {
        add(Level.OK, Channel.OUTPUT, text);
    }

    public void warn(String text) {
        add(Level.WARN, Channel.OUTPUT, text);
    }

    public void error(String text) {
        add(Level.ERROR, Channel.OUTPUT, text);
    }

    /** Входящее сообщение сервера. */
    public void chat(String text) {
        add(Level.INFO, Channel.CHAT, text);
    }

    /** То, что бот отправил: «→ /login пароль». */
    public void command(String text) {
        add(Level.INFO, Channel.COMMAND, text);
    }

    public void add(Level level, Channel channel, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        Entry entry = new Entry(System.currentTimeMillis(), level, channel, text);
        synchronized (this.entries) {
            this.entries.add(entry);
            while (this.entries.size() > CAPACITY) {
                this.entries.remove(0);
            }
        }
    }

    /** Все записи по порядку (живой список — только для внутренних нужд). */
    public List<Entry> entries() {
        synchronized (this.entries) {
            return Collections.unmodifiableList(new ArrayList<>(this.entries));
        }
    }

    /**
     * Снимок записей для отрисовки, при желании — одного канала.
     *
     * <p>Панель читает журнал в игровом потоке, а писать в него может поток
     * соединения. Обход живого списка дал бы
     * {@link java.util.ConcurrentModificationException} ровно в момент, когда
     * бот что-то логирует, поэтому копируем под блокировкой.
     */
    public List<Entry> snapshot(Channel channel) {
        synchronized (this.entries) {
            if (channel == null) {
                return new ArrayList<>(this.entries);
            }
            List<Entry> filtered = new ArrayList<>(this.entries.size());
            for (int i = 0, size = this.entries.size(); i < size; i++) {
                Entry entry = this.entries.get(i);
                if (entry.channel() == channel) {
                    filtered.add(entry);
                }
            }
            return filtered;
        }
    }

    /** «Полный вывод»: всё, кроме чата и команд. */
    public List<Entry> snapshotOutputOnly() {
        synchronized (this.entries) {
            List<Entry> filtered = new ArrayList<>(this.entries.size());
            for (int i = 0, size = this.entries.size(); i < size; i++) {
                Entry entry = this.entries.get(i);
                if (entry.channel() == Channel.OUTPUT) {
                    filtered.add(entry);
                }
            }
            return filtered;
        }
    }

    public void clear() {
        synchronized (this.entries) {
            this.entries.clear();
        }
    }

    public String last() {
        synchronized (this.entries) {
            return this.entries.isEmpty() ? "" : this.entries.get(this.entries.size() - 1).text();
        }
    }
}
