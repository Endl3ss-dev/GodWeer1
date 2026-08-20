package org.privates.client.managers.events.other;

import lombok.Getter;
import org.privates.client.api.events.Event;

public class GameUpdateEvent extends Event {
    @Getter
    private static final GameUpdateEvent instance = new GameUpdateEvent();
}
