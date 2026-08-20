package fun.volon.api.events.implement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.events.Event;

@Getter
@AllArgsConstructor
public class EventOnMovePost extends Event {
    private final float speed;
    private final Vec3d movementInput;
}
