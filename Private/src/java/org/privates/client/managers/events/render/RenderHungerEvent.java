package org.privates.client.managers.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public final class RenderHungerEvent extends CancellableEvent {
    private MatrixStack matrix;
}
