package tech.lightclient.client.events.impl.player;

import lombok.Getter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.api.Event;

@Getter
public class ElytraVelocityEvent extends Event {
    private final ClientPlayerEntity player;
    private final Vec3d originalVelocity;
    private Vec3d velocity;
    private boolean modified;

    public ElytraVelocityEvent(ClientPlayerEntity player, Vec3d velocity) {
        this.player = player;
        this.originalVelocity = velocity;
        this.velocity = velocity;
    }

    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
        this.modified = true;
    }
}
