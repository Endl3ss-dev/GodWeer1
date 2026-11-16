package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventAimAssist extends Event {
    private double deltaX;
    private double deltaY;
    private final double deltaSeconds;

    public EventAimAssist(double deltaX, double deltaY, double deltaSeconds) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaSeconds = deltaSeconds;
    }
}
