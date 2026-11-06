package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.packet.Packet;

@AllArgsConstructor
@Getter
@Setter
public class EventPacket extends Event {
    private Packet<?> packet;
    private boolean receive;

    public boolean isReceive() {
        return receive;
    }

    public boolean isSend() {
        return !receive;
    }
}
