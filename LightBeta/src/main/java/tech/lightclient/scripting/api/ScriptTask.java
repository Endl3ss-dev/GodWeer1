package tech.lightclient.scripting.api;

/**
 * A deferred or repeating job created by {@code nextTick}, {@code afterTicks} or
 * {@code everyTicks}. Ticked by the script manager on the client thread.
 */
public final class ScriptTask {

    public final Runnable action;
    /** Delay for a one-shot job, or the interval for a repeating one, in ticks. */
    public final int period;
    public final boolean repeat;

    private int counter;
    private volatile boolean cancelled = false;
    private volatile boolean finished = false;

    public ScriptTask(Runnable action, int period, boolean repeat) {
        this.action = action;
        this.period = Math.max(0, period);
        this.repeat = repeat;
        this.counter = this.period;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean cancelled() {
        return cancelled;
    }

    public boolean done() {
        return cancelled || finished;
    }

    /**
     * Advances the countdown by one tick.
     *
     * @return true when the action should run now
     */
    public boolean tick() {
        if (cancelled || finished) return false;
        if (counter > 0) {
            counter--;
            return false;
        }
        if (repeat) {
            counter = period;
        } else {
            finished = true;
        }
        return true;
    }
}
