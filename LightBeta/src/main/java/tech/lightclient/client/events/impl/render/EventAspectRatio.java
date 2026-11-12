package tech.lightclient.client.events.impl.render;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventAspectRatio extends Event {
    private float value = 1.0f;
}
