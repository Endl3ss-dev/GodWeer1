package swerve.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Журнал одного бота: последние события для панели статуса в студии.
 *
 * <p>Кольцевой буфер: старые записи вытесняются, память не растёт. Поток
 * соединения и клиентский тик пишут сюда из разных потоков, поэтому доступ
 * синхронизирован — но только на коротких критических секциях.
 */
public final class BotLog {

    public enum Level {
        INFO,
        OK,
        WARN,
        ERROR
    }

    /** Одна запись журнала. */
    public record Entry(long time, Level level, String text) {
    }

    private static final int CAPACITY = 160;

    private final List<Entry> entries = new ArrayList<>(CAPACITY);
    private final List<Entry> view = Collections.unmodifiableList(this.entries);

    public void info(String text) {
        add(Level.INFO, text);
    }

    public void ok(String text) {
        add(Level.OK, text);
    }

    public void warn(String text) {
        add(Level.WARN, text);
    }

    public void error(String text) {
        add(Level.ERROR, text);
    }

    public void add(Level level, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        Entry entry = new Entry(System.currentTimeMillis(), level, text);
        synchronized (this.entries) {
            this.entries.add(entry);
            while (this.entries.size() > CAPACITY) {
                this.entries.remove(0);
            }
        }
    }

    public List<Entry> entries() {
        return this.view;
    }

    /**
     * Копия записей для отрисовки: панель читает журнал в игровом потоке, а
     * писать в него может поток соединения. Обход живого списка дал бы
     * {@link java.util.ConcurrentModificationException} ровно в момент, когда
     * бот что-то логирует.
     */
    public List<Entry> snapshot() {
        synchronized (this.entries) {
            return new ArrayList<>(this.entries);
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
