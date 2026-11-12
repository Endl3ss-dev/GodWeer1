package releon.ru.api.events.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import releon.ru.api.events.Event;

public record BlockBreakingEvent(BlockPos blockPos, Direction direction) implements Event {
}
