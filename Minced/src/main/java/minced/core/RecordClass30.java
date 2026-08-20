package minced.core;

public final class RecordClass30 {
    public final boolean enabled;
    public final int delayTicks;

    public RecordClass30(boolean enabled, int delayTicks) {
        this.enabled = enabled;
        this.delayTicks = Math.max(1, delayTicks);
    }

    public boolean enabled() { return enabled; }
    public int delayTicks() { return delayTicks; }
}
