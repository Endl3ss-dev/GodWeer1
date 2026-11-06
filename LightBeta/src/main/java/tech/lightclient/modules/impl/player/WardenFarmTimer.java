package tech.lightclient.modules.impl.player;

final class WardenFarmTimer {
    private long timestamp = System.currentTimeMillis();
    void invoke() { timestamp = System.currentTimeMillis(); }
    boolean check5(long delay) { return System.currentTimeMillis() - timestamp > delay; }
}
