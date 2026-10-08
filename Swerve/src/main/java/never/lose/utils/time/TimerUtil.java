package never.lose.utils.time;

public class TimerUtil {

    private long lastMS = System.currentTimeMillis();
    private long ms     = lastMS;

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
        ms     = lastMS;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (getCurrentMS() - ms) > milliseconds;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }
        return false;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public void setMs(long ms) {
        lastMS = System.currentTimeMillis() + ms;
    }
}
