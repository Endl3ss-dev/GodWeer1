package tech.lightclient.client.events.impl.client;

import tech.lightclient.client.events.api.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventMouse extends Event {
    int button;
    int action;

    public EventMouse(int b,int action) {
        button = b;
        this.action = action;
    }
}