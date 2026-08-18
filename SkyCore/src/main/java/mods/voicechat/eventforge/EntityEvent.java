package mods.voicechat.eventforge;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.Entity;

public class EntityEvent implements Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
