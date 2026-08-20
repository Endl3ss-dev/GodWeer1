package org.privates.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.privates.client.api.events.CancellableEvent;

@Getter
@AllArgsConstructor
public class EventSlot extends CancellableEvent {
    private final int slot;
}

