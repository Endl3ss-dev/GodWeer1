package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class EventSync extends Event {
    public float yaw;
    public float pitch;
}
