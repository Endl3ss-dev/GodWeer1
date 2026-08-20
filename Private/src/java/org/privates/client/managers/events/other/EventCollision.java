package org.privates.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.forge.api.Event;
import net.minecraft.util.math.BlockPos;


@AllArgsConstructor

public class EventCollision extends Event {

    @Getter
    public final BlockPos blockPos;
}
