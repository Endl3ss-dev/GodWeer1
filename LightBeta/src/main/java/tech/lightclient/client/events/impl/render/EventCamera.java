package tech.lightclient.client.events.impl.render;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCamera extends Event {
    private boolean cameraClip = false;
    private float distance = 4f;
}
