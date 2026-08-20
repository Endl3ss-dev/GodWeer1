package org.privates.client.managers.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.privates.client.api.events.Event;

@Getter
@AllArgsConstructor
public class DamageEvent extends Event {
    private final DamageType damageType;

    public enum DamageType {
        ENDER_PEARL, ARROW, FALL
    }
}

