package tech.lightclient.client.events.impl.entity;

import tech.lightclient.client.events.api.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityRenderEvent extends Event {
    private float yaw;
    private float pitch;
    private float prevYaw;
    private float prevPitch;
    private float bodyYaw;
    private float prevBodyYaw;

    private float delta;
}
