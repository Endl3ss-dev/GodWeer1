package org.privates.client.managers.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class RenderHandEvent extends Event {
    public enum Type {
        PRE, POST
    }
    
    private final Type type;
    private MatrixStack matrix;
    private final Hand hand;
    private final HandSide handSide;
    private final ItemStack stack;
    private float swingProgress;
    private float equippedProgress;
}
