package org.privates.client.managers.events.render;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.privates.client.api.events.CancellableEvent;

@Getter
@Setter
@NoArgsConstructor
public final class RenderShadowEvent extends CancellableEvent {
    @Getter
    private static final RenderShadowEvent instance = new RenderShadowEvent();
}