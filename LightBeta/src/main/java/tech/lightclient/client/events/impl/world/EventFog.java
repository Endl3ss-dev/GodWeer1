package tech.lightclient.client.events.impl.world;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventFog extends Event {
    private float distance;
    private int color;
}
