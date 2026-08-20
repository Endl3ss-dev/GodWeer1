package org.privates.client.managers.events.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class HungerEvent extends Event {
    private MatrixStack matrixStack;
    private int scaledWidth;
    private int scaledHeight;
}
