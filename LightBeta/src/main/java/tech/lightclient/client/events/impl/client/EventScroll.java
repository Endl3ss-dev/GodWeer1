package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventScroll extends Event {
    double horizontal;
    double vertical;

    public EventScroll(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
}
