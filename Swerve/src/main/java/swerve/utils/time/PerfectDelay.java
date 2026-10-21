package swerve.utils.time;

/**
 * «Идеальная» задержка: после переключения предмета или свинга руки аура
 * выдерживает паузу, чтобы удары не выглядели автоматическими.
 *
 * <p>Адаптировано из Private/src/java/org/privates/client/utils/math/PerfectDelay.java:
 * таймер встроен в класс (в оригинале использовался StopWatch), привязка
 * к NoSlow/AutoTotem убрана — таких модулей в Swerve нет.
 */
public final class PerfectDelay {

    private long delay;
    private long lastReset;

    /** true, если с последнего сброса прошло заданное время. */
    public boolean cooldownComplete() {
        return cooldownComplete(this.delay);
    }

    /**
     * true, если с последнего сброса прошло не меньше {@code delayMs}.
     * Перегрузка как в Private: {@code AuraNew.shouldAttack} передаёт динамический
     * кулдаун инструмента, а не значение последнего {@link #reset(long)}.
     */
    public boolean cooldownComplete(long delayMs) {
        return System.currentTimeMillis() - lastReset >= delayMs;
    }

    /** Запустить задержку на delayMs миллисекунд. */
    public void reset(long delayMs) {
        this.lastReset = System.currentTimeMillis();
        this.delay = delayMs;
    }
}
