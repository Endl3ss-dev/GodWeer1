package tech.lightclient.client.events.impl.render;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import tech.lightclient.client.events.api.Event;

/** Rotation override for the render camera update. */
@Getter
@Setter
public class CameraRotationEvent extends Event {
    private final Entity entity;
    private final float tickDelta;
    private final float originalYaw;
    private final float originalPitch;
    private float yaw;
    private float pitch;

    public CameraRotationEvent(Entity entity, float yaw, float pitch, float tickDelta) {
        this.entity = entity;
        this.originalYaw = yaw;
        this.originalPitch = pitch;
        this.yaw = yaw;
        this.pitch = pitch;
        this.tickDelta = tickDelta;
    }

    public boolean isChanged() {
        return yaw != originalYaw || pitch != originalPitch;
    }
}
