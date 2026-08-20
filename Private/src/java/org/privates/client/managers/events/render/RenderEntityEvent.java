package org.privates.client.managers.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import org.privates.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class RenderEntityEvent extends CancellableEvent {
    private Entity entity;

    public static class Pre extends RenderEntityEvent {
        public Pre(Entity entity) {
            super(entity);
        }
    }

    public static class Post extends RenderEntityEvent {
        public Post(Entity entity) {
            super(entity);
        }
    }
}