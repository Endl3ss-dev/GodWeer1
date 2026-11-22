package tech.lightclient.client.utils.client.other;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.OptimizedUpdateEvent;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public final class OptimizedUpdate {

    public static final long INTERVAL_MS = 75L;

    private static final OptimizedUpdate INSTANCE = new OptimizedUpdate();

    private long lastPostMs = 0L;

    private OptimizedUpdate() {
    }

    public static void init() {
        LightClient.getEventBus().subscribe(INSTANCE);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            lastPostMs = 0L;
            return;
        }

        long now = System.currentTimeMillis();
        if (lastPostMs == 0L) {
            lastPostMs = now;
            LightClient.getEventBus().post(new OptimizedUpdateEvent(0L));
            return;
        }

        long delta = now - lastPostMs;
        if (delta < INTERVAL_MS) {
            return;
        }

        lastPostMs = now;
        LightClient.getEventBus().post(new OptimizedUpdateEvent(delta));
    }
}
