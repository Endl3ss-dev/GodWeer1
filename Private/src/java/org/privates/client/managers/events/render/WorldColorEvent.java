package org.privates.client.managers.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.privates.client.api.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class WorldColorEvent extends Event {
    private float red, green, blue;
}
