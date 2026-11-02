package tech.lightclient.modules.impl.player;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FunctionAdd(name = "PingSpoof", alias = "Ping Spoof", category = Category.Player, description = "Увеличивает ваш пинг до указанного значения")
public class PingSpoof extends Function {
    
    public final SliderSetting ping = new SliderSetting("Пинг", 100.0f, 0.0f, 1000.0f, 10.0f);
    
    private final ConcurrentLinkedQueue<PacketData> delayedPackets = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService scheduler;
    
    public PingSpoof() {
        addSettings(ping);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        delayedPackets.clear();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::processDelayedPackets, 0, 10, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }

        if (mc.getNetworkHandler() != null) {
            while (!delayedPackets.isEmpty()) {
                PacketData data = delayedPackets.poll();
                if (data != null && data.packet != null) {
                    mc.getNetworkHandler().getConnection().send(data.packet);
                }
            }
        }
        delayedPackets.clear();
    }
    
    @EventHandler
    public void onPacket(EventPacket event) {
        if (nullCheck() || mc.getNetworkHandler() == null) return;

        if (event.isSend() && event.getPacket() instanceof KeepAliveC2SPacket packet) {
            long delayMs = ping.get().longValue();
            
            if (delayMs > 0) {
                event.cancel();

                delayedPackets.add(new PacketData(packet, System.currentTimeMillis() + delayMs));
            }
        }
    }
    
    private void processDelayedPackets() {
        if (mc.getNetworkHandler() == null) return;
        
        long currentTime = System.currentTimeMillis();

        while (!delayedPackets.isEmpty()) {
            PacketData data = delayedPackets.peek();
            if (data == null) {
                delayedPackets.poll();
                continue;
            }
            
            if (currentTime >= data.sendTime) {
                delayedPackets.poll();
                if (data.packet != null) {
                    mc.getNetworkHandler().getConnection().send(data.packet);
                }
            } else {
                break;
            }
        }
    }
    
    private static class PacketData {
        final KeepAliveC2SPacket packet;
        final long sendTime;
        
        PacketData(KeepAliveC2SPacket packet, long sendTime) {
            this.packet = packet;
            this.sendTime = sendTime;
        }
    }
}
