package fun.volon.api.events.implement;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import fun.volon.api.events.Event;

@Getter
public class EventBlockCollide extends Event {
    private final BlockPos pos;

    public EventBlockCollide(BlockPos pos) {
        this.pos = pos;
    }
}
