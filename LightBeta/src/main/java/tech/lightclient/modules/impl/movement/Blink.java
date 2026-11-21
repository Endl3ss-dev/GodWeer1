package tech.lightclient.modules.impl.movement;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@FunctionAdd(name = "Blink", alias = "Blink", category = Category.Movement, description = "Задерживает отправку пакетов движения на сервер")
public class Blink extends Function {
    
    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    
    public final BooleanSetting pulse = new BooleanSetting("Пульс", false);
    public final BooleanSetting renderPath = new BooleanSetting("Показывать путь", true);
    
    public Blink() {
        addSettings(pulse, renderPath);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        packets.clear();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.getNetworkHandler() != null) {
            while (!packets.isEmpty()) {
                Packet<?> packet = packets.poll();
                if (packet != null) {
                    mc.getNetworkHandler().getConnection().send(packet);
                }
            }
        }
        packets.clear();
    }
    
    @EventHandler
    public void onPacket(EventPacket event) {
        if (nullCheck()) return;

        if (event.isSend()) {
            Packet<?> packet = event.getPacket();

            if (packet instanceof PlayerMoveC2SPacket ||
                packet instanceof PlayerInteractEntityC2SPacket ||
                packet instanceof PlayerInteractBlockC2SPacket ||
                packet instanceof PlayerInteractItemC2SPacket ||
                packet instanceof HandSwingC2SPacket) {
                
                packets.add(packet);
                event.cancel();
            }
        }
    }
    
    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;

        if (pulse.get() && mc.player.age % 20 == 0) {
            releasePackets();
        }
    }
    
    private void releasePackets() {
        if (mc.getNetworkHandler() != null) {
            int count = 0;
            while (!packets.isEmpty() && count < 10) {
                Packet<?> packet = packets.poll();
                if (packet != null) {
                    mc.getNetworkHandler().getConnection().send(packet);
                    count++;
                }
            }
        }
    }
    
    public Queue<Packet<?>> getPackets() {
        return packets;
    }
}
