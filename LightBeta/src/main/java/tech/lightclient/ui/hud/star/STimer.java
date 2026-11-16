package tech.lightclient.ui.hud.star;

public class STimer {
    private long millis;

    public STimer() {
        reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }
}
