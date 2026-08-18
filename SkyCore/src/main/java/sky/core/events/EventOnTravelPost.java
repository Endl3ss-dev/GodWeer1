package sky.core.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.math.vector.Vector3d;

@Data
@AllArgsConstructor
public class EventOnTravelPost implements Event {
    private Vector3d oldVelocity;
}