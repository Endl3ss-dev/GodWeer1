package org.privates.client.managers.events.player;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import org.privates.client.api.events.Event;


@Getter
@Setter
@AllArgsConstructor
public class SetSprintEvent extends Event {
    private LivingEntity living;
    private boolean state;
}
