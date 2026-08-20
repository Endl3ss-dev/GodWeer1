package org.privates.client.managers.events.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import org.privates.client.api.events.Event;

@Getter
@RequiredArgsConstructor
public class EntityRemoveEvent extends Event {
    private final Entity entity;
}