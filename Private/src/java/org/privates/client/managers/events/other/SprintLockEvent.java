package org.privates.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.privates.client.api.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class SprintLockEvent extends Event {
    private boolean unlock;

    public void unlockSprint() {
        this.unlock = true;
    }
}

