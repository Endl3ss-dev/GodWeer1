package org.privates.client.managers.events.player;

import lombok.Getter;
import org.privates.client.api.events.Event;

public class UpdateEvent extends Event {
    @Getter
    private static final UpdateEvent instance = new UpdateEvent();
}