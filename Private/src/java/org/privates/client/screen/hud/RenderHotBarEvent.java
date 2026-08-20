package org.privates.client.screen.hud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.CancellableEvent;

@Getter
@AllArgsConstructor
public class RenderHotBarEvent extends CancellableEvent {
    private final MatrixStack matrix;
    private final float partialTicks;
}
