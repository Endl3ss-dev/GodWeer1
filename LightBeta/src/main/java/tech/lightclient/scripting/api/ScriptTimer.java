package tech.lightclient.scripting.api;

import tech.lightclient.scripting.runtime.ScriptManager;

/**
 * Tick and millisecond stopwatch, mirroring the {@code timer()} docs.
 *
 * <p>Ticks are counted from the client's own tick counter rather than world time, so a timer keeps
 * working across dimension changes and is not thrown off by the world clock being reset or frozen
 * by the server.</p>
 */
public final class ScriptTimer {

    private long lastTick;
    private long lastMs;

    public ScriptTimer() {
        reset();
    }

    public void reset() {
        lastTick = ScriptManager.clientTick();
        lastMs = System.currentTimeMillis();
    }

    public boolean passed(long ticks) {
        return elapsedTicks() >= ticks;
    }

    public boolean passedMillis(long ms) {
        return elapsedMillis() >= ms;
    }

    public long elapsedTicks() {
        return ScriptManager.clientTick() - lastTick;
    }

    public long elapsedMillis() {
        return System.currentTimeMillis() - lastMs;
    }

    public boolean passedAndReset(long ticks) {
        if (passed(ticks)) {
            reset();
            return true;
        }
        return false;
    }

    public boolean passedMillisAndReset(long ms) {
        if (passedMillis(ms)) {
            reset();
            return true;
        }
        return false;
    }
}
