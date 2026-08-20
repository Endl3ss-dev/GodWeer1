package org.privates.client.utils.server;

import lombok.Getter;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;
import org.privates.client.api.events.Handler;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.utils.math.Mathf;

@Getter
public class ServerTPS extends Handler {
    private float TPS = 20;
    private float adjustTicks = 0;
    private long timestamp;

    private void update() {
        long delay = System.nanoTime() - timestamp;

        float maxTPS = 20;
        float rawTPS = maxTPS * (1e9f / delay);

        float boundedTPS = MathHelper.clamp(rawTPS, 0, maxTPS);

        TPS = (float) Mathf.round(boundedTPS);

        adjustTicks = boundedTPS - maxTPS;

        timestamp = System.nanoTime();
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (event.getPacket() instanceof SUpdateTimePacket) {
            update();
        }
    }
}