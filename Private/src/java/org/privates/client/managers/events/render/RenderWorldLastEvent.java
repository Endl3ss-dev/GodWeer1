package org.privates.client.managers.events.render;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.Event;

@Getter
@Setter
@NoArgsConstructor
public final class RenderWorldLastEvent extends Event {
    @Getter
    private static final RenderWorldLastEvent instance = new RenderWorldLastEvent();
    private MatrixStack matrixStack;
    private float partialTicks;

    public void set(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }
}