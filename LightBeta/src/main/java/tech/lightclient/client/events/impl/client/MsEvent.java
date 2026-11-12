package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MsEvent extends Event {
    public float yaw, pitch;
    private long ms = 1L;
}