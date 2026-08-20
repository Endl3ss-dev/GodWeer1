package fun.volon.api.events.implement;

import lombok.AllArgsConstructor;
import fun.volon.api.events.Event;

@AllArgsConstructor
public class EventCloseInv extends Event {
    public int windowId;
}

