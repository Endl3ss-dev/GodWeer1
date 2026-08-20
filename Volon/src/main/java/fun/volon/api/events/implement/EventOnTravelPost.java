package fun.volon.api.events.implement;

import fun.volon.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

@AllArgsConstructor
@Getter
@Setter
public class EventOnTravelPost extends Event {
    private Vec3d oldVelocity;
}
