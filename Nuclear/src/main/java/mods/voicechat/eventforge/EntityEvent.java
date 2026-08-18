package mods.voicechat.eventforge;

import nuclear.control.events.Event;
import net.minecraft.entity.Entity;

public class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
