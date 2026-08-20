package org.privates.client.managers.events.player;

import lombok.Getter;
import org.privates.client.api.events.Event;

public class PostUpdateEvent extends Event {
    @Getter
    private static final PostUpdateEvent instance = new PostUpdateEvent();
}